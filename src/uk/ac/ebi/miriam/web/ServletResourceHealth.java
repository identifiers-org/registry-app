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
import uk.ac.ebi.miriam.db.Resource;
import uk.ac.ebi.miriam.db.ResourceCheckDetails;
import uk.ac.ebi.miriam.db.ResourceDao;
import uk.ac.ebi.miriam.db.Restriction;
import uk.ac.ebi.miriam.db.RestrictionDao;
import uk.ac.ebi.miriam.tools.CommonFunctions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;
//import org.apache.log4j.Logger;


/**
 * <p>Servlet that handles the display of the report of the URL checking system for a specific resource.
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
 * @version 20130807
 */
public class ServletResourceHealth extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -6726372769040042123L;
    //private Logger logger = Logger.getLogger(ServletResourceHealth.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletResourceHealth()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String message = null;
        String jsp = null;
        String id = null;
        Boolean healthInfo = false;
        
        // retrieves the given resource identifier
        id = request.getParameter("id");
        
        // valid resource identifier
        if ((! MiriamUtilities.isEmpty(id)) && (id.matches("MIR:001\\d{5}")))   // MIR:00100005
        {
            // retrieves the name of the database pool
            String poolName = getServletContext().getInitParameter("miriam_db_pool");
            ResourceDao resDao = new ResourceDao(poolName);
            
            // retrieves the resource
            Resource res = null;
            res = resDao.getResource(id);
            
            // a resource with this identifier actually exists
            if (null != res)
            {
                request.setAttribute("resource", res);
                
                // retrieves the name of the parent data collection
                DataTypeDao dataDao = new DataTypeDao(poolName); 
                String collectionName = dataDao.getDataTypeName(res.getCollectionId());
                request.setAttribute("dataCollection", collectionName);
                
                // retrieves any restrictions that might be associated to the parent data collection
                RestrictionDao restrictDao = new RestrictionDao(poolName);
                List<Restriction> restrictions = restrictDao.getRestrictionsInPubl(res.getCollectionId());
                request.setAttribute("restrictions", restrictions);
                
                // retrieves the health data (if any) for this resource
                ResourceCheckDetails resDetails = resDao.getCheckDetails(id);
                
                // the resource has some health check info
                if (null !=  resDetails)
                {
                    healthInfo = true;
                    
                    Map<Integer, Map<String, List<Integer>>> history = resDao.getResourceCheckHistory(id);
                    
                    request.setAttribute("data", history);
                    request.setAttribute("resourceId", id);
                    request.setAttribute("resourceInfo", resDetails.getInfo());
                    request.setAttribute("obsolete", resDetails.isObsolete());
                    
                    request.setAttribute("dataType", resDetails.getDataName());
                    request.setAttribute("dataTypeId", resDetails.getDataId());
                    
                    // format the date to look like: "2010-03-23 06:33:42"
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String lastCheck = dateFormat.format(resDetails.getLastCheck());
                    request.setAttribute("lastCheck", lastCheck);
                    
                    request.setAttribute("state", ResourceDao.getStateDesc(resDetails.getState()));
                    request.setAttribute("stateColour", ResourceDao.getStateColour(resDetails.getState()));
                    
                    request.setAttribute("reliability", CommonFunctions.percentConvert(resDetails.getUptimeRatio()));
                    
                    request.setAttribute("url", StringEscapeUtils.escapeHtml4(resDetails.getUrl()));
                    
                    request.setAttribute("uptimeRatio", resDetails.getUptimeRatio());
                    request.setAttribute("uptime", resDetails.getUptime());
                    request.setAttribute("unknownRatio", resDetails.getUnknownRatio());
                    request.setAttribute("unknown", resDetails.getUnknown());
                    request.setAttribute("downtimeRatio", resDetails.getDowntimeRatio());
                    request.setAttribute("downtime", resDetails.getDowntime());
                }
                else   // no history information available
                {
                    healthInfo = false;
                }
                
                request.setAttribute("healthInfo", healthInfo);
                jsp = "resource.jsp";
                
                // cleaning
                dataDao.clean();
                restrictDao.clean();
            }
            else   // no resource with this (valid) identifier exists in the database
            {
                message = "The requested resource (" + id + ") does not exist!";
                request.setAttribute("section", "introduction.html");
                jsp = "static.jsp";
            }
            
            // cleaning
            resDao.clean();
        }
        else   // invalid identifier
        {
            message = "Invalid resource identifier!";
            request.setAttribute("section", "introduction.html");
            jsp = "static.jsp";
        }
        
        // sends information of the result of the process to the user
        if (null != message)
        {
            request.setAttribute("message", message);
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
}
