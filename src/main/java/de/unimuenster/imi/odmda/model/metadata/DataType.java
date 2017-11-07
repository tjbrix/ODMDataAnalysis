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
package de.unimuenster.imi.odmda.model.metadata;

/**
 * Enum for Item and CodeListItem data types.
 * 
 * @author Justin Doods
 * @author Tobias Brix
 */
public enum DataType {
	Boolean, Integer, Float, Double, String, Text, Date, Time, Datetime, Unsupported;
	
	static public DataType getDataType(String s){
		
		switch (s.toLowerCase()){
			case "boolean":
				return Boolean;
			case "integer":
				return Integer;
			case "float":
				return Float;
			case "double":
				return Double;
			case "string":
				return String;
			case "text":
				return Text;
			case "date":
				return Date;
			case "time":
				return Time;
			case "datetime":
				return Datetime;
			default:
				return Unsupported;
		}
	}
}
