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
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;


/**
 * <p>
 * Manages the connection to a database (with pooling).
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
public class DbPoolConnect extends DbConnection
{
    private Logger logger = Logger.getLogger(DbPoolConnect.class);
    private String poolName = new String();
    
    
    /**
     * Constructor
     */
    public DbPoolConnect(String poolName)
    {
        this.poolName = poolName;
    }
    
    
    /**
     * Recover a connection from the pool
     */
    public void newConnection()
    {
        Context initContext = null;
        Context envContext = null;
        DataSource dataSource = null;
        Connection connection = null;
        
        try
        {
            // initalisation of the connection
            try
            {
                initContext = new InitialContext();
            }
            catch (NamingException e)
            {
                logger.error("Connection to the database pool: failure, no initial context!");
                logger.error("NamingException: " + e.getMessage());
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
                    logger.error("NamingException: " + e.getMessage());
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
                        dataSource = (DataSource) envContext.lookup("jdbc/" + this.poolName+ DbServer.getDbServerText());
                    }
                    catch (NamingException e)
                    {
                        logger.error("Connection to the database pool: failure, no data source!");
                        logger.error("NamingException: " + e.getMessage());
                    }
                    
                    if (null == dataSource)
                    {
                        logger.error("Connection to the database pool: failed to retrieve the data source.");
                    }
                }
            }
            
            synchronized (dataSource)
            {
                connection = dataSource.getConnection();
            }
            
            if (null == connection)
            {
                logger.error("Unable to get a connection from the database pool '" + this.poolName + "'!");
            }
            else
            {
                setConnection(connection);
            }
            
            //setConnection(DriverManager.getConnection("jdbc:apache:commons:dbcp:" + poolName));
            //logger.debug("Successful recovery of a connection from the pool");
        }
        catch (SQLException e)
        {
            logger.error("Cannot open the database connection from the pool '" + poolName + "'!");
            logger.error("SQLException raised: " + e.getMessage());
        }
    }
}
