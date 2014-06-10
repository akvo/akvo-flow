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
import java.util.TreeMap;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.Translation;

/**
 * dao for manipulation of HelpMedia objects
 * 
 * @author Christopher Fagiani
 */
public class QuestionHelpMediaDao extends BaseDAO<QuestionHelpMedia> {

    private TranslationDao translationDao;

    public QuestionHelpMediaDao() {
        super(QuestionHelpMedia.class);
        translationDao = new TranslationDao();
    }

    /**
     * lists all help objects for a given question, including any translations.
     * 
     * @param questionId
     * @return
     */
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
