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
 * <p>Manages the qualifiers (specially designed for the BioModels.net qualifiers).
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
 * @version 20090513
 */
public class Qualifier
{
    public static final int SEED = 42;   // for hashcode purposes only
    
    private String name;
    private String definition;
    private String type;
    
    
    /**
     * Constructor with parameters 
     * 
     * @param format
     * @param definition
     * @param type
     */
    public Qualifier(String name, String definition, String type)
    {
       this.name = name;
       this.definition = definition;
       this.type = type;
    }
    
    
    /**
     * Indicates whether some other object is "equal to" this one.
     */
    @Override
    public boolean equals(Object obj)
    {
        return ((this.name).compareTo(((Qualifier) obj).name) == 0);
    }
    
    
    /**
     * Compares two object of type <code>Qualifier</code> based on their name.
     */
    public int compareTo(Object qualifier)
    {
        Qualifier data = (Qualifier) qualifier;
        return ((this.name).compareTo(data.name));
    }
    
    
    /**
     * Returns a hash code value for this object (based on the name).
     */
    @Override
    public int hashCode()
    {
        return SEED + this.name.hashCode();
    }
    
    
    /**
     * Returns a string representation of the object.
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        
        str.append("\n");
        str.append("Name:       " + this.name + "\n");
        str.append("Type:       " + this.type + "\n");
        str.append("Definition: " + this.definition + "\n");
        
        return str.toString();
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
}
