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

package uk.ac.ebi.miriam.wsi;


import uk.ac.ebi.miriam.lib.MiriamLink;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ajaxtags.servlets.BaseAjaxServlet;


/**
 * <p>
 * "controller" part of the MIRIAM web application, client for the Web Services
 * <p>
 * Link to the function: getLocations
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
 * @version 20120608
 */
public class ServletGetLocations extends BaseAjaxServlet
{
    private static final long serialVersionUID = -3415138524077558855L;
    
    
    /**
     * 
     * @return String containing the answer
     */
    public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        StringBuilder newResult = new StringBuilder();
        
        // recovery of the parameters
        String uri = request.getParameter("getLocationsParam1");
        
        // cleans parameters
        if (null != uri)
        {
            uri = uri.trim();
        }
        
        // retrieves the address to access to the Web Services
        String endPoint = getServletContext().getInitParameter("webServicesEndpoint");
        
        // processing the request
        MiriamLink mir = new MiriamLink();
        mir.setAddress(endPoint);
        String[] result = mir.getLocations(uri);
        
        // checking of the answer
        if (null == result)   // null: error in the query
        {
            newResult.append("<p>Existing data type but invalid identifier!</p>");
        }
        else if (result.length == 0)   // no data type retrieved
        {
            newResult.append("<p>Invalid URI or no entry in the database for this data type!</p>");
        }
        else   // creates a beautiful (x)html output
        {
            newResult.append("<ul>");
            for (int i=0; i<result.length; ++i)
            {
                newResult.append("<li><a href=\"" + result[i] + "\" title=\"" + result[i] + "\">" + result[i] + "</li>");
            }
            newResult.append("</ul>");
        }
        
        return newResult.toString();
    }
}
