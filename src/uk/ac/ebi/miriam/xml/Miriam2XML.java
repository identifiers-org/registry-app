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


package uk.ac.ebi.miriam.xml;


import uk.ac.ebi.miriam.db.ResourceDao;
import uk.ac.ebi.miriam.db.URI;
import uk.ac.ebi.miriam.db.DbPoolConnect;
import uk.ac.ebi.compneur.util.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import java.text.SimpleDateFormat;
import java.util.TimeZone;


/**
 * <p>Manages the XML export of the whole MIRIAM Registry (like the old 'Resource.xml').
 *
 * <p>
 * Uses the database pool created by the MIRIAM Web App.
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
 * @version 20130808
 */
public class Miriam2XML
{
    private Logger logger = Logger.getLogger(Miriam2XML.class);
    private PrintWriter file = null;
    private String fileName = null;
    private String poolName = null;
    private DbPoolConnect pool;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");



    /**
     * Constructor
     * @param poolName name of the database pool
     * @param fileName name of the output file
     */
    public Miriam2XML(String poolName, String fileName)
    {
        logger.debug("Creation of a 'Miriam2XML' object...");
        this.fileName = fileName;
        this.poolName = poolName;
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    
    
    /**
     * Official destructor.
     */
    public void finalize()
    {
        // nothing here.
    }
    
    
    /**
     * Returns a string representation of this object.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if (null != this.file)
        {
            return this.file.toString();
        }
        else
        {
            return null;
        }
    }
    
    
    /**
     * Exports the database in an output file.
     * If a 'project' is specified (not null), a customised export will be generated.
     * @return whether the export is a success or not
     */
    public boolean export(String project)
    {
        if (null != project)
        {
            logger.debug("Request for a customised XML export of the database (project: " + project + ").");
        }
        else
        {
            logger.debug("Request for an XML export of the database.");
        }
        
        pool = new DbPoolConnect(poolName);
        
        // open the output file
        if (openFile() == null)
        {
            logger.fatal("Impossible to proceed the XML export!");
            return false;
        }
        
        // connection to the database (via a pool)
        // test without 'newConnection()' before, let's see...
        pool.getConnection();
        
        // retrieves the newest date of last modification
        Date lastModif = null;
        String query = "SELECT date_modif FROM mir_datatype WHERE 1 ORDER BY date_modif DESC LIMIT 1";
        ResultSet sqlResult = pool.request(pool.getStatement(), query);
        try
        {
            sqlResult.next();
            lastModif = sqlResult.getTimestamp("date_modif");
            sqlResult.close();
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the newest date of last modification!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }


        // header of the file
        beginFile(file, dateFormat.format(new Date()), dateFormat.format(lastModif), project);
        
        // database dump
        if (dump(project) == false)
        {
            logger.fatal("An error occurred during the XML export!");
            return false;
        }
        
        // list of tags and there definition
        if (dumpTags() == false)
        {
            logger.fatal("An error occurred during the XML export!");
            return false;
        }
        
        // footer of the file
        endFile(file);
        
        // close the file
        file.flush();
        file.close();

        // without closing the statement, let's see...
        pool.closeConnection();

        // logging message
        logger.info("The XML export is a success!");

        return true;
    }


    /*
     * Opens the file
     * @return the PrintWriter object
     */
    private PrintWriter openFile()
    {
        try
        {
            file = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        }
        catch (IOException e)
        {
            logger.error("The output file cannot be opened!");
            logger.error("IO Exception raised: " + e.getMessage());
        }

        logger.info("Output file used: '" + fileName + "'");

        return file;
    }


    /*
     * Reads the content of the database and writes it in the output file.
     * If 'project' is not null, this will generate a customised export.
     * @return a boolean in order to know if there was a problem or not
     */
    private boolean dump(String project)
    {
        String id = new String();
        String tmp = new String();
        ResultSet sqlResult = null;
        String originalQuery = null; 
        
        if (null != project)
        {
            originalQuery = "SELECT * FROM mir_datatype d, mir_profiles p, mir_my_miriam m WHERE ((p.shortname = '" + project + "') AND (p.id = m.ptr_my_project) AND (m.ptr_datatype = d.datatype_id))";
        }
        else
        {
            originalQuery = "SELECT * FROM mir_datatype d";
        }

        // execute the query
        sqlResult = pool.request(pool.getStatement(), originalQuery);

        try
        {
            boolean notEmpty = sqlResult.first();
            
            // for each data type, search all the needed information
            while (notEmpty)
            {
                String preferredResourceId = null;   // preferred resource for the current data collection (if customised export requested)
                // new resource
                id = sqlResult.getString("d.datatype_id");
                boolean obsolete = sqlResult.getBoolean("d.obsolete");
                boolean restricted = sqlResult.getBoolean("d.restriction");
                // customised export requested
                if (null != project)
                {
                    preferredResourceId = sqlResult.getString("m.ptr_preferred_resource");
                }
                // the data type is obsolete
                if (obsolete)
                {
                    if (restricted)
                    {
                        file.println("\t<datatype id=\"" + id + "\" pattern=\"" + StringEscapeUtils.escapeXml(sqlResult.getString("d.pattern")) + "\" restricted=\"true\" obsolete=\"" + obsolete + "\" replacement=\"" + sqlResult.getString("d.replacement") + "\">");
                    }
                    else   // no restriction for the data collection
                    {
                        file.println("\t<datatype id=\"" + id + "\" pattern=\"" + StringEscapeUtils.escapeXml(sqlResult.getString("d.pattern")) + "\" obsolete=\"" + obsolete + "\" replacement=\"" + sqlResult.getString("d.replacement") + "\">");
                    }
                    file.println("\t\t<comment>" + StringEscapeUtils.escapeXml(sqlResult.getString("d.obsolete_comment")) + "</comment>");
                }
                else   // the data type is still valid
                {
                    if (restricted)
                    {
                        file.println("\t<datatype id=\"" + id + "\" pattern=\"" + StringEscapeUtils.escapeXml(sqlResult.getString("d.pattern")) + "\" restricted=\"true\">");
                    }
                    else   // no restriction for the data collection
                    {
                        file.println("\t<datatype id=\"" + id + "\" pattern=\"" + StringEscapeUtils.escapeXml(sqlResult.getString("d.pattern")) + "\">");
                    }
                }

                // primary name
                file.println("\t\t<name>" + StringEscapeUtils.escapeXml(sqlResult.getString("d.name")) + "</name>");

                // searching for synonym(s)
                Statement stmt2 = null;
                ResultSet sqlResult2 = null;
                boolean notEmpty2;
                String query2 = "SELECT * FROM mir_synonym WHERE (ptr_datatype = '" + id + "')";
                // new statement
                stmt2 = pool.getStatement();
                // query
                sqlResult2 = pool.request(stmt2, query2);
                int i2 = DbPoolConnect.getRowCount(sqlResult2);
                // there is at least one synonym stored for this data collection: beginning of the list
                if (i2 > 0)
                {
                    file.println("\t\t<synonyms>");
                    
                    try
                    {
                        notEmpty2 = sqlResult2.first();
                        while (notEmpty2)
                        {
                            file.println("\t\t\t<synonym>" + StringEscapeUtils.escapeXml(sqlResult2.getString("name")) + "</synonym>");
                            notEmpty2 = sqlResult2.next();
                        }
                    }
                    catch (SQLException e1)
                    {
                        logger.error("Error while searching the synonyms!");
                        logger.error("SQL Exception raised: " + e1.getMessage());
                    }
                    
                    // end of the list of synonyms
                    file.println("\t\t</synonyms>");
                }
                
                // searching for the definition
                if (sqlResult.getString("definition") != null)
                {
                    file.println("\t\t<definition>" + StringEscapeUtils.escapeXml(sqlResult.getString("definition")) + "</definition>");
                }
                
                // searching for URI(s)
                String namespace = null;
                String query3 = "SELECT * FROM mir_uri WHERE (ptr_datatype = '" + id + "')";
                sqlResult2 = pool.request(stmt2, query3);
                int i3 = DbPoolConnect.getRowCount(sqlResult2);
                // beginning of the list of URIs (there should be one, at least!)
                if (i3 > 0)
                {
                    file.println("\t\t<uris>");
                    
                    // retrieves URIs from the database
                    List<URI> uris = new ArrayList<URI>();
                    List<URI> deprecated = new ArrayList<URI>();
                    List<URI> official = new ArrayList<URI>();
                    try
                    {
                        notEmpty2 = sqlResult2.first();
                        while (notEmpty2)
                        {
                            if (sqlResult2.getString("deprecated").equals("0"))
                            {
                                official.add(new URI(StringEscapeUtils.escapeXml(sqlResult2.getString("uri")), sqlResult2.getString("uri_type")));
                            }
                            else if (sqlResult2.getString("deprecated").equals("1"))
                            {
                                deprecated.add(new URI(StringEscapeUtils.escapeXml(sqlResult2.getString("uri")), sqlResult2.getString("uri_type")));
                            }
                            else if (sqlResult2.getString("deprecated").equals("2"))
                            {
                                uris.add(new URI(StringEscapeUtils.escapeXml(sqlResult2.getString("uri")), sqlResult2.getString("uri_type")));
                            }
                            
                            notEmpty2 = sqlResult2.next();
                        }
                    }
                    catch (SQLException e1)
                    {
                        logger.error("Error while computing the synonyms!");
                        logger.error("SQL Exception raised: " + e1.getMessage());
                    }
                    
                    // official URIs (there should only be one non deprecated URN per data collection)
                    for (URI uri: official)
                    {
                        if(uri.getType() == URI.URItype.URN) {
                            file.println("\t\t\t<uri type=\"" + uri.getType() + "\">" + uri.getValue() + "</uri>");
                            namespace = uri.getValue();   // there should be only one, otherwise we take the last namespace
                        }
                    }
                    // removes the useless URN parts
                    namespace = namespace.substring(namespace.lastIndexOf(":")+1);
                    // Identifiers.org URL
                    file.println("\t\t\t<uri type=\"URL\">http://identifiers.org/" + namespace + "/</uri>");

                    //other
                    for (URI uri: uris)
                    {
                        file.println("\t\t\t<uri type=\"" + uri.getType() + "\" deprecated=\"true\">" + uri.getValue() + "</uri>");
                    }

                    // deprecated URI(s)
                    for (URI uri: deprecated)
                    {
                        file.println("\t\t\t<uri type=\"" + uri.getType() + "\" deprecated=\"true\">" + uri.getValue() + "</uri>");
                    }

                    
                    // end of the list of URIs
                    file.println("\t\t</uris>");
                }
                
                // namespace
                if ((null != namespace) && (!namespace.matches("\\s*")))
                {
                    file.println("\t\t<namespace>" + namespace + "</namespace>");
                }

                // searching for pieces of documentation(s) related to the data collection
                String query4 = "SELECT * FROM mir_doc WHERE (ptr_type = 'data' AND ptr_datatype = '" + id + "')";
                sqlResult2 = pool.request(stmt2, query4);
                
                int i4 = DbPoolConnect.getRowCount(sqlResult2);
                // beginning of the list of documentations (if any stored for the current data collection)
                if (i4 > 0)
                {
                    file.println("\t\t<documentations>");
                    
                    try
                    {
                        notEmpty2 = sqlResult2.first();
                        while (notEmpty2)
                        {
                            file.println("\t\t\t<documentation type=\"" + sqlResult2.getString("uri_type") + "\">" + StringEscapeUtils.escapeXml(sqlResult2.getString("uri")) + "</documentation>");
                            notEmpty2 = sqlResult2.next();
                        }
                    }
                    catch (SQLException e1)
                    {
                        logger.error("Error while searching the documentations of the current resource!");
                        logger.error("SQL Exception raised: " + e1.getMessage());
                    }
                    
                    // end of the list of documentations (if any stored for the current data type)
                    file.println("\t\t</documentations>");
                }

                // searching the resources (physical locationS)
                String query5 = "SELECT resource_id, url_element_prefix, url_element_suffix, url_resource, info, institution, location, `example`, obsolete, official, ptr_datatype FROM mir_resource WHERE (ptr_datatype = '" + id + "') ORDER BY obsolete";
                sqlResult2 = pool.request(stmt2, query5);
                Statement stmt4 = null;
                ResultSet sqlResult4 = null;
                
                // beginning of the list of resources for the current data collection
                file.println("\t\t<resources>");
                try
                {
                    notEmpty2 = sqlResult2.next();
                    while (notEmpty2)
                    {
                        String resourceId = sqlResult2.getString("resource_id");
                        file.print("\t\t\t<resource id=\"" + resourceId + "\"");
                        
                        // resource obsolete
                        if ((sqlResult2.getString("obsolete")).equalsIgnoreCase("1"))
                        {
                        	file.print(" obsolete=\"true\"");
                        }
                        else  // resource not obsolete
                        {
                            stmt4 = pool.getStatement();
                            sqlResult4 = pool.request(stmt4, "SELECT uptime, downtime, unknown, state FROM mir_url_check WHERE (resource_id = '" + resourceId + "')");
                            if (sqlResult4.first())
                            {
                                int state = sqlResult4.getInt("state");
                                int uptime = sqlResult4.getInt("uptime");
                                int downtime = sqlResult4.getInt("downtime");
                                
                                int workDays = uptime + downtime;   // 'unknown' not part of the uptime percent computation
                                int reliability = 0;
                                if (workDays > 0)
                                {
                                    reliability = (uptime * 100 / workDays);
                                }
                                file.print(" state=\"" + ResourceDao.getStateDesc(state) + "\" reliability=\"" + reliability + "\"");
                            }
                            sqlResult4.close();
                            stmt4.close();
                        }
                        // preferred resource
                        if ((null != project) && (preferredResourceId.equals(resourceId)))
                        {
                            file.print(" preferred=\"true\"");
                        }
                        // primary resource
                        if (sqlResult2.getInt("official") > 0)
                        {
                        	file.print(" primary=\"true\"");
                        }
                        file.println(">");
                        
                        file.println("\t\t\t\t<dataResource>" + StringEscapeUtils.escapeXml(sqlResult2.getString("url_resource")) + "</dataResource>");
                        tmp = StringEscapeUtils.escapeXml(sqlResult2.getString("url_element_prefix")) + "$id" + StringEscapeUtils.escapeXml(sqlResult2.getString("url_element_suffix"));   // for valid URLs
                        if (! sqlResult2.getString("example").equalsIgnoreCase(""))
                        {
                            file.println("\t\t\t\t<dataEntityExample>" + StringEscapeUtils.escapeXml(sqlResult2.getString("example")) + "</dataEntityExample>");
                        }
                        file.println("\t\t\t\t<dataEntry>" + tmp + "</dataEntry>");
                        file.println("\t\t\t\t<dataInfo>" + StringEscapeUtils.escapeXml(sqlResult2.getString("info")) + "</dataInfo>");
                        file.println("\t\t\t\t<dataInstitution>" + StringEscapeUtils.escapeXml(sqlResult2.getString("institution")) + "</dataInstitution>");
                        file.println("\t\t\t\t<dataLocation>" + StringEscapeUtils.escapeXml(sqlResult2.getString("location")) + "</dataLocation>");

                        // searching the documentation(s) related to the current resource
                        Statement stmt3 = null;
                        ResultSet sqlResult3 = null;
                        boolean notEmpty3;
                        String query6 = "SELECT * FROM mir_doc WHERE (ptr_type = 'loc' AND ptr_resource = '" + resourceId + "')";

                        stmt3 = pool.getStatement();
                        sqlResult3 = pool.request(stmt3, query6);
                        
                        int i6 = DbPoolConnect.getRowCount(sqlResult3);
                        // beginning of the list of documentations stored for the current resource (if any)
                        if (i6 > 0)
                        {
                            file.println("\t\t\t<documentations>");
                            
                            try
                            {
                                notEmpty3 = sqlResult3.next();
                                while (notEmpty3)
                                {
                                    file.println("\t\t\t\t<documentation type=\"" + sqlResult3.getString("uri_type") + "\">" + StringEscapeUtils.escapeXml(sqlResult3.getString("uri")) + "</documentation>");
                                    notEmpty3 = sqlResult3.next();
                                }
                                sqlResult3.close();
                                stmt3.close();
                            }
                            catch (SQLException e2)
                            {
                                logger.error("Error while searching the documentations of the current resource!");
                                logger.error("SQL Exception raised: " + e2.getMessage());
                            }
                            
                            // end of the documentation part for the current resource
                            file.println("\t\t\t</documentations>");
                        }

                        // end of the current resource
                        file.println("\t\t\t</resource>");
                        notEmpty2 = sqlResult2.next();
                    }
                }
                catch (SQLException e1)
                {
                    logger.error("Error while searching the resources!");
                    logger.error("SQL Exception raised: " + e1.getMessage());
                }
                
                // end of all the resources for the current data type
                file.println("\t\t</resources>");
                
                // displays any restriction(s), if any
                if (restricted)
                {
                    String query9 = "SELECT * FROM mir_restriction r, mir_restriction_type t WHERE ((r.ptr_datatype = '" + id + "') AND (r.ptr_restriction = t.id))";
                    sqlResult2 = pool.request(stmt2, query9);
                    int i9 = DbPoolConnect.getRowCount(sqlResult2);
                    if (i9 > 0)
                    {
                        file.println("\t\t<restrictions>");
                        try
                        {
                            notEmpty = sqlResult2.first();
                            while (notEmpty)
                            {
                                file.println("\t\t\t<restriction type=\"" + sqlResult2.getInt("t.id") + "\" desc=\"" + StringEscapeUtils.escapeXml(sqlResult2.getString("t.short_desc")) + "\">");
                                if ((null != sqlResult2.getString("r.desc")) && (!sqlResult2.getString("r.desc").matches("\\s*")))
                                {
                                    file.println("\t\t\t\t<statement>" + StringEscapeUtils.escapeXml(sqlResult2.getString("r.desc")) + "</statement>");
                                }
                                if ((null != sqlResult2.getString("r.link")) && (!sqlResult2.getString("r.link").matches("\\s*")))
                                {
                                    if ((null != sqlResult2.getString("r.link_text")) && (!sqlResult2.getString("r.link_text").matches("\\s*")))
                                    {
                                        file.println("\t\t\t\t<link desc=\"" + StringEscapeUtils.escapeXml(sqlResult2.getString("r.link_text")) + "\">" + StringEscapeUtils.escapeXml(sqlResult2.getString("r.link")) + "</link>");
                                    }
                                    else
                                    {
                                        file.println("\t\t\t\t<link>" + StringEscapeUtils.escapeXml(sqlResult2.getString("r.link")) + "</link>");
                                    }
                                }
                                file.println("\t\t\t</restriction>");
                                notEmpty = sqlResult2.next();
                            }
                        }
                        catch (SQLException e1)
                        {
                            logger.error("Error while searching the restrictions!");
                            logger.error("SQL Exception raised: " + e1.getMessage());
                        }
                        file.println("\t\t</restrictions>");
                    }
                    else   // there should be some restrictions stored somewhere...
                    {
                        logger.warn("No restrictions found for '" + id + "' when generating the XML export!");
                    }
                }
                
                // tags
                String query7 = "SELECT t.tag FROM mir_tag t, mir_tag_link l WHERE ((l.ptr_datatype = '" + id + "') AND (t.id = l.ptr_tag)) ORDER BY t.tag";
                sqlResult2 = pool.request(stmt2, query7);
                int i7 = DbPoolConnect.getRowCount(sqlResult2);
                if (i7 > 0)
                {
                    file.println("\t\t<tags>");
                    try
                    {
                        notEmpty = sqlResult2.first();
                        while (notEmpty)
                        {
                            file.println("\t\t\t<tag>" + StringEscapeUtils.escapeXml(sqlResult2.getString("t.tag")) + "</tag>");
                            notEmpty = sqlResult2.next();
                        }
                    }
                    catch (SQLException e1)
                    {
                        logger.error("Error while searching the tags!");
                        logger.error("SQL Exception raised: " + e1.getMessage());
                    }
                    file.println("\t\t</tags>");
                }
                
                // examples of annotation in various formats
                String query8 = "SELECT anno.id, anno.format, anno.name, anno.information FROM mir_annotation anno, mir_anno_link link WHERE ((link.ptr_datatype = '" + id + "') AND (link.ptr_annotation = anno.id)) ORDER BY anno.format";
                sqlResult2 = pool.request(stmt2, query8);
                int i8 = DbPoolConnect.getRowCount(sqlResult2);
                
                // beginning of the annotation part for the current data type (if any)
                if (i8 > 0)
                {
                    file.println("\t\t<annotation>");
                
                    try
                    {
                        String format = "";
                        String currentFormat;
                            
                        notEmpty2 = sqlResult2.first();
                        while (notEmpty2)
                        {
                            currentFormat = sqlResult2.getString("anno.format");
                            
                            // this the first format
                            if (format.equals(""))
                            {
                                file.println("\t\t\t<format name=\"" + currentFormat + "\">");
                                file.println("\t\t\t\t<elements>");
                                format = currentFormat;
                            }
                            
                            // new format (each tag is nested in a 'format' parent tag)
                            if (! format.equals(currentFormat))
                            {
                                format = currentFormat;
                                file.println("\t\t\t\t</elements>");
                                file.println("\t\t\t</format>");
                                file.println("\t\t\t<format name=\"" + currentFormat + "\">");
                                file.println("\t\t\t\t<elements>");
                            }
                            
                            file.println("\t\t\t\t\t<element>" + sqlResult2.getString("anno.name") + "</element>");
                            notEmpty2 = sqlResult2.next();
                        }
                    }
                    catch (SQLException e1)
                    {
                        logger.error("Error while searching the annotation!");
                        logger.error("SQL Exception raised: " + e1.getMessage());
                    }
                
                    // end of the annotation part for the current data type
                    file.println("\t\t\t\t</elements>");
                    file.println("\t\t\t</format>");
                    file.println("\t\t</annotation>");
                }
                
                sqlResult2.close();
                stmt2.close();

                // end of a resource
                file.println("\t</datatype>");
                file.println("");
                file.flush();
                notEmpty = sqlResult.next();
            }
            sqlResult.close();
        }
        catch (SQLException e)
        {
            logger.error("Error while computing the resources!");
            logger.error("SQL Exception raised: " + e.getMessage());
            return false;
        }

        // close all the statements
        pool.closeStatements();

        return true;
    }
    
    /*
     * Fetches from the database the list of all used tags and writes them (with their definition) in the file.
     * @return a boolean in order to know if there was a problem or not
     */
    private boolean dumpTags()
    {
        Boolean endRequired = false;
        ResultSet sqlResult = null;
        String query = "SELECT DISTINCT t.tag, t.info FROM mir_tag t, mir_tag_link l WHERE (t.id = l.ptr_tag) ORDER BY t.tag ASC";
        
        // execute the query
        sqlResult = pool.request(pool.getStatement(), query);
        
        try
        {
            boolean notEmpty = sqlResult.first();
            
            // beginning of the list of tags
            if (notEmpty)
            {
                file.println("\t<listOfTags>");
                endRequired = true;
            }
            
            // for each tag, search all the needed information (name + definition)
            while (notEmpty)
            {
                file.println("\t\t<tagDefinition>");
                file.println("\t\t\t<name>" + StringEscapeUtils.escapeXml(sqlResult.getString("tag")) + "</name>");
                file.println("\t\t\t<definition>" + StringEscapeUtils.escapeXml(sqlResult.getString("info")) + "</definition>");
                file.println("\t\t</tagDefinition>");
                
                notEmpty = sqlResult.next();
            }
            
            // end of the list of tags
            if (endRequired)
            {
                file.println("\t</listOfTags>");
            }
            
            sqlResult.close();
        }
        catch (SQLException e)
        {
            logger.error("Error while fetching the list of used tags!");
            logger.error("SQL Exception raised: " + e.getMessage());
            return false;
        }
        
        // close all the statements
        pool.closeStatements();
        
        return true;
    }


    /*
     * Creates the header of the XML file.
     * If 'project' is not null, mentions that this is a customised export.
     */
    private void beginFile(PrintWriter file, String now, String lastModif, String project)
    {

        // writing
        file.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        file.println("");   // empty line
        if (null != project)
        {
            file.println("<!-- Customised (" + project + ") export of Identifiers.org's Registry -->");
            file.println("<!-- Generated: " + dateFormat.format(new Date()) + "  " + spaces(project.length()) + "-->");
            file.println("<!-- http://identifiers.org/registry/         " + spaces(project.length()) + "-->");
        }
        else
        {
            file.println("<!-- Export of Identifiers.org's Registry (full) -->");
            file.println("<!-- Generated: " + dateFormat.format(new Date()) + "     -->");
            file.println("<!-- http://identifiers.org/registry/            -->");
        }
        file.println(""); // empty line
        file.println("<miriam xmlns=\"http://www.biomodels.net/MIRIAM/\" date=\"" + now + "\" data-version=\"" + lastModif + "\">");
        file.flush();
    }


    /*
     * Creates the footer of the XML file
     */
    private void endFile(PrintWriter file)
    {
        file.println("</miriam>");
        file.flush();
    }
    
    
    /*
     * Creates a String containing only white space(s) of the length provided in parameter. 
     */
    private String spaces(int length)
    {
        StringBuilder temp = new StringBuilder();
        for (int i=0; i<length; ++i)
        {
            temp.append(" ");
        }
        
        return temp.toString();
    }
}

