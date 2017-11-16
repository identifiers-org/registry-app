/*
 * MIRIAM Resources (Web Application)
 * MIRIAM is an online resource created to catalogue biological data types,
 * their URIs and the corresponding physical URLs,
 * whether these are controlled vocabularies or databases.
 * Ref. http://www.ebi.ac.uk/miriam/
 *  
 *  Copyright (C) 2005-2009  Mélanie Courtot <courtot@ebi.ac.uk> and Camille Laibe <camille.laibe@ebi.ac.uk>
 *  Computational Neurobiology 
 *  EMBL - European Bioinformatics Institute 
 *  Wellcome-Trust Genome Campus, Hinxton, CAMBRIDGE, CB10 1SD, UK
 * 
 *  SBO Browser is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  SBO Browser is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with SBO Browser.  If not, see <http://www.gnu.org/licenses/>.
 *  
 */


package uk.ac.ebi.miriam.tools;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;


/**
 * Loads properties from a file.
 * 
 * @author Mélanie Courtot <courtot@ebi.ac.uk>
 * @author Camille Laibe <camille.laibe@ebi.ac.uk>
 *
 * @version 20090108
 */
public class PropertyLoader
{
    private Logger logger = Logger.getLogger(PropertyLoader.class);   // never 'static' in a servlet or j2ee container!
    private Properties properties;
    
    
    /**
     * Loads properties from a file, which name is given in parameter.
     * The file must be in the WEB-INF/classes folder of the web app in the Tomcat
     * @param propName name of the properties file
     * @return Properties
     */
    public Properties load(String propName)
    {
        Properties properties = null;
        
        try
        {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(propName);
            if (is != null)
            {
                properties = new Properties();
                properties.load(is);
            }
            else
            {
                logger.error("Unable to access the '" + propName + "' properties file!");
            }
        }
        catch (IOException ioe)
        {
            logger.error("Exception raised while loading property file '" + propName + "': " + ioe.getMessage());
        }
        
        /* OLD METHOD: dosn't work any more on the new Tomcat infrastucture
        try
        {
            InputStream is = getClass().getClassLoader().getResourceAsStream(propName);
            if (is != null)
            {
                Properties properties = new Properties();
                properties.load(is);
                
                return properties;
            }
            else
            {
                System.out.println("InputStream is null!!!!!!!!!!");
            }
        }
        catch (IOException ioe)
        {
            System.out.println("Exception raised while loading property file '" + propName + "': " + ioe.getMessage());
        }
        */
        
        return properties;
    }
    
    
    /**
     * Loads properties from the file 'miriam.properties'.
     * This file should be stored under '/WEB-INF/classes/' in the Web application.
     * 
     * @return Properties
     */
    public Properties getMIRIAMProperties()
    {
        this.properties = new Properties();
        String propName = "miriam.properties";
        this.properties = load(propName);
        
        return this.properties;
    }
}
