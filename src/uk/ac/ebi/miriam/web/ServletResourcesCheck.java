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


import uk.ac.ebi.miriam.db.ResourceDao;
import uk.ac.ebi.miriam.db.ResourcesCheckReport;

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * <p>Servlet that handles the display of a general report of the regular resources health check process.
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
 * @version 20130704
 */
public class ServletResourcesCheck extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final long serialVersionUID = 622483727856967471L;
	// type of resources to display
	private final String ALL = "All";
	
	
	/**
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ServletResourcesCheck()
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
        String type = null;
        String jsp = null;
        
        // retrieves the user logged (if any)
        HttpSession session = request.getSession();
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(session))
            {
                List<ResourcesCheckReport> resources = null;
                
                // retrieves the name of the database pool
                String poolName = getServletContext().getInitParameter("miriam_db_pool");
                ResourceDao resDao = new ResourceDao(poolName);
                
                // retrieves the parameter corresponding to the type of resources to display ('up', 'down', 'probably up', 'unknown' or 'obsolete')
                type = request.getParameter("type");
                
                if (null != type)
                {
                    // retrieves the list of resources with simplified health report, according to the given type
                    if (type.equalsIgnoreCase(ResourceDao.getShortStateDesc(ResourceDao.STATE_SUCCESS)))
                    {
                        resources = resDao.getCheckReport(ResourceDao.STATE_SUCCESS);
                        type = ResourceDao.getStateDesc(ResourceDao.STATE_SUCCESS);
                    }
                    else if (type.equalsIgnoreCase(ResourceDao.getShortStateDesc(ResourceDao.STATE_FAILURE)))
                    {
                        resources = resDao.getCheckReport(ResourceDao.STATE_FAILURE);
                        type = ResourceDao.getStateDesc(ResourceDao.STATE_FAILURE);
                    }
                    else if (type.equalsIgnoreCase(ResourceDao.getShortStateDesc(ResourceDao.STATE_PROBABLY)))
                    {
                        resources = resDao.getCheckReport(ResourceDao.STATE_PROBABLY);
                        type = ResourceDao.getStateDesc(ResourceDao.STATE_PROBABLY);
                    }
                    else if (type.equalsIgnoreCase(ResourceDao.getShortStateDesc(ResourceDao.STATE_UNKNOWN)))
                    {
                        resources = resDao.getCheckReport(ResourceDao.STATE_UNKNOWN);
                        type = ResourceDao.getStateDesc(ResourceDao.STATE_UNKNOWN);
                    }
                    else if (type.equalsIgnoreCase(ResourceDao.getShortStateDesc(ResourceDao.STATE_OBSOLETE)))
                    {
                        resources = resDao.getCheckReport(ResourceDao.STATE_OBSOLETE);
                        type = ResourceDao.getStateDesc(ResourceDao.STATE_OBSOLETE);
                    }
                    else if (type.equalsIgnoreCase(ResourceDao.getShortStateDesc(ResourceDao.STATE_RESTRICTED)))
                    {
                        resources = resDao.getCheckReport(ResourceDao.STATE_RESTRICTED);
                        type = ResourceDao.getStateDesc(ResourceDao.STATE_RESTRICTED);
                    }
                    else if (type.equalsIgnoreCase(this.ALL))
                    {
                        resources = resDao.getCheckReport();
                    }
                    else
                    {
                        resources = resDao.getCheckReport();
                        type = "all";
                    }
                    request.setAttribute("type", type.toUpperCase());
                }
                else   // default case, no type given
                {
                    resources = resDao.getCheckReport(ResourceDao.STATE_FAILURE);
                    request.setAttribute("type", ResourceDao.getStateDesc(ResourceDao.STATE_FAILURE).toUpperCase());
                }
                
                // cleaning
                resDao.clean();
                
                request.setAttribute("data", resources);
                request.setAttribute("counter", resources.size());
                jsp = "resources_check.jsp";
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
        
        // sends information of the result of the process to the user
        if (null != message)
        {
            request.setAttribute("message", message);
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
