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



package uk.ac.ebi.miriam.db;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;


/**
 * <p>Object which stores all the information about a resource (= a physical location of a data collection).
 * <p>
 * Implements <code>Comparable</code> to be able to use the objects of this class inside a <code>TreeSet</code>
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
 * @version 20130807
 */
public class Resource implements Comparable<Object>
{
    /* stable identifier of the resource (something starting by 'MIR:001' and followed by 5 digits) */
    private String id = new String();
    /* prefix part of the physical location (URL) */
    private String url_prefix = new String();
    /* suffix part of the physical location (URL) */
    private String url_suffix = new String();
    /* address of the front page of the resource */
    private String url_root = new String();
    /* some useful information about the resource */
    private String info = new String();
    /* institution which manage the resource */
    private String institution = new String();
    /* country of the institution */
    private String location = new String();   // optional
    /* example of an identifier used by this resource */
    private String example = new String();
    /* is the resource obsolete or not? */
    private Boolean obsolete;
    /* percentage of reliability (uptime) */
    private Integer reliability;
    /* identifier of the associated data collection */
    private String collectionId = new String();
    /* whether or not this resource is the primary one for the data collection */
    private Boolean primary;
    /* converet prefix for conversion between uris*/
    private String convert_prefix = new String();

    private ArrayList<Format> formatList = new ArrayList<Format>();

    private int ownership_status = -1;

    private ArrayList<User> ownerList = new ArrayList<User>();

    /**
     * <p>
     * Default constructor (empty object).
     */
    public Resource()
    {
        // default parameters
        this.obsolete = false;
        this.primary = null;   /* not set */
    }
    
    
    /**
     * Overrides the 'toString()' method for the 'Resource' object
     * @return a string which contains all the information about the resource
     */
    public String toString()
    {
        StringBuilder tmp = new StringBuilder();
        
        tmp.append("       - Identifier:  " + getId() + " (data collection: " + getCollectionId() + ")\n");
        tmp.append("       - URL prefix:  " + getUrl_prefix() + "\n");
        tmp.append("       - URL suffix:  " + getUrl_suffix() + "\n");
        tmp.append("       - URL root:    " + getUrl_root() + "\n");
        tmp.append("       - Information: " + getInfo() + "\n");
        tmp.append("       - Institution: " + getInstitution() + "\n");
        if (! getLocation().isEmpty())
        {
        	tmp.append("       - Location:    " + getLocation() + "\n");
        }
        tmp.append("       - Example:     " + getExample() + "\n");
        tmp.append("       - Primary:     " + isPrimary() + "\n");
        tmp.append("       - Obsolete:    " + isObsolete() + "\n");
        tmp.append("       - URI convert prefix:    " + getConvert_prefix() + "\n");

        return tmp.toString();
    }
    
    
    /**
     * Tests if two <code>Resource</code> objects are the same (only checks the ID).
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Resource res)
    {
        return (this.id.equals(res.id));
    }
    
    
    /**
     * Checks if two <code>Resource</code> objects have the same content (and same ID).
     * @param res the other <code>Resource</code> to compare to
     * @return
     */
    public boolean hasSameContent(Resource res)
    {
        return ((this.id.equals(res.id)) &&
                (this.url_prefix.equals(res.url_prefix)) &&
                (this.url_suffix.equals(res.url_suffix)) &&
                (this.url_root.equals(res.url_root)) &&
                (this.info.equals(res.info)) &&
                (this.institution.equals(res.institution)) &&
                (this.location.equals(res.location)) &&
                (this.example.equals(res.example)) &&
                (this.convert_prefix.equals(res.convert_prefix)) &&
                (this.obsolete == res.obsolete)) &&
                (this.primary == res.primary);
    }
    
    
    /**
     * Checks if two <code>Resource</code> are similar (based on simple statistics studies).
     *
     * <p>
     * 7 attributes take into account (url_prefix, url_suffix, url_root, info, institution, location, obsolete).
     *
     * @param res the other <code>Resource</code> to compare to
     * @return 'true' if number of similarities >= 4 (7 attributes tested)
     */
    public boolean couldBeSimilar(Resource res)
    {
        int nb = 0;
        
        if (this.url_prefix.equals(res.url_prefix))
        {
            nb ++;
        }
        if (this.url_suffix.equals(res.url_suffix)){
            nb ++;
        }
        if (this.url_root.equals(res.url_root))
        {
            nb ++;
        }
        if (this.info.equals(res.info))
        {
            nb ++;
        }
        if (this.institution.equals(res.institution))
        {
            nb ++;
        }
        if (this.location.equals(res.location))
        {
            nb ++;
        }
        if (this.convert_prefix.equals(res.convert_prefix))
        {
            nb ++;
        }
        if (this.obsolete == res.obsolete)
        {
            nb ++;
        }
        if (this.primary == res.primary)
        {
        	nb ++;
        }
        
        return (nb >= 4);
    }
    
    
    /**
     * Compares to objects and determine whether they are equicalent or not
     * Mandatory method for the class to be able to implements 'Comparable'
     * <p>
     * WARNING: the test only uses the ID of the Resource object!
     * @param an unknown object
     * @return 0 if the two objects are the same
     */
    public int compareTo(Object obj)
    {
        Resource res = (Resource) obj;
        
        /*
        // different identifiers
        if ((this.getId()).compareToIgnoreCase(res.getId()) != 0)
        {
          return -1;
        }
        else   // same identifier
        {
          return 0;
        }
        */
        
        return (this.getId()).compareToIgnoreCase(res.getId());
    }
    
    
    /**
     * Getter of the stable identifier of the resource
     * @return the stable identifier of the resource
     */
    public String getId()
    {
        return this.id;
    }
    
    
    /**
     * Setter of the stable identifier of the resource
     * @param id the stable identifier of the resource
     */
    public void setId(String id)
    {
        if (null != this.id)
        {
            this.id = id.trim();
        }
        else
        {
            this.id = null;
        }
    }
    
    
    /**
     * Getter of some general information about the resource
     * @return some general information about the resource
     */
    public String getInfo()
    {
        return this.info;
    }
    
    
    /**
     * Setter of some general information about the resource
     * @param info some general information about the resource
     */
    public void setInfo(String info)
    {
        if (null != this.info)
        {
            this.info = info.trim();
        }
        else
        {
            this.info = null;
        }
    }
    
    
    /**
     * Getter of the institution managing the resource
     * @return the institution managing the resource
     */
    public String getInstitution()
    {
        return this.institution;
    }
    
    
    /**
     * Setter of the institution managing the resource
     * @param institution the institution managing the resource
     */
    public void setInstitution(String institution)
    {
        if (null != this.institution)
        {
            this.institution = institution.trim();
        }
        else
        {
            this.institution = null;
        }
    }
    
    
    /**
     * Getter of the country of the institution
     * @return the country of the institution
     */
    public String getLocation()
    {
        return this.location;
    }
    
    
    /**
     * Setter of the country of the institution
     * @param location the country of the institution
     */
    public void setLocation(String location)
    {
        if (null != this.location)
        {
            this.location = location.trim();
        }
        else
        {
            this.location = null;
        }
    }
    
    
    /**
     * Getter of the obsolete parameter
     * @return if the resource is obsolete or not
     */
    public Boolean isObsolete()
    {
        return this.obsolete;
    }
    
    /**
     * Getter of the obsolete parameter (for use in JSPs)
     * @return if the resource is obsolete or not
     */
    public Boolean getObsolete()
    {
        return this.obsolete;
    }
    
    
    /**
     * Setter of the obsolete parameter
     * @param obsolete the resource is obsolete or not (that is the question)
     */
    public void setObsolete(Boolean obsolete)
    {
        this.obsolete = obsolete;
    }
    
    
    /**
     * Getter of the prefix part of the address (link to an element)
     * @return the prefix part of the address (link to an element)
     */
    public String getUrl_prefix()
    {
        return this.url_prefix;
    }
    
    
    /**
     * Setter of the prefix part of the address (link to an element)
     * @param url_prefix the prefix part of the address (link to an element)
     */
    public void setUrl_prefix(String url_prefix)
    {
        if (null != this.url_prefix)
        {
            this.url_prefix = url_prefix.trim();
        }
        else
        {
            this.url_prefix = null;
        }
    }
    
    
    /**
     * Getter of the resource address (front page)
     * @return the resource address (front page)
     */
    public String getUrl_root()
    {
        return this.url_root;
    }
    
    
    /**
     * Setter of the resource address (front page)
     * @param url_root the resource address (front page)
     */
    public void setUrl_root(String url_root)
    {
        if (null != this.url_root)
        {
            this.url_root = url_root.trim();
        }
        else
        {
            this.url_root = null;
        }
    }
    
    
    /**
     * Getter of the suffix part of the address (link to an element)
     * @return the suffix part of the address (link to an element)
     */
    public String getUrl_suffix()
    {
        return this.url_suffix;
    }
    
    
    /**
     * Setter of the suffix part of the address (link to an element)
     * @param url_suffix the suffix part of the address (link to an element)
     */
    public void setUrl_suffix(String url_suffix)
    {
        if (null != this.url_suffix)
        {
            this.url_suffix = url_suffix.trim();
        }
        else
        {
            this.url_suffix = null;
        }
    }
    
    
    /**
     * Returns the URL to access an example of data stored by this resource.
     * @return
     */
    public String getUrl()
    {
        StringBuilder str = new StringBuilder();
        if ((null != this.example) && (!this.example.matches("\\s*")))
        {
            str.append(this.url_prefix).append(this.example).append(this.url_suffix);
        }
        else
        {
            str.append(this.url_prefix).append("-*--NO_EXAMPLE_IDENTIFIER_PROVIDED--*-").append(this.url_suffix);
        }
        
        return str.toString(); 
    }
    
    /**
     * Returns the URL to access an example of data stored by this resource, specially formated for HTML usage.
     * @return
     */
    public String getHtmlUrl()
    {
        return StringEscapeUtils.escapeHtml4(getUrl());   // for javascript usage with openPopup() method: .replaceAll("'", "\\\\'") 
    }
     
    
    /**
     * Getter of the example
     * @return the example
     */
     public String getExample()
     {
         return this.example;
     }
      
      
     /**
      * Setter of the example.
      * @param example the example to set
      */
     public void setExample(String example)
     {
         if (null != this.example)
         {
             this.example = example.trim();
         }
         else
         {
             this.example = null;
         }
     }
    
    
    /**
     * Getter
     * @return the reliability
     */
    public Integer getReliability()
    {
        return this.reliability;
    }
    
    
    /**
     * Setter
     * @param reliability the reliability to set
     */
    public void setReliability(Integer reliability)
    {
        this.reliability = reliability;
    }
    
    
    /**
     * Setter
     * @param collectionId the collectionId to set
     */
    public void setCollectionId(String collectionId)
    {
        this.collectionId = collectionId;
    }
    
    
    /**
     * Getter
     * @return the collectionId
     */
    public String getCollectionId()
    {
        return this.collectionId;
    }
    
    
	/**
	 * Getter
	 * @return the primary
	 */
	public Boolean isPrimary()
	{
		return this.primary;
	}
	
	
	/**
	 * Getter (for use in JSPs)
	 * @return the primary
	 */
	public Boolean getPrimary()
	{
		return this.primary;
	}
	
	
	/**
	 * Setter
	 * @param primary the primary to set
	 */
	public void setPrimary(Boolean primary)
	{
		this.primary = primary;
	}

    public String getConvert_prefix() {
        return convert_prefix;
    }

    public void setConvert_prefix(String convert_prefix) {
        this.convert_prefix = convert_prefix;
    }

    public void addFormat(Format format){
        formatList.add(format);
    }

    public ArrayList<Format> getFormatList() {
        return formatList;
    }
    public int getOwnership_status() {
        return ownership_status;
    }

    public void setOwnership_status(int ownership_status) {
        this.ownership_status = ownership_status;
    }

    public ArrayList<User> getOwnerList() {
        return ownerList;
    }

    public void addOwner(User owner) {
        ownerList.add(owner);
    }
}
