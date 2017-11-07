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

import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalItemData;
import de.unimuenster.imi.odmda.utils.CompletenessValue;
import de.unimuenster.imi.odmda.utils.CompletenessValueConverter;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Datebase class of meta-data items. 
 * 
 * Combines the attributes from ItemRef and ItemDef
 * 
 * @author Tobias Brix
 */
@Entity
public class MetaItem extends MetaOIDBase {
	/** Name of the MetaItem. */
	@Column
	@Getter
	@Setter
	private String name;

	/** DateType of the MetaItem. */
	@Enumerated(EnumType.ORDINAL)
	@Column
	@Getter
	@Setter
	private DataType dataType;

	/** Is the item mandatory? */
	@Column
	@Getter
	@Setter
	private boolean mandatory;
	
	/** Pointer to the MetaItemGroup */
	@ManyToOne(fetch=FetchType.LAZY)
	@Getter
	@Setter
	private MetaItemGroup metaItemGroup;

	/** List of associated translated texts (related to the question) */
	@OneToMany(mappedBy="metaItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private Set<TranslatedText> translatedTextList = new LinkedHashSet();
	public void addTranslatedText(TranslatedText data) {
		data.setMetaItem(this);
		if (Hibernate.isInitialized(translatedTextList)) translatedTextList.add(data);
	}

	/** CodeList of the MetaItem. (optional) */
	@ManyToOne(optional = true, fetch=FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private CodeList codeList;	

	//--------------------
	// Clinical Data
	//--------------------
	@OneToMany(mappedBy="metaItem", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@LazyCollection(LazyCollectionOption.EXTRA)
	@Getter
	@Setter
	private Set<ClinicalItemData> clinicalItemDataList = new HashSet<>();
	public void addClinicalItemData(ClinicalItemData data) {
		data.setMetaItem(this);
		if (Hibernate.isInitialized(clinicalItemDataList)) clinicalItemDataList.add(data);
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

	//--------------
	// Needed ?
	//--------------
	@Transient
	@Getter
	@Setter
	private CompletenessValue completeness = null;

	@Transient
	@Getter
	@Setter
	private boolean containsInvalidValues;
}
