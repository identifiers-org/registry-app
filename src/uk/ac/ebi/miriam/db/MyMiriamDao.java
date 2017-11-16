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


import uk.ac.ebi.miriam.tools.CommonFunctions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jasypt.util.password.StrongPasswordEncryptor;


/**
 * <p>Handles some database connections for handling myMIRIAM profile features, mainly dealing with the database tables 'mir_my_miriam' and 'mir_profiles'.
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
 * @version 20140305
 */
public class MyMiriamDao extends Dao
{
    private Logger logger = Logger.getLogger(MyMiriamDao.class);
    
    
    public MyMiriamDao(String pool)
    {
        super(pool);
    }
    
    
    /**
     * Checks that the given profile uses myMIRIAM.
     * @param profile shortname of a profile
     * @return whether the profile is registered in myMIRIAM or not
     */
    public Boolean isProfileExisting(String profile)
    {
        Boolean exist = false;
        PreparedStatement stmt = null;
        String sql = "SELECT * FROM mir_profiles WHERE (shortname = ?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, profile);
            ResultSet rs = stmt.executeQuery();
            int nbRows = DbConnection.getRowCount(rs);
            
            if (nbRows > 0)
            {
                if (nbRows == 1)
                {
                    exist = true;
                }
                else   // more than one profile with the given shortname (this should never happen)
                {
                    logger.error("More than one profile with the shortname '" + profile + "'!");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while checking the existence of the profile '" + profile + "'!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return exist;
    }
    
    
    /**
     * Checks that the given profile uses myMIRIAM.
     * @param profileId internal identifier of a profile
     * @return whether the profile is registered in myMIRIAM or not
     */
    public Boolean isProfileExisting(Integer profileId)
    {
        Boolean exist = false;
        PreparedStatement stmt = null;
        String sql = "SELECT * FROM mir_profiles WHERE (id = ?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            int nbRows = DbConnection.getRowCount(rs);
            
            if (nbRows > 0)
            {
                if (nbRows == 1)
                {
                    exist = true;
                }
                else   // more than one profile with the given identifier (this should never happen)
                {
                    logger.error("More than one profile with the identifier '" + profileId + "'!");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while checking the existence of the profile '" + profileId + "'!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return exist;
    }
    
    
    /**
     * Checks that the provided encoded key is correct.
     * You should first use isProfileExisting(profile).
     * @param profile shortname of a profile
     * @param key encoded key to access myMIRIAM for the given profile 
     * @return
     */
    public Boolean accessCheck(String profile, String key)
    {
        Boolean access = false;
        PreparedStatement stmt = null;
        String sql = "SELECT * FROM mir_profiles WHERE (shortname = ?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, profile);
            ResultSet rs = stmt.executeQuery();
            int nbRows = DbConnection.getRowCount(rs);
            
            if (nbRows > 0)
            {
                // only one record (profile) found
                if (nbRows == 1)
                {
                    // the provided encoded key is the same as in the database 
                    if (key.equals(rs.getString("key")))
                    {
                        access = true;
                    }
                }
                else   // more than one profile with the given shortname (this should never happen)
                {
                    logger.error("More than one profile with the same shortname and key (" + profile + ")!");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while checking the access to the profile '" + profile + "' with a given (encoded) key!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return access;
    }
    
    
    /**
     * Retrieves the list of all profiles registered in myMIRIAM.
     * @return
     */
    public List<Profile> getAllProfiles()
    {
        List<Profile> profiles = new ArrayList<Profile>();
        
        Statement stmt = null;
        String sql = "SELECT * FROM mir_profiles ORDER BY shortname";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                Profile temp = new Profile();
                temp.setId(rs.getInt("id"));
                temp.setName(rs.getString("name"));
                temp.setShortName(rs.getString("shortname"));
                temp.setDesc(rs.getString("description"));
                if (rs.getInt("public") == 0)
                {
                    temp.setOpenAccess(false);
                }
                else
                {
                    temp.setOpenAccess(true);
                }
                temp.setKey(rs.getString("key"));
                temp.setContactEmail(rs.getString("contact_email"));
                temp.setDateCreation(rs.getTimestamp("date_created"));

                if (rs.getInt("auto") == 0)
                {
                    temp.setAuto(false);
                }
                else
                {
                    temp.setAuto(true);
                }


                
                profiles.add(temp);
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of all profiles registered in myMIRIAM!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return profiles;
    }

    /**
     * Retrieves the list of public profiles registered in myMIRIAM.
     * @return
     */
    public List<Profile> getPublicProfiles()
    {
        List<Profile> profiles = new ArrayList<Profile>();

        Statement stmt = null;
        String sql = "SELECT * FROM mir_profiles WHERE public = 1 ORDER BY shortname";

        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);

            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                Profile temp = new Profile();
                temp.setId(rs.getInt("id"));
                temp.setName(rs.getString("name"));
                temp.setShortName(rs.getString("shortname"));
                temp.setDesc(rs.getString("description"));
                if (rs.getInt("public") == 0)
                {
                    temp.setOpenAccess(false);
                }
                else
                {
                    temp.setOpenAccess(true);
                }
                temp.setKey(rs.getString("key"));
                temp.setContactEmail(rs.getString("contact_email"));
                temp.setDateCreation(rs.getTimestamp("date_created"));

                if (rs.getInt("auto") == 0)
                {
                    temp.setAuto(false);
                }
                else
                {
                    temp.setAuto(true);
                }



                profiles.add(temp);
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of all profiles registered in myMIRIAM!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }

        return profiles;
    }

    /**
     * Retrieves the list of all profiles registered in myMIRIAM.
     * @return
     */
    public List<Profile> getUserProfiles(String login)
    {
        List<Profile> profiles = new ArrayList<Profile>();

        PreparedStatement stmt = null;
        String sql = "SELECT * FROM mir_profiles WHERE ptr_login = ? ORDER BY shortname";

        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                Profile temp = new Profile();
                temp.setId(rs.getInt("id"));
                temp.setName(rs.getString("name"));
                temp.setShortName(rs.getString("shortname"));
                temp.setDesc(rs.getString("description"));
                if (rs.getInt("public") == 0)
                {
                    temp.setOpenAccess(false);
                }
                else
                {
                    temp.setOpenAccess(true);
                }
                temp.setKey(rs.getString("key"));
                temp.setContactEmail(rs.getString("contact_email"));
                temp.setDateCreation(rs.getTimestamp("date_created"));

                if (rs.getInt("auto") == 0)
                {
                    temp.setAuto(false);
                }
                else
                {
                    temp.setAuto(true);
                }

                profiles.add(temp);
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of all profiles registered in myMIRIAM!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }

        return profiles;
    }

    
    
    /**
     * Retrieves additional information about a given profile (number of data types, date of last modification, ...).
     * This information is stored in other tables.
     * @param profile
     */
    public void getAdditionalInfo(Profile profile)
    {
        PreparedStatement stmt = null;
        String sql = "SELECT m.id, m.ptr_datatype, m.date_modif, p.date_created FROM mir_my_miriam m, mir_profiles p WHERE ((p.shortname = ?) AND (m.ptr_my_project = p.id))";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, profile.getShortName());
            ResultSet rs = stmt.executeQuery();
            int counter = 0;
            
            // creates an old random date for looking for the latest one
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try
            {
                date = format.parse("1982-08-06");
            }
            catch (ParseException e)
            {
                logger.error("Unable to parse the date: 1982-08-06!");
                logger.error("ParseException raised: " + e.getMessage());
                date = new Date();   // will mess up the following tests, but prevents a possible exception...
            }
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                counter++;
                Date current = rs.getTimestamp("date_modif");
                if (date.before(current))
                {
                    date = current;
                }
                
                // next data type (if any)
                notEmpty = rs.next();
            }
            
            profile.setDateLastModif(date);
            profile.setNbDataTypes(counter);
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving extra info about a given myMIRIAM registered profile (" + profile.getShortName() + ", " + profile.getId()  + ")!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
    }
    
    
    /**
     * Retrieves the list of data collections associated with a given myMIRIAM profile.
     * @param profileId internal identifier of a myMIRIAM profile
     * @return
     */
    public List<MyMiriamDataType> getDataTypesOfProfile(Integer profileId)
    {
        List<MyMiriamDataType> datatypes = new ArrayList<MyMiriamDataType>();
        PreparedStatement stmt = null;
        String sql = "SELECT m.ptr_datatype, d.name, m.date_added, m.date_modif, m.ptr_preferred_resource FROM mir_my_miriam m, mir_datatype d WHERE ((m.ptr_my_project = ?) AND (m.ptr_datatype = d.datatype_id))";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                MyMiriamDataType temp = new MyMiriamDataType();
                temp.setId(rs.getString("ptr_datatype"));
                temp.setName(rs.getString("name"));
                temp.setDateAdded(rs.getTimestamp("date_added"));
                temp.setDateModif(rs.getTimestamp("date_modif"));
                temp.setSelected(true);
                
                // retrieves the resources
                List<MyMiriamResource> resources = new ArrayList<MyMiriamResource>();
                String sql2 = "SELECT * FROM mir_resource WHERE (ptr_datatype = '" + temp.getId() + "')";
                Statement stmt2 = openStatement();
                ResultSet rs2 = stmt2.executeQuery(sql2);
                boolean notEmpty2 = rs2.next();
                while (notEmpty2)
                {
                    MyMiriamResource resource = new MyMiriamResource();
                    resource.setId(rs2.getString("resource_id"));
                    resource.setInfo(rs2.getString("info"));
                    resource.setInstitution(rs2.getString("institution"));
                    resource.setLocation(rs2.getString("location"));
                    if (rs2.getInt("obsolete") == 1)
                    {
                        resource.setObsolete(true);
                    }
                    else
                    {
                        resource.setObsolete(false);
                    }
                    // is it the preferred resource?
                    if (rs.getString("ptr_preferred_resource").equals(resource.getId()))
                    {
                        resource.setPreferred(true);
                    }
                    else
                    {
                        resource.setPreferred(false);
                    }
                    resources.add(resource);
                    
                    // next resource (if any)
                    notEmpty2 = rs2.next();
                }
                temp.setResources(resources);
                closeStatement(stmt2);
                
                // adds the current data type to the list
                datatypes.add(temp);
                
                // next data type (if any)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of data collection(s) associated with the profile '" + profileId + "'!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return datatypes;
    }
    
    /**
     * Retrieves the list of data collection, which name start by the character(s) provided in parameter, associated with a given myMIRIAM profile.
     * @param profileId internal identifier of a myMIRIAM profile
     * @param startBy one or more characters
     * @return
     */
    public List<MyMiriamDataType> getDataTypesStartingByOfProfile(Integer profileId, String startBy)
    {
        List<MyMiriamDataType> datatypes = new ArrayList<MyMiriamDataType>();
        
        if (startBy.equalsIgnoreCase("selected"))
        {
            datatypes = getDataTypesOfProfile(profileId);
        }
        else   // normal query (like start by "a")
        {
            // retrieves all data collections corresponding to the subset requested
            List<SimpleDataType> subset = getDataTypesNameStartingBy(profileId, startBy);
            
            // retrieves all data collections associated with the current profile
            List<MyMiriamDataType> selected = getDataTypesOfProfile(profileId);
            
            // generates a list of identifiers, for better performance
            Map<String, Integer> identifiers = new HashMap<String, Integer>();
            for (int i=0; i<selected.size(); ++i)
            {
                // we only consider the selected data collections which name starts by the requested character or string
                if (CommonFunctions.startsWithIgnoreCase(selected.get(i).getName(), startBy))
                {
                    identifiers.put(selected.get(i).getId(), i);
                }
            }
            
            // merges both previous lists
            for (SimpleDataType data: subset)
            {
                // the current data type is not selected by the profile
                if (!identifiers.containsKey(data.getId()))
                {
                    MyMiriamDataType temp = new MyMiriamDataType();
                    temp.setId(data.getId());
                    temp.setName(data.getName());
                    
                    // retrieves the resources of the current data collection
                    temp.setResources(getResourcesAssociatedToDatatype(temp.getId()));
                    temp.setDateAdded(null);
                    temp.setDateModif(null);
                    temp.setSelected(false);
                    
                    datatypes.add(temp);
                }
                else
                {
                    datatypes.add(selected.get(identifiers.get(data.getId())));
                }
            }
        }
        
        return datatypes;
    }
    
    
    /**
     * Retrieves all the data collections which name start by the character(s) provided in parameter.
     * Obsolete data collections not returned.
     * @param profileId internal identifier of a profile: is not used with queries like "name start by a", but is used for "data collections selected by profile X"
     * @param startBy one or more characters
     * @return
     */
    public List<SimpleDataType> getDataTypesNameStartingBy(Integer profileId, String startBy)
    {
        PreparedStatement stmt = null;
        String sql = null;
        List<SimpleDataType> datatypes = new ArrayList<SimpleDataType>();
        
        // to be able to execute a prepared statement with a LIKE clause
        startBy = startBy + "%";
        
        sql = "SELECT d.name, d.definition, d.datatype_id, u.uri FROM mir_datatype d, mir_uri u WHERE ((d.obsolete=0) AND (d.name LIKE ?) AND (u.ptr_datatype=d.datatype_id) AND (u.uri_type='URN') AND (u.deprecated=0)) ORDER BY d.name";
        
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
                datatypes.add(temp);
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of data types which name start by: '" + startBy + "'.");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return datatypes;
    }
    
    
    /**
     * Retrieves the resources associated with the data type which identifier is provided in parameter.
     * This data type are considered to not be associated with the current myMIRIAM profile, hence the 'selected=false' for each resource.
     * @param profileId public identifier of a data type 
     * @return
     */
    public List<MyMiriamResource> getResourcesAssociatedToDatatype(String datatypeId)
    {
        List<MyMiriamResource> resources = new ArrayList<MyMiriamResource>();
        String sql = "SELECT * FROM mir_resource WHERE (ptr_datatype = '" + datatypeId + "')";
        Statement stmt = null;
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                MyMiriamResource resource = new MyMiriamResource();
                resource.setId(rs.getString("resource_id"));
                resource.setInfo(rs.getString("info"));
                resource.setInstitution(rs.getString("institution"));
                resource.setLocation(rs.getString("location"));
                if (rs.getInt("obsolete") == 1)
                {
                    resource.setObsolete(true);
                }
                else
                {
                    resource.setObsolete(false);
                }
                resource.setPreferred(false);
                resources.add(resource);
                
                // next resource (if any)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the list of resources associated with the datatype '" + datatypeId + "'!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return resources;
    }
    
    
    /**
     * Retrieves the details of a myMIRIAM profile, given its internal identifier.
     * @param profileId
     * @return
     */
    public Profile getProfile(Integer profileId)
    {
        Profile profile = null;
        PreparedStatement stmt = null;
        String sql = "SELECT * FROM mir_profiles WHERE (id = ?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            int nbRows = DbConnection.getRowCount(rs);
            
            if (nbRows > 0)
            {
                if (nbRows == 1)
                {
                    profile = new Profile();
                    profile.setId(rs.getInt("id"));
                    profile.setName(rs.getString("name"));
                    profile.setShortName(rs.getString("shortname"));
                    profile.setDesc(rs.getString("description"));
                    if (rs.getInt("public") == 0)
                    {
                        profile.setOpenAccess(false);
                    }
                    else
                    {
                        profile.setOpenAccess(true);
                    }
                    profile.setKey(rs.getString("key"));
                    profile.setContactEmail(rs.getString("contact_email"));
                    profile.setDateCreation(rs.getTimestamp("date_created"));
                }
                else   // more than one profile with the given identifier (this should never happen)
                {
                    logger.error("More than one profile with the identifier '" + profileId + "'!");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the myMIRIAM registered profile: " + profileId + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return profile;
    }
    
    /**
     * Retrieves the details of a myMIRIAM profile, given its shortName.
     * @param profileName
     * @return
     */
    public Profile getProfile(String profileName)
    {
        Profile profile = null;
        PreparedStatement stmt = null;
        String sql = "SELECT * FROM mir_profiles WHERE (shortname = ?)";

        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, profileName);
            ResultSet rs = stmt.executeQuery();
            int nbRows = DbConnection.getRowCount(rs);

            if (nbRows > 0)
            {
                if (nbRows == 1)
                {
                    profile = new Profile();
                    profile.setId(rs.getInt("id"));
                    profile.setName(rs.getString("name"));
                    profile.setShortName(rs.getString("shortname"));
                    profile.setDesc(rs.getString("description"));
                    if (rs.getInt("public") == 0)
                    {
                        profile.setOpenAccess(false);
                    }
                    else
                    {
                        profile.setOpenAccess(true);
                    }
                    profile.setKey(rs.getString("key"));
                    profile.setContactEmail(rs.getString("contact_email"));
                    profile.setDateCreation(rs.getTimestamp("date_created"));
                }
                else   // more than one profile with the given identifier (this should never happen)
                {
                    logger.error("More than one profile with the name '" + profileName + "'!");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the myMIRIAM registered profile: " + profileName + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }

        return profile;
    }

    /**
     * Checks that the key correspond to the profile
     * @param profileId identifier of a myMIRIAM profile
     * @param oldKey key of the profile
     * @return
     */
    public boolean checkKey(Integer profileId, String key)
    {
        boolean valid = false;
        PreparedStatement stmt = null;
        String sql = "SELECT * FROM mir_profiles WHERE (id = ?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setInt(1, profileId);
            ResultSet rs = stmt.executeQuery();
            int nbRows = DbConnection.getRowCount(rs);
            
            if (nbRows > 0)
            {
                if (nbRows == 1)
                {
                    String dbKey = rs.getString("key");
                    
                    StrongPasswordEncryptor keyEncryptor = new StrongPasswordEncryptor();
                    if (keyEncryptor.checkPassword(key, dbKey))   // key are equal
                    {
                        valid = true;
                    }
                }
                else   // more than one profile with the given identifier (this should never happen)
                {
                    logger.error("More than one profile with the identifier '" + profileId + "'!");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the key of a myMIRIAM registered profile: " + profileId + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return valid;
    }
    
    
    /**
     * Updates the key of a myMIRIAM profile.
     * This method also takes care of the encryption of the key.
     * @param profileId identifier of a myMIRIAM profile
     * @param key new value of the key
     */
    public boolean updateKey(Integer profileId, String key)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        
        // the profile exists
        if (isProfileExisting(profileId))
        {
            StrongPasswordEncryptor keyEncryptor = new StrongPasswordEncryptor();
            String encryptedKey = keyEncryptor.encryptPassword(key);
            
            String sql = "UPDATE mir_profiles SET `key`=? WHERE (id=?)";   // "key" is a reserved keyword in MySQL, so it is mandatory to use "`"
            try
            {
                stmt = openPreparedStatement(sql);
                stmt.setString(1, encryptedKey);
                stmt.setInt(2, profileId);
                int state = stmt.executeUpdate();
                if (state == 1)
                {
                    result = true;
                }
            }
            catch (SQLException e)
            {
                logger.error("An exception occurred during the update of the key of the myMIRIAM profile: " + profileId);
                logger.error("SQLException raised: " + e.getMessage());
            }
            finally
            {
                closePreparedStatement(stmt);
            }
        }
        
        return result;
    }
    
    
    /**
     * Updates myMIRIAM profile specific information about a data type (currently only the preferred resource).
     * @param profileId internal identifier of a myMIRIAM profile
     * @param datatype identifier of a data type
     * @param resource identifier of the preferred resource
     * @return True if the update is a success, False otherwise
     */
    public boolean updateMyDatatype(Integer profileId, String datatype, String resource)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        
        String sql = "UPDATE mir_my_miriam SET ptr_preferred_resource=?, date_modif=NOW() WHERE ((ptr_my_project=?) AND (ptr_datatype=?))";   // no need to do more check, the database will do the job
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, resource);
            stmt.setInt(2, profileId);
            stmt.setString(3, datatype);
            int state = stmt.executeUpdate();
            if (state == 1)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("An exception occurred during the update of the profile specific (" + profileId + ") record for the data type " + datatype + "!");
            logger.error("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Adds a new data type (with attached info, like preferred resource) to a myMIRIAM profile.
     * TODO: does not check that the data type nor the resource exists!
     * @param profileId internal identifier of a myMIRIAM profile
     * @param datatype identifier of a data type
     * @param resource identifier of the preferred resource
     * @return True if the addition is a success, False otherwise
     */
    public boolean addMyDatatype(Integer profileId, String datatype, String resource)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        
        // checks that the profiles exists
        if (isProfileExisting(profileId))
        {
            String sql = "INSERT INTO mir_my_miriam (ptr_my_project, ptr_datatype, ptr_preferred_resource, date_added, date_modif) VALUES (?, ?, ?, NOW(), NOW())";
            try
            {
                stmt = openPreparedStatement(sql);
                stmt.setInt(1, profileId);
                stmt.setString(2, datatype);
                stmt.setString(3, resource);
                int state = stmt.executeUpdate();
                if (state == 1)
                {
                    result = true;
                }
            }
            catch (SQLException e)
            {
                logger.error("An exception occurred during the addition of the data type " + datatype + " to the myMIRIAM profile " + profileId + "!");
                logger.error("SQLException raised: " + e.getMessage());
            }
            finally
            {
                closePreparedStatement(stmt);
            }
        }
        
        return result;
    }
    
    
    /**
     * Removes a data type from a myMIRIAM profile.
     * TODO: does not check that the record previously existed.
     * @param profileId internal identifier of a myMIRIAM profile
     * @param datatype identifier of a data type
     * @return True if the addition is a success, False otherwise
     */
    public boolean removeMyDatatype(Integer profileId, String datatype)
    {
        PreparedStatement stmt = null;
        boolean result = false;
        
        // checks that the profiles exists
        if (isProfileExisting(profileId))
        {
            String sql = "DELETE FROM mir_my_miriam WHERE ((id=?) AND (datatype_id=?))";
            try
            {
                stmt = openPreparedStatement(sql);
                stmt.setInt(1, profileId);
                stmt.setString(2, datatype);
                int state = stmt.executeUpdate();
                if (state == 1)
                {
                    result = true;
                }
            }
            catch (SQLException e)
            {
                logger.error("An exception occurred during the deletion of the data type " + datatype + " from the myMIRIAM profile " + profileId + "!");
                logger.error("SQLException raised: " + e.getMessage());
            }
            finally
            {
                closePreparedStatement(stmt);
            }
        }
        
        return result;
    }

    public boolean createProfile(String user, String name, String shortName, String description, int status, String key, String email)
    {
        PreparedStatement stmt = null;
        boolean result = false;


        String sql = "INSERT INTO mir_profiles (name, shortname, description, `key`, public, ptr_login, auto, contact_email) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, shortName);
            stmt.setString(3, description);
            stmt.setString(4, key);
            stmt.setInt(5, status);
            stmt.setString(6,user);
            stmt.setInt(7, 0);
            stmt.setString(8,email);
            int state = stmt.executeUpdate();
            if (state == 1)
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("An exception occurred during the addition of the profile " + shortName + " to the mir_profile.");
            logger.error("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }


        return result;
    }

}
