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
 *
 */


package uk.ac.ebi.miriam.web;


import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * <p>Servlet that handles the basic search.
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
 * @version 20140307
 */
public class ServletSimpleSearch extends ServletTemplate
{
    private static final long serialVersionUID = 3903960397735739235L;
    
    
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response, StringBuilder page)
    {
        ResultSet rs = null;
        String message = null;
        HashMap<String, List<String>> resultsPublished = new HashMap<String, List<String>>();
        HashMap<String, List<String>> resultsUnderCuration = new HashMap<String, List<String>>();
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        PreparedStatement stmt3 = null;
        PreparedStatement stmt4 = null;
        PreparedStatement stmt5 = null;
        PreparedStatement stmt6 = null;
        PreparedStatement stmt7 = null;
        PreparedStatement stmt8 = null;
        Boolean logged = false;
        
        // to be able to retrieve UTF-8 elements from HTML forms
        try
        {
            request.setCharacterEncoding("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("An exception occured while setting the encoding to UTF-8 for parameters from HTML forms!");
            logger.error("UnsupportedEncodingException raised: " + e.getMessage());
        }

        // recovery of the parameters from the form
        String words = request.getParameter("query");
        
        // cleans the human input
        if ((null != words) && (! words.isEmpty()) && (!words.matches("\\s*")))
        {
            words = words.trim();
            logger.debug("Basic search for: >" + words + "<");
            
	        try
	        {
	            // search in the 'data type' table
	            stmt1 = this.connection.prepareStatement("SELECT name, definition, datatype_id FROM mir_datatype WHERE ((datatype_id LIKE ?) OR (name LIKE ?) OR (definition LIKE ?))");
	            // search in the 'synonym' table
	            stmt2 = this.connection.prepareStatement("SELECT d.name, d.definition, d.datatype_id FROM mir_datatype d, mir_synonym s WHERE ((d.datatype_id = s.ptr_datatype) AND (s.name LIKE ?))");
	            // search in the 'resource' table
	            stmt3 = this.connection.prepareStatement("SELECT d.name, d.definition, d.datatype_id FROM mir_datatype d, mir_resource r WHERE ((d.datatype_id = r.ptr_datatype) AND ((r.resource_id LIKE ?) OR (r.info LIKE ?) OR (r.institution LIKE ?) OR (r.location LIKE ?)))");
	            // search in the 'uri' table
	            stmt4 = this.connection.prepareStatement("SELECT d.name, d.definition, d.datatype_id FROM mir_datatype d, mir_uri u WHERE ((d.datatype_id = u.ptr_datatype) AND (u.uri LIKE ?))");
	            
	            // from 'data type' table
	            stmt1.setString(1, "%" + words + "%");
	            stmt1.setString(2, "%" + words + "%");
	            stmt1.setString(3, "%" + words + "%");
	            logger.debug("SQL query: " + stmt1.toString());
	            rs = stmt1.executeQuery();
	            
	            int nbLines = MiriamUtilities.getRowCount(rs);
	            for (int i=1; i<=nbLines; ++i)
	            {
	                ArrayList<String> temp = new ArrayList<String>();
	                temp.add(rs.getString("datatype_id"));
	                temp.add(shortDef(rs.getString("definition")));
	                resultsPublished.put(rs.getString("name"), temp);
	                
	                rs.next();
	            }
	            stmt1.close();
	            
	            // from 'synonym' table
	            stmt2.setString(1, "%" + words + "%");
	            logger.debug("SQL query: " + stmt2.toString());
	            rs = stmt2.executeQuery();
	            nbLines = MiriamUtilities.getRowCount(rs);
	            for (int i=1; i<=nbLines; ++i)
	            {
	                ArrayList<String> temp = new ArrayList<String>();
	                temp.add(rs.getString("datatype_id"));
	                temp.add(shortDef(rs.getString("definition")));
	                resultsPublished.put(rs.getString("name"), temp);
	                
	                rs.next();
	            }
	            stmt2.close();
	            
	            // from 'resource' table
	            stmt3.setString(1, "%" + words + "%");
	            stmt3.setString(2, "%" + words + "%");
	            stmt3.setString(3, "%" + words + "%");
	            stmt3.setString(4, "%" + words + "%");
	            logger.debug("SQL query: " + stmt3.toString());
	            rs = stmt3.executeQuery();
	            nbLines = MiriamUtilities.getRowCount(rs);
	            for (int i=1; i<=nbLines; ++i)
	            {
	                ArrayList<String> temp = new ArrayList<String>();
	                temp.add(rs.getString("datatype_id"));
	                temp.add(shortDef(rs.getString("definition")));
	                resultsPublished.put(rs.getString("name"), temp);
	                
	                rs.next();
	            }
	            stmt3.close();
	            
	            // from 'uri' table
	            stmt4.setString(1, "%" + words + "%");
	            logger.debug("SQL query: " + stmt4.toString());
	            rs = stmt4.executeQuery();
	            nbLines = MiriamUtilities.getRowCount(rs);
	            for (int i=1; i<=nbLines; ++i)
	            {
	                ArrayList<String> temp = new ArrayList<String>();
	                temp.add(rs.getString("datatype_id"));
	                temp.add(shortDef(rs.getString("definition")));
	                resultsPublished.put(rs.getString("name"), temp);
	                
	                rs.next();
	            }
	            stmt4.close();
	        }
	        catch (SQLException e)
	        {
	            logger.error("An exception occured during the search in published data collections!");
	            logger.error("SQL Exception raised: " + e.getMessage());
	            message = "Sorry, an error occurred during the processing of your search. As a consequence we cannot guarantee the accuracy of the result.<br />Please contact us <a href=\"mdb?section=contribute#team\" title=\"Contact page\">via this page</a> to solve this issue.";
	        }
	        finally
	        {
	            closePreparedStatement(stmt1);
	            closePreparedStatement(stmt2);
	            closePreparedStatement(stmt3);
	            closePreparedStatement(stmt4);
	        }
	        
	        
	        // the user is logged: we also search in the curation pipeline
	        HttpSession session = request.getSession();
	        if (MiriamUtilities.isUserAuthorised(session))
	        {
	            logged = true;
	            
	            try
	            {
	                // search in the 'data type' table
	                stmt5 = this.connection.prepareStatement("SELECT d.name, d.definition, d.datatype_id, m.state FROM cura_datatype d, cura_material m WHERE (((d.datatype_id LIKE ?) OR (d.name LIKE ?) OR (d.definition LIKE ?)) AND (d.datatype_id = m.ptr_datatype) AND ((m.state = 'Submitted') OR (m.state = 'Pending') OR (m.state = 'Curation') OR (m.state = 'Canceled')))");
	                // search in the 'synonym' table
	                stmt6 = this.connection.prepareStatement("SELECT d.name, d.definition, d.datatype_id, m.state FROM cura_datatype d, cura_synonym s, cura_material m WHERE ((d.datatype_id = s.ptr_datatype) AND (s.name LIKE ?) AND (d.datatype_id = m.ptr_datatype) AND ((m.state = 'Submitted') OR (m.state = 'Pending') OR (m.state = 'Curation') OR (m.state = 'Canceled')))");
	                // search in the 'resource' table
	                stmt7 = this.connection.prepareStatement("SELECT d.name, d.definition, d.datatype_id, m.state FROM cura_datatype d, cura_resource r, cura_material m WHERE ((d.datatype_id = r.ptr_datatype) AND ((r.resource_id LIKE ?) OR (r.info LIKE ?) OR (r.institution LIKE ?) OR (r.location LIKE ?)) AND (d.datatype_id = m.ptr_datatype) AND ((m.state = 'Submitted') OR (m.state = 'Pending') OR (m.state = 'Curation') OR (m.state = 'Canceled')))");
	                // search in the 'uri' table
	                stmt8 = this.connection.prepareStatement("SELECT d.name, d.definition, d.datatype_id, m.state FROM cura_datatype d, cura_uri u, cura_material m WHERE ((d.datatype_id = u.ptr_datatype) AND (u.uri LIKE ?) AND (d.datatype_id = m.ptr_datatype) AND ((m.state = 'Submitted') OR (m.state = 'Pending') OR (m.state = 'Curation') OR (m.state = 'Canceled')))");
	                
	            
	                // from 'data type' table
	                stmt5.setString(1, "%" + words + "%");
	                stmt5.setString(2, "%" + words + "%");
	                stmt5.setString(3, "%" + words + "%");
	                logger.debug("SQL query: " + stmt5.toString());
	                rs = stmt5.executeQuery();
	                
	                int nbLines = MiriamUtilities.getRowCount(rs);
	                for (int i=1; i<=nbLines; ++i)
	                {
	                    ArrayList<String> temp = new ArrayList<String>();
	                    temp.add(rs.getString("datatype_id"));
	                    temp.add(shortDef(rs.getString("definition")));
	                    temp.add(rs.getString("state"));
	                    resultsUnderCuration.put(rs.getString("name"), temp);
	                    
	                    rs.next();
	                }
	                stmt5.close();
	                
	                // from 'synonym' table
	                stmt6.setString(1, "%" + words + "%");
	                logger.debug("SQL query: " + stmt6.toString());
	                rs = stmt6.executeQuery();
	                nbLines = MiriamUtilities.getRowCount(rs);
	                for (int i=1; i<=nbLines; ++i)
	                {
	                    ArrayList<String> temp = new ArrayList<String>();
	                    temp.add(rs.getString("datatype_id"));
	                    temp.add(shortDef(rs.getString("definition")));
	                    temp.add(rs.getString("state"));
	                    resultsUnderCuration.put(rs.getString("name"), temp);
	                    
	                    rs.next();
	                }
	                stmt6.close();
	                
	                // from 'resource' table
	                stmt7.setString(1, "%" + words + "%");
	                stmt7.setString(2, "%" + words + "%");
	                stmt7.setString(3, "%" + words + "%");
	                stmt7.setString(4, "%" + words + "%");
	                logger.debug("SQL query: " + stmt7.toString());
	                rs = stmt7.executeQuery();
	                nbLines = MiriamUtilities.getRowCount(rs);
	                for (int i=1; i<=nbLines; ++i)
	                {
	                    ArrayList<String> temp = new ArrayList<String>();
	                    temp.add(rs.getString("datatype_id"));
	                    temp.add(shortDef(rs.getString("definition")));
	                    temp.add(rs.getString("state"));
	                    resultsUnderCuration.put(rs.getString("name"), temp);
	                    
	                    rs.next();
	                }
	                stmt7.close();
	                
	                // from 'uri' table
	                stmt8.setString(1, "%" + words + "%");
	                logger.debug("SQL query: " + stmt8.toString());
	                rs = stmt8.executeQuery();
	                nbLines = MiriamUtilities.getRowCount(rs);
	                for (int i=1; i<=nbLines; ++i)
	                {
	                    ArrayList<String> temp = new ArrayList<String>();
	                    temp.add(rs.getString("datatype_id"));
	                    temp.add(shortDef(rs.getString("definition")));
	                    temp.add(rs.getString("state"));
	                    resultsUnderCuration.put(rs.getString("name"), temp);
	                    
	                    rs.next();
	                }
	                stmt8.close();
	            }
	            catch (SQLException e)
	            {
	                logger.error("An exception occured during the search in 'under curation' data collections!");
	                logger.error("SQL Exception raised: " + e.getMessage());
	                message = "Sorry, an error occurred during the processing of your search. As a consequence we cannot guarantee the accuracy of the result.<br />Please contact us <a href=\"mdb?section=contribute#team\" title=\"Contact page\">via this page</a> to solve this issue.";
	            }
	            finally
	            {
	                closePreparedStatement(stmt5);
	                closePreparedStatement(stmt6);
	                closePreparedStatement(stmt7);
	                closePreparedStatement(stmt8);
	            } 
	        }
	        
	        request.setAttribute("counter", resultsPublished.size());
	        request.setAttribute("words", words);
	        request.setAttribute("data", resultsPublished);
	        request.setAttribute("curationData", resultsUnderCuration);
	        request.setAttribute("curationCounter", resultsUnderCuration.size());
	        request.setAttribute("userLogged", logged);
        }
        else   // no search query provided
        {
        	message = "No search query was provided!";
        	request.setAttribute("curationData", null);
        }
        
        if (null != message)
        {
            request.setAttribute("message", message);
        }
        
        page.append("search_result.jsp");
    }
    
    
    /**
     * Returns a shorter version of the definition.
     * Currently cuts after 160 characters and adds "...".
     * @param def
     * @return
     */
    private String shortDef(String def)
    {
    	if (def.length() > 160)
    	{
    		return def.substring(0, 160) + " [...]";
    	}
    	else
    	{
    		return def;
    	}
    }
}
