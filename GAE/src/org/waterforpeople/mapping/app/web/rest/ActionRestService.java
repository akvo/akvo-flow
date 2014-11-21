/*
 *  Copyright (C) 2012-2014 Stichting Akvo (Akvo Foundation)
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

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.analytics.dao.SurveyInstanceSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyInstanceSummary;
import org.waterforpeople.mapping.app.gwt.server.survey.SurveyServiceImpl;
import org.waterforpeople.mapping.app.web.dto.BootstrapGeneratorRequest;
import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyAssemblyRequest;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.dao.DeviceApplicationDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.DeviceApplication;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.HttpUtil;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.survey.dao.CascadeResourceDao;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.CascadeResource;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.app.web.SurveyalRestRequest;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.utils.SystemProperty;

@Controller
@RequestMapping("/actions")
public class ActionRestService {
	private static final Logger log = Logger.getLogger(SurveyUtils.class
            .getName());

    @Inject
    private SurveyDAO surveyDao;

    @Inject
    private QuestionDao questionDao;

    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> doAction(
            @RequestParam(value = "action", defaultValue = "")
            String action,
            @RequestParam(value = "surveyId", defaultValue = "")
            Long surveyId,
            @RequestParam(value = "cascadeResourceId", defaultValue = "")
            Long cascadeResourceId,
            @RequestParam(value = "surveyIds[]", defaultValue = "")
            Long[] surveyIds,
            @RequestParam(value = "email", defaultValue = "")
            String email,
            @RequestParam(value = "version", defaultValue = "")
            String version,
            @RequestParam(value = "dbInstructions", defaultValue = "")
            String dbInstructions) {
        String status = "failed";
        String message = "";
        final Map<String, Object> response = new HashMap<String, Object>();
        RestStatusDto statusDto = new RestStatusDto();

        // perform the required action
        if ("recomputeSurveyInstanceSummaries".equals(action)) {
            status = recomputeSurveyInstanceSummaries();
        } else if ("publishSurvey".equals(action) && surveyId != null) {
            status = publishSurvey(surveyId);
        } else if ("generateBootstrapFile".equals(action) && surveyIds != null
                && email != null) {
            message = generateBootstrapFile(surveyIds, dbInstructions, email);
            status = "ok";
            statusDto.setMessage(message);
        } else if ("removeZeroValues".equals(action)) {
            status = removeZeroMinMaxValues();
        } else if ("fixOptions2Values".equals(action)) {
            status = fixOptions2Values();
        } else if ("newApkVersion".equals(action)) {
            String path = newApkVersion(version);
            if (path.length() > 0) {
                status = "success";
                statusDto.setMessage("Created entry for " + path);
            }
        } else if ("populateGeocellsForLocale".equals(action)) {
            status = computeGeocellsForLocales();
        } else if ("createTestLocales".equals(action)) {
            status = createTestLocales();
        } else if ("publishCascade".equals(action)) {
        	status = publishCascade(cascadeResourceId);
        }
        statusDto.setStatus(status);
        response.put("actions", "[]");
        response.put("meta", statusDto);
        return response;
    }

    private String publishCascade(Long cascadeResourceId) {
		String status = "failed";
    	CascadeResourceDao crDao = new CascadeResourceDao();
    	CascadeResource cr = crDao.getByKey(cascadeResourceId);
    	if (cr != null){
    		final String flowServiceURL = PropertyUtil.getProperty("flowServices");
            final String uploadUrl = PropertyUtil.getProperty("surveyuploadurl");

            if (flowServiceURL == null || "".equals(flowServiceURL)) {
                log.log(Level.SEVERE,
                        "Error trying to publish cascade. Check `flowServices` property");
                return status;
            }

            try {
                final JSONObject payload = new JSONObject();
                payload.put("cascadeResourceId", cascadeResourceId);
                payload.put("uploadUrl", uploadUrl);

                log.log(Level.INFO, "Sending cascade publish request for cascade: " + cascadeResourceId);

                final String postString = URLEncoder.encode(payload.toString(), "UTF-8");
                log.log(Level.INFO, "POSTing to: " + flowServiceURL);
                log.log(Level.INFO, "POST string: " + postString);

                final String response = new String(HttpUtil.doPost(flowServiceURL
                        + "/publish_cascade", postString), "UTF-8");

                log.log(Level.INFO, "Response from server: " + response);
                status = "publish requested";
                cr.setVersion(cr.getVersion() + 1);
                cr.setPublished(true);
                crDao.save(cr);
            } catch (Exception e) {
                log.log(Level.SEVERE,
                        "Error publishing cascade: " + e.getMessage(), e);
            }
    	}
		return status;
	}

	/**
     * Used to create test locales. The only field populated is surveyId, which is set to 1. To be
     * used only to test clustering during development in order to speed this up, it is advisable to
     * comment out the code in SurveyalRestServlet which computes the geoplace while running this
     * method.
     **/
    private String createTestLocales() {
        double latc;
        double lonc;
        double lat;
        double lon;

        SurveyInstanceDAO sDao = new SurveyInstanceDAO();
        Random generator = new Random();
        // create random points, in clusters.
        for (int i = 0; i < 1; i++) {
            latc = generator.nextDouble() * 120 - 60;
            lonc = generator.nextDouble() * 360 - 180;
            for (int j = 0; j < 100; j++) {
                SurveyInstance newSI = new SurveyInstance();
                newSI.setSurveyId(1L);
                newSI.setCollectionDate(new Date());
                newSI = sDao.save(newSI);
                QuestionAnswerStore newQAS = new QuestionAnswerStore();
                newQAS.setSurveyInstanceId(newSI.getKey().getId());
                newQAS.setType("GEO");
                newQAS.setCollectionDate(new Date());
                lat = latc + generator.nextDouble() * 10 - 5;
                lon = lonc + generator.nextDouble() * 10 - 5;
                String geoloc = lat + "|" + lon + "|" + 0 + "|" + "aaaaaa";
                newQAS.setValue(geoloc);
                newQAS = sDao.save(newQAS);

                Queue queue = QueueFactory.getDefaultQueue();
                queue.add(TaskOptions.Builder
                        .withUrl("/app_worker/surveyalservlet")
                        .param(SurveyalRestRequest.ACTION_PARAM,
                                SurveyalRestRequest.INGEST_INSTANCE_ACTION)
                        .param(SurveyalRestRequest.SURVEY_INSTANCE_PARAM,
                                newSI.getKey().getId() + ""));
            }
        }
        return "ok";
    }

    /**
     * runs over all surveydLocale objects, and populates: the Geocells field based on the latitude
     * and longitude. New surveyedLocales will have these fields populated automatically, this
     * method is to update legacy data. This method is invoked as a URL request:
     * http://..../rest/actions?action=populateGeocellsForLocale Clusters are not automatically
     * computed.This is done by 1) deleting all the cluster objects by hand 2) running
     * recomputeLocaleClusters in the dataProcessorRestServlet.
     **/
    private String computeGeocellsForLocales() {
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/app_worker/surveyalservlet")
                .param(SurveyalRestRequest.ACTION_PARAM,
                        SurveyalRestRequest.POP_GEOCELLS_FOR_LOCALE_ACTION)
                .param("cursor", ""));
        return "Done";
    }

    // remove zero minVal and maxVal values
    // that were the result of a previous bug
    private String removeZeroMinMaxValues() {
        List<Question> questions = questionDao.list(Constants.ALL_RESULTS);
        int counter = 0;
        if (questions != null) {
            Double epsilon = 0.000001;
            for (Question q : questions) {
                if (q.getMinVal() != null && q.getMaxVal() != null) {
                    if (Math.abs(q.getMinVal()) < epsilon
                            && Math.abs(q.getMaxVal()) < epsilon) {
                        q.setMinVal(null);
                        q.setMaxVal(null);
                        questionDao.save(q);
                        counter += 1;
                    }
                }
            }
        }
        return "updated " + counter + " questions";
    }

    @SuppressWarnings("unused")
    private String recomputeSurveyInstanceSummaries() {
        List<Survey> surveys = surveyDao.list(Constants.ALL_RESULTS);
        String status = "failed";
        if (surveys != null) {
            SurveyInstanceSummary sis = null;
            SurveyInstanceSummaryDao sisDao = new SurveyInstanceSummaryDao();
            for (Survey s : surveys) {

                // need to do it per page
                Iterable<Entity> siList = null;
                SurveyInstanceDAO dao = new SurveyInstanceDAO();
                siList = dao.listSurveyInstanceKeysBySurveyId(s.getKey()
                        .getId());

                Long count = 0L;
                for (Entity si : siList) {
                    count++;
                }

                sis = sisDao.findBySurveyId(s.getKey().getId());

                if (sis == null) {
                    sis = new SurveyInstanceSummary();
                    sis.setCount(count);
                    sis.setSurveyId(s.getKey().getId());
                } else {
                    sis.setCount(count);
                }
                sisDao.save(sis);
            }
            status = "success";
        }
        return status;
    }

    private String publishSurvey(Long surveyId) {
        SurveyServiceImpl surveyService = new SurveyServiceImpl();
        surveyService.publishSurveyAsync(surveyId);
        return "publishing requested";
    }

    private String fixOptions2Values() {
        Queue queue = QueueFactory.getDefaultQueue();
        TaskOptions options = TaskOptions.Builder
                .withUrl("/app_worker/dataprocessor")
                .param(DataProcessorRequest.ACTION_PARAM,
                        DataProcessorRequest.FIX_OPTIONS2VALUES_ACTION);
        queue.add(options);

        return "fixing opions to values in surveyInstances requested";
    }

    private String generateBootstrapFile(Long[] surveyIdList,
            String dbInstructions, String notificationEmail) {

        StringBuilder buf = new StringBuilder();

        if (surveyIdList != null && surveyIdList[0] != null) {
            for (int i = 0; i < surveyIdList.length; i++) {
                if (i > 0) {
                    buf.append(BootstrapGeneratorRequest.DELMITER);
                }
                buf.append(String.valueOf(surveyIdList[i]));
            }
        }

        Queue queue = QueueFactory.getQueue("background-processing");
        queue.add(TaskOptions.Builder
                .withUrl("/app_worker/bootstrapgen")
                .param(BootstrapGeneratorRequest.ACTION_PARAM,
                        BootstrapGeneratorRequest.GEN_ACTION)
                .param(BootstrapGeneratorRequest.SURVEY_ID_LIST_PARAM,
                        buf.toString())
                .param(BootstrapGeneratorRequest.EMAIL_PARAM, notificationEmail)
                .param(BootstrapGeneratorRequest.DB_PARAM,
                        dbInstructions != null ? dbInstructions : ""));
        return "_request_submitted_email_will_be_sent";
    }

    /**
     * Create datastore entry for new apk version object called as:
     * http://host/rest/actions?action=newApkVersion&version=x.y.z appCode and deviceType properties
     * are defaults.
     * 
     * @Param version
     */
    private String newApkVersion(String version) {
        Properties props = System.getProperties();
        String apkS3Path = props.getProperty("apkS3Path");
        // apkS3Path property in appengine-web.xml has a trailing slash
        apkS3Path += SystemProperty.applicationId.get() + "/";

        if (version != null && version.length() > 0 && apkS3Path != null && apkS3Path.length() > 0) {
            DeviceApplicationDao daDao = new DeviceApplicationDao();
            DeviceApplication da = new DeviceApplication();
            da.setAppCode("fieldSurvey");
            da.setDeviceType("androidPhone");
            da.setVersion(version);
            da.setFileName(apkS3Path + "fieldsurvey-" + version + ".apk");
            daDao.save(da);
            return da.getFileName();
        } else {
            return "";
        }
    }
}
