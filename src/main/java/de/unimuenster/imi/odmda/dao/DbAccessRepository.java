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
package de.unimuenster.imi.odmda.dao;

import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalFormData;
import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalItemData;
import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalItemGroupData;
import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalStudyEventData;
import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalSubjectData;
import de.unimuenster.imi.odmda.model.clinicaldata.InvalidClinicalData;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.unimuenster.imi.odmda.model.metadata.DataType;
import de.unimuenster.imi.odmda.model.metadata.MetaForm;
import de.unimuenster.imi.odmda.model.metadata.MetaItem;
import de.unimuenster.imi.odmda.model.metadata.MetaItemGroup;
import de.unimuenster.imi.odmda.model.metadata.MetaStudyEvent;
import de.unimuenster.imi.odmda.statistics.BooleanStat;
import de.unimuenster.imi.odmda.statistics.CodeListStat;
import de.unimuenster.imi.odmda.statistics.DatetimeStat;
import de.unimuenster.imi.odmda.statistics.DoubleStat;
import de.unimuenster.imi.odmda.statistics.StatisticsBase;
import de.unimuenster.imi.odmda.statistics.StringStat;
import de.unimuenster.imi.odmda.statistics.UnsupportedStat;
import de.unimuenster.imi.odmda.utils.CompletenessValue;
import de.unimuenster.imi.odmda.utils.ProgressBarIterator;
import de.unimuenster.imi.odmda.utils.ProgressBarState;

import java.math.BigDecimal;
import java.sql.SQLException;
import static java.util.Objects.isNull;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.HibernateException;

/**
 * This class handles all not trivial database accesses/calculations.
 * 
 * @author Justin Doods
 * @author Tobias Brix
 */
@Repository
public class DbAccessRepository {

	/** Enum used in a single methode in this class. */
	public enum MetaType {
	METASTUDYEVENT,METAFORM,METAITEMGROUP,METAITEM
	};

	/**
	 * Factory for the DB access.
	 */
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private DaoFactory daoFactory;

	/**
	 * Used for logging
	 */
	private static final Logger LOGGER = LogManager.getLogger(DbAccessRepository.class);

	@Transactional(propagation = Propagation.REQUIRED)
	public void getSchema(){
		getSession().createSQLQuery("SCRIPT TO 'D:/script.txt'").list();
	}

	/**
	 * Used as short cut to get the current session.
	 * 
	 * @return the current session 
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Session getSession(){
		Session session;
		try {
			session = sessionFactory.getCurrentSession();
		} catch (HibernateException e) {
			session = sessionFactory.openSession();
		}		
		return session;
	}

	/**
	 * Updates the data and subject counts of all StudyEvents.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateStudyEventCounts(){
		LOGGER.debug("Start creating StudyEvent counts");

		getSession().createNativeQuery(
						" UPDATE MetaStudyEvent"
					+   " SET (dataCounts, subjectCounts) = (SELECT COUNT(DISTINCT CONCAT( subjectkey, '###', studyEventRepeatKey)), "
						  + "										COUNT(DISTINCT subjectKey) " 
						  + "								 FROM ClinicalData as cd "
						  + "								 WHERE metaStudyEvent_id = MetaStudyEvent.id"
						  + "									   AND cd.value IS NOT NULL " 
						  + "									   AND cd.value != '') "
					).executeUpdate();
		
		LOGGER.debug("Finished creating StudyEvent counts");
	}

	/**
	 * Updates the data and subject counts of all Forms.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateFormCounts(){
		LOGGER.debug("Start creating Form counts");

		getSession().createNativeQuery(
						" UPDATE MetaForm"
					+   " SET (dataCounts, subjectCounts) = (SELECT COUNT(DISTINCT CONCAT( subjectkey, '###', metaStudyEvent_id, '###', studyEventRepeatKey, "
						  +	"																		                             '###', formRepeatKey)), "
						  + "										COUNT(DISTINCT subjectKey) " 
						  + "								 FROM ClinicalData as cd "
						  + "								 WHERE metaForm_id = MetaForm.id"
						  + "									   AND cd.value IS NOT NULL " 
						  + "									   AND cd.value != '') "
					).executeUpdate();

		LOGGER.debug("Finished creating Form counts");	
	}

	/**
	 * Updates the data and subject counts of all MetaItemGroups.
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateItemGroupCounts() {
		LOGGER.debug("Start creating ItemGroup counts");

		getSession().createNativeQuery(
						" UPDATE MetaItemGroup"
					+   " SET (dataCounts, subjectCounts) = (SELECT COUNT(DISTINCT CONCAT( subjectkey, '###', metaStudyEvent_id, '###', studyEventRepeatKey, "
						  +	"																		   '###', metaForm_id,       '###', formRepeatKey, "
						  + "                                                                                                    '###', itemGroupRepeatKey)), "
						  + "										COUNT(DISTINCT subjectKey) " 
						  + "								 FROM ClinicalData as cd "
						  + "								 WHERE metaItemGroup_id = MetaItemGroup.id"
						  + "									   AND cd.value IS NOT NULL " 
						  + "									   AND cd.value != '') "
					).executeUpdate();

		LOGGER.debug("Finished creating ItemGroup counts");
	}

	/**
	 * Updates the data and subject counts of all MetaItems.
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateItemCounts() {
		LOGGER.debug("Start updating Item counts");

		getSession().createNativeQuery(
						" UPDATE MetaItem"
					+   " SET (dataCounts, subjectCounts) = (SELECT COUNT(id), COUNT(DISTINCT subjectKey) " 
						  + "								 FROM ClinicalData as cd "
						  + "								 WHERE metaItem_id = MetaItem.id"
						  + "									   AND cd.value IS NOT NULL " 
						  + "									   AND cd.value != '') "
					).executeUpdate();

		LOGGER.debug("Finished creating Item counts");
	}

	/**
	 * Calculates additional statistics for each item based on their type and
	 * returns a Map. 
     * The key consists of the studyEventOID + formOID + itemGroupOID + itemOID,
	 * the value is the string with the information of interest. "Value-types":
	 * Codelist: counts and shows the top5 codelistItems (int and string)
	 * Numbers: min, max, mean, median, standard deviation 
     * String: counts and shows the top 3 item values. 
     * Date/Datetime: determines earliest and latest date(/time)
     * Boolean: counts true and false values *
	 * 
	 * @param metaDataList
	 * @param progress
	 * @param taskPercentage
	 * @return Map<String, String>
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public Map<String, StatisticsBase> getItemStatistics(List<MetaStudyEvent> metaDataList, ProgressBarState progress, int taskPercentage) {
		
		LOGGER.debug("Start creating Item statistics");

		ProgressBarIterator iterator = new ProgressBarIterator(progress, ProgressBarState.SubTask.STATISTICS, countMetaItem(metaDataList), taskPercentage);

		Map<String, StatisticsBase> returnMap;
			returnMap = new HashMap<>();
			for(MetaStudyEvent mse : metaDataList) {
				for(MetaForm mf : mse.getMetaFormList()) {
					for(MetaItemGroup mig : mf.getMetaItemGroupList()) {
						for(MetaItem mi : mig.getMetaItemList()) {
							List<String> valueList = getSession().createQuery(
									" SELECT cid.value"
								  + " FROM ClinicalItemData cid"
								  + " WHERE metaItem.id = :pMID"
									  + "   AND cid.value IS NOT NULL "
									  + "   AND cid.value != '' "
							).setParameter("pMID", mi.getId()).getResultList();
							returnMap.put(mse.getOID() + " " + mf.getOID() + " " + mig.getOID() + " " + mi.getOID(), getItemStatistics(mi.getDataType(), valueList, mi));

							iterator.iterate();
						}
					}
				}
			}

		LOGGER.debug("Finished creating Item statistics");

		return returnMap;
	}

	/**
	 * helper method for the creation of item statistics. Determines whether the
	 * Item has a CodeList or not
	 * 
	 * @param dataType
	 * @param clinicalItemList
	 * @param mi
	 * @return [Number of Items, Statistics]
	 */
	private StatisticsBase getItemStatistics(DataType dataType, List<String> valueList, MetaItem mi) {

		StatisticsBase statistics = null;

		try {
			if (mi.getCodeList() == null) {
				statistics = getItemStatisticsNoCL(dataType, valueList,mi);
			} else {
				statistics =  getItemStatisticsCL(valueList, mi);
			}
		} catch (SQLException ex) {
			LOGGER.error("SQL Exception when parsing a Clob to String", ex);
		}

		return statistics;
	}

	
	/**
	 * creates statistics for different supported types. 
	 * @param dataType is type of data for which statistics are to be computed, if none of the
	 * supported type is matched, then object of  type UnsupportedStat will be returned
	 * @param valueList 
	 * @param mi
	 * @return Matched and Statistics calculation success: Object of type specified in the parameter will be returned. 
	 *         Matched but Statistics calculation fails: null
	 *         No Match Found: Object of type 'UnsupportedStat'
	 * @throws SQLException
	 */
	private StatisticsBase getItemStatisticsNoCL(DataType dataType, List<String> valueList, MetaItem mi) throws SQLException {
	
		StatisticsBase statistics = null;

		if (null != dataType) switch (dataType) {
			case Boolean: {
				BooleanStat boolStat = new BooleanStat(mi.getName());
				if(boolStat.calculateStatistics(valueList)){
					statistics = boolStat;
				}
				break;
				}
			case Integer:
			case Float:
			case Double: {
				DoubleStat doubleStat = new DoubleStat(mi.getName(),dataType);
				if(doubleStat.calculateStatistics(valueList)){
					statistics = doubleStat;
				}
				break;
				}
			case Text:
			case String:{
				StringStat stringStat = new StringStat();
				if(stringStat.calculateStatistics(valueList)){
					statistics = stringStat;
				}
				break;
				}
			case Date:
			case Time:
			case Datetime:{
				DatetimeStat dateTimeStat = new DatetimeStat(dataType,mi.getName());
				if(dateTimeStat.calculateStatistics(valueList)){
					statistics = dateTimeStat;
				}
				break;
				}
			default:
				statistics =  new UnsupportedStat();
				break;
		}

		return statistics;
	}

	/**
	 * Creates the Statistics for CodeLists.
	 * 
	 * @param clinicalItemList
	 * @param mi
	 */
	private StatisticsBase getItemStatisticsCL(List<String> valueList, MetaItem mi) throws SQLException {
		
		CodeListStat clStatistics = new CodeListStat(mi);
		if(clStatistics.calculateStatistics(valueList)){
			return clStatistics;
		}

		return null;
	}

	/**
	 * helper method to count the metaItems (to help animate the progress bar)
	 */
	private int countMetaItem(List<MetaStudyEvent> metaDataList){

		int countMetaItem = 0;

		for(MetaStudyEvent mse : metaDataList) {
			for(MetaForm mf : mse.getMetaFormList()) {
				for(MetaItemGroup mig : mf.getMetaItemGroupList()) {
					countMetaItem += mig.getMetaItemList().size();
				}
			}
		}

		return countMetaItem;
	}

//------------------------------------------------------
//------------------------------------------------------
// Completness functions
//------------------------------------------------------
//------------------------------------------------------

	/**
	 * Returns the Number of additional MetaEntities caused by RepeatKeys.
	 * 
	 * For example, an ODMFile contains 10 subjects. Thus, for an completed study a total number of 10 Basline-Formulars 
	 * are expected in the clinical data. If one formular exists twice for a subject (repeatkey (a) and (b)), a total number 
	 * of 11 formulars is expected. This functions returns the additinal MetaEntitis caused by repeatkeys. In this example
	 * for the Baseline-Formular the return value will be 1, since due to two repeatkeys one additinal formular is created.
	 * 
	 * @note Used for the completeness calculation. 
	 * @param mt Returns all expected additinal entities of the passed type. Valid values are METASTUDYEVENT, METAFORM and METAITEMGROUP.
	 * @param odmID The analysis is limited to this ODMFile.
	 * @return A map, mapping the internal entity ID onto the additinal number of entities.
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	private Map<BigInteger, Integer> getRepeatKeyCounts(MetaType mt) {
		String queryString = " SELECT meta####_id, SUM(repeatkeys)"
						+    " FROM (" 
						+	 "		SELECT subjectKey, meta####_id, COUNT(DISTINCT ####RepeatKey) - 1 as repeatkeys " 
						+    "		FROM ClinicalData "
						+	 "		GROUP BY subjectKey, meta####_id "
						+	 "		HAVING COUNT (DISTINCT ####RepeatKey) >=2"
						+    "	   )"
						+	 " GROUP BY meta####_id ";

		switch (mt) {
			case METASTUDYEVENT:
				queryString = queryString.replace("####", "StudyEvent");
				break;
			case METAFORM:
				queryString = queryString.replace("####", "Form");
				break;
			case METAITEMGROUP:
				queryString = queryString.replace("####", "ItemGroup");
				break;
			default:
				//unsupported
				return null;
		}
		
		List<Object[]> rkList = getSession().createNativeQuery(queryString).getResultList();		
		return  rkList.stream().collect(Collectors.toMap(i -> (BigInteger) i[0], i -> ((BigDecimal) i[1]).intValue()));
	}

	/**
	 * Calculates the completeness. 
	 * @return 
	 */
	public List<MetaStudyEvent> getCompleteness() {
		List<MetaStudyEvent> metaData = daoFactory.getMetaStudyEventDao().getAllEntities();

		//calc all repeatkey counts
		Map<BigInteger, Integer> seRK = getRepeatKeyCounts(MetaType.METASTUDYEVENT);
		Map<BigInteger, Integer> fRK  = getRepeatKeyCounts(MetaType.METAFORM);
		Map<BigInteger, Integer> igRK = getRepeatKeyCounts(MetaType.METAITEMGROUP);
		int subjects = (int) daoFactory.getClinicalSubjectDataDao().getNumEntities();
		int i = 1;
		//add them to the metadata
		for(MetaStudyEvent mse : metaData) {
			CompletenessValue mseAllCompleteness = new CompletenessValue();
//			LOGGER.error("Completeness SE: " + i++ + "/" + metaData.size());
			//handle MF
			for(MetaForm mf : mse.getMetaFormList()) {
				CompletenessValue mfAllCompleteness = new CompletenessValue();
				
				//handle MIG
				for(MetaItemGroup mig : mf.getMetaItemGroupList()) {
					CompletenessValue migAllCompleteness = new CompletenessValue();
					
					//handle MI
					for(MetaItem mi : mig.getMetaItemList()) {
						// MI handle normal completeness
						CompletenessValue miC = new CompletenessValue(mi.getDataCounts(), subjects +	igRK.getOrDefault(mig.getId(),0) +
																										fRK.getOrDefault(mf.getId(),0) +
																										seRK.getOrDefault(mse.getId(),0)); 
						mi.setCompleteness(miC);
						migAllCompleteness.addCompleteness(miC);
					}//end item
					
					// MIG handle normal completeness
					mig.setCompleteness(migAllCompleteness);
					mfAllCompleteness.addCompleteness(migAllCompleteness);
					
				}//end item group
				
				// MF handle normal completeness
				mf.setCompleteness(mfAllCompleteness);
				mseAllCompleteness.addCompleteness(mfAllCompleteness);
					
			}//end form
			
			// MSE handle normal completeness
			mse.setCompleteness(mseAllCompleteness);
							
		} //end study event 
							
		return metaData;
	}

	//static StopWatch stopWatch = new StopWatch("Mandatory");
	
	/**
	 * Perform magic...
	 * @param progress
	 * @param taskPercentage
	 */
	public void updateMandatoryCompleteness(ProgressBarState progress, int taskPercentage) {
		
		//stopWatch.start("Initialize");
		
		//inizialize
		List<ClinicalSubjectData> subData = daoFactory.getClinicalSubjectDataDao().getAllEntities();
		List<MetaStudyEvent> metaData = daoFactory.getMetaStudyEventDao().getAllEntities();
		
		////////////////////////////////////////
		//For Benchmarking only
		//consumes 33% of total running time
		ProgressBarIterator iterateProgressBar = new ProgressBarIterator(progress, ProgressBarState.SubTask.COMPLETENESS, subData.size(), taskPercentage);
		///////////////////////////////////////

		//handle subjects
		for(ClinicalSubjectData sub : subData) {
			sub = daoFactory.getClinicalSubjectDataDao().initializedAllClinicalDataEagerly(sub.getId());
			//handle missing mse
			for(MetaStudyEvent mse : metaData) {
				if(mse.isMandatory() && !sub.getClinicalStudyEventDataList().stream().anyMatch(cse -> cse.getMetaStudyEvent().getId() == mse.getId())) {
					ClinicalStudyEventData newDummy = new ClinicalStudyEventData();
					newDummy.setClinicalSubjectData(sub);
					newDummy.setMetaStudyEvent(mse);
					newDummy.setRepeatKey(null);
					//daoFactory.getClinicalStudyEventDataDao().save(newDummy);
				}
			}

			//handle clinical se data
			Set<ClinicalStudyEventData> seData = sub.getClinicalStudyEventDataList();
			for(ClinicalStudyEventData cse : seData) {

				//handle missing mf
				List<MetaForm> mfList = cse.getMetaStudyEvent().getMetaFormList();
				for(MetaForm mf : mfList) {
					if(mf.isMandatory() && !cse.getClinicalFormDataList().stream().anyMatch(cf -> cf.getMetaForm().getId() == mf.getId())) {
						ClinicalFormData newDummy = new ClinicalFormData();
						newDummy.setClinicalStudyEventData(cse);
						newDummy.setMetaForm(mf);
						newDummy.setRepeatKey(null);
						//daoFactory.getClinicalFormDataDao().save(newDummy);
					}
				}

				//handle clinical f data
				Set<ClinicalFormData> fData = cse.getClinicalFormDataList();
				for(ClinicalFormData cf : fData) {

					//handle missing mig
					List<MetaItemGroup> migList = cf.getMetaForm().getMetaItemGroupList();
					for(MetaItemGroup mig : migList) {
						if(mig.isMandatory() && !cf.getClinicalItemGroupDataList().stream().anyMatch(cig -> cig.getMetaItemGroup().getId() == mig.getId())) {
							ClinicalItemGroupData newDummy = new ClinicalItemGroupData();
							newDummy.setClinicalFormData(cf);
							newDummy.setMetaItemGroup(mig);
							newDummy.setRepeatKey(null);
							//daoFactory.getClinicalItemGroupDataDao().save(newDummy);
						}
					}

					//handle clinical ig data
					Set<ClinicalItemGroupData> igData = cf.getClinicalItemGroupDataList();
					for(ClinicalItemGroupData cig : igData) {

						//handle missing mi
						List<MetaItem> miList = cig.getMetaItemGroup().getMetaItemList();
						for(MetaItem mi : miList) {
							if(mi.isMandatory() && !cig.getClinicalItemDataList().stream().anyMatch(ci -> ci.getMetaItem().getId() == mi.getId())) {
								ClinicalItemData newDummy = new ClinicalItemData();
								newDummy.setClinicalItemGroupData(cig);
								newDummy.setMetaItem(mi);
								newDummy.setValue(null);
								//daoFactory.getClinicalItemDataDao().save(newDummy);
							}
						}

						//handle clnical i data
						Set<ClinicalItemData> iData = cig.getClinicalItemDataList();
						for(ClinicalItemData ci : iData) {

							//handle missing codelists
							/* Nothing */

							//handle code lists
							/* Nothing */

							//if block ci
							if(ci.getMetaItem().isMandatory())
								ci.setCompleteness(ci.getValue() != null && !"".equals(ci.getValue()));
							else
								ci.setCompleteness(null);
						} //end i data 

						//if block cig
						if(cig.getMetaItemGroup().isMandatory()) { 
							if(cig.getClinicalItemDataList().size() > 0 && cig.getClinicalItemDataList().stream().allMatch(ci -> isNull(ci.getCompleteness()) || ci.getCompleteness() == true)) //NOT false
								cig.setCompleteness(true);
							else
								cig.setCompleteness(false);
						} else {
							if(cig.getClinicalItemDataList().isEmpty() || cig.getClinicalItemDataList().stream().allMatch(ci -> isNull(ci.getCompleteness())))
								cig.setCompleteness(null);
							else
								if(cig.getClinicalItemDataList().stream().anyMatch(ci -> Boolean.FALSE.equals(ci.getCompleteness()))) //null is ok
									cig.setCompleteness(false);
								else
									cig.setCompleteness(true);
						}
					} //end cig

					//if block cf
					if(cf.getMetaForm().isMandatory()) { 
						if(cf.getClinicalItemGroupDataList().size() > 0 && cf.getClinicalItemGroupDataList().stream().allMatch(cig -> isNull(cig.getCompleteness()) || cig.getCompleteness() == true)) //NOT false
							cf.setCompleteness(true);
						else
							cf.setCompleteness(false);
					} else {
						if(cf.getClinicalItemGroupDataList().isEmpty() || cf.getClinicalItemGroupDataList().stream().allMatch(cig -> isNull(cig.getCompleteness())))
							cf.setCompleteness(null);
						else
							if(cf.getClinicalItemGroupDataList().stream().anyMatch(cig -> Boolean.FALSE.equals(cig.getCompleteness()))) // null is ok
								cf.setCompleteness(false);
							else
								cf.setCompleteness(true);
					}
				} // end cf

				//if block cse
				if(cse.getMetaStudyEvent().isMandatory()) { 
					if(cse.getClinicalFormDataList().size() > 0 && cse.getClinicalFormDataList().stream().allMatch(cf -> isNull(cf.getCompleteness()) || cf.getCompleteness() == true)) //NOT false
						cse.setCompleteness(true);
					else
						cse.setCompleteness(false);
				} else {
					if(cse.getClinicalFormDataList().isEmpty() || cse.getClinicalFormDataList().stream().allMatch(cf -> isNull(cf.getCompleteness())))
						cse.setCompleteness(null);
					else
						if(cse.getClinicalFormDataList().stream().anyMatch(cf -> Boolean.FALSE.equals(cf.getCompleteness()))) // null is ok
							cse.setCompleteness(false);
						else
							cse.setCompleteness(true);
				}
			} // end cse

			//if block of sub (subs are always mandatory)
			if(sub.getClinicalStudyEventDataList().size() > 0 && sub.getClinicalStudyEventDataList().stream().allMatch(cse -> isNull(cse.getCompleteness()) || cse.getCompleteness() == true)) //NOT false
				sub.setCompleteness(true);
			else
				sub.setCompleteness(false);
			daoFactory.getClinicalSubjectDataDao().saveOrUpdate(sub);
			
			//////////////////////////////////////////////////////////////
			//pushing progress value
			iterateProgressBar.iterate();
			//////////////////////////////////////////////////////////////
			
		} //end sub

		//Update the meta information
		for(MetaStudyEvent mse : metaData) {
			mse.setMandatoryCompleteness(new CompletenessValue(
					mse.getClinicalStudyEventDataList().stream().mapToInt(cse -> (Boolean.TRUE.equals(cse.getCompleteness()) ? 1 : 0)).sum(),
					mse.getClinicalStudyEventDataList().stream().mapToInt(cse -> (isNull(cse.getCompleteness()) ? 0 : 1)).sum()));
			daoFactory.getMetaStudyEventDao().update(mse);
			for(MetaForm mf : mse.getMetaFormList()) {
				mf.setMandatoryCompleteness(new CompletenessValue(
						mf.getClinicalFormDataList().stream().mapToInt(cf -> (Boolean.TRUE.equals(cf.getCompleteness()) ? 1 : 0)).sum(),
						mf.getClinicalFormDataList().stream().mapToInt(cf -> (isNull(cf.getCompleteness()) ? 0 : 1)).sum()));
				daoFactory.getMetaFormDao().update(mf);
				for(MetaItemGroup mig : mf.getMetaItemGroupList()) {
					mig.setMandatoryCompleteness(new CompletenessValue(
							mig.getClinicalItemGroupDataList().stream().mapToInt(cig -> (Boolean.TRUE.equals(cig.getCompleteness()) ? 1 : 0)).sum(),
							mig.getClinicalItemGroupDataList().stream().mapToInt(cig -> (isNull(cig.getCompleteness()) ? 0 : 1)).sum()));
					daoFactory.getMetaItemGroupDao().update(mig);
					for(MetaItem mi : mig.getMetaItemList()) {
						mi.setMandatoryCompleteness(new CompletenessValue(
								mi.getClinicalItemDataList().stream().mapToInt(ci -> (Boolean.TRUE.equals(ci.getCompleteness()) ? 1 : 0)).sum(),
								mi.getClinicalItemDataList().stream().mapToInt(ci -> (isNull(ci.getCompleteness()) ? 0 : 1)).sum()));
						daoFactory.getMetaItemDao().update(mi);
					}
				}
			}
			//daoFactory.getMetaStudyEventDao().update(mse); // use bulk updates
		}
	}

	/**
	 * Helper method to update all meta-information for which invalid data
	 * entries were found. It will help to flag those invalid values in jsp file
	 * @return 
	 */
	public List<MetaStudyEvent> flagMetaInvalidValues(){
		List<InvalidClinicalData> invalidClinicalDataList = daoFactory.getInvalidClinicalDataDao().getAllEntities();
		List<MetaStudyEvent> metaStudyEventList = daoFactory.getMetaStudyEventDao().getAllEntities();
		for(InvalidClinicalData icd : invalidClinicalDataList){    
			for(MetaStudyEvent mse : metaStudyEventList){
				if(mse.getOID() == icd.getMetaStudyEventOID()){
				for(MetaForm mf : mse.getMetaFormList()){
					if(mf.getOID() == icd.getMetaFormOID()){
					for(MetaItemGroup mig : mf.getMetaItemGroupList()){
						if(mig.getOID() == icd.getMetaItemGroupOID()){
						for(MetaItem mi : mig.getMetaItemList()){
							if(mi.getOID() == icd.getMetaItemOID()){
								mse.setContainsInvalidValues(true);
								mf.setContainsInvalidValues(true);
								mig.setContainsInvalidValues(true);
								mi.setContainsInvalidValues(true);
								daoFactory.getMetaStudyEventDao().update(mse);
								daoFactory.getMetaFormDao().update(mf);
								daoFactory.getMetaItemGroupDao().update(mig);
								daoFactory.getMetaItemDao().update(mi);
							}
						}}
					}}
				}}
			}
		}
		return metaStudyEventList;
	}

	/**
	 * Marks datatype as mandatory or not based on MetaItemgroup and MetaItem 'mandatory' values
	 * If MetaGroupItem is mandatory and contains at least one filled metaitem, then MetaItemForm and MetaStudyEvent are
	 * also becomes as mandatory. The marked values as true are helpful in visualizing the mandatory values
	 * in jsp file
	 * @return
	 */
	public List<MetaStudyEvent> flagCompletenessMandatoryValues(){
		List<MetaStudyEvent> metaStudyEventList = daoFactory.getMetaStudyEventDao().getAllEntities();
		for(MetaStudyEvent mse : metaStudyEventList){
			if(mse.getMandatoryCompleteness()!=null){
				mse.setContainsMandatoryValues(true);
				daoFactory.getMetaStudyEventDao().update(mse);
			}
			for(MetaForm mf : mse.getMetaFormList()){
				if(mf.getMandatoryCompleteness()!=null){
					mf.setContainsMandatoryValues(true);
					mse.setContainsMandatoryValues(true);
					daoFactory.getMetaStudyEventDao().update(mse);
					daoFactory.getMetaFormDao().update(mf);
				}
				for(MetaItemGroup mig : mf.getMetaItemGroupList()){

					if(mig.getMandatoryCompleteness()!=null){
						mig.setContainsMandatoryValues(true);
						mf.setContainsMandatoryValues(true);
						mse.setContainsMandatoryValues(true);
						daoFactory.getMetaStudyEventDao().update(mse);
						daoFactory.getMetaFormDao().update(mf);
						daoFactory.getMetaItemGroupDao().update(mig);
					}

					for(MetaItem mi : mig.getMetaItemList()){
						if(mig.getMandatoryCompleteness()!=null && mi.isMandatory()){
							mig.setContainsMandatoryValues(true);
							mf.setContainsMandatoryValues(true);
							mse.setContainsMandatoryValues(true);
							daoFactory.getMetaStudyEventDao().update(mse);
							daoFactory.getMetaFormDao().update(mf);
							daoFactory.getMetaItemGroupDao().update(mig);
//							LOGGER.info(mi.getName() + " is mandatory in" + mig.getName() + " in " + mf.getName() + " in "  + mse.getName());
						}
					}
				}
			}
		}
		return metaStudyEventList;
	}

}// end class
