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
package de.unimuenster.imi.odmda.performance.benchmark;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Used to collact all benchmark times and display them in a nice way.
 * 
 * @author Saad Sarfraz
 */
public class Benchmark {

	private Date benchmarkStart;
	private Date benchmarkEnd;

	private Date validationStart;
	private Date validationEnd;

	private Date parsingStart;
	private Date parsingEnd;

	private Date StudyEventCountStart;
	private Date StudyEventCountEnd;
	
	private Date formCountsStart;
	private Date formCountsEnd;

	private Date itemGroupCountsStart;
	private Date itemGroupCountsEnd;

	private Date itemCountsStart;
	private Date itemCountsEnd;

	private Date itemStatisticsStart;
	private Date itemStatisticsEnd;

	private Date pdfCreateStart;
	private Date pdfCreateEnd;

	private Date flagMetaInvalidValueStart;
	private Date flagMetaInvalidValueEnd;

	@Getter
	@Setter
private Date flagcompletenessValuesStart;

	@Getter
	@Setter
	private Date flagcompletenessValuesEnd;

	@Getter
	@Setter
	private Date calculateCompletenessStart;

	@Getter
	@Setter
	private Date calculateCompletenessEnd;

	@Getter
	@Setter
	private Date generalCompletenessStart;

	@Getter
	@Setter
	private Date generalCompletenessEnd;

	@Getter
	@Setter
	private Date mandatoryCompletenessStart;

	@Getter
	@Setter
	private Date mandatoryCompletenessEnd;

	@Getter
	@Setter
	private Date createCSVStart;

	@Getter
	@Setter
	private Date createCSVEnd;

	SimpleDateFormat format = new SimpleDateFormat();

	private HashMap<String, Double> benchmarkList = new LinkedHashMap<>();

	public HashMap<String, Double> getBenchmarkList() {
//			System.out.println(validationEnd.toString() + validationEnd.getTime());
//			Sstem.out.println(validationStart.toString() + validationStart.getTime());
		if(validationEnd!=null)
			benchmarkList.put("Validation",((double)(validationEnd.getTime()-validationStart.getTime()))/1000);
		if(parsingEnd!=null)
			benchmarkList.put("Parsing time", ((double)(parsingEnd.getTime()-parsingStart.getTime()))/1000);
		if(StudyEventCountEnd!=null)    
			benchmarkList.put("StudyEventCount", ((double)(StudyEventCountEnd.getTime()-StudyEventCountStart.getTime()))/1000);
		if(formCountsEnd!=null)
			benchmarkList.put("FormCount", ((double)(formCountsEnd.getTime()-formCountsStart.getTime()))/1000);
		if(itemGroupCountsEnd!=null)
			benchmarkList.put("ItemGroupCount", ((double)(itemGroupCountsEnd.getTime()-itemGroupCountsStart.getTime()))/1000);
		if(itemCountsEnd!=null)
			benchmarkList.put("ItemCount", ((double)(itemCountsEnd.getTime()-itemCountsStart.getTime()))/1000);
		if(flagMetaInvalidValueEnd!=null)
			benchmarkList.put("flagMetaInvalidValues", ((double)(flagMetaInvalidValueEnd.getTime()-flagMetaInvalidValueStart.getTime()))/1000);       
		if(itemStatisticsEnd!=null)
			benchmarkList.put("ItemStatisticsCount", ((double)(itemStatisticsEnd.getTime()-itemStatisticsStart.getTime()))/1000);      
		if(generalCompletenessEnd!=null)
			benchmarkList.put("General Completeness", ((double)(generalCompletenessEnd.getTime()-generalCompletenessStart.getTime()))/1000);
		if(mandatoryCompletenessEnd!=null)
			benchmarkList.put("Mandatory Completeness", ((double)(mandatoryCompletenessEnd.getTime()-mandatoryCompletenessStart.getTime()))/1000);
		if(flagcompletenessValuesEnd!=null)
			benchmarkList.put("Flag Completeness Values", ((double)(flagcompletenessValuesEnd.getTime()-flagcompletenessValuesStart.getTime()))/1000);
		if(calculateCompletenessEnd!=null)
			benchmarkList.put("Calculate Completeness", ((double)(calculateCompletenessEnd.getTime()-calculateCompletenessStart.getTime()))/1000); 
		if(pdfCreateEnd!=null)
			benchmarkList.put("PDFCreate", ((double)(pdfCreateEnd.getTime()-pdfCreateStart.getTime()))/1000);
		if(createCSVEnd!=null)
			benchmarkList.put("Export CSV", ((double)(createCSVEnd.getTime()-createCSVStart.getTime()))/1000);

		//set summeries
		benchmarkList.put("Total Parsing",    ((double)(validationEnd.getTime()- validationStart.getTime() +
					                                    parsingEnd.getTime()   - parsingStart.getTime()))/1000);
		if(StudyEventCountEnd != null) //if this is not null, then all are not null
			benchmarkList.put("Total Statistics", ((double)(StudyEventCountEnd.getTime()      - StudyEventCountStart.getTime() +
															formCountsEnd.getTime()           - formCountsStart.getTime() +
															itemGroupCountsEnd.getTime()      - itemGroupCountsStart.getTime() +
															itemCountsEnd.getTime()           - itemCountsStart.getTime() +
															flagMetaInvalidValueEnd.getTime() - flagMetaInvalidValueStart.getTime() +        
															itemStatisticsEnd.getTime()       - itemStatisticsStart.getTime()))/1000);
		if(calculateCompletenessEnd != null)
			benchmarkList.put("Total Completeness",    ((double)(calculateCompletenessEnd.getTime()-calculateCompletenessStart.getTime()))/1000);
		if(pdfCreateEnd != null)
			benchmarkList.put("Total PDF",    ((double)(pdfCreateEnd.getTime()-pdfCreateStart.getTime()))/1000);
		if(StudyEventCountEnd != null && calculateCompletenessEnd != null && pdfCreateEnd != null)
			benchmarkList.put("Total Overall",    ((double)(validationEnd.getTime()           - validationStart.getTime()+
						                                    parsingEnd.getTime()              - parsingStart.getTime() + 
					                                        StudyEventCountEnd.getTime()      - StudyEventCountStart.getTime() +
															formCountsEnd.getTime()           - formCountsStart.getTime() +
															itemGroupCountsEnd.getTime()      - itemGroupCountsStart.getTime() +
															itemCountsEnd.getTime()           - itemCountsStart.getTime() +
															flagMetaInvalidValueEnd.getTime() - flagMetaInvalidValueStart.getTime() +        
															itemStatisticsEnd.getTime()       - itemStatisticsStart.getTime() +
															calculateCompletenessEnd.getTime()- calculateCompletenessStart.getTime() +
															pdfCreateEnd.getTime()            - pdfCreateStart.getTime()))/1000);

		double sum = 0;
		for(double time : benchmarkList.values()){
			sum += time;
		}

		benchmarkList.put("Total sum", sum);

		if(benchmarkEnd!=null)
			benchmarkList.put("Total Benchmark", ((double)(benchmarkEnd.getTime()-benchmarkStart.getTime()))/1000);

		return benchmarkList;
	}

	public Date getValidationStart() {
		return validationStart;
	}
	public void setValidationStart(Date validationStart) {
		this.validationStart = validationStart;
	}
	public Date getValidationEnd() {
		return validationEnd;
	}
	public void setValidationEnd(Date validationEnd) {
		this.validationEnd = validationEnd;
	}
	public Date getParsingStart() {
		return parsingStart;
	}
	public void setParsingStart(Date parsingStart) {
		this.parsingStart = parsingStart;
	}
	public Date getParsingEnd() {
		return parsingEnd;
	}
	public void setParsingEnd(Date parsingEnd) {
		this.parsingEnd = parsingEnd;
	}
	public Date getStudyEventCountStart() {
		return StudyEventCountStart;
	}
	public void setStudyEventCountStart(Date studyEventCountStart) {
		StudyEventCountStart = studyEventCountStart;
	}
	public Date getStudyEventCountEnd() {
		return StudyEventCountEnd;
	}
	public void setStudyEventCountEnd(Date studyEventCountEnd) {
		StudyEventCountEnd = studyEventCountEnd;
	}
	public Date getFormCountsStart() {
		return formCountsStart;
	}
	public void setFormCountsStart(Date formCountsStart) {
		this.formCountsStart = formCountsStart;
	}
	public Date getFormCountsEnd() {
		return formCountsEnd;
	}
	public void setFormCountsEnd(Date formCountsEnd) {
		this.formCountsEnd = formCountsEnd;
	}
	public Date getItemGroupCountsStart() {
		return itemGroupCountsStart;
	}
	public void setItemGroupCountsStart(Date itemGroupCountsStart) {
		this.itemGroupCountsStart = itemGroupCountsStart;
	}
	public Date getItemGroupCountsEnd() {
		return itemGroupCountsEnd;
	}
	public void setItemGroupCountsEnd(Date itemGroupCountsEnd) {
		this.itemGroupCountsEnd = itemGroupCountsEnd;
	}
	public Date getItemStatisticsStart() {
		return itemStatisticsStart;
	}
	public void setItemStatisticsStart(Date itemStatisticsStart) {
		this.itemStatisticsStart = itemStatisticsStart;
	}
	public Date getItemStatisticsEnd() {
		return itemStatisticsEnd;
	}
	public void setItemStatisticsEnd(Date itemStatisticsEnd) {
		this.itemStatisticsEnd = itemStatisticsEnd;
	}
	public Date getPdfCreateStart() {
		return pdfCreateStart;
	}
	public void setPdfCreateStart(Date pdfCreateStart) {
		this.pdfCreateStart = pdfCreateStart;
	}
	public Date getPdfCreateEnd() {
		return pdfCreateEnd;
	}
	public void setPdfCreateEnd(Date pdfCreateEnd) {
		this.pdfCreateEnd = pdfCreateEnd;
	}

	public Date getItemCountsStart() {
		return itemCountsStart;
	}

	public void setItemCountsStart(Date itemCountsStart) {
		this.itemCountsStart = itemCountsStart;
	}
	public Date getItemCountsEnd() {
		return itemCountsEnd;
	}

	public void setItemCountsEnd(Date itemCountsEnd) {
		this.itemCountsEnd = itemCountsEnd;
	}

	public Date getBenchmarkStart() {
		return benchmarkStart;
	}

	public void setBenchmarkStart(Date benchmarkStart) {
		this.benchmarkStart = benchmarkStart;
	}

	public Date getBenchmarkEnd() {
		return benchmarkEnd;
	}

	public void setBenchmarkEnd(Date benchmarkEnd) {
		this.benchmarkEnd = benchmarkEnd;
	}

	public Date getFlagMetaInvalidValueEnd() {
		return flagMetaInvalidValueEnd;
	}

	public void setFlagMetaInvalidValueEnd(Date flagMetaInvalidValueEnd) {
		this.flagMetaInvalidValueEnd = flagMetaInvalidValueEnd;
	}

	public Date getFlagMetaInvalidValueStart() {
		return flagMetaInvalidValueStart;
	}

	public void setFlagMetaInvalidValueStart(Date flagMetaInvalidValueStart) {
		this.flagMetaInvalidValueStart = flagMetaInvalidValueStart;
	}

	@Override
	public String toString() {
		if(benchmarkList.isEmpty()) {
			return "Benchmark [benchmarkStart=" + benchmarkStart + ", benchmarkEnd=" + benchmarkEnd + ", validationStart="
					+ validationStart + ", validationEnd=" + validationEnd + ", parsingStart=" + parsingStart
					+ ", parsingEnd=" + parsingEnd + ", StudyEventCountStart=" + StudyEventCountStart
					+ ", StudyEventCountEnd=" + StudyEventCountEnd + ", formCountsStart=" + formCountsStart
					+ ", formCountsEnd=" + formCountsEnd + ", itemGroupCountsStart=" + itemGroupCountsStart
					+ ", itemGroupCountsEnd=" + itemGroupCountsEnd + ", itemCountsStart=" + itemCountsStart
					+ ", itemCountsEnd=" + itemCountsEnd + ", itemStatisticsStart=" + itemStatisticsStart
					+ ", itemStatisticsEnd=" + itemStatisticsEnd + ", pdfCreateStart=" + pdfCreateStart + ", pdfCreateEnd=" + pdfCreateEnd + "]";
		} else {
			String res = "";
			for(Map.Entry<String,Double> entry : benchmarkList.entrySet()) {
				res += entry.getKey() + " = " + entry.getValue() + "\n";
			}
			return res;
		}
	}
}
