/*
 * MIRIAM Registry (Web Application)
 * The Registry is an online resource created to catalogue data collections 
 * (their URIs and the corresponding physical URLs, whether these are controlled vocabularies or databases)
 * and provide unique and stable identifiers for life science, in the form of URIs. 
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2014  BioModels.net (EMBL - European Bioinformatics Institute)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */


package uk.ac.ebi.miriam.web;


import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.SimpleDataType;

import java.io.IOException;
import java.util.GregorianCalendar;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;


/**
 * <p>
 * Servlet that handles the deprecation of one data collection in the database (2nd step: actually deprecate the collection).
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2014 BioModels.net (EMBL - European Bioinformatics Institute)
 * <br />
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <br />
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * </dd>
 * </dl>
 * </p>
 *
 * @author Camille Laibe <camille.laibe@ebi.ac.uk>
 * @version 20140311
 */
public class ServletDataCollectionDeprecate2 extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final long serialVersionUID = 4418500924132687450L;
	private Logger logger = Logger.getLogger(ServletDataCollectionDeprecate2.class);
	
	
	/**
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String message = null;
    	String jsp = "/collections";
    	
        // retrieves the user logged who asked for the action
        HttpSession session = request.getSession();
        
        // the user is logged in
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(session))
            {
            	Boolean replacementExists = false;
            	Boolean deprecated = false;
            	
				// retrieves info about the data collection to be deprecated
		        String collectionId = request.getParameter("collectionId");
		        String comment = request.getParameter("comment");
		        String replacementId = request.getParameter("replacementId");
		        
		        // database access
                String poolName = getServletContext().getInitParameter("miriam_db_pool");
		        DataTypeDao dao = new DataTypeDao(poolName);
		        
		        // retrieves some basic info about the data collection, also checks that it actually exists
		        SimpleDataType collection = dao.getSimpleDataTypeById(collectionId);
		        
		        // checks that the replacement data collection exists
		        if ((null != replacementId) && (!replacementId.matches("\\s*")))
		        {
		        	replacementExists = dao.dataTypeExists(replacementId);
		        	if (! replacementExists)
		        	{
		        		replacementId = null;   // the replacement data collection does not exist, so we don't record it 
		        	}
		        }
		        else
		        {
		        	replacementId = null;  // no replacement data collection has been provided
		        }
		        
		        // deprecates data collection
		        if ((null != collection) && (null != comment) && (!comment.matches("\\s*")))
		        {
		        	deprecated = dao.deprecateDataCollection(collectionId, comment, replacementId);
		        	if (deprecated)
		        	{
			        	if (!dao.updateLastModifDate(collectionId))
			        	{
			        		logger.error("Unable to update the date of last modification of collection " + collection.getId() + " while deprecating it.");
			        	}
		        	}
		        }
		        else
		        {
		        	deprecated = false;
		        }
		        
		        // cleaning
		        dao.clean();
		        
		        // deprecation successful
		        if (deprecated)
		        {
		        	// retrieves the version of the web application (sid/local, alpha, main or demo)
		            String version = getServletContext().getInitParameter("version");
		        	
				    // retrieves the email addresses of the administrator and the curators
		        	String emailAdr = getServletContext().getInitParameter("admin.email");
		            String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
				    
		            // sends an email to the administrator and curators
		            StringBuilder emailBody = new StringBuilder();
                    emailBody.append("The data collection '");
                    emailBody.append(collection.getName());
                    emailBody.append("' (");
                    emailBody.append(collection.getId());
                    emailBody.append(") has been deprecated.");
                    emailBody.append("\n\nComment:\n");
                    emailBody.append(comment);
                    if (null != replacementId)
                    {
                    	emailBody.append("\n\nSuggested replacement data collection:\n");
                    	emailBody.append(replacementId);
                    }
                    GregorianCalendar cal = new GregorianCalendar();
                    emailBody.append("\n\nDate: " + cal.getTime());
                    emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
                    MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] Data collection deprecated: " + collection.getId(), emailBody.toString(), "text/plain; charset=UTF-8");
                    
                    // redirection
                    message = "The data collection <a href=\"./collections/" + collection.getId() + "\" title=\"Access to this data collection\">" + collection.getName() + "</a> has been deprecated.";
                    jsp = "/collections";
		        }
		        else
		        {
		        	// only display a message to the user, no email
		        	message = "The data collection '" + collection.getName() + "' could not be deprecated.";
                    jsp = "/collections";
		        }
            }
            else   // not a curator
            {
            	message = "Sorry, you are not authorised to access this page!";
            	jsp = "/user";
            }
        }
        else   // not logged in
        {
        	message = "You need to be authenticated to access this feature!";
            request.setAttribute("referrer", request.getQueryString());
            jsp = "login.jsp";
        }
        
        request.setAttribute("message", message);
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
