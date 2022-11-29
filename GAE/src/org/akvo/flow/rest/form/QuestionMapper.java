/*
 * Copyright (C) 2021 Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Flow.
 *
 * Akvo Flow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akvo Flow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akvo Flow.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.akvo.flow.rest.form;

import com.gallatinsystems.survey.domain.Question;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.List;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;

/**
 * maps a Question from QuestionGroup
 */
public class QuestionMapper {
    private final QuestionOptionMapper questionOptionMapper;

    public QuestionMapper(QuestionOptionMapper questionOptionMapper) {
        this.questionOptionMapper = questionOptionMapper;
    }

    @Nonnull
    TreeMap<Integer, Question> mapQuestions(List<QuestionDto> questionList) {
        TreeMap<Integer, Question> mappedQuestions = new TreeMap<>();
        if (questionList != null) {
            for (QuestionDto questionDto : questionList) {
                mappedQuestions.put(questionDto.getOrder(), mapQuestion(questionDto));
            }
        }
        return mappedQuestions;
    }

    private Question mapQuestion(QuestionDto questionDto) {
        Question question = new Question();
        question.setKey(KeyFactory.createKey("Question", questionDto.getKeyId()));
        question.setType(Question.Type.valueOf(questionDto.getType().toString()));
        question.setTip(questionDto.getTip());
        question.setText(questionDto.getText());
        question.setDependentFlag(questionDto.getDependentFlag());
        question.setAllowMultipleFlag(questionDto.getAllowMultipleFlag());
        question.setAllowOtherFlag(questionDto.getAllowOtherFlag());
        question.setCollapseable(questionDto.getCollapseable());
        question.setGeoLocked(questionDto.getGeoLocked());
        question.setRequireDoubleEntry(questionDto.getRequireDoubleEntry());
        question.setImmutable(questionDto.getImmutable());
        question.setDependentQuestionId(questionDto.getDependentQuestionId());
        question.setDependentQuestionAnswer(questionDto.getDependentQuestionAnswer());
        question.setCascadeResourceId(questionDto.getCascadeResourceId());
        question.setCaddisflyResourceUuid(questionDto.getCaddisflyResourceUuid());
        question.setQuestionGroupId(question.getQuestionGroupId());
        question.setSurveyId(questionDto.getSurveyId());
        question.setVariableName(questionDto.getVariableName());
        question.setOrder(questionDto.getOrder());
        question.setMandatoryFlag(questionDto.getMandatoryFlag());
        question.setPath(questionDto.getPath());
        question.setAllowDecimal(questionDto.getAllowDecimal());
        question.setAllowSign(questionDto.getAllowSign());
        question.setMinVal(questionDto.getMinVal());
        question.setMaxVal(question.getMaxVal());
        question.setAllowExternalSources(questionDto.getAllowExternalSources());
        question.setLocaleNameFlag(questionDto.getLocaleNameFlag());
        question.setLocaleLocationFlag(questionDto.getLocaleLocationFlag());
        question.setPersonalData(questionDto.getPersonalData());
        question.setAnswerStats(questionDto.getAnswerStats());
        question.setAllowPoints(questionDto.getAllowPoints());
        question.setAllowLine(questionDto.getAllowLine());
        question.setAllowPolygon(questionDto.getAllowPolygon());
        question.setSourceQuestionId(questionDto.getSourceId());
        question.setQuestionOptionMap(questionOptionMapper.mapOptions(questionDto.getOptionList()));
        return question;
    }
}
