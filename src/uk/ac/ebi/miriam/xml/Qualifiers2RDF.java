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


package uk.ac.ebi.miriam.xml;


import uk.ac.ebi.miriam.db.Qualifier;
import uk.ac.ebi.miriam.db.QualifiersDao;
import uk.ac.ebi.miriam.db.GroupQualifiers;
import uk.ac.ebi.compneur.util.DatetimeProcessor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;


/**
 * <p>Manages the RDF/XML export of the whole list of BioModels.net qualifiers.
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
 * @version 20090521
 */
public class Qualifiers2RDF
{
    private Logger logger = Logger.getLogger(Qualifiers2RDF.class);
    private String poolName = null;
    // namespace
    private static final String NAMESPACE = "http://biomodels.net/qualifiers/";
    
    
    /**
     * Default constructor.
     * @param pool name of the database pool
     */
    public Qualifiers2RDF(String pool)
    {
        this.poolName = pool;
    }
    
    
    /**
     * Official destructor.
     */
    public void finalize()
    {
        // nothing here.
    }
    
    
    /**
     * Generates the RDF export of the BioModels.net qualifiers.
     * @param name of the file where the export must be saved
     * @return whether the export is a success or not
     */
    public boolean export(String fileName)
    {
        boolean success = false;
        
        // retrieves the list of qualifiers
        QualifiersDao dao = new QualifiersDao(poolName);
        List<GroupQualifiers> listOfQualifiers = dao.getAllQualifiers();
        dao.clean();
        
        Model model = ModelFactory.createDefaultModel();
        
        // defines prefixes
        model.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
        model.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
        
        // kind of root
        Resource root = model.createResource(NAMESPACE);
        root.addProperty(DC.title, "BioModels.net Qualifiers");
        root.addProperty(DC.creator, "BioModels.net");
        root.addProperty(DC.source, "http://biomodels.net/qualifiers/");
        root.addProperty(DC.publisher, "http://www.ebi.ac.uk/miriam/");
        root.addProperty(DC.date, DatetimeProcessor.instance.formatToW3CDTF(new Date()));
        // license
        
        for (GroupQualifiers qualifiers: listOfQualifiers)
        {
            Resource node = model.createResource(qualifiers.getNamespace());
            node.addProperty(DC.title, qualifiers.getType());
            node.addProperty(DC.description, qualifiers.getDefinition());
            node.addProperty(DCTerms.isPartOf, root);
            
            for (Qualifier qualifier: qualifiers.getQualifiers())
            {
                Resource res = model.createResource(qualifiers.getNamespace() + qualifier.getName());
                res.addProperty(DC.title, qualifier.getName());
                res.addProperty(DC.description, qualifier.getDefinition());
                //res.addProperty(DCTerms.isPartOf, qualifiers.getNamespace());
                res.addProperty(DCTerms.isPartOf, node);
            }
        }
        
        /* OLDIES
            node.addProperty(VCARD.FN, "");
        */
        
        // serialisation to a file
        FileOutputStream file;
        try
        {
            // temporary file where the XML is stored
            file = new FileOutputStream(fileName);
            model.write(file, "RDF/XML");   // possible values: "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", (and "TTL") and "N3"
            file.close();
            success = true;
        }
        catch (FileNotFoundException e)
        {
            logger.error("Unable to open the file where the RDF/XML export of the BioModels.net qualifiers should be saved!");
            logger.error("File: " + fileName);
            logger.error("Error message: " + e.getMessage());
        }
        catch (IOException e)
        {
            logger.error("Unable to close the file where the RDF/XML export of the BioModels.net qualifiers should be saved!");
            logger.error("File: " + fileName);
            logger.error("Error message: " + e.getMessage());
        }
        
        return success;
    }
}
