package org.waterforpeople.mapping.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class SurveyInstanceDAO extends BaseDAO<SurveyInstance> {

	public String save(Date collectionDate, DeviceFiles deviceFile,
			Long userID, ArrayList<String> unparsedLines) {
		SurveyInstance si = new SurveyInstance();
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
			// Need to implement handling of date from text file here.
			qasList.add(qas);
		}
		si.setQuestionAnswersStore(qasList);
		return save(si).getKey().toString();
	}

	public SurveyInstanceDAO() {
		super(SurveyInstance.class);
	}

	public SurveyContainer getSurveyDocument(Long id) {
		SurveyContainer si = null;
		Key itemKey = KeyFactory.createKey(SurveyContainer.class
				.getSimpleName(), id);

		si = (SurveyContainer) pm.getObjectById(SurveyContainer.class, itemKey);
		return si;
	}
}
