package uk.ac.ebi.miriam.db;

import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 10/10/2014
 *         Time: 14:22
 */
public class EditHistoryDao extends Dao {
    private Logger logger = Logger.getLogger(EditHistoryDao.class);

    /**
     * Constructor.
     * Warning: before using the newly created object, you need to call setupEnv()!
     *
     * @param poolName
     */
    public EditHistoryDao(String poolName) {
        super(poolName);
    }

    public boolean insertChange(String collection, String login, String text){
        PreparedStatement stmt = null;
        boolean state = false;

        String sql = "INSERT INTO mir_edit_history (ptr_datatype, ptr_login, history) VALUES (?, ?, ?)";
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, collection);
            stmt.setString(2, login);
            stmt.setString(3, text);
            logger.debug("SQL prepared query: " + stmt.toString());
            stmt.executeUpdate();
            stmt.close();
            state = true;
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred while creating a edit history record for user (" + login + ") and collection (" + collection + ")!");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }

        return state;
    }
}
