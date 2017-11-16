package uk.ac.ebi.miriam.web;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openid4java.OpenIDException;
import org.openid4java.association.AssociationSessionType;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.*;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.sreg.SRegMessage;
import org.openid4java.message.sreg.SRegResponse;
import org.openid4java.util.HttpClientFactory;
import org.openid4java.util.ProxyProperties;
import uk.ac.ebi.miriam.db.User;
import uk.ac.ebi.miriam.db.UserDao;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 17/09/2014
 *         Time: 09:32
 */
public class ServletOpenId extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

       private static final long serialVersionUID = 1865215447530932510L;
       private Logger logger = Logger.getLogger(ServletOpenId.class);

       private ConsumerManager manager;

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
       protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
       {
           String role = new String();
           RequestDispatcher view = null;
           boolean success = false;

           // to be able to retrieve UTF-8 elements from HTML forms
           request.setCharacterEncoding("UTF-8");

           // recovery of the parameters from the form
           String login = request.getParameter("openid_identifier");
           String referrer = request.getParameter("referrer");

           if (null != login)
           {
               login = login.trim();
           }

           if (null != referrer)
           {
               referrer = referrer.trim();
           }


           String message = "";

           if(login != null) {

               // retrieves the name of the database pool
               String poolName = getServletContext().getInitParameter("auth_db_pool");

               // database connection
               UserDao dao = new UserDao(poolName);


               if(!login.isEmpty()) {
                   // attempts to retrieve the details of the user
                   User user = dao.retrieveUser(login);

                   // the user is registered in the database
                   if (null != user) {
                       // tests if a user has access to the application 'miriam'
                       Boolean hasAccess = dao.checkUserAccess(user.getLogin(), dao.REGISTRY_APP);

                       // the user has access to this application
                       if (hasAccess) {
                           // retrieves the role of a specific user for a specific application
                           role = dao.retrieveUserRole(user.getLogin(), dao.REGISTRY_APP);

                           // creation of a new session, with attributes (login and role)
                           HttpSession session = request.getSession();
                           session.setAttribute("login", user.getLogin());
                           session.setAttribute("role", role);
                           if (!user.getFirstName().isEmpty())
                               session.setAttribute("fname", user.getFirstName());
                           else
                               session.setAttribute("fname", "Guest");

                           // update the date of last login (not important if failure to do so)
                           dao.updateUserLastLogin(user.getLogin(), role, dao.REGISTRY_APP);

                           success = true;
                           logger.info("The user '" + login + "' is now logged in!");
                       } else   // the user has no access to the app
                       {
                           logger.warn("The user '" + login + "' is not registered in the Registry, but tried to access it!");
                       }

                   } else   // write the user to database
                   {
                       user = new User();
                       user.setLogin(login);
                       user.setEmail(login);

                       //add user to the database
                       if (dao.addUser(user)) {
                           //add user role
                           if (dao.addUserRole(user.getLogin())) {
                               HttpSession session = request.getSession();
                               session.setAttribute("login", user.getLogin());
                               session.setAttribute("role", MiriamUtilities.USER_GENERAL);
                               if (!user.getFirstName().isEmpty())
                                   session.setAttribute("fname", user.getFirstName());
                               else
                                   session.setAttribute("fname", "Guest");

                               success = true;
                           } else
                               logger.warn("Error adding user role for :" + user.getLogin());

                       } else {
                           logger.warn("Error adding user: " + user.getLogin());
                       }
                   }

                   // cleaning
                   dao.clean();

                   if (user.getFirstName().isEmpty() || user.getLastName().isEmpty() || user.getOrganisation().isEmpty())
                       request.setAttribute("message", "Please complete your personal information");
               }
               else {
                   message = "Google sign in could not be authenticated. Please contact us via biomodels-net-support@lists.sf.net";
               }
           }

           // login successful
           if (success)
           {

               if ((null != referrer) && (! referrer.matches("\\s*")))
               {
                   logger.debug("login success, forward to: " + referrer);
                   response.sendRedirect("mdb?" + referrer);
               }
               else
               {
                   view = request.getRequestDispatcher("/user");
                   view.forward(request, response);
               }
           }
           else   // login failure
           {
               if(message.isEmpty()){
                   message = "Login details could not be authenticated. Please, try again.";
               }
               request.setAttribute("message", message );
               if ((null != referrer) && (! referrer.matches("\\s*")))
               {
                   request.setAttribute("referrer", referrer);
               }
               view = request.getRequestDispatcher("login.jsp");
               view.forward(request, response);
           }
       }

        private ProxyProperties getProxyProperties() {
            ProxyProperties proxyProps=null;
            String http_proxy = System.getenv("http_proxy");
            logger.debug("proxy: " + http_proxy);
            if (http_proxy != null) {
                proxyProps = new ProxyProperties();
                String host = http_proxy.substring(7,http_proxy.lastIndexOf(":"));
                String port = http_proxy.substring(http_proxy.lastIndexOf(":")+1);
                proxyProps.setProxyHostName(host);
                proxyProps.setProxyPort(Integer.parseInt(port));
            }
            return proxyProps;
        }

       /*
        * @see javax.servlet.GenericServlet#init()
        */
       public void init(ServletConfig config) throws ServletException {
           super.init(config);
           // --- Forward proxy setup (only if needed) ---
           ProxyProperties proxyProps = getProxyProperties();
           if (proxyProps != null) {
               logger.debug("ProxyProperties: " + proxyProps);
               HttpClientFactory.setProxyProperties(proxyProps);
           }

/*           manager = new ConsumerManager();
           manager.setAssociations(new InMemoryConsumerAssociationStore());
           manager.setNonceVerifier(new InMemoryNonceVerifier(5000));
           manager.setMinAssocSessEnc(AssociationSessionType.DH_SHA256);*/
           // Context env = null;
           /*
            * logger.debug("--> SignIn: 'init' method..."); try { // Obtain our environment naming context Context
            * initContext = new InitialContext(); logger.debug("SignIn: context ok..."); Context envContext = (Context)
            * initContext.lookup("java:/comp/env"); logger.debug("initial context ok..."); // Look up our data source pool =
            * (DataSource) envContext.lookup("jdbc/MiriamDB"); logger.debug("SignIn: pool ok..."); if (pool == null) {
            * logger.debug("poool is nulll"); throw new ServletException("'Miriam' is an unknown DataSource"); } else {
            * logger.debug("pool is not null."); // Allocate and use a connection from the pool //Connection conn =
            * pool.getConnection(); //... use this connection to access the database ... //conn.close(); } } catch
            * (NamingException ne) { logger.debug("Exception lauched: " + ne.getMessage()); throw new
            * ServletException(ne.getMessage()); } catch (Exception se) { logger.debug("Exception standard lauched:" +
            * se.getMessage()); } logger.debug("SignIn: init end.");
            */
       }
   }
