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


import uk.ac.ebi.miriam.db.MyMiriamDao;
import uk.ac.ebi.miriam.db.MyMiriamDataType;
import uk.ac.ebi.miriam.db.MyMiriamResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * <p>
 * Servlet for the updating the list of data collections (and their preferred resource) of a given registered profile.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2014 BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20140307
 */
public class ServletUpdateMyMiriamDatatypes extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -2670988826143882545L;
    
    
    /*
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletUpdateMyMiriamDatatypes()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Map<String, String> fromForm = new HashMap<String, String>();   // data type identifier - resource identifier
        List<String> selected = new ArrayList<String>();   // data type identifier
        List<String> messages = new ArrayList<String>();   // feedback to the user
        String jsp = null;
        
        // retrieves the session of the current user
        HttpSession session = request.getSession();
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session))
        {
 /*           // the user has administration privileges
            if (MiriamUtilities.isUserAdministator(session))
            {
*/                // retrieves the internal identifier of the project
                String projectIdStr = request.getParameter("projectId");
                
                // retrieves the query which was used: defines the subset of data types displayed when requesting the update
                String query = request.getParameter("query");
                
                // basic checks
                if ((null != projectIdStr) && (null != query))
                {
                    // conversion to integer
                    Integer projectId = Integer.parseInt(projectIdStr);
                    
                    // retrieves the name of the database pool
                    String poolName = getServletContext().getInitParameter("miriam_db_pool");
                    
                    // database access
                    MyMiriamDao myMiriamDao = new MyMiriamDao(poolName);
                    
                    if (myMiriamDao.isProfileExisting(projectId))
                    {
                        // retrieves the complete list of parameters (some might not have been modified)
                        Enumeration<String> params = request.getParameterNames();
                        
                        // no specific order: sorting the parameters
                        for (String param: Collections.list(params))
                        {
                            // resources are in a 'select' tag which name is of the kind "resources_MIR:00000004" (the second part being the identifier of a data type)
                            if (param.startsWith("resources_"))
                            {
                                String resource = request.getParameterValues(param)[0];
                                // checks that there is a selected preferred resource
                                if ((null != resource) && (resource.length() > 0))
                                {
                                    fromForm.put(param.substring(10), request.getParameterValues(param)[0]);   // there should be only one value, hence the '[0]'
                                }
                                else   // the data type does not have a selected preferred resource, but it might not be selected itself
                                {
                                    // do nothing: this test is actually performed a few lines below
                                }
                            }
                            else if (param.matches("^MIR:\\d{8}$"))   // only data types (which have been explicitly selected by the user)
                            {
                                selected.add(param);
                            }
                            else   // not recognised parameter
                            {
                                // do nothing: who cares?
                            }
                        }
                        
                        // checks that all selected data types have a selected preferred resource
                        for (String data: selected)
                        {
                            // the selected data type has a selected preferred resource
                            if (! fromForm.containsKey(data))
                            {
                                messages.add("The newly selected data type " + data + " does not have a preferred resource: it has therefore been ignored from this update!");
                            }
                            else
                            {
                                // do nothing: everything is fine
                            }
                        }
                        
                        // checks that unselected data type with preferred resource selected are removed from the list (they removed)
                        List<String> tempToRemove = new ArrayList<String>();
                        for (String key: fromForm.keySet())
                        {
                            if (! selected.contains(key))
                            {
                                tempToRemove.add(key);   // impossible to inside the for loop remove elements
                            }
                        }
                        for (String key: tempToRemove)
                        {
                            fromForm.remove(key);
                        }
                        tempToRemove.clear();
                        
                        // retrieves list of current data types (with name starting by the user query) associated to the project
                        List<MyMiriamDataType> currentsList = myMiriamDao.getDataTypesStartingByOfProfile(projectId, query);
                        
                        // creates a map of the current data types, for easy access (key: identifier of the data type, value: identifier of the preferred resource)
                        Map<String, String> currents = new HashMap<String, String>();
                        for (MyMiriamDataType data: currentsList)
                        {
                            for (MyMiriamResource res: data.getResources())
                            {
                                if (res.isPreferred())
                                {
                                    currents.put(data.getId(), res.getId());
                                    break;
                                }
                            }
                        }
                        
                        // retrieves list of data types to update
                        Map<String, String> toUpdate = new HashMap<String, String>();
                        for (String key: fromForm.keySet())
                        {
                            if ((null != fromForm.get(key)) && (null != currents.get(key)))
                            {
                                if (! fromForm.get(key).equals(currents.get(key)))
                                {
                                    toUpdate.put(key, fromForm.get(key));
                                }
                            }
                        }
                        // updates the database
                        for (String key: toUpdate.keySet())
                        {
                            if (myMiriamDao.updateMyDatatype(projectId, key, toUpdate.get(key)))
                            {
                                messages.add("The preferred resource of the data collection " + key + " has been changed from to " + toUpdate.get(key) + ".");
                            }
                            else   // update failed
                            {
                                messages.add("Sorry, we were unable to change the preferred resource of the data collection " + key + " to " + toUpdate.get(key) + "!");
                            }
                        }
                        
                        // retrieves list of data types to add
                        Map<String, String> toAdd = new HashMap<String, String>();
                        for (String key: fromForm.keySet())
                        {
                            if (! currents.containsKey(key))
                            {
                                toAdd.put(key, fromForm.get(key));
                            }
                        }
                        // updates the database
                        for (String key: toAdd.keySet())
                        {
                            if (myMiriamDao.addMyDatatype(projectId, key, toAdd.get(key)))
                            {
                                messages.add("The data collection " + key + " (preferred resource: " + toAdd.get(key) + ") has been added to the profile.");
                            }
                            else   // addition failed
                            {
                                messages.add("Sorry, we were unable to add the data collection " + key + " (preferred resource: " + toAdd.get(key) + ") to the profile!");
                            }
                        }
                        
                        // retrieve list of data types to remove
                        List<String> toRemove = new ArrayList<String>();
                        for (String key: currents.keySet())
                        {
                            if (! fromForm.containsKey(key))
                            {
                                toRemove.add(key);
                            }
                        }
                        // updates the database
                        for (String id: toRemove)
                        {
                            if (myMiriamDao.removeMyDatatype(projectId, id))
                            {
                                messages.add("The previously selected data collection " + id + " has been removed from the profile.");
                            }
                            else   // deletion failed
                            {
                                messages.add("Sorry, we were unable to remove the data collection " + id + " from the profile!");
                            }
                        }
                        
                        request.setAttribute("id", projectIdStr);
                        jsp = "/adminProfile";
                    }
                    else   // project does not exist
                    {
                        messages.clear();   // no need to keep previous messages: no action has been performed
                        messages.add("Sorry, we were unable to perform the requested action as you did not provide a profile.");
                        jsp = "/adminProfiles";
                    }
                }
                else   // not project identifier provided
                {
                    messages.clear();   // no need to keep previous messages: no action has been performed
                    messages.add("Sorry, we were unable to perform the requested action as you provided incomplete or incorrect profile information.");
                    jsp = "/adminProfiles";
                }
/*            }
            else   // user hasn't enough privileges
            {
                messages.clear();   // no need to keep previous messages: no action has been performed
                messages.add("Sorry, you are not authorised to access this page!");
                jsp = "/user";
            }*/
        }
        else   // user not logged
        {
            messages.clear();   // no need to keep previous messages: no action has been performed
            messages.add("Sorry, you need to be authenticated to access this page!");
            request.setAttribute("referrer", request.getQueryString());
            jsp = "login.jsp";
        }
        
        if (null != messages)
        {
            StringBuilder temp = new StringBuilder();
            temp.append("<ul style=\"padding:0; margin:0;\">");
            for (int i=0; i<messages.size(); ++i)
            {
                temp.append("<li>" + messages.get(i) + "</li>");
                /*
                if (i<messages.size()-1)
                {
                    temp.append("<br />");
                }
                */
            }
            temp.append("</ul>");
            request.setAttribute("message", temp.toString());
        }
        RequestDispatcher view = request.getRequestDispatcher(jsp);
        view.forward(request, response);
    }
}
