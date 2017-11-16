/*
 * MIRIAM Registry (Web Application)
 * The Registry is an online resource created to catalogue data collections 
 * (their URIs and the corresponding physical URLs, whether these are controlled vocabularies or databases)
 * and provide unique and stable identifiers for life science, in the form of URIs. 
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2014  BioModels.net (EMBL - European Bioinformatics Institute)
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


package uk.ac.ebi.miriam.tools;


import uk.ac.ebi.miriam.db.ResourceDao;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.client.utils.URIBuilder;


/**
 * Log record class for URLs (or Resources).
 * 
 * @author Camille Laibe <camille.laibe@ebi.ac.uk>
 * @version 20140114
 */
public class ResourceLog
{
	private String resourceId;
	private String datatypeId;
	private String url;
	private String keyword;
	private boolean binary;
	private boolean ajax;
	private boolean obsolete;
	private boolean restricted;
	private StringBuilder logs;
	private StringBuilder errors;
	private StringBuilder stream;
	private boolean responsive;
	private boolean redirected;
	private boolean success;   // whether or not the resource is responsive and the keyword has been found
	private Integer state;
	private int nbAttempts;
	private List<ResourceLog> redirections;
	//private Document htmlDoc;
	
	
	/**
     * Default constructor (for redirected URLs).
     */
    public ResourceLog(String url, String keyword)
    {
        this.resourceId = "redirection";
        this.datatypeId = "redirection";
        this.url = encodeUrl(url);
        this.keyword = keyword;
        this.ajax = false;
        this.binary = false;
        this.obsolete = false;
        this.restricted = false;
        this.responsive = false;
        this.success = false;
        this.state = ResourceDao.STATE_UNKNOWN;
        this.redirected = false;
        this.nbAttempts = 0;
        this.logs = new StringBuilder();
        this.errors = new StringBuilder();
        this.stream = new StringBuilder();
        this.setRedirections(new ArrayList<ResourceLog>());
        //this.htmlDoc = null;
    }
	
    
	/**
	 * Constructor with parameters.
	 */
	public ResourceLog(String resourceId, String collectionId, String URL, String keyword, boolean obsolete, boolean restricted, boolean ajax, boolean binary)
	{
		this.resourceId = resourceId;
		this.datatypeId = collectionId;
		this.url = encodeUrl(URL);
		this.keyword = keyword;
		this.ajax = ajax;
		this.binary = binary;
		this.obsolete = obsolete;
        this.restricted = restricted;
		this.responsive = false;
		this.success = false;
		this.state = ResourceDao.STATE_UNKNOWN;
		this.redirected = false;
		this.nbAttempts = 0;
		this.stream = new StringBuilder();
		this.logs = new StringBuilder();
		this.errors = new StringBuilder();
		this.setRedirections(new ArrayList<ResourceLog>());
		//this.htmlDoc = null;
	}
	
	
	/**
	 * Properly encode URLs.
	 * This should handle things like '_' in the hostname or ' ' in the query part.
     * If both cases happen in the same URL, some customised code (which mainly replaces spaces with '%20') should take care of the potential issues.
	 * @param url
	 * @return
	 */
	private String encodeUrl(String url)
    {
        String encodedUrl = null;
        URL tmpUrl = null;
        URI tmpUri = null;
        
        try
        {
            // there is no '_' in the hostname
            if (! checkHostUnderscore(url))
            {
                tmpUrl = new URL(url);
                tmpUri = new URI(tmpUrl.getProtocol(), tmpUrl.getUserInfo(), tmpUrl.getHost(), tmpUrl.getPort(), tmpUrl.getPath(), tmpUrl.getQuery(), tmpUrl.getRef());
            }
            else   // there is at least one '_' in the hostname
            {
                // manually percent encodes all spaces (as URIUtils.createURI does not handle them)
                if (url.contains(" "))
                {
                    url = url.replaceAll(" ", "%20");
                }
                tmpUrl = new URL(url);
                URIBuilder tmp = new URIBuilder();
                tmp.setScheme(tmpUrl.getProtocol());
                tmp.setHost(tmpUrl.getHost());
                tmp.setPort(tmpUrl.getPort());
                tmp.setPath(tmpUrl.getPath());
                tmp.setQuery(tmpUrl.getQuery());   // should be replaced by setParameters()
                tmp.setFragment(tmpUrl.getRef());
                tmpUri = tmp.build();
                
                //tmpUri = URIUtils.createURI(tmpUrl.getProtocol(), tmpUrl.getHost(), tmpUrl.getPort(), tmpUrl.getPath(), tmpUrl.getQuery(), tmpUrl.getRef());
            }
            encodedUrl = tmpUri.toString();
        }
        catch (MalformedURLException e)
        {
            System.err.println("MalformedURLException raised while encoding the URL: " + url);
            System.err.println(e.getMessage());
        }
        catch (URISyntaxException e)
        {
            System.err.println("URISyntaxException raised while encoding the URL: " + url);
            System.err.println(e.getMessage());
        }
        
        
        return encodedUrl;
    }
	
	
	/**
     * Checks whether the hostname contains an underscore '_' or not.
     * Presence of underscore causes a URISyntaxException (Illegal character in hostname), when using java.net.URI (from Java 1.6).
     * @param url
     * @return
     */
    private static Boolean checkHostUnderscore(String url)
    {
        Boolean containsUnderscore = false;
        int hostStart = url.indexOf("://") + 3;
        
        if (hostStart > 0)
        {
            int hostEnd = url.indexOf("/", hostStart + 1);
            if (hostEnd > 0)
            {
                String host = url.substring(hostStart, hostEnd);
                if (host.contains("_"))
                {
                    containsUnderscore = true;
                }
            }
        }
        
        return containsUnderscore;
    }


    /**
	 * Indicates whether some other object is "equal to" this one.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		boolean equals = false;
		
		if (obj instanceof ResourceLog)
		{
			equals = this.getResourceId().equals(((ResourceLog) obj).getResourceId());
		}
		
		return equals;
	}
	
	
	/**
	 * Returns a string representation of this object.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		
		if (null != this.resourceId)
		{
			str.append("Resource Id: " + this.resourceId + "\n");
		}
		if (null != this.datatypeId)
		{
			str.append("Datatype Id: " + this.datatypeId + "\n");
		}
		str.append("URL: " + this.url + "\n");
		str.append("Keyword: " + this.keyword  + "\n");
		str.append("Obsolete: " + this.obsolete  + "\n");
		str.append("Access restricted: " + this.restricted  + "\n");
		str.append("Responsive? " + this.isResponsive() + "\n");
		if (this.isResponsive())
		{
			str.append("Keyword found? " + this.isSuccess() + "\n");
			str.append("Ajax used? " + this.ajax + "\n");
			str.append("Returns binary data? " + this.binary + "\n");
			str.append("Redirected? " + this.isRedirected() + "\n");
			if (this.isRedirected())
			{
				int i = 0;
				for (ResourceLog log: this.redirections)
				{
					str.append("- redirection #" + i + ":\n");
					String[] elts = log.toString().split("\n");
					for (String elt: elts)
					{
						str.append("\t" + elt + "\n");
					}
					++ i;
				}
			}
		}
		str.append("State: " + ResourceDao.getStateDesc(this.state) + "\n");
		str.append("Nb attempts: " + this.getNbAttempts() + "\n");
		str.append("Logs:\n");
		str.append(this.getLogs());
		str.append("Errors:\n");
		str.append(this.getErrors());
		
		if (! this.isSuccess())
		{
			str.append("Stream:\n");
			str.append(this.getStream());
		}
		
		return str.toString();
	}
	
	
	/**
	 * Raw report.
	 * @return
	 */
	public String getRawReport()
	{
	    return this.toString();
	}
	
	
	/**
	 * Getter
	 * @return the resourceId
	 */
	public String getResourceId()
	{
		return this.resourceId;
	}
	
	
	/**
	 * Setter
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(String resourceId)
	{
		this.resourceId = resourceId;
	}
	
	
	/**
	 * Getter
	 * @return the datatypeId
	 */
	public String getDatatypeId()
	{
		return this.datatypeId;
	}
	
	
	/**
	 * Setter
	 * @param datatypeId the datatypeId to set
	 */
	public void setDatatypeId(String datatypeId)
	{
		this.datatypeId = datatypeId;
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
	 * Setter
	 * @param url the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
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
	 * Setter
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword)
	{
		this.keyword = keyword;
	}
	
	/**
	 * Getter
	 * @return the logs
	 */
	public String getLogs()
	{
		return this.logs.toString();
	}
	
	/**
	 * Adds a piece of log
	 * @param log the log to add
	 */
	public void addLog(String log)
	{
		this.logs.append(log + "\n");
	}
	
	/**
	 * Getter
	 * @return the responsive
	 */
	public boolean isResponsive()
	{
		return this.responsive;
	}
	
	/**
	 * Setter
	 * @param responsive the responsive to set
	 */
	public void setResponsive(boolean responsive)
	{
		this.responsive = responsive;
	}
	
	/**
	 * Getter
	 * @return the nbAttempts
	 */
	public int getNbAttempts()
	{
		return this.nbAttempts;
	}
	
	/**
	 * Increments the number of attempts
	 */
	public void incNbAttempts()
	{
		this.nbAttempts ++;
	}
	
	/**
	 * Getter
	 * @return the errors
	 */
	public String getErrors()
	{
		return this.errors.toString();
	}
	
	/**
	 * Adds an error message
	 * @param error the error to add
	 */
	public void addError(String error)
	{
		this.errors.append(error + "\n");
	}
	
	/**
	 * Getter
	 * @return the stream
	 */
	public String getStream()
	{
		return this.stream.toString();
	}
	
	/**
	 * Setter
	 * @param stream
	 */
	public void setStream(String stream)
	{
	    this.stream = new StringBuilder(stream);
	}
	
	/**
	 * Appends some information to the stream
	 * @param stream the stream to set
	 */
	public void addStream(String stream)
	{
        this.stream.append(stream);
        this.stream.append("\n==============================================================================================================\n\n");   
	}
	
	/**
	 * Clean the stream (empty its content)
	 */
	public void cleanStream()
	{
		this.stream = new StringBuilder();
	}
	
	/**
	 * Getter
	 * @return the success
	 */
	public boolean isSuccess()
	{
		return this.success;
	}
	
	/**
	 * Setter
	 * @param success the success to set
	 */
	public void setSuccess(boolean success)
	{
		this.success = success;
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
     * Setter
     * @param state the state to set
     */
    public void setState(Integer state)
    {
        this.state = state;
    }
    
	/**
	 * Getter
	 * @return the redirected
	 */
	public boolean isRedirected()
	{
		return this.redirected;
	}
	
	/**
	 * Setter
	 * @param redirected the redirected to set
	 */
	public void setRedirected(boolean redirected)
	{
		this.redirected = redirected;
	}
	
	/**
	 * Getter
	 * @return the redirections
	 */
	public List<ResourceLog> getRedirections()
	{
		return this.redirections;
	}
	
	/**
	 * Setter
	 * @param redirections the redirections to set
	 */
	public void setRedirections(List<ResourceLog> redirections)
	{
		this.redirections = redirections;
	}
	
	/**
	 * Adds a new redirection.
	 * @param redirection
	 */
	public void addRedirection(ResourceLog redirection)
	{
		this.redirections.add(redirection);
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
     * Setter
     * @param ajax the ajax to set
     */
    public void setAjax(boolean ajax)
    {
        this.ajax = ajax;
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
     * Setter
     * @param binary the binary to set
     */
    public void setBinary(boolean binary)
    {
        this.binary = binary;
    }
    
    /**
     * Getter
     * @return the obsolete
     */
    public boolean isObsolete()
    {
        return this.obsolete;
    }
    
    /**
     * Setter
     * @param obsolete the obsolete to set
     */
    public void setObsolete(boolean obsolete)
    {
        this.obsolete = obsolete;
    }
    
    /**
     * Getter
     * @return the restricted
     */
    public boolean isRestricted()
    {
        return this.restricted;
    }
    
    /**
     * Setter
     * @param restricted the restricted to set
     */
    public void setRestricted(boolean restricted)
    {
        this.restricted = restricted;
    }
    
	/*
	 * Getter of the HTML document, requires fetching and parsing (lazy loading)
	 * @return the htmlDoc
	 * @throws IOException 
	 *
	public Document getHtmlDoc() throws IOException
	{
		if (null == htmlDoc)
		{
			Document doc = Jsoup.connect(this.getUrl()).get();
			return doc;
		}
		else
		{
			return this.htmlDoc;
		}
	}
	*/
    
	/*
	 * Setter
	 * @param htmlDoc the htmlDoc to set
	 *
	public void setHtmlDoc(Document htmlDoc)
	{
		this.htmlDoc = htmlDoc;
	}
	*/
}
