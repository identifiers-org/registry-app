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
import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.SimpleDataType;

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * <p>Servlet which handles the display of the edition page of the examples of annotation for a data type.
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
public class ServletAnnotationEdit extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 2173833733586011204L;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletAnnotationEdit()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String message = null;
        String jsp;
        
        // retrieves the id of the data type to be updated
        String dataId = request.getParameter("data");
        
        // retrieves the HTTP session
        HttpSession session = request.getSession();
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(session))
            {
                if (! MiriamUtilities.isEmpty(dataId))
                {
                    // retrieves the name of the database pool
                    String poolName = getServletContext().getInitParameter("miriam_db_pool");
                    
                    // access to the database functions
                    DataTypeDao dataDao = new DataTypeDao(poolName);
                    
                    // checks if the data type exists
                    if (dataDao.dataTypeExists(dataId))
                    {
                        // retrieves the details about the data type
                        SimpleDataType data = dataDao.getSimpleDataTypeById(dataId);
                        
                        // retrieves the examples of annotation for this data type
                        AnnotationDao annoDao = new AnnotationDao(poolName);
                        List<Annotation> anno = annoDao.getAnnotationFromDataId(dataId);
                        
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
                    else
                    {
                        message = "Sorry, the data type requested doesn't exist!";
                        jsp = "/collections";
                    }
                }
                else   // empty parameter
                {
                    //message = "no message dumb user!";
                    jsp = "/collections";
                }
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
            request.setAttribute("info", "Edit examples annotation for: " + dataId);
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
