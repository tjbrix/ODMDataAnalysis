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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

/**
 * Database class for TranslatedText. 
 * 
 * TranslatedTexts can belong to Items(Questions) or CodeListItems. When the TranslatedText belongs to an Item, 
 * codeListItem is null; when it belongs to a CodeListItem metaItem is null.
 * 
 * @author Tobias Brix
 */
@Entity
public class TranslatedText extends DatabaseEntity {
	/**
	 * Language of the text.
	 */
	@Column
	@Getter
	@Setter
	private String langAttribute;

	/**
	 * The associated text.
	 */
	@Column(columnDefinition="TEXT")
	@Getter
	@Setter
	private String text;

	/**
	 * The MetaItem, this text belongs to or null, if it belongs to a CodeListItem.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@Getter
	@Setter
	private MetaItem metaItem;

	/**
	 * The CodeListItem, this text belongs to or null, if it belongs to a MetaItem.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@Getter
	@Setter
	private CodeListItem codeListItem;
}
