/*
 *  Copyright (C) 2010-2015, 2017 Stichting Akvo (Akvo Foundation)
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.lang.StringUtils;
import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;
import org.waterforpeople.mapping.app.web.dto.ImageCheckRequest;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

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
import com.gallatinsystems.surveyal.app.web.SurveyalRestRequest;
import com.gallatinsystems.surveyal.dao.SurveyalValueDao;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
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

        // Recompute data summarization for new locations.
        for (QuestionAnswerStore qas : locations) {
            long geoQasId = qas.getKey().getId();
            Queue summQueue = QueueFactory.getQueue("dataSummarization");
            summQueue.add(TaskOptions.Builder
                    .withUrl("/app_worker/dataprocessor")
                    .param(DataProcessorRequest.ACTION_PARAM,
                            DataProcessorRequest.SURVEY_INSTANCE_SUMMARIZER)
                    .param("surveyInstanceId", si.getKey().getId() + "")
                    .param("qasId", geoQasId + "")
                    .param("delta", 1 + ""));
        }

        // Now that QAS IDs are set, enqueue imagecheck tasks,
        // whereby the presence of an image in S3 will be checked.
        if (!images.isEmpty()) {
            Device d = deviceDao.getDevice(deviceFile.getAndroidId(),
                    deviceFile.getImei(), deviceFile.getPhoneNumber());
            String deviceId = d == null ? "null" : String.valueOf(d.getKey().getId());

            for (QuestionAnswerStore qas : images) {
                String filename = qas.getValue().substring(
                        qas.getValue().lastIndexOf("/") + 1);

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

        deviceFile.setSurveyInstanceId(si.getKey().getId());
        si.updateSummaryCounts(true);

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
                sl.setLocaleType(surveyGroup.getPrivacyLevel().toString());
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
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
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
     * Deletes a surveyInstance and all its related objects
     *
     * @param surveyInstance survey instance to be deleted
     */
    // TODO update lastSurveyalInstanceId in surveydLocale objects
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    public void deleteSurveyInstance(SurveyInstance surveyInstance) {
        final Long surveyInstanceId = surveyInstance.getKey().getId();

        // update summary counts + delete question answers
        QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        QuestionDao qDao = new QuestionDao();
        List<QuestionAnswerStore> qasList = qasDao.listBySurveyInstance(surveyInstanceId);
        SurveyQuestionSummaryDao summDao = new SurveyQuestionSummaryDao();
        if (qasList != null && !qasList.isEmpty()) {
            for (QuestionAnswerStore qasItem : qasList) {
                // question summaries
                Question question = qDao.getByKey(Long.parseLong(qasItem.getQuestionID()));
                if (question != null && question.canBeCharted()) {
                    Queue questionSummaryQueue = QueueFactory.getQueue("surveyResponseCount");
                    List<SurveyQuestionSummary> summaryList = summDao.listByResponse(
                            qasItem.getQuestionID(), qasItem.getValue());
                    if (summaryList != null && !summaryList.isEmpty()) {
                        TaskOptions to = TaskOptions.Builder
                                .withUrl("/app_worker/dataprocessor")
                                .param(DataProcessorRequest.ACTION_PARAM,
                                        DataProcessorRequest.SURVEY_RESPONSE_COUNT)
                                .param(DataProcessorRequest.COUNTER_ID_PARAM,
                                        summaryList.get(0).getKey().getId() + "")
                                .param(DataProcessorRequest.DELTA_PARAM, "-1");
                        questionSummaryQueue.add(to);
                        continue;
                    }
                }

                // survey instance summary task
                if (Question.Type.GEO.toString().equals(qasItem.getType())) {
                    Queue summaryQueue = QueueFactory.getQueue("dataSummarization");

                    TaskOptions to = TaskOptions.Builder
                            .withUrl("/app_worker/dataprocessor")
                            .param(DataProcessorRequest.ACTION_PARAM,
                                    DataProcessorRequest.SURVEY_INSTANCE_SUMMARIZER)
                            .param(DataProcessorRequest.DELTA_PARAM, "-1");
                    summaryQueue.add(to);
                }
            }

            qasDao.delete(qasList);
        }

        // delete surveyal values
        SurveyedLocaleDao surveyedLocaleDao = new SurveyedLocaleDao();
        SurveyalValueDao svDao = new SurveyalValueDao();
        List<SurveyalValue> surveyalValues = surveyedLocaleDao
                .listSurveyalValuesByInstance(surveyInstanceId);
        if (surveyalValues != null && !surveyalValues.isEmpty()) {
            svDao.delete(surveyalValues);
        }

        // task to adapt cluster data + delete surveyedlocale if not needed anymore
        if (surveyInstance.getSurveyedLocaleId() != null) {
            Long surveyedLocaleId = surveyInstance.getSurveyedLocaleId();
            List<SurveyInstance> relatedSurveyInstances = listByProperty("surveyedLocaleId",
                    surveyedLocaleId, "Long");

            if (relatedSurveyInstances.size() < 2) {
                // only the current (or no) survey instance is related to the locale. we fire task
                // to delete locale and update clusters
                // The locale is deleted in the decrement cluster task.
                Queue queue = QueueFactory.getDefaultQueue();
                TaskOptions to = TaskOptions.Builder
                        .withUrl("/app_worker/surveyalservlet")
                        .param(SurveyalRestRequest.ACTION_PARAM,
                                SurveyalRestRequest.ADAPT_CLUSTER_DATA_ACTION)
                        .param(SurveyalRestRequest.SURVEYED_LOCALE_PARAM,
                                surveyedLocaleId + "")
                        .param(SurveyalRestRequest.DECREMENT_CLUSTER_COUNT_PARAM,
                                Boolean.TRUE.toString());
                queue.add(to);
            }
        }

        super.delete(surveyInstance);

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

        List<SurveyInstance> res = (List<SurveyInstance>) query.executeWithMap(paramMap);
        if (res != null && !res.isEmpty()) {
            return res.get(0);
        }

        return null;
    }

}
