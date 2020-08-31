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

package com.gallatinsystems.survey.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.domain.SurveyQuestion;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.exceptions.IllegalDeletionException;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.google.appengine.api.datastore.Key;

/**
 * Dao for manipulating survey objects
 */
public class SurveyDAO extends BaseDAO<Survey> {
    private static final Logger log = Logger.getLogger(SurveyDAO.class
            .getName());
    private QuestionGroupDao questionGroupDao;

    public SurveyDAO() {
        super(Survey.class);
        questionGroupDao = new QuestionGroupDao();
    }

    public SurveyGroup save(SurveyGroup surveyGroup) {
        return super.save(surveyGroup);
    }

    public Survey save(Survey survey, Key surveyGroupKey) {
        survey = super.save(survey);

        return survey;
    }

    public Survey getById(Long key) {
        return super.getByKey(key);
    }

    @Override
    public Survey getByKey(Key key) {
        return super.getByKey(key);
    }

    /**
     * loads a full form object (whole object tree, including questions).
     * This method takes time; can only be called reliably from a background task.
     * TODO: move to SurveyAssemblyServlet?
     *
     * @param formId
     * @return the form tree
     */
    public Survey loadFullForm(Long formId) {
        return loadFullFormIncludingQuestionOptions(formId, true);
    }

    public Survey loadFullFormIncludingQuestionOptions(Long formId, boolean loadQuestionOptionsAndTranslations) {
        //Fetch form
        Survey form = getById(formId);
        //Fetch groups
        TreeMap<Integer, QuestionGroup> qgMap = questionGroupDao.listQuestionGroupsBySurvey(formId);
        form.setQuestionGroupMap(qgMap);

        //Fetch all the questions for the form
        QuestionDao questionDao = new QuestionDao();
        List<Question> ql = questionDao.listQuestionsByForm(formId, loadQuestionOptionsAndTranslations); //Help and options, please
        HashMap<Long, Map<Integer, Question>> mapMap = new HashMap<>();
        //Make question maps for all the groups, and remember them by qgId
        for (QuestionGroup qg:qgMap.values()) {
            TreeMap<Integer, Question> map = new TreeMap<>();
            qg.setQuestionMap(map);
            mapMap.put(qg.getKey().getId(), map);
        }
        //Sort them into their respective groups' maps
        for (Question q : ql) {
            mapMap.get(q.getQuestionGroupId()).put(q.getOrder(), q);
        }
        return form;
    }

    /**
     * saves a surveyContainer containing the xml representation of the survey document.
     *
     * @param surveyId
     * @param surveyDocument
     * @return
     */
    public Long save(Long surveyId, String surveyDocument) {
        SurveyContainer sc = new SurveyContainer();
        sc.setSurveyId(surveyId);
        com.google.appengine.api.datastore.Text surveyText = new com.google.appengine.api.datastore.Text(
                surveyDocument);
        sc.setSurveyDocument(surveyText);
        sc = super.save(sc);
        return sc.getKey().getId();
    }

    /**
     * gets a document from the surveyContainer
     *
     * @param id
     * @return
     */
    public String getSurveyDocument(Long id) {
        SurveyContainer surveyContainer = getByKey(id, SurveyContainer.class);
        return surveyContainer.getSurveyDocument().getValue();
    }

    /**
     * Return a list of surveys accessible to the current user
     *
     * @return
     */
    public List<Survey> listAllFilteredByUserAuthorization() {
        List<Survey> allSurveys = list(Constants.ALL_RESULTS);
        return filterByUserAuthorizationObjectId(allSurveys);
    }

    /**
     * lists all survey container objects
     *
     * @return
     */
    public List<SurveyContainer> listSurveyContainers() {
        return list(SurveyContainer.class, "all");
    }

    /**
     * lists all questions of a given type (across all surveys)
     */
    public List<SurveyQuestion> listQuestionByType(String questionType) {
        return listByProperty("type", questionType, "String",
                SurveyQuestion.class);
    }

    /**
     * lists all survey groups
     *
     * @param cursorString
     * @return
     */
    public List<SurveyGroup> listSurveyGroup(String cursorString) {
        return list(SurveyGroup.class, cursorString);
    }

    /**
     * lists all surveys in a given surveyGroup
     *
     * @param surveyGroupId
     * @return
     */
    public List<Survey> listSurveysByGroup(Long surveyGroupId) {
        return listByProperty("surveyGroupId", surveyGroupId, "Long");
    }

    /**
     * gets a survey by the surveyGroupId and survey code
     *
     * @param code
     * @param surveyGroupId
     * @return
     */
    @SuppressWarnings("unchecked")
    public Survey getByParentIdAndCode(String code, Long surveyGroupId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Survey.class);
        query.setFilter(" code == codeParam && surveyGroupId == idParam");
        query.declareParameters("String codeParam, Long idParam");
        List<Survey> results = (List<Survey>) query
                .execute(code, surveyGroupId);
        if (results != null && results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    /**
     * gets a single survey by code and path. path is defined as "surveyGroupName"
     *
     * @param code
     * @param path
     * @return
     */
    @SuppressWarnings("unchecked")
    public Survey getByPath(String code, String path) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(Survey.class);
        query.setFilter(" path == pathParam && name == codeParam");
        query.declareParameters("String pathParam, String codeParam");
        List<Survey> results = (List<Survey>) query.execute(path, code);
        if (results != null && results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    /**
     * Deletes a survey
     *
     * @param survey
     * @throws IllegalDeletionException - if the system contains responses for this survey
     */
    public void delete(Survey survey) throws IllegalDeletionException {
        QuestionGroupDao qgDao = new QuestionGroupDao();
        qgDao.deleteGroupsForSurvey(survey.getKey().getId());
        super.delete(survey);
    }

    /**
     * lists all survey ids
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Key> listSurveyIds() {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery("select key from "
                + Survey.class.getName());
        List<Key> results = (List<Key>) query.execute();
        return results;
    }

}
