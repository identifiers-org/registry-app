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
import uk.ac.ebi.miriam.tools.CommonFunctions;

import java.io.IOException;
import java.util.GregorianCalendar;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * <p>Servlet that handles the update of the keyword used for health checking the resources.
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
 * @version 20130827
 */
public class ServletUpdateCheckKeyword extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -6097031843545130236L;
    
    
    /**
     * Default constructor.
     */
    public ServletUpdateCheckKeyword()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String message = null;
        String jsp = null;
        
        // to be able to retrieve UTF-8 elements from HTML forms
        request.setCharacterEncoding("UTF-8");
        
        // retrieves the user logged (if any)
        HttpSession session = request.getSession();
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(session))
            {
                // retrieves the identifier of the resource to be updated
                String resourceId = CommonFunctions.cleanHtmlField(request.getParameter("resourceId"));
                // retrieves the new keyword to be used
                String keyword = CommonFunctions.cleanHtmlField(request.getParameter("keyword"));
                
                // the given identifier is valid
                if ((null != resourceId) && (resourceId.matches("MIR:001\\d{5}")))   // MIR:00100005
                {
                    // retrieves the name of the database pool
                    String poolName = getServletContext().getInitParameter("miriam_db_pool");
                    
                    // data type management
                    ResourceDao resDao = new ResourceDao(poolName);
                    
                    // update of the keyword
                    boolean success = resDao.updateCheckKeyword(resourceId, keyword);
                    
                    // cleaning
                    resDao.clean();
                    
                    if (success)
                    {
                        emailNotification(resourceId, keyword, (String) session.getAttribute("login"));
                        message = "The keyword used for the health check of <a href=\"mdb?section=health_check_details&id=" + resourceId + "\" title=\"Access to the detailed health report\">" + resourceId + "</a> has been updated successfully!";
                    }
                    else
                    {
                        message = "An error occurred while trying to update the keyword used for the health check of <a href=\"mdb?section=health_check_details&id=" + resourceId + "\" title=\"Access to the detailed health report\">" + resourceId + "</a>!";
                    }
                    jsp = "/resourcesCheck";
                }
                else   // not a valid identifier
                {
                    message = "An attempt to update the check keyword of a resource failed (wrong identifier)!";
                    jsp = "/resourcesCheck";
                }
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


    private void emailNotification(String resourceId, String keyword, String user)
    {
        // retrieves the version of the web application (sid/local, alpha, main or demo)
        String version = getServletContext().getInitParameter("version");
        
        // retrieves the email addresses of the administrator and the curators
        String emailAdr = getServletContext().getInitParameter("admin.email");
        String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
        
        StringBuilder emailBody = new StringBuilder();
        
        emailBody.append("\nThe keyword used to check the health of the resource '" + resourceId + "' has been changed.\n");
        emailBody.append("The new keyword is:\n");
        emailBody.append("\n\t" + keyword + "\n");
        emailBody.append("\n\nUser: " + user);
        GregorianCalendar cal = new GregorianCalendar();
        emailBody.append("\nDate: " + cal.getTime());
        emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
        MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] Keyword changed: '" + resourceId + "'", emailBody.toString(), "text/plain; charset=UTF-8");
        
    }
}
