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


import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Engine (front controller) of the Registry web application: receives all the requests and transmits them to the dedicated servlets.
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
 * @version 20140423
 */
public class WebEngine extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -301588370558888237L;
    private enum sections {intro, browse, search, metadata, request, submit, standard, news, faq, contact, contribute, export, xml_export, resource, rdf, ws, edit, misc, about, restrictions, ws_help, media, docs, annotation, usage_anno, annotations, webservices, tags, uris, support, use, survey_urn, edit_tag, edit_anno, stats, signin, user, edit_tags, edit_ws, curation, error_404, health_check, health_check_details, health_check_history, resource_check, publish, users, admin_profiles, admin_profile};
    
    
    /** 
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public WebEngine()
    {
        super();
    }
    
    
    /** 
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String jsp = "static.jsp";   // default jsp to use
        HttpSession session = request.getSession(false);   // returns pre-existing session or null
        
        
        if (request.getParameter("section") != null)
        {
            String section = request.getParameter("section");
            
            sections enumSection = null;
            String question = null;
            
            // tests if the section exist in the list of sections
            try
            {
                enumSection = sections.valueOf(section);
            }
            catch (java.lang.IllegalArgumentException e)   // 404 page
            {
                enumSection = sections.valueOf("error_404");
            }
            
            switch (enumSection)
            {
                case intro:
                    jsp = "/displayHomePage";
                    break;
                case browse:
                    // TODO
                    // change this!
                    // get parameter for the data type
                    jsp = "/collections";
                    break;
                case search:
                    request.setAttribute("section", "search.html");
                    break;
                case metadata:
                    jsp = "/tags";
                    break;
                case request:
                    // get the parameter for the request needed
                    question = request.getParameter("request");
                    // if (question != null)
                    // {
                    request.setAttribute("request", question);
                    // }
                    jsp = "dynamic_WS.jsp";
                    break;
                case submit:
                    // request.setAttribute("section", "submission.html"); // old one
                    question = request.getParameter("request");
                    request.setAttribute("request", question);
                    jsp = "data_submit.jsp";

                    //if (MiriamUtilities.isSessionValid(session)) { request.setAttribute("section",
                    // "submission.html"); } else { request.setAttribute("section", "need_login.html"); }
                    break;
                case standard:
                    request.setAttribute("section", "standard.html");
                    break;
                case news:
                    // TODO
                    // change this!
                    request.setAttribute("section", "news.html");
                    break;
                case faq:
                    // TODO
                    // change this!
                    request.setAttribute("section", "faq.html"); // TODO
                    break;
                case contact:
                    request.setAttribute("section", "contact.html");
                    break;
                case contribute:
                	request.setAttribute("section", "contribute.html");
                	break;
                case export:
                    //request.setAttribute("section", "exports.html");   // old static version: now basic statistics
                    jsp = "/export";
                    break;
                case xml_export:
                    jsp = "/export/xml";
                    break;
                case resource:
                    jsp = "/resource";
                    break;
                case rdf:
                	jsp = "/rdf";
                	break;
                case ws:
                    request.setAttribute("section", "web_services.html");
                    break;
                case edit:
                    jsp = "/dataTypeEdit";
                    // only "admi' can edit an already existing data type if
                    // (MiriamUtilities.isUserAdministator(session)) { jsp =
                    // "/dataTypeEdit"; } else { request.setAttribute("section",
                    // "need_login.html"); }
                    break;
                /* qualifiers now provided from co.mbine.org
                case qualifiers:
                    //request.setAttribute("section", "qualifiers.html");
                    jsp = "/qualifiers";
                    break;
                */
                case misc:
                    jsp = "/misc";
                    break;
                case about:
                	request.setAttribute("section", "about.html");
                	break;
                case restrictions:
                    jsp = "/restrictions";
                    break;
                case ws_help:
                    request.setAttribute("section", "web_services_queries.html");
                    break;
                case media:
                    request.setAttribute("section", "media.html");
                    break;
                case docs:
                    request.setAttribute("section", "docs.html");
                    break;
                case annotation:
                    jsp = "/usage";
                    break;
                case usage_anno:
                    jsp = "/usage";
                    break;
                case annotations:
                    jsp = "/annotations";
                    break;
                case webservices:
                    jsp = "/webservices";
                    break;
                case tags:
                    jsp = "/tagsDisplay";
                    break;
                case uris:
                    request.setAttribute("section", "URIs.html");
                    break;
                case support:
                    jsp = "/support";
                    break;
                case use:
                    request.setAttribute("section", "use.html");
                    break;
                case survey_urn:
                    request.setAttribute("section", "SurveyURN.html");
                    break;
                case edit_tag:
                    jsp = "/tagEdit";
                    break;
                case edit_anno:
                    jsp = "/annoEdit";
                    break;
                /* qualifiers now provided from co.mbine.org
                case edit_qualifiers:
                    jsp = "/editQualifiers";
                    break;
                case qualifiers_xml:
                    jsp = "/qualifiers/xml";
                    break;
                */
                case stats:
                    jsp = "/stats";
                    break;
                case signin:
                    if (MiriamUtilities.isSessionValid(session))
                    {
                        //request.setAttribute("section", "already_logged.html");
                        String login = (String) session.getAttribute("login");
                        request.setAttribute("message", "Sorry, you are already logged in as '" + login + "'.<br />If you want to switch of user, you can use the [<a href=\"signOut\" title=\"Sign Out\">Sign Out</a>] button available via the account page at the top right of the page.");
                        jsp = "/user";
                    }
                    else
                    {
                        jsp = "login.jsp";
                    }
                    break;
                case user:
                    jsp = "/user";
                    break;
                case edit_tags:
                    jsp = "/tagsEdit";
                    break;
                case edit_ws:
                    jsp = "/webServicesEdit";
                    break;
                case curation:
                    jsp = "/curation";
                    break;
                case error_404:
                    jsp = "error_404.jsp";
                    break;
                case health_check:
                	jsp = "/resourcesCheck";
                	break;
                case health_check_details:
                	jsp = "/resourceCheckDetails";
                	break;
                case health_check_history:
                    jsp = "/resourceCheckHistory";
                    break;
                case resource_check:
                    jsp = "/checkResourceHealth";
                    break;
                case publish:
                    jsp = "/publishDataTypeRequested";
                    break;
                case users:
                    jsp = "/users";
                    break;
                case admin_profiles:
                    jsp = "/adminProfiles";
                    break;
                case admin_profile:
                    jsp = "/adminProfile";
                    break;
                default:
                	jsp = "/displayHomePage";
                    break;
            }
        }
        else
        {
        	jsp = "/displayHomePage";
        }
        
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
}
