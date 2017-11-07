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

import de.unimuenster.imi.odmda.model.metadata.MetaForm;
import de.unimuenster.imi.odmda.model.metadata.MetaItem;
import de.unimuenster.imi.odmda.model.metadata.MetaItemGroup;
import de.unimuenster.imi.odmda.model.metadata.MetaStudyEvent;
import de.unimuenster.imi.odmda.model.metadata.ODMFile;
import de.unimuenster.imi.odmda.statistics.StatisticsBase;
import de.unimuenster.imi.odmda.utils.ProgressBarIterator;
import de.unimuenster.imi.odmda.utils.ProgressBarState;
import de.unimuenster.imi.odmda.utils.pdf.PDPageCover;
import de.unimuenster.imi.odmda.utils.pdf.PDPageItemStatistics;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * This class is used to create a PDF out of all statistics.
 * 
 * @author Tobias Brix
 */
public class PDFCreator {

	String pdfName;

	public PDFCreator(String name){
		pdfName = new String(name);
	}

	/**
	 * Logger to get Feedback if an error occurs.
	 */
	private static final Logger LOGGER = LogManager.getLogger(PDFCreator.class);

	/**
	 * Main function to create the PDF. 
	 * 
	 * @param currentODMFile Current ODMFile. Used to create the title page. 
	 * @param metaData The entire MetaData. Used to iterate through all items. (@see DBAccess)
	 * @param itemStatistics Statistics of all MetaItems. (@see DBAccess)
	 * @param request Current HTTPRequest. Used to determine the correct folder to store the PDF.  
	 * @return The abs path to the newly created PDF
	 */
	public String createFinalPDFDocument(ODMFile currentODMFile, List<MetaStudyEvent> metaData,
										 Map<String, StatisticsBase> itemStatistics, HttpServletRequest request,
										 ProgressBarState progress) {

		try (final PDDocument document = new PDDocument()) {
			//add cover page
			PDPageCover cover = new PDPageCover();
			document.addPage(cover);
			cover.fillPage(document, currentODMFile, metaData);

			////////////////////////////////////////
			//For Benchmarking only
			//consumes 17% of total running time        
			int totalIncrements = countMetaItemGroupss(metaData);
			ProgressBarIterator iterateProgressBar = new ProgressBarIterator(progress, ProgressBarState.SubTask.PDF, totalIncrements);
			///////////////////////////////////////

			//add pages for each item

			// for each MetaStudyEvent
			for (MetaStudyEvent mse : metaData) {
				// for each MetaForm
				for (MetaForm mf : mse.getMetaFormList()) {
					// for each MetaItemGroup
					for (MetaItemGroup mig : mf.getMetaItemGroupList()) {
						// for each MetaItem
						for (MetaItem mi : mig.getMetaItemList()) {
							PDPageItemStatistics page = new PDPageItemStatistics();
							document.addPage(page);
							page.fillItemPage(document, mse, mf, mig, mi, itemStatistics);
						} //end mi
					//////////////////////////////////////////////////////////////
					//pushing progress value
					iterateProgressBar.iterate();
					//////////////////////////////////////////////////////////////
					} //end mig
				} //end me
			} // end mse

			//LOGGER.info("Current Path: " + System.getProperty("user.dir"));
			document.save(request.getSession().getServletContext().getRealPath("/WEB-INF/PDFs/" + request.getSession().getId().trim()+ pdfName + ".pdf"));
			LOGGER.info("PDF saved as "+ request.getSession().getServletContext().getRealPath("/WEB-INF/PDFs/" + request.getSession().getId()+ pdfName + ".pdf"));
		} catch (IOException ioEx) {
			LOGGER.error("Exception while trying to create the document - " + ioEx);
		}
		//should not get here
		return "";
	} //end createPDF 

	/**
	 * helper method to count metaitemGroups 
	 * @param metaData
	 * @return
	 */
	private int countMetaItemGroupss(List<MetaStudyEvent> metaData){
		int counter = 0;
		// for each MetaStudyEvent
		for (MetaStudyEvent mse : metaData) {
			// for each MetaForm
			for (MetaForm mf : mse.getMetaFormList()) {
				// for each MetaItemGroup
				for (MetaItemGroup mig : mf.getMetaItemGroupList()) {
					// for each MetaItem
					counter++;
				} //end mig
			} //end me 
		} // end mse  
		return counter;
	}
} //end class
