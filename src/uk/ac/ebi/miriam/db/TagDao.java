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


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;


/**
 * <p>Handles all the database connections for the retrieval, upgrade and addition of tags.
 * 
 * <p>TODO: the creation of a new tag is not thread safe!
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
 * @version 20140324
 */
public class TagDao extends Dao
{
    private Logger logger = Logger.getLogger(TagDao.class);
    
    
    /**
     * Constructor.
     * @param pool
     */
    public TagDao(String pool)
    {
        super(pool);
    }
    
    
    /*
     * Stores a new <code>Tag</code> in the database.
     * 
     * @param tag
     * @return integer value explaining if the operation is a success or not
     *
    public int store(Tag tag)
    {
        
        // TO FINISH
        
        
        return 0;
    }
    */
    
    
    /**
     * Retrieves the list of all used tags.
     * 
     * @return list of <code>Tag</code> (this list can be empty, but should not...)
     */
    public List<Tag> retrieveTags()
    {
        Statement stmt = null;
        List<Tag> result = new ArrayList<Tag>();
        String sql = "SELECT t.id, t.tag, t.info, COUNT(l.id) FROM mir_tag t, mir_tag_link l WHERE (l.ptr_tag = t.id) GROUP BY tag";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                String identifier = rs.getString("t.id");
                String name = rs.getString("t.tag");
                String info = rs.getString("t.info");
                int counter = rs.getInt("COUNT(l.id)");
                Tag temp = new Tag(identifier, name, info, counter);
                result.add(temp);
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of all used tags!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the list of all available tags (even if there are not used -linked to any data type-).
     * 
     * @return list of <code>Tag</code> (this list can be empty, but should not...)
     */
    public List<Tag> retrieveAllTags()
    {
        Statement stmt = null;
        List<Tag> result = new ArrayList<Tag>();
        String sql = "SELECT id, tag, info FROM mir_tag GROUP BY tag";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                String identifier = rs.getString("id");
                String name = rs.getString("tag");
                String info = rs.getString("info");
                Tag temp = new Tag(identifier, name, info);
                result.add(temp);
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of all tags!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the list of tags associated with a data type, based on its identifier.
     * 
     * @param id identifier of a data type (for example 'MIR:00000008')
     * @return list of <code>Tag</code> (this list can be empty)
     */
    public List<Tag> retrieveTags(String id)
    {
        Statement stmt = null;
        List<Tag> result = new ArrayList<Tag>();
        String sql = "SELECT t.id, t.tag, t.info FROM mir_tag t, mir_tag_link l WHERE ((l.ptr_datatype='" + id + "') AND (l.ptr_tag=t.id))";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                String identifier = rs.getString("id");
                String name = rs.getString("tag");
                String info = rs.getString("info");
                Tag temp = new Tag(identifier, name, info);
                result.add(temp);
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while searching the tags associated with the data type: '" + id + "'!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
    
    
    /*
     * Checks if a <code>Tag</code> exists in the database.
     * 
     * @param tag tag to check
     * @return True if the <code>Tag</code> exists, False otherwise
    public boolean exists(Tag tag)
    {
        
        // TO FINISH
        
        return false;
    }
    */
    
    
    /**
     * Checks if a <code>Tag</code> exists in the database, based on its identifier.
     * <p>Uses a <code>PreparedStatement</code> to shield us against SQL injection attacks.
     * 
     * @param id identifier of the tag to check (example: 'MIR:00600001')
     * @return True if a <code>Tag</code> with the given identifier exists, False otherwise
     */
    public boolean exists(String id)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        String sql = "SELECT tag FROM mir_tag WHERE (id=?)";
        
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
            logger.warn("An exception occurred during the existence test of the following tag: " + id);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the content of a tag based on its identifier.
     * 
     * @param id identifier of a tag (example: 'MIR:00600001')
     * @return <code>Tag</code> object (can be null)
     */
    public Tag getTagFromId(String id)
    {
        Statement stmt = null;
        Tag tag = null;
        
        // a tag with this id exists in the database
        if (exists(id))
        {
            String sql = "SELECT id, tag, info FROM mir_tag WHERE (id='" + id + "')";
            try
            {
                stmt = openStatement();
                ResultSet rs = stmt.executeQuery(sql);
                
                if (rs.first())
                {
                    String identifier = rs.getString("id");
                    String name = rs.getString("tag");
                    String info = rs.getString("info");
                    tag = new Tag(identifier, name, info);
                }
                else
                {
                    logger.error("An error occurred during the retrieval of the tag: " + id + "!");
                }
            }
            catch (SQLException e)
            {
                logger.error("Error while retrieving the list of all tags!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            finally
            {
                closeStatement(stmt);
            }
        }
        
        return tag;
    }
    
    
    /**
     * Retrieves the list of <DataType</code> (or <code>SimpleDataType</code> to be accurate) associated with a specific tag.
     * Warning: doesn't retrieve the URI of the data types!
     * @param id identifier of a tag (example : 'MIR:00600001') 
     * @return list of <code>SimpleDataType<code> (can be empty)
     */
    public List<SimpleDataType> getDataTypesFromTagId(String id)
    {
        Statement stmt = null;
        List<SimpleDataType> result = new ArrayList<SimpleDataType>();
        
        if (exists(id))
        {
            String sql = "SELECT d.datatype_id, d.name, d.definition FROM mir_datatype d, mir_tag_link l WHERE ((l.ptr_tag='" + id + "') AND (d.datatype_id=l.ptr_datatype)) ORDER BY d.name";
            try
            {
                stmt = openStatement();
                ResultSet rs = stmt.executeQuery(sql);
                
                boolean notEmpty = rs.next();
                while (notEmpty)
                {
                    String identifier = rs.getString("datatype_id");
                    String name = rs.getString("name");
                    String def = rs.getString("definition");
                    SimpleDataType temp = new SimpleDataType();
                    temp.setId(identifier);
                    temp.setName(name);
                    temp.setDefinition(def);
                    
                    result.add(temp);
                    // next resource (if it exists)
                    notEmpty = rs.next();
                }
            }
            catch (SQLException e)
            {
                logger.error("Error while searching the data types associated with the tag: '" + id + "'!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            finally
            {
                closeStatement(stmt);
            }
        }
        
        return result;
    }
    
    
    /**
     * Retrieves all the data types associated with *any* of the tags given in parameter.
     * @param tags list of identifiers of tags
     * @return list of data types
     */
    public List<SimpleDataType> getDataTypesFromAnyTagIds(String[] tags)
    {
        List<SimpleDataType> result = new ArrayList<SimpleDataType>();
        Set<SimpleDataType> dataTypes = new TreeSet<SimpleDataType>();
        
        // for each tag, we search the list of associated data types
        for (String id: tags)
        {
            List<SimpleDataType> temp = getDataTypesFromTagId(id);
            // the result of the search is put in a Tree, in order to prevent duplicate (cf. "any")
            for (SimpleDataType simple: temp)
            {
                dataTypes.add(simple);
            }
        }
        
        // the content of the Tree is put into a List (more convenient type)
        for (SimpleDataType data: dataTypes)
        {
            result.add(data);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves all the data types associated with *all* the tags given in parameter.
     * <p>This could be improved -performance regarding- with a specific SQL query, or not...
     * <p>It is implied that the number of data types is higher than the number of tags
     * 
     * @param tags list of identifiers of tags
     * @return list of data types
     */
    public List<SimpleDataType> getDataTypesFromAllTagIds(String[] tags)
    {
        List<SimpleDataType> result = new ArrayList<SimpleDataType>();
        List<List<SimpleDataType>> dump = new ArrayList<List<SimpleDataType>>();
        
        // checks if the number of selected tags is not too high
        int max = getMaxTagLinks();
        
        // the number of tags searched is lower or equal to the number max of tags associated to a data type
        // this search can return a result, otherwise: don't bother it's useless!
        if (tags.length <= max)
        {
            // for each tag, we search the list of associated data types
            for (String id: tags)
            {
                List<SimpleDataType> temp = getDataTypesFromTagId(id);
                dump.add(temp);
            }
            
            // initialise the resulting list with the content of the first result of the previous query
            result.addAll(0, dump.get(0));
            
            // get the intersection between the whold dump and the data types linked with each tag selected
            for (List<SimpleDataType> current: dump)
            {
                result.retainAll(current);
            }
        }
        
        return result;
    }
    
    
    /**
     * Removes an association between a data type and a tag.
     * @param tag internal identifier of a tag (for example: 'MIR:00600002')
     * @param dataType internal identifier of a data type (for example: 'MIR:00000005')
     */
    public boolean removeTag(String tag, String dataType)
    {
        Statement stmt = null;
        boolean exists = false;
        boolean success = false;
        
        // test if the association to remove actually exists
        String sql = "SELECT id FROM mir_tag_link WHERE ((ptr_tag='" + tag + "') AND (ptr_datatype='" + dataType + "'))";
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.first())
            {
                exists = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while searching for a data type/tag association: " + dataType + "/" + tag + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        // the association exists, therefore we can remove it
        if (exists)
        {
            String sql2 = "DELETE FROM mir_tag_link WHERE ((ptr_datatype='" + dataType + "') AND (ptr_tag='" + tag + "'))";
            try
            {
                stmt = openStatement();
                int state = stmt.executeUpdate(sql2);
                logger.info("Result of the deletion of the association " + dataType + "/" + tag + ": " + state);
                
                // success
                if (state == 1)
                {
                    success = true;
                }
            }
            catch (SQLException e)
            {
                logger.error("Error while removing the data type/tag association: '" + dataType + "/" + tag + "'!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            finally
            {
                closeStatement(stmt);
            }
        }
        
        return success;
    }
    
    
    /**
     * Retrieves the name of a tag from its identifier.
     * 
     * @param tagId internal identifier of a tag (for example: 'MIR:00600002')
     * @return name of the tag
     */
    public String getTagName(String tagId)
    {
        Statement stmt = null;
        String tagName = null;
        
        // a tag with this id exists in the database
        if (exists(tagId))
        {
            String sql = "SELECT tag FROM mir_tag WHERE (id='" + tagId + "')";
            try
            {
                stmt = openStatement();
                ResultSet rs = stmt.executeQuery(sql);
                
                if (rs.first())
                {
                    tagName = rs.getString("tag");
                }
                else
                {
                    logger.error("An error occurred during the retrieval of the name of the tag: " + tagId + "!");
                }
            }
            catch (SQLException e)
            {
                logger.error("Error while retrieving the list of all tags!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            finally
            {
                closeStatement(stmt);
            }
        }
        
        return tagName;
    }
    
    
    /**
     * Retrieves the number max of tags associated to a single data type.
     *  
     * @return integer
     */
    public int getMaxTagLinks()
    {
        Statement stmt = null;
        int result = 0;
        String sql = "SELECT MAX(total) AS result FROM (SELECT COUNT(id) AS total FROM mir_tag_link GROUP BY ptr_datatype) AS temp";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.first())
            {
                result = rs.getInt("result");
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the number max of tags associated to a single data type!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Creates a new association between a data type and a tag.
     * @param tagId identifier of a tag (for example: 'MIR:00600002')
     * @param dataTypeId identifier of a data type (for example: 'MIR:00000008')
     * @return result of the creation: True if it is a success, False otherwise.
     */
    public boolean addAssociation(String tagId, String dataTypeId)
    {
        PreparedStatement stmt1 = null;
        PreparedStatement stmt2 = null;
        boolean state = false;
        boolean existing = false;
        
        // checks if the association doesn't exist already
        String sql1 = "SELECT id FROM mir_tag_link WHERE ((ptr_tag=?) AND (ptr_datatype=?))";
        try
        {
            stmt1 = openPreparedStatement(sql1);
            stmt1.setString(1, tagId);
            stmt1.setString(2, dataTypeId);
            logger.debug("SQL prepared query: " + stmt1.toString());
            ResultSet rs = stmt1.executeQuery();
            
            if (rs.first())
            {
                existing = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the existence test of an association between a tag (" + tagId + ") and a data type (" + dataTypeId + ")!");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt1);
        }
        
        // creates the new association
        if (! existing)
        {
            String sql2 = "INSERT INTO mir_tag_link (ptr_tag, ptr_datatype) VALUES (?, ?)";
            try
            {
                stmt2 = openPreparedStatement(sql2);
                stmt2.setString(1, tagId);
                stmt2.setString(2, dataTypeId);
                logger.debug("SQL prepared query: " + stmt2.toString());
                stmt2.executeUpdate();
                state = true;
            }
            catch (SQLException e)
            {
                logger.warn("An exception occurred during the creation an association between a tag (" + tagId + ") and a data type (" + dataTypeId + ")!");
                logger.warn("SQLException raised: " + e.getMessage());
            }
            finally
            {
                closePreparedStatement(stmt2);
            }
        }
        
        return state;
    }
    
    
    /**
     * Checks if a tag with a given name exists.
     * @param tag name of a tag
     * @return
     */
    public boolean tagNameExists(String tag)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        String sql = "SELECT id FROM mir_tag WHERE (tag=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, tag);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the existence test of the following tag: " + tag);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves a tag, based on its name.
     * @param name name of a tag.
     */
    public Tag getTagFromName(String name)
    {
        PreparedStatement stmt = null;
        Tag tag = null;
        String sql = "SELECT id, tag, info FROM mir_tag WHERE (tag=?)";
        
        if (null != name)
        {
            name = name.trim();
            
            try
            {
                stmt = openPreparedStatement(sql);
                stmt.setString(1, name);
                logger.debug("SQL prepared query: " + stmt.toString());
                ResultSet rs = stmt.executeQuery();
                
                if (rs.first())
                {
                    String identifier = rs.getString("id");
                    String tagName = rs.getString("tag");
                    String info = rs.getString("info");
                    tag = new Tag(identifier, tagName, info);
                }
            }
            catch (SQLException e)
            {
                logger.warn("An exception occurred during the retrieval of the following tag: " + name);
                logger.warn("SQLException raised: " + e.getMessage());
            }
            finally
            {
                closePreparedStatement(stmt);
            }
        }
        
        return tag;
    }
    
    
    /**
     * Creates a new tag.
     * @param name name of the tag
     * @param definition definition of the tag
     * @return
     */
    public boolean CreateNew(String name, String definition)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        String sql = "INSERT INTO mir_tag (id, tag, info) VALUES (?, ?, ?)";
        
        if ((null != name) && (null != definition))
        {
            name = name.trim();
            definition = definition.trim();
            
            try
            {
                stmt = openPreparedStatement(sql);
                stmt.setString(1, generateId());
                stmt.setString(2, name);
                stmt.setString(3, definition);
                logger.debug("SQL prepared query: " + stmt.toString());
                stmt.executeUpdate();
                result = true;
            }
            catch (SQLException e)
            {
                logger.warn("An exception occurred during the creation a new tag (" + name + ")!");
                logger.warn("SQLException raised: " + e.getMessage());
            }
            finally
            {
                closePreparedStatement(stmt);
            }
        }
        
        return result;
    }
    
    
    /*
     * Generates a new identifier for a tag.
     * Warning: the use of this method followed by a creation of a new entity needs to be protected by a 
     *          semaphore/lock to prevent any update of the database in the meantime.
     * @return identifier newly generated (can be null if all the possible identifiers have been attributed)
     */
    private String generateId()
    {
        Statement stmt = null;
        String newId = "MIR:006";
        String sql = "SELECT MAX(id) AS maxId FROM mir_tag";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.first())
            {
                String lastId = rs.getString("maxId");
                String subLastId = lastId.substring(7);
                int intId =  Integer.parseInt(subLastId);   // last integer part of the identifier ('00007' for the identifier 'MIR:00600007')
                intId += 1;
                
                if (intId < 10)
                {
                    newId += "0000" + intId;
                }
                else
                {
                    if (intId < 100)
                    {
                        newId += "000" + intId;
                    }
                    else
                    {
                        if (intId < 1000)
                        {
                            newId += "00" + intId;
                        }
                        else
                        {
                            if (intId < 10000)
                            {
                                newId += "0" + intId;
                            }
                            else
                            {
                                if (intId < 100000)
                                {
                                    newId += intId;
                                }
                                else
                                {
                                    logger.error("Cannot create a new identifier for a tag: size overflow!");
                                    newId += "00001";   // identifier already existing: should generate a SQL error if tried to use like that...
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                logger.error("An error occurred during the retrieval of the highest identifier of a tag!");
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of all tags!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return newId;
    }
    
    
    /**
     * Returns the number of available tags (even if there are not used -linked to any data type-)
     * @return number of tags stored
     */
    public int getNbStoredTags()
    {
        Statement stmt = null;
        int nb = 0;
        String sql = "SELECT COUNT(id) AS number FROM mir_tag";
        
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
            logger.error("Error while retrieving the number of tags stored!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        
        return nb;
    }
    
    
    /**
     * Updates the details (name and definition) of a tag in the database.
     * @param tag to be updated
     * @return True if the update is a success, False otherwise
     */
    public boolean update(Tag tag)
    {
        PreparedStatement stmt = null;
        int state = 0;   // default value: failure
        String sql = null;
        sql = "UPDATE mir_tag SET tag=?, info=? WHERE (id=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, tag.getName());
            stmt.setString(2, tag.getInfo());
            stmt.setString(3, tag.getId());
            logger.debug("SQL prepared query: " + stmt.toString());
            state = stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the update of a tag: " + tag.getName() + " (" + tag.getId() + ")!");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        /* the following should not be necessary
        
        // retrieves the list of data types linked to the tag
        List<SimpleDataType> linked = getDataTypesFromTagId(tag.getId());
        
        // updates the date of last modification of all the data types linked with the tag
        DataTypeDao dataDao = new DataTypeDao(this.getPoolName());
        for (SimpleDataType sdt: linked)
        {
            dataDao.updateLastModifDate(sdt.getId());
        }
        
        // a bit of cleaning
        dataDao.clean();
        */
        
        return (state == 1);
    }
    
    
    /**
     * Updates the data types linked to a tag.
     * @param tagId identifier of a tag (example: 'MIR:00600003')
     * @param newDataTypeNames list of name of tags that need to be associated with the current tag (this set can overlap with the current set). 
     * @return True if the update is a success, False otherwise
     */
    public boolean updateLinks(String tagId, List<String> newDataTypeNames)
    {
        int state = 1;   // default value: success
        
        // retrieves the list of current data types associated with the tag
        List<SimpleDataType> currentData = getDataTypesFromTagId(tagId);
        
        /*
        logger.debug("CURRENT LIST: ");
        for (SimpleDataType sdt: currentData)
        {
            logger.debug(sdt.getName());
        }
        */
        
        // converts the list of data types names into a list of 'SimpleDataType'
        DataTypeDao dataDao = new DataTypeDao(this.getPoolName());
        List<SimpleDataType> newData = dataDao.getSimpleDataTypeByNames(newDataTypeNames);
        
        /*
        logger.debug("NEW LIST: ");
        for (SimpleDataType sdt: newData)
        {
            logger.debug(sdt.getName());
        }
        */
        
        // retrieves the list of connections which need to be kept
        List<SimpleDataType> toKeep = new ArrayList<SimpleDataType>();
        toKeep.addAll(currentData);
        toKeep.retainAll(newData);
        
        /*
        logger.debug("TO KEEP LIST: ");
        for (SimpleDataType sdt: toKeep)
        {
            logger.debug(sdt.getName());
        }
        */
        
        // retrieves the list of connections which need to be removed
        List<SimpleDataType> toRemove = new ArrayList<SimpleDataType>();
        toRemove.addAll(currentData);
        toRemove.removeAll(toKeep);
        
        /*
        logger.debug("TO REMOVE LIST: ");
        for (SimpleDataType sdt: toRemove)
        {
            logger.debug(sdt.getName());
        }
        */
        
        // retrieves the list of connections which need to be added
        List<SimpleDataType> toAdd = new ArrayList<SimpleDataType>();
        toAdd.addAll(newData);
        toAdd.removeAll(toKeep);
        
        /*
        logger.debug("TO ADD LIST: ");
        for (SimpleDataType sdt: toAdd)
        {
            logger.debug(sdt.getName());
        }
        */
        
        // removes the old connections
        String sql1 = "DELETE FROM mir_tag_link WHERE ((ptr_tag=?) AND (ptr_datatype=?))";
        PreparedStatement stmt1 = null;
        try
        {
            stmt1 = openPreparedStatement(sql1);
        }
        catch (SQLException e)
        {
            logger.error("Unable to retrieve a PreparedStatement from the connection to the database!");
            logger.error("SQLException raised: " + e.getMessage());
        }
        if (null != stmt1)
        {
            for (SimpleDataType sdt: toRemove)
            {
                try
                {
                    stmt1.setString(1, tagId);
                    stmt1.setString(2, sdt.getId());
                    logger.debug("SQL prepared query: " + stmt1.toString());
                    stmt1.executeUpdate();
                    // DELETE returns the number of entries deleted, so it is not wise to use it to check if the update process is a success or not
                }
                catch (SQLException e)
                {
                    logger.warn("An exception occurred during the deletion of the association between a tag (" + tagId + ") and a data type (" + sdt.getId() + ")!");
                    logger.warn("SQLException raised: " + e.getMessage());
                }
            }
            closeStatement(stmt1);
        }
        else
        {
            logger.warn("Unable to remove the old data type/tag connections: connection to database failed!");
        }
        
        // adds the new connections
        String sql2 = "INSERT INTO mir_tag_link (ptr_tag, ptr_datatype) VALUES (?, ?)";
        PreparedStatement stmt2 = null;
        try
        {
            stmt2 = openPreparedStatement(sql2);
        }
        catch (SQLException e)
        {
            logger.error("Unable to retrieve a PreparedStatement from the connection to the database!");
            logger.error("SQLException raised: " + e.getMessage());
        }
        int state2 = 1;   // default value: success
        if (null != stmt2)
        {
            for (SimpleDataType sdt: toAdd)
            {
                try
                {
                    stmt2.setString(1, tagId);
                    stmt2.setString(2, sdt.getId());
                    logger.debug("SQL prepared query: " + stmt2.toString());
                    state2 = stmt2.executeUpdate();
                }
                catch (SQLException e)
                {
                    logger.warn("An exception occurred during the creation of an association between a tag (" + tagId + ") and a data type (" + sdt.getId() + ")!");
                    logger.warn("SQLException raised: " + e.getMessage());
                    state2 = 0;   // failure
                }
                finally
                {
                    state= state * state2;   // if the result of the query is "1" (means success), the state will stay at the value "1" (means everything is OK)
                }
            }
            closeStatement(stmt2);
        }
        else
        {
            logger.warn("Unable to remove the old data type/tag connections: connection to database failed!");
        }
        
        // update the last modification date for all the necessary data types
        for (SimpleDataType sdt: toRemove)
        {
            dataDao.updateLastModifDate(sdt.getId());
        }
        for (SimpleDataType sdt: toAdd)
        {
            dataDao.updateLastModifDate(sdt.getId());
        }
        
        // a bit of cleaning
        dataDao.clean();
        
        return (state == 1);
    }
}
