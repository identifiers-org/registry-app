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
import org.apache.log4j.Logger;


/**
 * <p>
 * Manages the connection to a database (without pooling)
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
 * @version 20090129
 */
public class DbSimpleConnect extends DbConnection
{
  /** logger for debug and information */
  private Logger logger = Logger.getLogger(DbSimpleConnect.class);
  /** name of the driver used for the database connection */
  private String driver = null;
  /** type of the database: 'mysql' for example */
  private String type = null;
  /** name or IP address of the server where the DataBase Management System is running */
  private String server = null;
  /** port used by the DBMS */
  private String port = null;
  /** name of the database */
  private String name = null;
  /** username used for the connection */
  private String user = null;
  /** password used for the connection */
  private String pass = null;


  /**
   * Default constructor
   * <p>
   * WARNING: the user of this constructor needs to setup the connection settings with the setters!
   */
  public DbSimpleConnect()
  {
    logger.debug("New direct connection to a database (settings to setup!)");
    // nothing here, but the user needs to use the setters to setup the settings!
  }


  /**
   * Constructor
   * @param driver name of the driver used for the database connection
   * @param type type of the database: 'mysql' for example
   * @param server name or IP address of the server where the DataBase Management System is running
   * @param port port used by the DBMS
   * @param name name of the database
   * @param user username used for the connection
   * @param pass password used for the connection
   */
  public DbSimpleConnect(String driver, String type, String server, String port, String name, String user, String pass)
  {
    logger.debug("New direct connection to the database: " + name);
    this.driver = driver;
    this.type = type;
    this.server = server;
    this.port = port;
    this.name = name;
    this.user = user;
    this.pass = pass;
  }


  /**
   * Creates a new connection
   * @return Connection object
   */
  public void newConnection()
  {
    // Load the JDBC driver
      try
      {
          Class.forName(driver);
      }
      catch (ClassNotFoundException e)
      {
        logger.error("Cannot load the database driver!");
        logger.error("ClassNotFound Exception raised: " + e.getMessage());
      }

      // Connect to the database
    try
        {
      setConnection(DriverManager.getConnection(type + "://" + server + ":" + port + "/" + name, user, pass));
    }
        catch (SQLException e)
        {
          logger.error("Cannot open the database connection!");
          logger.error("SQL Exception raised: " + e.getMessage());
    }
        logger.debug("simple database connection: OK");
  }


  /**
   *
   * @return
   */
  public String getDriver()
  {
    return driver;
  }


  /**
   *
   * @param driver
   */
  public void setDriver(String driver)
  {
    this.driver = driver;
  }


  /**
   *
   * @return
   */
  public String getName()
  {
    return name;
  }


  public void setName(String name)
  {
    this.name = name;
  }


  public String getPass()
  {
    return pass;
  }


  public void setPass(String pass)
  {
    this.pass = pass;
  }


  public String getPort()
  {
    return port;
  }


  public void setPort(String port)
  {
    this.port = port;
  }


  public String getServer()
  {
    return server;
  }


  public void setServer(String server)
  {
    this.server = server;
  }


  public String getType()
  {
    return type;
  }


  public void setType(String type)
  {
    this.type = type;
  }


  public String getUser()
  {
    return user;
  }


  public void setUser(String user)
  {
    this.user = user;
  }



}
