/*
 *  Copyright (C) 2010-2022 Stichting Akvo (Akvo Foundation)
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.core.type.TypeReference;
import org.akvo.flow.domain.DataUtils;
import org.akvo.flow.util.FlowJsonObjectReader;
import org.akvo.flow.util.JFreechartChartUtil;
import org.akvo.flow.xml.PublishedForm;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.InstanceDataDto;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;
import org.waterforpeople.mapping.domain.CaddisflyResource;
import org.waterforpeople.mapping.domain.CaddisflyResult;
import org.waterforpeople.mapping.domain.response.value.Media;
import org.waterforpeople.mapping.serialization.response.MediaResponse;
import static org.waterforpeople.mapping.dataexport.ExportImportConstants.*;
import static org.waterforpeople.mapping.dataexport.ExportImportUtils.parseCellAsString;

import com.gallatinsystems.common.util.StringUtil;
import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.survey.dao.CaddisflyResourceDao;

import static com.gallatinsystems.common.Constants.*;

/**
 * Enhancement of the SurveySummaryExporter to support writing to Excel and including chart images.
 *
 */
public class GraphicalSurveySummaryExporter extends SurveySummaryExporter {

    private static final Logger log = Logger
            .getLogger(GraphicalSurveySummaryExporter.class.getName());

    private static final String IMAGE_PREFIX_OPT = "imgPrefix";
    private static final String TYPE_OPT = "exportMode";
    private static final String DATA_CLEANING_TYPE = "DATA_CLEANING";
    private static final String DATA_ANALYSIS_TYPE = "DATA_ANALYSIS";
    private static final String COMPREHENSIVE_TYPE = "COMPREHENSIVE";
    private static final String LAST_COLLECTION_OPT = "lastCollection";
    private static final String MAX_ROWS_OPT = "maxDataReportRows";
    private static final String FROM_OPT = "from";
    private static final String TO_OPT = "to";
    private static final String EMAIL_OPT = "email";
    private static final String UPLOAD_URL_OPT = "uploadUrl";
    private static final String UPLOAD_DIR_OPT = "uploadDir";
    private static final String PUBLISHED_OPT = "use PublishedForm";

    private static final String CADDISFLY_TESTS_FILE_URL_OPT = "caddisflyTestsFileUrl";

    private static final String DEFAULT_IMAGE_PREFIX = "http://waterforpeople.s3.amazonaws.com/images/";

    private static final String DIGEST_COLUMN = "NO_TITLE_DIGEST_COLUMN";

    //Statistics page header and labels
    private static final String REPORT_HEADER = "Survey Summary Report";
    private static final String FREQ_LABEL = "Frequency";
    private static final String PCT_LABEL = "Percent";
    private static final String SUMMARY_LABEL = "Summary";
    private static final String RAW_DATA_LABEL = "Raw Data";
    private static final String MEAN_LABEL = "Mean";
    private static final String MEDIAN_LABEL = "Median";
    private static final String MIN_LABEL = "Min";
    private static final String MAX_LABEL = "Max";
    private static final String VAR_LABEL = "Variance";
    private static final String STD_E_LABEL = "Std Error";
    private static final String STD_D_LABEL = "Std Deviation";
    private static final String TOTAL_LABEL = "Total";
    private static final String RANGE_LABEL = "Range";

    // Maximum number of rows of a sheet kept in memory by the streaming API
    // We must take care to never go back up longer than this
    // Max repeats in one instance we have seen as of 2019-04-15 is 172(!)
    private static final int WORKBOOK_WINDOW = 100;

    // Formatting for comprehensive summary sheet graphs
    private static final int CHART_WIDTH = 600;
    private static final int CHART_HEIGHT = 400;
    private static final int CHART_CELL_WIDTH = 10;
    private static final int CHART_CELL_HEIGHT = 22;
    private static final NumberFormat PCT_FMT = DecimalFormat.getPercentInstance();
    private static final int GEO_COLUMN_COUNT = 3;

    private CellStyle headerStyle;
    private CellStyle textStyle;
    private String imagePrefix;
    private String serverBase;
    private boolean includeSummarySheet;
    private boolean performGeoRollup;
    private boolean generateCharts;
    private boolean variableNamesInHeaders; // Also turns on splitting of answers into separate columns (options, geo, etc.) and turns off digests
    private boolean splitIntoColumns; // Turns on splitting of answers into separate columns (options, geo, etc.) and turns off digests
    private boolean separateSheetsForRepeatableGroups;
    private boolean justCodes; //Only output the codes from multiple-choice answers (option and cascade)
    private boolean doGroupHeaders; //First header line is group names spanned over the group columns
    private boolean usePublishedForm = true; //use the XML of last publication, not the current datastore state

    private Map<Long, QuestionDto> questionsById;
    private SurveyGroupDto surveyGroupDto;
    private boolean lastCollection = false;
    private CaddisflyResourceDao caddisflyResourceDao = new CaddisflyResourceDao();
    private String caddisflyTestsFileUrl;
    private String selectionFrom = null;
    private String selectionTo = null;
    private String selectionLimit = null;
    private String reportType = null;

    private Map<Long, Boolean> hasImageMap = new HashMap<>();
    private Map<Long, List<Integer>> resultIdMap = new HashMap<>();

    private Map<Long, List<QuestionOptionDto>> optionMap = new HashMap<>();
    private Map<Long, Boolean> allowOtherMap = new HashMap<>();
    private Map<String, Integer> optionsPositionCache = new HashMap<>();

    // store indices of file columns for lookup when generating responses
    private Map<String, Integer> columnIndexMap = new HashMap<>();

    // maps from a (repeatable) question group dto to the sheet that contains the raw data for it (if split)
    private Map<QuestionGroupDto, Sheet> qgSheetMap = new HashMap<>();

    // data about questions gathered while writing headers
    private List<String> questionIdList = new ArrayList<>();
    private List<String> unsummarizable = new ArrayList<>();

    Map<String, CaddisflyResource> caddisflyResourceMap = null;

    //data about the data
    private int totalInstances = 0;
    private int approvedInstances = 0;
    private Date firstSubmission = null;
    private Date lastSubmission = null;
    private Map<String, Integer> instancesByUser = new HashMap<>();
    private Map<String, Integer> instancesByDevice = new HashMap<>();
    private long totalDuration = 0L;
    private long minDuration = Long.MAX_VALUE;
    private long maxDuration = 0;

    @Override
    public void export(Map<String, String> criteria, File fileName,
            String serverBaseUrl, Map<String, String> options) {
        final String surveyId = criteria.get(SurveyRestRequest.SURVEY_ID_PARAM).trim();
        final String apiKey = criteria.get("apiKey").trim(); //To access cloud datastore
        final String s3formDir = options.get(UPLOAD_URL_OPT) + options.get(UPLOAD_DIR_OPT);

        log.finest("### Export criteria=" + criteria.toString() +
        		" filename=" + fileName.toString() +
        		" options=" + options.toString() );
        if (!processOptions(options)) {
            return;
        }

        questionsById = new HashMap<Long, QuestionDto>();

        surveyGroupDto = BulkDataServiceClient.fetchSurveyGroup(surveyId, serverBaseUrl, apiKey);

        serverBase = serverBaseUrl;

        orderedGroupList = new ArrayList<QuestionGroupDto>(); //Must exist

        try {
            Map<QuestionGroupDto, List<QuestionDto>> questionMap =
                    fetchQuestionMap(usePublishedForm, surveyId, serverBaseUrl, apiKey, s3formDir, orderedGroupList);

            if (questionMap.size() > 0) {
                //questionMap is now stable; make the id-to-dto map
                for (List<QuestionDto> qList : questionMap.values()) {
                    for (QuestionDto q : qList) {
                        questionsById.put(q.getKeyId(), q);
                    }
                }

                Workbook wb = createWorkbookAndFormats();

                Sheet baseSheet = createDataSheets(wb, orderedGroupList);

                SummaryModel model = fetchAndWriteRawData(
                        surveyId,
                        questionMap,
                        wb, baseSheet,
                        includeSummarySheet,
                        fileName,
                        apiKey);

                if (includeSummarySheet) {
                    writeStatsAndGraphsSheet(questionMap, model, null, wb);
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
                        writeStatsAndGraphsSheet(questionMap, model, sector, wb);
                    }
                }

                FileOutputStream fileOut = new FileOutputStream(fileName);
                wb.setActiveSheet(includeSummarySheet ? wb.getNumberOfSheets()-1 : 0);
                wb.write(fileOut);
                fileOut.close();

            } else {
                log.info("No questions for survey: "
                        + criteria.get(SurveyRestRequest.SURVEY_ID_PARAM)
                        + " - instance: " + serverBaseUrl);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE,"Error generating report: " + e.getMessage(), e);
            //TODO: return info to user
        }
    }

    private Workbook createWorkbookAndFormats() {
        Workbook wb = new SXSSFWorkbook(WORKBOOK_WINDOW);
        headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.LEFT);
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        short textFormat = wb.createDataFormat().getFormat("@"); // built-in text format

        textStyle = wb.createCellStyle();
        textStyle.setDataFormat(textFormat);
        // We tried a format like "0.###" to suppress scientific notation in number
        // answer cells, but it looked bad in Excel - "3" was shown as "3."

        return wb;
    };


    private Sheet createDataSheets(Workbook wb, List<QuestionGroupDto> groupList) {
        //make base sheet (for non-repeated data)
        Sheet baseSheet = wb.createSheet(RAW_DATA_LABEL);

        for (QuestionGroupDto groupDto : groupList) {
            if (separateSheetsForRepeatableGroups && safeTrue(groupDto.getRepeatable())) {
                // breaking this qg out, so create the sheet for it
                Sheet repSheet = wb.createSheet("Group " + groupDto.getOrder());
                qgSheetMap.put(groupDto, repSheet);  //Key not ḱnown for published groups
            }
        }

        return baseSheet;
    }


    private Map<QuestionGroupDto, List<QuestionDto>> fetchQuestionMap(
            boolean usingPublishedForm,
            String surveyId,
            String serverBaseUrl,
            String apiKey,
            String s3FormDir,
            List<QuestionGroupDto> groupList) throws Exception {
        Map<QuestionGroupDto, List<QuestionDto>> questionMap = null;
        if (!usingPublishedForm) {
            //Legacy method - use datastore current (potentially unpublished) form structure
            // Get minimal data plus cascade level names
            questionMap = loadAllQuestions(surveyId, performGeoRollup, serverBaseUrl, apiKey);
            // Need options to be able to split out "other" value
            loadQuestionOptions(surveyId, serverBaseUrl, questionMap, apiKey);
        } else {
            //Fetch the published form from S3, latest version
            String fileName = s3FormDir + "/" + surveyId + ".zip"; //no "v2.0" etc at end
            String formXml = null;
            final URL url = new URL(fileName);
            log.finest("Getting form XML from " + url);
            final URLConnection conn = url.openConnection();
            final BufferedInputStream deviceZipFileInputStream = new BufferedInputStream(conn.getInputStream());
            final ZipInputStream formFileStream = new ZipInputStream(deviceZipFileInputStream);
            formXml = ZipUtil.unZipFile(surveyId + ".xml", formFileStream);

            SurveyDto formDto = PublishedForm.parse(formXml).toDto();
            questionMap = new HashMap<>();
            rollupOrder = new ArrayList<QuestionDto>();
            boolean anyQuestionsWithoutVariableNames = false;

            for (QuestionGroupDto qgd : formDto.getQuestionGroupList()) {
                //Transform the question maps to question lists
                //An alternative solution (since this is the only use of this map *anywhere*),
                //  would be to change the map in the dto to a list...
                List<QuestionDto> qList = new ArrayList<>();
                List<QuestionDto> questionList = qgd.getQuestionList();

                if (questionList != null) {
                    //make sure the questions are ordered correctly
                    TreeMap<Integer,QuestionDto> qMap = new TreeMap<>();
                    for (QuestionDto q : questionList) {
                        qMap.put(q.getOrder(), q);
                    }
                    questionList = new ArrayList<>(qMap.values());
                    for (QuestionDto dto : questionList) { //Ordered by key
                        qList.add(dto);
                        if (dto.getVariableName() == null) {
                            anyQuestionsWithoutVariableNames = true;
                        }
                        //See if any questions have names that indicate they belong in rollups
                        if (performGeoRollup) {
                            for (int i = 0; i < ROLLUP_QUESTIONS.length; i++) {
                                if (ROLLUP_QUESTIONS[i].equalsIgnoreCase(dto.getText())) {
                                    rollupOrder.add(dto);
                                }
                            }
                        }
                    }
                }
                questionMap.put(qgd, qList);
                groupList.add(qgd); //The XML is written with the groups in order (but without any attributes saying so)
            }

            //Fall back to getting variable names from the datastore, since VNs in XML is a recent addition
            if (variableNamesInHeaders && anyQuestionsWithoutVariableNames) {
                loadVariableNames(surveyId, serverBaseUrl, questionMap, apiKey); //expensive and imperfect fallback
            }

        }
        return questionMap;
    }


    private boolean hasDataApproval() {
        return surveyGroupDto != null
                && safeTrue(surveyGroupDto.getRequireDataApproval())
                && surveyGroupDto.getDataApprovalGroupId() != null;
    }

    private boolean safeTrue(Boolean b) {
        return b != null && b.booleanValue();
    }

    private String neverNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
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
                    if (collapseIdMap.get(safeQuestionText(q)) == null) {
                        collapseIdMap.put(safeQuestionText(q), q.getKeyId().toString());
                    }
                    nameToIdMap.put(q.getKeyId().toString(), safeQuestionText(q));
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
                            log.log(Level.SEVERE, "Error fetching instance data ", e);
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
                log.finest("Sleeping, Queue has: " + jobQueue.size());
                Thread.sleep(5000);
            } catch (Exception e) {
                log.finest("Error thread sleep: " + e.getMessage());
            }
        }

        sortDataOnCollectionDate(allData);
        analyseData(allData);

        // write the data now, row by row
        // (We cannot go back up more than the Spreadsheet writer window)
        for (InstanceData instanceData : allData) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            if (separateSheetsForRepeatableGroups) {
                List<QuestionDto> baseSheetQuestions = new ArrayList<>(); //Could be empty if all repeatable!

                //For each group, write the repeats from top to bottom
                for (Entry<QuestionGroupDto, List<QuestionDto>> groupEntry : questionMap.entrySet()) {
                    if (safeTrue(groupEntry.getKey().getRepeatable())) {
                        writeInstanceDataSplit(qgSheetMap.get(groupEntry.getKey()),
                                instanceData,
                                groupEntry.getValue(),
                                digest,
                                true);
                    } else { //might never be called
                        baseSheetQuestions.addAll(groupEntry.getValue());
                    }
                }
                // Now do the rest on the base sheet
                writeInstanceDataSplit(baseSheet,
                        instanceData,
                        baseSheetQuestions,
                        digest,
                        false);

                if (!variableNamesInHeaders) {
                    String digestStr = StringUtil.toHexString(digest.digest());
                    // now add 1 more column on the base sheet that contains the digest
                    createCell(getRow(baseSheet.getLastRowNum(), baseSheet),
                            columnIndexMap.get(DIGEST_COLUMN),
                            digestStr,
                            null);
                }


            } else { //just one sheet - do all at once with a global repeat column, no digest
                int baseCurrentRow = baseSheet.getLastRowNum() + 1;
                baseCurrentRow = writeInstanceData(baseSheet, baseCurrentRow, instanceData,
                        generateSummary, nameToIdMap, collapseIdMap, model);
            }
        }

        threadPool.shutdown();
        return model;
    }

    private void sortDataOnCollectionDate(final List<InstanceData> allData) {
        log.finest("Starting data sort");
        Collections.sort(allData, new Comparator<InstanceData>() {
            @Override
            public int compare(InstanceData o1, InstanceData o2) {
                // by submission date
                return safeCompare(
                        o1.surveyInstanceDto.getCollectionDate(),
                        o2.surveyInstanceDto.getCollectionDate()
                        );
            }
            private int safeCompare(Date date1 , Date date2 ) {
                if (date1 == null || date2 == null) {
                    return 0;
                }
                return date1.compareTo(date2);
            }
        });
        log.finest("Finished data sort");
    }

    //gather some statistics on the data collection
    private void analyseData(final List<InstanceData> allData) {
        totalInstances = allData.size();

        for (InstanceData instance : allData) {
            SurveyInstanceDto sid = instance.surveyInstanceDto;
            if (sid.getApprovedFlag() != null && sid.getApprovedFlag().equalsIgnoreCase("true")) {
                approvedInstances++;
            }

            Date cd = sid.getCollectionDate();
            if (firstSubmission == null || cd.before(firstSubmission)) {
                firstSubmission = cd;
            }
            if (lastSubmission == null || cd.after(lastSubmission)) {
                lastSubmission = cd;
            }
            String u = sid.getSubmitterName();
            if (instancesByUser.containsKey(u)) {
                instancesByUser.put(u, instancesByUser.get(u) + 1);
            } else {
                instancesByUser.put(u, Integer.valueOf(1));
            }
            String dev = sid.getDeviceIdentifier();
            if (instancesByDevice.containsKey(dev)) {
                instancesByDevice.put(dev, instancesByDevice.get(dev) + 1);
            } else {
                instancesByDevice.put(dev, Integer.valueOf(1));
            }
            Long durationSeconds = sid.getSurveyalTime();
            if (durationSeconds != null) {
                totalDuration += durationSeconds;
                maxDuration = Math.max(maxDuration, durationSeconds);
                minDuration = Math.min(minDuration, durationSeconds);
            }

        }
    }

    /**
     * @param sheet
     * @param instanceData
     * @param whichQuestions
     * @param md
     * @param groupSheet
     * @throws NoSuchAlgorithmException
     */
    private synchronized void writeInstanceDataSplit(
            Sheet sheet,
            InstanceData instanceData,
            List<QuestionDto> whichQuestions,
            MessageDigest md,
            boolean groupSheet)
            throws NoSuchAlgorithmException {

        int startRow = sheet.getLastRowNum() + 1;
        //iterationsPresent is for entire instance. We need it for this sheet only!
        Set<Long> iterationsOnThisSheet = new TreeSet<>(); //Ordered
        if (!groupSheet) { //Must have one row of metadata on base sheet, even if no values
            iterationsOnThisSheet.add(0L);
        }
        for (QuestionDto qd : whichQuestions) {
            final Long questionId = qd.getKeyId();
            SortedMap<Long, String> iterationsMap = instanceData.responseMap.get(questionId);
            if (iterationsMap != null) {
                iterationsOnThisSheet.addAll(iterationsMap.keySet());
            }
        }

        int rowOffset = 0;
        for (long i : iterationsOnThisSheet) {
            Row iterationRow = getRow(startRow + rowOffset, sheet);
            writeMetadataRow(iterationRow, instanceData, (int) i + 1, groupSheet); //assume <2 billion repeats
            for (QuestionDto qd : whichQuestions) {
                final Long questionId = qd.getKeyId();
                SortedMap<Long, String> iterationsMap = instanceData.responseMap.get(questionId);
                if (iterationsMap == null) {
                    continue; //No answers on this sheet
                }
                String val = iterationsMap.get(Long.valueOf(i));
                if (val != null) {
                    writeAnswer(iterationRow, columnIndexMap.get(questionId.toString()), qd, val);
                }
            }
            //Include this row in the digest
            for (Cell cell : iterationRow) {
                String val = parseCellAsString(cell);
                if (val != null && !val.equals("")) {
                    md.update(val.getBytes());
                }
            }
            rowOffset++;
        }
    }


    //We write one row at a time to stay in window
    private void writeMetadataRow(Row r,
            InstanceData instanceData,
            final int iteration,
            final boolean showRepeatColumn) {
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
            createCell(r, col++, String.valueOf(iteration), null, Cell.CELL_TYPE_NUMERIC);
        }
        // Write other metadata
        createCell(r, col++, dto.getSurveyedLocaleDisplayName());
        createCell(r, col++, dto.getDeviceIdentifier());
        createCell(r, col++, dto.getKeyId().toString());
        createCell(r, col++, ExportImportUtils.formatDateTime(dto.getCollectionDate()));
        createCell(r, col++, sanitize(dto.getSubmitterName()));
        String duration = getDurationText(dto.getSurveyalTime());
        createCell(r, col++, duration);
        Double v = dto.getFormVersion();
        if (v != null) {
            createCell(r, col++, v);
        }
        //TODO else a cell with 0.0??
    }

    /**
     * Writes all the data for a single survey instance (form instance) to the same sheet.
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
            SummaryModel model) {

        //iterationsPresent is a set because there may be gaps
        int rowOffset = 0;
        for (long i : instanceData.iterationsPresent) { //sorted
            Row iterationRow = getRow(startRow + rowOffset, sheet);
            writeMetadataRow(iterationRow, instanceData, (int)i + 1, true); //assume <2 billion iterations
            for (String q : questionIdList) {
                final Long questionId = Long.valueOf(q);
                final QuestionDto questionDto = questionsById.get(questionId);
                SortedMap<Long, String> iterationsMap = instanceData.responseMap.get(questionId);
                if (iterationsMap == null) {
                    continue;
                }
                String val = iterationsMap.get(i);
                if (val != null) {
                    writeAnswer(iterationRow, columnIndexMap.get(q), questionDto, val);
                }
            }
            rowOffset++;
        }

        int maxRow = startRow + rowOffset;


        // Rebuild old response map format for rollups from instanceData.responseMap
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
            FlowJsonObjectReader jsonReader = new FlowJsonObjectReader();
            TypeReference<List<Map<String, String>>> typeReference = new TypeReference<List<Map<String, String>>>() {};


            for (Entry<String, String> entry : responseMap.entrySet()) {
                //OPTION, NUMBER and CASCADE summarizable now.
                if (!unsummarizable.contains(entry.getKey())) {
                    String effectiveId = entry.getKey();
                    if (nameToIdMap.get(effectiveId) != null) {
                        effectiveId = collapseIdMap.get(nameToIdMap.get(effectiveId));
                    }

                    String[] vals;
                    if (entry.getValue().startsWith("[")) { //JSON
                        try {
                            List<Map<String, String>> optionNodes = jsonReader.readObject(entry.getValue(), typeReference);
                            List<String> valsList = new ArrayList<>();
                            for (Map<String, String> optionNode : optionNodes) {
                                if (optionNode.containsKey("text")) {
                                    valsList.add(optionNode.get("text")); // OPTION and NUMBER
                                } else if (optionNode.containsKey("name")) {
                                    valsList.clear(); // Keep only the last one
                                    valsList.add(optionNode.get("name")); // "name" for CASCADE
                                }
                            }
                            vals = valsList.toArray(new String[valsList.size()]);
                        } catch (IOException e) {
                            vals = entry.getValue().split("\\|");
                        }
                    } else {
                        vals = entry.getValue().split("\\|");
                    }

                    synchronized (model) {
                        for (int j = 0; j < vals.length; j++) {
                            if (vals[j] != null && vals[j].trim().length() > 0) {
                                QuestionDto q = questionsById.get(Long.valueOf(effectiveId));
                                model.tallyResponse(effectiveId, rollups, vals[j], q);
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
     * @param row
     * @param startColumn
     * @param questionDto
     * @param value
     */
    private void writeAnswer(@Nonnull Row row,
            int startColumn,
            @Nonnull QuestionDto questionDto,
            @Nonnull String value) {

        assert value != null; //did not work when debugging in Eclipse

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

            case GEOSHAPE:
                cells.add(geoShapeValue(value));
                break;

            case CASCADE:
                if (questionDto.getLevelNames() != null) {
                    cells.addAll(cascadeCellValues(value, questionDto.getLevelNames().size()));
                } else {
                    log.warning("No CASCADE resource for question '" + questionDto.getText() + "'");
                }

                break;

            case OPTION:
                qId = questionDto.getKeyId();
                cells.addAll(optionCellValues(questionDto.getKeyId(), value,
                        optionMap.get(qId),
                        safeTrue(allowOtherMap.get(qId)),
                        safeTrue(questionDto.getAllowMultipleFlag()))
                        );
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
                //Normally numeric, unless that would cause rounding
                if (cellValue.length() < 16) {
                    createCell(row, col, cellValue, null, Cell.CELL_TYPE_NUMERIC);
                } else {
                    createCell(row, col, cellValue, textStyle);
                }
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
        if (filename != null) {
            filename = Paths.get(filename).getFileName().toString(); //strip path, if any
            if (filename.length() > 0) {
                cells.add(imagePrefix + filename);
                if (splitIntoColumns && media.getLocation() != null) {
                    cells.add(Double.toString(media.getLocation().getLatitude()));
                    cells.add(Double.toString(media.getLocation().getLongitude()));
                    cells.add(Double.toString(media.getLocation().getAccuracy()));
                }
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
        //discard geocode (if present)
        int partsToCopy = Math.min(geoParts.length, GEO_COLUMN_COUNT);
        for (count = 0; count < partsToCopy; count++) {
            cells.add(geoParts[count]);
        }
        // now handle any missing fields
        for (int j = count; j < GEO_COLUMN_COUNT; j++) {
            cells.add("");
        }

        return cells;
    }

    /*
     * Larger geoshapes with many points exceed the character limit of a cell
     * in Excel, so we limit the characters to prevent this error. We truncate
     * the geoshape but keep the original in the database
     *
     */
    protected static String geoShapeValue(String value) {
        if (value.length() > SpreadsheetVersion.EXCEL2007.getMaxTextLength()) {
            return value.substring(0, SpreadsheetVersion.EXCEL2007.getMaxTextLength());
        }
        return value;
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
            FlowJsonObjectReader jsonReader = new FlowJsonObjectReader();
            TypeReference<List<Map<String, String>>> typeReference = new TypeReference<List<Map<String, String>>>() {};

            try {
                cascadeNodes = jsonReader.readObject(value, typeReference);
            } catch (IOException e) {
                log.log(Level.WARNING, "Unable to parse CASCADE response - " + value, e);
            }
        } else if (!value.isEmpty()) {
            for (String name : value.split("\\|")) {
                Map<String, String> m = new HashMap<>();
                m.put("name", name);
                cascadeNodes.add(m);
            }
        }

        //We used to always set code=name if not otherwise set
        //and users did not like getting a:a, b:b, etc. everywhere
        boolean allCodesEqualsName = true;
        for (Map<String, String> cascadeNode : cascadeNodes) {
            String code = cascadeNode.get("code");
            String name = cascadeNode.get("name");
            if (code != null && !code.equalsIgnoreCase(name)) {
                allCodesEqualsName = false;
                break;
            }
        }
        if (allCodesEqualsName) {
            for (Map<String, String> cascadeNode : cascadeNodes) {
                cascadeNode.put("code", null);
            }
        }


        if (splitIntoColumns) {
            // +------------+------------+-----
            // |code1:value1|code2:value2| ...
            // +------------+------------+-----

            int padCount = levels - cascadeNodes.size();

            for (Map<String, String> map : cascadeNodes) {
                String code = map.get("code");
                String name = map.get("name");
                String nodeVal;
                if (code != null  && !code.isEmpty()) {
                    if (justCodes) {
                        nodeVal = code;
                    } else {
                        nodeVal = code + ":" + name;
                    }
                } else {
                    nodeVal = name;
                }

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
                if (code != null  && !code.isEmpty()) {
                    if (justCodes) {
                        cascadeString.append(code);
                    } else {
                        cascadeString.append(code + ":" + name);
                    }
                } else {
                    cascadeString.append(name);
                }
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
     * maps. The response can be either:
     *  old format: text1|text2|text3
     *  new format: [{"code":"code1", "text":"text1"},{"code":"code2", "text":"text2"}]
     */
    private List<Map<String, String>> getNodes(String value) {
        boolean isNewFormat = value.startsWith("[");
        List<Map<String, String>> optionNodes = new ArrayList<>();
        FlowJsonObjectReader jsonReader = new FlowJsonObjectReader();
        TypeReference<List<Map<String, String>>> typeReference = new TypeReference<List<Map<String, String>>>() {};

        if (isNewFormat) {
            try {
                optionNodes = jsonReader.readObject(value, typeReference);
            } catch (IOException e) {
                log.log(Level.WARNING, "Could not parse option response: " + value, e);
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
     * Build pipe-separated value from option nodes
     */
    private String buildOptionString(List<Map<String, String>> optionNodes) {
        StringBuilder optionString = new StringBuilder();
        for (Map<String, String> node : optionNodes) {
            String code = node.get("code");
            String text = node.get("text");
            optionString.append("|");
            if (code != null) {
                if (justCodes) {
                    optionString.append(code);
                } else {
                    optionString.append(code + ":" + text);
                }
            } else {
                optionString.append(text);
            }
        }
        if (optionString.length() > 0) {
            // Remove the first "|"
            optionString.deleteCharAt(0);
        }
        return optionString.toString();
    }

    /*
     * Creates list of option values. The value is always the pipe-separated format.
     * Depending on the splitIntoColumns variable, each option is given its own column.
     * A 0 or 1 denotes if that option was selected or not.
     * If the AllowOther flag is true, a column is created for the Other option.
     *  We first try to match on text, and if that fails, we try to match on code. This
     * guards against texts that are slightly changed during the evolution of a survey
     */
    private List<String> optionCellValues(Long questionId, String value,
            List<QuestionOptionDto> options, boolean allowOther, boolean allowMultiple) {
        List<String> cells = new ArrayList<>();
        if (options == null) {
            return cells;
        }

        // get optionNodes from packed string value
        List<Map<String, String>> optionNodes = getNodes(value);
        //'Other' value may or may not be tagged
        //Latest: isOther = true
        //If codes: code = OTHER
        //otherwise: if last and the text is not in option list
        Map<String, String> otherNode = null;
        String other = null;
        int numOptions = options.size();

        boolean[] optionFound = new boolean[numOptions]; //Zero-length array ok

        // if needed, scan options
        if (allowOther || (splitIntoColumns && allowMultiple)) { //Split options into own columns, if multiselect
            boolean found;
            String qId = questionId.toString();

            for (Map<String, String> optAnswer : optionNodes) {
                String text = neverNull(optAnswer.get("text"));
                String code = neverNull(optAnswer.get("code"));
                found = false;

                // try cache first
                String cacheId = qId + text + code;
                if (optionsPositionCache.containsKey(cacheId)) {
                    optionFound[optionsPositionCache.get(cacheId)] = true;
                    found = true;
                }

                // If it is not in the cache, try to match on text
                if (!found) {
                    for (int i = 0; i < numOptions; i++) {
                        if (text != null
                                && text.length() > 0
                                && text.equalsIgnoreCase(options.get(i).getText())) {
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
                                && code.equalsIgnoreCase(options.get(i).getCode())) {
                            optionFound[i] = true;
                            found = true;
                            // put in cache
                            optionsPositionCache.put(cacheId, i);
                            break;
                        }
                    }
                }

                // if still not found, keep this value as other
                //TODO: this guess is unnecessary when an isOther flag (or OTHER code) is present.
                if (!found) {
                    otherNode = optAnswer;
                    other = text;
                }
            }
        }
        if (otherNode != null) {
            optionNodes.remove(otherNode); //goes in it's own cell
        }
        // First build pipe-separated format to start the cell list
        String optionString = buildOptionString(optionNodes);
        cells.add(optionString);

        // if splitting multiples, create cells with 0 or 1
        if (variableNamesInHeaders && allowMultiple) {
            for (int i = 0; i < numOptions; i++) {
                cells.add(optionFound[i] ? "1" : "0");
            }
        }

        if (allowOther) {
            cells.add(other != null ? other : "");
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

        if (questionMap != null) {
            int offset = ++columnIdx;
            for (QuestionGroupDto grpDto : orderedGroupList) {
                if (questionMap.get(grpDto) != null) {
                    // if RQG, do on separate sheet
                    if (qgSheetMap.containsKey(grpDto))   {
                        Sheet groupSheet = qgSheetMap.get(grpDto);
                        int metaEnd = addMetaDataHeaders(groupSheet, true);
                        writeRawDataGroupHeaders(groupSheet, grpDto, questionMap.get(grpDto), metaEnd + 1);
                    } else {
                        // if not, keep adding it on to base sheet and return new offset
                        offset = writeRawDataGroupHeaders(baseSheet, grpDto, questionMap.get(grpDto), offset);
                    }
                }
            }

            // add digest column index; it has no header
            columnIndexMap.put(DIGEST_COLUMN, offset);
        }
    }


    private int addMetaDataHeaders(Sheet sheet, boolean showRepeatColumn) {
        Row row = getRow(doGroupHeaders ? 1 : 0, sheet);
        int columnIdx = -1;
        addMetaDataColumnHeader(IDENTIFIER_LABEL, ++columnIdx, row);
        if (hasDataApproval()) {
            addMetaDataColumnHeader(DATA_APPROVAL_STATUS_LABEL, ++columnIdx, row);
        }
        if (showRepeatColumn) {
            addMetaDataColumnHeader(REPEAT_LABEL, ++columnIdx, row);
        }
        addMetaDataColumnHeader(DISPLAY_NAME_LABEL, ++columnIdx, row);
        addMetaDataColumnHeader(DEVICE_IDENTIFIER_LABEL, ++columnIdx, row);
        addMetaDataColumnHeader(INSTANCE_LABEL, ++columnIdx, row);
        addMetaDataColumnHeader(SUB_DATE_LABEL, ++columnIdx, row);
        addMetaDataColumnHeader(SUBMITTER_LABEL, ++columnIdx, row);
        addMetaDataColumnHeader(DURATION_LABEL, ++columnIdx, row);
        addMetaDataColumnHeader(FORM_VER_LABEL, ++columnIdx, row);
        //Always put something in the top-left corner to identify the format
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


    /**
     * writes the raw data headers for one question group
     * @param sheet
     * @param grpDto
     * @param questions
     * @param startOffset
     * @return
     */
    private int writeRawDataGroupHeaders(Sheet sheet, QuestionGroupDto grpDto, List<QuestionDto> questions, final int startOffset) {
        int offset = startOffset;
        Row row = getRow(doGroupHeaders ? 1 : 0, sheet);

        for (QuestionDto q : questions) {
            questionIdList.add(q.getKeyId().toString());

            String varName = q.getVariableName();
            // Can we tag the column(s) with the variable name?
            final boolean useVarName = variableNamesInHeaders
                    && varName != null
                    && !varName.equals(""); //TODO other whitespace?

            columnIndexMap.put(q.getKeyId().toString(), offset);

            if (QuestionType.GEO == q.getType()) {
                offset = addGeoDataColumnHeaders(q, row, offset, varName,
                        variableNamesInHeaders, useVarName);
            } else if (QuestionType.PHOTO == q.getType()) {
                offset = addPhotoDataColumnHeader(q, row, offset, varName,
                        splitIntoColumns, useVarName);
            } else if (QuestionType.CASCADE == q.getType()
                    && q.getLevelNames() != null && splitIntoColumns) {
                // if no cascade assigned, column is not shown
                for (String level : q.getLevelNames()) {
                    String levelName = useVarName ? varName + "_"
                            + level.replaceAll(" ", "_")
                            : safeQuestionText(q) + " - " + level;
                    createHeaderCell(row, offset++, levelName);
                }
            } else if (QuestionType.CADDISFLY == q.getType()) {
                offset = addCaddisflyDataHeaderColumns(q, row, offset, varName, useVarName);
            } else { // All other cases
                String header = "";
                if (useVarName) {
                    header = varName;
                } else if (variableNamesInHeaders) { //fall back to sanitised text
                    String text = safeQuestionText(q);
                    header = text.replaceAll("\n", "").trim();
                } else {
                    String text = safeQuestionText(q);
                    String replaced = text.replaceAll("\n", "");
                    header = q.getKeyId().toString()
                            + "|"
                            + replaced.trim();
                }

                createHeaderCell(row, offset++, header);

                if (QuestionType.OPTION == q.getType()){
                    offset = addExtraOptionColumnHeaders(header, offset, row, q);
                }
            }

            if (!(QuestionType.NUMBER == q.getType()
                    || QuestionType.OPTION == q.getType()
                    || QuestionType.CASCADE == q.getType()
                    )) {
                unsummarizable.add(q.getKeyId().toString());
            }
        }

        if (doGroupHeaders && questions != null && questions.size() > 1) {
            //Now we know the width; write the group name spanned over entire group
            createCell(getRow(0, sheet), startOffset,
                    "Group " + grpDto.getOrder() + " - " + grpDto.getCode(), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, startOffset, offset - 1));
        }
        return offset;
    }

    private String safeQuestionText(QuestionDto q) {
        String text = q.getText();
        if (text == null) {
            text = "";
        }
        return text;
    }


    @SuppressWarnings("unchecked")
    private int addCaddisflyDataHeaderColumns(QuestionDto q, Row row, final int originalOffset,
            final String variableName, final boolean useVariableName) {
        int offset = originalOffset;
        StringBuilder caddisflyFirstResultColumnHeaderPrefix = new StringBuilder();
        if (useVariableName) {
            caddisflyFirstResultColumnHeaderPrefix.append(variableName);
        } else {
            caddisflyFirstResultColumnHeaderPrefix.append(q.getKeyId());
        }
        caddisflyFirstResultColumnHeaderPrefix.append("|").append(safeQuestionText(q)).append("|");

        if (caddisflyResourceMap == null) { //retrieve this only once
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
                createHeaderCell(row, offset++, columnHeader);
            }

            if (Boolean.TRUE.equals(cr.getHasImage())) {
                createHeaderCell(
                        row,
                        offset++,
                        "--CADDISFLY--|" + safeQuestionText(q)
                                + "--"
                                + IMAGE_LABEL);
            }

            // store hasImage in hashmap
            resultIdMap.put(q.getKeyId(), resultIds);
            hasImageMap.put(q.getKeyId(), Boolean.TRUE.equals(cr.getHasImage()));
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

    private int addPhotoDataColumnHeader(QuestionDto q, Row row, final int originalOffset, String variableName,
            boolean analysisFormat, final boolean useVariableName) {
        int offset = originalOffset;
        // Always a URL column
        String header = "";
        if (useVariableName) {
            header = variableName;
        } else if (analysisFormat) {
            header = safeQuestionText(q).replaceAll("\n", "").trim();
        } else {
            header = q.getKeyId().toString()
                    + "|"
                    + safeQuestionText(q).replaceAll("\n", "").trim();
        }
        createHeaderCell(row, offset++, header);
        if (analysisFormat) {
            // Media gets 3 extra columns: Latitude, Longitude and Accuracy
            String prefix = "--PHOTO--|";
            createHeaderCell(row, offset++, prefix + LAT_LABEL);
            createHeaderCell(row, offset++, prefix + LON_LABEL);
            createHeaderCell(row, offset++, prefix + ACC_LABEL);
        }
        return offset;
    }


    /**
     * @param q
     * @param row
     * @param originalOffset
     * @param variableName
     * @param analysisFormat
     * @param useVariableName
     * @return the new offset
     *
     */
    private int addGeoDataColumnHeaders(QuestionDto q, Row row,
            final int originalOffset,
            final String variableName,
            final boolean analysisFormat,
            final boolean useVariableName) {
        int offset = originalOffset;
        if (analysisFormat) {
            if (useVariableName) {
                createHeaderCell(row, offset++, variableName + "_" + LAT_LABEL);
                createHeaderCell(row, offset++, variableName + "_" + LON_LABEL);
                createHeaderCell(row, offset++, variableName + "_" + ELEV_LABEL);
            } else {
                createHeaderCell(row, offset++, safeQuestionText(q) + " - " + LAT_LABEL);
                createHeaderCell(row, offset++, safeQuestionText(q) + " - " + LON_LABEL);
                createHeaderCell(row, offset++, safeQuestionText(q) + " - " + ELEV_LABEL);
            }
        } else { //Import currently relies on the --GEO header prefix
            createHeaderCell(row, offset++, q.getKeyId() + "|" + LAT_LABEL);
            createHeaderCell(row, offset++, "--GEOLON--|"      + LON_LABEL);
            createHeaderCell(row, offset++, "--GEOELE--|"      + ELEV_LABEL);
        }
        return offset;
    }


    private int addExtraOptionColumnHeaders(String header,
            final int initialOffset,
            Row row,
            QuestionDto q) {
        int offset = initialOffset;
        // get options for question and create columns
        OptionContainerDto ocDto = q.getOptionContainerDto();
        if (ocDto != null) { //null used to be legal if no options
            List<QuestionOptionDto> qoList = ocDto.getOptionsList();
            if (qoList != null) {
                if ( splitIntoColumns & safeTrue(q.getAllowMultipleFlag())) {
                    for (QuestionOptionDto qo : qoList) {
                        // create split header column
                        String hdr = (qo.getCode() != null
                                && !qo.getCode().equals("null")
                                && qo.getCode().length() > 0)
                                ? qo.getCode() + ":"
                                : "";
                        createHeaderCell(row, offset++, "--OPTION--|" + hdr + qo.getText());
                    }
                }

                // add 'other' column if needed
                if (safeTrue(q.getAllowOtherFlag())){
                  createHeaderCell(row, offset++, header + OTHER_SUFFIX);
                }

                optionMap.put(q.getKeyId(), qoList);
                allowOtherMap.put(q.getKeyId(), q.getAllowOtherFlag());
            }
        }
        return offset;
    }


    /**
     * Writes the stats and graphs sheet
     */
    private void writeStatsAndGraphsSheet(
            Map<QuestionGroupDto,
            List<QuestionDto>> questionMap,
            SummaryModel summaryModel,
            String sector,
            Workbook wb)
            throws Exception {

        final int variableNameColumnIndex = 3;
        final int descriptiveStatsColumnIndex = 4;

        String title = sector == null ? SUMMARY_LABEL : sector;
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
        int rowIndex = 0;
        Row row = getRow(rowIndex++, sheet);
        if (sector == null) {
            createCell(row, 0, REPORT_HEADER, headerStyle);
            rowIndex = writeCollectionStats(questionMap, sheet, rowIndex);
        } else {
            createCell(row, 0, sector + " " + REPORT_HEADER, headerStyle);
        }

        for (QuestionGroupDto group : orderedGroupList) {
            if (questionMap.get(group) != null) {
                for (QuestionDto question : questionMap.get(group)) {
                    boolean doDescriptiveStats = false;
                    boolean doChart = false;
                    boolean doDataTable = false;

                    if (QuestionType.OPTION == question.getType()) {
                        doChart = true;
                        doDataTable = true;
                    } else if (QuestionType.CASCADE == question.getType()) {
                        doChart = true;
                        doDataTable = true;
                    } else if (QuestionType.NUMBER == question.getType()) {
                        doDescriptiveStats = true;
                        //skip table and chart (phone numbers etc get ridiculous)
                    } else {
                        continue;
                    }

                    if (summaryModel.getResponseCountsForQuestion(
                            question.getKeyId(), sector).size() == 0) {
                        // if there is no data, skip the question
                        continue;
                    }

                    //We want a header, spanned across 2 columns
                    int tableTopRow = rowIndex++;
                    int bottomRow = rowIndex;

                    row = getRow(tableTopRow, sheet);
                    // Span the question text over any data table
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 2));
                    createCell(
                            row,
                            0,
                            question.getText(),
                            headerStyle);
                    // Variable name
                    createCell(row, variableNameColumnIndex,
                            question.getVariableName(),
                            headerStyle);

                    DescriptiveStats stats = summaryModel.getDescriptiveStatsForQuestion(
                                    question.getKeyId(), sector);
                    if (doDescriptiveStats && stats != null && stats.getSampleCount() > 0) {
                        // span the question text over the stats table
                        sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 4, 5));
                        createCell(
                                row,
                                descriptiveStatsColumnIndex,
                                question.getText(), headerStyle);
                    }

                    // Collect data for use in table, stats and chart
                    Map<String, Long> counts = summaryModel
                            .getResponseCountsForQuestion(question.getKeyId(), sector);
                    int sampleTotal = 0;
                    List<String> labels = new ArrayList<String>();
                    List<String> values = new ArrayList<String>();
                    for (Entry<String, Long> count : counts.entrySet()) {
                        String labelText = count.getKey();
                        if (labelText == null) {
                            labelText = "";
                        } else {
                            // Handle the json option question response type
                            if (labelText.startsWith("[")) {
                                FlowJsonObjectReader jsonReader = new FlowJsonObjectReader();
                                TypeReference<List<Map<String, String>>> typeReference = new TypeReference<List<Map<String, String>>>() {};

                                try {
                                    List<Map<String, String>> optionNodes = jsonReader.readObject(labelText, typeReference);
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
                        labels.add(labelText);
                        values.add(count.getValue().toString());
                        sampleTotal += count.getValue();
                    }

                    //Output the descriptive stats;
                    // this section is short enough to stay within window
                    if (doDescriptiveStats && stats != null && stats.getSampleCount() > 0) {
                        int tempRow = tableTopRow + 1;
                        int c1 = descriptiveStatsColumnIndex;
                        int c2 = c1 + 1;
                        row = getRow(tempRow++, sheet);
                        createCell(row, c1, "N");
                        createCell(row, c2, sampleTotal);
                        row = getRow(tempRow++, sheet);
                        createCell(row, c1, MEAN_LABEL);
                        createCell(row, c2, stats.getMean());
                        row = getRow(tempRow++, sheet);
                        createCell(row, c1, STD_E_LABEL);
                        createCell(row, c2, stats.getStandardError());
                        row = getRow(tempRow++, sheet);
                        createCell(row, c1, MEDIAN_LABEL);
                        createCell(row, c2, stats.getMedian());
                        row = getRow(tempRow++, sheet);
                        createCell(row, c1, STD_D_LABEL);
                        createCell(row, c2, stats.getStandardDeviation());
                        row = getRow(tempRow++, sheet);
                        createCell(row, c1, VAR_LABEL);
                        createCell(row, c2, stats.getVariance());
                        row = getRow(tempRow++, sheet);
                        createCell(row, c1, RANGE_LABEL);
                        createCell(row, c2, stats.getRange());
                        row = getRow(tempRow++, sheet);
                        createCell(row, c1, MIN_LABEL);
                        createCell(row, c2, stats.getMin());
                        row = getRow(tempRow++, sheet);
                        createCell(row, c1, MAX_LABEL);
                        createCell(row, c2, stats.getMax());

                        bottomRow = tempRow;
                    }

                    //bar chart
                    if (doChart && labels.size() > 0) {
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
                                    JFreechartChartUtil.getBarChart(
                                            labels,
                                            values,
                                            question.getText(),
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
                            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
                            patriarch.createPicture(anchor, indx);
                            if (tableTopRow + CHART_CELL_HEIGHT > bottomRow) {
                                bottomRow = tableTopRow + CHART_CELL_HEIGHT;
                            }
                        }
                    }

                    if (doDataTable) {
                        rowIndex = tableTopRow;
                        //header
                        row = getRow(rowIndex++, sheet);
                        createCell(row, 1, FREQ_LABEL, headerStyle);
                        createCell(row, 2, PCT_LABEL, headerStyle);

                        //items
                        for (int i=0; i<labels.size(); i++) {
                            row = getRow(rowIndex++, sheet);
                            createCell(row, 0, labels.get(i));
                            createCell(row, 1, values.get(i));
                            if (sampleTotal > 0) {
                                createCell(row, 2,
                                        PCT_FMT.format((Double.parseDouble(values.get(i))
                                                / sampleTotal)));
                            } else {
                                createCell(row, 2, PCT_FMT.format(0));
                            }
                        }

                        //total
                        row = getRow(rowIndex++, sheet);
                        createCell(row, 0, TOTAL_LABEL);
                        createCell(row, 1, sampleTotal + "");
                        if (rowIndex > bottomRow) {
                            bottomRow = rowIndex;
                        }
                    }
                    rowIndex = bottomRow;

                    // add a blank row between questions
                    getRow(rowIndex++, sheet);
                    // flush the sheet so far to disk; we will not go back up
                    // File will be broken if we write outside the window!
                    ((SXSSFSheet) sheet).flushRows(0); // retain 0 last rows and
                    // flush all others
                }
            }
        }
    }

    /**
     * Write statitics on the collection itself
     * @param questionMap
     * @param sheet
     * @return
     */
    private int writeCollectionStats(Map<QuestionGroupDto,
            List<QuestionDto>> questionMap,
            Sheet sheet,
            int firstRowIndex) {
        final int tagCol = 0;
        final int valCol = 3;

        //Calculate them first (due to the window, we cannot sum this while looping and
        // then go back up and draw it last)
        int totalQuestions = 0;
        for (List<QuestionDto> group : questionMap.values()) {
            totalQuestions += group.size();
        }
        //Now draw them
        int rowIndex = firstRowIndex + 1;
        Row statRow = getRow(rowIndex++, sheet);
        createCell(statRow, tagCol, "Questions");
        createCell(statRow, valCol, totalQuestions);

        statRow = getRow(rowIndex++, sheet);
        createCell(statRow, tagCol, "Form submissions");
        createCell(statRow, valCol, totalInstances);

        if (totalInstances == 0) {
            return rowIndex + 2; //add a little space
        }

        if (safeTrue(surveyGroupDto.getRequireDataApproval())) {
            statRow = getRow(rowIndex++, sheet);
            createCell(statRow, tagCol, "Approved submissions");
            createCell(statRow, valCol, approvedInstances);
        }

        //The following two cells could also be made into date cells
        statRow = getRow(rowIndex++, sheet);
        createCell(statRow, tagCol, "First submission");
        createCell(statRow, valCol, ExportImportUtils.formatDateTime(firstSubmission));

        statRow = getRow(rowIndex++, sheet);
        createCell(statRow, tagCol, "Last submission");
        createCell(statRow, valCol, ExportImportUtils.formatDateTime(lastSubmission));

        statRow = getRow(rowIndex++, sheet);
        createCell(statRow, tagCol, "Shortest duration");
        createCell(statRow, valCol, getDurationText(minDuration));

        statRow = getRow(rowIndex++, sheet);
        createCell(statRow, tagCol, "Longest duration");
        createCell(statRow, valCol, getDurationText(maxDuration));

        statRow = getRow(rowIndex++, sheet);
        createCell(statRow, tagCol, "Average duration");
        try {
            createCell(statRow, valCol, getDurationText(totalDuration / totalInstances));
        } catch (Exception e) {
            // swallow exception, leave cell empty
        }

        for (String user : instancesByUser.keySet()) {
            statRow = getRow(rowIndex++, sheet);
            if (user != null) {
                createCell(statRow, tagCol, "User " + user);
            } else {
                createCell(statRow, tagCol, "No user");
            }
            createCell(statRow, valCol, instancesByUser.get(user));
        }

        for (String device : instancesByDevice.keySet()) {
            statRow = getRow(rowIndex++, sheet);
            createCell(statRow, tagCol, "Device " + device);
            createCell(statRow, valCol, instancesByDevice.get(device));
        }

        return rowIndex + 2; //add a little space
    }


    // Create a string cell
    protected Cell createCell(Row row, int col, String value) {
        return createCell(row, col, value, null, -1);
    }

    // Create a number cell
    protected Cell createCell(Row row, int col, int value) {
        Cell cell = row.createCell(col);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
        return cell;
    }

    // Create a number cell
    protected Cell createCell(Row row, int col, double value) {
        Cell cell = row.createCell(col);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
        return cell;
    }

    // Create a date cell
    //TODO: set date formatting on this cell
    protected Cell createCell(Row row, int col, Date value) {
        Cell cell = row.createCell(col);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(value);
        return cell;
    }

    // Create a header cell
    protected Cell createHeaderCell(Row row, int col, String value) {
        return createCell(row, col, value, headerStyle, -1);
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
        includeSummarySheet = true;
        performGeoRollup = true;
        generateCharts = true;
        separateSheetsForRepeatableGroups = false;
        doGroupHeaders = false;
        variableNamesInHeaders = false;

        if (options != null) {
            //What kind of report?
            reportType = options.get(TYPE_OPT);
            if (reportType == null || reportType.isEmpty()) {
                log.severe(TYPE_OPT + " was not set.");
                return false;
            } else
            if (DATA_CLEANING_TYPE.equalsIgnoreCase(reportType)) {
                includeSummarySheet = false;
                doGroupHeaders = true;
                separateSheetsForRepeatableGroups = true;
                variableNamesInHeaders = false; //So we can import - also enables digests
                splitIntoColumns = false;
                justCodes = false;
            } else if (DATA_ANALYSIS_TYPE.equalsIgnoreCase(reportType)) {
                includeSummarySheet = false;
                doGroupHeaders = false;
                separateSheetsForRepeatableGroups = false;
                variableNamesInHeaders = true;
                splitIntoColumns = true;
                justCodes = true;
            } else if (COMPREHENSIVE_TYPE.equalsIgnoreCase(reportType)) {
                includeSummarySheet = true;
                doGroupHeaders = false;
                separateSheetsForRepeatableGroups = false;
                variableNamesInHeaders = true;
                splitIntoColumns = true;
                justCodes = true;
            } else {
                log.severe("Unknown value " + reportType + " for " + TYPE_OPT);
                return false;
            }

            imagePrefix = options.get(IMAGE_PREFIX_OPT);
            selectionFrom = options.get(FROM_OPT);
            selectionTo = options.get(TO_OPT);
            selectionLimit = options.get(MAX_ROWS_OPT);

            Object lc = options.get(LAST_COLLECTION_OPT);
            if (lc != null
                    && "true".equalsIgnoreCase(lc.toString())) {
                lastCollection = true;
            }

            Object po = options.get(PUBLISHED_OPT);
            if (po != null
                    && "false".equalsIgnoreCase(po.toString())) {
                usePublishedForm = false;
            }

            if (options.get(CADDISFLY_TESTS_FILE_URL_OPT) != null
                    && !options.get(CADDISFLY_TESTS_FILE_URL_OPT).isEmpty()) {
                caddisflyTestsFileUrl = options.get(CADDISFLY_TESTS_FILE_URL_OPT);
            }
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

    /**
     * This main() method is only used for debugging;
     * when deployed on server, export() is called from Clojure code
     * @param args
     * [0] Output filename
     * [1] Flow instance URL, like https://akvoflowsandbox.appspot.com
     * [2] Survey id
     * [3] API key
     * [4] report type, DATA_CLEANING|DATA_ANALYSIS|COMPREHENSIVE
     * [5] usePublishedForm, true or false
     */
    public static void main(String[] args) {
        GraphicalSurveySummaryExporter exporter = new GraphicalSurveySummaryExporter();
        Map<String, String> criteria = new HashMap<String, String>();
        Map<String, String> options = new HashMap<String, String>();
        if (args.length < 5) {
            // Uncomment one of the following three lines
            options.put(TYPE_OPT, DATA_CLEANING_TYPE);
//          options.put(TYPE_OPT, DATA_ANALYSIS_TYPE);
//          options.put(TYPE_OPT, COMPREHENSIVE_TYPE);
        } else {
            options.put(TYPE_OPT, args[4]);
        }
        if (args.length >=6) {
            options.put(PUBLISHED_OPT, args[5]);
        }
        options.put(LAST_COLLECTION_OPT, "false");
        options.put(EMAIL_OPT, "email@example.com");
        options.put(FROM_OPT, null);
        options.put(TO_OPT, null);
        options.put(MAX_ROWS_OPT, null);
        String instance = args[1].replace(".appspot.com", "").replace("https://", "").replace("http://", "");
        options.put(UPLOAD_URL_OPT, String.format("https://%s.s3.amazonaws.com/", instance));
        options.put(UPLOAD_DIR_OPT, "surveys");


        criteria.put(SurveyRestRequest.SURVEY_ID_PARAM, args[2]);
        criteria.put("apiKey", args[3]);

        exporter.export(criteria, new File(args[0]), args[1], options);
    }
}
