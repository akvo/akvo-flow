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

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.app.web.rest.dto.SurveyGroupPayload;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;

@Controller
@RequestMapping("/survey_groups")
public class SurveyGroupRestService {

    private SurveyGroupDAO surveyGroupDao = new SurveyGroupDAO();

    private SurveyDAO surveyDao = new SurveyDAO();

    // TODO put in meta information?
    // list all survey groups
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> listSurveyGroups(
            @RequestParam(value = "preflight", defaultValue = "") String preflight,
            @RequestParam(value = "surveyGroupId", defaultValue = "") Long surveyGroupId) {
        final Map<String, Object> response = new HashMap<String, Object>();
        List<SurveyGroupDto> results = new ArrayList<SurveyGroupDto>();
        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("");
        statusDto.setMessage("");

        // if this is a pre-flight delete check, handle that
        if (preflight != null && preflight.equals("delete") && surveyGroupId != null) {
            SurveyDAO sDao = new SurveyDAO();
            statusDto.setStatus("preflight-delete-surveygroup");
            statusDto.setMessage("cannot_delete");

            if (sDao.listSurveysByGroup(surveyGroupId).size() == 0) {
                statusDto.setMessage("can_delete");
                statusDto.setKeyId(surveyGroupId);
            }

            response.put("survey_groups", results);
            response.put("meta", statusDto);
            return response;
        }

        // if we are here, it is a regular request
        List<SurveyGroup> surveyGroups = surveyGroupDao.listAllFilteredByUserAuthorization();

        // we do not need to filter the list of Survey entities (forms) i.e. we list *all* results
        // as the list will be filtered later when processing the list of SurveyGroup entities
        // (surveys) to include
        List<Survey> surveys = surveyDao.list(Constants.ALL_RESULTS);

        Map<Long, Survey> surveyGroupIdToSomeSurvey = new HashMap<>();

        if (surveys != null) {
            for (Survey s : surveys) {
                Long sgId = (Long) s.getSurveyGroupId();
                if (sgId != null) {
                    surveyGroupIdToSomeSurvey.put(sgId, s);
                }
            }
        }

        if (surveyGroups != null) {
            for (SurveyGroup sg : surveyGroups) {
                SurveyGroupDto dto = new SurveyGroupDto(sg);
                Survey survey = surveyGroupIdToSomeSurvey.get(sg.getKey().getId());
                if (survey != null) {
                    // we don't want/need the full object
                    dto.addSurvey(survey.getKey().getId());
                }
                results.add(dto);
            }
        }
        response.put("survey_groups", results);
        return response;
    }

    // find survey group by id
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, SurveyGroupDto> findSurveyGroupById(
            @PathVariable("id") Long id) {
        final Map<String, SurveyGroupDto> response = new HashMap<String, SurveyGroupDto>();

        SurveyGroup s = surveyGroupDao.getByKey(id);
        SurveyGroupDto dto = null;
        if (s != null) {
            dto = new SurveyGroupDto();
            DtoMarshaller.copyToDto(s, dto);
        }
        response.put("survey_group", dto);
        return response;
    }

    // delete survey group by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteSurveyGroupById(
            @PathVariable("id") Long id) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        SurveyGroup s = surveyGroupDao.getByKey(id);
        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // check if surveyGroup exists in the datastore
        if (s != null) {
            boolean delete = false;
            switch (s.getProjectType()) {
                case PROJECT:
                    // only delete surveyGroups if there are no surveys in there
                    List<Survey> surveys = surveyDao.listSurveysByGroup(id);
                    delete = surveys.size() == 0;
                    break;
                case PROJECT_FOLDER:
                    // only delete surveyGroups if there are no sub folders in there
                    List<SurveyGroup> surveyGroups = surveyGroupDao.listByProjectFolderId(id);
                    delete = surveyGroups.size() == 0;
                    break;
            }
            if (delete) {
                surveyGroupDao.delete(s);
                statusDto.setStatus("ok");
            }
        }
        response.put("meta", statusDto);
        return response;
    }

    // Update survey group
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingSurveyGroup(
            @RequestBody SurveyGroupPayload payLoad) {

        final Map<String, Object> response = new HashMap<String, Object>();
        final RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");
        response.put("meta", statusDto);

        final SurveyGroupDto requestDto = payLoad.getSurvey_group();
        final SurveyGroupDto responseDto = new SurveyGroupDto();
        response.put("survey_group", responseDto);

        if (requestDto == null || requestDto.getKeyId() == null) {
            return response;
        }

        SurveyGroup s = surveyGroupDao.getByKey(requestDto.getKeyId());
        if (s == null) {
            return response;
        }

        boolean hasMoved = requestDto.getParentId() == null || s.getParentId() == null
                || !requestDto.getParentId().equals(s.getParentId());

        BeanUtils.copyProperties(requestDto, s, new String[] {
                "createdDateTime", "lastUpdateDateTime",
                "displayName", "questionGroupList", ANCESTOR_IDS_FIELD
        });

        String name = s.getName();
        if (name != null) {
            String trimmedName = name.replaceAll(",", " ").trim();
            s.setName(trimmedName);
            s.setCode(trimmedName);
            s.setPath(SurveyUtils.fixPath(s.getPath(), trimmedName));
        }

        if (Boolean.FALSE.equals(s.getMonitoringGroup())) {
            s.setNewLocaleSurveyId(null);
        }
        s.setPublished(false);

        s.setAncestorIds(SurveyUtils.retrieveAncestorIds(s));
        if (hasMoved) {
            SurveyUtils.setChildObjects(s);
            surveyGroupDao.save(s.updateAncestorIds(true));
        }

        s = surveyGroupDao.save(s);

        DtoMarshaller.copyToDto(s, responseDto);
        statusDto.setStatus("ok");
        return response;
    }

    // Create new survey group
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> saveNewSurveyGroup(
            @RequestBody SurveyGroupPayload payLoad) {

        final SurveyGroupDto surveyGroupDto = payLoad.getSurvey_group();
        final Map<String, Object> response = new HashMap<String, Object>();
        SurveyGroupDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid surveyGroupDto, continue.
        // Otherwise, server 400 Bad Request
        if (surveyGroupDto != null) {
            SurveyGroup s = new SurveyGroup();

            // copy the properties, except the properties that are set or
            // provided by the Dao.
            BeanUtils.copyProperties(surveyGroupDto, s, new String[] {
                    "createdDateTime", "lastUpdateDateTime", "displayName",
                    "questionGroupList", ANCESTOR_IDS_FIELD
            });

            s.setAncestorIds(SurveyUtils.retrieveAncestorIds(s));

            // Make sure that code and name are the same
            s.setCode(s.getName());
            s = surveyGroupDao.save(s);

            dto = new SurveyGroupDto();
            DtoMarshaller.copyToDto(s, dto);
            statusDto.setStatus("ok");
        }
        response.put("meta", statusDto);
        response.put("survey_group", dto);
        return response;
    }

}
