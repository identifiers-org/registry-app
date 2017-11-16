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


import uk.ac.ebi.miriam.db.Profile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;


/**
 * <p>Custom tag handler for browsing all the registered profiles in myMIRIAM (for administrators only).
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
 * @version 20140305
 */
public class TagHandlerProfilesBrowseAdmin extends SimpleTagSupport
{
    private List<Profile> data;   /* list of profiles */
    
    
    /**
     * Setter of 'data'
     * @param ArrayList of the projects
     */
    public void setData(List<Profile> data)
    {
      this.data = data;
    }
    
    
    /**
     * this method contains all the business part of the tag handler.
     */
    public void doTag() throws JspException, IOException
    {
        JspContext context = getJspContext();
        int i = 0;
        
        for (Profile project: data)
        {
            context.setAttribute("name", project.getName());
            context.setAttribute("shortname", project.getShortName());
/*            if (project.getDesc().length() > 70)
            {
            	context.setAttribute("desc", project.getDesc().substring(0, 70) + " [...]");   // only first 30 characters
            }
            else
            {
            	context.setAttribute("desc", project.getDesc());
            }*/
            context.setAttribute("desc", project.getDesc());
            context.setAttribute("id", project.getId());
            context.setAttribute("access", project.getOpenAccessStr());
            context.setAttribute("counter", project.getNbDataTypes());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   // nicer format for dates
            context.setAttribute("creation", dateFormat.format(project.getDateCreation()));
            context.setAttribute("modification", dateFormat.format(project.getDateLastModif()));
            context.setAttribute("auto", project.getAuto());
            
            // class of the row, for nice colour in the table
            if (i % 2 == 0)
            {
                context.setAttribute("class", "par");
            }
            else
            {
                context.setAttribute("class", "odd");
            }
            i++;
            
            getJspBody().invoke(null);   // process the body of the tag and print it to the response
        }
    }
}
