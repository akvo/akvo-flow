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

package org.waterforpeople.mapping.analytics;

import java.util.List;

import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.Question;

/**
 * This class will summarize survey information by updating counts by response type by survey. The
 * key should be a key of a survey instance object
 * 
 * @author Christopher Fagiani
 */
public class SurveyQuestionSummarizer implements DataSummarizer {

    @Override
    public boolean performSummarization(String key, String type, String value,
            Integer offset, String cursor) {
        if (key != null) {
            SurveyInstanceDAO instanceDao = new SurveyInstanceDAO();
            List<QuestionAnswerStore> answers = instanceDao
                    .listQuestionAnswerStoreByType(new Long(key), "VALUE");
            if (answers != null && answers.size() > 0) {
                QuestionDao questionDao = new QuestionDao();
                List<Question> qList = questionDao.listQuestionsInOrder(
                        answers.get(0).getSurveyId(),
                        Question.Type.OPTION);
                int i = 0;
                if (offset != null) {
                    i = offset;
                } else {
                    offset = 0;
                }
                // process BATCH_SIZE items
                while (i < answers.size() && i < offset + BATCH_SIZE) {
                    if (isSummarizable(answers.get(i), qList)) {
                        SurveyQuestionSummaryDao.incrementCount(answers.get(i), 1);
                    }
                    i++;
                }
                // if we still have more answers to process, return false
                if (i < answers.size()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * returns true if the question type for the answer object is an OPTION type
     * 
     * @param answer
     * @param questions
     * @return
     */
    private boolean isSummarizable(QuestionAnswerStore answer,
            List<Question> questions) {
        if (questions != null && answer != null) {
            long id = Long.parseLong(answer.getQuestionID());
            for (Question q : questions) {
                if (q.getKey().getId() == id) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public String getCursor() {
        return null;
    }
}
