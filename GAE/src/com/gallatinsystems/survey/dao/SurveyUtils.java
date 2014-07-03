/*
 *  Copyright (C) 2013 Stichting Akvo (Akvo Foundation)
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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.HttpUtil;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.survey.domain.Translation.ParentType;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class SurveyUtils {

    private static final Logger log = Logger.getLogger(SurveyUtils.class
            .getName());

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
                        String.valueOf(source.getKey().getId()));

        queue.add(options);

        return newSurvey;
    }

    public static QuestionGroup copyQuestionGroup(QuestionGroup source,
            Long newSurveyId, Integer order, Map<Long, Long> qMap) {

        final QuestionGroupDao qgDao = new QuestionGroupDao();
        final QuestionDao qDao = new QuestionDao();
        final QuestionGroup tmp = new QuestionGroup();

        BeanUtils.copyProperties(source, tmp, Constants.EXCLUDED_PROPERTIES);
        tmp.setSurveyId(null); // reset parent SurveyId, it will get set by the
                               // save action

        log.log(Level.INFO, "Copying `QuestionGroup` "
                + source.getKey().getId());

        final QuestionGroup newQuestionGroup = qgDao.save(tmp, newSurveyId,
                order);

        log.log(Level.INFO, "New `QuestionGroup` ID: "
                + newQuestionGroup.getKey().getId());

        SurveyUtils.copyTranslation(source.getKey().getId(), newQuestionGroup
                .getKey().getId(), newSurveyId, newQuestionGroup.getKey().getId(),
                ParentType.QUESTION_GROUP_NAME,
                ParentType.QUESTION_GROUP_DESC);

        List<Question> qList = qDao.listQuestionsInOrderForGroup(source
                .getKey().getId());

        if (qList == null) {
            return newQuestionGroup;
        }

        log.log(Level.INFO, "Copying " + qList.size() + " `Question`");

        final List<Question> dependentQuestionList = new ArrayList<Question>();

        int qCount = 1;
        for (Question q : qList) {
            final Question qTmp = SurveyUtils.copyQuestion(q, newQuestionGroup
                    .getKey().getId(), qCount++, newSurveyId);
            qMap.put(q.getKey().getId(), qTmp.getKey().getId());
            if (qTmp.getDependentFlag() != null && qTmp.getDependentFlag()) {
                dependentQuestionList.add(qTmp);
            }
        }

        // fixing dependencies

        log.log(Level.INFO,
                "Fixing dependencies for " + dependentQuestionList.size()
                        + " `Question`");

        for (Question nQ : dependentQuestionList) {
            nQ.setDependentQuestionId(qMap.get(nQ.getDependentQuestionId()));
        }

        qDao.save(dependentQuestionList);

        return newQuestionGroup;
    }

    public static Question copyQuestion(Question source,
            Long newQuestionGroupId, Integer order, Long newSurveyId) {

        final QuestionDao qDao = new QuestionDao();
        final QuestionOptionDao qoDao = new QuestionOptionDao();
        final Question tmp = new Question();

        final String[] questionExcludedProps = {
                "questionOptionMap",
                "questionHelpMediaMap", "scoringRules", "translationMap",
                "order"
        };

        final String[] allExcludedProps = (String[]) ArrayUtils.addAll(
                questionExcludedProps, Constants.EXCLUDED_PROPERTIES);

        BeanUtils.copyProperties(source, tmp, allExcludedProps);
        tmp.setOrder(order);
        tmp.setSourceId(source.getKey().getId());
        log.log(Level.INFO, "Copying `Question` " + source.getKey().getId());

        final Question newQuestion = qDao.save(tmp, newQuestionGroupId);

        log.log(Level.INFO, "New `Question` ID: "
                + newQuestion.getKey().getId());

        log.log(Level.INFO, "Copying question translations");

        SurveyUtils.copyTranslation(source.getKey().getId(), newQuestion
                .getKey().getId(), newSurveyId, newQuestionGroupId, ParentType.QUESTION_NAME,
                ParentType.QUESTION_DESC, ParentType.QUESTION_TEXT,
                ParentType.QUESTION_TIP);

        if (!Question.Type.OPTION.equals(newQuestion.getType())) {
            // Nothing more to do
            return newQuestion;
        }

        final TreeMap<Integer, QuestionOption> options = qoDao
                .listOptionByQuestion(source.getKey().getId());

        if (options == null) {
            return newQuestion;
        }

        log.log(Level.INFO, "Copying " + options.values().size()
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

    private static String getPath(Survey s) {
        if (s == null) {
            return null;
        }

        final SurveyGroupDAO dao = new SurveyGroupDAO();
        final SurveyGroup sg = dao.getByKey(s.getSurveyGroupId());

        if (sg == null) {
            return null;
        }

        return sg.getName() + "/" + s.getName();
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
}
