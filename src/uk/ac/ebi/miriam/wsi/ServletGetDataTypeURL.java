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
 * "controller" part of the MIRIAM web application, client for the Web Services
 * <p>
 * Link to the function: getDataTypeURL
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
public class ServletGetDataTypeURL extends BaseAjaxServlet
{
    private static final long serialVersionUID = -149277998186284003L;
    private Logger logger = Logger.getLogger(ServletGetDataTypeURL.class);
    
    @SuppressWarnings("deprecation")
    public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        logger.debug("'ServletGetDataTypeURL' was called...");
        
        String newResult = new String();
        
        // recovery of the parameters
        String name = request.getParameter("getDataTypeURLParam");
        
        // cleans parameters
        if (null != name)
        {
            name = name.trim();
        }
        
        // retrieves the address to access to the Web Services
        String endPoint = getServletContext().getInitParameter("webServicesEndpoint");
        
        // processing the request
        MiriamLink mir = new MiriamLink();
        mir.setAddress(endPoint);
        String result = mir.getDataTypeURL(name);
        
        // checking of the answer
        if ((result == null) || (result.equalsIgnoreCase("")))
        {
            newResult = "<p>no URL has been defined for this data type</p>";
        }
        else
        {
            newResult = "<p>" + result + "</p>";
        }
        
        return newResult;
    }
}
