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


import uk.ac.ebi.compneur.util.DatetimeProcessor;
import uk.ac.ebi.miriam.db.GroupQualifiers;
import uk.ac.ebi.miriam.db.Qualifier;
import uk.ac.ebi.miriam.db.QualifiersDao;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;


/**
 * <p>Manages the XML export of the whole list of BioModels.net qualifiers.
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
 * @version 20130709
 */
public class Qualifiers2XML
{
    private Logger logger = Logger.getLogger(Qualifiers2XML.class);
    private String poolName = null;
    private Document dom;
    // XML tags used
    private static final String ELEMENT_ROOT = "biomodels_net";
    private static final String ELEMENT_GROUP = "qualifiers";
    private static final String ELEMENT_QUALIFIER = "qualifier";
    private static final String ELEMENT_NAME = "name";
    private static final String ELEMENT_DEF = "definition";
    // namespace
    private static final String NAMESPACE = "http://www.biomodels.net/qualifiers/";
    
    
    /**
     * Default constructor.
     * @param pool name of the database pool
     */
    public Qualifiers2XML(String pool)
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
     * Generates the XML export of the BioModels.net qualifiers.
     * Uses JAXP implementation independent manner
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
        
        // creates a document object (DOM)
        if (createDocument())
        {
            // creates the root element 
            Element root = createRoot();
            
            // fill the DOM by listing all the qualifiers of all types
            for (GroupQualifiers qualifiers: listOfQualifiers)
            {
                Element groupElt = createGroupElt(qualifiers);
                root.appendChild(groupElt);
            }
            
            // serialisation to a file
            DOMImplementationRegistry registry;
            FileOutputStream file = null;
			try
			{
				registry = DOMImplementationRegistry.newInstance();
				DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("XML 3.0 LS 3.0");
	            if (impl != null)
	            {
		            LSSerializer serialiser = impl.createLSSerializer();
		            LSOutput output = impl.createLSOutput();
		            output.setEncoding("UTF-8");
		            file = new FileOutputStream(fileName);
		            output.setByteStream(file);
		            serialiser.write(dom, output);
		            success = true;
	            }
	            else
	            {
	            	logger.error("Issue while generating the XML export of the qualifiers: no DOMImplementation found!");
	            }
			}
			catch (ClassNotFoundException e)
			{
				logger.error("ClassNotFoundException raised while generating the XML export of the qualifiers.");
				logger.error(e.getMessage());
			}
			catch (InstantiationException e)
			{
				logger.error("InstantiationException raised while generating the XML export of the qualifiers.");
				logger.error(e.getMessage());
			}
			catch (IllegalAccessException e)
			{
				logger.error("IllegalAccessException raised while generating the XML export of the qualifiers.");
				logger.error(e.getMessage());
			}
			catch (ClassCastException e)
			{
				logger.error("ClassCastException raised while generating the XML export of the qualifiers.");
				logger.error(e.getMessage());
			}
			catch (FileNotFoundException e)
			{
				logger.error("FileNotFoundException raised while generating the XML export of the qualifiers: " + fileName);
				logger.error(e.getMessage());
			}
			finally
			{
				if (null != file)
				{
					try
					{
						file.close();
					}
					catch (IOException e)
					{
						logger.error("IOException raised while closing the file (containing the XML export of the qualifiers): " + fileName);
						logger.error(e.getMessage());
						success = false;
					}
				}
				else
				{
					success = false;
				}
			}
        }
        
        return success;
    }
    
    
    /*
     * Creates the document object (DOM)
     */
    private boolean createDocument()
    {
        boolean success = false;
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder db = dbf.newDocumentBuilder();
            
            // create an instance of DOM
            dom = db.newDocument();
            
            success = true;
        }
        catch (ParserConfigurationException e)
        {
            logger.error("Error while trying to instantiate DocumentBuilder (XML export of the BioModels.net qualifiers): " + e.getMessage());
        }
        
        return success;
    }
    
    
    /*
     * Creates the root element.
     */
    private Element createRoot()
    {
        Element root = dom.createElement(ELEMENT_ROOT);
        root.setAttribute("xmlns", NAMESPACE);
        root.setAttribute("date", DatetimeProcessor.instance.formatToW3CDTF(new Date()));
        dom.appendChild(root);
        
        return root;
    }
    
    
    /*
     * Creates a new DOM element for a group of qualifiers.
     * @param group list of qualifiers corresponding to a single type
     * @return DOM element
     */
    private Element createGroupElt(GroupQualifiers group)
    {
        Element groupElt = dom.createElement(ELEMENT_GROUP);
        
        // adds attributes
        groupElt.setAttribute("type", group.getType());
        groupElt.setAttribute("namespace", group.getNamespace());
        groupElt.setAttribute("definition", group.getDefinition());
        
        // creates elements for each qualifier of the group
        for (Qualifier qualifier: group.getQualifiers())
        {
            groupElt.appendChild(createQualifierElt(qualifier));
        }
        
        return groupElt;
    }
    
    
    /*
     * Creates a new DOM element for a qualifier.
     * @param qualifier
     * @return
     */
    private Element createQualifierElt(Qualifier qualifier)
    {
        Element qualifierElt = dom.createElement(ELEMENT_QUALIFIER);
        
        // 'name' element
        Element nameElt = dom.createElement(ELEMENT_NAME);
        Text nameText = dom.createTextNode(qualifier.getName());
        nameElt.appendChild(nameText);
        qualifierElt.appendChild(nameElt);
        
        // 'definition' element
        Element defElt = dom.createElement(ELEMENT_DEF);
        Text defText = dom.createTextNode(qualifier.getDefinition());
        defElt.appendChild(defText);
        qualifierElt.appendChild(defElt);
        
        return qualifierElt;
    }
}
