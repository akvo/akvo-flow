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

package org.waterforpeople.mapping.dataexport;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.common.util.JFreechartChartUtil;
import com.gallatinsystems.common.util.StringUtil;
import com.gallatinsystems.framework.dataexport.applet.ProgressDialog;

/**
 * Enhancement of the SurveySummaryExporter to support writing to Excel and including chart images.
 *
 * @author Christopher Fagiani
 */
public class GraphicalSurveySummaryExporter extends SurveySummaryExporter {

    private static final Logger log = Logger.getLogger(GraphicalSurveySummaryExporter.class);

    private static final int MAX_COL = 255;
    private static final String IMAGE_PREFIX_OPT = "imgPrefix";
    private static final String DO_ROLLUP_OPT = "performRollup";
    private static final String LOCALE_OPT = "locale";
    private static final String TYPE_OPT = "exportMode";
    private static final String RAW_ONLY_TYPE = "RAW_DATA";
    private static final String NO_CHART_OPT = "nocharts";
    private static final String LAST_COLLECTION_OPT = "lastCollection";

    private static final String DEFAULT_IMAGE_PREFIX = "http://waterforpeople.s3.amazonaws.com/images/";

    private static final Map<String, String> REPORT_HEADER;
    private static final Map<String, String> FREQ_LABEL;
    private static final Map<String, String> PCT_LABEL;
    private static final Map<String, String> SUMMARY_LABEL;
    private static final Map<String, String> RAW_DATA_LABEL;
    private static final Map<String, String> INSTANCE_LABEL;
    private static final Map<String, String> SUB_DATE_LABEL;
    private static final Map<String, String> SUBMITTER_LABEL;
    private static final Map<String, String> DURATION_LABEL;
    private static final Map<String, String> MEAN_LABEL;
    private static final Map<String, String> MODE_LABEL;
    private static final Map<String, String> MEDIAN_LABEL;
    private static final Map<String, String> MIN_LABEL;
    private static final Map<String, String> MAX_LABEL;
    private static final Map<String, String> VAR_LABEL;
    private static final Map<String, String> STD_E_LABEL;
    private static final Map<String, String> STD_D_LABEL;
    private static final Map<String, String> TOTAL_LABEL;
    private static final Map<String, String> RANGE_LABEL;
    private static final Map<String, String> LOADING_QUESTIONS;
    private static final Map<String, String> LOADING_DETAILS;
    private static final Map<String, String> LOADING_INSTANCES;
    private static final Map<String, String> LOADING_INSTANCE_DETAILS;
    private static final Map<String, String> WRITING_SUMMARY;
    private static final Map<String, String> WRITING_RAW_DATA;
    private static final Map<String, String> WRITING_ROLLUPS;
    private static final Map<String, String> COMPLETE;
    private static final Map<String, String> LAT_LABEL;
    private static final Map<String, String> LON_LABEL;
    private static final Map<String, String> ELEV_LABEL;
    private static final Map<String, String> CODE_LABEL;
    private static final Map<String, String> IDENTIFIER_LABEL;
    private static final Map<String, String> DISPLAY_NAME_LABEL;

    private static final int CHART_WIDTH = 600;
    private static final int CHART_HEIGHT = 400;
    private static final int CHART_CELL_WIDTH = 10;
    private static final int CHART_CELL_HEIGHT = 22;
    private static final String DEFAULT_LOCALE = "en";
    private static final String DEFAULT = "default";
    private static final int FULL_STEPS = 7;
    private static final int RAW_STEPS = 5;
    private static final NumberFormat PCT_FMT = DecimalFormat
            .getPercentInstance();

    private static final DateFormat DATE_FMT = new SimpleDateFormat(
            "dd-MM-yyyy HH:mm:ss z");

    static {
        // populate all translations
        RANGE_LABEL = new HashMap<String, String>();
        RANGE_LABEL.put("en", "Range");
        RANGE_LABEL.put("es", "Distribución");

        MEAN_LABEL = new HashMap<String, String>();
        MEAN_LABEL.put("en", "Mean");
        MEAN_LABEL.put("es", "Media");

        MODE_LABEL = new HashMap<String, String>();
        MODE_LABEL.put("en", "Mode");
        MODE_LABEL.put("es", "Moda");

        MEDIAN_LABEL = new HashMap<String, String>();
        MEDIAN_LABEL.put("en", "Median");
        MEDIAN_LABEL.put("es", "Número medio");

        MIN_LABEL = new HashMap<String, String>();
        MIN_LABEL.put("en", "Min");
        MIN_LABEL.put("es", "Mínimo");

        MAX_LABEL = new HashMap<String, String>();
        MAX_LABEL.put("en", "Max");
        MAX_LABEL.put("es", "Máximo");

        VAR_LABEL = new HashMap<String, String>();
        VAR_LABEL.put("en", "Variance");
        VAR_LABEL.put("es", "Varianza");

        STD_D_LABEL = new HashMap<String, String>();
        STD_D_LABEL.put("en", "Std Deviation");
        STD_D_LABEL.put("es", "Desviación Estándar");

        STD_E_LABEL = new HashMap<String, String>();
        STD_E_LABEL.put("en", "Std Error");
        STD_E_LABEL.put("es", "Error Estándar");

        TOTAL_LABEL = new HashMap<String, String>();
        TOTAL_LABEL.put("en", "Total");
        TOTAL_LABEL.put("es", "Suma");

        REPORT_HEADER = new HashMap<String, String>();
        REPORT_HEADER.put("en", "Survey Summary Report");
        REPORT_HEADER.put("es", "Encuesta Informe Resumen");

        FREQ_LABEL = new HashMap<String, String>();
        FREQ_LABEL.put("en", "Frequency");
        FREQ_LABEL.put("es", "Frecuencia");

        PCT_LABEL = new HashMap<String, String>();
        PCT_LABEL.put("en", "Percent");
        PCT_LABEL.put("es", "Por ciento");

        SUMMARY_LABEL = new HashMap<String, String>();
        SUMMARY_LABEL.put("en", "Summary");
        SUMMARY_LABEL.put("es", "Resumen");

        RAW_DATA_LABEL = new HashMap<String, String>();
        RAW_DATA_LABEL.put("en", "Raw Data");
        RAW_DATA_LABEL.put("es", "Primas de Datos");

        INSTANCE_LABEL = new HashMap<String, String>();
        INSTANCE_LABEL.put("en", "Instance");
        INSTANCE_LABEL.put("es", "Instancia");

        SUB_DATE_LABEL = new HashMap<String, String>();
        SUB_DATE_LABEL.put("en", "Submission Date");
        SUB_DATE_LABEL.put("es", "Fecha de Presentación");

        SUBMITTER_LABEL = new HashMap<String, String>();
        SUBMITTER_LABEL.put("en", "Submitter");
        SUBMITTER_LABEL.put("es", "Peticionario");

        DURATION_LABEL = new HashMap<String, String>();
        DURATION_LABEL.put("en", "Duration");
        DURATION_LABEL.put("es", "Duración");

        LOADING_QUESTIONS = new HashMap<String, String>();
        LOADING_QUESTIONS.put("en", "Loading Questions");
        LOADING_QUESTIONS.put("es", "Cargando de preguntas");

        LOADING_DETAILS = new HashMap<String, String>();
        LOADING_DETAILS.put("en", "Loading Question Details");
        LOADING_DETAILS.put("es", "Cargando Detalles Pregunta");

        LOADING_INSTANCES = new HashMap<String, String>();
        LOADING_INSTANCES.put("en", "Loading Instances");
        LOADING_INSTANCES.put("es", "Cargando instancias");

        LOADING_INSTANCE_DETAILS = new HashMap<String, String>();
        LOADING_INSTANCE_DETAILS.put("en", "Loading Instance Details");
        LOADING_INSTANCE_DETAILS.put("es", "Cargando Datos Instancia");

        WRITING_SUMMARY = new HashMap<String, String>();
        WRITING_SUMMARY.put("en", "Writing Summary");
        WRITING_SUMMARY.put("es", "Escribiendo Resumen");

        WRITING_RAW_DATA = new HashMap<String, String>();
        WRITING_RAW_DATA.put("en", "Writing Raw Data");
        WRITING_RAW_DATA.put("es", "Escribiendo Primas de Datos");

        WRITING_ROLLUPS = new HashMap<String, String>();
        WRITING_ROLLUPS.put("en", "Writing Rollups");
        WRITING_ROLLUPS.put("es", "Escribiendo Resumen Municipales");

        COMPLETE = new HashMap<String, String>();
        COMPLETE.put("en", "Export Complete");
        COMPLETE.put("es", "Exportación Completa");

        LAT_LABEL = new HashMap<String, String>();
        LAT_LABEL.put("en", "Latitude");
        LAT_LABEL.put("es", "Latitud");

        LON_LABEL = new HashMap<String, String>();
        LON_LABEL.put("en", "Longitude");
        LON_LABEL.put("es", "Longitud");

        ELEV_LABEL = new HashMap<String, String>();
        ELEV_LABEL.put("en", "Elevation");
        ELEV_LABEL.put("es", "Elevación");

        CODE_LABEL = new HashMap<String, String>();
        CODE_LABEL.put("en", "Geo Code");
        CODE_LABEL.put("es", "Código Geo");

        IDENTIFIER_LABEL = new HashMap<String, String>();
        IDENTIFIER_LABEL.put("en", "Identifier");
        IDENTIFIER_LABEL.put("es", "Identificador");

        DISPLAY_NAME_LABEL = new HashMap<String, String>();
        DISPLAY_NAME_LABEL.put("en", "Display Name");
        DISPLAY_NAME_LABEL.put("es", "Nombre");
    }

    private CellStyle headerStyle;
    private String locale;
    private String imagePrefix;
    private String serverBase;
    private ProgressDialog progressDialog;
    private int currentStep;
    private int maxSteps;
    private boolean isFullReport;
    private boolean performGeoRollup;
    private ThreadPoolExecutor threadPool;
    private BlockingQueue<Runnable> jobQueue;
    private volatile int threadsCompleted = 0;
    private Object lock = new Object();
    private boolean generateCharts;
    private Map<Long, QuestionDto> questionsById;
    private boolean lastCollection = false;
    private boolean monitoringGroup = false;
    private List<Long> displayNameQuestionIds = new ArrayList<Long>();

    @Override
    public void export(Map<String, String> criteria, File fileName,
            String serverBase, Map<String, String> options) {
        processOptions(options);
        jobQueue = new LinkedBlockingQueue<Runnable>();
        threadPool = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS,
                jobQueue);
        if (!GraphicsEnvironment.isHeadless()) {
            progressDialog = new ProgressDialog(maxSteps, locale);
            progressDialog.setVisible(true);
        }
        questionsById = new HashMap<Long, QuestionDto>();
        currentStep = 1;
        this.serverBase = serverBase;
        PrintWriter pw = null;
        boolean useQuestionId = "true".equals(options.get("useQuestionId"));
        String from = options.get("from");
        String to = options.get("to");
        try {
            SwingUtilities.invokeLater(new StatusUpdater(currentStep++,
                    LOADING_QUESTIONS.get(locale)));

            List<SurveyGroupDto> sgs = fetchSurveyGroup(
                    criteria.get(SurveyRestRequest.SURVEY_ID_PARAM), serverBase,
                    criteria.get("apiKey"));

            if (sgs != null && !sgs.isEmpty()) {
                monitoringGroup = sgs.get(0).getMonitoringGroup();
            }

            Map<QuestionGroupDto, List<QuestionDto>> questionMap = loadAllQuestions(
                    criteria.get(SurveyRestRequest.SURVEY_ID_PARAM),
                    performGeoRollup, serverBase, criteria.get("apiKey"));
            if (questionMap != null) {
                for (List<QuestionDto> qList : questionMap.values()) {
                    for (QuestionDto q : qList) {
                        questionsById.put(q.getKeyId(), q);
                        if (q.getLocaleNameFlag() != null && q.getLocaleNameFlag()) {
                            displayNameQuestionIds.add(q.getKeyId());
                        }
                    }
                }
            }
            if (!DEFAULT_LOCALE.equals(locale) && questionMap.size() > 0) {
                // if we are using some other locale, we need to check for
                // translations
                SwingUtilities.invokeLater(new StatusUpdater(currentStep++,
                        LOADING_DETAILS.get(locale)));
                loadFullQuestions(questionMap, criteria.get("apiKey"));
            } else {
                currentStep++;
            }
            Workbook wb = null;
            if (questionMap != null && questionMap.size() > 0) {
                if (questionMap.size() > MAX_COL - 3) {
                    wb = new HSSFWorkbook();
                } else {
                    // wb = new XSSFWorkbook();
                    wb = new SXSSFWorkbook(-1); // handle flushing manually
                }
                headerStyle = wb.createCellStyle();
                headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
                Font headerFont = wb.createFont();
                headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
                headerStyle.setFont(headerFont);

                SummaryModel model = fetchAndWriteRawData(
                        criteria.get(SurveyRestRequest.SURVEY_ID_PARAM),
                        serverBase, questionMap, wb, isFullReport, fileName,
                        criteria.get("apiKey"), lastCollection, useQuestionId,
                        from, to);
                if (isFullReport) {
                    SwingUtilities.invokeLater(new StatusUpdater(currentStep++,
                            WRITING_SUMMARY.get(locale)));
                    writeSummaryReport(questionMap, model, null, wb);
                    SwingUtilities.invokeLater(new StatusUpdater(currentStep++,
                            WRITING_ROLLUPS.get(locale)));
                }
                if (model.getSectorList() != null
                        && model.getSectorList().size() > 0) {

                    Collections.sort(model.getSectorList(),
                            new Comparator<String>() {
                                @Override
                                public int compare(String o1, String o2) {
                                    if (o1 != null && o2 != null) {
                                        return o1.toLowerCase().compareTo(
                                                o2.toLowerCase());
                                    } else {
                                        return 0;
                                    }
                                }
                            });
                    for (String sector : model.getSectorList()) {
                        writeSummaryReport(questionMap, model, sector, wb);
                    }
                }

                FileOutputStream fileOut = new FileOutputStream(fileName);
                wb.setActiveSheet(isFullReport ? 1 : 0);
                wb.write(fileOut);
                fileOut.close();

                SwingUtilities.invokeLater(new StatusUpdater(currentStep++,
                        COMPLETE.get(locale)));
            } else {
                log.info("No questions for survey: "
                        + criteria.get(SurveyRestRequest.SURVEY_ID_PARAM) + " - instance: "
                        + serverBase);
            }

        } catch (Exception e) {
            log.error("Error generating report: " + e.getMessage(), e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected SummaryModel fetchAndWriteRawData(String surveyId,
            final String serverBase,
            Map<QuestionGroupDto, List<QuestionDto>> questionMap, Workbook wb,
            final boolean generateSummary, File outputFile, String apiKey, boolean lastCollection,
            boolean useQuestionId, String from, String to)
            throws Exception {
        final SummaryModel model = new SummaryModel();
        final String key = apiKey;

        final Sheet sheet = wb.createSheet(RAW_DATA_LABEL.get(locale));
        int curRow = 1;

        final Map<String, String> collapseIdMap = new HashMap<String, String>();
        final Map<String, String> nameToIdMap = new HashMap<String, String>();
        for (Entry<QuestionGroupDto, List<QuestionDto>> groupEntry : questionMap
                .entrySet()) {
            for (QuestionDto q : groupEntry.getValue()) {
                if (q.getCollapseable() != null && q.getCollapseable()) {
                    if (collapseIdMap.get(q.getText()) == null) {
                        collapseIdMap.put(q.getText(), q.getKeyId().toString());
                    }
                    nameToIdMap.put(q.getKeyId().toString(), q.getText());
                }
            }
        }

        Object[] results = createRawDataHeader(wb, sheet, questionMap, useQuestionId);
        final List<String> questionIdList = (List<String>) results[0];
        final List<String> unsummarizable = (List<String>) results[1];

        SwingUtilities.invokeLater(new StatusUpdater(currentStep++,
                LOADING_INSTANCES.get(locale)));
        Map<String, String> instanceMap = BulkDataServiceClient
                .fetchInstanceIds(surveyId, serverBase, key, lastCollection, from, to);
        SwingUtilities.invokeLater(new StatusUpdater(currentStep++,
                LOADING_INSTANCE_DETAILS.get(locale)));

        final List<RowData> allData = new ArrayList<RowData>();
        int started = 0;
        for (Entry<String, String> instanceEntry : instanceMap.entrySet()) {
            final String instanceId = instanceEntry.getKey();
            final String dateString = instanceEntry.getValue();
            started++;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    int attempts = 0;
                    boolean done = false;

                    while (!done && attempts < 10) {
                        try {

                            Map<String, String> responseMap = BulkDataServiceClient
                                    .fetchQuestionResponses(instanceId,
                                            serverBase, key);

                            SurveyInstanceDto dto = BulkDataServiceClient
                                    .findSurveyInstance(
                                            Long.parseLong(instanceId.trim()),
                                            serverBase, key);
                            if (dto != null) {
                                done = true;
                            }
                            synchronized (allData) {
                                RowData rd = new RowData();
                                rd.setResponseMap(responseMap);
                                rd.setDto(dto);
                                rd.setInstanceId(instanceId);
                                rd.setDateString(dateString);
                                allData.add(rd);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            synchronized (lock) {
                                threadsCompleted++;
                            }
                        }
                        attempts++;
                    }
                }
            });
        }
        while (!jobQueue.isEmpty() || threadPool.getActiveCount() > 0
                || started > threadsCompleted) {
            try {
                log.debug("Sleeping, Queue has: " + jobQueue.size());
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // write the data now
        for (RowData rd : allData) {
            Row row = getRow(curRow++, sheet);
            writeRow(row, rd.getDto(), rd.getResponseMap(), rd.getDateString(),
                    rd.getInstanceId(), generateSummary, questionIdList,
                    unsummarizable, nameToIdMap, collapseIdMap, model, useQuestionId);
        }

        SwingUtilities.invokeLater(new StatusUpdater(currentStep++,
                WRITING_RAW_DATA.get(locale)));
        return model;
    }

    private synchronized void writeRow(Row row, SurveyInstanceDto dto,
            Map<String, String> responseMap, String dateString,
            String instanceId, boolean generateSummary,
            List<String> questionIdList, List<String> unsummarizable,
            Map<String, String> nameToIdMap, Map<String, String> collapseIdMap,
            SummaryModel model, boolean useQuestionId) throws Exception {
        int col = 0;
        MessageDigest digest = MessageDigest.getInstance("MD5");

        if (monitoringGroup) {
            createCell(row, col++, dto.getSurveyedLocaleIdentifier(), null);
            createCell(row, col++, dto.getSurveyedLocaleDisplayName(), null);
        }

        createCell(row, col++, instanceId, null);
        createCell(row, col++, dateString, null);

        if (dto != null) {
            String name = dto.getSubmitterName();
            if (name != null) {
                createCell(row, col++,
                        dto.getSubmitterName().replaceAll("\n", " ")
                                .replaceAll("\t", " ").trim(), null);
            } else {
                createCell(row, col++, " ", null);
            }
            // Surveyal time
            final Long duration = dto.getSurveyalTime();
            final String durationText = getDurationText(duration);
            createCell(row, col++, durationText, null,
                    Cell.CELL_TYPE_STRING);
            // Surveyal time also computes for our hash
            digest.update(durationText.getBytes());
        }

        for (String q : questionIdList) {
            String val = null;
            QuestionDto qdto = questionsById.get(Long.parseLong(q));
            if (responseMap != null) {
                val = responseMap.get(q);
            }
            if (val != null) {
                try {
                    if (qdto != null && QuestionType.DATE == qdto.getType()) {
                        val = DATE_FMT.format(new Date(Long.parseLong(val
                                .trim())));
                    }
                } catch (Exception e) {
                    log.error("couldn't format value for question id: "
                            + q + " -  " + e.getMessage(), e);
                }

                if (qdto != null && QuestionType.PHOTO == qdto.getType()) {
                    final int filenameIndex = val.lastIndexOf("/") + 1;
                    if (filenameIndex > 0 && filenameIndex < val.length()) {
                        val = imagePrefix + val.substring(filenameIndex);
                    }
                }

                if (qdto != null && QuestionType.GEO == qdto.getType()) {
                    String[] geoParts = val.split("\\|");
                    int count = 0;
                    for (count = 0; count < geoParts.length; count++) {
                        createCell(row, col++, geoParts[count], null);
                        digest.update(geoParts[count].getBytes());
                    }
                    // now handle any missing fields
                    for (int j = count; j < 4; j++) {
                        createCell(row, col++, "", null);
                    }
                } else if (qdto != null && QuestionType.NUMBER.equals(qdto.getType())) {
                    String cellVal = val.trim();
                    createCell(row, col++, cellVal, null, Cell.CELL_TYPE_NUMERIC);
                    digest.update(cellVal.getBytes());
                } else if (qdto != null && QuestionType.CASCADE.equals(qdto.getType())
                        && useQuestionId) {
                    String cellVal = val.trim();
                    ArrayList<String> parts = new ArrayList<String>(Arrays.asList(cellVal
                            .split("\\|", -1)));
                    int padCount = qdto.getLevelNames().size() - parts.size();
                    for (int p = 0; p < padCount; p++) { // padding
                        parts.add("");
                    }
                    for (String lVal : parts) {
                        createCell(row, col++, lVal, null, Cell.CELL_TYPE_STRING);
                        digest.update(lVal.getBytes());
                    }
                } else {
                    String cellVal = val.replaceAll("\n", " ").trim();
                    createCell(row, col++, cellVal, null);
                    digest.update(cellVal.getBytes());
                }
            } else {
                if (qdto != null && QuestionType.GEO == qdto.getType()) {
                    for (int j = 0; j < 4; j++) {
                        createCell(row, col++, "", null);
                    }
                } else {
                    createCell(row, col++, "", null);
                }
            }
        }

        if (!useQuestionId) {
            // now add 1 more col that contains the digest
            createCell(row, col++, StringUtil.toHexString(digest.digest()), null);
        }

        if (generateSummary && responseMap != null) {
            Set<String> rollups = null;
            if (rollupOrder != null && rollupOrder.size() > 0) {
                rollups = formRollupStrings(responseMap);
            }
            for (Entry<String, String> entry : responseMap.entrySet()) {
                if (!unsummarizable.contains(entry.getKey())) {
                    String effectiveId = entry.getKey();
                    if (nameToIdMap.get(effectiveId) != null) {
                        effectiveId = collapseIdMap.get(nameToIdMap
                                .get(effectiveId));
                    }
                    String[] vals = entry.getValue().split("\\|");
                    synchronized (model) {
                        for (int i = 0; i < vals.length; i++) {
                            if (vals[i] != null && vals[i].trim().length() > 0) {
                                model.tallyResponse(effectiveId, rollups,
                                        vals[i]);
                            }
                        }
                    }
                }
            }
        }
        // flush the sheet so far to disk; we will not go back up
        // TODO: ((SXSSFSheet)sheet).flushRows(0); // retain 0 last rows and
        // flush all others

    }

    /**
     * creates the header for the raw data tab
     *
     * @param row
     * @param questionMap
     * @return - returns a 2 element array. The first element is a List of String objects
     *         representing all the question Ids. The second element is a List of Strings
     *         representing all the non-summarizable question Ids (i.e. those that aren't OPTION or
     *         NUMBER questions)
     */
    protected Object[] createRawDataHeader(Workbook wb, Sheet sheet,
            Map<QuestionGroupDto, List<QuestionDto>> questionMap,
            boolean useQuestionId) {
        Row row = null;

        row = getRow(0, sheet);

        int columnIdx = 0;

        if (monitoringGroup) {
            createCell(row, columnIdx++, IDENTIFIER_LABEL.get(locale), headerStyle);
            createCell(row, columnIdx++, DISPLAY_NAME_LABEL.get(locale), headerStyle);
        }

        createCell(row, columnIdx++, INSTANCE_LABEL.get(locale), headerStyle);
        createCell(row, columnIdx++, SUB_DATE_LABEL.get(locale), headerStyle);
        createCell(row, columnIdx++, SUBMITTER_LABEL.get(locale), headerStyle);
        createCell(row, columnIdx++, DURATION_LABEL.get(locale), headerStyle);

        List<String> questionIdList = new ArrayList<String>();
        List<String> nonSummarizableList = new ArrayList<String>();

        if (questionMap != null) {
            int offset = columnIdx;
            for (QuestionGroupDto group : orderedGroupList) {
                if (questionMap.get(group) != null) {
                    for (QuestionDto q : questionMap.get(group)) {
                        questionIdList.add(q.getKeyId().toString());

                        String questionId = q.getQuestionId();
                        boolean useQID = useQuestionId
                                && questionId != null
                                && !questionId.equals("");

                        String columnLocale = useQID ? "en" : locale;

                        if (QuestionType.GEO == q.getType()) {
                            if (useQuestionId) {
                                createCell(row, offset++,
                                        (useQID ? questionId + "_" : getLocalizedText(q.getText(),
                                                q.getTranslationMap()) + " - ")
                                                + LAT_LABEL.get(columnLocale),
                                        headerStyle);
                            } else {
                                createCell(row, offset++,
                                        q.getKeyId() + "|" + LAT_LABEL.get(columnLocale),
                                        headerStyle);
                            }
                            createCell(row, offset++,
                                    (useQID ? questionId + "_" : "--GEOLON--|")
                                            + LON_LABEL.get(columnLocale),
                                    headerStyle);
                            createCell(row, offset++,
                                    (useQID ? questionId + "_" : "--GEOELE--|")
                                            + ELEV_LABEL.get(columnLocale),
                                    headerStyle);
                            String codeLabel = CODE_LABEL.get(columnLocale);
                            createCell(row, offset++,
                                    useQID ? questionId + "_" + codeLabel.replaceAll("\\s", "")
                                            : "--GEOCODE--|" + codeLabel,
                                    headerStyle);
                        } else if (QuestionType.CASCADE == q.getType() && q.getLevelNames() != null
                                && useQuestionId) {
                            for (String level : q.getLevelNames()) {
                                String levelName = useQID ? questionId + "_"
                                        + level.replaceAll(" ", "_") : getLocalizedText(
                                        q.getText(), q.getTranslationMap())
                                        + " - " + level;
                                createCell(row, offset++, levelName, headerStyle);
                            }
                        } else {
                            String header = "";
                            if (useQID) {
                                header = questionId;
                            } else if (useQuestionId) {
                                header = getLocalizedText(q.getText(),
                                        q.getTranslationMap())
                                        .replaceAll("\n", "")
                                        .trim();
                            } else {
                                header = q.getKeyId().toString()
                                        + "|"
                                        + getLocalizedText(q.getText(),
                                                q.getTranslationMap())
                                                .replaceAll("\n", "")
                                                .trim();
                            }
                            createCell(
                                    row,
                                    offset++,
                                    header,
                                    headerStyle);
                        }
                        if (!(QuestionType.NUMBER == q.getType() || QuestionType.OPTION == q
                                .getType())) {
                            nonSummarizableList.add(q.getKeyId().toString());
                        }
                    }
                }
            }
        }
        Object[] temp = new Object[2];
        temp[0] = questionIdList;
        temp[1] = nonSummarizableList;
        return temp;
    }

    /**
     * Writes the report as an XLS document
     */
    private void writeSummaryReport(
            Map<QuestionGroupDto, List<QuestionDto>> questionMap,
            SummaryModel summaryModel, String sector, Workbook wb)
            throws Exception {
        String title = sector == null ? SUMMARY_LABEL.get(locale) : sector;
        Sheet sheet = null;
        int sheetCount = 2;
        String curTitle = WorkbookUtil.createSafeSheetName(title);
        while (sheet == null) {
            sheet = wb.getSheet(curTitle);
            if (sheet == null) {
                sheet = wb.createSheet(WorkbookUtil
                        .createSafeSheetName(curTitle));
            } else {
                sheet = null;
                curTitle = title + " " + sheetCount;
                sheetCount++;
            }
        }
        CreationHelper creationHelper = wb.getCreationHelper();
        Drawing patriarch = sheet.createDrawingPatriarch();
        int curRow = 0;
        Row row = getRow(curRow++, sheet);
        if (sector == null) {
            createCell(row, 0, REPORT_HEADER.get(locale), headerStyle);
        } else {
            createCell(row, 0, sector + " " + REPORT_HEADER.get(locale),
                    headerStyle);
        }
        for (QuestionGroupDto group : orderedGroupList) {
            if (questionMap.get(group) != null) {
                for (QuestionDto question : questionMap.get(group)) {
                    if (!(QuestionType.OPTION == question.getType() || QuestionType.NUMBER == question
                            .getType())) {
                        continue;
                    } else {
                        if (summaryModel.getResponseCountsForQuestion(
                                question.getKeyId(), sector).size() == 0) {
                            // if there is no data, skip the question
                            continue;
                        }
                    }
                    // for both options and numeric, we want a pie chart and
                    // data table for numeric, we also want descriptive
                    // statistics
                    int tableTopRow = curRow++;
                    int tableBottomRow = curRow;
                    row = getRow(tableTopRow, sheet);
                    // span the question heading over the data table
                    sheet.addMergedRegion(new CellRangeAddress(curRow - 1,
                            curRow - 1, 0, 2));
                    createCell(
                            row,
                            0,
                            getLocalizedText(question.getText(),
                                    question.getTranslationMap()), headerStyle);
                    DescriptiveStats stats = summaryModel
                            .getDescriptiveStatsForQuestion(
                                    question.getKeyId(), sector);
                    if (stats != null && stats.getSampleCount() > 0) {
                        sheet.addMergedRegion(new CellRangeAddress(curRow - 1,
                                curRow - 1, 4, 5));
                        createCell(
                                row,
                                4,
                                getLocalizedText(question.getText(),
                                        question.getTranslationMap()),
                                headerStyle);
                    }
                    row = getRow(curRow++, sheet);
                    createCell(row, 1, FREQ_LABEL.get(locale), headerStyle);
                    createCell(row, 2, PCT_LABEL.get(locale), headerStyle);

                    // now create the data table for the option count
                    Map<String, Long> counts = summaryModel
                            .getResponseCountsForQuestion(question.getKeyId(),
                                    sector);
                    int sampleTotal = 0;
                    List<String> labels = new ArrayList<String>();
                    List<String> values = new ArrayList<String>();
                    int firstOptRow = curRow;
                    for (Entry<String, Long> count : counts.entrySet()) {
                        row = getRow(curRow++, sheet);
                        String labelText = count.getKey();
                        if (labelText == null) {
                            labelText = "";
                        }
                        StringBuilder builder = new StringBuilder();
                        if (QuestionType.OPTION == question.getType()
                                && !DEFAULT_LOCALE.equals(locale)) {
                            String[] tokens = labelText.split("\\|");
                            // see if we have a translation for this option
                            for (int i = 0; i < tokens.length; i++) {
                                if (i > 0) {
                                    builder.append("|");
                                }
                                if (question.getOptionContainerDto() != null
                                        && question.getOptionContainerDto()
                                                .getOptionsList() != null) {
                                    boolean found = false;
                                    for (QuestionOptionDto opt : question
                                            .getOptionContainerDto()
                                            .getOptionsList()) {
                                        if (opt.getText() != null
                                                && opt.getText()
                                                        .trim()
                                                        .equalsIgnoreCase(
                                                                tokens[i])) {
                                            builder.append(getLocalizedText(
                                                    tokens[i],
                                                    opt.getTranslationMap()));
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        builder.append(tokens[i]);
                                    }
                                }
                            }
                        } else {
                            builder.append(labelText);
                        }
                        createCell(row, 0, builder.toString(), null);
                        createCell(row, 1, count.getValue().toString(), null);

                        labels.add(builder.toString());
                        values.add(count.getValue().toString());
                        sampleTotal += count.getValue();
                    }
                    row = getRow(curRow++, sheet);
                    createCell(row, 0, TOTAL_LABEL.get(locale), null);
                    createCell(row, 1, sampleTotal + "", null);
                    for (int i = 0; i < values.size(); i++) {
                        row = getRow(firstOptRow + i, sheet);
                        if (sampleTotal > 0) {
                            createCell(row, 2,
                                    PCT_FMT.format((Double.parseDouble(values
                                            .get(i)) / sampleTotal)),
                                    null);
                        } else {
                            createCell(row, 2, PCT_FMT.format(0), null);
                        }
                    }

                    tableBottomRow = curRow;

                    if (stats != null && stats.getSampleCount() > 0) {
                        int tempRow = tableTopRow + 1;
                        row = getRow(tempRow++, sheet);
                        createCell(row, 4, "N", null);
                        createCell(row, 5, sampleTotal + "", null);
                        row = getRow(tempRow++, sheet);
                        createCell(row, 4, MEAN_LABEL.get(locale), null);
                        createCell(row, 5, stats.getMean() + "", null);
                        row = getRow(tempRow++, sheet);
                        createCell(row, 4, STD_E_LABEL.get(locale), null);
                        createCell(row, 5, stats.getStandardError() + "", null);
                        row = getRow(tempRow++, sheet);
                        createCell(row, 4, MEDIAN_LABEL.get(locale), null);
                        createCell(row, 5, stats.getMedian() + "", null);
                        row = getRow(tempRow++, sheet);
                        createCell(row, 4, MODE_LABEL.get(locale), null);
                        createCell(row, 5, stats.getMode() + "", null);
                        row = getRow(tempRow++, sheet);
                        createCell(row, 4, STD_D_LABEL.get(locale), null);
                        createCell(row, 5, stats.getStandardDeviation() + "",
                                null);
                        row = getRow(tempRow++, sheet);
                        createCell(row, 4, VAR_LABEL.get(locale), null);
                        createCell(row, 5, stats.getVariance() + "", null);
                        row = getRow(tempRow++, sheet);
                        createCell(row, 4, RANGE_LABEL.get(locale), null);
                        createCell(row, 5, stats.getRange() + "", null);
                        row = getRow(tempRow++, sheet);
                        createCell(row, 4, MIN_LABEL.get(locale), null);
                        createCell(row, 5, stats.getMin() + "", null);
                        row = getRow(tempRow++, sheet);
                        createCell(row, 4, MAX_LABEL.get(locale), null);
                        createCell(row, 5, stats.getMax() + "", null);
                        if (tableBottomRow < tempRow) {
                            tableBottomRow = tempRow;
                        }
                    }
                    curRow = tableBottomRow;
                    if (labels.size() > 0) {
                        boolean hasVals = false;
                        if (values != null) {
                            for (String val : values) {
                                try {
                                    if (val != null
                                            && new Double(val.trim()) > 0D) {
                                        hasVals = true;
                                        break;
                                    }
                                } catch (Exception e) {
                                    // no-op
                                }
                            }
                        }
                        // only insert the image if we have at least 1 non-zero
                        // value
                        if (hasVals && generateCharts) {
                            // now insert the graph
                            int indx = wb
                                    .addPicture(
                                            JFreechartChartUtil
                                                    .getPieChart(
                                                            labels,
                                                            values,
                                                            getLocalizedText(
                                                                    question.getText(),
                                                                    question.getTranslationMap()),
                                                            CHART_WIDTH,
                                                            CHART_HEIGHT),
                                            Workbook.PICTURE_TYPE_PNG);
                            ClientAnchor anchor = creationHelper
                                    .createClientAnchor();
                            anchor.setDx1(0);
                            anchor.setDy1(0);
                            anchor.setDx2(0);
                            anchor.setDy2(255);
                            anchor.setCol1(6);
                            anchor.setRow1(tableTopRow);
                            anchor.setCol2(6 + CHART_CELL_WIDTH);
                            anchor.setRow2(tableTopRow + CHART_CELL_HEIGHT);
                            anchor.setAnchorType(2);
                            patriarch.createPicture(anchor, indx);
                            if (tableTopRow + CHART_CELL_HEIGHT > tableBottomRow) {
                                curRow = tableTopRow + CHART_CELL_HEIGHT;
                            }
                        }
                    }

                    // add a blank row between questions
                    getRow(curRow++, sheet);
                    // flush the sheet so far to disk; we will not go back up
                    ((SXSSFSheet) sheet).flushRows(0); // retain 0 last rows and
                    // flush all others

                }
            }
        }
    }

    /**
     * creates a cell in the row passed in and sets the style and value (if non-null)
     */

    protected Cell createCell(Row row, int col, String value, CellStyle style) {
        return createCell(row, col, value, style, -1);
    }

    protected Cell createCell(Row row, int col, String value, CellStyle style, int type) {
        Cell cell = row.createCell(col);

        if (style != null) {
            cell.setCellStyle(style);
        }
        if (value != null) {
            if (type == Cell.CELL_TYPE_NUMERIC) {
                Double val = null;
                try {
                    val = Double.parseDouble(value);
                } catch (Exception e) {
                    // no-op
                }
                if (val != null) {
                    cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                    cell.setCellValue(val.doubleValue());
                } else {
                    cell.setCellValue(value);
                }
            } else {
                cell.setCellValue(value);
            }
        }

        return cell;
    }

    /**
     * finds or creates the row at the given index
     *
     * @param index
     * @param rowLocalMax
     * @param sheet
     * @return
     */
    private synchronized Row getRow(int index, Sheet sheet) {
        Row row = null;
        if (index < sheet.getLastRowNum()) {
            row = sheet.getRow(index);
            if (row == null) {
                row = sheet.createRow(index);
            }
        } else {
            row = sheet.createRow(index);
        }
        log.debug("Row " + index); // debug printout to study backward jumps

        return row;

    }

    /**
     * sets instance variables to the values passed in in the Option map. If the option is not set,
     * the default values are used.
     *
     * @param options
     */
    protected void processOptions(Map<String, String> options) {
        isFullReport = true;
        performGeoRollup = true;
        maxSteps = FULL_STEPS;
        generateCharts = true;
        if (options != null) {
            log.debug(options);

            locale = options.get(LOCALE_OPT);
            imagePrefix = options.get(IMAGE_PREFIX_OPT);
            if (RAW_ONLY_TYPE.equalsIgnoreCase(options.get(TYPE_OPT))) {
                isFullReport = false;
                maxSteps = RAW_STEPS;
            }
            if (options.get(DO_ROLLUP_OPT) != null) {
                if ("false".equalsIgnoreCase(options.get(DO_ROLLUP_OPT))) {
                    performGeoRollup = false;
                }
            }
            if (options.get(NO_CHART_OPT) != null) {
                if ("true".equalsIgnoreCase(options.get(NO_CHART_OPT))) {
                    generateCharts = false;
                }
            }
            if (options.get(LAST_COLLECTION_OPT) != null
                    && "true".equals(options.get(LAST_COLLECTION_OPT))) {
                lastCollection = true;
            }
        }
        if (locale != null) {
            locale = locale.trim().toLowerCase();
            if (DEFAULT.equalsIgnoreCase(locale)) {
                locale = DEFAULT_LOCALE;
            }

        } else {
            locale = DEFAULT_LOCALE;
        }
        if (imagePrefix != null) {
            imagePrefix = imagePrefix.trim();
            if (!imagePrefix.endsWith("/")) {
                imagePrefix = imagePrefix + "/";
            }
        } else {
            imagePrefix = DEFAULT_IMAGE_PREFIX;
        }
    }

    /**
     * call the server to augment the data already loaded in each QuestionDto in the map passed in.
     *
     * @param questionMap
     */
    private void loadFullQuestions(
            Map<QuestionGroupDto, List<QuestionDto>> questionMap, String apiKey) {
        for (List<QuestionDto> questionList : questionMap.values()) {
            for (int i = 0; i < questionList.size(); i++) {
                try {
                    QuestionDto newQ = BulkDataServiceClient
                            .loadQuestionDetails(serverBase, questionList
                                    .get(i).getKeyId(), apiKey);
                    if (newQ != null) {
                        questionList.set(i, newQ);
                    }
                } catch (Exception e) {
                    System.err.println("Could not fetch question details");
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    /**
     * uses the locale and the translation map passed in to determine what value to use for the
     * string
     *
     * @param text
     * @param translationMap
     * @return
     */
    private String getLocalizedText(String text,
            Map<String, TranslationDto> translationMap) {
        TranslationDto trans = null;
        if (translationMap != null) {
            trans = translationMap.get(locale);
        }
        if (trans != null && trans.getText() != null
                && trans.getText().trim().length() > 0) {
            return trans.getText();
        } else {
            return text;

        }
    }

    private String getDurationText(Long duration) {
        if (duration == null) {
            return "";
        }
        String result = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            df.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
            result = df.format(duration * 1000);
        } catch (Exception e) {
            // swallow, the default value of result will be used.
        }
        return result;
    }

    protected String getImagePrefix() {
        return this.imagePrefix;
    }

    public static void main(String[] args) {
        // Log4j stuff - http://stackoverflow.com/a/9003191
        ConsoleAppender console = new ConsoleAppender();
        console.setLayout(new PatternLayout("%d{ISO8601} [%t] %-5p %c - %m%n"));
        console.setThreshold(Level.DEBUG);
        console.activateOptions();
        Logger.getRootLogger().addAppender(console);

        GraphicalSurveySummaryExporter exporter = new GraphicalSurveySummaryExporter();
        Map<String, String> criteria = new HashMap<String, String>();
        Map<String, String> options = new HashMap<String, String>();
        options.put(LOCALE_OPT, "en");
        options.put(TYPE_OPT, RAW_ONLY_TYPE);
        options.put(LAST_COLLECTION_OPT, "false");
        options.put("useQuestionId", "true");
        options.put("email", "email@example.com");
        options.put("from", "2013/02/03");
        options.put("to", "2015/03/03");
        criteria.put(SurveyRestRequest.SURVEY_ID_PARAM, args[2]);
        criteria.put("apiKey", args[3]);
        exporter.export(criteria, new File(args[0]), args[1], options);
    }

    /**
     * Private class to handle updating of the UI thread from our worker thread
     */
    private class StatusUpdater implements Runnable {

        private int step;
        private String msg;

        public StatusUpdater(int step, String message) {
            msg = message;
            this.step = step;
        }

        @Override
        public void run() {
            if (!GraphicsEnvironment.isHeadless()) {
                progressDialog.update(step, msg);
            }
        }
    }

    private class RowData {
        private Map<String, String> responseMap;
        private String dateString;
        private String instanceId;
        private SurveyInstanceDto dto;

        public Map<String, String> getResponseMap() {
            return responseMap;
        }

        public void setResponseMap(Map<String, String> responseMap) {
            this.responseMap = responseMap;
        }

        public String getDateString() {
            return dateString;
        }

        public void setDateString(String dateString) {
            this.dateString = dateString;
        }

        public String getInstanceId() {
            return instanceId;
        }

        public void setInstanceId(String instanceId) {
            this.instanceId = instanceId;
        }

        public SurveyInstanceDto getDto() {
            return dto;
        }

        public void setDto(SurveyInstanceDto dto) {
            this.dto = dto;
        }

    }

}
