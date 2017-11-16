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


import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.SimpleDataType;
import uk.ac.ebi.miriam.db.Tag;
import uk.ac.ebi.miriam.db.TagDao;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ajaxtags.servlets.BaseAjaxServlet;


/**
 * <p>Servlet that handles the ajax request for displaying the details of a tag.
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
 * @version 20130806
 */
public class ServletAjaxTagDetails extends BaseAjaxServlet
{
    private static final long serialVersionUID = 4842962790446832607L;
    
    
    /**
     * @see org.ajaxtags.servlets.BaseAjaxServlet#getXmlContent(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public String getXmlContent(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        StringBuffer html = new StringBuffer();
        Tag tag = null;
        List<SimpleDataType> dataTypes = null;
        String def = null;
        String tagName = request.getParameter("tagId");
        
        if (null != tagName)
        {
            tagName = tagName.trim();
        }
        
        // retrieves the name of the database pool
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        
        // retrieves the path to the static elements
        String staticPath = getServletContext().getInitParameter("www");
        
        // tag management
        TagDao tagDao = new TagDao(poolName);
        
        // retrieves the list of all the tags stored in the database
        tag= tagDao.getTagFromName(tagName);
        if (null != tag)
        {
            dataTypes = tagDao.getDataTypesFromTagId(tag.getId());
        }
        
        // cleaning
        tagDao.clean();
        
        // tests if the definition of a tag is empty
        if (MiriamUtilities.isEmpty(tag.getInfo()))
        {
            def = "None";
        }
        else
        {
            def = tag.getInfo();
        }
        
        html.append("<b>Details:</b>\n");
        html.append("<form method=\"post\" id=\"tag_edition_1\" action=\"tagEditInfo\" onsubmit=\"return validate_tag_edition();\">\n");
        html.append("<table class=\"nothing\">\n");
        html.append("<tr>\n");
        html.append("<td>\n");
        html.append("Identifier:&nbsp;\n");
        html.append("</td>\n");
        html.append("<td>\n");
        html.append("<input type=\"text\" name=\"tagId\" size=\"55\" id=\"tagId_id\" style=\"background-color: #C5CED5;\" onfocus=\"this.blur();\" value=\"" + tag.getId() + "\" readonly=\"readonly\" />\n");
        html.append("</td>\n");
        html.append("</tr>\n");
        html.append("<tr>\n");
        html.append("<td>\n");
        html.append("Name:&nbsp;\n");
        html.append("</td>\n");
        html.append("<td>\n");
        html.append("<input type=\"text\" name=\"tagName\" size=\"55\" id=\"tagName_id\" value=\"" + tag.getName() + "\" />\n");
        html.append("</td>\n");
        html.append("</tr>\n");
        html.append("<tr>\n");
        html.append("<td>\n");
        html.append("Definition:&nbsp;\n");
        html.append("</td>\n");
        html.append("<td>\n");
        html.append("<textarea cols=\"46\" rows=\"4\" name=\"tagDef\" id=\"tagDef_id\">" + def + "</textarea>\n");
        html.append("</td>\n");
        html.append("</tr>\n");
        html.append("</table>\n");
        html.append("<input type=\"submit\" value=\"Update Info\" class=\"submit_button\" />\n");
        html.append("</form>\n");
        html.append("<br />\n");
        html.append("<form method=\"post\" id=\"tag_edition_2\" action=\"tagEditLinks\">\n");
        html.append("<b>Connected data types:</b>\n");
        
        int cpt = 0;
        if ((null != dataTypes) && (! dataTypes.isEmpty()))
        {
            html.append("<ul style=\"list-style-type: none;\">\n");
            for (SimpleDataType data: dataTypes)
            {
                cpt ++;
                html.append("\t<li id=\"li_link_" + cpt + "\"><a href=\"#\" onclick=\"deleteTagLink('li_link_" + cpt + "');return false;\" title=\"Removes this connection\"><img src=\"" + staticPath + "img/Delete.gif\" alt=\"delete icon\" title=\"Deletes this connection\" height=\"16\" width=\"16\" /></a><input name=\"n_link_" + cpt + "\" id=\"link_" + cpt + "\" type=\"text\" size=\"40\" style=\"border:none;\" readonly=\"readonly\" value=\"" + data.getName() + "\" /></li>\n");
            }
            html.append("</ul>\n");
            html.append("<input type=\"hidden\" name=\"counter\" id=\"counter_id\" value=\"" + dataTypes.size() + "\" />\n");
        }
        else
        {
            html.append("<ul style=\"list-style-type: none;\">\n");
            html.append("\t<li id=\"li_link_0\"><em>No data type associated</em></li>\n");
            html.append("</ul>\n");
            html.append("<input type=\"hidden\" name=\"counter\" id=\"counter_id\" value=\"0\" />\n");
        }
        
        html.append("<select name=\"newConnection\" id=\"newConnection_id\">\n");
        
        DataTypeDao dataDao = new DataTypeDao(poolName);
        List<SimpleDataType> data = dataDao.getSimpleDataTypes();
        dataDao.clean();
        
        for (SimpleDataType sdt: data)
        {
            html.append("<option value=\"" + sdt.getName() + "\">" + sdt.getName() + "</option>\n");
        }
        html.append("</select>\n");
        html.append("&nbsp;[<a href=\"#\" onclick=\"addTagLink($('newConnection_id').value);return false;\" title=\"Adds the selected data collection\">Add</a>]\n");
        html.append("<br />\n");
        html.append("<input type=\"hidden\" name=\"tagId\" id=\"tagId_id\" value=\"" + tag.getId() + "\" />\n");
        html.append("<input type=\"submit\" value=\"Update Links\" class=\"submit_button\" />\n");
        html.append("</form>\n");
        
        return html.toString();
    }

}
