package org.waterforpeople.mapping.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

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
					si = save(si);
				} catch (NumberFormatException e) {
					logger.log(Level.SEVERE, "Could not parse survey id: "
							+ parts[0]);
				}
			}
			qas.setSurveyId(si.getSurveyId());
			qas.setSurveyInstanceId(si.getKey().getId());
			qas.setArbitratyNumber(new Long(parts[1]));
			qas.setQuestionID(parts[2]);
			qas.setType(parts[3]);
			if (parts.length > 4) {
				qas.setValue(parts[4]);
			}
			qas = save(qas);
			qasList.add(qas);
		}
		si.setQuestionAnswersStore(qasList);
		return si;
	}

	public SurveyInstanceDAO() {
		super(SurveyInstance.class);
	}

	@SuppressWarnings("unchecked")
	public List<SurveyInstance> listByDateRange(Date beginDate, Date endDate) {
		PersistenceManager pm = PersistenceFilter.getManager();
		List<SurveyInstance> results = null;
		javax.jdo.Query q = pm.newQuery(SurveyInstance.class);
		q.setFilter("collectionDate >= pBeginDate");
		q.declareParameters("java.util.Date pBeginDate");
		results = (List<SurveyInstance>) q.execute(beginDate);

		return results;
	}

	/**
	 * finds a questionAnswerStore object for the surveyInstance and questionId
	 * passed in (if it exists)
	 * 
	 * @param surveyInstanceId
	 * @param questionId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public QuestionAnswerStore findQuestionAnswerStoreForQuestion(
			Long surveyInstanceId, String questionId) {
		PersistenceManager pm = PersistenceFilter.getManager();
		Query q = pm.newQuery(QuestionAnswerStore.class);
		q
				.setFilter("surveyInstanceId == surveyInstanceIdParam && questionID == questionIdParam");
		q.declareParameters("Long surveyInstanceIdParam, String questionIdParam");
		List<QuestionAnswerStore> result = (List<QuestionAnswerStore>) q
				.execute(surveyInstanceId, questionId);
		if (result != null && result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}

	/**
	 * lists all questionAnswerStore objects for a survey instance
	 * 
	 * @param instanceId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<QuestionAnswerStore> listQuestionAnswerStore(Long instanceId) {
		PersistenceManager pm = PersistenceFilter.getManager();
		Query q = pm.newQuery(QuestionAnswerStore.class);
		q.setFilter("surveyInstanceId == surveyInstanceIdParam");
		q.declareParameters("Long surveyInstanceIdParam");
		return (List<QuestionAnswerStore>) q.execute(instanceId);
	}

}
