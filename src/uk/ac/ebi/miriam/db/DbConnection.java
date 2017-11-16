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


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import org.apache.log4j.Logger;


/**
 * <p>
 * Manages the connection to a database (template). Only one connection, but several statements allowed.
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
 * @version 20090529
 */
abstract class DbConnection
{
  private static Logger logger = Logger.getLogger(DbConnection.class);   // should not be static
  private Connection connection = null;
  private LinkedList<Statement> statements = new LinkedList<Statement>();
  private LinkedList<PreparedStatement> preparedStatements = new LinkedList<PreparedStatement>();
  
  
  /**
   * Creates a new connection or recovery from a pool
   * @return Connection object
   */
  public abstract void newConnection();


  /**
   * Setter: update the connection
   */
  protected void setConnection(Connection conn)
  {
    this.connection = conn;
  }


  /**
   * Getter: access to the Connection object
   */
  public Connection getConnection()
  {
    // if no call of the 'newConnection()' before, we do it now
    if (connection == null)
    {
      newConnection();
    }

    return connection;
  }


  /**
   * Closes the connection to the database.
   * @param Connection
   */
  public void closeConnection()
  {
    // we close all the open statements, if needed
    if (! this.statements.isEmpty())
    {
      closeStatements();
    }

        // we close all the open prepared statements, if needed
        if (! this.preparedStatements.isEmpty())
        {
            closePreparedStatements();
        }

    // now we close the connection
    try
    {
      this.connection.close();
    }
    catch (SQLException e)
    {
      logger.warn("An exception occured during the closing of the connection!");
      logger.warn("SQL Exception raised: " + e.getMessage());
    }
  }


  /**
   * Creates a (new) <code>Statement</code>
   * @return Statement created
   */
  public Statement getStatement()
  {
    Statement stmt = null;

    if (connection != null)
    {
      try
      {
        stmt = connection.createStatement();
        //logger.debug("successful creation of a new statement");
      }
      catch (SQLException e)
      {
        logger.error("An exception occured during the creation of a statement!");
        logger.error("SQL Exception raised: " + e.getMessage());
      }
    }
    if (stmt != null)
    {
      statements.add(stmt);
    }
    
    return statements.getLast();
  }


  /**
   * Closes the lastest open statement
   */
  public void closeStatement()
  {
    Statement temp;

    temp = statements.removeLast();
    try
    {
      temp.close();
    }
    catch (SQLException e)
    {
      logger.warn("An exception occured during the closing of a statement!");
      logger.warn("SQL Exception raised: " + e.getMessage());
    }
    }


  /**
   * Closes all the opened statements
   */
  public void closeStatements()
  {
    Statement temp;

    while (! statements.isEmpty())
    {
      temp = statements.removeLast();
      try
      {
        temp.close();
      }
      catch (SQLException e)
      {
        logger.warn("An exception occured during the closing of a statement!");
        logger.warn("SQL Exception raised: " + e.getMessage());
      }
    }
  }


    /**
     * Creates a (new) <code>PreparedStatement</code>
     * @param sql prepared statement (including "?" in it)
     * @return PreparedStatement created
     */
    public PreparedStatement getPreparedStatement(String sql)
    {
        PreparedStatement stmt = null;

        if (connection != null)
        {
            try
            {
                stmt = connection.prepareStatement(sql);
                //logger.debug("successful creation of a new prepared statement");
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the creation of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
        }
        else
        {
            // perhaps we could open a connection here, or not...
        }

        if (stmt != null)
        {
            this.preparedStatements.add(stmt);
        }

        return preparedStatements.getLast();
    }


    /**
     * Closes the lastest open <code>PreparedStatement</code>
     */
    public void closePreparedStatement()
    {
        PreparedStatement temp;

        temp = this.preparedStatements.removeLast();
        try
        {
            temp.close();
        }
        catch (SQLException e)
        {
            logger.warn("An exception occured during the closing of a prepared statement!");
            logger.warn("SQL Exception raised: " + e.getMessage());
        }
    }


    /**
     * Closes all the opened <code>PreparedStatements</code>
     */
    public void closePreparedStatements()
    {
        PreparedStatement temp;

        while (! preparedStatements.isEmpty())
        {
            temp = this.preparedStatements.removeLast();
            try
            {
                temp.close();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the closing of a prepared statement!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
        }
    }


  /**
   * Closes all the useless open things (connection, statements, prepared statements, the door behind you, ...)
   */
  public void closeAll()
  {
    closeStatements();
    closePreparedStatements();
    closeConnection();
  }


  /**
   * Performes an SQL request using the Statement in parameter
   * @param Statement object already created
   * @param String SQL query
   * @return ResultSet result of the query
   */
  public ResultSet request(Statement stmt, String query)
  {
    try
    {
      logger.debug("SQL query: " + query);
      return stmt.executeQuery(query);
    }
    catch (SQLException e)
    {
        logger.error("Error during the execution of a query!");
        logger.error("Query: " + query);
        logger.error("SQL Exception raised: " + e.getMessage());
        return null;
    }
  }


  /**
   * Performes an SQL update request using the Statement in parameter
   * @param Statement object already created
   * @param String SQL query
   * @return ResultSet rsult of the query
   */
  public int requestUpdate(Statement stmt, String query)
  {
    try
    {
      logger.debug("SQL update query: " + query);
      return stmt.executeUpdate(query);
    }
    catch (SQLException e)
    {
        logger.error("Error during the execution of an update query!");
        logger.error("Query: " + query);
        logger.error("SQL Exception raised: " + e.getMessage());
        return 0;
    }
  }


  /**
   * Counts the number of rows in a ResultSet (like 'getColumnCount()', via 'getMetaData()', for the columns)
   * @param ResultSet object already created
   * @return int number of rows in the ResultSet object
   */
  public static int getRowCount(ResultSet data)
  {
    int result = -1;

    try
    {
      data.last();
      result = data.getRow();
      data.first();
    }
    catch (SQLException e)
    {
      logger.error("An exception occurred while counting the number of rows in a ResultSet!");
      logger.error("SQLException raised: " + e.getMessage());
    }

    return result;
  }
    
    
    /**
     * Sets the connection's auto-commit mode to the given state.
     * 
     * @param bool boolean (true or false) representing the wanted state for the AutoCommit feature
     */
    public void setAutoCommit(boolean bool)
    {
        try
        {
            this.connection.setAutoCommit(bool);
        }
        catch (SQLException e)
        {
            logger.error("Error during the setting at '" + bool + "' of the auto-commit feature!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
    }
    
    
    /**
     * Makes all changes made since the previous commit/rollback permanent and releases any database locks currently
     * held by the connection. This method should be used only when auto-commit mode has been disabled (with the
     * 'setAutoCommit' method).
     */
    public void commit()
    {
        try
        {
            this.connection.commit();
        }
        catch (SQLException e)
        {
            logger.error("Error during the the commit of the connection!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
    }
}
