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

import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalStudyEventData;
import de.unimuenster.imi.odmda.utils.CompletenessValue;
import de.unimuenster.imi.odmda.utils.CompletenessValueConverter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Database class for MetaStudyEvents. Combines the attributes from StudyEventRef and StudyEventDef.
 * 
 * @author Tobias Brix
 */
@Entity
public class MetaStudyEvent extends MetaOIDBase {
	
	public enum StudyEventType {
		SCHEDULED, UNSCHEDULED, COMMON, UNKNOWN
	};
	
	//--------------------
	//--------------------
	// Attributes
	//--------------------
	//--------------------
	/** The name of the StudyEvent. */
	@Column
	@Getter
	@Setter
	private String name;
	
	/** Is the SE mandatory? */
	@Column
	@Getter
	@Setter
	private boolean mandatory;
	
	/** Is the SE repeating? */
	@Column
	@Getter
	@Setter
	private boolean repeating;
	
	/** The type of SE */
	@Column
	@Enumerated(EnumType.STRING)
	@Getter
	@Setter
	private StudyEventType type;
	
	//--------------------
	//--------------------
	// Meta Data
	//--------------------
	//--------------------
	
	/** Pointer to the MetaStudy */
	@ManyToOne(fetch=FetchType.LAZY)
	@Getter
	@Setter
	private MetaStudy metaStudy;
	
	/** List of associated MetaForms */
	@OneToMany(mappedBy="metaStudyEvent", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@OrderBy("orderNumber")
	@Getter
	@Setter
	private List<MetaForm> metaFormList = new LinkedList();
	void addMetaItem(MetaForm mf) { metaFormList.add(mf); }
	
	//--------------------
	//--------------------
	// Clinical Data
	//--------------------
	//--------------------
	/** List of associated ClinicalStudyEventData */
	@OneToMany(mappedBy="metaStudyEvent", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Getter
	@Setter
	private Set<ClinicalStudyEventData> clinicalStudyEventDataList = new HashSet();
	public void addClinicalStudyEventData(ClinicalStudyEventData data) {
		data.setMetaStudyEvent(this);
		if (Hibernate.isInitialized(clinicalStudyEventDataList)) clinicalStudyEventDataList.add(data);
	}
	
	//--------------------
	//--------------------
	// Statistics
	//--------------------
	//--------------------
	@Column
	@Getter
	@Setter
	private int dataCounts = 0;
	
	@Column
	@Getter
	@Setter
	private int subjectCounts = 0;
	
	@Column(nullable = true)
	@Convert(converter = CompletenessValueConverter.class)
	@Getter
	@Setter
	private CompletenessValue mandatoryCompleteness = null;	
	
	@Transient
	@Getter
	@Setter
	private boolean containsInvalidValues;

	@Transient
	@Getter
	@Setter
	private boolean containsMandatoryValues;
	//----------------------
	// Needed ?
	//----------------------
	
	@Transient
	@Getter
	@Setter
	private boolean containsRepeatings;

	@Transient
	@Getter
	@Setter
	private CompletenessValue completeness;
}
