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
package de.unimuenster.imi.odmda.model.clinicaldata;

import de.unimuenster.imi.odmda.model.DatabaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.Getter;
import lombok.Setter;

/**
 * Datbase base class for invalid clinical data.
 * 
 * @author Tobias Brix
 */
@Entity
public class InvalidClinicalData extends DatabaseEntity {
	/** Associated Subject. */
	@ManyToOne(optional = true, fetch=FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private ClinicalSubjectData clinicalSubjectData;

	/** Associated Subject (Key). */
	@Column
	@Getter
	@Setter
	private String subjectKey;

	/** Associated StudyEventOID. */
	@Column
	@Getter
	@Setter
	private String metaStudyEventOID;

	/** Associated StudyEvent RK. */
	@Column
	@Getter
	@Setter
	private String studyEventRepeatKey;

	/** Associated FormOID. */
	@Column
	@Getter
	@Setter
	private String metaFormOID;

	/** Associated Form RK. */
	@Column
	@Getter
	@Setter
	private String formRepeatKey;

	/** Associated ItemGroupOID. */
	@Column
	@Getter
	@Setter
	private String metaItemGroupOID;
	
	/** Associated ItemGroup RK. */
	@Column
	@Getter
	@Setter
	private String itemGroupRepeatKey;
	
	/** Associated ItemOID. */
	@Column
	@Getter
	@Setter
	private String metaItemOID;
	
	/** Current Value*/
	@Column(nullable = true, columnDefinition="TEXT")
	@Getter
	@Setter
	private String value;
	
	/** Reason why the entry is invalid. */
	@Column(columnDefinition="TEXT")
	@Getter
	@Setter
	private String reason;
}
