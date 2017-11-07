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
import java.util.Comparator;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;


/**
 * Super class for most ODM meta entities, e.g., MetaItem, MetaForm 
  * It specifies the OID which is needed for same DAOs.
 *
 * @author Tobias Brix
 */
@MappedSuperclass
public abstract class MetaOrderNumberBase extends DatabaseEntity /*implements Comparable<MetaOrderNumberBase>*/ {
	/**
	 * Optional orderNumber
	 */
	@Column(nullable = true)
	@Getter
	@Setter
	private Integer orderNumber;
	
	/*public static final Comparator<MetaOrderNumberBase> OrderNumberComparator = 
						Comparator.comparing(MetaOrderNumberBase::getOrderNumber, Comparator.nullsLast(Integer::compareTo));
	
	@Override
	public int compareTo(MetaOrderNumberBase that) {
		//System.out.println(OrderNumberComparator.compare(this, that));
		return OrderNumberComparator.compare(this, that);
	}*/
}
