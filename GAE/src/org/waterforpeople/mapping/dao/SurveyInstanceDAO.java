package org.waterforpeople.mapping.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.dao.BaseDAO;

public class SurveyInstanceDAO extends BaseDAO<SurveyInstance> {

	private static final Logger logger = Logger
			.getLogger(SurveyInstanceDAO.class.getName());

	public SurveyInstance save(Date collectionDate, DeviceFiles deviceFile,
			Long userID, ArrayList<String> unparsedLines) {
		SurveyInstance si = new SurveyInstance();
		si.setCollectionDate(collectionDate);
		si.setDeviceFile(deviceFile);
		si.setUserID(userID);
		ArrayList<QuestionAnswerStore> qasList = new ArrayList<QuestionAnswerStore>();
		for (String line : unparsedLines) {
			String[] parts = line.split(",");
			QuestionAnswerStore qas = new QuestionAnswerStore();
			if (si.getSurveyId() == null) {
				try {
					si.setSurveyId(Long.parseLong(parts[0]));
				} catch (NumberFormatException e) {
					logger.log(Level.SEVERE,"Could not parse survey id: " + parts[0]);
				}
			}
			qas.setArbitratyNumber(new Long(parts[1]));
			qas.setQuestionID(parts[2]);
			qas.setType(parts[3]);
			if (parts.length > 4) {
				qas.setValue(parts[4]);
			}
			// Need to implement handling of date from text file here.
			qasList.add(qas);
		}
		si.setQuestionAnswersStore(qasList);
		return save(si);
	}

	public SurveyInstanceDAO() {
		super(SurveyInstance.class);
	}

}
