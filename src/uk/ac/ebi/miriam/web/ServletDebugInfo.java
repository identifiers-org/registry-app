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


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
* Gathers and displays some information about the code running, for testing and debug purposes.
*
* @author Camille Laibe
* @version 20100319
*/
public class ServletDebugInfo extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = 8375871900558867032L;
    
    
    /*
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletDebugInfo()
    {
        super();
    }
    
    
    /*
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        RequestDispatcher view = null;
        InetAddress localMachine;
        String hostName = null;
        
        // retrieves the name of the machine which is currently used to serve the user
        try
        {
            localMachine = InetAddress.getLocalHost();
            hostName = localMachine.getHostName();   // will return 'tomcat-11.ebi.ac.uk' or 'tomcat-12.ebi.ac.uk'
        }
        catch (UnknownHostException e)
        {
            hostName = "unknown";
        }
        
        request.setAttribute("server", hostName);
        view = request.getRequestDispatcher("debug.jsp");
        view.forward(request, response);
    }
    
    
    /*
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
}
