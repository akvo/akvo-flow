package com.gallatinsystems.survey.dao;

import java.util.List;
import java.util.TreeMap;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.Translation;

public class QuestionHelpMediaDao extends BaseDAO<QuestionHelpMedia> {

	private TranslationDao translationDao;

	public QuestionHelpMediaDao() {
		super(QuestionHelpMedia.class);
		translationDao = new TranslationDao();
	}

	public TreeMap<Integer, QuestionHelpMedia> listHelpByQuestion(
			Long questionId) {
		List<QuestionHelpMedia> hList = listByProperty("questionId",
				questionId, "Long");
		TreeMap<Integer, QuestionHelpMedia> map = new TreeMap<Integer, QuestionHelpMedia>();
		if (hList != null) {
			int i = 1;
			for (QuestionHelpMedia h : hList) {
				h.setTranslationMap(translationDao.findTranslations(
						Translation.ParentType.QUESTION_HELP_MEDIA_TEXT, h
								.getKey().getId()));
				map.put(i++, h);
			}
		}
		return map;
	}
}
