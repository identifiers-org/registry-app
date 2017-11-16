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
import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.ResourceDao;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>Servlet that handles the display of the exports page with some basic statistics.
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
 * @version 20130304
 */
public class ServletExports extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 7426765195447888944L;
    
    
    /**
     * Default constructor.
     */
    public ServletExports()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        RequestDispatcher view = null;
        int nbDataTypes = 0;
        int nbAllDataTypes = 0;
        int nbResources = 0;
        int nbAllResources = 0;
        int nbDataTypesCura = 0;
        int nbResourcesCura = 0;
        String lastUpdate = null;
        String lastUpdateCura = null;
        
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        DataTypeDao dao = new DataTypeDao(poolName);
        CuraDataTypeDao daoCura = new CuraDataTypeDao(poolName);
        ResourceDao resDao = new ResourceDao(poolName);
        
        // published data
        // retrieves the number of (non obsolete) data collections stored
        nbDataTypes = dao.getNbDataTypes();
        // retrieves the total number of data collections (including obsolete ones)
        nbAllDataTypes = dao.getNbAllDataTypes();
        // retrieves the number of (non deprecated) resources
        nbResources = resDao.getNbResources();
        // retrieves the total number of resources (including deprecated ones)
        nbAllResources = resDao.getNbAllResources();
        // retrieve the date of last update
        Date lastUpdateDate = dao.getLastModifDate();
        
        // under curation data
        // retrieves the number of (non obsolete) data collections stored
        nbDataTypesCura = daoCura.getNbDataTypesActive();
        // retrieves the number of (non deprecated) resources
        nbResourcesCura = daoCura.getNbResourcesActive();
        // retrieve the date of last update
        Date lastUpdateDateCura = daoCura.getLastModifDate();
        
        // cleaning
        dao.clean();
        resDao.clean();
        daoCura.clean();
        
        // convert date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        lastUpdate = dateFormat.format(lastUpdateDate);
        lastUpdateCura = dateFormat.format(lastUpdateDateCura);
        
        request.setAttribute("nbDataTypes", nbDataTypes);
        request.setAttribute("nbAllDataTypes", nbAllDataTypes);
        request.setAttribute("nbResources", nbResources);
        request.setAttribute("nbAllResources", nbAllResources);
        request.setAttribute("dateLastUpdate", lastUpdate);
        request.setAttribute("nbDataTypesCura", nbDataTypesCura);
        request.setAttribute("nbResourcesCura", nbResourcesCura);
        request.setAttribute("dateLastUpdateCura", lastUpdateCura);
        view = request.getRequestDispatcher("exports.jsp");
        view.forward(request, response);
    }
    
    
    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException
    {
        // nothing here.
    }
}
