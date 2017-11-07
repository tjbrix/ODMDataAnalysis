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
package de.unimuenster.imi.odmda.export;

import de.unimuenster.imi.odmda.dao.DaoFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.unimuenster.imi.odmda.dao.DbAccessRepository;
import de.unimuenster.imi.odmda.model.clinicaldata.InvalidClinicalData;

/**
 * This class handles the creation of the CSV file.
 * 
 * @author Tobias Brix
 */
@Component
public class CSVCreator {

	@Autowired
	private DaoFactory daoFactory;

	private static final Logger LOGGER = LogManager.getLogger(CSVCreator.class);

	public void createCSVFile(String csvName, String storePath) {
		List<InvalidClinicalData> list = daoFactory.getInvalidClinicalDataDao().getAllEntities();

		try {
			FileWriter writer = new FileWriter(storePath);
			writer.append("Subject;StudyEventOID;StudyEventRepeatKey;FormOID;FormRepeatKey;ItemGroup;ItemGroupRepeatKey;ItemOID;Value;Reason\n");
			for(InvalidClinicalData icd : list) {
				writer.append(followCVSformat(icd.getSubjectKey()) + ";" + 
												followCVSformat(icd.getMetaStudyEventOID() != null ? icd.getMetaStudyEventOID() : "null")    + ";" + 
												followCVSformat(icd.getStudyEventRepeatKey())                                                   + ";" +
												followCVSformat(icd.getMetaFormOID() != null ? icd.getMetaFormOID() : "null")                + ";" + 
												followCVSformat(icd.getFormRepeatKey())                                                         + ";" +
												followCVSformat(icd.getMetaItemGroupOID() != null ? icd.getMetaItemGroupOID() : "null")      + ";" + 
												followCVSformat(icd.getItemGroupRepeatKey())                                                    + ";" +
												followCVSformat(icd.getMetaItemOID() != null ? icd.getMetaItemOID() : "null")                + ";" + 
												followCVSformat(icd.getValue() != null ? icd.getValue() : "null") + ";" + 
												followCVSformat(icd.getReason()) + "\n");
			}
			writer.flush();
			writer.close();
			LOGGER.info("CSV File Exported");
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(DbAccessRepository.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
	}

	private static String followCVSformat(String value) {
		if(value == null) return value;
		String result = value;
		if (result.contains("\"")) {
			result = result.replace("\"", "\"\"");
		}
		if (result.contains("\n")) {
			result = result.replace("\n", " ");
		}
		if (result.contains("\r")) {
			result = result.replace("\r", " ");
		}
		if (result.contains(";")) {
			result = "\"" + result + "\"";
		}
		return result;
	}
}
