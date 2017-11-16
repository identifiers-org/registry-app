/*
 * MIRIAM Registry (Web Application)
 * The Registry is an online resource created to catalogue data collections 
 * (their URIs and the corresponding physical URLs, whether these are controlled vocabularies or databases)
 * and provide unique and stable identifiers for life science, in the form of URIs. 
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2014  BioModels.net (EMBL - European Bioinformatics Institute)
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
import uk.ac.ebi.miriam.db.SimpleDataType;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringEscapeUtils;


/**
 * <p>Servlet which handles the queries to the database for listing the data collections.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2014  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20140311
 */
public class ServletDbBrowse extends ServletTemplate
{
    private static final long serialVersionUID = 8149522239197419697L;
    
    
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response, StringBuilder page)
    {
        String message = null;   // which kind of data types are currently displayed (all, obsolete, ...)
        String feedback = null;   // feedback to user
        String option = "";   // option of the display (current, obsolete or all)
        String query = null;
        String jsp = null;
        
        // retrieves the parameter corresponding to the identifier of a data collection
        String param = request.getParameter("data");
        // optional message parameter
        String msg = (String) request.getAttribute("message");
        if ((null != msg) && (!msg.matches("\\s*")))
        {
        	feedback = msg;
        }
        
        // a specific data collection has been requested (not the display of the list of stored data types)
        if (param != null)
        {
            logger.debug("Complete information about a data collection requested...");
            page.append("/dataTypeInfo");
        }
        else   // displays the list of stored data collections
        {
            // retrieves the parameter indicating the scope of the request: 'all', 'obsolete' or 'current' (default if no param)
            String display = request.getParameter("display");
            
            // retrieves the parameter indicating which data types to list 
            String startBy = request.getParameter("startBy");
            
            // display = all (should not be used any more), obsolete, restricted or current (== no value)
            if (null == display)
            {
                display = "current";   // default value
            }
            else
            {
                option = "&amp;display=" + display;
            }
            
            // database access
            DataTypeDao dao = new DataTypeDao(getPoolName());
            
            // retrieves the requested data types
            List<SimpleDataType> datatypes = null;
            
            if (display.equalsIgnoreCase("restricted"))
            {
                datatypes = dao.getRestrictedDataTypes();
                message = "restricted";
                query = "restricted data collections";
                jsp = "simple_browse.jsp";
            }
            else if (display.equalsIgnoreCase("obsolete"))
            {
                datatypes = dao.getObsoleteDataTypes();
                message = "obsolete";
                query = "obsolete data collections";
                jsp = "simple_browse.jsp";
            }
            else if ((null == startBy) || (startBy.matches("\\s*")) || (startBy.equalsIgnoreCase("updated")))   // in case nobody asked for any thing special (just request for "../collections/")
            {
                // displays the 30 recently updated data types
                startBy = "updated";
                datatypes = dao.getDataTypesRecentlyUpdated(30);
                query = "of the most recently created or modified data collections";
                jsp = "browse_collections.jsp";
            }
            else
            {
                startBy = startBy.trim();
                if (display.equalsIgnoreCase("all"))   // this option should not be used any more
                {
                    datatypes = dao.getDataTypesNameStartingBy(startBy, true, true);
                    message = "ALL";
                    jsp = "browse_collections.jsp";
                    query = "all data collections";
                }
                else   // default case (do not display obsolete data types)
                {
                    datatypes = dao.getDataTypesNameStartingBy(startBy, true, false);
                    jsp = "browse_collections.jsp";
                    query = "data collections which name starts by '<i>" + startBy + "</i>'";
                }
            }
            
            // cleaning
            dao.clean();
            
            // make the data safe for HTML consumption
            for (SimpleDataType datatype: datatypes)
            {
                datatype.setDefinition(StringEscapeUtils.escapeHtml4(datatype.getDefinition()));
                datatype.setName(StringEscapeUtils.escapeHtml4(datatype.getName()));
            }
            
            // in case there is no result
            if ((null == datatypes) || (datatypes.size() == 0))
            {
                feedback = "There is no data collection which name starts by '" + startBy + "'.<br />Don't forget that you can always <a href=\"mdb?section=submit\" title=\"Submit a new data collection\">submit one</a> yourself.";
            }
            
            request.setAttribute("preferences", message);
            request.setAttribute("message", feedback);
            request.setAttribute("data", datatypes);
            request.setAttribute("option", option);
            request.setAttribute("nb_data", datatypes.size());
            request.setAttribute("startBy", startBy);
            if (null != startBy)
            {
                if (startBy.equalsIgnoreCase("updated"))
                {
                    request.setAttribute("startByTitle", "recently updated");
                }
                else
                {
                    request.setAttribute("startByTitle", startBy.toLowerCase());
                }
            }
            request.setAttribute("query", query);
            page.append(jsp);
        }
    }
}
