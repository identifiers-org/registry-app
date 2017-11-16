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


package uk.ac.ebi.miriam.web;


import uk.ac.ebi.miriam.db.DbPoolConnect;
import uk.ac.ebi.miriam.web.MiriamUtilities;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.apache.log4j.Logger;


/**
 * <p>
 * Custom tag handler for browsing the documentations (the ones stored with a URI, not a physical address)
 * 
 * @TODO: externalise the preferred resource for a specific data type.
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
 * @version 20080417
 */
public class TagHandlerDocBrowse extends SimpleTagSupport
{
    private Logger logger = Logger.getLogger(TagHandlerDocBrowse.class);
    private List<String> data;   /* list of URIs */
    private String pool;   /* name of the database connection pool */


    /**
     * Setter of 'data'
     * @param ArrayList list of all the MIRIAM URIs of the documentations
     */
    public void setData(List<String> data)
    {
        this.data = data;
    }

    /**
     * Setter of 'pool'
     * @param String name of the database connection pool
     */
    public void setPool(String pool)
    {
        this.pool = pool;
    }

    /**
     * this method contains all the business part of a tag handler
     */
    public void doTag() throws JspException, IOException
    {
        logger.debug("tag handler for data type documentations (from URIs)");
        JspContext context = getJspContext();
        DbPoolConnect dbPool;
        ResultSet sqlResult;
        // primary resources (for DOI, PubMed, ...)
        Map<String, String> prefixPrimary = new HashMap<String, String>();
        Map<String, String> suffixPrimary = new HashMap<String, String>();
        Map<String, String> infoPrimary = new HashMap<String, String>();
        Map<String, String> institutionPrimary = new HashMap<String, String>();
        Map<String, String> locationPrimary = new HashMap<String, String>();
        // secondary resources (for DOI, PubMed, ...)
        Map<String, List<String>> prefixList = new HashMap<String, List<String>>();
        Map<String, List<String>> suffixList = new HashMap<String, List<String>>();
        Map<String, List<String>> infoList = new HashMap<String, List<String>>();
        Map<String, List<String>> institutionList = new HashMap<String, List<String>>();
        Map<String, List<String>> locationList = new HashMap<String, List<String>>();
        // temporary list, to store the URLs generated
        List<String> urlList = new ArrayList<String>();

        // connection pool management
        dbPool = new DbPoolConnect(pool);

        // test without 'newConnection()' before, let's see...
        dbPool.getConnection();

        // list of data types which can be used to reference a piece of documentation
        // the data associated is: identifier of the data type and identifier of the preferred resource
        Map<String, List<String>> listOfReferences = new HashMap<String,List<String>>();
        listOfReferences.put("PubMed", Arrays.asList("MIR:00000015", "MIR:00100028"));   // PubMed, Medline at the EBI
        listOfReferences.put("DOI", Arrays.asList("MIR:00000019", "MIR:00100010"));   // DOI, International DOI Foundation
        
        // list of MIRIAM URIs of data types which can be used to reference a piece of documentation (we don't consider the obsolete URIs!)
        Map<String, String> listOfReferenceUris = new HashMap<String, String>();
        listOfReferenceUris.put("urn:miriam:pubmed", "PubMed");
        listOfReferenceUris.put("urn:miriam:doi", "DOI");
        
        // temporary lists to store the different elements about a resource (the behaviour when updating a Map is pretty weird...)
        List<String> tmpPrefixList = new ArrayList<String>();
        List<String> tmpSuffixList = new ArrayList<String>();
        List<String> tmpInfoList = new ArrayList<String>();
        List<String> tmpInstitutionList = new ArrayList<String>();
        List<String> tmpLocationList = new ArrayList<String>();
        
        // retrieves all the resources of all the data types which can be used to reference a piece of documentation
        for (String docProvider: listOfReferences.keySet())
        {
            // cleans the temporary variables
            tmpPrefixList.clear();
            tmpSuffixList.clear();
            tmpInfoList.clear();
            tmpInstitutionList.clear();
            tmpLocationList.clear();
            
            String query = "SELECT resource_id, url_element_prefix, url_element_suffix, info, institution, location FROM mir_resource WHERE (ptr_datatype = '" + listOfReferences.get(docProvider).get(0) + "')";   // the first (0) element of the list is the Identifier of the data type  
            sqlResult = dbPool.request(dbPool.getStatement(), query);
            try
            {
                boolean notEmpty = sqlResult.next();
                while (notEmpty)
                {
                    // this is the preferred resource for documentations referenced with a the current data type
                    if ((sqlResult.getString("resource_id")).compareToIgnoreCase(listOfReferences.get(docProvider).get(1)) == 0)   // the second (1) element of the list is the Identifier of the preferred resource 
                    {
                        prefixPrimary.put(docProvider, MiriamUtilities.urlConvert(sqlResult.getString("url_element_prefix")));
                        suffixPrimary.put(docProvider, MiriamUtilities.urlConvert(sqlResult.getString("url_element_suffix")));
                        infoPrimary.put(docProvider, sqlResult.getString("info"));
                        institutionPrimary.put(docProvider, sqlResult.getString("institution"));
                        locationPrimary.put(docProvider, sqlResult.getString("location"));
                    }
                    else   // secondary resource
                    {
                        // a secondary resource has already been assigned to the data type (a data type can have more than 2 resources!)
                        if (prefixList.containsKey(docProvider))
                        {
                            tmpPrefixList.add(MiriamUtilities.urlConvert(sqlResult.getString("url_element_prefix")));   // DEBUG
                            tmpSuffixList.add(MiriamUtilities.urlConvert(sqlResult.getString("url_element_suffix")));
                            tmpInfoList.add(sqlResult.getString("info"));
                            tmpInstitutionList.add(sqlResult.getString("institution"));
                            tmpLocationList.add(sqlResult.getString("location"));
                        }
                        else   // a secondary resource has not already been assigned to the data type
                        {
                            tmpPrefixList.add(MiriamUtilities.urlConvert(sqlResult.getString("url_element_prefix")));   // DEBUG
                            tmpSuffixList.add(MiriamUtilities.urlConvert(sqlResult.getString("url_element_suffix")));
                            tmpInfoList.add(sqlResult.getString("info"));
                            tmpInstitutionList.add(sqlResult.getString("institution"));
                            tmpLocationList.add(sqlResult.getString("location"));
                        }
                    }
                    notEmpty = sqlResult.next();
                }
            }
            catch (SQLException e)
            {
                logger.error("Error during the transformation URIs to URLs (for display)!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            
            if ((tmpPrefixList != null) && (! tmpPrefixList.isEmpty()))
            {
                prefixList.put(docProvider, new ArrayList<String>(tmpPrefixList));
            }
            
            if ((tmpSuffixList != null) && (! tmpSuffixList.isEmpty()))
            {
                suffixList.put(docProvider, new ArrayList<String>(tmpSuffixList));
            }
            
            if ((tmpInfoList != null) && (! tmpInfoList.isEmpty()))
            {
                infoList.put(docProvider, new ArrayList<String>(tmpInfoList));
            }
            
            if ((tmpInstitutionList != null) && (! tmpInstitutionList.isEmpty()))
            {
                institutionList.put(docProvider, new ArrayList<String>(tmpInstitutionList));
            }
            
            if ((tmpLocationList != null) && (! tmpLocationList.isEmpty()))
            {
                locationList.put(docProvider, new ArrayList<String>(tmpLocationList));
            }
        }
        
        // without closing the statement, let's see...
        dbPool.closeConnection();
        
        // for all the pieces of documentation...
        for (int i=0; i<data.size(); ++i)
        {
            // empties the lists
            urlList.clear();
            // gets the ID of the element from the URI (removing the URI of the data type)
            String id = MiriamUtilities.getElementPart((String) data.get(i));
            // gets the URI of the data type
            String type = MiriamUtilities.getDataPart((String) data.get(i));
            
            // for all the secondary resources available for this data type, we build the physical address...
            if (prefixList.get(listOfReferenceUris.get(type)) != null)
            {
                for (int j=0; j<prefixList.get(listOfReferenceUris.get(type)).size(); ++j)
                {
                    urlList.add((prefixList.get(listOfReferenceUris.get(type))).get(j) + id + suffixList.get(listOfReferenceUris.get(type)).get(j));   // generates the physical location, according to the data type
                }
            }
            
            // generates the physical location of the primary resource
            String url = prefixPrimary.get(listOfReferenceUris.get(type)) + URLDecoder.decode(id, "UTF-8") + suffixPrimary.get(listOfReferenceUris.get(type));

            context.setAttribute("id", String.valueOf(i+1));

            context.setAttribute("url", url);
            context.setAttribute("info", infoPrimary.get(listOfReferenceUris.get(type)));
            context.setAttribute("institution", institutionPrimary.get(listOfReferenceUris.get(type)));
            context.setAttribute("location", locationPrimary.get(listOfReferenceUris.get(type)));

            context.setAttribute("urlList", urlList);
            context.setAttribute("infoList", infoList.get(listOfReferenceUris.get(type)));
            context.setAttribute("institutionList", institutionList.get(listOfReferenceUris.get(type)));
            context.setAttribute("locationList", locationList.get(listOfReferenceUris.get(type)));
            
            getJspBody().invoke(null);   // process the body of the tag and print it to the response
        }
    }
}
