/*
 * MIRIAM Registry (Web Application)
 * The Registry is an online resource created to catalogue biological data collections,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2011  BioModels.net (EMBL - European Bioinformatics Institute)
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
import java.util.List;
import org.apache.log4j.Logger;


/**
 * <p>Handles some database connections for manipulating <code>Restriction</code>, mainly dealing with the database table 'mir_restriction' and 'mir_restriction_type'.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2011  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20111215
 */
public class RestrictionDao extends Dao
{
    private Logger logger = Logger.getLogger(RestrictionDao.class);
    
    
    /**
     * Constructor.
     * @param pool database pool
     */
    public RestrictionDao(String poolName)
    {
        super(poolName);
        // TODO Auto-generated constructor stub
    }
    
    
    /**
     * Retrieves the list of all existing restriction categories.
     * @return list of existing restriction categories
     */
    public List<RestrictionType> getRestrictionCategories()
    {
        Statement stmt = null;
        List<RestrictionType> result = new ArrayList<RestrictionType>();
        String sql = "SELECT * FROM mir_restriction_type ORDER BY short_desc ASC";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            boolean notEmpty = rs.first();
            while (notEmpty)
            {
                RestrictionType temp = new RestrictionType();
                temp.setId(rs.getInt("id"));
                temp.setCategory(rs.getString("short_desc"));
                temp.setDesc(rs.getString("long_desc"));
                result.add(temp);
                notEmpty = rs.next();
            }
            rs.close();
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of categories of restrictions!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves all the restriction(s) associated with a given data collection (already published).
     * @param id identifier of a data collection
     * @return list of restriction(s) associated with the data collection
     */
    public List<Restriction> getRestrictionsInPubl(String collectionId)
    {
        return getRestrictions(collectionId, "mir_restriction");
    }
    
    /**
     * Retrieves all the restriction(s) associated with a given data collection (in the curation pipeline).
     * @param id identifier of a data collection
     * @return list of restriction(s) associated with the data collection
     */
    public List<Restriction> getRestrictionsInCura(String collectionId)
    {
        return getRestrictions(collectionId, "cura_restriction");
    }
    
    
    /**
     * Retrieves all the restriction(s) associated with a given data collection (whether in the curation pipeline or already published).
     * @param id identifier of a data collection
     * @param table name of the database table to use (either: 'mir_restriction' or 'cura_restriction')
     * @return list of restriction(s) associated with the data collection
     */
    private List<Restriction> getRestrictions(String collectionId, String table)
    {
        Statement stmt = null;
        List<Restriction> result = new ArrayList<Restriction>();
        String sql = "SELECT t.id AS rid, t.id AS tid, r.desc, r.link, r.link_text, t.short_desc, t.long_desc FROM " + table + " r, mir_restriction_type t WHERE ((r.ptr_datatype = '" + collectionId + "') AND (r.ptr_restriction = t.id))";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                Restriction temp = new Restriction();
                temp.setId(rs.getInt("rid"));
                temp.getType().setId(rs.getInt("tid"));
                temp.getType().setCategory(rs.getString("short_desc"));
                temp.getType().setDesc(rs.getString("long_desc"));
                temp.setInfo(rs.getString("desc"));
                temp.setLink(rs.getString("link"));
                temp.setLinkText(rs.getString("link_text"));
                // add the restriction to the data collection
                result.add(temp);
                // next resource (if it exists)
                notEmpty = rs.next();
            }
            rs.close();
        }
        catch (SQLException e)
        {
            logger.error("Error while searching the restrictions associated with: " + collectionId + " (in '" + table + "')!");
            logger.error("SQLException raised: " + e.getMessage());
        }
        
        return result;
    }
    
    
    /**
     * Retrieves a type of restriction given its internal identifier.
     * @param typeId internal identifier of a type of restriction
     * @return type of restriction
     */
    public RestrictionType getRestrictionType(Integer typeId)
    {
        Statement stmt = null;
        RestrictionType type = null;
        String sql = "SELECT * FROM mir_restriction_type WHERE (id = " + typeId + ")";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.first())
            {
                type = new RestrictionType();
                type.setId(rs.getInt("id"));
                type.setCategory(rs.getString("short_desc"));
                type.setDesc(rs.getString("long_desc"));
            }
            rs.close();
        }
        catch (SQLException e)
        {
            logger.error("Error while looking for restriction type: " + typeId + "!");
            logger.error("SQLException raised: " + e.getMessage());
        }
        
        return type;
    }
    
    
    /**
     * Attached a new restriction to a data collection (in the curation pipeline).
     * No test of the existence of the data collection is performed at this stage.
     * @param collectionId identifier of a data collection
     * @param categoryId internal identifier of a type of restriction
     * @param desc description of the restriction
     * @param link URL towards further information
     * @param linkDesc short description (content of the 'title' attribute of the link) of the link
     * @return whether or not the addition is a success
     */
    public Boolean addRestrictionInCura(String collectionId, Integer categoryId, String desc, String link, String linkDesc)
    {
        return addRestriction(collectionId, categoryId, desc, link, linkDesc, "cura_restriction");
    }
    
    
    /**
     * Attached a new restriction to a data collection (already published).
     * No test of the existence of the data collection is performed at this stage.
     * @param collectionId identifier of a data collection
     * @param categoryId internal identifier of a type of restriction
     * @param desc description of the restriction
     * @param link URL towards further information
     * @param linkDesc short description (content of the 'title' attribute of the link) of the link
     * @return whether or not the addition is a success
     */
    public Boolean addRestrictionInPubl(String collectionId, Integer categoryId, String desc, String link, String linkDesc)
    {
        return addRestriction(collectionId, categoryId, desc, link, linkDesc, "mir_restriction");
    }
    
    
    /**
     * Attached a new restriction to a data collection (whether already published or in the curation pipeline).
     * No test of the existence of the data collection is performed at this stage.
     * @param collectionId identifier of a data collection
     * @param categoryId internal identifier of a type of restriction
     * @param desc description of the restriction
     * @param link URL towards further information
     * @param linkDesc short description (content of the 'title' attribute of the link) of the link
     * @param table name of the database table to use (either: 'mir_restriction' or 'cura_restriction')
     * @return whether or not the addition is a success
     */
    public Boolean addRestriction(String collectionId, Integer categoryId, String desc, String link, String linkDesc, String table)
    {
        Boolean result = false;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO " + table + " (`desc`, link, link_text, ptr_datatype, ptr_restriction) VALUES (?, ?, ?, ?, ?)"; 
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, desc);
            stmt.setString(2, link);
            stmt.setString(3, linkDesc);
            stmt.setString(4, collectionId);
            stmt.setInt(5, categoryId);
            int state = stmt.executeUpdate();
            if (state == 1)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("An exception occurred during the creation of a new restriction (" + categoryId + ") associated with: " + collectionId +  " (in '" + table + "')");
            logger.error("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /*
    public int deleteRestriction(String collectionId, Integer restrictionId)
    {
        
    }
    */
}
