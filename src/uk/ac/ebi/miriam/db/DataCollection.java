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


/*
 * TODO:
 * 
 * - create a Documentation object
 * 
 * - change DataType and DataTypeHibernate to add the support of the new fields of 'mir_resource' (info, institution, location and obsolete)
 * -
 * 
 * 
 * - add new attributes to this class
 * - add comments
 * -
 */


package uk.ac.ebi.miriam.db;


import uk.ac.ebi.miriam.web.MiriamUtilities;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;


/**
 * <p>
 * Object which stores all the information about a data collection (previously called "data type").
 * 
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2014  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20140312
 */
public class DataCollection
{
    private Logger logger = Logger.getLogger(DataCollection.class);
    
    /* stable identifier of the data type (something starting by 'MIR:000' and followed by 5 digits) */
    private String id = new String();
    /* official name of the data type */
    private String name = new String();
    /* name of the data type for HTML links (with "%20" instead of spaces) */
    private String nameURL = new String();
    /* synonyms of the name of the data type */
    private List<String> synonyms = new ArrayList<String>();
    /* official URL of the data type */
    private String URL = new String();
    /* official URN of the data type */
    private String URN = new String();
    /* deprecated URIs */
    private List<String> deprecatedURIs = new ArrayList<String>();
    private List<String> deprecatedURNs = new ArrayList<String>();   // for read only purposes
    private List<String> deprecatedURLs = new ArrayList<String>();   // for read only purposes
    /* definition of the data type */
    private String definition = new String();
    /* regular expression of the data type */
    private String regexp = new String();
    /* resources (= physical locations) */
    private List<Resource> resources = new ArrayList<Resource>();
    /* list of physical locations of pieces of documentation of the data type */
    private List<String> documentationURLs = new ArrayList<String>();
    /* list of identifiers of pieces of documentation of the data collection */
    private List<String> documentationIDs = new ArrayList<String>();
    /* type of the identifiers of pieces of documentation of the data collection (PubMed, DOI, ...) */
    private List<String> documentationIDsType = new ArrayList<String>();
    /* list of the physical locations of the pieces of documentation (in fact: 'documentationURLs' AND transformed 'documentationIDs') */
    private List<String> docHtmlURLs = new ArrayList<String>();
    /* date of creation of the data type (the Date and String versions are linked and are modified together) */
    private Date dateCreation = new Date(0);
    private String dateCreationStr = new String(); // for direct display in JSP following the good pattern
    /* date of last modification of the data type (the Date and String versions are linked and are modified together) */
    private Date dateModification = new Date(0);
    /* for direct display in JSP following the good pattern */
    private String dateModificationStr = new String();
    /* if the data type is obsolete or not */
    private boolean obsolete;
    /* why the data type is obsolete */
    private String obsoleteComment = new String();
    /* if the data type is obsolete, this field must have a value */
    private String replacedBy = new String();
    /* whether or not some restriction exist on the access and usage of the data set */
    private List<Restriction> restrictions;   // the kind of limitations, if any, null otherwise
    private List<Tag> tags;   // list of tags/categories
    //uri schemes
    private HashSet<URI> uris = new HashSet<URI>();

    private List<URI> orderedUris;

    private List<MimeType> mimeTypeList = new ArrayList<MimeType>();
    /**
     * Default constructor
     */
    public DataCollection()
    {
        // nothing here, for the moment.
    }
    
    
    /**
     * Destroys the object (free the memory)
     */
    public void destroy()
    {
        this.id = "";
        this.name = "";
        this.nameURL = "";
        (this.synonyms).clear();
        this.URL = "";
        this.URN = "";
        (this.deprecatedURIs).clear();
        (this.deprecatedURNs).clear();
        (this.deprecatedURLs).clear();
        this.definition = "";
        this.regexp = "";
        (this.resources).clear();
        (this.documentationURLs).clear();
        (this.documentationIDs).clear();
        (this.docHtmlURLs).clear();
        this.dateCreation = new Date(0);
        this.dateCreationStr = "";
        this.dateModification = new Date(0);
        this.dateModificationStr = "";
        this.obsolete = false;
        this.replacedBy = "";
        if (null != this.tags)
        {
        	(this.tags).clear();
        }
        this.uris.clear();
    }
    
    
    /**
     * Overrides the 'toString()' method for the 'DataType' object
     * @return a string which contains all the information about the data type
     */
    public String toString()
    {
        StringBuilder tmp = new StringBuilder();
        
        tmp.append("\n");
        if (this.isObsolete())
        {
            tmp.append("WARNING: this data collection is obsolete and replaced by: ");
            tmp.append(this.replacedBy);
        }
        tmp.append("+ Registry id:        " + getId() + "\n");
        tmp.append("+ Name:               " + getName() + "\n");
        if (null != getSynonyms())
        {
            tmp.append("+ Alt names:      " + getSynonyms().toString() + "\n");
        }
        tmp.append("+ Description:        " + getDefinition() + "\n");
        tmp.append("+ Regular Expression: " + getRegexp() + "\n");
        tmp.append("+ Official URL:       " + getURL() + "\n");
        tmp.append("+ Root URN:         " + getURN() + "\n");
        if (null != getUris())
        {
            tmp.append("+ URI Scheme (s): \n");
            int i = 0;
            for (URI uri : getUris()) {
                tmp.append("    * URI Scheme #" + (i++) + ":\n");
                tmp.append(uri.toString() + "\n");
            }
        }
        if (null != getResources())
        {
            tmp.append("+ Resource(s):\n");
            for (int i = 0; i < getResources().size(); ++i)
            {
                tmp.append("    * Resource #" + i + ":\n");
                tmp.append(getResource(i).toString() + "\n");
            }
        }
        if (null != getDocumentationIDs())
        {
            tmp.append("+ Documentation ID(s):\n");
            for (int i = 0; i < getDocumentationIDs().size(); ++i)
            {
                tmp.append("       - " + getDocumentationID(i) + "\n");
            }
        }
        if (null != getDocumentationURLs())
        {
            tmp.append("+ Documentation URL(s):\n");
            for (int i = 0; i < getDocumentationURLs().size(); ++i)
            {
                tmp.append("       - " + getDocumentationURL(i) + "\n");
            }
        }
        if ((null != getTags()) && (getTags().size() > 0))
        {
        	tmp.append("+ Tag(s):\n");
        	for (Tag tag: getTags())
        	{
        		tmp.append("       - ");
        		tmp.append(tag.getName());
        		tmp.append(" (");
        		tmp.append(tag.getId());
        		tmp.append(")");
        		tmp.append("\n         ");
        		tmp.append(tag.getInfo());
        		tmp.append("\n");
        	}
        }
        
        return tmp.toString();
    }
    
    
    /**
     * Searches the type of the URI in parameter (URL or URN?)
     * <p> WARNING: doesn't check if the parameter is a valid URI!
     * @param uri a Uniform Request Identifier (can be a URL or a URN)
     * @return a boolean with the answer to the question above
     */
    public String getURIType(String uri)
    {
        // "urn:" not found in the URI
        if ((uri.indexOf("urn:")) == -1)
        {
            return "URL";
        }
        else
        {
            return "URN";
        }
    }
    
    
    /**
     * Returns the answer to the question: is this URI a URL?
     * @param uri a Uniform Request Identifier
     * @return a boolean with the answer to the question above
     */
    public boolean isURL(String uri)
    {
        if (getURIType(uri).equalsIgnoreCase("URL"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * Returns the answer to the question: is this URI a URN?
     * @param uri a Uniform Request Identifier
     * @return a boolean with the answer to the question above
     */
    public boolean isURN(String uri)
    {
        if (getURIType(uri).equalsIgnoreCase("URN"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * Returns the answer to the question: is the deprecated URI, identified by the index, a URN?s
     * @param i index of a deprecated URI
     * @return a boolean with the answer to the question above
     */
    public boolean isDeprecatedURN(int i)
    {
        if (getURIType(getDeprecatedURI(i)).equalsIgnoreCase("URN"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * Returns the answer to the question: is the deprecated URI, identified by the index, a URL?
     * @param i index of a deprecated URI
     * @return a boolean with the answer to the question above
     */
    public boolean isDeprecatedURL(int i)
    {
        if (getURIType(getDeprecatedURI(i)).equalsIgnoreCase("URL"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * Getter of the definition of the data type
     * @return definition of the data type
     */
    public String getDefinition()
    {
        return this.definition;
    }
    
    
    /**
     * Setter of the definition of the data type
     * @param definition definition of the data type
     */
    public void setDefinition(String definition)
    {
        if (null != definition)
        {
            this.definition = definition.trim();
        }
        else
        {
            this.definition = null;
        }
    }
    
    
    /*
     * Returns all the deprecated URIs of the data collection
     * @return all the deprecated URIs of the data collection
     */
    public List<String> getDeprecatedURIs()
    {
        return this.deprecatedURIs;
    }
    
    
    /**
     * Returns all the deprecated URLs of the data collection (read only!).
     * @return all the deprecated URLs of the data collection
     */
    public List<String> getDeprecatedURLs()
    {
        return this.deprecatedURLs;
    }
    
    /**
     * Returns all the deprecated URNs of the data collection (read only!).
     * @return all the deprecated URNs of the data collection
     */
    public List<String> getDeprecatedURNs()
    {
        return this.deprecatedURNs;
    }
    
    
    /**
     * Returns one precise deprecated URI of the data type
     * @param i index of the deprecated URI
     * @return one precise deprecated URI of the data type
     */
    public String getDeprecatedURI(int i)
    {
        if ((i >= 0) && (i < this.deprecatedURIs.size()))
        {
            return (String) this.deprecatedURIs.get(i);
        }
        else
        {
            return null;
        }
    }
    
    
    /**
     * Setter of the deprecated forms of the URI (URN or URL) of the data collection
     * @param deprecatedURI list of all the deprecated URIs
     */
    public void setDeprecatedURIs(List<String> deprecatedURIs)
    {
        // reset of the list, in case already existing
        this.deprecatedURIs = new ArrayList<String>();

        for (int i = 0; i < deprecatedURIs.size(); ++i)
        {
            // we don't add null elements
            if (null != deprecatedURIs.get(i))
            {
                this.deprecatedURIs.add(((String) deprecatedURIs.get(i)).trim());
            }
        }
    }
    
    /**
     * Adds a new deprecated URI for the data collection
     * @param deprecatedURI
     */
    public void addDeprecatedURI(String deprecatedURI)
    {
    	if (null == this.deprecatedURIs)
    	{
    		this.deprecatedURIs = new ArrayList<String>();
    	}
    	this.deprecatedURIs.add(deprecatedURI);
    }
    
    /**
     * Setter of the deprecated forms of the URNs of the data catalogue
     * @param deprecatedURI list of all the deprecated URNs
     */
    public void setDeprecatedURNs(List<String> deprecatedURNs)
    {
        // reset of the list, in case already existing
        this.deprecatedURNs = new ArrayList<String>();

        for (int i = 0; i < deprecatedURNs.size(); ++i)
        {
            // we don't add null elements
            if (null != deprecatedURNs.get(i))
            {
                this.deprecatedURNs.add(((String) deprecatedURNs.get(i)).trim());
            }
        }
    }
    
    /**
     * Adds a new deprecated URN for the data collection
     * @param deprecatedURN
     */
    public void addDeprecatedURN(String deprecatedURN)
    {
    	if (null == this.deprecatedURNs)
    	{
    		this.deprecatedURNs = new ArrayList<String>();
    	}
    	this.deprecatedURNs.add(deprecatedURN);
    }
    
    /**
     * Setter of the deprecated forms of the URLs of the data catalogue
     * @param deprecatedURI list of all the deprecated URLs
     */
    public void setDeprecatedURLs(List<String> deprecatedURLs)
    {
        // reset of the list, in case already existing
        this.deprecatedURLs = new ArrayList<String>();

        for (int i = 0; i < deprecatedURLs.size(); ++i)
        {
            // we don't add null elements
            if (null != deprecatedURLs.get(i))
            {
                this.deprecatedURLs.add(((String) deprecatedURLs.get(i)).trim());
            }
        }
    }
    
    
    /**
     * Adds a new deprecated URL for the data collection
     * @param deprecatedURL
     */
    public void addDeprecatedURL(String deprecatedURL)
    {
    	if (null == this.deprecatedURLs)
    	{
    		this.deprecatedURLs = new ArrayList<String>();
    	}
    	this.deprecatedURLs.add(deprecatedURL);
    }
    
    
    /**
     * Getter of the stable ID (in the database) of the data type
     * @return the internal ID of the data type
     */
    public String getId()
    {
        return this.id;
    }
    
    
    /**
     * Setter of the internal ID (in the database) of the data type
     * @param internalId internal ID of the data type
     */
    public void setId(String id)
    {
        if (null != id)
        {
            this.id = id.trim();
        }
        else
        {
            this.id = null;
        }
    }
    
    
    /**
     * Getter of the official name (not a synonym) of the data type
     * @return name of the data type
     */
    public String getName()
    {
        return this.name;
    }
    
    
    /**
     * Setter of the official name of the data type
     * @param name name of the data type
     */
    public void setName(String name)
    {
        if (null != name)
        {
            this.name = name.trim();
        }
        else
        {
            this.name = null;
        }
    }
    
    
    /**
     * Getter of the HTML name (without any space)
     * @return
     */
    public String getNameURL()
    {
        return this.nameURL;
    }
    
    
    /**
     * Setter of the HTML name (without any space)
     * @param nameURL
     */
    public void setNameURL(String nameURL)
    {
        if (null != nameURL)
        {
            this.nameURL = nameURL.trim();
        }
        else
        {
            this.nameURL = null;
        }
    }
    
    
    /**
     * Getter of the regular expression of the data type
     * @return regular expression of the data type
     */
    public String getRegexp()
    {
        return this.regexp;
    }
    
    
    /**
     * Setter of the regular expression of the data type
     * @param regexp regular expression of the data type
     */
    public void setRegexp(String regexp)
    {
        if (null != regexp)
        {
            this.regexp = regexp.trim();
        }
        else
        {
            this.regexp = null;
        }
    }
    
    
    /**
     * Getter of the synonyms of the name of the data type
     * @return list of all the synonyms of the name of the data type
     */
    public List<String> getSynonyms()
    {
        return this.synonyms;
    }
    
    
    /**
     * Getter of one of the synonyms of the name of the data type
     * @param i index of the synonym
     * @return one precise synonym of the name of the data type
     */
    public String getSynonym(int i)
    {
        if ((i >= 0) && (i < this.synonyms.size()))
        {
            return (String) this.synonyms.get(i);
        }
        else
        {
            return null;
        }
    }
    
    
    /**
     * Setter of the synonyms of the data type
     * @param synonyms list of all the synonyms of the data type
     */
    public void setSynonyms(List<String> synonyms)
    {
        // reset of the list, in case already existing
        this.synonyms = new ArrayList<String>();

        // we only add non null elements
        for (int i = 0; i < synonyms.size(); ++i)
        {
            if (null != synonyms.get(i))
            {
                this.synonyms.add(((String) synonyms.get(i)).trim());
            }
        }
    }
    
    
    /**
     * Getter of the official URL of the data type
     * @return URL of the data type
     */
    public String getURL()
    {
        return this.URL;
    }
    
    
    /**
     * Setter of the official URL of the data type
     * @param url URL of the data type
     */
    public void setURL(String url)
    {
        if (null != url)
        {
            this.URL = url.trim();
        }
        else
        {
            this.URL = null;
        }
    }
    
    
    /**
     * Getter of the official URN of the data type
     * @return URN of the data type
     */
    public String getURN()
    {
        return this.URN;
    }
    
    
    /**
     * Setter of the official URN of the data type
     * @param urn URN of the data type
     */
    public void setURN(String urn)
    {
        if (null != urn)
        {
            this.URN = urn.trim();
        }
        else
        {
            this.URN = null;
        }
    }
    
    
    /**
     * Getter of the resources (physical locations) of the data type
     * @return the resources of the data type
     */
    public List<Resource> getResources()
    {
        return this.resources;
    }
    
    
    /**
     * Getter of a specific resource (physical location) of the data type
     * @return a precise resource of the data type
     */
    public Resource getResource(int index)
    {
        if ((index >= 0) && (index < this.resources.size()))
        {
            return (Resource) this.resources.get(index);
        }
        else
        {
            return null;
        }
    }
    
    
    /**
     * Retrieves all the non deprecated resources.
     * @return list of non deprecated resources, ordered by their reliability
     */
    public List<Resource> getCurrentResources()
    {
        List<Resource> current = new ArrayList<Resource>();
        
        // retrieves only the non deprecated resources
        for (Resource res: getResources())
        {
            if (! res.isObsolete())
            {
                current.add(res);
            }
        }
        
        // orders the resources by uptime percentage
        ReliabilityCompare compare = new ReliabilityCompare();
        Collections.sort(current, compare);   // ordering by identifier
        Collections.reverse(current);   // order: reliability from high to low
        
        return current;
    }
    
    
    /**
     * Retrieves all the deprecated resources.
     * @return list of deprecated resources (no order by their reliability)
     */
    public List<Resource> getDeprecatedResources()
    {
        List<Resource> deprecated = new ArrayList<Resource>();
        
        // retrieves only the deprecated resources
        for (Resource res : getResources())
        {
            if (res.isObsolete())
            {
                deprecated.add(res);
            }
        }
        
        // orders the resources by uptime percentage
        // ReliabilityCompare compare = new ReliabilityCompare();
        // Collections.sort(deprecated, compare); // ordering by identifier
        // Collections.reverse(deprecated); // order: reliability from high to low
        
        return deprecated;
    }
    
    
    /**
     * Comparator class to compare two <code>Resource</code> using their reliability (instead of, by default, their identifier).
     */
    class ReliabilityCompare implements Comparator<Resource>
    {
        public int compare(Resource res1, Resource res2)
        {
            return res1.getReliability().compareTo(res2.getReliability());
        }
    }
    
    
    /**
     * Setter of the resources (physical locations) of the data type
     * @param locations list of the resources of the data type
     */
    public void setResources(List<Resource> resources)
    {
        this.resources = resources;
    }
    
    
    /**
     * Adds another resource to the data type
     * @param res the new resource to add to the data type
     */
    public void addResource(Resource res)
    {
        if (null != res)
        {
            this.resources.add(res);
        }
    }
    
    
    /**
     * Getter of the prefix of the physical location of all the data entries (one precise element)
     * @return the prefix of the physical location of all the data entries
     */
    public List<String> getDataEntriesPrefix()
    {
        ArrayList<String> result = new ArrayList<String>();
        
        for (int i=0; i <this.resources.size(); ++i)
        {
            result.add(((Resource) this.resources.get(i)).getUrl_prefix());
        }
        
        return result;
    }
    
    
    /**
     * Getter of the prefix of the physical location of one data entry (one precise element)
     * <p>WARNING: no check of the validity of the parameter ('out of range' possible...)
     * @param index index of the resource
     * @return the prefix of the physical location of one precise the data entry
     */
    public String getDataEntryPrefix(int index)
    {
        return (String) (((Resource) this.resources.get(index)).getUrl_prefix());
    }
    
    
    /**
     * Getter of the suffix of the physical location of all the data entries (one precise element)
     * @return the suffix of the physical location of all the data entries
     */
    public List<String> getDataEntriesSuffix()
    {
        ArrayList<String> result = new ArrayList<String>();
        
        for (int i=0; i<this.resources.size(); ++i)
        {
            result.add(((Resource) this.resources.get(i)).getUrl_suffix());
        }
        
        return result;
    }
    
    
    /**
     * Getter of the suffix of the physical location of one data entry (one precise element)
     * @param index index of the resource
     * @return the suffix of the physical location of one precise the data entry
     */
    public String getDataEntrySuffix(int index)
    {
        return (String) (((Resource) this.resources.get(index)).getUrl_suffix());
    }
    
    
    /**
     * Getter of the physical locations of all the resources (information page)
     * @return the physical locations of all the resources
     */
    public List<String> getDataResources()
    {
        ArrayList<String> result = new ArrayList<String>();
        
        for (int i=0; i<this.resources.size(); ++i)
        {
            result.add(((Resource) this.resources.get(i)).getUrl_root());
        }
        
        return result;
    }
    
    
    /**
     * Getter of the physical location of one precise resource (information page)
     * @param index index of the resource
     * @return the physical location of one precise resource
     */
    public String getDataResource(int index)
    {
        if ((index >= 0) && (index < this.resources.size()))
        {
            return (String) (((Resource) this.resources.get(index)).getUrl_root());
        }
        else
        {
            return null;
        }
    }
    
    
    /**
     * Getter of the physical locations (URLs) of all the pieces of documentation of the data type
     * @return physical locations of all the pieces of documentation of the data type
     */
    public List<String> getDocumentationURLs()
    {
        return this.documentationURLs;
    }
    
    
    /**
     * Getter of the physical location (URL) of one piece of documentation
     * @param index index of one documentation
     * @return physical location of one piece of documentation of the data type
     */
    public String getDocumentationURL(int index)
    {
        if ((index >= 0) && (index < this.documentationURLs.size()))
        {
            return (String) this.documentationURLs.get(index);
        }
        else
        {
            return null;
        }
    }
    
    
    /**
     * Setter of physical locations (URLs) of pieces of documentation of the data collection
     * @param docs_url list physical locations (URLs)
     */
    public void setDocumentationURLs(List<String> docs_url)
    {
        // reset of the list in case it is not empty
        this.documentationURLs = new ArrayList<String>();

        // we only add non null elements
        for (int i=0; i<docs_url.size(); ++i)
        {
            if (null != docs_url.get(i))
            {
                this.documentationURLs.add(((String) docs_url.get(i)).trim());
            }
        }
    }

    /**
     * Getter of the identifier of all the pieces of documentation of the data type
     * @return identifiers of all the pieces of documentation of the data type
     */
    public List<String> getDocumentationIDs()
    {
        return this.documentationIDs;
    }
    
    
    /**
     * Getter of the identifier of one piece of documentation of the data type
     * @param index index of one documentation
     * @return identifier of one piece of documentation of the data type
     */
    public String getDocumentationID(int index)
    {
        if ((index >= 0) && (index < this.documentationIDs.size()))
        {
            return (String) this.documentationIDs.get(index);
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Removes one piece of documentation of the data collection
     * @param doc
     */
    public void removeDocumentationID(String doc)
    {
    	if (null != doc)
    	{
    		this.documentationIDs.remove(doc);
    	}
    }
    
    /**
     * Setter of URIs of pieces of documentation for the data collection.
     * @param docs_id list of URIs (that can be managed/handled by the Registry)
     */
    public void setDocumentationIDs(List<String> docs_id)
    {
    	 // reset of the list in case it is not empty
        this.documentationIDs = new ArrayList<String>();
    	
        // we only add non null elements
        for (int i=0; i<docs_id.size(); ++i)
        {
            if (null != docs_id.get(i))
            {
                this.documentationIDs.add(((String) docs_id.get(i)).trim());
            }
        }
    }
    
    /**
     * Getter of the type of the identifier of all the pieces of documentation of the data type
     * @return the documentationIDsType
     */
    public List<String> getDocumentationIDsType()
    {
        return this.documentationIDsType;
    }
    
    
    /**
     * Setter of the type of the identifier of all the pieces of documentation of the data type
     * @param documentationIDsType the documentationIDsType to set
     */
    public void setDocumentationIDsType(List<String> documentationIDsType)
    {
        // reset list, in case already existing
        this.documentationIDsType = new ArrayList<String>();
        
        for (String type: documentationIDsType)
        {
            addDocumentationIDType(type);
        }
    }
    
    
    /**
     * Adds a type for an identifier of one piece of documentation of the data type
     * @param type (PubMed, DOI, ...)
     */
    public void addDocumentationIDType(String type)
    {
        // we only add non null elements
        if (null != type)
        {
            this.documentationIDsType.add(type.trim());
        }
    }
    
    
    /**
     * Getter of the type of the identifier of one piece of documentation of the data type
     * @param type of a specific identifier
     * @return the documentationIDsType
     */
    public String getDocumentationIDType(int index)
    {
        if ((index >= 0) && (index < this.documentationIDsType.size()))
        {
            return (this.documentationIDsType).get(index);
        }
        else
        {
            return null;
        }
    }
    
    
    /**
     * Adds identifiers of pieces of documentation of a data type.
     * @param docs_id list of identifiers (that can be managed/handled by MIRIAM Resources)
     */
    public void addDocumentationIDs(List<String> docs_id)
    {
        for (int i=0; i<docs_id.size(); ++i)
        {
            // we only add non null elements
            if (null != docs_id.get(i))
            {
                this.documentationIDs.add(((String) docs_id.get(i)).trim());
            }
        }
    }
    
    
    /**
     * Adds the identifier of one piece of documentation of a data type.
     * @param docs_id identifier (that can be managed/handled by MIRIAM Resources)
     */
    public void addDocumentationID(String docs_id)
    {
        // we only add non null elements
        if (null != docs_id)
        {
            this.documentationIDs.add(docs_id.trim());
        }
    }
    
    
    /**
     * Setter of the identifier of one piece of documentation of the data type
     * @param index index of one documentation
     * @param id identifier of one documentation
     */
    public void setDocumentationID(int index, String id)
    {
        if (id != null)   // && ((index >= 0) && (index < this.documentationIDs.size())))
        {
            this.documentationIDs.set(index, id.trim());
        }
    }
    
    
    /**
     * Getter of the physical locations of ALL the pieces of documentation (even the documentations identified by an ID)
     * @return list of the physical locations of ALL the pieces of documentation
     */
    public List<String> getDocHtmlURLs()
    {
        return this.docHtmlURLs;
    }
    
    
    /**
     * Getter of one precise physical location of a piece of documentation
     * @param index index of the physical location wanted
     * @return one precise physical location of a piece of documentation
     */
    public String getDocHtmlURL(int index)
    {
        if ((index >= 0) && (index < this.docHtmlURLs.size()))
        {
            return (String) this.docHtmlURLs.get(index);
        }
        else
        {
            return null;
        }
    }
    
    
    /**
     * Setter of the physical locations of ALL the pieces of documentation (even the documentations identified by an ID)
     * @param docHtmlURLs list of the physical locations of ALL the pieces of documentation
     */
    public void setDocHtmlURLs(List<String> docHtmlURLs)
    {
        // reset the list, in case already existing
        this.docHtmlURLs = new ArrayList<String>();
        
        // we only add non null elements
        for (String url: docHtmlURLs)
        {
            if (null !=  url)
            {
                this.docHtmlURLs.add(url.trim());
            }
        }
    }
    
    
    /**
     * Getter of the date (Date) of creation of the data type
     * @return dateCreation date of creation of the data type
     */
    public Date getDateCreation()
    {
        return this.dateCreation;
    }
    
    
    /**
     * Setter of the date (Date) of creation of the data type
     * @param dateCreation date of creation of the data type
     */
    public void setDateCreation(Date dateCreation)
    {
        this.dateCreation = dateCreation;
        
        // modification of the String form of the creation date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'GMT'");
        this.dateCreationStr = dateFormat.format(this.dateCreation);
    }
    
    
    /**
     * Getter of the date (String) of creation of the data type
     * @return dateCreation date of creation of the data type
     */
    public String getDateCreationStr()
    {
        return this.dateCreationStr;
    }
    
    
    /**
     * Setter of the date (String) of creation of the data type
     * @param dateCreation date of creation of the data type
     */
    public void setDateCreationStr(String dateCreationStr)
    {
        this.dateCreationStr = dateCreationStr;
        
        // modification of the Date form of the creation date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            this.dateCreation = dateFormat.parse(dateCreationStr);
        }
        catch (Exception e)
        {
            logger.error("Date conversion error (" + dateCreationStr + ")" + e);
            this.dateCreation = new Date(0); // 1st January 1970
        }
    }
    
    
    /**
     * Getter of the date (Date) of last modification of the data type
     * @return date of last modification of the data type
     */
    public Date getDateModification()
    {
        return this.dateModification;
    }
    
    
    /**
     * Setter of the date (Date) of last modification of the data type
     * @param dateModif date of last modification of the data type
     */
    public void setDateModification(Date dateModification)
    {
        this.dateModification = dateModification;
        
        // modification of the String form of the last modification date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'GMT'");
        this.dateModificationStr = dateFormat.format(this.dateModification);
    }
    
    
    /**
     * Getter of the date (String) of last modification of the data type.
     * @return date of last modification of the data type
     */
    public String getDateModificationStr()
    {
        return this.dateModificationStr;
    }
    
    
    /**
     * Setter of the date (String) of last modification of the data type
     * @param dateModif date of last modification of the data type
     */
    public void setDateModificationStr(String dateModificationStr)
    {
        this.dateModificationStr = dateModificationStr;
        
        // modification of the Date form of the last modification date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            this.dateModification = dateFormat.parse(dateModificationStr);
        }
        catch (Exception e)
        {
            logger.error("Date conversion error (" + dateModificationStr + ")" + e);
            this.dateModification = new Date(0); // 1st January 1970
        }
    }
    
    
    /**
     * Check all the mandatory parameters of the data type, if something is missing or wrong, the method will return 'false'
     * @return a boolean saying if the data type is valid or not
     */
    public boolean isValid()
    {
        return !(MiriamUtilities.isEmpty(getName()) || 
                 MiriamUtilities.isEmpty(getDefinition()) || 
                 MiriamUtilities.isEmpty(getRegexp()) || 
                 (MiriamUtilities.isEmpty(getURL()) && MiriamUtilities.isEmpty(getURN())) || 
                 ((getDataEntriesPrefix().isEmpty()) || (getDataResources().isEmpty())));
    }
    
    
    /**
     * Checks if the data type has (at least) one resource is official (there is at least one resource and the resources are not all deprecated)
     * @return a boolean which says if the data type has (at least) one official resource
     */
    public boolean hasOfficialResource()
    {
        boolean result = false;
        
        for (int i = 0; i<(getResources()).size(); ++i)
        {
            // one resource (at least) is not obsolete
            if (! (getResource(i)).isObsolete())
            {
                result = true;
            }
        }
        
        return result;
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
     * Setter (from int)
     * @param obsolete the obsolete to set
     */
    public void setObsolete(int obsolete)
    {
        if (obsolete == 0)
        {
            this.obsolete = false;
        }
        else
        {
            this.obsolete = true;
        }
    }
    
    
    /**
     * Getter
     * @return the replacedBy
     */
    public String getReplacedBy()
    {
        return this.replacedBy;
    }
    
    
    /**
     * Setter
     * @param replacedBy the replacedBy to set
     */
    public void setReplacedBy(String replacedBy)
    {
        if (null != replacedBy)
        {
            this.replacedBy = replacedBy.trim();
        }
        else
        {
            this.replacedBy = null;
        }
    }
    
    
    /**
     * Getter
     * @return the comment
     */
    public String getObsoleteComment()
    {
        return this.obsoleteComment;
    }
    
    
    /**
     * Setter
     * @param comment the comment to set
     */
    public void setObsoleteComment(String obsoleteComment)
    {
        if (null != obsoleteComment)
        {
            this.obsoleteComment = obsoleteComment.trim();
        }
        else
        {
            this.obsoleteComment = null;
        }
    }
    
    
    /**
     * Retrieve the "namespace" of the data type (the part after the "urn:miriam:" in the official URN).
     * @return namespace of the data type
     */
    public String getNamespace()
    {
        return this.URN.substring(11);
    }
    
    
    /**
     * @return the list of restriction(s)
     */
    public List<Restriction> getRestrictions()
    {
        return this.restrictions;
    }
    
    
    /*
     * Returns a human description of the restriction on the usage of the data set.
     * @return
     *
    public String getRestrictionDescription()
    {
        String desc = null;
        
        switch(this.restriction)
        {
            case 0:
                desc = "There is no limitation on the usage of this data collection.";
                break;
            case 1:
                desc = "The license under which this data collection is made available restricts some of its potential usages.";
                break;
            case 2:
                desc = "This data collection is an aggregation of different kinds of data.";
                break;
            case 3:
                desc = "The way this data collection is distributed prevents linking to one specific entity.";
                break;
            default:
                desc = "An unknown restriction limits some potential usages of this data collection.";
            // X: any combination of categories -we need to list them-
        }
        
        return desc;
    }
    */
    
    /**
     * Adds a new restriction to the data collection.
     * @param restriction the restriction to set
     */
    public void addRestriction(Restriction restriction)
    {
        if (null == this.restrictions)
        {
            this.restrictions = new ArrayList<Restriction>();
        }
        this.restrictions.add(restriction);
    }
    
    /**
     * Setter
     * @param restrictions
     */
    public void setRestrictions(List<Restriction> restrictions)
    {
        this.restrictions = restrictions;
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
	 * Adds a new tag to the data collection.
	 * @param tag
	 */
	public void addTag(Tag tag)
	{
		if (null == this.tags)
		{
			this.tags = new ArrayList<Tag>();
		}
		this.tags.add(tag);
	}
	
	/**
	 * Setter
	 * @param tags the tags to set
	 */
	public void setTags(List<Tag> tags)
	{
		this.tags = tags;
	}

    public HashSet<URI> getUris() {
        return uris;
    }

    public void setUris(HashSet<URI> uris) {
        this.uris = uris;
    }

    public ArrayList getOrderedUris(){
        ArrayList<URI> uris = new ArrayList<URI>(this.uris);
        Collections.sort(uris);
        return uris;
    }

    public void addMimeType(MimeType mimeType){
        mimeTypeList.add(mimeType);
    }

    public List<MimeType> getMimeTypeList() {
        return mimeTypeList;
    }

    public MimeType getMimeType(int id){
        for(MimeType mimeType: mimeTypeList){
            if (id == mimeType.getId()){
                return mimeType;
            }
        }
        return null;
    }


}
