/*
 *  Copyright (C) 2010-2016 Stichting Akvo (Akvo Foundation)
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

    private static final int SIZE_THRESHOLD = 2000 * 400;

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
            // Count the number of rows in the sheet
            Iterator<Row> rowIterator = sheet.rowIterator();
            int rowCount = 0;
            while (rowIterator.hasNext()) {
                rowIterator.next();
                rowCount++;
            }
            Thread.sleep(5000);
            log.debug("Updating summaries");
            if (rowCount * questionIdToQuestionDto.size() < SIZE_THRESHOLD) {
                invokeUrl(serverBase, "action=" + RawDataImportRequest.UPDATE_SUMMARIES_ACTION
                        + "&" + RawDataImportRequest.SURVEY_ID_PARAM + "=" + surveyId, true,
                        criteria.get(KEY_PARAM));
            }
            invokeUrl(serverBase, "action=" + RawDataImportRequest.SAVE_MESSAGE_ACTION + "&"
                    + RawDataImportRequest.SURVEY_ID_PARAM + "=" + surveyId, true,
                    criteria.get(KEY_PARAM));

        } catch (Exception e) {
            log.error("Failed to import raw data report", e);
        } finally {
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

        // Find the last empty/null cell in the header row. This is the position of the md5 hashes
        int md5Column = 0;
        for (Cell cell : sheet.getRow(0)) {
            md5Column++;
            if (cell == null || cell.getStringCellValue().equals("")) {
                break;
            }
        }

        // TODO Consider removing this when old (pre repeat question groups) reports no longer need
        // to be supported
        int firstQuestionColumnIndex = Collections.min(columnIndexToQuestionId.keySet());
        boolean hasIterationColumn = firstQuestionColumnIndex == 8;
        boolean hasDeviceIdentifierColumn = firstQuestionColumnIndex == 8
                || firstQuestionColumnIndex == 7;

        int row = 1;
        while (true) {
            InstanceData instanceData = parseInstance(sheet, row, questionIdToQuestionDto,
                    columnIndexToQuestionId, optionNodes, hasIterationColumn,
                    hasDeviceIdentifierColumn);

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

    /**
     * Parse an instance starting from startRow
     *
     * @param sheet
     * @param startRow
     * @param columnIndexToQuestionId
     * @param questionIdToQuestionDto
     * @param optionNodes
     * @return InstanceData
     */
    public InstanceData parseInstance(Sheet sheet, int startRow,
            Map<Long, QuestionDto> questionIdToQuestionDto,
            Map<Integer, Long> columnIndexToQuestionId,
            Map<Long, List<QuestionOptionDto>> optionNodes, boolean hasIterationColumn,
            boolean hasDeviceIdentifierColumn) {

        // File layout
        // 0. SurveyedLocaleIdentifier
        // 1. Repeat (if hasIterationColumn)
        // 2. SurveyedLocaleDisplayName
        // 3. DeviceIdentifier (if hasDeviceIdentifierColumn)
        // 4. SurveyInstanceId
        // 5. CollectionDate
        // 6. SubmitterName
        // 7. SurveyalTime

        // 8 - N. Questions
        // N + 1. Digest

        Row baseRow = sheet.getRow(startRow);
        if (baseRow == null) {
            return null;
        }

        int firstQuestionColumnIndex = Collections.min(columnIndexToQuestionId.keySet());
        String surveyedLocaleIdentifier = ExportImportUtils.parseCellAsString(baseRow.getCell(0));
        String surveyedLocaleDisplayName = ExportImportUtils.parseCellAsString(baseRow
                .getCell(hasIterationColumn ? 2 : 1));
        String deviceIdentifier = "";
        if (hasDeviceIdentifierColumn) {
            deviceIdentifier = ExportImportUtils.parseCellAsString(baseRow
                    .getCell(hasIterationColumn ? 3 : 2));
        }
        String surveyInstanceId = ExportImportUtils.parseCellAsString(baseRow
                .getCell(firstQuestionColumnIndex - 4));
        Date collectionDate = ExportImportUtils.parseDate(ExportImportUtils
                .parseCellAsString(baseRow.getCell(firstQuestionColumnIndex - 3)));
        String submitterName = ExportImportUtils.parseCellAsString(baseRow
                .getCell(firstQuestionColumnIndex - 2));
        String surveyalTime = ExportImportUtils.parseCellAsString(baseRow
                .getCell(firstQuestionColumnIndex - 1));

        int iterations = 1;

        // Count the maximum number of iterations for this instance
        if (hasIterationColumn) {
            while (true) {
                Row row = sheet.getRow(startRow + iterations);
                if (row == null
                        || ExportImportUtils.parseCellAsString(row.getCell(1)).equals("1")) {
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
            QuestionType questionType = questionDto.getQuestionType();

            for (int iter = 0; iter < iterations; iter++) {

                Row iterationRow = sheet.getRow(startRow + iter);

                long iteration = 1;
                if (hasIterationColumn) {
                    Cell cell = iterationRow.getCell(1);
                    if (cell != null) {
                        iteration = (long) iterationRow.getCell(1).getNumericCellValue();
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
                            Date date = ExportImportUtils.parseDate(dateString);
                            if (date != null) {
                                val = String.valueOf(date.getTime());
                            } else {
                                log.warn("Could not parse date string: " + dateString);
                            }
                            break;

                        case SIGNATURE:
                            // we do not allow importing / overwriting signature question responses
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
            if (cell.getStringCellValue().indexOf("|") > -1 && !cellValue.startsWith("--GEO")) {
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
            if (QuestionType.OPTION.equals(question.getQuestionType())) {
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
        String dateString = ExportImportUtils.formatDate(dto.getCollectionDate());

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
                switch (questionDto.getQuestionType()) {
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
            boolean hasIterationColumn = false;

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
                    int idx = cell.getColumnIndex();
                    // idx == 6, monitoring, old format
                    // idx == 7, new format
                    // idx == 8, new format, with repeat column
                    if (!(idx == 6 || idx == 7 || idx == 8)) {
                        errorMap.put(idx, "Found the first question at the wrong column index");
                        break;
                    }
                    if (idx == 8) {
                        hasIterationColumn = true;
                    }

                }
            }

            if (!firstQuestionFound) {
                errorMap.put(-1, "A question could not be found");
            }

            if (hasIterationColumn) {
                Iterator<Row> iter = sheet.iterator();
                iter.next(); // Skip the header row.
                while (iter.hasNext()) {
                    Row row = iter.next();
                    Cell cell = row.getCell(1);
                    if (cell == null) {
                        errorMap.put(-1, "Repeat column is empty");
                        break;
                    }
                    if (cell.getCellType() != Cell.CELL_TYPE_NUMERIC) {
                        errorMap.put(-1, "Repeat column must contain a numeric value");
                        break;
                    }
                }
            }

        } catch (Exception e) {
            errorMap.put(-1, e.getMessage());
        }

        return errorMap;
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
            log.error("Error.\nUsage:\n\tjava org.waterforpeople.mapping.dataexport.RawDataSpreadsheetImporter <file> <serverBase> <surveyId>");
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
