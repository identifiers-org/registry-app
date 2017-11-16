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


package uk.ac.ebi.miriam.tools;


import uk.ac.ebi.miriam.db.ResourceDao;
import uk.ac.ebi.miriam.db.ResourcesCheckReport;
import uk.ac.ebi.miriam.web.MailFacade;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
//import org.apache.log4j.Logger;


/**
 * <p><p>Retrieves the resources which have been detected down for several consecutive days, in order to inform the curator(s).
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2013 BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20130801
 */
public class UrlWarning
{
    //private Logger logger = Logger.getLogger(UrlWarning.class);
    private String poolName;   // name of the database pool
    private String version;   // version of the app (main, demo or alpha)
    private String emailAdmin;   // email address of the admin
    private String[] emailsCura;   // email addresses of the curator(s)
    
    
    public UrlWarning()
    {
        // retrieves some info from the miriam.properties file
        PropertyLoader pl = new PropertyLoader();
        Properties properties  = pl.getMIRIAMProperties();
        this.poolName = properties.getProperty("database_pool");
        this.version = properties.getProperty("app_version");
        this.emailAdmin = properties.getProperty("email_admin");
        String emailCura = properties.getProperty("email_cura");
        this.emailsCura = emailCura.split(",");
        
        properties.clear();   // a bit of cleaning
    }
    
    
    /**
     * Does the whole checking work for all resources in the database.
     * @return number of resources checked
     */
    public void check()
    {
        List<ResourcesCheckReport> resources3days = null;
        List<ResourcesCheckReport> resources10days = null;
        StringBuilder email = new StringBuilder();
        
        // retrieves the time of the beginning of the process
        GregorianCalendar calBegin = new GregorianCalendar();
        Date begin = calBegin.getTime();
        
        // database access
        ResourceDao resDao = new ResourceDao(poolName);
        resources3days = resDao.getResourcesDown(3, false);   // non-obsolete resources only
        resources10days = resDao.getResourcesDown(10, false);   // non-obsolete resources only
        
        // cleaning
        resDao.clean();
        
        // removes from the list 3days, the resource which are already in the list 10days: no need to have them twice in the summary
        resources3days.removeAll(resources10days);
        
        // sends a report by email
        email.append("Identifiers.org Registry: Resources Health Checking System\n");
        email.append("\nDate: " + begin + "\n");
        
        // no long time down resources
        if ((resources3days.isEmpty()) && (resources10days.isEmpty()))
        {
            email.append("\nNo resource has been detected as down for more than 3 days.");
        }
        else
        {
            if (! resources3days.isEmpty())
            {
                email.append("\nResources detected as down for more than 3 consecutive days:\n");
                for (ResourcesCheckReport resource: resources3days)
                {
                    email.append("- ").append(resource.getId()).append(" (").append(resource.getDataName()).append(") - ").append(resource.getInfo()).append("\n");
                }
            }
            else
            {
                email.append("\nNo resource has been detected as down for more than 3 days and less than 10 days.\n");
            }
            
            if (! resources10days.isEmpty())
            {
                email.append("\nResources detected as down for more than 10 consecutive days:\n");
                for (ResourcesCheckReport resource: resources10days)
                {
                    email.append("- ").append(resource.getId()).append(" (").append(resource.getDataName()).append(") - ").append(resource.getInfo()).append("\n");
                }
            }
            else
            {
                email.append("\nNo resource has been detected as down for more than 10 days.");
            }
        }
        
        email.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
        
        // we only send the email to the curator(s) for the main version
        if (this.version.equalsIgnoreCase("main"))
        {
            MailFacade.send("Registry-" + this.version + "@ebi.ac.uk", this.emailAdmin, this.emailsCura, "[Registry] Resources Health Warning", email.toString(), "text/plain; charset=UTF-8");
        }
        else
        {
            MailFacade.send("Registry-" + this.version + "@ebi.ac.uk", this.emailAdmin, "[Registry] Resources Health Warning", email.toString(), "text/plain; charset=UTF-8");
        }
    }

    public String getVersion() {
        return version;
    }
}
