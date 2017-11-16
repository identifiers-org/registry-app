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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>Servlet that handles the queries to the database for accessing the complete information about a data collection.
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
 * @author Camille Laibe
 * @version 20130702
 */
public class ServletDataTypeInfo extends ServletTemplate
{
    private static final long serialVersionUID = -6045828167769196502L;
    
    
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response, StringBuilder page)
    {
        Statement stmt = null;
        DataTypeHibernate data = new DataTypeHibernate();
        boolean exist = false;
        
        // retrieves the parameter (identifier of a data type)
        String id = request.getParameter("data");
        
        // tests if there is an existing data type with this name
        if (! MiriamUtilities.isEmpty(id))
        {
        	// checks that the parameter looks like a MIRIAM Identifier (to stop attempts to attack the service via SQL Injection)
        	if (id.matches("MIR:\\d{8}"))
        	{
        	    try
                {
                    stmt = this.connection.createStatement();
                    String sql = "SELECT name FROM mir_datatype WHERE (datatype_id='" + id + "')";
                    ResultSet rs = stmt.executeQuery(sql);
                    if (rs.first())
                    {
                        exist = true;
                    }
                }
                catch (SQLException e)
                {
                    logger.warn("An exception occured during the test to know if a particular data type exists!");
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
            // retrieves all the information about the data collection
            data.retrieveData(getPoolName(), id);

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

            // converts MIRIAM URNs into Identifiers.org URIs in the references
            convertsDocURIs(data);
            
            // the data collection is obsolete (we retrieve the name of the replacement one)
            if ((data.isObsolete()) && (null != data.getReplacedBy()))
            {
                DataTypeDao dataTypeDao = new DataTypeDao(getPoolName());
                String replacedName = dataTypeDao.getDataTypeName(data.getReplacedBy());
                dataTypeDao.clean();
                request.setAttribute("replacementName", replacedName);
            }
            else   // should not be necessary, but in case...
            {
                request.setAttribute("replacementName", null);
            }
            
            // retrieves the list of tags/categories
            TagDao tagDao = new TagDao(getPoolName());
            List<Tag> tags = tagDao.retrieveTags(id);
            tagDao.clean();


            if((String)request.getSession().getAttribute("login")!=null) {
                //check whether there are any pending requests
                for (Resource resource : data.getResources()) {
                    if (resource.getOwnership_status() == 0) {
                        request.setAttribute("message", "You are waiting to become a maintainer for one or more resources. Curators have been notified and please wait for them to authorise access.");
                        break;
                    }
                }
            }


            request.setAttribute("data", data);
            request.setAttribute("tags", tags);
            page.append("data_browse.jsp");
        }
        else
        {
            request.setAttribute("section", "not_existing.html");
            page.append("static.jsp");
        }
    }
    
    
    /**
     * Converts all MIRIAM URN in the list of references to Identifiers.org URLs
     */
	private void convertsDocURIs(DataTypeHibernate data)
	{
		List<String> urls = new ArrayList<String>();
		// converts all URNs
		for (String urn: data.getDocumentationIDs())
		{
			urls.add(MiriamUtilities.convertValidURN(urn));
		}
		// removes all URNs and add all URLs
		data.setDocumentationIDs(urls);
	}
}
