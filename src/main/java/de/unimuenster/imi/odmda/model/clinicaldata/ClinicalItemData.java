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
import de.unimuenster.imi.odmda.model.metadata.MetaItem;
import static java.util.Objects.isNull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * Database class for clinical data items in the clinical data.
 * @author Tobias Brix
 */
@Entity
public class ClinicalItemData extends DatabaseEntity {

	/** Pointer to the MetaItem, this data belongs to. */
	@ManyToOne(fetch=FetchType.LAZY)
	@Getter
	@Setter
	private MetaItem metaItem;
	
	/** Pointer to the ClinicalItemGroupData, this data belongs to. */
	@ManyToOne(fetch=FetchType.LAZY)
	@Getter
	//@Setter
	private ClinicalItemGroupData clinicalItemGroupData;
	public void setClinicalItemGroupData(ClinicalItemGroupData cig) {
		assert !isNull(cig) : "ClinicalItemGroupData is not allowed to be set to null";
		this.clinicalItemGroupData = cig;
		cig.addClinicalItemData(this);
	}

	/** Current Value*/
	@Column(nullable = true, columnDefinition="TEXT")
	@Getter
	@Setter
	private String value;

	//--------------
	//  Statistics
	//--------------
	/** Needed to calculate the mandatory completeness. */
	@Column(nullable = true)
	@Getter
	@Setter
	private Boolean completeness;
	
}
