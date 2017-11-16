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


package uk.ac.ebi.miriam.db;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * <p>Object which stores some specific (and not complete) information about a data type for curation purposes.
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
 * @version 20080620
 */
public class SimpleCuraDataType extends SimpleDataType
{
    private String state;
    private Date submissionDate;
    private String publicId;
    
    


    /**
     * Default constructor: builds an empty object.
     */
    public SimpleCuraDataType()
    {
        super();
    }
    
    
    /**
     * Constructor with parameters: builds a full object.
     * @param state
     * @param submissionDate
     */
    /*
    public SimpleCuraDataType(String state, Date submissionDate)
    {
        super();
        this.state = state;
        this.submissionDate = submissionDate;
    }
    */
    
    
    /**
     * Returns a <code>String</code> representation of this object.
     */
    @Override
    public String toString()
    {
        StringBuilder tmp = new StringBuilder();
        
        tmp.append(super.toString());
        tmp.append("Submission date: " + getSubmissionDateStr() + "\n");
        tmp.append("State: " + getState() + "\n");
        tmp.append("Public ID: " + getPublicId() + "\n");
        
        return tmp.toString();
    }
    
    
    /**
     * Getter
     * @return the state
     */
    public String getState()
    {
        return this.state;
    }
    
    
    /**
     * Setter
     * @param state the state to set
     */
    public void setState(String state)
    {
        this.state = state;
    }
    
    
    /**
     * Getter
     * @return the submissionDate
     */
    public Date getSubmissionDate()
    {
        return this.submissionDate;
    }
    
    
    /**
     * Getter of the submission date as a String (using a specific format).
     * @return
     */
    public String getSubmissionDateStr()
    {
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
        return df.format(this.submissionDate);
    }
    
    
    /**
     * Setter
     * @param submissionDate the submissionDate to set
     */
    public void setSubmissionDate(Date submissionDate)
    {
        this.submissionDate = submissionDate;
    }
    
    
    /**
     * Returns a short version of the definition if it is longer that 150 characters, otherwise returns the full definition.
     * @return
     */
    public String getShortDef()
    {
        if (this.getDefinition().length() > 150)
        {
            return (this.getDefinition().substring(0, 150) + " [...]");
        }
        else
        {
            return this.getDefinition();
        }
    }
    
    
    /**
     * Getter
     * @return the publicId
     */
    public String getPublicId()
    {
        return this.publicId;
    }
    
    
    /**
     * Setter
     * @param publicId the publicId to set
     */
    public void setPublicId(String publicId)
    {
        this.publicId = publicId;
    }
}
