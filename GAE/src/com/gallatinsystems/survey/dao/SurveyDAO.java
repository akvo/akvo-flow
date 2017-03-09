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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.xml.bind.JAXBException;

import org.waterforpeople.mapping.domain.SurveyQuestion;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.exceptions.IllegalDeletionException;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;
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
     * loads a full survey object (whole object graph, including questions). This method can only be
     * called reliably from a background task or backend
     *
     * @param id
     * @return
     */
    public Survey loadFullSurvey(Long surveyId) {
        Survey survey = getById(surveyId);
        survey.setQuestionGroupMap(questionGroupDao
                .listQuestionGroupsBySurvey(survey.getKey().getId()));
        return survey;
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
     * returns a Survey xml pojo obtained after unmarshalling the SurveyContainer
     *
     * @param id
     * @return
     */
    public com.gallatinsystems.survey.domain.xml.Survey get(Long id) {
        SurveyContainer surveyContainer = getByKey(id, SurveyContainer.class);

        SurveyXMLAdapter sxa = new SurveyXMLAdapter();
        com.gallatinsystems.survey.domain.xml.Survey survey = null;
        try {
            survey = sxa.unmarshall(surveyContainer.getSurveyDocument()
                    .toString());
        } catch (JAXBException e) {
            log.log(Level.SEVERE, "Could not unmarshal xml", e);
        }
        return survey;
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
