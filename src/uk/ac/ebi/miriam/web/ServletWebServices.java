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
import uk.ac.ebi.miriam.db.ServicesDao;
import uk.ac.ebi.miriam.db.WebService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import org.apache.log4j.Logger;


/**
 * <p>
 * Servlet for the display of the Web Services available for a specific data type
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
 * @version 20111013
 */
public class ServletWebServices extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -7221645231220209790L;
    //private Logger logger = Logger.getLogger(ServletWebServices.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletWebServices()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String message = null;
        RequestDispatcher view = null;
        boolean exist = false;
        String jsp;
        
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        // database connection
        DataTypeDao dataTypeDao = new DataTypeDao(poolName);
        ServicesDao dao = new ServicesDao(poolName);
        
        // retrieves the data type identifier
        String datatypeId = request.getParameter("id");
        
        if (! MiriamUtilities.isEmpty(datatypeId))
        {
            // checks that the parameter looks like a MIRIAM Identifier (to stop attempts to attack the service via SQL Injection)
            if (datatypeId.matches("MIR:\\d{8}"))
            {
                // the data type exists
                if (dataTypeDao.dataTypeExists(datatypeId))
                {
                    exist = true;
                }
            }
        }
        
        // the data type exists
        if (exist)
        {
            // retrieves the name of the data collection
            String datatypeName = dataTypeDao.getDataTypeName(datatypeId);
            request.setAttribute("datatypeName", datatypeName);
            request.setAttribute("datatypeId", datatypeId);
            
            // if the data type is obsolete, we retrieve some replacement info
            HashMap<String, String> obsoleteInfo = dataTypeDao.getObsoleteInfo(datatypeId);
            if (null != obsoleteInfo)
            {
                request.setAttribute("obsolete", true);
                request.setAttribute("replacementName", obsoleteInfo.get("replacementName"));
                if ((null != obsoleteInfo.get("replacementId")) && (!obsoleteInfo.get("replacementId").isEmpty()))
                {
                    request.setAttribute("replacementId", obsoleteInfo.get("replacementId"));
                }
                else
                {
                    request.setAttribute("replacementId", null);
                }
                request.setAttribute("replacementComment", obsoleteInfo.get("replacementComment"));
            }
            else
            {
                request.setAttribute("obsolete", false);
            }
            
            // retrieves the list of available web services
            Map<String, List<WebService>> listOfServices = dao.getDataTypeServices(datatypeId);
            
            request.setAttribute("data", listOfServices);
            jsp = "webservices.jsp";
        }
        else
        {
            message = "The provided parameter is not valid! The identifier of a MIRIAM data type is expected (for example: MIR:00000008).";
            jsp = "error404.jsp";
        }
        
        // cleaning
        dataTypeDao.clean();
        dao.clean();
        
        if (null != message)
        {
            request.setAttribute("message", "<p>" + message + "</p>");
        }
        view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
}
