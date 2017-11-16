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


/**
 * <p>Object which stores some limited information about a resource (can be used for an overview of the resources stored).
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
 * @version 20090629
 */
public class SimpleResource implements Comparable<Object>
{
    private String id;
    private String name;
    private String organisation;
    private String country;
    private String datatypeId;
    private String datatypeName;
    private int reliability;    // uptime ratio
    
    
    /**
     * Default constructor: builds an empty object.
     */
    public SimpleResource()
    {
        // nothing here
    }
    
    
    /**
     * Constructor with parameters: builds a full object.
     * @param id
     * @param name
     * @param uri
     * @param definition
     */
    public SimpleResource(String id, String name, String organisation, String country, String datatypeId, String datatypeName, int reliability)
    {
        this.id = id;
        this.name = name;
        this.organisation = organisation;
        this.country = country;
        this.datatypeId = datatypeId;
        this.datatypeName = datatypeName;
        this.reliability = reliability;
    }
    
    
    /**
     * Returns a <code>String</code> representation of this object.
     */
    @Override
    public String toString()
    {
        StringBuilder tmp = new StringBuilder();
        
        tmp.append("\n");
        tmp.append("Id: " + getId() + "\n");
        tmp.append("Name: " + getName() + "\n");
        tmp.append("Organisation: " + getOrganisation() + " (" + getCountry() + ")\n");
        tmp.append("Data type: " + getDatatypeName() + " (" + getDatatypeId() + ")\n");
        tmp.append("Reliability: " + getReliability() + "\n");
        
        return tmp.toString();
    }
    
    
    /**
     * Compares two object of type <code>SimpleDataType</code> based on their identifier.
     */
    public int compareTo(Object obj)
    {
        SimpleResource resource = (SimpleResource) obj;
        return ((this.id).compareTo(resource.id));
    }
    
    
    /**
     * Indicates whether some other object is "equal to" this one.
     */
    @Override
    public boolean equals(Object obj)
    {
        return ((this.id).compareTo(((SimpleResource) obj).id) == 0);
    }
    
    
    /**
     * Returns a hash code of the object (the integer part of the identifier).
     */
    @Override
    public int hashCode()
    {
        String sub = (this.id).substring(4);
        return Integer.parseInt(sub);
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
     * @return the country
     */
    public String getCountry()
    {
        return this.country;
    }
    
    
    /**
     * Setter
     * @param country the country to set
     */
    public void setCountry(String country)
    {
        this.country = country;
    }
    
    
    /**
     * Getter
     * @return the datatypeId
     */
    public String getDatatypeId()
    {
        return this.datatypeId;
    }
    
    
    /**
     * Setter
     * @param datatypeId the datatypeId to set
     */
    public void setDatatypeId(String datatypeId)
    {
        this.datatypeId = datatypeId;
    }
    
    
    /**
     * Getter
     * @return the datatypeName
     */
    public String getDatatypeName()
    {
        return this.datatypeName;
    }
    
    
    /**
     * Setter
     * @param datatypeName the datatypeName to set
     */
    public void setDatatypeName(String datatypeName)
    {
        this.datatypeName = datatypeName;
    }
    
    
    /**
     * Getter
     * @return the reliability
     */
    public int getReliability()
    {
        return this.reliability;
    }
    
    
    /**
     * Setter
     * @param reliability the reliability to set
     */
    public void setReliability(int reliability)
    {
        this.reliability = reliability;
    }
}
