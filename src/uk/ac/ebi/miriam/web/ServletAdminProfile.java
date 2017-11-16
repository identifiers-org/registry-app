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
import uk.ac.ebi.miriam.db.MyMiriamDataType;
import uk.ac.ebi.miriam.db.Profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * <p>
 * Servlet for the administration of a given registered profile.
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
 * @version 20140305
 */
public class ServletAdminProfile extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -9147961585338870763L;
    
    
    /*
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletAdminProfile()
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
        
        // retrieves potential previous message
        message = request.getParameter("message");   // TODO: can be overwritten later, need to find a better solution...
        
        // retrieves the (internal) identifier of the profile
        String profileIdStr = request.getParameter("id");
        if (null == profileIdStr)
        {
        	profileIdStr = (String) request.getAttribute("id");   // useful if the parameter is sent from another Servlet
        }
        Integer profileId = 0;
        
        // retrieves the set of data types (associated or not) to be displayed: A | B | ... | Z | Selected
        String displaySet = request.getParameter("set");
        
        // the user is logged
/*        if (MiriamUtilities.isUserAuthorised(session))
        {*/
            // the user has administration privileges
/*
            if (MiriamUtilities.isUserAdministator(session))
            {
*/
                if (null != profileIdStr)
                {
                    // get integer form of the identifier
                	profileId = Integer.parseInt(profileIdStr);
                    
                    // retrieves the name of the database pool
                    String poolName = getServletContext().getInitParameter("miriam_db_pool");
                    
                    // database access
                    MyMiriamDao myMiriamDao = new MyMiriamDao(poolName);
                    
                    // the requested profile exists
                    if (myMiriamDao.isProfileExisting(profileId))
                    {
                        // retrieves the profile
                        Profile profile = myMiriamDao.getProfile(profileId);
                        myMiriamDao.getAdditionalInfo(profile);
                        
                        // the list of data types to be display should be the one selected by the profile
                        List<MyMiriamDataType> datatypes = new ArrayList<MyMiriamDataType>();
                        if ((null == displaySet) || ((null != displaySet) && (displaySet.matches("\\s*"))) || ((null != displaySet) && (displaySet.equalsIgnoreCase("selected"))))
                        {
                            displaySet = "selected";
                            datatypes = myMiriamDao.getDataTypesOfProfile(profileId);
                        }
                        else   // display data types (official name) started by a given letter
                        {
                            datatypes = myMiriamDao.getDataTypesStartingByOfProfile(profileId, displaySet);
                        }
                        
                        request.setAttribute("set", displaySet);
                        request.setAttribute("project", profile);
                        request.setAttribute("datatypes", datatypes);
                        request.setAttribute("nbData", datatypes.size());
                        jsp = "admin_profile.jsp";
                    }
                    else   // the profile does not exist
                    {
                        message = "Sorry, the requested profile does not exist!";
                        jsp = "/adminProfiles";
                    }
                    
                    // cleaning
                    myMiriamDao.clean();
                }
                else   // id of the profile is null
                {
                    message = "Which profile are you looking for?";
                    jsp = "/adminProfiles";
                }
/*            }
            else   // user hasn't enough privileges
            {
                message = "Sorry, you are not authorised to access this page!";
                jsp = "/user";
            }*/
/*        }
        else   // user not logged
        {
            message = "Sorry, you need to be authenticated to access this page!";
            request.setAttribute("referrer", request.getQueryString());
            jsp = "login.jsp";
        }*/
        
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
