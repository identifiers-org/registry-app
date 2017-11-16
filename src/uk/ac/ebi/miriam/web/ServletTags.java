/*
 * MIRIAM Resources (Web Application)
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2011  BioModels.net (EMBL - European Bioinformatics Institute)
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
import uk.ac.ebi.miriam.db.Tag;
import uk.ac.ebi.miriam.db.TagDao;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>Servlet which Handles the display of the tags associated with a data type.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2011  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20111014
 */
public class ServletTags extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -3700268935154599182L;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletTags()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        RequestDispatcher view = null;
        List<Tag> data = null;
        String name = null;
        Boolean restricted = false;
        
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        DataTypeDao dataTypeDao = new DataTypeDao(poolName);
        
        // retrieves the parameter (identifier of a data type)
        String id = request.getParameter("data");
        
        // parameter not empty
        if (! MiriamUtilities.isEmpty(id))
        {
            if (dataTypeDao.dataTypeExists(id))
            {
                name = dataTypeDao.getDataTypeName(id);
                restricted = dataTypeDao.isRestricted(id);
                
                TagDao tagDao = new TagDao(poolName);
                data = tagDao.retrieveTags(id);
                tagDao.clean();
                
                // the data collection is obsolete (we retrieve the name and id of the replacement one)
                HashMap<String, String> obsoleteInfo = dataTypeDao.getObsoleteInfo(id);
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
                
                request.setAttribute("id", id);
                request.setAttribute("name", name);
                request.setAttribute("data", data);
                request.setAttribute("restricted", restricted);
                view = request.getRequestDispatcher("tags.jsp");
            }
            else
            {
                String message = "Sorry, the requested data type doesn't exist in the database...";
                request.setAttribute("message", "<p>" + message + "</p>");
                view = request.getRequestDispatcher("/collections");
            }
        }
        else   // empty parameter (identifier of the data type)
        {
            // TODO: modify this to redirected to a page deisplaying all the possible examples of annotation (classified by format) 
            
            String message = "Sorry, the requested data type doesn't exist in the database...";
            request.setAttribute("message", "<p>" + message + "</p>");
            view = request.getRequestDispatcher("/collections");
        }
        
        dataTypeDao.clean();
        view.forward(request, response);
    }
}
