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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import uk.ac.ebi.miriam.web.MiriamUtilities;


/**
 * <p>Performs all the persistence features (link with the database) of a <code>User</code> object, such as "retrieve" or "save".
 * <p>It is necessary to use the method "setParameters()" before any of the other methods available!
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
 * @version 20140114
 */
public class UserDao extends Dao
{
    private Logger logger = Logger.getLogger(UserDao.class);
    /**
     * Identification of the Registry within the authentication database. 
     */
    public final String REGISTRY_APP = "miriam";


    /**
     * Default constructor.
     * @param pool database pool
     */
    public UserDao(String pool)
    {
        super(pool);
    }


    /**
     * Retrieves a <code>User</code> from the database. Returns the status of this retrieval. The retrieved <code>User</code> is stored in this object.
     * @param login
     * @return True if retrieval is a success, False otherwise
     */
    public User retrieveUser(String login)
    {
    	User user = null;
        ResultSet rs = null;
        String sql = "SELECT firstname, lastname, email, password, organisation FROM auth_user WHERE (login=?)";
        PreparedStatement stmt = null;
        try
        {
        	stmt = openPreparedStatement(sql);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            int nbLines = DbPoolConnect.getRowCount(rs);
            if (nbLines == 1)
            {
            	user = new User();
                user.setLogin(login);
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setOrganisation(rs.getString("organisation"));
            }
            else
            {
                if (nbLines > 1)
                {
                    logger.warn("The user '" + login + "' is recorded several times in the database!");
                }
                else
                {
                    logger.warn("The user '" + login + "' is not recoded in the database!");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error during a SQL query (with prepared statements): retrieving personnal info about the user (" + login + ")!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	closeResultSet(rs);
            closePreparedStatement(stmt);
        }

        return user;
    }

    /**
     * Checks whether a given user has access to one specific application.
     * @param login
     * @param app, please use the constant provided by UserDao.REGISTRY_APP
     * @return
     */
    public Boolean checkUserAccess(String login, String app)
    {
    	Boolean hasAccess = false;
    	ResultSet rs = null;
    	PreparedStatement stmt = null;
    	String sql = "SELECT u.login, a.id FROM auth_user u, auth_app a, auth_link l WHERE ((u.login = ?) AND (a.id = ?)  AND (l.ptr_user = ?) AND (l.ptr_app = ?))";
    	try
    	{
    		stmt = openPreparedStatement(sql);
    		stmt.setString(1, login);
    		stmt.setString(2, app);
    		stmt.setString(3, login);
    		stmt.setString(4, app);
    		rs = stmt.executeQuery();

    		int nbLines = DbPoolConnect.getRowCount(rs);
    		if (nbLines == 1)
    		{
    			hasAccess = true;
    		}
    	}
    	catch (SQLException e)
    	{
    		logger.error("Error during a SQL query (with prepared statements): check if a user '" + login + "' has access to the application '" + app + "'!");
    		logger.error("SQL Exception raised: " + e.getMessage());
    	}
    	finally
        {
    		closeResultSet(rs);
            closePreparedStatement(stmt);
        }

    	return hasAccess;
    }


    /**
     * Retrieves the role of the user for the given application.
     * @param login
     * @param app
     * @return user role or 'null' in case of user not found or SQL error.
     */
    public String retrieveUserRole(String login, String app)
    {
    	String role = null;
    	ResultSet rs = null;
    	PreparedStatement stmt = null;
    	String sql = "SELECT l.ptr_role FROM auth_link l WHERE ((l.ptr_user = ?) AND (l.ptr_app = ?))";
        try
        {
        	stmt = openPreparedStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, app);
            rs = stmt.executeQuery();

            // retrieves the role of the user
            rs.first();
            role = rs.getString(1);
        }
        catch (SQLException e)
        {
            logger.error("Error during a SQL query (with prepared statements): retrieves the role of user '" + login + "' for application '" + app + "'!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	closeResultSet(rs);
            closePreparedStatement(stmt);
        }

        return role;
    }


    /**
     * Saves the user information in the database. This will only update the 'first name', 'last name', 'email' and 'organisation' fields, not the password or others!
     * @return True if the save is a success, False otherwise
     */
    public Boolean updateUserInfo(User user)
    {
        boolean status = false;
        PreparedStatement stmt = null;
        String sql = "UPDATE auth_user SET firstname=?, lastname=?, email=?, organisation=? WHERE (login=?)";
        try
        {
        	stmt = openPreparedStatement(sql);
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getOrganisation());
            stmt.setString(5, user.getLogin());
            logger.debug("SQL query (user information update): " + stmt.toString());
            int resultStatus = stmt.executeUpdate();
            stmt.close();

            if (resultStatus != 1)   // failure to perform the update
            {
                status = false;
            }
            else
            {
                status = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("Error during a SQL query (with prepared statements): updating personnal info about the user (" + user.getLogin() + ")!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	closePreparedStatement(stmt);
        }

        return status;
    }


    /**
     * Updates the password of the provided user.
     * @param login
     * @param encryptedPassword
     * @return whether the update is succesful or not.
     */
    public Boolean updateUserPassword(String login, String encryptedPassword)
    {
    	 boolean updated = false;
    	 PreparedStatement stmt = null;
         String sql = "UPDATE auth_user SET password=? WHERE (login=?)";

         try
         {
        	 stmt = openPreparedStatement(sql);
	         stmt.setString(1, encryptedPassword);
	         stmt.setString(2, login);
	         //logger.debug("SQL query (password update): " + stmt.toString());
	         int resultStatus = stmt.executeUpdate();

	         if (resultStatus != 1)   // failure to perform the update
	         {
	        	 updated = false;
	         }
	         else
	         {
	        	 updated = true;
	         }
         }
         catch (SQLException e)
         {
             logger.error("Error during a SQL query (with prepared statements): updating user password (" + login + ")!");
             logger.error("SQL Exception raised: " + e.getMessage());
         }
         finally
         {
         	closePreparedStatement(stmt);
         }

    	 return updated;
    }


    /**
     * Updates the date of last login for a given user and application.
     * @param login
     * @param role
     * @param app
     * @return whether the update has been successful
     */
	public Boolean updateUserLastLogin(String login, String role, String app)
	{
		Boolean updated = false;
		PreparedStatement stmt = null;
		String sql = "UPDATE auth_link SET last_login=NOW() WHERE ((ptr_user=?) AND (ptr_app=?) AND (ptr_role=?))";
        try
        {
        	stmt = openPreparedStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, app);
            stmt.setString(3, role);
            //logger.debug("SQL query: " + stmt.toString());
            int resultStatus = stmt.executeUpdate();

            if (resultStatus != 1)    // failure to update the last login date
            {
                logger.warn("Unable to update the last login date of '" + login + "' for app '" + app + "' (" + role + ")");
            }
            else
            {
            	updated = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("An exception occurred while updating the date of last login of the user: '" + login + "' for app '" + app + "'!");
            logger.error("SQLException raised: " + e.getMessage());
        }
        finally
        {
        	closePreparedStatement(stmt);
        }

		return updated;
	}


    /**
     * Retrieves all the users of the Registry.
     */
    public List<UserManageDetails> getUsers()
    {
        List<UserManageDetails> result = new ArrayList<UserManageDetails>();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        String sql = "SELECT u.login, u.firstname, u.lastname, r.role, l.last_login FROM auth_user u, auth_role r, auth_app a, auth_link l WHERE ((l.ptr_app = 'miriam') AND (l.ptr_user = u.login) AND (l.ptr_app = a.id) AND (l.ptr_role = r.id))";
        try
        {
        	stmt = openPreparedStatement(sql);
            rs = stmt.executeQuery();
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                UserManageDetails temp = new UserManageDetails();
                temp.setLogin(rs.getString("login"));
                temp.setName(rs.getString("firstname") + " " + rs.getString("lastname"));
                temp.setRole(rs.getString("role"));
                temp.setLastLogin(rs.getTimestamp("last_login"));   // the date *MUST* be different to '0000-00-00 00:00:00'!!!
                /*
                Error during a simple (without pooling) SQL query (with prepared statements): retrieving some information about the users!
                2008-05-15 17:14:53,699 ERROR db.UserDao        (            ?:?)     - SQL Exception raised: Value '0000-00-00 00:00:00' can not be represented as java.sql.Date
                */
                result.add(temp);

                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error during a SQL query (with prepared statements): retrieving some information about the users!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	closeResultSet(rs);
        	closePreparedStatement(stmt);
        }

        return result;
    }

    public boolean addUser(User user){
        Boolean added = false;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO auth_user (login, firstname, lastname, email, password, organisation) VALUES (?, ?, ?, ?, ?, ?)";
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getOrganisation());
            int resultStatus = stmt.executeUpdate();
            System.out.println("SQL query: " + stmt.toString());
            if (resultStatus != 1)    // failure to add new user
            {
                logger.warn("Unable to add '" + user.getLogin());
            }else
                added = true;

        }
        catch (SQLException e)
        {
            logger.error("An exception occurred while adding a new user: '" + user.getLogin());
            logger.error("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        return added;
    }

    public boolean addUserRole(String login) {
        Boolean added = false;
        PreparedStatement stmt = null;
        String sql = "INSERT INTO auth_link (last_login, ptr_user, ptr_app, ptr_role) VALUES (?, ?, ?, ?)";
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            stmt.setString(2, login);
            stmt.setString(3, REGISTRY_APP);
            stmt.setString(4, MiriamUtilities.USER_GENERAL);
            int resultStatus = stmt.executeUpdate();

            if (resultStatus != 1)    // failure to add new user role
            {
                logger.warn("Unable to add '" + login);
            } else
                added = true;
        }
        catch (SQLException e)
        {
            logger.error("An exception occurred while adding a new user role: '" + login);
            logger.error("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }

        return added;
    }
}
