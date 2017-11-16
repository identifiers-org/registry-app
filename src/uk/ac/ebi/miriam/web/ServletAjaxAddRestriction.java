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


import uk.ac.ebi.miriam.db.CuraDataTypeDao;
import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.RestrictionDao;
import uk.ac.ebi.miriam.db.RestrictionType;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * <p>Object which stores all the information about a data collection for curation purposes.
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
 * @version 20130304
 */
public class ServletAjaxAddRestriction extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 7577793776341692461L;
    private Logger logger = Logger.getLogger(ServletAjaxAddRestriction.class);
    
    
    /*
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletAjaxAddRestriction()
    {
        super();
    }
    
    
    /*
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        JSONObject jsonAnswer = new JSONObject();
        Boolean status = false;
        String msg = null;
        Integer categoryId = null;
        RestrictionType category = null;
        
        // recovery of the parameters
        //String user = request.getParameter("user");
        //String sessionId = request.getParameter("session");
        String branch = request.getParameter("phase");
        String collectionId = request.getParameter("id");
        String catIdStr = request.getParameter("cat");
        String desc = request.getParameter("desc");
        String link = request.getParameter("link");
        String linkDesc = request.getParameter("linkDesc");
        
        // cleaning
        if (null != desc)
        {
            desc = desc.trim();
        }
        if (null != link)
        {
            link = link.trim();
        }
        if (null != linkDesc)
        {
            linkDesc = linkDesc.trim();
        }
        if (null != catIdStr)
        {
            try
            {
                categoryId = Integer.parseInt(catIdStr);
            }
            catch (NumberFormatException e)
            {
                logger.warn("A user tried to hack the feature provided by 'ServletAjaxAddRestriction'!");
                logger.warn("Value submitted for the 'category' field (should have been an integer): " + catIdStr);
                categoryId = 0;
            }
        }
        
        // checks that the user is logged in
        HttpSession session = request.getSession();
        
        // the user is logged
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(session))
            {
                // checks that the restriction is valid
                if ((!desc.matches("\\s*")) && (null != categoryId) && (categoryId > 0))
                {
                    // if a link is provided, a link description should be too
                    if ((!link.matches("\\s*")) && ((null == linkDesc) || (linkDesc.matches("\\s*"))))
                    {
                        status = false;
                        msg = "Please provide a very short description explaining where does the link point to.";
                    }
                    else
                    {
                        String poolName = getServletContext().getInitParameter("miriam_db_pool");
                        
                        if (branch.equalsIgnoreCase("publ"))   // published data collection
                        {
                            // checks that the data collection exists
                            DataTypeDao dataDao = new DataTypeDao(poolName);
                            if (dataDao.dataTypeExists(collectionId))
                            {
                                // adds new restriction
                                RestrictionDao restrictDao = new RestrictionDao(poolName);
                                if (restrictDao.addRestrictionInPubl(collectionId, categoryId, desc, link, linkDesc))
                                {
                                    // retrieves the type of restriction
                                    category = restrictDao.getRestrictionType(categoryId);
                                    
                                    // flags the collection as restricted
                                    // TODO: should probably done only once...
                                    Boolean flagged = dataDao.setRestricted(collectionId);
                                    
                                    // updates the last modification date of the data collection
                                    dataDao.updateLastModifDate(collectionId);   // we don't care if the date of last modif did not work...
                                    
                                    status = true;
                                    msg = "A new restriction (" + category.getCategory() + ") has now been associated to this data collection!";
                                    if (! flagged)
                                    {
                                        msg += "\nSome information may not have been properly recorded, please contact 'biomodels-net-support@lists.sf.net'!";
                                    }
                                }
                                else   // failure when adding the restriction in the database 
                                {
                                    status = false;
                                    msg = "Unable to add the restriction (a technical issue happened)!";
                                }
                                restrictDao.clean();
                            }
                            else   // the data collection does not exist
                            {
                                status = false;
                                msg = "The data collection you are trying to update does not seem to exist!";
                            }
                            
                            dataDao.clean();
                        }
                        else if (branch.equalsIgnoreCase("cura"))  // data collection in the curation pipeline
                        {
                            // checks that the data collection exists
                            CuraDataTypeDao curaDao = new CuraDataTypeDao(poolName);
                            if (curaDao.existsById(collectionId))
                            {
                                // adds new restriction
                                RestrictionDao restrictDao = new RestrictionDao(poolName);
                                if (restrictDao.addRestrictionInCura(collectionId, categoryId, desc, link, linkDesc))
                                {
                                    // retrieves the type of restriction
                                    category = restrictDao.getRestrictionType(categoryId);
                                    
                                    // flags the collection as restricted
                                    // TODO: should probably done only once...
                                    Boolean flagged = curaDao.setRestricted(collectionId);
                                    
                                    // update the last modification date of the data collection
                                    curaDao.updateLastModifDate(collectionId);   // we don't care if the date of last modif did not work...
                                    
                                    status = true;
                                    msg = "A new restriction (" + category.getCategory() + ") has now been associated to this data collection!";
                                    if (! flagged)
                                    {
                                        msg += "\nSome information may not have been properly recorded, please contact 'biomodels-net-support@lists.sf.net'!";
                                    }
                                }
                                else   // failure when adding the restriction in the database 
                                {
                                    status = false;
                                    msg = "Unable to add the restriction (a technical issue happened)!";
                                }
                                restrictDao.clean();
                            }
                            else   // the data collection does not exist
                            {
                                status = false;
                                msg = "The data collection you are trying to curate does not seem to exist!";
                            }
                            curaDao.clean();
                        }
                        else   // invalid branch: should be either 'publ' or 'cura'
                        {
                            status = false;
                            msg = "Invalid request!";
                        }
                    }
                }
                else   // invalid request
                {
                    status = false;
                    msg = "Invalid request: please select a category and provide a description for the restriction.";
                }
            }
            else   // not enough privileges
            {
                status = false;
                msg = "You don't have enough privileges to perform this action!";
            }
        }
        else   // not logged
        {
            status = false;
            msg = "The access to this feature is restricted!";
        }
        
        // response
        try
        {
            jsonAnswer.put("status", status);
            if (status)
            {
                jsonAnswer.put("cat", category.getCategory());
                jsonAnswer.put("catId", categoryId);
                jsonAnswer.put("desc", desc);
                jsonAnswer.put("link", link);
                jsonAnswer.put("linkDesc", linkDesc);
            }
            jsonAnswer.put("msg", msg);
        }
        catch (JSONException e)
        {
            logger.error("JSONException raised while creating a JSON answer for ServletAjaxAddRestriction!");
            logger.error("Status: " + status + "; Message: " + msg);
            logger.error(e.getMessage());
        }
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        out.println(jsonAnswer.toString());
    }
    
    
    /*
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
}
