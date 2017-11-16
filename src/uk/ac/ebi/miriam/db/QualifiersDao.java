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
import java.util.List;

import org.apache.log4j.Logger;


/**
 * <p>Servlet which handles the database access for examples of annotation.
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
 * @version 20090515
 */
public class QualifiersDao extends Dao
{
    private Logger logger = Logger.getLogger(QualifiersDao.class);
    
    /**
     * Constructor.
     * @param poolName
     */
    public QualifiersDao(String pool)
    {
        super(pool);
    }

    
    /**
     * Retrieves the list of existing types of BioModels.net qualifiers.
     * @return list of existing types of BioModels.net qualifiers
     */
    public List<String> getExistingTypes()
    {
        Statement stmt = null;
        List<String> result = new ArrayList<String>();
        String sql = "SELECT DISTINCT name FROM biom_qualifiers_types ORDER BY name DESC";
        
        boolean notEmpty;
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            notEmpty = rs.next();
            while (notEmpty)
            {
                result.add(rs.getString("name"));
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the available types of BioModels.net qualifiers!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the list of all the BioModels.net qualifiers (including all their associated information).
     * @return list of all the BioModels.net qualifiers
     */
    public List<GroupQualifiers> getAllQualifiers()
    {
        Statement stmt = null;
        List<GroupQualifiers> result = new ArrayList<GroupQualifiers>();
        String sql = "SELECT * FROM biom_qualifiers q, biom_qualifiers_types t WHERE (q.ptr_type = t.id) ORDER BY t.name DESC, q.name ASC";
        
        boolean notEmpty;
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            GroupQualifiers tmpGroup = null;
            Qualifier tmpQualifier = null;
            String currentType = null;
            
            notEmpty = rs.next();
            while (notEmpty)
            {
                // new type of qualifier detected: creation of a new <code>GroupQualifiers</code> object
                if ((null == currentType) || (! currentType.equalsIgnoreCase(rs.getString("t.name"))))
                {
                    tmpGroup = new GroupQualifiers(rs.getString("t.name"), rs.getString("t.def"), rs.getString("t.namespace"));
                    tmpQualifier = new Qualifier(rs.getString("q.name"), rs.getString("q.def"), rs.getString("t.name"));
                    tmpGroup.addQualifier(tmpQualifier);
                    result.add(tmpGroup);
                }
                else
                {
                    tmpQualifier = new Qualifier(rs.getString("q.name"), rs.getString("q.def"), rs.getString("t.name"));
                    result.get(result.size()-1).addQualifier(tmpQualifier);
                }
                
                // updates the value of the current type of qualifiers
                currentType =  rs.getString("t.name");
                
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the available types of BioModels.net qualifiers!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Adds a new qualifier to the list of BioModels.net qualifiers.
     * @param qualifier new qualifier to add
     * @return true if the addition is a success, false otherwise
     */
    public boolean addQualifier(Qualifier qualifier)
    {
        PreparedStatement stmt = null;
        boolean state = false;
        boolean existing = false;
        
        // checks if the qualifier doesn't exist already
        existing = isExistingQualifier(qualifier);
        
        // creates the new association
        if (! existing)
        {
            String sql = "INSERT INTO biom_qualifiers (name, def, ptr_type) VALUES (?, ?, ?)";
            try
            {
                stmt = openPreparedStatement(sql);
                stmt.setString(1, qualifier.getName());
                stmt.setString(2, qualifier.getDefinition());
                stmt.setInt(3, getQualiferTypeId(qualifier.getType()));
                logger.debug("SQL prepared query: " + stmt.toString());
                stmt.executeUpdate();
                stmt.close();
                state = true;
                logger.debug("Creation of a new BioModels.net qualifier: ''" + qualifier.getName() + "' (" + qualifier.getType() + ").");
            }
            catch (SQLException e)
            {
                logger.warn("An exception occurred during the addition of a new BioModels.net qualifer (" + qualifier.getName() + ")!");
                logger.warn("SQLException raised: " + e.getMessage());
            }
            finally
            {
                closePreparedStatement(stmt);
            }
        }
        
        return state;
    }
    
    
    /**
     * Checks is a BioModels qualifier exists in the database, based on its name and type.
     * @param qualifier fully defined qualifier
     * @return true if the qualifier exists, false otherwise
     */
    public boolean isExistingQualifier(Qualifier qualifier)
    {
        PreparedStatement stmt = null;
        boolean existing = false;
        String sql = "SELECT * FROM biom_qualifiers q, biom_qualifiers_types t WHERE ((q.ptr_type = t.id) AND (q.name LIKE ?) AND (t.name LIKE ?))";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, qualifier.getName());
            stmt.setString(2, qualifier.getType());
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())
            {
                existing = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the existence test of a BioModels.net qualifier (" + qualifier.getName() + ", " + qualifier.getType() + ")!");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return existing;
    }
    
    
    /*
     * Retrieves the identifier (integer) of a type of qualifers, given its name.
     * @param type name of a type of qualifers
     * @return integer identifier of this type of qualifiers
     */
    private int getQualiferTypeId(String type)
    {
        PreparedStatement stmt = null;
        int result = 0;
        String sql = "SELECT t.id FROM biom_qualifiers_types t WHERE (t.name LIKE ?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, type);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())            
            {
                result = rs.getInt("t.id");
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the internal identifier of a type of qualifiers '" + type + "'!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
}
