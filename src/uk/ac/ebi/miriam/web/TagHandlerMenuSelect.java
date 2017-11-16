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
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
//import org.apache.log4j.Logger;


/**
 * <p>
 * Custom tag handler for choosing the right menu to display,
 *  depending on the user (anonymous, curator or administrator).
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
 * @version 20110603
 */
public class TagHandlerMenuSelect extends SimpleTagSupport
{
    //private Logger logger = Logger.getLogger(TagHandlerMenuSelect.class);
    private final String ANONYMOUS = "iframe_leftmenu_anonymous";
    private final String CURATOR = "iframe_leftmenu_curator";
    private final String ADMINISTRATOR = "iframe_leftmenu_admin";
    private String user = null;
    
    
    public void setUser(String user)
    {
        this.user = user;
    }
    
    
    /**
     * This method contains all the business part of the tag handler.
     */
    public void doTag() throws JspException, IOException
    {
        JspContext context = getJspContext();
        String menu = null;
        
        if ((null == this.user) || (MiriamUtilities.isEmpty(this.user)))
        {
            menu = ANONYMOUS;
        }
        else
        {
            if (this.user.equals(MiriamUtilities.USER_CURA))
            {
                menu = CURATOR;
            }
            else
            {
                if (this.user.equals(MiriamUtilities.USER_ADMIN))
                {
                    menu = ADMINISTRATOR;
                }
                else   // any other user
                {
                    menu = ANONYMOUS;
                }
            }
        }
        
        context.setAttribute("menu", menu);
        getJspBody().invoke(null);   // process the body of the tag and print it to the response
    }
}