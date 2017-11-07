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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 * Statistic class used to calculate statistics and charts for items with the data type 'string' and 'text'.
 * 
 * @author Saad Sarfraz
 */
public class StringStat extends StatisticsBase{

	private int differentValues=0;

	//Max-number of different string values to be displayed
	private static final int LIMIT = 3;

	private Map<String,Integer> firstThreeValue = new LinkedHashMap<>();

	public int getDifferentValues() {
		return differentValues;
	}

	@Override
	public boolean calculateStatistics(List<String> valueList) {
		boolean success = false;
		Map<String, Integer> counterMap = new HashMap<>();

		// fill counter map
		for (String value : valueList) {
			if (counterMap.containsKey(value))
				counterMap.put(value, counterMap.get(value) + 1);
			else
				counterMap.put(value, 1);// counterMap didn't contain the value yet
		}

		// get statistics, if not empty 
		if (!counterMap.isEmpty()) {

			success = true;

			//sort map
			Stream<Map.Entry<String, Integer>> sortedDesc = counterMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
			Iterator<Entry<String, Integer>> iterator = sortedDesc.iterator();

			int counter = 0;
			differentValues = (Integer) counterMap.size();
			do {
				if (iterator.hasNext()) {
					counter = counter + 1;
					Entry<String, Integer> entry = (Entry<String, Integer>) iterator.next();
					//returnArray[counter] = entry.getKey(); returnArray[counter+3] = (Integer) entry.getValue();                        
					firstThreeValue.put(entry.getKey(), (Integer) entry.getValue());
				} else {
					counter = LIMIT;
				}
			} while (counterMap.size() > counter && counter < LIMIT);
		}
		return success;
	}

	@Override
	public String toString(boolean inline) {
		String result = "Different values: " + differentValues + " - Top3: " + (inline ? "" : "\n") ;
		int valueNumber = 1;
		for(String key : firstThreeValue.keySet()){
			result += valueNumber+". " + key+ " (" + firstThreeValue.get(key) + ")" + getDelimiter(inline,"|");
			valueNumber++;
		}

		return result;
	}

	@Override
	public String getStatisticChartBase64() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getStatisticChartBytes() {
		// TODO Auto-generated method stub
		return null;
	}
}
