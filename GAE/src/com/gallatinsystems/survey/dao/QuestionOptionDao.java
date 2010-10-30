package com.gallatinsystems.survey.dao;

import java.util.List;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Translation;

public class QuestionOptionDao extends BaseDAO<QuestionOption> {

	private TranslationDao translationDao;

	public QuestionOptionDao() {
		super(QuestionOption.class);
		translationDao = new TranslationDao();
	}

	public TreeMap<Integer, QuestionOption> listOptionByQuestion(Long questionId) {
		List<QuestionOption> oList = listByProperty("questionId", questionId,
				"Long");
		TreeMap<Integer, QuestionOption> map = new TreeMap<Integer, QuestionOption>();
		if (oList != null) {
			int i = 1;
			for (QuestionOption o : oList) {
				o.setTranslationMap(translationDao.findTranslations(
						Translation.ParentType.QUESTION_OPTION, o.getKey()
								.getId()));
				i++;
				map.put(o.getOrder() != null ? o.getOrder() : i, o);
			}
		}
		return map;
	}

	/**
	 * deletes all options associated with a given question
	 * 
	 * @param questionId
	 */
	public void deleteOptionsForQuestion(Long questionId) {
		List<QuestionOption> oList = listByProperty("questionId", questionId,
				"Long");
		if (oList != null) {
			PersistenceManager pm = PersistenceFilter.getManager();
			TranslationDao tDao = new TranslationDao();
			for (QuestionOption opt : oList) {
				tDao.deleteTranslationsForParent(opt.getKey().getId(), Translation.ParentType.QUESTION_OPTION);
				pm.deletePersistent(opt);
			}
		}
	}
}
