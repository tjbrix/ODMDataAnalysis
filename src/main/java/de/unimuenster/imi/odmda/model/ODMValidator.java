/**
 * ODM Data Analysis - a tool for the automatic validation, monitoring and
 * generation of generic descriptive statistics of clinical data.
 * 
 * Copyright (c) 2017 Institut für Medizinische Informatik, Münster
 *
 * ODM Data Analysis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, version 3.
 *
 * ODM Data Analysis is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for 
 * more details.
 *
 * You should have received a copy of the GNU General Public License in the file
 * COPYING along with ODM Data Analysis. If not, see <http://www.gnu.org/licenses/>.
 */
package de.unimuenster.imi.odmda.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import de.unimuenster.imi.odmda.service.ODMErrorHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

/**
 * class handles ODM validation tasks against uploaded file
 * 
 * @author Saad Sarfraz
 *
 */
public class ODMValidator {

	private static final Logger LOGGER = LogManager.getLogger(ODMValidator.class);

	private static final String[] SUPPORTED_ODM_VERSIONS = {"1-3-0","1-3-1","1-3-2"}; 

	private List<SAXParseException> parseErrors = new ArrayList<>();

	private ArrayList<String> errorMsgs = new ArrayList<>();
 
	/**
	 * 
	 * @param file
	 * @return List of Error Messages to be displayed to user 
	 */
	public ArrayList<String> validateXML(MultipartFile file){

		LOGGER.info("File for validation received");

		String version;
		try {
			version = parseAndFindODMVersion(file);

			//replacing . with - to match with the schema file names
			String vFormatted = changeFormat(version);

			LOGGER.info("Version for this file is "+ version);

			switch(vFormatted){
				case "1-3-0":
				case "1-3-1":
				case "1-3-2":
					try{
						ValidateXMLWithSchema(file,vFormatted);
					}catch (SAXParseException e) {
						this.parseErrors.add(e);
					}catch(Exception e){
						e.printStackTrace();
					}
					break;
				case "1-3": 
					List<SAXParseException> parseErrorsV130 = new ArrayList<>();         
//						LOGGER.info("ODM Version is 1.3 with error size " + errorMsgs.size() +" : " + parseErrors.size() );
					for (String v : SUPPORTED_ODM_VERSIONS){
						try{
							int errorSizeBefore = parseErrors.size();        
							ValidateXMLWithSchema(file,v);
							int errorSizeAfter = parseErrors.size();

							if(v.equals("1-3-0")){
								parseErrorsV130.addAll(parseErrors);
							}

							//no errors were produced during validation against this schema instance
							//(during this loop) clear errors and proceed
							if (errorSizeBefore==errorSizeAfter){ 
								parseErrors.clear();
								LOGGER.info("error removed");
								break;
							}
						}catch (SAXParseException e) {
							this.parseErrors.add(e);
							LOGGER.info("error caught");
						}catch(Exception e){
							e.printStackTrace();
						}
					}

					//Failed to validate against any of the schemas
					//display only the errors for v 1.3.0
					if(parseErrors.size()>0){
						parseErrors.clear();
						errorMsgs.add( "Fail to validate against supported ODM Schema"
								+ " versions <i>1.3.0, 1.3.1 and 1.3.2</i>. Please specifiy "
								+ "the exact <strong>ODMVersion</strong> in XML file to see specific syntatic errors."
								+ "<br>Following errors were found for <strong>ODM Version 1.3.0</strong> (default).");

						parseErrors.addAll(parseErrorsV130);
					}
					break;
				case "":
					errorMsgs.add("ODM Version not defined. Please note that only "
							+ "version 1.3/1.3.0, 1.3.1, 1.3.2 are supported");
					break;
				default:
					errorMsgs.add("ODM Version:" +version+ " is not Supported");
					break;
			}
		} catch (SAXParseException e1) {
			// TODO Auto-generated catch block
			parseErrors.add(e1);
			LOGGER.error(e1.getMessage());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			SAXParseException temp = new SAXParseException("File is syntactically incorrect because " + e1.getLocalizedMessage(),null);
			parseErrors.add(temp);
		}

		return clearAndReturnErrorMessages();
	}


	private ArrayList<String> clearAndReturnErrorMessages() {      
		for(SAXParseException e : parseErrors){
			errorMsgs.add(e.getMessage() + 
				  "<br> Line : "+ e.getLineNumber() + 
				  ", Column : "+e.getColumnNumber());
		}

		ArrayList<String> requestedEMsgs = new ArrayList<>(errorMsgs);

		errorMsgs.clear();
		parseErrors.clear();

		return requestedEMsgs;
	}

	public void addError(SAXParseException parseError){
		this.parseErrors.add(parseError);
	}

	/**
	 * Returns the schema source based on specifies version
	 * @param version only possible values are 0,1,2
	 * @return
	 */
	private Source[] getODMSchemaSource(String version){
		//path to xsd file stored in resources
		final String baseXSDPath = ODMValidator.class.getClassLoader().getResource("schema-odm/"+version+"/ODM"+version+".xsd").getPath();
		final String xmlXSDPath = ODMValidator.class.getClassLoader().getResource("schema-odm/"+version+"/xml.xsd").getPath();
		final String coreXSDPath = ODMValidator.class.getClassLoader().getResource("schema-odm/"+version+"/xmldsig-core-schema.xsd").getPath();
		final String foundationXSDPath = ODMValidator.class.getClassLoader().getResource("schema-odm/"+version+"/ODM"+version+"-foundation.xsd").getPath();
		final String xlinkXSDPath = ODMValidator.class.getClassLoader().getResource("schema-odm/"+version+"/xlink.xsd").getPath();

		//Creating file objects
		File baseXSDFile = new File(baseXSDPath);
		File xmlXSDFile = new File(xmlXSDPath);
		File coreXSDFile = new File(coreXSDPath);
		File foundationXSDFile = new File(foundationXSDPath);
		File xLinkFile = new File(xlinkXSDPath);

		// create a SchemaFactory capable of understanding WXS schemas
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// load a WXS schema, represented by a Schema instance
		Source [] schemaFile =   new Source [] {
												new StreamSource(baseXSDFile),
												new StreamSource(foundationXSDFile),
												new StreamSource(xLinkFile),
												new StreamSource(xmlXSDFile),
												new StreamSource(coreXSDFile)
												};

		return schemaFile;
	}

	/**
	 * Parses the XML file and returns the ODM 'ODMVersion' found in ODM tag
	 * @param file
	 * @return ODM version e.g. 1.3.1, if not found then returns ""
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	private String parseAndFindODMVersion(MultipartFile file) throws ParserConfigurationException, SAXException, IOException{

		String odmVersion = "";

		SAXParserFactory factory = SAXParserFactory.newInstance();
		javax.xml.parsers.SAXParser saxParser = factory.newSAXParser();

		class ODMHandler extends DefaultHandler {

			private String version;
			
			@Override
			public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {

				if (qName.equalsIgnoreCase("ODM")) {
					version = attributes.getValue("ODMVersion");
				}
			}

			public String getVersion(){
				return version;
			}
		}

		ODMHandler handler = new ODMHandler();

		saxParser.parse(file.getInputStream(), handler);

		odmVersion = handler.getVersion();

		return new String(odmVersion);
	}

	private String changeFormat(String version){
		return version.replace(".", "-");
	}

	/**
	 * validates given file with the specified schema type
	 * @param file
	 * @param vFormatted possible values are 'SUPPORTED_ODM_VERSIONS'
	 * @return true if succeeded else return false
	 * @throws SAXException
	 * @throws IOException
	 */
	private boolean ValidateXMLWithSchema(MultipartFile file,String vFormatted ) throws SAXException, IOException{

		boolean result= false;

		// create a SchemaFactory capable of understanding WXS schemas
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		//creating a new schema from loaded files
		Schema schema = factory.newSchema(getODMSchemaSource(vFormatted));

		// create a Validator instance, which can be used to validate an instance document
		Validator validator = schema.newValidator();

		//ODMErrorHandler allows to collect all errors found in XML File
		validator.setErrorHandler(new ODMErrorHandler(this));

		LOGGER.info("Validation starts");

		// validate the inputstream source       
		validator.validate(new StreamSource(file.getInputStream()));

		result = true;

		LOGGER.info("Validation ended");

		return result;
	}
}
