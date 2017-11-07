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
package de.unimuenster.imi.odmda.parser;

import de.unimuenster.imi.odmda.dao.DaoFactory;
import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalData;

import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalFormData;
import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalItemData;
import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalItemGroupData;
import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalStudyEventData;
import de.unimuenster.imi.odmda.model.clinicaldata.ClinicalSubjectData;
import de.unimuenster.imi.odmda.model.clinicaldata.InvalidClinicalData;
import de.unimuenster.imi.odmda.model.metadata.CodeList;
import de.unimuenster.imi.odmda.model.metadata.CodeListItem;
import de.unimuenster.imi.odmda.model.metadata.DataType;
import de.unimuenster.imi.odmda.model.metadata.MetaForm;
import de.unimuenster.imi.odmda.model.metadata.MetaItem;
import de.unimuenster.imi.odmda.model.metadata.MetaItemGroup;
import de.unimuenster.imi.odmda.model.metadata.MetaStudy;
import de.unimuenster.imi.odmda.model.metadata.MetaStudyEvent;
import de.unimuenster.imi.odmda.model.metadata.MetaStudyEvent.StudyEventType;
import de.unimuenster.imi.odmda.model.metadata.ODMFile;
import de.unimuenster.imi.odmda.model.metadata.TranslatedText;
import de.unimuenster.imi.odmda.utils.ProgressBarIterator;
import de.unimuenster.imi.odmda.utils.OIDtoEntitiesMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static java.util.Objects.isNull;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

/**
 * Class implementation for parsing an ODM files with SAX.
 * 
 * @author Tobias Brix
 *
 */
public class ODMToDatabaseSaxParser extends DefaultHandler {
	
	/** Beans, autowire is not working. So added in Constructor */
	private final DaoFactory daoFactory;	
		
	//--------------------------------------
	// Helper needed for the meta data
	//--------------------------------------	
	
	/** Map to store meta item groups between Ref and Def */
	private final OIDtoEntitiesMap<MetaStudyEvent> metaStudyEventMap;   
	/** Map to store meta item groups between Ref and Def */
	private final OIDtoEntitiesMap<MetaForm> metaFormMap;
	/** Map to store meta item groups between Ref and Def */
	private final OIDtoEntitiesMap<MetaItemGroup> metaItemGroupMap;
	/** Map to store meta items between Ref and Def */
	private final OIDtoEntitiesMap<MetaItem> metaItemMap;
	/** Map to store CodeLists between Ref and Def */
	private final OIDtoEntitiesMap<CodeList> codeListMap;

	/** tracks the StudyEventOID of the last processed odm element. Use object, since there is only one. */
	private ODMFile currentODMFile;
	/** tracks the StudyEventOID of the last processed Study element, Use object, since there is only one*/
	private MetaStudy currentStudy;
	/** tracks the StudyEventOID of the last processed StudyEvent element */
	private String currentStudyEventOID;
	/** tracks the FormOID of the last processed Form element */
	private String currentFormOID;
	/** tracks the ItemGroupOID of the last processed ItemGroup element */
	private String currentItemGroupOID;
	/** tracks the ItemOID of the last processed Item element */
	private String currentItemOID;
	/** tracks the CodeListOID of the last processed CodeList element */
	private String currentCodeListOID;
	/** Tracks the CodeListIteme of the last processed CodeListItem element */
	private CodeListItem currentCodeListItem;
	/** object to store the current TranslatedText over events */
	private TranslatedText currentTT;
	/** tracks the value of TranslatedText elements */
	private String elementValue;

	//--------------------------------------
	// Helper needed for the clinical data
	//--------------------------------------
	/** */ 
	private MetaStudyEvent currentMetaStudyEventData;
	/** */
	private MetaForm currentMetaFormData;
	/** */
	private MetaItemGroup currentMetaItemGroupData;
	/** */
	private MetaItem currentMetaItemData;
		
	/** */ 
	private ClinicalSubjectData currentClinicalSubjectData;
	/** */ 
	private ClinicalStudyEventData currentClinicalStudyEventData;
	/** */
	private ClinicalFormData currentClinicalFormData;
	/** */
	private ClinicalItemGroupData currentClinicalItemGroupData;

	/** Used to store the first appearnce of a unknown OID in the clinical data */
	private String unknownClinicalOID;

	/** ProgressBar updates*/
	private ProgressBarIterator progressBarIterator;

	/**
	 * The constructor initializes all variables and sets the sessionFactory.
	 * 
	 * @param factory needed to access the databse.
	 */
	public ODMToDatabaseSaxParser(DaoFactory factory, ProgressBarIterator iterator) {
		this.daoFactory = factory;
		
		//maps
		this.metaStudyEventMap = new OIDtoEntitiesMap<>();
		this.metaFormMap = new OIDtoEntitiesMap<>();
		this.metaItemGroupMap = new OIDtoEntitiesMap<>();
		this.metaItemMap = new OIDtoEntitiesMap<>();
		this.codeListMap = new OIDtoEntitiesMap<>();

		//current values
		this.currentODMFile = null;
		this.currentStudy = null;
		this.currentStudyEventOID = null;
		this.currentFormOID = null;
		this.currentItemGroupOID = null;
		this.currentItemOID = null;
		this.currentCodeListOID = null;
		this.currentCodeListItem = null;
		this.elementValue = null;

		//current clinical meta item 
		this.currentMetaStudyEventData = null;
		this.currentMetaFormData = null;
		this.currentMetaItemGroupData = null;
		this.currentMetaItemData = null;

		//current clinical data
		this.currentClinicalSubjectData = null;
		this.currentClinicalStudyEventData = null;
		this.currentClinicalFormData = null;
		this.currentClinicalItemGroupData = null;
		this.unknownClinicalOID = null;

		//progress bar updates
		this.progressBarIterator = iterator;
	}

	public ODMFile getCurrentODMFile() {
		return this.currentODMFile;
	}

	//static int i = 1;
	//static StopWatch stopWatchMeta = new StopWatch("Meta Watch");
	//static StopWatch stopWatchCD = new StopWatch("CD Watch");
	
	/**
	 * method gets called every time a start element is being processed
	 * @param uri
	 * @param localName
	 * @param qName
	 * @param attributes
	 * @throws org.xml.sax.SAXException
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//original if else with (qName.equalsIgnoreCase("Study") || localName.equals("Study"))
		switch (localName) {
			case "ODM": {
				String oID = attributes.getValue("FileOID");
				currentODMFile = new ODMFile(oID);
				daoFactory.getOdmFileDao().save(currentODMFile);
				} break;
			// start MetaData
			case "Study": {
					//stopWatchMeta.start();
					String oID = attributes.getValue("OID");
					currentStudy = new MetaStudy();
					currentStudy.setOID(oID);
					// will be saved at EndElement and cascades all meta items 
				} break;
			case "StudyEventRef": {
				String oid = attributes.getValue("StudyEventOID");
				Integer orderNumber = this.getOrderNumber(attributes);
				boolean mandatory = this.getMandatory(attributes);				
				//update meta forms
				//List<MetaStudy> currentStudys = daoFactory.getMetaStudyDao().getAllEntitiesByOID(currentStudyOID, currentODMFile.getId());
				//for(MetaStudy ms : currentStudys ) {
					MetaStudyEvent mse = new MetaStudyEvent();
					mse.setOID(oid);
					mse.setOrderNumber(orderNumber);
					mse.setMandatory(mandatory);				
					//mse.setMetaStudy(ms);
					metaStudyEventMap.add(mse);// add to list so that ItemDef attributes can be added later
				//}
			} break;
			case "StudyEventDef": {
				String oID = attributes.getValue("OID");
				String name = attributes.getValue("Name");
				StudyEventType type = this.getStudyEventType(attributes);
				boolean repeating = this.getRepeating(attributes);
				currentStudyEventOID = oID;
				//update all item groups
				for (MetaStudyEvent mse : metaStudyEventMap.getEntities(oID)) {
					mse.setName(name);
					mse.setRepeating(repeating);
					mse.setType(type);
					daoFactory.getMetaStudyEventDao().save(mse);
				}
				metaStudyEventMap.remove(oID);
			} break;
			case "FormRef": {
				String oid = attributes.getValue("FormOID");
				Integer orderNumber = this.getOrderNumber(attributes);
				boolean mandatory = this.getMandatory(attributes);				
				//update meta forms
				List<MetaStudyEvent> currentStudyEvents = daoFactory.getMetaStudyEventDao().getAllEntitiesByOID(currentStudyEventOID);
				for(MetaStudyEvent mse : currentStudyEvents ) {
					MetaForm mf = new MetaForm();
					mf.setOID(oid);
					mf.setOrderNumber(orderNumber);
					mf.setMandatory(mandatory);				
					mf.setMetaStudyEvent(mse);
					metaFormMap.add(mf);// add to list so that ItemDef attributes can be added later
				}
			} break;
			case "FormDef": {
				String oID = attributes.getValue("OID");
				String name = attributes.getValue("Name");
				boolean repeating = this.getRepeating(attributes);
				currentFormOID = oID;
				//update all item groups
				for (MetaForm mf : metaFormMap.getEntities(oID)) {
					mf.setName(name);
					mf.setRepeating(repeating);
					daoFactory.getMetaFormDao().save(mf);
				}
				metaFormMap.remove(oID);
			} break;
			case "ItemGroupRef": {
				String oid = attributes.getValue("ItemGroupOID");
				Integer orderNumber = this.getOrderNumber(attributes);
				boolean mandatory = this.getMandatory(attributes);				
				//update meta forms
				List<MetaForm> currentForms = daoFactory.getMetaFormDao().getAllEntitiesByOID(currentFormOID);
				for(MetaForm mf : currentForms ) {
					MetaItemGroup mig = new MetaItemGroup();
					mig.setOID(oid);
					mig.setOrderNumber(orderNumber);
					mig.setMandatory(mandatory);				
					mig.setMetaForm(mf);
					metaItemGroupMap.add(mig);// add to list so that ItemDef attributes can be added later
				}
			} break;
			case "ItemGroupDef": {
				String oID = attributes.getValue("OID");
				String name = attributes.getValue("Name");
				boolean repeating = this.getRepeating(attributes);
				currentItemGroupOID = oID;
				//update all item groups
				for (MetaItemGroup mig : metaItemGroupMap.getEntities(oID)) {
					mig.setName(name);
					mig.setRepeating(repeating);
					daoFactory.getMetaItemGroupDao().save(mig);
				}
				metaItemGroupMap.remove(oID);
			} break;
			case "ItemRef": {
				String oid = attributes.getValue("ItemOID");
				Integer orderNumber = this.getOrderNumber(attributes);
				boolean mandatory = this.getMandatory(attributes);
				//update meta itemgroups
				List<MetaItemGroup> currentGroups = daoFactory.getMetaItemGroupDao().getAllEntitiesByOID(currentItemGroupOID);
				for(MetaItemGroup mig : currentGroups ) {
					MetaItem mi = new MetaItem();
					mi.setOID(oid);
					mi.setOrderNumber(orderNumber);
					mi.setMandatory(mandatory);				
					mi.setMetaItemGroup(mig);
					metaItemMap.add(mi);// add to list so that ItemDef attributes can be added later
				}
			} break;
			case "ItemDef": {
				String oID = attributes.getValue("OID");
				String name = attributes.getValue("Name");
				DataType dt = DataType.getDataType(attributes.getValue("DataType"));
				currentItemOID = oID;
  				//update all items
				for (MetaItem mi : metaItemMap.getEntities(oID)) {
					mi.setName(name);
					mi.setDataType(dt);
					daoFactory.getMetaItemDao().save(mi);
				}
				metaItemMap.remove(oID);
			} break;
			case "TranslatedText":
				String lang = attributes.getValue("xml:lang");
				currentTT = new TranslatedText();
				currentTT.setLangAttribute(lang);
				break;
			case "CodeListRef": {
				String oID = attributes.getValue("CodeListOID");
				//CodeLists are only needed once
				List<CodeList> list = codeListMap.get(oID);
				if(list == null) {
					CodeList codeList = new CodeList();
					codeList.setOID(oID);
					codeList.associatedMetaItemOIDs.add(currentItemOID);
					codeListMap.add(codeList);
				} else {
					list.get(0).associatedMetaItemOIDs.add(currentItemOID);
				}
			} break;
			case "CodeList": {
				String oID = attributes.getValue("OID");
				String name = attributes.getValue("Name");
				String dataTypeString = attributes.getValue("DataType");
				DataType dataType = DataType.getDataType(dataTypeString);
				//CodeListRef is missing (NOT GOOD!!! Remove silent.)
				if(codeListMap.getEntities(oID).isEmpty()) {
					currentCodeListOID = null;
				} else { 
					//update all lists (should be only one)
					currentCodeListOID = oID;
					for (CodeList cl : codeListMap.getEntities(oID)) {
						cl.setName(name);
						cl.setDataType(dataType);
						daoFactory.getCodeListDao().save(cl);
						for(String itemOID : cl.associatedMetaItemOIDs) {
							List<MetaItem> itemList = daoFactory.getMetaItemDao().getAllEntitiesByOID(itemOID);
							if(itemList != null) {
								for(MetaItem mi : itemList) {
									mi.setCodeList(cl);
									daoFactory.getMetaItemDao().update(mi);
								}
							}
						}
						cl.associatedMetaItemOIDs = null;
					}
					codeListMap.remove(oID);
				}
			} break;
			case "CodeListItem":
			case "EnumeratedItem": {
				//Can be null. if codelistref is missing... handle silent...
				if(!isNull(currentCodeListOID)) {
					String codedValue = attributes.getValue("CodedValue");
					CodeListItem cli = new CodeListItem();
					cli.setCodedValue(codedValue);
					cli.setCodeList(daoFactory.getCodeListDao().getAllEntitiesByOID(currentCodeListOID).get(0)); 
					currentCodeListItem = cli;
				}
			} break;
				
				//--------------------------------------------------------------------------------------
				//--------------------------------------------------------------------------------------
				// Start ClinicalData
				//--------------------------------------------------------------------------------------
				//--------------------------------------------------------------------------------------
			case "ClinicalData":
				break;
			case "SubjectData":
				progressBarIterator.iterate(); //update the progressbar
				currentClinicalSubjectData = new ClinicalSubjectData();
				currentClinicalSubjectData.setSubjectKey(attributes.getValue("SubjectKey"));
				break;
			case "StudyEventData": {
				String oid = attributes.getValue("StudyEventOID");
				List<MetaStudyEvent> mseList = daoFactory.getMetaStudyEventDao().getAllEntitiesByOID(oid);
				if(mseList.size() == 1) {					
					currentMetaStudyEventData = mseList.get(0);
					//add study event only if it is present
					currentClinicalStudyEventData = new ClinicalStudyEventData();
					currentClinicalStudyEventData.setRepeatKey(attributes.getValue("StudyEventRepeatKey"));
					currentMetaStudyEventData.addClinicalStudyEventData(currentClinicalStudyEventData);
					currentClinicalStudyEventData.setClinicalSubjectData(currentClinicalSubjectData);
				} else {
					unknownClinicalOID = oid;
					saveClinicalData(null); // will be invalid data
				}
			} break;
			case "FormData":
				if(currentMetaStudyEventData != null) {
					String oid = attributes.getValue("FormOID");
					Optional<MetaForm> opt = currentMetaStudyEventData.getMetaFormList().stream().filter(mf -> mf.getOID().equals(oid)).findFirst();
					if(opt.isPresent()) {
						currentMetaFormData = opt.get();
						currentClinicalFormData = new ClinicalFormData();
						currentClinicalFormData.setRepeatKey(attributes.getValue("FormRepeatKey"));
						currentMetaFormData.addClinicalFormData(currentClinicalFormData);
						currentClinicalFormData.setClinicalStudyEventData(currentClinicalStudyEventData);
					} else {
						unknownClinicalOID = oid;
						saveClinicalData(null); // will be invalid data
					}
				}
				break;
			case "ItemGroupData":
				if(currentMetaFormData != null) {
					String oid = attributes.getValue("ItemGroupOID");					
					Optional<MetaItemGroup> opt = currentMetaFormData.getMetaItemGroupList().stream().filter(mig -> mig.getOID().equals(oid)).findFirst();
					if(opt.isPresent()) {
						currentMetaItemGroupData = opt.get();
						currentClinicalItemGroupData = new ClinicalItemGroupData();
						currentClinicalItemGroupData.setRepeatKey(attributes.getValue("ItemGroupRepeatKey"));
						currentMetaItemGroupData.addClinicalItemGroupData(currentClinicalItemGroupData);
						currentClinicalItemGroupData.setClinicalFormData(currentClinicalFormData);
					} else {
						unknownClinicalOID = oid;
						saveClinicalData(null); // will be invalid data
					}
				}
				break;
			case "ItemData":
				if(currentMetaItemGroupData != null) {
					String oid = attributes.getValue("ItemOID");
					Optional<MetaItem> opt = currentMetaItemGroupData.getMetaItemList().stream().filter(mi -> mi.getOID().equals(oid)).findFirst();
					if(opt.isPresent()) {
						currentMetaItemData = opt.get();
					} else {
						unknownClinicalOID = oid; //currentMetaItemData is null
					}
					saveClinicalData(attributes.getValue("Value"));
				}				
				break;
			default:
				//nothing yet
		}
	}

	/**
	 * Method gets called every time a end element is being processed. Mostly
	 * resets variables or stores elements to the db.
	 * @param uri
	 * @param localName
	 * @param qName
	 * @throws org.xml.sax.SAXException
	 */
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch(localName) {
			// start MetaData 
			case "Study":
				currentStudy = null;
				progressBarIterator.setProgress(25);
				//stopWatchMeta.stop(); 
				//System.out.println(stopWatchMeta.prettyPrint());
			case "StudyEventDef":
				currentStudyEventOID = null;
				break;
			case "FormDef":
				currentFormOID = null;
				break;
			case "ItemGroupDef":
				currentItemGroupOID = null;
				break;
			case "ItemDef":
				currentItemOID = null;
				break;
			case "TranslatedText":
				currentTT.setText(elementValue);
				// checks if the current TranslatedText belongs to an Item or CodelistItem
				if (currentItemOID != null) {
					List<MetaItem> itemList = daoFactory.getMetaItemDao().getAllEntitiesByOID(currentItemOID);
					if(itemList != null) {
						for(MetaItem mi : itemList) {
							mi.addTranslatedText(currentTT);
							daoFactory.getMetaItemDao().saveOrUpdate(mi);
						}
					}
				} else if (currentCodeListItem != null) {
					currentCodeListItem.addTranslatedText(currentTT);
					//daoFactory.getCodeListItemDao().update(currentCodeListItem); IS DONE IN END->CodeListItem
				}
				//daoFactory.getTranslatedTextDao().save(currentTT); SAVED as cascade on MI or CLI
				break;
			case "CodeList":
				currentCodeListOID = null;
				break;
			case "CodeListItem":
			case "EnumeratedItem":
				//can be null, if the codelistref is missing...
				if(currentCodeListItem != null) {
					daoFactory.getCodeListItemDao().save(currentCodeListItem);
					currentCodeListItem = null;
				}
				break;

			//--------------------------------------------------------------------------------------
			//--------------------------------------------------------------------------------------
			// Start ClinicalData
			//--------------------------------------------------------------------------------------
			//--------------------------------------------------------------------------------------
			case "ClinicalData":
				break;
			case "SubjectData":
				daoFactory.getClinicalSubjectDataDao().save(currentClinicalSubjectData);
				currentClinicalSubjectData = null;
				break;
			case "StudyEventData":
				currentMetaStudyEventData = null;
				currentClinicalStudyEventData = null;
				break;
			case "FormData":
				currentMetaFormData = null;
				currentClinicalFormData = null;
				break;
			case "ItemGroupData":
				currentMetaItemGroupData = null;
				currentClinicalItemGroupData = null;
				break;
			case "ItemData":
				currentMetaItemData = null;
				break;
			default:
					//not needed yet
		}
	}

	/**
	 * method gets called every time a value is being processed (<el>value</el>)
	 */
	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		elementValue = new String(ch, start, length); // value from TranslatedTexts
	}
	
	
	//----------------------------------------------------------------------------------------
	//----------------------------------------------------------------------------------------
	//  Helper
	//----------------------------------------------------------------------------------------
	//----------------------------------------------------------------------------------------
	
	/**
	 * Helper function to get the mandatory tag and cast it into a boolean.
	 * 
	 * @param attributes passed through
	 * @return true, if Mandatory == "Yes"
	 */
	private boolean getMandatory(Attributes attributes) {
		String mandatoryString = attributes.getValue("Mandatory");
		boolean mandatory = false;
		if (mandatoryString != null && mandatoryString.equals("Yes")) { // || mandatoryString.equalsIgnoreCase("true") || mandatoryString.equalsIgnoreCase("1")) {
			mandatory = true;
		}
		return mandatory;
	}

	/**
	 * Helper function to get the order number tag and cast it into an Integer.
	 * 
	 * @param attributes passed through
	 * @return the order number or null
	 */
	private Integer getOrderNumber(Attributes attributes) {
		String orderNumberString = attributes.getValue("OrderNumber");
		Integer result;
		try {
			result = Integer.parseInt(orderNumberString);
		} catch(NumberFormatException e) {
			result = null;
		}
		return result;
	}

	/**
	 * Helper function to get the repeating tag and cast it into a boolean.
	 * 
	 * @param attributes passed through
	 * @return true, if Repeating == "Yes"
	 */
	private boolean getRepeating(Attributes attributes) {
		String repeatingString = attributes.getValue("Repeating");
		boolean repeating = false;
		if (repeatingString != null && repeatingString.equals("Yes")) { // || repeatingString.equalsIgnoreCase("true") || repeatingString.equalsIgnoreCase("1")) {
			repeating = true;
		}
		return repeating;
	}	

	/**
	 * Helper function to get the study event type and cast it into a StudyEventType.
	 * 
	 * @param attributes passed through
	 * @return true, if Repeating == "Yes"
	 */
	private StudyEventType getStudyEventType(Attributes attributes) {
		String setString = attributes.getValue("Type");
		StudyEventType set;
		if(setString == null) {
			set = StudyEventType.UNKNOWN;
		} else {
			switch(setString) {
				case "Scheduled":
				case "SCHEDULED":
					set = StudyEventType.SCHEDULED;
					break;
				case "Unscheduled":
				case "UNSCHEDULED":
					set = StudyEventType.UNSCHEDULED;
					break;
				case "Common":
				case "COMMON":
					set = StudyEventType.COMMON;
					break;
				default:
					set = StudyEventType.UNKNOWN;
					break;
			}
		}		
		
		return set;
	}			

	/**
	 * Used to store clinical data. also performce a validation test.
	 * 
	 * @param value Rest has been stored in attributes.
	 */
	private void saveClinicalData(String value) {
		//perform validation
		String invalid = validateValue(currentMetaStudyEventData, currentMetaFormData, currentMetaItemGroupData, currentMetaItemData, value);

		if(isNull(invalid)) {
			ClinicalItemData cid = new ClinicalItemData();
			cid.setValue(value);
			cid.setClinicalItemGroupData(currentClinicalItemGroupData);
			currentMetaItemData.addClinicalItemData(cid);

			ClinicalData cd = new ClinicalData();
			cd.setSubjectKey(currentClinicalSubjectData.getSubjectKey());
			cd.setMetaStudyEvent(currentMetaStudyEventData);
			cd.setStudyEventRepeatKey(currentClinicalStudyEventData.getRepeatKey());
			cd.setMetaForm(currentMetaFormData);
			cd.setFormRepeatKey(currentClinicalFormData.getRepeatKey());
			cd.setMetaItemGroup(currentMetaItemGroupData);
			cd.setItemGroupRepeatKey(currentClinicalItemGroupData.getRepeatKey());
			cd.setMetaItem(currentMetaItemData);
			cd.setValue(value);
			currentClinicalSubjectData.addClinicalData(cd);
		} else {
			InvalidClinicalData icd = new InvalidClinicalData();
			int unknownClincalOIDHasBeenUsed = 0;
			icd.setSubjectKey(currentClinicalSubjectData.getSubjectKey());
			icd.setMetaStudyEventOID(currentMetaStudyEventData == null ? (unknownClincalOIDHasBeenUsed++ == 0 ? unknownClinicalOID : null) : currentMetaStudyEventData.getOID());
			icd.setStudyEventRepeatKey(currentClinicalStudyEventData == null ? null : currentClinicalStudyEventData.getRepeatKey());
			icd.setMetaFormOID(currentMetaFormData == null ? (unknownClincalOIDHasBeenUsed++ == 0 ? unknownClinicalOID : null) : currentMetaFormData.getOID());
			icd.setFormRepeatKey((currentClinicalFormData == null ? null : currentClinicalFormData.getRepeatKey()));
			icd.setMetaItemGroupOID(currentMetaItemGroupData == null ? (unknownClincalOIDHasBeenUsed++ == 0 ? unknownClinicalOID : null) : currentMetaItemGroupData.getOID());
			icd.setItemGroupRepeatKey((currentClinicalItemGroupData == null ? null : currentClinicalItemGroupData.getRepeatKey()));
			icd.setMetaItemOID(currentMetaItemData == null ? (unknownClincalOIDHasBeenUsed++ == 0 ? unknownClinicalOID : null) : currentMetaItemData.getOID());
			icd.setValue(value);
			icd.setReason(invalid);
			currentClinicalSubjectData.addInvalidClinicalData(icd);
		}
	}

	@Transactional
	private String validateValue(MetaStudyEvent mse, MetaForm mf, MetaItemGroup mig, MetaItem mi, String value) {
		//check, if path is correct
		if(isNull(mse))
			return "StudyEventOID is not specified in the MetaData.";
		if(isNull(mf))
			return "FormOID is not specified in the MetaData.";
		if(isNull(mig))
			return "ItemGroupOID is not specified in the MetaData.";
		if(isNull(mi))
			return "ItemOID is not specified in the MetaData.";
		//empty values are allowed
		if(isNull(value) || "".equals(value)) 
			return null;

		//check aktuell value...
		String returnString = null;
		SimpleDateFormat databaseFormat = null;
		if(mi.getCodeList() == null) {
			switch(mi.getDataType()) {
				case Boolean:
					if (!value.equals("true") && !value.equals("false") && !value.equals("1") && !value.equals("0"))
						returnString = "Value is no supported boolean (true, false, 1, 0).";
						break;
				case Date:
					databaseFormat = new SimpleDateFormat("yyyy-MM-dd");
					try {
						Date tmp =databaseFormat.parse(value);
						if(tmp.before(new Date(0,0,1)) || tmp.after(new Date(8099,11,31)))
							returnString = "The year must be between 1900 and 9999.";
					} catch (ParseException e) {
						returnString = "Value can not be parsed into Date.";
					}
					break;
				case Time:
					databaseFormat = new SimpleDateFormat("HH:mm:ss");
					try {
						Date tmp =databaseFormat.parse(value);
					} catch (ParseException e) {
						returnString = "Value can not be parsed into Time.";
					}
					break;
				case Datetime:
					databaseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					try {
						Date tmp =databaseFormat.parse(value);
						if(tmp.before(new Date(0,0,1)) || tmp.after(new Date(8099,11,31)))
							returnString = "The year must be between 1900 and 9999.";
					} catch (ParseException e) {
						returnString = "Value can not be parsed into Datetime.";
					}
					break;
				case Integer:
					try {
						Integer.parseInt(value);
					} catch (NumberFormatException nfe) {
						returnString = "Value can not be parsed into an integer.";
					}
					break;
				case Float:
				case Double:
					try {
						Double.parseDouble(value.trim().replace(",", "."));
					} catch (NumberFormatException nfe) {
						returnString = "Value can not be parsed into a numeric value.";
					}
					break;
			}
		} else { //handle Codelist
			if(!mi.getCodeList().getCodeListItemList().stream().anyMatch(cli -> cli.getCodedValue().equals(value))) {
				returnString = "Value is not contained in the associated CodeList.";
			}
		}
		return returnString;
	}
}
