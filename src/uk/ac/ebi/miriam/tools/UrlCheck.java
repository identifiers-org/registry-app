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


import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.Resource;
import uk.ac.ebi.miriam.db.ResourceDao;
import uk.ac.ebi.miriam.db.SimpleDataType;
import uk.ac.ebi.miriam.web.MailFacade;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;


/**
 * <p>Checks all the URLs and update the 'mir_url_check' table in the database.
 *
 * <p>
 * <dl>
 * <dt><b>Copyright:</b></dt>
 * <dd>
 * Copyright (C) 2006-2014  BioModels.net (EMBL - European Bioinformatics Institute)
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
 * @version 20140114
 */
public class UrlCheck
{
    private Logger logger = Logger.getLogger(UrlCheck.class);
	private String poolName;   // name of the database pool
	private String version;   // version of the app (main, demo or alpha)
	private String emailAdmin;   // email address of the admin
	
	
	/**
	 * Default constructor.
	 * @param pool name of the database pool
	 */
	public UrlCheck()
	{
		// retrieves some info from the miriam.properties file
        PropertyLoader pl = new PropertyLoader();
        Properties properties  = pl.getMIRIAMProperties();
        this.poolName = properties.getProperty("database_pool");
        this.version = properties.getProperty("app_version");
        this.emailAdmin = properties.getProperty("email_admin");
        
        properties.clear();   // a bit of cleaning
	}
	
	
	/**
	 * Does the whole checking work for all resources in the database.
	 * @return number of resources checked
	 */
	public int check()
	{
		List<ResourceLog> failure1 = new ArrayList<ResourceLog>(10);   // failure after first attempt
		List<ResourceLog> failure2 = new ArrayList<ResourceLog>(10);   // failure after second attempt
		List<String> urlSuccess = new ArrayList<String>(200);   // list of URLs which successfully passed the check
		List<String> urlProbablyUp = new ArrayList<String>(20);   // list of URLs which are probably up (server responding but usage of Javascript/Ajax freatures prevent the detection of the keyword)
		List<String> urlFailure = new ArrayList<String>(10);   // list of URLs which failed the check
		List<String> urlUnknown = new ArrayList<String>(10);   // list of URLs which state is unknown
		List<String> urlAccessRestricted = new ArrayList<String>(10);   // list of URLs which has a restriction on their access
		List<String> urlObsolete = new ArrayList<String>(15);   // list of URLs which are obsolete
		UCS checking = new UCS();
		StringBuilder email = new StringBuilder();
		int counter = 0;
		
		// connection to the database
		DataTypeDao dataDao = new DataTypeDao(this.poolName);
		ResourceDao resDao = new ResourceDao(poolName);
		
		// retrieves the time of the beginning of the process
		GregorianCalendar calBegin = new GregorianCalendar();
		Date begin = calBegin.getTime();
		
		// retrieves the list of all data collections not deprecated stored in the database
		List<SimpleDataType> datatypes = dataDao.getSimpleDataTypesNotDeprecated();
		for (SimpleDataType datatype: datatypes)
		{
			// retrieves the list of all the resources associated with the current data collection
			List<Resource> resources = resDao.getResources(datatype.getId(), true);   // we want all the resources
			
			// for all its resources
			for (Resource resource: resources)
			{
				counter ++;
				
                // check if there is a record of this resource in 'mir_url_check' (that can be a newly added data collection for example)
                if (resDao.existingCheckDetails(resource.getId()))
                {
                    String keyword = resDao.getCheckingKeyword(resource.getId());
                    
                    // is the resource using Javascript/Ajax features?
                    boolean ajax = resDao.ajaxUsed(resource.getId());
                    // is the resource returning a binary file?
                    boolean binary = resDao.returnsBinaryData(resource.getId());
                    
                    ResourceLog report = new ResourceLog(resource.getId(), datatype.getId(), resource.getUrl(), keyword, resource.isObsolete(), resDao.hasAccessRestriction(resource.getId()), ajax, binary); 
                    
                    // checks whether the resource is responsive and gives the expected content
                    Integer state = checking.check(report);
                    
                    switch (state)
                    {
                        case ResourceDao.STATE_SUCCESS:
                            urlSuccess.add(resource.getId());
                            resDao.updateCheckRecordSuccess(report);   // updates the health record in the database
                            break;
                        case ResourceDao.STATE_PROBABLY:
                            urlProbablyUp.add(resource.getId());
                            resDao.updateCheckRecordSuccess(report);   // updates the health record in the database
                            break;
                        case ResourceDao.STATE_UNKNOWN:
                            urlUnknown.add(resource.getId());
                            resDao.updateCheckRecordUnknown(report);   // updates the health record in the database
                            break;
                        case ResourceDao.STATE_FAILURE:
                            failure1.add(report);
                            break;
                        case ResourceDao.STATE_RESTRICTED:
                            urlAccessRestricted.add(resource.getId());
                            resDao.updateCheckRecord(report);   // updates the health record in the database
                            break;
                        case ResourceDao.STATE_OBSOLETE:
                            urlObsolete.add(resource.getId());
                            resDao.updateCheckRecord(report);   // updates the health record in the database
                            break;
                        default:
                            logger.error("Unknown state code returned after health check of resource " + resource.getId() + ": " + state + "!");
                            urlUnknown.add(resource.getId());
                            resDao.updateCheckRecord(report);   // updates the health record in the database
                            break;
                    }
                }
                else   // no record yet
                {
                    // some resources were made obsolete before having any health record created in 'mir_url_check'
                    if (resource.isObsolete())
                    {
                        urlObsolete.add(resource.getId());
                        
                        // creates a new record for this resource, containing the result of the (limited) check
                        resDao.createCheckRecord(datatype.getId(), resource.getId(), ResourceDao.STATE_OBSOLETE, "Resource is obsolete.");
                    }
                    else
                    {
                        urlUnknown.add(resource.getId());
                        
                        // creates a new record for this resource, containing the result of the (limited) check
                        resDao.createCheckRecord(datatype.getId(), resource.getId(), ResourceDao.STATE_UNKNOWN, "New resource: no previous health record and most probably no keyword stored!");
                    }
                }
			}
		}
		
		// wait 10 seconds
        logger.debug("waiting (10s)...");
        Thread.currentThread();
        try
        {
            Thread.sleep(10000);
        }
        catch (InterruptedException e)
        {
            logger.debug("InterruptedException raised while sleeping 10s: " + e.getMessage());
        }
		
		// try again the URLs which previously failed the check
		for (ResourceLog resource: failure1)
		{
		    // the resource is responsive and gives the expected content
		    Integer state2 = checking.check(resource);
		    
		    switch (state2)
            {
                case ResourceDao.STATE_SUCCESS:
                    urlSuccess.add(resource.getResourceId());
                    resDao.updateCheckRecordSuccess(resource);   // updates the health record in the database
                    break;
                case ResourceDao.STATE_PROBABLY:
                    urlProbablyUp.add(resource.getResourceId());
                    resDao.updateCheckRecordSuccess(resource);   // updates the health record in the database
                    break;
                case ResourceDao.STATE_UNKNOWN:
                    urlUnknown.add(resource.getResourceId());
                    resDao.updateCheckRecordUnknown(resource);   // updates the health record in the database
                    break;
                case ResourceDao.STATE_FAILURE:
                    failure2.add(resource);
                    break;
                case ResourceDao.STATE_RESTRICTED:
                    urlAccessRestricted.add(resource.getResourceId());
                    resDao.updateCheckRecord(resource);   // updates the health record in the database
                    break;
                case ResourceDao.STATE_OBSOLETE:
                    urlObsolete.add(resource.getResourceId());
                    resDao.updateCheckRecord(resource);   // updates the health record in the database
                    break;
                default:
                    urlUnknown.add(resource.getResourceId());
                    resDao.updateCheckRecord(resource);   // updates the health record in the database
                    break;
            }
		}
		
	    // wait 20 seconds
        logger.debug("waiting (20s)...");
        Thread.currentThread();
        try
        {
            Thread.sleep(20000);
        }
        catch (InterruptedException e)
        {
            logger.debug("InterruptedException raised while sleeping 20s: " + e.getMessage());
        }
		
		// try again (for the last time) the URLs which already failed twice the check
		for (ResourceLog resource: failure2)
		{
		    // the resource is responsive and gives the expected content
			Integer state3 = checking.check(resource);
			
			switch (state3)
            {
                case ResourceDao.STATE_SUCCESS:
                    urlSuccess.add(resource.getResourceId());
                    resDao.updateCheckRecordSuccess(resource);   // updates the health record in the database
                    break;
                case ResourceDao.STATE_PROBABLY:
                    urlProbablyUp.add(resource.getResourceId());
                    resDao.updateCheckRecordSuccess(resource);   // updates the health record in the database
                    break;
                case ResourceDao.STATE_UNKNOWN:
                    urlUnknown.add(resource.getResourceId());
                    resDao.updateCheckRecordUnknown(resource);   // updates the health record in the database
                    break;
                case ResourceDao.STATE_FAILURE:
                    urlFailure.add(resource.getResourceId());
                    resDao.updateCheckRecordFailure(resource);   // updates the health record in the database
                    break;
                case ResourceDao.STATE_RESTRICTED:
                    urlAccessRestricted.add(resource.getResourceId());
                    resDao.updateCheckRecord(resource);   // updates the health record in the database
                    break;
                case ResourceDao.STATE_OBSOLETE:
                    urlObsolete.add(resource.getResourceId());
                    resDao.updateCheckRecord(resource);   // updates the health record in the database
                    break;
                default:
                    urlUnknown.add(resource.getResourceId());
                    resDao.updateCheckRecord(resource);   // updates the health record in the database
                    break;
            }
		}
		
		// retrieves the list of all deprecated data collections stored in the database (to record them as obsolete for the purpose of the summary email)
		List<SimpleDataType> deprecatedCollections = dataDao.getSimpleDataTypesDeprecated();
		for (SimpleDataType collection: deprecatedCollections)
		{
			// retrieves the list of all the resources associated with the current data collection
			List<Resource> resources = resDao.getResources(collection.getId(), true);   // we want all the resources
			
			for (Resource resource: resources)
			{
				urlObsolete.add(resource.getId());
			}
		}
		
		// cleaning
		dataDao.clean();
		resDao.clean();
		
		logger.debug("Resources Health Check finished.");
		
		// retrieve the time of the end of the process
		GregorianCalendar calEnd = new GregorianCalendar();
		Date end = calEnd.getTime();
		
		// sends a report by email
		email.append("Identifiers.org Registry: Resources Health Checking System\n");
		email.append("\nUp               : " + urlSuccess.size());
		email.append("\nProbably up      : " + urlProbablyUp.size());
		email.append("\nDown             : " + urlFailure.size());
		email.append("\nObsolete         : " + urlObsolete.size());
		email.append("\nAccess restricted: " + urlAccessRestricted.size());
		email.append("\nUnknown          : " + urlUnknown.size());
		email.append("\n----------------   ---");
		email.append("\nTotal            : " + counter);
		email.append("\n\nStart time: " + begin);
		email.append("\nEnd time  : " + end);
		email.append("\n\n\nNotice: DO NOT REPLY TO THIS EMAIL!\n\n-- \nIdentifiers.org Registry\nhttp://identifiers.org/registry/");
		MailFacade.send("Registry-" + this.version + "@ebi.ac.uk", this.emailAdmin, "[Registry] Resources Health Check", email.toString(), "text/plain; charset=UTF-8");
		
		return counter;
	}

    public String getVersion() {
        return version;
    }
}
