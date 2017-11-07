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

import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalFormData;
import de.unimuenster.imi.odmda.utils.CompletenessValue;
import de.unimuenster.imi.odmda.utils.CompletenessValueConverter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
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
 * Database class for Forms. Combines the attributes from FormRef and FormDef.
 * @author Tobias Brix
 *
 */
@Entity
public class MetaForm extends MetaOIDBase {
	/** Name of the MetaForm. */
	@Column
	@Getter
	@Setter
	private String name;

	/** Is the Form mandatory? */
	@Column
	@Getter
	@Setter
	private boolean mandatory;

	/** Is it Repeating? */
	@Column
	@Getter
	@Setter
	private boolean repeating;

	/** Pointer to the MetaStudyEvent */
	@ManyToOne(fetch=FetchType.LAZY)
	@Getter
	@Setter
	private MetaStudyEvent metaStudyEvent;

	/** List of associated MetaItemGroups */
	@OneToMany(mappedBy="metaForm", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@OrderBy("orderNumber")

	@Getter
	@Setter
	private List<MetaItemGroup> metaItemGroupList = new LinkedList<>();
	void addMetaItem(MetaItemGroup mig) { metaItemGroupList.add(mig); }

	//--------------------
	// Clinical Data
	//--------------------
	/** List of associated ClinicalFormData */
	@OneToMany(mappedBy="metaForm", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Getter
	@Setter
	private Set<ClinicalFormData> clinicalFormDataList = new HashSet<>();
	public void addClinicalFormData(ClinicalFormData data) {
		data.setMetaForm(this);
		if (Hibernate.isInitialized(clinicalFormDataList)) clinicalFormDataList.add(data);
	}

	//--------------------
	// Statistics
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

	//-----------------------
	// Needed?
	//-----------------------
	
	@Transient
	@Getter
	@Setter
	private boolean containsRepeatings;

	@Transient
	@Getter
	@Setter
	private CompletenessValue completeness;
}
