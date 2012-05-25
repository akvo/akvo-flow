package org.waterforpeople.mapping.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.Status.StatusCode;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class SurveyInstanceDAO extends BaseDAO<SurveyInstance> {

	private static final Logger logger = Logger
			.getLogger(SurveyInstanceDAO.class.getName());

	public SurveyInstance save(Date collectionDate, DeviceFiles deviceFile,
			Long userID, List<String> unparsedLines) {
		SurveyInstance si = new SurveyInstance();
		boolean hasErrors = false;
		si.setDeviceFile(deviceFile);
		si.setUserID(userID);
		String delimiter = "\t";

		ArrayList<QuestionAnswerStore> qasList = new ArrayList<QuestionAnswerStore>();
		for (String line : unparsedLines) {

			String[] parts = line.split(delimiter);
			if (parts.length < 5) {
				delimiter = ",";
				parts = line.split(delimiter);
			}
			// TODO: this will have to be removed when we use Strength and
			// ScoredValue Questions
			while (",".equals(delimiter) && parts.length > 9) {
				try {
					new Date(new Long(parts[7].trim()));
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
						String secondPart = line.substring(startIndex + 1,
								line.length());
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
					if (collDate != null) {
						si.setCollectionDate(collDate);
					}
					si.setSurveyId(Long.parseLong(parts[0].trim()));
					if (parts.length >= 12) {
						String uuid = parts[parts.length - 1];
						if (uuid != null && uuid.trim().length() > 0) {
							SurveyInstance existingSi = findByUUID(uuid);
							if (existingSi != null) {
								return null;
							} else {
								si.setUuid(uuid);
							}
						}
					}
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
			if (parts.length > 5) {
				if (si.getSubmitterName() == null
						|| si.getSubmitterName().trim().length() == 0) {
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
	}

	@SuppressWarnings("unchecked")
	public List<SurveyInstance> listByDateRange(Date beginDate,
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
	public List<SurveyInstance> listByDateRange(Date beginDate, Date endDate,
			boolean unapprovedOnlyFlag, Long surveyId, String source,
			String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(SurveyInstance.class);

		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("surveyId", filterString, paramString, "Long",
				surveyId, paramMap);
		appendNonNullParam("deviceIdentifier", filterString, paramString,
				"String", source, paramMap);
		appendNonNullParam("collectionDate", filterString, paramString, "Date",
				beginDate, paramMap, GTE_OP);
		appendNonNullParam("collectionDate", filterString, paramString, "Date",
				endDate, paramMap, LTE_OP);
		if (unapprovedOnlyFlag) {
			appendNonNullParam("approvedFlag", filterString, paramString,
					"String", "False", paramMap);
		}
		if (beginDate != null || endDate != null) {
			query.declareImports("import java.util.Date");
		}

		query.setOrdering("collectionDate desc");

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		prepareCursor(cursorString, query);

		return (List<SurveyInstance>) query.executeWithMap(paramMap);

	}

	public Iterable<Entity> listRawEntity(Boolean returnKeysOnly,
			Date beginDate, Date endDate, Long surveyId) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		// The Query interface assembles a query
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query(
				"SurveyInstance");
		if (returnKeysOnly) {
			q.setKeysOnly();
		}

		if (surveyId != null)
			q.addFilter("surveyId", FilterOperator.EQUAL, surveyId);
		if (beginDate != null)
			q.addFilter("collectionDate", FilterOperator.GREATER_THAN_OR_EQUAL,
					beginDate);
		if (endDate != null)
			q.addFilter("collectionDate", FilterOperator.LESS_THAN_OR_EQUAL,
					endDate);
		PreparedQuery pq = datastore.prepare(q);
		return pq.asIterable();

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
		q.setFilter("surveyInstanceId == surveyInstanceIdParam && questionID == questionIdParam");
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
	 * lists all questionAnswerStore objects for a single surveyInstance,
	 * optionally filtered by type
	 * 
	 * @param surveyInstanceId
	 *            - mandatory
	 * @param type
	 *            - optional
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<QuestionAnswerStore> listQuestionAnswerStoreByType(
			Long surveyInstanceId, String type) {
		if (surveyInstanceId != null) {
			PersistenceManager pm = PersistenceFilter.getManager();
			javax.jdo.Query query = pm.newQuery(QuestionAnswerStore.class);

			Map<String, Object> paramMap = null;

			StringBuilder filterString = new StringBuilder();
			StringBuilder paramString = new StringBuilder();
			paramMap = new HashMap<String, Object>();

			appendNonNullParam("surveyInstanceId", filterString, paramString,
					"Long", surveyInstanceId, paramMap);
			appendNonNullParam("type", filterString, paramString, "String",
					type, paramMap);

			query.setFilter(filterString.toString());
			query.declareParameters(paramString.toString());

			return (List<QuestionAnswerStore>) query.executeWithMap(paramMap);
		} else {
			throw new IllegalArgumentException(
					"surveyInstanceId may not be null");
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

	public List<SurveyInstance> listSurveyInstanceBySurvey(Long surveyId,
			Integer count) {
		return listSurveyInstanceBySurvey(surveyId, count, null);
	}

	@SuppressWarnings("unchecked")
	public List<SurveyInstance> listSurveyInstanceBySurvey(Long surveyId,
			Integer count, String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		Query q = pm.newQuery(SurveyInstance.class);
		q.setFilter("surveyId == surveyIdParam");
		q.declareParameters("Long surveyIdParam");
		prepareCursor(cursorString, count, q);
		List<SurveyInstance> siList = (List<SurveyInstance>) q
				.execute(surveyId);

		return siList;
	}

	public List<SurveyInstance> listSurveyInstanceBySurveyId(Long surveyId,
			String cursorString) {
		return listSurveyInstanceBySurvey(surveyId, null, cursorString);
	}

	public Iterable<Entity> listSurveyInstanceKeysBySurveyId(Long surveyId) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query(
				"SurveyInstance");
		q.setKeysOnly();
		q.addFilter("surveyId", FilterOperator.EQUAL, surveyId);
		PreparedQuery pq = datastore.prepare(q);
		return pq.asIterable();
	}

	/**
	 * lists instances for the given surveyedLocale optionally filtered by the
	 * dates passed in
	 * 
	 * @param surveyedLocaleId
	 * @return
	 */
	public List<SurveyInstance> listInstancesByLocale(Long surveyedLocaleId,
			Date dateFrom, Date dateTo, String cursor) {
		return listInstancesByLocale(surveyedLocaleId, dateFrom, dateTo,
				DEFAULT_RESULT_COUNT, cursor);
	}

	/**
	 * lists instances for the given surveyedLocale optionally filtered by the
	 * dates passed in
	 * 
	 * @param surveyedLocaleId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SurveyInstance> listInstancesByLocale(Long surveyedLocaleId,
			Date dateFrom, Date dateTo, Integer pageSize, String cursor) {

		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(SurveyInstance.class);

		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("surveyedLocaleId", filterString, paramString,
				"Long", surveyedLocaleId, paramMap);

		if (dateFrom != null || dateTo != null) {
			appendNonNullParam("collectionDate", filterString, paramString,
					"Date", dateFrom, paramMap, GTE_OP);
			appendNonNullParam("collectionDate", filterString, paramString,
					"Date", dateTo, paramMap, LTE_OP);
			query.declareImports("import java.util.Date");
		}

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());
		query.setOrdering("collectionDate desc");
		prepareCursor(cursor, pageSize, query);
		return (List<SurveyInstance>) query.executeWithMap(paramMap);

	}

	/**
	 * lists all survey instances by the submitter passed in
	 * 
	 * @param submitter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SurveyInstance> listInstanceBySubmitter(String submitter) {
		if (submitter != null) {
			return listByProperty("submitterName", submitter, "String");
		} else {
			PersistenceManager pm = PersistenceFilter.getManager();
			javax.jdo.Query query = pm.newQuery(SurveyInstance.class,
					"submitterName == null");
			return (List<SurveyInstance>) query.execute();
		}
	}

	/**
	 * finds a single survey instance by uuid. This method will NOT load all
	 * QuestionAnswerStore objects.
	 * 
	 * @param uuid
	 * @return
	 */
	public SurveyInstance findByUUID(String uuid) {
		return findByProperty("uuid", uuid, "String");
	}

}
