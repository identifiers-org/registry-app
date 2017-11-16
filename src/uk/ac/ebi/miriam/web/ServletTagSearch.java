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


import uk.ac.ebi.miriam.db.SimpleDataType;
import uk.ac.ebi.miriam.db.Tag;
import uk.ac.ebi.miriam.db.TagDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import org.apache.log4j.Logger;


/**
 * <p>Servlet which Handles the search for data types based on a list of tags.
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
 * @version 20090112
 */
public class ServletTagSearch extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -1355997278886269594L;
    //private Logger logger = Logger.getLogger(ServletTagSearch.class);
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletTagSearch()
    {
        super();
    }
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // retrieves the ids of all the tags which need to be included in the search
        String[] tagsId = request.getParameterValues("tags2search");
        String qualifier = request.getParameter("qualifier");
        
        RequestDispatcher view = null;
        
        if ((null != tagsId) && (tagsId.length != 0))
        {
	        // retrieves the name of the database pool
	        String poolName = getServletContext().getInitParameter("miriam_db_pool");
	        
	        TagDao tagDao = new TagDao(poolName);
	        List<Tag> tags = new ArrayList<Tag>();
	        
	        // retrieves the list of tags from their identifier
	        for (String id: tagsId)
	        {
	            Tag tmp = tagDao.getTagFromId(id);
	            tags.add(tmp);
	        }
	        
	        // retrieves the list of data types associated with theses tags
	        List<SimpleDataType> data = null;
	        if ((null != qualifier) && (qualifier.equalsIgnoreCase("any")))
	        {
	            data = tagDao.getDataTypesFromAnyTagIds(tagsId);
	        }
	        else
	        {
	            data = tagDao.getDataTypesFromAllTagIds(tagsId);
	        }
	        
	        // cleaning
	        tagDao.clean();
	        
	        request.setAttribute("tagCount", tags.size());   // number of tags provided by the user
	        request.setAttribute("dataCount", data.size());   // number of data types returned by the search
	        request.setAttribute("data", data);   // list of data types
	        request.setAttribute("tags", tags);   // list of tags
	        view = request.getRequestDispatcher("metadata_links.jsp");
        }
        else   // no tag selected
        {
        	// go back to the tag cloud
        	request.setAttribute("message", "You need to select one or more tag(s) before launching a search!");
        	view = request.getRequestDispatcher("/metadata");
        }
        
        view.forward(request, response);
    }
}
