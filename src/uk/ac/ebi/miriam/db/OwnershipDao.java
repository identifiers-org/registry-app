package uk.ac.ebi.miriam.db;

import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 06/10/2014
 *         Time: 12:38
 */
public class OwnershipDao extends Dao {
    private Logger logger = Logger.getLogger(OwnershipDao.class);

    public OwnershipDao(String poolName) {
        super(poolName);
    }

    public int retrieveOwnershipStatus(String user, String resource){
        int status = -1;
        ResultSet rs = null;
        String sql = "SELECT status FROM  user_ownership WHERE (ptr_user=? AND ptr_resource=?)";
        PreparedStatement stmt = null;
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, user);
            stmt.setString(2, resource);
            rs = stmt.executeQuery();

            int nbLines = DbPoolConnect.getRowCount(rs);
            if (nbLines == 1){
                status = rs.getInt("status");
            }
            else
            {
                if (nbLines > 1)
                {
                    logger.warn("The ownership of resource '" + resource + "' owned by '"+user+"' is recorded several times in the database!");
                }
                else
                {
                    logger.warn("The resource '"+ resource+ "' is not owned by the user '" + user + "'");
                }
            }
        }
        catch (SQLException e)
        {
            logger.error("Error during a SQL query (with prepared statements): retrieving ownership info about the user (" + user + ")!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeResultSet(rs);
            closePreparedStatement(stmt);
        }

        return status;

    }

    public boolean addOwnership(String user, String resource)
    {
        PreparedStatement stmt = null;
        boolean state = false;

        // creates the new ownership it it doesn't already exist
        if (retrieveOwnershipStatus(user,resource)==-1)
        {
            String sql = "INSERT INTO user_ownership (ptr_user, ptr_resource, status) VALUES (?, ?, ?)";
            try
            {
                stmt = openPreparedStatement(sql);
                stmt.setString(1, user);
                stmt.setString(2, resource);
                stmt.setInt(3, 0);
                logger.debug("SQL prepared query: " + stmt.toString());
                stmt.executeUpdate();
                stmt.close();
                state = true;
            }
            catch (SQLException e)
            {
                logger.warn("An exception occurred while creating a ownership record for user (" + user + ") and resource (" + resource + ")!");
                logger.warn("SQLException raised: " + e.getMessage());
            }
            finally
            {
                closePreparedStatement(stmt);
            }
        }

        return state;
    }


}
