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
import java.util.logging.Logger;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;

/**
 * Dao for manipulating surveyGroups
 */
public class SurveyGroupDAO extends BaseDAO<SurveyGroup> {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(SurveyGroupDAO.class
            .getName());

    private SurveyDAO surveyDao;

    public SurveyGroupDAO() {
        super(SurveyGroup.class);
        surveyDao = new SurveyDAO();
    }

    /**
     * saves the survey group and any surveys contained therein
     *
     * @param group
     * @return
     */
    public SurveyGroup save(SurveyGroup group) {
        group = super.save(group);
        if (group.getSurveyList() != null) {
            for (Survey s : group.getSurveyList()) {
                s.setSurveyGroupId(group.getKey().getId());
                surveyDao.save(s);
            }
        }
        return group;
    }

    /**
     * finds a single survey group by code
     *
     * @param name
     * @return
     */
    public SurveyGroup findBySurveyGroupName(String name) {
        return super.findByProperty("code", name, "String");
    }

    /**
     * deletes the survey group and spawns asynchronous delete survey messages for any surveys
     * contained therein.
     *
     * @param item
     */
    public void delete(SurveyGroup item) {
        // This probably won't work on the server
        SurveyDAO surveyDao = new SurveyDAO();
        item = super.getByKey(item.getKey().getId());
        for (Survey survey : surveyDao
                .listSurveysByGroup(item.getKey().getId())) {
            SurveyTaskUtil.spawnDeleteTask("deleteSurvey", survey.getKey()
                    .getId());
        }
        super.delete(item);
    }

    /**
     * @param parentId
     * @return
     */
    public List<SurveyGroup> listByProjectFolderId(Long parentId) {

        return super.listByProperty("parentId", parentId, "Long");
    }

    /**
     * Return a list of survey groups that are accessible by the current user
     *
     * @return
     */
    public List<SurveyGroup> listAllFilteredByUserAuthorization() {
        List<SurveyGroup> allSurveyGroups = list(Constants.ALL_RESULTS);
        return filterByUserAuthorizationObjectId(allSurveyGroups);
    }
}
