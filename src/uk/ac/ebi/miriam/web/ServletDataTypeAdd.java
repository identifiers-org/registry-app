/*
 * MIRIAM Registry (Web Application)
 * The Registry is an online resource created to catalogue data collections 
 * (their URIs and the corresponding physical URLs, whether these are controlled vocabularies or databases)
 * and provide unique and stable identifiers for life science, in the form of URIs. 
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2014  BioModels.net (EMBL - European Bioinformatics Institute)
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


import uk.ac.ebi.miriam.db.CuraDataTypeDao;
import uk.ac.ebi.miriam.db.DataTypeHibernate;
import uk.ac.ebi.miriam.db.Resource;
import uk.ac.ebi.miriam.tools.CommonFunctions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import java.net.URLEncoder;


/*
 * TODO:
 *
 * - remove the "urn:miriam:pubmed" and other "urn:miriam:doi" which are hard coded ==> retrieve that from the DB!
 * - add the setting of the 'obsolete' parameter for a resource in the edit interface
 */


/**
 * <p>Servlet that handles the submission of a new data collection.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2014  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20140312
 */
public class ServletDataTypeAdd extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -3573751137425794361L;
    private Logger logger = Logger.getLogger(ServletDataTypeAdd.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletDataTypeAdd()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        DataTypeHibernate data = new DataTypeHibernate();
        int queryResult = 0;
        StringBuilder emailBody = new StringBuilder();
        String jsp = "data_edit_done.jsp";
        boolean isSpam = true;   // worse case
        
        // to be able to retrieve UTF-8 elements from HTML forms
        request.setCharacterEncoding("UTF-8");
        
        // retrieves all the parameters of the data collection
        String name = CommonFunctions.cleanHtmlField(request.getParameter("name"));
        String strSynonymsCount = request.getParameter("synonymsCounter");
        String definition = CommonFunctions.cleanHtmlField(request.getParameter("def"));
        String pattern = CommonFunctions.cleanHtmlField(request.getParameter("pattern"));
        String url = CommonFunctions.cleanHtmlField(request.getParameter("url"));
        String urn = CommonFunctions.cleanHtmlField(request.getParameter("urn"));
        String strDeprecatedCount = request.getParameter("deprecatedCounter");
        String strResourcesCount = request.getParameter("resourcesCounter");
        String strDocsCount = request.getParameter("docCounter");
        String userInfo = CommonFunctions.cleanHtmlField(request.getParameter("user"));
        String spam = request.getParameter("pourriel");
        String publi = request.getParameter("publi_option");
        
        // retrieves the synonym(s), if necessary
        int synonymsCount = Integer.parseInt(strSynonymsCount);
        ArrayList<String> synonyms = new ArrayList<String>();
        for (int i=1; i<=synonymsCount; ++i)
        {
            if ((request.getParameter("synonym"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("synonym"+i))))
            {
                synonyms.add(CommonFunctions.cleanHtmlField(request.getParameter("synonym"+i)));
            }
        }
        
        // retrieves the obsolete/depreciated URI(s), if necessary
        int deprecatedCount = Integer.parseInt(strDeprecatedCount);
        ArrayList<String> deprecated = new ArrayList<String>();
        for (int i=1; i<=deprecatedCount; ++i)
        {
            if ((request.getParameter("deprecated"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("deprecated"+i))))
            {
                deprecated.add(CommonFunctions.cleanHtmlField(request.getParameter("deprecated"+i)));
            }
        }

        // retrieves the supplementary resource(s) information, if necessary
        int resourcesCount = Integer.parseInt(strResourcesCount);
        ArrayList<Resource> resources = new ArrayList<Resource>();
        //ArrayList resourcesDeSuffix = new ArrayList();
        //ArrayList resourcesDr = new ArrayList();
        for (int i=1; i<=resourcesCount; ++i)
        {
            if ((request.getParameter("dataEntryPrefix"+i) != null) && (request.getParameter("dataResource"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("dataEntryPrefix"+i))) && (! MiriamUtilities.isEmpty(request.getParameter("dataResource"+i))))   // the 'dataEntrySuffix' is optional
            {
                Resource temp = new Resource();
                temp.setUrl_prefix(CommonFunctions.cleanHtmlField(request.getParameter("dataEntryPrefix"+i)));
                temp.setUrl_suffix(CommonFunctions.cleanHtmlField(request.getParameter("dataEntrySuffix"+i)));
                temp.setUrl_root(CommonFunctions.cleanHtmlField(request.getParameter("dataResource"+i)));
                temp.setInfo(CommonFunctions.cleanHtmlField(request.getParameter("information"+i)));
                temp.setExample(CommonFunctions.cleanHtmlField(request.getParameter("dataExample"+i)));
                temp.setInstitution(CommonFunctions.cleanHtmlField(request.getParameter("institution"+i)));
                temp.setLocation(CommonFunctions.cleanHtmlField(request.getParameter("country"+i)));
                
                // TODO: the field "obsolete" cannot be set during the "submission" step, but need to be modified in the "edit" step
                
                resources.add(temp);
            }
        }
        
        // retrieves the documentation(s) information, if necessary (can be a URL, a PMID, a DOI, ...)
        int docsCount = Integer.parseInt(strDocsCount);
        Map<String, List<String>> docUris = new HashMap<String, List<String>>();
        ArrayList<String> docsUri = new ArrayList<String>();
        ArrayList<String> docsDoi = new ArrayList<String>();
        ArrayList<String> docsUrl = new ArrayList<String>();
        for (int i=1; i<=docsCount; ++i)
        {
            if ((request.getParameter("docUri"+i) != null) && (request.getParameter("docType"+i) != null))
            {
                if (request.getParameter("docType"+i).equalsIgnoreCase("PMID"))   // PMID
                {
                    String tmp = "urn:miriam:pubmed" + ":" + URLEncoder.encode(CommonFunctions.cleanHtmlField(request.getParameter("docUri"+i)), "UTF-8");
                    docsUri.add(tmp);
                }
                else
                {
                    if (request.getParameter("docType"+i).equalsIgnoreCase("DOI"))   // DOI
                    {
                        String tmp = "urn:miriam:doi" + ":" + URLEncoder.encode(CommonFunctions.cleanHtmlField(request.getParameter("docUri"+i)), "UTF-8");
                        docsDoi.add(tmp);
                    }
                    else   // default: physical location
                    {
                        docsUrl.add(request.getParameter("docUri"+i));
                    }
                }
            }
        }

        // some checks
        if ((MiriamUtilities.isEmpty(definition)) || (definition.equalsIgnoreCase("Enter definition here...")))
        {
            definition = "not provided";
        }
        if ((MiriamUtilities.isEmpty(pattern)) || (pattern.equalsIgnoreCase("Enter Identifier pattern here...")))
        {
            pattern = "not provided";
        }
        
        // fills the data type
        // doesn't set the identifier (normal: not created yet)
        data.setName(name);
        data.setSynonyms(synonyms);
        data.setURL(url);
        data.setURN(urn);
        data.setDeprecatedURIs(deprecated);
        data.setDefinition(definition);
        data.setRegexp(pattern);
        if (resources.isEmpty())
        {
            data.setResources(null);   // perhaps useless...
        }
        else
        {
            data.setResources(resources);
        }
        data.setDocumentationURLs(docsUrl);
        
        // stores the MIRIAM URIs and their type in the object DataType(Hibernate)
        docUris.put("PMID", docsUri);
        docUris.put("DOI", docsDoi);
        for (String uri: docsUri)
        {
            data.addDocumentationIDType("PMID");
            data.addDocumentationID(uri);
        }
        for (String uri: docsDoi)
        {
            data.addDocumentationIDType("DOI");
            data.addDocumentationID(uri);
        }
        
        // retrieves the version of the web application (local/sid, alpha, main or demo)
        String version = getServletContext().getInitParameter("version");
        
        // retrieves the email addresses of the administrator and the curators
        String emailAdr = getServletContext().getInitParameter("admin.email");
        String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
        
        // retrieves the user logged who asked for the action
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("login");
        
        // retrieves the name of the pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        // is this submission a spam?
        if (spam.equalsIgnoreCase(""))
        {
            isSpam = false;
        }
        else
        {
            isSpam = true;
        }
        
        // is the data valid?
        Boolean valid = data.isValid();
        
        // is the data already existing?
        Boolean existsInPublic = data.isExisting(poolName, true);
        Boolean existsInCura = data.isExistingInCura(poolName);
        Boolean exist = ((existsInPublic) || (existsInCura));
        
        
        // PROCESSING THE QUERY
        
        
        // submission is possibly a spam
        if (isSpam)
        {
            emailBody = new StringBuilder();   // just to be safe...
            emailBody.append("\nWARNING: there are strong suspicions that this submission is a spam!!!");
            emailBody.append("\n");
            emailBody.append(data.toString());
            // user info
            if (user != null)   // the user is not anonymous
            {
                emailBody.append("\nUser: " + user);
            }
            else   // anonymous user
            {
                emailBody.append("\n\nTHE USER WAS NOT AUTHENTICATED!");
                emailBody.append("\nUser information: " + userInfo);
                emailBody.append("\nIP address: " + request.getHeader("x-cluster-client-ip"));
                emailBody.append("\nUser Agent: " + request.getHeader("user-agent"));
                emailBody.append("\nAccept: " + request.getHeader("accept"));
                emailBody.append("\nAccept language: " + request.getHeader("accept-language"));
                emailBody.append("\nAccept charset: " + request.getHeader("accept-charset"));
                emailBody.append("\nReferer: " + request.getHeader("referer"));
            }
            GregorianCalendar cal = new GregorianCalendar();
            emailBody.append("\nDate: " + cal.getTime());
            emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nMIRIAM Registry\nhttp://www.ebi.ac.uk/miriam/");
            // sends the email
            MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] SPAM submission?", emailBody.toString(), "text/plain; charset=UTF-8");
            
            // no log
            
            // sends all the information to the JSP
            jsp = "data_edit_done.jsp";
            request.setAttribute("data", data);
            request.setAttribute("actionType", "add");
        }
        
        // data is invalid
        if ((! isSpam) && (! valid))
        {
            // user logged
            if (null != user)
            {
                // no email
                
                // log
                logger.warn("One or more element(s) required for submitting a new data collection is/are missing or wrong!");
                
                // sends all the information to the JSP
                jsp = "static.jsp";
                request.setAttribute("section", "add_error.html");
            }
            else   // user not logged: we do not enforce validation
            {
                if (MiriamUtilities.isEmpty(data.getName()))
                {
                    data.setName("Not provided!");
                }
                if (MiriamUtilities.isEmpty(data.getDefinition()))
                {
                    data.setDefinition("Not provided!");
                }
                if (MiriamUtilities.isEmpty(data.getRegexp()))
                {
                    data.setRegexp("Not provided!");
                }
                if (MiriamUtilities.isEmpty(data.getURL()) && MiriamUtilities.isEmpty(data.getURN()))
                {
                    data.setURN("To complete...");
                }
                if ((data.getDataEntriesPrefix().isEmpty()) || (data.getDataResources().isEmpty()))
                {
                    // nothing
                }
                valid = true;
            }
        }
        
        // data already exist
        if ((! isSpam) && (valid) && (exist))
        {
            // log
            if (existsInPublic)
            {
                logger.warn("This data collection '" + data.getName() + "' already exists in the public database!");
                logger.warn("Therefore, the submission process was canceled.");
            }
            if (existsInCura)
            {
                logger.warn("This data collection '" + data.getName() + "' already exists in the curation pipeline!");
                logger.warn("Therefore, the submission process was canceled.");
            }
            
            // email notification
            emailBody = new StringBuilder();   // just to be safe...
            if (existsInCura)
            {
                emailBody.append("\nSomebody tried to submit a data collection which is already present in the curation pipeline (" + version + " version): ");
                emailBody.append(data.getName());
                emailBody.append(".\nIf there is an email address provided, please inform him/her of the reason(s) why this data collection is not in MIRIAM Registry.");
            }
            else
            {
                emailBody.append("\nSomebody tried to submit a data collection which is already present on the public website (" + version + " version): ");
                emailBody.append(data.getName());
                emailBody.append(".\nIf there is an email address provided, please inform him/her of his/her mistake and provide a link to the appropriate entry.");
            }
            emailBody.append("\nFor your information, here is the information provided:");
            // adds all the information about the new data type in the email
            emailBody.append("\n");
            emailBody.append(data.toString());
            
            // the user is not anonymous
            if (user != null)
            {
                emailBody.append("\nUser: " + user);
            }
            else   // anonymous user
            {
                emailBody.append("\nUser information: " + userInfo);
                emailBody.append("\nIP address: " + request.getHeader("x-cluster-client-ip"));
            }
            GregorianCalendar cal = new GregorianCalendar();
            emailBody.append("\n\nDate: " + cal.getTime());
            emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
            MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] Duplicate submission: '" + data.getName() + "'", emailBody.toString(), "text/plain; charset=UTF-8");
            
            // sends all the information to the JSP
            jsp = "static.jsp";
            if (existsInPublic)
            {
                request.setAttribute("section", "add_already_existing.html");
            }
            if (existsInCura)
            {
                request.setAttribute("section", "add_already_pending.html");
            }
        }
        
        // data not already existing and curator's session timeout: storage in the curation pipeline
        if ((! isSpam) && (valid) && (! exist) && (null == user) && (null == userInfo))
        {
            CuraDataTypeDao curaDao = new CuraDataTypeDao(poolName);
            queryResult = curaDao.storePendingObject(data, "curator's session timeout");
            
            // email notification
            emailBody = new StringBuilder();   // just to be safe...
            emailBody.append("\nA new data collection has just been submitted [" + version + " version]:");
            emailBody.append("\n\nWARNING: it seems that this data collection has been submitted by a curator which session has timed out...");
            if ((null != publi) && publi.equalsIgnoreCase("publication"))
            {
                emailBody.append("\nThe data collection was originally planned to be directly published!");
            }
            else
            {
                emailBody.append("\nThe data collection was originally supposed to be stored in the curation's pipeline.");
            }
            emailBody.append("\n");
            // result of the queries
            if (queryResult == 1)
            {
                emailBody.append("\nEverything is all right: the new data collection is now stored in the curation's pipeline.");
            }
            else
            {
                emailBody.append("\nWARNING: a problem occurred during the update of the curation database!");
            }
            // adds all the information about the new data type in the email
            emailBody.append("\n");
            emailBody.append(data.toString());
            emailBody.append("\n\nTHE USER WAS NOT AUTHENTICATED: THIS DATA TYPE IS NOW PENDING!");
            emailBody.append("\nUser information: curator's session timeout");
            emailBody.append("\nIP address: " + request.getHeader("x-cluster-client-ip"));
            GregorianCalendar cal = new GregorianCalendar();
            emailBody.append("\nDate: " + cal.getTime());
            emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
            MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] data collection pending: '" + data.getName() + "'", emailBody.toString(), "text/plain; charset=UTF-8");
            
            // log
            if (queryResult == 1)   // success
            {
                logger.info("New data collection submitted (but still pending): " + data.getName() + " (" + data.getSynonyms().toString() + ")");
                logger.info("User information: " + userInfo);
            }
            else
            {
                logger.error("A problem occured while trying to add a new data collection to the curation's pipeline: " + data.getName() + " (" + data.getSynonyms().toString() + ")");
                logger.error("User information: " + userInfo);
            }
            logger.info(data.toString());
            
            // sends all the information to the JSP
            StringBuilder message = new StringBuilder();
            message.append("It seems you started the submission process while being logged in, but this is no longer the case. Perhaps your session has timed out...<br />");
            if ((null != publi) && publi.equalsIgnoreCase("publication"))
            {
                message.append("As a result, the new data collection has not been published, but has been stored in the curation's pipeline instead.");
            }
            else
            {
                message.append("Anyway, the new data collection is now stored in the curation's pipeline.");
            }
            jsp = "data_edit_done.jsp";
            request.setAttribute("message", message.toString());
            request.setAttribute("curator", "not null");
            request.setAttribute("data", data);
            request.setAttribute("actionType", "add");
            
            curaDao.clean();
        }
        
        // data not already existing and user anonymous: storage in the curation pipeline
        if ((! isSpam) && (! exist) && (null == user) && (null != userInfo))   // ne need for "valid" data
        {
            CuraDataTypeDao curaDao = new CuraDataTypeDao(poolName);
            queryResult = curaDao.storePendingObject(data, userInfo);
            
            // email notification
            emailBody = new StringBuilder();   // just to be safe...
            emailBody.append("\nA new data collection has just been submitted [" + version + " version]:");
            // result of the queries
            if (queryResult == 1)
            {
                emailBody.append("\nEverything is all right: the new data collection is now stored in the curation's pipeline.");
            }
            else
            {
                emailBody.append("\nWARNING: a problem occurred during the update of the curation database!");
            }
            // adds all the information about the new data type in the email
            emailBody.append("\n");
            emailBody.append(data.toString());
            emailBody.append("\n\nTHE USER WAS NOT AUTHENTICATED: THIS DATA COLLECTION IS NOW PENDING!");
            emailBody.append("\nUser information: " + userInfo);
            emailBody.append("\nIP address: " + request.getHeader("x-cluster-client-ip"));
            GregorianCalendar cal = new GregorianCalendar();
            emailBody.append("\nDate: " + cal.getTime());
            emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
            MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] data collection pending: '" + data.getName() + "'", emailBody.toString(), "text/plain; charset=UTF-8");
            
            // log
            if (queryResult == 1)   // success
            {
                logger.info("New data collection submitted (but still pending): " + data.getName() + " (" + data.getSynonyms().toString() + ")");
                logger.info("User information: " + userInfo);
            }
            else
            {
                logger.error("A problem occured while trying to add a new data collection to the curation's pipeline: " + data.getName() + " (" + data.getSynonyms().toString() + ")");
                logger.error("User information: " + userInfo);
            }
            logger.info(data.toString());
            
            curaDao.clean();
            
            // sends all the information to the JSP
            jsp = "data_edit_done.jsp";
            request.setAttribute("message", "The data collection is now pending. A curator will now check its correctness, before making it available publicly in the Registry.");
            request.setAttribute("data", data);
            request.setAttribute("actionType", "add");
        }
        
        // data not already existing and user logged: direct publication or storage in the curation pipeline
        if ((! isSpam) && (valid) && (! exist) && (null != user))
        {
            Boolean publish = ((null != publi) && publi.equalsIgnoreCase("publication"));
            
            // instant publication
            if (publish)
            {
                queryResult = data.storeObject(poolName);
            }
            else   // storage in the curation pipeline
            {
                CuraDataTypeDao curaDao = new CuraDataTypeDao(poolName);
                String comment = "Submitted by curator: " + user;
                queryResult = curaDao.storePendingObject(data, comment);
                curaDao.clean();
            }
            
            // email notification
            emailBody = new StringBuilder();   // just to be safe...
            emailBody.append("\nA new data collection has just been submitted [" + version + " version]:");
            // result of the queries
            if (publish)
            {
                if (queryResult == 1)
                {
                    emailBody.append("\nEverything is all right: the new data collection is now stored in the database.");
                }
                else
                {
                    emailBody.append("\nWARNING: a problem occurred during the update of the database!");
                }
            }
            else
            {
                if (queryResult == 1)
                {
                    emailBody.append("\nEverything is all right: the new data collection is now stored in the curation's pipeline.");
                }
                else
                {
                    emailBody.append("\nWARNING: a problem occurred during the update of the curation database!");
                }
            }
            // adds all the information about the new data collection in the email
            emailBody.append("\n");
            emailBody.append(data.toString());
            emailBody.append("\nUser: " + user);
            GregorianCalendar cal = new GregorianCalendar();
            emailBody.append("\nDate: " + cal.getTime());
            emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
            if (publish)
            {
                MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] data collection added: '" + data.getName() + "'", emailBody.toString(), "text/plain; charset=UTF-8");
            }
            else
            {
                MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] data collection pending: '" + data.getName() + "'", emailBody.toString(), "text/plain; charset=UTF-8");
            }
            
            // log
            if (publish)
            {
                if (queryResult == 1)   // success
                {
                    logger.info("New data collection submitted and published: " + data.getName() + " (" + data.getSynonyms().toString() + ")");
                    logger.info("Curator: " + user);
                }
                else
                {
                    logger.error("A problem occured while trying to add a new data collection to the database and publish it, by " + user + ": " + data.getName() + " (" + data.getSynonyms().toString() + ")");
                }
            }
            else
            {
                if (queryResult == 1)   // success
                {
                    logger.info("New data collection submitted (but still pending): " + data.getName() + " (" + data.getSynonyms().toString() + ")");
                    logger.info("Curator: " + user);
                }
                else
                {
                    logger.error("A problem occured while trying to add a new data collection to the curation's pipeline, by " + user + ": " + data.getName() + " (" + data.getSynonyms().toString() + ")");
                }
            }
            logger.info(data.toString());
            
            // sends all the information to the JSP
            jsp = "data_edit_done.jsp";
            if (publish)
            {
            	request.setAttribute("message", "New data collection successfully added to the Registry!");
            }
            else
            {
            	request.setAttribute("message", "New data collection pending in the curation pipeline.");
            }
            request.setAttribute("data", data);
            request.setAttribute("actionType", "add");
        }
        
        // sends information of the result of the process to the user
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
