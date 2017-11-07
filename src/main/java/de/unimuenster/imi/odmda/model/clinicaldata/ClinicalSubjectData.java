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
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Database class for subjects in the clinical data.
 * 
 * @author Tobias Brix
 */
@Entity
public class ClinicalSubjectData extends DatabaseEntity {

	/** The subject key */
	@Column
	@Getter
	@Setter
	String subjectKey;

	/** List of child clinical data items */
	@OneToMany(mappedBy="clinicalSubjectData", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private Set<ClinicalStudyEventData> clinicalStudyEventDataList = new HashSet();
	void addClinicalStudyEventData(ClinicalStudyEventData data) {
		clinicalStudyEventDataList.add(data);
	}

	/** List of all clinical data used to enable buld inserts of hibernate */
	@OneToMany(mappedBy="clinicalSubjectData", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private Set<ClinicalData> clinicalDataList = new HashSet();
	public void addClinicalData(ClinicalData data) {
		clinicalDataList.add(data);
	}

	/** List of all invalid clinical data used to enable buld inserts of hibernate */
	@OneToMany(mappedBy="clinicalSubjectData", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private Set<InvalidClinicalData> invalidClinicalDataList = new HashSet();
	public void addInvalidClinicalData(InvalidClinicalData data) {
		invalidClinicalDataList.add(data);
	}

	//--------------
	//  Statistics
	//--------------
	/** Needed to calculate the mandatory completeness. */
	@Column(nullable = true)
	@Getter
	@Setter
	private Boolean completeness;
}
