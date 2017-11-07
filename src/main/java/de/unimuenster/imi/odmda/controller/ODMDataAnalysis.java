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
package de.unimuenster.imi.odmda.controller;

import de.unimuenster.imi.odmda.config.databases.DataSourceMap;
import de.unimuenster.imi.odmda.dao.DbAccessRepository;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import javax.servlet.ServletContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import de.unimuenster.imi.odmda.model.File;
import de.unimuenster.imi.odmda.model.FileValidator;
import de.unimuenster.imi.odmda.model.ODMValidator;
import de.unimuenster.imi.odmda.performance.benchmark.Benchmark;
import de.unimuenster.imi.odmda.service.ProcessXMLService;
import de.unimuenster.imi.odmda.utils.ProgressBarState;
import static de.unimuenster.imi.odmda.utils.ProgressBarState.SubTask.PARSING;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Main class of ODM Data Analysis.
 * 
* @author Justin Doods
* @author Tobias Brix
 */
@Controller
@RequestMapping("/")
public class ODMDataAnalysis {

	/** Hack to handle problems Docker under windows */
	static {
		System.setProperty("java.awt.headless", "true");
	}

	private static final Logger LOGGER = LogManager.getLogger(ODMDataAnalysis.class);

	@Autowired
	FileValidator validator;

	@Autowired
	ServletContext servletContext;

	@Autowired
	ProcessXMLService processXMLService;

	@Autowired
	DataSourceMap dataSourceMap;

	@Autowired
	DbAccessRepository dbAccessRepository;

	//display final results on result page
	private static final boolean displayBenchmarkResults = false;

	/** Counter used to establish the connection between database and request */
	private static long uploadCounter = 1;

	@RequestMapping(method = RequestMethod.GET)
	public String getForm(Model model) {
		File fileModel = new File();
		model.addAttribute("file", fileModel);
		return "upload_page";
	}

	/**
	 * Method is called on fileUpload and adds Maps of MetaData,
	 * ItemGroupCounts, ItemCounts and ItemStatistics to the model.
	 * It also adds the completeness information and returns it in the returned jsp
	 * page. The completeness data is hidden and can be visualized using javascript 
	 * as a separate page. 
	 * @param model
	 * @param file
	 * @param result
	 * @param request
	 * @param response 
	 * @param calculateStat 
	 * @param calculateCompleteness 
	 * @param exportFile 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String fileUploaded(Model model, File file, BindingResult result, 
			HttpServletRequest request, HttpServletResponse response,
			@RequestParam("calculateStat") Optional<String> calculateStat,
			@RequestParam("calculateCompleteness") Optional<String> calculateCompleteness,
			@RequestParam("exportFile") Optional<String> exportFile) {

		ODMValidator odmValidator = new ODMValidator();

		Benchmark benchmark = new Benchmark();
		benchmark.setBenchmarkStart(new Date());

		String returnVal = "results_page";
		validator.validate(file, result);

		if (result.hasErrors()) {
			returnVal = "upload_page";
			LOGGER.error("Upload failed");
		} else {
			String nameWithExtension = file.getFile().getOriginalFilename();
			String  fileName = nameWithExtension.substring(0, nameWithExtension.indexOf("."));
			String taskKey = request.getSession().getId()+ fileName;
			//starting progress
			processXMLService.taskProgress.put(taskKey, new ProgressBarState());

			benchmark.setValidationStart(new Date());
			LOGGER.debug("Validation starts at "+ new Date().toString());

			ArrayList<String> errorMsgs = odmValidator.validateXML( file.getFile());
			benchmark.setValidationEnd(new Date());
			LOGGER.debug("Validation Ends at "+ new Date().toString());
			//validation takes 1%
			processXMLService.taskProgress.get(taskKey).increaseState(PARSING, 1);
			if(errorMsgs!=null && !errorMsgs.isEmpty()){
				model.addAttribute("errorMsgs",errorMsgs);
				returnVal = "error";
				processXMLService.taskProgress.remove(taskKey);
			}else{
				LOGGER.debug("Upload successful");  			
				LOGGER.debug("File Key created: " + taskKey);

				//create a new database
				long currentCounter = uploadCounter++;
				request.setAttribute("currentUploadCounter", Long.toString(currentCounter));
				dataSourceMap.addDataSource(Long.toString(currentCounter));
				
				//dbAccessRepository.getSchema();
				
				processXMLService.processXML(file,fileName, request,response,model,calculateStat,calculateCompleteness,exportFile,taskKey,benchmark); 		
				
				//for debugging only
				if(displayBenchmarkResults){
					benchmark.setBenchmarkEnd(new Date());
					model.addAttribute("benchmarkList", benchmark.getBenchmarkList());
					LOGGER.info(benchmark);
				}

				//delete the used database
				dataSourceMap.removeSource(Long.toString(currentCounter));
			}
		}

		return returnVal;
	}

	/**
	 * Allows user to get the status of file uploading+parsing process.
	 * @param request 
	 * @param response
	 * @param fileName fileName against which status to be retrieved
	 * @return value of percent completed between 0-100
	 */
	@RequestMapping(value ="/parsingStatus", method = RequestMethod.POST, produces="application/json" )
	@ResponseBody
	private ProgressBarState getParsingStatus(HttpServletRequest request,HttpServletResponse response, @RequestParam("fileName")String fileName){
		//System.out.println("parse!");
		if(processXMLService.taskProgress.containsKey(request.getSession().getId()+fileName)){
			//int percent_completed = (int) processXMLService.taskProgress.get(request.getSession().getId()+fileName).getParsingPercentage();
			//LOGGER.info(request.getSession().getId()+fileName+" : " +percent_completed+"");
			//return percent_completed+"" ;
			return processXMLService.taskProgress.get(request.getSession().getId()+fileName);
		}

		//no need to display user an error page
		//this error is only for debugging on client side
		//in-case of failure only bar won't be working
		try {
			response.sendError(404);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Allows users to download example XML ODM file 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value= "/downloadTestFile")
	public void downloadExampleXML(HttpServletRequest request, HttpServletResponse response){
		//path to file
		ServletContext context = request.getServletContext();
		String path = context.getRealPath("") + "/WEB-INF/ExampleFile/ODMTestfile.xml";
		
		downloadFile(request, response, path,"ODMTestFile.xml");
		
	}

	/**
	 * Allows user to download PDF analysis results
	 * @param request
	 * @param response
	 * @param fileName must be without extension e.g. ODMFile.xml must be 'ODMFile'
	 */
	@RequestMapping(value="/downloadPDF", method=RequestMethod.GET)
	private void downloadPDFFile(HttpServletRequest request, HttpServletResponse response ,@RequestParam("fileName") String fileName){
		String path = request.getSession().getServletContext().getRealPath("/WEB-INF/PDFs/" + request.getSession().getId()+fileName + ".pdf");
		downloadFile(request, response, path,"analysis_results.pdf");
	}

	/**
	 * Allows user to download csv file of invalid values
	 * @param request
	 * @param response
	 * @param fileName 
	 */
	@RequestMapping(value="/downloadCSV", method=RequestMethod.GET)
	private void downloadCSVFile(HttpServletRequest request, HttpServletResponse response ,@RequestParam("fileName") String fileName){
		String path = request.getSession().getServletContext().getRealPath("/WEB-INF/CSVs/" + request.getSession().getId()+fileName + ".csv");
		downloadFile(request, response, path,"invalid_values.csv");
	}

	/**
	 * Helper method to download files on server
	 * @param request 
	 * @param response 
	 * @param path is the absolute path of file on server
	 * @param downloadFileName name of file displayed when downloading
	 */
	private void downloadFile(HttpServletRequest request, HttpServletResponse response, String path, String downloadFileName){
		//path to file
		ServletContext context = request.getServletContext();

		LOGGER.info("Pdf file to be downloaded has path : "+ path);

		java.io.File downloadFile = new java.io.File(path);
		try {
		FileInputStream inputStream = new FileInputStream(downloadFile);

		// get MIME type of the file
		String mimeType = "application/xml";

		// set content attributes for the response
		response.setContentType(mimeType);
		response.setContentLength((int) downloadFile.length());

		// set headers for the response
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", downloadFileName);
		response.setHeader(headerKey, headerValue);

		// get output stream of the response
		OutputStream outStream = response.getOutputStream();

		byte[] buffer = new byte[4096];
		int bytesRead = -1;

		// write bytes read from the input stream into the output stream
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytesRead);
		}

		inputStream.close();
		outStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * returns a File from a MultipartFile
	 * 
	 * @param file MultipartFile
	 * @return convFile java.io.File
	 * @throws IOException
	 */
	public java.io.File convert(MultipartFile file) throws IOException {
		java.io.File directory = (java.io.File) servletContext.getAttribute("javax.servlet.context.tempdir");
		java.io.File convFile = java.io.File.createTempFile("upload", ".tmp", directory);
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		LOGGER.info("File retrieved from MulipartFile ");
		return convFile;
	}
}
