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

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import de.unimuenster.imi.odmda.model.metadata.DataType;

/**
 * Statistic class used to calculate statistics and charts for items with the data type 'time', 'date' and 'datetime'.
 * 
 * @author Saad Sarfraz
 */
public class DatetimeStat extends StatisticsBase{

	private Date minDate = null, maxDate = null;
	private int invalidValues = 0;

	private List<Integer> histogramm = new ArrayList<>();

	private SimpleDateFormat databaseFormat = null;
	private DataType dataType;
	private String metaItemName;

	public DatetimeStat(DataType dataType,String metaItemName){
		this.metaItemName = metaItemName;
		this.dataType = dataType;

		switch (dataType) {
			case Date:
				databaseFormat = new SimpleDateFormat("yyyy-MM-dd");
				break;
			case Time:
				databaseFormat = new SimpleDateFormat("HH:mm:ss");
				break;
			case Datetime:
				databaseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
				break;
		}
	}

	@Override
	public boolean calculateStatistics(List<String> valueList) throws SQLException {

		boolean success = false;

		Date date = null; 
//			Date minDate = null; Date maxDate = null;
//			int invalidValues = 0;
		List<Date> dateList = new ArrayList<>();
		for (String value : valueList) {
			try {
				date = databaseFormat.parse(value);
				if (minDate == null || date.before(minDate)) {
					minDate = date;
				} 
				if (maxDate == null || date.after(maxDate)) {
					maxDate = date;
				}
				dateList.add(date);
			} catch (ParseException e) {
				//LOGGER.error("ParseException when parsing date", e);
				invalidValues++;
			}
		}

		if (!dateList.isEmpty() && minDate !=null) {
			success = true;
			if(minDate.compareTo(maxDate) == 0) {
				histogramm.add((Integer) valueList.size());
			} else {
				long bucketWidth = Math.max(1, ChronoUnit.SECONDS.between(minDate.toInstant(), maxDate.toInstant()) / 10);
				for(int i = 0; i < 10; i++) {
					histogramm.add((Integer) 0);
				}
				for (Date d : dateList) {
					int bucket = Math.min((int)((d.toInstant().getEpochSecond() - minDate.toInstant().getEpochSecond()) / bucketWidth),9);
					histogramm.set(bucket, (Integer) histogramm.get(bucket) + 1);
				}
			}
		} 
		return success;
	}


	public Date getMinDate() {
		return minDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public int getInvalidValues() {
		return invalidValues;
	}

	public List<Integer> getHistogramm() {
		return histogramm;
	}

	@Override
	public String toString(boolean inline) { 
		String returnString="";

		if(minDate == null){ //if atleast one value existed but invalid
			return "No values found!";
		}

		String pattern = "";
		switch (dataType) {
			case Date:
				pattern = "dd MMMMMMMMM yyyy";
				break;
			case Time:
				pattern = "HH:mm:ss";
				break;
			case Datetime:
				pattern = "HH:mm:ss 'on' dd MMMMMMMMM yyyy";
				break;
		}

		databaseFormat = new SimpleDateFormat(pattern);
		
		if(histogramm.size() == 1) {
			returnString = "Only one value: " + databaseFormat.format((Date) minDate);
		} else {
			returnString = "Range: " + databaseFormat.format((Date) minDate) + " - " + databaseFormat.format((Date) maxDate);
		}
		returnString += (invalidValues > 0 ? getDelimiter(inline,"|") +
						"Invalid Values: " +invalidValues : "");

		return returnString;
	}

	@Override
	public String getStatisticChartBase64() {
		if(histogramm.size() >0){
			byte[] array =drawDateChart(dataType, metaItemName, minDate, maxDate, (List<Integer>) histogramm);          
			return Base64.getEncoder().encodeToString(array);
		}else{
			return null;
		}
	}

	@Override
	public byte[] getStatisticChartBytes() {
		return drawDateChart(dataType, metaItemName, minDate, maxDate, (List<Integer>) histogramm);       
	}
}
