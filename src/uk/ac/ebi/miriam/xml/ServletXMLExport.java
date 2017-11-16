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
import uk.ac.ebi.miriam.web.MiriamUtilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;


/**
 * <p>
 * Servlet for the export of the whole MIRIAM DataBase in an XML file (interface part, not business part).
 * WARNING: this is the old way to get the export: by submitting a form. See 'ServletInstantXmlExport' for the instant export accessible with a GET request.
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
 * @version 20130801
 */
public class ServletXMLExport extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
  private static final long serialVersionUID = 9134086517606902726L;
  private Logger logger = Logger.getLogger(ServletXMLExport.class);
  private static Object lock = new Object();   /* lock used to protect concurrent access to critical section */


  /*
   * Constructor
   */
  public ServletXMLExport()
  {
    super();
  }

  /*
   * Answering to the requests (via html forms)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String path = new String();
    String version = new String();
    String fileName = new String();
    String poolName = new String();
    String shortName = new String();
    ServletOutputStream stream = null;
    BufferedInputStream buf = null;
    String realName = null;
    
    path = getServletContext().getInitParameter("exportDir");
    version = getServletContext().getInitParameter("version");
    Date now = new Date();
    String nowStr = DatetimeProcessor.instance.formatToW3CDTF(now);
    
    // where is this app running: tomcat-11 or tomcat-12?
    // this is useful because it is impossible to do synchronisation between servers
    InetAddress localMachine = InetAddress.getLocalHost();
    String hostName = localMachine.getHostName();   // will return 'tomcat-11.ebi.ac.uk' or 'tomcat-12.ebi.ac.uk'
    hostName = hostName.substring(0, hostName.indexOf("."));   // we only keep the 'tomcat-11' or 'tomcat-12' part
    
    // generates the temporary file name
    realName = "Identifiers-org_Registry-" + version + "_" + hostName + "_" + nowStr + ".xml";
    
    // recovery of the parameters
    shortName = request.getParameter("getNameParam");
    
    // the name given is not proper (full of spaces)
    if (MiriamUtilities.isEmpty(shortName))
    {
        shortName = "IdentifiersOrg-Registry_" + nowStr;
    }
    // add the extension '.xml' if it doesn't already exist
    if (shortName.indexOf(".xml") == -1)
    {
    	shortName += ".xml";
    }
    
    // the actual generated (and temporary) data
    if ((path.charAt(path.length() - 1)) == File.separatorChar)   // checks that the path contains the final path separator character
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
    Miriam2XML dump = null;
    // critical section to protected against concurrent access (in order to not mess up the export file)
    synchronized(lock)
    {
        dump = new Miriam2XML(poolName, fileName);
    }
    if ((null != dump) && (dump.export(null)))
    {
    	// everything's ok
    }
    else
    {
    	logger.error("Unable to generate an XML export of Identifiers.org's Registry!");
    }

    // sends the file generated
    try
    {
    	stream = response.getOutputStream();
      	File xml = new File(fileName);
      	// set response headers
      	response.setContentType("text/xml");
      	response.setCharacterEncoding("UTF-8");
      	response.addHeader("Content-Disposition", "attachment; filename=" + shortName);
      	response.setContentLength((int) xml.length());
      	FileInputStream input = new FileInputStream(xml);
      	buf = new BufferedInputStream(input);
      	int readBytes = 0;
      	// read from the file and write to the servletOutputStream
      	while ((readBytes = buf.read()) != -1)
      	{
      		stream.write(readBytes);
      	}
    }
    catch (IOException e)
    {
    	throw new ServletException(e.getMessage());
    }
    finally
    {
    	// close the input/output streams
    	if (stream != null)
    	{
    		stream.close();
    	}
    	if (buf != null)
    	{
    		buf.close();
    	}
    }
  }
}
