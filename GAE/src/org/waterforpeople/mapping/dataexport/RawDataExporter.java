/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.dataexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.framework.dataexport.applet.AbstractDataExporter;

/**
 * exports raw data based on a date
 *
 * @author Christopher Fagiani
 */
public class RawDataExporter extends AbstractDataExporter {

    private static final Logger log = Logger.getLogger(RawDataExporter.class);
    private static final String IMAGE_PREFIX = "http://waterforpeople.s3.amazonaws.com/images/";

    private String serverBase;
    private String surveyId;
    private String imgPrefix = null;
    private String apiKey;

    public static final String SURVEY_ID = "surveyId";
    private Map<String, QuestionDto> questionMap;
    private List<String> keyList;

    @Override
    @SuppressWarnings("unchecked")
    public void export(Map<String, String> criteria, File fileName,
            String serverBase, Map<String, String> options) {
        this.serverBase = serverBase;
        surveyId = criteria.get(SURVEY_ID);
        imgPrefix = options.get("imgPrefix");
        apiKey = criteria.get("apiKey");

        Writer pw = null;
        log.debug("In CSV exporter");
        final String apiKey = criteria.get("apiKey");
        try {
            Object[] results = BulkDataServiceClient.loadQuestions(surveyId,
                    serverBase, apiKey);
            if (results != null) {
                keyList = (List<String>) results[0];
                questionMap = (Map<String, QuestionDto>) results[1];
                pw = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(fileName), "UTF8"));
                writeHeader(pw, questionMap);
                exportInstances(pw, keyList);
            } else {
                log.error("Error getting questions");
            }
        } catch (Exception e) {
            log.error("Error exporting CSV:" + e.getMessage(), e);
        } finally {
            if (pw != null) {
                try {
                    pw.close();
                } catch (IOException e) {
                    log.error("Could not close writer: " + e.getMessage(), e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void export(String serverBase, Long surveyIdentifier, Writer pw) {
        try {
            this.surveyId = surveyIdentifier.toString();
            this.serverBase = serverBase;
            Object[] results = BulkDataServiceClient.loadQuestions(surveyId,
                    serverBase, apiKey);
            if (results != null) {
                keyList = (List<String>) results[0];
                questionMap = (Map<String, QuestionDto>) results[1];
                writeHeader(pw, questionMap);
                exportInstances(pw, keyList);
            } else {
                log.error("Error getting questions");
            }
        } catch (Exception e) {
            log.error("Error exporting: " + e.getMessage(), e);
        }
    }

    private void writeHeader(Writer pw, Map<String, QuestionDto> questions)
            throws Exception {
        pw.write("Instance\tSubmission Date\tSubmitter\tDuration");
        if (keyList != null) {
            for (String key : keyList) {
                pw.write("\t");
                if (questions.get(key).getType() != null
                        && QuestionType.GEO == questions.get(key).getType()) {
                    pw.write(key
                            + "|"
                            + "Latitude"
                            + "\t--GEOLON--|Longitude\t--GEOELE--|Elevation\t--GEOCODE--|Geo Code");
                } else {
                    String questionText = questions.get(key).getText()
                            .replaceAll("\n", " ").trim();
                    questionText = questionText.replaceAll("\r", " ").trim();
                    pw.write(key + "|" + questionText);
                }
            }
        }
        pw.write("\n");
    }

    private void exportInstances(Writer pw, List<String> idList)
            throws Exception {
        Map<String, String> instances = BulkDataServiceClient.fetchInstanceIds(
                surveyId, serverBase, apiKey, false, null, null, null);
        if (instances != null) {
            String imagePrefix = imgPrefix != null ? imgPrefix : IMAGE_PREFIX;

            if (imagePrefix != null && !imagePrefix.endsWith("/")) {
                imagePrefix = imagePrefix + "/";
            }

            int i = 0;
            for (Entry<String, String> instanceEntry : instances.entrySet()) {
                String instanceId = instanceEntry.getKey();
                String dateString = instanceEntry.getValue();
                if (instanceId != null && instanceId.trim().length() > 0) {
                    try {
                        Map<String, String> responses = new HashMap<>();
                        /*
                         * BulkDataServiceClient.fetchQuestionResponses(instanceId, serverBase,
                         * apiKey);
                         */

                        if (responses != null && responses.size() > 0) {
                            pw.write(instanceId);
                            pw.write("\t");
                            pw.write(dateString);
                            pw.write("\t");
                            SurveyInstanceDto dto = BulkDataServiceClient
                                    .findSurveyInstance(
                                            Long.parseLong(instanceId.trim()),
                                            serverBase, apiKey);
                            if (dto != null) {
                                String name = dto.getSubmitterName();
                                if (name != null) {
                                    pw.write(dto.getSubmitterName()
                                            .replaceAll("\n", " ")
                                            .replaceAll("\t", " ").trim());
                                }
                                pw.write("\t");
                                Long duration = dto.getSurveyalTime();
                                if (duration != null) {
                                    try {
                                        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                                        df.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
                                        pw.write(df.format(duration * 1000));
                                    } catch (Exception e) {
                                        pw.write("");
                                    }
                                }
                            }
                            for (String key : idList) {
                                String val = responses.get(key);
                                pw.write("\t");
                                if (val != null) {
                                    QuestionDto qdto = questionMap != null ? questionMap.get(key)
                                            : null;
                                    if (qdto != null && QuestionType.GEO == qdto.getType()) {
                                        String[] geoParts = val.split("\\|");
                                        int count = 0;
                                        for (count = 0; count < Math.min(geoParts.length, 3); count++) {
                                            if (count > 0) {
                                                pw.write("\t");
                                            }
                                            pw.write(geoParts[count]);
                                        }
                                        // now handle any missing fields
                                        for (int j = count; j < 3; j++) {
                                            pw.write("\t");
                                        }
                                    } else {
                                        if (qdto != null
                                                && (QuestionType.PHOTO == qdto.getType() || QuestionType.VIDEO == qdto
                                                        .getType())) {
                                            final int filenameIndex = val.lastIndexOf("/") + 1;
                                            if (filenameIndex > 0 && filenameIndex < val.length()) {
                                                val = imagePrefix + val.substring(filenameIndex);
                                            }
                                        }
                                        pw.write(val.replaceAll("\n", " ")
                                                .trim());
                                    }
                                }
                            }

                            pw.write("\n");
                            pw.flush();
                            i++;
                            log.debug("Row: " + i);
                            responses = null;
                        }
                    } catch (Exception ex) {
                        log.error("Swallow the exception for now and continue", ex);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        RawDataExporter exporter = new RawDataExporter();
        Map<String, String> criteria = new HashMap<String, String>();
        Map<String, String> options = new HashMap<String, String>();
        criteria.put(SURVEY_ID, args[2]);
        criteria.put("apiKey", args[3]);
        exporter.export(criteria, new File(args[0]), args[1], options);
    }

}
