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


import uk.ac.ebi.miriam.db.AnnotationDao;
import uk.ac.ebi.miriam.db.AnnotationTag;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ajaxtags.xml.AjaxXmlBuilder;
import org.ajaxtags.servlets.BaseAjaxServlet;


/**
 * <p>Servlet which handles the retrieval of all the tags corresponding to a format (SBML, CellML, BioPAX, ...).
 * <p>Used for the Ajax form: edition of the examples of annotation
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
 * @version 20100528
 */
public class ServletAjaxAnnotationTags extends BaseAjaxServlet
{
    private static final long serialVersionUID = 6081285432255603144L;

    /**
     * @see org.ajaxtags.servlets.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        // retrieves the name of the format
        String format = request.getParameter("format");
        
        if (null != format)
        {
            format = format.trim();
        }
        
        // retrieves all the tags belonging to the given format
        AnnotationDao annoDao = new AnnotationDao(poolName);
        List<AnnotationTag> dump = annoDao.getTagsByFormat(format);
        annoDao.clean();
        
        // puts that in a piece of XML
        AjaxXmlBuilder xml = new AjaxXmlBuilder();
        for (AnnotationTag tag: dump)
        {
            xml.addItemAsCData(tag.getName(), tag.getId());
        }
        
        return xml.toString();
    }
    
    
}
