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

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * helper class that checks if a file was selected. copied from somewhere.
 * 
 * @author <Unknwon> 
 *
 */
public class FileValidator implements Validator {
	
	public boolean supports(Class<?> paramClass){
		return File.class.equals(paramClass);
	}

	public void validate(Object obj, Errors errors){
		File file = (File) obj;
		if(file.getFile().getSize() == 0){
			errors.rejectValue("file","valid.file");
		}
	}
}
