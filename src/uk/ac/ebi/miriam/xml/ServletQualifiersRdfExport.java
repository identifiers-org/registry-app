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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import uk.ac.ebi.compneur.util.DatetimeProcessor;


/**
 * <p>Servlet that handles the generation of the RDF/XML export on the fly of the BioModels.net qualifiers.
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
 * @version 20090521
 */
public class ServletQualifiersRdfExport extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -2709073815275470580L;
    private Logger logger = Logger.getLogger(ServletQualifiersRdfExport.class);
    private static Object lock = new Object();   /* lock used to protect concurrent access to critical section */
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String path = new String();
        String version = new String();
        String fileName = new String();
        String poolName = new String();
        String realName = null;
        
        path = getServletContext().getInitParameter("exportDir");
        version = getServletContext().getInitParameter("version");
        
        // where is this app running: tomcat-11 or tomcat-12?
        // this is useful because it is impossible to do synchronisation between servers
        InetAddress localMachine = InetAddress.getLocalHost();
        String hostName = localMachine.getHostName();   // will return 'tomcat-11.ebi.ac.uk' or 'tomcat-12.ebi.ac.uk'
        hostName = hostName.substring(0, hostName.indexOf("."));   // we only keep the 'tomcat-11' or 'tomcat-12' part
        
        // generates the temporary file name
        realName = "BioModels_net_Qualifiers-" + version + "_" + hostName + "_" + DatetimeProcessor.instance.formatToW3CDTF(new Date()) + ".rdf";
        
        // checks that the path contains the final path separator character
        if ((path.charAt(path.length() - 1)) == File.separatorChar)
        {
            fileName = path + realName;
        }
        else   // no final separator
        {
            fileName = path + File.separator + realName;
        }
        
        // retrieves the name of the pool
        poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        // processing the request
        Qualifiers2RDF export = null;
        
        // critical section to protected against concurrent access (in order to not mess up the export file)
        synchronized(lock)
        {
            export = new Qualifiers2RDF(poolName);
        }
        
        if ((null != export) && (export.export(fileName)))
        {
            // reads the generated file and sends the data back to the user
            StringBuilder fileData = new StringBuilder(1000);
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead=reader.read(buf)) != -1)
            //while (reader.read(buf) != -1)
            {
                fileData.append(buf, 0, numRead);
                //out.print(buf);
            }
            reader.close();
            
            response.setContentType("rdf+xml");
            response.setCharacterEncoding("UTF-8");
            response.setContentLength(fileData.length());
            PrintWriter out = response.getWriter();
            out.print(fileData.toString());
            
            // cleaning
            fileData = null;
        }
        else
        {
            logger.error("Unable to generate the RDF/XML export of the BioModels.net qualifiers!");
            
            // sends back an error message to the user
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("Unable to generate the RDF/XML export of the BioModels.net qualifiers!");
        }
    }
}
