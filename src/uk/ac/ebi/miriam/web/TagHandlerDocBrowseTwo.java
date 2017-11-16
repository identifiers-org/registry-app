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
 * Custom tag handler for browsing the documentations (the ones stored with a URI, not a physical address),
 * part two (all the resources except the first -without any order- one)
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
 * @author Camille Laibe
 * @version 20100324
 */
public class TagHandlerDocBrowseTwo extends SimpleTagSupport
{
  private Logger logger = Logger.getLogger(TagHandlerDocBrowse.class);
  private List<String> urls;   /* list of physical address */
  private List<String> infos;   /* information */
  private List<String> institutions;   /* institutions */
  private List<String> locations;   /* location (country) */


  /**
   * Setter of 'urls'
   * @param ArrayList list of physical address to access to a documentation
   */
  public void setUrls(List<String> urls)
  {
    this.urls = urls;
  }

  /**
   * Setter of 'infos'
   * @param ArrayList list of information
   */
  public void setInfos(List<String> infos)
  {
    this.infos = infos;
  }

  /**
   * Setter of 'institutions'
   * @param ArrayList list of institutions
   */
  public void setInstitutions(List<String> institutions)
  {
    this.institutions = institutions;
  }

  /**
   * Setter of 'locations'
   * @param ArrayList list of locations (countries)
   */
  public void setLocations(List<String> locations)
  {
    this.locations = locations;
  }

  /**
   * this method contains all the business part of a tag handler
   */
  public void doTag() throws JspException, IOException
  {
    logger.debug("tag handler for data type documentations (not first ones -without any order-)");
    JspContext context = getJspContext();

    for (int i=0; i<urls.size(); ++i)
    {
      context.setAttribute("url2", urls.get(i));
      context.setAttribute("info2", infos.get(i));
      context.setAttribute("institution2", institutions.get(i));
      context.setAttribute("location2", locations.get(i));

      getJspBody().invoke(null);   // process the body of the tag and print it to the response
    }
  }
}

