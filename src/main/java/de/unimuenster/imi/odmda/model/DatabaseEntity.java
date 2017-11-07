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
package de.unimuenster.imi.odmda.model;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


/**
 * Super class for most ODM meta entities and clinical data, e.g., MetaItem, MetaForm ...
 * It specifies the id and the connection to the associated ODMFile.
 * It does NOT specify the OID @see TranslatedText etc...
 *
 * @author Tobias Brix
 */
@MappedSuperclass
public abstract class DatabaseEntity implements Serializable {
	/**
	 * Auto-generated Id used for identification.
	 */
	@Id
	//@GeneratedValue(strategy=GenerationType.AUTO)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "sequence_generator") //AUTO
	@GenericGenerator(
			name = "sequence_generator",
			strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
			parameters = {
					@Parameter(name = "sequence_name", value = "hilo_seqeunce"),
					@Parameter(name = "initial_value", value = "1"),
					@Parameter(name = "increment_size", value = "256"),
					@Parameter(name = "optimizer", value = "hilo")
			})
	@Getter
	@Setter
	protected long id;
}
