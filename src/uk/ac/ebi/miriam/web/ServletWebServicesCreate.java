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


package uk.ac.ebi.miriam.web;


import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.ServicesDao;
import uk.ac.ebi.miriam.db.WebService;

import java.io.IOException;
import java.util.GregorianCalendar;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;


/**
 * <p>
 * Servlet for the creation of a single Web Services record.
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
 * @version 20100628
 */
public class ServletWebServicesCreate extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -6107266613365198048L;
    private Logger logger = Logger.getLogger(ServletWebServicesCreate.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletWebServicesCreate()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String message = null;
        RequestDispatcher view = null;
        String jsp;
        boolean stored = false;   // if set to 'true', the new Web Service record has been directly stored in the database (only for curators)
        boolean error = false;   // set to 'true' is an error happened
        
        // retrieves all the parameters
        String datatypeId = request.getParameter("dataTypeId");
        String type = request.getParameter("type");
        String provider = request.getParameter("provider");
        String desc = request.getParameter("desc");
        String endpoint = request.getParameter("endpoint");
        String wsdl = request.getParameter("wsdl");
        String doc = request.getParameter("doc");
        
        // basic tests for requested information
        if ((MiriamUtilities.isEmpty(datatypeId)) || (MiriamUtilities.isEmpty(type)) || (MiriamUtilities.isEmpty(provider)) || (MiriamUtilities.isEmpty(desc)) || (MiriamUtilities.isEmpty(endpoint)) || (MiriamUtilities.isEmpty(doc)) || ((type.equalsIgnoreCase("SOAP")) && (MiriamUtilities.isEmpty(wsdl))))
        {
            message = "Incomplete information: please fill in all necessary fields!";
            jsp = "/mdb?section=edit_ws&id=" + datatypeId;
        }
        else
        {
            datatypeId = datatypeId.trim();
            type = type.trim();
            provider = provider.trim();
            desc = desc.trim();
            endpoint = endpoint.trim();
            doc = doc.trim();
            if (null != wsdl)   // parameter not mandatory
            {
                wsdl = wsdl.trim();
            }
            
            // retrieves the user logged (if any)
            HttpSession session = request.getSession();
            
            // the user is logged
            if (MiriamUtilities.isUserAuthorised(session))
            {
                // the user has curation privileges
                if (MiriamUtilities.isUserCurator(session))
                {
                    // retrieves the name of the database pool
                    String poolName = getServletContext().getInitParameter("miriam_db_pool");
                    
                    // database connection
                    ServicesDao dao = new ServicesDao(poolName);
                    DataTypeDao dataTypeDao = new DataTypeDao(poolName);
                    
                    // creates new WebService object
                    WebService service = new WebService();
                    service.setResId(provider);
                    service.setType(type);
                    service.setDesc(desc);
                    service.setEndpoint(endpoint);
                    service.setWsdl(wsdl);
                    service.setDoc(doc);
                    
                    // creates the new Web Services record
                    if (dao.createWSrecord(service))
                    {
                        // updates the last modification date of the corresponding data type
                        if (dataTypeDao.updateLastModifDate(datatypeId))
                        {
                            // success
                            logger.info("Successful creation of a new record of " + type + " Web Services, provided by " + provider + ".");
                            message = "The new record of a " + type + " Web Services, provided by " + provider + ", has been successfully created!";
                        }
                        else
                        {
                            logger.error("A new " + type + " Web Service record, provided by " + provider + ", has been created but the 'last modified date' of the related data type (" + datatypeId + ") has not been updated!");
                            message = "A new " + type + " Web Service record, provided by " + provider + ", has been created but the 'last modified date' of the related data type (" + datatypeId + ") has not been updated!";
                            error = true;
                        }
                    }
                    else   // failure to create the new record of the Web Service
                    {
                        logger.error("Unable to create a new record of " + type + " Web Services, provided by " + provider + "!");
                        message = "Unable to create a new record of " + type + " Web Services, provided by " + provider + "!";
                        error = true;
                    }
                    
                    // cleaning
                    dao.clean();
                    dataTypeDao.clean();
                    
                    stored = true;
                }
                else   // user hasn't enough privileges
                {
                    // no direct storage in database: all the information is sent to the curator(s)
                    message = "Thank you for your contribution to MIRIAM Resources! Your submission has been recorded and will shortly be processed by our curators.";
                }
            }
            else   // user not logged
            {
                // no direct storage in database: all the information is sent to the curator(s)
                message = "Thank you for your contribution to MIRIAM Resources! Your submission has been recorded and will shortly be processed by our curators.";
            }
            
            // retrieves the version of the web application (sid, alpha, demo or main)
            String version = getServletContext().getInitParameter("version");
            
            // retrieves the email addresses of the administrator and the curators
            String emailAdmin = getServletContext().getInitParameter("admin.email");
            String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
            
            // the new record has been stored in the database
            if ((! error) && stored)
            {
                StringBuilder emailBody = new StringBuilder();
                
                emailBody.append("The following Web Services record has been created!\n");
                emailBody.append("Here is the new information stored:");
                emailBody.append("\n\n- Provider (resource): " + provider + " (associated with the data type " + datatypeId + ")");
                emailBody.append("\n- Type:          " + type);
                emailBody.append("\n- Description:   " + desc);
                emailBody.append("\n- Endpoint:      " + endpoint);
                emailBody.append("\n- WSDL location: " + wsdl);
                emailBody.append("\n- Documentation: " + doc);
                GregorianCalendar cal = new GregorianCalendar();
                emailBody.append("\n\nDate: " + cal.getTime());
                emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdmin, emailsCura, "[MIRIAM-" + version + "] New " + type + " Web Services, provided by " + provider, emailBody.toString(), "text/plain");
            }
            
            // the new record is only sent by email (anonymous user)
            if ((! error) && (! stored))
            {
                StringBuilder emailBody = new StringBuilder();
                
                emailBody.append("The following new Web Services record has been submitted and is now pending (no change committed so far)!\n");
                emailBody.append("Please review the provided information and, if necessary, add the new Web Service:");
                emailBody.append("\n\n- Provider (resource): " + provider + " (associated with the data type " + datatypeId + ")");
                emailBody.append("\n- Type:          " + type);
                emailBody.append("\n- Description:   " + desc);
                emailBody.append("\n- Endpoint:      " + endpoint);
                emailBody.append("\n- WSDL location: " + wsdl);
                emailBody.append("\n- Documentation: " + doc);
                GregorianCalendar cal = new GregorianCalendar();
                emailBody.append("\n\nDate: " + cal.getTime());
                emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdmin, emailsCura, "[MIRIAM-" + version + "] Request for new " + type + " Web Services record, provided by " + provider, emailBody.toString(), "text/plain");
            }
            
            // an error happened: we inform the admin
            if (error)
            {
                StringBuilder emailBody = new StringBuilder();
                
                emailBody.append("Something went horribly wrong when trying to store the following new Web Services record!\n");
                emailBody.append("Please check the state of the database and the logs in order to solve this issue.");
                emailBody.append("\n\n- Provider (resource): " + provider + " (associated with the data type " + datatypeId + ")");
                emailBody.append("\n- Type:          " + type);
                emailBody.append("\n- Description:   " + desc);
                emailBody.append("\n- Endpoint:      " + endpoint);
                emailBody.append("\n- WSDL location: " + wsdl);
                emailBody.append("\n- Documentation: " + doc);
                GregorianCalendar cal = new GregorianCalendar();
                emailBody.append("\n\nDate: " + cal.getTime());
                emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdmin, "[MIRIAM-" + version + "] ERROR when submitting new " + type + " Web Services record!", emailBody.toString(), "text/plain");
            }
            
            jsp = "/mdb?section=webservices&id=" + datatypeId;
        }
        
        if (null != message)
        {
            request.setAttribute("message", "<p>" + message + "</p>");
        }
        view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
}
