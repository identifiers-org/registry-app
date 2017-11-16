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


package uk.ac.ebi.miriam.db;


import uk.ac.ebi.miriam.tools.ResourceLog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * <p>Handles some database connections for manipulating <code>Resource</code>.
 * <p>Information: the states and associated colours are also duplicated in the CSS.
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
 * @version 20140324
 */
public class ResourceDao extends Dao
{
    private Logger logger = Logger.getLogger(ResourceDao.class);
    public static final Map<Integer, String> STATES;
    public static final int STATE_FAILURE = 0;
    public static final int STATE_SUCCESS = 1;
    public static final int STATE_UNKNOWN = 2;
    public static final int STATE_PROBABLY = 3;
    public static final int STATE_OBSOLETE = 4;
    public static final int STATE_RESTRICTED = 5;
    public static final int STATE_NA = 8;   // default value: no data
    public static final int STATE_NOTHING = 9;   // unexisting data
    
    
    static
    {
        STATES = new HashMap<Integer, String>();
        STATES.put(0, "down");   // FAILURE
        STATES.put(1, "up");   // SUCCESS
        STATES.put(2, "unknown");   // UNKNOWN
        STATES.put(3, "probably up");   // PROBABLY UP (usage of Javascript)
        STATES.put(4, "obsolete resource");   // RESOURCE OBSOLETE
        STATES.put(5, "restricted access");   // ACCESS RESTRICTED
        STATES.put(8, "na");   // NA (default value: no data)
        STATES.put(9, "no data");   // NOTHING (unexisting data)
    }
    
    
    /**
     * Constructor.
     * @param pool database pool
     */
    public ResourceDao(String pool)
    {
        super(pool);
    }
    
    
    /**
     * Retrieves the number of (not obsolete) resources currently stored in the database.
     * @return
     */
    public int getNbResources()
    {
        Statement stmt = null;
        int nb = 0;
        String sql = "SELECT COUNT(resource_id) AS number FROM mir_resource WHERE (obsolete=0)";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.first())
            {
                nb = rs.getInt("number");
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the number of (not obsolete) resources stored!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return nb;
    }
    
    
    /**
     * Retrieves the number of obsolete resources currently stored in the database.
     * @return
     */
    public int getNbObsoleteResources()
    {
        Statement stmt = null;
        int nb = 0;
        String sql = "SELECT COUNT(resource_id) AS number FROM mir_resource WHERE (obsolete=1)";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.first())
            {
                nb = rs.getInt("number");
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the number of obsolete resources stored!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return nb;
    }
    
    
    /**
     * Retrieves the number of all resources (obsolete and not) currently stored in the database.
     * @return
     */
    public int getNbAllResources()
    {
        Statement stmt = null;
        int nb = 0;
        String sql = "SELECT COUNT(resource_id) AS number FROM mir_resource";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.first())
            {
                nb = rs.getInt("number");
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the number of all resources stored!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return nb;
    }
    
    
    /**
     * Retrieves a resource given its identifier.
     * @param identifier resource identifier (for example 'MIR:00100008')
     * @return the resource or 'null' if none exist with the given identifier.
     */
    public Resource getResource(String identifier)
    {
        Resource res = null;
        String sql = "SELECT * FROM mir_resource WHERE (resource_id = ?)";
        PreparedStatement stmt = null;
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, identifier);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())   // there is at least one result returned (we don't check if more than one result is returned)
            {
                res = new Resource();
                res.setId(rs.getString("resource_id"));
                res.setInfo(rs.getString("info"));
                res.setInstitution(rs.getString("institution"));
                res.setLocation(rs.getString("location"));
                if (rs.getInt("obsolete") == 0)
                {
                    res.setObsolete(false);
                }
                else
                {
                    res.setObsolete(true);
                }
                res.setUrl_root(rs.getString("url_resource"));
                res.setUrl_prefix(rs.getString("url_element_prefix"));
                res.setUrl_suffix(rs.getString("url_element_suffix"));
                res.setExample(rs.getString("example"));
                res.setCollectionId(rs.getString("ptr_datatype"));
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the resource identified by: " + identifier);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return res;
    }
    
    
    /**
     * Retrieves all the resources associated with a data collection.
     * @param identifier official identifier of a data type (for example 'MIR:00000008')
     * @param whether one wants all the resources (including the non-deprecated ones)
     * @return
     */
    public List<Resource> getResources(String identifier, Boolean all)
    {
        PreparedStatement stmt = null;
        List<Resource> resources = new ArrayList<Resource>(230);   // ArrayList increases its array size by 50 percent when full
        String sql = null;
        
        // returns all resources
        if (all)
        {
            sql = "SELECT * FROM mir_resource WHERE ptr_datatype=?";
        }
        else   // returns only non-deprecated resources
        {
            sql = "SELECT * FROM mir_resource WHERE ((ptr_datatype=?) AND (obsolete = 0))";
        }
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, identifier);
            ResultSet rs = stmt.executeQuery();
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                Resource resource = new Resource();
                resource.setId(rs.getString("resource_id"));
                resource.setInfo(rs.getString("info"));
                resource.setInstitution(rs.getString("institution"));
                resource.setLocation(rs.getString("location"));
                if (rs.getInt("obsolete") == 0)
                {
                    resource.setObsolete(false);
                }
                else
                {
                    resource.setObsolete(true);
                }
                resource.setUrl_root(rs.getString("url_resource"));
                resource.setUrl_prefix(rs.getString("url_element_prefix"));
                resource.setUrl_suffix(rs.getString("url_element_suffix"));
                resource.setExample(rs.getString("example"));
                resource.setCollectionId(rs.getString("ptr_datatype"));
                resources.add(resource);
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the resource(s) of: " + identifier);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return resources;
    }
    
    
    /**
     * Retrieves the list of resources associated with the given data type, which don't provide Web Services.
     * @param identifier official identifier of a data type (for example 'MIR:00000008')
     * @return
     */
    public List<Resource> getResourcesWithoutWs(String identifier)
    {
        // all the resources associated with the given data type
        List<Resource> resources = getResources(identifier, true);
        
        // resources associated with the given data type and providing Web Services
        List<Resource> wsProvider = getResourcesProvidingWS(identifier);
        
        // remove the one providing Web Services
        resources.removeAll(wsProvider);
        
        return resources;
    }
    
    
    /**
     * Checks if a resource has a record in the table 'mir_url_check'.
     * @param identifier official identifier of a resource (for example 'MIR:00100008')
     * @return true or false
     */
    public boolean existingCheckDetails(String identifier)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        String sql = "SELECT * FROM mir_url_check WHERE resource_id=?";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, identifier);
            ResultSet rs = stmt.executeQuery();
            result = rs.first();
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the check of the existence of checking details for: " + identifier);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Creates a basic checking record (in 'mir_url_check').
     * Some information needs to be added by a curator (keyword).
     * 
     * @param datatypeId official identifier of a data type (for example 'MIR:00000008')
     * @param resourceId official identifier of a resource (for example 'MIR:00100008')
     * @param message comment to store
     * @return success of the creation or not?
     */
    public boolean createCheckRecord(String datatypeId, String resourceId, Integer state, String message)
    {
        PreparedStatement stmt = null;
        boolean success = true;
        String sql = "INSERT INTO mir_url_check (resource_id, datatype_id, date_last_check, comment, state) VALUES (?, ?, NOW(), ?, ?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resourceId);
            stmt.setString(2, datatypeId);
            stmt.setString(3, message);
            stmt.setInt(4, state);
            
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            logger.error("An exception occured while creating an new (empty) check record for: " + resourceId + " (state: " + state + ")!");
            logger.error("SQL Exception raised: " + e.getMessage());
            success = false;
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        // records the state of this resource for this specific day
        newStateRecord(resourceId, state);
        
        return success;
    }
    
    
    /**
     * Retrieves the checking keyword of a specific resource.
     * @param resourceId official identifier of a resource (for example 'MIR:00100008')
     * @return keyword used to check the health of the resource
     */
    public String getCheckingKeyword(String resourceId)
    {
        PreparedStatement stmt = null;
        String keyword = null;
        String sql = "SELECT keyword FROM mir_url_check WHERE (resource_id=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resourceId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())
            {
                keyword = rs.getString("keyword");
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the check keyword for: " + resourceId);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return keyword;
    }
    
    
    /**
     * Updates the check record of a resource with the data of the last successful check.
     * The only suitable states for using this method are: STATE_SUCCESS and STATE_PROBABLY
     * @param report data provided by the last checking process of this resource
     * @param state database record of the state of the resource: STATE_SUCCESS or STATE_PROBABLY only!
     * @return
     */
    public boolean updateCheckRecordSuccess(ResourceLog resource)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        String query = null;
        
        try
        {
            // previously the resource was responsive, so no need to update 'begin_uptime_period'
            if (wasConsideredWorking(resource.getResourceId()))
            {
                query = "UPDATE mir_url_check SET date_last_check=NOW(), date_last_check_success=NOW(), uptime=uptime+1, comment=?, errors=?, state=? WHERE (resource_id=?)";
            }
            else   // previously, the resource was not working fine, so 'begin_uptime_period' needs to be updated to current date
            {
                query = "UPDATE mir_url_check SET date_last_check=NOW(), date_last_check_success=NOW(), begin_uptime_period=NOW(), uptime=uptime+1, comment=?, errors=?, state=? WHERE (resource_id=?)";
            }
            
            stmt = openPreparedStatement(query);
            stmt.setString(1, resource.getLogs());
            stmt.setString(2, resource.getErrors());
            stmt.setInt(3, resource.getState());
            stmt.setString(4, resource.getResourceId());
            int returnedValue = stmt.executeUpdate();
            if (returnedValue > 0)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the update of the health record of: " + resource.getResourceId() + " (" + resource.getState() + ")");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        // records the state of this resource for this specific day
        newStateRecord(resource.getResourceId(), resource.getState());
        
        return result;
    }
    
    
    /**
     * Updates the health check record of a resource (state = up).
     * @param report data provided by the last checking process of this resource
     * @return
     */
    public boolean updateCheckRecordFailure(ResourceLog resource)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        String query = null;
        
        try
        {
            // the resource was failing beforehand
            if (wasResourceFailing(resource.getResourceId()))
            {
                query = "UPDATE mir_url_check SET date_last_check=NOW(), date_last_check_failure=NOW(), downtime=downtime+1, comment=?, errors=?, state=" + STATE_FAILURE + " WHERE (resource_id=?)";
            }
            else   // the resource was not failing the check test before, we need to update the 'begin_downtime_period'
            {
                query = "UPDATE mir_url_check SET date_last_check=NOW(), date_last_check_failure=NOW(), begin_downtime_period=NOW(), downtime=downtime+1, comment=?, errors=?, state=" + STATE_FAILURE + " WHERE (resource_id=?)";
            }
            
            stmt = openPreparedStatement(query);
            stmt.setString(1, resource.getLogs());
            stmt.setString(2, resource.getErrors());
            stmt.setString(3, resource.getResourceId());
            
            int state = stmt.executeUpdate();
            if (state > 0)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the update of the health record: " + resource.getResourceId() + " (" + STATE_FAILURE + ")");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        // records the state of this resource for this specific day
        newStateRecord(resource.getResourceId(), STATE_FAILURE);
        
        return result;
    }
    
    
    /**
     * Updates the health check record of a resource (state = unknown).
     * @param identifier
     */
    public boolean updateCheckRecordUnknown(ResourceLog resource)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        String sql = "UPDATE mir_url_check SET date_last_check=NOW(), unknown=unknown+1, comment=?, errors=?, state=? WHERE (resource_id=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resource.getLogs());
            stmt.setString(2, resource.getErrors());
            stmt.setInt(3, resource.getState());
            stmt.setString(4, resource.getResourceId());
            int resultInt = stmt.executeUpdate();
            
            if (resultInt > 0)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the update of the health record of: " + resource.getResourceId() + " (" + resource.getState() + ")");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        // records the state of this resource for this specific day
        newStateRecord(resource.getResourceId(), resource.getState());
        
        return result;
    }
    
    
    /**
     * Sets the current state of a resource.
     * Also updates the date of last check.
     * This method should *not* be used for the following states: STATE_FAILURE, STATE_SUCCESS and STATE_UNKNOWN. Dedicated methods exist for those!
     * @return whether the update was a success or not 
     */
    public Boolean updateCheckRecord(ResourceLog resource)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        String query = null;
        
        try
        {
            query = "UPDATE mir_url_check SET date_last_check=NOW(), comment=?, errors=?, state=? WHERE (resource_id=?)";
            
            stmt = openPreparedStatement(query);
            stmt.setString(1, resource.getLogs());
            stmt.setString(2, resource.getErrors());
            stmt.setInt(3, resource.getState());
            stmt.setString(4, resource.getResourceId());
            int returnedValue = stmt.executeUpdate();
            if (returnedValue > 0)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the update of the health state of: " + resource.getResourceId() + " (" + resource.getState() + ")");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        // records the state of this resource for this specific day
        newStateRecord(resource.getResourceId(), resource.getState());
        
        return result;
    }
    
    
    /**
     * Retrieves the health check details of a resource,given its identifier.
     * @param id official identifier of a resource (for example 'MIR:00100005')
     * @return heath check details of the resource, can be null if the resource doesn't exist
     */
    public ResourceCheckDetails getCheckDetails(String id)
    {
        PreparedStatement stmt = null;
        ResourceCheckDetails details = null;
        String sql = "SELECT * FROM mir_url_check c, mir_resource r, mir_datatype d WHERE ((c.resource_id = r.resource_id) AND (c.datatype_id = d.datatype_id) AND (r.ptr_datatype = d.datatype_id) AND (c.resource_id=?))";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.first())
            {
                details = new ResourceCheckDetails(rs.getString("resource_id"), rs.getString("info"), rs.getString("datatype_id"), rs.getString("name"), rs.getInt("state"), rs.getTimestamp("date_last_check"), rs.getTimestamp("date_last_check_success"), rs.getTimestamp("date_last_check_failure"), rs.getTimestamp("begin_uptime_period"), rs.getTimestamp("begin_downtime_period"), rs.getInt("uptime"), rs.getInt("downtime"), rs.getInt("unknown"), rs.getInt("ajax"), rs.getInt("frame_deny"), rs.getInt("binary"), rs.getString("keyword"), rs.getString("comment"), rs.getString("errors"), rs.getString("url_element_prefix") + rs.getString("example") + rs.getString("url_element_suffix"));
                if (rs.getInt("obsolete") == 0)
                {
                    details.setObsolete(false);
                }
                else
                {
                    details.setObsolete(true);
                }
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the health check details of: " + id);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return details;
    }
    
    
    /**
     * Retrieves the list of all resources stored
     * @param rootUrl root URL of where the webapp is deployed
     * @return list of all the resources stored
     */
    public List<SimpleResource> getSimpleResources(String rootUrl)
    {
        PreparedStatement stmt = null;
        List<SimpleResource> resources = new ArrayList<SimpleResource>();
        String sql = "SELECT r.resource_id, r.info, r.institution, r.location, d.name, d.datatype_id, c.uptime, c.downtime, c.unknown, c.state FROM mir_url_check c, mir_resource r, mir_datatype d WHERE ((c.resource_id = r.resource_id) AND (c.datatype_id = d.datatype_id) AND (r.ptr_datatype = d.datatype_id))";
        
        try
        {
            stmt = openPreparedStatement(sql);
            ResultSet rs = stmt.executeQuery();
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                SimpleResourceHtml res = new SimpleResourceHtml();
                res.setUrlRoot(rootUrl);
                res.setId(rs.getString("resource_id"));
                res.setName(rs.getString("info"));
                res.setOrganisation(rs.getString("institution"));
                res.setCountry(rs.getString("location"));
                res.setDatatypeId(rs.getString("datatype_id"));
                res.setDatatypeName(rs.getString("name"));
                // computes reliability
                int uptime = rs.getInt("uptime");
                int downtime = rs.getInt("downtime");
                int unknown = rs.getInt("unknown");
                int reliability = 0;
                int totalDays = uptime + downtime + unknown;
                if (totalDays > 0)
                {
                    reliability = Math.round(uptime * 100 / totalDays);
                }
                res.setReliability(reliability);
                
                resources.add(res);
                
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of all the resources.");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return resources;
    }
    
    
    /**
     * Retrieves the list of simple health report for all resources.
     * @return
     */
    public List<ResourcesCheckReport> getCheckReport()
    {
        PreparedStatement stmt = null;
        int i = 0;
        List<ResourcesCheckReport> resources = new ArrayList<ResourcesCheckReport>();
        String sql = "SELECT * FROM mir_url_check c, mir_resource r, mir_datatype d WHERE ((c.resource_id = r.resource_id) AND (c.datatype_id = d.datatype_id) AND (r.ptr_datatype = d.datatype_id)) ORDER BY d.name, c.resource_id";
        
        try
        {
            stmt = openPreparedStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                ResourcesCheckReport resource = new ResourcesCheckReport(rs.getString("resource_id"), rs.getString("info"), rs.getString("datatype_id"), rs.getString("name"), rs.getInt("state"), rs.getInt("uptime"), rs.getInt("downtime"), rs.getInt("unknown"));
                
                // class of the row, for nice colour in the table
                if (i % 2 == 0)
                {
                    resource.setHtmlClass("par");
                }
                else
                {
                    resource.setHtmlClass("odd");
                }
                resources.add(resource);
                i++;
                
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the basic health reports for all resources!");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return resources;
    }
    
    
    /**
     * Retrieves the list of simple health report for resources of a given state.
     * @param type (no check on the type!)
     * @return
     */
    public List<ResourcesCheckReport> getCheckReport(int type)
    {
        PreparedStatement stmt = null;
        int i = 0;
        List<ResourcesCheckReport> resources = new ArrayList<ResourcesCheckReport>();
        String sql = null;
        
        try
        {
            // request for obsolete resources (includes all resources from obsolete data collections)
            if (type == 4)
            {
                sql = "SELECT * FROM mir_url_check c, mir_resource r, mir_datatype d WHERE (((r.obsolete = 1) OR (d.obsolete = 1)) AND (c.resource_id = r.resource_id) AND (c.datatype_id = d.datatype_id) AND (r.ptr_datatype = d.datatype_id)) ORDER BY d.name, c.resource_id";
                stmt = openPreparedStatement(sql);
            }
            else   // do not display obsolete resources or resources from obsolete collections
            {
                sql = "SELECT * FROM mir_url_check c, mir_resource r, mir_datatype d WHERE ((c.state = ?) AND (r.obsolete = 0) AND (d.obsolete = 0) AND (c.resource_id = r.resource_id) AND (c.datatype_id = d.datatype_id) AND (r.ptr_datatype = d.datatype_id)) ORDER BY d.name, c.resource_id";
                stmt = openPreparedStatement(sql);
                stmt.setInt(1, type);    
            }
            
            ResultSet rs = stmt.executeQuery();
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                ResourcesCheckReport resource = new ResourcesCheckReport(rs.getString("resource_id"), rs.getString("info"), rs.getString("datatype_id"), rs.getString("name"), rs.getInt("state"), rs.getInt("uptime"), rs.getInt("downtime"), rs.getInt("unknown"));
                
                // class of the row, for nice colour in the table
                if (i % 2 == 0)
                {
                    resource.setHtmlClass("par");
                }
                else
                {
                    resource.setHtmlClass("odd");
                }
                resources.add(resource);
                i++;
                
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the basic health reports for all resources!");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return resources;
    }
    
    
    /**
     * Tests if a specific resource exists in the database, based on its identifier.
     * 
     * @param id identifier of a resource, for example 'MIR:00100008'
     * @return <code>boolean</code>: True if the resource exists in the database, False otherwise
     */
    public boolean resourceExists(String id)
    {
        PreparedStatement stmt = null;
        boolean result = false;   // default value
        String sql = "SELECT * FROM mir_resource WHERE (resource_id=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, id);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the test of existence of the resource: " + id);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Checks if a given resource is associated with a given data type.
     * @param resourceId identifier of a resource, for example 'MIR:00100008'
     * @param datatypeId identifier of a data type, for example 'MIR:00000008'
     * @return
     */
    public boolean isResourceAssociatedWithDataType(String resourceId, String datatypeId)
    {
        boolean result = false;
        PreparedStatement stmt = null;
        String sql = "SELECT * FROM mir_datatype d, mir_resource r WHERE ((d.datatype_id = ?) AND (r.resource_id = ?) AND (r.ptr_datatype = d.datatype_id))";
        
        if ((datatypeId.matches("MIR:\\d{8}")) && (resourceExists(resourceId)))
        {
            try
            {
                stmt = openPreparedStatement(sql);
                stmt.setString(1, datatypeId);
                stmt.setString(2, resourceId);
                logger.debug("SQL prepared query: " + stmt.toString());
                ResultSet rs = stmt.executeQuery();
                
                if (rs.first())
                {
                    result = true;
                }
            }
            catch (SQLException e)
            {
                logger.warn("An exception occurred during the test of association of the resource " + resourceId + " and the data type " + datatypeId);
                logger.warn("SQLException raised: " + e.getMessage());
            }
            finally
            {
                closePreparedStatement(stmt);
            }
        }
        
        return result;
    }
    
    
    /**
     * Updates the value of the keyword used to check the health of a resource.
     * @param resourceId official identifier of a resource (for example 'MIR:00100005')
     * @param keyword health check keyword
     * @return
     */
    public boolean updateCheckKeyword(String resourceId, String keyword)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        
        // the data type exists
        if (resourceExists(resourceId))
        {
            String sql = "UPDATE mir_url_check SET keyword=? WHERE (resource_id=?)";
            try
            {
                stmt = openPreparedStatement(sql);
                stmt.setString(1, keyword);
                stmt.setString(2, resourceId);
                int state = stmt.executeUpdate();
                if (state == 1)
                {
                    result = true;
                }
            }
            catch (SQLException e)
            {
                logger.warn("An exception occurred during the update of the check keyword for the resource: " + resourceId);
                logger.warn("SQLException raised: " + e.getMessage());
            }
            finally
            {
                closePreparedStatement(stmt);
            }
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the resources which have been down for more than a given number of checks.
     * Usually, there is a check per day.
     * @param length number of continuous checks for which the resource has been down
     * @param all whether to consider all resources or only non-obsolete ones and ones associated to non-obsolete data collections
     * @return
     */
    public List<ResourcesCheckReport> getResourcesDown(int length, Boolean all)
    {
        PreparedStatement stmt = null;
        int i = 0;
        List<ResourcesCheckReport> resources = new ArrayList<ResourcesCheckReport>();
        String sql = null;
        // search from all resources
        if (all)
        {
            sql = "SELECT * FROM mir_url_check c, mir_resource r, mir_datatype d WHERE ((c.state = 0) AND (DATEDIFF(date_last_check_failure, begin_downtime_period) > ?) AND (c.resource_id = r.resource_id) AND (c.datatype_id = d.datatype_id) AND (r.ptr_datatype = d.datatype_id)) ORDER BY d.name, c.resource_id";
        }
        else   // search from non-obsolete resources only (also excludes resources from obsolete data collections)
        {
            sql = "SELECT * FROM mir_url_check c, mir_resource r, mir_datatype d WHERE ((d.obsolete = 0) AND (r.obsolete = 0) AND (c.state = 0) AND (DATEDIFF(date_last_check_failure, begin_downtime_period) > ?) AND (c.resource_id = r.resource_id) AND (c.datatype_id = d.datatype_id) AND (r.ptr_datatype = d.datatype_id)) ORDER BY d.name, c.resource_id";
        }
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setInt(1, length);
            ResultSet rs = stmt.executeQuery();
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                ResourcesCheckReport resource = new ResourcesCheckReport(rs.getString("resource_id"), rs.getString("info"), rs.getString("datatype_id"), rs.getString("name"), rs.getInt("state"), rs.getInt("uptime"), rs.getInt("downtime"), rs.getInt("unknown"));
                
                // class of the row, for nice colour in the table
                if (i % 2 == 0)
                {
                    resource.setHtmlClass("par");
                }
                else
                {
                    resource.setHtmlClass("odd");
                }
                resources.add(resource);
                i++;
                
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the basic health reports for resources failing for more than " + length + " checks/days!");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return resources;
    }
    
    
    /**
     * Checks if a resource uses Ajax to load pages.
     * @param resourceId official identifier of a resource (for example 'MIR:00100005')
     * @return
     */
    public boolean ajaxUsed(String resourceId)
    {
        PreparedStatement stmt = null;
        boolean ajaxUsed = false;
        String sql = "SELECT ajax FROM mir_url_check WHERE (resource_id=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resourceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.first())
            {
                if (rs.getInt("ajax") == 1)
                {
                    ajaxUsed = true;
                }
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the ajax usage for: " + resourceId);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return ajaxUsed;
    }
    
    
    /**
     * Checks if a resource returns a binary file.
     * @param resourceId official identifier of a resource (for example 'MIR:00100005')
     * @return
     */
    public boolean returnsBinaryData(String resourceId)
    {
        PreparedStatement stmt = null;
        boolean binary = false;
        String sql = "SELECT `binary` FROM mir_url_check WHERE (resource_id=?)";   // 'binary' is a reserved word
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resourceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.first())
            {
                if (rs.getInt("binary") > 0)
                {
                    binary = true;
                }
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the binary usage for: " + resourceId);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return binary;
    }
    
    
    /**
     * Retrieves the full health checks history of a given resource  
     * @param resourceId official identifier of a resource (for example 'MIR:00100005')
     * @return
     */
    public Map<Integer, Map<String, List<Integer>>> getResourceCheckHistory(String resourceId)
    {
        PreparedStatement stmt = null;
        String sql = "SELECT * FROM mir_url_history WHERE (resource_id=?) ORDER BY check_date DESC";
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int currentYear = 0;
        String currentMonth = "empty";
        Map<Integer, Map<String, List<Integer>>> history = new LinkedHashMap<Integer, Map<String, List<Integer>>>();   // keeps the order
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resourceId);
            ResultSet rs = stmt.executeQuery();
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                int state = rs.getInt("state");
                Date date = rs.getTimestamp("check_date");
                
                // converts Date into Calendar
                Calendar cal = new GregorianCalendar();
                cal.setTime(date);
                
                // retrieves some parts of the date
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                String monthName = monthNames[month];
                int day = cal.get(Calendar.DAY_OF_MONTH);
                
                // new year
                if (currentYear != year)
                {
                    currentYear = year;
                    
                    Map<String, List<Integer>> yearValues = new LinkedHashMap<String, List<Integer>>();
                    // initialises the full year
                    //for (int i=0; i<cal.getActualMaximum(Calendar.MONTH); ++i)
                    for (int monthIndex=0; monthIndex<12; ++monthIndex)
                    {
                        List<Integer> monthValues = new ArrayList<Integer>();
                        Calendar cal2 = new GregorianCalendar();
                        cal2.set(year, monthIndex, 1);
                        int maxDays = cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
                        for (int dayIndex=1; dayIndex<=31; ++dayIndex)
                        {
                            if (dayIndex <= maxDays)
                            {
                                monthValues.add(STATE_NA);
                            }
                            else
                            {
                                monthValues.add(STATE_NOTHING);
                            }
                        }
                        
                        yearValues.put(monthNames[monthIndex], monthValues);
                    }
                    
                    history.put(year, yearValues);
                }
                
                // new month
                if (!currentMonth.equals(monthName))
                {
                    currentMonth = monthName;
                }
                
                history.get(currentYear).get(monthName).set(day-1, state);   // index of List starts at 0
                
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the ajax usage for: " + resourceId);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return history;
    }
    
    
    /**
     * Check whether a given resource has some kind of access restriction.
     * @param id identifier of a resource
     * @return
     */
    public boolean hasAccessRestriction(String resourceId)
    {
        PreparedStatement stmt = null;
        boolean hasAccessRestriction = false;
        String sql = "SELECT r.ptr_restriction FROM mir_resource a, mir_restriction r WHERE ((r.ptr_restriction = 3) AND (r.ptr_datatype = a.ptr_datatype) AND (a.resource_id = ?))";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resourceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.first())
            {
                if (rs.getInt("ptr_restriction") == 3)
                {
                    hasAccessRestriction = true;
                }
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred when checking whether resource " + resourceId + " has some kind of access restriction.");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return hasAccessRestriction;
    }
    
    
    /**
     * Retrieves the state (integer) of a resource.
     * @param resourceId identifier of a resource
     * @return state (integer)
     */
    public Integer getState(String resourceId)
    {
    	PreparedStatement stmt = null;
    	Integer state = null;
    	String sql = "SELECT state FROM mir_url_check WHERE (resource_id = ?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resourceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.first())
            {
                state = rs.getInt("state");
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the state of the resource: " + resourceId);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return state;
    }
    
    /**
     * Safely retrieves the (human) description of a state identifier.
     * If the provided identifier does not exist, returns "unknown".
     * @param stateId
     * @return
     */
    public static String getStateDesc(Integer stateId)
    {
    	String state = null;
    	
    	if (null != stateId)
    	{
	        state = STATES.get(stateId);
	        if (null == state)
	        {
	            state = "unknown";
	        }
    	}
    	else
    	{
    		state = "unknown";
    	}
        
        return state;
    }
    
    /**
     * Safely retrieves the first word (human) description of a state identifier (this can be useful for generating hyperlinks).
     * If the provided identifier does not exist, returns "unknown".
     * @param stateId
     * @return
     */
    public static String getShortStateDesc(Integer stateId)
    {
        String state = STATES.get(stateId);
        if (null == state)
        {
            state = "unknown";
        }
        // retrieves the first word, if the description is composed of more than one word separated by at least one space
        if (state.contains(" "))
        {
            state = state.substring(0, state.indexOf(" "));
        }
        
        return state;
    }
    
    /**
     * Provide the colour to be used in the interface to represent a given state.
     * The colour provided can be directly used as it is in HTML pages.
     * WARNING: those colours are duplicated in the CSS.
     * @param stateId
     * @return
     */
    public static String getStateColour(Integer state)
    {
        String colour = null;
        
        switch (state)
        {
            case ResourceDao.STATE_FAILURE:
                colour = "#f4b6b6";   // light red
                break;
            case ResourceDao.STATE_SUCCESS:
                colour = "#a1c7c7";   // light EBI green
                break;
            case ResourceDao.STATE_PROBABLY:
                colour = "#cae2c3";   // light green
                break;
            case ResourceDao.STATE_UNKNOWN:
                colour = "#deebeb";   // very light EBI green
                break;
            case ResourceDao.STATE_OBSOLETE:
                colour = "#5e5e5e";   // light grey
                break;
            case ResourceDao.STATE_RESTRICTED:
                colour = "#feca5b";   // light yellow
                break;
            default:
                colour = "#deebeb";   // = unknown (very light EBI green)
                break;
        }
        
        return colour;
    }
    
    /**
     * Records the state of the resource for this specific test (usually equivalent to a day). 
     * @param resourceId
     * @param stateFailure
     * @return 'true' if success, 'false' otherwise
     */
    private boolean newStateRecord(String resourceId, int state)
    {
        boolean result = false;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO mir_url_history (resource_id, check_date, state) VALUES (?, NOW(), ?)";
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resourceId);
            stmt.setInt(2, state);
            int sqlResult = stmt.executeUpdate();
            if (sqlResult == 1)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the creation of a new record state for the resource: " + resourceId + " (" + state + ")");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /*
     * Retrieves the current state (recorded in the database) of a resource.
     * @param resourceId official identifier of a resource (for example 'MIR:00100005')
     * @return
     */
    private Integer retrieveRecordedState(String resourceId)
    {
        int result = 0;
        PreparedStatement stmt = null;
        String sql = "SELECT state FROM mir_url_check WHERE (resource_id=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resourceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.first())
            {
                result = rs.getInt("state");
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the state of: " + resourceId);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    /*
     * Checks whether a resource was up or probably up during the last check.
     * @param resourceId official identifier of a resource (for example 'MIR:00100005')
     * @return
     */
    private boolean wasConsideredWorking(String resourceId)
    {
        int state = retrieveRecordedState(resourceId);
        return ((STATE_SUCCESS == state) || (STATE_PROBABLY == state));
    }
    
    /*
     * Checks whether a resource was failing during the last check.
     * @param resourceId official identifier of a resource (for example 'MIR:00100005')
     * @return
     */
    private boolean wasResourceFailing(String resourceId)
    {
        int state = retrieveRecordedState(resourceId);
        return (STATE_FAILURE == state);
    }
    
    
    /*
     * Retrieves the number of days of uptime for a resource.
     * Returns -1 if an error occurred during the retrieval.
     * @param resourceId
     * @return
     */
    private int getUptimeCount(String resourceId)
    {
        PreparedStatement stmt = null;
        int result = -1;
        String sql = "SELECT uptime FROM mir_url_check WHERE (resource_id=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resourceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.first())
            {
                result = rs.getInt("uptime");
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the uptime for: " + resourceId);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /*
     * Retrieves the number of days of downtime for a resource.
     * Returns -1 if an error occurred during the retrieval.
     * @param resourceId
     * @return
     */
    private int getCheckDowntime(String resourceId)
    {
        PreparedStatement stmt = null;
        int result = -1;
        String sql = "SELECT downtime FROM mir_url_check WHERE (resource_id=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resourceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.first())
            {
                result = rs.getInt("downtime");
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the downtime for: " + resourceId);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /*
     * Retrieves the number of days of unknown state for a resource.
     * Returns -1 if an error occurred during the retrieval.
     * @param resourceId
     * @return
     */
    private int getCheckUnknown(String resourceId)
    {
        PreparedStatement stmt = null;
        int result = -1;
        String sql = "SELECT unknown FROM mir_url_check WHERE (resource_id=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resourceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.first())
            {
                result = rs.getInt("unknown");
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the unknown state for: " + resourceId);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /*
     * Retrieves the list of resources, associated with the given data collection, providing Web Services.
     * @param identifier official identifier of a data type (for example 'MIR:00000008')
     * @return
     */
    private List<Resource> getResourcesProvidingWS(String identifier)
    {
        PreparedStatement stmt = null;
        List<Resource> resources = new ArrayList<Resource>();
        String sql = "SELECT * FROM mir_resource r, mir_web_services s WHERE ((r.ptr_datatype=?) AND (s.ptr_resource = r.resource_id))";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, identifier);
            ResultSet rs = stmt.executeQuery();
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                Resource resource = new Resource();
                resource.setId(rs.getString("resource_id"));
                resource.setInfo(rs.getString("info"));
                resource.setInstitution(rs.getString("institution"));
                resource.setLocation(rs.getString("location"));
                if (rs.getInt("obsolete") == 0)
                {
                    resource.setObsolete(false);
                }
                else
                {
                    resource.setObsolete(true);
                }
                resource.setUrl_root(rs.getString("url_resource"));
                resource.setUrl_prefix(rs.getString("url_element_prefix"));
                resource.setUrl_suffix(rs.getString("url_element_suffix"));
                resource.setExample(rs.getString("example"));
                resource.setCollectionId(rs.getString("ptr_datatype"));
                resources.add(resource);
                
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the list of resources, associated with the data type '" + identifier + "', providing Web Services!");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return resources;
    }
}
