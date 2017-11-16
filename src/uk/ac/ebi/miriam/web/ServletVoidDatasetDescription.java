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


import uk.ac.ebi.compneur.util.DatetimeProcessor;
import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.tools.RdfUtilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;


/**
 * Generates (on demand) the VoID dataset description for the Registry.
 * TODO: add the proper license as URI!
 * 
 * @author Camille Laibe
 * @version 20130820
 */
public class ServletVoidDatasetDescription extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final long serialVersionUID = -5092640230231731826L;
	
	
	/**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String jenaFormat = null;
        String mime = null;
        RdfUtilities.FORMAT format = null;
        String version = getServletContext().getInitParameter("version");
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        DataTypeDao dao = new DataTypeDao(poolName);
        Date lastUpdateDate = dao.getLastModifDate();
        dao.clean();
        
        // request for a specific format)
        String formatParam = request.getParameter("format");
        if (null != formatParam)
        {
        	formatParam = formatParam.trim();
        	if (formatParam.equalsIgnoreCase("turtle"))
        	{
        		jenaFormat = "TURTLE";   // could also be "TTL" (same result?)
        		mime = "text/turtle";
        		format = RdfUtilities.FORMAT.turtle;
        	}
        	else  // default: RDF/XML
        	{
        		jenaFormat = "RDF/XML-ABBREV";   // could also be "RDF/XML"
        		mime = "application/rdf+xml";
        		format = RdfUtilities.FORMAT.rdfxml;
        	}
        	// other possible values: "N-TRIPLE" and "N3"
        }
        else  // default: RDF/XML
        {
        	jenaFormat = "RDF/XML-ABBREV";   // could also be "RDF/XML"
    		mime = "application/rdf+xml";
    		format = RdfUtilities.FORMAT.rdfxml;
        }
        
        Model model = ModelFactory.createDefaultModel();
        
        // defines namespaces
		model.setNsPrefix("void", "http://rdfs.org/ns/void#");
		model.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
		model.setNsPrefix("pav", "http://purl.org/pav/");
		model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		model.setNsPrefix("voag", "http://voag.linkedmodel.org/schema/voag#");
        
		//  metadata about the file
		Resource uriRequest = null;
     	switch(format)
     	{
     		case turtle:
     			uriRequest = model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/void/turtle");         			
     			break;
     		case rdfxml:
     			uriRequest = model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/void/rdfxml");
     			break;
     		default:
     			uriRequest = model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/void/rdfxml");
     			break;
     	}
        //Resource registryVoid = model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/void");
        Property voidDatasetDescription = model.createProperty("http://rdfs.org/ns/void#", "DatasetDescription");
        Property voidDataset = model.createProperty("http://rdfs.org/ns/void#", "Dataset");
        Property voidExampleResource = model.createProperty("http://rdfs.org/ns/void#", "exampleResource");
        Property voidUriSpace = model.createProperty("http://rdfs.org/ns/void#", "uriSpace");
        Property voidDataDump = model.createProperty("http://rdfs.org/ns/void#", "dataDump");
        Property voidVocabulary = model.createProperty("http://rdfs.org/ns/void#", "vocabulary");
        Property pavCreatedBy = model.createProperty("http://purl.org/pav/", "createdBy");
        Property pavCreatedOn = model.createProperty("http://purl.org/pav/", "createdOn");
        Property pavLastUpdateOn = model.createProperty("http://purl.org/pav/", "lastUpdateOn");
        Property pavCreatedWith = model.createProperty("http://purl.org/pav/", "createdWith");
        Property foafPrimaryTopic = model.createProperty("http://xmlns.com/foaf/0.1/", "primaryTopic");
        Property foafHomepage = model.createProperty("http://xmlns.com/foaf/0.1/", "homepage");
        Property voagFrequencyOfChange = model.createProperty("http://voag.linkedmodel.org/schema/voag#", "frequencyOfChange");
        
        // requested URI (includes the format): root element
     	uriRequest.addProperty(RDF.type, voidDatasetDescription);
     	uriRequest.addProperty(DCTerms.title, model.createTypedLiteral("VoID description: Identifiers.org's Registry", XSDDatatype.XSDstring));
     	uriRequest.addProperty(DCTerms.description, model.createTypedLiteral("This is a VoID Description of the Registry underlying Identifiers.org.", XSDDatatype.XSDstring));
     	uriRequest.addProperty(pavCreatedBy, model.createResource("http://identifiers.org/"));
     	uriRequest.addProperty(pavCreatedOn, model.createTypedLiteral("2013-04-01T09:30:23+01:00", XSDDatatype.XSDdateTime));
     	uriRequest.addProperty(pavLastUpdateOn, model.createTypedLiteral("2013-07-18T13:30:07+01:00", XSDDatatype.XSDdateTime));
     	uriRequest.addProperty(DCTerms.format, model.createTypedLiteral(mime, XSDDatatype.XSDstring));
     	uriRequest.addProperty(foafPrimaryTopic, model.createResource("http://identifiers.org/registry/void#dataset"));
     	
        // description of the Registry dataset
     	Resource registryVoidDataset = model.createResource("http://identifiers.org/registry/void#dataset");
     	registryVoidDataset.addProperty(RDF.type, voidDataset);
     	registryVoidDataset.addProperty(DCTerms.title, model.createTypedLiteral("Identifiers.org Registry", XSDDatatype.XSDstring));
     	registryVoidDataset.addProperty(DCTerms.alternative, model.createTypedLiteral("MIRIAM Registry", XSDDatatype.XSDstring));
     	registryVoidDataset.addProperty(DCTerms.alternative, model.createTypedLiteral("Registry", XSDDatatype.XSDstring));
     	registryVoidDataset.addProperty(DCTerms.description, model.createTypedLiteral("The Registry provides a set of online services for the generation of unique and perennial identifiers, in the form of URIs. It provides the core data which is used by the Identifiers.org resolver.", XSDDatatype.XSDstring));
     	registryVoidDataset.addProperty(foafHomepage, model.createResource("http://identifiers.org/registry/"));
     	//registryVoidDataset.addProperty(DCTerms.license, model.createResource("http://creativecommons.org/licenses/by-sa/3.0/"));
     	registryVoidDataset.addProperty(DCTerms.license, model.createResource("http://www.ebi.ac.uk/about/terms-of-use"));
     	//registryVoidDataset.addProperty(DCTerms.rights, model.createTypedLiteral("Attribution-ShareAlike 3.0 Unported (CC BY-SA 3.0)", XSDDatatype.XSDstring));
     	registryVoidDataset.addProperty(DCTerms.rights, model.createTypedLiteral("Terms of Use of the EBI Services", XSDDatatype.XSDstring));
     	registryVoidDataset.addProperty(DCTerms.language, model.createTypedLiteral("en", XSDDatatype.XSDstring));
     	registryVoidDataset.addProperty(voagFrequencyOfChange, model.createResource("http://purl.org/cld/freq/continuous"));
     	registryVoidDataset.addProperty(DCTerms.creator, model.createTypedLiteral("BioModels.net, EMBL-EBI", XSDDatatype.XSDstring));
     	registryVoidDataset.addProperty(DCTerms.created, model.createTypedLiteral("2006-04-06T09:30:00+01:00", XSDDatatype.XSDdateTime));
     	registryVoidDataset.addProperty(DCTerms.modified, model.createTypedLiteral(DatetimeProcessor.instance.formatToW3CDTF(lastUpdateDate), XSDDatatype.XSDdateTime));
     	registryVoidDataset.addProperty(pavCreatedWith, model.createResource("http://sourceforge.net/projects/identifiers-org/"));
     	registryVoidDataset.addProperty(DCTerms.publisher, model.createResource("http://identifiers.org/registry/"));
     	registryVoidDataset.addProperty(DCTerms.subject, model.createResource("http://dbpedia.org/page/Biological_database"));
     	registryVoidDataset.addProperty(voidExampleResource, model.createResource("http://www.ebi.ac.uk/miriam/main/collections/MIR:00000008"));
     	registryVoidDataset.addProperty(voidUriSpace, model.createTypedLiteral("http://identifiers.org/", XSDDatatype.XSDstring));
     	// exports
     	registryVoidDataset.addProperty(voidDataDump, model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/export/rdfxml")); 
     	registryVoidDataset.addProperty(voidDataDump, model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/export/turtle"));
     	registryVoidDataset.addProperty(voidDataDump, model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/export/xml/"));
     	// vocabularies
     	registryVoidDataset.addProperty(voidVocabulary, model.createResource("http://rdfs.org/ns/void#"));
     	registryVoidDataset.addProperty(voidVocabulary, model.createResource("http://purl.org/dc/terms/"));
     	registryVoidDataset.addProperty(voidVocabulary, model.createResource("http://purl.org/pav/"));
     	registryVoidDataset.addProperty(voidVocabulary, model.createResource("http://xmlns.com/foaf/0.1/"));
     	registryVoidDataset.addProperty(voidVocabulary, model.createResource("http://voag.linkedmodel.org/schema/voag#"));
        
        response.setContentType(mime);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        model.write(out, jenaFormat);   // possible values: "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", (and "TTL") and "N3"
    }
}
