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
import de.unimuenster.imi.odmda.model.metadata.MetaForm;
import de.unimuenster.imi.odmda.model.metadata.MetaItem;
import de.unimuenster.imi.odmda.model.metadata.MetaItemGroup;
import de.unimuenster.imi.odmda.model.metadata.MetaStudyEvent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Datbase base class for invalid clinical data.
 * 
 * @author Tobias Brix
 */
@Entity
public class ClinicalData extends DatabaseEntity {
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

	/** Associated StudyEvent. */
	@ManyToOne(optional = true, fetch=FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private MetaStudyEvent metaStudyEvent;

	/** Associated StudyEvent RK. */
	@Column
	@Getter
	@Setter
	private String studyEventRepeatKey;

	/** Associated Form. */
	@ManyToOne(optional = true, fetch=FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private MetaForm metaForm;

	/** Associated Form RK. */
	@Column
	@Getter
	@Setter
	private String formRepeatKey;

	/** Associated ItemGroup. */
	@ManyToOne(optional = true, fetch=FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private MetaItemGroup metaItemGroup;

	/** Associated ItemGroup RK. */
	@Column
	@Getter
	@Setter
	private String itemGroupRepeatKey;

	/** Associated Item. */
	@ManyToOne(optional = true, fetch=FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private MetaItem metaItem;

	/** Current Value*/
	@Column(nullable = true, columnDefinition="TEXT")
	@Getter
	@Setter
	private String value;
}
