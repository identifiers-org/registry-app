/*
 * MIRIAM Registry (Web Application)
 * The Registry is an online resource created to catalogue data collections 
 * (their URIs and the corresponding physical URLs, whether these are controlled vocabularies or databases)
 * and provide unique and stable identifiers for life science, in the form of URIs. 
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2013  BioModels.net (EMBL - European Bioinformatics Institute)
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
 */


package uk.ac.ebi.miriam.db;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * <p>Stores a simple report about the health of a specific resource (cf. table 'mir_url_check').
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2013  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20130710
 */
public class ResourcesCheckReport// implements Comparable<ResourcesCheckReport>
{
    private String id;
    private String info;
    private String dataId;
    private String dataName;
    private Integer state;
    private Integer uptime;
    private Integer downtime;
    private Integer unknown;
    private Integer uptimeRatio;   // percentage
    // HTML purpose only
    private String htmlClass;
    
    
    /**
     * Constructor with parameters.
     * @param id
     * @param info
     * @param dataId
     * @param dataName
     * @param state
     * @param uptime
     * @param downtime
     * @param unknown
     */
    public ResourcesCheckReport(String id, String info, String dataId, String dataName, Integer state, Integer uptime, Integer downtime, Integer unknown)
    {
        this.id = id;
        this.info = info;
        this.dataId = dataId;
        this.dataName = dataName;
        this.state = state;
        this.uptime = uptime;
        this.downtime = downtime;
        this.unknown = unknown;
        
        // computes other data
        //int totalDays = this.uptime + this.downtime + this.unknown;
        int totalDays = this.uptime + this.downtime;   // 'unknown' not part of the uptime percent computation
        if (totalDays > 0)
        {
            this.uptimeRatio = Math.round(this.uptime * 100 / totalDays);
        }
        else
        {
            this.uptimeRatio = 0;
        }
    }
    
    
    /**
     * Indicates whether some other object is "equal to" this one.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        return (this.getId().equals(((ResourcesCheckReport) obj).getId()));
    }
    
    
    /**
     * Returns a hash code value for this object.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Integer.parseInt(this.getId().substring(4));
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
     * Getter
     * @return the info
     */
    public String getInfo()
    {
        return this.info;
    }
    
    /**
     * Getter
     * @return the dataId
     */
    public String getDataId()
    {
        return this.dataId;
    }
    
    /**
     * Getter
     * @return the dataName
     */
    public String getDataName()
    {
        return this.dataName;
    }
    
    /**
     * Getter
     * @return the state
     */
    public Integer getState()
    {
        return this.state;
    }
    
    /**
     * Getter (String)
     * @return
     */
    public String getStateStr()
    {
        return ResourceDao.getStateDesc(this.state);
    }
    
    /**
     * Getter (String only one word)
     * @return
     */
    public String getStateShortStr()
    {
        return ResourceDao.getShortStateDesc(this.state);
    }
    
    /**
     * Getter
     * @return the uptime
     */
    public Integer getUptime()
    {
        return this.uptime;
    }
    
    /**
     * Getter
     * @return the downtime
     */
    public int getDowntime()
    {
        return this.downtime;
    }
    
    /**
     * Getter
     * @return the unknown
     */
    public Integer getUnknown()
    {
        return this.unknown;
    }
    
    /**
     * Getter
     * @return the uptimeRatio
     */
    public Integer getUptimeRatio()
    {
        return this.uptimeRatio;
    }
    
    /**
     * Getter
     * @return the uptimeRatio
     */
    public String getUptimeRatioStr()
    {
        return "<abbr class=\"idInfo\" title=\"up: " + this.uptime + " / down: " + this.downtime + " / unknown: " + this.unknown + "\">" + this.uptimeRatio + "%</abbr>";
    }
    
    /**
     * Getter
     * @return the htmlClass
     */
    public String getHtmlClass()
    {
        return this.htmlClass;
    }
    
    /**
     * Setter
     * @param htmlClass the htmlClass to set
     */
    public void setHtmlClass(String htmlClass)
    {
        this.htmlClass = htmlClass;
    }
    
    /**
     * Getter
     * @return the colour
     */
    public String getColour()
    {
        return ResourceDao.getStateColour(this.state);
    }
    
    /**
     * Generates a HTML link towards the details of the health of the current resource.
     * @return
     */
    public String getResourceLink()
    {
        return "<a href=\"mdb?section=health_check_details&amp;id=" + this.getId() + "\" title=\"Detailed health report for: " + this.getInfo() + "\">" + this.getId() + "</a>";
    }
    
    /**
     * Generates a HTML link towards the data type detailed page.
     * The title is first to be able to have the sorting working in names and not identifiers.
     * @return
     */
    public String getDataTypeLink()
    {
        String link;
        try
        {
            link = URLEncoder.encode(this.getDataId(), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            link = this.getDataId();
        }
        return "<a title=\"Access to: " + this.getDataName() + "\" href=\"" + link + "\">" +  this.getDataName() + "</a>";
    }
}
