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


import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.SimpleDataType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * <p>
 * Servlet that handles the deprecation of one data collection in the database (1st step: displays a form to the user).
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
 * @version 20140310
 */
public class ServletDataCollectionDeprecate extends ServletTemplate
{
    private static final long serialVersionUID = -3063704726612619124L;
    
    
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response, StringBuilder page)
    {
        // retrieves the user logged who asked for the action
        HttpSession session = request.getSession();
        
        // the user is logged in
        if (MiriamUtilities.isUserAuthorised(session))
        {
            // the user has curation privileges
            if (MiriamUtilities.isUserCurator(session))
            {
                // retrieves the id of the data collection to be deprecated
                String collectionId = request.getParameter("collection2deprecate");
            	
                // retrieves minimal info about data collection
                DataTypeDao dao = new DataTypeDao(getPoolName());
                SimpleDataType collection = dao.getSimpleDataTypeById(collectionId);
                dao.clean();   // cleaning
                
                if (null != collection)
                {
                	request.setAttribute("collection", collection);
                	page.append("data_delete.jsp");
                }
                else
                {
                	request.setAttribute("message", "The data collection doesn't exist in the Registry.");
                    page.append("/collections");
                }
            }
            else
            {
            	request.setAttribute("message", "Sorry, you are not authorised to access this page!");
            	page.append("/user");
            }
        }
        else
        {
        	request.setAttribute("message", "You need to be authenticated to access this feature!");
            request.setAttribute("referrer", request.getQueryString());
            page.append("login.jsp");
        }
    }
}
