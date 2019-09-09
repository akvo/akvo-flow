/*
 *  Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.Device.DeviceType;

import org.akvo.flow.dao.DataPointAssignmentDao;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleDto;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

@Controller
@RequestMapping("/assigned_datapoints")
public class DataPointRestService {

    private SurveyedLocaleDao surveyedLocaleDao = new SurveyedLocaleDao();
    private DeviceDAO deviceDao = new DeviceDAO();
    private DataPointAssignmentDao dpaDao = new DataPointAssignmentDao();

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> listQuestions(
            @RequestParam(value = "androidId", defaultValue = "") String androidId,
            @RequestParam(value = "phoneNumber", defaultValue = "") String phoneNumber,
            @RequestParam(value = "imei", defaultValue = "") String imei,
            @RequestParam(value = "surveyId", defaultValue = "") Long surveyId, //this is proper Survey, not Form
            @RequestParam(value = "identifier", defaultValue = "") String identifier,
            @RequestParam(value = "displayName", defaultValue = "") String displayName,
            @RequestParam(value = "since", defaultValue = "") String since) {

        Map<String, Object> response = new HashMap<String, Object>();

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus(""); //nothing known yet
        statusDto.setMessage("");

        //Identify the device
        Device d = deviceDao.getDevice(androidId, imei, phoneNumber);
        if (d == null) {
            // if device is unknown, we just fail(?)
            statusDto.setStatus("failed");
            statusDto.setMessage("unknown device");
            response.put("meta", statusDto);
            return response;
        }
        Long deviceId = d.getKey().getId();

        //This is where a JOIN would be handy...

        //Get the list of assigned DP ids
        Set<Long> ids = dpaDao.listDataPointIds(deviceId, surveyId);

        //Fetch those data points

        List<SurveyedLocale> sls = new ArrayList<SurveyedLocale>();
        List<SurveyedLocaleDto> locales = new ArrayList<SurveyedLocaleDto>();

        sls = surveyedLocaleDao.listSurveyedLocales(since, surveyId, null, null);

        for (SurveyedLocale sl : sls) {
            SurveyedLocaleDto dto = new SurveyedLocaleDto();
            DtoMarshaller.copyToDto(sl, dto);
            locales.add(dto);
        }

        Integer num = sls.size();
        String newSince = SurveyedLocaleDao.getCursor(sls);
        statusDto.setNum(num);
        statusDto.setSince(newSince);

        response.put("surveyed_locales", locales);
        response.put("meta", statusDto);
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{dataPointId}")
    @ResponseBody
    public Map<String, Object> findDataPointById(
            @PathVariable("dataPointId") Long dataPointId) {
        final Map<String, Object> response = new HashMap<>();
        final RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("");
        statusDto.setMessage("");

        SurveyedLocale dataPoint = surveyedLocaleDao.getByKey(dataPointId);
        SurveyedLocaleDto dto = null;
        if (dataPoint != null) {
            dto = new SurveyedLocaleDto();
            BeanUtils.copyProperties(dataPoint, dto, Constants.EXCLUDED_PROPERTIES);
            dto.setKeyId(dataPoint.getKey().getId());
        }
        response.put("surveyed_locale", dto);
        response.put("meta", statusDto);
        return response;
    }
}
