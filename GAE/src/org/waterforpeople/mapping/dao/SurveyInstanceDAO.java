/*
 *  Copyright (C) 2010-2015, 2017-2021 Stichting Akvo (Akvo Foundation)
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

import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import org.akvo.flow.domain.DataUtils;
import org.apache.commons.lang.StringUtils;
import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.web.DataProcessorRestServlet;
import org.waterforpeople.mapping.app.web.dto.ImageCheckRequest;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import javax.annotation.Nonnull;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class SurveyInstanceDAO extends BaseDAO<SurveyInstance> {
    private static final String DEFAULT_ORG_PROP = "defaultOrg";
    private final QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
    private final DeviceDAO deviceDao = new DeviceDAO();
    private final QuestionDao questionDao = new QuestionDao();

    public SurveyInstanceDAO() {
        super(SurveyInstance.class);
    }

    public SurveyInstance save(SurveyInstance si, DeviceFiles deviceFile) {
        // Check whether the instance is already stored in the database.
        boolean isNew = true;
        SurveyInstance existing = findByUUID(si.getUuid());
        if (existing != null) {
            si.setKey(existing.getKey());
            si.setCreatedDateTime(existing.getCreatedDateTime());
            isNew = false;
        }

        SurveyedLocale sl = saveSurveyedLocale(si);
        si.setSurveyedLocaleId(sl.getKey().getId());
        if (si.getSurveyedLocaleDisplayName() == null && sl != null) {
            //in case app did not send SL name, we may get it from an old SL
            si.setSurveyedLocaleDisplayName(sl.getDisplayName());
        }
        si.setDeviceFile(deviceFile);
        si = save(si);// Save the SurveyInstance just once, ensuring the Key is set.

        final long surveyInstanceId = si.getKey().getId();
        qasDao.listBySurveyInstance(surveyInstanceId);// Cache existing qas????

        final Set<QuestionAnswerStore> images = new HashSet<>();
        final Set<QuestionAnswerStore> locations = new HashSet<>();
        final List<QuestionAnswerStore> responses = new ArrayList<>();
        for (QuestionAnswerStore qas : si.getQuestionAnswersStore()) {
            if (isProcessable(qas, si)) {
                qas.setSurveyInstanceId(surveyInstanceId);

                if (Question.Type.GEO.toString().equals(qas.getType()) && isNew) {
                    locations.add(qas);
                } else if ("IMAGE".equals(qas.getType())) {
                    // The device send values as IMAGE and not PHOTO.
                    images.add(qas);
                }
                responses.add(qas);
            }
        }

        // batch save all responses
        try {
            qasDao.save(responses);
        } catch (DatastoreTimeoutException te) {
            sleep();
            qasDao.save(responses);
        }

        // Now that QAS IDs are set, enqueue imagecheck tasks,
        // whereby the presence of an image in S3 will be checked.
        if (!images.isEmpty()) {
            Device d = deviceDao.getDevice(deviceFile.getAndroidId(),
                    deviceFile.getImei(), deviceFile.getPhoneNumber());
            String deviceId = d == null ? "null" : String.valueOf(d.getKey().getId());

            for (QuestionAnswerStore qas : images) {
                String value = qas.getValue();
                String filename = null;
                if (value.startsWith("{")) { // JSON
                    final String key = "\"filename\":\"";
                    int i = value.indexOf(key);
                    if (i > -1) { //key found, grab all until next "
                        filename = value.substring(i + key.length()).split("\"", 2)[0];
                    }
                } else { //legacy: naked filename
                    filename = value;
                }
                if (filename != null) {
                    filename = filename.substring(filename.lastIndexOf("/") + 1); //strip path
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
        }

        deviceFile.setSurveyInstanceId(si.getKey().getId());
        DataProcessorRestServlet.queueSynchronizedSummaryUpdate(si, true);

        return si;
    }

    private boolean isProcessable(QuestionAnswerStore qas, SurveyInstance si) {
        Long qid = Long.valueOf(qas.getQuestionID());
        if (qasDao.isCached(qid, si.getKey().getId())) {
            log.log(Level.INFO,
                    "Skipping QAS already present in datasore [SurveyInstance, Survey, Question]: "
                            + qas.getSurveyInstanceId() + ", " + si.getSurveyId() + ", "
                            + qas.getQuestionID());
            return false;
        } else if (questionDao.getByKey(qid) == null) {
            log.log(Level.WARNING, String.format("Question %d not found in the datastore", qid));
            return false;
        }

        return true;
    }

    private SurveyedLocale saveSurveyedLocale(SurveyInstance si) {
        final SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        // Fetch or create the corresponding locale for this instance.
        SurveyedLocale sl = si.getSurveyedLocaleIdentifier() != null ?
                slDao.getByIdentifier(si.getSurveyedLocaleIdentifier())
                : null;
        if (sl == null) {
            sl = new SurveyedLocale();

            sl.setOrganization(PropertyUtil.getProperty(DEFAULT_ORG_PROP));

            if (StringUtils.isNotBlank(si.getSurveyedLocaleIdentifier())) {
                sl.setIdentifier(si.getSurveyedLocaleIdentifier());
            } else {
                // if we don't have an identifier, create a random UUID.
                sl.setIdentifier(SurveyedLocale.generateBase32Uuid());
            }

            Survey survey = SurveyUtils.retrieveSurvey(si.getSurveyId());
            if (survey != null) {
                SurveyGroup surveyGroup = SurveyUtils
                        .retrieveSurveyGroup(survey.getSurveyGroupId());
                sl.setSurveyGroupId(survey.getSurveyGroupId());
                sl.setCreationSurveyId(surveyGroup.getNewLocaleSurveyId());
            }
        }

        // Update the display name and location, if applies.
        if (si.getSurveyedLocaleDisplayName() != null) {
            sl.setDisplayName(si.getSurveyedLocaleDisplayName());
        }
        if (si.getLocaleGeoLocation() != null) {
            String[] tokens = si.getLocaleGeoLocation().split("\\|", -1);
            if (tokens.length >= 2) {
                try {
                    sl.setLatitude(Double.parseDouble(tokens[0]));
                    sl.setLongitude(Double.parseDouble(tokens[1]));
                } catch (NumberFormatException nfe) {
                    log.log(Level.SEVERE,
                            "Could not parse lat/lon from META_GEO: " + si.getLocaleGeoLocation());
                }
            }
        }
        sl = slDao.save(sl);

        return sl;
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
            boolean unapprovedOnlyFlag, Long surveyId, String deviceIdentifier,
            String submitterName,
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
            Date beginDate, Date endDate, Integer limit, Long surveyId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        // The Query interface assembles a query
        com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query(
                "SurveyInstance");
        List<Filter> filters = new ArrayList<Filter>();
        if (returnKeysOnly) {
            q.setKeysOnly();
        }

        if (surveyId != null) {
            filters.add(new FilterPredicate("surveyId", FilterOperator.EQUAL,
                    surveyId));
        }
        if (beginDate != null) {
            filters.add(new FilterPredicate("collectionDate",
                    FilterOperator.GREATER_THAN_OR_EQUAL, beginDate));
        }
        if (endDate != null) {
            filters.add(new FilterPredicate("collectionDate",
                    FilterOperator.LESS_THAN_OR_EQUAL, endDate));
        }

        if (filters.size() == 1) {
            q.setFilter(filters.get(0));
        }

        if (filters.size() > 1) {
            q.setFilter(CompositeFilterOperator.and(filters));
        }
        q.addSort("collectionDate", SortDirection.DESCENDING);
        PreparedQuery pq = datastore.prepare(q);
        // TODO: Should we add .withChunkSize as well?
        FetchOptions fetchOptions;
        if (limit != null) {
            fetchOptions = FetchOptions.Builder.withLimit(limit);
        } else {
            fetchOptions = FetchOptions.Builder.withDefaults();
        }
        return pq.asIterable(fetchOptions);
    }

    /**
     * finds a questionAnswerStore object for the surveyInstance and questionId passed in (if it
     * exists)
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
     * lists all questionAnswerStore objects for a single surveyInstance, optionally filtered by
     * type
     *
     * @param surveyInstanceId - mandatory
     * @param type - optional
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
     * Update counts of SurveyQuestionSummary entities related to responses from this survey
     * instance.
     * Execute on the surveyResponseCount task queue to prevent concurrent access errors!
     */
    public void updateSummaryCounts(long siId, boolean increment) {
        // retrieve all summary objects
        SurveyQuestionSummaryDao summaryDao = new SurveyQuestionSummaryDao();
        QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        QuestionDao qDao = new QuestionDao();

        List<QuestionAnswerStore> answerList = qasDao.listBySurveyInstance(siId);
        if (answerList == null || answerList.isEmpty()) {
            return;
        }
        List<SurveyQuestionSummary> saveList = new ArrayList<SurveyQuestionSummary>();
        List<SurveyQuestionSummary> deleteList = new ArrayList<SurveyQuestionSummary>();

        for (QuestionAnswerStore response : answerList) {
            final Long questionId = Long.parseLong(response.getQuestionID());
            Question question = qDao.getByKey(questionId);
            if (question == null || !question.canBeCharted()) {
                continue;
            }

            final String questionIdStr = response.getQuestionID();
            //JSON or |-separated content; take it apart
            final String[] questionResponse = DataUtils.optionResponsesTextArray(response
                    .getValue());

            for (int i = 0; i < questionResponse.length; i++) {
                List<SurveyQuestionSummary> questionSummaryList = summaryDao
                        .listByResponse(questionIdStr, questionResponse[i]); //could be longer than MAX_DS_STRING_LENGTH!
                SurveyQuestionSummary questionSummary = null;
                if (questionSummaryList.isEmpty()) {
                    questionSummary = new SurveyQuestionSummary();
                    questionSummary.setQuestionId(response.getQuestionID());
                    questionSummary.setResponse(questionResponse[i]); //could be longer than MAX_DS_STRING_LENGTH!
                    questionSummary.setCount(0L);
                    summaryDao.save(questionSummary);
                } else {
                    questionSummary = questionSummaryList.get(0);
                }

                // update and save or delete
                long count = questionSummary.getCount() == null ? 0 : questionSummary.getCount();
                count = increment ? ++count : --count;
                questionSummary.setCount(count);

                if (count > 0) {
                    saveList.add(questionSummary);
                } else {
                    deleteList.add(questionSummary);
                }
            }
        }

        summaryDao.save(saveList);
        summaryDao.delete(deleteList);
    }


    public void deleteSurveyInstance(SurveyInstance surveyInstance) {
        final Long surveyInstanceId = surveyInstance.getKey().getId();

        deleteSurveyInstanceContent(surveyInstanceId);

        super.delete(surveyInstance);
    }

    public void deleteSurveyInstanceContent(Long surveyInstanceId) {

        // update summary counts + delete question answers
        QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        QuestionDao qDao = new QuestionDao();
        List<QuestionAnswerStore> qasList = qasDao.listBySurveyInstance(surveyInstanceId);
        SurveyQuestionSummaryDao summDao = new SurveyQuestionSummaryDao();
        if (qasList != null && !qasList.isEmpty()) {
            for (QuestionAnswerStore qasItem : qasList) {
                // question summaries
                Question question = qDao.getByKey(Long.parseLong(qasItem.getQuestionID())); //QAS question IDs are stored as String...
                if (question != null && question.canBeCharted()) {
                    //JSON or |-separated, possibly multiple choices; take it apart
                    final String[] responses = DataUtils.optionResponsesTextArray(qasItem.getValue());
                    for (int i = 0; i < responses.length; i++) {
                        List<SurveyQuestionSummary> summaryList = summDao.listByResponse(
                                qasItem.getQuestionID(),
                                responses[i]); //could be longer than MAX_DS_STRING_LENGTH!

                        if (summaryList != null && !summaryList.isEmpty()) {
                            String key = Long.toString(summaryList.get(0).getKey().getId());
                            DataProcessorRestServlet.queueAdjustSurveyResponseCount(key, false);
                            continue;
                        }
                    }
                }
            }

            qasDao.delete(qasList);
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
        List<SurveyInstance> siList = (List<SurveyInstance>) q.execute(surveyId);

        return siList;
    }

    public List<SurveyInstance> listSurveyInstanceBySurveyId(Long surveyId,
            String cursorString) {
        return listSurveyInstanceBySurvey(surveyId, null, cursorString);
    }

    public Iterable<Entity> listSurveyInstanceKeysBySurveyId(Long surveyId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query(
                "SurveyInstance");
        q.setKeysOnly().setFilter(new FilterPredicate("surveyId", FilterOperator.EQUAL, surveyId));
        PreparedQuery pq = datastore.prepare(q);
        return pq.asIterable();
    }

    /**
     * lists instances for the given surveyedLocale optionally filtered by the dates passed in
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
     * lists instances for the given surveyedLocale optionally filtered by the dates passed in
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
     * lists questionAnswerStore objects of particular types passed in
     */
    @SuppressWarnings("unchecked")
    public List<QuestionAnswerStore> listQAOptions(String cursorString, Integer pageSize,
            String... options) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query q = pm.newQuery(QuestionAnswerStore.class);
        StringBuffer filter = new StringBuffer();
        for (String op : options) {
            filter.append("type == '").append(op).append("' ||");
        }
        q.setFilter(filter.substring(0, filter.length() - 3).toString());
        prepareCursor(cursorString, pageSize, q);
        return (List<QuestionAnswerStore>) q.execute();
    }

    /**
     * finds a single survey instance by uuid. This method will NOT load all QuestionAnswerStore
     * objects.
     *
     * @param uuid
     * @return
     */
    public SurveyInstance findByUUID(String uuid) {
        return findByProperty("uuid", uuid, "String");
    }

    public SurveyInstance getRegistrationSurveyInstance(SurveyedLocale locale,
            Long registrationSurveyId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        Query query = pm.newQuery(SurveyInstance.class);

        Map<String, Object> paramMap = null;

        StringBuilder filterString = new StringBuilder();
        StringBuilder paramString = new StringBuilder();
        paramMap = new HashMap<String, Object>();

        appendNonNullParam("surveyId", filterString, paramString,
                "Long", registrationSurveyId, paramMap);
        appendNonNullParam("surveyedLocaleId", filterString, paramString,
                "Long", locale.getKey().getId(), paramMap);
        query.setFilter(filterString.toString());
        query.declareParameters(paramString.toString());
        query.setOrdering("collectionDate ascending");

        @SuppressWarnings("unchecked")
        List<SurveyInstance> res = (List<SurveyInstance>) query.executeWithMap(paramMap);
        if (res != null && !res.isEmpty()) {
            return res.get(0);
        }
        log.warning(String.format("Registration form withId: %s submission not found for dataPointId: %s", locale.getCreationSurveyId(), locale.getIdentifier()));
        return null;
    }

    public List<SurveyInstance> getRegistrationFormData(@Nonnull List<SurveyedLocale> surveyedLocales) {

        if (surveyedLocales.isEmpty()) {
            return Collections.emptyList();
        }

        Long registrationFormId = surveyedLocales.get(0).getCreationSurveyId();
        List<Long> dataPointIds = new ArrayList<>();
        for (SurveyedLocale s : surveyedLocales) {
            dataPointIds.add(s.getKey().getId());
        }

        return getRegistrationFormInstances(dataPointIds, registrationFormId);
    }

    private List<SurveyInstance> getRegistrationFormInstances(List<Long> dataPointIds, Long registrationFormId) {
        if (dataPointIds == null || dataPointIds.isEmpty()) {
            return Collections.emptyList();
        }
        PersistenceManager pm = PersistenceFilter.getManager();
        String queryString = "surveyId == :p1 && :p2.contains(surveyedLocaleId)";
        javax.jdo.Query query = pm.newQuery(SurveyInstance.class, queryString);

        return (List<SurveyInstance>) query.execute(registrationFormId, dataPointIds);
    }
}
