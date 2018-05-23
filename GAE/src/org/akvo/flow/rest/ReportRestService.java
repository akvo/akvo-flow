/*
 *  Copyright (C) 2018 Stichting Akvo (Akvo Foundation)
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.akvo.flow.dao.ReportDao;
import org.akvo.flow.domain.persistent.Report;
import org.akvo.flow.rest.dto.ReportDto;
import org.akvo.flow.rest.dto.ReportPayload;
import org.akvo.flow.rest.dto.ReportTaskRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.dto.TaskRequest;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.User;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@Controller
@RequestMapping("/reports")
public class ReportRestService {

    private final String[] doNotCopy = {
            "user",
            "createdDateTime",
            "lastUpdateDateTime"
          };

    private ReportDao reportDao = new ReportDao();
    private UserDao userDao = new UserDao();

    /**
     * Create a new Report from posted payload.
     *
     * @param requestPayload
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewReport(@RequestBody
    ReportPayload payLoad) {
        final ReportDto reportDto = payLoad.getReport();
        final Map<String, Object> response = new HashMap<String, Object>();
        ReportDto dto = null;
        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid ReportDto, continue.
        // Otherwise, server will respond with 400 Bad Request
        if (reportDto != null) {
            User user = userDao.findUserByEmail(reportDto.getUser());

            if (user != null) {
                Report r = new Report();

                BeanUtils.copyProperties(reportDto, r, doNotCopy);
                r.setUser(user.getKey().getId());
                r.setState(Report.QUEUED);
                // Save it, so we get an id assigned
                r = reportDao.save(r);

                //Queue it
                Queue queue = QueueFactory.getDefaultQueue();
                TaskOptions options = TaskOptions.Builder.withUrl("/app_worker/reportservlet")
                        .param(TaskRequest.ACTION_PARAM, ReportTaskRequest.START_ACTION)
                        .param(ReportTaskRequest.ID_PARAM, Long.toString(r.getKey().getId()));
                queue.add(options); //overwrite any supplied state
                dto = new ReportDto();
                DtoMarshaller.copyToDto(r, dto);
                statusDto.setStatus("ok");
            }
        }

        response.put("meta", statusDto);
        response.put("report", dto);
        return response;
    }

    // find all reports belonging to the current user
    //TODO: get an unfiltered list if superAdmin?
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> listMyReports() {
        final Map<String, Object> response = new HashMap<String, Object>();
        List<ReportDto> results = new ArrayList<ReportDto>();
        List<Report> reports = reportDao.listAllByCurrentUser();
        if (reports != null) {
            for (Report r : reports) {
                ReportDto dto = new ReportDto();
                DtoMarshaller.copyToDto(r, dto);
                results.add(dto);
            }
        }
        response.put("reports", results);
        return response;
    }

    // find a single report by the reportId
    // TODO: restrict use to owner+superAdmins
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, ReportDto> findReport(@PathVariable("id") Long id) {
        final Map<String, ReportDto> response = new HashMap<String, ReportDto>();
        Report qo = reportDao.getByKey(id);
        ReportDto dto = null;
        if (qo != null) {
            dto = new ReportDto();
            DtoMarshaller.copyToDto(qo, dto);
        }
        response.put("report", dto);
        return response;

    }

    // delete report by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteReportById(@PathVariable("id") Long id) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        Report qo = reportDao.getByKey(id);
        RestStatusDto statusDto = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // check if report exists in the datastore
        if (qo != null) {
            // delete report group
            reportDao.delete(qo);
            statusDto.setStatus("ok");
        }
        response.put("meta", statusDto);
        return response;
    }

    // update existing report
    //TODO: only allow status changes?
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingReport(@RequestBody
    ReportPayload payLoad) {
        final ReportDto reportDto = payLoad.getReport();
        final Map<String, Object> response = new HashMap<String, Object>();
        ReportDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid reportDto, continue.
        // Otherwise, server will respond with 400 Bad Request
        if (reportDto != null) {
            Long keyId = reportDto.getKeyId();
            Report qo;

            // if the reportDto has a key, try to get the report.
            if (keyId != null) {
                qo = reportDao.getByKey(keyId);
                // if we find the report, update it's properties
                if (qo != null) {
                    BeanUtils.copyProperties(reportDto, qo, doNotCopy);
                    //TODO: look up user (but why would it change?)
                    qo = reportDao.save(qo); //Also stores lastUpdateDateTime
                    dto = new ReportDto();
                    DtoMarshaller.copyToDto(qo, dto);
                    statusDto.setStatus("ok");
                }
            }
        }
        response.put("meta", statusDto);
        response.put("report", dto);
        return response;
    }

}
