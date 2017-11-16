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


import uk.ac.ebi.miriam.db.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>Servlet which handles the modification of an existed data collection (part 1: displays the information).
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
 * @version 20130312
 */
public class ServletDataTypeEdit extends ServletTemplate
{
    private static final long serialVersionUID = -1926642849664446407L;
    
    
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response, StringBuilder page)
    {
        Statement stmt = null;
        boolean exist = false;
        String message = null;
        
        // retrieves the parameter (name of a data type)
        String param = request.getParameter("data");
        
        // tests if there is an existing data collection with this name
        if (null != param)
        {
            param = param.trim();
            
            if (param.matches("MIR:000\\d{5}"))
            {
                try
                {
                    stmt = this.connection.createStatement();
                    String sql = "SELECT name FROM mir_datatype WHERE (datatype_id='" + param + "')";
                    ResultSet rs = stmt.executeQuery(sql);
                    if (rs.first())
                    {
                        exist = true;
                    }
                }
                catch (SQLException e)
                {
                    logger.warn("An exception occurred during the test to know if a particular data collection exists!");
                    logger.warn("SQL Exception raised: " + e.getMessage());
                }
                finally
                {
                    closeStatement(stmt);
                }
            }
        }
        
        if (exist)
        {
            logger.debug("Edition mode of the information about a data collection needed...");
            
            // connection to the database
            DataTypeHibernate data = new DataTypeHibernate();
            
            // retrieves all the information about the data collection
            data.retrieveData(getPoolName(), param);

            //sets resource ownership status
/*            String login = (String)request.getSession().getAttribute("login");
            if(login!=null) {
                OwnershipDao ownershipDao = new OwnershipDao("auth");
                for (Resource resource : data.getResources()) {
                    int stauts = ownershipDao.retrieveOwnershipStatus(login, resource.getId());
                    resource.setOwnership_status(stauts);
                }
            }*/
            data.setOwnershipForResources((String)request.getSession().getAttribute("login"));

            // modify the MIRIAM IDs of the documentation (to remove the data collection part and the separator)
            for (int i=0; i<data.getDocumentationIDs().size(); ++i)
            {
                String tmp = MiriamUtilities.getElementPart(data.getDocumentationID(i));
                // decode the string
                try
                {
                    tmp = URLDecoder.decode(tmp, "UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    logger.error("An exception occurred while decoding a String using UTF8!");
                    logger.error("UnsupportedEncodingException: " + e.getMessage());
                }
                data.setDocumentationID(i, tmp);
            }
            
            RestrictionDao restrictDao = new RestrictionDao(getPoolName());
            // retrieves restriction(s) associated with the current data collection (if any)
            data.setRestrictions(restrictDao.getRestrictionsInPubl(param));
            // retrieves all restrictions categories
            List<RestrictionType> restrictionTypes = restrictDao.getRestrictionCategories();
            restrictDao.clean();
            
            // removes from the list of restrictions, the one(s) that are already associated with the data collection
            for (Restriction restrict: data.getRestrictions())
            {
                restrictionTypes.remove(restrict.getType());
            }
            
            request.setAttribute("restriction_types", restrictionTypes);
            request.setAttribute("data", data);
            page.append("data_edit.jsp");
        }
        else
        {
            message = "The requested data collection doesn't exist in the database.";
            request.setAttribute("section", "introduction.html");
            page.append("static.jsp");
        }
        
        if (null != message)
        {
            request.setAttribute("message", message);
        }
    }
}
