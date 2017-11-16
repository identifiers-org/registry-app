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


import uk.ac.ebi.miriam.db.ResourceCheckDetails;
import uk.ac.ebi.miriam.db.ResourceDao;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;
//import org.apache.log4j.Logger;


/**
 * <p>Servlet that handles the display of the report of the URL checking system for a specific resource.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2013  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20130807
 */
public class ServletResourceCheckDetails extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final long serialVersionUID = -5068160764787981773L;
	//private Logger logger = Logger.getLogger(ServletResourceCheckDetails.class);
	
	
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletResourceCheckDetails()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String message = null;
    	String jsp = null;
    	String id = null;
    	
    	// the user is logged
        if (MiriamUtilities.isUserAuthorised(request.getSession()))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(request.getSession()))
            {
            	// retrieves the parameter (identifier of a resource)
                id = request.getParameter("id");
                
                // valid identifier
                if ((! MiriamUtilities.isEmpty(id)) && (id.matches("MIR:001\\d{5}")))   // MIR:00100005
                {
                    // retrieves the name of the database pool
                    String poolName = getServletContext().getInitParameter("miriam_db_pool");
                    ResourceDao resDao = new ResourceDao(poolName);
                    
                    // retrieves the data corresponding to the given identifier
                	ResourceCheckDetails details = resDao.getCheckDetails(id);
                	
                	// resource exists in database
                	if (null != details)
                	{
                    	// better HTML validity
                    	details.setLogs(StringEscapeUtils.escapeHtml4(details.getLogs()));
                    	details.setErrors(StringEscapeUtils.escapeHtml4(details.getErrors()));
                    	
                    	request.setAttribute("data", details);
                    	jsp = "resource_check_details.jsp";
                	}
                	else
                	{
                	    message = "The requested resource (" + id + ") doesn't exist!";
                        jsp = "/resourcesCheck";
                	}
                    
                    // cleaning
                    resDao.clean();
                }
                else
                {
                    message = "Invalid resource identifier!";
                    jsp = "/resourcesCheck";
                }
            }
            else   // not enough privileges
            {
                message = "Sorry, you are not authorised to access this page!";
                jsp = "/user";
            }
        }
        else   // not logged
        {
            message = "You need to be authenticated to access this feature!";
            request.setAttribute("referrer", request.getQueryString());
            jsp = "login.jsp";
        }
        
        // sends information of the result of the process to the user
        if (null != message)
        {
            request.setAttribute("message", message);
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
