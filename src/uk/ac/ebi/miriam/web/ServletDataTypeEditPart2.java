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


import uk.ac.ebi.miriam.db.*;
import uk.ac.ebi.miriam.tools.CommonFunctions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;


/**
 * <p>Servlet which handles the modification of an existed data collection (part 2: update the database).
 * 
 * <p>
 * TODO:
 *   - remove the URLs hard coded ("urn:miriam:pubmed" or "urn:miriam:doi") and replace them with a database access
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
public class ServletDataTypeEditPart2 extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 8119310952378238094L;
    private Logger logger = Logger.getLogger(ServletDataTypeEditPart2.class);
    
    
    /* (non-Java-doc)
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletDataTypeEditPart2()
    {
        super();
    }
    
    
    /* (non-Java-doc)
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    
    
    /* (non-Java-doc)
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        DataTypeHibernate data = new DataTypeHibernate();
        int queryResult = 0;
        boolean isSpam = true;   // worse case
        StringBuilder emailBody = new StringBuilder();
        String jsp = "data_edit_done.jsp";
        
        logger.debug("The edit form for an update has been filled...");
        
        // to be able to retrieve UTF-8 elements from HTML forms
        request.setCharacterEncoding("UTF-8");
        
        // retrieves all the parameters of the data type
        String internalId = CommonFunctions.cleanHtmlField(request.getParameter("id"));   // Integer.parseInt(request.getParameter("internalId"));
        String name = CommonFunctions.cleanHtmlField(request.getParameter("name"));
        String strSynonymsCount = request.getParameter("synonymsCounter");
        String definition = CommonFunctions.cleanHtmlField(request.getParameter("def"));
        String pattern = CommonFunctions.cleanHtmlField(request.getParameter("pattern"));
        String url = CommonFunctions.cleanHtmlField(request.getParameter("url"));
        String urn = CommonFunctions.cleanHtmlField(request.getParameter("urn"));
        String strURICount = request.getParameter("uriCounter");
        String strDeprecatedCount = request.getParameter("deprecatedCounter");
        String strResourcesCount = request.getParameter("resourcesCounter");
        String strDocsCount = request.getParameter("docCounter");
        String userInfo = CommonFunctions.cleanHtmlField(request.getParameter("user"));
        Boolean obsolete = false;
        if (CommonFunctions.cleanHtmlField(request.getParameter("dataCollectionObsolete")).equalsIgnoreCase("true"))
        {
            obsolete = true;
        }
        String spam = request.getParameter("pourriel");
        
        // is this submission a spam?
        if (spam.equalsIgnoreCase(""))
        {
            isSpam = false;
        }
        else
        {
            isSpam = true;
        }
        
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

        // retrieves the URI(s)
        int uriCount = Integer.parseInt(strURICount);
        HashSet<URI> uris = new HashSet<URI>();
        for (int i=1; i<=uriCount; ++i)
        {
            URI uri = new URI();
            if ((request.getParameter("uriVal"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("uriVal"+i))))
            {
                uri.setValue(CommonFunctions.cleanHtmlField(request.getParameter("uriVal"+i)));
            }
            else{
                break;
            }
            if ((request.getParameter("uriCon"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("uriCon"+i))))
            {
                uri.setConvertPrefix(CommonFunctions.cleanHtmlField(request.getParameter("uriCon"+i)));
            }
            uri.setType(request.getParameter("uriVal"+i).startsWith("http") ? "URL" : "URN");

            if(request.getParameter("uriDep" + i) != null){
                uri.setDeprecated(1);
            }else{
                if (uri.getValue().equals(urn)) {
                    uri.setDeprecated(0);
                }
                else if (uri.getValue().contains("identifiers.org") && CommonFunctions.getNamespace(urn).equals(CommonFunctions.getNamespaceFromIdOrg(uri.getValue()))){
                    uri.setDeprecated(0);
                }else{
                    uri.setDeprecated(2);
                }


            }
            uris.add(uri);
        }
        
/*        // retrieves the obsolete/depreciated URI(s), if necessary
        int deprecatedCount = Integer.parseInt(strDeprecatedCount);
        ArrayList<String> deprecated = new ArrayList<String>();
        for (int i=1; i<=deprecatedCount; ++i)
        {
            if ((request.getParameter("deprecated"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("deprecated"+i))))
            {
                deprecated.add(CommonFunctions.cleanHtmlField(request.getParameter("deprecated"+i)));
            }
        }
        // later below we check whether the namespace has been modified, in which case we need to record the obsolete Identifiers.org URI*/
        
        // retrieves the supplementary resource(s) information, if necessary
        int resourcesCount = Integer.parseInt(strResourcesCount);
        ArrayList<Resource> resources = new ArrayList<Resource>();
        for (int i=1; i<=resourcesCount; ++i)
        {
            if ((request.getParameter("dataEntryPrefix"+i) != null) && (request.getParameter("dataResource"+i) != null) && (! MiriamUtilities.isEmpty(request.getParameter("dataEntryPrefix"+i))) && (! MiriamUtilities.isEmpty(request.getParameter("dataResource"+i))))   // the 'dataEntrySuffix' is optional
            {
                Resource temp = new Resource();
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
                        temp.setId(request.getParameter("resourceId"+i));
                    }
                }
                temp.setUrl_prefix(CommonFunctions.cleanHtmlField(request.getParameter("dataEntryPrefix"+i)));
                temp.setUrl_suffix(CommonFunctions.cleanHtmlField(request.getParameter("dataEntrySuffix"+i)));
                temp.setUrl_root(CommonFunctions.cleanHtmlField(request.getParameter("dataResource"+i)));
                temp.setConvert_prefix(CommonFunctions.cleanHtmlField(request.getParameter("convert_prefix"+i)));
                temp.setExample(CommonFunctions.cleanHtmlField(request.getParameter("dataExample"+i)));
                temp.setInfo(CommonFunctions.cleanHtmlField(request.getParameter("information"+i)));
                temp.setInstitution(CommonFunctions.cleanHtmlField(request.getParameter("institution"+i)));
                temp.setLocation(CommonFunctions.cleanHtmlField(request.getParameter("country"+i)));
                // checks whether the resource is the primary one
                if ((null != request.getParameter("primaryResource")) && ((request.getParameter("resourceId"+i)).compareToIgnoreCase("null") != 0) && (request.getParameter("primaryResource").equalsIgnoreCase(request.getParameter("resourceId"+i))))
                {
                	temp.setPrimary(true);
                }
                else
                {
                	temp.setPrimary(false);
                }
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

                
                // TODO: the field "obsolete" cannot be set during the "submission" step, but need to be modified in the "edit" step (that means HERE!)
                //       for the moment, the only way to make a resource 'obsolete' is to delete it, and there is not way to make it official again (except messing the database by hand)...

                //Setting formats
                String formatCounterVal = request.getParameter("formatCounter" + i);
                if(formatCounterVal != null){
                    int formatCount = Integer.parseInt(formatCounterVal);
                    for (int j = 1; j <= formatCount; j++) {
                        if (!request.getParameter("formatPre" + i + j).isEmpty()) {
                            Format format = new Format();
                            format.setUrlPrefix(request.getParameter("formatPre" + i + j));
                            format.setUrlSuffix(request.getParameter("formatSuf" + i + j));
                            format.setMimeTypeId(Integer.parseInt(request.getParameter("formatType" + i + j)));
                            if (request.getParameter("formatDep" + i + j) == null)
                                format.setDeprecated(0);
                            else
                                format.setDeprecated(1);
                            temp.addFormat(format);
                        }
                    }
                }

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
                        docsUrl.add(CommonFunctions.cleanHtmlField(request.getParameter("docUri"+i)));
                    }
                }
            }
        }
        
        /*
        int docsCount = Integer.parseInt(strDocsCount);
        ArrayList<String> docsUri = new ArrayList<String>();
        ArrayList<String> docsUrl = new ArrayList<String>();
        for (int i=1; i<=docsCount; ++i)
        {
            if ((request.getParameter("doc"+i) != null) && (request.getParameter("docType"+i) != null))   // old documentations
            {
                //logger.debug("-------- OLD DOC: " + request.getParameter("doc"+i));   // TEST
                if (request.getParameter("docType"+i).equalsIgnoreCase("URL"))
                {
                    docsUrl.add(request.getParameter("doc"+i));
                }
                else
                {
                    docsUri.add(request.getParameter("doc"+i));
                }
            }
            else   // new documentation(s) added
            {
                //logger.debug("-------- NEW DOC: " + request.getParameter("docUri"+i));   // TEST
                if ((request.getParameter("docUri"+i) != null) && (request.getParameter("docType"+i) != null))
                {
                    if (request.getParameter("docType"+i).equalsIgnoreCase("URL"))
                    {
                        docsUrl.add(request.getParameter("docUri"+i));
                    }
                    else
                    {
                        docsUri.add("http://www.pubmed.gov/#" + request.getParameter("docUri"+i));
                    }
                }
            }
        }
        */
        
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
        data.setName(name);
        data.setSynonyms(synonyms);
        data.setURL(url);
        data.setURN(urn);
        data.setUris(uris);
//        data.setDeprecatedURIs(deprecated);
        data.setDefinition(definition);
        data.setRegexp(pattern);
        data.setId(internalId);
        data.setResources(resources);
        data.setOwnershipForResources((String)request.getSession().getAttribute("login"));
        data.setObsolete(obsolete);
        /*
    if ((resourcesDePrefix.isEmpty()) || (resourcesDr.isEmpty()))
    {
      data.setLocations(null);
    }
    else
    {
      data.setLocations(resourcesDePrefix, resourcesDeSuffix, resourcesDr);
    }
         */
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
        
        // retrieves the version of the web application (sid/local, alpha, main or demo)
        String version = getServletContext().getInitParameter("version");
        
        // retrieves the email addresses of the administrator and the curators
        String emailAdr = getServletContext().getInitParameter("admin.email");
        String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
        
        // retrieves the user logged who asked for the action
        HttpSession session = request.getSession();
        String user = (String) session.getAttribute("login");
        String role = (String) session.getAttribute("role");



        // retrieves the name of the pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        
        // minimum valid information needed to create a new data collection
        if (data.isValid())
        {
            // the data collection already exists
            if (data.isExisting(poolName, false))
            {
                // check if (at least) one resource is official (not obsolete)
                if (data.hasOfficialResource())
                {
                	// submission is possibly a spam
                    if (isSpam)
                    {
                        emailBody = new StringBuilder();   // just to be safe...
                        emailBody.append("\nWARNING: there are strong suspicions that this update is a spam!!!\n");
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
//                        MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] SPAM update? '" + data.getName() + "'", emailBody.toString(), "text/plain; charset=UTF-8");
                        
                        // next step
                        request.setAttribute("section", "edit_error.html");
                        jsp = "static.jsp";
                    }
                    else   // submission is not a spam
                    {
                        // the user is not anonymous: update of the database
                        if (user != null && role != null) {

                            EditHistoryDao editHistoryDao = new EditHistoryDao(poolName);
                            User userdetails = getUserInfo(user);

                            if(!role.equals("user")){
                                // retrieves the old data collection
                                DataTypeHibernate oldOne = new DataTypeHibernate();
                                oldOne.retrieveData(poolName, data.getId());

                                // checks whether the namespace has been updated, in which case the old Identifiers.org URL needs to be recorded
                                String oldNamespace = CommonFunctions.getNamespace(oldOne.getURN());
                                String newNamespace = CommonFunctions.getNamespace(data.getURN());
                                if (!oldNamespace.equals(newNamespace))   // namespace has been updated
                                {
/*	                        	data.addDeprecatedURI("http://identifiers.org/" + oldNamespace + "/");
	                        	data.addDeprecatedURI("urn:miriam:" + oldNamespace);*/

                                    for (URI uri : data.getUris()) {
                                        if (uri.getValue().equals(oldOne.getURN()) || uri.getValue().equals(oldOne.getURL())) {
                                            uri.setDeprecated(1);
                                        }
                                    }

                                    //add new urn
                                    URI uri = new URI(data.getURN(), "URN");
                                    uri.setDeprecated(0);
                                    data.getUris().add(uri);
                                    data.setURL("http://identifiers.org/" + newNamespace + "/");


                                    uri = new URI(data.getURL(), "URL");
                                    uri.setDeprecated(0);
                                    data.getUris().add(uri);

                                }

                                // logs the old information about the data collection (just in case)
                                logger.info("The data collection '" + data.getId() + "' will be updated soon, by " + user + ": " + oldOne.getName() + " (synonyms: " + oldOne.getSynonyms().toString() + "; ID: " + oldOne.getId() + ")");
                                logger.info("Here is the information previously stored in the database:\n");
                                logger.info(data.toString());

                                // sends an email to the administrator and curators with the OLD information about the data collection
                                emailBody.append("\nThe data collection '" + data.getId() + "' will be updated [" + version + " version]!\n");
                                emailBody.append("Here is the information previously stored:\n");
                                emailBody.append(oldOne.toString());

                                emailBody.append("\nUser Information: ");
                                emailBody.append("\n\tUser: " + user);
                                if(!userdetails.getFullName().isEmpty())
                                    emailBody.append("\n\tName: " + userdetails.getFullName());
                                if(!userdetails.getEmail().isEmpty())
                                    emailBody.append("\n\tE-mail: " + userdetails.getEmail());
                                if(!userdetails.getOrganisation().isEmpty())
                                    emailBody.append("\n\tOrganisation: " + userdetails.getOrganisation());

                                emailBody.append("\nIP address: " + request.getHeader("x-cluster-client-ip"));
                                GregorianCalendar cal = new GregorianCalendar();
                                emailBody.append("\nDate: " + cal.getTime());
                                emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
//                               MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] Backup: '" + data.getName() + "'", emailBody.toString(), "text/plain; charset=UTF-8");

                                // updates the database
                                queryResult = data.updateObject(oldOne, poolName);

                                // updates the DataType object (to retrieve the "deprecated" resources)
                                String tmpId = data.getId();
                                data.destroy();
                                data.retrieveData(poolName, tmpId);

                                // sends the email to the administrator with the NEW information about the data collection
                                emailBody = new StringBuilder();
                                emailBody.append("\nThe data collection '" + data.getId() + "' has just been updated [" + version + " version]:");

                                // result of the queries
                                if (queryResult == 1) {
                                    emailBody.append("\nEverything is OK: the data collection was updated with success.\n");
                                }
                                if (queryResult != 1) {
                                    emailBody.append("\nWARNING: a problem occurred during the update of the data collection!\n");
                                }

                                // add all the information about the new data collection in the email
                                emailBody.append(data.toString());

                                // quick diff
                                DataTypeHibernate newData = new DataTypeHibernate();
                                newData.retrieveData(poolName, data.getId());
                                emailBody.append("\n--- QuickDiff ---\n\n" + newData.diff(oldOne));

                                emailBody.append("\nUser Information: ");
                                emailBody.append("\n\tUser: " + user);
                                if(!userdetails.getFullName().isEmpty())
                                    emailBody.append("\n\tName: " + userdetails.getFullName());
                                if(!userdetails.getEmail().isEmpty())
                                    emailBody.append("\n\tE-mail: " + userdetails.getEmail());
                                if(!userdetails.getOrganisation().isEmpty())
                                    emailBody.append("\n\tOrganisation: " + userdetails.getOrganisation());

                                emailBody.append("\nIP address: " + request.getHeader("x-cluster-client-ip"));
                                emailBody.append("\nDate: " + cal.getTime());
                                emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");

                                // sends the email
//                                MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] Updated: '" + data.getName() + "'", emailBody.toString(), "text/plain; charset=UTF-8");

                                // log the event, with all the information
                                if (queryResult == 1)   // success
                                {
                                    editHistoryDao.insertChange(data.getId(), user, newData.diff(oldOne));
                                    logger.info("Data collection '" + data.getId() + "' updated in the database, by " + user + ": " + data.getName() + " (synonyms: " + data.getSynonyms().toString() + "; ID: " + data.getId() + ")");
                                } else   // failure
                                {
                                    logger.info("A problem occured while trying to update the data collection '" + data.getId() + "' in the database, by " + user + ": " + data.getName() + " (" + data.getSynonyms().toString() + ")");
                                }
                                logger.info(data.toString());
                                //logger.info("IP address: " + request.getHeader("x-cluster-client-ip"));
                                //logger.info("IP address: " + request.getRemoteAddr());

                                // sends all the information to the JSP
                                request.setAttribute("message", "This data collection has now been updated in the Registry.");
                                request.setAttribute("data", data);
                                request.setAttribute("actionType", "edit");
                            }
                            // logged in as user
                            else{
	                            // retrieves the old data collection
	                            DataTypeHibernate oldOne = new DataTypeHibernate();
	                            oldOne.retrieveData(poolName, data.getId());

                                queryResult = data.updateResource(poolName);
	                            // logs the old information about the data collection (just in case)
	                            logger.info("The data collection '" + data.getId() + "' needs to be updated (and still pending): " + oldOne.getName() + " (synonyms: " + oldOne.getSynonyms().toString() + "; ID: " + oldOne.getId() + ")");
	                            logger.info("Here are the information previously stored in the database:\n");
	                            logger.info(oldOne.toString());

	                            // sends an email to the administrator with the OLD information about the data collection
	                            emailBody.append("\nModifications to the data collection '" + data.getId() + "' have been suggested (" + version + " version).\n");
	                            emailBody.append("Please review them and amend if necessary.\n");
	                            // new info
	                            emailBody.append("\n--- Suggested modifications (deleted resources not shown here) ---\n" + data.toString());
	                            // old info
	                            emailBody.append("\n--- Information previously stored in the database ---\n\n" + oldOne.toString());

                                //updated resources
                                StringBuffer resourceList = new StringBuffer("\nTHE "+ user+" IS AUTHENTICATED to change the following resources.\n");
                                for (Resource resource : data.getResources()) {
                                    if(resource.getOwnership_status()==1){
                                        resourceList.append("\t"+resource.getId()+"\n");
                                    }
                                }

                                emailBody.append(resourceList.toString());

                                emailBody.append("\n\nTHE "+ user+ "  IS NOT AUTHENTICATED to update other content: therefore, no change have been done so far (and no modification is done automatically).");

                                // quick diff
	                            emailBody.append("\n--- Complete comparison ---\n\n" + data.diff(oldOne));

                                emailBody.append("\nUser Information: ");
                                emailBody.append("\n\tUser: " + user);
                                if(!userdetails.getFullName().isEmpty())
                                    emailBody.append("\n\tName: " + userdetails.getFullName());
                                if(!userdetails.getEmail().isEmpty())
                                    emailBody.append("\n\tE-mail: " + userdetails.getEmail());
                                if(!userdetails.getOrganisation().isEmpty())
                                    emailBody.append("\n\tOrganisation: " + userdetails.getOrganisation());


	                            emailBody.append("\nIP address: " + request.getHeader("x-cluster-client-ip"));
	                            GregorianCalendar cal = new GregorianCalendar();
	                            emailBody.append("\nDate: " + cal.getTime());
	                            emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
                                System.out.print(emailBody.toString());
//	                            MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] Update pending: '" + data.getName() + "' by user '"+ user + "'", emailBody.toString(), "text/plain; charset=UTF-8");


                                if (queryResult == 1)   // success
                                {

                                    editHistoryDao.insertChange(data.getId(), user, resourceList.toString() +"\n--- Complete comparison, only refer to the resources that the user is authenticated to update---\n\n"+ data.diff(oldOne));
                                    logger.info("Data collection '" + data.getId() + "' updated in the database, by " + user + ": " + data.getName() + " (synonyms: " + data.getSynonyms().toString() + "; ID: " + data.getId() + ")");
                                } else   // failure
                                {
                                    logger.info("A problem occured while trying to update the data collection '" + data.getId() + "' in the database, by " + user + ": " + data.getName() + " (" + data.getSynonyms().toString() + ")");
                                }
                                logger.info(data.toString());

                                // sends all the information to the JSP
                                request.setAttribute("message", "Your resources have been updated. Other editions has been recorded. A curator will now check its correctness, before making it available publicly in the Registry.");
                                request.setAttribute("data", data);
                                request.setAttribute("actionType", "edit");
                            }
                        }
	                    else   // the user is anonymous
	                    {
	                        // no user info: probably the session of a curator just timed out...
	                        if (null == userInfo)
	                        {
	                            // modify the MIRIAM IDs of the documentation (to remove the data collection part and the separator)
	                            for (int i=0; i<data.getDocumentationIDs().size(); ++i)
	                            {
	                                String tmp = MiriamUtilities.getElementPart(data.getDocumentationID(i));
	                                // decode the string
	                                try
	                                {
	                                    tmp = URLDecoder.decode(tmp, "UTF-8");
	                                }
	                                catch (UnsupportedEncodingException e)
	                                {
	                                    logger.error("An exception occurred while decoding a String using UTF8!");
	                                    logger.error("UnsupportedEncodingException: " + e.getMessage());
	                                }
	                                data.setDocumentationID(i, tmp);
	                            }
	                            
	                            request.setAttribute("message", "It seems you accessed this page while being logged in but that is no longer the case. Perhaps your session has timed out...<br />If you don't want to lose any data, please keep this page open, login in on another tab or window, and submit this form again.");
	                            request.setAttribute("curator", "not null");
	                            request.setAttribute("data", data);
	                            jsp = "data_edit.jsp";
	                        }
	                        else   // normal anonymous edition (suggestion)
	                        {
	                            // retrieves the old data collection
	                            DataTypeHibernate oldOne = new DataTypeHibernate();
	                            oldOne.retrieveData(poolName, data.getId());
	                            
	                            // logs the old information about the data collection (just in case)
	                            logger.info("The data collection '" + data.getId() + "' needs to be updated (and still pending): " + oldOne.getName() + " (synonyms: " + oldOne.getSynonyms().toString() + "; ID: " + oldOne.getId() + ")");
	                            logger.info("Here are the information previously stored in the database:\n");
	                            logger.info(oldOne.toString());
	                            
	                            // sends an email to the administrator with the OLD information about the data collection
	                            emailBody.append("\nModifications to the data collection '" + data.getId() + "' have been suggested (" + version + " version).\n");
	                            emailBody.append("Please review them and amend if necessary.\n");
	                            // new info
	                            emailBody.append("\n--- Suggested modifications (deleted resources not shown here) ---\n" + data.toString());
	                            // old info
	                            emailBody.append("\n--- Information previously stored in the database ---\n\n" + oldOne.toString());
	                            // quick diff
	                            emailBody.append("\n--- Comparison ---\n\n" + data.diff(oldOne));
	                            emailBody.append("\n\nTHE USER WAS NOT AUTHENTICATED: therefore, no change have been done so far (and no modification will be done automatically).");
	                            emailBody.append("\nUser information: " + userInfo);
	                            emailBody.append("\nIP address: " + request.getHeader("x-cluster-client-ip"));
	                            GregorianCalendar cal = new GregorianCalendar();
	                            emailBody.append("\nDate: " + cal.getTime());
	                            emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
//	                            MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] Update pending: '" + data.getName() + "'", emailBody.toString(), "text/plain; charset=UTF-8");
	                            
	                            // logs the event, with all the information
	                            logger.info("The Data collection '" + data.getId() + "' needs to be updated in the database (still pending): " + data.getName() + " (synonyms: " + data.getSynonyms().toString() + "; ID: " + data.getId() + ")");
	                            logger.info("User information: " + userInfo);
	                            //logger.info("IP address: " + request.getHeader("x-cluster-client-ip"));
	                            logger.info("Here are the updated information which need to be modified in the database:");
	                            logger.info(data.toString());
	                        }
	                        
	                        // sends all the information to the JSP
	                        request.setAttribute("message", "Your edition has been recorded. A curator will now check its correctness, before making it available publicly in the Registry.");
	                        request.setAttribute("data", data);
	                        request.setAttribute("actionType", "edit");
	                    }
                    }
                }
                else   // the data collection has no official resource
                {
                    logger.info("The data collection '" + data.getName() + "' (id: " + data.getId() + ") has no official resource!");
                    logger.info(data.toString());
                    logger.info("==> Therefore, the updating process was canceled. Sorry about that.");
                    
                    request.setAttribute("section", "edit_error.html");
                    jsp = "static.jsp";
                }
            }
            else   // the data collection doesn't exit: so we can't update it
            {
                logger.info("The data collection '" + data.getName() + "' (id: " + data.getId() + ") doesn't exist in the database!");
                logger.info(data.toString());
                logger.info("==> Therefore, the updating process was canceled. Sorry about that.");
                
                request.setAttribute("section", "edit_error.html");
                jsp = "static.jsp";
            }
        }
        else
        {
            logger.info("One or more element(s) required for updating the data collection '" + data.getName() + "' (id: " + data.getId() + ") are missing or wrong!");
            logger.info(data.toString());
            logger.info("==> Therefore, the updating process was canceled. Sorry about that.");
            
            request.setAttribute("section", "edit_error.html");
            jsp = "static.jsp";
        }

        // sends information of the result of the process to the user
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }

    private User getUserInfo(String login){
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("auth_db_pool");

        // database connection
        UserDao dao = new UserDao(poolName);
        return dao.retrieveUser(login);
    }

}
