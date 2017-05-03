/*
 *  Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web;

import static com.gallatinsystems.common.util.MemCacheUtils.containsKey;
import static com.gallatinsystems.common.util.MemCacheUtils.initCache;
import static com.gallatinsystems.common.util.MemCacheUtils.putObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.akvo.flow.domain.DataUtils;
import org.waterforpeople.mapping.analytics.dao.SurveyInstanceSummaryDao;
import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.dataexport.SurveyReplicationImporter;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.GeoCoordinates;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.gis.location.GeoLocationService;
import com.gallatinsystems.gis.location.GeoLocationServiceGeonamesImpl;
import com.gallatinsystems.gis.location.GeoPlace;
import com.gallatinsystems.gis.map.MapUtils;
import com.gallatinsystems.messaging.dao.MessageDao;
import com.gallatinsystems.messaging.domain.Message;
import com.gallatinsystems.operations.dao.ProcessingStatusDao;
import com.gallatinsystems.operations.domain.ProcessingStatus;
import com.gallatinsystems.survey.dao.CascadeNodeDao;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.QuestionOptionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.dao.TranslationDao;
import com.gallatinsystems.survey.domain.CascadeNode;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.surveyal.dao.SurveyalValueDao;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * Restful servlet to do bulk data update operations
 *
 * @author Christopher Fagiani
 */
public class DataProcessorRestServlet extends AbstractRestApiServlet {
    private static final Logger log = Logger
            .getLogger("DataProcessorRestServlet");
    private static final long serialVersionUID = -7902002525342262821L;
    private static final String REBUILD_Q_SUM_STATUS_KEY = "rebuildQuestionSummary";
    private static final Integer QAS_PAGE_SIZE = 300;
    private static final Integer LOCALE_PAGE_SIZE = 500;
    private static final Integer T_PAGE_SIZE = 300;
    private static final Integer SVAL_PAGE_SIZE = 600;
    private static final String QAS_TO_REMOVE = "QAStoRemove";
    private static final long NAME_ASSEMBLY_TASK_DELAY = 3 * 1000;// 3 seconds

    private SurveyInstanceDAO siDao;

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new DataProcessorRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        DataProcessorRequest dpReq = (DataProcessorRequest) req;
        if (DataProcessorRequest.PROJECT_FLAG_UPDATE_ACTION
                .equalsIgnoreCase(dpReq.getAction())) {
            updateAccessPointProjectFlag(dpReq.getCountry(), dpReq.getCursor());
        } else if (DataProcessorRequest.REBUILD_QUESTION_SUMMARY_ACTION
                .equalsIgnoreCase(dpReq.getAction())) {
            rebuildQuestionSummary(dpReq.getSurveyId());
        } else if (DataProcessorRequest.COPY_SURVEY.equalsIgnoreCase(dpReq
                .getAction())) {
            copySurvey(dpReq.getSurveyId(), Long.valueOf(dpReq.getSource()));
        } else if (DataProcessorRequest.COPY_QUESTION_GROUP.equalsIgnoreCase(dpReq
                .getAction())) {
            QuestionGroupDao qgDao = new QuestionGroupDao();
            QuestionGroup newQuestionGroup = qgDao.getByKey(dpReq.getQuestionGroupId());
            QuestionGroup originalQuestionGroup = qgDao.getByKey(Long.valueOf(dpReq.getSource()));
            if (originalQuestionGroup != null && newQuestionGroup != null) {
                
                Long surveyId = originalQuestionGroup.getSurveyId();
                SurveyUtils.copyQuestionGroup(originalQuestionGroup, newQuestionGroup,
                        surveyId, null, SurveyUtils.listQuestionIdsUsedInSurveyGroup(surveyId));

                newQuestionGroup.setStatus(QuestionGroup.Status.READY); // copied
                qgDao.save(newQuestionGroup);

            }
        } else if (DataProcessorRequest.FIX_QUESTIONGROUP_DEPENDENCIES_ACTION
                .equalsIgnoreCase(dpReq.getAction())) {
            QuestionGroup newQuestionGroup = new QuestionGroupDao().getByKey(dpReq
                    .getQuestionGroupId());
            QuestionGroup originalQuestionGroup = new QuestionGroupDao()
                    .getByKey(Long.valueOf(dpReq.getSource()));

            if (originalQuestionGroup != null && newQuestionGroup != null) {
                final List<Long> unresolvedDependentIds = dpReq.getDependentQuestionIds();
                final List<Question> processedQuestionList = fixQuestionGroupDependencies(
                        newQuestionGroup, originalQuestionGroup, unresolvedDependentIds);

                // check all resolved else reschedule
                List<Question> unresolvedDependencies = new ArrayList<Question>();
                for (Question q : processedQuestionList) {
                    if (q.getDependentQuestionId() == null) {
                        unresolvedDependencies.add(q);
                    }
                }

                if (!unresolvedDependencies.isEmpty()) {
                    TaskOptions options = TaskOptions.Builder
                            .withUrl("/app_worker/dataprocessor")
                            .param(DataProcessorRequest.ACTION_PARAM, dpReq.getAction())
                            .param(DataProcessorRequest.QUESTION_GROUP_ID_PARAM,
                                    dpReq.getQuestionGroupId().toString())
                            .param(DataProcessorRequest.SOURCE_PARAM, dpReq.getSource());
                    for (Question q : unresolvedDependencies) {
                        options.param(DataProcessorRequest.DEPENDENT_QUESTION_PARAM,
                                Long.toString(q.getKey().getId()));
                    }

                    int retry = dpReq.getRetry();
                    if (unresolvedDependencies.size() == unresolvedDependentIds.size()) {
                        // in case none of dependencies were resolved include retry param
                        options.param(DataProcessorRequest.RETRY_PARAM, Integer.toString(++retry));
                    }
                    if (retry < DataProcessorRequest.MAX_TASK_RETRIES) {
                        Queue queue = QueueFactory.getQueue("dataUpdate");
                        queue.add(options);
                    } else {
                        log.severe("Failed to resolve dependencies for copied QuestionGroup "
                                + newQuestionGroup.getKey().getId() + " after multiple retries");
                    }
                }

            }
        } else if (DataProcessorRequest.IMPORT_REMOTE_SURVEY_ACTION
                .equalsIgnoreCase(dpReq.getAction())) {
            SurveyReplicationImporter sri = new SurveyReplicationImporter();
            sri.executeImport(dpReq.getSource(), dpReq.getSurveyId(), dpReq.getApiKey());
        } else if (DataProcessorRequest.RESCORE_AP_ACTION
                .equalsIgnoreCase(dpReq.getAction())) {
            rescoreAp(dpReq.getCountry());
        } else if (DataProcessorRequest.FIX_DUPLICATE_OTHER_TEXT_ACTION
                .equalsIgnoreCase(dpReq.getAction())) {
            fixDuplicateOtherText();
        } else if (DataProcessorRequest.TRIM_OPTIONS.equalsIgnoreCase(dpReq
                .getAction())) {
            trimOptions();
        } else if (DataProcessorRequest.FIX_OPTIONS2VALUES_ACTION
                .equalsIgnoreCase(dpReq.getAction())) {
            fixOptions2Values();
        } else if (DataProcessorRequest.SURVEY_INSTANCE_SUMMARIZER
                .equalsIgnoreCase(dpReq.getAction())) {
            surveyInstanceSummarizer(dpReq.getSurveyInstanceId(),
                    dpReq.getQasId(), dpReq.getDelta());
        } else if (DataProcessorRequest.DELETE_DUPLICATE_QAS
                .equalsIgnoreCase(dpReq.getAction())) {
            deleteDuplicatedQAS(dpReq.getOffset());
        } else if (DataProcessorRequest.CHANGE_LOCALE_TYPE_ACTION
                .equalsIgnoreCase(dpReq.getAction())) {
            changeLocaleType(dpReq.getSurveyId());
        } else if (DataProcessorRequest.ADD_TRANSLATION_FIELDS
                .equalsIgnoreCase(dpReq.getAction())) {
            addTranslationFields(dpReq.getCursor());
        } else if (DataProcessorRequest.RECOMPUTE_LOCALE_CLUSTERS
                .equalsIgnoreCase(dpReq.getAction())) {
            recomputeLocaleClusters(dpReq.getCursor());
        } else if (DataProcessorRequest.ADD_CREATION_SURVEY_ID_TO_LOCALE
                .equalsIgnoreCase(dpReq.getAction())) {
            addCreationSurveyIdToLocale(dpReq.getCursor());
        } else if (DataProcessorRequest.POPULATE_MONITORING_FIELDS_LOCALE_ACTION
                .equalsIgnoreCase(req.getAction())) {
            populateMonitoringFieldsLocale(dpReq.getCursor(), dpReq.getSurveyId(), dpReq.getSurveyedLocaleId());
        } else if (DataProcessorRequest.CREATE_NEW_IDENTIFIERS_LOCALES_ACTION
                .equalsIgnoreCase(req.getAction())) {
            createNewIdentifiersLocales(dpReq.getCursor(), dpReq.getSurveyId());
        } else if (DataProcessorRequest.POP_QUESTION_ORDER_FIELDS_ACTION.equalsIgnoreCase(req
                .getAction())) {
            populateQuestionOrdersSurveyalValues(dpReq.getSurveyId(), req.getCursor());
        } else if (DataProcessorRequest.DELETE_SURVEY_INSTANCE_ACTION.equalsIgnoreCase(req
                .getAction())) {
            if (dpReq.getSurveyInstanceId() != null) {
                deleteSurveyResponses(dpReq.getSurveyInstanceId());
            }
        } else if (DataProcessorRequest.SURVEY_RESPONSE_COUNT.equalsIgnoreCase(req
                .getAction())) {
            if (dpReq.getSummaryCounterId() != null && dpReq.getDelta() != null) {
                updateSurveyResponseCounter(dpReq.getSummaryCounterId(), dpReq.getDelta());
            }
        } else if (DataProcessorRequest.DELETE_CASCADE_NODES.equalsIgnoreCase(req.getAction())) {
            deleteCascadeNodes(dpReq.getCascadeResourceId(), dpReq.getParentNodeId());
        } else if (DataProcessorRequest.ASSEMBLE_DATAPOINT_NAME.equalsIgnoreCase(req.getAction())) {
            assembleDatapointName(dpReq.getSurveyGroupId(), dpReq.getSurveyedLocaleId());
        }
        return new RestResponse();
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
    }

    /**
     * lists all QuestionOptions and trims trailing/leading spaces. Then does the same for any
     * dependencies
     */
    private void trimOptions() {
        QuestionOptionDao optDao = new QuestionOptionDao();
        QuestionDao qDao = new QuestionDao();
        String cursor = null;
        do {
            List<QuestionOption> optList = optDao.list(cursor);

            if (optList != null && optList.size() > 0) {
                for (QuestionOption opt : optList) {
                    if (opt.getText() != null) {
                        opt.setText(opt.getText().trim());
                    }
                    List<Question> qList = qDao.listQuestionsByDependency(opt
                            .getQuestionId());
                    for (Question q : qList) {
                        if (q.getText() != null) {
                            q.setText(q.getText().trim());
                        }
                        if (q.getDependentQuestionAnswer() != null) {
                            q.setDependentQuestionAnswer(q
                                    .getDependentQuestionAnswer().trim());
                        }
                    }
                }
                if (optList.size() == QuestionOptionDao.DEFAULT_RESULT_COUNT) {
                    cursor = QuestionOptionDao.getCursor(optList);
                } else {
                    cursor = null;
                }
            } else {
                cursor = null;
            }
        } while (cursor != null);
    }

    /**
     * lists all "OTHER" type answers and checks if the last tokens are duplicates. Fixes if they
     * are.
     */
    private void fixDuplicateOtherText() {
        QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        int pageSize = 300;
        String cursor = null;
        do {
            List<QuestionAnswerStore> answers = qasDao.listByTypeAndDate(
                    "OTHER", null, null, cursor, pageSize);
            if (answers != null) {
                for (QuestionAnswerStore ans : answers) {
                    if (ans.getValue() != null && ans.getValue().contains("|")) {
                        String[] tokens = ans.getValue().split("\\|");
                        String lastVal = null;
                        boolean droppedVal = false;
                        StringBuilder buf = new StringBuilder();
                        for (int i = 0; i < tokens.length; i++) {
                            if (!tokens[i].equals(lastVal)) {
                                lastVal = tokens[i];
                                if (i > 0) {
                                    buf.append("|");
                                }
                                buf.append(lastVal);
                            } else {
                                droppedVal = true;
                            }
                        }
                        if (droppedVal) {
                            // only dirty the object if needed
                            ans.setValue(buf.toString());
                        }
                    }
                }

                if (answers.size() == pageSize) {

                    cursor = QuestionAnswerStoreDao.getCursor(answers);
                } else {
                    cursor = null;
                }
            }
        } while (cursor != null);
    }

    /**
     * changes the surveyedLocales attached to a survey to a different type 1 = Point 2 = Household
     * 3 = Public Institutions
     */
    private void changeLocaleType(Long surveyId) {
        SurveyInstanceDAO siDao = new SurveyInstanceDAO();
        SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        SurveyDAO sDao = new SurveyDAO();
        String cursor = null;
        // get the desired type from the survey definition
        Survey s = sDao.getByKey(surveyId);
        if (s != null && s.getPointType() != null && s.getPointType().length() > 0) {
            String localeType = s.getPointType();

            do {
                List<SurveyInstance> siList = siDao.listSurveyInstanceBySurvey(surveyId,
                        QAS_PAGE_SIZE, cursor);
                if (siList != null && siList.size() > 0) {
                    for (SurveyInstance si : siList) {
                        if (si.getSurveyedLocaleId() != null) {
                            SurveyedLocale sl = slDao.getByKey(si.getSurveyedLocaleId());
                            if (sl != null) {
                                // if the locale type is not set or if it is not equal to the survey
                                // setting,
                                // reset the local type
                                if (sl.getLocaleType() == null
                                        || !sl.getLocaleType().equals(localeType)) {
                                    sl.setLocaleType(localeType);
                                    // Ensure the save time is unique. See
                                    // https://github.com/akvo/akvo-flow/issues/605
                                    slDao.save(sl);
                                }
                            }
                        }
                    }
                    if (siList.size() == QAS_PAGE_SIZE) {
                        cursor = SurveyInstanceDAO.getCursor(siList);
                    } else {
                        cursor = null;
                    }
                }
            } while (cursor != null);

            // recompute all clusters
            final TaskOptions options = TaskOptions.Builder
                    .withUrl("/app_worker/dataprocessor")
                    .param(DataProcessorRequest.ACTION_PARAM,
                            DataProcessorRequest.RECOMPUTE_LOCALE_CLUSTERS);
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(options);
        }
    }

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    private void deleteDuplicatedQAS(Long offset) {
        log.log(Level.INFO, "Searching for duplicated QAS entities [Offset: "
                + offset + "]");

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

        if (cache == null) {
            return;
        }

        final PersistenceManager pm = PersistenceFilter.getManager();
        final Query q = pm.newQuery(QuestionAnswerStore.class);
        q.setOrdering("createdDateTime asc");
        q.setRange(offset, offset + QAS_PAGE_SIZE);

        final List<QuestionAnswerStore> results = (List<QuestionAnswerStore>) q
                .execute();

        List<QuestionAnswerStore> toRemove;

        if (cache.containsKey(QAS_TO_REMOVE)) {
            toRemove = (List<QuestionAnswerStore>) cache.get(QAS_TO_REMOVE);
        } else {
            toRemove = new ArrayList<QuestionAnswerStore>();
        }

        for (QuestionAnswerStore item : results) {

            final Long questionID = Long.valueOf(item.getQuestionID());
            final Long surveyInstanceId = item.getSurveyInstanceId();

            final Map<Long, Long> k = new HashMap<Long, Long>();
            k.put(surveyInstanceId, questionID);

            if (cache.containsKey(k)) {
                toRemove.add(item);
            }

            cache.put(k, true);
        }

        if (results.size() == QAS_PAGE_SIZE) {
            cache.put(QAS_TO_REMOVE, toRemove);

            final TaskOptions options = TaskOptions.Builder
                    .withUrl("/app_worker/dataprocessor")
                    .param(DataProcessorRequest.ACTION_PARAM,
                            DataProcessorRequest.DELETE_DUPLICATE_QAS)
                    .param(DataProcessorRequest.OFFSET_PARAM,
                            String.valueOf(offset + QAS_PAGE_SIZE))
                    .header("Host",
                            BackendServiceFactory.getBackendService()
                                    .getBackendAddress("dataprocessor"));
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(options);

        } else {
            log.log(Level.INFO, "Removing " + toRemove.size()
                    + " duplicated QAS entities");
            QuestionAnswerStoreDao dao = new QuestionAnswerStoreDao();
            pm.makePersistentAll(toRemove); // some objects are in "transient" state
            dao.delete(toRemove);
        }
    }

    /**
     * This recomputes all Locale clusters. Clusters are deleted in the testharnessservlet. The keys
     * are first removed in the testharnessservlet.
     *
     * @param offset
     */
    private void recomputeLocaleClusters(String cursor) {

        log.log(Level.INFO, "recomputing locale clusters [cursor: " + cursor + "]");

        final SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        final List<SurveyedLocale> results = slDao.listAll(cursor, LOCALE_PAGE_SIZE);

        // initialize the memcache
        final Cache cache = initCache(12 * 60 * 60);

        if (cache == null) {
            return;
        }

        for (SurveyedLocale locale : results) {
            // adjust Geocell cluster data
            if (locale.getGeocells() != null && !locale.getGeocells().isEmpty()) {
                MapUtils.recomputeCluster(cache, locale, 1);
            }
        }

        if (results.size() == LOCALE_PAGE_SIZE) {
            final String newCursor = SurveyedLocaleDao.getCursor(results);
            final TaskOptions options = TaskOptions.Builder
                    .withUrl("/app_worker/dataprocessor")
                    .param(DataProcessorRequest.ACTION_PARAM,
                            DataProcessorRequest.RECOMPUTE_LOCALE_CLUSTERS)
                    .param(DataProcessorRequest.CURSOR_PARAM,
                            newCursor != null ? newCursor : "")
                    .header("Host", BackendServiceFactory.getBackendService()
                            .getBackendAddress("dataprocessor"));
            ;
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(options);

        }
    }

    /**
     * this method re-runs scoring on all access points for a country
     *
     * @param country
     */
    private void rescoreAp(String country) {
        AccessPointDao apDao = new AccessPointDao();
        String cursor = null;
        List<AccessPoint> apList = null;
        do {
            apList = apDao.listAccessPointByLocation(country, null, null, null,
                    cursor, 200);
            if (apList != null) {
                cursor = AccessPointDao.getCursor(apList);

                for (AccessPoint ap : apList) {
                    apDao.save(ap);
                }
            }
        } while (apList != null && apList.size() == 200);
    }

    private void copySurvey(Long copiedSurveyId, Long originalSurveyId) {

        final QuestionGroupDao qgDao = new QuestionGroupDao();

        final List<QuestionGroup> qgList = qgDao.listQuestionGroupBySurvey(originalSurveyId);
        final Map<Long, Long> qDependencyResolutionMap = new HashMap<Long, Long>();

        if (qgList == null) {
            log.log(Level.INFO, "Nothing to copy from {surveyId: " + originalSurveyId
                    + "} to {surveyId: " + copiedSurveyId + "}");
            SurveyUtils.resetSurveyState(copiedSurveyId);
            return;
        }

        log.log(Level.INFO, "Copying " + qgList.size() + " `QuestionGroup`");

        for (final QuestionGroup sourceGroup : qgList) {
            // need a temp group to avoid state sharing exception
            QuestionGroup tmpGroup = new QuestionGroup();
            SurveyUtils.shallowCopy(sourceGroup, tmpGroup);
            tmpGroup.setSurveyId(copiedSurveyId);

            final QuestionGroup copyGroup = qgDao.save(tmpGroup);
            SurveyUtils.copyQuestionGroup(sourceGroup, copyGroup, copiedSurveyId,
                    qDependencyResolutionMap, null); //new survey, so id re-use is OK
        }

        final SurveyDAO sDao = new SurveyDAO();
        final Survey copiedSurvey = SurveyUtils.resetSurveyState(copiedSurveyId);
        final Survey originalSurvey = sDao.getById(originalSurveyId);

        MessageDao mDao = new MessageDao();
        Message message = new Message();

        message.setObjectId(copiedSurveyId);
        message.setObjectTitle(copiedSurvey.getName());
        message.setActionAbout("copySurvey");
        message.setShortMessage("Copying from Survey " + originalSurveyId + " ("
                + originalSurvey.getName() + ") completed");
        mDao.save(message);

    }

    /**
     * Resolve dependencies for copied questions that are not in the same group as the question on
     * which they are dependent
     *
     * @param newQuestionGroupId the copied question group
     * @param oldQuestionGroupId the original question group from which this copy has been made
     * @param dependentQuestionIdsList list of ids for questions in the copied group that are
     *            dependent on questions *not* in the copied group
     * @return returns the list of dependentQuestions that has been processed.
     */
    private List<Question> fixQuestionGroupDependencies(QuestionGroup newQuestionGroup,
            QuestionGroup oldQuestionGroup, List<Long> dependentQuestionIdsList) {

        log.info("Resolving dependencies for " + dependentQuestionIdsList.size() + " questions");
        QuestionDao qDao = new QuestionDao();

        final List<Question> unresolvedDependentQuestions = qDao
                .listByKeys(dependentQuestionIdsList);
        List<Question> originalDependentQuestions = new ArrayList<Question>();

        for (Question q : unresolvedDependentQuestions) {
            if (q.getSourceQuestionId() != null) {
                Question source = qDao.getByKey(q.getSourceQuestionId());
                if (source == null) {
                    continue;
                }
                originalDependentQuestions.add(source);
            }
        }

        // build mapping from unresolved qn-> original qn -> new dependency question
        Map<Long, Question> originalQuestionsIdMap = new HashMap<Long, Question>();
        List<Long> originalDependentQuestionIds = new ArrayList<Long>();
        for (Question q : originalDependentQuestions) {
            originalQuestionsIdMap.put(q.getKey().getId(), q);
            if (q.getDependentQuestionId() != null) {
                originalDependentQuestionIds.add(q.getDependentQuestionId());
            }
        }

        List<Question> newQuestions = qDao.listBySourceQuestionId(originalDependentQuestionIds);

        Map<Long, Question> newQuestionsSourceIdMap = new HashMap<Long, Question>();
        for (Question q : newQuestions) {
            if (q.getSourceQuestionId() != null) {
                newQuestionsSourceIdMap.put(q.getSourceQuestionId(), q);
            }
        }

        // resolve question dependencies
        for (Question unresolved : unresolvedDependentQuestions) {
            Question source = originalQuestionsIdMap.get(unresolved.getSourceQuestionId());
            if (source == null) {
                continue;
            }

            if (newQuestionsSourceIdMap.containsKey(source.getDependentQuestionId())) {
                Question newQuestion = newQuestionsSourceIdMap.get(source.getDependentQuestionId());
                unresolved.setDependentQuestionId(newQuestion.getKey().getId());
            }
        }

        qDao.save(unresolvedDependentQuestions);
        return unresolvedDependentQuestions;
    }

    /**
     * rebuilds the SurveyQuestionSummary object for ALL data in the system. This method should only
     * be run on a Backend instance as it is unlikely to complete within the task duration limits on
     * other instances.
     */
    private void rebuildQuestionSummary(Long surveyId) {
        ProcessingStatusDao statusDao = new ProcessingStatusDao();
        List<Long> surveyIds = new ArrayList<Long>();
        if (surveyId == null) {
            SurveyDAO surveyDao = new SurveyDAO();
            List<Survey> surveys = surveyDao.list(Constants.ALL_RESULTS);
            if (surveys != null) {
                for (Survey s : surveys) {
                    surveyIds.add(s.getKey().getId());
                }
            }
        } else {
            surveyIds.add(surveyId);
        }

        for (Long sid : surveyIds) {
            ProcessingStatus status = statusDao
                    .getStatusByCode(REBUILD_Q_SUM_STATUS_KEY
                            + (sid != null ? ":" + sid : ""));

            Map<String, Map<String, Long>> summaryMap = summarizeQuestionAnswerStore(
                    sid, null);
            if (summaryMap != null) {
                saveSummaries(summaryMap);
            }
            // now update the status so we can know it last ran
            if (status == null) {
                status = new ProcessingStatus();
                status.setCode(REBUILD_Q_SUM_STATUS_KEY
                        + (sid != null ? ":" + sid : ""));
            }
            status.setInError(false);
            status.setLastEventDate(new Date());
            statusDao.save(status);
        }
    }

    /**
     * iterates over the new summary counts and updates the records in the datastore. Where
     * appropriate, new records will be created and defunct records will be removed.
     *
     * @param summaryMap
     */
    private void saveSummaries(Map<String, Map<String, Long>> summaryMap) {
        SurveyQuestionSummaryDao summaryDao = new SurveyQuestionSummaryDao();
        for (Entry<String, Map<String, Long>> summaryEntry : summaryMap
                .entrySet()) {
            List<SurveyQuestionSummary> summaryList = summaryDao
                    .listByQuestion(summaryEntry.getKey());
            // iterate over all the counts and update the summaryList with the
            // count values. Create any missing elements and remove defunct
            // entries as we go
            List<SurveyQuestionSummary> toDeleteList = new ArrayList<SurveyQuestionSummary>(
                    summaryList);
            List<SurveyQuestionSummary> toCreateList = new ArrayList<SurveyQuestionSummary>();
            for (Entry<String, Long> valueEntry : summaryEntry.getValue()
                    .entrySet()) {
                String val = valueEntry.getKey();
                boolean found = false;
                for (SurveyQuestionSummary sum : summaryList) {
                    if (sum.getResponse() != null
                            && sum.getResponse().equals(val)) {
                        // since it's still valid, remove it from toDeleteList
                        toDeleteList.remove(sum);
                        // update the count. Since we still have the
                        // persistenceContext open, this will automatically be
                        // flushed to the datastore without an explicit call to
                        // save
                        sum.setCount(valueEntry.getValue());
                        found = true;
                    }
                }
                if (!found) {
                    // need to create it
                    SurveyQuestionSummary s = new SurveyQuestionSummary();
                    s.setCount(valueEntry.getValue());
                    s.setQuestionId(summaryEntry.getKey());
                    s.setResponse(val);
                    toCreateList.add(s);
                }
            }
            // delete the unseen entities
            if (toDeleteList.size() > 0) {
                summaryDao.delete(toDeleteList);
            }
            // save the new items
            if (toCreateList.size() > 0) {
                summaryDao.save(toCreateList);
            }
            // flush the datastore operation
            summaryDao.flushBatch();
        }
    }

    /**
     * loads all the summarizable QuestionAnswerStore instances from the data store and accrues
     * counts by value occurrence in a map keyed on the questionId
     *
     * @param sinceDate
     * @return
     */
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    private Map<String, Map<String, Long>> summarizeQuestionAnswerStore(
            Long surveyId, Date sinceDate) {

        final QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        final QuestionDao questionDao = new QuestionDao();
        final List<Question> qList = questionDao.listQuestionsInOrder(surveyId,
                Question.Type.OPTION);

        Cache cache = null;
        Map props = new HashMap();
        props.put(GCacheFactory.EXPIRATION_DELTA, 60 * 60 * 2); // 2h
        props.put(MemcacheService.SetPolicy.SET_ALWAYS, true);
        try {
            CacheFactory cacheFactory = CacheManager.getInstance()
                    .getCacheFactory();
            cache = cacheFactory.createCache(props);
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    "Couldn't initialize cache: " + e.getMessage(), e);
        }

        String cursor = null;
        final Map<String, Map<String, Long>> summaryMap = new HashMap<String, Map<String, Long>>();

        for (Question q : qList) {
            List<QuestionAnswerStore> qasList = qasDao.listByQuestion(q.getKey().getId(), cursor,
                    QAS_PAGE_SIZE);

            if (qasList == null || qasList.size() == 0) {
                continue; // skip
            }

            do {
                cursor = QuestionAnswerStoreDao.getCursor(qasList);

                for (QuestionAnswerStore qas : qasList) {

                    if (cache != null) {
                        Map<Long, String> answer = new HashMap<Long, String>();
                        answer.put(qas.getSurveyInstanceId(), qas.getQuestionID());

                        if (cache.containsKey(answer)) {
                            log.log(Level.INFO,
                                    "Found duplicated QAS {surveyInstanceId: "
                                            + qas.getSurveyInstanceId() + " , questionID: "
                                            + qas.getQuestionID() + "}");
                            continue;
                        }

                        cache.put(answer, true);
                    }

                    Map<String, Long> countMap = summaryMap.get(qas
                            .getQuestionID());

                    if (countMap == null) {
                        countMap = new HashMap<String, Long>();
                        summaryMap.put(qas.getQuestionID(), countMap);
                    }

                    // split up multiple answers
                    String[] answers = DataUtils.optionResponsesTextArray(qas.getValue());

                    // perform count
                    for (int i = 0; i < answers.length; i++) {
                        Long count = countMap.get(answers[i]);
                        if (count == null) {
                            count = 1L;
                        } else {
                            count = count + 1;
                        }
                        countMap.put(answers[i], count);
                    }
                }

                qasList = qasDao.listByQuestion(q.getKey().getId(), cursor, QAS_PAGE_SIZE);

            } while (qasList != null && qasList.size() > 0);

            cursor = null;
        }

        return summaryMap;
    }

    /**
     * iterates over all AccessPoints in a country and applies a static set of rules to determine
     * the proper value of the WFPProjectFlag
     *
     * @param country
     * @param cursor
     */
    private void updateAccessPointProjectFlag(String country, String cursor) {
        AccessPointDao apDao = new AccessPointDao();
        Integer pageSize = 200;
        List<AccessPoint> apList = apDao.listAccessPointByLocation(country,
                null, null, null, cursor, pageSize);
        if (apList != null) {
            for (AccessPoint ap : apList) {

                if ("PE".equalsIgnoreCase(ap.getCountryCode())) {
                    ap.setWaterForPeopleProjectFlag(false);
                } else if ("RW".equalsIgnoreCase(ap.getCountryCode())) {
                    ap.setWaterForPeopleProjectFlag(false);
                } else if ("MW".equalsIgnoreCase(ap.getCountryCode())) {
                    if (ap.getCommunityName().trim()
                            .equalsIgnoreCase("Kachere/Makhetha/Nkolokoti")) {
                        ap.setCommunityName("Kachere/Makhetha/Nkolokoti");
                        if (ap.getWaterForPeopleProjectFlag() == null) {
                            ap.setWaterForPeopleProjectFlag(true);
                        }
                    } else if (ap.getWaterForPeopleProjectFlag() == null) {
                        ap.setWaterForPeopleProjectFlag(false);
                    }
                } else if ("HN".equalsIgnoreCase(ap.getCountryCode())) {
                    if (ap.getCommunityCode().startsWith("IL")) {
                        ap.setWaterForPeopleProjectFlag(false);
                    } else {
                        ap.setWaterForPeopleProjectFlag(true);
                    }

                } else if ("IN".equalsIgnoreCase(ap.getCountryCode())) {
                    if (ap.getWaterForPeopleProjectFlag() == null) {
                        ap.setWaterForPeopleProjectFlag(true);
                    }
                } else if ("GT".equalsIgnoreCase(ap.getCountryCode())) {
                    if (ap.getWaterForPeopleProjectFlag() == null) {
                        ap.setWaterAvailableDayVisitFlag(true);
                    }
                } else {
                    // handles BO, DO, SV
                    if (ap.getWaterForPeopleProjectFlag() == null) {
                        ap.setWaterForPeopleProjectFlag(false);
                    }
                }
            }

            if (apList.size() == pageSize) {
                // check for more
                sendProjectUpdateTask(country, AccessPointDao.getCursor(apList));
            }
        }
    }

    /**
     * Sends a message to a task queue to start or continue the processing of the AP Project Flag
     *
     * @param country
     * @param cursor
     */
    public static void sendProjectUpdateTask(String country, String cursor) {
        TaskOptions options = TaskOptions.Builder
                .withUrl("/app_worker/dataprocessor")
                .param(DataProcessorRequest.ACTION_PARAM,
                        DataProcessorRequest.PROJECT_FLAG_UPDATE_ACTION)
                .param(DataProcessorRequest.COUNTRY_PARAM, country)
                .param(DataProcessorRequest.CURSOR_PARAM,
                        cursor != null ? cursor : "");
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(options);

    }

    /**
     * fixes wrong Types in questionAnswerStore objects. When cleaned data is uploaded using an
     * excel file, the type of the answer is set according to the type of the question, while the
     * device sets the type according to a different convention. The action handles QAS_PAGE_SIZE
     * items in one call, and invokes new tasks as necessary if there are more items.
     *
     * @param cursor
     * @author M.T. Westra
     */
    public static void fixOptions2Values() {
        SurveyInstanceDAO siDao = new SurveyInstanceDAO();
        QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        List<QuestionAnswerStore> qasList = siDao.listQAOptions(null,
                QAS_PAGE_SIZE, "OPTION", "FREE_TEXT", "NUMBER", "SCAN", "PHOTO");
        List<QuestionAnswerStore> qasChangedList = new ArrayList<QuestionAnswerStore>();
        log.log(Level.INFO, "Running fixOptions2Values");
        if (qasList != null) {
            for (QuestionAnswerStore qas : qasList) {
                if (Question.Type.OPTION.toString().equals(qas.getType())
                        || Question.Type.NUMBER.toString()
                                .equals(qas.getType())
                        || Question.Type.FREE_TEXT.toString().equals(
                                qas.getType())
                        || Question.Type.SCAN.toString().equals(qas.getType())) {
                    qas.setType("VALUE");
                    qasChangedList.add(qas);
                } else if (Question.Type.PHOTO.toString().equals(qas.getType())) {
                    qas.setType("IMAGE");
                    qasChangedList.add(qas);
                }
            }
            qasDao.save(qasChangedList);
            // if there are more, invoke another task

            if (qasList.size() == QAS_PAGE_SIZE) {
                log.log(Level.INFO, "invoking another fixOptions task");
                Queue queue = QueueFactory.getDefaultQueue();
                TaskOptions options = TaskOptions.Builder
                        .withUrl("/app_worker/dataprocessor")
                        .param(DataProcessorRequest.ACTION_PARAM,
                                DataProcessorRequest.FIX_OPTIONS2VALUES_ACTION);

                queue.add(options);
            }
        }
    }

    public static void surveyInstanceSummarizer(Long surveyInstanceId,
            Long qasId, Integer delta) {
        SurveyInstanceDAO siDao = new SurveyInstanceDAO();
        QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        boolean success = false;
        if (surveyInstanceId != null) {
            SurveyInstance si = siDao.getByKey(surveyInstanceId);
            if (si != null && qasId != null) {
                QuestionAnswerStore qas = qasDao.getByKey(qasId);
                if (qas != null) {
                    GeoCoordinates geoC = null;
                    if (qas.getValue() != null
                            && qas.getValue().trim().length() > 0) {
                        geoC = GeoCoordinates.extractGeoCoordinate(qas
                                .getValue());
                    }
                    if (geoC != null) {
                        GeoLocationService gisService = new GeoLocationServiceGeonamesImpl();
                        GeoPlace gp = gisService.findDetailedGeoPlace(geoC
                                .getLatitude().toString(), geoC.getLongitude()
                                .toString());
                        if (gp != null) {
                            SurveyInstanceSummaryDao.incrementCount(
                                    gp.getSub1(), gp.getCountryCode(),
                                    qas.getCollectionDate(), delta.intValue());
                            success = true;
                        }
                    }
                }
            }
        }
        if (!success) {
            log.log(Level.SEVERE,
                    "Couldnt find geoplace for instance. Instance id: "
                            + surveyInstanceId);
        }
    }

    /**
     * Adds surveyId and questionGroupId to translations This only needs to happen once to populate
     * the fields on old translation values.
     */
    private void addTranslationFields(String cursor) {
        SurveyDAO sDao = new SurveyDAO();
        QuestionGroupDao qgDao = new QuestionGroupDao();
        QuestionDao qDao = new QuestionDao();
        QuestionOptionDao qoDao = new QuestionOptionDao();
        TranslationDao tDao = new TranslationDao();
        QuestionGroup qg;
        Question qu;
        QuestionOption qo;

        Long surveyId = null;
        Long questionGroupId = null;
        List<Translation> tListSave = new ArrayList<Translation>();

        final List<Translation> results = tDao.listTranslations(T_PAGE_SIZE, cursor);
        for (Translation t : results) {
            surveyId = null;
            questionGroupId = null;
            switch (t.getParentType()) {
                case SURVEY_NAME:
                case SURVEY_DESC:
                    Survey s = sDao.getById(t.getParentId());
                    if (s != null) {
                        surveyId = s.getKey().getId();
                    }
                    break;
                case QUESTION_GROUP_DESC:
                case QUESTION_GROUP_NAME:
                    qg = qgDao.getByKey(t.getParentId());
                    if (qg != null) {
                        surveyId = qg.getSurveyId();
                        questionGroupId = qg.getKey().getId();
                    }
                    break;
                case QUESTION_NAME:
                case QUESTION_DESC:
                case QUESTION_TEXT:
                case QUESTION_TIP:
                    qu = qDao.getByKey(t.getParentId());
                    if (qu != null) {
                        surveyId = qu.getSurveyId();
                        questionGroupId = qu.getQuestionGroupId();
                    }
                    break;
                case QUESTION_OPTION:
                    qo = qoDao.getByKey(t.getParentId());
                    if (qo != null) {
                        Long questionId = qo.getQuestionId();
                        qu = qDao.getByKey(questionId);
                        if (qu != null) {
                            surveyId = qu.getSurveyId();
                            questionGroupId = qu.getQuestionGroupId();
                        }
                    }
                    break;
                default:
                    break;
            }
            t.setSurveyId(surveyId);
            t.setQuestionGroupId(questionGroupId);
            tListSave.add(t);
        }
        tDao.save(tListSave);

        if (results.size() == T_PAGE_SIZE) {
            cursor = TranslationDao.getCursor(results);
            final TaskOptions options = TaskOptions.Builder
                    .withUrl("/app_worker/dataprocessor")
                    .param(DataProcessorRequest.ACTION_PARAM,
                            DataProcessorRequest.ADD_TRANSLATION_FIELDS)
                    .param(DataProcessorRequest.CURSOR_PARAM,
                            cursor != null ? cursor : "");
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(options);
        }
    }

    /**
     * populates the creationSurveyId field for existing locales started from testharness with
     * host/webapp/testharness?action=addCreationSurveyIdToLocale
     *
     * @param cursor
     */
    public static void addCreationSurveyIdToLocale(String cursor) {
        final SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        final SurveyInstanceDAO siDao = new SurveyInstanceDAO();
        final List<SurveyedLocale> results = slDao.listAll(cursor, LOCALE_PAGE_SIZE);

        for (SurveyedLocale sl : results) {
            // make it idempotent
            if (sl.getCreationSurveyId() == null
                    && sl.getLastSurveyalInstanceId() != null) {
                SurveyInstance si = siDao.getByKey(sl.getLastSurveyalInstanceId());
                if (si != null) {
                    sl.setCreationSurveyId(si.getSurveyId());
                    // Ensure the save time is unique. See
                    // https://github.com/akvo/akvo-flow/issues/605
                    slDao.save(sl);
                }
            }
        }

        if (results.size() == LOCALE_PAGE_SIZE) {
            final String cursorParam = SurveyedLocaleDao.getCursor(results);
            final TaskOptions options = TaskOptions.Builder
                    .withUrl("/app_worker/dataprocessor")
                    .param(DataProcessorRequest.ACTION_PARAM,
                            DataProcessorRequest.ADD_CREATION_SURVEY_ID_TO_LOCALE)
                    .param(DataProcessorRequest.CURSOR_PARAM,
                            cursorParam != null ? cursorParam : "");
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(options);
        }
    }

    /**
     * populates the displayName, surveyGroupId, and surveyInstanceContrib, setCreationSurveyId
     * fields for existing locales started from testharness with
     * host/webapp/testharness?action=populateMonitoringFieldsLocale&surveyId=xxxx
     *
     * @param cursor
     * @param surveyId
     */
    @SuppressWarnings("unchecked")
    public static void populateMonitoringFieldsLocale(String cursor,
            Long surveyId, Long surveyedLocaleId) {
        final SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        final SurveyDAO sDao = new SurveyDAO();
        final SurveyInstanceDAO siDao = new SurveyInstanceDAO();
        final QuestionDao qDao = new QuestionDao();
        final QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        
        List<SurveyedLocale> locales = null;
        if (surveyedLocaleId != null) {
            // Single locale update
            locales = new ArrayList<>();
            locales.add(slDao.getById(surveyedLocaleId));
        } else {
            // get locales by createdSurveyId
            locales = slDao.listLocalesByCreationSurvey(surveyId, cursor, LOCALE_PAGE_SIZE);
            log.log(Level.INFO, "found surveyedLocales: " + locales.size());
        }

        Long sIsurveyId;
        String sgKey;
        String qKey;
        Boolean addSl;
        List<Long> qList;
        String displayName;

        // initialize the memcache
        Cache cache = initCache(12 * 60 * 60); // 12 hours

        for (SurveyedLocale sl : locales) {
            try {
                if (sl.getLastSurveyalInstanceId() == null) {
                    log.log(Level.WARNING,
                            "lastSurveyalInstanceId null for locale: "
                                    + sl.getKey().getId());
                    continue;
                }

                SurveyInstance si = siDao.getByKey(sl
                        .getLastSurveyalInstanceId());
                addSl = false;

                if (si == null) {
                    // log an error and continue to next locale
                    log.log(Level.WARNING,
                            "Couldn't find surveyInstance: "
                                    + sl.getLastSurveyalInstanceId());
                    continue;
                }

                // if empty, set surveyInstanceContrib
                if (sl.getSurveyInstanceContrib() == null
                        || sl.getSurveyInstanceContrib().size() == 0) {
                    sl.addContributingSurveyInstance(sl.getLastSurveyalInstanceId());
                    addSl = true;
                }

                // always set surveyGroupId, because it might have changed.
                sIsurveyId = si.getSurveyId(); // get the surveyId from the survey instance
                sgKey = "popMonitoringFields-sISurveyGroupId-" + sIsurveyId; // create the key for
                                                                             // the cache

                if (containsKey(cache, sgKey)) {
                    // get the surveyGroup from the cache
                    sl.setSurveyGroupId((Long) cache.get(sgKey));
                    addSl = true;
                } else {
                    // look it up in the datastore
                    Survey s = sDao.getByKey(sIsurveyId);
                    putObject(cache, sgKey, s.getSurveyGroupId());
                    sl.setSurveyGroupId(s.getSurveyGroupId());
                    addSl = true;
                }

                // always populate the display names, as it might have changed
                qKey = "popMonitoringFields-displayNameQuestions-" + sIsurveyId;
                qList = null;
                if (containsKey(cache, qKey)) {
                    // get the question list from the cache
                    qList = (List<Long>) cache.get(qKey);
                } else {
                    // look it up in the datastore
                    qList = new ArrayList<Long>();
                    List<Question> questions = qDao
                            .listDisplayNameQuestionsBySurveyId(sIsurveyId);
                    if (questions != null) {
                        for (Question q : questions) {
                            qList.add(q.getKey().getId());
                        }
                    }
                    putObject(cache, qKey, qList);
                }
                // for each question, find the corresponding question answer store, and add it to
                // the
                // display name
                if (qList.size() > 0) {
                    displayName = "";
                    for (Long qId : qList) {
                        QuestionAnswerStore qas = qasDao
                                .getByQuestionAndSurveyInstance(qId, si
                                        .getKey().getId());
                        if (qas != null && qas.getValue() != null) {
                            if (displayName.length() > 0) {
                                displayName += " - ";
                            }
                            displayName += qas.getValue().replaceAll("\\s*\\|\\s*", " - ");
                        }
                    }
                    addSl = true;
                    sl.setDisplayName(displayName);
                }

                if (addSl) {
                    // Ensure the save time is unique. See
                    // https://github.com/akvo/akvo-flow/issues/605
                    slDao.save(sl);
                }
            } catch (Exception e) {
                log.log(Level.SEVERE,
                        "Problem while populating monitoring fields: "
                                + e.getMessage(), e);
            }
        }

        if (locales.size() == LOCALE_PAGE_SIZE) {
            final String cursorParam = SurveyedLocaleDao.getCursor(locales);
            final TaskOptions options = TaskOptions.Builder
                    .withUrl("/app_worker/dataprocessor")
                    .header("Host",
                            BackendServiceFactory.getBackendService().getBackendAddress(
                                    "dataprocessor"))
                    .param(DataProcessorRequest.ACTION_PARAM,
                            DataProcessorRequest.POPULATE_MONITORING_FIELDS_LOCALE_ACTION)
                    .param(DataProcessorRequest.SURVEY_ID_PARAM, surveyId.toString())
                    .param(DataProcessorRequest.CURSOR_PARAM,
                            cursorParam != null ? cursorParam : "");
            Queue queue = QueueFactory.getQueue("background-processing");
            queue.add(options);
        }
    }

    /**
     * Creates new identifiers for existing locales. Handle with care! This will overwrite all
     * existing identifiers, which might be used by users. Only use if you are sure that this data
     * has not been used for monitoring already, and that identifiers are not in use. To be
     * conservative, if there is an existing identifier, we reshape it to fit the new style
     * (xxxx-xxxx-xxxx), using the existing identifier. In that way, the method is idempotent.
     * Started from testharness with
     * host/webapp/testharness?action=createNewIdentifiersLocales&surveyId=xxxxxx
     *
     * @param cursor
     * @param surveyId
     */
    private void createNewIdentifiersLocales(String cursor, Long surveyId) {
        final SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        final Pattern localeIdPattern = Pattern.compile(SurveyedLocale.IDENTIFIER_PATTERN);

        // get locales by createdSurveyId
        final List<SurveyedLocale> results = slDao.listLocalesByCreationSurvey(surveyId, cursor,
                LOCALE_PAGE_SIZE);
        log.log(Level.INFO,
                "Found " + results.size() + " surveyedLocales for surveyId=" + surveyId);

        for (SurveyedLocale sl : results) {
            if (sl.getIdentifier() != null) {
                final String identifier = sl.getIdentifier();

                // if the identifier has the shape 'xxxx-xxxx-xxxx', leave it alone
                final Matcher localeIdMatcher = localeIdPattern.matcher(identifier);
                if (localeIdMatcher.matches()) {
                    continue;
                }

                // if it is an old style identifier, based on geolocation, reuse it if possible
                sl.setIdentifier(SurveyedLocale.generateBase32Uuid(identifier));
            } else {
                sl.setIdentifier(SurveyedLocale.generateBase32Uuid());
            }

            // Ensure the save time is unique. See https://github.com/akvo/akvo-flow/issues/605
            slDao.save(sl);
        }

        if (results.size() == LOCALE_PAGE_SIZE) {
            final String cursorParam = SurveyedLocaleDao.getCursor(results);
            final TaskOptions options = TaskOptions.Builder
                    .withUrl("/app_worker/dataprocessor")
                    .header("Host",
                            BackendServiceFactory.getBackendService().getBackendAddress(
                                    "dataprocessor"))
                    .param(DataProcessorRequest.ACTION_PARAM,
                            DataProcessorRequest.CREATE_NEW_IDENTIFIERS_LOCALES_ACTION)
                    .param(DataProcessorRequest.SURVEY_ID_PARAM, surveyId.toString())
                    .param(DataProcessorRequest.CURSOR_PARAM,
                            cursorParam != null ? cursorParam : "");
            Queue queue = QueueFactory.getQueue("background-processing");
            queue.add(options);
        }
    }

    /**
     * runs over all surveyal value objects, and populates: the questionOrder and questionGroupOrder
     * fields, and the surveyId if it is not populated This method is invoked as a URL request:
     * http://..../webapp/testharness?action=populateQuestionOrders with optional parameter surveyId
     *
     * @param cursor
     */
    @SuppressWarnings("unchecked")
    private void populateQuestionOrdersSurveyalValues(Long surveyId, String cursor) {

        final SurveyalValueDao svDao = new SurveyalValueDao();
        List<SurveyalValue> svList = null;

        // get list of surveyalValues, either by surveyId, or all of them
        if (surveyId != null) {
            svList = svDao.listBySurvey(surveyId, cursor, SVAL_PAGE_SIZE);
        } else {
            svList = svDao.list(cursor, SVAL_PAGE_SIZE);
        }

        if (svList == null || svList.size() == 0) {
            return; // nothing to do
        }

        final QuestionDao qDao = new QuestionDao();
        final QuestionGroupDao qgDao = new QuestionGroupDao();
        final List<SurveyalValue> svSaveList = new ArrayList<SurveyalValue>();

        // initialize the memcache
        Cache cache = initCache(12 * 60 * 60); // 12 hours

        for (SurveyalValue sv : svList) {

            Long sId = null;

            // if the surveyQuestionId is not there, skip this surveyalValue
            if (sv.getSurveyQuestionId() != null) {

                final Long sqId = sv.getSurveyQuestionId();
                final String orderKey = "q-order-" + sqId;

                // get orders from the cache
                if (containsKey(cache, orderKey)) {
                    final Map<String, Object> orderMap = (Map<String, Object>) cache.get(orderKey);
                    sv.setQuestionOrder((Integer) orderMap.get("q-order"));
                    sv.setQuestionGroupOrder((Integer) orderMap.get("qg-order"));
                    sId = (Long) orderMap.get("q-survey-id");
                } else {
                    // get orders from the datastore
                    final Question q = qDao.getByKey(sqId);
                    final QuestionGroup qg = q != null && q.getQuestionGroupId() != null ? qgDao
                            .getByKey(q.getQuestionGroupId()) : null;

                    if (q != null) {
                        sv.setQuestionOrder(q.getOrder());
                        sId = q.getSurveyId();
                    }

                    if (qg != null) {
                        sv.setQuestionGroupOrder(qg.getOrder());
                    }

                    // put it in the cache for further reference
                    final Map<String, Object> v = new HashMap<String, Object>();
                    v.put("q-order", sv.getQuestionOrder());
                    v.put("qg-order", sv.getQuestionGroupOrder());
                    v.put("q-survey-id", sv.getSurveyId());
                    putObject(cache, orderKey, v);
                }
                // if the surveyId field of the surveyalValue has not been
                // populated, do it now.
                if (sv.getSurveyId() == null) {
                    sv.setSurveyId(sId);
                }

                svSaveList.add(sv);
            }
        }

        svDao.save(svSaveList);

        if (svList.size() == SVAL_PAGE_SIZE) {
            final Queue queue = QueueFactory.getDefaultQueue();
            final String newCursor = SurveyalValueDao.getCursor(svList);
            final TaskOptions to = TaskOptions.Builder
                    .withUrl("/app_worker/dataprocessor")
                    .param(DataProcessorRequest.ACTION_PARAM,
                            DataProcessorRequest.POP_QUESTION_ORDER_FIELDS_ACTION)
                    .param("cursor", newCursor)
                    .header("host",
                            BackendServiceFactory.getBackendService().getBackendAddress(
                                    "dataprocessor"));

            if (surveyId != null) {
                to.param(DataProcessorRequest.SURVEY_ID_PARAM, surveyId.toString());
            }
            queue.add(to);
        }
    }

    /**
     * Delete the specified survey instance
     *
     * @param surveyInstanceId
     */
    private void deleteSurveyResponses(Long surveyInstanceId) {
        siDao = new SurveyInstanceDAO();
        SurveyInstance surveyInstance = siDao.getByKey(surveyInstanceId);
        if (surveyInstance != null) {
            siDao.deleteSurveyInstance(surveyInstance);
        }
    }

    /**
     * Update a survey response counter according to the provided delta. This method should be
     * invoked though the task queue 'surveyResponseCount' to avoid concurrent updates
     *
     * @param summaryCounterId
     * @param delta
     */
    private void updateSurveyResponseCounter(long summaryCounterId, int delta) {
        SurveyQuestionSummaryDao summaryDao = new SurveyQuestionSummaryDao();
        SurveyQuestionSummary summary = summaryDao.getByKey(summaryCounterId);
        if (summary == null) {
            return;
        }

        summary.setCount(summary.getCount() + delta);
        summaryDao.save(summary);
    }

    private void deleteCascadeNodes(Long cascadeResourceId, Long parentNodeId) {
        final CascadeNodeDao dao = new CascadeNodeDao();
        List<CascadeNode> nodes = dao.listCascadeNodesByResourceAndParentId(cascadeResourceId,
                parentNodeId == null ? 0l : parentNodeId);

        if (nodes.isEmpty()) {
            return;
        }

        if (!areLeafNodes(dao, cascadeResourceId, nodes)) {
            for (CascadeNode node : nodes) {
                scheduleCascadeNodeDeletion(cascadeResourceId, node.getKey().getId());
            }
        }

        dao.delete(nodes);
    }

    private boolean areLeafNodes(CascadeNodeDao dao, Long cascadeResourceId,
            List<CascadeNode> nodes) {
        CascadeNode firstNode = nodes.get(0);
        List<CascadeNode> childNodes = dao.listCascadeNodesByResourceAndParentId(
                cascadeResourceId, firstNode.getKey().getId());
        return childNodes.size() == 0;
    }

    private void scheduleCascadeNodeDeletion(Long cascadeResourceId, Long parentNodeId) {
        try {
            final TaskOptions options = TaskOptions.Builder
                    .withUrl("/app_worker/dataprocessor")
                    .header("Host",
                            BackendServiceFactory.getBackendService()
                                    .getBackendAddress("dataprocessor"))
                    .param(DataProcessorRequest.ACTION_PARAM,
                            DataProcessorRequest.DELETE_CASCADE_NODES)
                    .param(DataProcessorRequest.CASCADE_RESOURCE_ID,
                            cascadeResourceId.toString())
                    .param(DataProcessorRequest.PARENT_NODE_ID, parentNodeId.toString());
            final Queue queue = QueueFactory.getQueue("background-processing");
            queue.add(options);
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    String.format(
                            "Error scheduling Cascade Node deletion - cascadeResourceId: %s - parentNodeId: %s",
                            cascadeResourceId, parentNodeId), e);
        }
    }
    
    private void assembleDatapointName(Long surveyGroupId, Long surveyedLocaleId) {
        if (surveyedLocaleId == null && surveyGroupId == null) {
            log.log(Level.WARNING, "Either surveyGroupId or surveyedLocaleId must be defined");
            return;
        }
        
        final SurveyGroupDAO sgDao = new SurveyGroupDAO();
        final SurveyedLocaleDao slDao = new SurveyedLocaleDao();
        final SurveyInstanceDAO siDao = new SurveyInstanceDAO();
        final QuestionDao qDao = new QuestionDao();
        final QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
        
        List<SurveyedLocale> locales = null;
        if (surveyedLocaleId != null) {
            // Single locale update
            SurveyedLocale sl = slDao.getById(surveyedLocaleId);
            if (sl == null) {
                log.log(Level.WARNING, "SurveyedLocale not found: " + surveyedLocaleId);
                return;
            }
            surveyGroupId = sl.getSurveyGroupId();
            locales = new ArrayList<>();
            locales.add(slDao.getById(surveyedLocaleId));
        } else {
            // Fetch all locales for this survey group
            locales = slDao.listLocalesBySurveyGroupId(surveyGroupId);
        }
        
        SurveyGroup sg = sgDao.getByKey(surveyGroupId);
        if (sg == null || sg.getNewLocaleSurveyId() == null) {
            log.log(Level.WARNING, "SurveyGroup or registration form not found: " + surveyGroupId);
            return;
        }

        List<Question> nameQuestions = qDao.listDisplayNameQuestionsBySurveyId(sg.getNewLocaleSurveyId());
        
        for (SurveyedLocale sl : locales) {
            try {
                SurveyInstance si = siDao.getRegistrationSurveyInstance(sl, sg.getNewLocaleSurveyId());
                if (si == null) {
                    log.log(Level.WARNING, "Null registartion SurveyInstance for locale: " + sl.getKey().getId());
                    continue;
                }
                
                List<QuestionAnswerStore> responses = qasDao.listBySurveyInstance(si.getKey().getId());
                sl.assembleDisplayName(nameQuestions, responses);
                log.info("Reassembled display name for SurveyedLocale : " + 
                            sl.getKey().getId() + ": " + sl.getDisplayName());
                slDao.save(sl);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Problem while assembling datapoint name: " + e.getMessage(), e);
            }
        }
    }
    
    public static void scheduleDatapointNameAssembly(Long surveyGroupId, Long surveyedLocaleId) {
        scheduleDatapointNameAssembly(surveyGroupId, surveyedLocaleId, false);
    }
    
    public static void scheduleDatapointNameAssembly(Long surveyGroupId, Long surveyedLocaleId, boolean delay) {
        log.info("Scheduling name assembly for survey group, locale - " + surveyGroupId +", " + surveyedLocaleId);
        final TaskOptions options = TaskOptions.Builder
                .withUrl("/app_worker/dataprocessor")
                .header("Host", BackendServiceFactory.getBackendService().getBackendAddress("dataprocessor"))
                .param(DataProcessorRequest.ACTION_PARAM, DataProcessorRequest.ASSEMBLE_DATAPOINT_NAME);
        
        if (delay) {
            options.countdownMillis(NAME_ASSEMBLY_TASK_DELAY);
        }
        if (surveyGroupId != null) {
            options.param(DataProcessorRequest.SURVEY_GROUP_PARAM, String.valueOf(surveyGroupId));
        }
        if (surveyedLocaleId != null) {
            options.param(DataProcessorRequest.LOCALE_ID_PARAM, String.valueOf(surveyedLocaleId));
        }
        com.google.appengine.api.taskqueue.Queue queue = com.google.appengine.api.taskqueue.QueueFactory
                .getQueue("background-processing");
        queue.add(options);
    }

}
