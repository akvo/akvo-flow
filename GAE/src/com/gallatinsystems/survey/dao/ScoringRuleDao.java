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

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.ScoringRule;

/**
 * data access class for ScoringRules. Rules are always owned by a question.
 * 
 * @author Christopher Fagiani
 */
public class ScoringRuleDao extends BaseDAO<ScoringRule> {

    public ScoringRuleDao() {
        super(ScoringRule.class);
    }

    /**
     * lists all scoring rules for a single question
     * 
     * @param questionId
     * @return
     */
    public List<ScoringRule> listRulesByQuestion(Long questionId) {
        return listByProperty("questionId", questionId, "Long");
    }

    /**
     * deletes all rules for a given question
     * 
     * @param questionId
     */
    public void deleteRulesForQuestion(Long questionId) {
        List<ScoringRule> rules = listRulesByQuestion(questionId);
        if (rules != null) {
            delete(rules);
        }
    }
}
