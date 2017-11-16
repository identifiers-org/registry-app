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
import java.util.GregorianCalendar;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.jasypt.util.password.StrongPasswordEncryptor;


/**
 * <p>Servlet that handles the password change function (update the password and send a reminder to the user).
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
public class ServletPassChange extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -2508702980340674515L;
    private Logger logger = Logger.getLogger(ServletPassChange.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletPassChange()
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
        String jsp = "login.jsp";   // default value
        
        // to be able to retrieve UTF-8 elements from HTML forms
        request.setCharacterEncoding("UTF-8");
        
        // recovery of the parameters from the form
        String oldPass = request.getParameter("oldPass");
        String newPass1 = request.getParameter("newPass1");
        String newPass2 = request.getParameter("newPass2");
        
        if ((null != oldPass) && (null != newPass1) && (null != newPass2))
        {
            oldPass = oldPass.trim();
            newPass1 = newPass1.trim();
            newPass2 = newPass2.trim();
            
            // the user is logged
            if (MiriamUtilities.isUserAuthorised(request.getSession()))
            {
                // checks if the two new passwords are equals
                if (newPass1.equals(newPass2))
                {
                	// retrieves the name of the database pool
                    String poolName = getServletContext().getInitParameter("auth_db_pool");
                	
                    // database connection
                    UserDao dao = new UserDao(poolName);
                    
                    // retrieves the login of the current user
                    HttpSession session = request.getSession(false);   // returns pre-existing session or null
                    String login = (String) session.getAttribute("login");
                    
                    // retrieves the user record
                    User loggedUser = dao.retrieveUser(login);
                    
                    if (null != loggedUser)
                    {
	                    // checks if the old password given is the same than the one stored in the database
	                    StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
	                    if (passwordEncryptor.checkPassword(oldPass, loggedUser.getPassword()))   // passwords are equal
	                    {
	                    	// encrypt the new password
	                        String encryptedPassword = passwordEncryptor.encryptPassword(newPass1);
	                        
	                        // updates the database
	                        Boolean updated = dao.updateUserPassword(loggedUser.getLogin(), encryptedPassword);
	                        
	                        if (! updated)
	                        {
	                        	message = "Sorry, we were unable to update your password!<br /> Please contact us <a href=\"mdb?section=contribute#team\" title=\"Contact page\">via this page</a> to solve this issue. Thank you.";
	                        }
	                        else
	                        {
	                        	// retrieves the version of the web application (sid, alpha, main or demo)
	                            String version = getServletContext().getInitParameter("version");
	                            
	                            // retrieves the email address of the administrator
	                            String emailAdmin = getServletContext().getInitParameter("admin.email");
	                            
	                            // sending the email
	                            StringBuilder emailBody = new StringBuilder();
	                            emailBody.append("Dear " + loggedUser.getFirstName() + " " + loggedUser.getLastName() + ",");
	                            GregorianCalendar cal = new GregorianCalendar();
	                            emailBody.append("\n\nDate: " + cal.getTime());
	                            emailBody.append("\nThis is a confirmation that your password to access Identifiers.org Registry has been successfully updated.");
	                            emailBody.append("\nIf you have problems to login, please contact: <" + emailAdmin + ">.");
	                            emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
	                            MailFacade.send("Registry-" + version + "@ebi.ac.uk", loggedUser.getEmail(), "[Registry] Password updated", emailBody.toString(), "text/plain; charset=UTF-8");
	                            
	                            logger.info("Password updated for the user '" + login + "', email notification sent to " + loggedUser.getEmail());
	                            
	                            message = "Your password has now been successfully updated!";
	                        }
	                    }
	                    else   // passwords are different
	                    {
	                        message = "As long as you don't accurately provide your current password, we can't update it!";
	                    }
                    }
                    else   // safety...
                    {
                        message = "Sorry, we are unable to update your password!<br /> Please contact us <a href=\"mdb?section=contribute#team\" title=\"Contact page\">via this page</a> to solve this issue. Thank you.";
                    }
                    
                    dao.clean();
                }
                else   // the new passwords are different
                {
                    message = "The new password and its confirmation need to be equal!";
                }
                jsp = "/user";
            }
            else   // not logged
            {
                message = "You need to be authenticated to access this feature!";
                jsp = "login.jsp";
            }
        }
        else   // some parameters are null
        {
            message = "Missing information: please fill all the fields.";
            jsp = "/user";
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
