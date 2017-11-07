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
import de.unimuenster.imi.odmda.model.metadata.MetaStudyEvent;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Database class for clinical study event data in the clinical data.
 * 
 * @author Tobias Brix
 */
@Entity
public class ClinicalStudyEventData extends DatabaseEntity {

	/** The subject key */
	@Column
	@Getter
	@Setter
	String repeatKey;
	
	/** Pointer to the MetaStudyEvent, this data belongs to. */
	@ManyToOne(fetch=FetchType.LAZY)
	@Getter
	@Setter
	private MetaStudyEvent metaStudyEvent;

	/** Pointer to the ClinicalSubjectData, this data belongs to. */
	@ManyToOne(fetch=FetchType.LAZY)
	@Getter
	//@Setter
	private ClinicalSubjectData clinicalSubjectData;
	public void setClinicalSubjectData(ClinicalSubjectData sub) {
		if(this.clinicalSubjectData != sub) {
			this.clinicalSubjectData = sub;
			if(sub != null) sub.addClinicalStudyEventData(this);
		}
	}

	/** List of child clinical data items */
	@OneToMany(mappedBy="clinicalStudyEventData", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private Set<ClinicalFormData> clinicalFormDataList = new HashSet();
	void addClinicalFormData(ClinicalFormData data) {
		clinicalFormDataList.add(data);
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
