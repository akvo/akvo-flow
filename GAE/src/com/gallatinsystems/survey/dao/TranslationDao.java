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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.Translation;

/**
 * dao for manipulating Translation objects
 * 
 * @author Christohper Fagiani
 * 
 */
public class TranslationDao extends BaseDAO<Translation> {

	public TranslationDao() {
		super(Translation.class);
	}

	/**
	 * gets all translations for a given id and parentType combination. The map
	 * returned is keyed on language code.
	 * 
	 * @param parentType
	 * @param parentId
	 * @return
	 */
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

	/**
	 * Finds all translations for a certain question group
	 *
	 * @param questionGroupId
	 */
	@SuppressWarnings("unchecked")
	public List<Translation> listTranslationsByQuestionGroup(long questionGroupId){
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(Translation.class);
		StringBuilder filterString = new StringBuilder();
		StringBuilder paramString = new StringBuilder();
		Map<String, Object> paramMap = null;
		paramMap = new HashMap<String, Object>();

		appendNonNullParam("questionGroupId", filterString, paramString, "Long",
				questionGroupId, paramMap);

		query.setFilter(filterString.toString());
		query.declareParameters(paramString.toString());

		List<Translation> result = (List<Translation>) query
				.executeWithMap(paramMap);
		return result;
	}

	/**
	 * deletes all items translations for a given parent
	 * 
	 * @param parentId
	 * @param parentType
	 */
	public void deleteTranslationsForParent(Long parentId,
			Translation.ParentType parentType) {
		HashMap<String, Translation> trans = findTranslations(parentType,
				parentId);
		Collection<Translation> values = trans.values();
		if (values != null && values.size() > 0) {
			delete(trans.values());
		}
	}

	/**
	 * list all translations
	 * 
	 * @param cursor
	 * @param count
	 */
	 @SuppressWarnings("unchecked")
	 public List<Translation> listTranslations(Integer count, String cursorString) {
		 PersistenceManager pm = PersistenceFilter.getManager();
		 javax.jdo.Query q = pm.newQuery(Translation.class);
		 prepareCursor(cursorString, count, q);
		 List<Translation> tList = (List<Translation>) q.execute();
		 return tList;
		 }
}
