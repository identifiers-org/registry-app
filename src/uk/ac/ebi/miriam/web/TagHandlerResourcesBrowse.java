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


import uk.ac.ebi.miriam.db.Resource;
import uk.ac.ebi.miriam.tools.CommonFunctions;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;


/**
 * <p>Custom tag handler for browsing a ResultSet object (for the summary page of the database)
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
 * @version 20130704
 */
public class TagHandlerResourcesBrowse extends SimpleTagSupport
{
  private Logger logger = Logger.getLogger(TagHandlerResourcesBrowse.class);
  private List<Resource> data;   // list of resources
  private Boolean edit = false;   // whether the tag is used in a display or edit interface


  /**
   * Setter of 'data' attribute
   * @param ArrayList of the resources
   */
  public void setData(List<Resource> data)
  {
    this.data = data;
  }
  
  /**
   * Setter of 'edit' attribute
   * @param edit 
   */
  public void setEdit(Boolean edit)
  {
      this.edit = edit;
  }

  /**
   * this method contains all the business part of the tag handler.
   */
  public void doTag() throws JspException, IOException
  {
    logger.debug("tag handler for data type resources (physical locations) browsing");
    JspContext context = getJspContext();
    int size;

    size = data.size();

    for (int j=0; j<size; ++j)
    {
      if (j == (size - 1))
      {
        context.setAttribute("end", "true");
      }
      else
      {
        context.setAttribute("end", "false");
      }
      context.setAttribute("id", String.valueOf(j+1));
      context.setAttribute("resourceId", data.get(j).getId());
      if (edit)   // in edit mode
      {
          context.setAttribute("prefix", data.get(j).getUrl_prefix());
          context.setAttribute("suffix", data.get(j).getUrl_suffix());
          context.setAttribute("base", data.get(j).getUrl_root());
          context.setAttribute("convert_prefix",data.get(j).getConvert_prefix());
      }
      else   // in display mode
      {
          context.setAttribute("prefix", MiriamUtilities.urlConvert((String) data.get(j).getUrl_prefix()));
          context.setAttribute("suffix", MiriamUtilities.urlConvert((String) data.get(j).getUrl_suffix()));
          context.setAttribute("base", MiriamUtilities.urlConvert((String) data.get(j).getUrl_root()));
          context.setAttribute("convert_prefix", MiriamUtilities.urlConvert((String) data.get(j).getConvert_prefix()));
      }
      context.setAttribute("info", MiriamUtilities.urlConvert(data.get(j).getInfo()));
      context.setAttribute("institution", MiriamUtilities.urlConvert(data.get(j).getInstitution()));
      context.setAttribute("location", MiriamUtilities.urlConvert(data.get(j).getLocation()));
      context.setAttribute("example", MiriamUtilities.urlConvert(data.get(j).getExample()));
      context.setAttribute("htmlUrl", data.get(j).getHtmlUrl());
      context.setAttribute("primary", data.get(j).isPrimary());
      context.setAttribute("formatList",data.get(j).getFormatList());
      context.setAttribute("ownership_status", data.get(j).getOwnership_status());
      context.setAttribute("ownerList", data.get(j).getOwnerList());

      // process the presentage of uptime
      Integer uptime = data.get(j).getReliability();
      if (null != uptime)
      {
          context.setAttribute("uptime_class", CommonFunctions.percentConvert(uptime));
          context.setAttribute("uptime", uptime);
      }
      else
      {
          context.setAttribute("uptime_class", "null");
          context.setAttribute("uptime", "null");
      }
      
      if (data.get(j).isObsolete())
      {
        context.setAttribute("obsolete", "1");
      }
      else
      {
        context.setAttribute("obsolete", "0");
      }

      getJspBody().invoke(null);   // process the body of the tag and print it to the response
    }
  }
}
