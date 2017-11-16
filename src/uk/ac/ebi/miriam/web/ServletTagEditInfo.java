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


import uk.ac.ebi.miriam.db.Tag;
import uk.ac.ebi.miriam.db.TagDao;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import org.apache.log4j.Logger;
import javax.servlet.http.HttpSession;


/**
 * <p>Servlet that handles the edition of the information (name and definition) of a tag (tags management page).
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
public class ServletTagEditInfo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 8107421786042636851L;
    //private Logger logger = Logger.getLogger(ServletTagEditInfo.class);
    
    
    /**
     * Default constructor.
     */
    public ServletTagEditInfo()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
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
        
        // retrieves the user logged (if any)
        HttpSession session = request.getSession();
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(session))
            {
                // retrieves the information about the tag
                String tagId = request.getParameter("tagId");
                String tagName = request.getParameter("tagName");
                String tagDef = request.getParameter("tagDef");
                
                if (! MiriamUtilities.isEmpty(tagName))
                {
                    tagName = tagName.trim();
                    if (null != tagDef)
                    {
                        tagDef = tagDef.trim();
                        
                        // doesn't store the definition if it is empty
                        if (tagDef.equalsIgnoreCase("None"))
                        {
                            tagDef = "";
                        }
                    }
                    if (null != tagId)
                    {
                        tagId = tagId.trim();
                    }
                    
                    Tag current = new Tag(tagId, tagName, tagDef);
                    
                    // retrieves the name of the database pool
                    String poolName = getServletContext().getInitParameter("miriam_db_pool");
                    
                    // tag management
                    TagDao tagDao = new TagDao(poolName);
                    
                    // updates of the name and definition of the tag
                    boolean result = tagDao.update(current);
                    
                    // cleaning
                    tagDao.clean();
                    
                    // message to the user
                    if (result)
                    {
                        message = "The tag '" + current.getName() + "' (" + current.getId() + ") has been updated with success.";
                    }
                    else
                    {
                        message = "An error occurred during the update of the tag '" + current.getName() + "' (" + current.getId() + ")!";
                    }
                }
                else   // the name of the tag should not be empty!
                {
                    message = "The update has failed: the name of the tag can't be empty!";
                }
                
                jsp = "/tagsEdit";
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
}
