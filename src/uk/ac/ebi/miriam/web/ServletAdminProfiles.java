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


import uk.ac.ebi.miriam.db.MyMiriamDao;
import uk.ac.ebi.miriam.db.Profile;

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * <p>
 * Servlet for the administration of the list of registered profiles in myMIRIAM.
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
 * @version 20140307
 */
public class ServletAdminProfiles extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 3666505177371456964L;
    
    
    /*
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletAdminProfiles()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String message = null;
        String jsp = null;
        
        // retrieves the session of the current user
        HttpSession session = request.getSession();
        
        // retrieves a potential message (for example if an admin wants to access a profile which does not exist)
        message = request.getParameter("message");
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has administration privileges
            if (MiriamUtilities.isUserAdministator(session) || MiriamUtilities.isUserCurator(session))
            {
                // retrieves the name of the database pool
                String poolName = getServletContext().getInitParameter("miriam_db_pool");
                
                // access to the database
                MyMiriamDao myMiriamDao = new MyMiriamDao(poolName);
                
                // retrieves the list of registered profile in myMIRIAM
                List<Profile> profiles = myMiriamDao.getAllProfiles();
                
                // retrieves additional info about each profile (number of data collections, date of last modification, ...)
                for (Profile profile: profiles)
                {
                    myMiriamDao.getAdditionalInfo(profile);
                }
                
                // cleaning
                myMiriamDao.clean();
                
                request.setAttribute("data", profiles);
                jsp = "admin_profiles.jsp";
            }
            else   // General user
            {
                // retrieves the name of the database pool
                String poolName = getServletContext().getInitParameter("miriam_db_pool");

                // access to the database
                MyMiriamDao myMiriamDao = new MyMiriamDao(poolName);

                // retrieves the list of registered profile in myMIRIAM
                List<Profile> profiles = myMiriamDao.getUserProfiles((String)request.getSession().getAttribute("login"));

                // retrieves additional info about each profile (number of data collections, date of last modification, ...)
                for (Profile profile: profiles)
                {
                    myMiriamDao.getAdditionalInfo(profile);
                }

                // cleaning
                myMiriamDao.clean();

                request.setAttribute("data", profiles);
                jsp = "admin_profiles.jsp";
/*
                message = "Sorry, you are not authorised to access this page!";
                jsp = "/user";*/
            }
        }
        else   // user not logged
        {
            // retrieves the name of the database pool
            String poolName = getServletContext().getInitParameter("miriam_db_pool");

            // access to the database
            MyMiriamDao myMiriamDao = new MyMiriamDao(poolName);

            // retrieves the list of registered profile in myMIRIAM
            List<Profile> profiles = myMiriamDao.getPublicProfiles();

            // retrieves additional info about each profile (number of data collections, date of last modification, ...)
            for (Profile profile: profiles)
            {
                myMiriamDao.getAdditionalInfo(profile);
            }

            // cleaning
            myMiriamDao.clean();

            request.setAttribute("data", profiles);
            jsp = "admin_profiles.jsp";

/*            message = "Sorry, you need to be authenticated to access this page!";
            request.setAttribute("referrer", request.getQueryString());
            jsp = "login.jsp";*/
        }
        
        if (null != message)
        {
            if (jsp.endsWith(".jsp"))
            {
                request.setAttribute("message", "<p>" + message + "</p>");   // direct display
            }
            else
            {
                request.setAttribute("message", message);   // reuse
            }
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
}
