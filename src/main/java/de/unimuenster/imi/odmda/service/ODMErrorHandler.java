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
package de.unimuenster.imi.odmda.service;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.unimuenster.imi.odmda.model.ODMValidator;

/**
 * Manages the errors thrown by the corresponding {@link ODMParser}.
 * 
 * @author Saad Sarfraz
 */
public class ODMErrorHandler implements ErrorHandler {

	private ODMValidator odmValidator;

	public ODMErrorHandler(ODMValidator odmValidator) {
		this.odmValidator = odmValidator;
	}

	@Override
	public void warning(SAXParseException parseError) throws SAXException {
		odmValidator.addError(parseError);
	}

	@Override
	public void error(SAXParseException parseError) throws SAXException {
		odmValidator.addError(parseError);
	}

	@Override
	public void fatalError(SAXParseException parseError) throws SAXException {
		odmValidator.addError(parseError);
	}
}
