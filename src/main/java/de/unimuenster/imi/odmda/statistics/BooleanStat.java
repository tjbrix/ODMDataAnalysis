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
import java.util.Base64;
import java.util.List;

/**
 * Statistic class used to calculate statistics and charts for items with the data type 'boolean'.
 * 
 * @author Saad Sarfraz
 */
public class BooleanStat extends StatisticsBase{

	private Integer trueCount, falseCount, undefinedCount;

	private String metaItemName;

	public BooleanStat(String metaItemName){
		this.metaItemName = metaItemName;
	}

	@Override
	public boolean calculateStatistics(List<String> valueList) throws SQLException{

		boolean success = false;

		int trueCounter = 0, falseCounter = 0, undefCounter = 0;

		for (String value : valueList) {
			switch(value) {
				case "true":
				case "1":
					trueCounter++;
					break;
				case "false":
				case "0":
					falseCounter++;
					break;
				default:
					undefCounter++;
			}
		}

		//if data is present
		if(trueCounter+falseCounter+undefCounter>0) {
			falseCount = falseCounter;
			trueCount = trueCounter;
			undefinedCount = undefCounter;
			success = true;
		}

		return success;
	}

	@Override
	public String toString(boolean inline) {
		return "True: " + trueCount + " | False: " + falseCount  ;
	}

	@Override
	public String getStatisticChartBase64() {
		if(trueCount !=null && falseCount != null){
			byte[] array =drawBooleanChart(metaItemName,trueCount, falseCount);
			return Base64.getEncoder().encodeToString(array);
		}else
			return null;
	}

	@Override
	public byte[] getStatisticChartBytes() {
		// TODO Auto-generated method stub
		return drawBooleanChart(metaItemName,trueCount, falseCount);
	}
}
