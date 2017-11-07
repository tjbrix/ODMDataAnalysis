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

import de.unimuenster.imi.odmda.model.DatabaseEntity;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Database class for CodeListItems.
 * 
 * EnumeratedItems are stored as CodelistItems without TranslatedText.
 * 
 * @author Tobias Brix
 */
@Entity
public class CodeListItem extends DatabaseEntity {
	
	/** CodeList this item belongs to. */
	@ManyToOne(fetch=FetchType.LAZY)
	@Getter
	@Setter
	private CodeList codeList;

	/** The coded value. */
	@Column
	@Getter
	@Setter
	private String codedValue;

	/** The optinal rank. */
	@Column(nullable = true)
	@Getter
	@Setter
	private float rank;

	/** The optinal order number. */
	@Column(nullable = true)
	@Getter
	@Setter
	private int orderNumber;

	/** TranslatedTexts to this item. */
	@OneToMany(mappedBy="codeListItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@Getter
	@Setter
	private Set<TranslatedText> translatedTextList = new LinkedHashSet();
	public void addTranslatedText(TranslatedText data) {
		data.setCodeListItem(this);
		if (Hibernate.isInitialized(translatedTextList)) translatedTextList.add(data);
	}
}
