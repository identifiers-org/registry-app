/*
 * MIRIAM Registry (Web Application)
 * The Registry is an online resource created to catalogue biological data collections,
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
 * <p>
 * Object which stores all the information about a kind of restriction.
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
 * @version 20111214
 */
public class RestrictionType
{
    private Integer id;   // internal identifier
    private String category;   // few words description (shared)
    private String desc;   // one sentence description (shared)
    
    
    /**
     * Default constructor.
     */
    public RestrictionType()
    {
        this.id = null;
        this.category = null;
        this.desc = null;
    }
    
    /**
     * Default constructor.
     */
    public RestrictionType(Integer id, String category, String desc)
    {
        this.id = id;
        this.category = category;
        this.desc = desc;
    }
    
    
    /**
     * Indicates whether some other object is "equal to" this one.
     */
    @Override
    public boolean equals(Object obj)
    {
        return ((this.id).compareTo(((RestrictionType) obj).id) == 0);
    }
    
    /**
     * Compares two object of type <code>RestrictionType</code> based on their identifier.
     */
    public int compareTo(Object type)
    {
        RestrictionType data = (RestrictionType) type;
        return ((this.id).compareTo(data.id));
    }
    
    
    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString()
    {
        StringBuilder tmp = new StringBuilder();
        
        tmp.append("Type of restriction:\n");
        tmp.append("  Category:    " + getCategory() + " (" + getId() + ")\n");
        tmp.append("  Description: " + getDesc() + "\n");
        
        return tmp.toString();
    }
    
    
    /**
     * Getter
     * @return the internal id
     */
    public Integer getId()
    {
        return this.id;
    }
    
    /**
     * Setter
     * @param id the internal id to set
     */
    public void setId(Integer id)
    {
        this.id = id;
    }
    
    /**
     * Getter
     * @return the category
     */
    public String getCategory()
    {
        return this.category;
    }
    
    /**
     * Setter
     * @param category the category to set
     */
    public void setCategory(String category)
    {
        this.category = category;
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
}
