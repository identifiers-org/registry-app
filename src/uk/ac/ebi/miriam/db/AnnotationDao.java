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


package uk.ac.ebi.miriam.db;


import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;


/**
 * <p>Servlet which handles the database access for examples of annotation.
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
 * @version 20100322
 */
public class AnnotationDao extends Dao
{
    private Logger logger = Logger.getLogger(AnnotationDao.class);
    
    
    /**
     * Constructor.
     * @param pool database pool
     */
    public AnnotationDao(String pool)
    {
        super(pool);
    }
    
    
    /**
     * Retrieves the tags belonging to a given format.
     * @param format such as SBML, BioPAX or CellML
     * @return list of tags (for XML formats) with their identifier
     */
    public List<AnnotationTag> getTagsByFormat(String format)
    {
        PreparedStatement stmt = null;
        List<AnnotationTag> result = new ArrayList<AnnotationTag>();
        String sql = "SELECT id, name, information, format FROM mir_annotation WHERE (format=?) ORDER BY name";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, format);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                AnnotationTag temp = new AnnotationTag();
                temp.setId(rs.getString("id"));
                temp.setName(rs.getString("name"));
                temp.setInfo(rs.getString("information"));
                temp.setFormat(rs.getString("format"));
                result.add(temp);
                
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the tags from the format: " + format);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the examples of annotation for a given data type.
     * @param dataTypeId identifier of a data type (for example: 'MIR:00000008')
     * @return
     */
    public List<Annotation> getAnnotationFromDataId(String dataTypeId)
    {
        Statement stmt = null;
        Annotation anno = null;
        List<Annotation> result = new ArrayList<Annotation>();
        String sql = "SELECT anno.id, anno.format, anno.name, anno.information FROM mir_annotation anno, mir_anno_link link WHERE ((link.ptr_datatype = '" + dataTypeId + "') AND (link.ptr_annotation = anno.id)) ORDER BY anno.format, anno.name";
        
        boolean notEmpty;
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            String format = new String();
            notEmpty = rs.next();
            
            while (notEmpty)
            {
                
                Tag temp = new Tag();
                format = rs.getString("anno.format");
                temp.setId(rs.getString("anno.id"));
                temp.setName(rs.getString("anno.name"));
                temp.setInfo(rs.getString("anno.information"));
                
                // an annotation with the same format already exists
                if ((anno != null) && (anno.getFormat().equals(format)))
                {
                    // adds the new tag
                    anno.addTag(temp);
                }
                else
                {
                    // no annotation created so far
                    if (anno == null)
                    {
                        // creation of an Annotation
                        anno = new Annotation(format);
                        
                        // adds the new tag
                        anno.addTag(temp);
                    }
                    else   // an annotation has already been created 
                    {
                        // adds the previous annotation to list
                        result.add(anno);
                        
                        // creation of a new Annotation (new format)
                        anno = new Annotation(format);
                        
                        // adds the new tag
                        anno.addTag(temp);
                    }
                }
                // next resource (if it exists)
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while searching the annotation!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        // adds the last annotation to the list
        if (anno != null)
        {
            result.add(anno);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the available formats for examples of annotations.
     * @return list of formats (SBML, CellML, BioPAX, ...)
     */
    public List<String> getAvailableFormats()
    {
        Statement stmt = null;
        List<String> result = new ArrayList<String>();
        String sql = "SELECT DISTINCT format FROM mir_annotation ORDER BY format DESC";
        
        boolean notEmpty;
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            notEmpty = rs.next();
            while (notEmpty)
            {
                result.add(rs.getString("format"));
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving the available formats for examples of annotation!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Retrieves the list of data types (<code>SimpleDataType</code>) which can be annotated by the given annotation.
     * @param id identifier of an example of annotation, for example "MIR:00500001"
     * @return
     */
    public List<SimpleDataType> getDataFromUsageAnno(String id)
    {
        PreparedStatement stmt = null;
        List<SimpleDataType> data = new ArrayList<SimpleDataType>();
        String sql = "SELECT data.datatype_id, data.name, data.definition FROM mir_anno_link link, mir_datatype data WHERE ((link.ptr_datatype = data.datatype_id) AND (link.ptr_annotation = ?)) ORDER BY data.name";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, id);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            boolean notEmpty = rs.next();
            while (notEmpty)
            {
                SimpleDataType temp = new SimpleDataType();
                temp.setId(rs.getString("datatype_id"));
                temp.setName(rs.getString("name"));
                temp.setDefinition(rs.getString("definition"));
                temp.setUri("");   // we don't retrieve the URI: it should not be needed
                data.add(temp);
                
                notEmpty = rs.next();
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the retrieval of the data types which can be linked to the annotation: " + id);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return data;
    }


    /**
     * Checks if an example of annotation exists in the database.
     * @param tagId identifier of a tag (for example: 'MIR:00500009')
     * @return True or False
     */
    public boolean exists(String tagId)
    {
        PreparedStatement stmt = null;
        boolean result = false;   // default value
        String sql = "SELECT name FROM mir_annotation WHERE (id=?)";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, tagId);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())
            {
                result = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the existence test of the following annotation: " + tagId);
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return result;
    }
    
    
    /**
     * Checks if an association between a data type and a annotation tag exists.
     * @param dataId identifier of a data type (for example: 'MIR:00000022')
     * @param tagId identifier of a annotation tag (for example: 'MIR:00500009')
     * @return
     */
    public boolean annotationExists(String dataId, String tagId)
    {
        PreparedStatement stmt = null;
        boolean existing = false;
        String sql = "SELECT id FROM mir_anno_link WHERE ((ptr_annotation=?) AND (ptr_datatype=?))";
        
        try
        {
            stmt = openPreparedStatement(sql);
            stmt.setString(1, tagId);
            stmt.setString(2, dataId);
            logger.debug("SQL prepared query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.first())
            {
                existing = true;
            }
        }
        catch (SQLException e)
        {
            logger.warn("An exception occurred during the existence test of an association between an example of annotation (" + tagId + ") and a data type (" + dataId + ")!");
            logger.warn("SQLException raised: " + e.getMessage());
        }
        finally
        {
            closePreparedStatement(stmt);
        }
        
        return existing;
    }
    
    
    /**
     * Adds a new association between a data type and a tag (example of annotation)
     * @param dataId identifier of a data type (for example: 'MIR:00000022')
     * @param tagId identifier of a annotation tag (for example: 'MIR:00500009')
     * @return true if the addition is a success, false otherwise
     */
    public boolean addAnnotation(String dataId, String tagId)
    {
        PreparedStatement stmt = null;
        boolean state = false;
        boolean existing = false;
        
        // checks if the association doesn't exist already
        existing = annotationExists(dataId, tagId);
        
        // creates the new association
        if (! existing)
        {
            String sql = "INSERT INTO mir_anno_link (ptr_annotation, ptr_datatype) VALUES (?, ?)";
            try
            {
                stmt = openPreparedStatement(sql);
                stmt.setString(1, tagId);
                stmt.setString(2, dataId);
                logger.debug("SQL prepared query: " + stmt.toString());
                stmt.executeUpdate();
                stmt.close();
                state = true;
            }
            catch (SQLException e)
            {
                logger.warn("An exception occurred during the creation an association between an example of annotation (" + tagId + ") and a data type (" + dataId + ")!");
                logger.warn("SQLException raised: " + e.getMessage());
            }
            finally
            {
                closePreparedStatement(stmt);
            }
        }
        
        return state;
    }


    /**
     * Retrieves an example of annotation given its identifier.
     * @param tagId identifier of an example of annotation (for example: 'MIR:00500009')
     * @return can be null
     */
    public AnnotationTag getAnnoFromId(String tagId)
    {
        Statement stmt = null;
        AnnotationTag result = null;
        String sql = "SELECT id, name, information, format FROM mir_annotation WHERE (id='" + tagId + "')";
        
        try
        {
            stmt = openStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.first())            
            {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String info = rs.getString("information");
                String format = rs.getString("format");
                result = new AnnotationTag(id, name, info, format);
            }
        }
        catch (SQLException e)
        {
            logger.error("Error while retrieving an example of annotation: " + tagId + "!");
            logger.error("SQL Exception raised: " + e.getMessage());
        }
        finally
        {
            closeStatement(stmt);
        }
        
        return result;
    }
}
