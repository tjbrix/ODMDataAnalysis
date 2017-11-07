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
package de.unimuenster.imi.odmda.utils;

import javax.persistence.AttributeConverter;

/**
 * Used to store a CompletenessValue inline.
 * This class is needed to store a custom object as database attribut.
 * 
 * @author Tobias Brix
 */
public class CompletenessValueConverter implements AttributeConverter<CompletenessValue, String>{

	@Override
	public String convertToDatabaseColumn(CompletenessValue value) {
		if(value == null) return null;
		return value.toString();
	}

	@Override
	public CompletenessValue convertToEntityAttribute(String valueString) {
		if(valueString == null) return null;
		String[] value = valueString.split("#");
		return new CompletenessValue(Integer.parseInt(value[0]),Integer.parseInt(value[1]));
	}
}
