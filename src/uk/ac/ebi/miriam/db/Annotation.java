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
 * <p>Manages the annotation part of a data type.
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
 * @version 20090512
 */
public class Annotation
{
    private String format;   // can be SBML, CellML, BioPAX, ...
    private List<Tag> tags;   // list of Tag(s)
    
    
    /**
     * Default constructor.
     */
    public Annotation(String format)
    {
        this.format = format;
        this.tags = new ArrayList<Tag>();
    }
    
    
    /**
     * Constructor with parameters.
     * 
     * @param format
     * @param tags
     */
    public Annotation(String format, ArrayList<Tag> tags)
    {
       this.format = format;
       this.tags = new ArrayList<Tag>(tags);
    }
    
    
    /**
     * Returns a string representation of the object.
     */
    public String toString()
    {
        StringBuffer str = new StringBuffer();
        
        str.append("Annotation\n");
        str.append("- format: " + this.format + "\n");
        str.append("- tags: \n");
        for (int i=0; i<this.getTags().size(); ++i)
        {
            str.append(this.getTag(i).toString("\t"));
        }
        
        return str.toString();
    }
    
    
    /**
     * Getter
     * @return the format
     */
    public String getFormat()
    {
        return this.format;
    }
    
    
    /**
     * Setter
     * @param format the format to set
     */
    public void setFormat(String format)
    {
        this.format = format;
    }
    
    
    /**
     * Getter
     * @return the tags
     */
    public List<Tag> getTags()
    {
        return this.tags;
    }
    
    
    /**
     * Getter of a specific tag (using its index)
     * 
     * @param index
     * @return
     */
    public Tag getTag(int index)
    {
        return (Tag) this.tags.get(index);
    }
    
    
    /**
     * Setter
     * @param tags the tags to set
     */
    public void setTags(ArrayList<Tag> tags)
    {
        this.tags = new ArrayList<Tag>(tags);
    }
    
    
    /**
     * Adds a tag to the current list of tags of the annotation
     * 
     * @param tag new tag to add
     */
    public void addTag(Tag tag)
    {
        this.tags.add(tag);
    }
}
