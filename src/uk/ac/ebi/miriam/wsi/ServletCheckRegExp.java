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
 * Link to the function: checkRegExp
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
public class ServletCheckRegExp extends BaseAjaxServlet
{
    private static final long serialVersionUID = -6905097197286132782L;
    
    
    /**
     * 
     * @return String containing the answer
     */
    public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String newResult = new String();
        
        // recovery of the parameters
        String datatype = request.getParameter("checkRegExpParam1");
        String identifier = request.getParameter("checkRegExpParam2");
        
        // cleans parameters
        if (null != datatype)
        {
            datatype = datatype.trim();
        }
        if (null != identifier)
        {
            identifier = identifier.trim();
        }
        
        // retrieves the address to access to the Web Services
        String endPoint = getServletContext().getInitParameter("webServicesEndpoint");
        
        // processing the request
        MiriamLink mir = new MiriamLink();
        mir.setAddress(endPoint);
        boolean result = mir.checkRegExp(identifier, datatype);
        
        // creates a beautiful (x)html output
        if (result)
        {
            newResult = "<p>The identifier '" + identifier + "' is valid for the data type '" + datatype + "'.</p>";
        }
        else
        {
            newResult = "<p>The identifier '" + identifier + "' is not valid for the data type '" + datatype + "'!</p>";
        }
        
        return newResult;
    }
}
