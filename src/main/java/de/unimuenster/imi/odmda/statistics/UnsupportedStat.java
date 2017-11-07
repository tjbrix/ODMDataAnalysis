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
import java.util.List;

/**
 * Statistic class used to calculate statistics and charts for items with the data type 'Unsupported'.
 * 
 * @author Saad Sarfraz
 */
public class UnsupportedStat extends StatisticsBase{

	@Override
	public boolean calculateStatistics(List<String> valueList) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString(boolean inline) {
		return "Statistics of this data type are not supported!";
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
