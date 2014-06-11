/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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
import java.util.Map;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;

/**
 * Dao for question groups
 */
public class QuestionGroupDao extends BaseDAO<QuestionGroup> {

    public QuestionGroupDao() {
        super(QuestionGroup.class);
    }

    /**
     * saves a question group and associates it with the survey specified
     * 
     * @param item
     * @param surveyId
     * @param order
     * @return
     */
    public QuestionGroup save(QuestionGroup item, Long surveyId, Integer order) {
        if (item.getSurveyId() == null || item.getSurveyId() == 0) {
            item.setSurveyId(surveyId);
        }
        item = save(item);
        return item;
    }

    /**
     * deletes a group
     * 
     * @param item
     * @param surveyId
     */
    public void delete(QuestionGroup item, Long surveyId) {

        delete(item);
    }

    public List<QuestionGroup> listQuestionGroupsByName(String code) {
        return listByProperty("name", code, "String");
    }

    /**
     * lists all question groups within a survey
     * 
     * @param surveyId
     * @return
     */
    public TreeMap<Integer, QuestionGroup> listQuestionGroupsBySurvey(
            Long surveyId) {
        List<QuestionGroup> groups = listByProperty("surveyId", surveyId,
                "Long");
        TreeMap<Integer, QuestionGroup> map = new TreeMap<Integer, QuestionGroup>();
        if (groups != null) {
            int i = 1;
            for (QuestionGroup group : groups) {
                // TODO: Hack because we seem to have quesitongroups with same
                // order key so put an arbitrary value there for now since it
                // isn't used.
                if (map.containsKey(group.getOrder())) {
                    map.put(i, group);
                    group.setOrder(i);
                } else {
                    map.put(group.getOrder() != null ? group.getOrder() : i,
                            group);
                }
                i++;
            }
        }
        return map;
    }

    /**
     * lists all question groups by survey, ordered by the order field
     * 
     * @param surveyId
     * @return
     */
    public List<QuestionGroup> listQuestionGroupBySurvey(Long surveyId) {
        return super.listByProperty("surveyId", surveyId, "Long", "order",
                "asc");
    }

    /**
     * gets a group by its code and survey id
     * 
     * @param code
     * @param surveyId
     * @return
     */
    @SuppressWarnings("unchecked")
    public QuestionGroup getByParentIdandCode(String code, Long surveyId) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(QuestionGroup.class);
        query.setFilter(" code==codeParam && surveyId == surveyIdParam");
        query.declareParameters("String codeParam, Long surveyIdParam");
        List<QuestionGroup> results = (List<QuestionGroup>) query.execute(code,
                surveyId);
        if (results != null && results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }

    }

    /**
     * finds a group by code and path. Path is "surveyGroupName/surveyName"
     * 
     * @param code
     * @param path
     * @return
     */
    @SuppressWarnings("unchecked")
    public QuestionGroup getByPath(String code, String path) {
        PersistenceManager pm = PersistenceFilter.getManager();
        javax.jdo.Query query = pm.newQuery(QuestionGroup.class);
        query.setFilter(" path == pathParam && code == codeParam");
        query.declareParameters("String pathParam, String codeParam");
        List<QuestionGroup> results = (List<QuestionGroup>) query.execute(path,
                code);
        if (results != null && results.size() > 0) {
            return results.get(0);
        } else {
            return null;
        }
    }

    /**
     * deletes a questionGroup and spawns an asynchronous task to delete all the questions for that
     * group. NOTE: it is possible for the group to be deleted and the questions to remain (if there
     * are existing answers in the system, the question delete will fail). This is permissible since
     * we need to keep the questions around to be able to display the question text when rendering
     * the results but we don't need to keep the question permanently attached to the group.
     * 
     * @param item
     */
    public void delete(QuestionGroup item) {
        QuestionDao qDao = new QuestionDao();
        for (Map.Entry<Integer, Question> qItem : qDao
                .listQuestionsByQuestionGroup(item.getKey().getId(), false)
                .entrySet()) {
            SurveyTaskUtil.spawnDeleteTask("deleteQuestion", qItem.getValue()
                    .getKey().getId());
        }
        QuestionGroup group = getByKey(item.getKey());
        if (group != null) {
            super.delete(item);
        }
    }

}
