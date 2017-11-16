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


package uk.ac.ebi.miriam.db;


import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;


/**
 * <p>Stores some information related to a user for management purpose (available for the administrator).
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
 * @version 20080516
 */
public class UserManageDetails
{
    private Logger logger = Logger.getLogger(UserManageDetails.class);
    private String login;
    private String name;
    private String role;
    private Date lastLogin;
    
    
    /**
     * Default constructor; builds an empty object.
     */
    public UserManageDetails()
    {
       this.login = new String();
       this.name = new String();
       this.role = new String();
       this.lastLogin = new Date();
    }
    
    
    /**
     * Constructor with parameters: builds a full object. 
     * @param login
     * @param name
     * @param role
     * @param lastLogin
     */
    public UserManageDetails(String login, String name, String role, Date lastLogin)
    {
        this.login = login;
        this.name = name;
        this.role = role;
        this.lastLogin = lastLogin;
    }
    
    
    /**
     * Getter
     * @return the login
     */
    public String getLogin()
    {
        return this.login;
    }
    
    
    /**
     * Setter
     * @param login the login to set
     */
    public void setLogin(String login)
    {
        this.login = login;
    }
    
    
    /**
     * Getter
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }
    
    
    /**
     * Setter
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    
    /**
     * Getter
     * @return the role
     */
    public String getRole()
    {
        return this.role;
    }
    
    
    /**
     * Setter
     * @param role the role to set
     */
    public void setRole(String role)
    {
        this.role = role;
    }
    
    
    /**
     * Getter
     * @return the lastLogin
     */
    public Date getLastLogin()
    {
        return this.lastLogin;
    }
    
    
    /**
     * Setter
     * @param lastLogin the lastLogin to set
     */
    public void setLastLogin(Date lastLogin)
    {
        this.lastLogin = lastLogin;
    }
    
    
    /**
     * Getter of the date as a String (using a specific format).
     * @return
     */
    public String getLastLoginStr()
    {
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
        return df.format(this.lastLogin);
    }
    
    
    /**
     * Setter <code>Date</code> from <code>String</code>
     * @param date
     */
    public void setLastLogin(String lastLogin)
    {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            date = format.parse(lastLogin);
        }
        catch (Exception e)
        {
            logger.error("Date conversion error (" + lastLogin + ")!");
            logger.error("Exception raised: " + e.getMessage());
            date = new Date(0);   // 1st January 1970
        }
        
        this.lastLogin = date;
    }
}
