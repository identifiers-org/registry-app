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
 * <p>Adds some HTML features to <code>SimpleResource</code> (generation of links).
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
 * @version 20111014
 */
public class SimpleResourceHtml extends SimpleResource
{
    private String urlRoot;
    
    
    
    /**
     * Generates a HTML link towards the details of the health of the current resource.
     * @return
     */
    public String getIdLink()
    {
        return "<a href=\"" + this.urlRoot + "/resources/" + this.getId() + "\" title=\"Detailed health report for: " + this.getName() + "\">" + this.getId() + "</a>";
    }
    
    
    /**
     * Generates a HTML link towards the details of the data type of the current resource.
     * @return
     */
    public String getDatatypeLink()
    {
        return "<a href=\"" + this.urlRoot + "/collections/" + this.getDatatypeId() + "\" title=\"Access to the data type: " + this.getDatatypeName() + "\">" + this.getDatatypeId() + "</a>";
    }
    
    
    /**
     * Getter
     * @return the urlRoot
     */
    public String getUrlRoot()
    {
        return this.urlRoot;
    }
    
    
    /**
     * Setter
     * @param urlRoot the urlRoot to set
     */
    public void setUrlRoot(String urlRoot)
    {
        this.urlRoot = urlRoot;
    }
}
