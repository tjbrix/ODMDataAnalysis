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
package de.unimuenster.imi.odmda.dao.clinicaldata;

import de.unimuenster.imi.odmda.dao.GenericDao;
import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalSubjectData;
import org.springframework.stereotype.Repository;

/**
 * Data access object used for clinical subject data.
 * 
 * @author Tobais Brix
 */
@Repository
public class ClinicalSubjectDataDao extends GenericDao<ClinicalSubjectData>{

	/** Loads all Clinical Data of a subject eagerly. */
	public ClinicalSubjectData initializedAllClinicalDataEagerly(Long csdID) {
		return sessionFactory.getCurrentSession().createQuery(" FROM ClinicalSubjectData csd "
																+ "  LEFT JOIN FETCH csd.clinicalStudyEventDataList csed "
																+ "  LEFT JOIN FETCH csed.metaStudyEvent mse"
																//+ "  LEFT JOIN FETCH mse.metaFormList"//											
																+ "  LEFT JOIN FETCH csed.clinicalFormDataList cfd"
																+ "  LEFT JOIN FETCH cfd.metaForm mf"
																//+ "  LEFT JOIN FETCH mf.metaItemGroupList"//
																+ "  LEFT JOIN FETCH cfd.clinicalItemGroupDataList cigd"
																+ "  LEFT JOIN FETCH cigd.metaItemGroup mig"
																+ "  LEFT JOIN FETCH mig.metaItemList "
																+ "  LEFT JOIN FETCH cigd.clinicalItemDataList cid"
																+ "  LEFT JOIN FETCH cid.metaItem"
															+ " WHERE csd.id = :pID", daoType)
				.setParameter("pID", csdID)
				.getResultList().get(0);
	}

	public long getNumCompletedSubjects() { return (long) sessionFactory.getCurrentSession().createQuery(" SELECT COUNT(*) FROM " + daoType.getSimpleName() 
																									   + " WHERE completeness = true").uniqueResult(); }
}
