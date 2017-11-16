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
import uk.ac.ebi.miriam.db.DataCollection;
import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.tools.RdfUtilities;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

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
 * <p>Servlet that handles the generation of the RDF exports (RDF/XML and Turtle) on the fly for the whole Registry.
 * <p>The export is available via a simple HTTP GET request.
 * <p>Optional parameters: format.
 * 
 * @author Camille Laibe <camille.laibe@ebi.ac.uk>
 * @version 20130827
*/
public class ServletWholeRdfExport extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final long serialVersionUID = 442876808667420203L;
	
	
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
        
        // request for a specific format
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
        		jenaFormat = "RDF/XML";   // could also be "RDF/XML-ABBREV": not recommended for serialising large models from a persistent database
        		mime = "application/rdf+xml";
        		format = RdfUtilities.FORMAT.rdfxml;
        	}
        	// other possible values: "N-TRIPLE" and "N3"
        }
        else  // default: RDF/XML
        {
        	jenaFormat = "RDF/XML";   // could also be "RDF/XML-ABBREV": not recommended for serialising large models from a persistent database
    		mime = "application/rdf+xml";
    		format = RdfUtilities.FORMAT.rdfxml;
        }
        
        Model model = ModelFactory.createDefaultModel();
        
        // defines namespaces
     	model.setNsPrefix("void", "http://rdfs.org/ns/void#");
     	model.setNsPrefix("dcterms", "http://purl.org/dc/terms/");
     	model.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
     	model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
     	
     	Resource uriRequest = null;
     	switch(format)
     	{
     		case turtle:
     			uriRequest = model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/export/turtle");         			
     			break;
     		case rdfxml:
     			uriRequest = model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/export/rdfxml");
     			break;
     		default:
     			uriRequest = model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/export/rdfxml");
     			break;
     	}
     	Resource registry = model.createResource("http://identifiers.org/registry/");
     	Property dcatCatalog = model.createProperty("http://www.w3.org/ns/dcat#", "Catalog");
     	Property foafDocument = model.createProperty("http://xmlns.com/foaf/0.1/", "Document");
        Property foafPrimaryTopic = model.createProperty("http://xmlns.com/foaf/0.1/", "primaryTopic");
        Property voidInDataset = model.createProperty("http://rdfs.org/ns/void#", "inDataset");
        
     	// requested URI (includes the format): root element
     	uriRequest.addProperty(RDF.type, foafDocument);
     	uriRequest.addProperty(foafPrimaryTopic, registry);
     	
     	// root URI (no format info)
     	// metadata about the Registry/Identifiers.org
     	registry.addProperty(RDF.type, dcatCatalog);
     	registry.addProperty(DCTerms.title, model.createTypedLiteral("Identifiers.org's Registry", XSDDatatype.XSDstring));
     	registry.addProperty(DCTerms.description, model.createTypedLiteral("Identifiers.org is a system providing resolvable persistent URIs used to identify data for the scientific community. It relies on the information stored in the Registry (a list of data collections and the physical locations where information can be accessed).", XSDDatatype.XSDstring));
     	registry.addProperty(DCTerms.license, model.createTypedLiteral("TBD", XSDDatatype.XSDstring));
     	registry.addProperty(DCTerms.modified, model.createTypedLiteral(DatetimeProcessor.instance.formatToW3CDTF(lastUpdateDate), XSDDatatype.XSDdateTime));
     	registry.addProperty(DCTerms.publisher, model.createResource("http://identifiers.org/"));
     	
     	// list all collections
     	List<DataCollection> collections = dao.getDataCollections(poolName);
     	for (DataCollection collection: collections)
     	{
     		Resource collectionURI = model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/collections/" + collection.getId());   // maybe later replace that URI by http://identifiers.org/[namespace]
     		//Resource dcatCatalogRecord = model.createResource("http://www.w3.org/ns/dcat#" + "CatalogRecord");
     		//collectionURI.addProperty(RDF.type, dcatCatalogRecord);
     		
     		RdfUtilities.generateCollectionRdfModel(model, collection, format, version, poolName);
     		
     		collectionURI.addProperty(voidInDataset, registry);
     	}
     	
     	// cleaning
     	dao.clean();
     	
     	response.setContentType(mime);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        model.write(out, jenaFormat);   // possible values: "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", (and "TTL") and "N3"
    }
}
