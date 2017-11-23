/*
 *  Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.akvo.flow.domain.DataUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.InstanceDataDto;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;
import org.waterforpeople.mapping.domain.CaddisflyResource;
import org.waterforpeople.mapping.domain.CaddisflyResult;
import org.waterforpeople.mapping.domain.response.value.Media;
import org.waterforpeople.mapping.serialization.response.MediaResponse;

import com.gallatinsystems.common.util.JFreechartChartUtil;
import com.gallatinsystems.survey.dao.CaddisflyResourceDao;

import static com.gallatinsystems.common.Constants.*;

/**
 * Enhancement of the SurveySummaryExporter to support writing to Excel and including chart images.
 *
 * @author Christopher Fagiani
 */
public class GraphicalSurveySummaryExporter extends SurveySummaryExporter {

    private static final Logger log = Logger
            .getLogger(GraphicalSurveySummaryExporter.class);

    private static final String IMAGE_PREFIX_OPT = "imgPrefix";
    private static final String LOCALE_OPT = "locale";
    private static final String TYPE_OPT = "exportMode";
    private static final String DATA_CLEANING_TYPE = "DATA_CLEANING";
    private static final String DATA_ANALYSIS_TYPE = "DATA_ANALYSIS";
    private static final String COMPREHENSIVE_TYPE = "COMPREHENSIVE";
    private static final String LAST_COLLECTION_OPT = "lastCollection";
    private static final String MAX_ROWS_OPT = "maxDataReportRows";
    private static final String FROM_OPT = "from";
    private static final String TO_OPT = "to";
    private static final String EMAIL_OPT = "email";
    
    private static final String CADDISFLY_TESTS_FILE_URL_OPT = "caddisflyTestsFileUrl";
    private static final String METADATA_LABEL = "Metadata"; //Constant. Localization is going away.
    private static final String REPORT_COMMENT = "Data Cleaning Report - Akvo Flow v1.9.25";
    

    private static final String DEFAULT_IMAGE_PREFIX = "http://waterforpeople.s3.amazonaws.com/images/";

    private static final String DIGEST_COLUMN = "NO_TITLE_DIGEST_COLUMN";

    private static final Map<String, String> REPORT_HEADER;
    private static final Map<String, String> FREQ_LABEL;
    private static final Map<String, String> PCT_LABEL;
    private static final Map<String, String> SUMMARY_LABEL;
    private static final Map<String, String> RAW_DATA_LABEL;
    private static final Map<String, String> INSTANCE_LABEL;
    private static final Map<String, String> SUB_DATE_LABEL;
    private static final Map<String, String> SUBMITTER_LABEL;
    private static final Map<String, String> DURATION_LABEL;
    private static final Map<String, String> REPEAT_LABEL;
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
    private static final Map<String, String> IMAGE_LABEL;
    private static final Map<String, String> ELEV_LABEL;
    private static final Map<String, String> ACC_LABEL;
    private static final Map<String, String> CODE_LABEL;
    private static final Map<String, String> IDENTIFIER_LABEL;
    private static final Map<String, String> DISPLAY_NAME_LABEL;
    private static final Map<String, String> DEVICE_IDENTIFIER_LABEL;
    private static final Map<String, String> DATA_APPROVAL_STATUS_LABEL;

    private static final int CHART_WIDTH = 600;
    private static final int CHART_HEIGHT = 400;
    private static final int CHART_CELL_WIDTH = 10;
    private static final int CHART_CELL_HEIGHT = 22;
    private static final String DEFAULT_LOCALE = "en";
    private static final String DEFAULT = "default";
    private static final NumberFormat PCT_FMT = DecimalFormat.getPercentInstance();

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

        REPEAT_LABEL = new HashMap<String, String>();
        REPEAT_LABEL.put("en", "Repeat no"); //Periods in headers were annoying to some consumers
        REPEAT_LABEL.put("es", "No repetición");

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

        IMAGE_LABEL = new HashMap<String, String>();
        IMAGE_LABEL.put("en", "Image");
        IMAGE_LABEL.put("es", "Imagen");

        ELEV_LABEL = new HashMap<String, String>();
        ELEV_LABEL.put("en", "Elevation");
        ELEV_LABEL.put("es", "Elevación");

        CODE_LABEL = new HashMap<String, String>();
        CODE_LABEL.put("en", "Geo Code");
        CODE_LABEL.put("es", "Código Geo");

        ACC_LABEL = new HashMap<String, String>();
        ACC_LABEL.put("en", "Accuracy (m)");
        ACC_LABEL.put("es", "Precisión (m)");

        IDENTIFIER_LABEL = new HashMap<String, String>();
        IDENTIFIER_LABEL.put("en", "Identifier");
        IDENTIFIER_LABEL.put("es", "Identificador");

        DISPLAY_NAME_LABEL = new HashMap<String, String>();
        DISPLAY_NAME_LABEL.put("en", "Display Name");
        DISPLAY_NAME_LABEL.put("es", "Nombre");

        DEVICE_IDENTIFIER_LABEL = new HashMap<String, String>();
        DEVICE_IDENTIFIER_LABEL.put("en", "Device identifier");
        DEVICE_IDENTIFIER_LABEL.put("es", "Identificador de dispositivo");

        DATA_APPROVAL_STATUS_LABEL = new HashMap<String, String>();
        DATA_APPROVAL_STATUS_LABEL.put("en", "Data approval status");
        DATA_APPROVAL_STATUS_LABEL.put("es", "Estado de aprobación");
    }

   
    private CellStyle headerStyle;
    private CellStyle textStyle;
    private String locale;
    private String imagePrefix;
    private String serverBase;
    private boolean isFullReport;
    private boolean performGeoRollup;
    private boolean generateCharts;
    private boolean useQuestionId; //=Variable names. Also turns on splitting of answers into separate columns (options, geo, etc.) and turns off digests
    private boolean separateSheetsForRepeatableGroups;
    private boolean doGroupHeaders; //First header line is group names spanned over the group columns
    private Map<Long, QuestionDto> questionsById;
    private SurveyGroupDto surveyGroupDto;
    private boolean lastCollection = false;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private CaddisflyResourceDao caddisflyResourceDao = new CaddisflyResourceDao();
    private String caddisflyTestsFileUrl;
    private String selectionFrom = null;
    private String selectionTo = null;
    private String selectionLimit = null;

    // for caddisfly-specific metadata
    //TODO private Map<Long, Integer> numResultsMap = new HashMap<>();
    private Map<Long, Boolean> hasImageMap = new HashMap<>();
    private Map<Long, List<Integer>> resultIdMap = new HashMap<>();

    private Map<Long, List<QuestionOptionDto>> optionMap = new HashMap<>();
    private Map<Long, Boolean> allowOtherMap = new HashMap<>();
    private Map<String, Integer> optionsPositionCache = new HashMap<>();

    // store indices of file columns for lookup when generating responses
    private Map<String, Integer> columnIndexMap = new HashMap<>();

    // maps from a (repeatable) question group id to the sheet that contains the raw data for it (if split)
    private Map<Long, Sheet> qgSheetMap = new HashMap<>();

    // data about questions gathered while writing headers
    private List<String> questionIdList = new ArrayList<String>();
    private List<String> unsummarizable = new ArrayList<String>();


    //@Override
    public void export(Map<String, String> criteria, File fileName,
            String serverBaseUrl, Map<String, String> options) {
        final String surveyId = criteria.get(SurveyRestRequest.SURVEY_ID_PARAM).trim();
        final String apiKey = criteria.get("apiKey").trim();

        if (!processOptions(options)) {
            return;
        }

        questionsById = new HashMap<Long, QuestionDto>();

        surveyGroupDto = BulkDataServiceClient.fetchSurveyGroup(surveyId, serverBaseUrl, apiKey);

        serverBase = serverBaseUrl;
        try {
            Map<QuestionGroupDto, List<QuestionDto>> questionMap = 
                    loadAllQuestions(surveyId, performGeoRollup, serverBaseUrl, apiKey);
            //minimal data plus cascade level names
            if (useQuestionId && DEFAULT_LOCALE.equals(locale)) {
                loadQuestionOptions(surveyId, serverBaseUrl, questionMap, apiKey);
            }
            

            if (questionMap.size() > 0) {
                if (!DEFAULT_LOCALE.equals(locale)) {
                    // we are using some other locale; need to check for translations.
                    // also gets option and cascade names for columns
                    loadFullQuestions(questionMap, apiKey); //modifies questionMap
                }
                //questionMap is now stable; make the id-to-dto map
                for (List<QuestionDto> qList : questionMap.values()) {
                    for (QuestionDto q : qList) {
                        questionsById.put(q.getKeyId(), q);
                    }
                }

                Workbook wb = createWorkbookAndFormats();
                
                Sheet baseSheet = createDataSheets(wb, questionMap);
                
                SummaryModel model = fetchAndWriteRawData(
                        criteria.get(SurveyRestRequest.SURVEY_ID_PARAM),
                        questionMap,
                        wb, baseSheet,
                        isFullReport, fileName,
                        criteria.get("apiKey"));
                if (isFullReport) {
                    writeSummaryReport(questionMap, model, null, wb);
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
                wb.setActiveSheet(isFullReport ? wb.getNumberOfSheets()-1 : 0);
                wb.write(fileOut);
                fileOut.close();

            } else {
                log.info("No questions for survey: "
                        + criteria.get(SurveyRestRequest.SURVEY_ID_PARAM)
                        + " - instance: " + serverBaseUrl);
            }
        } catch (Exception e) {
            log.error("Error generating report: " + e.getMessage(), e);
        }
    }

    private Workbook createWorkbookAndFormats(){
        Workbook wb = new SXSSFWorkbook(100);

        headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(CellStyle.ALIGN_LEFT);
        Font headerFont = wb.createFont();
        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerStyle.setFont(headerFont);

        short textFormat = wb.createDataFormat().getFormat("@"); // built-in text format
        textStyle = wb.createCellStyle();
        textStyle.setDataFormat(textFormat);
        // We tried a format like "0.###" to suppress scientific notation in number
        // answer cells, but it looked bad in Excel - "3" was shown as "3."
        //TODO: can we set properties on the Workbook?
        
        return wb;
    };
    
    private Sheet createDataSheets(Workbook wb, Map<QuestionGroupDto, List<QuestionDto>> questionMap) {
        //make base sheet (for non-repeated data)
        Sheet baseSheet = wb.createSheet(RAW_DATA_LABEL.get(locale));

        for (Entry<QuestionGroupDto, List<QuestionDto>> groupEntry : questionMap.entrySet()) {
            if (separateSheetsForRepeatableGroups && safeTrue(groupEntry.getKey().getRepeatable())) {
                // breaking this qg out, so create the sheet for it
                Long gid = groupEntry.getKey().getKeyId();
                qgSheetMap.put(gid, wb.createSheet("Group " + groupEntry.getKey().getOrder()));
            }
        }

        
        return baseSheet;
    }
    
    private boolean hasDataApproval() {
        return surveyGroupDto != null
                && safeTrue(surveyGroupDto.getRequireDataApproval())
                && surveyGroupDto.getDataApprovalGroupId() != null;
    }

    private boolean safeTrue(Boolean b) {
        return b != null && b.booleanValue();
    }
    
    /*
     * Fetches data from FLOW instance, and writes it to a file row by row. Called from export
     * method.
     */
    protected SummaryModel fetchAndWriteRawData(String surveyId,
            Map<QuestionGroupDto, List<QuestionDto>> questionMap,
            Workbook wb,
            Sheet baseSheet,
            final boolean generateSummary,
            File outputFile,
            String apiKey
            ) throws Exception {

        BlockingQueue<Runnable> jobQueue = new LinkedBlockingQueue<Runnable>();
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, jobQueue);

        final AtomicLong threadsCompleted = new AtomicLong();
        final Object lock = new Object();

        final SummaryModel model = new SummaryModel();
        final String key = apiKey;

        final Map<String, String> collapseIdMap = new HashMap<String, String>();
        final Map<String, String> nameToIdMap = new HashMap<String, String>();
        for (Entry<QuestionGroupDto, List<QuestionDto>> groupEntry : questionMap.entrySet()) {
            for (QuestionDto q : groupEntry.getValue()) {
                if (safeTrue(q.getCollapseable())) {
                    if (collapseIdMap.get(q.getText()) == null) {
                        collapseIdMap.put(q.getText(), q.getKeyId().toString());
                    }
                    nameToIdMap.put(q.getKeyId().toString(), q.getText());
                }
            }
        }

        createRawDataHeader(wb, baseSheet, questionMap);

        Map<String, String> instanceMap = BulkDataServiceClient.fetchInstanceIds(
                surveyId, serverBase, key, lastCollection, selectionFrom, selectionTo, selectionLimit
                );

        final List<InstanceData> allData = new ArrayList<>();
        int started = 0;
        for (Entry<String, String> instanceEntry : instanceMap.entrySet()) {
            final String instanceId = instanceEntry.getKey();
            started++;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    int attempts = 0;
                    boolean done = false;

                    while (!done && attempts < 10) {
                        try {
                            // responseMap is a map from question-id -> iteration -> value
                            Map<Long, Map<Long, String>> responseMap = BulkDataServiceClient
                                    .fetchQuestionResponses(instanceId,
                                            serverBase, key);

                            InstanceDataDto instanceDataDto = BulkDataServiceClient
                                    .fetchInstanceData(Long.parseLong(instanceId.trim()),
                                            serverBase, key);

                            if (instanceDataDto.surveyInstanceData != null) {
                                done = true;
                            }

                            synchronized (allData) {
                                allData.add(new InstanceData(instanceDataDto, responseMap));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            synchronized (lock) {
                                threadsCompleted.getAndIncrement();
                            }
                        }
                        attempts++;
                    }
                }
            });
        }
        while (!jobQueue.isEmpty() || threadPool.getActiveCount() > 0
                || started > threadsCompleted.get()) {
            try {
                log.debug("Sleeping, Queue has: " + jobQueue.size());
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // write the data now, row by row
        for (InstanceData instanceData : allData) {
            if (separateSheetsForRepeatableGroups) {
                List<QuestionDto> baseSheetQuestions = new ArrayList<>();
                List<Row> digestRows = new ArrayList<>();

                //For each group, write the repeats from top to bottom            
                for (Entry<QuestionGroupDto, List<QuestionDto>> groupEntry : questionMap.entrySet()) {
                    Long gid = groupEntry.getKey().getKeyId();
                    if (safeTrue(groupEntry.getKey().getRepeatable())) {
                        writeInstanceDataSplit(qgSheetMap.get(gid),
                                instanceData,
                                groupEntry.getValue(),
                                digestRows,
                                true); 
                    } else {
                        baseSheetQuestions.addAll(groupEntry.getValue());                   
                    }
                }
                // Now do the rest on the base sheet
                writeInstanceDataSplit(baseSheet,
                        instanceData,
                        baseSheetQuestions,
                        digestRows,
                        false); 
                
                String digest = ExportImportUtils.md5Digest(digestRows,
                        columnIndexMap.get(DIGEST_COLUMN), baseSheet); //in case any rep group is wider than the base sheet

                if (!useQuestionId) {
                    // now add 1 more col on the base sheet that contains the digest
                    createCell(getRow(baseSheet.getLastRowNum(), baseSheet),
                            columnIndexMap.get(DIGEST_COLUMN),
                            digest,
                            null);
                }


                
            } else { //just one sheet - do all at once with a global repeat column
                int baseCurrentRow = baseSheet.getLastRowNum();
                baseCurrentRow = writeInstanceData(baseSheet, baseCurrentRow, instanceData,
                        generateSummary, nameToIdMap, collapseIdMap, model);
            }
        }

        threadPool.shutdown();
        return model;
    }

    private synchronized void writeInstanceDataSplit(
            Sheet sheet,
            InstanceData instanceData,
            List<QuestionDto> whichQuestions,
            List<Row> digestRows,
            boolean showRepeatColumn)
            throws NoSuchAlgorithmException {
        
        int startRow = sheet.getLastRowNum() + 1;
    
        for (QuestionDto questionDto : whichQuestions) {
            final Long questionId = questionDto.getKeyId();

            SortedMap<Long, String> iterationsMap = instanceData.responseMap.get(questionId);
            if (iterationsMap == null) {
                continue;
            }

            // Write downwards (and possibly rightwards) per iteration
            int currentRow = startRow - 1;
            for (Map.Entry<Long, String> iteration : iterationsMap.entrySet()) {
                String val = iteration.getValue();
                Row iterationRow = getRow(++currentRow, sheet);
                writeAnswer(sheet, iterationRow, columnIndexMap.get(questionId.toString()),
                        questionDto, val);
                if (!digestRows.contains(iterationRow)) { //A Set would be neater
                    digestRows.add(iterationRow);
                }
            }
        }
        // all cells written; now we know how far down we went
        writeMetadata(sheet, instanceData, startRow, sheet.getLastRowNum() - startRow + 1,
                showRepeatColumn);
    }

    private void writeMetadata(Sheet sheet,
            InstanceData instanceData,
            final int firstRowNo,
            final int iterations,
            final boolean showRepeatColumn) {
        for (int i = 0; i < iterations; i++) { 

            Row r = getRow(firstRowNo + i, sheet);
            SurveyInstanceDto dto = instanceData.surveyInstanceDto;
            int col = 0;
            // Write the identifier
            createCell(r, col++, dto.getSurveyedLocaleIdentifier());
            // Write data approval status
            if (hasDataApproval()) {
                createCell(r, col++, instanceData.latestApprovalStatus);
            }
            // Write the "Repeat" column
            if (showRepeatColumn) {
                createCell(r, col++, String.valueOf(i + 1), null, Cell.CELL_TYPE_NUMERIC);
            }
            // Write other metadata
            createCell(r, col++, dto.getSurveyedLocaleDisplayName());
            createCell(r, col++, dto.getDeviceIdentifier());
            createCell(r, col++, dto.getKeyId().toString());
            createCell(r, col++, ExportImportUtils.formatDateTime(dto.getCollectionDate()));
            createCell(r, col++, sanitize(dto.getSubmitterName()));
            String duration = getDurationText(dto.getSurveyalTime());
            createCell(r, col++, duration);
        }

    }
    
    /**
     * Writes all the data for a single survey instance (form instance) to a sheet.
     *
     * @param sheet
     * @param startRow The start row for this instance
     * @param instanceData
     * @param generateSummary
     * @param nameToIdMap
     * @param collapseIdMap
     * @param model
     * @return The row where the next instance should be written
     * @throws NoSuchAlgorithmException
     */
    private synchronized int writeInstanceData(
            Sheet sheet,
            final int startRow,
            InstanceData instanceData,
            boolean generateSummary,
            Map<String, String> nameToIdMap,
            Map<String, String> collapseIdMap,
            SummaryModel model)
            throws NoSuchAlgorithmException {

        // maxRow will increase when we write repeatable question groups
        int maxRow = startRow;

        Row firstRow = getRow(startRow, sheet);

        writeMetadata(sheet, instanceData, startRow, (int) instanceData.maxIterationsCount, true);
        
        for (String q : questionIdList) {
            final Long questionId = Long.valueOf(q);
            final QuestionDto questionDto = questionsById.get(questionId);

            SortedMap<Long, String> iterationsMap = instanceData.responseMap.get(questionId);

            if (iterationsMap == null) {
                continue;
            }

            // Write downwards (and possibly rightwards) per iteration
            int rowOffset = -1;
            for (Map.Entry<Long, String> iteration : iterationsMap.entrySet()) {
                String val = iteration.getValue();
                rowOffset++;
                Row iterationRow = getRow(startRow + rowOffset, sheet);
                writeAnswer(sheet, iterationRow, columnIndexMap.get(q), questionDto, val);
            }
            maxRow = Math.max(maxRow, startRow + rowOffset);
        }

        // Calculate the digest
        List<Row> rows = new ArrayList<>();
        for (int r = startRow; r <= maxRow; r++) {
            rows.add(sheet.getRow(r));
        }

        String digest = ExportImportUtils.md5Digest(rows,
                columnIndexMap.get(DIGEST_COLUMN), sheet);

        if (!useQuestionId) {
            // now add 1 more col that contains the digest
            createCell(firstRow, columnIndexMap.get(DIGEST_COLUMN), digest, null);
        }

        // Rebuild old response map format for from instanceData.responseMap
        // Question id -> response
        Map<String, String> responseMap = new HashMap<>();

        for (Entry<Long, SortedMap<Long, String>> entry : instanceData.responseMap.entrySet()) {
            String questionId = entry.getKey().toString();

            // Pick the first iteration response since we currently don't
            // support Repeatable Question Groups
            Collection<String> iterations = entry.getValue().values();
            if (!iterations.isEmpty()) {
                String response = iterations.iterator().next();
                responseMap.put(questionId, response);
            }
        }

        if (generateSummary && responseMap != null) {
            Set<String> rollups = null;
            if (rollupOrder != null && rollupOrder.size() > 0) {
                rollups = formRollupStrings(responseMap);
            }
            for (Entry<String, String> entry : responseMap.entrySet()) {
                //TODO: only OPTION and NUMBER summarizable now. Simple to add CASCADE.
                if (!unsummarizable.contains(entry.getKey())) {
                    String effectiveId = entry.getKey();
                    if (nameToIdMap.get(effectiveId) != null) {
                        effectiveId = collapseIdMap.get(nameToIdMap.get(effectiveId));
                    }

                    String[] vals;
                    if (entry.getValue().startsWith("[")) { //JSON
                        try {
                            List<Map<String, String>> optionNodes = OBJECT_MAPPER.readValue(
                                    entry.getValue(),
                                    new TypeReference<List<Map<String, String>>>() {}
                                    );
                            List<String> valsList = new ArrayList<>();
                            for (Map<String, String> optionNode : optionNodes) {
                                valsList.add(optionNode.get("text")); //get "name" for CASCADE
                            }
                            vals = valsList.toArray(new String[valsList.size()]);
                        } catch (IOException e) {
                            vals = entry.getValue().split("\\|");
                        }
                    } else {
                        vals = entry.getValue().split("\\|");
                    }

                    synchronized (model) {
                        for (int i = 0; i < vals.length; i++) {
                            if (vals[i] != null && vals[i].trim().length() > 0) {
                                QuestionDto q = questionsById.get(Long.valueOf(effectiveId));
                                model.tallyResponse(effectiveId, rollups, vals[i], q);
                            }
                        }
                    }
                }
            }
        }

        return maxRow + 1;
    }

    /**
     * Write the cells for a single answer. Some answers are split into multiple cells. Called from
     * writeInstanceData method
     *
     * @param sheet
     * @param row
     * @param startColumn
     * @param questionDto
     * @param value
     */
    private void writeAnswer(Sheet sheet, Row row, int startColumn,
            QuestionDto questionDto, String value) {

        assert value != null;

        // Some question types splits the value into several columns.
        List<String> cells = new ArrayList<>();

        QuestionType questionType = questionDto.getType();
        Long qId;
        switch (questionType) {
            case DATE:
                cells.add(dateCellValue(value));
                break;

            case PHOTO:
            case VIDEO:
                cells.addAll(mediaCellValues(value, imagePrefix));
                break;

            case GEO:
                cells.addAll(geoCellValues(value));
                break;

            case CASCADE:
                if (questionDto.getLevelNames() != null) {
                    cells.addAll(cascadeCellValues(value, questionDto.getLevelNames().size()));
                } else {
                    log.warn("No CASCADE resource for question '" + questionDto.getText() + "'");
                }

                break;

            case OPTION:
                qId = questionDto.getKeyId();
                cells.addAll(optionCellValues(questionDto.getKeyId(), value,
                        optionMap.get(qId), allowOtherMap.get(qId)));
                break;

            case CADDISFLY:
                qId = questionDto.getKeyId();
                boolean hasMap = safeTrue(hasImageMap.get(qId));
                cells.addAll(caddisflyCellValues(qId, value, hasMap, imagePrefix));
                break;

            default:
                cells.add(sanitize(value));
                break;
        }

        int col = startColumn;
        for (String cellValue : cells) {
            if (questionType == QuestionType.NUMBER) {
                createCell(row, col, cellValue, null, Cell.CELL_TYPE_NUMERIC);
            } else if (questionType == QuestionType.PHOTO) {
                if (col == startColumn) { // URL is text
                    createCell(row, col, cellValue, textStyle);
                } else { // Coordinates numerical
                    createCell(row, col, cellValue, null, Cell.CELL_TYPE_NUMERIC);
                }
            } else if (questionType == QuestionType.OPTION
                    && (cellValue.equals("0") || cellValue.equals("1"))) {
                // the type of an option column depends on the contents - if it
                // is 0 or 1, we
                // assume it to be numerical, because it will part of the
                // expanded columns.
                createCell(row, col, cellValue, null, Cell.CELL_TYPE_NUMERIC);
            } else {
                createCell(row, col, cellValue, textStyle);
            }
            col++; // also takes care of padding in case no cell content added
        }
    }

    private static String dateCellValue(String value) {
        return ExportImportUtils.formatDateResponse(value);
    }

    private List<String> mediaCellValues(String value, String imagePrefix) {
        List<String> cells = new ArrayList<>();
        Media media = MediaResponse.parse(value);
        String filename = media.getFilename();
        final int filenameIndex = filename != null ? filename.lastIndexOf("/") + 1
                : -1;
        if (filenameIndex > 0 && filenameIndex < filename.length()) {
            cells.add(imagePrefix + filename.substring(filenameIndex));
            if (useQuestionId && media.getLocation() != null) {
                cells.add(Double.toString(media.getLocation().getLatitude()));
                cells.add(Double.toString(media.getLocation().getLongitude()));
                cells.add(Double.toString(media.getLocation().getAccuracy()));
            }
        }
        return cells;
    }

    /*
     * Creates the cell values for a geolocation question.
     */
    private static List<String> geoCellValues(String value) {

        String[] geoParts = value.split("\\|");
        List<String> cells = new ArrayList<>();
        int count = 0;
        for (count = 0; count < geoParts.length; count++) {
            cells.add(geoParts[count]);
        }
        // now handle any missing fields
        for (int j = count; j < 4; j++) {
            cells.add("");
        }

        return cells;
    }

    /*
     * Validates the map containing values from the parsed caddisfly response string
     */
    @SuppressWarnings("unchecked")
    private static boolean validateCaddisflyValue(Map<String, Object> caddisflyResponseMap,
            boolean hasImage) {
        // check presence of uuid and result
        if (caddisflyResponseMap.get(CADDISFLY_UUID) == null
                || caddisflyResponseMap.get(CADDISFLY_RESULT) == null) {
            return false;
        }

        if (hasImage && caddisflyResponseMap.get(CADDISFLY_IMAGE) == null) {
            return false;
        }

        // check presence of name, value, unit and id properties on results
        List<Map<String, Object>> results = (List<Map<String, Object>>) caddisflyResponseMap
                .get(CADDISFLY_RESULT);
        for (Map<String, Object> result : results) {
            if (result.get(CADDISFLY_RESULT_ID) == null
                    || result.get(CADDISFLY_RESULT_VALUE) == null) {
                return false;
            }
        }

        return true;
    }

    /*
     * Creates the cells containing responses to caddisfly questions
     */
    @SuppressWarnings("unchecked")
    private List<String> caddisflyCellValues(Long questionId, String value, boolean hasImage,
            String imagePrefix) {

        List<String> caddisflyCellValues = new ArrayList<>();

        List<Integer> resultIds = resultIdMap.get(questionId);

        Map<String, Object> caddisflyResponseMap = DataUtils.parseCaddisflyResponseValue(value);

        if (!validateCaddisflyValue(caddisflyResponseMap, hasImage)) {
            // fill empty cells and return in case of failure to validate caddisfly response
            for (int i = 0; i < resultIds.size(); i++) {
                caddisflyCellValues.add("");
            }

            if (hasImage) {
                caddisflyCellValues.add("");
            }
            return caddisflyCellValues;
        }

        List<Map<String, Object>> caddisflyTestResultsList = 
                (List<Map<String, Object>>) caddisflyResponseMap.get(CADDISFLY_RESULT);

        Map<Integer, Map<String, Object>> caddisflyTestResultsMap = mapCaddisflyResultsById(caddisflyTestResultsList);

        // get valid result ids for this question. The ids are already
        // in order.
        if (resultIds != null) {
            for (Integer resultId : resultIds) {
                Map<String, Object> caddisflyTestResult = caddisflyTestResultsMap.get(resultId);
                if (caddisflyTestResult != null) {
                    String testValue = "" + caddisflyTestResult.get(CADDISFLY_RESULT_VALUE);
                    caddisflyCellValues.add(testValue);
                } else {
                    caddisflyCellValues.add("");
                }
            }
        }
        
        // add image URL if available
        if (hasImage) {
            final String imageName = (String) caddisflyResponseMap.get(CADDISFLY_IMAGE);
            if (imageName == null) {
                caddisflyCellValues.add("");
            } else {
                caddisflyCellValues.add(imagePrefix + imageName);
            }
        }

        return caddisflyCellValues;
    }

    private Map<Integer, Map<String, Object>> mapCaddisflyResultsById(
            List<Map<String, Object>> caddisflyTestResults) {
        Map<Integer, Map<String, Object>> resultsMap = new HashMap<>();
        for (Map<String, Object> result : caddisflyTestResults) {
            resultsMap.put((Integer) result.get(CADDISFLY_RESULT_ID), result);
        }
        return resultsMap;
    }

    /*
     * Creates the cell values for a cascade question. The different levels are split into multiple
     * cells.
     */
    private /*static*/ List<String> cascadeCellValues(String value, int levels) {
        List<String> cells = new ArrayList<>();
        List<Map<String, String>> cascadeNodes = new ArrayList<>();

        if (value.startsWith("[")) {
            try {
                cascadeNodes = OBJECT_MAPPER.readValue(value,
                        new TypeReference<List<Map<String, String>>>() {
                        });
            } catch (IOException e) {
                log.warn("Unable to parse CASCADE response - " + value, e);
            }
        } else if (!value.isEmpty()) {
            for (String name : value.split("\\|")) {
                Map<String, String> m = new HashMap<>();
                m.put("name", name);
                cascadeNodes.add(m);
            }
        }

        boolean allCodesEqualsName = true;
        for (Map<String, String> cascadeNode : cascadeNodes) {
            String code = cascadeNode.get("code");
            String name = cascadeNode.get("name");

            if (code != null && name != null
                    && !code.toLowerCase().equals(name.toLowerCase())) {
                allCodesEqualsName = false;
                break;
            }
        }
        if (allCodesEqualsName) {
            for (Map<String, String> cascadeNode : cascadeNodes) {
                cascadeNode.put("code", null);
            }
        }

        if (useQuestionId) {
            // +------------+------------+-----
            // |code1:value1|code2:value2| ...
            // +------------+------------+-----

            int padCount = levels - cascadeNodes.size();

            for (Map<String, String> map : cascadeNodes) {
                String code = map.get("code");
                String name = map.get("name");
                String nodeVal = (code == null ? "" : code + ":") + name;

                if (cells.size() == levels) {
                    // Don't create too many cells
                    String currentVal = cells.get(cells.size() - 1);
                    cells.add(cells.size() - 1, currentVal + "|" + nodeVal);
                } else {
                    cells.add(nodeVal);
                }
            }

            for (int p = 0; p < padCount; p++) { // padding
                cells.add("");
            }

        } else {
            // +---------------------------------
            // | code1:value1|code2:value2|...
            // +---------------------------------
            StringBuilder cascadeString = new StringBuilder();
            for (Map<String, String> node : cascadeNodes) {
                String code = node.get("code");
                String name = node.get("name");
                cascadeString.append("|");
                cascadeString.append((code == null ? "" : code + ":") + name);
            }
            if (cascadeString.length() > 0) {
                // Drop the first pipe character.
                cascadeString.deleteCharAt(0);
            }
            cells.add(cascadeString.toString());
        }
        return cells;
    }

    /*
     * Takes a option question value in either the old or new format, and returns a list of option
     * maps. The response can be either: old format: text1|text2|text3 new format: [{"code":
     * "code1", "text": "text1"},{"code": "code2", "text": "text2"}]
     */
    private List<Map<String, String>> getNodes(String value) {
        boolean isNewFormat = value.startsWith("[");
        List<Map<String, String>> optionNodes = new ArrayList<>();
        if (isNewFormat) {
            try {
                optionNodes = OBJECT_MAPPER.readValue(value,
                        new TypeReference<List<Map<String, String>>>() {
                        });
            } catch (IOException e) {
                log.warn("Could not parse option response: " + value, e);
            }
        } else {
            String[] texts = value.split("\\|");
            for (String text : texts) {
                Map<String, String> node = new HashMap<>();
                node.put("text", text.trim());
                optionNodes.add(node);
            }
        }
        return optionNodes;
    }

    /*
     * Build pipe-separated value from option nodes To be included in reports
     */
    private String buildOptionString(List<Map<String, String>> optionNodes) {
        StringBuilder optionString = new StringBuilder();
        for (Map<String, String> node : optionNodes) {
            String code = node.get("code");
            String text = node.get("text");
            optionString.append("|");
            if (code != null) {
                optionString.append(code + ":" + text);
            } else {
                optionString.append(text);
            }
        }
        if (optionString.length() > 0) {
            // Remove the first |
            optionString.deleteCharAt(0);
        }
        return optionString.toString();
    }

    /*
     * Creates list of option values. The first value is always the pipe-separated format Depending
     * on the useQuestionId parameter, each option is given its own column A 0 or 1 denotes if that
     * option was selected or not if the AllowOther flag is true, a column is created for the Other
     * option. We first try to match on text, and if that fails, we try to match on code. This
     * guards against texts that are slightly changed during the evolution of a survey
     */
    private List<String> optionCellValues(Long questionId, String value,
            List<QuestionOptionDto> options, Boolean allowOther) {
        List<String> cells = new ArrayList<>();

        // get optionNodes from packed string value
        List<Map<String, String>> optionNodes = getNodes(value);

        // build pipe-separated format and add this to cell list
        String optionString = buildOptionString(optionNodes);
        cells.add(optionString);

        // if needed, build cells for options
        if (useQuestionId) {
            String text;
            String code;
            String cacheId;
            String other = null;
            boolean found;
            int numOptions = options.size();
            boolean[] optionFound = new boolean[numOptions];
            String qId = questionId.toString();

            for (Map<String, String> optAnswer : optionNodes) {
                text = optAnswer.get("text") != null ? optAnswer.get("text")
                        : "";
                code = optAnswer.get("code") != null ? optAnswer.get("code")
                        : "";
                found = false;

                // try cache first
                cacheId = qId + text + code;
                if (optionsPositionCache.containsKey(cacheId)) {
                    optionFound[optionsPositionCache.get(cacheId)] = true;
                    found = true;
                }

                // If it is not in the cache, try to match on text
                if (!found) {
                    for (int i = 0; i < numOptions; i++) {
                        if (text != null
                                && text.length() > 0
                                && text.equalsIgnoreCase(options.get(i)
                                        .getText())) {
                            optionFound[i] = true;
                            found = true;
                            // put in cache
                            optionsPositionCache.put(cacheId, i);
                            break;
                        }
                    }
                }

                // finally, try to match on code
                if (!found) {
                    for (int i = 0; i < numOptions; i++) {
                        if (code != null
                                && code.length() > 0
                                && code.equalsIgnoreCase(options.get(i)
                                        .getCode())) {
                            optionFound[i] = true;
                            found = true;
                            // put in cache
                            optionsPositionCache.put(cacheId, i);
                            break;
                        }
                    }
                }

                // if still not found, keep this value as other
                if (!found) {
                    other = text;
                }
            }

            // create cells with 0 or 1
            for (int i = 0; i < numOptions; i++) {
                cells.add(optionFound[i] ? "1" : "0");
            }

            if (allowOther) {
                cells.add(other != null ? other : "");
            }
        }
        return cells;
    }

    private String sanitize(String s) {
        if (s == null) {
            return "";
        } else {
            return s.replaceAll("\n", " ").replaceAll("\t", "").trim();
        }
    }

    /**
     * creates the column headers for the raw data sheets. Some questions lead
     * to multiple column headers.
     *
     */
    protected void createRawDataHeader(Workbook wb,
            Sheet baseSheet,
            Map<QuestionGroupDto,
            List<QuestionDto>> questionMap
            ) {

        int columnIdx = addMetaDataHeaders(baseSheet, !separateSheetsForRepeatableGroups);
        addComment(baseSheet);

        if (questionMap != null) {
            int offset = ++columnIdx;
            for (QuestionGroupDto group : orderedGroupList) {
                if (questionMap.get(group) != null) {
                    // if RQG, do on separate sheet
                    if (qgSheetMap.containsKey(group.getKeyId()))   {
                        Sheet groupSheet = qgSheetMap.get(group.getKeyId());
                        int metaEnd = addMetaDataHeaders(groupSheet, true);
                        addComment(groupSheet);
                        writeRawDataGroupHeaders(groupSheet, group, questionMap.get(group), metaEnd + 1);
                    } else {
                    // if not, keep adding it on to base sheet and return new offset
                        offset = writeRawDataGroupHeaders(baseSheet, group, questionMap.get(group), offset);
                    }
                    
                }
            }

            // add digest column index; it has no header
            columnIndexMap.put(DIGEST_COLUMN, offset);
        }
    }


    private void addComment(Sheet sheet)
    {
        Row row = getRow(0, sheet);
        Cell cell = row.getCell(0); //Assuming it exists
        CreationHelper factory = sheet.getWorkbook().getCreationHelper();
        ClientAnchor anchor = factory.createClientAnchor();
        anchor.setCol1(cell.getColumnIndex());
        anchor.setCol2(cell.getColumnIndex());
        anchor.setRow1(row.getRowNum());
        anchor.setRow2(row.getRowNum());

        Drawing drawing = sheet.createDrawingPatriarch();
        Comment comment = drawing.createCellComment(anchor);
        RichTextString str = factory.createRichTextString(REPORT_COMMENT);
        comment.setString(str);
        //comment.setAuthor("Akvo Flow v1.9.99"); //Not visible in the spreadsheet applications.
        cell.setCellComment(comment);
    } 
    
    private int addMetaDataHeaders(Sheet sheet, boolean showRepeatColumn) {
        Row row = getRow(doGroupHeaders ? 1 : 0, sheet);
        int columnIdx = -1;
        addMetaDataColumnHeader(IDENTIFIER_LABEL.get(locale), ++columnIdx, row);
        if (hasDataApproval()) {
            addMetaDataColumnHeader(DATA_APPROVAL_STATUS_LABEL.get(locale), ++columnIdx, row);
        }
        if (showRepeatColumn) {
            addMetaDataColumnHeader(REPEAT_LABEL.get(locale), ++columnIdx, row);
        }
        addMetaDataColumnHeader(DISPLAY_NAME_LABEL.get(locale), ++columnIdx, row);
        addMetaDataColumnHeader(DEVICE_IDENTIFIER_LABEL.get(locale), ++columnIdx, row);
        addMetaDataColumnHeader(INSTANCE_LABEL.get(locale), ++columnIdx, row);
        addMetaDataColumnHeader(SUB_DATE_LABEL.get(locale), ++columnIdx, row);
        addMetaDataColumnHeader(SUBMITTER_LABEL.get(locale), ++columnIdx, row);
        addMetaDataColumnHeader(DURATION_LABEL.get(locale), ++columnIdx, row);
        //Always put something in the top-left corner for the comment
        if (doGroupHeaders) {
            row = getRow(0, sheet);
            addMetaDataColumnHeader(METADATA_LABEL, 0, row); //constant (locale is going away)           
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnIdx));
        }
        return columnIdx;
    }

    /*
     * Add a meta data column header to the report.
     */
    private void addMetaDataColumnHeader(String columnHeaderName, int columnIdx, Row row) {
        //columnIndexMap.put(columnHeaderName, columnIdx);
        //metadata no longer in map since it is different on different sheets
        createCell(row, columnIdx, columnHeaderName, headerStyle);
    }


    private int writeRawDataGroupHeaders(Sheet sheet, QuestionGroupDto group, List<QuestionDto> questions, final int startOffset) {
        int offset = startOffset;
        Row row = getRow(doGroupHeaders ? 1 : 0, sheet);

        for (QuestionDto q : questions) {
            questionIdList.add(q.getKeyId().toString());

            String questionId = q.getQuestionId();
            final boolean useQID = useQuestionId && questionId != null
                    && !questionId.equals("");

            String columnLocale = useQID ? "en" : locale;
            columnIndexMap.put(q.getKeyId().toString(), offset);

            if (QuestionType.GEO == q.getType()) {
                offset = addGeoDataColumnHeader(q, row, offset, questionId, useQuestionId,
                        useQID, columnLocale);
            } else if (QuestionType.PHOTO == q.getType()) {
                offset = addPhotoDataColumnHeader(q, row, offset, questionId,
                        useQuestionId, useQID, columnLocale);
            } else if (QuestionType.CASCADE == q.getType()
                    && q.getLevelNames() != null && useQuestionId) {
                // if no cascade assigned, column is not shown
                for (String level : q.getLevelNames()) {
                    String levelName = useQID ? questionId + "_"
                            + level.replaceAll(" ", "_")
                            : getLocalizedText(q.getText(),
                                    q.getTranslationMap())
                                    + " - " + level;
                    createCell(row, offset++, levelName, headerStyle);
                }
            } else if (QuestionType.CADDISFLY == q.getType()) {
                offset = addCaddisflyDataHeaderColumns(q, row, offset, questionId,
                        useQID, columnLocale);
            } else { // All other types
                String header = "";
                if (useQID) {
                    header = questionId;
                } else if (useQuestionId) {
                    header = getLocalizedText(q.getText(),
                            q.getTranslationMap()).replaceAll("\n", "").trim();
                } else {
                    header = q.getKeyId().toString()
                            + "|"
                            + getLocalizedText(q.getText(),
                                    q.getTranslationMap())
                                    .replaceAll("\n", "").trim();
                }

                createCell(row, offset++, header, headerStyle);

                // check if we need to create columns for all options
                //TODO cascade
                if (QuestionType.OPTION == q.getType() && useQuestionId) {

                    // get options for question and create columns
                    OptionContainerDto ocDto = q.getOptionContainerDto();
                    if (ocDto != null) { //used to be legal
                        List<QuestionOptionDto> qoList = ocDto.getOptionsList();
                        if (qoList != null) {
                            for (QuestionOptionDto qo : qoList) {
                                // create header column
                                header = (qo.getCode() != null
                                        && !qo.getCode().equals("null")
                                        && qo.getCode().length() > 0)
                                        ? qo.getCode() + ":"
                                        : "";
                                createCell(row,
                                        offset++,
                                        "--OPTION--|" + header + qo.getText(),
                                        headerStyle);
                            }

                            // add 'other' column if needed
                            if (q.getAllowOtherFlag()) {
                                createCell(row,
                                        offset++,
                                        "--OTHER--",
                                        headerStyle);
                            }

                            optionMap.put(q.getKeyId(), qoList);
                            allowOtherMap.put(q.getKeyId(), q.getAllowOtherFlag());
                        }
                    }
                }
            }
            //TODO: add cascade
            if (!(QuestionType.NUMBER == q.getType() 
                    || QuestionType.OPTION == q.getType())) {
                unsummarizable.add(q.getKeyId().toString());
            }
        }
        if (doGroupHeaders) {
            //Now we know the width; write the group name spanned over entire group
            createCell(getRow(0, sheet), startOffset,
                    "Group " + group.getOrder() + " - " + group.getCode(), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, startOffset, offset - 1));
        }
        return offset;
    }
    
    @SuppressWarnings("unchecked")
    private int addCaddisflyDataHeaderColumns(QuestionDto q, Row row, int originalOffset,
            String questionId, final boolean useQID, String columnLocale) {
        int offset = originalOffset;
        Map<String, CaddisflyResource> caddisflyResourceMap = null;
        StringBuilder caddisflyFirstResultColumnHeaderPrefix = new StringBuilder();
        if (useQID) {
            caddisflyFirstResultColumnHeaderPrefix.append(questionId);
        } else {
            caddisflyFirstResultColumnHeaderPrefix.append(q.getKeyId());
        }
        caddisflyFirstResultColumnHeaderPrefix.append("|").append(q.getText()).append("|");

        if (caddisflyResourceMap == null) {
            caddisflyResourceMap = new HashMap<String, CaddisflyResource>();
            for (CaddisflyResource r : retrieveCaddisflyTestsDefinitions()) {
                caddisflyResourceMap.put(r.getUuid().trim(), r);
            }
        }
        
        CaddisflyResource cr = null;
        String resId = q.getCaddisflyResourceUuid();
        if (resId != null) {
            cr =  caddisflyResourceMap.get(resId.trim());
        }
        // get expected results for this test, if it exists
        if (cr != null) {
            List<CaddisflyResult> crResults = cr.getResults();
            // sort results on id value
            Collections.sort(crResults);

            List<Integer> resultIds = new ArrayList<Integer>();

            // create column headers
            for (int i = 0; i < crResults.size(); i++) {
                // put result ids in map, so we can use it
                // for validation later
                CaddisflyResult result = crResults.get(i);
                resultIds.add(result.getId());

                StringBuilder columnHeaderSuffix = new StringBuilder(
                        result.getName());
                if (result.getUnit() != null && !result.getUnit().isEmpty()) {
                    columnHeaderSuffix
                        .append("(")
                        .append(result.getUnit())
                        .append(")");
                }

                String columnHeader;
                if (i == 0) {
                    columnHeader = caddisflyFirstResultColumnHeaderPrefix
                            .toString() + columnHeaderSuffix;
                } else {
                    columnHeader = "--CADDISFLY--|" + columnHeaderSuffix;
                }
                createCell(row, offset++, columnHeader, headerStyle);
            }

            if (cr.getHasImage()) {
                createCell(
                        row,
                        offset++,
                        "--CADDISFLY--|" + q.getText()
                                + "--"
                                + IMAGE_LABEL.get(columnLocale),
                        headerStyle);
            }

            // store hasImage in hashmap
            resultIdMap.put(q.getKeyId(), resultIds);
            hasImageMap.put(q.getKeyId(), cr.getHasImage());
        }
        return offset;
    }

    private List<CaddisflyResource> retrieveCaddisflyTestsDefinitions() {
        if (caddisflyTestsFileUrl == null || caddisflyTestsFileUrl.isEmpty()) {
            return caddisflyResourceDao.listResources();
        } else {
            return caddisflyResourceDao.listResources(caddisflyTestsFileUrl);
        }
    }

    private int addPhotoDataColumnHeader(QuestionDto q, Row row, int originalOffset, String questionId,
            boolean useQuestionId, final boolean useQID, String columnLocale) {
        int offset = originalOffset;
        // Always a URL column
        String header = "";
        if (useQID) {
            header = questionId;
        } else if (useQuestionId) {
            header = getLocalizedText(q.getText(),
                    q.getTranslationMap()).replaceAll("\n", "").trim();
        } else {
            header = q.getKeyId().toString()
                    + "|"
                    + getLocalizedText(q.getText(),
                            q.getTranslationMap()).replaceAll("\n", "").trim();
        }
        createCell(row, offset++, header, headerStyle);
        if (useQuestionId) {
            // Media gets 3 extra columns: Latitude, Longitude and Accuracy
            String prefix = "--PHOTO--|";
            createCell(row, offset++,
                    prefix + LAT_LABEL.get(columnLocale), headerStyle);
            createCell(row, offset++,
                    prefix + LON_LABEL.get(columnLocale), headerStyle);
            createCell(row, offset++,
                    prefix + ACC_LABEL.get(columnLocale), headerStyle);
        }
        return offset;
    }

    private int addGeoDataColumnHeader(QuestionDto q, Row row, int originalOffset, String questionId,
            boolean useQuestionId, final boolean useQID, String columnLocale) {
        int offset = originalOffset;
        if (useQuestionId) {
            createCell(
                    row,
                    offset++,
                    (useQID ? questionId + "_"
                            : getLocalizedText(q.getText(),
                                    q.getTranslationMap())
                                    + " - ")
                            + LAT_LABEL.get(columnLocale),
                    headerStyle);
        } else {
            createCell(row, offset++, q.getKeyId() + "|"
                    + LAT_LABEL.get(columnLocale),
                    headerStyle);
        }
        createCell(row, offset++, (useQID ? questionId
                + "_" : "--GEOLON--|")
                + LON_LABEL.get(columnLocale), headerStyle);
        createCell(row, offset++, (useQID ? questionId
                + "_" : "--GEOELE--|")
                + ELEV_LABEL.get(columnLocale), headerStyle);
        String codeLabel = CODE_LABEL.get(columnLocale);
        createCell(row, offset++, useQID ? questionId + "_"
                + codeLabel.replaceAll("\\s", "")
                : "--GEOCODE--|" + codeLabel, headerStyle);
        return offset;
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
        String curTitle = WorkbookUtil.createSafeSheetName(title); //first try the whole sector name
        while (sheet == null) {
            sheet = wb.getSheet(curTitle);
            if (sheet == null) { //Name free - use it
                sheet = wb.createSheet(curTitle);
            } else { //Name in use, try another. Max is 31 chars.
                sheet = null;
                curTitle = WorkbookUtil.createSafeSheetName(
                        title.substring(0,Math.min(title.length(),27)) + " " + sheetCount
                        );
                if (++sheetCount >= 1000) {
                    throw new Exception("Could not create unique sheet name after 1000 tries.");
                }
            }
        }
        CreationHelper creationHelper = wb.getCreationHelper();
        Drawing patriarch = sheet.createDrawingPatriarch();
        int curRow = 0;
        Row row = getRow(curRow++, sheet);
        if (sector == null) {
            createCell(row, 0, REPORT_HEADER.get(locale), headerStyle);
        } else {
            createCell(row, 0, sector + " " + REPORT_HEADER.get(locale), headerStyle);
        }
        for (QuestionGroupDto group : orderedGroupList) {
            if (questionMap.get(group) != null) {
                for (QuestionDto question : questionMap.get(group)) {
                    if (!(QuestionType.OPTION == question.getType() //TODO: add cascade
                            || QuestionType.NUMBER == question.getType())) {
                        continue;
                    } else {
                        if (summaryModel.getResponseCountsForQuestion(
                                question.getKeyId(), sector).size() == 0) {
                            // if there is no data, skip the question
                            continue;
                        }
                    }
                    // for both options and numeric, we want a pie chart and
                    // data table. for numeric, we also want descriptive
                    // statistics.
                    int tableTopRow = curRow++;
                    int tableBottomRow = curRow;
                    row = getRow(tableTopRow, sheet);
                    // span the question heading over the data table
                    sheet.addMergedRegion(new CellRangeAddress(curRow - 1, curRow - 1, 0, 2));
                    createCell(
                            row,
                            0,
                            getLocalizedText(question.getText(), question.getTranslationMap()),
                            headerStyle);
                    DescriptiveStats stats = summaryModel
                            .getDescriptiveStatsForQuestion(
                                    question.getKeyId(), sector);
                    if (stats != null && stats.getSampleCount() > 0) {
                        sheet.addMergedRegion(new CellRangeAddress(curRow - 1, curRow - 1, 4, 5));
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
                            .getResponseCountsForQuestion(question.getKeyId(), sector);
                    int sampleTotal = 0;
                    List<String> labels = new ArrayList<String>();
                    List<String> values = new ArrayList<String>();
                    int firstOptRow = curRow;
                    for (Entry<String, Long> count : counts.entrySet()) {
                        row = getRow(curRow++, sheet);
                        String labelText = count.getKey();
                        if (labelText == null) {
                            labelText = "";
                        } else {
                            // Handle the json option question response type
                            if (labelText.startsWith("[")) {
                                try {
                                    List<Map<String, String>> optionNodes = OBJECT_MAPPER
                                            .readValue(
                                                    labelText,
                                                    new TypeReference<List<Map<String, String>>>() {
                                                    });
                                    StringBuilder labelTextBuilder = new StringBuilder();

                                    for (Map<String, String> optionNode : optionNodes) {
                                        labelTextBuilder.append("|");
                                        labelTextBuilder.append(optionNode.get("text"));
                                    }
                                    if (labelTextBuilder.length() > 0) {
                                        labelTextBuilder.deleteCharAt(0);
                                    }
                                    labelText = labelTextBuilder.toString();
                                } catch (IOException e) {
                                }
                            }
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
                                            .get(i)) / sampleTotal)), null);
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
                                    if (val != null && new Double(val.trim()) > 0D) {
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
                            int indx = wb.addPicture(
                                    JFreechartChartUtil.getPieChart(
                                            labels,
                                            values,
                                            getLocalizedText(
                                                    question.getText(),
                                                    question.getTranslationMap()),
                                                    CHART_WIDTH,
                                                    CHART_HEIGHT),
                                            Workbook.PICTURE_TYPE_PNG);
                            ClientAnchor anchor = creationHelper.createClientAnchor();
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
    protected Cell createCell(Row row, int col, String value) {
        return createCell(row, col, value, null, -1);
    }

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
     * @param sheet
     * @return
     */
    private synchronized Row getRow(int index, Sheet sheet) {

        Row row = sheet.getRow(index);
        if (row == null) {
            row = sheet.createRow(index);
        }
        return row;

    }

    /**
     * sets instance variables to the values passed in in the Option map. If the option is not set,
     * the default values are used.
     *
     * @param options
     */
    protected boolean processOptions(Map<String, String> options) {
        isFullReport = true;
        performGeoRollup = true;
        generateCharts = true;
        separateSheetsForRepeatableGroups = false;
        doGroupHeaders = false;
        useQuestionId = false;
        
        if (options != null) {
            log.debug(options);

            //What kind of report?
            String reportType = options.get(TYPE_OPT);
            if (reportType == null || reportType.isEmpty()) {
                log.error(TYPE_OPT + " was not set.");
                return false;
            } else
            if (DATA_CLEANING_TYPE.equalsIgnoreCase(reportType)) {
                isFullReport = false;
                doGroupHeaders = true;
                separateSheetsForRepeatableGroups = true;
                useQuestionId = false; //So we can import
            } else if (DATA_ANALYSIS_TYPE.equalsIgnoreCase(reportType)) {
                isFullReport = false;
                doGroupHeaders = false;
                separateSheetsForRepeatableGroups = false;
                useQuestionId = true; //also splits options into columns, and prevents digests
            } else if (COMPREHENSIVE_TYPE.equalsIgnoreCase(reportType)) {
                isFullReport = true;
                doGroupHeaders = false;
                separateSheetsForRepeatableGroups = false;
                useQuestionId = false; //is this correct?
            } else {
                log.error("Unknown value " + reportType + " for " + TYPE_OPT);                
                return false;
            }
            
            locale = options.get(LOCALE_OPT); 
            imagePrefix = options.get(IMAGE_PREFIX_OPT);
            selectionFrom = options.get(FROM_OPT);
            selectionTo = options.get(TO_OPT);
            selectionLimit = options.get(MAX_ROWS_OPT);

            if (options.get(LAST_COLLECTION_OPT) != null
                    && "true".equals(options.get(LAST_COLLECTION_OPT))) {
                lastCollection = true;
            }

            if (options.get(CADDISFLY_TESTS_FILE_URL_OPT) != null
                    && !options.get(CADDISFLY_TESTS_FILE_URL_OPT).isEmpty()) {
                caddisflyTestsFileUrl = options.get(CADDISFLY_TESTS_FILE_URL_OPT);
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
        return true;
    }

    /**
     * call the server to augment the data already loaded in each QuestionDto in the map passed in.
     *
     * @param questionMap
     * @param apiKey
     */
    private void loadFullQuestions(
            Map<QuestionGroupDto, List<QuestionDto>> questionMap, String apiKey) {
        for (List<QuestionDto> questionList : questionMap.values()) {
            for (int i = 0; i < questionList.size(); i++) {
                try {
                    QuestionDto newQ = BulkDataServiceClient.loadQuestionDetails(
                            serverBase, questionList.get(i).getKeyId(), apiKey);
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

    // This main() method is only used for debugging;
    // when deployed on server, export() is called from Clojure code
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
        options.put(TYPE_OPT, DATA_CLEANING_TYPE);
//        options.put(TYPE_OPT, DATA_ANALYSIS_TYPE);
//        options.put(TYPE_OPT, COMPREHENSIVE_TYPE);
        options.put(LAST_COLLECTION_OPT, "false");
        options.put(EMAIL_OPT, "email@example.com");
        options.put(FROM_OPT, null);
        options.put(TO_OPT, null);
        options.put(MAX_ROWS_OPT, null);

        criteria.put(SurveyRestRequest.SURVEY_ID_PARAM, args[2]);
        criteria.put("apiKey", args[3]);
        exporter.export(criteria, new File(args[0]), args[1], options);
    }
}
