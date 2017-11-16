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


import java.util.ArrayList;
import java.util.List;


/**
 * <p>Manages group of qualifiers of the same type/kind (specially designed for the BioModels.net qualifiers).
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
public class GroupQualifiers
{
    public static final int SEED = 42;   // for hashcode purposes only
    
    private String type;
    private String definition;
    private String namespace;
    private List<Qualifier> qualifiers;
    
    
    /**
     * Default constructor.
     */
    public GroupQualifiers()
    {
        this.qualifiers = new ArrayList<Qualifier>();
    }
    
    
    /**
     * Constructor with parameters.
     */
    public GroupQualifiers(String type, String definition, String namespace)
    {
        this.type = type;
        this.definition = definition;
        this.namespace = namespace;
        this.qualifiers = new ArrayList<Qualifier>();
    }
    
    
    /**
     * Indicates whether some other object is "equal to" this one.
     */
    @Override
    public boolean equals(Object obj)
    {
        return ((this.type).compareTo(((GroupQualifiers) obj).type) == 0);
    }
    
    
    /**
     * Compares two object of type <code>Qualifier</code> based on their name.
     */
    public int compareTo(Object qualifier)
    {
        GroupQualifiers data = (GroupQualifiers) qualifier;
        return ((this.type).compareTo(data.type));
    }
    
    
    /**
     * Returns a hash code value for this object (based on the name).
     */
    @Override
    public int hashCode()
    {
        return SEED + this.type.hashCode();
    }
    
    
    /**
     * Returns a string representation of the object.
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        
        str.append("\n" + this.type + ":");
        str.append("\n- definition: " + this.definition);
        str.append("\n- namespace:" + this.namespace);
        str.append("\n- list of qualifiers:\n");
        for (int i=0; i<this.getQualifiers().size(); ++i)
        {
            str.append("\n\t-" + this.getQualifier(i).toString());
        }
        
        return str.toString();
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
     * @return the namespace
     */
    public String getNamespace()
    {
        return this.namespace;
    }
    
    
    /**
     * Setter
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }
    
    
    /**
     * Getter
     * @return the qualifiers
     */
    public List<Qualifier> getQualifiers()
    {
        return this.qualifiers;
    }
    
    
    /**
     * Retrieves a specific qualifier.
     * @param index
     * @return
     */
    public Qualifier getQualifier(int index)
    {
        return this.qualifiers.get(index);
    }
    
    
    /**
     * Setter
     * @param qualifiers the qualifiers to set
     */
    public void setQualifiers(List<Qualifier> qualifiers)
    {
        this.qualifiers = qualifiers;
    }
    
    
    /**
     * Adds a new qualifier to the list.
     * @param qualifier
     */
    public void addQualifier(Qualifier qualifier)
    {
        this.qualifiers.add(qualifier);
    }
}
