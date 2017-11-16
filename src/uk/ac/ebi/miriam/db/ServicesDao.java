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


package uk.ac.ebi.miriam.db;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * <p>Handles some database connections for manipulating <code>WebService</code>, mainly dealing with the database table 'mir_web_services'.
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
 * @version 20100628
 */
public class ServicesDao extends Dao
{
    private Logger logger = Logger.getLogger(ServicesDao.class);
    
    
    /**
     * Constructor.
     * @param pool database pool
     */
    public ServicesDao(String pool)
    {
        super(pool);
    }
    
    
    /**
     * Retrieves a Web Service record, given its identifier.
     * @param id Web Service record identifier
     * @return
     */
    public WebService getWebService(String id)
    {
        WebService service = null;
        PreparedStatement stmt = null;
        String sql = "SELECT w.id, w.type, w.ptr_resource, w.info, w.endpoint, w.wsdl, w.doc, w.info, r.info, r.institution, r.location FROM mir_web_services w, mir_resource r WHERE ((w.id = ?) AND (w.ptr_resource = r.resource_id))";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())
            {
                service = new WebService();
                service.setId(rs.getString("w.id"));
                service.setType(rs.getString("w.type"));
                service.setResId(rs.getString("w.ptr_resource"));
                service.setResInfo(rs.getString("r.info"));
                service.setDesc(rs.getString("w.info"));
                service.setEndpoint(rs.getString("w.endpoint"));
                service.setWsdl(rs.getString("w.wsdl"));
                service.setDoc(rs.getString("w.doc"));
                service.setOrg(rs.getString("r.institution"));
                service.setLoc(rs.getString("r.location"));
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the Web Services record " + id + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return service;
    }
    
    
    /**
     * Retrieves all the services available for a given data type.
     * 
     * @param datatypeId identifier of a data type
     * @return list of all the services available for a data type, listed by type (SOAP, REST, ...)
     */
    public Map<String, List<WebService>> getDataTypeServices(String datatypeId)
    {
        PreparedStatement stmt = null;
        Map<String, List<WebService>> result = new HashMap<String, List<WebService>>();
        String sql = "SELECT w.id, w.type, w.ptr_resource, w.info, w.endpoint, w.wsdl, w.doc, w.info, r.info, r.institution, r.location FROM mir_web_services w, mir_resource r, mir_datatype d WHERE ((r.ptr_datatype = ?) AND (d.datatype_id = r.ptr_datatype) AND (w.ptr_resource = r.resource_id))";
        
        // initialisation
        List<String> types = getServicesTypes();
        for (String type: types)
        {
            result.put(type, new ArrayList<WebService>());
        }
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, datatypeId);
            ResultSet rs = stmt.executeQuery();
            
            boolean notEmpty = rs.first();
            while (notEmpty)
            {
                WebService temp = new WebService();
                temp.setId(rs.getString("w.id"));
                temp.setType(rs.getString("w.type"));
                temp.setResId(rs.getString("w.ptr_resource"));
                temp.setResInfo(rs.getString("r.info"));
                temp.setDesc(rs.getString("w.info"));
                temp.setEndpoint(rs.getString("w.endpoint"));
                temp.setWsdl(rs.getString("w.wsdl"));
                temp.setDoc(rs.getString("w.doc"));
                temp.setOrg(rs.getString("r.institution"));
                temp.setLoc(rs.getString("r.location"));
                
                result.get(rs.getString("w.type")).add(temp);
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of Web Services available for: " + datatypeId + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the list of all the different types of Web Services (for example: 'REST', 'SOAP', ...)
     * @return
     */
    public List<String> getServicesTypes()
    {
        Statement stmt = null;
        List<String> result = new ArrayList<String>();
        String sql = "SELECT DISTINCT type FROM mir_web_services ORDER BY type ASC";
        
        try
        {
            stmt = openStatement();
            ResultSet sqlResult = stmt.executeQuery(sql);
            boolean notEmpty = sqlResult.first();
            while (notEmpty)
            {
                result.add(sqlResult.getString("type"));
                notEmpty = sqlResult.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of types of Web Services!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Updates a Web Services record.
     * @param service Web Services to update (contains the new data)
     * @return True if the update is a success, False otherwise
     */
    public boolean updateWSrecord(WebService service)
    {
        boolean result = false;
        PreparedStatement stmt = null;
        String sql = "UPDATE mir_web_services SET type=?, endpoint=?, wsdl=?, doc=?, info=? WHERE ((id = ?) AND (ptr_resource = ?))";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, service.getType());
            stmt.setString(2, service.getEndpoint());
            stmt.setString(3, service.getWsdl());
            stmt.setString(4, service.getDoc());
            stmt.setString(5, service.getDesc());
            stmt.setString(6, service.getId());
            stmt.setString(7, service.getResId());
            int state = stmt.executeUpdate();
            if (state == 1)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the update of the Web Service record '" + service.getId() + "', provided by: " + service.getResId() + "!");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Creates a Web Services record.
     * 
     * @param service service Web Services to create
     * @return True if the update is a success, False otherwise
     */
    public boolean createWSrecord(WebService service)
    {
        boolean result = false;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO mir_web_services (ptr_resource, type, endpoint, wsdl, doc, info) VALUES (?, ?, ?, ?, ?, ?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, service.getResId());
            stmt.setString(2, service.getType());
            stmt.setString(3, service.getEndpoint());
            stmt.setString(4, service.getWsdl());
            stmt.setString(5, service.getDoc());
            stmt.setString(6, service.getDesc());
            int state = stmt.executeUpdate();
            if (state == 1)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the creation of the Web Service record '" + service.getId() + "', provided by: " + service.getResId() + "!");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
}
