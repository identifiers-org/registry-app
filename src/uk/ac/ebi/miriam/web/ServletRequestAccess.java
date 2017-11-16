package uk.ac.ebi.miriam.web;

import org.apache.log4j.Logger;
import uk.ac.ebi.miriam.db.Ownership;
import uk.ac.ebi.miriam.db.OwnershipDao;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 06/10/2014
 *         Time: 11:52
 */
public class ServletRequestAccess extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

    private static final long serialVersionUID = 1865215447530932510L;
    private Logger logger = Logger.getLogger(ServletRequestAccess.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accessResource = request.getParameter("accessResource");
        String loggedInUser = request.getParameter("loggedInUser");
        String collectionid = request.getParameter("collectionid");

        // retrieves the version of the web application (sid/local, alpha, main or demo)
        String version = getServletContext().getInitParameter("version");

        // retrieves the email addresses of the administrator and the curators
        String emailAdr = getServletContext().getInitParameter("admin.email");
        String[] emailsCura = getServletContext().getInitParameter("curators.email").split(",");

        String poolName = getServletContext().getInitParameter("auth_db_pool");

        OwnershipDao ownershipDao = new OwnershipDao(poolName);
        boolean ownershipRequested = ownershipDao.addOwnership(loggedInUser,accessResource);
        if(ownershipRequested){
            String emailBody = "User " + loggedInUser + " requests to become a maintainer to the Resource " + accessResource;
        //    MailFacade.send("Registry-" + version + "@ebi.ac.uk", emailAdr, emailsCura, "[Registry] Ownership Request for resource '" + accessResource + "'", emailBody, "text/plain; charset=UTF-8");
        }

        response.sendRedirect("collections/"+collectionid);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
