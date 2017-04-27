/*
 *  Copyright (C) 2010-2016 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("surveyrpcservice")
public interface SurveyService extends RemoteService {

    public static final String DATE_ROLL_UP = "collectionDate";
    // TODO: change this to the region field name once it's added
    public static final String REGION_ROLL_UP = "collectionDate";

    public SurveyDto[] listSurvey();

    public QuestionDto[] listSurveyQuestionByType(Long surveyId,
            QuestionType type, boolean loadTranslations);

    /**
     * lists all surveys for a group
     */
    public ArrayList<SurveyDto> listSurveysByGroup(String surveyGroupId);

    public ArrayList<QuestionGroupDto> listQuestionGroupsBySurvey(
            String surveyId);

    public ArrayList<QuestionDto> listQuestionsByQuestionGroup(
            String questionGroupId, boolean needDetails);

    /**
     * fully hydrates a survey object
     *
     * @param surveyId
     * @return
     */
    public SurveyDto loadFullSurvey(Long surveyId);

    public List<SurveyDto> listSurveysForSurveyGroup(String surveyGroupCode);

    public QuestionDto saveQuestion(QuestionDto value, Long questionGroupId, boolean forceReorder);

    public String deleteSurveyGroup(SurveyGroupDto value);

    public String deleteSurvey(SurveyDto value, Long surveyGroupId);

    public String deleteQuestionGroup(QuestionGroupDto value, Long surveyId);

    public String deleteQuestion(QuestionDto value, Long questionGroupId);

    public SurveyDto saveSurvey(SurveyDto surveyDto, Long surveyGroupId);

    public QuestionGroupDto saveQuestionGroup(QuestionGroupDto dto,
            Long surveyId);

    public SurveyGroupDto saveSurveyGroup(SurveyGroupDto dto);

    public void publishSurveyAsync(Long surveyId);

    public List<TranslationDto> saveTranslations(
            List<TranslationDto> translations);

    public void rerunAPMappings(Long surveyId);

    public List<QuestionHelpDto> listHelpByQuestion(Long questionId);

    public List<QuestionHelpDto> saveHelp(List<QuestionHelpDto> helpList);

    public Map<String, TranslationDto> listTranslations(Long parentId,
            String parentType);

    public List<QuestionGroupDto> saveQuestionGroups(
            List<QuestionGroupDto> dtoList);

    public QuestionDto copyQuestion(QuestionDto existingQuestion,
            QuestionGroupDto newParentGroup);

    public void updateQuestionOrder(List<QuestionDto> questions);

    public void updateQuestionGroupOrder(List<QuestionGroupDto> groups);

    /**
     * returns a surveyDto populated from the published xml. This domain graph lacks many keyIds so
     * it is not suitable for updating the survey structure. It is, however, suitable for rendering
     * the survey and collecting responses.
     *
     * @param surveyId
     * @return
     */
    public SurveyDto getPublishedSurvey(String surveyId);

    /**
     * fires an async request to generate a bootstrap xml file
     *
     * @param surveyIdList
     * @param dbInstructions
     * @param notificationEmail
     */
    public void generateBootstrapFile(List<Long> surveyIdList,
            String dbInstructions, String notificationEmail);

    /**
     * returns a survey (core info only, not fully populated) based on its id
     *
     * @param id
     * @return
     */
    public SurveyDto findSurvey(Long id);

    /**
     * marks that a set of changes to a survey are done so we can publish a notification
     *
     * @param id
     */
    public void markSurveyChangesComplete(Long id);

    /**
     * lists the base question info for all questions that depend on the questionId passed in
     *
     * @param questionId
     * @return
     */
    public ArrayList<QuestionDto> listQuestionsDependentOnQuestion(Long questionId);

    public ArrayList<QuestionDto> listQuestionsByQuestionGroup(
            String questionGroupId, boolean needDetails, boolean allowSideEffects);
}
