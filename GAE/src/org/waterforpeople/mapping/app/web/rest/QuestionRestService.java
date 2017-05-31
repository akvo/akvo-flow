/*
 *  Copyright (C) 2012-2017 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web.rest;

import com.gallatinsystems.metric.dao.SurveyMetricMappingDao;
import com.gallatinsystems.metric.domain.SurveyMetricMapping;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionOptionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.surveyal.dao.SurveyalValueDao;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import org.akvo.flow.domain.mapper.QuestionDtoMapper;
import org.akvo.flow.domain.mapper.QuestionOptionDtoMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.web.dto.SurveyTaskRequest;
import org.waterforpeople.mapping.app.web.rest.dto.QuestionPayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/questions")
public class QuestionRestService {

    private QuestionDao questionDao = new QuestionDao();

    private QuestionOptionDao questionOptionDao = new QuestionOptionDao();

    private SurveyMetricMappingDao surveyMetricMappingDao = new SurveyMetricMappingDao();

    private SurveyDAO surveyDao = new SurveyDAO();

    private SurveyGroupDAO surveyGroupDao = new SurveyGroupDAO();

    // list questions by questionGroup or by survey.
    // if optionQuestionHeadersOnly is true, only the option questions are returned
    // and without any of the actual options loaded. In the dashboard, this is used
    // merely to make a choice between options, so not all details are necessary.
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> listQuestions(
            @RequestParam(value = "questionGroupId", defaultValue = "") Long questionGroupId,
            @RequestParam(value = "surveyId", defaultValue = "") Long surveyId,
            @RequestParam(value = "optionQuestionsOnly", defaultValue = "") String optionQuestionsOnlyParam,
            @RequestParam(value = "preflight", defaultValue = "") String preflight,
            @RequestParam(value = "questionId", defaultValue = "") Long questionId,
            @RequestParam(value = "cascadeResourceId", defaultValue = "") Long cascadeResourceId) {
        final Map<String, Object> response = new HashMap<>();
        List<Question> questions = new ArrayList<>();
        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("");
        statusDto.setMessage("");

        boolean listOptionQuestionOnly = "true".equals(optionQuestionsOnlyParam);

        // if this is a pre-flight delete check, handle that
        if (preflight != null && preflight.equals("delete")
                && questionId != null) {
            QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
            SurveyalValueDao svDao = new SurveyalValueDao();
            statusDto.setStatus("preflight-delete-question");
            statusDto.setMessage("cannot_delete");

            if (qasDao.listByQuestion(questionId).size() == 0
                    && svDao.listByQuestion(questionId).size() == 0) {
                statusDto.setMessage("can_delete");
                statusDto.setKeyId(questionId);
            }

            // if questionGroupId is present, load questions in that group
        } else if (questionGroupId != null) {
            questions = questionDao.listQuestionsInOrderForGroup(questionGroupId);
        } else if (surveyId != null) {
            if (listOptionQuestionOnly) {
                questions = questionDao.listQuestionsInOrder(surveyId, Question.Type.OPTION);
            } else {
                questions = questionDao.listQuestionsInOrder(surveyId, null);
            }
        } else if (cascadeResourceId != null) {
            questions = questionDao.listByCascadeResourceId(cascadeResourceId);
        }

        List<QuestionDto> questionDtoList = new ArrayList<>();
        List<QuestionOptionDto> questionOptionDtoList = new ArrayList<>();

        if (questions != null && questions.size() > 0) {
            for (Question question : questions) {
                QuestionDto qDto = QuestionDtoMapper.transform(question);
                if (qDto != null) {
                    questionDtoList.add(qDto);
                }
            }

            if (!listOptionQuestionOnly) {
                questionOptionDtoList.addAll(retrieveQuestionOptionList(questionDtoList));
                setQuestionOptionsIdList(questionDtoList, questionOptionDtoList);
            }
        }

        response.put("questionOptions", questionOptionDtoList);
        response.put("questions", questionDtoList);
        response.put("meta", statusDto);
        return response;
    }

    private List<QuestionOptionDto> retrieveQuestionOptionList(List<QuestionDto> questionDtoList) {
        if (questionDtoList == null || questionDtoList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> optionTypeQuestionIds = new ArrayList<>();
        for (QuestionDto question : questionDtoList) {
            if (QuestionDto.QuestionType.OPTION.equals(question.getType())) {
                optionTypeQuestionIds.add(question.getKeyId());
            }
        }

        List<QuestionOptionDto> questionOptionDtoList = new ArrayList<>();

        for (QuestionOption qo : questionOptionDao.listByQuestionId(optionTypeQuestionIds)) {
            QuestionOptionDto qoDto = QuestionOptionDtoMapper.transform(qo);
            questionOptionDtoList.add(qoDto);
        }

        return questionOptionDtoList;
    }

    private static void setQuestionOptionsIdList(List<QuestionDto> questionDtoList,
            List<QuestionOptionDto> questionOptionDtoList) {

        Map<Long, QuestionDto> questionDtoMap = new HashMap<>();
        for (QuestionDto questionDto : questionDtoList) {
            if (!QuestionDto.QuestionType.OPTION.equals(questionDto.getType())) {
                continue;
            }
            questionDtoMap.put(questionDto.getKeyId(), questionDto);
        }

        for (QuestionOptionDto qoDto : questionOptionDtoList) {
            QuestionDto questionDto = questionDtoMap.get(qoDto.getQuestionId());
            if (questionDto.getQuestionOptions() == null) {
                questionDto.setQuestionOptions(new ArrayList<Long>());
            }
            questionDto.getQuestionOptions().add(qoDto.getKeyId());
        }
    }

    // find a single question by the questionId
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, Object> findQuestion(@PathVariable("id") Long id) {
        final Map<String, Object> response = new HashMap<String, Object>();
        List<QuestionOptionDto> qoResults = new ArrayList<QuestionOptionDto>();
        Question q = questionDao.getByKey(id);
        QuestionDto dto = null;
        if (q != null) {
            dto = QuestionDtoMapper.transform(q);
            qoResults = attachAnyOptions(dto);
        }
        response.put("questionOptions", qoResults);
        response.put("question", dto);
        return response;

    }

    // delete question by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteQuestionById(
            @PathVariable("id") Long questionId) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        Question q = questionDao.getByKey(questionId);
        RestStatusDto statusDto = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("failed");
        statusDto.setMessage("_cannot_delete");

        // check if question exists in the datastore
        if (q != null) {
            try {
                TaskOptions deleteQuestionTask = TaskOptions.Builder
                        .withUrl("/app_worker/surveytask")
                        .param(SurveyTaskRequest.ACTION_PARAM,
                                SurveyTaskRequest.DELETE_QUESTION_ACTION)
                        .param(SurveyTaskRequest.ID_PARAM, questionId.toString());
                QueueFactory.getQueue("deletequeue").add(deleteQuestionTask);
                statusDto.setStatus("ok");
                statusDto.setMessage("deleted");
            } catch (Exception e) {
                statusDto.setStatus("failed");
                statusDto.setMessage(e.getMessage());
            }
        }

        response.put("meta", statusDto);
        return response;
    }

    // update existing question
    // questionOptions are saved and updated on their own
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingQuestion(
            @RequestBody QuestionPayload payLoad) {
        final QuestionDto questionDto = payLoad.getQuestion();
        final Map<String, Object> response = new HashMap<String, Object>();
        QuestionDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");
        statusDto.setMessage("Cannot change question");

        // if the POST data contains a valid questionDto, continue. Otherwise,
        // server will respond with 400 Bad Request
        if (questionDto != null) {
            Long keyId = questionDto.getKeyId();
            Question q;

            // if the questionDto has a key, try to get the question.
            if (keyId != null) {
                q = questionDao.getByKey(keyId);
                // if we find the question, update it's properties
                if (q != null) {
                    // copy the properties, except the createdDateTime property,
                    // because it is set in the Dao.
                    BeanUtils.copyProperties(questionDto, q, new String[] {
                            "createdDateTime", "type", "optionList"
                    });
                    if (questionDto.getType() != null)
                        q.setType(Question.Type.valueOf(questionDto.getType()
                                .toString()));

                    if (questionDto.getMetricId() != null) {
                        // delete existing mappings
                        surveyMetricMappingDao.deleteMetricMapping(keyId);

                        // create a new mapping
                        SurveyMetricMapping newMapping = new SurveyMetricMapping();
                        newMapping.setMetricId(questionDto.getMetricId());
                        newMapping.setQuestionGroupId(questionDto
                                .getQuestionGroupId());
                        newMapping.setSurveyId(questionDto.getSurveyId());
                        newMapping.setSurveyQuestionId(keyId);
                        surveyMetricMappingDao.save(newMapping);
                    }
                    q = questionDao.save(q);

                    dto = QuestionDtoMapper.transform(q);
                    statusDto.setStatus("ok");
                    statusDto.setMessage("");
                }
            }
        }
        response.put("meta", statusDto);
        response.put("question", dto);
        return response;
    }

    // create new question
    // questionOptions are saved and updated on their own
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewQuestion(
            @RequestBody QuestionPayload payLoad) {
        final QuestionDto questionDto = payLoad.getQuestion();
        final Map<String, Object> response = new HashMap<String, Object>();
        List<QuestionOptionDto> qoResults = new ArrayList<QuestionOptionDto>();
        QuestionDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");
        statusDto.setMessage("Cannot create question");

        // if the POST data contains a valid questionDto, continue. Otherwise,
        // server will respond with 400 Bad Request
        if (questionDto != null) {
            Question q = null;

            if (questionDto.getSourceId() == null) {
                q = newQuestion(questionDto);
            } else {
                q = copyQuestion(questionDto);
            }
            dto = QuestionDtoMapper.transform(q);
            statusDto.setStatus("ok");
            statusDto.setMessage("");

            qoResults = attachAnyOptions(dto);
        }
        response.put("meta", statusDto);
        response.put("questionOptions", qoResults);
        response.put("question", dto);

        return response;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{id}/validate")
    @ResponseBody
    public Map<String, Object> validateQuestionId(
            @PathVariable("id") Long id,
            @RequestParam(value = "questionId") String questionId) {

        Question question = questionDao.getByKey(id);

        Long surveyId = question.getSurveyId();
        Survey survey = surveyDao.getById(surveyId);

        Long surveyGroupId = survey.getSurveyGroupId();
        SurveyGroup surveyGroup = surveyGroupDao.getByKey(surveyGroupId);

        boolean isMonitoringGroup = surveyGroup.getMonitoringGroup();

        List<Survey> surveys = new ArrayList<Survey>();

        if (isMonitoringGroup) {
            surveys = surveyDao.listSurveysByGroup(surveyGroupId);
        } else {
            surveys.add(survey);
        }

        List<Question> questions = new ArrayList<Question>();

        for (Survey s : surveys) {
            questions.addAll(questionDao.listQuestionsBySurvey(s.getKey().getId()));
        }

        Map<String, Object> result = new HashMap<String, Object>();

        for (Question q : questions) {
            if (questionId.equals(q.getQuestionId())
                    && !question.getKey().equals(q.getKey())) {
                result.put("success", false);
                result.put("reason", "Question id not unique");
                return result;
            }
        }

        result.put("success", true);
        return result;
    }

    private Question copyQuestion(QuestionDto dto) {
        final Question source = questionDao.getByKey(dto.getSourceId());

        if (source == null) {
            // source question not found, the getByKey already logged the problem
            return null;
        }
        return SurveyUtils.copyQuestion(source, dto.getQuestionGroupId(), dto.getOrder(),
                source.getSurveyId(),
                SurveyUtils.listQuestionIdsUsedInSurveyGroup(source.getSurveyId()));
    }

    private Question newQuestion(QuestionDto dto) {
        final Question q = new Question();
        // copy the properties, except the createdDateTime property, because
        // it is set in the Dao.
        BeanUtils.copyProperties(dto, q, new String[] {
                "createdDateTime", "type"
        });
        if (dto.getType() != null) {
            q.setType(Question.Type.valueOf(dto.getType()
                    .toString()));
        }
        final Question result = questionDao.save(q);
        return result;
    }

    private List<QuestionOptionDto> attachAnyOptions(QuestionDto dto) {
        if (dto.getType() == QuestionDto.QuestionType.OPTION) {
            ArrayList<QuestionOptionDto> qoResults = new ArrayList<QuestionOptionDto>();
            // since we do not need translations:
            List<QuestionOption> qoList = questionOptionDao.listByQuestionId(dto.getKeyId());
            List<Long> qoIdList = new ArrayList<Long>();
            for (QuestionOption qo : qoList) {
                qoIdList.add(qo.getKey().getId());
                QuestionOptionDto qoDto = QuestionOptionDtoMapper.transform(qo);
                qoResults.add(qoDto);
            }
            dto.setQuestionOptions(qoIdList);
            return qoResults;
        }
        return Collections.emptyList();
    }

}

