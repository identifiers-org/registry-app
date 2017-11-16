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


package uk.ac.ebi.miriam.db;


/**
 * <p>Stores the information related to a user.
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
 * @version 20140113
 */
public class User
{
    private String login ="";
    private String firstName ="";
    private String lastName ="";
    private String email="";
    private String organisation ="";
    private String password ="";   // encrypted version
    
    
    /**
     * Default constructor (builds an empty object).
     */
    public User()
    {
        // nothing here
    }


    /**
     * Constructor with arguments: builds a full object.
     * 
     * @param login
     * @param firstName
     * @param lastName
     * @param email
     * @param organisation
     * @param password
     */
    public User(String login, String firstName, String lastName, String email, String organisation, String password)
    {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.organisation = organisation;
        this.password = password;
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
     * @return the firstName
     */
    public String getFirstName()
    {
        return this.firstName;
    }
    
    
    /**
     * Setter
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    
    
    /**
     * Getter
     * @return the lastName
     */
    public String getLastName()
    {
        return this.lastName;
    }
    
    
    /**
     * Setter
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
    
    
    /**
     * Getter
     * @return the email
     */
    public String getEmail()
    {
        return this.email;
    }
    
    
    /**
     * Setter
     * @param email the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    
    /**
     * Getter
     * @return the organisation
     */
    public String getOrganisation()
    {
        return this.organisation;
    }
    
    
    /**
     * Setter
     * @param organisation the organisation to set
     */
    public void setOrganisation(String organisation)
    {
        this.organisation = organisation;
    }
    
    
    /**
     * Getter
     * @return the password
     */
    public String getPassword()
    {
        return this.password;
    }
    
    
    /**
     * Setter
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getFullName() {
        String fullName ="";
        if(!firstName.isEmpty() && !lastName.isEmpty())
            fullName = firstName + " " + lastName;
        else if(!firstName.isEmpty())
            fullName = firstName;
        else if(!lastName.isEmpty())
            fullName = lastName;
        return fullName;
    }
}
