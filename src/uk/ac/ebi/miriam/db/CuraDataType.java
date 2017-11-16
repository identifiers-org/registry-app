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


package uk.ac.ebi.miriam.db;


import java.util.Date;


/**
 * <p>Object which stores all the information about a data collection for curation purposes.
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
 * @version 20080518
 */
public class CuraDataType extends DataCollection
{
    private String state;
    private Date submissionDate;
    private String comment;
    private String subInfo;
    private String publicId;
    
    
    
    public CuraDataType()
    {
        super();
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
     * Setter
     * @param submissionDate the submissionDate to set
     */
    public void setSubmissionDate(Date submissionDate)
    {
        this.submissionDate = submissionDate;
    }
    
    
    /**
     * Getter
     * @return the comment
     */
    public String getComment()
    {
        return this.comment;
    }
    
    
    /**
     * Setter
     * @param comment the comment to set
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }
    
    
    /**
     * Getter
     * @return the subInfo
     */
    public String getSubInfo()
    {
        return this.subInfo;
    }
    
    
    /**
     * Setter
     * @param subInfo the subInfo to set
     */
    public void setSubInfo(String subInfo)
    {
        this.subInfo = subInfo;
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
