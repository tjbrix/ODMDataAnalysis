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
package de.unimuenster.imi.odmda.statistics;

import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.unimuenster.imi.odmda.model.metadata.DataType;

/**
 * Base class of all other statistic classes.
 * 
 * @author Saad Sarfraz
 */
public abstract class StatisticsBase {

	private static final Logger LOGGER = LogManager.getLogger(StatisticsBase.class);

	/**
	 * Magic number of each charts width.
	 */
	final static int CHART_WIDTH = 400;
	/**
	 * Magic number of each charts height.
	 */
	final static int CHART_HEIGHT = 300;

	protected DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
	
	/**
	 * 
	 * @param valueList List of values against which statistics are to be computed
	 * @return true: if statistics are calculated successfully,
	 *         false: if statistics are not calculated successfully
	 * @throws SQLException
	 */
	public abstract boolean calculateStatistics(List<String> valueList) throws SQLException;

	/**
	 * 
	 * @return absolute path to chart statistics 
	 */
	public abstract String getStatisticChartBase64();

	/**
	 * Function used to create a chart as PNG image. The return value is an byte[].
	 * 
	 * @see de.ukm.imi.controller.DBAccess for objects definition (depending on the DataType). 
	 * @return The PNG image as byte[]. returns null otherwise
	 */
	public abstract byte[] getStatisticChartBytes();

	public abstract String toString(boolean inline);

	/**
	 * Helper function to support inline Strings.
	 * 
	 * @param inline Should the String be inline?
	 * @param delimiter Used delimiter if inline is true.
	 * @return If inline is true, the output is " " + delimiter + " ", otherwise linebreak.
	 */
	protected String getDelimiter(boolean inline, String delimiter) {
		return (inline ? " " + delimiter + " " : " \n");
	}

	/**
	 * This function is used to draw a chart for boolean MetaItems. 
	 * 
	 * In the current implementation it is a Pie-Chart.
	 * 
	 * @param itemName Name of the item the chart is created for. It is used as chart title.
	 * @param trueCount Number of trues in the clinicla data.
	 * @param falseCount Number of false in the clinical data.
	 * @return The boolean chart as byte[]. 
	 */
	protected byte[] drawBooleanChart(String itemName, int trueCount, int falseCount) {

		DefaultPieDataset dpd = new DefaultPieDataset();
		dpd.setValue("True",trueCount);
		dpd.setValue("False",falseCount);

		//draw and customize the chart
		JFreeChart chart = ChartFactory.createPieChart3D(itemName,  // title 
															dpd,  // data
															true, // include legend
															false, // tooltip
															false); // URL
		chart.setBorderVisible(true); // makes the border visible
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setLabelGenerator(null); //remove labels from Pie pieces

		makeTitleFontSmaller(chart); //make title smaller

		//plot.setStartAngle(290);
		//plot.setDirection(Rotation.CLOCKWISE);
		//plot.setForegroundAlpha(1.0f);

		//copy chart to output
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try{
			ChartUtilities.writeChartAsPNG(stream, chart, CHART_WIDTH, CHART_HEIGHT);
			stream.close();
		}
		catch (IOException ex){
			ex.printStackTrace();
		}

		return stream.toByteArray();
	}

	/**
	 * This function is used to draw charts for Integer, Float and Double MetaItems. 
	 * 
	 * In the current implementation it is a histogramm with one or ten buckets.
	 * 
	 * @param itemName Name of the item the chart is created for. It is used as chart title.
	 * @param itemMin The minimum value of the clinical data. 
	 * @param itemMax The maximum value of the clinical data.
	 * @param bucketList The number of histogramm buckets. (Currently 1 or 10).
	 * @return The numeric chart as byte[].
	 */
	protected byte[] drawNumericChart(String itemName, double itemMin, double itemMax, List<Integer> bucketList) {

//			LOGGER.info("Numeric "+itemName + "itemMin" + itemMin + "bucketlistsize " + bucketList.size() );
		//define dataset
		final XYSeries series = new XYSeries("Histogram Data");
		//handle case with min==max separately  
		if(bucketList.size() == 1) {
			series.add( itemMin - 0.5,bucketList.get(0));
			series.add( itemMax + 0.5,bucketList.get(0));
		} else {
			double counter = 0.0;
			double width = (itemMax - itemMin) / (double) bucketList.size();
			for (Integer i : bucketList) {
				if(i != null) series.add(itemMin + width * counter, i);
				counter += 1.0;
			}
			series.add(itemMax, bucketList.get(bucketList.size()-1));
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);

		//draw and customize the chart  
		JFreeChart chart = ChartFactory.createXYLineChart(itemName + " - Histogram", "Range", "Counts", dataset, PlotOrientation.VERTICAL, false, false, false);

		chart.setBorderVisible(true); // makes the border visible
		XYPlot plot = chart.getXYPlot();
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); //show only integer as counts
		XYStepAreaRenderer renderer = new XYStepAreaRenderer(XYStepAreaRenderer.AREA);
		renderer.setDataBoundsIncludesVisibleSeriesOnly(false);
		//renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		//renderer.setDefaultEntityRadius(6);
		plot.setRenderer(renderer);
		makeTitleFontSmaller(chart); //make title smaller

		//copy chart to output        
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try{
			ChartUtilities.writeChartAsPNG(stream, chart, CHART_WIDTH, CHART_HEIGHT);
			stream.close();
		}
		catch (IOException ex){
			ex.printStackTrace();
		}

		return stream.toByteArray();
	}

	/**
	 * This function is used to draw charts for Date, Time and Datetime MetaItems. 
	 * 
	 * In the current implementation it is a histogramm with one or ten buckets.
	 * 
	 * @param dataType The exect data type of the meta item. It is used to format the chart labels.
	 * @param itemName Name of the item the chart is created for. It is used as chart title.
	 * @param minDate The minimum value of the clinical data. 
	 * @param maxDate The maximum value of the clinical data. 
	 * @param bucketList The number of histogramm buckets. (Currently 1 or 10).
	 * @return The data chart as byte[].  
	 */
	protected byte[] drawDateChart(DataType dataType, String itemName, Date minDate, Date maxDate, List<Integer> bucketList) {
//			LOGGER.info("Date "+itemName + "datatype" + dataType + "bucketlistsize " + bucketList.size() );
		//data type specific varibales
		String labelFormat = "";
		switch (dataType) {
			case Date:
				labelFormat = "dd.MM.yyyy";
				break;
			case Time:
				labelFormat = "HH:mm:ss";
				break;
			case Datetime:
				labelFormat = "HH:mm:ss dd.MM.yyyy";
				break;
		}

		//parse string to date
		//SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH);
		Calendar minDateCal = new GregorianCalendar();
		Calendar maxDateCal = new GregorianCalendar();
		minDateCal.setTime(minDate);//  dateFormat.parse(minDateStr));
		maxDateCal.setTime(maxDate);//dateFormat.parse(maxDateStr));

		//define dataset
		final TimeSeries series = new TimeSeries("Histogram Data");
		//handle case with min==max separately  
		if(minDateCal.compareTo(maxDateCal) == 0) {  
			try {
			series.add(new Second(minDateCal.get(Calendar.SECOND),minDateCal.get(Calendar.MINUTE),minDateCal.get(Calendar.HOUR_OF_DAY), // +1 month, since java counts January as 0...
								  minDateCal.get(Calendar.DAY_OF_MONTH),minDateCal.get(Calendar.MONTH)+1,minDateCal.get(Calendar.YEAR)), bucketList.get(0));
			series.add(new Second(minDateCal.get(Calendar.SECOND)+10,minDateCal.get(Calendar.MINUTE),minDateCal.get(Calendar.HOUR_OF_DAY), // +1 month, since java counts January as 0...
								  minDateCal.get(Calendar.DAY_OF_MONTH),minDateCal.get(Calendar.MONTH)+1,minDateCal.get(Calendar.YEAR)),bucketList.get(0));
			} catch(Exception e) {
				LOGGER.error("Invalid date range", e);
			}
		} else {
			long width = Math.max(1,ChronoUnit.MILLIS.between(minDateCal.toInstant(), maxDateCal.toInstant()) / bucketList.size());
			for (Integer i : bucketList) {
				try {
				series.add(new Second(minDateCal.get(Calendar.SECOND),minDateCal.get(Calendar.MINUTE),minDateCal.get(Calendar.HOUR_OF_DAY), // +1 month, since java counts January as 0...
									  minDateCal.get(Calendar.DAY_OF_MONTH),minDateCal.get(Calendar.MONTH)+1,minDateCal.get(Calendar.YEAR)), i);
				//minDateCal.add(Calendar.SECOND, (int) width); dont worg with data of year 9999 cause diff is no int
				minDateCal.setTimeInMillis(minDateCal.getTimeInMillis() + width);
				} catch(Exception e) {
					LOGGER.error("Invalid date range", e);
				}
			}
			try{
				series.add(new Second(maxDateCal.get(Calendar.SECOND),maxDateCal.get(Calendar.MINUTE),maxDateCal.get(Calendar.HOUR_OF_DAY), // +1 month, since java counts January as 0...
								maxDateCal.get(Calendar.DAY_OF_MONTH),maxDateCal.get(Calendar.MONTH)+1,maxDateCal.get(Calendar.YEAR)), bucketList.get(bucketList.size()-1));
			} catch(Exception e) {
				LOGGER.error("Invalid date range", e);
			}
		}
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(series);

		//draw and customize the chart      
		JFreeChart chart = ChartFactory.createXYLineChart(itemName + " - Histogram", "Range", "Counts", dataset, PlotOrientation.VERTICAL, false, false, false);

		chart.setBorderVisible(true); // makes the border visible
		XYPlot plot = chart.getXYPlot();
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); //show only integer as counts
		XYStepAreaRenderer renderer = new XYStepAreaRenderer(XYStepAreaRenderer.AREA);
		renderer.setDataBoundsIncludesVisibleSeriesOnly(false);
		//renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		//renderer.setDefaultEntityRadius(6);
		plot.setRenderer(renderer);
		makeTitleFontSmaller(chart); //make title smaller

		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		Font oldFont = xAxis.getLabelFont();
		DateAxis dateAxis = new DateAxis();
		dateAxis.setDateFormatOverride(new SimpleDateFormat(labelFormat)); 
		dateAxis.setLabel("Range");
		dateAxis.setLabelFont(oldFont);
		plot.setDomainAxis(dateAxis);

		//copy chart to output        
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try{
			ChartUtilities.writeChartAsPNG(stream, chart, CHART_WIDTH, CHART_HEIGHT);
			stream.close();
		}
		catch (IOException ex){
			ex.printStackTrace();
		}

		return stream.toByteArray();
	}

	/**
	 * This function is used to draw charts for CodeList MetaItems. 
	 * 
	 * In the current implementation it is a bar-plot with upto 10 bars.
	 * 
	 * @param itemName Name of the item the chart is created for. It is used as chart title.
	 * @param values A list consisting of CodeList values, names und their quantity.
	 * @return The CodeList chart as byte[].
	 */
	protected byte[] drawCodeListChart(String itemName, List<String> values) {
//			LOGGER.info("CL "+"itemName" + itemName + values.size());
		//define dataset
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(int i = 0; i < values.size(); i+=3) {
			String legendString = (values.get(i+1) == null ? values.get(i) : values.get(i+1) + " (" + values.get(i) +")");
			dataset.addValue(Integer.parseInt(values.get(i+2)), legendString, "");
		}

		//draw and customize the chart
		JFreeChart chart = ChartFactory.createBarChart(itemName,  // title 
														"", "",      
														dataset,  // data
														PlotOrientation.VERTICAL,
														true, // include legend
														false, // tooltip
														false); // URL

		chart.setBorderVisible(true); // makes the border visible
		CategoryPlot plot = chart.getCategoryPlot();
		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); //show only integer as counts
		makeTitleFontSmaller(chart); //make title smaller

		//copy chart to output        
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try{
			ChartUtilities.writeChartAsPNG(stream, chart, CHART_WIDTH, CHART_HEIGHT);
			stream.close();
		}
		catch (IOException ex){
			ex.printStackTrace();
		}

		return stream.toByteArray();
	}

	/**
	 * Makes the title font of each chart a little bit smaller.
	 * 
	 * @param chart Chart, which title should be smaller.
	 */
	private void makeTitleFontSmaller(JFreeChart chart) { 
		TextTitle title = chart.getTitle();
		Font oldFont = title.getFont();
		title.setFont(new Font(oldFont.getFamily(),oldFont.getStyle(),oldFont.getSize()-2));
	}
}
