/*
 *  Copyright (C) 2018, 2020 Stichting Akvo (Akvo Foundation)
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
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.akvo.flow.dao.ReportDao;
import org.akvo.flow.domain.persistent.Report;
import org.akvo.flow.rest.dto.ReportDto;
import org.akvo.flow.rest.dto.ReportPayload;
import org.akvo.flow.rest.security.AppRole;
import org.akvo.flow.servlet.ReportServlet;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.waterforpeople.mapping.app.web.CurrentUserServlet;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.user.domain.User;


@Controller
@RequestMapping("/reports")
public class ReportRestService {

	private static final Logger log = Logger.getLogger(ReportRestService.class.getName());

    private final String statsType = "STATISTICS";
    private final String[] doNotCopy = {
            "createdDateTime",
            "lastUpdateDateTime"
          };

    private ReportDao reportDao = new ReportDao();

    /**
     * Create a new Report from posted payload.
     */
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewReport(@RequestBody
    ReportPayload payLoad,
    @RequestHeader(value = "Host", required = false) String host) {

        final ReportDto reportDto = payLoad.getReport();
        final Map<String, Object> response = new HashMap<String, Object>();
        ReportDto dto = null;
        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid ReportDto, continue.
        // Otherwise, server will respond with 400 Bad Request
        if (reportDto != null) {
            //Check if it is a statistics report, which only admins can get
            if (statsType.equalsIgnoreCase(reportDto.getReportType()) && !isAdminRole()) {
                statusDto.setMessage("Must be Admin");
            } else {

                Report r = new Report();

                BeanUtils.copyProperties(reportDto, r, doNotCopy);

                r.setUser(reportDao.currentUserId());
                r.setState(Report.QUEUED);  //overwrite any supplied state
                // Save it, so we get an id assigned
                r = reportDao.save(r);
                String baseUrl = null;
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                        .getRequestAttributes()).getRequest();
                if (request == null) {
                	throw new RuntimeException("Request details not available!");
                } else {
                	baseUrl = request.getScheme() + "://" + host;
                }
                ReportServlet.queueStart(baseUrl, r);

                dto = new ReportDto();
                BeanUtils.copyProperties(r, dto);
                dto.setKeyId(r.getKey().getId());
                statusDto.setStatus("ok");
                response.put("report", dto);
            }
        }
        response.put("meta", statusDto);
        return response;
    }

    private boolean isAdminRole() {
        User u = CurrentUserServlet.getCurrentUser();
        return u.getPermissionList().equals(Integer.toString(AppRole.ROLE_ADMIN.getLevel()))
            || u.getPermissionList().equals(Integer.toString(AppRole.ROLE_SUPER_ADMIN.getLevel()));
    }

    // find all reports belonging to the current user
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> listMyReports(
            @RequestParam(value = "reportType", defaultValue = "") String reportType
            ) {
        final Map<String, Object> response = new HashMap<String, Object>();
        List<ReportDto> results = new ArrayList<ReportDto>();
        List<Report> reports;
        if (reportType.equals("")) { //Cannot have null as the defaultValue
            reportType = null;
        }
        reports = reportDao.listAllByCurrentUserAndType(reportType);

        if (reports != null) {
            for (Report r : reports) {
                ReportDto dto = new ReportDto();
                BeanUtils.copyProperties(r, dto);
                dto.setKeyId(r.getKey().getId());
                results.add(dto);
            }
        }
        response.put("reports", results);
        return response;
    }

    /*
     *  find a single report by the reportId
     *  TODO: restrict use to owner+superAdmins
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, ReportDto> findReport(@PathVariable("id") Long id) {
        final Map<String, ReportDto> response = new HashMap<String, ReportDto>();
        Report r = reportDao.getByKey(id);
        ReportDto dto = null;
        if (r != null) {
            dto = new ReportDto();
            BeanUtils.copyProperties(r, dto);
            dto.setKeyId(r.getKey().getId());
        }
        response.put("report", dto);
        return response;

    }

    // delete report by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteReportById(@PathVariable("id") Long id) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        Report r = reportDao.getByKey(id);
        RestStatusDto statusDto = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("failed");
        response.put("meta", statusDto);

        if (r == null) { //bad id
        	statusDto.setMessage("Nonexistent id");
        	return response;
        }
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        final Authentication authentication = securityContext.getAuthentication();
        Object deleter = authentication.getCredentials();
        if (r.getUser() == null || !r.getUser().equals(deleter)) { //wrong user
        	statusDto.setMessage("You may not delete other users' reports");
        	return response;
        }
        // ok, delete report
        reportDao.delete(r);
        statusDto.setStatus("ok");
        return response;
    }

    // update an existing report
    // only allow status changes
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

            // if the reportDto has a key, try to get the report.
            if (keyId != null) {
                Report r = reportDao.getByKey(keyId);
                if (r != null) {
                    // found the report, update selected properties
                    r.setState(reportDto.getState());
                    r.setMessage(reportDto.getMessage());
                    r.setFilename(reportDto.getFilename());
                    r = reportDao.save(r); //Updates lastUpdateDateTime
                    dto = new ReportDto();
                    BeanUtils.copyProperties(r, dto);
                    statusDto.setStatus("ok");
                }
            }
        }
        response.put("meta", statusDto);
        response.put("report", dto);
        return response;
    }

}
