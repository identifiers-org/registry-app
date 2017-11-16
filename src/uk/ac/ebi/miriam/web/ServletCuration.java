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


import uk.ac.ebi.miriam.db.CuraDataTypeDao;
import uk.ac.ebi.miriam.db.SimpleCuraDataType;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;


/**
 * <p>Servlet that handles the display of the data types in the curation pipeline.
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
public class ServletCuration extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -7967829397304646329L;
    private Logger logger = Logger.getLogger(ServletCuration.class);
    private final String SUBMITTED = "Submitted";
    private final String CURATION = "Curation";
    private final String PUBLISHED = "Canceled";
    private final String PENDING = "Pending";
    private final String CANCELED = "Published";
    //private final String ALL = "All";
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletCuration()
    {
        super();
    }
    
    
    /*
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.debug("Curation in progress...");
        String message = null;
        ArrayList<SimpleCuraDataType> result = new ArrayList<SimpleCuraDataType>();
        String jsp;
        String id = null;
        String type = null;
        HttpSession session = request.getSession();
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(session))
            {
                // retrieves the parameter corresponding to the type of data types to display ('pending', 'canceled' or 'published')
                type = request.getParameter("type");
                
                // retrieves the id a specific data type in the curation pipeline
                id = request.getParameter("data");
                
                // if the user is redirected here (for some reason or another) and the list of data types should be displayed
                //Boolean generic = (Boolean) request.getAttribute("generic");
                
                // retrieves the name of the pool
                String poolName = getServletContext().getInitParameter("miriam_db_pool");
                
                //(null != generic) && (! generic) && 
                if ((null != id) && (! MiriamUtilities.isEmpty(id)))
                {
                    logger.debug("Data type in curation pipeline requested: " + id);
                    jsp = "/curationData";
                }
                else
                {
                    CuraDataTypeDao curaDao = new CuraDataTypeDao(poolName);
                    
                    if (null != type)
                    {
                        if (type.equalsIgnoreCase(this.SUBMITTED))
                        {
                            result = (ArrayList<SimpleCuraDataType>) curaDao.retrieveWithState(this.SUBMITTED);
                        }
                        else if (type.equalsIgnoreCase(this.CURATION))
                        {
                            result = (ArrayList<SimpleCuraDataType>) curaDao.retrieveWithState(this.CURATION);
                        }
                        else if (type.equalsIgnoreCase(this.PUBLISHED))
                        {
                            result = (ArrayList<SimpleCuraDataType>) curaDao.retrieveWithState(this.PUBLISHED);
                        }
                        else if (type.equalsIgnoreCase(this.PENDING))
                        {
                            result = (ArrayList<SimpleCuraDataType>) curaDao.retrieveWithState(this.PENDING);
                        }
                        else if (type.equalsIgnoreCase(this.CANCELED))
                        {
                            result = (ArrayList<SimpleCuraDataType>) curaDao.retrieveWithState(this.CANCELED);
                        }
                        else
                        {
                            result = (ArrayList<SimpleCuraDataType>) curaDao.retrieveAll();
                            type = "all";
                        }
                        request.setAttribute("type", type.toUpperCase());
                    }
                    else
                    {
                        result = (ArrayList<SimpleCuraDataType>) curaDao.retrieveAll();
                        request.setAttribute("type", "ALL");
                    }
                    
                    curaDao.clean();
                    
                    request.setAttribute("data", result);
                    jsp = "curation.jsp";
                }
            }
            else   // not enough privileges
            {
                message = "Sorry, you are not authorised to access this page!";
                jsp = "/user";
            }
        }
        else   // not logged
        {
            message = "You need to be authenticated to access this feature!";
            request.setAttribute("referrer", request.getQueryString());
            jsp = "login.jsp";
        }
        
        // sends information of the result of the process to the user
        if (null != message)
        {
            request.setAttribute("message", message);
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
