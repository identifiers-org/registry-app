/*
 * MIRIAM Registry (Web Application)
 * The Registry is an online resource created to catalogue data collections 
 * (their URIs and the corresponding physical URLs, whether these are controlled vocabularies or databases)
 * and provide unique and stable identifiers for life science, in the form of URIs. 
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2013  BioModels.net (EMBL - European Bioinformatics Institute)
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


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/**
 * <p>Generic abstract class for database pool connection (Data Access Object).
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2013  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20130312
 */
public abstract class Dao
{
    private Logger logger = Logger.getLogger(Dao.class);
    private String poolName;
    protected Connection connection = null;
    
    
    /**
     * Constructor.
     * Warning: before using the newly created object, you need to call setupEnv()!
     * @param pool data base pool
     */
    public Dao(String poolName)
    {
        this.poolName = poolName;
        
        // setup env
        if (! setupEnv())
        {
            logger.error("Unable to connect to database pool!");
        }
    }
    
    
    /**
     * Sets up the database connection environment.
     */
    protected boolean setupEnv()
    {
        DataSource dataSource = null;
        Context initContext = null;
        Context envContext = null;
        boolean success = false;
        
        try
        {
            initContext = new InitialContext();
        }
        catch (NamingException e)
        {
            logger.error("Connection to the database pool: failure, no initial context!");
            logger.error("NamingException raised: " + e.getMessage());
        }

        if (null == initContext)
        {
            logger.error("Connection to the database pool: failed to retrieve the initial context.");
        }
        else
        {
            try
            {
                envContext = (Context) initContext.lookup("java:/comp/env");
            }
            catch (NamingException e)
            {
                logger.error("Connection to the database pool: failure, no environment context!");
                logger.error("NamingException raised: " + e.getMessage());
            }
            
            if (null == envContext)
            {
                logger.error("Connection to the database pool: failed to retrieve the environment context.");
            }
            else
            {
                try
                {
                    // look up a DataSource, which represents a connection pool
                    dataSource = (DataSource) envContext.lookup("jdbc/" + this.poolName + DbServer.getDbServerText());
                }
                catch (NamingException e)
                {
                    logger.error("Connection to the database pool: failure, no data source 'jdbc/" + this.poolName + DbServer.getDbServerText()+ "' available!");
                    logger.error("NamingException raised: " + e.getMessage());
                    logger.error("StackTrace: ",e);
                }
                
                if (null == dataSource)
                {
                    logger.error("Connection to the database pool: failed to retrieve the data source.");
                }
                else
                {
                    try
                    {
                        synchronized (dataSource)
                        {
                          this.connection = dataSource.getConnection();
                        }
                    }
                    catch (Exception e)
                    {
                        logger.error("Unable to open a database connection!");
                        logger.error("Exception raised: " + e.getMessage());
                        logger.error("StackTrace: ",e);
                    }
                    
                    if (null == this.connection)
                    {
                        logger.error("Unable to get a connection from the database pool!");
                    }
                    else
                    {
                        success = true;
                    }
                }
            }
        }
        
        return success;
    }
    
    
    /**
     * Cleans the database connection environment.
     */
    public void clean()
    {
        try
        {
            if (null != this.connection)
            {
                this.connection.close();   // returns the connection to the pool
            }
        }
        catch (Exception e)
        {
            logger.warn("Unable to close a previously opened database connection!");
            logger.warn("Exception raised: " + e.getMessage());
        }
    }
    
    
    /**
     * Closes a SQL <code>ResultSet</code>
     * @param rs
     */
    public void closeResultSet(ResultSet rs)
    {
    	if (null != rs)
		{
	    	try
	    	{
    			rs.close();
			}
	    	catch (SQLException e)
	    	{
				logger.debug("Unable to close a ResultSet.");
				logger.debug(e.getMessage());
			}
		}
    }
    
    
    /**
     * Retrieves a SQL <code>Statement</code> from the database connection.
     * @return
     */
    public Statement openStatement() throws SQLException
    {
        Statement statement = null;
        statement = this.connection.createStatement();
        
        /*
        try
        {
            statement = this.connection.createStatement();
        }
        catch (SQLException e)
        {
            logger.error("Unable to retrieve a Statement from the connection to the database!");
        }
        */
        
        return statement;
    }
    
    /**
     * Closes a SQL <code>Statement</code>.
     * @param statement
     */
    public void closeStatement(Statement statement)
    {
        if (null != statement)
        {
            try
            {
                statement.close();
            }
            catch (SQLException e)
            {
                logger.debug("Unable to close a SQL Statement!");
                logger.debug("SQLException raised: " + e.getMessage());
            }
        }
    }
    
    
    /**
     * Retrieves a SQL <code>PreparedStatement</code> from the database connection.
     * @param sql SQL query, including place holders '?'
     * @return
     * @throws SQLException
     */
    public PreparedStatement openPreparedStatement(String sql) throws SQLException
    {
        PreparedStatement preparedStatement = null;
        preparedStatement = this.connection.prepareStatement(sql);
        
        return preparedStatement;
    }
    
    
    /**
     * Closes a SQL <code>PreparedStatement</code>.
     * @param preparedStatement
     */
    public void closePreparedStatement(PreparedStatement preparedStatement)
    {
        try
        {
        	if (null != preparedStatement)
        	{
        		preparedStatement.close();
        	}
        }
        catch (SQLException e)
        {
            logger.debug("Unable to close a SQL PreparedStatement!");
            logger.debug("SQLException raised: " + e.getMessage());
        }
    }
    
    
    /*
     * Executes a simple SQL query.
     * @param sql SQL query
     * @return ResultSet
     *
    public ResultSet simpleQuery(String sql)
    {
        ResultSet result;
        result = pool.request(pool.getStatement(), sql);
        
        return result;
    }
    */
    
    
    /**
     * Getter
     * @return the poolName
     */
    public String getPoolName()
    {
        return this.poolName;
    }
    
    
    /**
     * Setter
     * @param poolName the poolName to set
     */
    private void setPoolName(String poolName)
    {
        this.poolName = poolName;
    }
    
    
    
    /*
     * Executes a prepared statement.
     * @param SQL prepared statement
     * @return ResultSet
    public ResultSet preparedQuery(String sql, List<Object> params)
    {
        ResultSet result = null;
        
        PreparedStatement stmt = pool.getPreparedStatement(sql);
        try
        {
            // what to do with the different parameters (different types)?
            //stmt.setString(1, ?);
            //stmt.setInt(5, ?);
            result = stmt.executeQuery();
            
            // what to do with the result before closing the prepared statement?
            
            stmt.close();
        }
        catch (SQLException e)
        {
            // do something here!
        }
        
        return result;
    }
    */
}
