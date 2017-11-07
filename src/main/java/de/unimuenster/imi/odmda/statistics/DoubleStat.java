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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import de.unimuenster.imi.odmda.model.metadata.DataType;

/**
 * Statistic class used to calculate statistics and charts for items with the data type 'integer', 'floa' and 'double'.
 * 
 * @author Saad Sarfraz
 */
public class DoubleStat extends StatisticsBase{

	private double min, max;
	private double median, mean, std;

	//can be double or string
	private DataType dataType;

	private int invalidValues = 0;
	
	private List<Integer> histogramm = new ArrayList<>();

	private String metaItemName;

	public DoubleStat(String metaItemName,DataType dataType){
		this.metaItemName = metaItemName;
		this.dataType = dataType;
	}

	@Override
	public boolean calculateStatistics(List<String> valueList) throws SQLException {
		boolean  success = false;
		Double min = null, max = null;
		double median, mean, std;

//			DoubleStat doubleStat = new DoubleStat();
//			int invalidValues = 0;

		List<Double> doubleList = new ArrayList<>();
		for (String stringValue : valueList) {
			Double value = Double.parseDouble(stringValue.trim().replace(",", ".").replaceAll("[^0-9.]", ""));
			if (min == null || value < min) min = value;
			if (max == null || value > max) max = value;
			doubleList.add(value);
		}

		if (doubleList.size() > 0 && min!=null) {
			success = true;
			// sort list (needed for median)
			Collections.sort(doubleList);

			// Median
			if (doubleList.size() % 2 == 1)
				median = doubleList.get((((doubleList.size() + 1) / 2)) - 1);
			else
				median = (doubleList.get(((doubleList.size() / 2)) - 1) + doubleList.get(((doubleList.size() / 2)))) / 2;

			// Mean
			double sum = doubleList.stream().mapToDouble(Double::doubleValue).sum();
			mean = sum / doubleList.size();

			// Std
			std = Math.sqrt((doubleList.stream().mapToDouble(d -> d - mean).map(t -> t * t).sum() / doubleList.size()));

			this.min = min;
			this.max = max;
			this.mean = mean;
			this.median = median;
			this.std = std;

			if(min == max) {
				histogramm.add((Integer) doubleList.size());
			} else {
				double range = max - min;
				double bucketWidth = range / 10.0;
				for(int i = 0; i < 10; i++) {
					histogramm.add((Integer) 0);
				}
				for (Double d : doubleList) {
					int bucket = Math.min((int)((d - min) / bucketWidth),9);
					histogramm.set(bucket, (Integer) histogramm.get(bucket) + 1);
				}
			}
		}
		return success;
	}

	@Override
	public String toString(boolean inline) {
		if(dataType == DataType.Double || dataType  == DataType.Float){
			return  "Min: "    +  min + getDelimiter(inline,"|") +
					"Max: "    + max + getDelimiter(inline,"|") +
					"Mean: "   + new DecimalFormat("##0.0####",decimalFormatSymbols).format((Double) mean) + getDelimiter(inline,"|") +
					"Median: " + (Double) median + getDelimiter(inline,"|") +
					"StdDev: " + new DecimalFormat("##0.0####",decimalFormatSymbols).format((Double) std) +
																							( invalidValues> 0 ? getDelimiter(inline,"|") +
					"Invalid Values: " + invalidValues : "");
		}else{ //integer
			return  "Min: "    +  (int)min + getDelimiter(inline,"|") +
					"Max: "    + (int) max + getDelimiter(inline,"|") +
					"Mean: "   + new DecimalFormat("##0.0####",decimalFormatSymbols).format((Double) mean) + getDelimiter(inline,"|") +
					"Median: " + (Double) median + getDelimiter(inline,"|") +
					"StdDev: " + new DecimalFormat("##0.0####",decimalFormatSymbols).format((Double) std) +
																							( invalidValues> 0 ? getDelimiter(inline,"|") +
					"Invalid Values: " +invalidValues : "");
		}
	}

	@Override
	public String getStatisticChartBase64() {
//			LOGGER.info(this.toString());
		if(histogramm.size()>0){
			byte[] array = drawNumericChart(metaItemName, min, max, (List<Integer>) histogramm);
			return Base64.getEncoder().encodeToString(array);
		}else{
			return null;
		}
	}

	@Override
	public byte[] getStatisticChartBytes() {
		return drawNumericChart(metaItemName, min, max, (List<Integer>) histogramm);
	}
}
