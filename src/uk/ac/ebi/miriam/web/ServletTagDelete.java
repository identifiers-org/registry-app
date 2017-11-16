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
//import org.apache.log4j.Logger;


/**
 * <p>Servlet which handles the deletion of the association between a data type and a tag.
 * <p>This will *not* delete a tag from the database!
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
public class ServletTagDelete extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -4660973147338465115L;
    //private Logger logger = Logger.getLogger(ServletTagDelete.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletTagDelete()
    {
        super();
    }
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        StringBuilder message = new StringBuilder();
        StringBuilder emailPart = new StringBuilder();
        boolean state;
        boolean dateUpdate = false;
        String jsp;
        
        // retrieves the user logged who asked for the action
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("login");
        
        // retrieves the identifier of the data type to be updated
        String dataId = request.getParameter("dataTypeId");
        
        // retrieves the ids of all the tags which need to be removed from being associated with the current data type
        String[] tags = request.getParameterValues("tags2remove");
        
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        // one or more tags selected
        if (null != tags)
        {
            // the user is logged
            if (MiriamUtilities.isUserAuthorised(session))
            {
                // the user has curation privileges
                if (MiriamUtilities.isUserCurator(session))
                {
                    message.append("Result of the deletion:<br />");
                    
                    // retrieves the version of the web application (sid, alpha, main or demo)
                    String version = getServletContext().getInitParameter("version");
                    
                    // retrieves the email addresses of the administrator and the curators
                    String emailAdmin = getServletContext().getInitParameter("admin.email");
                    String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
                    
                    // access to the Tag database functions
                    TagDao tagDao = new TagDao(poolName);
                    DataTypeDao dataDao = new DataTypeDao(poolName);
                    
                    // removes the association between the data type and the selected tag(s)
                    for (String tag: tags)
                    {
                        state = tagDao.removeTag(tag, dataId);
                        String tmpName = tagDao.getTagName(tag);
                        if (state)
                        {
                            message.append("The following tag has been successfully removed: " + tmpName + "<br />");
                            emailPart.append("\n\t- " + tmpName + " (" + tag + "): removed");
                            // update the last modification date of the data type
                            dateUpdate = dataDao.updateLastModifDate(dataId);
                        }
                        else
                        {
                            message.append("An error occurred during the removal of the following data type: " + tmpName + "<br />");
                            emailPart.append("\n\t+ " + tmpName + " (" + tag + "): an error occurred!");
                        }
                    }
                    
                    // retrieves the current data type
                    SimpleDataType data = dataDao.getSimpleDataTypeById(dataId);
                    dataDao.clean();
                    
                    // retrieves the newly updated list of tags associated with the current data type
                    List<Tag> currentTags = new ArrayList<Tag>();
                    currentTags = tagDao.retrieveTags(dataId);
                    // retrieves the list of all the available tags
                    List<Tag> allTags = new ArrayList<Tag>();
                    allTags = tagDao.retrieveAllTags();
                    // remove the tags already associated with the data type
                    allTags.removeAll(currentTags);
                    tagDao.clean();
                    
                    // email notification to the admin and curators
                    StringBuilder emailBody = new StringBuilder();
                    emailBody.append("\nThe association between a data type and one or more tags has been removed:");
                    emailBody.append("\n\nData type: " + data.getName() + " (" + data.getId() + ")");
                    emailBody.append("\nTag(s):");
                    emailBody.append(emailPart.toString());   // tag(s) removed
                    emailBody.append("\nCurrent list of tag(s):");
                    for (Tag tag: currentTags)
                    {
                        emailBody.append("\n\t- " + tag.getName() + " (" + tag.getId() + ")");
                    }
                    if (! dateUpdate)   // TODO: only takes into account the last tag deleted!
                    {
                        emailBody.append("\n\nThe last modification date of the data type has not been updated!");
                    }
                    emailBody.append("\n\nUser: " + user);
                    GregorianCalendar cal = new GregorianCalendar();
                    emailBody.append("\nDate: " + cal.getTime());
                    emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                    MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdmin, emailsCura, "[MIRIAM-" + version + "] Tag removed: " + dataId, emailBody.toString(), "text/plain");
                    
                    request.setAttribute("data", data);
                    request.setAttribute("tags", currentTags);
                    request.setAttribute("allTags", allTags);
                    jsp = "tag_edit.jsp";
                }
                else   // user hasn't enough privileges
                {
                    message = new StringBuilder();
                    message.append("Sorry, you are not authorised to access this page!");
                    jsp = "/user";
                }
            }
            else   // user not logged
            {
                message = new StringBuilder();
                message.append("Sorry this feature is not yet implemented! Alternatively, you can use the following form to send us your request.");
                
                StringBuilder tagsStr = new StringBuilder();
                for (String str: tags)
                {
                    tagsStr.append(str + ", ");
                }
                request.setAttribute("info", "Delete tags: " + tagsStr.toString() + "from: " + dataId);
                
                jsp = "support.jsp";
            }
        }
        else   // no tag selected
        {
            message.append("You need to select at least one tag to perform a deletion!");
            
            // access to the Tag database functions
            TagDao tagDao = new TagDao(poolName);
            
            // retrieves the current data type
            DataTypeDao dataDao = new DataTypeDao(poolName);
            SimpleDataType data = dataDao.getSimpleDataTypeById(dataId);
            dataDao.clean();
            
            // retrieves the newly updated list of tags associated with the current data type
            List<Tag> currentTags = new ArrayList<Tag>();
            currentTags = tagDao.retrieveTags(dataId);
            // retrieves the list of all the available tags
            List<Tag> allTags = new ArrayList<Tag>();
            allTags = tagDao.retrieveAllTags();
            // remove the tags already associated with the data type
            allTags.removeAll(currentTags);
            tagDao.clean();
            
            request.setAttribute("data", data);
            request.setAttribute("tags", currentTags);
            request.setAttribute("allTags", allTags);
            jsp = "tag_edit.jsp";
        }
        
        if (null != message)
        {
            request.setAttribute("message", message.toString());
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
