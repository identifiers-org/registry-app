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

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>Servlet that handles the change of a myMIRIAM project key.
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
public class ServletUpdateMyMiriamProjectKey extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -4815439886990506840L;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletUpdateMyMiriamProjectKey()
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
        String oldKey = request.getParameter("oldKey");
        String newKey1 = request.getParameter("newKey1");
        String newKey2 = request.getParameter("newKey2");
        String projectId = request.getParameter("projectId");
        
        if ((null != oldKey) && (null != newKey1) && (null != newKey2) && (null != projectId))
        {
            oldKey = oldKey.trim();
            newKey1 = newKey1.trim();
            newKey2 = newKey2.trim();
            Integer projectIdInt = Integer.parseInt(projectId);   // the project identifier is actually an integer
            
            // the user is logged
            if (MiriamUtilities.isUserAuthorised(request.getSession()))
            {
                // the user is allowed to access this project
/*                if (MiriamUtilities.hasAccessToProject(request.getSession(), projectIdInt))
                {*/
                    // retrieves the name of the database pool
                    String poolName = getServletContext().getInitParameter("miriam_db_pool");
                    
                    // access to the database
                    MyMiriamDao dao = new MyMiriamDao(poolName);
                    
                    // checks if the previous key is correct
                    if (dao.checkKey(projectIdInt, oldKey))
                    {
                        // checks if the two new keys are equals
                        if (newKey1.equals(newKey2))
                        {
                            // updates key in database
                            if (dao.updateKey(projectIdInt, newKey1))
                            {
                                message = "The key of this profile has been successfully updated!";
                            }
                            else   // failure to update the key
                            {
                                message = "Sorry, we were unable to update the key of this profile!<br /> Please contact us <a href=\"mdb?section=contribute#team\" title=\"Contact page\">via this page</a> to solve this issue. Thank you.";
                            }
                        }
                        else   // the new keys are different
                        {
                            message = "The new key and its confirmation need to be equal!";
                        }
                    }
                    else   // the old key is incorrect
                    {
                        message = "As long as you don't accurately provide the current key, we can't update it!";
                    }
                    
                    // cleaning
                    dao.clean();
                    
                    request.setAttribute("id", projectId);
                    jsp = "/adminProfile";
/*                }
                else   // the current user has no access to this project
                {
                    message = "Sorry, you don't have access to the requested profile.";
                    jsp = "/user";
                }*/
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
            if (null != projectId)
            {
                request.setAttribute("id", projectId);
                jsp = "/adminProfile";
            }
            else   // list of all projects, TODO: change that in the future, as not everybody could see the list of all projects
            {
                jsp = "/adminProfiles";
            }
        }
        
        if (null != message)
        {
            request.setAttribute("message", message);
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
