/*
 * MIRIAM Resources (Web Application)
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2012  BioModels.net (EMBL - European Bioinformatics Institute)
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


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;


/**
 * <p>
 * Object which stores all the information about a profile registered in myMIRIAM.
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2012 BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20120824
 */
public class Profile
{
    private Logger logger = Logger.getLogger(Profile.class);
    
    private Integer id;
    private String shortName;
    private String name;
    private String desc;
    private Boolean openAccess;   // public or private
    private String key;   // SHA-1 hash of the key
    private String contactEmail;   // email address
    private Date dateCreation;
    private Boolean auto;
    // additional fields
    private Integer nbDataTypes;
    private Date dateLastModif;
    
    
    /**
     * Default constructor: builds an empty object.
     */
    public Profile()
    {
        // nothing here.
    }
    
    /**
     * Getter
     * @return the id
     */
    public Integer getId()
    {
        return this.id;
    }
    
    /**
     * Setter
     * @param id the id to set
     */
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    /**
     * Getter
     * @return the shortName
     */
    public String getShortName()
    {
        return this.shortName;
    }
    
    /**
     * Setter
     * @param shortName the shortName to set
     */
    public void setShortName(String shortName)
    {
        this.shortName = shortName;
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
     * @return the desc
     */
    public String getDesc()
    {
        return this.desc;
    }
    
    /**
     * Setter
     * @param desc the desc to set
     */
    public void setDesc(String desc)
    {
        this.desc = desc;
    }
    
    /**
     * Getter
     * @return the openAccess
     */
    public Boolean isOpenAccess()
    {
        return this.openAccess;
    }
    
    /**
     * Getter (nice String)
     * @return the openAccess
     */
    public String getOpenAccessStr()
    {
        if (openAccess)
        {
            return "public";
        }
        else
        {
            return "private";
        }
    }
    
    /**
     * Setter
     * @param openAccess the openAccess to set
     */
    public void setOpenAccess(Boolean openAccess)
    {
        this.openAccess = openAccess;
    }
    
    /**
     * Getter
     * @return the key
     */
    public String getKey()
    {
        return this.key;
    }
    
    /**
     * Getter
     * @return the key encoded for use in URLs
     */
    public String getKeyUrl()
    {
        try
        {
            return URLEncoder.encode(this.key, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("Unable to encode the key '" + key + "' for use in URLs!");
            return key;
        }
    }
    
    /**
     * Setter
     * @param key the key to set
     */
    public void setKey(String key)
    {
        this.key = key;
    }
    
    /**
     * Getter
     * @return the contact email
     */
    public String getContactEmail()
    {
        return this.contactEmail;
    }
    
    /**
     * Setter
     * @param contact the contact email to set
     */
    public void setContactEmail(String email)
    {
        this.contactEmail = email;
    }
    
    /**
     * Getter
     * @return the dateCreation
     */
    public Date getDateCreation()
    {
        return this.dateCreation;
    }
    
    /**
     * Getter
     * @return the date of creation in a nice human readable format
     */
    public String getDateCreationStr()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   // nicer format for dates
        return dateFormat.format(this.dateCreation);
    }
    
    /**
     * Setter
     * @param dateCreation the dateCreation to set
     */
    public void setDateCreation(Date dateCreation)
    {
        this.dateCreation = dateCreation;
    }
    
    /**
     * Getter
     * @return the nbDataTypes
     */
    public Integer getNbDataTypes()
    {
        return this.nbDataTypes;
    }
    
    /**
     * Setter
     * @param nbDataTypes the nbDataTypes to set
     */
    public void setNbDataTypes(Integer nbDataTypes)
    {
        this.nbDataTypes = nbDataTypes;
    }
    
    /**
     * Getter
     * @return the date of last modification
     */
    public Date getDateLastModif()
    {
        return this.dateLastModif;
    }
    
    /**
     * Getter
     * @return the date of last modification in a nice human readable form
     */
    public String getDateLastModifStr()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   // nicer format for dates
        return dateFormat.format(this.dateLastModif);
    }
    
    /**
     * Setter
     * @param dateLastModif the date of last modification to set
     */
    public void setDateLastModif(Date dateLastModif)
    {
        this.dateLastModif = dateLastModif;
    }

    public Boolean getAuto() {
        return auto;
    }

    public void setAuto(Boolean auto) {
        this.auto = auto;
    }
}
