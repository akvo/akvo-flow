package org.waterforpeople.mapping.dao;

import java.util.ArrayList;
import java.util.Date;

import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.SurveyContainer;

public class SurveyInstanceDAO extends BaseDAO<SurveyInstance> {

	public Long save(Date collectionDate, DeviceFiles deviceFile,
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
		return save(si).getKey().getId();
	}

	public SurveyInstanceDAO() {
		super(SurveyInstance.class);
	}

	public SurveyContainer getSurveyDocument(Long id) {
		SurveyContainer si = null;		
		si = (SurveyContainer) getByKey(id, SurveyContainer.class);
		return si;
	}
}
