package org.waterforpeople.mapping.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class SurveyInstanceDAO {
	PersistenceManager pm;
	SurveyInstance si;

	public Long save(Date collectionDate, DeviceFiles deviceFile,
			Long userID, ArrayList<String> unparsedLines) {
		Boolean savedSuccessFlag = false;
		si = new SurveyInstance();
		si.setCollectionDate(collectionDate);
		si.setDeviceFile(deviceFile);
		si.setUserID(userID);
		ArrayList<QuestionAnswerStore> qasList = new ArrayList<QuestionAnswerStore>();
		for (String line : unparsedLines) {
			String[] parts = line.split(",");
			QuestionAnswerStore qas = new QuestionAnswerStore();
			qas.setArbitratyNumber(new Long(parts[0]));
			qas.setQuestionID(parts[1]);
			qas.setType(parts[2]);
			if (parts.length > 3) {
				qas.setValue(parts[3]);
			}
			//Need to implement handling of date from text file here.
			qasList.add(qas);
		}
		si.setQuestionAnswersStore(qasList);
		pm.makePersistent(si);
		return si.getId();
	}

	public SurveyInstanceDAO() {
		init();
	}
	

	public SurveyContainer getSurveyDocument(Long id) {
		SurveyContainer si = null;
		Key itemKey = KeyFactory.createKey(SurveyContainer.class.getSimpleName(), id);
		
		si = (SurveyContainer)pm.getObjectById(SurveyContainer.class,itemKey);
		 
		 
		
		 
		 return si;
		 
		
		
//		javax.jdo.Query query = pm.newQuery(SurveyContainer.class);
//		query.setFilter("key.id == idParam");
//		query.declareParameters("Key idParam");
//		List<SurveyContainer> results = (List<SurveyContainer>) query.execute(id);
//		if (results.size() > 0) {
//			si = results.get(0);
//		}
//		return si;
	}


	public SurveyInstance get(Long id) {
		SurveyInstance si = null;

		javax.jdo.Query query = pm.newQuery(SurveyInstance.class);
		query.setFilter("id == idParam");
		query.declareParameters("Long idParam");
		List<SurveyInstance> results = (List<SurveyInstance>) query.execute(id);
		if (results.size() > 0) {
			si = results.get(0);
		}
		return si;
	}

	private void init() {
		pm = PMF.get().getPersistenceManager();
	}

}
