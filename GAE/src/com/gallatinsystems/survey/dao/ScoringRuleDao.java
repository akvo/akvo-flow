package com.gallatinsystems.survey.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.ScoringRule;

/**
 * data access class for ScoringRules. Rules are always owned by a question.
 * 
 * @author Christopher Fagiani
 * 
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
