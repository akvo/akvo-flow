package com.gallatinsystems.survey.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.Translation;

public class TranslationDao extends BaseDAO<Translation> {

	public TranslationDao() {
		super(Translation.class);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Translation> findTranslations(
			Translation.ParentType parentType, Long parentId) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(Translation.class);
		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = null;
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("parentType", filterString, paramString, "String",
				parentType, paramMap);
		appendNonNullParam("parentId", filterString, paramString, "Long",
				parentId, paramMap);

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		HashMap<String, Translation> translations = new HashMap<String, Translation>();
		List<Translation> translationList = (List<Translation>) query
				.executeWithMap(paramMap);
		if (translationList != null) {
			for (Translation t : translationList) {
				translations.put(t.getLanguageCode(), t);
			}
		}
		return translations;
	}

	public void deleteTranslationsForParent(Long parentId,
			Translation.ParentType parentType) {
		HashMap<String, Translation> trans = findTranslations(parentType,
				parentId);
		if (trans != null) {
			PersistenceManager pm = PersistenceFilter.getManager();
			for (Translation t : trans.values()) {
				pm.deletePersistent(t);
			}
		}
	}
}
