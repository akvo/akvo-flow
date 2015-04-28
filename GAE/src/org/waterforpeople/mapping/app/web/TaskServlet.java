/*
 *  Copyright (C) 2010-2015 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.waterforpeople.mapping.app.web.dto.TaskRequest;
import org.waterforpeople.mapping.dao.DeviceFilesDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.ProcessingAction;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.Status.StatusCode;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.domain.response.FormInstance;
import org.waterforpeople.mapping.domain.response.Response;
import org.waterforpeople.mapping.helper.AccessPointHelper;
import org.waterforpeople.mapping.helper.SurveyEventHelper;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.MailUtil;
import com.gallatinsystems.common.util.S3Util;
import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.exceptions.SignedDataException;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.messaging.dao.MessageDao;
import com.gallatinsystems.messaging.domain.Message;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyUtils;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.app.web.SurveyalRestRequest;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class TaskServlet extends AbstractRestApiServlet {

    private static final String TSV_FILENAME = "data.txt";
    private static final String JSON_FILENAME = "data.json";
    private static String DEVICE_FILE_PATH;
    private static String FROM_ADDRESS;
    private static String BUCKET_NAME;
    private static final long serialVersionUID = -2607990749512391457L;
    private static final Logger log = Logger.getLogger(TaskServlet.class
            .getName());
    private AccessPointHelper aph;
    private SurveyInstanceDAO siDao;
    private final static String EMAIL_FROM_ADDRESS_KEY = "emailFromAddress";
    private TreeMap<String, String> recepientList = null;
    private static final String OBJECTKEY_PREFIX = "devicezip/";

    public TaskServlet() {
        DEVICE_FILE_PATH = com.gallatinsystems.common.util.PropertyUtil
                .getProperty("deviceZipPath");
        FROM_ADDRESS = com.gallatinsystems.common.util.PropertyUtil
                .getProperty(EMAIL_FROM_ADDRESS_KEY);
        BUCKET_NAME = com.gallatinsystems.common.util.PropertyUtil.getProperty("s3bucket");
        aph = new AccessPointHelper();
        siDao = new SurveyInstanceDAO();
        recepientList = MailUtil.loadRecipientList();
    }

    /**
     * Retrieve the file from S3 storage and persist the data to the data store
     *
     * @param fileProcessTaskRequest
     */
    private List<SurveyInstance> processFile(TaskRequest fileProcessTaskRequest) {
        String fileName = fileProcessTaskRequest.getFileName();
        String phoneNumber = fileProcessTaskRequest.getPhoneNumber();
        String imei = fileProcessTaskRequest.getImei();
        String checksum = fileProcessTaskRequest.getChecksum();
        String url = DEVICE_FILE_PATH + fileName;

        // add private ACL to zip file
        try {
            S3Util.putObjectAcl(BUCKET_NAME, OBJECTKEY_PREFIX + fileName, S3Util.ACL.PRIVATE);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error trying to secure zip file: " + e.getMessage(), e);
        }

        // attempt retrieve and extract zip file
        URLConnection conn = null;
        BufferedInputStream deviceZipFileInputStream = null;
        ZipInputStream deviceFilesStream = null;
        Map<String, String> files = null;
        final ArrayList<SurveyInstance> emptyList = new ArrayList<SurveyInstance>();

        try {
            conn = S3Util.getConnection(BUCKET_NAME, OBJECTKEY_PREFIX + fileName);
            deviceZipFileInputStream = new BufferedInputStream(conn.getInputStream());
            deviceFilesStream = new ZipInputStream(deviceZipFileInputStream);
            files = extract(deviceFilesStream);
        } catch (Exception e) {
            // catchall
            int retry = fileProcessTaskRequest.getRetry();
            if (++retry > Constants.MAX_TASK_RETRIES) {
                String message = String.format("Failed to process file (%s) after (%s) retries.",
                        url, Constants.MAX_TASK_RETRIES);
                sendMail(fileProcessTaskRequest, message);
                log.severe(message + "\n\n" + e.getMessage());
                return emptyList;
            }

            // retry processing
            fileProcessTaskRequest.setRetry(retry);
            rescheduleTask(fileProcessTaskRequest);
            log.log(Level.WARNING,
                    "Failed to process zip file: Rescheduling... " + url + " : " + e.getMessage());
            return emptyList;
        } finally {
            try {
                deviceFilesStream.close();
            } catch (IOException e) {
                log.log(Level.WARNING, "Failed to close zip input stream");
            }
        }

        // create device file entity
        DeviceFilesDao dfDao = new DeviceFilesDao();

        List<DeviceFiles> dfList = null;
        DeviceFiles deviceFile = null;
        dfList = dfDao.listByUri(url);
        if (dfList != null && dfList.size() > 0) {
            deviceFile = dfList.get(0);
        }
        if (deviceFile == null) {
            deviceFile = new DeviceFiles();
        }
        deviceFile.setProcessDate(getNowDateTimeFormatted());
        deviceFile.setProcessedStatus(StatusCode.IN_PROGRESS);
        deviceFile.setURI(url);
        deviceFile.setPhoneNumber(phoneNumber);
        deviceFile.setImei(imei);
        deviceFile.setChecksum(checksum);
        deviceFile.setUploadDateTime(new Date());
        
        final List<SurveyInstance> surveyInstances = new ArrayList<>();
        if (files.containsKey(JSON_FILENAME)) {
            // Process JSON-formatted response.
            SurveyInstance instance = fromJSON(files.get(JSON_FILENAME));
            if (instance != null) {
                surveyInstances.add(instance);
            }
        } else if (files.containsKey(TSV_FILENAME)) {
            // Process TSV-formatted response (can contain multiple instances).
            Map<String, List<String>> data = splitSurveyInstances(files.get(TSV_FILENAME));
            for (String id : data.keySet()) {
                SurveyInstance instance = fromTSV(data.get(id));
                if (instance != null) {
                    surveyInstances.add(instance);
                }
            }
        }
        
        if (surveyInstances.isEmpty()) {
            // No data
            String message = "Error empty file: " + deviceFile.getURI();
            log.log(Level.SEVERE, message);
            deviceFile.setProcessedStatus(StatusCode.PROCESSED_WITH_ERRORS);
            deviceFile.addProcessingMessage(message);
            sendMail(fileProcessTaskRequest, message);
        } else {
            deviceFile.setProcessedStatus(StatusCode.PROCESSED_NO_ERRORS);
            for (SurveyInstance si : surveyInstances) {
                si = siDao.save(si, deviceFile);
                // Fire a survey event
                SurveyEventHelper.fireEvent(SurveyEventHelper.SUBMISSION_EVENT,
                        si.getSurveyId(), si.getKey().getId());
            }
        }
        
        dfDao.save(deviceFile);
        if (dfList != null) {
            for (DeviceFiles dfitem : dfList) {
                dfitem.setProcessedStatus(deviceFile.getProcessedStatus());
            }
        }
        dfDao.save(dfList);

        return surveyInstances;
    }
    
    private SurveyInstance fromJSON(String data) {
        FormInstance formInstance = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            formInstance = mapper.readValue(data, FormInstance.class);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error mapping JSON data: " + e.getMessage(), e);
            return null;
        }
        
        SurveyInstance si = new SurveyInstance();
        si.setUserID(1L);
        si.setCollectionDate(new Date(formInstance.getSubmissionDate()));
        si.setSubmitterName(formInstance.getUsername());
        si.setDeviceIdentifier(formInstance.getDeviceId());
        si.setSurveyalTime(formInstance.getDuration());
        si.setSurveyedLocaleIdentifier(formInstance.getDataPointId());
        si.setSurveyId(formInstance.getFormId());
        si.setUuid(formInstance.getUUID());
        si.setQuestionAnswersStore(new ArrayList<QuestionAnswerStore>());
        
        // Process form responses
        for (Response response : formInstance.getResponses()) {
            QuestionAnswerStore qas = new QuestionAnswerStore();
            qas.setSurveyId(si.getSurveyId());
            qas.setQuestionID(response.getQuestionId());
            qas.setCollectionDate(si.getCollectionDate());
            qas.setType(response.getAnswerType());
            qas.setValue(response.getValue());
            
            si.getQuestionAnswersStore().add(qas);
        }
        
        return si;
    }
    
    private SurveyInstance fromTSV(List<String> data) {
        final SurveyInstance si = new SurveyInstance();
        si.setUserID(1L);
        si.setQuestionAnswersStore(new ArrayList<QuestionAnswerStore>());
        
        boolean first = true;
        for (String line : data) {
            final String[] parts = line.split("\t");
            if (parts.length < 12) {
                return null;
            }
            
            if (first) {
                try {
                    si.setSurveyId(Long.parseLong(parts[0].trim()));
                    si.setCollectionDate(new Date(new Long(parts[7].trim())));
                } catch (NumberFormatException e) {
                    log.log(Level.SEVERE, "Could not parse line: " + line, e);
                    return null;
                }
                si.setSubmitterName(parts[5].trim());
                si.setDeviceIdentifier(parts[8].trim());
                si.setUuid(parts[11].trim());
                
                // Time and LocaleID. Old app versions might not include these columns.
                if (parts.length > 12 && si.getSurveyalTime() == null) {
                    try {
                        si.setSurveyalTime(Long.valueOf(parts[12].trim()));
                    } catch (NumberFormatException e) {
                        log.log(Level.WARNING, "Surveyal time column is not a number", e);
                    }
                }
                if (parts.length > 13 && si.getSurveyedLocaleIdentifier() == null) {
                    si.setSurveyedLocaleIdentifier(parts[13].trim());
                }
                
                first = false;
            }

            QuestionAnswerStore qas = new QuestionAnswerStore();
            qas.setSurveyId(si.getSurveyId());
            qas.setQuestionID(parts[2].trim());
            qas.setType(parts[3].trim());
            qas.setCollectionDate(si.getCollectionDate());
            qas.setValue(parts[4].trim());
            qas.setScoredValue(parts[9].trim());
            qas.setStrength(parts[10].trim());
            si.getQuestionAnswersStore().add(qas);
        }

        return si;
    }
    
    public static Map<String, String> extract(ZipInputStream deviceZipFileInputStream) 
            throws ZipException, IOException, SignedDataException {
        Map<String, String> files = new HashMap<>();
        ZipEntry entry;
        while ((entry = deviceZipFileInputStream.getNextEntry()) != null) {
            final String name = entry.getName();
            log.info("Unzipping: " + name);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int size;
            while ((size = deviceZipFileInputStream.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, size);
            }
            
            // Skip empty files
            if (out.size() > 0) {
                files.put(name, out.toString("UTF-8"));
            }
        }
        return files;
    }
    
    /**
     * Group lines by survey instance.
     * @param content
     * @return Map containing unique IDs as keys, and a list of lines per instance.
     */
    private Map<String, List<String>> splitSurveyInstances(String content) {
        Map<String, List<String>> instances = new HashMap<>();
        for (String line : content.split("\n")) {
            line = line.replaceAll("\u0000", "");
            String[] parts = line.split("\t");
            if (parts.length < 5) {
                parts = line.split(",");
            }
            
            String id = parts.length >= 2 ? parts[1] : null;
            if (id != null) {
                List<String> lines = instances.get(id);
                if (lines == null) {
                    lines = new ArrayList<>();
                }
                lines.add(line);
                instances.put(id, lines);
            }
        }
            
        return instances;
    }

    /**
     * Requeue the file processing task for execution after TASK_RETRY_INTERVAL mins
     *
     * @param fileProcessingRequest
     */
    private void rescheduleTask(TaskRequest fileProcessingRequest) {
        Queue defaultQueue = QueueFactory.getDefaultQueue();
        TaskOptions options = TaskOptions.Builder.withUrl("/app_worker/task")
                .param(TaskRequest.ACTION_PARAM, TaskRequest.PROCESS_FILE_ACTION)
                .param(TaskRequest.TASK_RETRY_PARAM, fileProcessingRequest.getRetry().toString())
                .param(TaskRequest.FILE_NAME_PARAM, fileProcessingRequest.getFileName())
                .countdownMillis(Constants.TASK_RETRY_INTERVAL);

        if (fileProcessingRequest.getPhoneNumber() != null) {
            options.param(TaskRequest.PHONE_NUM_PARAM, fileProcessingRequest.getPhoneNumber());
        }

        if (fileProcessingRequest.getImei() != null) {
            options.param(TaskRequest.IMEI_PARAM, fileProcessingRequest.getImei());
        }

        if (fileProcessingRequest.getChecksum() != null) {
            options.param(TaskRequest.CHECKSUM_PARAM, fileProcessingRequest.getChecksum());
        }

        if (fileProcessingRequest.getOffset() != null) {
            options.param(TaskRequest.OFFSET_PARAM, fileProcessingRequest.getOffset().toString());
        }

        defaultQueue.add(options);
    }

    /**
     * Send an email regarding file processing status/outcome
     *
     * @param fileProcessingRequest
     * @param subject
     * @param messageBody
     */
    private void sendMail(TaskRequest fileProcessingRequest, String body) {
        String fileName = fileProcessingRequest.getFileName();
        String subject = "Device File Processing Error: " + fileName;
        String messageBody = DEVICE_FILE_PATH + fileName + "\n" + body;

        MailUtil.sendMail(FROM_ADDRESS, "FLOW", recepientList, subject, messageBody);
    }

    private String getNowDateTimeFormatted() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
        java.util.Date date = new java.util.Date();
        String dateTime = dateFormat.format(date);
        return dateTime;
    }

    private ProcessingAction dispatch(String surveyKey) {
        ProcessingAction pa = new ProcessingAction();

        pa.setAction("addAccessPoint");
        pa.setDispatchURL("/worker/task");
        pa.addParam("surveyId", surveyKey);
        return pa;
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new TaskRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest request) throws Exception {
        RestResponse response = new RestResponse();
        TaskRequest taskReq = (TaskRequest) request;
        if (TaskRequest.PROCESS_FILE_ACTION.equalsIgnoreCase(taskReq
                .getAction())) {
            ingestFile(taskReq);
        } else if (TaskRequest.ADD_ACCESS_POINT_ACTION.equalsIgnoreCase(taskReq
                .getAction())) {
            addAccessPoint(taskReq);
        }
        return response;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        getResponse().setStatus(200);
    }

    private void addAccessPoint(TaskRequest req) {
        Long surveyInstanceId = req.getSurveyId();
        log.info("Received Task Queue calls for surveyInstanceId: "
                + surveyInstanceId);

        aph.processSurveyInstance(surveyInstanceId.toString());
    }

    /**
     * handles the callback from the device indicating that a new data file is available. This
     * method will call processFile to retrieve the file and persist the data to the data store it
     * will then add access points for each water point in the survey responses.
     *
     * @param req
     */
    @SuppressWarnings("rawtypes")
    private void ingestFile(TaskRequest req) {
        if (req.getFileName() != null) {
            log.info("	Task->processFile");
            List<SurveyInstance> surveyInstances = null;
            try {
                surveyInstances = processFile(req);
            } catch (Exception e) {
                String message = "Failed to process zip file:" + req.getFileName() + " : "
                        + e.getMessage();
                log.severe(message);
                sendMail(req, message);
                surveyInstances = new ArrayList<SurveyInstance>();
            }
            Map<Long, Survey> surveyMap = new HashMap<Long, Survey>();
            SurveyDAO surveyDao = new SurveyDAO();
            Queue defaultQueue = QueueFactory.getDefaultQueue();
            for (SurveyInstance instance : surveyInstances) {
                Survey s = surveyMap.get(instance.getSurveyId());
                if (s == null) {
                    s = surveyDao.getById(instance.getSurveyId());
                    surveyMap.put(instance.getSurveyId(), s);
                }
                if (s != null && s.getRequireApproval() != null && s.getRequireApproval()) {
                    // if the survey requires approval, don't run any of the
                    // processors
                    instance.setApprovedFlag("False");
                    continue;
                } else {
                    ProcessingAction pa = dispatch(instance.getKey().getId()
                            + "");
                    TaskOptions options = TaskOptions.Builder.withUrl(pa.getDispatchURL());
                    Iterator it = pa.getParams().keySet().iterator();
                    while (it.hasNext()) {
                        options.param("key", (String) it.next());
                    }
                    log.info("Received Task Queue calls for surveyInstanceKey: "
                            + instance.getKey().getId() + "");
                    aph.processSurveyInstance(instance.getKey().getId() + "");

                    defaultQueue.add(TaskOptions.Builder.withUrl("/app_worker/surveyalservlet")
                            .param(
                                    SurveyalRestRequest.ACTION_PARAM,
                                    SurveyalRestRequest.INGEST_INSTANCE_ACTION).param(
                                    SurveyalRestRequest.SURVEY_INSTANCE_PARAM,
                                    instance.getKey().getId() + ""));
                }
            }
            SurveyUtils.notifyReportService(surveyMap.keySet(), "invalidate");

            MessageDao msgDao = new MessageDao();
            Message message = new Message();
            message.setShortMessage(req.getFileName() + " processed - Surveys: "
                    + surveyMap.keySet());

            if (req.getFileName().startsWith("wfpGenerated")) {
                message.setActionAbout("bulkProcessed");
            } else {
                message.setActionAbout("fileProcessed");
            }

            if (surveyMap.keySet().size() == 1) {
                Survey s = surveyMap.values().iterator().next();
                if (s != null) {
                    message.setObjectId(s.getKey().getId());
                    message.setObjectTitle(s.getPath() + "/" + s.getName());
                }
            }
            msgDao.save(message);
        }
    }

}
