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
import org.waterforpeople.mapping.domain.Status.StatusCode;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.google.appengine.api.datastore.DatastoreTimeoutException;

public class SurveyInstanceDAO extends BaseDAO<SurveyInstance> {

	private static final Logger logger = Logger
			.getLogger(SurveyInstanceDAO.class.getName());

	public SurveyInstance save(Date collectionDate, DeviceFiles deviceFile,
			Long userID, List<String> unparsedLines) {
		SurveyInstance si = new SurveyInstance();
		boolean hasErrors = false;
		si.setDeviceFile(deviceFile);
		si.setUserID(userID);

		ArrayList<QuestionAnswerStore> qasList = new ArrayList<QuestionAnswerStore>();
		for (String line : unparsedLines) {

			String[] parts = line.split(",");
			// TODO: this will have to be removed when we use Strength and
			// ScoredValue Questions
			while (parts.length > 9) {
				try {
					@SuppressWarnings("unused")
					Date testDate = new Date(new Long(parts[7].trim()));
					break;
				} catch (Exception e) {
					logger.log(Level.INFO,
							"Removing comma because 7th pos doesn't pass got string: "
									+ parts[7] + "instead of date");
				}
				log.log(Level.INFO, "Has too many commas: " + line);
				int startIndex = 0;
				int iCount = 0;

				while ((startIndex = line.indexOf(",", startIndex + 1)) != -1) {
					if (iCount == 4) {
						String firstPart = line.substring(0, startIndex);
						String secondPart = line.substring(startIndex + 1, line
								.length());
						line = firstPart + secondPart;
						break;
					}
					iCount++;
				}
				parts = line.split(",");
			}
			QuestionAnswerStore qas = new QuestionAnswerStore();

			Date collDate = collectionDate;
			try {
				collDate = new Date(new Long(parts[7].trim()));
			} catch (Exception e) {
				logger.log(Level.WARNING,
						"Could not construct collection date", e);
				deviceFile
						.addProcessingMessage("Could not construct collection date from: "
								+ parts[7]);
				hasErrors = true;
			}

			if (si.getSurveyId() == null) {
				try {
					si.setCollectionDate(collDate);
					si.setSurveyId(Long.parseLong(parts[0].trim()));
					si = save(si);
				} catch (NumberFormatException e) {
					logger.log(Level.SEVERE, "Could not parse survey id: "
							+ parts[0], e);
					deviceFile
							.addProcessingMessage("Could not parse survey id: "
									+ parts[0] + e.getMessage());
					hasErrors = true;
				} catch (DatastoreTimeoutException te) {
					sleep();
					si = save(si);

				}
			}
			qas.setSurveyId(si.getSurveyId());
			qas.setSurveyInstanceId(si.getKey().getId());
			qas.setArbitratyNumber(new Long(parts[1].trim()));
			qas.setQuestionID(parts[2].trim());
			qas.setType(parts[3].trim());
			qas.setCollectionDate(collDate);

			if (parts.length > 4) {
				qas.setValue(parts[4].trim());
			}
			if (parts.length >= 5) {
				if (si.getSubmitterName() == null) {
					si.setSubmitterName(parts[5].trim());
				}
			}
			if (parts.length >= 9) {
				if (si.getDeviceIdentifier() == null) {
					si.setDeviceIdentifier(parts[8].trim());
				}
			}
			if (parts.length >= 10) {
				qas.setScoredValue(parts[9].trim());
			}
			if (parts.length >= 11) {
				qas.setStrength(parts[10].trim());
			}
			qasList.add(qas);
		}
		try {
			save(qasList);
		} catch (DatastoreTimeoutException te) {
			sleep();
			save(qasList);
		}
		deviceFile.setSurveyInstanceId(si.getKey().getId());
		if (!hasErrors) {
			si.getDeviceFile().setProcessedStatus(
					StatusCode.PROCESSED_NO_ERRORS);
		} else {
			si.getDeviceFile().setProcessedStatus(
					StatusCode.PROCESSED_WITH_ERRORS);
		}
		si.setQuestionAnswersStore(qasList);
		return si;
	}

	public SurveyInstanceDAO() {
		super(SurveyInstance.class);
		// loadReplaceList();
	}

	@SuppressWarnings("unchecked")
	public List<SurveyInstance> listByDateRange(Date beginDate, Date endDate,
			String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query q = pm.newQuery(SurveyInstance.class);
		q.setFilter("collectionDate >= pBeginDate");
		q.declareParameters("java.util.Date pBeginDate");
		q.setOrdering("collectionDate desc");

		prepareCursor(cursorString, q);

		return (List<SurveyInstance>) q.execute(beginDate);
	}
	
	@SuppressWarnings("unchecked")
	public List<SurveyInstance> listByDateRange(Date beginDate, Date endDate,boolean unapprovedOnlyFlag,
			String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query q = pm.newQuery(SurveyInstance.class);
		StringBuilder filterBuffer = new StringBuilder("collectionDate >= pBeginDate");
		StringBuilder paramBuffer = new StringBuilder("java.util.Date pBeginDate");
		
		if(unapprovedOnlyFlag){
			filterBuffer.append(" && approvedFlag == pApprovedFlag");
			paramBuffer.append(", String pApprovedFlag");
		}	
		q.setFilter(filterBuffer.toString());
		q.declareParameters(paramBuffer.toString());
		q.setOrdering("collectionDate desc");

		prepareCursor(cursorString, q);

		if(unapprovedOnlyFlag){
			return (List<SurveyInstance>) q.execute(beginDate,"False");
		}else{
			return (List<SurveyInstance>) q.execute(beginDate);
		}
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
		q
				.declareParameters("Long surveyInstanceIdParam, String questionIdParam");
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
	public List<QuestionAnswerStore> listQuestionAnswerStore(Long instanceId,
			Integer count) {
		PersistenceManager pm = PersistenceFilter.getManager();
		Query q = pm.newQuery(QuestionAnswerStore.class);
		q.setFilter("surveyInstanceId == surveyInstanceIdParam");
		q.declareParameters("Long surveyInstanceIdParam");
		if (count != null) {
			q.setRange(0, count);
		}
		return (List<QuestionAnswerStore>) q.execute(instanceId);
	}

	@SuppressWarnings("unchecked")
	public List<QuestionAnswerStore> listQuestionAnswerStoreGeoQuestions(
			Long instanceId) {
		PersistenceManager pm = PersistenceFilter.getManager();
		Query q = pm.newQuery(QuestionAnswerStore.class);
		q.setFilter("surveyInstanceId == surveyInstanceIdParam");
		q.declareParameters("Long surveyInstanceIdParam");
		q.setFilter("type=typeParam");
		q.setFilter("String typeParam");

		return (List<QuestionAnswerStore>) q.execute(instanceId, "GEO");
	}

	/**
	 * lists all questionAnswerStore objects for a specific question
	 * 
	 * @param questionId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<QuestionAnswerStore> listQuestionAnswerStoreForQuestion(
			String questionId, String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query q = pm.newQuery(QuestionAnswerStore.class);
		q.setFilter("questionID == qidParam");
		q.declareParameters("String qidParam");
		prepareCursor(cursorString, q);
		return (List<QuestionAnswerStore>) q.execute(questionId);
	}

	/**
	 * lists all surveyInstance records for a given survey
	 * 
	 * @param surveyId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SurveyInstance> listSurveyInstanceBySurvey(Long surveyId,
			Integer count) {
		PersistenceManager pm = PersistenceFilter.getManager();
		Query q = pm.newQuery(SurveyInstance.class);
		q.setFilter("surveyId == surveyIdParam");
		q.declareParameters("Long surveyIdParam");
		if (count != null) {
			q.setRange(0, count);
		}
		return (List<SurveyInstance>) q.execute(surveyId);
	}

	@SuppressWarnings("unchecked")
	public List<SurveyInstance> listSurveyInstanceBySurveyId(Long surveyId,
			String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		Query q = pm.newQuery(SurveyInstance.class);
		q.setFilter("surveyId == surveyIdParam");
		q.declareParameters("Long surveyIdParam");
		prepareCursor(cursorString, q);
		List<SurveyInstance> siList = (List<SurveyInstance>) q
				.execute(surveyId);

		return siList;
	}

}
