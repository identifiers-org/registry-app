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


/**
 * <p>Servlet that handles the authentication of the user.
 *
 * <p>
 * Direct connection to the Web Authentication database ('web-auth'), using PreparedStatements.
 * <p>
 * All passwords are stored encrypted with a one-way technique. cf. http://www.jasypt.org/
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
 * @version 20140114
 */
 public class ServletSignIn extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
 {
    private static final long serialVersionUID = 1865215447530932510L;
    private Logger logger = Logger.getLogger(ServletSignIn.class);
    
    
    /*
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletSignIn()
    {
        super();
    }
    
    
    /*
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    
    
    /*
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String role = new String();
        RequestDispatcher view = null;
        boolean success = false;
        
        // to be able to retrieve UTF-8 elements from HTML forms
        request.setCharacterEncoding("UTF-8");

        // recovery of the parameters from the form
        String login = request.getParameter("username");
        String pass = request.getParameter("password");
        String referrer = request.getParameter("referrer");
        
        if (null != login)
        {
            login = login.trim();
        }
        if (null != pass)
        {
            pass = pass.trim();
        }
        if (null != referrer)
        {
            referrer = referrer.trim();
        }
        
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("auth_db_pool");
    	
        // database connection
        UserDao dao = new UserDao(poolName);
        
        // attempts to retrieve the details of the user
        User user = dao.retrieveUser(login);
        
        // the user is registered in the database
        if (null != user)
        {
            // tests if the password provided is the same as the one stored in the database
            StrongPasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
            if (passwordEncryptor.checkPassword(pass, user.getPassword()))
            {
                // tests if a user has access to the application 'miriam'
            	Boolean hasAccess = dao.checkUserAccess(user.getLogin(), dao.REGISTRY_APP);
                
                // the user has access to this application
                if (hasAccess)
                {
                    // retrieves the role of a specific user for a specific application
                	role = dao.retrieveUserRole(user.getLogin(), dao.REGISTRY_APP);
                    
                    // creation of a new session, with attributes (login and role)
                    HttpSession session = request.getSession();
                    session.setAttribute("login", user.getLogin());
                    session.setAttribute("role", role);
                    
                    // update the date of last login (not important if failure to do so)
                    dao.updateUserLastLogin(user.getLogin(), role, dao.REGISTRY_APP);
                    
                    success = true;
                    logger.info("The user '" + login + "' is now logged in!");
                }
                else   // the user has no access to the app
                {
                    logger.warn("The user '" + login + "' is not registered in the Registry, but tried to access it!");
                }
            }
            else
            {
                logger.warn("The user '" + login + "' tried to log in with a wrong password!");
            }
        }
        else   // for safety...
        {
            logger.warn("The user '" + login + "' (not registered in the User database) tried to log in!");
        }
        
        // cleaning
        dao.clean();
        
        // login successful
        if (success)
        {
            if ((null != referrer) && (! referrer.matches("\\s*")))
            {
                logger.debug("login success, forward to: " + referrer);
                response.sendRedirect("mdb?" + referrer);
            }
            else
            {
                view = request.getRequestDispatcher("/user");
                view.forward(request, response);
            }
        }
        else   // login failure
        {
            request.setAttribute("message", "Invalid username or password! Please, try again.");
            if ((null != referrer) && (! referrer.matches("\\s*")))
            {
                request.setAttribute("referrer", referrer);
            }
            view = request.getRequestDispatcher("login.jsp");
            view.forward(request, response);
        }
    }
    
    
    /*
     * @see javax.servlet.GenericServlet#init()
     */
    public void init() throws ServletException
    {
        // Context env = null;
        /*
         * logger.debug("--> SignIn: 'init' method..."); try { // Obtain our environment naming context Context
         * initContext = new InitialContext(); logger.debug("SignIn: context ok..."); Context envContext = (Context)
         * initContext.lookup("java:/comp/env"); logger.debug("initial context ok..."); // Look up our data source pool =
         * (DataSource) envContext.lookup("jdbc/MiriamDB"); logger.debug("SignIn: pool ok..."); if (pool == null) {
         * logger.debug("poool is nulll"); throw new ServletException("'Miriam' is an unknown DataSource"); } else {
         * logger.debug("pool is not null."); // Allocate and use a connection from the pool //Connection conn =
         * pool.getConnection(); //... use this connection to access the database ... //conn.close(); } } catch
         * (NamingException ne) { logger.debug("Exception lauched: " + ne.getMessage()); throw new
         * ServletException(ne.getMessage()); } catch (Exception se) { logger.debug("Exception standard lauched:" +
         * se.getMessage()); } logger.debug("SignIn: init end.");
         */
    }
}
