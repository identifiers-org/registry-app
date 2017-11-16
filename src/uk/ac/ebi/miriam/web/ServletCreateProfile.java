package uk.ac.ebi.miriam.web;

import org.apache.log4j.Logger;
import org.jasypt.util.password.StrongPasswordEncryptor;
import uk.ac.ebi.miriam.db.MyMiriamDao;
import uk.ac.ebi.miriam.db.User;
import uk.ac.ebi.miriam.db.UserDao;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 22/10/2014
 *         Time: 12:55
 */
public class ServletCreateProfile extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet  {
    private static final long serialVersionUID = 1865215447530932510L;
    private Logger logger = Logger.getLogger(ServletCreateProfile.class);


           /*
        * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
        */
       protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
       {
           doPost(request, response);
       }


       /*
        * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
        */
       protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

           RequestDispatcher view = null;
           String message = request.getParameter("message");
           // retrieves the name of the database pool
           String poolName = getServletContext().getInitParameter("miriam_db_pool");

           // access to the database
           MyMiriamDao myMiriamDao = new MyMiriamDao(poolName);

           String login = (String)request.getSession().getAttribute("login");
           String name = request.getParameter("name");
           String shortname = request.getParameter("shortname");
           String description = request.getParameter("description");
           String status = request.getParameter("p_status");
           String key = request.getParameter("key");

           if(!myMiriamDao.isProfileExisting(shortname)){
               int p_status = 1 ;
               if(status != null && status.equals("private")){
                   p_status = 0;
               }

               String encryptedKey = "";
               if(key!=null) {
                   StrongPasswordEncryptor keyEncryptor = new StrongPasswordEncryptor();
                   encryptedKey = keyEncryptor.encryptPassword(key);
               }

               boolean result = myMiriamDao.createProfile(login,name,shortname,description,p_status, encryptedKey, getUserInfo(login).getEmail());
               if (result){
                   message = "Successully created the new profile: " + shortname;
               }
               else
               {
                   message = "Error occured while creating the new profile " + shortname + ". Please try again.";
               }
           }
           else{
               request.setAttribute("name",name);
               request.setAttribute("description", description);
               request.setAttribute("key", key);
               request.setAttribute("p_status", status);
               message = "Sorry this profile short name '" + shortname + "' already exists. Please select a different name.";
           }

           if (null != message)
           {
                   request.setAttribute("message", message);
           }

           view = request.getRequestDispatcher("/mdb?section=admin_profiles");
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
