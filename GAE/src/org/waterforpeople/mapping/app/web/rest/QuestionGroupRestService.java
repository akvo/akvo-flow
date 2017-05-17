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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.akvo.flow.domain.mapper.QuestionGroupDtoMapper;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyTaskRequest;
import org.waterforpeople.mapping.app.web.rest.dto.QuestionGroupPayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.surveyal.dao.SurveyalValueDao;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@Controller
@RequestMapping("/question_groups")
public class QuestionGroupRestService {

    private QuestionGroupDao questionGroupDao = new QuestionGroupDao();

    private QuestionDao questionDao = new QuestionDao();

    private SurveyalValueDao svDao = new SurveyalValueDao();

    private QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();

    // Properties to exclude when copying question groups
    private static final String[] QUESTION_GROUP_COPY_EXCLUDED_PROPS = (String[]) ArrayUtils.add(
            Constants.EXCLUDED_PROPERTIES, "status");

    // TODO put in meta information?
    // list all questionGroups
    @RequestMapping(method = RequestMethod.GET, value = "/all")
    @ResponseBody
    public Map<String, List<QuestionGroupDto>> listQuestionGroups() {
        final Map<String, List<QuestionGroupDto>> response =
                new HashMap<String, List<QuestionGroupDto>>();
        List<QuestionGroupDto> results = new ArrayList<QuestionGroupDto>();
        List<QuestionGroup> questionGroups = questionGroupDao.list(Constants.ALL_RESULTS);
        if (questionGroups != null) {
            for (QuestionGroup qg : questionGroups) {
                results.add(QuestionGroupDtoMapper.transform(qg));
            }
        }
        response.put("question_groups", results);
        return response;
    }

    /**
     * list questionGroups by survey id Perform preflight check for deletion of question group
     *
     * @param surveyId
     * @param preflight
     * @param questionGroupId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> listQuestionGroupBySurvey(
            @RequestParam(value = "surveyId", defaultValue = "") Long surveyId,
            @RequestParam(value = "preflight", defaultValue = "") String preflight,
            @RequestParam(value = "questionGroupId", defaultValue = "") Long questionGroupId) {
        final Map<String, Object> response = new HashMap<String, Object>();

        final RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("");
        statusDto.setMessage("");

        if (preflight.equals("delete") && questionGroupId != null) {
            statusDto.setStatus("preflight-delete-questiongroup");
            statusDto.setMessage("can_delete");
            statusDto.setKeyId(questionGroupId);

            for (Question q : questionDao.listQuestionsByQuestionGroup(questionGroupId,
                    Boolean.FALSE).values()) {
                if (qasDao.listByQuestion(q.getKey().getId()).size() > 0
                        && svDao.listByQuestion(q.getKey().getId()).size() > 0) {
                    statusDto.setMessage("cannot_delete");
                    statusDto.setKeyId(null);
                }
            }
        }

        final List<QuestionGroupDto> results = new ArrayList<QuestionGroupDto>();
        if (surveyId != null) {
            final List<QuestionGroup> questionGroups = questionGroupDao
                    .listQuestionGroupBySurvey(surveyId);
            if (questionGroups != null) {
                for (QuestionGroup qg : questionGroups) {
                    results.add(QuestionGroupDtoMapper.transform(qg));
                }
            }
        }

        // in this case, we are just trying to get a single question group
        if (questionGroupId != null && preflight.isEmpty()) {
            QuestionGroup qg = questionGroupDao.getByKey(questionGroupId);
            if (qg != null) {
                results.add(QuestionGroupDtoMapper.transform(qg));
            }
        }
        response.put("question_groups", results);
        response.put("meta", statusDto);
        return response;
    }

    // find a single questionGroup by the questionGroupId
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, QuestionGroupDto> findQuestionGroup(
            @PathVariable("id") Long id) {
        final Map<String, QuestionGroupDto> response = new HashMap<String, QuestionGroupDto>();
        QuestionGroup qg = questionGroupDao.getByKey(id);
        QuestionGroupDto dto = null;
        if (qg != null) {
            dto = QuestionGroupDtoMapper.transform(qg);
        }
        response.put("question_group", dto);
        return response;
    }

    // delete questionGroup by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteQuestionGroupById(
            @PathVariable("id") Long questionGroupId) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        QuestionGroup group = questionGroupDao.getByKey(questionGroupId);
        RestStatusDto statusDto = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // check if questionGroup exists in the datastore
        if (group != null) {
            try {
                TaskOptions deleteQuestionGroupTask = TaskOptions.Builder
                        .withUrl("/app_worker/surveytask")
                        .param(SurveyTaskRequest.ACTION_PARAM,
                                SurveyTaskRequest.DELETE_QUESTION_GROUP_ACTION)
                        .param(SurveyTaskRequest.ID_PARAM, Long.toString(group.getKey().getId()));
                QueueFactory.getQueue("deletequeue").add(deleteQuestionGroupTask);
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

    // update existing questionGroup
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingQuestionGroup(
            @RequestBody QuestionGroupPayload payLoad) {
        final QuestionGroupDto questionGroupDto = payLoad.getQuestion_group();
        final Map<String, Object> response = new HashMap<String, Object>();
        QuestionGroupDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");
        statusDto.setMessage("Cannot find question group");

        // if the POST data contains a valid questionGroupDto, continue.
        // Otherwise,
        // server will respond with 400 Bad Request
        if (questionGroupDto != null) {
            Long keyId = questionGroupDto.getKeyId();
            QuestionGroup qg;

            // if the questionGroupDto has a key, try to get the questionGroup.
            if (keyId != null) {
                qg = questionGroupDao.getByKey(keyId);
                // if we find the questionGroup, update it's properties
                if (qg != null) {
                    // Integer origOrder = qg.getOrder();
                    BeanUtils.copyProperties(questionGroupDto, qg,
                            new String[] {
                                    "createdDateTime", "status"
                            });
                    qg = questionGroupDao.save(qg);

                    dto = QuestionGroupDtoMapper.transform(qg);
                    statusDto.setStatus("ok");
                    statusDto.setMessage("");
                }
            }
        }
        response.put("meta", statusDto);
        response.put("question_group", dto);
        return response;
    }

    // create new questionGroup
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewQuestionGroup(
            @RequestBody QuestionGroupPayload payLoad) {

        final QuestionGroupDto questionGroupDto = payLoad.getQuestion_group();

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");
        statusDto.setMessage("Cannot create question group");

        final Map<String, Object> response = new HashMap<String, Object>();
        response.put("meta", statusDto);
        response.put("question_group", null);

        // if the POST data contains a valid questionGroupDto, continue.
        // Otherwise, server will respond with 400 Bad Request
        if (questionGroupDto == null) {
            return response;
        }

        QuestionGroup qg = null;
        // deal with copying a question group
        if (questionGroupDto.getSourceId() != null) {
            // copy question group
            qg = copyGroup(questionGroupDto);
        } else {
            // new question group
            qg = new QuestionGroup();
            BeanUtils.copyProperties(questionGroupDto, qg, new String[] {
                    "createdDateTime", "status"
            });
            qg.setStatus(QuestionGroup.Status.valueOf(questionGroupDto.getStatus()));
            qg = questionGroupDao.save(qg);
        }

        if (qg == null) {
            return response;
        }

        QuestionGroupDto dto = QuestionGroupDtoMapper.transform(qg);
        statusDto.setStatus("ok");
        statusDto.setMessage("");

        response.put("meta", statusDto);
        response.put("question_group", dto);
        return response;
    }

    /**
     * Copy a question group within a survey
     *
     * @param questionGroupDto
     * @return
     */
    private QuestionGroup copyGroup(QuestionGroupDto questionGroupDto) {
        // need a temp group to avoid state sharing exception
        QuestionGroup tmpGroup = new QuestionGroup();
        BeanUtils.copyProperties(questionGroupDto, tmpGroup, QUESTION_GROUP_COPY_EXCLUDED_PROPS);
        tmpGroup.setStatus(QuestionGroup.Status.COPYING);
        final QuestionGroup copyGroup = questionGroupDao.save(tmpGroup);

        // schedule deep copy
        final Queue queue = QueueFactory.getDefaultQueue();
        final TaskOptions options = TaskOptions.Builder
                .withUrl("/app_worker/dataprocessor")
                .param(DataProcessorRequest.ACTION_PARAM,
                        DataProcessorRequest.COPY_QUESTION_GROUP)
                .param(DataProcessorRequest.QUESTION_GROUP_ID_PARAM,
                        String.valueOf(copyGroup.getKey().getId()))
                .param(DataProcessorRequest.SOURCE_PARAM, questionGroupDto.getSourceId().toString())
                .header("Host",
                        BackendServiceFactory.getBackendService()
                                .getBackendAddress("dataprocessor"));

        queue.add(options);

        return copyGroup;
    }
}
