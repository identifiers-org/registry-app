/*
 * MIRIAM Resources (Web Application)
 * MIRIAM is an online resource created to catalogue biological data collections,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2012  BioModels.net (EMBL - European Bioinformatics Institute)
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ebi.miriam.db.Annotation;
import uk.ac.ebi.miriam.db.AnnotationDao;
import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.DataTypeHibernate;


/**
 * <p>Servlet which handles the queries to the database for displaying miscellaneous info about a data collection.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2012  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20120306
 */
public class ServletMisc extends ServletTemplate
{
    private static final long serialVersionUID = 1150660685632578742L;
    
    
    @Override
    protected void execute(HttpServletRequest request, HttpServletResponse response, StringBuilder page)
    {
        Statement stmt = null;
        DataTypeHibernate data = new DataTypeHibernate();
        boolean exist = false;
        
        // retrieves the parameter (identifier of a data collection)
        String id = request.getParameter("data");
        
        // tests if there is an existing data type with this name
        if (! MiriamUtilities.isEmpty(id))
        {
            id = id.trim();
            // checks that the parameter looks like a MIRIAM Identifier (to stop attempts to attack the service via SQL Injection)
            if (id.matches("MIR:000\\d{5}"))
            {
                try
                {
                    stmt = this.connection.createStatement();
                    String sql = "SELECT name FROM mir_datatype WHERE (datatype_id='" + id + "')";
                    ResultSet rs = stmt.executeQuery(sql);
                    if (rs.first())
                    {
                        exist = true;
                    }
                }
                catch (SQLException e)
                {
                    logger.warn("An exception occured during the test to know if a particular data collection exists (" + id + ")!");
                    logger.warn("SQL Exception raised: " + e.getMessage());
                }
                finally
                {
                    closeStatement(stmt);
                }
            }
        }
        
        if (exist)
        {
            // retrieves all the information about the data collection
            data.retrieveData(getPoolName(), id);
            
            // the data collection is obsolete (we retrieve the name of the replacement one)
            if ((data.isObsolete()) && (null != data.getReplacedBy()))
            {
                DataTypeDao dataTypeDao = new DataTypeDao(getPoolName());
                String replacedName = dataTypeDao.getDataTypeName(data.getReplacedBy());
                dataTypeDao.clean();
                request.setAttribute("replacementName", replacedName);
            }
            else   // should not be necessary, but in case...
            {
                request.setAttribute("replacementName", null);
            }
            
            // retrieves examples of usage
            AnnotationDao annoDao = new AnnotationDao(getPoolName());
            List<Annotation> examples = annoDao.getAnnotationFromDataId(id);
            
            // answer
            request.setAttribute("data", data);
            request.setAttribute("examples", examples);
            page.append("misc.jsp");
        }
        else
        {
            request.setAttribute("section", "not_existing.html");
            page.append("static.jsp");
        }
    }
}
