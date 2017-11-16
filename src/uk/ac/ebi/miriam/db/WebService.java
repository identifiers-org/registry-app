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


/**
 * <p>Manages the storage of Web Services information, cf database table 'mir_web_services'. 
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2013 BioModels.net (EMBL - European Bioinformatics Institute)
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
public class WebService
{
    private String id = null;   // internal identifier (stored as a 'int' in the database)
    private String resId = null;   // identifier of the resource providing the services
    private String resInfo = null;   // information/description of the resource providing the services
    private String desc = null;   // description of the Web Services
    private String endpoint = null;   // endpoint of the Web Services
    private String wsdl = null;   // address of the WSDL
    private String doc = null;   // address of the documentation of the Web Services
    private String type = null;   // type of Web Services (SOAP, REST, ...)
    private String org = null;   // organisation behind the resource
    private String loc = null;   // country where the servers of the resource are located
    
    
    /**
     * Default constructor: builds an empty object.
     */
    public WebService()
    {
        // nothing here.
    }
    
    
    /**
     * Returns a string representation of the object.
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        
        str.append(this.type + " Web Service (" + this.id + "):\n");
        str.append("- Provider: " + this.resInfo + " (" + this.resId + ")\n");
        str.append("- Description: " + this.desc + "\n");
        str.append("- Endpoint: " + this.endpoint + "\n");
        str.append("- WSDL location: " + this.wsdl + "\n");
        str.append("- Documentation: " + this.doc + "\n");
        
        return str.toString();
    }
    
    
    /**
     * Finds differences between two <code>WebService</code>.
     * <p>
     * In order to keep things clean, 'this' corresponds to the new data and 'data' to the old one.
     * 
     * @param other the other Web Service to compare this one to (the old one)
     * @return all the differences found
     */
    public String diff(WebService other)
    {
        StringBuilder diff = new StringBuilder();
        
        // identifier
        if (! other.getId().equals(this.getId()))
        {
            diff.append("Identifier:\n");
            diff.append("\t< ").append(other.getId()).append("\n");
            diff.append("\t> ").append(this.getId()).append("\n\n");
        }
        
        // resource identifier (provider)
        if (! other.getResId().equals(this.getResId()))
        {
            diff.append("Provider:\n");
            diff.append("\t< ").append(other.getResId()).append("\n");
            diff.append("\t> ").append(this.getResId()).append("\n\n");
        }
        
        // ws desc
        if (! other.getDesc().equals(this.getDesc()))
        {
            diff.append("Description:\n");
            diff.append("\t< ").append(other.getDesc()).append("\n");
            diff.append("\t> ").append(this.getDesc()).append("\n\n");
        }
        
        // type
        if (! other.getType().equals(this.getType()))
        {
            diff.append("Type:\n");
            diff.append("\t< ").append(other.getType()).append("\n");
            diff.append("\t> ").append(this.getType()).append("\n\n");
        }
        
        // endpoint
        if (! other.getEndpoint().equals(this.getEndpoint()))
        {
            diff.append("Endpoint:\n");
            diff.append("\t< ").append(other.getEndpoint()).append("\n");
            diff.append("\t> ").append(this.getEndpoint()).append("\n\n");
        }
        
        // wsdl
        if (! other.getWsdl().equals(this.getWsdl()))
        {
            diff.append("WSDL:\n");
            diff.append("\t< ").append(other.getWsdl()).append("\n");
            diff.append("\t> ").append(this.getWsdl()).append("\n\n");
        }
        
        // doc
        if (! other.getDoc().equals(this.getDoc()))
        {
            diff.append("Documentation:\n");
            diff.append("\t< ").append(other.getDoc()).append("\n");
            diff.append("\t> ").append(this.getDoc()).append("\n\n");
        }
        
        return diff.toString();
    }
    
    
    /**
     * Getter
     * @return the id
     */
    public String getId()
    {
        return this.id;
    }
    
    
    /**
     * Setter
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }
    
    
    /**
     * Getter
     * @return the resource identifier
     */
    public String getResId()
    {
        return this.resId;
    }
    
    
    /**
     * Setter
     * @param resId the resource identifier to set
     */
    public void setResId(String resId)
    {
        this.resId = resId;
    }
    
    
    /**
     * Getter
     * @return the resource information
     */
    public String getResInfo()
    {
        return this.resInfo;
    }
    
    
    /**
     * Retrieves the information about the resource providing the Web Services
     * @return information properly encoded to be used in HTML pages
     */
    public String getResInfoHTML()
    {
        return StringEscapeUtils.escapeHtml4(this.resInfo);
    }
    
    
    /**
     * Setter
     * @param resInfo the resource information to set
     */
    public void setResInfo(String resInfo)
    {
        this.resInfo = resInfo;
    }
    
    
    /**
     * Getter
     * @return the description
     */
    public String getDesc()
    {
        return this.desc;
    }
    
    
    /**
     * Retrieves the description of the Web Services
     * @return description properly encoded to be used in HTML pages
     */
    public String getDescHTML()
    {
        return StringEscapeUtils.escapeHtml4(this.desc);
    }
    
    
    /**
     * Setter
     * @param desc the description to set
     */
    public void setDesc(String desc)
    {
        this.desc = desc;
    }
    
    
    /**
     * Getter
     * @return the endpoint
     */
    public String getEndpoint()
    {
        return this.endpoint;
    }
    
    
    /**
     * Retrieves the endpoint of the Web Services
     * @return endpoint properly encoded to be used in HTML pages
     */
    public String getEndpointHTML()
    {
        return StringEscapeUtils.escapeHtml4(this.endpoint);
    }
    
    
    /**
     * Setter
     * @param endpoint the endpoint to set
     */
    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }
    
    
    /**
     * Getter
     * @return the wsdl
     */
    public String getWsdl()
    {
        return this.wsdl;
    }
    
    
    /**
     * Retrieves the WSDL of the Web Services
     * @return WSDL properly encoded to be used in HTML pages
     */
    public String getWsdlHTML()
    {
        return StringEscapeUtils.escapeHtml4(this.wsdl);
    }
    
    
    /**
     * Setter
     * @param wsdl the wsdl to set
     */
    public void setWsdl(String wsdl)
    {
        this.wsdl = wsdl;
    }
    
    
    /**
     * Getter
     * @return the documentation
     */
    public String getDoc()
    {
        return this.doc;
    }
    
    
    /**
     * Retrieves the doc of the Web Services
     * @return doc properly encoded to be used in HTML pages
     */
    public String getDocHTML()
    {
        return StringEscapeUtils.escapeHtml4(this.doc);
    }
    
    
    /**
     * Setter
     * @param doc the documentation to set
     */
    public void setDoc(String doc)
    {
        this.doc = doc;
    }
    
    
    /**
     * Getter
     * @return the type
     */
    public String getType()
    {
        return this.type;
    }
    
    
    /**
     * Setter
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }
    
    
    /**
     * Getter
     * @return the org
     */
    public String getOrg()
    {
        return this.org;
    }
    
    
    /**
     * Retrieves the organisation providing the Web Services
     * @return organisation properly encoded to be used in HTML pages
     */
    public String getOrgHTML()
    {
        return StringEscapeUtils.escapeHtml4(this.org);
    }
    
    
    /**
     * Setter
     * @param org the org to set
     */
    public void setOrg(String org)
    {
        this.org = org;
    }
    
    
    /**
     * Getter
     * @return the loc
     */
    public String getLoc()
    {
        return this.loc;
    }
    
    
    /**
     * Retrieves the location of the organisation providing the Web Services
     * @return location properly encoded to be used in HTML pages
     */
    public String getLocHTML()
    {
        return StringEscapeUtils.escapeHtml4(this.loc);
    }
    
    
    /**
     * Setter
     * @param loc the loc to set
     */
    public void setLoc(String loc)
    {
        this.loc = loc;
    }
}
