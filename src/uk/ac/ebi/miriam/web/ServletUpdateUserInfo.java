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


import uk.ac.ebi.miriam.db.User;
import uk.ac.ebi.miriam.db.UserDao;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;


/**
 * <p>Servlet that handles the update of a user information.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2014  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20140307
 */
public class ServletUpdateUserInfo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -62371977743718760L;
    private Logger logger = Logger.getLogger(ServletUpdateUserInfo.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletUpdateUserInfo()
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
        String jsp;
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(request.getSession()))
        {
            // to be able to retrieve UTF-8 elements from HTML forms
            request.setCharacterEncoding("UTF-8");
            
            // recovery of the parameters from the form
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String organisation = request.getParameter("organisation");
            
            // retrieves the login of the current user
            HttpSession session = request.getSession(false);   // returns pre-existing session or null
            String login = (String) session.getAttribute("login");
            
            // all the parameters must not be empty
            if ((!MiriamUtilities.isEmpty(firstName)) && (!MiriamUtilities.isEmpty(lastName)) && (!MiriamUtilities.isEmpty(email)) && (!MiriamUtilities.isEmpty(organisation)))
            {
            	User user = new User();
                user.setLogin(login);
                user.setFirstName(firstName.trim());
                user.setLastName(lastName.trim());
                user.setEmail(email.trim());
                user.setOrganisation(organisation.trim());
                
                // email valid (very simple check)
                if (user.getEmail().contains("@"))
                {
                    // retrieves the name of the database pool
                    String poolName = getServletContext().getInitParameter("auth_db_pool");
                    UserDao dao = new UserDao(poolName);
                	
                    if (dao.updateUserInfo(user))
                    {
                        message = "Your user information have been updated successfully!";
                        logger.debug("User information updated for '" + login + "'");
                    }
                    else
                    {
                        message = "Sorry, we are unable to update your user informations!<br /> Please contact us <a href=\"mdb?section=contribute#team\" title=\"Contact page\">via this page</a> to solve this issue. Thank you.";
                        logger.warn("Unable to update the user information of '" + login + "'!");
                    }
                    
                    dao.clean();
                }
                else
                {
                    message = "The email address must be valid!";
                }
            }
            else
            {
                message = "All the fields must be populated!";
            }
            jsp = "/user";
        }
        else   // user not logged
        {
            message = "You need to be authenticated to access this feature!";
            jsp = "login.jsp";
        }
        
        if (null != message)
        {
            request.setAttribute("message", message);
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
    
    
    /** 
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
        // nothing here.
    }
}
