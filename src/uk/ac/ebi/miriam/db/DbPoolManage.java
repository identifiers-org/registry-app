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


import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;


/**
 * <p>
 * Creates and manages the connection pooling.
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
 * @version 20060616
 */
public class DbPoolManage
{
  private Logger logger = Logger.getLogger(DbPoolManage.class);
  private String dbDriver = new String();
  private String dbType = new String();
  private String dbServer = new String();
  private String dbPort = new String();
  private String dbName = new String();
  private String dbUser = new String();
  private String dbPass = new String();
  private String dbPool = new String();


  /**
   * Constructor: create a database pool
   * @param String name of the driver
   * @param String type of the driver (used for the access: 'jdbc:mysql' for example)
   * @param String name of the server (or IP address)
   * @param String number of the TCP port used
   * @param String name of the database
   * @param String name of the user account
   * @param String password of the account
   * @param String name of the pool that will be created, and access (in the future) via 'conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:" + getDbPool());'
   */
  public DbPoolManage(String dbDriver, String dbType, String dbServer, String dbPort, String dbName, String dbUser, String dbPass, String dbPool)
  {
    // recovery of all the parameters needed
    this.dbDriver = dbDriver;
    this.dbType = dbType;
    this.dbServer = dbServer;
    this.dbPort = dbPort;
    this.dbName = dbName;
    this.dbUser = dbUser;
    this.dbPass = dbPass;
    this.dbPool = dbPool;

    // Loading the underlying JDBC driver
        logger.debug("Loading underlying JDBC driver...");
        try
        {
          Class.forName(dbDriver);
        }
        catch (ClassNotFoundException e)
        {
          logger.error("Cannot load the database driver!");
        logger.error("ClassNotFound Exception raised: " + e.getMessage());
        }
        logger.debug("JDBC driver loaded.");

        // Setting up and registering the PoolingDriver
        logger.debug("Setting up driver...");
        try
        {
          setupDriver(dbType + "://" + dbServer + ":" + dbPort + "/" + dbName + "?user=" + dbUser + "&password=" + dbPass);
        }
        catch (Exception e)
        {
          logger.error("Cannot create the pool!");
        logger.error("Exception raised: " + e.getMessage());
        }
        logger.info("Connection pool now setup!");
  }


  /**
   * Close the database pool
   */
  public void close()
  {
    PoolingDriver driver;

    try
    {
      driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");
      driver.closePool(dbPool);
    }
    catch (SQLException e)
    {
      logger.error("Error during the closing of the connection!");
        logger.error("SQL Exception raised: " + e.getMessage());
    }
  }


  /*
   * This is the end of this beautiful pool (snif)
   */
  protected void finalize() throws Throwable
  {
    // close the database pool
    close();

    // always do that
      super.finalize();
  }


  /**
   * Getter
   * @return String name of the driver
   */
  public String getDbDriver()
  {
    return dbDriver;
  }


  /**
   * Getter
   * @return String name of the database
   */
  public String getDbName()
  {
    return dbName;
  }


  /**
   * Getter
   * @return String password of the account
   */
  public String getDbPass()
  {
    return dbPass;
  }


  /**
   * Getter
   * @return String name of the pool (after creation, access via 'conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:" + getDbPool());')
   */
  public String getDbPool()
  {
    return dbPool;
  }


  /**
   * Getter
   * @return String port used for the connection to the database
   */
  public String getDbPort()
  {
    return dbPort;
  }


  /**
   * Getter
   * @return String name (or IP address) of the server where the database management system is installed
   */
  public String getDbServer()
  {
    return dbServer;
  }


  /**
   * Getter
   * @return String type of the database (in order to create the URL used for the access), something like 'jdbc:mysql'
   */
  public String getDbType()
  {
    return dbType;
  }


  /**
   * Getter
   * @return String name of the user account
   */
  public String getDbUser()
  {
    return dbUser;
  }


  /*
   * Setup of the connection pool
   */
  private void setupDriver(String connectURI) throws Exception
  {
        // First, we'll need a ObjectPool that serves as the
        // actual pool of connections.
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        ObjectPool connectionPool = new GenericObjectPool(null);

        // Next, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, null);

        // Now we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);

    // Finally, we create the PoolingDriver itself...
        Class.forName("org.apache.commons.dbcp.PoolingDriver");
        PoolingDriver driver = (PoolingDriver) DriverManager.getDriver("jdbc:apache:commons:dbcp:");

        // ...and register our pool with it.
        driver.registerPool(dbPool, connectionPool);

        // Now we can just use the connect string "jdbc:apache:commons:dbcp:POOL_NAME"
        // to access our pool of Connections.
    }

}
