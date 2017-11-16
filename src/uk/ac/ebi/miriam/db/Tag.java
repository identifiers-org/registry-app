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
 * <p>General Tag class (multipurpose: examples of annotation and classification).
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
 * @version 20080608
 */
public class Tag
{
    private String id;
    private String name;
    private String info;
    private int nbOccurrence;   // optional
    
    
    /**
     * Default constructor.
     */
    public Tag()
    {
        this.id = new String();
        this.name = new String();
        this.info = new String();
    }
    
    
    /**
     * Partial constructor with parameters. No parameter for 'number of occurrences'
     * 
     * @param id
     * @param name
     * @param info
     */
    public Tag(String id, String name, String info)
    {
        this.id = id;
        this.name = name;
        this.info = info;
    }
    
    
    /**
     * Full constructor with parameters.
     * 
     * @param id
     * @param name
     * @param info
     * @param nbOccurrence
     */
    public Tag(String id, String name, String info, int nbOccurrence)
    {
        this.id = id;
        this.name = name;
        this.info = info;
        this.nbOccurrence = nbOccurrence;
    }
    
    
    /**
     * Indicates whether some other object is "equal to" this one.
     */
    @Override
    public boolean equals(Object obj)
    {
        return ((this.id).compareTo(((Tag) obj).id) == 0);
    }
    
    
    /**
     * Compares two object of type <code>Tag</code> based on their identifier.
     */
    public int compareTo(Object tag)
    {
        Tag data = (Tag) tag;
        return ((this.id).compareTo(data.id));
    }
    
    
    /**
     * Returns a hash code value for this object (based on the integer part of the identifier).
     */
    @Override
    public int hashCode()
    {
        String sub = (this.id).substring(4);
        super.hashCode();
        return Integer.parseInt(sub);
    }
    
    
    /**
     * Returns a string representation of the object.
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        
        str.append("\n");
        str.append("Id:          " + this.id + "\n");
        str.append("Name:        " + this.name + "\n");
        str.append("Information: " + this.info + "\n");
        
        return str.toString();
    }
    
    
    /**
     * Returns a string representation of the object.
     */
    public String toString(String indent)
    {
        StringBuffer str = new StringBuffer();
        
        str.append(indent + "Id:          " + this.id + "\n");
        str.append(indent + "Name:        " + this.name + "\n");
        str.append(indent + "Information: " + this.info + "\n");
        
        return str.toString();
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
     * @return the info
     */
    public String getInfo()
    {
        return this.info;
    }
    
    
    /**
     * Setter
     * @param info the info to set
     */
    public void setInfo(String info)
    {
        this.info = info;
    }
    
    
    /**
     * Getter
     * @return the nbOccurrence
     */
    public int getNbOccurrence()
    {
        return this.nbOccurrence;
    }
    
    
    /**
     * Setter
     * @param nbOccurrence the nbOccurrence to set
     */
    public void setNbOccurrence(int nbOccurrence)
    {
        this.nbOccurrence = nbOccurrence;
    }
}
