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
import org.jasypt.util.password.StrongPasswordEncryptor;
import java.util.GregorianCalendar;


/**
 * <p>Servlet that handles the password reset function (generates a new random one and send it to the user).
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
public class ServletPassReminder extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 1865215447530932510L;
    private Logger logger = Logger.getLogger(ServletPassReminder.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletPassReminder()
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
        HttpSession session = request.getSession();
        
        // to be able to retrieve UTF-8 elements from HTML forms
        request.setCharacterEncoding("UTF-8");
        
        // recovery of the parameters from the form
        String login = request.getParameter("username");
        
        if (null != login)
        {
            login = login.trim();
            
	        // retrieves the name of the database pool
	        String poolName = getServletContext().getInitParameter("auth_db_pool");
	        
	        // database connection
	        UserDao dao = new UserDao(poolName);
	        
	        // checks if the user exists in the our records
	        User user = dao.retrieveUser(login);
	        
	        // a user with this login exists in the database
	        if (null != user)
	        {
	            // creates a new (random) password
	            String newPass = (MiriamUtilities.randomPassGen(12));
	            // encrypts the password
	            StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
	            String encryptedPassword = passwordEncryptor.encryptPassword(newPass);
	            
	            // updates the database
	            Boolean updated = dao.updateUserPassword(user.getLogin(), encryptedPassword);
	            
	            if (! updated)   // failure to update the password
	            {
	                message = "Sorry, we were unable to reset your password!<br /> Please <a href=\"mdb?section=contribute#team\" title=\"Contact page\">contact us</a> to solve this issue. Thank you.";
	                logger.warn("Unable to reset the password of '" + login + "'!");
	            }
	            else
	            {
	                // retrieves the version of the web application (sid, alpha, main or demo)
	                String version = getServletContext().getInitParameter("version");
	                
	                // retrieves the email address of the administrator
	                String emailAdmin = getServletContext().getInitParameter("admin.email");
	                
	                // sending the email
	                StringBuilder emailBody = new StringBuilder();
	                emailBody.append("Dear " + user.getFirstName() + " " + user.getLastName() + ",");
	                GregorianCalendar cal = new GregorianCalendar();
	                emailBody.append("\n\nDate: " + cal.getTime());
	                emailBody.append("\nYou asked for the reinitialisation of your password in order to access Identifiers.org Registry.");
	                emailBody.append("\nHere are your new login details:");
	                emailBody.append("\nlogin: " + login);
	                emailBody.append("\npassword: " + newPass);
	                emailBody.append("\n\nWe strongly suggest you to change the password for a more convenient one (which can be done after login on the website).");
	                emailBody.append("\nIf you still have problems to login, please contact: <" + emailAdmin + ">.");
	                emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
	                MailFacade.send("Registry-" + version + "@ebi.ac.uk", user.getEmail(), "[Registry] Password reset", emailBody.toString(), "text/plain; charset=UTF-8");
	                
	                logger.info("Password reset for the user '" + login + "', email sent to " + user.getEmail());
	                message = "Your password has been successfully sent to the email address registered in our users database.<br />Should you experience any problem in receiving the new password, please <a href=\"mdb?section=contribute#team\" title=\"Contact page\">contact us</a>.";
	            }
	        }
	        else
	        {
	            message = "We are very sorry, but we cannot perform the requested operation.<br /> Please <a href=\"mdb?section=contribute#team\" title=\"Contact page\">contact us</a>.";
	            logger.info("An anonymous user wanted to reset a password for the non existing login '" + login + "'!");
	        }
	        
	        dao.clean();
        }
        else
        {
        	message = "You need to provide a username to use the password reset function.";
        }
        
        jsp = "login.jsp";
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
