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


import uk.ac.ebi.miriam.db.SimpleDataType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * <p>Handles some database connections for manipulating <code>DataType</code> (some other features are provided by <code>DataTypeHibernate</code>).
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2014 BioModels.net (EMBL - European Bioinformatics Institute)
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
public class DataTypeDao extends Dao
{
    private Logger logger = Logger.getLogger(DataTypeDao.class);
    
    
    /**
     * Constructor.
     * @param pool database pool
     */
    public DataTypeDao(String pool)
    {
        super(pool);
    }
    
    
    /**
     * Tests if a specific data type exists in the database, based on its identifier.
     * 
     * @param id identifier of a data type, for example 'MIR:00000008'
     * @return <code>boolean</code>: True if the data type exists in the database, False otherwise
     */
    public boolean dataTypeExists(String id)
    {
        PreparedStatement stmt = null;
        boolean result = false;   // default value
        String sql = "SELECT name FROM mir_datatype WHERE (datatype_id=?)";
        
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
            logger.warn("An exception occurred during the test of existence of the data type: " + id);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the name of a data type based on its identifier.
     * @param id identifier of a data collection, for example 'MIR:00000008'.
     * @return name of the data type (<code>null</code> if the data type doesn't exist)
     */
    public String getDataTypeName(String id)
    {
        PreparedStatement stmt = null;
        String result = null;
        String sql = "SELECT name FROM mir_datatype WHERE (datatype_id=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, id);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())
            {
                result = rs.getString("name");
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the existence test of the following data collection: " + id);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Checks whether a data collection has any restrictions associated with it.
     * @param id identifier of a data collection, for example 'MIR:00000008'.
     * @return
     */
    public Boolean isRestricted(String id)
    {
        PreparedStatement stmt = null;
        Boolean result = null;
        Integer value = null;
        String sql = "SELECT restriction FROM mir_datatype WHERE (datatype_id=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, id);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())
            {
                value = rs.getInt("restriction");
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the restricted status of the following data collection: " + id);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        if (null != value)
        {
            if (value == 0)
            {
                result = false;
            }
            else
            {
                result = true;
            }
        }
        else
        {
            result = true;   // default: restricted data collection
            logger.warn("Unable to retrieve the restricted status of the following data collection: " + id);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves a data type (simple version) from the database, based on its identifier.
     * 
     * @param id identifier of a data type (for example: 'MIR:00000008')
     * @return a data type (can be null)
     */
    public SimpleDataType getSimpleDataTypeById(String id)
    {
        PreparedStatement stmt = null;
        SimpleDataType data = null;
        String sql = "SELECT d.datatype_id, d.name, d.definition, u.uri FROM mir_datatype d, mir_uri u WHERE ((d.datatype_id=?) AND (d.datatype_id=u.ptr_datatype) AND (u.uri_type='URN') AND (u.deprecated=0))";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, id);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            // no check if several results...
            if (rs.first())
            {
                data = new SimpleDataType();
                data.setId(rs.getString("d.datatype_id"));
                data.setName(rs.getString("d.name"));
                data.setDefinition(rs.getString("d.definition"));
                data.setUri(rs.getString("u.uri"));
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the data type: " + id);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return data;
    }
    
    
    /**
     * Retrieves a list of data type from the database, based on their (primary, not synonym) name.
     * Warning: doesn't retrieve the URI of the data types!
     * @param dataTypeNames list of name of a data types
     * @return list of <code>SimpleDataType</code> (can be empty)
     */
    public List<SimpleDataType> getSimpleDataTypeByNames(List<String> dataTypeNames)
    {
        PreparedStatement stmt = null;
        List<SimpleDataType> data = new ArrayList<SimpleDataType>();
        String sql = "SELECT datatype_id, name, definition FROM mir_datatype WHERE (name=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
        }
        catch (SQLException e)
        {
            logger.error("Unable to retrieve a PreparedStatement from the connection to the database!");
            logger.error("SQLException raised: " + e.getMessage());
        }
        
        if (null != stmt)
        {
            for (String name: dataTypeNames)
            {
                try
                {
                    stmt.setString(1, name);
                    logger.debug("SQL prepared query: " + stmt.toString());
                    ResultSet rs = stmt.executeQuery();
                    // no check if several results...
                    if (rs.first())
                    {
                        SimpleDataType tmp = new SimpleDataType();
                        tmp.setId(rs.getString("datatype_id"));
                        tmp.setName(rs.getString("name"));
                        tmp.setDefinition(rs.getString("definition"));
                        
                        data.add(tmp);
                    }
                }
                catch (SQLException e)
                {
                    logger.warn("An exception occurred during the retrieval of the data type: " + name);
                    logger.warn("SQLException raised: " + e.getMessage());
                }
            }
            closeStatement(stmt);
        }
        else
        {
            logger.warn("Unable to remove the old data type/tag connections: connection to database failed!");
        }
        
        return data;
    }
    
    
    /**
     * Updates the last modification date of a data type, based on its identifier.
     * @param dataTypeId identifier of a data type (for example: 'MIR:00000008')
     * @return True if the update is a success, False otherwise
     */
    public boolean updateLastModifDate(String dataTypeId)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        
        // the data type exists
        if (dataTypeExists(dataTypeId))
        {
            String sql = "UPDATE mir_datatype SET date_modif=NOW() WHERE (datatype_id=?)";
            try
            {
                stmt = openPreparedStatement(sql);
                stmt.setString(1, dataTypeId);
                int state = stmt.executeUpdate();
                if (state == 1)
                {
                    result = true;
                }
            }
            catch (SQLException e)
            {
                logger.warn("An exception occurred during the update of the last modification date of the data type: " + dataTypeId);
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
     * Flags the data collection as restricted.
     * @param dataTypeId data collection identifier 
     * @return whether the update is a success or not
     */
    public Boolean setRestricted(String dataTypeId)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        
        // the data type exists
        if (dataTypeExists(dataTypeId))
        {
            String sql = "UPDATE mir_datatype SET restriction=1 WHERE (datatype_id=?)";
            try
            {
                stmt = openPreparedStatement(sql);
                stmt.setString(1, dataTypeId);
                int state = stmt.executeUpdate();
                if (state == 1)
                {
                    result = true;
                }
            }
            catch (SQLException e)
            {
                logger.warn("An exception occurred during the update of the restriction status of the data collection: " + dataTypeId);
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
     * Retrieves the number of (not obsolete) data types currently stored in the database.
     * @return
     */
    public int getNbDataTypes()
    {
        Statement stmt = null;
        int nb = 0;
        String sql = "SELECT COUNT(name) AS number FROM mir_datatype WHERE (obsolete=0)";
        
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
            logger.error("Error while retrieving the number of (not obsolete) data types stored!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return nb;
    }
    
    
    /**
     * Retrieves the number of (obsolete) data types currently stored in the database.
     * @return
     */
    public int getNbObsoleteDataTypes()
    {
        Statement stmt = null;
        int nb = 0;
        String sql = "SELECT COUNT(name) AS number FROM mir_datatype WHERE (obsolete=1)";
        
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
            logger.error("Error while retrieving the number of (obsolete) data types stored!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return nb;
    }
    
    
    /**
     * Retrieves the number of all data types (obsolete and not) currently stored in the database.
     * @return
     */
    public int getNbAllDataTypes()
    {
        Statement stmt = null;
        int nb = 0;
        String sql = "SELECT COUNT(name) AS number FROM mir_datatype";
        
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
            logger.error("Error while retrieving the number of all data types stored!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return nb;
    }
    
    
    /**
     * Retrieves the obsolete information about a given data type (based on its identifier).
     * The result can be null if the data type is not obsolete
     * @param identifier of a data type (for example: "MIR:00000001")
     * @return a <code>HashMap</code> which contains the following keys (and associated values): replacementId, replacementName and replacementComment.
     */
    public HashMap<String, String> getObsoleteInfo(String identifier)
    {
        PreparedStatement stmt = null;
        HashMap<String, String> result = null;
        String sql = "SELECT obsolete, obsolete_comment, replacement FROM mir_datatype WHERE (datatype_id=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, identifier);
            ResultSet rs = stmt.executeQuery();
            if (rs.first())
            {
                if (rs.getInt("obsolete") == 1)
                {
                    result = new HashMap<String, String>();
                    result.put("replacementComment", rs.getString("obsolete_comment"));
                    result.put("replacementId", rs.getString("replacement"));
                    result.put("replacementName", getDataTypeName(rs.getString("replacement")));
                }
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of obsolete information about: " + identifier);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the newest date of last modification of the database.
     * @return <code>Date</code> of last modification of the database
     */
    public Date getLastModifDate()
    {
        Statement stmt = null;
        Date lastModif = null;
        String sql = "SELECT date_modif FROM mir_datatype WHERE 1 ORDER BY date_modif DESC LIMIT 1";
        
        try
        {
            stmt = openStatement();
            ResultSet sqlResult = stmt.executeQuery(sql);
            sqlResult.next();
            lastModif = sqlResult.getTimestamp("date_modif");
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the newest date of last modification!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return lastModif;
    }
    
    
    /**
     * Retrieves all the data collections (limited information only) stored in the database.
     * @return list of <code>SimpleDataType</code>
     */
    public List<SimpleDataType> getSimpleDataTypes()
    {
        Statement stmt = null;
        List<SimpleDataType> result = new ArrayList<SimpleDataType>(200);   // ArrayList increases its array size by 50 percent when full
        String sql = "SELECT datatype_id, name, definition FROM mir_datatype ORDER BY name";
        
        try
        {
            stmt = openStatement();
            ResultSet sqlResult = stmt.executeQuery(sql);
            boolean notEmpty = sqlResult.first();
            while (notEmpty)
            {
                SimpleDataType temp = new SimpleDataType();
                temp.setId(sqlResult.getString("datatype_id"));
                temp.setName(sqlResult.getString("name"));
                temp.setDefinition(sqlResult.getString("definition"));
                
                result.add(temp);
                notEmpty = sqlResult.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of all data collections published!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
    
    /**
     * Retrieves all the data collections (limited information only) not deprecated stored in the database.
     * @return list of <code>SimpleDataType</code>
     */
    public List<SimpleDataType> getSimpleDataTypesNotDeprecated()
    {
        Statement stmt = null;
        List<SimpleDataType> result = new ArrayList<SimpleDataType>(200);   // ArrayList increases its array size by 50 percent when full
        String sql = "SELECT datatype_id, name, definition FROM mir_datatype WHERE (obsolete = 0) ORDER BY name";
        
        try
        {
            stmt = openStatement();
            ResultSet sqlResult = stmt.executeQuery(sql);
            boolean notEmpty = sqlResult.first();
            while (notEmpty)
            {
                SimpleDataType temp = new SimpleDataType();
                temp.setId(sqlResult.getString("datatype_id"));
                temp.setName(sqlResult.getString("name"));
                temp.setDefinition(sqlResult.getString("definition"));
                
                result.add(temp);
                notEmpty = sqlResult.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of (non deprecated) data collections published!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
    
    /**
     * Retrieves all the deprecated data collections (limited information only) stored in the database.
     * @return list of <code>SimpleDataType</code>
     */
    public List<SimpleDataType> getSimpleDataTypesDeprecated()
    {
        Statement stmt = null;
        List<SimpleDataType> result = new ArrayList<SimpleDataType>(200);   // ArrayList increases its array size by 50 percent when full
        String sql = "SELECT datatype_id, name, definition FROM mir_datatype WHERE (obsolete = 1) ORDER BY name";
        
        try
        {
            stmt = openStatement();
            ResultSet sqlResult = stmt.executeQuery(sql);
            boolean notEmpty = sqlResult.first();
            while (notEmpty)
            {
                SimpleDataType temp = new SimpleDataType();
                temp.setId(sqlResult.getString("datatype_id"));
                temp.setName(sqlResult.getString("name"));
                temp.setDefinition(sqlResult.getString("definition"));
                
                result.add(temp);
                notEmpty = sqlResult.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of deprecated data collections published!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the identifiers of all published data collections.
     * @return
     */
    public List<String> getDataCollectionIds()
    {
    	List<String> ids = new ArrayList<String>();
    	Statement stmt = null;
    	String sql = "SELECT datatype_id FROM mir_datatype ORDER BY datatype_id";
        
        try
        {
            stmt = openStatement();
            ResultSet sqlResult = stmt.executeQuery(sql);
            boolean notEmpty = sqlResult.first();
            while (notEmpty)
            {
                ids.add(sqlResult.getString("datatype_id"));
                notEmpty = sqlResult.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the identifiers of all data collections published!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
    	
    	return ids;
    }
    
    
    /**
     * Retrieves a list of data type from the database, based on their (primary, not synonym) name.
     * Warning: doesn't retrieve the URI of the data types!
     * @param dataTypeNames list of name of a data types
     * @return list of <code>SimpleDataType</code> (can be empty)
     */
    public List<DataCollection> getDataCollections(String dbPool)
    {
    	List<DataCollection> collections = new ArrayList<DataCollection>(200);   // ArrayList increases its array size by 50 percent when full
    	List<String> ids = getDataCollectionIds();
    	
    	for (String id: ids)
    	{
    		DataTypeHibernate collection = new DataTypeHibernate();
    		collection.retrieveData(dbPool, id);
    		
    		collections.add(collection);
    	}
    	
    	return collections;
    }
    
    
    /**
     * Retrieves all the data collections which name start by the character(s) provided in parameter.
     * @param startBy one or more characters
     * @param all should all data types be returned (including the obsolete ones)?
     * @param should obsolete data types be returned (either with others or on their own)?
     * @return
     */
    public List<SimpleDataType> getDataTypesNameStartingBy(String startBy, Boolean all, Boolean obsolete)
    {
        PreparedStatement stmt = null;
        String sql = null;
        List<SimpleDataType> datatypes = new ArrayList<SimpleDataType>();
        
        // to be able to execute a prepared statement with a LIKE clause
        startBy = startBy + "%";
        
        // only obsolete data types to be returned
        if ((obsolete) && (!all))
        {
            sql = "SELECT d.name, d.definition, d.datatype_id, u.uri, d.restriction FROM mir_datatype d, mir_uri u WHERE ((d.obsolete=1) AND (d.name LIKE ?) AND (u.ptr_datatype=d.datatype_id) AND (u.uri_type='URN') AND (u.deprecated=0)) ORDER BY d.name";
        }
        else if ((obsolete) && (all))   // all data types (including obsolete ones) to be returned
        {
            sql = "SELECT d.name, d.definition, d.datatype_id, u.uri, d.restriction FROM mir_datatype d, mir_uri u WHERE ((d.name LIKE ?) AND (u.ptr_datatype=d.datatype_id) AND (u.uri_type='URN') AND (u.deprecated=0)) ORDER BY d.name";
        }
        else   // default: only non obsolete data types to be returned 
        {
            sql = "SELECT d.name, d.definition, d.datatype_id, u.uri, d.restriction FROM mir_datatype d, mir_uri u WHERE ((d.obsolete=0) AND (d.name LIKE ?) AND (u.ptr_datatype=d.datatype_id) AND (u.uri_type='URN') AND (u.deprecated=0)) ORDER BY d.name";
        }
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, startBy);
            ResultSet rs = stmt.executeQuery();
            boolean notEmpty = rs.first();
            while (notEmpty)
            {
                SimpleDataType temp = new SimpleDataType();
                temp.setId(rs.getString("datatype_id"));
                temp.setName(rs.getString("name"));
                temp.setDefinition(rs.getString("definition"));
                temp.setUri(rs.getString("uri"));
                temp.setRestricted(rs.getInt("restriction"));
                datatypes.add(temp);
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of data collections which name start by: '" + startBy + " (all=" + all + ", obsolete=" + obsolete + ")");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return datatypes;
    }
    
    
    /**
     * Retrieves the data collections which have been recently updated (either created or modified).
     * @param number number of data types to return
     * @return
     */
    public List<SimpleDataType> getDataTypesRecentlyUpdated(Integer number)
    {
        Statement stmt = null;
        String sql = null;
        List<SimpleDataType> datatypes = new ArrayList<SimpleDataType>();
        sql = "SELECT d.name, d.definition, d.datatype_id, u.uri, d.restriction FROM mir_datatype d, mir_uri u WHERE ((d.obsolete=0) AND (u.ptr_datatype=d.datatype_id) AND (u.uri_type='URN') AND (u.deprecated=0)) ORDER BY date_modif DESC LIMIT " + number;
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            boolean notEmpty = rs.first();
            while (notEmpty)
            {
                SimpleDataType temp = new SimpleDataType();
                temp.setId(rs.getString("datatype_id"));
                temp.setName(rs.getString("name"));
                temp.setDefinition(rs.getString("definition"));
                temp.setUri(rs.getString("uri"));
                temp.setRestricted(rs.getInt("restriction"));
                datatypes.add(temp);
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the " + number + " recently updated data collections.");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return datatypes;
    }
    
    
    /**
     * Retrieves the data collections which have some restrictions and are not obsolete.
     * @return
     */
    public List<SimpleDataType> getRestrictedDataTypes()
    {
        Statement stmt = null;
        String sql = null;
        List<SimpleDataType> datatypes = new ArrayList<SimpleDataType>();
        sql = "SELECT d.name, d.definition, d.datatype_id, u.uri, d.restriction FROM mir_datatype d, mir_uri u WHERE ((restriction != 0) AND (u.ptr_datatype=d.datatype_id) AND (u.uri_type='URN') AND (u.deprecated=0) AND (d.obsolete = 0)) ORDER BY name";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            boolean notEmpty = rs.first();
            while (notEmpty)
            {
                SimpleDataType temp = new SimpleDataType();
                temp.setId(rs.getString("datatype_id"));
                temp.setName(rs.getString("name"));
                temp.setDefinition(rs.getString("definition"));
                temp.setUri(rs.getString("uri"));
                temp.setRestricted(rs.getInt("restriction"));
                datatypes.add(temp);
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of all data collections non obsolete with restrictions.");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return datatypes;
    }
    
    
    /**
     * Retrieves the deprecated data collections.
     * @return
     */
    public List<SimpleDataType> getObsoleteDataTypes()
    {
        Statement stmt = null;
        String sql = null;
        List<SimpleDataType> datatypes = new ArrayList<SimpleDataType>();
        sql = "SELECT d.name, d.definition, d.datatype_id, u.uri, d.restriction FROM mir_datatype d, mir_uri u WHERE ((d.obsolete != 0) AND (u.ptr_datatype=d.datatype_id) AND (u.uri_type='URN') AND (u.deprecated=0)) ORDER BY name";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            boolean notEmpty = rs.first();
            while (notEmpty)
            {
                SimpleDataType temp = new SimpleDataType();
                temp.setId(rs.getString("datatype_id"));
                temp.setName(rs.getString("name"));
                temp.setDefinition(rs.getString("definition"));
                temp.setUri(rs.getString("uri"));
                temp.setRestricted(rs.getInt("restriction"));
                datatypes.add(temp);
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of all obsolete data collections.");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return datatypes;
    }
    
    
    /**
     * Very simple and quick check for the load balancer.
     * Let's be very efficient, as this is performed every 3s...
     * This should return "MIRIAM Registry collection", the name of the data collection identified by "MIR:00000008".
     * @return
     */
    public String checkForLoadBalancer()
    {
        Statement stmt = null;
        String result = null;
        String sql = "SELECT name FROM mir_datatype WHERE datatype_id LIKE 'MIR:00000008'";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.first())
            {
                result = rs.getString("name");
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the number of (not obsolete) data types stored!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Makes a data collection deprecated.
     * Warning: this does not check whether the data collection actually exists!
     * @param collectionId data collection identifier
     * @param comment deprecation comment (public)
     * @param replacementId replacement data collection identifier
     * @return whether the deprecation was successful or not
     */
	public Boolean deprecateDataCollection(String collectionId, String comment, String replacementId)
	{
		PreparedStatement stmt = null;
        boolean success = false;
        
        String sql = "UPDATE mir_datatype SET obsolete=1, obsolete_comment=?, replacement=? WHERE (datatype_id=?)";
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, comment);
            stmt.setString(2, replacementId);
            stmt.setString(3, collectionId);
            int state = stmt.executeUpdate();
            if (state == 1)
            {
                success = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the deprecation the data collection: " + collectionId);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return success;
	}
}
