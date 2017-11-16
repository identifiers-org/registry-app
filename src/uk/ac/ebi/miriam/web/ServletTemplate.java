/*
 * MIRIAM Resources (Web Application)
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2011  BioModels.net (EMBL - European Bioinformatics Institute)
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


package uk.ac.ebi.miriam.web;


import uk.ac.ebi.miriam.db.DbServer;
import uk.ac.ebi.miriam.tools.PropertyLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;


/**
 * <p>Template Servlet which includes all the needed stuff for connection and usage of the database pool.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2011  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20111019
 */
public abstract class ServletTemplate extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -7826471451672444155L;
    protected Logger logger = Logger.getLogger(ServletTemplate.class);
    private DataSource dataSource = null;
    private String poolName = null;
    protected Connection connection = null;
    
    
    /**
     * Default constructor.
     */
    public ServletTemplate()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        boolean failure = false;
        Context initContext = null;
        Context envContext = null;
        
        // retrieves some info from the miriam.properties file
        PropertyLoader pl = new PropertyLoader();
        Properties properties  = pl.getMIRIAMProperties();
        this.poolName = properties.getProperty("database_pool");
        properties.clear();   // a bit of cleaning
        
        // Initialisation of the connection
        try
        {
            initContext = new InitialContext();
        }
        catch (NamingException e)
        {
            logger.error("Connection to the database pool: failure, no initial context!");
            logger.error("NamingException: " + e.getMessage());
            failure = true;
        }
        
        if (null == initContext)
        {
            logger.error("Connection to the database pool: failed to retrieve the initial context.");
            failure = true;
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
                failure = true;
            }
            
            if (null == envContext)
            {
                logger.error("Connection to the database pool: failed to retrieve the environment context.");
                failure = true;
            }
            else
            {
                try
                {
                    // look up a DataSource, which represents a connection pool
                    this.dataSource = (DataSource) envContext.lookup("jdbc/" + this.poolName + DbServer.getDbServerText());
                }
                catch (NamingException e)
                {
                    logger.error("Connection to the database pool '" + this.poolName + DbServer.getDbServerText()+ "': failure, no data source!");
                    logger.error("NamingException: " + e.getMessage());
                    failure = true;
                }
                
                if (null == this.dataSource)
                {
                    logger.error("Connection to the database pool '" + this.poolName + "': failed to retrieve the data source.");
                    failure = true;
                }
            }
        }
        
        // failure to setup the connection to the database pool
        if (failure)
        {
            logger.error("Connection to the database pool: failed.");
            throw new ServletException("Failure to connect to the database pool!");
        }
    }
    
    
    /**
     * javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        RequestDispatcher view = null;
        StringBuilder jsp = new StringBuilder();
        
        try
        {
            synchronized (this.dataSource)
            {
              this.connection = this.dataSource.getConnection();
            }
        }
        catch (Exception e)
        {
            logger.error("Unable to open a database connection!");
            logger.error("Exception: " + e.getMessage());
            logger.error("StackTrace: " + e.getStackTrace().toString());
        }
        
        if (null == this.connection)
        {
            logger.error("Unable to get a connection from the database pool!");
        }
        else
        {
            
            // custom work performed by the Servlet (implemented in the inherited class)
            execute(request, response, jsp);
            
        }
        
        try
        {
            if (null != null)
            {
                this.connection.close();   // returns the connection to the pool
            }
        }
        catch (Exception e)
        {
            logger.warn("Unable to close opened statements and/or database connection!");
            logger.error("Exception: " + e.getMessage());
        }
        
        // sends the data back to the 'view'
        view = request.getRequestDispatcher(jsp.toString());
        view.forward(request, response);
    }
    
    
    /**
     * Work needed to be performed by the Servlet.
     * The object 'connection' can be used there to perform SQL queries.
     */
    protected abstract void execute(HttpServletRequest request, HttpServletResponse response, StringBuilder page);
    //protected abstract void execute(String jsp, String message, Map<String, Object> data);
    
    
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
                statement.close();   // all associated ResultSet will be closed as well
            }
            catch (SQLException e)
            {
                logger.debug("Unable to close a SQL Statement!");
                logger.debug("SQLException raised: " + e.getMessage());
            }
        }
    }
    
    
    /**
     * Closes a SQL <code>PreparedStatement</code>.
     * @param preparedStatement
     */
    public void closePreparedStatement(PreparedStatement preparedStatement)
    {
        if (null != preparedStatement)
        {
            try
            {
                preparedStatement.close();
            }
            catch (SQLException e)
            {
                logger.debug("Unable to close a SQL PreparedStatement!");
                logger.debug("SQLException raised: " + e.getMessage());
            }
        }
    }
    
    
    /**
     * Getter
     * @return poolName
     */
    public String getPoolName()
    {
        return this.poolName;
    }
}
