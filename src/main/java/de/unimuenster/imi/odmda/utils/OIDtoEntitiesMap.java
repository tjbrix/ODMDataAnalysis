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
package de.unimuenster.imi.odmda.utils;

import de.unimuenster.imi.odmda.model.metadata.MetaOIDBase;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Map used to handle a mapping from OIDs to the meta entities.
 * 
 * @param <T> The current class the map is used for.
 * @author Tobias Brix
 */
public class OIDtoEntitiesMap<T extends MetaOIDBase> extends HashMap<String,LinkedList<T>> {

	public void add(T entity) {
		if(this.containsKey(entity.getOID())) {
			List<T> list = this.get(entity.getOID());
			list.add(entity);
		} else {
			LinkedList<T> list = new LinkedList<>();
			list.add(entity);
			this.put(entity.getOID(), list);
		}
	}

	public List<T> getEntities(String OID) {
		return this.getOrDefault(OID,new LinkedList<>());
	}
}
