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


import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.SimpleDataType;
import uk.ac.ebi.miriam.db.Tag;
import uk.ac.ebi.miriam.db.TagDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * <p>Servlet which handles the addition of an association between a data type and a tag.
 * <p>This will not create a new tag in the database, cf. <code>ServletTagNew</code> for this feature.
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
 * @version 20130306
 */
public class ServletTagAdd extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 8565379517405168757L;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletTagAdd()
    {
        super();
    }
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String message = null;
        boolean success = false;
        boolean action = false;
        boolean dateUpdate = false;
        String jsp;
        
        // retrieves the user logged who asked for the action
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("login");
        
        // retrieves the identifier of the data type to be updated
        String dataId = request.getParameter("dataTypeId");
        
        // retrieves the identifier of the tag to be added
        String tagId = request.getParameter("tag");
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(session))
            {
                // retrieves the version of the web application (sid, alpha, main or demo)
                String version = getServletContext().getInitParameter("version");
                
                // retrieves the name of the database pool
                String poolName = getServletContext().getInitParameter("miriam_db_pool");
                
                // retrieves the email addresses of the administrator and the curators
                String emailAdmin = getServletContext().getInitParameter("admin.email");
                String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
                
                // access to the database functions
                TagDao tagDao = new TagDao(poolName);
                DataTypeDao dataDao = new DataTypeDao(poolName);
                
                // adds the new association, if the tag exists
                if ((tagDao.exists(tagId)) && (dataDao.dataTypeExists(dataId)))
                {
                    action = true;
                    success = tagDao.addAssociation(tagId, dataId);
                }
                else   // the tag doesn't exist
                {
                    message = "Sorry, the action cannot be performed because the tag or the data type doesn't exist!";
                }
                
                // update the last modification date of the data type
                if (success)
                {
                    dateUpdate = dataDao.updateLastModifDate(dataId);
                }
                
                // retrieves the current data type
                SimpleDataType data = dataDao.getSimpleDataTypeById(dataId);
                
                // retrieves the newly updated list of tags associated with the current data type
                List<Tag> currentTags = new ArrayList<Tag>();
                currentTags = tagDao.retrieveTags(dataId);
                // retrieves the list of all the available tags
                List<Tag> allTags = new ArrayList<Tag>();
                allTags = tagDao.retrieveAllTags();
                // remove the tags already associated with the data type
                allTags.removeAll(currentTags);
                
                // an action has been performed
                if (action)
                {
                    Tag tag = tagDao.getTagFromId(tagId);
                    
                    // email notification
                    StringBuilder emailBody = new StringBuilder();
                    emailBody.append("\nA new association between a data type and a tag has been created:");
                    emailBody.append("\n\nData type: " + data.getName() + " (" + data.getId() + ")");
                    emailBody.append("\nNew tag: " + tag.getName() + " (" + tag.getId() + ")");
                    if (success)
                    {
                        emailBody.append("\n\tNew association successfully added!");
                    }
                    else
                    {
                        emailBody.append("\n\tAn error occurred during the addition of this new association!");
                        emailBody.append("\n\tPlease check the state of the database...");
                    }
                    emailBody.append("\nCurrent list of tag(s):");
                    for (Tag t: currentTags)
                    {
                        emailBody.append("\n\t- " + t.getName() + " (" + t.getId() + ")");
                    }
                    emailBody.append("\n\nUser: " + user);
                    GregorianCalendar cal = new GregorianCalendar();
                    emailBody.append("\nDate: " + cal.getTime());
                    emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                    MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdmin, emailsCura, "[MIRIAM-" + version + "] Tag " + tagId + " added to " + dataId, emailBody.toString(), "text/plain");
                    
                    // message for the user
                    if (success)
                    {
                        message = "This data type is now associated with the tag '" + tag.getName() + "'.";
                        
                        if (! dateUpdate)
                        {
                            emailBody.append("\n\nThe last modification date of the data type has not been updated!");
                        }
                    }
                    else
                    {
                        message = "Sorry, an error occured during the addition of the tag. Therefore the result of this action cannot be guaranteed!";
                    }
                }
                
                // cleaning
                tagDao.clean();
                dataDao.clean();
                
                request.setAttribute("data", data);
                request.setAttribute("tags", currentTags);
                request.setAttribute("allTags", allTags);
                jsp = "tag_edit.jsp";
            }
            else   // user hasn't enough privileges
            {
                message = "Sorry, you are not authorised to access this page!";
                jsp = "/user";
            }
        }
        else   // user not logged
        {
            message = "Sorry this feature is not yet implemented! Alternatively, you can use the following form to send us your request.";
            request.setAttribute("info", "Add existing tag: " + tagId + " to: " + dataId);
            jsp = "support.jsp";
        }
        
        if (null != message)
        {
            request.setAttribute("message", message);
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
