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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.HttpUtil;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;

public class SurveyUtils {

	private static final Logger log = Logger.getLogger(SurveyUtils.class
			.getName());

	public static Survey copySurvey(Survey source) {

		final SurveyDAO sDao = new SurveyDAO();
		final Survey tmp = new Survey();
		final QuestionGroupDao qgDao = new QuestionGroupDao();

		BeanUtils.copyProperties(source, tmp, Constants.EXCLUDED_PROPERTIES);
		tmp.setCode(tmp.getCode() + " <Copy>"); // FIXME: I18N
		tmp.setName(tmp.getName() + " <Copy>"); // FIXME: I18N
		tmp.setStatus(Survey.Status.NOT_PUBLISHED);
		tmp.setPath(getPath(tmp));

		log.log(Level.INFO, "Copying `Survey` " + source.getKey().getId());
		final Survey newSurvey = sDao.save(tmp);

		log.log(Level.INFO, "New `Survey` ID: " + newSurvey.getKey().getId());

		final List<QuestionGroup> qgList = qgDao
				.listQuestionGroupBySurvey(source.getKey().getId());

		if (qgList == null) {
			return newSurvey;
		}

		log.log(Level.INFO, "Copying " + qgList.size() + " `QuestionGroup`");
		int qgOrder = 1;
		for (final QuestionGroup sourceQG : qgList) {
			SurveyUtils.copyQuestionGroup(sourceQG, newSurvey.getKey().getId(),
					qgOrder++);
		}

		return newSurvey;
	}

	public static QuestionGroup copyQuestionGroup(QuestionGroup source,
			Long newSurveyId, Integer order) {

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

		List<Question> qList = qDao.listQuestionsInOrderForGroup(source
				.getKey().getId());

		if (qList == null) {
			return newQuestionGroup;
		}

		log.log(Level.INFO, "Copying " + qList.size() + " `Question`");

		final Map<Long, Long> qMap = new HashMap<Long, Long>();
		final List<Question> newQuestionList = new ArrayList<Question>();

		int qCount = 1;
		for (Question q : qList) {
			final Question qTmp = SurveyUtils.copyQuestion(q, newQuestionGroup
					.getKey().getId(), qCount++);
			if (qTmp.getDependentFlag()) {
				qMap.put(q.getKey().getId(), qTmp.getKey().getId());
				newQuestionList.add(qTmp);
			}
		}

		// fixing dependencies

		log.log(Level.INFO, "Fixing dependencies for " + newQuestionList.size()
				+ " `Question`");

		for (Question nQ : newQuestionList) {
			nQ.setDependentQuestionId(qMap.get(nQ.getDependentQuestionId()));
		}

		qDao.save(newQuestionList);

		return newQuestionGroup;
	}

	public static Question copyQuestion(Question source,
			Long newQuestionGroupId, Integer order) {

		final QuestionDao qDao = new QuestionDao();
		final QuestionOptionDao qoDao = new QuestionOptionDao();
		final Question tmp = new Question();

		final String[] questionExcludedProps = { "questionOptionMap",
				"questionHelpMediaMap", "scoringRules", "translationMap" };

		final String[] allExcludedProps = (String[]) ArrayUtils.addAll(
				questionExcludedProps, Constants.EXCLUDED_PROPERTIES);

		BeanUtils.copyProperties(source, tmp, allExcludedProps);

		log.log(Level.INFO, "Copying `Question` " + source.getKey().getId());

		final Question newQuestion = qDao.save(tmp, newQuestionGroupId);

		log.log(Level.INFO, "New `Question` ID: "
				+ newQuestion.getKey().getId());

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
			SurveyUtils.copyQuestionOption(qo, newQuestion.getKey().getId());
		}

		return newQuestion;
	}

	public static QuestionOption copyQuestionOption(QuestionOption source,
			Long newQuestionId) {

		final QuestionOptionDao qDao = new QuestionOptionDao();
		final QuestionOption tmp = new QuestionOption();

		BeanUtils.copyProperties(source, tmp, Constants.EXCLUDED_PROPERTIES);
		tmp.setQuestionId(newQuestionId);

		log.log(Level.INFO, "Copying `QuestionOption` "
				+ source.getKey().getId());

		final QuestionOption newQuestionOption = qDao.save(tmp);

		log.log(Level.INFO, "New `QuestionOption` ID: "
				+ newQuestionOption.getKey().getId());

		return newQuestionOption;
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

	public static String notifyReportService(Collection<Long> surveyIds,
			String action) {
		final String reportServiceURL = PropertyUtil
				.getProperty("reportService");

		if (reportServiceURL == null || "".equals(reportServiceURL)) {
			log.log(Level.SEVERE,
					"Error trying to notify server. It's not configured, check `reportService` property");
			return null;
		}

		try {
			final JSONObject payload = new JSONObject();
			payload.put("surveyIds", surveyIds);
			log.log(Level.INFO, "Sending notification (" + action
					+ ") for surveys: " + surveyIds);
			final String response = new String(HttpUtil.doPost(reportServiceURL
					+ "/" + action, payload.toString(), "application/json"),
					"UTF-8");
			log.log(Level.INFO, "Response from server: " + response);
			return response;
		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Error notifying the report service: " + e.getMessage(), e);
		}
		return null;
	}

}
