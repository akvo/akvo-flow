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

package org.waterforpeople.mapping.analytics;

import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.framework.domain.DataChangeRecord;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.Question;

/**
 * handles updates to questionSummary objects
 * 
 * @author Christopher Fagiani
 */
public class SurveyQuestionSummaryUpdater implements DataSummarizer {

    public SurveyQuestionSummaryUpdater() {
    }

    @Override
    public String getCursor() {
        return null;
    }

    /**
     * handles changes to question responses by using the DataChangeRecord to decrement the question
     * response counts for the oldValue and increment the counts for the newValue in the
     * SurveyQuestionSummary.
     */
    @Override
    public boolean performSummarization(String key, String type, String value,
            Integer offset, String cursor) {

        DataChangeRecord changeRecord = new DataChangeRecord(value);
        QuestionDao qDao = new QuestionDao();
        Question q = qDao.getByKey(new Long(changeRecord.getId()));
        if (q != null && Question.Type.OPTION.equals(q.getType())) {
            SurveyQuestionSummaryDao
                    .incrementCount(
                            constructQAS(changeRecord.getId(),
                                    changeRecord.getOldVal()), -1);
            if (changeRecord.getNewVal() != null
                    && changeRecord.getNewVal().trim().length() > 0) {
                SurveyQuestionSummaryDao.incrementCount(
                        constructQAS(changeRecord.getId(),
                                changeRecord.getNewVal()), 1);
            }
        }

        return true;
    }

    /**
     * helper method to create a new QuestionAnswerStore object using the values passed in.
     * 
     * @param id
     * @param value
     * @return
     */
    private QuestionAnswerStore constructQAS(String id, String value) {
        QuestionAnswerStore qas = new QuestionAnswerStore();
        qas.setQuestionID(id);
        qas.setValue(value);
        return qas;
    }
}
