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
package de.unimuenster.imi.odmda.dao.metadata;

import de.unimuenster.imi.odmda.dao.GenericDao;
import de.unimuenster.imi.odmda.model.metadata.MetaOIDBase;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Data access object used for meta entities.
 * 
 * @author Tobais Brix
 * @param <T> The entity which should be handled. Must be if type MetaEntityBase
 */
@Repository 
public abstract class MetaOIDBaseDao<T extends MetaOIDBase> extends GenericDao<T>{
	
	public List<T> getAllEntitiesByOID(String oID) { 
		return sessionFactory.getCurrentSession().createQuery("FROM " + daoType.getSimpleName() + " t WHERE t.OID = :pOID", daoType)
				.setParameter("pOID", oID)
				.getResultList();
	}
}
