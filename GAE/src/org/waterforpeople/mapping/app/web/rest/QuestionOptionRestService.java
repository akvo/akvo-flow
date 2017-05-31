/*
 *  Copyright (C) 2012,2017 Stichting Akvo (Akvo Foundation)
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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.app.web.rest.dto.QuestionOptionPayload;

import com.gallatinsystems.survey.dao.QuestionOptionDao;
import com.gallatinsystems.survey.domain.QuestionOption;

@Controller
@RequestMapping("/question_options")
public class QuestionOptionRestService {

    private QuestionOptionDao questionOptionDao = new QuestionOptionDao();

    // find a single questionOption by the questionOptionId
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, QuestionOptionDto> findQuestionOption(@PathVariable("id")
    Long id) {
        final Map<String, QuestionOptionDto> response = new HashMap<String, QuestionOptionDto>();
        QuestionOption qo = questionOptionDao.getByKey(id);
        QuestionOptionDto dto = null;
        if (qo != null) {
            dto = new QuestionOptionDto();
            DtoMarshaller.copyToDto(qo, dto);
        }
        response.put("question_option", dto);
        return response;

    }

    // delete questionOption by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteQuestionOptionById(@PathVariable("id")
    Long id) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        QuestionOption qo = questionOptionDao.getByKey(id);
        RestStatusDto statusDto = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // check if questionOption exists in the datastore
        if (qo != null) {
            // delete questionOption group
            questionOptionDao.delete(qo);
            statusDto.setStatus("ok");
        }
        response.put("meta", statusDto);
        return response;
    }

    // update existing questionOption
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingQuestionOption(@RequestBody
    QuestionOptionPayload payLoad) {
        final QuestionOptionDto questionOptionDto = payLoad.getQuestion_option();
        final Map<String, Object> response = new HashMap<String, Object>();
        QuestionOptionDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid questionOptionDto, continue.
        // Otherwise, server will respond with 400 Bad Request
        if (questionOptionDto != null) {
            Long keyId = questionOptionDto.getKeyId();
            QuestionOption qo;

            // if the questionOptionDto has a key, try to get the questionOption.
            if (keyId != null) {
                qo = questionOptionDao.getByKey(keyId);
                // if we find the questionOption, update it's properties
                if (qo != null) {
                    BeanUtils.copyProperties(questionOptionDto, qo);
                    qo = questionOptionDao.save(qo);
                    dto = new QuestionOptionDto();
                    DtoMarshaller.copyToDto(qo, dto);
                    statusDto.setStatus("ok");
                }
            }
        }
        response.put("meta", statusDto);
        response.put("question_option", dto);
        return response;
    }

    // create new questionOption
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewQuestionOption(@RequestBody
    QuestionOptionPayload payLoad) {
        final QuestionOptionDto questionOptionDto = payLoad.getQuestion_option();
        final Map<String, Object> response = new HashMap<String, Object>();
        QuestionOptionDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid questionOptionDto, continue.
        // Otherwise, server will respond with 400 Bad Request
        if (questionOptionDto != null) {
            QuestionOption qo = new QuestionOption();

            BeanUtils.copyProperties(questionOptionDto, qo);
            qo = questionOptionDao.save(qo);

            dto = new QuestionOptionDto();
            DtoMarshaller.copyToDto(qo, dto);
            statusDto.setStatus("ok");
        }

        response.put("meta", statusDto);
        response.put("question_option", dto);
        return response;
    }
}
