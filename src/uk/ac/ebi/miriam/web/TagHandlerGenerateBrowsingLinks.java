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


import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;


/**
 * <p>
 * Custom tag handler for generating the alphabetical links for the general data collections browsing feature.
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
 * @version 20130403
 */
public class TagHandlerGenerateBrowsingLinks extends SimpleTagSupport
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
        String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        JspContext context = getJspContext();
        Boolean found = false;
        
        // special case: recently updated data types
        if (query.equalsIgnoreCase("updated"))
        {
            context.setAttribute("class", "padding-left:3px; padding-right:3px; color:#E33E3E; font-weight:bold; text-decoration:none;");
            context.setAttribute("title", "Displays the recently updated data collections (either added or modified)");
            context.setAttribute("letter", "updated");
            context.setAttribute("link", "Recently updated");
            context.setAttribute("end", false);
            found = true;
            getJspBody().invoke(null);   // process the body of the tag and print it to the response
        }
        else
        {
            context.setAttribute("class", "padding-left:3px; padding-right:3px;");
            context.setAttribute("title", "Displays the recently updated data collections (either added or modified)");
            context.setAttribute("letter", "updated");
            context.setAttribute("link", "Recently updated");
            context.setAttribute("end", false);
            getJspBody().invoke(null);   // process the body of the tag and print it to the response
        }
        
        for (String letter: letters)
        {
            if (query.equalsIgnoreCase(letter))
            {
                context.setAttribute("class", "padding-left:3px; padding-right:3px; color:#E33E3E; font-weight:bold; text-decoration:none;");   // it is mandatory to directly change the "style" attribute, otherwise if just using the "class" attribute, all changes are overwritten
                found = true;
            }
            else
            {
                context.setAttribute("class", "padding-left:3px; padding-right:3px;");
            }
            
            // is it the end of the list
            if (letter.equalsIgnoreCase("Z") && found)
            {
                context.setAttribute("end", true);
            }
            else
            {
                context.setAttribute("end", false);
            }
            
            context.setAttribute("link", letter);
            context.setAttribute("title", "Displays the data collections with a name starting by '" + letter + "'");
            context.setAttribute("letter", letter.toLowerCase());
            getJspBody().invoke(null);   // process the body of the tag and print it to the response
        }
        
        // custom query (not just a letter of the alphabet)
        if (!found)
        {
            context.setAttribute("class", "padding-left:3px; padding-right:3px; color:#E33E3E; font-weight:bold; text-decoration:none;");
            context.setAttribute("letter", query);
            context.setAttribute("link", query);
            context.setAttribute("title", "Displays the data collections with a name starting by '" + query + "'");
            context.setAttribute("end", true);
            getJspBody().invoke(null);   // process the body of the tag and print it to the response
        }
    }
}
