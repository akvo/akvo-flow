/*
 *  Copyright (C) 2013-2019 Stichting Akvo (Akvo Foundation)
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.web.SurveyAssemblyServlet;
import org.waterforpeople.mapping.app.web.dto.BootstrapGeneratorRequest;
import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.dao.DeviceApplicationDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.DeviceApplication;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.surveyal.app.web.SurveyalRestRequest;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.utils.SystemProperty;

@Controller
@RequestMapping("/actions")
public class ActionRestService {

    private static final Logger logger = Logger.getLogger(ActionRestService.class.getName());

    private SurveyDAO surveyDao = new SurveyDAO();

    private SurveyGroupDAO surveyGroupDao = new SurveyGroupDAO();

    private QuestionDao questionDao = new QuestionDao();

    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> doAction(
            @RequestParam(value = "action", defaultValue = "") String action,
            @RequestParam(value = "surveyId", defaultValue = "") Long surveyId,
            @RequestParam(value = "cascadeResourceId", defaultValue = "") Long cascadeResourceId,
            @RequestParam(value = "surveyIds[]", defaultValue = "") Long[] surveyIds,
            @RequestParam(value = "email", defaultValue = "") String email,
            @RequestParam(value = "version", defaultValue = "") String version,
            @RequestParam(value = "dbInstructions", defaultValue = "") String dbInstructions,
            @RequestParam(value = "targetId", defaultValue = "") Long targetId,
            @RequestParam(value = "folderId", defaultValue = "") Long folderId,
            @RequestParam(value = "immutable", defaultValue = "false") Boolean immutable) {
        String status = "failed";
        String message = "";
        final Map<String, Object> response = new HashMap<String, Object>();
        RestStatusDto statusDto = new RestStatusDto();

        // perform the required action
        if ("publishSurvey".equals(action) && surveyId != null) {
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
        } else if ("publishCascade".equals(action)) {
            status = SurveyUtils.publishCascade(cascadeResourceId);
        } else if ("copyProject".equals(action)) {
            status = copyProject(targetId, folderId, false);
        } else if ("copyTemplate".equals(action)) {
            status = copyProject(targetId, folderId, true);
        }

        statusDto.setStatus(status);
        response.put("actions", "[]");
        response.put("meta", statusDto);
        return response;
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


    private String publishSurvey(Long surveyId) {
        SurveyAssemblyServlet.runAsTask(surveyId);
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

    /**
     * Copy a survey via "copy" -> the copied survey will keep all the original properties. If the original survey is
     * a template, the copy will also be a template
     * or via the "Create survey / from template" -> the copied survey will NOT be a template
     * @param targetId
     * @param folderId
     * @param createFromTemplate if it's a "Create survey / from template" action
     * @return
     */
    private String copyProject(Long targetId, Long folderId, boolean createFromTemplate) {

        SurveyGroup projectSource = surveyGroupDao.getByKey(targetId);
        SurveyGroup projectParent = null;
        if (folderId != null) {
            projectParent = surveyGroupDao.getByKey(folderId);
        }
        if (projectSource == null) {
            logger.log(Level.WARNING,
                    String.format("Failed to copy project %s to folder %s", targetId, folderId));
            return "failed";
        }
        SurveyGroup projectCopy = new SurveyGroup();

        BeanUtils.copyProperties(projectSource, projectCopy, Constants.EXCLUDED_PROPERTIES);
        // if set, projectCopy.newLocaleSurveyId is now wrong

        projectCopy.setCode(projectSource.getCode() + " copy");
        projectCopy.setName(projectSource.getName() + " copy");
        String parentPath = null;
        if (projectParent != null) {
            parentPath = projectParent.getPath();
        } else {
            parentPath = ""; // root folder
        }
        projectCopy.setPath(parentPath + "/" + projectCopy.getName());
        projectCopy.setParentId(folderId);
        if (createFromTemplate) {
            //the survey created from a template is never a template
            projectCopy.setTemplate(false);
        }
        //else: when using a regular "copy" we maintain the original state of the survey either template or not

        boolean isCopiedToDifferentFolder = projectSource.getParentId() != null && folderId != null
                && !projectSource.getParentId().equals(folderId);
        if (isCopiedToDifferentFolder) {
            // reset ancestorIds when copying to a different folder
            projectCopy.setAncestorIds(SurveyUtils.retrieveAncestorIds(projectCopy));
        }
        projectCopy.setPublished(false);

        SurveyGroup savedProjectCopy = surveyGroupDao.save(projectCopy); // saves

        List<Survey> sourceSurveys = surveyDao.listSurveysByGroup(targetId);

        List<Long> surveysAncestorIds = new ArrayList<Long>(savedProjectCopy.getAncestorIds());
        surveysAncestorIds.add(savedProjectCopy.getKey().getId());

        for (Survey sourceSurvey : sourceSurveys) {
            SurveyDto surveyDto = new SurveyDto();
            surveyDto.setCode(sourceSurvey.getCode());
            surveyDto.setName(sourceSurvey.getName());
            surveyDto.setPath(projectCopy.getPath() + "/" + sourceSurvey.getName());
            surveyDto.setSurveyGroupId(savedProjectCopy.getKey().getId());
            Survey surveyCopy = SurveyUtils.copySurvey(sourceSurvey, surveyDto);
            surveyCopy.setAncestorIds(surveysAncestorIds);
            surveyCopy.setSurveyGroupId(savedProjectCopy.getKey().getId());
            long copyId = surveyDao.save(surveyCopy).getKey().getId();
            if (isRegistrationFormId(sourceSurvey.getKey().getId(),
                    projectSource.getNewLocaleSurveyId())) {
                // original was the registration survey for its survey group
                savedProjectCopy.setNewLocaleSurveyId(copyId); // fix it
                surveyGroupDao.save(savedProjectCopy);
            }
        }
        return "success";

    }

    private static boolean isRegistrationFormId(Long sourceFormId,
            Long sourceProjectRegistrationFormId) {
        return sourceFormId != null && sourceProjectRegistrationFormId != null
                && sourceFormId.equals(sourceProjectRegistrationFormId);
    }
}
