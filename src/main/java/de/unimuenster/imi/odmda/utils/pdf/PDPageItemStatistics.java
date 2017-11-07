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
import de.unimuenster.imi.odmda.model.metadata.MetaItem;
import de.unimuenster.imi.odmda.model.metadata.MetaItemGroup;
import de.unimuenster.imi.odmda.model.metadata.MetaStudyEvent;
import de.unimuenster.imi.odmda.statistics.StatisticsBase;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;

/**
 * PDF page for the statistics of a single MetaItem.
 * 
 * It is used as template to garantee always the same look of each statistics page.
 * 
 * @see PDFCreator
 * @author Tobias Brix
 */
public class PDPageItemStatistics extends PDPageBase {

	/***************************************************************************
	 * Methods
	 ***************************************************************************/

	/**
	 * Constructor
	 * 
	 * Calculates the length of the longest "key" value and stores it in maxTableHeaderWidth.
	 */
	public PDPageItemStatistics() {
		super();
		float tmpWidth = 1.f;
		try {
			tmpWidth = textFontSize * boldTextFont.getStringWidth("StudyEvent: ") / 1000;
		} catch (IOException ex) {
			LOGGER.error(ex.getMessage());
		}
		this.maxTableHeaderWidth = tmpWidth;       
	}

	/**
	 * Main function to create (e.g. fill) the statistics page.
	 * 
	 * @param document The document this page will be added to. Needed for the PDFBox API.
	 * @param mse Current MetaStudyEvent the current MetaItem is in.
	 * @param mf Current MetaForm the current MetaItem is in.
	 * @param mig Current MetaItemGroup the current MetaItem is in.
	 * @param mi Current MetaItem this page should represent.
	 * @param itemStatistics Statictics of all items. (@see DBAccess)
	 */
	public void fillItemPage(PDDocument document, MetaStudyEvent mse, MetaForm mf, MetaItemGroup mig, MetaItem mi, Map<String, StatisticsBase> itemStatistics) {
		try {
			contentStream = new PDPageContentStream(document, this);
			contentStream.beginText();
			currentYPosition = this.getMediaBox().getHeight() - pageMargin;
			contentStream.newLineAtOffset(pageMargin, currentYPosition);
			contentStream.setLeading(textFontLeading);

			//header
				// study event
			addKeyValue("StudyEvent: ", mse.getName());
			addKeyValue("", "(" + (mse.isRepeating() ? "Repeating" : "Non-Repeating") +
							" | StudyEvent Data Count: " + mse.getDataCounts()    + 
							" | Subject Count: "         + mse.getSubjectCounts() + ")");
			newLineAtOffset(0,-0.5f*textFontLeading);
				// forms
			addKeyValue("Form: ", mf.getName());
			addKeyValue("", "(" + (mf.isRepeating() ? "Repeating" : "Non-Repeating") + 
							" | Form Data Count: " + mf.getDataCounts()    + 
							" | Subject Count: "   + mf.getSubjectCounts() + ")");
			newLineAtOffset(0,-0.5f*textFontLeading);
				// item group
			addKeyValue("ItemGroup: ", mig.getName());
			addKeyValue("", "(" + (mig.isRepeating() ? "Repeating" : "Non-Repeating") + 
							" | Group Data Count: " + mig.getDataCounts()    + 
							" | Subject Count: "    + mig.getSubjectCounts() + ")");
			newLineAtOffset(0,-0.5f*textFontLeading);
				// item
			addKeyValue("Item: ", mi.getName());
			addKeyValue("", "(Data Type: " + mi.getDataType() + (mi.getCodeList() == null ? "" : "[CL]") +
							" | Item Data Count: " + mi.getDataCounts()    + 
							" | Subject Count: "   + mi.getSubjectCounts() + ")");

			// statistics
				//string
			StatisticsBase statisticsObect = itemStatistics.get(mse.getOID() + " " + mf.getOID() + " " + mig.getOID() + " " + mi.getOID());
//				LOGGER.info(statisticsObect);
			if(statisticsObect!=null ){
				String statisticString = statisticsObect.toString(false);
				newLineAtOffset(0,-3*textFontLeading);
				addKeyValue("Statistics: ", statisticString);
					//chart
				byte[] image = statisticsObect.getStatisticChartBytes();
				newLineAtOffset(0,-3*textFontLeading);
				addKeyValue("Chart: ", (image == null ? "No chart image avaiable!" : ""));
				contentStream.endText();

				if(image != null) {
					ByteArrayInputStream bais = new ByteArrayInputStream(image);
					BufferedImage bim = ImageIO.read(bais);
					float imageXStart = this.getMediaBox().getWidth() / 2 - 200; //200 half image width
					contentStream.drawImage(LosslessFactory.createFromImage(document, bim), imageXStart, currentYPosition-300);
				}
			}else{
				newLineAtOffset(0,-3*textFontLeading);
				addKeyValue("Statistics: ", "No values found!");
				newLineAtOffset(0,-3*textFontLeading);
				addKeyValue("Chart: ", "No chart image avaiable!" );
			}
			//close
			contentStream.close();
		} catch (IOException ex) {
			LOGGER.error("Problems while filling a PDF-Page: " + ex.getMessage());
		}
	}
}
