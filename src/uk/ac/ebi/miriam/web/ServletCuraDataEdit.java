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


import uk.ac.ebi.miriam.db.CuraDataType;
import uk.ac.ebi.miriam.db.CuraDataTypeDao;
import uk.ac.ebi.miriam.db.Resource;
import uk.ac.ebi.miriam.tools.CommonFunctions;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;


/**
 * <p>Servlet that handles the edition of a data collection in the curation pipeline.
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
public class ServletCuraDataEdit extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 7501881921916433161L;
    private Logger logger = Logger.getLogger(ServletCuraDataEdit.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletCuraDataEdit()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        CuraDataType data = new CuraDataType();
        StringBuilder emailBody = new StringBuilder();
        String message = null;
        String jsp;
        
        // to be able to retrieve UTF-8 elements from HTML forms
        request.setCharacterEncoding("UTF-8");
        
        // retrieves all the parameters of the data collection
        String internalId = CommonFunctions.cleanHtmlField(request.getParameter("id"));   // Integer.parseInt(request.getParameter("internalId"));
        String name = CommonFunctions.cleanHtmlField(request.getParameter("name"));
        String strSynonymsCount = request.getParameter("synonymsCounter");
        String definition = CommonFunctions.cleanHtmlField(request.getParameter("def"));
        String pattern = CommonFunctions.cleanHtmlField(request.getParameter("pattern"));
        String url = CommonFunctions.cleanHtmlField(request.getParameter("url"));
        String urn = CommonFunctions.cleanHtmlField(request.getParameter("urn"));
        String strDeprecatedCount = request.getParameter("deprecatedCounter");
        String strResourcesCount = request.getParameter("resourcesCounter");
        String strDocsCount = request.getParameter("docCounter");
        String userInfo = CommonFunctions.cleanHtmlField(request.getParameter("subComment"));
        String curatorComment = CommonFunctions.cleanHtmlField(request.getParameter("curator"));
        String state = request.getParameter("state");
        
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
        for (int i=1; i<=resourcesCount; ++i)
        {
            if ((request.getParameter("dataEntryPrefix"+i) != null) && (request.getParameter("dataResource"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("dataEntryPrefix"+i))) && (! MiriamUtilities.isEmpty(request.getParameter("dataResource"+i))))   // the 'dataEntrySuffix' is optional
            {
                Resource temp = new Resource();
                temp.setUrl_prefix(CommonFunctions.cleanHtmlField(request.getParameter("dataEntryPrefix"+i)));
                temp.setUrl_suffix(CommonFunctions.cleanHtmlField(request.getParameter("dataEntrySuffix"+i)));
                temp.setUrl_root(CommonFunctions.cleanHtmlField(request.getParameter("dataResource"+i)));
                temp.setExample(CommonFunctions.cleanHtmlField(request.getParameter("dataExample"+i)));
                temp.setInfo(CommonFunctions.cleanHtmlField(request.getParameter("information"+i)));
                temp.setInstitution(CommonFunctions.cleanHtmlField(request.getParameter("institution"+i)));
                temp.setLocation(CommonFunctions.cleanHtmlField(request.getParameter("country"+i)));
                // simple check: the new resources don't have this parameter
                if (request.getParameter("obsolete"+i) != null)
                {
                    if ((request.getParameter("obsolete"+i)).compareToIgnoreCase("1") == 0)
                    {
                        temp.setObsolete(true);
                    }
                    else
                    {
                        temp.setObsolete(false);
                    }
                }
                // simple check: the new resources don't have this parameter
                if (request.getParameter("resourceId"+i) != null)
                {
                    if ((request.getParameter("resourceId"+i)).compareToIgnoreCase("null") == 0)
                    {
                        //temp.setId(null);
                        temp.setId("null");
                    }
                    else
                    {
                        temp.setId(CommonFunctions.cleanHtmlField(request.getParameter("resourceId"+i)));
                    }
                }
                
                // TODO: the field "obsolete" cannot be set during the "submission" step, but need to be modified in the "edit" step (that means HERE!)
                //       for the moment, the only way to make a resource 'obsolete' is to delete it, and there is not way to make it official again (except messing the database by hand)...
                
                resources.add(temp);
            }
        }
        
        // retrieves the documentation(s) information, if necessary
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
            definition = null;
        }
        if ((MiriamUtilities.isEmpty(pattern)) || (pattern.equalsIgnoreCase("Enter Identifier pattern here...")))
        {
            pattern = null;
        }
        
        // fills the data collection (with the updated information)
        data.setId(internalId);
        data.setName(name);
        data.setSynonyms(synonyms);
        data.setURL(url);
        data.setURN(urn);
        data.setDeprecatedURIs(deprecated);
        data.setDefinition(definition);
        data.setRegexp(pattern);
        data.setResources(resources);
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
        data.setComment(curatorComment);
        data.setSubInfo(userInfo);
        data.setState(state);
        
        // the user is logged and has curation privileges
        if (MiriamUtilities.isUserCurator(request.getSession()))
        {
            // retrieves the version of the web application (sid/local, alpha, main or demo)
            String version = getServletContext().getInitParameter("version");
            
            // retrieves the email addresses of the administrator and the curators
            String emailAdr = getServletContext().getInitParameter("admin.email");
            String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
            
            // retrieves the user logged who asked for the action
            HttpSession session = request.getSession();
            String user = (String) session.getAttribute("login");
            
            // retrieves the name of the pool
            String poolName = getServletContext().getInitParameter("miriam_db_pool");
            
            // access to the database functions
            CuraDataTypeDao curaDao = new CuraDataTypeDao(poolName);
            
            // minimum valid information needed to create a new data type
            if (data.isValid())
            {
                // the data type already exists
                if (curaDao.exists(data)) //data.isExisting(poolName, false))
                {
                    // check if (at least) one resource is official (not obsolete)
                    if (data.hasOfficialResource())
                    {
                        // retrieves the old data type
                        /*
                        DataTypeHibernate oldOne = new DataTypeHibernate();
                        oldOne.retrieveCuraData(poolName, data.getId());
                        */
                        CuraDataType oldOne = curaDao.retrieve(data.getId());
                        
                        // sends an email to the administrator and curators with the OLD information about the data type
                        emailBody.append("\nThe data collection '" + data.getName() + "' (" + data.getId() + ") will be updated in the curation pipeline [" + version + " version], here are the information previously stored:\n");
                        emailBody.append(oldOne.toString());
                        emailBody.append("\nComment: \n" + data.getComment() + "\n-");
                        emailBody.append("\nState: " + oldOne.getState());
                        emailBody.append("\nUser: " + user);
                        GregorianCalendar cal = new GregorianCalendar();
                        emailBody.append("\nDate: " + cal.getTime());
                        emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
                        MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] Curation edit-1: '" + data.getName()  + "'", emailBody.toString(), "text/plain; charset=UTF-8");
                        
                        // update the database
                        /*int queryResult = data.updateCuraData(oldOne, poolName);*/
                        int queryResult = curaDao.update(data, oldOne);
                        
                        // update the DataType object (to retrieve the "deprecated" resources)
                        String tmpId = data.getId();
                        data.destroy();
                        /*data.retrieveCuraData(poolName, tmpId);*/
                        data = curaDao.retrieve(tmpId);
                        
                        // sends the email to the administrator with the NEW information about the data type
                        emailBody = new StringBuilder();
                        emailBody.append("\nThe data collection '" + data.getId() + "' has just been updated [" + version + " version]:");
                        // result of the update
                        if (queryResult == 1)
                        {
                            emailBody.append("\nEverything is OK: the data type was updated with success.\n");
                            message = "The data type '" + name + "' (" + data.getState() + ") has been updated.";
                        }
                        if (queryResult != 1)
                        {
                            emailBody.append("\nWARNING: a problem occurred during the update of the data collection!\n");
                            emailBody.append("\nPlease check the state of the database...\n");
                            message = "An error occurred during the update of the data collection '" + name + "' (" + data.getState() + ").";
                        }
                        // adds all the information about the new data type in the email
                        emailBody.append(data.toString());
                        // quick diff
                        /*
                        DataTypeHibernate newData = new DataTypeHibernate();
                        newData.retrieveCuraData(poolName, data.getId());
                        */
                        CuraDataType newData = curaDao.retrieve(data.getId());                        
                        
                        /*emailBody.append("\n--- QuickDiff ---\n\n" + newData.diff(oldOne));*/
                        emailBody.append("\n--- QuickDiff ---\n\n" + curaDao.diff(oldOne, newData));
                        emailBody.append("\nComment: \n" + newData.getComment() + "\n-");
                        emailBody.append("\nState: " + newData.getState());
                        emailBody.append("\nUser: " + user);
                        emailBody.append("\nDate: " + cal.getTime());
                        emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
                        MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] Curation edit-2: '" + data.getName()  + "'", emailBody.toString(), "text/plain; charset=UTF-8");
                        
                        // default next page
                        jsp = "/curation";
                    }
                    else   // the data collection has no official resource
                    {
                        logger.info("The data collection '" + data.getName() + "' (id: " + data.getId() + ") has no official resource!");
                        logger.info(data.toString());
                        logger.info("==> Therefore, the updating process was canceled. Sorry about that.");
                        
                        message = "The data collection '" + data.getName() + "' (id: " + data.getId() + ") has no official resource!";
                        request.setAttribute("section", "edit_error.html");
                        jsp = "static.jsp";
                    }
                }
                else   // the data collection doesn't exit: so we can't update it
                {
                    logger.info("The data collection '" + data.getName() + "' (id: " + data.getId() + ") doesn't exist in the database!");
                    logger.info(data.toString());
                    logger.info("==> Therefore, the updating process was canceled. Sorry about that.");
                    
                    message = "The data collection '" + data.getName() + "' (id: " + data.getId() + ") doesn't exist in the database!";
                    request.setAttribute("section", "edit_error.html");
                    jsp = "static.jsp";
                }
            }
            else
            {
                logger.info("One or more element(s) required for updating the data collection '" + data.getName() + "' (id: " + data.getId() + ") are missing or wrong!");
                logger.info(data.toString());
                logger.info("==> Therefore, the updating process was canceled. Sorry about that.");
                
                message = "One or more element(s) required for updating the data collection '" + data.getName() + "' (id: " + data.getId() + ") are missing or wrong!";
                request.setAttribute("section", "edit_error.html");
                jsp = "static.jsp";
            }
            
            curaDao.clean();
        }
        else
        {
            message = "You need to be authenticated to access this feature!";
            request.setAttribute("referrer", request.getQueryString());
            jsp = "login.jsp";
        }
        
        request.setAttribute("message", message);
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
