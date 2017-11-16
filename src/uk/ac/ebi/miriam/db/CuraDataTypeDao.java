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


import uk.ac.ebi.miriam.web.MiriamUtilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;


/**
 * <p>Performs all the persistence features (link with the database) of a <code>CuraDataType</code> object, such as "retrieve" or "save".
 * <p>It is necessary to use the method "setParameters()" before any of the other methods available!
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
 * @version 20130827
 */
public class CuraDataTypeDao extends Dao
{
    private Logger logger = Logger.getLogger(CuraDataTypeDao.class);
    private static Object lock = new Object();   /* lock used to protect concurrent access to critical section */
    private final int DATATYPE = 1;   /* constant used in the 'generateID()' method */
    private final int RESOURCE = 2;   /* constant used in the 'generateID()' method */
    private final int CURA_DATATYPE = 3;   /* constant used in the 'generateID()' method */
    private final String SUBMITTED = "Submitted";
    private final String CURATION = "Curation";
    private final String PUBLISHED = "Canceled";
    private final String PENDING = "Pending";
    private final String CANCELED = "Published";
    private final String ALL = "All";
    
    /**
     * Default Constructor.
     */
    public CuraDataTypeDao(String pool)
    {
    	super(pool);
    }
    
    
    /**
     * NOT IMPLEMENTED!
     * @param dataType
     * @return
     *
    public int save(CuraDataType dataType)
    {
        
        // TODO: complete that!
        
        return 13;
    }
    */
    
    /**
     * Retrieves all the information about a data collection, based on its identifier.
     * @param internal identifier of the data collection
     * @return can be null
     */
    public CuraDataType retrieve(String id)
    {
        CuraDataType dataType = null;
        boolean exist = false;
        
        // retrieves basic info about the data collection
        PreparedStatement stmt01 = null;
        ResultSet rs01 = null;
        try
        {
        	stmt01 = openPreparedStatement("SELECT datatype_id, name, pattern, definition, date_creation, date_modif, obsolete, obsolete_comment, replacement FROM cura_datatype WHERE (datatype_id=?)");
            stmt01.setString(1, id);
            logger.debug("SQL query: " + stmt01.toString());
            rs01 = stmt01.executeQuery();
            
            int nb = MiriamUtilities.getRowCount(rs01);
            if (nb == 1)
            {
                exist = true;
                
                dataType = new CuraDataType();
                dataType.setId(rs01.getString("datatype_id"));
                dataType.setName(rs01.getString("name"));
                dataType.setRegexp(rs01.getString("pattern"));
                dataType.setDefinition(rs01.getString("definition"));
                dataType.setDateCreation(rs01.getTimestamp("date_creation"));
                dataType.setDateModification(rs01.getTimestamp("date_modif"));
                dataType.setObsolete(rs01.getBoolean("obsolete"));
                dataType.setObsoleteComment(rs01.getString("obsolete_comment"));
                dataType.setReplacedBy(rs01.getString("replacement"));
            }
            else
            {
                if (nb > 1)
                {
                    logger.warn("The data type '" + id + "' in the curation pipeline is not unique!");
                }
                exist = false;
            }
        }
        catch (SQLException e)
        {
            logger.error("An exception occured during the processing or closing of a prepared statement (retrieving data type general info from curation pipeline)!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	closeResultSet(rs01);
        	closePreparedStatement(stmt01);
        }
        
        // retrieves the other information attached to the data collection (if it exists)
        if (exist)
        {
            // retrieves the synonyms (if any)
        	PreparedStatement stmt02 = null;
        	ResultSet rs02 = null;
            try
            {
            	stmt02 = openPreparedStatement("SELECT ms.name FROM cura_datatype md, cura_synonym ms WHERE ((ms.ptr_datatype = md.datatype_id) AND (md.datatype_id=?))");
                stmt02.setString(1, id);
                logger.debug("SQL query: " + stmt02.toString());
                rs02 = stmt02.executeQuery();
                ArrayList<String> syn = new ArrayList<String>();
                syn = (ArrayList<String>) MiriamUtilities.ArrayConvert(rs02);
                dataType.setSynonyms(syn);
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing or closing of a prepared statement (retrieving data type synonyms from curation pipeline)!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            finally
            {
            	closeResultSet(rs02);
            	closeStatement(stmt02);
            }
            
            // retrieves the official URL (none normally)
            Statement stmt21 = null;
            ResultSet rs21 = null;
			try
			{
				stmt21 = openStatement();
				rs21 = stmt21.executeQuery("SELECT mu.uri FROM cura_datatype md, cura_uri mu WHERE ((mu.ptr_datatype = md.datatype_id) AND (md.datatype_id = '" + id + "') AND (mu.uri_type = 'URL') AND (mu.deprecated = '0'))");
	            String url = MiriamUtilities.StringConvert(rs21);
	            dataType.setURL(url);
			}
			catch (SQLException e1)
			{
				logger.debug("SQLException raised when retrieving the official URL of a data collection.");
				logger.debug(e1.getMessage());
			}
            finally
            {
            	closeResultSet(rs21);
            	closeStatement(stmt21);
            }
            
            // retrieves the official URN
            Statement stmt22 = null;
            ResultSet rs22 = null;
			try
			{
				stmt22 = openStatement();
				rs22 = stmt22.executeQuery("SELECT mu.uri FROM cura_datatype md, cura_uri mu WHERE ((mu.ptr_datatype = md.datatype_id) AND (md.datatype_id = '" + id + "') AND (mu.uri_type = 'URN') AND (mu.deprecated = '0'))");
	            String urn = MiriamUtilities.StringConvert(rs22);
	            dataType.setURN(urn);
			}
			catch (SQLException e1)
			{
				logger.debug("SQLException raised when retrieving the official URN of a data collection.");
				logger.debug(e1.getMessage());
			}
            finally
            {
            	closeResultSet(rs22);
            	closeStatement(stmt22);
            }
            
            // retrieves the deprecated URIs
			Statement stmt23 = null;
			ResultSet rs23 = null;
			try
			{
				stmt23 = openStatement();
				rs23 = stmt23.executeQuery("SELECT mu.uri FROM cura_datatype md, cura_uri mu WHERE ((mu.ptr_datatype = md.datatype_id) AND (md.datatype_id = '" + id + "') AND (mu.deprecated = '1'))");
				ArrayList<String> deprecated = (ArrayList<String>) MiriamUtilities.ArrayConvert(rs23);
				dataType.setDeprecatedURIs(deprecated);
			}
			catch (SQLException e1)
			{
				logger.debug("SQLException raised when retrieving the deprecated URIs of a data collection.");
				logger.debug(e1.getMessage());
			}
            finally
            {
            	closeResultSet(rs23);
            	closeStatement(stmt23);
            }

            // retrieves the resources (physical locations)
			Statement stmt24 = null;
			ResultSet rs24 = null;
			try
			{
				stmt24 = openStatement();
				rs24 = stmt24.executeQuery("SELECT mr.resource_id, mr.url_element_prefix, mr.url_element_suffix, mr.url_resource, mr.info, mr.institution, mr.location, mr.example, mr.obsolete FROM cura_datatype mdt, cura_resource mr WHERE ((mr.ptr_datatype = mdt.datatype_id) AND (mdt.datatype_id = '" + id + "'))");
                boolean notEmpty = rs24.next();
                while (notEmpty)
                {
                    Resource temp = new Resource();
                    temp.setId(rs24.getString("resource_id"));
                    temp.setUrl_prefix(StringEscapeUtils.escapeHtml4(rs24.getString("url_element_prefix")));
                    temp.setUrl_suffix(StringEscapeUtils.escapeHtml4(rs24.getString("url_element_suffix")));
                    temp.setUrl_root(StringEscapeUtils.escapeHtml4(rs24.getString("url_resource")));
                    temp.setInfo(rs24.getString("info"));
                    temp.setInstitution(StringEscapeUtils.escapeHtml4(rs24.getString("institution")));
                    temp.setLocation(rs24.getString("location"));
                    temp.setExample(rs24.getString("example"));
                    if (rs24.getString("obsolete").compareToIgnoreCase("0") == 0)
                    {
                        temp.setObsolete(false);
                    }
                    else
                    {
                        temp.setObsolete(true);
                    }
                    // add the resource to the data type
                    dataType.addResource(temp);
                    // next resource (if it exists)
                    notEmpty = rs24.next();
                }
            }               
            catch (SQLException e)
            {
                logger.error("Error while searching the resources (in curation pipeline)!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            finally
            {
            	closeResultSet(rs24);
            	closeStatement(stmt24);
            }
            
            // retrieves documentation information (URLs) linked to the data collection
			Statement stmt3 = null;
			ResultSet rs3 = null;
			try
			{
				stmt3 = openStatement();
				rs3 = stmt3.executeQuery("SELECT md.uri FROM cura_datatype mdt, cura_doc md WHERE ((md.ptr_datatype = mdt.datatype_id) AND (mdt.datatype_id = '" + id + "') AND (md.ptr_type = 'data') AND (md.uri_type = 'URL'))");
				ArrayList<String> docs_url = null;
				docs_url = (ArrayList<String>) MiriamUtilities.ArrayConvert(rs3, true);
				dataType.setDocumentationURLs(docs_url);
			}
			catch (SQLException e)
            {
                logger.error("Error while retrieving the documentation info (URLs) linked to the data collection!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            finally
            {
            	closeResultSet(rs3);
            	closeStatement(stmt3);
            }
			
            // search documentation information (MIRIAM URIs) linked to the data collection
			Statement stmt4 = null;
			ResultSet rs4 = null;
			try
			{
				stmt4 = openStatement();
				rs4 = stmt4.executeQuery("SELECT md.uri, md.uri_type FROM cura_datatype mdt, cura_doc md WHERE ((md.ptr_datatype = mdt.datatype_id) AND (mdt.datatype_id = '" + id + "') AND (md.ptr_type = 'data') AND (md.uri_type != 'URL'))");
                boolean notEmpty = rs4.next();
                while (notEmpty)
                {
                    dataType.addDocumentationID(rs4.getString("md.uri"));
                    dataType.addDocumentationIDType(rs4.getString("md.uri_type"));
                    
                    notEmpty = rs4.next();
                }
            }
            catch (SQLException e)
            {
                logger.error("Error during the transformation URIs to URLs (for display in curation pipeline)!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            finally
            {
            	closeResultSet(rs4);
            	closeStatement(stmt4);
            }
            
            // retrieves some information for curation purposes
			Statement stmt5 = null;
			ResultSet rs5 = null;
			try
			{
				stmt5 = openStatement();
				rs5 = stmt5.executeQuery("SELECT comment, state, sub_info, public_id FROM cura_material WHERE (ptr_datatype='" + id + "')");
                if (rs5.first())
                {
                    dataType.setComment(rs5.getString("comment"));
                    dataType.setState(rs5.getString("state"));
                    dataType.setSubInfo(rs5.getString("sub_info"));
                    dataType.setPublicId(rs5.getString("public_id"));
                }
            }               
            catch (SQLException e)
            {
                logger.error("Error while searching the curation specific information!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            finally
            {
            	closeResultSet(rs5);
            	closeStatement(stmt5);
            }
            
            // retrieves the list of associated restrictions (if any)
			Statement stmt6 = null;
			ResultSet rs6 = null;
			try
			{
				stmt6 = openStatement();
				rs6 = stmt6.executeQuery("SELECT r.id AS rid, t.id AS tid, r.desc, r.link, r.link_text, t.short_desc, t.long_desc FROM cura_restriction r, mir_restriction_type t WHERE ((r.ptr_datatype = '" + id + "') AND (r.ptr_restriction = t.id))");
                boolean notEmpty = rs6.next();
                while (notEmpty)
                {
                    Restriction temp = new Restriction();
                    temp.setId(rs6.getInt("rid"));
                    temp.getType().setId(rs6.getInt("tid"));
                    temp.getType().setCategory(rs6.getString("short_desc"));
                    temp.getType().setDesc(rs6.getString("long_desc"));
                    temp.setInfo(rs6.getString("desc"));
                    temp.setLink(rs6.getString("link"));
                    temp.setLinkText(rs6.getString("link_text"));
                    // adds the restriction to the data collection
                    dataType.addRestriction(temp);
                    // moves to the next restriction (if it exists)
                    notEmpty = rs6.next();
                }
            }
            catch (SQLException e)
            {
                logger.error("Error while searching the restrictions for the data collection: " + id + "!");
                logger.error("SQLException raised: " + e.getMessage());
            }
            finally
            {
            	closeResultSet(rs6);
            	closeStatement(stmt6);
            }
        }
        
        return dataType;
    }
    
    /**
     * Retrieves the number of active data collections in the curation pipeline.
     * This includes data collections submitted, under curation and pending.
     * This does not include cancelled and published data collections.
     */
    public int getNbDataTypesActive()
    {
    	Statement stmt = null;
        ResultSet rs = null;
        int nb = 0;
        
        try
        {
        	stmt = openStatement();
        	rs = stmt.executeQuery("SELECT COUNT(ptr_datatype) AS number FROM cura_material WHERE (state='Submitted' OR state='Curation' OR state='Pending')");
            if (rs.first())
            {
                nb = rs.getInt("number");
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while counting the number of active data collections in the curation pipeline!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	closeResultSet(rs);
        	closeStatement(stmt);
        }
        
        return nb;
    }
    
    
    /**
     * Retrieves the number of resources associated to active data collections in the curation pipeline (with status: Submitted, Curation or Pending).
     */
    public int getNbResourcesActive()
    {
    	Statement stmt =null;
        ResultSet rs = null;
        int nb = 0;
        
        try
        {
        	stmt = openStatement();
    		rs = stmt.executeQuery("SELECT COUNT(r.resource_id) AS number FROM cura_material m, cura_resource r WHERE ((m.state='Submitted' OR m.state='Curation' OR m.state='Pending') AND (r.ptr_datatype = m.ptr_datatype) AND (r.obsolete = 0))");
            if (rs.first())
            {
                nb = rs.getInt("number");
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while counting the number of active data collections in the curation pipeline!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	closeResultSet(rs);
        	closeStatement(stmt);
        }
        
        return nb;
    }
    
    
    /**
     * Retrieves the newest date of last modification of the info in the curation pipeline.
     */
    public Date getLastModifDate()
    {
        Statement stmt = null;
        ResultSet rs = null;
        Date lastModif = null;
        
        try
        {
        	stmt = openStatement();
    		rs = stmt.executeQuery("SELECT date_modif FROM cura_datatype WHERE 1 ORDER BY date_modif DESC LIMIT 1");
            if (rs.first())
            {
                lastModif = rs.getDate("date_modif");
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the newest date of last modification in the curation pipeline!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	closeResultSet(rs);
        	closeStatement(stmt);
        }
        
        return lastModif;
    }
    
    
    /**
     * Updates the last modification date of a data collection in the curation pipeline.
     * @param collectionId
     * @return
     */
    public Boolean updateLastModifDate(String collectionId)
    {
    	PreparedStatement stmt = null;
        int result = -1;   // default: failure
        String sql = "UPDATE cura_datatype SET date_modif=NOW() WHERE (datatype_id = ?) ";
        
        // resource main information (we just update it in order to keep the ID stable)
        try
        {
        	stmt = openPreparedStatement(sql);
            stmt.setString(1, collectionId);
            result = stmt.executeUpdate();   // if result is "1", means success
        }
        catch (SQLException e)
        {
            logger.error("An exception occured during the update of the last modification date of the data collection in the pipeline: " + collectionId + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
            result = -1;   // failure
        }
        finally
        {
        	closePreparedStatement(stmt);
        }
        
        return (result == 1);   // if result is "1", that means success
    }
    
    
    /**
     * Flags the data collection as restricted.
     * @param dataTypeId data collection identifier 
     * @return whether the update is a success or not
     */
    public Boolean setRestricted(String dataTypeId)
    {
    	PreparedStatement stmt = null;
        int result = -1;   // default: failure
        String sql = "UPDATE cura_datatype SET restriction=1 WHERE (datatype_id=?)";
        
        // resource main information (we just update it in order to keep the ID stable)
        try
        {
        	stmt = openPreparedStatement(sql);
            stmt.setString(1, dataTypeId);
            result = stmt.executeUpdate();   // if result is "1", means success
        }
        catch (SQLException e)
        {
            logger.error("An exception occured during the update of the restriction status of the data collection in the pipeline: " + dataTypeId + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
            result = -1;   // failure
        }
        finally
        {
        	closePreparedStatement(stmt);
        }
        
        return (result == 1);   // if result is "1", that means success
    }
    
    
    /**
     * Retrieves all the data collections in the curation pipeline.
     * @return list of data types
     */
    public List<SimpleCuraDataType> retrieveAll()
    {
    	Statement stmt = null;
        ResultSet rs = null;
        List<SimpleCuraDataType> result = new ArrayList<SimpleCuraDataType>();
        boolean notEmpty;
        
        try
        {
        	stmt = openStatement();
    		rs = stmt.executeQuery("SELECT d.name, d.definition, d.datatype_id, m.state, d.date_creation, m.public_id FROM cura_datatype d, cura_material m WHERE (d.datatype_id = m.ptr_datatype) ORDER BY d.date_creation");
            notEmpty = rs.next();
            while (notEmpty)
            {
                SimpleCuraDataType temp = new SimpleCuraDataType();
                temp.setId(rs.getString("datatype_id"));
                temp.setName(rs.getString("name"));
                temp.setDefinition(rs.getString("definition"));
                temp.setState(rs.getString("state"));
                temp.setSubmissionDate(rs.getTimestamp("date_creation"));
                temp.setPublicId(rs.getString("public_id"));
                
                // adds this simple data type to the list
                result.add(temp);
                
                // next data type (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while searching the data types in the curation pipeline!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	closeResultSet(rs);
        	closeStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Tests if a data collection exists in the curation pipeline, based on its main attributes.
     * @param data type
     * @return
     */
    public boolean exists(CuraDataType data)
    {
        boolean state = true;   // default: we need to proof the uniqueness
        Statement stmt1 = null;
        Statement stmt2 = null;
        Statement stmt3 = null;
        Statement stmt4 = null;
        Statement stmt5 = null;
        Statement stmt6 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        ResultSet rs4 = null;
        ResultSet rs5 = null;
        ResultSet rs6 = null;
        String sql;
        
        // query to test the uniqueness of the name (part one)
        sql = "SELECT name FROM cura_datatype WHERE (name='" + data.getName() + "')";
        try
        {
        	stmt1 = openStatement();
    		rs1 = stmt1.executeQuery(sql);
    		
	        // no name of existing data types is equivalent of the name of the data type
	        if (DbPoolConnect.getRowCount(rs1) == 0)
	        {
	            // query to test the uniqueness of the name (part two)
	            sql = "SELECT name FROM cura_synonym WHERE (name='" + data.getName() + "')";
	            stmt2 = openStatement();
	            rs2 = stmt2.executeQuery(sql);
	            
	            // no synonym of existing data types is equivalent to the name of the new data type  
	            if (DbPoolConnect.getRowCount(rs2) == 0)
	            {
	                boolean tempExist1 = false;
	                
	                // queries to test the uniqueness of the synonyms (part one)
	                for (int i=0; i<data.getSynonyms().size(); ++i)
	                {
	                    sql = "SELECT name FROM cura_datatype WHERE (name='" + data.getSynonym(i) + "')";
	                    stmt3 = openStatement();
	                    rs3 = stmt3.executeQuery(sql);
	                    
	                    // one of the synonyms of the new data type is equivalent to the name of an existing data type
	                    if (DbPoolConnect.getRowCount(rs3) != 0)
	                    {
	                        tempExist1 = true;
	                        break;
	                    }
	                }
	                
	                // no name of existing data types is equivalent to the synonyms of the new data type
	                if (! tempExist1)
	                {
	                    boolean tempExist2 = false;
	                    
	                    // queries to test the uniqueness of the synonyms (part two)
	                    for (int i=0; i<data.getSynonyms().size(); ++i)
	                    {
	                        sql = "SELECT name FROM cura_synonym WHERE (name='" + data.getSynonym(i) + "')";
	                        stmt4 = openStatement();
		                    rs4 = stmt4.executeQuery(sql);
	                        
	                        if (DbPoolConnect.getRowCount(rs4) != 0)
	                        {
	                            tempExist2 = true;
	                            break;
	                        }
	                    }
	                    
	                    // no synonym of existing data types is equivalent to a synonym of the new data type
	                    if (! tempExist2)
	                    {
	                        // query to test the uniqueness of the URIs
	                        sql = "SELECT uri FROM cura_uri WHERE ((uri='" + data.getURL() + "') OR (uri='" + data.getURN()  + "'))";
	                        stmt5 = openStatement();
		                    rs5 = stmt5.executeQuery(sql);
	                        
	                        // no URI of existing data types is equivalent to one of the URIs of the new data type 
	                        if (DbPoolConnect.getRowCount(rs5) == 0)
	                        {
	                            boolean tempExist3 = false;
	                            
	                            // queries to test the uniqueness of the deprecated URI(s)
	                            for (int i=0; i<data.getDeprecatedURIs().size(); ++i)
	                            {
	                                sql = "SELECT uri FROM cura_uri WHERE (uri='" + data.getDeprecatedURI(i) + "')";
	                                stmt6 = openStatement();
	    		                    rs6 = stmt6.executeQuery(sql);
	                                
	                                if (DbPoolConnect.getRowCount(rs6) != 0)
	                                {
	                                    tempExist3 = true;
	                                    break;
	                                }
	                            }
	                            
	                            // no URI of existing data types is equivalent to one of the deprecated URI(s) of the new data type
	                            if (! tempExist3)
	                            {
	                                state = false;  // and finally we have our proof of uniqueness
	                            }
	                        }
	                    }
	                }
	            }
	        }
        }
        catch (SQLException e)
        {
            logger.error("Error while checking whether a collection already exists in the registry.");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	closeResultSet(rs1);
        	closeStatement(stmt1);
        	closeResultSet(rs2);
        	closeStatement(stmt2);
        	closeResultSet(rs3);
        	closeStatement(stmt3);
        	closeResultSet(rs4);
        	closeStatement(stmt4);
        	closeResultSet(rs5);
        	closeStatement(stmt5);
        	closeResultSet(rs6);
        	closeStatement(stmt6);
        }

        
        return state;
    }


    /**
     * Performs all the SQL queries to update a data type in the curation pipeline.
     * @param data new data type
     * @param oldOne old data type
     * @return result of the queries: 1 equals success, 0 equals failure
     */
    public int update(CuraDataType data, CuraDataType oldOne)
    {
        int resultStatus = -1;
        int resultGlobal = 1;   // we consider that everything is ok by default
        String sql = new String();
        
        // critical section to protected against concurrent access (the 'setAutoCommit' can't be used any more)
        synchronized(lock)
        {
            // begin of the transaction
            //pool.setAutoCommit(false);   CAN'T BE USED ANY MORE
            
            // resource main information (we just update it in order to keep the ID stable)
        	PreparedStatement stmt1 = null;
        	try
            {
        		stmt1 = openPreparedStatement("UPDATE cura_datatype SET name=?, definition=?, pattern=?, date_modif=NOW(), obsolete=?, obsolete_comment=?, replacement=? WHERE (datatype_id=?)");
                stmt1.setString(1, data.getName());
                stmt1.setString(2, data.getDefinition());
                stmt1.setString(3, data.getRegexp());
                if (data.isObsolete())
                {
                    stmt1.setInt(4, 1);
                }
                else
                {
                    stmt1.setInt(4, 0);
                }
                stmt1.setString(5, data.getObsoleteComment());
                stmt1.setString(6, data.getReplacedBy());
                stmt1.setString(7, data.getId());
                logger.debug("SQL query: " + stmt1.toString());
                resultStatus = stmt1.executeUpdate();
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing or closing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
                resultStatus = 0;   // failure
            }
            finally
            {
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                logger.info("Result of the update query #1 (edit data type curation, resource): " + resultStatus + " (" + resultGlobal + ")");
                closePreparedStatement(stmt1);
            }
            
            
            // synonym(s): removes all the previous one(s), if necessary
        	Statement stmt2 = null;
        	PreparedStatement stmt3 = null;
        	ResultSet rs2 = null;
            sql = "SELECT * FROM cura_synonym WHERE (ptr_datatype='" + data.getId() + "')";
            try
            {
            	stmt2 = openStatement();
                rs2 = stmt2.executeQuery(sql);
	            if (DbPoolConnect.getRowCount(rs2) > 0)
	            {
	                stmt3 = openPreparedStatement("DELETE FROM cura_synonym WHERE (ptr_datatype='" + data.getId() + "')");
	                resultStatus = stmt3.executeUpdate();
	                logger.info("Result of the update query #2 (edit data type curation, remove old synonyms): " + resultStatus + "*");
	                /* DELETE returns the number of entries deleted, so it is not wise to use it to check if the update process is a success or not
	                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)*/
	            }
	            else
	            {
	                logger.info("No update query #2 (edit data type curation, remove old synonyms): no synonym to delete.");
	            }
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during removal of previous synonym(s)!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            finally
            {
            	closeResultSet(rs2);
            	closeStatement(stmt2);
            	closePreparedStatement(stmt3);
            }
            
            // synonym(s): add the new one(s)
            PreparedStatement stmt4 = null;
            int counter = 0;
            try
            {
            	stmt4 = openPreparedStatement("INSERT INTO cura_synonym (name, ptr_datatype) VALUES (?, ?)");
	            for (int i=0; i<data.getSynonyms().size(); ++i)
	            {
	            	counter = i;
	            	stmt4.setString(1, data.getSynonym(i));
	            	stmt4.setString(2, data.getId());
                    logger.debug("SQL query: " + stmt4.toString());
                    resultStatus = stmt4.executeUpdate();
                }
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
                resultStatus = 0;   // failure
            }
            finally
            {
            	closePreparedStatement(stmt4);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                logger.info("Result of the update query #3." + counter + " (edit data type curation, synonyms): " + resultStatus + " (" + resultGlobal + ")");
            }
            
            // URL and URN (official and deprecated): removes all the previous one(s)
            PreparedStatement stmt5 = null;
            try
            {
            	stmt5 = openPreparedStatement("DELETE FROM cura_uri WHERE (ptr_datatype='" + data.getId() + "')");
            	resultStatus = stmt5.executeUpdate();
                logger.info("Result of the update query #4 (edit data type curation, remove old URIs): " + resultStatus + "*");
                /* DELETE returns the number of entries deleted, so it is not wise to use it to check if the update process is a success or not
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK) */	
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the removal of previous URLs and URNs!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            finally
            {
                closePreparedStatement(stmt5);
            }
            
            // official URL: add the new one
            if (! MiriamUtilities.isEmpty(data.getURL()))
            {
                PreparedStatement stmt6 = null;
                try
                {
                	stmt6 = openPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URL', '0', ?)");
                    stmt6.setString(1, data.getURL());
                    stmt6.setString(2, data.getId());
                    logger.debug("SQL query: " + stmt6.toString());
                    resultStatus = stmt6.executeUpdate();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing or closing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                	closePreparedStatement(stmt6);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                    logger.info("Result of the adding query #5 (edit data type curation, URL): " + resultStatus + " (" + resultGlobal + ")");
                }
            }
            
            // official URN: add the new one
            if (! MiriamUtilities.isEmpty(data.getURN()))
            {
                PreparedStatement stmt7 = null;
                try
                {
                	stmt7 = openPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URN', '0', ?)");
                    stmt7.setString(1, data.getURN());
                    stmt7.setString(2, data.getId());
                    logger.debug("SQL query: " + stmt7.toString());
                    resultStatus = stmt7.executeUpdate();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing or closing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                	closePreparedStatement(stmt7);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                    logger.info("Result of the adding query #6 (edit data type curation, URN): " + resultStatus + " (" + resultGlobal + ")");
                }
            }
            
            // obsolete URI(s): add the new one(s)
            PreparedStatement stmt8Urn = null;
            PreparedStatement stmt8Url = null;
            int counter2 = 0;
            try
            {
            	stmt8Urn = openPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URN', '1', ?)");
            	stmt8Url = openPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URL', '1', ?)");
	            for (int i=0; i<data.getDeprecatedURIs().size(); ++i)
	            {
	            	counter2 = i;
                    if (data.isDeprecatedURN(i))
                    {
                        stmt8Urn.setString(1, data.getDeprecatedURI(i));
                        stmt8Urn.setString(2, data.getId());
                        logger.debug("SQL query: " + stmt8Urn.toString());
                        resultStatus = stmt8Urn.executeUpdate();
                    }
                    else
                    {
                        if (data.isDeprecatedURL(i))
                        {
                            stmt8Url.setString(1, data.getDeprecatedURI(i));
                            stmt8Url.setString(2, data.getId());
                            logger.debug("SQL query: " + stmt8Url.toString());
                            resultStatus = stmt8Url.executeUpdate();
                        }
                        else
                        {
                            logger.warn("This URI '" + data.getDeprecatedURI(i) + "' should be deprecated but is not...");
                            //TODO: check that in the logs...
                        }
                    }
                }
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
                resultStatus = 0;   // failure
            }
            finally
            {
            	closePreparedStatement(stmt8Url);
                closePreparedStatement(stmt8Urn);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                logger.info("Result of the adding query #7." + counter2 +" (edit data type curation, deprecated URIs): " + resultStatus + " (" + resultGlobal + ")");
            }
            
            // retrieves the deleted resources (by comparison of two datasets)
            List[] diff = new ArrayList[3];
            diff = differentiateResources(oldOne.getResources(), data.getResources());
            
            ArrayList<Resource> oldResources = new ArrayList<Resource>(diff[0]);
            ArrayList<Resource> newResources = new ArrayList<Resource>(diff[1]);
            ArrayList<Resource> deletedResources = new ArrayList<Resource>(diff[2]);
            
            // modifies the resources already existing
            Iterator<Resource> itr = oldResources.iterator();
            int cpt = 0;
            PreparedStatement stmt9 = null;
            PreparedStatement stmt9obs = null;
            try
            {
            	stmt9 = openPreparedStatement("UPDATE cura_resource SET url_element_prefix=?, url_element_suffix=?, url_resource=?, info=?, institution=?, location=?, example=?, obsolete='0' WHERE (resource_id=?)");
            	stmt9obs = openPreparedStatement("UPDATE cura_resource SET url_element_prefix=?, url_element_suffix=?, url_resource=?, info=?, institution=?, location=?, example=?, obsolete='1' WHERE (resource_id=?)");
	            while (itr.hasNext())
	            {
	                Resource r = (Resource) itr.next();
	                
                    if (r.isObsolete())   // resource obsolete
                    {
                    	stmt9obs.setString(1, r.getUrl_prefix());
                    	stmt9obs.setString(2, r.getUrl_suffix());
                    	stmt9obs.setString(3, r.getUrl_root());
                    	stmt9obs.setString(4, r.getInfo());
                    	stmt9obs.setString(5, r.getInstitution());
                    	stmt9obs.setString(6, r.getLocation());
                    	stmt9obs.setString(7, r.getExample());
                    	stmt9obs.setString(8, r.getId());
                        logger.debug("SQL query: " + stmt9obs.toString());
                        resultStatus = stmt9obs.executeUpdate();
                    }
                    else
                    {
                    	stmt9.setString(1, r.getUrl_prefix());
                    	stmt9.setString(2, r.getUrl_suffix());
                    	stmt9.setString(3, r.getUrl_root());
                    	stmt9.setString(4, r.getInfo());
                    	stmt9.setString(5, r.getInstitution());
                    	stmt9.setString(6, r.getLocation());
                    	stmt9.setString(7, r.getExample());
                    	stmt9.setString(8, r.getId());
                        logger.debug("SQL query: " + stmt9.toString());
                        resultStatus = stmt9.executeUpdate();
                    }
                }
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
                resultStatus = 0;   // failure
            }
            finally
            {
            	closePreparedStatement(stmt9);
            	closePreparedStatement(stmt9obs);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                logger.info("Result of the update query #8." + cpt + " (edit data type curation, update old resources): " + resultStatus + " (" + resultGlobal + ")");
                cpt++;
            }
            
            // adds the new resources
            ListIterator it3 = newResources.listIterator();
            cpt = 0;
            // a new resource can't be 'obsolete'
            PreparedStatement stmt10 = null;
            try
            {
            	stmt10 = openPreparedStatement("INSERT INTO cura_resource (resource_id, url_element_prefix, url_element_suffix, url_resource, info, institution, location, example, obsolete, ptr_datatype) VALUES (?, ?, ?, ?, ?, ?, ?, ?, '0', '" + data.getId() + "')");
	            while (it3.hasNext())
	            {
	                Resource temp = (Resource) it3.next();
	                
	                stmt10.setString(1, generateID(RESOURCE));
	                stmt10.setString(2, temp.getUrl_prefix());
	                stmt10.setString(3, temp.getUrl_suffix());
	                stmt10.setString(4, temp.getUrl_root());
	                stmt10.setString(5, temp.getInfo());
	                stmt10.setString(6, temp.getInstitution());
	                stmt10.setString(7, temp.getLocation());
	                stmt10.setString(8, temp.getExample());
                    logger.debug("SQL query: " + stmt10.toString());
                    resultStatus = stmt10.executeUpdate();
                }
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
                resultStatus = 0;   // failure
            }
            finally
            {
            	closePreparedStatement(stmt10);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                logger.info("Result of the adding query #9." + cpt + " (edit data type curation, add new resources): " + resultStatus + " (" + resultGlobal + ")");
            }
            
            // indicates as 'obsolete' the resources "removed"
            ListIterator it4 = deletedResources.listIterator();
            cpt = 0;
            PreparedStatement stmt11 = null;
            try
            {
            	stmt11 = openPreparedStatement("UPDATE cura_resource SET obsolete='1' WHERE (resource_id=?)");
	            while (it4.hasNext())
	            {
	                Resource temp = (Resource) it4.next();
	                stmt11.setString(1, temp.getId());
	                resultStatus = stmt11.executeUpdate();
	                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
	                logger.info("Result of the update query #10." + cpt + " (edit data type curation, update obsolete resources): " + resultStatus + " (" + resultGlobal + ")");
	            }
            }
            catch (SQLException e)
            {
            	logger.debug("SQLException raised when obsoletise a resource");
            	logger.debug(e.getMessage());
            }
            finally
            {
            	closePreparedStatement(stmt11);
            }
            
            // documentation IDs and URLs: removes all the previous one(s), if necessary (means if any stored)
            Statement stmt12 = null;
            PreparedStatement stmt121 = null;
            ResultSet rs12 = null;
            try
            {
            	stmt121 = openPreparedStatement("DELETE FROM cura_doc WHERE (ptr_datatype=?)");
            	stmt12 = openStatement();
            	rs12 = stmt12.executeQuery("SELECT * FROM cura_doc WHERE (ptr_datatype='" + data.getId() + "')");
	            if (DbPoolConnect.getRowCount(rs12) > 0)
	            {
	            	stmt121.setString(1, data.getId());
	                resultStatus = stmt121.executeUpdate();
	                logger.info("Result of the update query #11 (edit data type curation, remove old documentation IDs and URIs): " + resultStatus + "*");
	                /* DELETE returns the number of entries deleted, so it is not wise to use it to check if the update process is a success or not
	                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
	                */
	            }
	            else
	            {
	                logger.info("No update query #11 (edit data type curation, remove old documentation IDs and URIs): no documentation to delete.");
	            }
            }
            catch (SQLException e)
            {
            	logger.debug("SQLException raised when removing previous documentation IDs and URLs.");
            	logger.debug(e.getMessage());
            }
            finally
            {
            	closeResultSet(rs12);
            	closeStatement(stmt12);
            	closePreparedStatement(stmt121);
            }
            
            // documentation ID(s): add the new (or old ones that have been deleted in the previous step) one(s)
            PreparedStatement stmt13 = null;
            int counter13 = 0;
            try
            {
            	stmt13 = openPreparedStatement("INSERT INTO cura_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES (?, ?, 'data', '" + data.getId() + "', NULL)");
	            for (int i=0; i<data.getDocumentationIDs().size(); ++i)
	            {
	            	counter13 = i;
                	stmt13.setString(1, data.getDocumentationID(i));
                	stmt13.setString(2, data.getDocumentationIDType(i));
                    logger.debug("SQL query: " + stmt13.toString());
                    resultStatus = stmt13.executeUpdate();
	            }
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing or closing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
                resultStatus = 0;   // failure
            }
            finally
            {
            	closePreparedStatement(stmt13);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means succes), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                logger.info("Result of the adding query #12." + counter13 +" (edit data type curation, documentation IDs): " + resultStatus + " (" + resultGlobal + ")");
            }
            
            // documentation URL(s)
            PreparedStatement stmt14 = null;
            int counter14 = 0;
            try
            {
            	stmt14 = openPreparedStatement("INSERT INTO cura_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES (?, 'URL', 'data', '" + data.getId() + "', NULL)");
            	for (int i=0; i<data.getDocumentationURLs().size(); ++i)
            	{
            		counter14 = i;
            		stmt14.setString(1, data.getDocumentationURL(i));
                    logger.debug("SQL query: " + stmt14.toString());
                    resultStatus = stmt14.executeUpdate();
                }
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing or closing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
                resultStatus = 0;   // failure
            }
            finally
            {
            	closePreparedStatement(stmt14);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                logger.info("Result of the adding query #13." + counter14 +" (edit data type curation, documentation URLs): " + resultStatus + " (" + resultGlobal + ")");
            }
            
            // curation stuff
            PreparedStatement stmt15 = null;
            try
            {
            	stmt15 = openPreparedStatement("UPDATE cura_material SET comment=?, state=? WHERE (ptr_datatype=?)");
            	
            	stmt15.setString(1, data.getComment());
            	stmt15.setString(2, data.getState());
            	stmt15.setString(3, data.getId());
                logger.debug("SQL query: " + stmt15.toString());
                resultStatus = stmt15.executeUpdate();
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing or closing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
                resultStatus = 0;   // failure
            }
            finally
            {
            	closePreparedStatement(stmt15);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                logger.info("Result of the update query #14 (edit data type curation, curation stuff): " + resultStatus + " (" + resultGlobal + ")");
            }
            
            // end of the transaction
            //pool.commit();   CAN'T BE USED ANY MORE
        }
        
        logger.debug("... end of the update of a curation data type (GLOBAL RESULT: " + resultGlobal + ")");
        
        return resultGlobal;
    }
    
    
    /**
     * Returns three lists of <code>Resource</code> objects. 
     * The first list contains the old resources (that have not been added or deleted). 
     * The second list contains the new resources (that have just been added). 
     * The third list contains the resources that have been deleted during the update (in order to be able to mark them as "deprecated").
     * The resources are differentiated by their ID.
     * @param oldList list of <code>Resource</code> objects: the resources previously stored
     * @param newList list of <code>Resource</code> objects: the updated resources (some unchanged, some deleted and some added)
     * @return list of old <code>Resource</code> objects, list of new <code>Resource</code> objects and list of deleted <code>Resource</code> objects
     */
    private static List[] differentiateResources(List<Resource> oldList, List<Resource> newList)
    {
        List[] result = new ArrayList[3];
        List<Resource> deletedResources = new ArrayList<Resource>();   // removed resources (to put as "deprecated")
        List<Resource> newResources = new ArrayList<Resource>();   // new resources
        List<Resource> oldResources = new ArrayList<Resource>();   // old undeleted resources
        
        // separates the resources: new/old ones (according to the ID)
        ListIterator<Resource> it = newList.listIterator();
        while (it.hasNext())
        {
            Resource res = (Resource) it.next();
            
            // this is an old resource (not a new addition)
            if (! (res.getId()).equalsIgnoreCase("null"))
            {
                oldResources.add(res);
            }
            else   // this is a new added resource
            {
                newResources.add(res);
            }
        }
        
        // retrieves the deleted resources (by comparison of two datasets)
        ListIterator<Resource> it2 = oldList.listIterator();
        while (it2.hasNext())
        {
            Resource obj = (Resource) it2.next();
            ListIterator<Resource> temp = newList.listIterator();
            Resource tmp = null;
            boolean find = false;
            while (temp.hasNext())
            {
                tmp = (Resource) temp.next();
                if (tmp.equals(obj))
                {
                    find = true;
                    break;
                }
            }
            
            // resource not removed
            if (find)
            {
                // resource not removed: nothing to do.
            }
            else   // resource removed: "deprecated"
            {
                deletedResources.add(obj);
            }
        }
        
        // creates the result
        result[0] = oldResources;
        result[1] = newResources;
        result[2] = deletedResources;
        
        return result;
    }
    
    
    /**
     * Stores a new data type in the curation pipeline
     * @param data new data type to store
     * @param subInfo submission information (often: name and email address of submitter and reasons for the submission)
     * @return 1 if success
     */
    public int storePendingObject(DataCollection data, String subInfo)
    {
        logger.debug("Begin of the addition of a new data type in the curator's interface...");
        
        int resultStatus = -1;
        int resultGlobal = 1;   // we consider that everything is ok by default
        String index;
        
        // critical section to protect against concurrent access (the 'setAutoCommit' can't be used any more...)
        synchronized(lock)
        {
            // begin of the transaction
            //pool.setAutoCommit(false);   //CAN'T BE USED ANY MORE, SINCE WE MOVED TO MYISAM ENGINE BECAUSE OF NFS LOCKS ISSUES!!! 
            
            // generates new ID for the data type in the curation pipeline
            index = generateID(CURA_DATATYPE);
            
            // stores resource main information
            PreparedStatement stmt1 = null;
            try
            {
            	stmt1 = openPreparedStatement("INSERT INTO cura_datatype (datatype_id, name, pattern, definition, date_creation, date_modif, obsolete, obsolete_comment, replacement) VALUES (?, ?, ?, ?, NOW(), NOW(), ?, ?, ?)");
            	stmt1.setString(1, index);
            	stmt1.setString(2, data.getName());
            	stmt1.setString(3, data.getRegexp());
            	stmt1.setString(4, data.getDefinition());
                if (data.isObsolete())
                {
                	stmt1.setInt(5, 1);
                }
                else
                {
                	stmt1.setInt(5, 0);
                }
                stmt1.setString(6, data.getObsoleteComment());
                stmt1.setString(7, data.getReplacedBy());
                logger.debug("SQL query: " + stmt1.toString());
                resultStatus = stmt1.executeUpdate();
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing or closing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
                resultStatus = 0;   // failure
            }
            finally
            {
            	closePreparedStatement(stmt1);
                logger.info("Result of the adding query #1 (new data type, resource): " + resultStatus);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
            }
            
            // synonym(s)
            if ((null != data.getSynonyms()) && (! data.getSynonyms().isEmpty()))
            {
                PreparedStatement stmt2 = null;
                String currentSyn = null;
                try
                {
                	stmt2 = openPreparedStatement("INSERT INTO cura_synonym (name, ptr_datatype) VALUES (?, ?)");
	                for (String synonym: data.getSynonyms())
	                {
	                	currentSyn = synonym;
	                	stmt2.setString(1, synonym);
	                	stmt2.setString(2, index);
                        logger.debug("SQL query: " + stmt2.toString());
                        resultStatus = stmt2.executeUpdate();
                    }
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                	closePreparedStatement(stmt2);
                    logger.info("Result of the adding query #2" + " (new data type, synonym '" + currentSyn + "'): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            
            // official URL
            if (! MiriamUtilities.isEmpty(data.getURL()))
            {
                PreparedStatement stmt3 = null;
                try
                {
                	stmt3 = openPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URL', '0', ?)");
                    stmt3.setString(1, data.getURL());
                    stmt3.setString(2, index);
                    logger.debug("SQL query: " + stmt3.toString());
                    resultStatus = stmt3.executeUpdate();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing or closing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                	closePreparedStatement(stmt3);
                    logger.info("Result of the adding query #3 (new data type, URL): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            
            // official URN
            if (! MiriamUtilities.isEmpty(data.getURN()))
            {
                PreparedStatement stmt4 = null;
                try
                {
                	stmt4 = openPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URN', '0', ?)");
                    stmt4.setString(1, data.getURN());
                    stmt4.setString(2, index);
                    logger.debug("SQL query: " + stmt4.toString());
                    resultStatus = stmt4.executeUpdate();
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing or closing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                	closePreparedStatement(stmt4);
                    logger.info("Result of the adding query #4 (new data type, URN): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            
            // obsolete URI(s)
            if (null != data.getDeprecatedURIs())
            {
                PreparedStatement stmt5Urn = null;
                PreparedStatement stmt5Url = null;
                int counter = 0;
                try
                {
                	stmt5Urn =  openPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URN', '1', ?)");
                	stmt5Url = openPreparedStatement("INSERT INTO cura_uri (uri, uri_type, deprecated, ptr_datatype) VALUES (?, 'URL', '1', ?)");
	                for (int i=0; i<data.getDeprecatedURIs().size(); ++i)
	                {
	                	counter = i;
                        if (data.isDeprecatedURN(i))
                        {
                            stmt5Urn.setString(1, data.getDeprecatedURI(i));
                            stmt5Urn.setString(2, index);
                            logger.debug("SQL query: " + stmt5Urn.toString());
                            resultStatus = stmt5Urn.executeUpdate();
                        }
                        else
                        {
                            stmt5Url.setString(1, data.getDeprecatedURI(i));
                            stmt5Url.setString(2, index);
                            logger.debug("SQL query: " + stmt5Url.toString());
                            resultStatus = stmt5Url.executeUpdate();
                        }
                    }
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                	closePreparedStatement(stmt5Url);
                	closePreparedStatement(stmt5Urn);
                    logger.info("Result of the adding query #5." + counter +" (new data type, deprecated URIs): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            
            // resources (physical locations)
            if ((null != data.getResources()) && (! data.getResources().isEmpty()))
            {
                ListIterator<Resource> it = data.getResources().listIterator();
                int cpt = 0;
                PreparedStatement stmt6 = null;
                try
                {
                	stmt6 = openPreparedStatement("INSERT INTO cura_resource (resource_id, url_element_prefix, url_element_suffix, url_resource, info, institution, location, example, obsolete, ptr_datatype) VALUES (?, ?, ?, ?, ?, ?, ?, ?, '0', ?)");
	                while (it.hasNext())
	                {
	                    // generation of a new ID for the resource
	                    Resource temp = (Resource) it.next();
	                    
	                    stmt6.setString(1, generateID(RESOURCE));
	                    stmt6.setString(2, temp.getUrl_prefix());
	                    stmt6.setString(3, temp.getUrl_suffix());
	                    stmt6.setString(4, temp.getUrl_root());
	                    stmt6.setString(5, temp.getInfo());
	                    stmt6.setString(6, temp.getInstitution());
	                    stmt6.setString(7, temp.getLocation());
	                    stmt6.setString(8, temp.getExample());
	                    stmt6.setString(9, index);
                        logger.debug("SQL query: " + stmt6.toString());
                        resultStatus = stmt6.executeUpdate();
                    }
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                	closePreparedStatement(stmt6);
                    logger.info("Result of the adding query #6." + cpt +" (new data type, resources): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                    cpt++;
                }
            }
            
            // documentation ID(s)
            if (null != data.getDocumentationIDs())
            {
                PreparedStatement stmt7 = null;
                int counter7 = 0;
                try
                {
                	stmt7 = openPreparedStatement("INSERT INTO cura_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES (?, ?, 'data', ?, NULL)");
                	for (int i=0; i<data.getDocumentationIDs().size(); ++i)
                	{
                		counter7 = i;
                        stmt7.setString(1, data.getDocumentationID(i));
                        stmt7.setString(2, data.getDocumentationIDType(i));
                        stmt7.setString(3, index);
                        logger.debug("SQL query: " + stmt7.toString());
                        resultStatus = stmt7.executeUpdate();
                    }
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                	closePreparedStatement(stmt7);
                    logger.info("Result of the adding query #7." + counter7 +" (new data type, documentation IDs): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            
            // documentation URL(s)
            if (null != data.getDocumentationURLs())
            {
                PreparedStatement stmt8 = null;
                int counter8 = 0;
                try
                {
                	stmt8 = openPreparedStatement("INSERT INTO cura_doc (uri, uri_type, ptr_type, ptr_datatype, ptr_resource) VALUES (?, 'URL', 'data', ?, NULL)");
                	for (int i=0; i<data.getDocumentationURLs().size(); ++i)
                	{
                		counter8 = i;
                        stmt8.setString(1, data.getDocumentationURL(i));
                        stmt8.setString(2, index);
                        logger.debug("SQL query: " + stmt8.toString());
                        resultStatus = stmt8.executeUpdate();
                    }
                }
                catch (SQLException e)
                {
                    logger.error("An exception occured during the processing of a prepared statement!");
                    logger.error("SQL Exception raised: " + e.getMessage());
                    resultStatus = 0;   // failure
                }
                finally
                {
                	closePreparedStatement(stmt8);
                    logger.info("Result of the adding query #8." + counter8 +" (new data type, documentation URLs): " + resultStatus);
                    resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
                }
            }
            
            // submission info
            PreparedStatement stmt9 = null;
            try
            {
            	stmt9 = openPreparedStatement("INSERT INTO cura_material (ptr_datatype, comment, state, sub_info, public_id) VALUES (?, ?, ?, ?, ?)");
                stmt9.setString(1, index);
                stmt9.setString(2, "New submission");
                stmt9.setString(3, "Submitted");
                stmt9.setString(4, subInfo);
                stmt9.setString(5, "");
                logger.debug("SQL query: " + stmt9.toString());
                resultStatus = stmt9.executeUpdate();
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the processing of a prepared statement!");
                logger.error("SQL Exception raised: " + e.getMessage());
                resultStatus = 0;   // failure
            }
            finally
            {
            	closePreparedStatement(stmt9);
                logger.info("Result of the adding query #9 (new data type, curation material): " + resultStatus);
                resultGlobal= resultGlobal * resultStatus;   // if the result is "1" (means success), the 'resultGlobal' will stay at the value "1" (means everything is OK)
            }
            
            // end of the transaction
            //pool.commit();   // CAN'T BE USED ANY MORE!
        }
        
        logger.debug("... end of the adding of a new data type in curation (GLOBAL RESULT: " + resultGlobal + ")");
        
        return resultGlobal;
    }
    
    
    /*
     * Generates an ID for a new data type or for a new data type or a resource (physical location).
     * Warning: the use of this method followed by a creation of a new entity needs to be protected by a 
     *          semaphore/lock to prevent any update of the database in the meantime.
     * 
     * @param database connection pool
     * @param type of ID to generate: for a data type ('DATATYPE') or a resource ('RESOURCE')
     */
    private String generateID(int type)
    {
    	Statement stmt = null;
        ResultSet rs = null;
        String id = new String();
        String sql = new String();
        int size;
        
        if (type == RESOURCE)
        {
            id = "MIR:001";
            sql = "SELECT resource_id FROM cura_resource";
        }
        if (type == CURA_DATATYPE)
        {
            id = "MIR:009";
            sql = "SELECT datatype_id FROM cura_datatype";
        }
        
        try
        {
			stmt = openStatement();
			rs = stmt.executeQuery(sql);
			size = MiriamUtilities.getRowCount(rs);
	        size += 1;
	        
	        if (size < 10)
	        {
	            id += "0000" + size;
	        }
	        else
	        {
	            if (size < 100)
	            {
	                id += "000" + size;
	            }
	            else
	            {
	                if (size < 1000)
	                {
	                    id += "00" + size;
	                }
	                else
	                {
	                    if (size < 10000)
	                    {
	                        id += "0" + size;
	                    }
	                    else
	                    {
	                        if (size < 100000)
	                        {
	                            id += size;
	                        }
	                        else
	                        {
	                            if (type == RESOURCE)
	                            {
	                                logger.error("Generation of the ID of the new resource impossible: size overflow!");
	                                id = "MIR:00100001";   // ID already existing: should generate a SQL error if tried to use like that...                             
	                            }
	                            if (type == CURA_DATATYPE)
	                            {
	                                logger.error("Generation of the ID of the new temporary data type impossible: size overflow!");
	                                id = "MIR:00900001";   // ID already existing: should generate a SQL error if tried to use like that...
	                            }
	                        } 
	                    }
	                } 
	            }
	        }
		}
        catch (SQLException e)
        {
			logger.debug("SQLException raised while generating a new collection identifier!");
			logger.debug(e.getMessage());
		}
        finally
        {
        	closeResultSet(rs);
        	closeStatement(stmt);
        }
        
        if (type == RESOURCE)
        {
            logger.info("ID generated for a new resource: " + id);
        }
        
        return id;
    }
    
    
    /**
     * Finds differences between two <code>DataTypeHibernate</code>.
     * 
     * <p>
     * In order to keep things clean, 'this' corresponds to the new data and 'data' to the old one.
     * 
     * @param data data type to compare the other one
     * @param otherData
     * @return all the differences founded
     */
    public String diff(CuraDataType data, CuraDataType otherData)
    {
        String diff = new String();
        
        // identifier
        if (! data.getId().equals(otherData.getId()))
        {
            diff += "Id:\n";
            diff += "\t< " + data.getId() + "\n";
            diff += "\t> " + otherData.getId() + "\n\n";
        }
        
        // name
        if (! data.getName().equals(otherData.getName()))
        {
            diff += "Name:\n";
            diff += "\t< " + data.getName() + "\n";
            diff += "\t> " + otherData.getName() + "\n\n";
        }
        
        // synonyms
        diff += "Synonyms:\n";
        for (int i=0; i<otherData.getSynonyms().size(); ++i)
        {
            if (data.getSynonyms().contains(otherData.getSynonym(i)))
            {
                // nothing: no change have been done on this synonym
            }
            else
            {
                // a synonym has been added
                diff += "\t> " + otherData.getSynonym(i) + "\n";
            }
        }
        for (int i=0; i<data.getSynonyms().size(); ++i)
        {
            if (otherData.getSynonyms().contains(data.getSynonym(i)))
            {
                // nothing: no change have been done on this synonym
            }
            else
            {
                // a synonym has been removed
                diff += "\t< " + data.getSynonym(i) + "\n";
            }
        }
        
        // URL
        if (! data.getURL().equals(otherData.getURL()))
        {
            diff += "Official URL:\n";
            diff += "\t< " + data.getURL() + "\n";
            diff += "\t> " + otherData.getURL() + "\n\n";
        }
        
        // URN
        if (! data.getURN().equals(otherData.getURN()))
        {
            diff += "MIRIAM URN:\n";
            diff += "\t< " + data.getURN() + "\n";
            diff += "\t> " + otherData.getURN() + "\n\n";
        }
        
        // deprecatedURIs
        diff += "Deprecated URIs:\n";
        for (int i=0; i<otherData.getDeprecatedURIs().size(); ++i)
        {
            if (data.getDeprecatedURIs().contains(otherData.getDeprecatedURI(i)))
            {
                // nothing: no change have been done on this deprecated URI
            }
            else
            {
                // a deprecated URI has been added
                diff += "\t> " + otherData.getDeprecatedURI(i) + "\n";
            }
        }
        for (int i=0; i<data.getDeprecatedURIs().size(); ++i)
        {
            if (otherData.getDeprecatedURIs().contains(data.getDeprecatedURI(i)))
            {
                // nothing: no change have been done on this deprecated URI
            }
            else
            {
                // a deprecated URI has been removed
                diff += "\t< " + data.getDeprecatedURI(i) + "\n";
            }
        }
        
        // definition
        if (! data.getDefinition().equals(otherData.getDefinition()))
        {
            diff += "Definition:\n";
            diff += "\t< " + data.getDefinition() + "\n";
            diff += "\t> " + otherData.getDefinition() + "\n\n";
        }
        
        // regexp
        if (! data.getRegexp().equals(otherData.getRegexp()))
        {
            diff += "Regular expression:\n";
            diff += "\t< " + data.getRegexp() + "\n";
            diff += "\t> " + otherData.getRegexp() + "\n\n";
        }
        
        // resources (two cases: identifiers available or not)
        diff += "Resources:\n";
        
        // checks if some resources have been added or have been modified: (data.getResources().contains(this.getResource(i)))
        for (int i=0; i<otherData.getResources().size(); ++i)
        {
            boolean find1 = false;
            boolean modif1 = false;
            ListIterator<Resource> it1 = data.getResources().listIterator();
            Resource tmp1 = null;
            while (it1.hasNext())
            {
                tmp1 = (Resource) it1.next();
                
                boolean test = false;
                //if (identifierAvailable)
                if ((! (tmp1.getId()).equalsIgnoreCase("null")) && (! otherData.getResource(i).getId().equalsIgnoreCase("null")))
                {
                    test = tmp1.equals(otherData.getResource(i));
                }
                else
                {
                    test = tmp1.couldBeSimilar(otherData.getResource(i));
                }
                
                if (test)
                {
                    find1 = true;
                    
                    if (! tmp1.hasSameContent(otherData.getResource(i)))
                    {
                        modif1 = true;
                    }
                    
                    break;
                }
            }
            
            if (find1)
            {
                // some changes have been done on this resource
                if (modif1)
                {
                    diff += "\t<< " + tmp1 + "\n";   // ld version of the resource
                    diff += "\t>> " + otherData.getResource(i) + "\n";   // new version of the resource
                }
            }
            else
            {
                // a resource has been added
                diff += "\t> " + otherData.getResource(i) + "\n";
            }
        }
        
        // checks if some resources have been deleted: (this.getResources().contains(data.getResource(i)))
        for (int i=0; i<data.getResources().size(); ++i)
        {
            boolean find2 = false;
            ListIterator<Resource> it2 = otherData.getResources().listIterator();
            Resource tmp2 = null;
            
            while (it2.hasNext())
            {
                tmp2 = (Resource) it2.next();
            
                boolean test = false;
                //if (identifierAvailable)
                if ((! (tmp2.getId()).equalsIgnoreCase("null")) && (! data.getResource(i).getId().equalsIgnoreCase("null")))
                {
                    test = tmp2.equals(data.getResource(i));
                }
                else
                {
                    test = tmp2.couldBeSimilar(data.getResource(i));
                }
                
                if (test)
                {
                    find2 = true;
                    break;
                }
            }
            
            if (! find2)
            {
                // a resource has been removed
                diff += "\t< " + data.getResource(i) + "\n";
            }
        }
        
        
        // documentationURLs
        diff += "URLs towards pieces of documentation:\n";
        for (int i=0; i<otherData.getDocumentationURLs().size(); ++i)
        {
            if (data.getDocumentationURLs().contains(otherData.getDocumentationURL(i)))
            {
                // nothing: no change have been done on this URL towards a piece of documentation
            }
            else
            {
                // an URL towards a piece of documentation has been added
                diff += "\t> " + otherData.getDocumentationURL(i) + "\n";
            }
        }
        for (int i=0; i<data.getDocumentationURLs().size(); ++i)
        {
            if (otherData.getDocumentationURLs().contains(data.getDocumentationURL(i)))
            {
                // nothing: no change have been done on this URL towards a piece of documentation
            }
            else
            {
                // an URL towards a piece of documentation has been removed
                diff += "\t< " + data.getDocumentationURL(i) + "\n";
            }
        }
        
        // documentationIDs
        diff += "IDs of pieces of documentation:\n";
        for (int i=0; i<otherData.getDocumentationIDs().size(); ++i)
        {
            if (data.getDocumentationIDs().contains(otherData.getDocumentationID(i)))
            {
                // nothing: no change have been done on this ID of a piece of documentation
            }
            else
            {
                // an ID of a piece of documentation has been added
                diff += "\t> " + otherData.getDocumentationID(i) + "\n";
            }
        }
        for (int i=0; i<data.getDocumentationIDs().size(); ++i)
        {
            if (otherData.getDocumentationIDs().contains(data.getDocumentationID(i)))
            {
                // nothing: no change have been done on this ID of a piece of documentation
            }
            else
            {
                // an ID of a piece of documentation has been removed
                diff += "\t< " + data.getDocumentationID(i) + "\n";
            }
        }
        
        return diff;
    }


    /**
     * Publishes a data collection from the curation pipeline to the public registry.
     * @param dataId identifier of a data collection in the curation pipeline (for example: 'MIR:00900002')
     * @return can be null (if an error happened during the process) otherwise returns the public identifier of the data collection
     */
    public String publish(String dataId)
    {
        String result = null;
        CuraDataType data = retrieve(dataId);
        DataTypeHibernate toPublish = copyDataTypeContent(data);
        
        logger.debug("Publication of: " + dataId);
        
        // publish the data collection
        int success = toPublish.storeObject(getPoolName());
        
        // everything is ok so far...
        if (success == 1)
        {
        	Statement stmt1 = null;
        	ResultSet rs1 = null;
            String id = null;
            // retrieves the public identifier of the newly public data collection
            try
            {
            	stmt1 = openStatement();
            	rs1 = stmt1.executeQuery("SELECT datatype_id FROM mir_datatype WHERE (name='" + toPublish.getName() + "')");
            	
                if (rs1.first())
                {
                    id = rs1.getString("datatype_id");
                }
                else
                {
                    logger.error("Can't retrieve the public identifier of a newly published data type: '" + toPublish.getName() + "'!");
                }
            }
            catch (SQLException e)
            {
                logger.error("An exception occured during the retrieval of the identifier of a newly published data type: '" + toPublish.getName() + "'!");
                logger.error("SQL Exception raised: " + e.getMessage());
            }
            finally
            {
            	closeResultSet(rs1);
            	closeStatement(stmt1);
            }
            
            // updates the state of the now published data collection in the curation pipeline and add a comment
            PreparedStatement stmt2 = null;
            try
            {
            	stmt2 = openPreparedStatement("UPDATE cura_material SET state='Published', public_id='" + id + "' WHERE (ptr_datatype='" + dataId + "')");
            	int resultStatus = stmt2.executeUpdate();
            	
	            if (resultStatus == 1)
	            {
	                result = id;
	            }
	            else
	            {
	                logger.error("An error occurred while updating the curation records of a newly published data type: " + dataId + "!");
	            }
            }
            catch (SQLException e)
            {
            	logger.error("SQL Exception raised while updating the state of a data collection in the curation pipeline!");
            	logger.error(e.getMessage());
            }
            finally
            {
            	closePreparedStatement(stmt2);
            }
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the state of a data type
     * @param dataId identifier of a data type in the curation pipeline (for example: 'MIR:00900002')
     * @return can be null
     */
    public String getState(String dataId)
    {
        String result = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        
        try
        {
        	stmt = openPreparedStatement("SELECT state FROM cura_material WHERE (ptr_datatype=?)");
            stmt.setString(1, dataId);
            logger.debug("SQL query: " + stmt.toString());
            rs = stmt.executeQuery();
            if (rs.first())
            {
                result = rs.getString("state");
            }
        }
        catch (SQLException e)
        {
            logger.error("An exception occured during the retrieval of the state of the data type: " + dataId + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	closeResultSet(rs);
        	closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Checks if a data type exists in the curation pipeline, based on its identifier.
     * @param dataId identifier of a data type in the curation pipeline (for example: 'MIR:00900002');
     * @return True or False
     */
    public boolean existsById(String dataId)
    {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        boolean exist = false;
        
        try
        {
        	stmt = openPreparedStatement("SELECT name FROM cura_datatype WHERE (datatype_id=?)");
            stmt.setString(1, dataId);
            logger.debug("SQL query: " + stmt.toString());
            rs = stmt.executeQuery();
            if (rs.first())
            {
                exist = true;
            }
        }
        catch (SQLException e)
        {
            logger.error("An exception occured during the check of existence of a data type in curation: " + dataId + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	closeResultSet(rs);
        	closePreparedStatement(stmt);
        }
        
        return exist;
    }
    
    
    /**
     * Retrieves all data types in the curation pipeline in a specific state.
     * <p>WARNING: no check on the state!
     *  
     * @param state state of a data type in the curation pipeline (submitted, curation, canceled, pending or published)
     * @return list of simple data types
     */
    public List<SimpleCuraDataType> retrieveWithState(String state)
    {
        List<SimpleCuraDataType> result = new ArrayList<SimpleCuraDataType>();
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT d.name, d.definition, d.datatype_id, m.state, d.date_creation, m.public_id FROM cura_datatype d, cura_material m WHERE ((d.datatype_id = m.ptr_datatype) AND (m.state = '" + state + "')) ORDER BY d.date_creation";
        boolean notEmpty;
        try
        {
        	stmt = openStatement();
        	rs = stmt.executeQuery(sql);
            notEmpty = rs.first();
            while (notEmpty)
            {
                SimpleCuraDataType temp = new SimpleCuraDataType();
                temp.setId(rs.getString("datatype_id"));
                temp.setName(rs.getString("name"));
                temp.setDefinition(StringEscapeUtils.escapeHtml4(rs.getString("definition")));
                temp.setState(rs.getString("state"));
                temp.setSubmissionDate(rs.getTimestamp("date_creation"));
                temp.setPublicId(rs.getString("public_id"));
                
                // adds this simple data type to the list
                result.add(temp);
                
                // next data type (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while searching the data types in the curated pipeline!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
        	closeResultSet(rs);
        	closeStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Copies the content of a <code>CuraDataType</code> object into a <code><DataTypeHibernate/code> object (in order to be able to publish it for example).
     * @param from 
     * @return the fully created <code>CuraDataType</code> object based on the content of <code>CuraDataType</code> object
     */
    private DataTypeHibernate copyDataTypeContent(CuraDataType from)
    {
        DataTypeHibernate result = new DataTypeHibernate();
        
        result.setId(from.getId());
        result.setName(from.getName());
        result.setNameURL(from.getNameURL());
        result.setSynonyms(from.getSynonyms());
        result.setURL(from.getURL());
        result.setURN(from.getURN());
        result.setDeprecatedURIs(from.getDeprecatedURIs());
        result.setDefinition(from.getDefinition());
        result.setRegexp(from.getRegexp());
        result.setResources(from.getResources());
        result.setDocumentationURLs(from.getDocumentationURLs());
        result.setDocumentationIDs(from.getDocumentationIDs());
        result.setDocumentationIDsType(from.getDocumentationIDsType());
        result.setDocHtmlURLs(from.getDocHtmlURLs());
        //result.setDateCreation();
        //result.setDateCreationStr();
        //result.setDateModification()
        //result.setDateModificationStr():
        result.setObsolete(from.isObsolete());
        result.setObsoleteComment(from.getObsoleteComment());
        result.setReplacedBy(from.getReplacedBy());
        result.setRestrictions(from.getRestrictions());
        
        return result;
    }
}
