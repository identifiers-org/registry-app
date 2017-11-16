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


import java.io.IOException;
import java.util.GregorianCalendar;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * <p>Servlet which handles the process of a submitted support form.
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
 * @version 20160827
 */
public class ServletSupportProcess extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
    private static final long serialVersionUID = -4543282375315531017L;
    
    
    /**
     * @see javax.servlet.http.HttpServlet#HttpServlet()
     */
    public ServletSupportProcess()
    {
        super();
    }
    
    
    /**
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String message = null;
        
        // retrieves the email address of the user
        String email = request.getParameter("email");
        
        // retrieves the type of query
        String type = request.getParameter("type");
        
        // retrieves the message
        String query = request.getParameter("query");
        
        // retrieves the possible information associated with the submission form (from where the form has been filled)
        String info = request.getParameter("info");
        
        // retrieves the value of the spam detector
        String spam = request.getParameter("pourriel");
        
        // is this query a spam? no.
        if (MiriamUtilities.isEmpty(spam))
        {
            // retrieves the version of the web application (sid/local, alpha, main or demo)
            String version = getServletContext().getInitParameter("version");
            
            // retrieves the email addresses of the administrator and the curators
            String emailAdr = getServletContext().getInitParameter("admin.email");
            String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");
            
            // retrieves the user logged who asked for the action
            HttpSession session = request.getSession();
            String user = (String) session.getAttribute("login");
            
            // sends an email to the admin and curator(s)
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("\nUser support needed for the Registry (" + version + "):\n");
            emailBody.append("\n+ Query:");
            emailBody.append("\n" + query + "\n");
            emailBody.append("\n+ Type: " + type);
            emailBody.append("\n+ User email: " + email);
            if (!MiriamUtilities.isEmpty(user))
            {
                emailBody.append(" (logged as: " + user + ")");
            }
            emailBody.append("\n+ From: " + info);
            emailBody.append("\n\nIP address: " + request.getHeader("x-cluster-client-ip"));
            emailBody.append("\nUser Agent: " + request.getHeader("user-agent"));
            emailBody.append("\nAccept: " + request.getHeader("accept"));
            emailBody.append("\nAccept language: " + request.getHeader("accept-language"));
            emailBody.append("\nAccept charset: " + request.getHeader("accept-charset"));
            emailBody.append("\nReferer: " + request.getHeader("referer"));
            GregorianCalendar cal = new GregorianCalendar();
            emailBody.append("\n+ Date: " + cal.getTime());
            emailBody.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
            MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] user query", emailBody.toString(), "text/plain; charset=UTF-8");
            
            message = "Your query has been transmitted successfully to the support team of the Registry. Thank you.";
        }
        else   // this is highly possible that this query is a spam
        {
            message = "Your query has been discarded because of spam suspicion!";
        }
        
        request.setAttribute("message", "<p>" + message + "</p>");
        RequestDispatcher view = request.getRequestDispatcher("support.jsp");
        view.forward(request, response);
    }
}
