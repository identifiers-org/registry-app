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


import uk.ac.ebi.miriam.tools.CommonFunctions;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.commons.lang3.StringEscapeUtils;


/**
 * <p>Stores all the details about the health of a specific resource (cf. table 'mir_url_check').
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
 * @version 20130807
 */
public class ResourceCheckDetails
{
	private String id;
	private String info;
	private String dataId;
	private String dataName;
	private Boolean obsolete;
	private int state;
	private Date lastCheck;
	private Date lastSuccessCheck;
	private Date lastFailingCheck;
	private Date beginUptimePeriod;   // first day of the last uptime period
	private Date beginDowntimePeriod;   // first day of the last downtime period
	private int uptime;
	private int downtime;
	private int unknown;
	private int uptimeRatio;   // percentage
	private int downtimeRatio;   // percentage
	private int unknownRatio;   // percentage
	private int lastUptimePeriod;   // length (in days) of the last uptime period
	private int lastDowntimePeriod;   // length (in days) of the last downtime period
	private String keyword;
	private Boolean ajax;   // if this resource uses Ajax to load the content of the pages
	private Boolean preventsFrame;   // if this resource prevents its content to be loaded in a frame
	private Boolean binary;   // is this resource returns a binary file (where no keyword can be checked)
	private String logs;
	private String errors;
	private String url;
	
	
	/*
	 * Default constructor (builds an empty object).
	 *
	public ResourceCheckDetails()
	{
		// nothing here
	}
	*/
	
	
	/**
	 * Constructor with parameters (builds a full object).
	 */
	public ResourceCheckDetails(String id, String info, String dataId, String dataName, int state, Date lastCheck, Date lastSuccessCheck, Date lastFailingCheck, Date beginUptimePeriod, Date beginDowntimePeriod, int uptime, int downtime, int unknown, int ajax, int frame, int binary, String keyword, String logs, String errors, String url)
	{
		//super();
		this.id = id;
		this.info = info;
		this.dataId = dataId;
		this.dataName = dataName;
		this.state = state;
		this.lastCheck = lastCheck;
		this.lastSuccessCheck = lastSuccessCheck;
		this.lastFailingCheck = lastFailingCheck;
		this.beginUptimePeriod = beginUptimePeriod;
		this.beginDowntimePeriod = beginDowntimePeriod;
		this.uptime = uptime;
		this.downtime = downtime;
		this.unknown = unknown;
		if (ajax == 0)
		{
		    this.ajax = false;
		}
		else
		{
		    this.ajax = true;
		}
		if (frame == 0)
		{
		    this.preventsFrame = false;
		}
		else
		{
		    this.preventsFrame = true;
		}
		if (binary == 0)
		{
		    this.binary = false;
		}
		else
		{
		    this.binary = true;
		}
		this.keyword = keyword;
		this.logs = logs;
		this.errors = errors;
		this.url = url;
		
		// computes other data
		int totalDays = this.uptime + this.downtime + this.unknown;
		int workDays = this.uptime + this.downtime;
		if (totalDays > 0)
		{
    		this.unknownRatio = (this.unknown * 100 / totalDays);
		}
		else
		{
		    this.unknownRatio = 0;
		}
		if (workDays > 0)
		{
		    this.uptimeRatio = Math.round(this.uptime * 100 / workDays);   // 'unknown' not part of the uptime percent computation
            this.downtimeRatio = Math.round(this.downtime * 100 / workDays);   // 'unknown' not part of the downtime percent computation
		}
		else
		{
		    this.uptimeRatio = 0;
            this.downtimeRatio = 0;
		}
		
		
		// the resource is responsive
		if (this.state == 1)
		{
    		Calendar calA = new GregorianCalendar();
            calA.setTime(this.lastSuccessCheck);
            Calendar calB = new GregorianCalendar();
            calB.setTime(this.beginUptimePeriod);
    		this.lastUptimePeriod = CommonFunctions.daysDifference(calA, calB);
    		this.lastDowntimePeriod = 0;
		}
		else
		{
		    Calendar calA = new GregorianCalendar();
            calA.setTime(this.lastFailingCheck);
            Calendar calB = new GregorianCalendar();
            calB.setTime(this.beginDowntimePeriod);
            this.lastDowntimePeriod = CommonFunctions.daysDifference(calA, calB);
		    this.lastUptimePeriod = 0;
		}
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
	 * Setter
     * @param obsolete the obsolete to set
     */
    public void setObsolete(Boolean obsolete)
    {
        this.obsolete = obsolete;
    }
    
    /**
     * Getter
     * @return the obsolete
     */
    public Boolean isObsolete()
    {
        return this.obsolete;
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
	 * Getter
	 * @return the state
	 */
	public int getState()
	{
		return this.state;
	}
	
	/**
	 * Returns the colour associated with a given state identifier.
	 * For usage within JSPs.
	 * @return
	 */
	public String getStateColour()
	{
	    return ResourceDao.getStateColour(this.state);
	}
	
	/**
	 * Returns the description associated with a given state identifier.
     * For usage within JSPs.
	 * @return
	 */
	public String getStateStr()
	{
	    return ResourceDao.getStateDesc(this.state);
	}
	
	/**
	 * Getter
	 * @return the lastCheck
	 */
	public Date getLastCheck()
	{
		return this.lastCheck;
	}
	
	/**
	 * Getter
	 * @return the lastSuccessCheck
	 */
	public Date getLastSuccessCheck()
	{
		return this.lastSuccessCheck;
	}
	
	/**
	 * Getter
     * @return the lastFailingCheck
     */
    public Date getLastFailingCheck()
    {
        return this.lastFailingCheck;
    }
    
    /**
	 * Getter
     * @return the beginUptimePeriod
     */
    public Date getBeginUptimePeriod()
    {
        return this.beginUptimePeriod;
    }
    
    /**
     * Getter
     * @return the beginDowntimePeriod
     */
    public Date getBeginDowntimePeriod()
    {
        return this.beginDowntimePeriod;
    }
    
    /**
	 * Getter
	 * @return the uptime
	 */
	public int getUptime()
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
    public int getUnknown()
    {
        return this.unknown;
    }
    
    /**
	 * Getter
	 * @return the uptimeRatio
	 */
	public int getUptimeRatio()
	{
		return this.uptimeRatio;
	}
	
	/**
	 * Getter
     * @return the downtimeRatio
     */
    public int getDowntimeRatio()
    {
        return this.downtimeRatio;
    }
    
    /**
     * Getter
     * @return the unknownRatio
     */
    public int getUnknownRatio()
    {
        return this.unknownRatio;
    }
    
    /**
     * Getter
     * @return the lastUptimePeriod
     */
    public int getLastUptimePeriod()
    {
        return this.lastUptimePeriod;
    }
    
    /**
     * Getter
     * @return the lastDowntimePeriod
     */
    public int getLastDowntimePeriod()
    {
        return this.lastDowntimePeriod;
    }
    
    /**
     * Getter
     * @return the keyword
     */
    public String getKeyword()
    {
        return this.keyword;
    }
    
    /**
     * Getter
     * @return the ajax
     */
    public boolean isAjax()
    {
        return this.ajax;
    }
    
    /**
     * @return the preventsFrame
     */
    public Boolean getPreventsFrame()
    {
        return this.preventsFrame;
    }
    
    /**
     * @param preventsFrame the preventsFrame to set
     */
    public void setPreventsFrame(Boolean preventsFrame)
    {
        this.preventsFrame = preventsFrame;
    }
    
    /**
     * Getter
     * @return the binary
     */
    public boolean isBinary()
    {
        return this.binary;
    }
    
    /**
     * Getter
     * @return the binary
     */
    public boolean getBinary()
    {
        return this.binary;
    }
    
    /**
     * Setter
     * @param binary the binary to set
     */
    public void setBinary(boolean binary)
    {
        this.binary = binary;
    }
    
    /**
	 * Getter
	 * @return the logs
	 */
	public String getLogs()
	{
		return this.logs;
	}
	
	/**
	 * Setter
	 * @param logs the logs to set
	 */
	public void setLogs(String logs)
	{
		this.logs = logs;
	}
	
	/**
	 * Getter
	 * @return the errors
	 */
	public String getErrors()
	{
		return this.errors;
	}
	
	/**
     * Setter
     * @param errors the errors to set
     */
    public void setErrors(String errors)
    {
        this.errors = errors;
    }
	
	/**
	 * Getter
     * @return the url
     */
    public String getUrl()
    {
        return this.url;
    }
    
    /**
     * Getter (formated URL for usage in HTML pages, specially javascript)
     * @return
     */
    public String getHtmlUrl()
    {
        return StringEscapeUtils.escapeHtml4(this.url).replaceAll("'", "\\\\'");
    }
}
