/*
 * MIRIAM Resources (Web Application)
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2011  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * <p>Object which stores some limited information about a data type (can be used for an overview of the data types stored).
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2011  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20111124
 */
public class SimpleDataType implements Comparable
{
    private String id;
    private String name;
    private String uri;
    private String definition;
    private Boolean restricted;
    
    
    /**
     * Default constructor: builds an empty object.
     */
    public SimpleDataType()
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
    public SimpleDataType(String id, String name, String uri, String definition, Boolean restricted)
    {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.definition = definition;
        this.restricted = restricted;
    }
    
    
    /**
     * Constructor with parameters: builds a full object.
     * @param id
     * @param name
     * @param uri
     * @param definition
     */
    public SimpleDataType(String id, String name, String uri, String definition, int restricted)
    {
        this.id = id;
        this.name = name;
        this.uri = uri;
        this.definition = definition;
        if (restricted == 0)
        {
            this.restricted = false;
        }
        else
        {
            this.restricted = true;
        }
    }
    
    
    /**
     * Returns a <code>String</code> representation of this object.
     */
    @Override
    public String toString()
    {
        StringBuilder tmp = new StringBuilder();
        
        tmp.append("\n");
        tmp.append("Id: " + getId());
        if (isRestricted())
        {
            tmp.append("   (* is restricted)\n");
        }
        else
        {
            tmp.append("\n");
        }
        tmp.append("Name: " + getName() + "\n");
        tmp.append("URI: " + getUri() + "\n");
        tmp.append("Definition: " + getDefinition() + "\n");
        
        return tmp.toString();
    }
    
    
    /**
     * Compares two object of type <code>SimpleDataType</code> based on their identifier.
     */
    public int compareTo(Object dataType)
    {
        SimpleDataType data = (SimpleDataType) dataType;
        return ((this.id).compareTo(data.id));
    }
    
    
    /**
     * Indicates whether some other object is "equal to" this one.
     */
    @Override
    public boolean equals(Object obj)
    {
        return ((this.id).compareTo(((SimpleDataType) obj).id) == 0);
    }
    
    
    /**
     * Returns a hash code of the object (the integer part of the identifier).
     */
    @Override
    public int hashCode()
    {
        String sub = (this.id).substring(4);
        super.hashCode();
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
     * @return the definition
     */
    public String getDefinition()
    {
        return this.definition;
    }
    
    
    /**
     * Setter
     * @param definition the definition to set
     */
    public void setDefinition(String definition)
    {
        this.definition = definition;
    }
    
    
    /**
     * Getter
     * @return the uri
     */
    public String getUri()
    {
        return this.uri;
    }
    
    
    /**
     * Setter
     * @param uri the uri to set
     */
    public void setUri(String uri)
    {
        this.uri = uri;
    }
    
    
    /**
     * Retrieves the "namespace" of the data type (the part after the "urn:miriam:" in the official URN).
     * @return namespace of the data type
     */
    public String getNamespace()
    {
        return this.uri.substring(11);
    }
    
    
    /**
     * Getter
     * @return the restricted
     */
    public Boolean isRestricted()
    {
        return this.restricted;
    }
    
    /**
     * Getter (for usage inside JSPs)
     * @return the restricted
     */
    public Boolean getRestricted()
    {
        return this.restricted;
    }
    
    
    /**
     * Setter
     * @param restricted the restricted to set
     */
    public void setRestricted(Boolean restricted)
    {
        this.restricted = restricted;
    }
    
    /**
     * Setter
     * @param restricted the restricted to set
     */
    public void setRestricted(int restricted)
    {
        if (restricted == 0)
        {
            this.restricted = false;
        }
        else
        {
            this.restricted = true;
        }
    }
}
