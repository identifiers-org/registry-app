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


package uk.ac.ebi.miriam.web;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;


/**
 * <p>
 * Custom tag handler for generating the alphabetical links for the general myMIRIAM projects browsing feature.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2014 BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20140307
 */
public class TagHandlerGenerateProjectBrowsingLinks extends SimpleTagSupport
{
    private String query = null;
    
    
    public void setQuery(String query)
    {
        this.query = query;
    }
    
    
    /**
     * This method contains all the business part of the tag handler.
     */
    public void doTag() throws JspException, IOException
    {
        String[] subsets = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "Selected"};
        JspContext context = getJspContext();
        
        for (String subset: subsets)
        {
            if (query.equalsIgnoreCase(subset))
            {
                context.setAttribute("class", "padding-left:3px; padding-right:3px; color:#E33E3E; font-weight:bold; text-decoration:none;");   // it is mandatory to directly change the "style" attribute, otherwise if just using the "class" attribute, all changes are overwritten
            }
            else
            {
                context.setAttribute("class", "padding-left:3px; padding-right:3px;");
            }
            
            // is it the end of the list
            if (subset.equalsIgnoreCase("Selected"))
            {
                context.setAttribute("title", "Displays data collections associated with this profile");
                context.setAttribute("end", true);
            }
            else
            {
                context.setAttribute("title", "Displays data collections with a name starting by '" + subset + "'");
                context.setAttribute("end", false);
            }
            context.setAttribute("subset", subset);
            getJspBody().invoke(null);   // process the body of the tag and print it to the response
        }
    }
}
