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


import java.io.IOException;
import java.util.List;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.apache.log4j.Logger;


/**
 * <p>
 * Custom tag handler for editing the documentations related to a data type.
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
 * @version 20080219
 */
public class TagHandlerResourcesEdit extends SimpleTagSupport
{
    private Logger logger = Logger.getLogger(TagHandlerResourcesEdit.class);
    private List<String> uris;
    private List<String> types;
    private int start;
    
    
    /**
     * this method contains all the business part of a tag handler
     */
    public void doTag() throws JspException, IOException
    {
        logger.debug("Custom tag handler for editing resources...");
        JspContext context = getJspContext();
        
        for (int i=0; i<this.uris.size(); ++i)
        {
            // sets the variables to send to the Web page
            context.setAttribute("id", String.valueOf(i + 1 + this.start));   // +1 to avoid the first value: 0 
            context.setAttribute("uri", this.uris.get(i));
            context.setAttribute("uriType", this.types.get(i));
            
            // processes the body of the tag and prints it to the response (Web page)
            getJspBody().invoke(null);
        }
    }
    
    
    /*
     * Getter of the list of identifiers of pieces of documentation (can be a PMID, a DOI, ...)
     * @return the uris
     */
    /*
    public List<String> getUris()
    {
        return this.uris;
    }
    */
    
    
    /**
     * Setter of the list of identifiers of pieces of documentation (can be a PMID, a DOI, ...)
     * @param uris the uris to set
     */
    public void setUris(List<String> uris)
    {
        this.uris = uris;
    }
    
    
    /*
     * Getter of the list of type of the identifiers stored in 'uris'
     * @return the type
     */
    /*
    public List<String> getTypes()
    {
        return this.types;
    }
    */
    
    
    /**
     * Setter of the list of type of the identifiers stored in 'uris'
     * @param uriType the uriType to set
     */
    public void setTypes(List<String> types)
    {
        this.types = types;
    }
    
    
    /**
     * Setter of the starting point of the counter
     * @param start starting value for the counter
     */
    public void setStart(int start)
    {
        this.start = start;
    }
}
