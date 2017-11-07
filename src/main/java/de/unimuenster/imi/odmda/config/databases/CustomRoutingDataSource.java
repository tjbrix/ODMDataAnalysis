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
package de.unimuenster.imi.odmda.config.databases;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * Handles which request belongs to which database instance.
 * 
 * @author Tobias Brix
 */
@Component
public class CustomRoutingDataSource extends AbstractRoutingDataSource{

	@Autowired
	DataSourceMap dataSources;

	@Override
	protected Object determineCurrentLookupKey() {
		setDataSources(dataSources); //must be reset to refresh the dataSources
		afterPropertiesSet();        //forces the refresh... StackOverflow..

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request.getAttribute("currentUploadCounter");
	}

	@Autowired
	public void setDataSources(DataSourceMap dataSources) {
		setTargetDataSources(dataSources.getSourceMap());
	}
}
