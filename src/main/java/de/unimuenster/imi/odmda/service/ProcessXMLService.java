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
package de.unimuenster.imi.odmda.service;

import de.unimuenster.imi.odmda.dao.DaoFactory;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import de.unimuenster.imi.odmda.dao.DbAccessRepository;
import de.unimuenster.imi.odmda.export.CSVCreator;
import de.unimuenster.imi.odmda.export.PDFCreator;
import de.unimuenster.imi.odmda.model.File;
import de.unimuenster.imi.odmda.model.metadata.MetaStudyEvent;
import de.unimuenster.imi.odmda.model.metadata.ODMFile;
import de.unimuenster.imi.odmda.parser.ODMPreProcessSaxParser;
import de.unimuenster.imi.odmda.parser.ODMToDatabaseSaxParser;
import de.unimuenster.imi.odmda.performance.benchmark.Benchmark;
import de.unimuenster.imi.odmda.statistics.StatisticsBase;
import de.unimuenster.imi.odmda.utils.ProgressBarIterator;
import de.unimuenster.imi.odmda.utils.ProgressBarState;

import java.util.List;

/**
 * Allows parsing of file, storing in database, adding it to the model to sent
 * back to server and exporting of file to PDF. The class also allows user to
 * track the progress of parsing by requesting value of "complete" attribute by
 * using static taskProgress variable. The value is added to static variable
 * when processing(processXML) starts and deleted when the method ends. The
 * value for progress can be only requested during this period. The value for
 * taskProgress for a given key is removed after processing is finished in order
 * to save static memory and avoid conflicts for future. Key is generated using
 * sessionkey+fileName (without .xml) Works for multiple clients and even with
 * multiple tabs in single browser with different file names.
 * 
 * Limitation: It will send error values of progress if two files with the same
 * name are uploaded at same time using different tabs for the same session i.e.
 * keys will be same
 * 
 * @author Saad Sarfraz
 * @author Tobias Brix
 * @author Justin Doods
 */
@Service
public class ProcessXMLService {


	@Autowired
	DbAccessRepository dbAccessRepository;

	@Autowired
	DaoFactory daoFactory;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	private CSVCreator csvCreator;
	

	private static final Logger LOGGER = LogManager.getLogger(ProcessXMLService.class);

	// Stores the parsing progress for each file using unique key
	// key is generated using SessionKey+filename without extension i.e. .xml
	// A new entry is created for every request and deleted before response is sent to 
	// user
	public static HashMap<String, ProgressBarState> taskProgress = new HashMap<>();

	public void processXML(File file, String fileName, HttpServletRequest request, HttpServletResponse response,
			Model model, Optional<String> calculateStat, Optional<String> calculateCompleteness,
			Optional<String> exportFile,String taskKey, Benchmark benchmark) {

		ProgressBarState progress = taskProgress.get(taskKey);
		// Get current ODMFile for multi user support
		benchmark.setParsingStart(new Date());
		ODMFile currentODMFile = parseAndStoreXML(file, progress);
		benchmark.setParsingEnd(new Date());

		//case: only completeness is required
		if (calculateCompleteness.isPresent() && !calculateStat.isPresent() && !exportFile.isPresent()) {
			// Get all meta information
			dbAccessRepository.updateItemCounts();
//				benchmark.setCalculateCompletenessStart(new Date());
			calculateCompleteness(model,progress,benchmark);
//				benchmark.setCalculateCompletenessEnd(new Date());
		} else { //if none is selected or/+ other option is selected
			// Get counts
			benchmark.setStudyEventCountStart(new Date());
			dbAccessRepository.updateStudyEventCounts();
			progress.increaseState(ProgressBarState.SubTask.STATISTICS, 2);
			benchmark.setStudyEventCountEnd(new Date());
			benchmark.setFormCountsStart(new Date());
			dbAccessRepository.updateFormCounts();
			progress.increaseState(ProgressBarState.SubTask.STATISTICS, 3);
			benchmark.setFormCountsEnd(new Date());
//				LOGGER.debug("FormAndSubjectCounts retrieved from DB");

			benchmark.setItemGroupCountsStart(new Date());
			dbAccessRepository.updateItemGroupCounts();
			progress.increaseState(ProgressBarState.SubTask.STATISTICS, 4);
//				LOGGER.debug("ItemGroupCounts retrieved from DB");
			benchmark.setItemGroupCountsEnd(new Date());
			benchmark.setItemCountsStart(new Date());

			dbAccessRepository.updateItemCounts();
			progress.increaseState(ProgressBarState.SubTask.STATISTICS, 6);
			benchmark.setItemCountsEnd(new Date());
//				LOGGER.debug("ItemCounts retrieved from DB");
			// Get all meta information

			benchmark.setFlagMetaInvalidValueStart(new Date());
			List<MetaStudyEvent> metaData = dbAccessRepository.flagMetaInvalidValues();
			benchmark.setFlagMetaInvalidValueEnd(new Date());
			LOGGER.debug("MetaStudyEventList retrieved from DB");
			// Set statistics
			// takes 1% of total running time
			benchmark.setItemStatisticsStart(new Date());
			Map<String, StatisticsBase> itemStatistics = dbAccessRepository.getItemStatistics(metaData, progress, 84); 
			progress.setState(ProgressBarState.SubTask.STATISTICS, 100);
			benchmark.setItemStatisticsEnd(new Date());
			LOGGER.debug("ItemStatisticsMap retrieved from DB");
			LOGGER.debug("Analysis complete");

			if (calculateCompleteness.isPresent()) {
				benchmark.setCalculateCompletenessStart(new Date());
				calculateCompleteness(model,progress, benchmark);
				benchmark.setCalculateCompletenessEnd(new Date());
			}
			if(exportFile.isPresent()){
				benchmark.setPdfCreateStart(new Date());
				LOGGER.debug("Starting Creating PDF");

				// create a nice PDF of all Statistics
				PDFCreator myCreator = new PDFCreator(fileName);
				myCreator.createFinalPDFDocument(currentODMFile, metaData, itemStatistics, request, progress);
				progress.setState(ProgressBarState.SubTask.PDF, 100);
				LOGGER.debug("Finished Creating PDF");
				benchmark.setPdfCreateEnd(new Date());
				model.addAttribute("exportFile", "true");

				//extra for debugging
				java.io.File pdfFile = new java.io.File(request.getSession().getServletContext().getRealPath("/WEB-INF/PDFs/" + request.getSession().getId().trim()+fileName + ".pdf"));
				if (pdfFile.exists()) {
					model.addAttribute("fileName",fileName);
					LOGGER.debug("PDF file exists");
				} else {
					model.addAttribute("errorMsg", "File Not Found");
					LOGGER.error("PDF file not found");
				}// extra for debugging ends here 
			}
			if(calculateStat.isPresent()|| (!exportFile.isPresent() && !calculateCompleteness.isPresent())){ //view statistics anyway even none is selected
				model.addAttribute("ODMFile", currentODMFile);
				model.addAttribute("studyEventList", metaData);
				model.addAttribute("itemStatistics", itemStatistics);
				model.addAttribute("analysisAdded", "true");
			}

			model.addAttribute("invalidClinicalDataList", daoFactory.getInvalidClinicalDataDao().getAllEntities());
			String storePath = request.getSession().getServletContext().getRealPath("/WEB-INF/CSVs/" + request.getSession().getId().trim()+ fileName + ".csv");
			benchmark.setCreateCSVStart(new Date());
			csvCreator.createCSVFile(fileName, storePath);

			benchmark.setCreateCSVEnd(new Date());
		}

		//remove static value
		taskProgress.remove(taskKey);
	}

	/**
	 * Parses the xml file and stores the data in database
	 * @param file
	 * @param taskKey
	 * @return
	 */
	@Transactional 
	public ODMFile parseAndStoreXML(File file, ProgressBarState progress) {

		MultipartFile mulipartFile = file.getFile();

		LOGGER.debug("Parsing started");
		//inizialize used parsers		
		ODMPreProcessSaxParser odmPreProcessParser = new ODMPreProcessSaxParser();
		ODMToDatabaseSaxParser odmToDatabaseParser = null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			SAXParser saxParser = factory.newSAXParser();
			LOGGER.debug("Start pre process parsing");
			saxParser.parse(mulipartFile.getInputStream(), odmPreProcessParser);
			progress.setState(ProgressBarState.SubTask.PARSING, 15);			
			odmToDatabaseParser = new ODMToDatabaseSaxParser(daoFactory, new ProgressBarIterator(progress, ProgressBarState.SubTask.PARSING, odmPreProcessParser.getNumClinicalSubjects(),74));
			LOGGER.debug("Start parsing and storing ODM file");
			saxParser.parse(mulipartFile.getInputStream(), odmToDatabaseParser);
			LOGGER.debug("Finished parsing and storing ODM file");
		} catch (ParserConfigurationException e) {
			LOGGER.error("ParserConfigurationException", e);
		} catch (SAXException e) {
			LOGGER.error("SAXException during parsing of the XML file", e);
		} catch (IOException e) {
			LOGGER.error("IOException", e);
		}
		LOGGER.debug("Parsing Ended");

		progress.setState(ProgressBarState.SubTask.PARSING, 100);
			
		return odmToDatabaseParser.getCurrentODMFile();
	}

	/**
	 * Calculates the required values for completeness and adds it to the Model
	 * Requires: MetaItem counts to be computed before calling this function
	 * @param odmId
	 * @param model
	 * @param taskKey
	 * @param benchmark
	 * @param taskPercentage %of total time taken by this task 
	 */
	private void calculateCompleteness(Model model, ProgressBarState progress, Benchmark benchmark) {
		LOGGER.debug("Starting Calculating Completeness");
		//calculate completeness
		benchmark.setGeneralCompletenessStart(new Date());
		List<MetaStudyEvent> metaStudyEvents_AllCompleteness = dbAccessRepository.getCompleteness();
		benchmark.setGeneralCompletenessEnd(new Date());
		model.addAttribute("studyEventListCompletenessAll", metaStudyEvents_AllCompleteness);
		progress.increaseState(ProgressBarState.SubTask.COMPLETENESS, 10);

		//calculate mandatory completeness
		benchmark.setMandatoryCompletenessStart(new Date());
		//this is the most expensive task in this function
		//takes 96% of the total completeness task so for simplicity of benchmarking 96==100%
		dbAccessRepository.updateMandatoryCompleteness(progress, 84); 
		benchmark.setMandatoryCompletenessEnd(new Date());

		benchmark.setFlagcompletenessValuesStart(new Date());
		List<MetaStudyEvent> metaStudyEvents_Mandatory = dbAccessRepository.flagCompletenessMandatoryValues(); 
		benchmark.setFlagcompletenessValuesEnd(new Date());
		model.addAttribute("studyEventListCompletenessMandatory", metaStudyEvents_Mandatory);
		model.addAttribute("completenessAdded", "true");

		//adding Total subjects counts and Number of subjects completed mandatory Form
		int totalSubjects = (int) daoFactory.getClinicalSubjectDataDao().getNumEntities();
		int completedSubjects = (int) daoFactory.getClinicalSubjectDataDao().getNumCompletedSubjects();
		model.addAttribute("totalSubjects", totalSubjects);
		model.addAttribute("completedSubjects", completedSubjects);
		progress.setState(ProgressBarState.SubTask.COMPLETENESS, 100);
		LOGGER.debug("Ended Calculating Completeness");
	}
}
