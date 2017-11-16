/*
 * MIRIAM Registry (Web Application)
 * The Registry is an online resource created to catalogue data collections,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2012  BioModels.net (EMBL - European Bioinformatics Institute)
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
import uk.ac.ebi.miriam.db.ResourceCheckDetails;
import uk.ac.ebi.miriam.db.ResourceDao;
import uk.ac.ebi.miriam.tools.ResourceLog;
import uk.ac.ebi.miriam.tools.UCS;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>Servlet which checks the health of an individual resource for testing purposes (no record stored in the database).
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2012  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20120629
 */
public class ServletCheckResourceHealth extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -4009279132535773731L;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletCheckResourceHealth()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        RequestDispatcher view = null;
        String message = null;
        String jsp = null;
        String id = null;
        
        // retrieves the identifier of the resource
        id = request.getParameter("id");
        
        // valid resource identifier
        if ((! MiriamUtilities.isEmpty(id)) && (id.matches("MIR:001\\d{5}")))   // MIR:00100005
        {
            // retrieves the name of the database pool
            String poolName = getServletContext().getInitParameter("miriam_db_pool");
            DataTypeDao dataDao = new DataTypeDao(poolName);
            ResourceDao resDao = new ResourceDao(poolName);
            
            // retrieves the data corresponding to the given identifier
            ResourceCheckDetails resource = resDao.getCheckDetails(id);
            
            // the resource with this identifier actually exists
            if (null !=  resource)
            {
                // health check
                UCS rhcs = new UCS();
                ResourceLog report = new ResourceLog(resource.getId(), resource.getDataId(), resource.getUrl(), resource.getKeyword(), resource.isObsolete(), resDao.hasAccessRestriction(resource.getId()), resource.isAjax(), resource.isBinary());
                rhcs.check(report);
                
                // retrieves the current time
                GregorianCalendar calEnd = new GregorianCalendar();
                Date now = calEnd.getTime();
                
                request.setAttribute("date", now.toString());
                request.setAttribute("stateStr", ResourceDao.getStateDesc(report.getState()));
                request.setAttribute("stateColour", ResourceDao.getStateColour(report.getState()));
                request.setAttribute("report", report);
                request.setAttribute("resource", resource);
                jsp = "resource_check.jsp";
            }
            else   // no resource with this (valid) identifier exists in the database
            {
                message = "The requested resource (" + id + ") doesn't exist (or no health record has been previously created)!";
                jsp = "/collections";
            }
            
            // cleaning
            dataDao.clean();
            resDao.clean();
        }
        else   // invalid resource identifier
        {
            message = "Invalid resource identifier!";
            jsp = "/collections";
        }
        
        // sends information of the result of the process to the user
        if (null != message)
        {
            request.setAttribute("message", "<p>" + message + "</p>");
        }
        view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
