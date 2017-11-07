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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

/**
 * Base-Class of all PDPages used in the PDFCreator.
 * 
 * Used to define helper functions and set uniform layouts.
 * 
 * @author Tobias Brix
 */
public class PDPageBase extends PDPage {
	/**
	 * For handling possible output.
	 */
	protected static final Logger LOGGER = LogManager.getLogger(PDPageBase.class);

	/**
	 * Content stream. Must be initialized in the sub-classes, since it is dependent of the document.
	 */
	PDPageContentStream contentStream;

	/**
	 * Current y position of the internal new line position.  
	 */
	float currentYPosition;
 
	/**
	 * Top, bottom, left, and right margin of the page 
	*/
	protected final int pageMargin = 75;

	/**
	 * Font used for normal Text.
	 */
	protected final PDFont normalTextFont = PDType1Font.TIMES_ROMAN;
	/**
	 * Font used for normal bold text.
	 */
	protected final PDFont boldTextFont = PDType1Font.TIMES_BOLD;
	/**
	 * Normal text font size.
	 */
	protected final int textFontSize = 12;
	/**
	 * Leading between normal text lines.
	 */
	protected final int textFontLeading = (int) (textFontSize * 1.2f);

	/**
	 * Width of the maximum "key" value in the table structure of this page.
	 */
	protected float maxTableHeaderWidth;

	/***************************************************************************
	 * Methods
	 ***************************************************************************/

	/**
	 * Constructor
	 */
	protected PDPageBase() {
		super(PDRectangle.A4);
	}

	/**
	 * Adds the Footer to a page.
	 * 
	 * The ContentStream must be initialized and not closed yet. endText() has to be called first.
	 */
	protected void addFooter() {
		try {
			//draw text
			contentStream.beginText();
			contentStream.setLeading(textFontLeading);
			contentStream.setFont(normalTextFont, textFontSize);
			contentStream.newLineAtOffset(pageMargin, pageMargin);
			contentStream.setNonStrokingColor(100, 100, 100);
			contentStream.showText("Generated by ODM Data Analysis (");
			contentStream.setNonStrokingColor(0, 0, 255);
			contentStream.showText("odmanalysis.uni-muenster.de");
			contentStream.setNonStrokingColor(100, 100, 100);
			contentStream.showText(")");
			contentStream.endText();

			//add link
			PDBorderStyleDictionary borderULine = new PDBorderStyleDictionary();
			borderULine.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
			PDAnnotationLink txtLink = new PDAnnotationLink();
			txtLink.setBorderStyle(borderULine);
			txtLink.setColor(new PDColor(new float[]{0.f,0.f,255.f}, PDDeviceRGB.INSTANCE));

			// add an action
			PDActionURI action = new PDActionURI();
			action.setURI("https://odmanalysis.uni-muenster.de");
			txtLink.setAction(action);

			PDRectangle position = new PDRectangle();
			position.setLowerLeftX(290);
			position.setLowerLeftY(pageMargin-2); 
			position.setUpperRightX(445); 
			position.setUpperRightY(pageMargin + textFontSize); 
			txtLink.setRectangle(position);
			getAnnotations().add(txtLink);
		} catch (IOException ex) {
			LOGGER.error("Problems while filling a PDF-Page: " + ex.getMessage());
		}
	}

	/**
	 * Helper function to imitate a nice key: value structure.
	 * 
	 * @param key Key value shown in bold on the left side of the page.
	 * @param value String shown right of the key value.
	 * @throws IOException The PDFBox classes may throw this exception.
	 */
	protected void addKeyValue(String key, String value) throws IOException {
		contentStream.setFont(boldTextFont, textFontSize);
		float currentHeaderWidth = textFontSize * boldTextFont.getStringWidth(key) / 1000;
		contentStream.newLineAtOffset(maxTableHeaderWidth - currentHeaderWidth, 0);
		contentStream.showText(key);
		contentStream.setFont(normalTextFont, textFontSize);
		contentStream.newLineAtOffset(currentHeaderWidth, 0);
		addLongString(value, maxTableHeaderWidth);
		newLineAtOffset(-maxTableHeaderWidth,-textFontLeading);
	}

	/**
	 * Wrapper of the PDPageContentStream.newLine() function.
	 * 
	 * It takes track of the current Y position on the page.
	 * 
	 * @throws IOException The PDFBox classes may throw this exception.
	 */
	protected void newLine() throws IOException {
		currentYPosition -= textFontLeading;
		contentStream.newLine();
	}

	/**
	 * Wrapper of the PDPageContentStream.newLineAtOffet(float,float) function.
	 * 
	 * It takes track of the current Y position on the page.
	 * 
	 * @param x Move the start postion of the new line by this x offset.
	 * @param y Move the start postion of the new line by this y offset.
	 * @throws IOException The PDFBox classes may throw this exception. 
	 */
	protected void newLineAtOffset(float x, float y) throws IOException {
		currentYPosition += y;
		contentStream.newLineAtOffset(x, y);
	}

	/**
	 * Helper function to handle long Strings, which would exceed the available line length.
	 * 
	 * This function also handles linebreaks in Strings correctly.
	 * 
	 * @param value The String which should be added
	 * @param lineStartXPos X position of the starting line. It is used to determine the available line width.
	 * @throws IOException The PDFBox classes may throw this exception.
	 */
	protected void addLongString(String value, float lineStartXPos) throws IOException {
		//calculate current linewidth
		PDRectangle mediabox = this.getMediaBox();
		float lineWidth = mediabox.getWidth() - lineStartXPos - pageMargin;

		//split input string into lines fitting the current line width
		List<String> lines = new ArrayList<>();

		//for each string part (take line breaks inti account)
		for (String text : value.split("\n")) {
			int lastFittingSpaceIndex = -1;
			while (text.length() > 0) {
				int nextSpaceIndex = text.indexOf(' ', lastFittingSpaceIndex + 1); //+1 to find the next one
				if (nextSpaceIndex < 0) nextSpaceIndex = text.length(); //if no space exists, take the entire text
				String subString = text.substring(0, nextSpaceIndex); //take this part of the text and determine the length
				float size = textFontSize * normalTextFont.getStringWidth(subString) / 1000;
				if (size > lineWidth){
					if (lastFittingSpaceIndex < 0) lastFittingSpaceIndex = nextSpaceIndex; //line has no space and the word is to long. so write it anyway (over the page border)
					subString = text.substring(0, lastFittingSpaceIndex); //take last fitting text part
					lines.add(subString); //add it
					text = text.substring(lastFittingSpaceIndex).trim(); //and remove it from the remaining line
					lastFittingSpaceIndex = -1; //reset index
				} else {
					//add the rest of the line
					if (nextSpaceIndex == text.length()) {
						lines.add(text);
						text = "";
					} else { // current space fitts into the line
						lastFittingSpaceIndex = nextSpaceIndex;
					}
				}
			} // end while
		} //end for

		//add lines
		for (Iterator<String> it = lines.iterator(); it.hasNext();) {
			contentStream.showText(it.next());
			if(it.hasNext()) {
				newLineAtOffset(0, -textFontLeading);
			}
		}
	}
}
