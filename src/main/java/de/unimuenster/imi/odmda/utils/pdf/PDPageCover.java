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
package de.unimuenster.imi.odmda.utils.pdf;

import de.unimuenster.imi.odmda.model.metadata.MetaForm;
import de.unimuenster.imi.odmda.model.metadata.MetaItemGroup;
import de.unimuenster.imi.odmda.model.metadata.MetaStudyEvent;
import de.unimuenster.imi.odmda.model.metadata.ODMFile;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * Cover page of the statistics beeing exported.
 * 
 * @author Tobias Brix
 */
public class PDPageCover extends PDPageBase {

	/**
	 * Constructor
	 * 
	 * Calculates the length of the longest "key" value and stores it in maxTableHeaderWidth.
	 */
	public PDPageCover() {
		super();
		float tmpWidth = 1.f;
		try {
			tmpWidth = textFontSize * boldTextFont.getStringWidth("PDF created on: ") / 1000;
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage());
		}
		this.maxTableHeaderWidth = tmpWidth;       
	}

	/**
	 * 
	 * @param document The document this page will be added to. Needed for the PDFBox API.
	 * @param file Current ODMFile to extract the OID.
	 * @param metaData Current meta information.
	 * @param numItems Number of provided items.
	 */
	public void fillPage(PDDocument document, ODMFile file, List<MetaStudyEvent> metaData) {
		try {
			contentStream = new PDPageContentStream(document, this);
			contentStream.beginText();
			currentYPosition = this.getMediaBox().getHeight() - 2*pageMargin;
			contentStream.newLineAtOffset(pageMargin, currentYPosition);
			contentStream.setLeading(textFontLeading);

			//title
			contentStream.setFont(boldTextFont, 48);
			contentStream.showText("Generic Statistics");
			newLineAtOffset(0,-15*textFontLeading);

			//more information
			contentStream.setFont(normalTextFont, textFontSize);
			addKeyValue("Statistics of file: ", file.getOID()); 
			newLineAtOffset(0,-0.5f*textFontLeading);
			addKeyValue("PDF created on: ", new SimpleDateFormat("dd MMMMMMMMM yy").format(new Date(System.currentTimeMillis()))); 
			newLineAtOffset(0,-0.5f*textFontLeading);
			addKeyValue("Containing: ", "One page per Item");
			
			int numForms = 0, numItemGroups = 0, numItems = 0;
			for(MetaStudyEvent mse : metaData) {
				numForms += mse.getMetaFormList().size();
				for(MetaForm mf : mse.getMetaFormList()) {
					numItemGroups += mf.getMetaItemGroupList().size();
					for(MetaItemGroup mig : mf.getMetaItemGroupList()) {
						numItems += mig.getMetaItemList().size();
					}
				}
			}
			addKeyValue("", "(" + numItems + " Items in " + numItemGroups + " ItemGroups on " + numForms + " Forms in " + metaData.size() + " StudyEvents)");

			contentStream.endText();

			//add footer to cover page
			addFooter();

			//close the stream
			contentStream.close();

		} catch (IOException ex) {
			LOGGER.error("Problems while filling a PDF-Page: " + ex.getMessage());
		}
	}
} //end class
