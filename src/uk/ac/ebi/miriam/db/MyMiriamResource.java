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
 * <p>Object for storing resource information for usage within myMIRIAM. 
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
 * @version 20110615
 */
public class MyMiriamResource
{
    private String id;
    private Boolean preferred;
    private String info;
    private String institution;
    private String location;
    private Boolean obsolete;
    
    
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
     * @return the preferred
     */
    public Boolean isPreferred()
    {
        return this.preferred;
    }
    
    /**
     * Getter
     * @return the preferred
     */
    public Boolean getPreferred()
    {
        return this.preferred;
    }
    
    /**
     * Setter
     * @param preferred the preferred to set
     */
    public void setPreferred(Boolean preferred)
    {
        this.preferred = preferred;
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
     * @return the institution
     */
    public String getInstitution()
    {
        return this.institution;
    }
    
    /**
     * Setter
     * @param institution the institution to set
     */
    public void setInstitution(String institution)
    {
        this.institution = institution;
    }
    
    /**
     * Getter
     * @return the location
     */
    public String getLocation()
    {
        return this.location;
    }
    
    /**
     * Setter
     * @param location the location to set
     */
    public void setLocation(String location)
    {
        this.location = location;
    }
    
    /**
     * Getter
     * @return the obsolete
     */
    public Boolean getObsolete()
    {
        return this.obsolete;
    }
    
    /**
     * Setter
     * @param obsolete the obsolete to set
     */
    public void setObsolete(Boolean obsolete)
    {
        this.obsolete = obsolete;
    }
}
