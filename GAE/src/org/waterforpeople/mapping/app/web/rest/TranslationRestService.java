/*
 *  Copyright (C) 2012-2013,2017 Stichting Akvo (Akvo Foundation)
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

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.app.web.rest.dto.TranslationBulkDeletePayload;
import org.waterforpeople.mapping.app.web.rest.dto.TranslationBulkPayload;
import org.waterforpeople.mapping.app.web.rest.dto.TranslationPayload;

import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.TranslationDao;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.survey.domain.Translation.ParentType;

@Controller
@RequestMapping("/translations")
public class TranslationRestService {

    private TranslationDao tDao = new TranslationDao();

    private SurveyDAO sDao = new SurveyDAO();

    Map<String, Translation> translations = new HashMap<String, Translation>();

    // list translations by surveyId and questionGroupId
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> listTranslationsBySurveyId(
            @RequestParam(value = "surveyId", defaultValue = "")
            Long surveyId,
            @RequestParam(value = "questionGroupId", defaultValue = "")
            Long questionGroupId) {
        final Map<String, Object> response = new HashMap<String, Object>();
        List<TranslationDto> results = new ArrayList<TranslationDto>();
        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("");
        statusDto.setMessage("");

        if (surveyId != null) {
            Survey survey = sDao.getById(surveyId);
            if (survey != null && questionGroupId != null) {
                addTranslations(Translation.ParentType.SURVEY_NAME, survey
                        .getKey().getId(), surveyId, results);
                addTranslations(Translation.ParentType.SURVEY_DESC, survey
                        .getKey().getId(), surveyId, results);

                // get question group translations
                List<Translation> translations = tDao.listTranslationsByQuestionGroup(
                        questionGroupId);
                for (Translation t : translations) {
                    TranslationDto tDto = new TranslationDto();
                    DtoMarshaller.copyToDto(t, tDto);
                    tDto.setLangCode(t.getLanguageCode());
                    tDto.setSurveyId(surveyId);
                    results.add(tDto);
                }
            }
        }
        response.put("translations", results);
        return response;
    }

    private void addTranslations(ParentType parentType, long id, long surveyId,
            List<TranslationDto> results) {
        Map<String, Translation> translations = tDao.findTranslations(
                parentType, id);
        for (Translation t : translations.values()) {
            TranslationDto tDto = new TranslationDto();
            DtoMarshaller.copyToDto(t, tDto);
            tDto.setLangCode(t.getLanguageCode());
            tDto.setSurveyId(surveyId);
            results.add(tDto);
        }
    }

    // create new Translation
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewTranslation(
            @RequestBody
            TranslationPayload payLoad) {
        final TranslationDto translationDto = payLoad.getTranslation();
        final Map<String, Object> response = new HashMap<String, Object>();
        TranslationDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");
        statusDto.setMessage("Cannot create translation");

        // if the POST data contains a valid translationDto, continue.
        // Otherwise, server will respond with 400 Bad Request

        if (translationDto != null) {
            dto = createTranslation(translationDto);
            statusDto.setStatus("ok");
            statusDto.setMessage("");
        }
        response.put("meta", statusDto);
        response.put("translation", dto);
        return response;
    }

    // bulk create new Translation
    @RequestMapping(method = RequestMethod.POST, value = "/bulk")
    @ResponseBody
    public Map<String, Object> bulkSaveNewTranslation(
            @RequestBody
            TranslationBulkPayload payLoad) {
        final List<TranslationDto> translationDtoList = payLoad
                .getTranslations();
        final Map<String, Object> response = new HashMap<String, Object>();
        List<TranslationDto> results = new ArrayList<TranslationDto>();
        TranslationDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();

        Boolean stateSuccess = true;
        for (TranslationDto tDto : translationDtoList) {
            dto = createTranslation(tDto);
            if (dto != null) {
                results.add(dto);
            } else {
                stateSuccess = false;
            }
        }
        String status = stateSuccess ? "ok" : "failed";
        if (status.equals("failed")) {
            statusDto.setMessage("Cannot create translation");
        }
        statusDto.setStatus(status);
        response.put("meta", statusDto);
        response.put("translations", results);
        return response;
    }

    private TranslationDto createTranslation(TranslationDto translationDto) {
        Translation t = new Translation();
        BeanUtils.copyProperties(translationDto, t, new String[] {
                "createdDateTime", "parentType", "langCode"
        });
        t.setLanguageCode(translationDto.getLangCode());
        if (translationDto.getParentType() != null) {
            t.setParentType(Translation.ParentType.valueOf(translationDto
                    .getParentType().toString()));
        }
        t = tDao.save(t);

        TranslationDto tDto = new TranslationDto();
        DtoMarshaller.copyToDto(t, tDto);
        tDto.setLangCode(t.getLanguageCode());
        return tDto;
    }

    // update existing translation
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingTranslation(
            @RequestBody
            TranslationPayload payLoad) {
        final TranslationDto tDto = payLoad.getTranslation();
        final Map<String, Object> response = new HashMap<String, Object>();
        TranslationDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid userDto, continue.
        // Otherwise, server will respond with 400 Bad Request
        if (tDto != null) {
            dto = updateTranslation(tDto);
            if (dto != null) {
                statusDto.setStatus("ok");
            }
        }
        response.put("meta", statusDto);
        response.put("translation", dto);
        return response;
    }

    // bulkupdate existing translation
    @RequestMapping(method = RequestMethod.PUT, value = "/bulk")
    @ResponseBody
    public Map<String, Object> bulkSaveExistingTranslation(
            @RequestBody
            TranslationBulkPayload payLoad) {
        final List<TranslationDto> translationDtoList = payLoad
                .getTranslations();
        final Map<String, Object> response = new HashMap<String, Object>();
        TranslationDto dto = null;
        RestStatusDto statusDto = new RestStatusDto();
        List<TranslationDto> results = new ArrayList<TranslationDto>();

        Boolean stateSuccess = true;
        for (TranslationDto tDto : translationDtoList) {
            dto = updateTranslation(tDto);
            if (dto != null) {
                results.add(dto);
            } else {
                stateSuccess = false;
            }
        }
        String status = stateSuccess ? "ok" : "failed";
        statusDto.setStatus(status);

        response.put("meta", statusDto);
        response.put("translations", results);
        return response;
    }

    private TranslationDto updateTranslation(TranslationDto tDto) {
        Long keyId = tDto.getKeyId();
        Translation t;

        // if the tDto has a key, try to get the translation.
        if (keyId != null) {
            t = tDao.getByKey(keyId);
            // if we find the translation, update it's properties
            if (t != null) {
                // copy the properties, except the createdDateTime property,
                // because it is set in the Dao.
                BeanUtils.copyProperties(tDto, t, new String[] {
                        "createdDateTime", "parentType", "langCode", "surveyId"
                });

                t.setLanguageCode(tDto.getLangCode());
                if (tDto.getParentType() != null) {
                    t.setParentType(Translation.ParentType.valueOf(tDto
                            .getParentType().toString()));
                }

                t = tDao.save(t);
                TranslationDto dto = new TranslationDto();
                BeanUtils.copyProperties(t, dto, new String[] {
                        "parentType",
                        "languageCode"
                });

                dto.setLangCode(t.getLanguageCode());
                if (t.getParentType() != null) {
                    dto.setParentType(t.getParentType().toString());
                }

                if (t.getKey() != null) {
                    dto.setKeyId(t.getKey().getId());
                }

                // the surveyId is taken directly from the input dto
                // as it is not stored on the object
                dto.setSurveyId(tDto.getSurveyId());

                return dto;
            }
        }
        return null;
    }

    // delete translation by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteTranslationById(
            @PathVariable("id")
            Long id) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();

        Translation t = tDao.getByKey(id);
        RestStatusDto statusDto = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // check if translation exists in the datastore
        if (t != null) {
            // delete translation
            tDao.delete(t);
            statusDto.setStatus("ok");
        }
        // }
        response.put("meta", statusDto);
        return response;
    }

    // bulk delete translation by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/bulk")
    @ResponseBody
    public Map<String, RestStatusDto> bulkDeleteTranslationById(
            @RequestBody
            TranslationBulkDeletePayload payLoad) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        final List<Number> tIds = payLoad
                .getTranslations();
        RestStatusDto statusDto = null;
        Translation t = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("ok");
        if (tIds != null && tIds.size() > 0) {
            for (int i = 0; i < tIds.size(); i++) {
                Number temp = tIds.get(i);
                t = tDao.getByKey(temp.longValue());
                // check if translation exists in the datastore
                if (t != null) {
                    // delete translation
                    tDao.delete(t);
                } else {
                    statusDto.setStatus("failed");
                }
            }
        }

        response.put("meta", statusDto);
        return response;
    }
}
