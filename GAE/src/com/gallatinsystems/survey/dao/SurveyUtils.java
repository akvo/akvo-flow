/*
 *  Copyright (C) 2013-2017 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.survey.dao;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.akvo.flow.domain.SecuredObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.HttpUtil;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.survey.domain.CascadeResource;
import com.gallatinsystems.survey.domain.CascadeResource.Status;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.survey.domain.Translation.ParentType;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * @author stellan
 *
 */
public class SurveyUtils {

    private static final Logger log = Logger.getLogger(SurveyUtils.class.getName());

    public static Survey copySurvey(Survey source, SurveyDto dto) {

        final SurveyDAO sDao = new SurveyDAO();
        final Survey tmp = new Survey();

        BeanUtils.copyProperties(source, tmp, Constants.EXCLUDED_PROPERTIES);
        // set name and surveyGroupId to values we got from the dashboard
        tmp.setCode(dto.getCode());
        tmp.setName(dto.getName());
        tmp.setSurveyGroupId(dto.getSurveyGroupId());

        tmp.setStatus(Survey.Status.COPYING);
        tmp.setPath(getPath(tmp));
        tmp.setVersion(Double.valueOf("1.0"));

        log.log(Level.INFO, "Copying `Survey` " + source.getKey().getId());
        final Survey newSurvey = sDao.save(tmp);

        log.log(Level.INFO, "New `Survey` ID: " + newSurvey.getKey().getId());

        SurveyUtils.copyTranslation(source.getKey().getId(), newSurvey.getKey()
                .getId(), newSurvey.getKey().getId(), null, ParentType.SURVEY_NAME,
                ParentType.SURVEY_DESC);

        log.log(Level.INFO, "Running rest of copy functionality as a task...");

        final Queue queue = QueueFactory.getDefaultQueue();

        final TaskOptions options = TaskOptions.Builder
                .withUrl("/app_worker/dataprocessor")
                .param(DataProcessorRequest.ACTION_PARAM,
                        DataProcessorRequest.COPY_SURVEY)
                .param(DataProcessorRequest.SURVEY_ID_PARAM,
                        String.valueOf(newSurvey.getKey().getId()))
                .param(DataProcessorRequest.SOURCE_PARAM,
                        String.valueOf(source.getKey().getId()))
                .header("Host",
                        BackendServiceFactory.getBackendService()
                                .getBackendAddress("dataprocessor"));

        queue.add(options);

        return newSurvey;
    }

    
    /**
     * @param sourceGroup
     * @param copyGroup
     * @param newSurveyId
     * @param qDependencyResolutionMap
     * @return
     * 
     * copies a question group to another survey or within the same survey (which risks creating duplicated question ids).
     */
    public static QuestionGroup copyQuestionGroup(QuestionGroup sourceGroup,
            QuestionGroup copyGroup, Long newSurveyId, Map<Long, Long> qDependencyResolutionMap, Set<String> idsInUse) {

        final QuestionDao qDao = new QuestionDao();
        final Long sourceGroupId = sourceGroup.getKey().getId();
        final Long copyGroupId = copyGroup.getKey().getId();

        SurveyUtils.copyTranslation(sourceGroupId, copyGroupId, newSurveyId, copyGroupId,
                ParentType.QUESTION_GROUP_NAME, ParentType.QUESTION_GROUP_DESC);

        List<Question> qList = qDao.listQuestionsInOrderForGroup(sourceGroupId);

        if (qList == null) {
            return copyGroup;
        }

        log.log(Level.INFO, "Copying " + qList.size() + " `Question`");

        int qCount = 1;
        List<Question> qCopyList = new ArrayList<Question>();
        for (Question question : qList) {
            final Question questionCopy = SurveyUtils.copyQuestion(question, copyGroupId, qCount++,
                    newSurveyId, idsInUse);
            qCopyList.add(questionCopy);
        }

        if (qDependencyResolutionMap == null) {
            return copyGroup;
        }

        // fixing dependencies
        final List<Question> dependentQuestionList = new ArrayList<Question>();
        for (Question questionCopy : qCopyList) {
            qDependencyResolutionMap.put(questionCopy.getSourceQuestionId(), questionCopy.getKey()
                    .getId());
            if (questionCopy.getDependentFlag() == null || !questionCopy.getDependentFlag()) {
                continue;
            }
            Long originalDependentId = questionCopy.getDependentQuestionId();
            questionCopy.setDependentQuestionId(qDependencyResolutionMap.get(originalDependentId));
            dependentQuestionList.add(questionCopy);
        }
        qDao.save(dependentQuestionList);

        log.log(Level.INFO, "Resolved dependencies for " + dependentQuestionList.size()
                + " `Question`");

        return copyGroup;
    }

    /**
     * @param source
     * @param newQuestionGroupId
     * @param order
     * @param newSurveyId
     * @param idsInUse the set of all questionIds in use anywhere in the survey group
     * @return the new question
     * 
     * copies one question, ensuring that it has a unique questionId
     */
    public static Question copyQuestion(Question source,
            Long newQuestionGroupId, Integer order, Long newSurveyId, Set<String> idsInUse) {

        final QuestionDao qDao = new QuestionDao();
        final QuestionOptionDao qoDao = new QuestionOptionDao();
        final Question tmp = new Question();
        final Long sourceQuestionId = source.getKey().getId();

        final String[] questionExcludedProps = {
                "questionOptionMap",
                "questionHelpMediaMap", "scoringRules", "translationMap",
                "order", "questionId"
        };

        final String[] allExcludedProps = (String[]) ArrayUtils.addAll(
                questionExcludedProps, Constants.EXCLUDED_PROPERTIES);

        log.log(Level.INFO, "Copying `Question` " + sourceQuestionId);

        BeanUtils.copyProperties(source, tmp, allExcludedProps);
        tmp.setOrder(order);
        tmp.setSourceQuestionId(sourceQuestionId);

        if (source.getQuestionId() != null) {
            if (idsInUse != null) { //must avoid these
                String newId = source.getQuestionId() + "_1";
                int index = 2;
                while  (idsInUse.contains(newId)) {
                    newId = source.getQuestionId() + "_" + index++;
                }
                tmp.setQuestionId(newId);
                //one more to avoid
                idsInUse.add(newId);
                log.log(Level.FINE, "Changing QuestionId from " + source.getQuestionId() + " to " + newId);
            } else {
                tmp.setQuestionId(source.getQuestionId());
                log.log(Level.FINE, "Keeping QuestionId " + source.getQuestionId());
            }
        }

        final Question newQuestion = qDao.save(tmp, newQuestionGroupId);

        log.log(Level.FINE, "New `Question` ID: "
                + newQuestion.getKey().getId());

        log.log(Level.FINE, "Copying question translations");

        SurveyUtils.copyTranslation(sourceQuestionId, newQuestion
                .getKey().getId(), newSurveyId, newQuestionGroupId, ParentType.QUESTION_NAME,
                ParentType.QUESTION_DESC, ParentType.QUESTION_TEXT,
                ParentType.QUESTION_TIP);

        if (!Question.Type.OPTION.equals(newQuestion.getType())) {
            // Nothing more to do
            return newQuestion;
        }

        final TreeMap<Integer, QuestionOption> options = qoDao
                .listOptionByQuestion(sourceQuestionId);

        if (options == null) {
            return newQuestion;
        }

        log.log(Level.FINE, "Copying " + options.values().size()
                + " `QuestionOption`");

        // Copying Question Options
        for (QuestionOption qo : options.values()) {
            SurveyUtils.copyQuestionOption(qo, newQuestion.getKey().getId(), newSurveyId,
                    newQuestionGroupId);
        }

        return newQuestion;
    }

    public static QuestionOption copyQuestionOption(QuestionOption source,
            Long newQuestionId, Long newSurveyId, Long newQuestionGroupId) {

        final QuestionOptionDao qDao = new QuestionOptionDao();
        final QuestionOption tmp = new QuestionOption();

        BeanUtils.copyProperties(source, tmp, Constants.EXCLUDED_PROPERTIES);
        tmp.setQuestionId(newQuestionId);

        log.log(Level.INFO, "Copying `QuestionOption` "
                + source.getKey().getId());

        final QuestionOption newQuestionOption = qDao.save(tmp);

        log.log(Level.INFO, "New `QuestionOption` ID: "
                + newQuestionOption.getKey().getId());

        log.log(Level.INFO, "Copying question option translations");

        SurveyUtils.copyTranslation(source.getKey().getId(), newQuestionOption
                .getKey().getId(), newSurveyId, newQuestionGroupId, ParentType.QUESTION_OPTION);

        return newQuestionOption;
    }

    public static Survey resetSurveyState(Long surveyId) {
        final SurveyDAO sDao = new SurveyDAO();
        final Survey s = sDao.getById(surveyId);
        s.setStatus(Survey.Status.NOT_PUBLISHED);
        return sDao.save(s);
    }

    public static Survey retrieveSurvey(Long surveyId) {
        final SurveyDAO sDao = new SurveyDAO();
        return sDao.getById(surveyId);
    }

    public static SurveyGroup retrieveSurveyGroup(Long surveyGroupId) {
        final SurveyGroupDAO surveyGroupDAO = new SurveyGroupDAO();
        return surveyGroupDAO.getByKey(surveyGroupId);
    }

    public static String getPath(Survey s) {
        if (s == null) {
            return null;
        }

        final SurveyGroupDAO dao = new SurveyGroupDAO();
        final SurveyGroup sg = dao.getByKey(s.getSurveyGroupId());

        if (sg == null) {
            return null;
        }

        return sg.getPath() + "/" + s.getName();
    }

    public static List<Long> retrieveAncestorIds(SecuredObject s) {
        List<Long> ancestorIds = new ArrayList<Long>();
        if (s.getParentObject() == null) {
            return null;
        }

        SecuredObject parent = s.getParentObject();

        if (parent.listAncestorIds() != null) {
            ancestorIds.addAll(parent.listAncestorIds());
        }
        ancestorIds.add(parent.getObjectId()); // add parent id to returned ancestor list

        return ancestorIds;
    }

    public static String fixPath(String oldPath, String newName) {
        if (oldPath == null || newName == null) {
            return oldPath;
        }
        int idx = oldPath.lastIndexOf("/");
        if (idx >= 0) {
            return oldPath.substring(0, idx) + "/" + newName;
        }
        return oldPath;
    }

    public static List<Translation> getTranslations(Long parentId,
            ParentType... types) {
        final List<Translation> trs = new ArrayList<Translation>();
        final TranslationDao trDao = new TranslationDao();
        for (ParentType pt : types) {
            trs.addAll(trDao.findTranslations(pt, parentId).values());
        }
        return trs;
    }

    public static void saveTranslationCopy(List<Translation> trs,
            Long newParentId, Long newSurveyId, Long newQuestionGroupId) {
        final TranslationDao trDao = new TranslationDao();
        for (Translation t : trs) {
            Translation copy = new Translation();
            BeanUtils.copyProperties(t, copy, Constants.EXCLUDED_PROPERTIES);
            copy.setParentId(newParentId);
            copy.setQuestionGroupId(newQuestionGroupId);
            copy.setSurveyId(newSurveyId);
            trDao.save(copy);
        }
    }

    public static void copyTranslation(Long sourceParentId, Long copyParentId,
            Long newSurveyId, Long newQuestionGroupId, ParentType... types) {
        SurveyUtils.saveTranslationCopy(
                SurveyUtils.getTranslations(sourceParentId, types),
                copyParentId, newSurveyId, newQuestionGroupId);
    }

    /**
     * Sends a POST request to publish a cascade resource to a server defined by the `flowServices`
     * property
     *
     * @param cascadeResourceId The id of the cascade resource to publish
     * @return "failed" or "publishing requested", depending on the success.
     */
    public static String publishCascade(Long cascadeResourceId) {
        String status = "failed";
        CascadeResourceDao crDao = new CascadeResourceDao();
        CascadeResource cr = crDao.getByKey(cascadeResourceId);
        if (cr != null) {
            final String flowServiceURL = PropertyUtil.getProperty("flowServices");
            final String uploadUrl = PropertyUtil.getProperty("surveyuploadurl");

            if (flowServiceURL == null || "".equals(flowServiceURL)) {
                log.log(Level.SEVERE,
                        "Error trying to publish cascade. Check `flowServices` property");
                return status;
            }

            try {
                final JSONObject payload = new JSONObject();
                payload.put("cascadeResourceId", cascadeResourceId.toString());
                payload.put("uploadUrl", uploadUrl);
                payload.put("version", String.valueOf(cr.getVersion() + 1));

                log.log(Level.INFO, "Sending cascade publish request for cascade: "
                        + cascadeResourceId);

                final String postString = payload.toString();
                log.log(Level.INFO, "POSTing to: " + flowServiceURL);

                final String response = new String(HttpUtil.doPost(flowServiceURL
                        + "/publish_cascade", postString, "application/json"), "UTF-8");

                log.log(Level.INFO, "Response from server: " + response);
                status = "publish requested";
                cr.setStatus(Status.PUBLISHING);
                crDao.save(cr);
            } catch (Exception e) {
                log.log(Level.SEVERE,
                        "Error publishing cascade: " + e.getMessage(), e);
            }
        }
        return status;
    }

    /**
     * Sends a POST request of a collection of surveyIds to a server defined by the `flowServices`
     * property The property `alias` define the baseURL property that is sent in the request
     *
     * @param surveyIds Collection of ids (Long) that requires processing
     * @param action A string indicating the action that will be used, this string is used for
     *            building the URL, with the `flowServices` property + / + action
     * @return The response from the server or null when `flowServices` is not defined, or an error
     *         in the request happens
     */
    public static String notifyReportService(Collection<Long> surveyIds,
            String action) {
        final String flowServiceURL = PropertyUtil.getProperty("flowServices");
        final String baseURL = PropertyUtil.getProperty("alias");

        if (flowServiceURL == null || "".equals(flowServiceURL)) {
            log.log(Level.SEVERE,
                    "Error trying to notify server. It's not configured, check `flowServices` property");
            return null;
        }

        try {

            final JSONObject payload = new JSONObject();
            payload.put("surveyIds", surveyIds);
            payload.put("baseURL", (baseURL.startsWith("http") ? baseURL
                    : "http://" + baseURL));

            log.log(Level.INFO, "Sending notification (" + action
                    + ") for surveys: " + surveyIds);

            final String postString = "criteria="
                    + URLEncoder.encode(payload.toString(), "UTF-8");

            log.log(Level.FINE, "POST string: " + postString);

            final String response = new String(HttpUtil.doPost(flowServiceURL
                    + "/" + action, postString), "UTF-8");

            log.log(Level.INFO, "Response from server: " + response);

            return response;
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    "Error notifying the report service: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Given the path of an object, return a list of the paths of all its parent objects
     *
     * @param objectPath the path of an object
     * @param includeRootPath include the root path in the list of parent paths
     * @return
     */
    public static List<String> listParentPaths(String objectPath, boolean includeRootPath) {
        List<String> parentPaths = new ArrayList<String>();
        StringBuilder path = new StringBuilder(objectPath);
        while (path.length() > 1) {
            path.delete(path.lastIndexOf("/"), path.length());
            if (StringUtils.isNotBlank(path.toString())) {
                parentPaths.add(path.toString().trim());
            }
        }

        if (includeRootPath) {
            parentPaths.add("/");
        }

        return parentPaths;
    }

    /**
     * Copy publicly visible properties from one BaseDomain subclass to another. Should be used for
     * two classes of the same type.
     *
     * @param source
     * @param copy
     */
    public static void shallowCopy(BaseDomain source, BaseDomain copy) {
        BeanUtils.copyProperties(source, copy, Constants.EXCLUDED_PROPERTIES);
        String kind = source.getKey().getKind();
        log.log(Level.INFO, "Copying `" + kind + "` " + source.getKey().getId());
    }

    /**
     * Set the non-persistent child objects of a SurveyGroup entity
     *
     * @param surveyGroup
     */
    public static void setChildObjects(SurveyGroup surveyGroup) {
        if (surveyGroup == null || surveyGroup.getKey() == null) {
            return;
        }

        Long surveyGroupId = surveyGroup.getKey().getId();

        List<SurveyGroup> childFolders = new SurveyGroupDAO().listByProjectFolderId(surveyGroupId);
        surveyGroup.setChildFolders(childFolders);

        List<Survey> childForms = new SurveyDAO().listSurveysByGroup(surveyGroupId);
        surveyGroup.setChildForms(childForms);
    }
    
    /**
     * to prevent collisions, it is useful to collect all ids already in use in a survey group
     * @param surveyId
     * @return
     */
    public static Set<String> listQuestionIdsUsedInSurveyGroup(Long surveyId) {
        final SurveyDAO sDao = new SurveyDAO();
        final QuestionDao qDao = new QuestionDao();
        Set<String> idsInUse = new HashSet<>();        

        Survey s0 = sDao.getById(surveyId);
        final Long surveyGroupId = s0.getSurveyGroupId();
        List<Survey> sList = sDao.listSurveysByGroup(surveyGroupId);
        for (Survey s : sList) {
            List<Question> qList = qDao.listQuestionsBySurvey(s.getKey().getId());
            for (Question q : qList) {
                idsInUse.add(q.getQuestionId());
            }
        }

        return idsInUse;
    }
}
