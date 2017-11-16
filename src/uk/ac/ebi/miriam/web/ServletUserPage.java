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
 * <p>Servlet that handles the personal page of a logged user.
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
 * @version 20140113
 */
public class ServletUserPage extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -1641170917109370773L;
    private Logger logger = Logger.getLogger(ServletUserPage.class);
    
    
    /**
     * Default constructor.
     */
    public ServletUserPage()
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
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        RequestDispatcher view = null;
        String message = null;
        String jsp;
        
        // retrieves the user logged (if any)
        HttpSession session = request.getSession();
        String login = null;
        //String role = null;
        login = (String) session.getAttribute("login");
        //role = (String) session.getAttribute("role");
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session) || MiriamUtilities.isUserGeneral(session))
        {
        	// retrieves the name of the database pool
            String poolName = getServletContext().getInitParameter("auth_db_pool");
            
            UserDao dao = new UserDao(poolName);
            User user = dao.retrieveUser(login);
            dao.clean();
            
            if (null != user)   // successful retrieval of the personal data of the user
            {
                request.setAttribute("data", user);
                jsp = "user.jsp";
            }
            else   // the personal data of the user can't be retrieved from the database
            {
                logger.warn("Can't retrieve personnal information about '" + login + "'");
                request.setAttribute("message", "<p>We are unable to retrieve your user information!<br /> Please contact us in order to solve this issue. We apologise for any inconvenience caused.</p>");
                jsp = "login.jsp";
            }
        }
        else   // user not logged
        {
            message = "Sorry, you need to be authenticated to access this page!";
            jsp = "login.jsp";
        }
        
        // sends information of the result of the process to the user
        if (null != message)
        {
            request.setAttribute("message", message);
        }
        view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
    
    
    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException
    {
        // nothing here.
    }
}
