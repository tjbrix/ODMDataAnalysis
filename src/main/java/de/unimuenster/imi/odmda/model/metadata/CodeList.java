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

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Class used for CodeLists and EnumeratedLists.
 * 
 * @author Tobias Brix
 *
 */
@Entity
public class CodeList extends MetaOIDBase {
	/** Name of the CodeList. */
	@Column
	@Getter
	@Setter
	private String name;

	/** DateType of the CodeList. */	
	@Enumerated(EnumType.STRING)
	@Column
	@Getter
	@Setter
	private DataType dataType;

	/** List of associated items. */
	@OneToMany(mappedBy="codeList", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@OrderBy("orderNumber")
	@Getter
	@Setter
	private List<CodeListItem> codeListItemList = new LinkedList();
	void addCodeListItem(CodeListItem cli) { codeListItemList.add(cli); }

	/**
	 * Helper needed during the database construction.
	 */
	@Transient
	public List<String> associatedMetaItemOIDs = new LinkedList<>();	
}
