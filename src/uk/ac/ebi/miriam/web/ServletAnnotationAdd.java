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


import uk.ac.ebi.miriam.db.Annotation;
import uk.ac.ebi.miriam.db.AnnotationDao;
import uk.ac.ebi.miriam.db.AnnotationTag;
import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.SimpleDataType;
import uk.ac.ebi.miriam.db.Tag;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * <p>Servlet which handles the addition of a new association between a data type and an existing tag (example of annotation).
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
public class ServletAnnotationAdd extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -809404334808068953L;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletAnnotationAdd()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String message = null;
        SimpleDataType data = null;
        List<Annotation> anno = null;
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
        
        // retrieves the identifier of the tag to be added
        String tagId = request.getParameter("tag");
        
        if (null != tagId)
        {
            tagId = tagId.trim();
        }
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(session))
            {
                // retrieves the name of the database pool
                String poolName = getServletContext().getInitParameter("miriam_db_pool");
                
                // access to the database functions
                DataTypeDao dataDao = new DataTypeDao(poolName);
                AnnotationDao annoDao = new AnnotationDao(poolName);
                
                if ((dataDao.dataTypeExists(dataId)) && (annoDao.exists(tagId)))
                {
                    if (! annoDao.annotationExists(dataId, tagId))
                    {
                        // adds the new association
                        boolean success = annoDao.addAnnotation(dataId, tagId);
                        
                        // update the last modification date of the data type
                        if (success)
                        {
                            dateUpdate = dataDao.updateLastModifDate(dataId);
                        }
                        
                        if (success)
                        {
                            message = "A new example of annotation has been added for this data type.";
                        }
                        else
                        {
                            message = "An error occurred during the addition of a new example of annotation! Therefore the result of this action cannot be guaranteed!";
                        }
                        
                        // retrieves the version of the web application (sid, alpha, main or demo)
                        String version = getServletContext().getInitParameter("version");
                        
                        // retrieves the email addresses of the administrator and the curators
                        String emailAdmin = getServletContext().getInitParameter("admin.email");
                        String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
                        
                        // retrieves the details about the data type
                        data = dataDao.getSimpleDataTypeById(dataId);
                        
                        // retrieves the examples of annotation for this data type
                        anno = annoDao.getAnnotationFromDataId(dataId);
                        
                        // email notification
                        StringBuilder emailBody = new StringBuilder();
                        emailBody.append("\nA new association between a data type and an example of annotation has been created:");
                        emailBody.append("\n\nData type: " + data.getName() + " (" + data.getId() + ")");
                        AnnotationTag tempTag = annoDao.getAnnoFromId(tagId);
                        emailBody.append("\nNew example of annotation: " + tempTag.getName() + " (" + tagId + ") from " + tempTag.getFormat());
                        if (success)
                        {
                            emailBody.append("\n\tNew association successfully added!");
                            if (! dateUpdate)
                            {
                                emailBody.append("\n\nThe last modification date of the data type has not been updated!");
                            }
                        }
                        else
                        {
                            emailBody.append("\n\tAn error occurred during the addition of this new association!");
                            emailBody.append("\n\tPlease check the state of the database...");
                        }
                        emailBody.append("\nCurrent examples of annotation:");
                        for (Annotation ann: anno)
                        {
                            emailBody.append("\n\t+ " + ann.getFormat() + ":");
                            for (Tag tag: ann.getTags())
                            {
                                emailBody.append("\n\t\t- " + tag.getName());
                            }
                        }
                        emailBody.append("\n\nUser: " + user);
                        GregorianCalendar cal = new GregorianCalendar();
                        emailBody.append("\nDate: " + cal.getTime());
                        emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                        MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdmin, emailsCura, "[MIRIAM-" + version + "] Annotation " + tagId + " added to " + dataId, emailBody.toString(), "text/plain");
                    }
                    else   // example of annotation already existing
                    {
                        // retrieves the details about the data type
                        data = dataDao.getSimpleDataTypeById(dataId);
                        
                        // retrieves the examples of annotation for this data type
                        anno = annoDao.getAnnotationFromDataId(dataId);
                        
                        message = "The data type already have this example of annotation!";
                    }
                }
                else
                {
                    // retrieves the details about the data type
                    data = dataDao.getSimpleDataTypeById(dataId);
                    
                    // retrieves the examples of annotation for this data type
                    anno = annoDao.getAnnotationFromDataId(dataId);
                    
                    message = "Sorry, the data type or the annotation tag doesn't exit!";
                }
                
                // retrieves the list of available formats
                List<String> formats = annoDao.getAvailableFormats();
                
                // a bit of cleaning
                dataDao.clean();
                annoDao.clean();
                
                request.setAttribute("data", data);
                request.setAttribute("annotation", anno);
                request.setAttribute("formats", formats);
                jsp = "annotation_edit.jsp";
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
            request.setAttribute("info", "Add existing annotation: " + tagId + " to: " + dataId);
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
