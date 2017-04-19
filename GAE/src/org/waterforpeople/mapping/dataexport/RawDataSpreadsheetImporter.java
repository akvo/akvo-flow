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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.codehaus.jackson.map.ObjectMapper;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.RawDataImportRequest;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.framework.dataexport.applet.DataImporter;

public class RawDataSpreadsheetImporter implements DataImporter {

    private static final Logger log = Logger.getLogger(RawDataSpreadsheetImporter.class);

    private static final String SERVLET_URL = "/rawdatarestapi";
    public static final String SURVEY_CONFIG_KEY = "surveyId";
    protected static final String KEY_PARAM = "apiKey";
    private InputStream stream;
    private ThreadPoolExecutor threadPool;
    private BlockingQueue<Runnable> jobQueue;
    private List<String> errorIds;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final int LEGACY_MONITORING_FORMAT = 6;
    private static final int MONITORING_FORMAT_WITH_DEVICE_ID_COLUMN = 7;
    private static final int MONITORING_FORMAT_WITH_REPEAT_COLUMN = 8;
    private static final int MONITORING_FORMAT_WITH_APPROVAL_COLUMN = 9;

    public static final String DATAPOINT_IDENTIFIER_COLUMN_KEY = "dataPointIdentifier";
    private static final String DATAPOINT_APPROVAL_COLUMN_KEY = "dataPointApproval";
    public static final String REPEAT_COLUMN_KEY = "repeat";
    public static final String DATAPOINT_NAME_COLUMN_KEY = "dataPointDisplayName";
    public static final String DEVICE_IDENTIFIER_COLUMN_KEY = "deviceIdentifier";
    public static final String SURVEY_INSTANCE_COLUMN_KEY = "surveyInstanceId";
    public static final String COLLECTION_DATE_COLUMN_KEY = "collectionDate";
    public static final String SUBMITTER_COLUMN_KEY = "submitterName";
    public static final String DURATION_COLUMN_KEY = "surveyalTime";

    /**
     * opens a file input stream using the file passed in and tries to return the first worksheet in
     * that file
     *
     * @param file
     * @return
     * @throws Exception
     */
    public Sheet getDataSheet(File file) throws Exception {
        stream = new PushbackInputStream(new FileInputStream(file));
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(stream);
        } catch (Exception e) {
        }
        return wb.getSheetAt(0);
    }

    /**
     * closes open input streams
     */
    protected void cleanup() {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void executeImport(File file, String serverBase, Map<String, String> criteria) {
        try {
            log.info(String.format("Importing %s to %s using criteria %s", file, serverBase,
                    criteria));
            Sheet sheet = getDataSheet(file);
            String surveyId = criteria.get("surveyId");
            Map<Integer, Long> columnIndexToQuestionId = processHeader(sheet);
            Map<Long, QuestionDto> questionIdToQuestionDto = fetchQuestions(serverBase, criteria);

            Map<Long, List<QuestionOptionDto>> optionNodes = fetchOptionNodes(serverBase,
                    criteria, questionIdToQuestionDto.values());

            List<InstanceData> instanceDataList = parseSheet(sheet, questionIdToQuestionDto,
                    columnIndexToQuestionId, optionNodes);

            List<String> importUrls = new ArrayList<>();

            for (InstanceData instanceData : instanceDataList) {
                String importUrl = buildImportURL(instanceData, surveyId,
                        questionIdToQuestionDto);
                importUrls.add(importUrl);
            }

            log.info(String.format("Attempting to upload %s form instances to %s",
                    importUrls.size(), serverBase));

            // Send updated instances to GAE
            errorIds = new ArrayList<String>();
            jobQueue = new LinkedBlockingQueue<Runnable>();
            threadPool = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, jobQueue);
            for (String importUrl : importUrls) {
                sendDataToServer(serverBase, importUrl, criteria.get(KEY_PARAM));
            }
            while (!jobQueue.isEmpty() && threadPool.getActiveCount() > 0) {
                Thread.sleep(5000);
            }
            if (errorIds.size() > 0) {
                log.error("There were ERRORS: ");
                for (String line : errorIds) {
                    log.error(line);
                }
            }

            Thread.sleep(5000);
            log.debug("Updating summaries");
            invokeUrl(serverBase, "action=" + RawDataImportRequest.UPDATE_SUMMARIES_ACTION
                    + "&" + RawDataImportRequest.SURVEY_ID_PARAM + "=" + surveyId, true,
                    criteria.get(KEY_PARAM));
            invokeUrl(serverBase, "action=" + RawDataImportRequest.SAVE_MESSAGE_ACTION + "&"
                    + RawDataImportRequest.SURVEY_ID_PARAM + "=" + surveyId, true,
                    criteria.get(KEY_PARAM));

        } catch (Exception e) {
            log.error("Failed to import raw data report", e);
        } finally {
            if (threadPool != null)
                threadPool.shutdown();
            cleanup();
        }

    }

    /**
     * Parse a raw data report file into a list of InstanceData
     *
     * @param sheet
     * @param columnIndexToQuestionId
     * @param questionIdToQuestionDto
     * @param optionNodes
     * @return
     */
    public List<InstanceData> parseSheet(Sheet sheet,
            Map<Long, QuestionDto> questionIdToQuestionDto,
            Map<Integer, Long> columnIndexToQuestionId,
            Map<Long, List<QuestionOptionDto>> optionNodes)
            throws Exception {

        List<InstanceData> result = new ArrayList<>();

        // Find the first empty/null cell in the header row. This is the position of the md5 hashes
        int md5Column = 0;
        while (true) {
            if (isEmptyCell(sheet.getRow(0).getCell(md5Column))) {
                break;
            }
            md5Column++;
        }

        int firstQuestionColumnIndex = Collections.min(columnIndexToQuestionId.keySet());
        Map<String, Integer> metadataColumnHeaderIndex = calculateMetadataColumnIndex(firstQuestionColumnIndex);

        int row = 1;
        while (true) {
            InstanceData instanceData = parseInstance(sheet, row, metadataColumnHeaderIndex,
                    firstQuestionColumnIndex, questionIdToQuestionDto, columnIndexToQuestionId,
                    optionNodes);

            if (instanceData == null) {
                break;
            }

            // Get all the parsed rows for md5 calculation
            List<Row> rows = new ArrayList<>();
            for (int r = row; r < row + instanceData.maxIterationsCount; r++) {
                rows.add(sheet.getRow(r));
            }

            String existingMd5Hash = "";
            Cell md5Cell = sheet.getRow(row).getCell(md5Column);
            // For new data the md5 hash column could be empty
            if (md5Cell != null) {
                existingMd5Hash = md5Cell.getStringCellValue();
            }
            String newMd5Hash = ExportImportUtils.md5Digest(rows, md5Column - 1);

            if (!newMd5Hash.equals(existingMd5Hash)) {
                result.add(instanceData);
            }

            row += instanceData.maxIterationsCount;
        }

        return result;
    }

    private static Map<String, Integer> calculateMetadataColumnIndex(int firstQuestionColumnIndex) {
        Map<String, Integer> metadataColumnIndex = new HashMap<>();

        int currentColumnIndex = -1;

        metadataColumnIndex.put(DATAPOINT_IDENTIFIER_COLUMN_KEY, ++currentColumnIndex);

        if (hasApprovalColumn(firstQuestionColumnIndex)) {
            metadataColumnIndex.put(DATAPOINT_APPROVAL_COLUMN_KEY, ++currentColumnIndex);
        }

        if (hasRepeatIterationColumn(firstQuestionColumnIndex)) {
            metadataColumnIndex.put(REPEAT_COLUMN_KEY, ++currentColumnIndex);
        }

        metadataColumnIndex.put(DATAPOINT_NAME_COLUMN_KEY, ++currentColumnIndex);

        if (hasDeviceIdentifierColumn(firstQuestionColumnIndex)) {
            metadataColumnIndex.put(DEVICE_IDENTIFIER_COLUMN_KEY, ++currentColumnIndex);
        }
        metadataColumnIndex.put(SURVEY_INSTANCE_COLUMN_KEY, ++currentColumnIndex);
        metadataColumnIndex.put(COLLECTION_DATE_COLUMN_KEY, ++currentColumnIndex);
        metadataColumnIndex.put(SUBMITTER_COLUMN_KEY, ++currentColumnIndex);
        metadataColumnIndex.put(DURATION_COLUMN_KEY, ++currentColumnIndex);

        return metadataColumnIndex;
    }

    private static boolean hasApprovalColumn(int firstQuestionColumnIndex) {
        return firstQuestionColumnIndex == MONITORING_FORMAT_WITH_APPROVAL_COLUMN;
    }

    private static boolean hasRepeatIterationColumn(int firstQuestionColumnIndex) {
        return hasApprovalColumn(firstQuestionColumnIndex)
                || firstQuestionColumnIndex == MONITORING_FORMAT_WITH_REPEAT_COLUMN;
    }

    private static boolean hasDeviceIdentifierColumn(int firstQuestionColumnIndex) {
        return hasRepeatIterationColumn(firstQuestionColumnIndex)
                || firstQuestionColumnIndex == MONITORING_FORMAT_WITH_DEVICE_ID_COLUMN;
    }

    /**
     * Parse an instance starting from startRow
     *
     * @param sheet
     * @param startRow
     * @param firstQuestionColumnIndex
     * @param questionIdToQuestionDto
     * @param columnIndexToQuestionId
     * @param optionNodes
     * @return InstanceData
     */
    public InstanceData parseInstance(Sheet sheet, int startRow,
            Map<String, Integer> metadataColumnHeaderIndex,
            int firstQuestionColumnIndex,
            Map<Long, QuestionDto> questionIdToQuestionDto,
            Map<Integer, Long> columnIndexToQuestionId,
            Map<Long, List<QuestionOptionDto>> optionNodes) {

        // File layout
        // 0. SurveyedLocaleIdentifier
        // 1. Repeat (if hasIterationColumn)
        // 2. SurveyedLocaleDisplayName
        // 3. DeviceIdentifier (if hasDeviceIdentifierColumn)
        // 4. SurveyInstanceId
        // 5. CollectionDate
        // 6. SubmitterName
        // 7. SurveyalTime

        // 9 - N. Questions
        // N + 1. Digest

        // First check if we are done with the sheet
        Row baseRow = sheet.getRow(startRow);
        if (isEmptyRow(baseRow)) { // a row without any cells defined
            return null;
        }

        String surveyedLocaleIdentifier = getMetadataCellContent(baseRow,
                metadataColumnHeaderIndex, DATAPOINT_IDENTIFIER_COLUMN_KEY);
        String surveyedLocaleDisplayName = getMetadataCellContent(baseRow,
                metadataColumnHeaderIndex, DATAPOINT_NAME_COLUMN_KEY);

        String deviceIdentifier = "";
        if (hasDeviceIdentifierColumn(firstQuestionColumnIndex)) {
            deviceIdentifier = getMetadataCellContent(baseRow, metadataColumnHeaderIndex,
                    DEVICE_IDENTIFIER_COLUMN_KEY);
        }

        String surveyInstanceId = getMetadataCellContent(baseRow, metadataColumnHeaderIndex,
                SURVEY_INSTANCE_COLUMN_KEY);
        Date collectionDate = ExportImportUtils.parseSpreadsheetDate(getMetadataCellContent(
                baseRow, metadataColumnHeaderIndex, COLLECTION_DATE_COLUMN_KEY));
        String submitterName = getMetadataCellContent(baseRow, metadataColumnHeaderIndex,
                SUBMITTER_COLUMN_KEY);
        String surveyalTime = getMetadataCellContent(baseRow, metadataColumnHeaderIndex,
                DURATION_COLUMN_KEY);

        int iterations = 1;
        int repeatIterationColumnIndex = metadataColumnHeaderIndex.get(REPEAT_COLUMN_KEY);

        // Count the maximum number of iterations for this instance
        if (hasRepeatIterationColumn(firstQuestionColumnIndex)) {
            while (true) {
                Row row = sheet.getRow(startRow + iterations);
                if (row == null // no row
                        || isEmptyCell(row.getCell(repeatIterationColumnIndex))
                        || ExportImportUtils.parseCellAsString(
                                row.getCell(repeatIterationColumnIndex)).equals("1") // next q
                ) {
                    break;
                }
                iterations++;
            }
        }

        // question-id -> iteration -> response
        Map<Long, Map<Long, String>> responseMap = new HashMap<>();

        for (Entry<Integer, Long> m : columnIndexToQuestionId.entrySet()) {
            int columnIndex = m.getKey();
            long questionId = m.getValue();

            QuestionDto questionDto = questionIdToQuestionDto.get(questionId);
            QuestionType questionType = questionDto.getType();

            for (int iter = 0; iter < iterations; iter++) {

                Row iterationRow = sheet.getRow(startRow + iter);

                long iteration = 1;
                if (hasRepeatIterationColumn(firstQuestionColumnIndex)) {
                    Cell cell = iterationRow.getCell(repeatIterationColumnIndex);
                    if (cell != null) {
                        iteration = (long) iterationRow.getCell(repeatIterationColumnIndex)
                                .getNumericCellValue();
                    }
                }
                String val = "";

                Cell cell = iterationRow.getCell(columnIndex);

                if (cell != null) {
                    switch (questionType) {
                        case GEO:
                            String latitude = ExportImportUtils.parseCellAsString(cell);
                            String longitude = ExportImportUtils.parseCellAsString(iterationRow
                                    .getCell(columnIndex + 1));
                            String elevation = ExportImportUtils.parseCellAsString(iterationRow
                                    .getCell(columnIndex + 2));
                            String geoCode = ExportImportUtils.parseCellAsString(iterationRow
                                    .getCell(columnIndex + 3));
                            val = latitude + "|" + longitude + "|" + elevation + "|" + geoCode;
                            break;
                        case CASCADE:
                            // Two different possible formats:
                            // With codes: code1:val1|code2:val2|...
                            // Without codes: val1|val2|...
                            String cascadeString = ExportImportUtils.parseCellAsString(cell);
                            String[] cascadeParts = cascadeString.split("\\|");
                            List<Map<String, String>> cascadeList = new ArrayList<>();
                            for (String cascadeNode : cascadeParts) {
                                String[] codeAndName = cascadeNode.split(":");
                                Map<String, String> cascadeMap = new HashMap<>();
                                if (codeAndName.length == 1) {
                                    cascadeMap.put("name", codeAndName[0]);

                                } else if (codeAndName.length == 2) {
                                    cascadeMap.put("code", codeAndName[0]);
                                    cascadeMap.put("name", codeAndName[1]);
                                } else {
                                    log.warn("Invalid cascade node: " + cascadeNode);
                                }
                                cascadeList.add(cascadeMap);
                            }
                            try {
                                val = OBJECT_MAPPER.writeValueAsString(cascadeList);
                            } catch (IOException e) {
                                log.warn("Could not parse cascade string: " + cascadeString);
                            }
                            break;

                        case OPTION:
                            // Two different possible formats:
                            // With codes: code1:val1|code2:val2|...
                            // Without codes: val1|val2|...
                            String optionString = ExportImportUtils.parseCellAsString(cell);
                            if (optionString.isEmpty()) {
                                break;
                            }
                            String[] optionParts = optionString.split("\\|");
                            List<Map<String, Object>> optionList = new ArrayList<>();
                            for (String optionNode : optionParts) {
                                String[] codeAndText = optionNode.split(":", 2);
                                Map<String, Object> optionMap = new HashMap<>();
                                if (codeAndText.length == 1) {
                                    optionMap.put("text", codeAndText[0].trim());

                                } else if (codeAndText.length == 2) {
                                    optionMap.put("code", codeAndText[0].trim());
                                    optionMap.put("text", codeAndText[1].trim());
                                }
                                optionList.add(optionMap);
                            }

                            // Should we add the 'allowOther' flag to the last node?
                            if (Boolean.TRUE.equals(questionDto.getAllowOtherFlag())
                                    && !optionList.isEmpty()) {
                                Map<String, Object> lastNode = optionList
                                        .get(optionList.size() - 1);
                                String lastNodeText = (String) lastNode.get("text");
                                boolean isOther = true;
                                List<QuestionOptionDto> existingOptions = optionNodes
                                        .get(questionId);
                                if (existingOptions != null && lastNodeText != null) {
                                    for (QuestionOptionDto questionOptionDto : existingOptions) {
                                        if (lastNodeText.equals(questionOptionDto.getText())) {
                                            isOther = false;
                                            break;
                                        }
                                    }
                                }

                                if (isOther) {
                                    lastNode.put("isOther", true);
                                }
                            }

                            try {
                                if (!optionList.isEmpty()) {
                                    val = OBJECT_MAPPER.writeValueAsString(optionList);
                                }
                            } catch (IOException e) {
                                log.warn("Could not parse option string: " + optionString, e);
                            }

                            break;

                        case DATE:
                            String dateString = ExportImportUtils.parseCellAsString(cell);
                            Date date = ExportImportUtils.parseSpreadsheetDate(dateString);
                            if (date != null) {
                                val = String.valueOf(date.getTime());
                            } else {
                                log.warn("Could not parse date string: " + dateString);
                            }
                            break;

                        case SIGNATURE:
                            // we do not allow importing / overwriting of signature question
                            // responses
                            val = null;
                            break;

                        case CADDISFLY:
                            // we do not allow importing / overwriting Caddisfly question responses
                            val = null;
                            break;

                        default:
                            val = ExportImportUtils.parseCellAsString(cell);
                            break;
                    }

                    if (val != null && !val.equals("")) {
                        // Update response map
                        // iteration -> response
                        Map<Long, String> iterationToResponse = responseMap.get(questionId);

                        if (iterationToResponse == null) {
                            iterationToResponse = new HashMap<>();
                            iterationToResponse.put(iteration - 1, val);
                            responseMap.put(questionId, iterationToResponse);
                        } else {
                            iterationToResponse.put(iteration - 1, val);
                        }
                    }
                }
            }
        }

        SurveyInstanceDto surveyInstanceDto = new SurveyInstanceDto();
        surveyInstanceDto.setSurveyedLocaleIdentifier(surveyedLocaleIdentifier);
        surveyInstanceDto.setSurveyedLocaleDisplayName(surveyedLocaleDisplayName);
        surveyInstanceDto.setDeviceIdentifier(deviceIdentifier);
        if (!surveyInstanceId.equals("")) {
            surveyInstanceDto.setKeyId(Long.parseLong(surveyInstanceId));
        }
        surveyInstanceDto.setCollectionDate(collectionDate);
        surveyInstanceDto.setSubmitterName(submitterName);
        surveyInstanceDto.setSurveyalTime((long) durationToSeconds(surveyalTime));

        InstanceData instanceData = new InstanceData(surveyInstanceDto, responseMap);
        instanceData.maxIterationsCount = iterations;
        return instanceData;
    }

    private static String getMetadataCellContent(Row baseRow,
            Map<String, Integer> metadataColumnHeaderIndex, String metadataCellColumnKey) {
        Cell metadataCell = baseRow.getCell(metadataColumnHeaderIndex.get(metadataCellColumnKey));
        return ExportImportUtils.parseCellAsString(metadataCell);
    }

    /**
     * Return a map of column index -> question id
     *
     * @param sheet
     * @return A map from column index to question id.
     */
    private static Map<Integer, Long> processHeader(Sheet sheet) {
        Map<Integer, Long> columnIndexToQuestionId = new HashMap<>();
        Row headerRow = sheet.getRow(0);

        for (Cell cell : headerRow) {
            String cellValue = cell.getStringCellValue();
            if (cell.getStringCellValue().indexOf("|") > -1 && !cellValue.startsWith("--GEO")
                    && !cellValue.startsWith("--CADDISFLY")) {
                String[] parts = cell.getStringCellValue().split("\\|");
                if (parts[0].trim().length() > 0) {
                    columnIndexToQuestionId.put(cell.getColumnIndex(),
                            Long.valueOf(parts[0].trim()));
                }
            }
        }

        return columnIndexToQuestionId;
    }

    /**
     * @return map from question id to QuestionDto
     */
    @SuppressWarnings("unchecked")
    private static Map<Long, QuestionDto> fetchQuestions(String serverBase,
            Map<String, String> criteria) throws Exception {

        String surveyId = criteria.get("surveyId");
        String apiKey = criteria.get("apiKey");

        Object[] results = BulkDataServiceClient.loadQuestions(surveyId, serverBase, apiKey);

        if (results == null) {
            // TODO proper error reporting
            throw new Exception("Could not fetch questions");
        }
        Map<Long, QuestionDto> questionMap = new HashMap<>();
        for (Entry<String, QuestionDto> entry : ((Map<String, QuestionDto>) results[1]).entrySet()) {
            questionMap.put(Long.valueOf(entry.getKey()), entry.getValue());
        }
        return questionMap;
    }

    /**
     * Fetch option nodes for each option question
     *
     * @param serverBase
     * @param criteria
     * @param questions
     * @return A mapping from question id to list of option texts
     */
    private static Map<Long, List<QuestionOptionDto>> fetchOptionNodes(String serverBase,
            Map<String, String> criteria, Collection<QuestionDto> questions) throws Exception {
        String surveyId =
                criteria.get("surveyId");
        String apiKey = criteria.get("apiKey");
        List<Long> optionQuestionIds = new ArrayList<>();
        for (QuestionDto question : questions) {
            if (QuestionType.OPTION.equals(question.getType())) {
                optionQuestionIds.add(question.getKeyId());
            }
        }
        return BulkDataServiceClient.fetchOptionNodes(surveyId, serverBase, apiKey,
                optionQuestionIds);
    }

    private static String buildImportURL(InstanceData instanceData, String surveyId,
            Map<Long, QuestionDto> questionIdToQuestionDto)
            throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();
        SurveyInstanceDto dto = instanceData.surveyInstanceDto;

        sb.append("action="
                + RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION
                + "&" + RawDataImportRequest.SURVEY_ID_PARAM + "="
                + surveyId + "&");

        // Instance id
        if (dto.getKeyId() != null) {
            sb.append(RawDataImportRequest.SURVEY_INSTANCE_ID_PARAM + "="
                    + dto.getKeyId() + "&");
        }

        // Collection date
        String dateString = ExportImportUtils.formatDateTime(dto.getCollectionDate());

        sb.append(
                RawDataImportRequest.COLLECTION_DATE_PARAM + "="
                        + URLEncoder.encode(dateString,
                                "UTF-8") + "&");

        // Submitter
        sb.append("submitter=" + URLEncoder.encode(dto.getSubmitterName(), "UTF-8") + "&");

        // Duration
        sb.append("duration=" + dto.getSurveyalTime());

        // questionId=123|0=sfijd|2=fjsoi|type=GEO&questionId=...
        for (Entry<Long, SortedMap<Long, String>> entry : instanceData.responseMap
                .entrySet()) {
            Long questionId = entry.getKey();
            sb.append("&questionId=" + questionId);
            SortedMap<Long, String> iterations = entry.getValue();

            StringBuilder responseBuilder = new StringBuilder();

            for (Entry<Long, String> iterationEntry : iterations.entrySet()) {
                Long iteration = iterationEntry.getKey();
                String response = iterationEntry.getValue();
                // URL encode the response text a second time in order to escape pipe characters
                responseBuilder
                        .append("|" + iteration + "=" + URLEncoder.encode(response, "UTF-8"));
            }

            sb.append(URLEncoder.encode(responseBuilder.toString(), "UTF-8"));

            String typeString = "VALUE";
            QuestionDto questionDto = questionIdToQuestionDto.get(questionId);
            if (questionDto != null) {
                switch (questionDto.getType()) {
                    case GEO:
                        typeString = "GEO";
                        break;
                    case PHOTO:
                        typeString = "IMAGE";
                        break;
                    case VIDEO:
                        typeString = "VIDEO";
                        break;
                    case DATE:
                        typeString = "DATE";
                        break;
                    case CASCADE:
                        typeString = "CASCADE";
                        break;
                    case OPTION:
                        typeString = "OPTION";
                        break;
                    default:
                        break;
                }
            }

            sb.append(URLEncoder.encode("|type=" + typeString, "UTF-8"));

        }

        return sb.toString();
    }

    private Integer durationToSeconds(String duration) {
        if (duration == null || duration.length() == 0)
            return 0;

        // try to parse as integer
        if (!duration.contains(":")) {
            try {
                int seconds = Integer.parseInt(duration);
                return seconds;
            } catch (Exception e) {
                return 0;
            }
        }

        // try do parse as hh:mm:ss
        String[] tokens = duration.split(":");
        if (tokens.length != 3)
            return 0;
        try {
            int hours = Integer.parseInt(tokens[0]);
            int minutes = Integer.parseInt(tokens[1]);
            int seconds = Integer.parseInt(tokens[2]);
            return 3600 * hours + 60 * minutes + seconds;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @param serverBase
     * @param resetUrlString
     * @param saveUrlString
     */
    private void sendDataToServer(final String serverBase, final String saveUrlString,
            final String key) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    invokeUrl(serverBase, saveUrlString, true, key);
                } catch (Exception e) {
                    errorIds.add(saveUrlString);
                    System.err.println("Could not invoke rest services: " + e);
                    e.printStackTrace(System.err);
                }
            }
        });
    }

    /**
     * calls a remote api by posting to the url passed in.
     *
     * @param serverBase
     * @param urlString
     * @throws Exception
     */
    protected void invokeUrl(String serverBase, String urlString,
            boolean shouldSign, String key) throws Exception {

        BulkDataServiceClient.fetchDataFromServer(serverBase + SERVLET_URL,
                urlString, shouldSign, key);
    }

    @Override
    public Map<Integer, String> validate(File file) {
        Map<Integer, String> errorMap = new HashMap<Integer, String>();

        try {
            Sheet sheet = getDataSheet(file);
            Row headerRow = sheet.getRow(0);
            boolean firstQuestionFound = false;
            int firstQuestionColumnIndex = 0;

            for (Cell cell : headerRow) {
                String cellValue = cell.getStringCellValue();
                // if encountering a null cell make sure its only due to phantom cells at the end of
                // the row. If null or empty cell occurs in middle of header row report an error
                if ((cellValue == null || cellValue.trim().isEmpty()) && isMissingHeaderCell(cell)) {
                    errorMap.put(
                            cell.getColumnIndex(),
                            String.format(
                                    "Cannot import data from Column %s - \"%s\". Please check and/or fix the header cell",
                                    CellReference.convertNumToColString(cell.getColumnIndex()),
                                    cellValue));

                    break;
                }

                if (!firstQuestionFound && cellValue.matches("[0-9]+\\|.+")) {
                    firstQuestionFound = true;
                    firstQuestionColumnIndex = cell.getColumnIndex();
                    if (!isSupportedReportFormat(firstQuestionColumnIndex)) {
                        errorMap.put(firstQuestionColumnIndex,
                                "Found the first question at the wrong column index");
                        break;
                    }
                    log.info("Importing report with first question column index: "
                            + firstQuestionColumnIndex);
                }
            }

            if (!firstQuestionFound) {
                errorMap.put(-1, "A question could not be found");
            }

            if (firstQuestionFound && hasRepeatIterationColumn(firstQuestionColumnIndex)) {
                Iterator<Row> iter = sheet.iterator();
                iter.next(); // Skip the header row.

                int repeatIterationColumnIndex = -1;
                if (hasApprovalColumn(firstQuestionColumnIndex)) {
                    repeatIterationColumnIndex = 2;
                } else {
                    repeatIterationColumnIndex = 1;
                }

                while (iter.hasNext()) { // gets "phantom" rows, too
                    Row row = iter.next();
                    if (isEmptyRow(row)) {
                        break; // phantom row - just stop
                    }
                    Cell cell = row.getCell(repeatIterationColumnIndex);
                    if (cell == null) {
                        // include 1-based row number in error log
                        errorMap.put(-1, "Repeat column is empty in row: " + row.getRowNum() + 1);
                        break;
                    }
                    if (cell.getCellType() != Cell.CELL_TYPE_NUMERIC) {
                        errorMap.put(-1, "Repeat column must contain a numeric value in row: "
                                + row.getRowNum() + 1);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            errorMap.put(-1, e.getMessage());
        }

        return errorMap;
    }

    private boolean isSupportedReportFormat(int firstQuestionColumnIndex) {
        return firstQuestionColumnIndex == LEGACY_MONITORING_FORMAT
                || firstQuestionColumnIndex == MONITORING_FORMAT_WITH_DEVICE_ID_COLUMN
                || firstQuestionColumnIndex == MONITORING_FORMAT_WITH_REPEAT_COLUMN
                || firstQuestionColumnIndex == MONITORING_FORMAT_WITH_APPROVAL_COLUMN;
    }

    /**
     * Check if a cell is any kind of empty
     *
     * @param cell
     * @return
     */
    private boolean isEmptyCell(Cell cell) {
        return cell == null
                || cell.getCellType() == Cell.CELL_TYPE_BLANK
                || (cell.getCellType() == Cell.CELL_TYPE_STRING
                && cell.getStringCellValue().trim().equals(""));
    }

    /**
     * Check if a row is any kind of empty
     *
     * @param row
     * @return
     */

    private boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getFirstCellNum() == -1) { // a row without any cells defined
            return true; // phantom row
        }
        // maybe cells are all blank/contain only spaces?
        boolean blank = true;
        for (int ix = row.getFirstCellNum(); ix < row.getLastCellNum(); ix++) {
            if (!isEmptyCell(row.getCell(ix))) {
                blank = false;
                break;
            }
        }
        if (blank) {
            return true;
        }
        return false;
    }

    /**
     * When a blank or null cell is incurred while processing, make sure that this is the last cell
     * in the row and ignore any other "phantom cells" that may occur. We only allow this for the
     * header row. If the blank cell occurs in between valid header cells we return true.
     *
     * @param cell
     * @return
     */
    private boolean isMissingHeaderCell(Cell cell) {
        assert cell.getRow().getRowNum() == 0; // only process header rows

        Row row = cell.getRow();
        for (int i = cell.getColumnIndex(); i < row.getLastCellNum(); i++) {
            if (row.getCell(i) != null && !row.getCell(i).getStringCellValue().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            log.error("Error.\nUsage:\n\tjava org.waterforpeople.mapping.dataexport.RawDataSpreadsheetImporter <file> <serverBase> <surveyId> <apiKey>");
            System.exit(1);
        }
        File file = new File(args[0].trim());
        String serverBaseArg = args[1].trim();
        RawDataSpreadsheetImporter r = new RawDataSpreadsheetImporter();
        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put(SURVEY_CONFIG_KEY, args[2].trim());
        configMap.put("apiKey", args[3].trim());

        r.executeImport(file, serverBaseArg, configMap);
    }
}
