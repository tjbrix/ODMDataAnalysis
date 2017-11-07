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
package de.unimuenster.imi.odmda.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import lombok.Getter;


/**
 * Class implementation for parsing an ODM files with SAX.
 * 
 * It is used to determine the number of Subjects to have a nice progress bar.
 * 
 * @author Tobias Brix
 *
 */
public class ODMPreProcessSaxParser extends DefaultHandler {

	/** Number of Items */
	@Getter
	private int numMetaItems;

	/** Number of Subjects */
	@Getter
	private int numClinicalSubjects;

	/**
	 * The constructor initializes all variables.
	 */
	public ODMPreProcessSaxParser() {		
		this.numMetaItems = 0;
		this.numClinicalSubjects = 0;
	}

	/**
	 * method gets called every time a start element is being processed
	 * @param uri
	 * @param localName
	 * @param qName
	 * @param attributes
	 * @throws org.xml.sax.SAXException
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (localName) {
			case "ItemRef":
				numMetaItems++;
				break;
			case "SubjectData":
				numClinicalSubjects++;
				break;            
			default:
				//nothing yet
		}
	}

	/**
	 * Method gets called every time a end element is being processed. Mostly
	 * resets variables or stores elements to the db.
	 * @param uri
	 * @param localName
	 * @param qName
	 * @throws org.xml.sax.SAXException
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {}
}
