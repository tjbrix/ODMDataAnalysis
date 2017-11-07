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

import java.util.HashMap;
import java.util.Map;
import static java.util.Objects.isNull;
import lombok.Getter;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.stereotype.Component;

/**
 * Map handling the different databases fopr each analysis request.
 * 
 * @author Tobias Brix
 */
@Component
public class DataSourceMap {

	@Getter
	private final Map<Object,Object> sourceMap;

	public DataSourceMap() {
		//initialize Map
		this.sourceMap = new HashMap<>();
	}

	/** create and add */
	public void addDataSource(String sessionID) {
		this.sourceMap.put(sessionID, new EmbeddedDatabaseBuilder().generateUniqueName(true)
																   .setType(EmbeddedDatabaseType.H2)
																   .addScript("SQL/script.sql")
																   .build());
	}

	/** remove and destroy */
	public void removeSource(String sessionID) {
		EmbeddedDatabase db = (EmbeddedDatabase) sourceMap.get(sessionID);
		if(!isNull(db)) db.shutdown();
		sourceMap.remove(sessionID);
	}
}
