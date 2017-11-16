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

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.connectors.HTTPTunnelConnector;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * URLs Checking System.
 * Handles:
 * - redirections via meta refresh
 * - redirection via javascript
 * - frames
 * Checks that the returned content is correct (not an error message)
 * If necessary, will fake a Mozilla based web browser in order to get the proper content (via customised user-agent)
 * This uses Apache HttpComponents (http://hc.apache.org/) and jsoup (http://jsoup.org/).
 * 
 * @author Camille Laibe <camille.laibe@ebi.ac.uk>
 * @version 20140307
 */
public class UCS
{
    private Logger logger = Logger.getLogger(UCS.class);
	private final static String SCRIPT_REDIRECT_REGEXP_1 = "(?i)<script\\s+[^>]*>\\s*window.location(.href)?\\s*=\\s*[\"\'][^>]*</script>";
	private final static String SCRIPT_REDIRECT_REGEXP_2 = "(?i)<script\\s+[^>]*>\\s*window.location.replace\\(\\s*[\"\'][^>]*</script>";
	private final static String META_REDIRECT_REGEXP = "(?i)<meta\\s*http-equiv\\s*=\\s*[\"\']refresh[\"\']\\s*content\\s*=\\s*'\\d;url\\s*=\\s*[^\"\'>]*[\"\']/?>";
	//private final static String FRAME_LOADING_REGEXP = "(?i)<frame\\s+[^>]*(>\\s*</frame|\\s*/?)>";
	//private final static String FRAME_LOADING_REGEXP = "(?i)<frame\\s+(.|\\s)*(>\\s*</frame|[\"\']\\s*/?)>";  // does not work
	//private final static String FRAME_LOADING_REGEXP = "(?i)<frame\\s+.*?(>\\s*</frame|[\"\']\\s*/?)>";   // does not work
	private final static String APP_NAME = "RHCS";
	private final static String APP_VERSION = "4.2";
	private final static String USER_AGENT = "Mozilla/5.0 (compatible; Linux x86_64) Identifiers.org (like Gecko) " + APP_NAME + "/" + APP_VERSION;   // default: "Apache-HttpClient/4.0.1 (java 1.5)"
	private final static String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	private final static String FROM = "biomodels-net-support@lists.sf.net";
	private final static String REFERER = "http://identifiers.org/registry/";
	//private final static String PROXY_HOST = "www-proxy.ebi.ac.uk";   // old proxy
	private final static String PROXY_HOST = "hx-wwwcache.ebi.ac.uk";
	//private final static Integer PROXY_PORT = 8080;   // old proxy
	private final static Integer PROXY_PORT = 3128;
	
	
	/**
	 * Default constructor.
	 */
	public UCS()
	{
		// nothing
	}
	
	
	/**
	 * Checks if a resource (URL) is responsive and gives the expected content.
	 * @param resource custom object storing info about a resource (kind of extended URL)
	 * @return state code, which value can be: STATE_SUCCESS, STATE_PROBABLY, STATE_FAILURE, STATE_OBSOLETE, STATE_RESTRICTED or STATE_UNKNOWN.
	 */
	public synchronized Integer check(ResourceLog resource)
	{
	    Boolean performCheck = true;
	    
	    // the fragment identifier (part after the '#' in the URL) causes issues with HttpClient, so we remove it (anyway, it should not be sent to the server)
	    if (resource.getUrl().contains("#"))
	    {
	        resource.setUrl(resource.getUrl().substring(0, resource.getUrl().indexOf("#")));
	    }
	    
	    if (resource.isObsolete())   // checks whether the resource is obsolete
        {
	        resource.addLog("- resource is obsolete.");
	        resource.setSuccess(false);
	        resource.setState(ResourceDao.STATE_OBSOLETE);
            performCheck = false;
        }
	    else if (resource.isRestricted())   // checks whether the resource is access restricted
	    {
	        resource.addLog("- resource has some kind of access restriction.");
	        resource.setSuccess(false);
            resource.setState(ResourceDao.STATE_RESTRICTED);
	        performCheck = false;
	    }
	    else if (resource.getUrl().contains("-*--NO_EXAMPLE_IDENTIFIER_PROVIDED--*-"))   // checks whether an example identifier is available
        {
            resource.addLog("- no example identifier stored.");
            resource.setSuccess(false);
            resource.setState(ResourceDao.STATE_UNKNOWN);
            performCheck = false;
        }
	    else if ((null == resource.getUrl()) || (resource.getUrl().matches("\\s*")))   // checks whether a URL is available
        {
            resource.addLog("- no URL stored.");
            resource.setSuccess(false);
            resource.setState(ResourceDao.STATE_UNKNOWN);
            performCheck = false;
        }
	    else if ((null == resource.getKeyword()) || (resource.getKeyword().matches("\\s*")))   // checks whether a keyword is available
	    {
	        resource.addLog("- no keyword stored.");
	        resource.setSuccess(false);
	        resource.setState(ResourceDao.STATE_UNKNOWN);
	        performCheck = false;
	    }
	    
	    if (performCheck)
	    {
    	    // generic check: server error or keyword not found (customised user-agent)
    	    int state = checkUrl(resource, true);
    	    
    	    if (ResourceDao.STATE_FAILURE == state)
    	    {
    	        holdOn(1);   // wait 1s
    	        checkRedirections(resource, true);   // customised user-agent
        	    
        	    holdOn(1);   // wait 1s
                // still no success, we do all this again without custom user-agent
        	    if (! resource.isSuccess())
        	    {
        	        if (ResourceDao.STATE_FAILURE == checkUrl(resource, false))   // no customised user agent
        	        {
        	            checkRedirections(resource, false);
        	        }
        	    }
    	    	
    	    	resource.incNbAttempts();
    	    }
	    }
		
		return resource.getState();
	}
	
	
	/*
	 * Checks if some redirections or frames are used.  
	 * @param resource
	 */
	private void checkRedirections(ResourceLog resource, Boolean agent)
	{
        // redirection via meta refresh
        if (checkMetaRedirect(resource.getStream()))
        {
            resource.setRedirected(true);
            resource.addLog("- redirection via meta refresh");
            
            List<String> redirectUrls = getUrlsFromMetaRedirect(resource.getStream());
            //report.addLog("- redirected URL(s):");
            if (null != redirectUrls)
            {
                for (String url: redirectUrls)
                {
                    String redirectedUrl = urlCompletion(url, resource.getUrl());
                    //report.addLog("\t- " + redirectedUrl);
                    
                    ResourceLog redirection = new ResourceLog(resource.getResourceId(), resource.getDatatypeId(), redirectedUrl, resource.getKeyword(), resource.isObsolete(), resource.isRestricted(), resource.isAjax(), resource.isBinary());
                    resource.addRedirection(redirection);
                    
                    if (ResourceDao.STATE_SUCCESS == checkUrl(redirection, agent))
                    {
                        resource.setSuccess(true);
                        resource.setState(ResourceDao.STATE_SUCCESS);
                    }
                }
            }
        }
        
        // redirection via javascript (1)
        else if (checkScriptRedirect1(resource.getStream()))
        {
            resource.setRedirected(true);
            resource.addLog("- redirection via javascript (1)");
            
            List<String> redirectUrls = getUrlsFromScriptRedirect1(resource.getStream());
            //report.addLog("- redirected URL(s):");
            if (null != redirectUrls)
            {
                for (String url: redirectUrls)
                {
                    String redirectedUrl = urlCompletion(url, resource.getUrl());
                    //report.addLog("\t- " + redirectedUrl);
    
                    ResourceLog redirection = new ResourceLog(resource.getResourceId(), resource.getDatatypeId(), redirectedUrl, resource.getKeyword(), resource.isObsolete(), resource.isRestricted(), resource.isAjax(), resource.isBinary());
                    resource.addRedirection(redirection);
                    
                    if (ResourceDao.STATE_SUCCESS == checkUrl(redirection, agent))
                    {
                        resource.setSuccess(true);
                        resource.setState(ResourceDao.STATE_SUCCESS);
                    }
                }
            }
        }
        
        // redirection via javascript (2)
        else if (checkScriptRedirect2(resource.getStream()))
        {
            resource.setRedirected(true);
            resource.addLog("- redirection via javascript (2)");
            
            List<String> redirectUrls = getUrlsFromScriptRedirect2(resource.getStream());
            //report.addLog("- redirected URL(s):");
            if (null != redirectUrls)
            {
                for (String url: redirectUrls)
                {
                    String redirectedUrl = urlCompletion(url, resource.getUrl());
                    //report.addLog("\t- " + redirectedUrl);
    
                    ResourceLog redirection = new ResourceLog(resource.getResourceId(), resource.getDatatypeId(), redirectedUrl, resource.getKeyword(), resource.isObsolete(), resource.isRestricted(), resource.isAjax(), resource.isBinary());
                    resource.addRedirection(redirection);
                    
                    if (ResourceDao.STATE_SUCCESS == checkUrl(redirection, agent))
                    {
                        resource.setSuccess(true);
                        resource.setState(ResourceDao.STATE_SUCCESS);
                    }
                }
            }
        }
        
        // frame(s) used
        else if (checkFrameUsed(resource.getStream()))
        {
            List<String> redirectedUrls = new ArrayList<String>();
            searchFrameRedirect(resource, redirectedUrls, agent);
            checkFrameRedirectState(resource);
        }
        
        // no redirection detected...
        else
        {
            resource.addLog("- no redirection detected");
        }
	}
	
	
	/*
	 * Check what kind of URL the resource is associated with. Depending on the type, redirects to the proper checking method.
	 */
	private Integer checkUrl(ResourceLog resource, Boolean agent)
	{
	    Integer result = ResourceDao.STATE_UNKNOWN;   // by default the result of the check is 'unknown'
	    
	    if ((resource.getUrl().startsWith("http:")) || (resource.getUrl().startsWith("https:")))
	    {
	        result = checkHttpUrl(resource, agent);
	    }
	    else if (resource.getUrl().startsWith("ftp:"))
	    {
	        result = checkFtpUrl(resource, agent);
	    }
	    else   // unsupported URL
	    {
	        resource.addError("- Unsupported URL!");
	    }
	    
	    return result;
	}
	
	
	/*
     * Checks if a URL (either http or https) is responsive and gives the expected content.
     * This does only a partial (and simple) check: it doesn't follow potential redirections.
     * @param resource
     * @param proxy whether to use a proxy or not (that sometimes is an issue, in both cases)
     * @return
     */
	private Integer checkHttpUrl(ResourceLog resource, Boolean agent)
	{
		CloseableHttpClient httpclient = null;
		HttpEntity entity = null;
		Integer httpCode = 0;
		
		resource.addLog("+ HTTP GET: " + resource.getUrl());
		resource.addLog("- customised user-agent used: " + agent);
		
		try
		{
			// sets the parameters
			Builder requestBuilder = RequestConfig.custom();
			requestBuilder.setCookieSpec(CookieSpecs.BEST_MATCH);   // cookie policy
			requestBuilder.setConnectionRequestTimeout(30000);
			requestBuilder.setConnectTimeout(30000);
			requestBuilder.setSocketTimeout(30000);
			requestBuilder.setRedirectsEnabled(true);
			RequestConfig config = requestBuilder.build();
			
			// proxy (EBI infrastructure requirement)
		    HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
		    DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		    // proxy (from environment) > seems to cause issues with some resources, for example the ones using https
		    //ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(httpclient.getConnectionManager().getSchemeRegistry(), ProxySelector.getDefault());
            //((AbstractHttpClient) httpclient).setRoutePlanner(routePlanner);
		    
		    // creates client with all parameters
		    HttpClientBuilder clientBuilder = HttpClients.custom();
		    clientBuilder.setRoutePlanner(routePlanner);
		    clientBuilder.setDefaultRequestConfig(config);
            if (agent)   // customised user-agent required
            {
            	clientBuilder.setUserAgent(USER_AGENT);   // causes issues with IMEx (for example)
                //httpget.setHeader("User-Agent", "UserAgent: " + USER_AGENT);
                //httpget.setHeader("user-agent", USER_AGENT);
                resource.addLog("- user agent: " + USER_AGENT);
            }
            if (resource.getUrl().startsWith("https:"))   // handling of HTTPS connection
            {
            	// custom strategy: trusts all certificate chains regardless of their validity
            	TrustStrategy acceptingTrustStrategy = new TrustStrategy()
            	{
                    @Override
                    public boolean isTrusted(X509Certificate[] certificate, String authType)
                    {
                        return true;   // always trusted
                    }
                };
                
            	SSLContextBuilder sslBuilder = new SSLContextBuilder();
            	try
            	{
					sslBuilder.loadTrustMaterial(null, acceptingTrustStrategy);
					SSLConnectionSocketFactory sslconnection = new SSLConnectionSocketFactory(sslBuilder.build());
					clientBuilder.setSSLSocketFactory(sslconnection);
					resource.addLog("- creation of a SSL connection");
				}
            	catch (NoSuchAlgorithmException e)
            	{
            		logger.error("Unable to create an SSL connection!");
            		resource.addLog("- unable to create SSL connection");
				}
            	catch (KeyStoreException e)
            	{
            		logger.error("Unable to create an SSL connection!");
            		resource.addLog("- unable to create SSL connection");
				}
				catch (KeyManagementException e)
				{
					logger.error("Unable to create an SSL connection!");
					resource.addLog("- unable to create SSL connection");
				}
            }
		    httpclient = clientBuilder.build();
		    
		    // request
            HttpGet httpget = new HttpGet(resource.getUrl());
            
            // sets additional parameters
            httpget.setHeader("Referer", REFERER);
            httpget.setHeader("From", FROM);
            httpget.setHeader("Accept", ACCEPT);
            //httpget.setHeader("Content-Encoding", "UTF-8");   // seems to cause some issues
            
            // logs the headers used
            Header[] headers = httpget.getAllHeaders();
            for (Header head: headers)
            {
                resource.addLog("- header " + head.getName() + ": " + head.getValue());   // DEBUG
            }
            
            // performs the HTTP GET query
            try
            {
            	CloseableHttpResponse response = httpclient.execute(httpget);
            	httpCode = response.getStatusLine().getStatusCode();   // for example "200"
                String message = response.getStatusLine().getReasonPhrase();   // for example "OK"
    			resource.addLog("- HTTP code returned: " + httpCode + " (" + message + ")");
    			entity = response.getEntity();
            }
            catch (IOException e)
            {
            	resource.addLog("! Unable to perform the HTTP request: " + e.getMessage());
            }
            
			if (entity != null)
            {
			    String streamStr = null;
			    
			    // retrieves the HTML content (if possible)
			    if (null != entity.getContent())
			    {
			        InputStream stream = entity.getContent();
			        streamStr = convertStreamToString(stream, resource);
			        resource.addStream(streamStr);
			        stream.close();
			        resource.addLog("- content retrieved");
			    }
			    else
			    {
			        resource.addLog("- no content retrieved");
			    }
			    
    			// successful request
    			if ((httpCode >= 200) && (httpCode < 300))
    			{
                    resource.setResponsive(true);
    			    
                    // can we search for a keyword?
                    if (resource.isBinary())
                    {
                        resource.addLog("- The returned file is binary, so we cannot check for the presence of a keyword.");
                        resource.setState(ResourceDao.STATE_PROBABLY);
                    }
                    else if (resource.isAjax())
                    {
                        resource.addLog("- The resource uses Ajax, so we cannot check for the presence of a keyword.");
                        resource.setState(ResourceDao.STATE_PROBABLY);
                    }
                    else
                    {
        			    // searches for the given keyword
        			    if ((null != streamStr) && (streamStr.contains(resource.getKeyword())))
        			    {
        			    	resource.addLog("- keyword found (" + resource.getKeyword() + ")");
        			    	resource.setSuccess(true);
        			    	resource.setState(ResourceDao.STATE_SUCCESS);
        			    }
        			    else
                        {
                            resource.addLog("- keyword not found (" + resource.getKeyword() + ")");
                            resource.setState(ResourceDao.STATE_FAILURE);
                        }
                    }
    			}
    			else
    			{
    				resource.addError("- server not responding (HTTP code returned: " + httpCode + ")!");
    				resource.setState(ResourceDao.STATE_FAILURE);
    			}
            }
			else
            {
			    resource.addError("- HttpEntity response is null!");
			    resource.setState(ResourceDao.STATE_FAILURE);
            }
		}
		catch (UnknownHostException e)
		{
			resource.addError("- unknown host: " + e.getMessage());
			//logger.debug("RESOURCE ID: " + resource.getResourceId());
			//logger.debug("UnknownHostException raised: " + e.getMessage());
			//logger.debug("StackTrace: " + e.getStackTrace().toString());
			resource.setState(ResourceDao.STATE_FAILURE);
		}
		catch (MalformedURLException e)
		{
			resource.addError("- malformed URL: " + e.getMessage());   // e.getMessage().substring(0, e.getMessage().lastIndexOf(':'))
			//logger.debug("RESOURCE ID: " + resource.getResourceId());
			//logger.debug("MalformedURLException raised: " + e.getMessage());
            //logger.debug("StackTrace: " + e.getStackTrace().toString());
			resource.setState(ResourceDao.STATE_FAILURE);
		}
		catch (FileNotFoundException e)
		{
			resource.addError("- file not found (404): " + e.getMessage());
			//logger.debug("RESOURCE ID: " + resource.getResourceId());
			//logger.debug("FileNotFoundException raised: " + e.getMessage());
            //logger.debug("StackTrace: " + e.getStackTrace().toString());
			resource.setState(ResourceDao.STATE_FAILURE);
		}
		catch (IOException e)
		{
			resource.addError("- communication failure: " + e.getMessage() + "\n");
			resource.addError("  details:     " + e.toString() + "\n");
			Writer result = new StringWriter();
			PrintWriter printWriter = new PrintWriter(result);
		    e.printStackTrace(printWriter);
			resource.addError("  stack trace: " + result.toString());
			//logger.debug("RESOURCE ID: " + resource.getResourceId());
			//logger.debug("IOException raised: " + e.getMessage());
            //logger.debug("StackTrace: " + e.getStackTrace().toString());
			resource.setState(ResourceDao.STATE_FAILURE);
		}
		finally
		{
		    // cleaning
		    if (null != httpclient)
		    {
		    	try
		    	{
					httpclient.close();
				}
		    	catch (IOException e)
		    	{
					// ignore
				}
		    }
		}
		
		return resource.getState();
	}
	
	
	/*
     * Checks if a URL (ftp only) is responsive and gives the expected content.
     * This does only a partial (and simple) check: it doesn't follow potential redirections.
     * @param resource
     * @param proxy whether to use a proxy or not (that sometimes is an issue, in both cases)
     * @return
     */
    private Integer checkFtpUrl(ResourceLog resource, Boolean agent)
    {
        URL url = null;
        
        resource.addLog("+ FTP: " + resource.getUrl());
        resource.addLog("- customised user-agent used: " + agent + " (not used)");
        
        // parses the URL
        try
        {
            url = new URL(resource.getUrl());
        }
        catch (MalformedURLException e)
        {
            resource.addLog("- MalformedURLException raised when parsing the URL!");
            resource.addLog("  " + e.getMessage());
            url = null;   // just to be sure...
        }
        
        // the parsing of the URL was a success
        if (null != url)
        {
            FTPClient ftp = new FTPClient();
            
            // creates a connector using the EBI HTTP proxy
            HTTPTunnelConnector httpProxy = new HTTPTunnelConnector(PROXY_HOST, PROXY_PORT);
            // sets the proxy
            ftp.setConnector(httpProxy);
            
            // connection to server
            try
            {
                resource.addLog("- Connecting to: " + url.getHost() + " ...");
                ftp.connect(url.getHost());
                ftp.login("anonymous", APP_NAME);   // required, the password is supposed to be an email address...
            }
            catch (IllegalStateException e)
            {
                resource.addLog("- IllegalStateException raised when connecting to the FTP server!");
                resource.addLog("  " + e.getMessage());
            }
            catch (IOException e)
            {
                resource.addLog("- IOException raised when connecting to the FTP server!");
                resource.addLog("  " + e.getMessage());
            }
            catch (FTPIllegalReplyException e)
            {
                resource.addLog("- FTPIllegalReplyException raised when connecting to the FTP server!");
                resource.addLog("  " + e.getMessage());
            }
            catch (FTPException e)
            {
                resource.addLog("- FTPException raised when connecting to the FTP server!");
                resource.addLog("  " + e.getMessage());
            }
            
            resource.addLog("- Trying to retrieve the file: " + url.getFile().substring(1) + " ...");
            
            OutputStream stream = new ByteArrayOutputStream();
            try
            {
                ftp.download(url.getFile().substring(1), stream, 0, null);
                resource.setResponsive(true);
                resource.addLog("  file successfully downloaded.");
            }
            catch (IllegalStateException e)
            {
                resource.addLog("- IllegalStateException raised when downloading the file!");
                resource.addLog("  " + e.getMessage());
            }
            catch (IOException e)
            {
                resource.addLog("- IOException raised when downloading the file!");
                resource.addLog("  " + e.getMessage());
            }
            catch (FTPIllegalReplyException e)
            {
                resource.addLog("- FTPIllegalReplyException raised when downloading the file!");
                resource.addLog("  " + e.getMessage());
            }
            catch (FTPException e)
            {
                resource.addLog("- FTPException raised when downloading the file!");
                resource.addLog("  " + e.getMessage());
            }
            catch (FTPDataTransferException e)
            {
                resource.addLog("- FTPDataTransferException raised when downloading the file!");
                resource.addLog("  " + e.getMessage());
            }
            catch (FTPAbortedException e)
            {
                resource.addLog("- FTPAbortedException raised when downloading the file!");
                resource.addLog("  " + e.getMessage());
            }
            
            // saves the file
            String streamStr = stream.toString();
            resource.addStream(streamStr);
            
            // end of the request
            try
            {
                stream.close();
            }
            catch (IOException e)
            {
                resource.addLog("- IOException raised when closing the stream containing the file!");
                resource.addLog("  " + e.getMessage());
            }
            
            // there might have been some errors happening before this stage, so let's check that the resource was responsive
            if (resource.isResponsive())
            {
                // can we look for the keyword in the returned file?
                if (resource.isBinary())
                {
                    resource.addLog("- The returned file is binary, so we cannot check for the presence of a keyword.");
                    resource.setState(ResourceDao.STATE_PROBABLY);
                }
                else if (resource.isAjax())   // this maybe will never be used for FTP resources...
                {
                    resource.addLog("- The resource uses Ajax, so we cannot check for the presence of a keyword.");
                    resource.setState(ResourceDao.STATE_PROBABLY);
                }
                else
                {
                    // searches for the given keyword
                    if ((null != streamStr) && (streamStr.contains(resource.getKeyword())))
                    {
                        resource.addLog("- Keyword found (" + resource.getKeyword() + ").");
                        resource.setSuccess(true);
                        resource.setState(ResourceDao.STATE_SUCCESS);
                    }
                    else
                    {
                        resource.addLog("- Keyword not found (" + resource.getKeyword() + ")!");
                        resource.setState(ResourceDao.STATE_FAILURE);
                    }
                }
            }
            else   // resource not responsive
            {
                resource.addLog("  unable to download the file.");
                resource.setState(ResourceDao.STATE_FAILURE);
            }
            
            // disconnection
            try
            {
                ftp.disconnect(true);
            }
            catch (IllegalStateException e)
            {
                resource.addLog("- IllegalStateException raised when disconnecting from the FTP server!");
                resource.addLog("  " + e.getMessage());
            }
            catch (IOException e)
            {
                resource.addLog("- IOException raised when disconnecting from the FTP server!");
                resource.addLog("  " + e.getMessage());
            }
            catch (FTPIllegalReplyException e)
            {
                resource.addLog("- FTPIllegalReplyException raised when disconnecting from the FTP server!");
                resource.addLog("  " + e.getMessage());
            }
            catch (FTPException e)
            {
                resource.addLog("- FTPException raised when disconnecting from the FTP server!");
                resource.addLog("  " + e.getMessage());
            }
        }
        else   // issues when parsing the URL
        {
            // log message already written
            resource.setState(ResourceDao.STATE_FAILURE);
        }
        
        return resource.getState();
    }
	
	
	/**
     * Searches in the enclosed frames of the resource if the keyword is present
     * @param resource
     * @param redirectedUrls list of URLs already checked (prevent endless loops)
     * @param agent whether a customised user-agent is required or not
     */
    private void searchFrameRedirect(ResourceLog resource, List<String> urls, Boolean agent)
    {
        resource.setRedirected(true);
        resource.addLog("- frame(s) used");
        
        List<String> redirectUrls = getUrlsFromFrame(resource.getStream());
        //report.addLog("- redirected URL(s):");
        for (String str: redirectUrls)
        {
            if (! urls.contains(str))
            {
                urls.add(str);
                
                String redirectedUrl = urlCompletion(str, resource.getUrl());
                //report.addLog("\t- " + redirectedUrl);

                ResourceLog redirection = new ResourceLog(redirectedUrl, resource.getKeyword());   // TODO: this should take into account whether the expected returned data is text based or binary
                resource.addRedirection(redirection);
                
                if (ResourceDao.STATE_SUCCESS == checkUrl(redirection, agent))
                {
                    redirection.setSuccess(true);
                    redirection.setState(ResourceDao.STATE_SUCCESS);
                }
                else   // other redirections perhaps?
                {
                    if (redirection.isResponsive())
                    {
                        if (checkFrameUsed(redirection.getStream()))
                        {
                            searchFrameRedirect(redirection, urls, agent);
                        }
                    }
                }
            }
        }
    }
    
    
    /**
     * Checks if one of the redirected resource is a success, therefore parent resource is a success as well (and is set as a success).
     * @param resource
     */
    private void checkFrameRedirectState(ResourceLog resource)
    {
        if (isResourceSuccessful(resource))
        {
            resource.setSuccess(true);
            resource.setState(ResourceDao.STATE_SUCCESS);
        }
    }
    
    
    /**
     * Checks if a resource or any of its descendants is successful (server responsive and keyword found).
     * @param resource
     * @return
     */
    private boolean isResourceSuccessful(ResourceLog resource)
    {
        boolean result = false;
        
        if (resource.isSuccess())
        {
            result = true;
        }
        else   // checks the redirected resources (first level of descendants)
        {
            for (ResourceLog redirected: resource.getRedirections())
            {
                if (isResourceSuccessful(redirected))
                {
                    result = true;
                }
            }
        }
        
        return result;
    }
	
	
	/**
     * Converts an <code>InputStream</code> into a <code>String</code>.
     * @param stream <code>InputStream</code> to convert
     * @return converted <code>String</code>
     */
	private String convertStreamToString(InputStream stream, ResourceLog report)
	{
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder result = new StringBuilder();
        String line = null;
        
        try
        {
            while (null != (line = reader.readLine()))
            {
                result.append(line + "\n");
            }
        }
        catch (IOException e)
        {
            report.addError("- IOException raised while converting an InputStream into a String!");
            report.addError("  Stream: " + stream);
            report.addError("  Message: " + e.getMessage());
        }
        
        // stream not closed: it could be needed afterwards!
        
        return result.toString();
	}
	
	
	/**
	 * Retrieves URLs from meta refresh redirection.
	 * Looks for:
	 *   <meta http-equiv="refresh" content='0;url=http://www.ebi.ac.uk/'>
	 * @param text
	 * @return
	 */
	private List<String> getUrlsFromMetaRedirect(String text)
	{
		List<String> urls = new ArrayList<String>();
		Pattern patternBegin = Pattern.compile("(?i)content\\s*=\\s*'\\d\\s*;\\s*url\\s*=\\s*");
		Pattern patternEnd = Pattern.compile("(?i)[\"\']\\s*/?>");
		
		List<String> rawUrls = getContentFromRegExp(text, META_REDIRECT_REGEXP);
		for (String rawUrl: rawUrls)
		{
	        Matcher matcherBegin = patternBegin.matcher(rawUrl);
	        Matcher matcherEnd = patternEnd.matcher(rawUrl);
	        
	        if ((matcherBegin.find()) && (matcherEnd.find()))
            {
    			int begin = matcherBegin.end();
    			int end = matcherEnd.start();
    			if ((begin >= 0) && (end >= 0))
    			{
    				urls.add((rawUrl.substring(begin, end)).trim());
    			}
            }
		}
		
		if (! urls.isEmpty())
		{
			return urls;
		}
		else
		{
			return null;
		}
	}
	
	
	/**
	 * Retrieves URLs from a piece of HTML containing javascript redirections.
	 * Looks for:
	 *   <script language="javascript" type="text/javascript">window.location='publ-model.do?mid=BIOMD0000000003';</script>
	 * @param text
	 * @return
	 */
	private List<String> getUrlsFromScriptRedirect1(String text)
	{
		List<String> urls = new ArrayList<String>();
		Pattern patternBegin = Pattern.compile("(?i)<script\\s+[^>]*>\\s*window.location(.href)?\\s*=\\s*[\"\']");
		Pattern patternEnd = Pattern.compile("(?i)[\"\']\\s*;?\\s*</script>");
		
		List<String> rawUrls = getContentFromRegExp(text, SCRIPT_REDIRECT_REGEXP_1);
		for (String rawUrl: rawUrls)
		{
	        Matcher matcherBegin = patternBegin.matcher(rawUrl);
	        Matcher matcherEnd = patternEnd.matcher(rawUrl);
	        
	        if ((matcherBegin.find()) && (matcherEnd.find()))
	        {
    			int begin = matcherBegin.end();
    			int end = matcherEnd.start();
    			if ((begin >= 0) && (end >= 0))
    			{
    				urls.add((rawUrl.substring(begin, end)).trim());
    			}
	        }
		}
		
		if (! urls.isEmpty())
		{
			return urls;
		}
		else
		{
			return null;
		}
	}
	
	
	/**
	 * Retrieves URLs from a piece of HTML containing javascript redirections.
	 * Looks for:
	 *   <script TYPE="text/javascript">window.location.replace('/ECOLI/NEW-IMAGE?object=CYT-D-UBIOX-CPLX');</script>
	 * @param text
	 * @return
	 */
	private List<String> getUrlsFromScriptRedirect2(String text)
	{
		List<String> urls = new ArrayList<String>();
		Pattern patternBegin = Pattern.compile("(?i)<script\\s+[^>]*>\\s*window.location.replace\\(\\s*[\"\']");
		Pattern patternEnd = Pattern.compile("(?i)[\"\']\\s*\\)\\s*;?\\s*</script>");
		
		List<String> rawUrls = getContentFromRegExp(text, SCRIPT_REDIRECT_REGEXP_2);
		for (String rawUrl: rawUrls)
		{
	        Matcher matcherBegin = patternBegin.matcher(rawUrl);
	        Matcher matcherEnd = patternEnd.matcher(rawUrl);
	        
	        if ((matcherBegin.find()) && (matcherEnd.find()))
	        {
    			int begin = matcherBegin.end();
    			int end = matcherEnd.start();
    			if ((begin >= 0) && (end >= 0))
    			{
    				urls.add((rawUrl.substring(begin, end)).trim());
    			}
	        }
		}
		
		if (! urls.isEmpty())
		{
			return urls;
		}
		else
		{
			return null;
		}
	}
	
	
	/**
	 * Retrieves URLs from a piece of HTML loading external (i)frames.
	 * Looks for:
	 *   <frame name="left" src="family.php?tc=5.A#5.A.1" scrolling=yes>
	 *   <iframe src="/inc/head.html" name="head" id="head" marginwidth="0" marginheight="0" width="100%"></iframe>
	 * @param text
	 * @return
	 */
	private List<String> getUrlsFromFrame(String html)
	{
		List<String> urls = new ArrayList<String>();
		
		// parses the HTML document with jsoup
		Document doc = Jsoup.parse(html);
		
		Elements elts = doc.select("frame");
		Iterator<Element> iter = elts.iterator();
		while (iter.hasNext())
		{
			Element elt = iter.next();
			String src = elt.attr("src");
			// remove any (useless) newlines
			src = src.replaceAll("\n", "");
			urls.add(src);
		}
		
		
		/* OLD CODE
		
		Pattern pattDelimiter = Pattern.compile("(?i)src\\s*=\\s*[\"]");
		// ' and " considered as delimiters
		Pattern patternBeginA = Pattern.compile("(?i)\\s+src\\s*=\\s*[\"\']\\s*");
		Pattern patternEndA = Pattern.compile("\\s*[\"\']");
		// only " considered as delimiter
		Pattern patternBeginB = Pattern.compile("(?i)\\s+src\\s*=\\s*[\"]\\s*");
		Pattern patternEndB = Pattern.compile("\\s*[\"]");
		
		List<String> rawUrls = getContentFromRegExp(text, FRAME_LOADING_REGEXP);
		for (String rawUrl: rawUrls)
		{
			System.out.println("raw URL: " + rawUrl);
			
			// checks whether ' or " are used as delimiters
			Matcher matcherDelimiter = pattDelimiter.matcher(rawUrl);
			Matcher matcherBegin;
			Matcher matcherEnd;
			if (matcherDelimiter.find())   // the URL uses " as delimiter, so we can include ' in the acceptable characters
			{
				System.out.println("includes ' within URL");
				
				matcherBegin = patternBeginB.matcher(rawUrl);
				matcherEnd = patternEndB.matcher(rawUrl);
			}
			else
			{
				System.out.println("does not include ' within URL");
				
				matcherBegin = patternBeginA.matcher(rawUrl);
				matcherEnd = patternEndA.matcher(rawUrl);
			}
			
			if (matcherBegin.find())
			{
    			int begin = matcherBegin.end();
    			matcherEnd.find(begin);   // search from the beginning of 'src='
    			int end = matcherEnd.start();
    			if ((begin >= 0) && (end >= 0))
    			{
    				urls.add(rawUrl.substring(begin, end));   // trim() useless
    			}
			}
		}
		*/
		
		if (! urls.isEmpty())
		{
			return urls;
		}
		else
		{
			return null;
		}
	}
	
	
	/**
	 * Completes a partial URL, with the help of a complete but different URL.
	 * @param str
	 * @param u
	 * @return
	 */
	private String urlCompletion(String partialUrl, String rootUrl)
	{
		String fullUrl;
		
		// relative URL
		if (! completeUrl(partialUrl))
		{
			int index = rootUrl.lastIndexOf("/");
			fullUrl = rootUrl.substring(0, index) + "/" + partialUrl;
		}
		else   // full URL
		{
			fullUrl = partialUrl;
		}
		
		return fullUrl;
	}
	
	
	/**
	 * Checks is a URL is full or partial.
	 * @param url
	 * @return
	 */
	private boolean completeUrl(String url)
	{
		boolean complete = false;
		
		url = url.trim();
		
		if ((url.startsWith("http://")) || (url.startsWith("www.")))
		{
			complete = true;
		}
		
		return complete;
	}
	
	
	/**
	 * Retrieves content matching a regular expression. 
	 * @param text
	 * @param regexp
	 * @return
	 */
	public List<String> getContentFromRegExp(String text, String regexp)
	{
		List<String> content = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(text);
		
		while (matcher.find())
        {
			content.add(matcher.group());
        }
		
		return content;
	}
	
	
	/**
	 * Checks if there is a redirection via javascript in a piece of HTML.
	 * Looks for something like: <script language="javascript" type="text/javascript">window.location='publ-model.do?mid=BIOMD0000000003';</script>
	 * @param text
	 * @return
	 */
	private boolean checkScriptRedirect1(String text)
	{
		return checkRegExp(text, SCRIPT_REDIRECT_REGEXP_1);
	}
	
	/**
	 * Checks if there is a redirection via javascript in a piece of HTML.
	 * Looks for something like: <script TYPE="text/javascript">window.location.replace('/ECOLI/NEW-IMAGE?object=CYT-D-UBIOX-CPLX');</script>
	 * @param text
	 * @return
	 */
	private boolean checkScriptRedirect2(String text)
	{
		return checkRegExp(text, SCRIPT_REDIRECT_REGEXP_2);
	}
	
	
	/**
	 * Checks if there is a redirection via meta refresh in a piece of HTML.
	 * Looks for something like: <meta http-equiv="refresh" content='0;URL=http://www.ebi.ac.uk/'>
	 * @param text
	 * @return
	 */
	private boolean checkMetaRedirect(String text)
	{
		return checkRegExp(text, META_REDIRECT_REGEXP);
	}
	
	
	/**
	 * Checks if frames are used within a piece of HTML.
	 * Looks for something like: <frame or <iframe
	 * @param text
	 * @return
	 */
	private boolean checkFrameUsed(String html)
	{
		//return checkRegExp(text, FRAME_LOADING_REGEXP);   // regular expression based frame detection is not powerful enough, specially if the HTML is not fully valid
		Boolean frameUsed = false;
		
		// parses the HTML document with jsoup
		Document doc = Jsoup.parse(html);
		
		Elements elts = doc.select("frame");
		Iterator<Element> iter = elts.iterator();
		if (iter.hasNext())
		{
			frameUsed = true;
		}
		
		return frameUsed;
	}
	
	
	/**
	 * Checks if a regular expression is found in a piece of text.
	 * @param text
	 * @param regexp
	 * @return
	 */
	private boolean checkRegExp(String text, String regexp)
	{
		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(text);
		
		return matcher.find();
	}
	
	
	/**
	 * Wait X second(s).
	 */
	private void holdOn(Integer seconds)
	{
        Thread.currentThread();
        try
        {
            Thread.sleep(seconds * 1000);
        }
        catch (InterruptedException e)
        {
            logger.debug("InterruptedException raised while sleeping " + seconds + "s: " + e.getMessage());
        }
	}
}
