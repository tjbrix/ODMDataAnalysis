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
package de.unimuenster.imi.odmda.utils;

import lombok.Getter;

/**
 * State of the ProgressBar during fileupload.
 * 
 * @author Tobias Brix
 */
public class ProgressBarState {

	public enum SubTask {
		PARSING(0), STATISTICS(1), COMPLETENESS(2), PDF(3);
		@Getter
		private final int index;
		SubTask(int index) {
			this.index = index;
		}
	}

	@Getter
	private double[] currentState;

	public ProgressBarState(){
		currentState = new double[SubTask.values().length];
	}

	//--------------
	// Manipulate
	//--------------
	public void increaseState(SubTask task, double increment) {
		currentState[task.getIndex()] += increment;
	}
	public void setState(SubTask task, double state) {
		currentState[task.getIndex()] = state;
	}

	//--------------
	// Output
	//--------------
	public double getState(SubTask task) {
		return currentState[task.getIndex()];
	}
} //end of class
