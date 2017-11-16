/*
 * MIRIAM Registry (Web Application)
 * The Registry is an online resource created to catalogue data collections 
 * (their URIs and the corresponding physical URLs, whether these are controlled vocabularies or databases)
 * and provide unique and stable identifiers for life science, in the form of URIs. 
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2013  BioModels.net (EMBL - European Bioinformatics Institute)
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


import uk.ac.ebi.miriam.db.CuraDataTypeDao;

import java.io.IOException;
import java.util.GregorianCalendar;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;


/**
 * <p>Servlet that handles the publication of a data collection from the curation pipeline to the public registry.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2011  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20130704
 */
public class ServletPublishDataType extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 4805726678547283317L;
    private Logger logger = Logger.getLogger(ServletPublishDataType.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletPublishDataType()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String jsp = null;
        String message = null;
        HttpSession session = request.getSession();
        
        logger.debug("Publication of a data type...");
        
        // retrieves the identifier of the data type to publish
        String dataId = request.getParameter("dataId");
        String dataName = request.getParameter("dataName");
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(session))
            {
                // retrieves the user logged who asked for the action
                String user = (String) session.getAttribute("login");
                
                // retrieves the version of the web application (sid, alpha, main or demo)
                String version = getServletContext().getInitParameter("version");
                
                // retrieves the email addresses of the administrator and the curators
                String emailAdmin = getServletContext().getInitParameter("admin.email");
                String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
                
                // TODO: do some basic check to be sure that the data collection in the curation pipeline doesn't already exist in the public registry
                // retrieves the requested data collection
                String poolName = getServletContext().getInitParameter("miriam_db_pool");
                CuraDataTypeDao curaDao = new CuraDataTypeDao(poolName);
                String newId = curaDao.publish(dataId);
                curaDao.clean();
                
                if (null != newId)
                {
                    message = "The data collection '" + dataName + "' (" + dataId + ") is now available publicly: " + newId + "!";
                }
                else   // something when wrong...
                {
                    message = "Sorry, an error occurred while publishing the data collection '" + dataName + "' (" + dataId + ")!";
                }
                
                // email notification
                StringBuilder emailBody = new StringBuilder();
                emailBody.append("\nA data collection in the curation pipeline has been published:");
                emailBody.append("\n\nData collection: " + dataName + " (" + dataId + ")");
                if (null != newId)
                {
                    emailBody.append("\nPublic identifier: " + newId);
                }
                else
                {
                    emailBody.append("\n\tAn error occurred during the publication process!");
                    emailBody.append("\n\tPlease check the state of the database...");
                }
                emailBody.append("\n\nUser: " + user);
                GregorianCalendar cal = new GregorianCalendar();
                emailBody.append("\nDate: " + cal.getTime());
                emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
                MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdmin, emailsCura, "[Registry] Publication: " + dataName, emailBody.toString(), "text/plain");
                
                request.setAttribute("type", "all");
                jsp = "/curation";
            }
            else   // user hasn't enough privileges
            {
                message = "Sorry, you are not authorised to access this page!";
                jsp = "/user";
            }
        }
        else   // user not logged
        {
            message = "You need to be authenticated to access this feature!";
            request.setAttribute("referrer", request.getQueryString());
            jsp = "login.jsp";
        }
        
        if (null != message)
        {
            request.setAttribute("message", message);
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
