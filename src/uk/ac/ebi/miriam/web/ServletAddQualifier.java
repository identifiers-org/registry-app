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


package uk.ac.ebi.miriam.web;


import uk.ac.ebi.miriam.db.Qualifier;
import uk.ac.ebi.miriam.db.QualifiersDao;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;


/**
 * <p>
 * Servlet for the addition of new BioModels.net qualifiers (one at a time).
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
 * @version 20130704
 */
public class ServletAddQualifier extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -2838458206858299583L;
    private Logger logger = Logger.getLogger(ServletAddQualifier.class);
    

    /*
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletAddQualifier()
    {
        super();
    }
    
    
    /*
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String message = null;
        String jsp;
        
        // retrieves the user logged who asked for the action
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("login");
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(session))
            {
                // retrieves the parameters from the form
                String type = request.getParameter("type");
                String name = request.getParameter("name");
                String def = request.getParameter("def");
                
                // retrieves the name of the database pool
                String poolName = getServletContext().getInitParameter("miriam_db_pool");
                
                // connection to the database
                QualifiersDao dao = new QualifiersDao(poolName);
                
                // checks that there is some useful information provided 
                if ((null != name) && (null != type) && (null != def) && (! name.matches("\\s*")) && (! type.matches("\\s*")) && (! def.matches("\\s*")))
                {
                    name = name.trim();
                    type = type.trim();
                    def = def.trim();
                    
                    Qualifier qualifier = new Qualifier(name, def, type);
                    
                    // checks if the given type exists (just in case...)
                    List<String> existingTypes = dao.getExistingTypes();
                    if (existingTypes.contains(type))
                    {
                        // test if the qualifiers doesn't already exist
                        if (! dao.isExistingQualifier(qualifier))
                        {
                            boolean success = dao.addQualifier(qualifier);
                            
                            if (success)
                            {
                                message = "The qualifier '" + name + "' (" + type + ") has been added to the list of BioModels.net qualifiers!";
                                logger.debug("The qualifier '" + name + "' (" + type + ") has been added to the list of BioModels.net qualifiers!");
                            }
                            else
                            {
                                message = "An error happened while adding the qualifier '" + name + "' (" + type + ") to the list of BioModels.net qualifiers!";
                                logger.error("An error happened while adding the qualifier '" + name + "' (" + type + ") to the list of BioModels.net qualifiers!");
                            }
                            
                            // retrieves the version of the web application (sid, alpha, main or demo)
                            String version = getServletContext().getInitParameter("version");
                            
                            // retrieves the email addresses of the administrator and the curators
                            String emailAdmin = getServletContext().getInitParameter("admin.email");
                            String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
                            
                            // email notification
                            StringBuilder emailBody = new StringBuilder();
                            if (success)
                            {
                                emailBody.append("\nA new BioModels.net qualifier has been successfully created!");
                            }
                            else
                            {
                                emailBody.append("\nAn error happened while creating a new BioModels.net qualifier!");
                                emailBody.append("\nPlease have a look at the logs to understand what happened...");
                            }
                            emailBody.append("\n\nName: " + name);
                            emailBody.append("\nType: " + type);
                            emailBody.append("\nDefinition: " + def);
                            emailBody.append("\n\nUser: " + user);
                            GregorianCalendar cal = new GregorianCalendar();
                            emailBody.append("\nDate: " + cal.getTime());
                            emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Resources\nhttp://www.ebi.ac.uk/miriam/");
                            MailFacade.send("MIRIAM-" + version + "@ebi.ac.uk", emailAdmin, emailsCura, "[MIRIAM-" + version + "] New BioModels.net qualifier: " + name, emailBody.toString(), "text/plain");
                        }
                        else
                        {
                            message = "The qualifier '" + name + "' (" + type + ") already exists!";
                        }
                    }
                    else
                    {
                        message = "You need must select an existing type of qualifiers!";
                    }
                }
                else
                {
                    message = "You need to fill in all the requested information!";
                }
                
                // a bit of cleaning
                dao.clean();
                
                jsp = "/editQualifiers";
            }
            else   // user hasn't enough privileges
            {
                message = "Sorry, you are not authorised to access this page!";
                jsp = "/user";
            }
        }
        else   // user not logged
        {
            message = "You need to be authenticated to access this feature!";
            request.setAttribute("referrer", request.getQueryString());
            jsp = "login.jsp";
        }
        
        if (null != message)
        {
            request.setAttribute("message", message);
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
