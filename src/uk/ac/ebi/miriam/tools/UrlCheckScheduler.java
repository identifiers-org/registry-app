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


package uk.ac.ebi.miriam.tools;


import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


/**
 * <p>Launches the check of all the URLs (UrlCheck).
 * This is called daily by a Quartz scheduler.
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
 * @version 20140424
 */
public class UrlCheckScheduler implements Job
{
	private static Logger logger = Logger.getLogger(UrlCheckScheduler.class);
	
	
	/**
	 * Triggers the whole checking work.
	 */
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{
	    InetAddress localMachine;
        String hostName = null;
        
        // the check should be launched only on one machine
	    try
        {
            localMachine = InetAddress.getLocalHost();
            hostName = localMachine.getHostName();   // will a string of the form 'tomcat-11.ebi.ac.uk'
        }
        catch (UnknownHostException e)
        {
            logger.error("Unable to retrieve the hostname of the computer: failure to launch the resources checking system!");
            logger.error("UnknownHostException raised: " + e.getMessage());
        }
        
        // the app runs on two VMs (in one given data centre), we only want to perform the health check once from the production curation pipeline (running on ves-hx-4d.ebi.ac.uk)
        if (hostName.equals("ves-ebi-6c.ebi.ac.uk") ||hostName.equals("ves-hx-4d.ebi.ac.uk")){
            UrlCheck urlCheckingProcess = new UrlCheck();
            if((urlCheckingProcess.getVersion().equals("cura") && hostName.equals("ves-ebi-6c.ebi.ac.uk")) || (urlCheckingProcess.getVersion().equals("devcura") && hostName.equals("ves-hx-4d.ebi.ac.uk"))) {
                logger.debug("UCS in progress on '" + hostName + "'...");
                int counter = urlCheckingProcess.check();
                logger.debug("UCS process finished: " + counter + " resources checked.");
            }
        }
	}
}
