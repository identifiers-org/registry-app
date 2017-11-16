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
import org.apache.log4j.Logger;


/**
 * <p>
 * Servlet used in the client part of MIRIAM WebServices. Call a precise remote method.
 * <p>
 * Link to the function: getDataTypeURLs
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
public class ServletGetDataTypeURLs extends BaseAjaxServlet
{
    private static final long serialVersionUID = 6308316327596985655L;
    private Logger logger = Logger.getLogger(ServletGetDataTypeURLs.class);
    
    @SuppressWarnings("deprecation")
    public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        logger.debug("'ServletGetDataTypeURLs' was called...");
        
        StringBuilder newResult = new StringBuilder();
        
        // recovery of the parameters
        String name = request.getParameter("getDataTypeURLsParam");
        
        // cleans parameters
        if (null != name)
        {
            name = name.trim();
        }
        
        // retrieves the address to access to the Web Services (in the 'web.xml' file)
        String endPoint = getServletContext().getInitParameter("webServicesEndpoint");
        
        // processing the request
        MiriamLink mir = new MiriamLink();
        mir.setAddress(endPoint);
        String[] result = mir.getDataTypeURLs(name);
        
        // checking of the answer
        if ((result == null) || (result.length == 0))
        {
            newResult.append("<p>no URL has been defined for this data type</p>");
        }
        // create a beautiful (x)html output
        else
        {
            newResult.append("<ul>");
            for (int i=0; i<result.length; ++i)
            {
                newResult.append("<li>" + result[i] + "</li>");
            }
            newResult.append("</ul>");
        }
        
        return newResult.toString();
    }
}
