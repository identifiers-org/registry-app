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


package uk.ac.ebi.miriam.web;


import uk.ac.ebi.miriam.db.Annotation;
import uk.ac.ebi.miriam.db.AnnotationDao;
import uk.ac.ebi.miriam.db.AnnotationTag;
import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.SimpleDataType;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * <p>Servlet which Handles the queries to the database for browsing the examples of usage (annotation part) of a data collection.
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
 * @version 20140311
 */
public class ServletAnnotationDisplay extends ServletTemplate
{
    private static final long serialVersionUID = 728527662440668395L;
    
    
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response, StringBuilder page)
    {
        boolean exist = false;
        AnnoDisplay result = new AnnoDisplay();
        String name = null;
        
        // retrieves the parameter (identifier of a data type)
        String id = request.getParameter("data");
        result.setId(id);
        
        DataTypeDao dao = new DataTypeDao(getPoolName());
        
        // tests if there is an existing data type with this name
        if (id != null)
        {
            if (id.matches("MIR:000\\d{5}"))   // possible data type identifier
            {
                exist = dao.dataTypeExists(id);
                name = dao.getDataTypeName(id);
                
                if (exist)
                {
                    AnnotationDao annoDao = new AnnotationDao(getPoolName());
                    List<Annotation> data = annoDao.getAnnotationFromDataId(id);
                    
/**
     Statement stmt = null;
     List<Annotation> data = new ArrayList<Annotation>();
     Annotation anno = null;
     
                    try
                    {
                        stmt = this.connection.createStatement();
                        String sql = "SELECT anno.id, anno.format, anno.name, anno.information FROM mir_annotation anno, mir_anno_link link WHERE ((link.ptr_datatype = '" + id + "') AND (link.ptr_annotation = anno.id)) ORDER BY anno.format, anno.name";
                        ResultSet rs = stmt.executeQuery(sql);
                        boolean notEmpty;
                        
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
                                    data.add(anno);
                                    
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
                        logger.error("Error while retrieving the example annotation for: " + id + "!");
                        logger.error("SQL Exception raised: " + e.getMessage());
                    }
                    finally
                    {
                        closeStatement(stmt);
                    }
                    
                    // adds the last annotation to the list
                    if (anno != null)
                    {
                        data.add(anno);
                    }
                    
                    // add the list of annotation to the resulting object
                    result.setAnnotation(data);
*/
                    
                    // checks if the data type is obsolete (to add a warning to the user in this case)
                    HashMap<String, String> obsoleteInfo = dao.getObsoleteInfo(id);
                    if (null != obsoleteInfo)
                    {
                        request.setAttribute("obsolete", true);
                        request.setAttribute("replacementName", obsoleteInfo.get("replacementName"));
                        if ((null != obsoleteInfo.get("replacementId")) && (!obsoleteInfo.get("replacementId").isEmpty()))
                        {
                            request.setAttribute("replacementId", obsoleteInfo.get("replacementId"));
                        }
                        else
                        {
                            request.setAttribute("replacementId", null);
                        }
                        request.setAttribute("replacementComment", obsoleteInfo.get("replacementComment"));
                    }
                    
                    // cleaning
                    annoDao.clean();
                    
                    result.setAnnotation(data);
                    
                    request.setAttribute("name", name);
                    request.setAttribute("data", result);
                    page.append("annotation.jsp");
                }
                else   // the data type doesn't exist
                {
                    String message = "Sorry, the requested data collection doesn't exist in the Registry...";
                    request.setAttribute("message", message);
                    page.append("/collections");
                }
            }
            else if (id.matches("MIR:005\\d{5}"))   // possible example of annotation identifier
            {
                AnnotationDao annoDao = new AnnotationDao(getPoolName());
                List<SimpleDataType> datatypes = annoDao.getDataFromUsageAnno(id);
                AnnotationTag anno = null;
                anno = annoDao.getAnnoFromId(id);
                
                // cleaning
                annoDao.clean();
                
                // the example of annotation actually exists
                if (null != anno)
                {
                    request.setAttribute("anno", anno);
                    request.setAttribute("data", datatypes);
                    page.append("usage_annotation.jsp");
                }
                else   // the example of annotation does not exist
                {
                    String message = "Sorry, the requested example of annotation doesn't exist in the Registry...";
                    request.setAttribute("message", message);
                    page.append("/collections");
                }
            }
            else   // definitively crap data
            {
                String message = "Sorry, You need to provide the identifier of a data collection.";
                request.setAttribute("message", message);
                page.append("/collections");
            }
        }
        else   // no data type id given
        {
            String message = "Sorry, You need to provide the identifier of a data collection.";
            request.setAttribute("message", message);
            page.append("/collections");
        }
        
        // cleaning
        dao.clean();
    }
}
