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

import de.unimuenster.imi.odmda.utils.ProgressBarState.SubTask;

/**
 * Helper class to increment the Progressbar for a expensive task which has iterations. 
 * The progressbar can be incremented for each iteration by dividing the total percentage it covers to size of loop.
 * Progressbar can then be incremented for each loop. 
 * 
 * @author Saad Sarfraz
 * @author Tobias Brix
 */
public class ProgressBarIterator {

	private ProgressBarState currentState;
	private final SubTask currentTask; 

	//percentage / size of loop
	private final double incrementPerLoop;

	/**
	 * 
	 * @param progress current progress, which should be increased
	 * @param task must be a valid sub task
	 * @param totalIterations values >0
	 * @param coversPercentage must be >0
	 */
	public ProgressBarIterator(ProgressBarState progress, SubTask task, int totalIterations, double coversPercentage){
		currentState = progress;
		currentTask = task;

		incrementPerLoop = coversPercentage/totalIterations;
	}
	public ProgressBarIterator(ProgressBarState progress, SubTask task, int totalIterations){
		this(progress,task,totalIterations,100.d);
	}

	/**
	 * should be called for each loop iteration
	 * Require: number of method calls should be same as that of totalIterations specified in constructor
	 */
	public void iterate(){ 
		currentState.increaseState(currentTask, incrementPerLoop);
	}

	public void setProgress(double value) {
		currentState.setState(currentTask, value);
	}
}
