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


import java.util.Calendar;


/**
 * <p>Common useful functions.
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
 * @version 20140312
 */
public class CommonFunctions
{
    
    /**
     * Cleans a String value coming from an HTML field.
     * Basically just performs a protected 'trim()'.
     * @param fieldValue
     * @return
     */
    public static String cleanHtmlField(String fieldValue)
    {
        if (null != fieldValue)
        {
            fieldValue = fieldValue.trim();
        }
        
        return fieldValue;
    }
    
    /**
     * Returns the number of days between two dates. 
     * @param calA
     * @param calB
     * @return
     */
    public static int daysDifference(Calendar calA, Calendar calB)
    {
        Calendar firstCal = (calA.before(calB) ? (Calendar)calA.clone() : (Calendar)calB.clone());
        Calendar secondCal = (calA.before(calB) ? calB : calA);
        
        // adjusts the starting date back to the beginning of the year
        int diff = -firstCal.get(Calendar.DAY_OF_YEAR);
        firstCal.set(Calendar.DAY_OF_YEAR, 1);
        
        // adjusts the years to have the same value, adding the number of days in that year to the difference accumulator
        while ((firstCal.get(Calendar.YEAR)) < (secondCal.get(Calendar.YEAR)))
        {
            diff += firstCal.getActualMaximum(Calendar.DAY_OF_YEAR);
            firstCal.add(Calendar.YEAR, 1);
        }
        
        // both calendars are in the same year, adds back the ending "day of the year" value
        diff += secondCal.get(Calendar.DAY_OF_YEAR);
        
        return diff;
    }
    
    
    /**
     * Convert a percentage into an index (between 1 and 4).
     * For example used to convert an uptime percentage of a resource into a index used for a color code.  
     * @param percent
     * @return
     */
    public static int percentConvert(int percent)
    {
        int index = 0;
        if (percent >= 90)
        {
            index = 1;
        }
        else if (percent >= 65)
        {
            index = 2;
        }
        else if (percent >= 21)
        {
            index = 3;
        }
        else
        {
            index = 4;
        }
        
        return index;
    }
    
    
    /**
     * Checks whether a string start by a prefix in a case insensitive way. 
     * @param string
     * @param prefix
     * @return
     */
    public static Boolean startsWithIgnoreCase(String string, String prefix)
    {
        Boolean start = false;
        
        if ((null != string) && (null != prefix) && (string.length() >= prefix.length()))
        {
            start = string.substring(0, prefix.length()).equalsIgnoreCase(prefix);
        }
            
        return start;
    }
    
    
    /**
     * Retrieves the "namespace" of a data collection (the part after the "urn:miriam:" in the official URN) given its MIRIAM URN.
     * @return namespace of a data collection
     */
    public static String getNamespace(String urn)
    {
        return urn.substring(11);
    }

    /**
     * Retrieves the "namespace" of a data collection (the part after the "http://identifiers.org/" in the official URL) given its idOrgURL.
     * @return namespace of a data collection
     */
    public static String getNamespaceFromIdOrg(String url)
    {
        return url.substring(23,url.lastIndexOf("/"));
    }
}
