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


import uk.ac.ebi.compneur.util.DatetimeProcessor;
import uk.ac.ebi.miriam.db.DataCollection;
import uk.ac.ebi.miriam.db.ResourceDao;
import uk.ac.ebi.miriam.db.Tag;

import org.apache.http.client.utils.URIBuilder;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.VCARD;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * Utilities methods for RDF handling.
 * 
 * @author Camille Laibe
 * @version 20130827
 */
public class RdfUtilities
{
	private static final String IDENTIFIER_TOKEN_1 = "_*_TOKEN-IDENTIFIER_*_";
	private static final String IDENTIFIER_TOKEN_2 = "$id";
	public static enum FORMAT {rdfxml, turtle};
	
	/* Should we consider the following approach?
	private static Model model = ModelFactory.createDefaultModel();
	public static final Property foafDocument = model.createProperty("http://xmlns.com/foaf/0.1/", "Document");
    public static final Property foafPrimaryTopic = model.createProperty("http://xmlns.com/foaf/0.1/", "primaryTopic");
	*/
	
	/**
	 * Creates the RDF model of a data collection.
	 * @param model RDF model
	 * @param collection data collection
	 * @param format requested format for the RDF: RDF/XML, Turtle, ...
	 * @param version version of the application running (demo, main, ...)
	 * @param dbPool database pool name
	 * @return
	 */
	public static void generateCollectionRdfModel(Model model, DataCollection collection, FORMAT format, String version, String dbPool)
	{
		ResourceDao resDao = new ResourceDao(dbPool);
    	
        // defines namespaces
     	model.setNsPrefix("void", "http://rdfs.org/ns/void#");
     	model.setNsPrefix("dcterms", DCTerms.getURI());   // "http://purl.org/dc/terms/"
     	model.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
     	model.setNsPrefix("idot", "http://identifiers.org/terms#");
     	model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
     	
     	// metadata about the Registry/Identifiers.org
     	Resource collectionRdf = model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/collections/" + collection.getId());   // maybe later replace that URI by http://identifiers.org/[namespace]
     	//Property dcatCatalog = model.createProperty("http://www.w3.org/ns/dcat#", "Catalog");
     	Resource dcatCatalogRecord = model.createResource("http://www.w3.org/ns/dcat#" + "CatalogRecord");   // 'Dataset' represents the actual dataset as published by the dataset publisher
     	Property dcatTitle = model.createProperty("http://www.w3.org/ns/dcat#", "title");
     	Property dcatDescription = model.createProperty("http://www.w3.org/ns/dcat#", "description");
     	Property dcatIssued = model.createProperty("http://www.w3.org/ns/dcat#", "issued");
     	Property dcatModified = model.createProperty("http://www.w3.org/ns/dcat#", "modified");
     	Property dcatIdentifier = model.createProperty("http://www.w3.org/ns/dcat#", "identifier");
     	Property dcatKeyword = model.createProperty("http://www.w3.org/ns/dcat#", "keyword");
     	Property dcatdistribution = model.createProperty("http://www.w3.org/ns/dcat#", "distribution");
     	Property dcatDistribution = model.createProperty("http://www.w3.org/ns/dcat#", "Distribution");
     	Property dcatAccessURL = model.createProperty("http://www.w3.org/ns/dcat#", "accessURL");
     	Property dcatMediaType = model.createProperty("http://www.w3.org/ns/dcat#", "mediaType");
     	Property dcatPublisher = model.createProperty("http://www.w3.org/ns/dcat#", "publisher");
     	Property dcatLandingPage = model.createProperty("http://www.w3.org/ns/dcat#", "landingPage");
     	Property voidUriSpace = model.createProperty("http://rdfs.org/ns/void#", "uriSpace");
     	Property voidExampleResource = model.createProperty("http://rdfs.org/ns/void#", "exampleResource");
     	
     	Property idotNamespace = model.createProperty("http://identifiers.org/terms#", "namespace");
     	Property idotIdRegexPattern = model.createProperty("http://identifiers.org/terms#", "idRegexPattern");
     	Property idotIdObsolete = model.createProperty("http://identifiers.org/terms#", "obsolete");
     	Property idotState = model.createProperty("http://identifiers.org/terms#", "state");
     	Property idotReliability = model.createProperty("http://identifiers.org/terms#", "reliability");
     	
     	// root URI (no format info)
     	collectionRdf.addProperty(RDF.type, dcatCatalogRecord);
     	
     	collectionRdf.addProperty(dcatIdentifier, model.createTypedLiteral(collection.getId(), XSDDatatype.XSDstring));
     	collectionRdf.addProperty(dcatTitle, model.createTypedLiteral(collection.getName(), XSDDatatype.XSDstring));
     	collectionRdf.addProperty(dcatDescription, model.createTypedLiteral(collection.getDefinition(), XSDDatatype.XSDstring));
     	collectionRdf.addProperty(dcatIssued, model.createTypedLiteral(DatetimeProcessor.instance.formatToW3CDTF(collection.getDateCreation()), XSDDatatype.XSDdateTime));
     	collectionRdf.addProperty(dcatModified, model.createTypedLiteral(DatetimeProcessor.instance.formatToW3CDTF(collection.getDateModification()), XSDDatatype.XSDdateTime));
			/* TODO:
			 dct:publisher
			 dcat:landingPage
			.addProperty(dct:license, model.createTypedLiteral("TBD", XSDDatatype.XSDstring))
			 dct:rights
			 dcat:contactPoint
			 dcterms:subject (using EDAM?)
			*/
     	collectionRdf.addProperty(idotNamespace, model.createTypedLiteral(collection.getNamespace(), XSDDatatype.XSDstring));
     	collectionRdf.addProperty(voidUriSpace, model.createResource("http://identifiers.org/" + collection.getNamespace() + "/"));
     	collectionRdf.addProperty(idotIdRegexPattern, model.createTypedLiteral(collection.getRegexp(), XSDDatatype.XSDstring));
     	if (collection.isObsolete())
     	{
     		collectionRdf.addProperty(idotIdObsolete, model.createTypedLiteral(true, XSDDatatype.XSDboolean));
     	}
     	collectionRdf.addProperty(voidExampleResource, model.createTypedLiteral(collection.getResource(0).getExample(), XSDDatatype.XSDstring));
     	for (String synonym: collection.getSynonyms())
     	{
     		collectionRdf.addProperty(DCTerms.alternative, model.createTypedLiteral(synonym, XSDDatatype.XSDstring));
     	}
     	if (null != collection.getTags())
     	{
         	for (Tag tag: collection.getTags())
         	{
         		collectionRdf.addProperty(dcatKeyword, model.createTypedLiteral(tag.getName(), XSDDatatype.XSDstring));
         	}
     	}
     	if (null != collection.getResources())
     	{
         	for (uk.ac.ebi.miriam.db.Resource res: collection.getResources())
         	{
         		if (! res.isObsolete())
         		{
         			Resource accessURI = model.createResource("http://identifiers.org/miriam.resource/" + res.getId() + "#application/xhtml+xml");
         			collectionRdf.addProperty(dcatdistribution, accessURI);
         		}
         		// TODO: obsolete resources?
         	}
     	}
     	// TODO: obsolete/alternative URIs/namespaces?
     	
     	// physical locations
     	if (null != collection.getResources())
     	{
         	for (uk.ac.ebi.miriam.db.Resource res: collection.getResources())
         	{
         		if (! res.isObsolete())
         		{
         			Resource accessURI = model.createResource("http://identifiers.org/miriam.resource/" + res.getId() + "#application/xhtml+xml");
         			Resource resourceURI = model.createResource("http://identifiers.org/miriam.resource/" + res.getId());
         			
         			// access details
         			accessURI.addProperty(RDF.type, dcatDistribution);
         			String accessURL = encodeUrl4Rdf(res.getUrl_prefix() + IDENTIFIER_TOKEN_1 + res.getUrl_suffix());
         			accessURL = accessURL.replace(IDENTIFIER_TOKEN_1, IDENTIFIER_TOKEN_2);   // if using '$id' during the encoding, it would get encoded
         			accessURI.addProperty(dcatAccessURL, model.createResource(accessURL));
         			accessURI.addProperty(dcatMediaType, model.createTypedLiteral("application/xhtml+xml", XSDDatatype.XSDstring));
         			accessURI.addProperty(dcatPublisher, resourceURI);
         			
         			// resource details
         			Resource resourceDetails = model.createResource("http://identifiers.org/miriam.resource/" + res.getId());
         			resourceDetails.addProperty(DCTerms.title, model.createTypedLiteral(res.getInfo(), XSDDatatype.XSDstring));
         			resourceDetails.addProperty(VCARD.Orgname, model.createTypedLiteral(res.getInstitution(), XSDDatatype.XSDstring));
         			if ((null != res.getLocation()) && (! res.getLocation().isEmpty()))
         			{
         				resourceDetails.addProperty(VCARD.Country, model.createTypedLiteral(res.getLocation(), XSDDatatype.XSDstring));
         			}
         			resourceDetails.addProperty(dcatLandingPage, model.createResource(res.getUrl_root()));
         			resourceDetails.addProperty(idotState, model.createTypedLiteral(ResourceDao.getStateDesc(resDao.getState(res.getId())), XSDDatatype.XSDstring));
         			resourceDetails.addProperty(idotReliability, model.createTypedLiteral(res.getReliability(), XSDDatatype.XSDdecimal));
         		}
         		// TODO: obsolete resources?
         	}
     	}
     	
     	// cleaning
     	resDao.clean();
	}
	
	
	/**
	 * Performs all the necessary encoding to have a fully valid URL to be used in a RDF/XML file.
	 * @param url potentially invalid URL
	 * @return valid URI for use in a RDF/XML file.
	 */
	private static String encodeUrl4Rdf(String url)
    {
		String encodedUrl = null;
	    URL tmpUrl = null;
        URI tmpUri = null;
        
		try
		{
			tmpUrl = new URL(url);
			URIBuilder builder = new URIBuilder();
			builder.setScheme(tmpUrl.getProtocol());
			builder.setHost(tmpUrl.getHost());
			builder.setPort(tmpUrl.getPort());
			builder.setPath(tmpUrl.getPath());
			builder.setQuery(tmpUrl.getQuery());
			builder.setFragment(tmpUrl.getRef());
			
			tmpUri = builder.build();
		}
		catch (MalformedURLException e)
        {
			System.out.println("MalformedURLException raised while creating a URL from: " + url);
			System.out.println(e.getMessage());
        }
		catch (URISyntaxException e)
		{
			System.out.println("URISyntaxException raised when building a URI from: " + url);
			System.out.println(e.getMessage());
		}
		
		encodedUrl = tmpUri.toASCIIString();
		
        return encodedUrl;
    }
}
