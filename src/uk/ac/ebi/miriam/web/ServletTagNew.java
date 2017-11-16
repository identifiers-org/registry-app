/*
 * MIRIAM Resources (Web Application)
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2011  Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
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
 *
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
 * <p>Servlet which handles the creation of a new tag in the data base.
 * <p>As a result, a new association with a data type will be also created.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2011 Camille Laibe (EMBL - European Bioinformatics Institute, Computational Neurobiology Group)
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
 * @version 20100413
 */
public class ServletTagNew extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -2192253849698060950L;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletTagNew()
    {
        super();
    }
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String message = null;
        boolean creation = false;
        boolean association = false;
        Tag newTag = null;
        boolean dateUpdate = false;
        String jsp;
        
        // retrieves the user logged who asked for the action
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("login");
        
        // retrieves the identifier of the data type to be updated
        String dataId = request.getParameter("dataTypeId");
        
        if (null != dataId)
        {
            dataId = dataId.trim();
        }
        
        // retrieves the name of the identifier to be added
        String tagName = request.getParameter("tag");
        // retrieves the definition of the tag to be added
        String tagDef = request.getParameter("def");
        
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(session))
            {
                // access to database functions
                TagDao tagDao = new TagDao(poolName);
                DataTypeDao dataDao = new DataTypeDao(poolName);
                
                if (! MiriamUtilities.isEmpty(tagName))
                {
                    // the data type exists
                    if (dataDao.dataTypeExists(dataId))
                    {
                        if (! tagDao.tagNameExists(tagName))
                        {
                            creation = tagDao.CreateNew(tagName, tagDef);
                            
                            if (creation)
                            {
                                newTag = tagDao.getTagFromName(tagName);
                                
                                // add an association between this new tag and the current data type
                                association = tagDao.addAssociation(newTag.getId(), dataId);
                                
                                if (association)
                                {
                                    // update the last modification date of the data type
                                    dateUpdate = dataDao.updateLastModifDate(dataId);
                                    message = "The new tag '" + tagName + "' has been created and associated with this data type.";
                                }
                                else
                                {
                                    message = "The new tag has been created with success!<br />Unfortunately, an error occurred and it has not been associated with this data type...";
                                }
                            }
                            else
                            {
                                message = "Sorry, an error occurred! Therefore the creation of this new tag is not guaranteed.";
                            }
                            
                            // retrieves the version of the web application (sid, alpha, main or demo)
                            String version = getServletContext().getInitParameter("version");
                            
                            // retrieves the email addresses of the administrator and the curators
                            String emailAdmin = getServletContext().getInitParameter("admin.email");
                            String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
                            
                            // email notification
                            StringBuilder emailBody = new StringBuilder();
                            emailBody.append("\nThe creation of a new tag has been requested:");
                            emailBody.append("\n\nName: " + tagName);
                            emailBody.append("\nDefinition: " + tagDef);
                            if (creation)
                            {
                                emailBody.append("\n\nThe tag has been created with success!");
                                emailBody.append(newTag.toString());
                                
                                if (association)
                                {
                                    emailBody.append("\nThe tag has been associated with the data type: " + dataId);
                                }
                                else
                                {
                                    emailBody.append("\n\nAn error occurred during the association of this tag with the following data type: " + dataId);
                                    emailBody.append("\n\tPlease check the state of the database...");
                                }
                            }
                            else
                            {
                                emailBody.append("\n\n\tAn error occurred during the creation of this tag!");
                                emailBody.append("\n\tPlease check the state of the database...");
                            }
                            if (! dateUpdate)
                            {
                                emailBody.append("\n\nThe last modification date of the data type has not been updated!");
                            }
                            
                            emailBody.append("\n\nUser: " + user);
                            GregorianCalendar cal = new GregorianCalendar();
                            emailBody.append("\nDate: " + cal.getTime());
                            emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                            MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdmin, emailsCura, "[MIRIAM-" + version + "] Tag created: " + tagName, emailBody.toString(), "text/plain");
                        }
                        else
                        {
                            message = "Sorry, a tag with the same name already exists!<br />Use the function 'Add existing tag' instead.";
                        }
                    }
                    else
                    {
                        message = "Sorry, the data type doesn't exist! Therefore we are unable to create the requested tag!";
                    }
                }
                else
                {
                    message = "The new tag must have a name!";
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
            message = "Sorry, you are not authorised to access this feature. Alternatively, you can use the following form to send us your request.";
            request.setAttribute("info", "Add new tag: " + tagName + " (" + tagDef + ") to: " + dataId);
            jsp = "support.jsp";
        }
        
        if (null != message)
        {
            request.setAttribute("message", "<p>" + message + "</p>");
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
