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

import static com.gallatinsystems.common.Constants.ANCESTOR_IDS_FIELD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.analytics.dao.SurveyInstanceSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyInstanceSummary;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.dto.SurveyTaskRequest;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.app.web.rest.dto.SurveyPayload;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;

import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.dao.SurveyalValueDao;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@Controller
@RequestMapping("/surveys")
public class SurveyRestService {

    private static final Logger log = Logger.getLogger(SurveyRestService.class.getName());

    @Inject
    private SurveyDAO surveyDao;

    @Inject
    private SurveyInstanceSummaryDao sisDao;

    @Inject
    private SurveyalValueDao svDao;

    @Inject
    private QuestionAnswerStoreDao qasDao;

    // TODO put in meta information?
    // list all surveys
    public Map<String, Object> listSurveys() {
        final Map<String, Object> response = new HashMap<String, Object>();
        List<SurveyDto> results = new ArrayList<SurveyDto>();
        SurveyInstanceSummary sis = null;
        List<Survey> surveys = surveyDao.listAllFilteredByUserAuthorization();
        if (surveys != null) {
            for (Survey s : surveys) {
                SurveyDto dto = new SurveyDto();
                DtoMarshaller.copyToDto(s, dto);

                // add surveyInstance Count
                sis = sisDao.findBySurveyId(s.getKey().getId());
                if (sis != null) {
                    dto.setInstanceCount(sis.getCount());
                }
                // needed because of different names for description in survey
                // and surveyDto
                dto.setDescription(s.getDesc());
                results.add(dto);
            }
        }
        response.put("surveys", results);
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> listSurveysByGroupId(
            @RequestParam(value = "surveyGroupId", defaultValue = "") Long surveyGroupId,
            @RequestParam(value = "ids[]", defaultValue = "") Long[] ids,
            @RequestParam(value = "preflight", defaultValue = "") String preflight,
            @RequestParam(value = "surveyId", defaultValue = "") Long surveyId) {

        // If none of the optional query params are specified, return all surveys
        if (surveyGroupId == null
                && ids[0] == null
                && preflight.equals("")
                && surveyId == null) {
            return listSurveys();
        }

        final Map<String, Object> response = new HashMap<String, Object>();
        List<SurveyDto> results = new ArrayList<SurveyDto>();
        List<Survey> surveys = null;
        SurveyInstanceSummary sis = null;
        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("");
        statusDto.setMessage("");

        // if this is a pre-flight delete check, handle that
        if (preflight != null && preflight.equals("delete") && surveyId != null) {
            statusDto.setStatus("preflight-delete-survey");
            statusDto.setMessage("cannot_delete");

            if (qasDao.listBySurvey(surveyId).size() == 0
                    && svDao.listBySurvey(surveyId, null, null).size() == 0) {
                statusDto.setMessage("can_delete");
                statusDto.setKeyId(surveyId);
            }

            response.put("surveys", results);
            response.put("meta", statusDto);
            return response;
        }

        // if we are here, it is a regular request and not preflight
        if (surveyGroupId != null) {
            surveys = surveyDao.listSurveysByGroup(surveyGroupId);
        } else if (ids[0] != null) {
            surveys = surveyDao.listByKeys(ids);
        }

        if (surveys != null) {
            for (Survey s : surveyDao.filterByUserAuthorizationObjectId(surveys)) {
                SurveyDto dto = new SurveyDto();
                DtoMarshaller.copyToDto(s, dto);

                // add surveyInstance Count
                sis = sisDao.findBySurveyId(s.getKey().getId());
                if (sis != null) {
                    dto.setInstanceCount(sis.getCount());
                }

                // needed because of different names for description in survey
                // and surveyDto
                dto.setDescription(s.getDesc());
                results.add(dto);
            }
        }

        response.put("surveys", results);
        return response;
    }

    // find a single survey by the surveyId
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, SurveyDto> findSurvey(@PathVariable("id") Long id) {
        final Map<String, SurveyDto> response = new HashMap<String, SurveyDto>();
        Survey s = surveyDao.getByKey(id);
        SurveyDto dto = null;
        SurveyInstanceSummary sis = null;

        if (s != null) {
            dto = new SurveyDto();
            DtoMarshaller.copyToDto(s, dto);
            // add surveyInstance Count

            sis = sisDao.findBySurveyId(s.getKey().getId());
            if (sis != null) {
                dto.setInstanceCount(sis.getCount());
            }

            // needed because of different names for description in survey and
            // surveyDto
            dto.setDescription(s.getDesc());
        }
        response.put("survey", dto);
        return response;

    }

    /**
     * Spawns a task to delete a survey by survey id
     *
     * @param surveyId
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteSurveyById(
            @PathVariable("id") Long surveyId) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        Survey survey = surveyDao.getByKey(surveyId);
        RestStatusDto statusDto = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // check if survey exists in the datastore
        if (survey != null) {
            try {
                TaskOptions deleteSurveyTask = TaskOptions.Builder
                        .withUrl("/app_worker/surveytask")
                        .param(SurveyTaskRequest.ACTION_PARAM,
                                SurveyTaskRequest.DELETE_SURVEY_ACTION)
                        .param(SurveyTaskRequest.ID_PARAM, surveyId.toString());
                QueueFactory.getQueue("deletequeue").add(deleteSurveyTask);
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

    // update existing survey
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingSurvey(
            @RequestBody SurveyPayload payLoad) {
        final SurveyDto requestDto = payLoad.getSurvey();
        final Map<String, Object> response = new HashMap<String, Object>();
        SurveyDto responseDto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid surveyDto, continue. Otherwise,
        // server will respond with 400 Bad Request
        if (requestDto != null && requestDto.getKeyId() != null) {
            Survey s = surveyDao.getByKey(requestDto.getKeyId());

            if (s != null) {
                // copy the properties, except the createdDateTime property,
                // because it is set in the Dao.
                BeanUtils.copyProperties(requestDto, s, new String[] {
                        "createdDateTime", "status", "sector", "version",
                        "lastUpdateDateTime", "description",
                        "instanceCount", ANCESTOR_IDS_FIELD
                });

                s.setAncestorIds(SurveyUtils.retrieveAncestorIds(s));

                s.setDesc(requestDto.getDescription());

                String name = s.getName();
                if (name != null) {
                    String trimmedName = name.replaceAll(",", " ").trim();
                    s.setName(trimmedName);
                    s.setCode(trimmedName);
                    s.setPath(SurveyUtils.fixPath(s.getPath(), trimmedName));
                }

                if (requestDto.getStatus() != null) {
                    // increment version for surveys already published
                    if (s.getStatus().equals(Survey.Status.PUBLISHED)
                            && !s.getStatus().equals(
                                    Survey.Status.valueOf(requestDto.getStatus()))) {
                        s.incrementVersion();
                    }
                }
                s.setStatus(Survey.Status.NOT_PUBLISHED);
                if (requestDto.getSector() != null) {
                    s.setSector(Survey.Sector.valueOf(requestDto.getSector()));
                }
                if (!requestDto.getVersion().equals(s.getVersion().toString())) {
                    log.log(Level.WARNING, "Survey version does not match (dashboard="
                            + requestDto.getVersion() + " datastore=" + s.getVersion() + ")");
                }

                s = surveyDao.save(s);
                responseDto = new SurveyDto();
                DtoMarshaller.copyToDto(s, responseDto);
                responseDto.setDescription(s.getDesc());
                statusDto.setStatus("ok");
            }
        }
        response.put("meta", statusDto);
        response.put("survey", responseDto);
        return response;
    }

    // create new survey
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewSurvey(@RequestBody SurveyPayload payLoad) {
        final SurveyDto surveyDto = payLoad.getSurvey();
        final Map<String, Object> response = new HashMap<String, Object>();

        // if the POST data contains a valid surveyDto, continue. Otherwise,
        // server will respond with 400 Bad Request
        if (surveyDto == null) {
            return getErrorResponse();
        }

        Survey s = null;

        if (surveyDto.getSourceId() == null) {
            s = newSurvey(surveyDto);
        } else {
            s = copySurvey(surveyDto);
        }

        if (s == null) {
            return getErrorResponse();
        }

        final RestStatusDto statusDto = new RestStatusDto();
        final SurveyDto dto = new SurveyDto();
        DtoMarshaller.copyToDto(s, dto);
        dto.setDescription(s.getDesc());
        statusDto.setStatus("ok");

        response.put("meta", statusDto);
        response.put("survey", dto);
        return response;
    }

    private Survey newSurvey(SurveyDto dto) {
        final Survey result = surveyDao.save(marshallToDomain(dto));
        return result;
    }

    private Survey copySurvey(SurveyDto dto) {
        final Survey source = surveyDao.getById(dto.getSourceId());

        if (source == null) {
            // source survey not found, the getById already logged the problem
            return null;
        }
        return SurveyUtils.copySurvey(source, dto);
    }

    private Survey marshallToDomain(SurveyDto dto) {
        final Survey s = new Survey();

        // copy the properties, except the createdDateTime property, because
        // it is set in the Dao.
        BeanUtils.copyProperties(dto, s, new String[] {
                "createdDateTime",
                "status", "sector", "version", "lastUpdateDateTime",
                "displayName", "questionGroupList", "instanceCount", ANCESTOR_IDS_FIELD
        });

        if (dto.getStatus() != null) {
            s.setStatus(Survey.Status
                    .valueOf(dto.getStatus().toString()));
        }
        if (dto.getSector() != null) {
            s.setSector(Survey.Sector.valueOf(dto.getSector()));
        }

        s.setAncestorIds(SurveyUtils.retrieveAncestorIds(s));

        // ignore version number sent by Dashboard and initialise
        s.getVersion();

        // Make sure that code and name are the same
        s.setCode(s.getName());

        return s;
    }

    private Map<String, Object> getErrorResponse() {
        final Map<String, Object> response = new HashMap<String, Object>();
        final RestStatusDto statusDto = new RestStatusDto();

        statusDto.setStatus("failed");

        response.put("meta", statusDto);
        response.put("survey", null);

        return response;
    }
}
