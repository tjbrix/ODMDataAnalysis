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


import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * Database class for ODM-Files to ensure multi-user support.
 * 
 * @author Tobias Brix
 */
@Entity
public class ODMFile implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Getter
	@Setter
	private long id;

	@Column(columnDefinition="TEXT")
	@Getter
	@Setter
	private String OID;

	/**
	 * Used to delete old database entries after 24 hours.
	 */
	@Column
	@Getter
	@Setter
	private Timestamp uploadTime; 

	/** Constructor needed to get the upload time. */
	public ODMFile(String oID) {
		this.OID = oID;
		this.uploadTime = new Timestamp(System.currentTimeMillis());
	}
}
