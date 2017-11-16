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


package uk.ac.ebi.miriam.xml;


import uk.ac.ebi.compneur.util.DatetimeProcessor;
import uk.ac.ebi.miriam.db.MyMiriamDao;
import uk.ac.ebi.miriam.db.DataTypeDao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import uk.ac.ebi.miriam.db.Profile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * <p>Servlet that handles the generation of the XML export on the fly for the whole Registry.
 * <p>The export is available via a simple HTTP GET request.
 * <p>Optional parameters: project and key for project specific exports (cf. MyMIRIAM feature).
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2013 BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20130808
 */
public class ServletInstantXmlExport extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -5526815895316177191L;
    private Logger logger = Logger.getLogger(ServletInstantXmlExport.class);
    private static Object lock = new Object();   /* lock used to protect concurrent access to critical section */
    private static final Integer BUFFER_SIZE = 10240;   // 10KB
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String path = new String();
        String version = new String();
        String fileName = new String();
        String realName = null;
        String project = null;
        String key = null;
        //String hashKey = null;
        
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        // retrieves potential parameters from the URL
        project = request.getParameter("project");
        key = request.getParameter("key");   // no need to URL decode it, the server already does it
        
        // request for a project specific export
        if ((null != project) && (!project.matches("\\s*")) )
        {
            // access to the database
            MyMiriamDao myMiriamDao = new MyMiriamDao(poolName);
            // checks that the project exists
            if (myMiriamDao.isProfileExisting(project))
            {
                Profile profile = myMiriamDao.getProfile(project);
                if(!profile.isOpenAccess() && (null != key) && (!key.matches("\\s*"))) {
                    // checks that the encoded key to access the info of this project is correct
                    if (myMiriamDao.accessCheck(project, key)) {
                        logger.info("Custom export requested: " + project);
                    } else {
                        logger.warn("Attempt to access the customised XML export for '" + project + "' with an incorrect key.");
                        project = null;
                    }
                }
            }
            else   // the project does not exist: puts 'project' back to null
            {
                project = null;
            }
        }
        else
        {
            project = null;   // in case the key was null but not the project
        }
        
        path = getServletContext().getInitParameter("exportDir");
        version = getServletContext().getInitParameter("version");
        Date now = new Date();
        String nowStr = DatetimeProcessor.instance.formatToW3CDTF(now);
        
        // where is this app running: tomcat-11 or tomcat-12?
        // this is useful because it is impossible to do synchronization between servers
        InetAddress localMachine = InetAddress.getLocalHost();
        String hostName = localMachine.getHostName();   // will return 'tomcat-11.ebi.ac.uk' or 'tomcat-12.ebi.ac.uk'
        if(hostName.indexOf(".")!= -1)
            hostName = hostName.substring(0, hostName.indexOf("."));   // we only keep the 'tomcat-11' or 'tomcat-12' part
        
        // checks that the path contains the final path separator character
        if ((path.charAt(path.length() - 1)) == File.separatorChar)
        {
            fileName = path + realName;
        }
        else   // no final separator
        {
            fileName = path + File.separator + realName;
        }
        
        // processing the request
        Miriam2XML dump = null;
        Boolean successfulDump = false;
        // critical section to protected against concurrent access (in order to not mess up the export file)
        // TODO: have a proper cache solution: this slows down a lot concurrent requests
        synchronized(lock)
        {
        	// generates the temporary file name
            realName = "Identifiers-org_Registry-" + version + "_" + hostName + "_" + nowStr + ".xml";
            
            dump = new Miriam2XML(poolName, fileName);
            
            if (null != dump)
            {
            	successfulDump = dump.export(project);
            }
        }
        
        if ((null != dump) && (successfulDump))
        {
        	File generatedExport = new File(fileName);
        	
            // sets the headers for the response
        	response.reset();
            response.setBufferSize(BUFFER_SIZE);
            response.setContentType("application/xml");   //"text/xml"
            response.setCharacterEncoding("UTF-8");
            response.setContentLength((int)generatedExport.length());
            if (null != project)   // customised export
            {
                //response.setHeader("Content-Disposition", "attachment; filename=Resources_" + project + ".xml");
                response.setHeader("Content-Disposition", "inline; filename=IdentifiersOrg-Registry[" + project + "]_" + nowStr + ".xml");
            }
            else    // non customised export
            {
                //response.setHeader("Content-Disposition", "attachment; filename=Resources_all.xml");   // puts unicode for all special characters
                response.setHeader("Content-Disposition", "inline; filename=IdentifiersOrg-Registry_" + nowStr + ".xml");
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Last-Modified",getLastModifiedDate());

            // reads the generated file and sends the data back to the user
            BufferedInputStream input = null;
            BufferedOutputStream output = null;
            try
            {
                input = new BufferedInputStream(new FileInputStream(generatedExport), BUFFER_SIZE);
                output = new BufferedOutputStream(response.getOutputStream(), BUFFER_SIZE);
                byte[] buffer = new byte[BUFFER_SIZE];
                int data = 0;
                while ((data = input.read(buffer)) > 0)
                {
                    output.write(buffer, 0, data);
                }
            }
            finally
            {
                close(output);
                close(input);
            }
        }
        else
        {
            logger.error("Unable to generate an XML export of MIRIAM Resources!");
            
            // sends back an error message to the user
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.print("Unable to generate an XML export of the Registry!");
        }
    }

    private String getLastModifiedDate(){
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        DataTypeDao dao = new DataTypeDao(poolName);
        Date lastUpdateDate = dao.getLastModifDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(lastUpdateDate);
    }
    
    
    /**
     * Closes a resource.
     * @param resource
     */
    private void close(Closeable resource)
    {
        if (resource != null)
        {
            try
            {
                resource.close();
            }
            catch (IOException e)
            {
            	logger.warn("IOException raised while closing a resource.");
            	logger.warn(e.getMessage());
            }
        }
    }
}
