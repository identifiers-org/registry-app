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


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * <p>Object for storing data types information for usage within myMIRIAM. 
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2011 BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20110608
 */
public class MyMiriamDataType
{
    private String id;
    private String name;
    private Boolean selected;
    private Date dateAdded;
    private Date dateModif;
    private List<MyMiriamResource> resources;
    
    
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
     * @return the selected
     */
    public Boolean isSelected()
    {
        return this.selected;
    }
    
    /**
     * Getter (for usage in JSPs)
     * @return the selected
     */
    public Boolean getSelected()
    {
        return this.selected;
    }
    
    /**
     * Setter
     * @param selected the selected to set
     */
    public void setSelected(Boolean selected)
    {
        this.selected = selected;
    }
    
    /**
     * Getter
     * @return the dateAdded
     */
    public Date getDateAdded()
    {
        return this.dateAdded;
    }
    
    /**
     * Getter
     * @return the date added in a nice human readable format
     */
    public String getDateAddedStr()
    {
        if (null == this.dateAdded)
        {
            return "NA";
        }
        else
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   // nicer format for dates
            return dateFormat.format(this.dateAdded);
        }
    }
    
    /**
     * Setter
     * @param dateAdded the dateAdded to set
     */
    public void setDateAdded(Date dateAdded)
    {
        this.dateAdded = dateAdded;
    }
    
    /**
     * Getter
     * @return the dateModif
     */
    public Date getDateModif()
    {
        return this.dateModif;
    }
    
    /**
     * Getter
     * @return the date of last modification in a nice human readable form
     */
    public String getDateModifStr()
    {
        if (null == this.dateModif)
        {
            return "NA";
        }
        else
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   // nicer format for dates
            return dateFormat.format(this.dateModif);
        }
    }
    
    /**
     * Setter
     * @param dateModif the dateModif to set
     */
    public void setDateModif(Date dateModif)
    {
        this.dateModif = dateModif;
    }
    
    /**
     * Getter
     * @return the resources
     */
    public List<MyMiriamResource> getResources()
    {
        return this.resources;
    }
    
    /**
     * Setter
     * @param resources the resources to set
     */
    public void setResources(List<MyMiriamResource> resources)
    {
        this.resources = resources;
    }
}
