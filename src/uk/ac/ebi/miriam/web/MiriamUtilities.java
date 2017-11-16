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


import uk.ac.ebi.miriam.db.DbPoolConnect;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;


/**
 * <p>Useful methods for the session tracking, handling ResultSet, and other cool stuff...
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2014  BioModels.net (EMBL - European Bioinformatics Institute)
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
public class MiriamUtilities
{
    private static Logger logger = Logger.getLogger(MiriamUtilities.class);   // static logger in a web app: not good?
    // user who can add and modify data types
    public static final String USER_AUTHORIZED = "miriam";
    // god
    public static final String USER_ADMIN = "admin";
    // curator
    public static final String USER_CURA = "cura";
    // general user
    public static final String USER_GENERAL = "user";
    private static final String IDENTIFIERS_ORG_ROOT_URL = "http://identifiers.org";
    
    
    /**
     * Tests if a session is valid (equals the user is logged).
     * @param session current session
     * @return boolean true is a user is logged, false if not
     */
    public static boolean isSessionValid(HttpSession session)
    {
        if ((session != null) && (session.getAttribute("login") != null) && (session.getAttribute("role") != null))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * Tests if a user is logged in, and if true, if he is an authorised member ('cura' or 'admin').
     * @param session current session
     * @return boolean answer to the question: is the user an authorised member?
     */
    public static boolean isUserAuthorised(HttpSession session)
    {
        boolean result = false;
        
        if (isSessionValid(session))
        {
            String login = (String) session.getAttribute("login");
            String role = (String) session.getAttribute("role");
            if ((null != role) && (null != login) && (! login.matches("\\s*")) && ((role.equalsIgnoreCase(USER_ADMIN)) || (role.equalsIgnoreCase(USER_CURA)) || (role.equalsIgnoreCase(USER_GENERAL))))
            {
                result = true;
            }
        }
        
        return result;
    }

    public static boolean isUserGeneral(HttpSession session)
    {
        boolean result = false;

        if (isSessionValid(session))
        {
            String login = (String) session.getAttribute("login");
            String role = (String) session.getAttribute("role");
            if ((null != role) && (null != login) && (! login.matches("\\s*")) && (role.equalsIgnoreCase(USER_GENERAL)))
            {
                result = true;
            }
        }

        return result;
    }
    
    
    /**
     * Tests if a user is logged in, and if true, if he is an administrator.
     * @param session current session
     * @return boolean answer to the question: is the user an administrator?
     */
    public static boolean isUserAdministator(HttpSession session)
    {
        boolean result = false;
        
        if (isSessionValid(session))
        {
            String login = (String) session.getAttribute("login");
            String role = (String) session.getAttribute("role");
            if ((null != login) && (! role.matches("\\s*")) && (null != role) && (role.equalsIgnoreCase(USER_ADMIN)))
            {
                result = true;
            }
        }
        
        return result;
    }
    
    
    /**
     * Tests if a user has curator's rights (therefore if he is a curator or an administrator).
     * @param session current session
     * @return boolean answer to the question: has the user curator's rights
     */
    public static boolean isUserCurator(HttpSession session)
    {
        boolean result = false;
        
        if (isSessionValid(session))
        {
            String login = (String) session.getAttribute("login");
            String role = (String) session.getAttribute("role");
            if ((null != login) && (! role.matches("\\s*")) && (null != role) && ((role.equals(USER_ADMIN)) || (role.equals(USER_CURA))))
            {
                result = true;
            }
        }
        
        return result;
    }
    
    
    /**
     * Counts the number of rows in a <code>ResultSet</code> (like 'getColumnCount()' for the columns)
     * 
     * @param <code>ResultSet</code> object already created
     * @return int number of rows in the <code>ResultSet</code> object
     */
    public static int getRowCount(ResultSet data)
    {
        int result = -1;
        
        try
        {
            data.last();
            result = data.getRow();
            data.first();
        }
        catch (SQLException e)
        {
            logger.info("An error occurred while counting the rows of a ResultSet!");
            logger.info("SQLException exception raised: " + e.getMessage());
        }
        
        return result;
    }
    
    
    /**
     * Converts a <code>ResultSet</code> (from a SQL query) to a <code>List</code>
     * 
     * @param result result of a SQL query
     * @return a <code>List</code> object with all the elements in the first field (column) of the <code>ResultSet</code>
     */
    public static List<String> ArrayConvert(ResultSet result)
    {
        List<String> conv = new ArrayList<String>();
        
        int nbLines = DbPoolConnect.getRowCount(result);
        
        for (int i = 1; i <= nbLines; ++i)
        {
            try
            {
                conv.add(result.getString(1));
                result.next();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the conversion of a ResultSet to an ArrayList!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
        }
        
        return conv;
    }
    
    
    /**
     * Converts a <code>ResultSet</code> (from a SQL query) to a <code>List</code>, with a transformation of the elements (for example,if
     * they are designed to be included in a physical URL, for valid XHTML links)
     * 
     * @param result result of a SQL query
     * @return a <code>List</code> object with all the elements in the first field (column) of the <code>ResultSet</code>
     */
    public static List<String> ArrayConvert(ResultSet result, boolean URL)
    {
        List<String> conv = new ArrayList<String>();
        
        int nbLines = DbPoolConnect.getRowCount(result);
        for (int i = 1; i <= nbLines; ++i)
        {
            try
            {
                if (URL)
                {
                    conv.add(transURL(result.getString(1), '&', "&amp;"));
                }
                else
                {
                    conv.add(result.getString(1));
                }
                result.next();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the conversion of a ResultSet to an ArrayList!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
        }
        
        return conv;
    }
    
    
    /**
     * Converts a <code>ResultSet</code> (from a SQL query) to a <code>List</code>
     * 
     * @param result result of a SQL query
     * @param nbCol number of columns in the ResultSet to convert
     * @return a <code>List</code> object with all the elements in the 'nbCols' first fields (columns) of the <code>ResultSet</code>
     */
    public static List ArrayConvert(ResultSet result, int nbCol)
    {
        List conv = new ArrayList();
        List[] temp = new ArrayList[nbCol];
        
        for (int i = 0; i < nbCol; ++i)
        {
            temp[i] = new ArrayList<String>();
        }
        
        int nbLines = DbPoolConnect.getRowCount(result);
        for (int i = 0; i < nbLines; ++i)
        {
            try
            {
                for (int j = 0; j < nbCol; ++j)
                {
                    temp[j].add(result.getString(j + 1));
                }
                result.next();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the conversion of a ResultSet to an ArrayList!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
        }
        
        for (int i = 0; i < nbCol; ++i)
        {
            conv.add(temp[i]);
        }
        
        return conv;
    }
    
    
    /**
     * Converts a <code>ResultSet</code> (from a SQL query) to a <code>List</code>, with a transformation of the elements (for example,if
     * they are designed to be included in a physical URL, for valid XHTML links)
     * 
     * @param result result of a SQL query
     * @param nbCol number of columns in the ResultSet to convert
     * @param URL indicates if the String values need or not to be converted into a XHTML valid format (cf. '&')
     * @return a <code>List</code> object with all the elements in the 'nbCols' first fields (columns) of the <code>ResultSet</code>
     */
    public static List ArrayConvert(ResultSet result, int nbCol, boolean URL)
    {
        List conv = new ArrayList();
        List[] temp = new ArrayList[nbCol];
        
        for (int i = 0; i < nbCol; ++i)
        {
            temp[i] = new ArrayList();
        }
        
        int nbLines = DbPoolConnect.getRowCount(result);
        for (int i = 0; i < nbLines; ++i)
        {
            try
            {
                for (int j = 0; j < nbCol; ++j)
                {
                    if (URL)
                    {
                        temp[j].add(transURL(result.getString(j + 1), '&', "&amp;"));
                    }
                    else
                    {
                        temp[j].add(result.getString(j + 1));
                    }
                }
                result.next();
            }
            catch (SQLException e)
            {
                logger.warn("An exception occured during the conversion of a ResultSet to an ArrayList!");
                logger.warn("SQL Exception raised: " + e.getMessage());
            }
        }
        
        for (int i = 0; i < nbCol; ++i)
        {
            conv.add(temp[i]);
        }
        
        return conv;
    }
    
    
    /**
     * Converts a <code>ResultSet</code> from a SQL query (only the first element) to a <code>String</code>
     * 
     * @param result result of a SQL query
     * @return a <code>String</code> object with the first element (first column, first row) of the <code>ResultSet</code>
     */
    public static String StringConvert(ResultSet result)
    {
        String conv = new String();
        
        try
        {
            if (result.first())
            {
                conv = result.getString(1);
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occured during the conversion of a ResultSet to a String!");
            logger.warn("SQL Exception raised: " + e.getMessage());
        }
        
        return conv;
    }
    
    
    /**
     * Converts a <code>ResultSet</code> from a SQL query (only the first element) to a <code>String</code>
     * 
     * @param result result of a SQL query
     * @param field name of the field to retrieve the value from
     * @return a <code>String</code> object with the first element (first column, first row) of the <code>ResultSet</code>
     */
    public static String StringConvert(ResultSet result, String field)
    {
        String conv = new String();
        
        try
        {
            if (result.first())
            {
                conv = result.getString(field);
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occured during the conversion of a ResultSet to a String!");
            logger.warn("SQL Exception raised: " + e.getMessage());
        }
        
        return conv;
    }
    
    
    /**
     * Converts a <code>ResultSet</code> from a SQL query (only the first element) to a <code>int</code>
     * 
     * @param result result of a SQL query
     * @return an <code>int</code> with the first element (first column, first row) of the <code>ResultSet</code>
     */
    public static int intConvert(ResultSet result)
    {
        int conv = 0;
        
        try
        {
            if (result.first())
            {
                conv = result.getInt(1);
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occured during the conversion of a ResultSet to an int!");
            logger.warn("SQL Exception raised: " + e.getMessage());
        }
        
        return conv;
    }
    
    
    /**
     * Converts a <code>ResultSet</code> from a SQL query (only the first element) to a <code>int</code>
     * 
     * @param result result of a SQL query
     * @param field name of the field to retrieve the value from
     * @return an <code>int</code> with the first element (first column, first row) of the <code>ResultSet</code>
     */
    public static int intConvert(ResultSet result, String field)
    {
        int conv = 0;
        
        try
        {
            if (result.first())
            {
                conv = result.getInt(field);
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occured during the conversion of a ResultSet to an int!");
            logger.warn("SQL Exception raised: " + e.getMessage());
        }
        
        return conv;
    }
    
    
    /**
     * Replaces a substrings in a <code>String</code>
     * 
     * @param str original String
     * @param pattern
     * @param replace
     * @return modified character string
     */
    public static String replace(String base, String pattern, String replace)
    {
        int begin = 0;
        int end = 0;
        StringBuffer result = new StringBuffer();
        
        while ((end = base.indexOf(pattern, begin)) >= 0)
        {
            result.append(base.substring(begin, end));
            result.append(replace);
            begin = end + pattern.length();
        }
        result.append(base.substring(begin));
        
        return result.toString();
    }
    
    
    /**
     * Converts a URL into a (X)HTML valid way: replace '&' by '&amp;'
     * 
     * @param url physical URL
     * @return the same URL, but W3C valid
     */
    public static String urlConvert(String url)
    {
        //String valid = replace(url, "&", "&amp;");   // bad old way
        String valid = StringEscapeUtils.escapeHtml4(url);
        
        return valid;
    }
    
    
    /**
     * Returns the String passed in parameter (used for example in URLs to have valid XHTML links), replacing the
     * 'pattern' character by the "replace" string
     * 
     * @param original character string
     * @param pattern
     * @param replace
     * @return modified character string
     */
    public static String transURL(String str, char pattern, String replace)
    {
        String newStr = "";
        
        for (int j = 0; j < str.length(); ++j)
        {
            if (str.charAt(j) == pattern)
            {
                newStr += replace;
            }
            else
            {
                newStr += str.charAt(j);
            }
        }
        
        return newStr;
    }
    
    
    /**
     * Tests if a string is composed only of space(s) or empty or null
     * 
     * @param str character string
     * @return response to the question: "is this character string only composed of space(s)?"
     */
    public static boolean isEmpty(String str)
    {
        boolean space = true;
        
        if ((str == null) || (str.equalsIgnoreCase("")))
        {
            return space;   // true
        }
        else
        {
            for (int i = 0; i < str.length(); ++i)
            {
                if (str.charAt(i) != ' ')
                {
                    space = false;
                }
            }
        }
        
        return space;
    }
    
    
    /**
     * Returns a new <code>String</code> equivalent to the string in parameter, but with all the spaces replaced by '%20' (to have
     * valid XHTML links)
     * 
     * @param oldStr a string (usually a name with space)
     * @return the string in parameter without any space but "%20" instead
     */
    public static String nameTrans(String oldStr)
    {
        String newStr = "";
        
        for (int j = 0; j < oldStr.length(); ++j)
        {
            if (oldStr.charAt(j) == ' ')
            {
                newStr += "%20";
            }
            else
            {
                newStr += oldStr.charAt(j);
            }
        }
        
        return newStr;
    }
    
    
    /**
     * Returns the data type part of a URI. Basically the data type part corresponds to everything before the character
     * "#" or the last ":".
     * 
     * @param uri a URI (example: "urn:miriam:pubmed:10812475" or the obsolete one "http://www.pubmed.gov/#10812475")
     * @return the data type part of a URI (the full URI if the process is not success full)
     */
    public static String getDataPart(String uri)
    {
        int index;
        
        if (isURN(uri))
        {
            index = uri.lastIndexOf(":");
        }
        else
        {
            index = uri.lastIndexOf("#");
        }
        
        if (index != -1)
        {
            return uri.substring(0, index);
        }
        // ":" or "#" not found
        else
        {
            return uri;
        }
    }
    
    
    /**
     * Returns the identifier part of a URI. Basically the identifier part corresponds to everything before the
     * character "#" or the last ":".
     * 
     * @param uri a URI (example: "urn:miriam:pubmed:10812475" or the obsolete one "http://www.pubmed.gov/#10812475")
     * @return the identifier part of a URI (the full URI if the process is not success full)
     */
    public static String getElementPart(String uri)
    {
        int index;
        
        if (isURN(uri))
        {
            index = uri.lastIndexOf(":");
        }
        else
        {
            index = uri.lastIndexOf("#");
        }
        
        if (index != -1)
        {
            return uri.substring(index + 1, uri.length());
        }
        // ":" or "#" not found
        else
        {
            return uri;
        }
    }
    
    
    /**
     * Searches the type of the URI (URL or URN?).
     * <p>
     * WARNING: doesn't check if the parameter is a valid URI!
     * 
     * @param uri a Uniform Request Identifier (can be a URL or a URN)
     * @return a boolean with the answer to the question above
     */
    public static String getURIType(String uri)
    {
        // "urn:" not found at the beginning of the URI
        if (uri.startsWith("urn:"))
        {
            return "URN";
        }
        else
        {
            return "URL";
        }
    }
    
    
    /**
     * Returns the answer to the question: is this URI a URL?
     * 
     * @param uri a Uniform Request Identifier
     * @return a boolean with the answer to the question above
     */
    public static boolean isURL(String uri)
    {
        if (getURIType(uri).equalsIgnoreCase("URL"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * Returns the answer to the question: is this URI a URN?
     * 
     * @param uri a Uniform Request Identifier
     * @return a boolean with the answer to the question above
     */
    public static boolean isURN(String uri)
    {
        if (getURIType(uri).equalsIgnoreCase("URN"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * Converts a MIRIAM URN into its equivalent Identifiers.org URL.
     * The method expects a fully valid and official URN!
     * @param urn a MIRIAM URN
     * @return the Identifiers.org URL corresponding to the provided URN or 'null' if the provided URN does not exist
     */
    public static String convertValidURN(String urn)
    {
		String url = null;
		String namespace = null;
		String identifier = null;
		
		if (null != urn)
		{
			// retrieves the data collection namespace and identifier
			String[] urnParts = urn.split(":");
			namespace = urnParts[2];
			identifier = urnParts[3];
			
			// remove any encoding of the identifier part (if any)
			try
			{
				identifier =  URLDecoder.decode(identifier, "UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				logger.error("UnsupportedEncodingException raised while URL decoding >" + identifier + "<!");
				logger.error(e.getMessage());
				identifier = null;
			}
			
			// generates the Identifiers.org URL (if possible)
			if ((null != namespace) && (null != identifier))
			{
				url = IDENTIFIERS_ORG_ROOT_URL + "/" + namespace + "/" + identifier;
			}
		}
		else   // null query
		{
			// null returned
		}
		
		return url;
    }
    
    
    /**
     * Generates a random word (could be used as a password) of a given length.
     * 
     * @param length
     * @return random password
     */
    public static String randomPassGen(Integer length)
    {
        String generated = new String();
        Random rand = new Random();
        
        for (int i=0; i<length.intValue(); i++)
        {
            String tmp = new Character((char)((int) 34 + ((int)(rand.nextFloat() * 93)))).toString();
            generated = generated + tmp;
        }
        
        return generated;
    }
    
    
    /**
     * Checks that the user of the session provided has access to the myMIRIAM profile which identifier is provided in parameter.
     * TODO: currently only admin can do anything there...
     * @param session
     * @param profileId identifier of a myMIRIAM profile
     * @return
     */
    public static boolean hasAccessToProject(HttpSession session, Integer profileId)
    {
        boolean result = false;
        
        if (isSessionValid(session))
        {
            String login = (String) session.getAttribute("login");
            String role = (String) session.getAttribute("role");
            if ((null != role) && (null != login) && (! login.matches("\\s*")) && ((role.equalsIgnoreCase(USER_ADMIN))))
            {
                result = true;
            }
        }
        
        return result;
    }
}
