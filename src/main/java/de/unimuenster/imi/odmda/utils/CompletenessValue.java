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

import lombok.Getter;
import lombok.Setter;

/**
 * Helper class to store the completeness values in the database and get a unified format.
 * 
 * @author Tobias Brix
 */
public class CompletenessValue {
	/** Current item/subject count */
	@Getter
	@Setter
	private int count;
	/** Expected number (100%) */
	@Getter
	@Setter
	private int total;

	/** Default Constructor */
	public CompletenessValue() {
		this.count = 0;
		this.total = 0;
	}

	/** Constructor setting the members
	 * @param count Current item/subject count
	 * @param total Expected number (100%)
	 */
	public CompletenessValue(int count, int total) {
		this.count = count;
		this.total = total;
	}

	/**
	 * Add a new completeness value to this.
	 * @param count The increase of the current count
	 * @param total the increase of the expected number
	 */
	public void addCompleteness(int count, int total) {
		this.count += count;
		this.total += total;
	}
	/**
	 * Add a new completeness value to this.
	 * @param value The value to be added. Null values are ignored
	 */
	public void addCompleteness(CompletenessValue value) {
		if(value != null) {
			this.count += value.count; 
			this.total += value.total;
		}
	}

	/** Get the current completeness as percentage. */
	public float getPercentage() {
		return (total == 0 ? 100.f : (float)count * 100.f / (float)total);
	}

	/** Nice string output */
	@Override
	public String toString() {
		return count + "#" + total;
	}
	
}
