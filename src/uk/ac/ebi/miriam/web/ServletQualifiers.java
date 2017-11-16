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


import uk.ac.ebi.miriam.db.GroupQualifiers;
import uk.ac.ebi.miriam.db.QualifiersDao;

import java.io.IOException;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;


/**
 * <p>
 * Servlet for the dynamic display of the BioModels.net qualifiers (+ other useful information).
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
 * @version 20090514
 */
public class ServletQualifiers extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 426043741606433002L;
    private Logger logger = Logger.getLogger(ServletQualifiers.class);
    
    
    /*
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletQualifiers()
    {
        super();
    }
    
    
    /*
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String message = null;
        RequestDispatcher view = null;
        
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        // retrieves the list of qualifiers
        QualifiersDao dao = new QualifiersDao(poolName);
        List<GroupQualifiers> listOfQualifiers = dao.getAllQualifiers();
        
        // attaches the list of qualifiers to the response
        if (null != listOfQualifiers)
        {
            request.setAttribute("qualifiers", listOfQualifiers);
        }
        else
        {
            message = "Sorry, we are currently unable to retrieve the list of BioModels.net qualifiers!";
            logger.error("Unable to retrieve the list of BioModels.net qualifiers!");
        }
        
        if (null != message)
        {
            request.setAttribute("message", "<p>" + message + "</p>");
        }
        
        dao.clean();
        view = request.getRequestDispatcher("qualifiers.jsp");
        view.forward(request, response);
    }
    
    
    /*
     * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
}
