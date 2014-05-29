/*
 *  Copyright (C) 2010-2013 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

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

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.app.web.DataProcessorRestServlet;
import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;
import org.waterforpeople.mapping.app.web.dto.ImageCheckRequest;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.Status.StatusCode;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.gis.map.MapUtils;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class SurveyInstanceDAO extends BaseDAO<SurveyInstance> {

	private static final Logger logger = Logger
			.getLogger(SurveyInstanceDAO.class.getName());
	// the set of unparsedLines we have here represent values from one surveyInstance
	// as they are split up in the TaskServlet task.
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SurveyInstance save(Date collectionDate, DeviceFiles deviceFile,
			Long userID, List<String> unparsedLines) {

		SurveyInstance si = new SurveyInstance();
		boolean hasErrors = false;
		si.setDeviceFile(deviceFile);
		si.setUserID(userID);
		String delimiter = "\t";
		Boolean surveyInstanceIsNew = true;
		Long geoQasId = null;
		DeviceDAO deviceDao = new DeviceDAO();
		final QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();

		ArrayList<QuestionAnswerStore> qasList = new ArrayList<QuestionAnswerStore>();

		Cache cache = null;
		Map props = new HashMap();
		props.put(GCacheFactory.EXPIRATION_DELTA, 15 * 60); // 15min
		props.put(MemcacheService.SetPolicy.SET_ALWAYS, true);
		try {
			CacheFactory cacheFactory = CacheManager.getInstance()
					.getCacheFactory();
			cache = cacheFactory.createCache(props);
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Couldn't initialize cache: " + e.getMessage(), e);
		}

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

			// Time tracking new column - 13
			if (parts.length >= 13) {
				try {
					final Long time = Long.valueOf(parts[12].trim());
					si.setSurveyalTime(time);
				} catch (NumberFormatException e) {
					logger.log(Level.WARNING, "Surveyal time column is not a number", e);
				}
			}

			// if this is the first time round, save the surveyInstance or use an existing one
			if (si.getSurveyId() == null) {
				try {
					if (collDate != null) {
						si.setCollectionDate(collDate);
					}
					si.setSurveyId(Long.parseLong(parts[0].trim()));
					if (parts.length >= 12) {
						String uuid = parts[11];
						if (uuid != null && uuid.trim().length() > 0) {
							SurveyInstance existingSi = findByUUID(uuid);
							if (existingSi != null) {
								// SurveyInstance found, reuse it to process missing data
								si = existingSi;
								si.setDeviceFile(deviceFile);
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

			if (qasDao.listBySurveyInstance(si.getKey().getId(),
					si.getSurveyId(), parts[2].trim()).size() != 0) {
				log.log(Level.INFO, "Skipping QAS already present in datasore [SurveyInstance, Survey, Question]: " + si.getKey().getId() + ", " + si.getSurveyId() + ", " + parts[2].trim());
				continue; // skip processing
			}

			if (cache != null) {
				Map<Long, Long> ck = new HashMap<Long, Long>();
				// {surveyInstanceId, questionID}
				ck.put(si.getKey().getId(), Long.valueOf(parts[2].trim()));

				if (cache.containsKey(ck)) {
					log.log(Level.INFO,
							"Skipping QAS already present in temporary cache [SurveyInstance, Survey, Question]: "
									+ si.getKey().getId() + ", "
									+ si.getSurveyId() + ", " + parts[2].trim());
					continue; // skip processing
				} else {
					cache.put(ck, true);
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

		QuestionDao questionDao = new QuestionDao();
		List<Question> qOptionList = questionDao.listQuestionByType(si.getSurveyId(), Question.Type.OPTION);

		for (QuestionAnswerStore qas : qasList){
			if (Question.Type.GEO.toString().equals(qas.getType())){
				geoQasId = qas.getKey().getId();
			}
			// update count of questionAnswerSummary objects
			if (isSummarizable(qas, qOptionList)) {
				SurveyQuestionSummaryDao.incrementCount(qas,1);
			}

			if ("IMAGE".equals(qas.getType())) {
				// the device send values as IMAGE and not PHOTO
				String filename = qas.getValue().substring(
						qas.getValue().lastIndexOf("/") + 1);
				Device d = null;

				if (deviceFile.getImei() != null) {
					d = deviceDao.getByImei(deviceFile.getImei());
				}

				if (d == null && deviceFile.getPhoneNumber() != null) {
					d = deviceDao.get(deviceFile.getPhoneNumber());
				}

				String deviceId = d == null ? "null" : String.valueOf(d
						.getKey().getId());

				Queue queue = QueueFactory.getQueue("background-processing");
				TaskOptions to = TaskOptions.Builder
						.withUrl("/app_worker/imagecheck")
						.param(ImageCheckRequest.FILENAME_PARAM, filename)
						.param(ImageCheckRequest.DEVICE_ID_PARAM, deviceId)
						.param(ImageCheckRequest.QAS_ID_PARAM, String.valueOf(qas.getKey().getId()))
						.param(ImageCheckRequest.ATTEMPT_PARAM, "1");
				queue.add(to);
			}
		}

		// invoke a task to update corresponding surveyInstanceSummary objects
		if (surveyInstanceIsNew && geoQasId != null){
			Queue summQueue = QueueFactory.getQueue("dataSummarization");
			summQueue.add(TaskOptions.Builder.withUrl("/app_worker/dataprocessor").param(
					DataProcessorRequest.ACTION_PARAM, DataProcessorRequest.SURVEY_INSTANCE_SUMMARIZER)
					.param("surveyInstanceId", si.getKey().getId() + "")
					.param("qasId", geoQasId + "")
					.param("delta",1 + ""));
		}
		return si;
	}

	/**
	 * returns true if the question type for the answer object is an OPTION type
	 * @param answer
	 * @param questions
	 * @return
	 */
	private boolean isSummarizable(QuestionAnswerStore answer,
			List<Question> questions) {
		if (questions != null && answer != null) {
			long id = Long.parseLong(answer.getQuestionID());
			for (Question q : questions) {
				if (q.getKey().getId() == id) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
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

	// same as listByDateRange, but adds sumbitterName, country, and sublevels as search fields
	// @Author: M.T.Westra
	@SuppressWarnings("unchecked")
	public List<SurveyInstance> listByDateRangeAndSubmitter(Date beginDate, Date endDate,
			boolean unapprovedOnlyFlag, Long surveyId, String deviceIdentifier, String submitterName,
			String countryCode, String level1, String level2, String cursorString) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(SurveyInstance.class);

		Map<String, Object> paramMap = null;

		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("surveyId", filterString, paramString, "Long",
				surveyId, paramMap);
		appendNonNullParam("deviceIdentifier", filterString, paramString,
				"String", deviceIdentifier, paramMap);
		appendNonNullParam("submitterName", filterString, paramString,
				"String", submitterName, paramMap);
		appendNonNullParam("countryCode", filterString, paramString,
				"String", countryCode, paramMap);
		appendNonNullParam("sublevel1", filterString, paramString,
				"String", level1, paramMap);
		appendNonNullParam("sublevel2", filterString, paramString,
				"String", level2, paramMap);
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




	/***********************
	 * returns raw entities
	 *
	 * @param returnKeysOnly
	 * @param beginDate
	 * @param endDate
	 * @param surveyId
	 * @return
	 */
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
	 * Deletes a surveyInstance and all its related questionAnswerStore objects
	 * Based on the version in DataBackoutServlet
	 *
	 * @param item
	 * @return
	 */
	// TODO update lastSurveyalInstanceId in surveydLocale objects
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void deleteSurveyInstance(SurveyInstance item) {
		SurveyInstanceDAO siDao = new SurveyInstanceDAO();
		QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
		SurveyedLocaleDao localeDao = new SurveyedLocaleDao();
		QuestionDao qDao = new QuestionDao();
		BaseDAO<SurveyalValue> svDao = new BaseDAO<SurveyalValue>(SurveyalValue.class);
		Long surveyInstanceId = item.getKey().getId();

		List<QuestionAnswerStore> qasList = siDao.listQuestionAnswerStore(
				surveyInstanceId, null);

		// to account for the slim change if we have two geo questions in one surveyInstance

		boolean sisCountUpdated = false;

		if (qasList != null && qasList.size() > 0) {
			// update the questionAnswerSummary counts
			for (QuestionAnswerStore qasItem : qasList) {

				// if the questionAnswerStore item is the GEO type, try to update
				// the surveyInstanceSummary

				if (Question.Type.GEO.toString().equals(qasItem.getType()) && !sisCountUpdated){
					DataProcessorRestServlet.surveyInstanceSummarizer(surveyInstanceId, qasItem.getKey().getId(), -1);
					sisCountUpdated = true;
				}

				// if the questionAnswerStore item belongs to an OPTION type,
				// update the count
				Question q = qDao.getByKey(Long.parseLong(qasItem.getQuestionID()));
				if (q != null && Question.Type.OPTION.equals(q.getType())) {
					SurveyQuestionSummaryDao.incrementCount(qasItem, -1);
				}
			}

			// delete the questionAnswerStore objects in a single datastore
			// operation
			qasDao.delete(qasList);
		}

		// get the instances that have contributed to the Locale, for later use
		List<SurveyInstance> instancesForLocale = siDao.listByProperty("surveyedLocaleId",item.getSurveyedLocaleId(),"Long");

		// delete the surveyInstance
		SurveyInstance instance = siDao.getByKey(item.getKey());
		if (instance != null) {
			// send notification
			List<Long> ids = new ArrayList<Long>();
			ids.add(instance.getSurveyId());
			SurveyUtils.notifyReportService(ids, "invalidate");

			Long localeId = instance.getSurveyedLocaleId();

			List<SurveyalValue> valsForInstance = localeDao
				.listSurveyalValuesByInstance(instance.getKey().getId());
			if (valsForInstance != null && valsForInstance.size() > 0) {
				svDao.delete(valsForInstance);
			}

			// if there is only one surveyInstance that has contributed to this Locale,
			// we can delete the SurveyedLocale. The values should already have been deleted
			// in the previous step.
			if (instancesForLocale != null && instancesForLocale.size() == 1) {
				SurveyedLocale l = localeDao.getByKey(localeId);
				if (l != null) {
					// initialize the memcache
					Cache cache = null;
					Map props = new HashMap();
					props.put(GCacheFactory.EXPIRATION_DELTA, 12 * 60 * 60);
					props.put(MemcacheService.SetPolicy.SET_ALWAYS, true);
					try {
						CacheFactory cacheFactory = CacheManager.getInstance()
								.getCacheFactory();
						cache = cacheFactory.createCache(props);
					} catch (Exception e) {
						log.log(Level.SEVERE,
								"Couldn't initialize cache: " + e.getMessage(), e);
					}
					MapUtils.recomputeCluster(cache, l, -1);
					localeDao.delete(l);
				}
			}
			// now delete the surveyInstance
			siDao.delete(instance);
		}
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
		q.setOrdering("createdDateTime asc");
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


	/** lists questionAnswerStore objects of particular types passed in
	 */
	public List<QuestionAnswerStore> listQAOptions(String cursorString, Integer pageSize, String... options){
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query q = pm.newQuery(QuestionAnswerStore.class);
		StringBuffer filter = new StringBuffer();
		for (String op : options) {
			filter.append("type == '").append(op).append("' ||");
		}
		q.setFilter(filter.substring(0,filter.length()-3).toString());
		prepareCursor(cursorString, pageSize, q);
		return (List<QuestionAnswerStore>) q.execute();
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
