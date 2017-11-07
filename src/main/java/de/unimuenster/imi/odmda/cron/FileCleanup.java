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
package de.unimuenster.imi.odmda.cron;

import java.io.File;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * This class is used to clean up old PDF and CSV files.
 * 
 * It starts a cron-job on the server, which deletes each hour xx:00:00 all PDFs/CSVs, which are older than 24 hours.
 * In the Docker realization, this job is not needed, since alle files are deleted on exiting the Docker file.
 * 
 * @author Tobias Brix
 */
@Service
public class FileCleanup {

	/**
	 * Context needed to get the correct "relative" folder of the created PDFs.
	 */
	@Autowired
	private ServletContext servletContext; 

	/**
	 * This function is automatically called each hour at xx:00:00 to delete PDF files which are older than 24 hours.
	 * 
	 * Cron-job config: 00****
	 */
	@Scheduled(cron = "0 0 * * * *")
	public void cleanPDFDirectory() {
		//get right directory
		String directory = servletContext.getRealPath("/WEB-INF/PDFs");

		final File folder = new File(directory);
		for (final File file : folder.listFiles()) {
			String filename = file.getName();
			String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
			//check, if extension is a pdf file (not the README)
			if("pdf".equalsIgnoreCase(extension)) {
				//delete the file, if it is older than 1 day
				if(System.currentTimeMillis() - file.lastModified() > 86400000) {// 1 day in ms
					file.delete();
				}
			}
		}
	}

	/**
	 * This function is automatically called each hour at xx:00:00 to delete CSV files which are older than 24 hours.
	 * 
	 * Cron-job config: 00****
	 */
	@Scheduled(cron = "0 0 * * * *")
	public void cleanCSVDirectory() {
		//get right directory
		String directory = servletContext.getRealPath("/WEB-INF/CSVs");

		final File folder = new File(directory);
		for (final File file : folder.listFiles()) {
			String filename = file.getName();
			String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
			//check, if extension is a csv file (not the README)
			if("csv".equalsIgnoreCase(extension)) {
				//delete the file, if it is older than 1 day
				if(System.currentTimeMillis() - file.lastModified() > 86400000) {// 1 day in ms
					file.delete();
				}
			}
		}
	}

} // end class
