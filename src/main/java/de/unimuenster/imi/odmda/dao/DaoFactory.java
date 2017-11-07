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
package de.unimuenster.imi.odmda.dao;

import de.unimuenster.imi.odmda.dao.clinicaldata.ClinicalDataDao;
import de.unimuenster.imi.odmda.dao.clinicaldata.InvalidClinicalDataDao;
import de.unimuenster.imi.odmda.dao.clinicaldata.ClinicalFormDataDao;
import de.unimuenster.imi.odmda.dao.clinicaldata.ClinicalItemDataDao;
import de.unimuenster.imi.odmda.dao.clinicaldata.ClinicalItemGroupDataDao;
import de.unimuenster.imi.odmda.dao.clinicaldata.ClinicalStudyEventDataDao;
import de.unimuenster.imi.odmda.dao.clinicaldata.ClinicalSubjectDataDao;
import de.unimuenster.imi.odmda.dao.metadata.CodeListItemDao;
import de.unimuenster.imi.odmda.dao.metadata.MetaStudyEventDao;
import de.unimuenster.imi.odmda.dao.metadata.TranslatedTextDao;
import de.unimuenster.imi.odmda.dao.metadata.MetaItemGroupDao;
import de.unimuenster.imi.odmda.dao.metadata.MetaFormDao;
import de.unimuenster.imi.odmda.dao.metadata.MetaItemDao;
import de.unimuenster.imi.odmda.dao.metadata.CodeListDao;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Factory to access all data access objects.
 * 
 * @author Tobais Brix
 */
@Repository  
public class DaoFactory {
	//----------
	// MetaData
	//----------
	@Autowired 
	@Getter
	ODMFileDao odmFileDao;	
	@Autowired
	@Getter
	MetaStudyEventDao metaStudyEventDao;	
	@Autowired  
	@Getter
	MetaFormDao metaFormDao;	
	@Autowired  
	@Getter
	MetaItemGroupDao metaItemGroupDao;	
	@Autowired  
	@Getter
	MetaItemDao metaItemDao;	
	@Autowired  
	@Getter
	CodeListDao codeListDao;
	@Autowired  
	@Getter
	CodeListItemDao codeListItemDao;
	@Autowired  
	@Getter
	TranslatedTextDao translatedTextDao;
	
	//--------------
	// ClinicalData
	//--------------
	@Autowired
	@Getter
	ClinicalSubjectDataDao clinicalSubjectDataDao; 
	@Autowired
	@Getter
	ClinicalStudyEventDataDao clinicalStudyEventDataDao; 
	@Autowired
	@Getter
	ClinicalFormDataDao clinicalFormDataDao; 
	@Autowired
	@Getter
	ClinicalItemGroupDataDao clinicalItemGroupDataDao; 
	@Autowired
	@Getter
	ClinicalItemDataDao clinicalItemDataDao; 

	@Autowired
	@Getter
	ClinicalDataDao clinicalDataDao;
	@Autowired
	@Getter
	InvalidClinicalDataDao invalidClinicalDataDao;
}
