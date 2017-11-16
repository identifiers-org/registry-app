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


import uk.ac.ebi.miriam.db.DataTypeDao;
import uk.ac.ebi.miriam.db.DataTypeHibernate;
import uk.ac.ebi.miriam.tools.RdfUtilities;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;


/**
 * <p>Servlet that handles the generation of the RDF exports (RDF/XML and Turtle) on the fly for a given data collection.
 * <p>The export is available via a simple HTTP GET request.
 * <p>Optional parameters: id and format. 
 * 
 * @author Camille Laibe <camille.laibe@ebi.ac.uk>
 * @version 20130806
 */
public class ServletCollectionRdfExport extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet
{
	private static final long serialVersionUID = -203261449045573282L;
	
	/**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	String jenaFormat = null;
        String mime = null;
        String version = getServletContext().getInitParameter("version");
        String poolName = getServletContext().getInitParameter("miriam_db_pool");
        DataTypeDao dao = new DataTypeDao(poolName);
        DataTypeHibernate collection = new DataTypeHibernate();
        RdfUtilities.FORMAT format = null;
        
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
        
        // retrieves the which data collection is been viewed
        String id = request.getParameter("id");
        if (null != id)
        {
        	id = id.trim();
        }
        
        // checks the data collection exists
        if ((null != id) && (id.matches("MIR:\\d{8}")) && (dao.dataTypeExists(id)))
        {
        	// retrieves the collection information
        	collection.retrieveData(poolName, id);
        	
        	// initialise the RDF model
        	Model model = ModelFactory.createDefaultModel();
        	
/*        	Resource uriRequest = null;
         	switch(format)
         	{
         		case turtle:
         			uriRequest = model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/collections/" + collection.getId() + ".ttl");         			
         			break;
         		case rdfxml:
         			uriRequest = model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/collections/" + collection.getId() + ".rdf");
         			break;
         		default:
         			uriRequest = model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/collections/" + collection.getId() + ".rdf");
         			break;
         	}*/


        	Property foafDocument = model.createProperty("http://xmlns.com/foaf/0.1/", "Document");
         	Property foafPrimaryTopic = model.createProperty("http://xmlns.com/foaf/0.1/", "primaryTopic");
        	
        	// requested URI (includes the format): root element
         	Resource collectionRdf = model.createResource("http://www.ebi.ac.uk/miriam/" + version + "/collections/" + collection.getId());   // maybe later replace that URI by http://identifiers.org/[namespace]
/*         	uriRequest.addProperty(RDF.type, foafDocument);
         	uriRequest.addProperty(foafPrimaryTopic, collectionRdf);*/
        	
        	RdfUtilities.generateCollectionRdfModel(model, collection, format, version, poolName);
         	
        	// cleaning
            dao.clean();
            
            // sends back the generated RDF
            response.setContentType(mime);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            model.write(out, jenaFormat);   // possible values: "RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "TURTLE", (and "TTL") and "N3"
        }
        else
        {
        	// cleaning
            dao.clean();
        	
        	// sends back an error message to the user
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.print("Unable to generate a RDF export: the data collection '" + id + "' does not exist!");
        }
    }
}
