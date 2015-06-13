/*
 *  Copyright (C) 2012-2015 Stichting Akvo (Akvo Foundation)
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

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.app.web.rest.dto.SurveyGroupPayload;

import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;

import static com.gallatinsystems.common.Constants.ANCESTOR_IDS_FIELD;

@Controller
@RequestMapping("/survey_groups")
public class SurveyGroupRestService {

    @Inject
    private SurveyGroupDAO surveyGroupDao;

    @Inject
    private SurveyDAO surveyDao;

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
        List<SurveyGroup> surveys = surveyGroupDao.listAllFilteredByUserAuthorization();
        SurveyDAO surveyDao = new SurveyDAO();
        if (surveys != null) {
            for (SurveyGroup s : surveys) {
                SurveyGroupDto dto = new SurveyGroupDto();
                DtoMarshaller.copyToDto(s, dto);
                List<Survey> surveyList = surveyDao.listSurveysByGroup(s.getKey().getId());
                if (surveyList != null && !surveyList.isEmpty()) {
                    SurveyDto sDto = new SurveyDto();
                    // we don't want/need the full object
                    sDto.setKeyId(surveyList.get(0).getKey().getId());
                    dto.addSurvey(sDto);
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

        final SurveyGroupDto surveyGroupDto = payLoad.getSurvey_group();
        final Map<String, Object> response = new HashMap<String, Object>();
        SurveyGroupDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid surveyGroupDto, continue.
        // Otherwise, server 400 Bad Request
        if (surveyGroupDto != null) {
            Long keyId = surveyGroupDto.getKeyId();
            SurveyGroup s;

            // if the surveyGroupDto has a key, try to get the surveyGroup.
            if (keyId != null) {
                s = surveyGroupDao.getByKey(keyId);
                // if we find the surveyGroup, update it's properties
                if (s != null) {
                    // copy the properties, except the properties that are set
                    // or provided by the Dao.
                    BeanUtils.copyProperties(surveyGroupDto, s, new String[] {
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

                    s = surveyGroupDao.save(s);

                    s.setAncestorIds(SurveyUtils.retrieveAncestorIds(s));
                    dto = new SurveyGroupDto();
                    DtoMarshaller.copyToDto(s, dto);
                    statusDto.setStatus("ok");
                }
            }
        }
        response.put("meta", statusDto);
        response.put("survey_group", dto);
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
