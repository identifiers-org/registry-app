/*
 * MIRIAM Resources (Web Application)
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *
 * Copyright (C) 2006-2011  BioModels.net (EMBL - European Bioinformatics Institute)
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


import uk.ac.ebi.miriam.db.SimpleDataType;

import java.io.IOException;
import java.util.List;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
//import org.apache.log4j.Logger;


/**
 * <p>
 * Custom tag handler for browsing a ResultSet object (for the summary page of the database).
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2011 BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20110613
 */
public class TagHandlerDbBrowse extends SimpleTagSupport
{
    //private Logger logger = Logger.getLogger(TagHandlerDbBrowse.class);
    private List<SimpleDataType> data;
    
    
    /**
     * Setter of 'data'
     * 
     * @param ResultSet result of a SQL query
     */
    public void setData(List<SimpleDataType> data)   // SQLResult
    {
        this.data = data;
    }
    
    
    /**
     * This method contains all the business part of the tag handler.
     */
    public void doTag() throws JspException, IOException
    {
        //logger.debug("tag handler for general browsing (beginning)");
        JspContext context = getJspContext();
        
        if (null != data) 
        {
            for (int j=0; j<data.size(); ++j)
            {
                if (j % 2 == 0)
                {
                    context.setAttribute("class", "par");
                }
                else
                {
                    context.setAttribute("class", "odd");
                }
                
                context.setAttribute("datatype", data.get(j));
                
                getJspBody().invoke(null);   // processes the body of the tag and print it to the response
            }
        }
        else
        {
            // TODO: not sure how to handle this case...
        }
    }
}
