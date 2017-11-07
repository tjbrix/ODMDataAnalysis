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
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import de.unimuenster.imi.odmda.model.metadata.CodeListItem;
import de.unimuenster.imi.odmda.model.metadata.MetaItem;

/**
 * Statistic class used to calculate statistics and charts for items which are associated with a 'CodeList'.
 * 
 * @author Saad Sarfraz
 */
public class CodeListStat extends StatisticsBase{

	private List<CodeListItem> clItemList;

	private int differentValues = 0;
	private int codeListItemSize = 0;

	private String metaItemName;

	boolean success=false;
	List<String> stringList = new ArrayList<>();

	public CodeListStat(MetaItem mi) {
		metaItemName = mi.getName();
		clItemList = mi.getCodeList().getCodeListItemList();
	}

	@Override
	public boolean calculateStatistics(List<String> valueList) throws SQLException {

		Map<String, String> codeListItemMap = new HashMap<>();
		// fetches for the CodeList the first TranslatedText of all
		// CodeListItems and stores them with the codedValues of the CLI.
		// this can be skiped, if the codelist is based on EnumeratedItems.
		for (CodeListItem cli : clItemList) {
			if(!cli.getTranslatedTextList().isEmpty() ) {
				codeListItemMap.put(cli.getCodedValue(), cli.getTranslatedTextList().iterator().next().getText());
			} else {
				codeListItemMap.put(cli.getCodedValue(), null);
			}
		}

		//count quantities
		Map<String, Integer> counterMap = new HashMap<>();
		for (String value : valueList) {
			if (counterMap.containsKey(value))
				counterMap.put(value, counterMap.get(value) + 1);
			else
				counterMap.put(value, 1);// counterMap didn't contain the value yet
		}
		
		Stream<Map.Entry<String, Integer>> sortedDesc = counterMap.entrySet()
																  .stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
		Iterator<Entry<String, Integer>> iterator = sortedDesc.iterator();

		List<String> returnList = new ArrayList<>();        
		//copy all values, or use just 9 and combine the rest
		if(counterMap.size() <= 10) {
			do {
				if (iterator.hasNext()) {
					Entry<String, Integer> entry = (Entry<String, Integer>) iterator.next();
					returnList.add(entry.getKey());
					returnList.add(codeListItemMap.get(entry.getKey()));
					returnList.add(entry.getValue().toString());
				}
			} while (iterator.hasNext());
		} else {
			//handle first 9 values normally
			for (int i = 1; i <= 9; i++) {
				Entry<String, Integer> entry = (Entry<String, Integer>) iterator.next();
				returnList.add(entry.getKey());
				returnList.add(codeListItemMap.get(entry.getKey()));
				returnList.add(entry.getValue().toString()); 
			}
			//combine the rest for the 10th value
			int sum = 0;
			do {
				if (iterator.hasNext()) {
					Entry<String, Integer> entry = (Entry<String, Integer>) iterator.next();
					sum += entry.getValue();
				}
			} while (iterator.hasNext());
			returnList.add("all remaining items");
			returnList.add(null);
			returnList.add(((Integer) sum).toString());
		}

		if(!counterMap.isEmpty() && clItemList.size() > 0) { // was "codeListItemSize > 0" Error?
			differentValues =  counterMap.size();
			codeListItemSize = clItemList.size();
			stringList = returnList;
			success = true;
		}
		return success;
	}

	@Override
	public String toString(boolean inline) {
		return "Different values: " + differentValues + "/" + codeListItemSize + " - Top3: " + (inline ? "" : "\n") +
												 "1. " + (stringList.get(1) == null ? stringList.get(0) : stringList.get(1) + " (" + stringList.get(2) + ")") + 
														 (stringList.size() <= 3 ? "" : 
					  getDelimiter(inline,"|") + "2. " + (stringList.get(4) == null ? stringList.get(3) : stringList.get(4) + " (" + stringList.get(5) + ")")) +
														 (stringList.size() <= 6 ? "" :
					  getDelimiter(inline,"|") + "3. " + (stringList.get(7) == null ? stringList.get(6) : stringList.get(7) + " (" + stringList.get(8) + ")"));
	}

	@Override
	public String getStatisticChartBase64() {
		byte[] array =drawCodeListChart(metaItemName, (List<String>) stringList);
		return Base64.getEncoder().encodeToString(array);
	}

	@Override
	public byte[] getStatisticChartBytes() {
		return drawCodeListChart(metaItemName, (List<String>) stringList);
	}
}
