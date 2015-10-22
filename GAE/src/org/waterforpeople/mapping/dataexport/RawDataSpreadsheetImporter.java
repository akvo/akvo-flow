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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
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
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.RawDataImportRequest;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.common.util.StringUtil;
import com.gallatinsystems.framework.dataexport.applet.DataImporter;

public class RawDataSpreadsheetImporter implements DataImporter {

    private static final Logger log = Logger.getLogger(RawDataSpreadsheetImporter.class);

    private static final String SERVLET_URL = "/rawdatarestapi";
    private static final String DEFAULT_LOCALE = "en";
    public static final String SURVEY_CONFIG_KEY = "surveyId";
    protected static final String KEY_PARAM = "apiKey";
    private static final Map<String, String> SAVING_DATA;
    private static final Map<String, String> COMPLETE;
    private Long surveyId;
    private InputStream stream;
    private String locale = DEFAULT_LOCALE;
    private ThreadPoolExecutor threadPool;
    private BlockingQueue<Runnable> jobQueue;
    private List<String> errorIds;

    private static final int SIZE_THRESHOLD = 2000 * 400;

    static {
        SAVING_DATA = new HashMap<String, String>();
        SAVING_DATA.put("en", "Saving Data");

        COMPLETE = new HashMap<String, String>();
        COMPLETE.put("en", "Complete");
    }

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

    protected void setSurveyId(Map<String, String> criteria) {
        if (criteria != null && criteria.get(SURVEY_CONFIG_KEY) != null) {
            setSurveyId(new Long(criteria.get(SURVEY_CONFIG_KEY).trim()));
        }
    }

    public void runImport(File file, String serverBase, Map<String, String> criteria)
            throws Exception {

        Sheet sheet = getDataSheet(file);
        Map<Long, Long> columnIndexToQuestionId = processHeader(sheet);
        Map<Long, QuestionDto> questionIdToQuestionDto = fetchQuestions(serverBase, criteria);

        List<InstanceData> instanceDataList = parseSheet(sheet, questionIdToQuestionDto,
                columnIndexToQuestionId);

        List<String> importUrls = new ArrayList<>();

        for (InstanceData instanceData : instanceDataList) {
            String importUrl = buildImportURL(instanceData, criteria.get("surveyId"),
                    questionIdToQuestionDto);
            importUrls.add(importUrl);
        }

        for (String importUrl : importUrls) {
            invokeUrl(serverBase, importUrl, true,
                    criteria.get(KEY_PARAM));
        }
        Iterator<Row> rowIterator = sheet.rowIterator();
        int rowCount = 0;
        while (rowIterator.hasNext()) {
            rowIterator.next();
            rowCount++;
        }
        // now update the summaries
        if (rowCount * questionIdToQuestionDto.size() < SIZE_THRESHOLD) {
            invokeUrl(serverBase, "action=" + RawDataImportRequest.UPDATE_SUMMARIES_ACTION + "&" +
                    RawDataImportRequest.SURVEY_ID_PARAM + "=" + surveyId, true,
                    criteria.get(KEY_PARAM));
        }
        invokeUrl(serverBase, "action=" + RawDataImportRequest.SAVE_MESSAGE_ACTION + "&" +
                RawDataImportRequest.SURVEY_ID_PARAM + "=" + surveyId, true,
                criteria.get(KEY_PARAM));

    }

    /**
     * Parse a raw data report file into a list of InstanceData
     * 
     * @param sheet
     * @param columnIndexToQuestionId
     * @param questionIdToQuestionDto
     * @return
     */
    public List<InstanceData> parseSheet(Sheet sheet,
            Map<Long, QuestionDto> questionIdToQuestionDto, Map<Long, Long> columnIndexToQuestionId)
            throws Exception {

        List<InstanceData> result = new ArrayList<>();

        int md5Column = sheet.getRow(0).getLastCellNum();

        int row = 1;

        while (true) {
            InstanceData instanceData = parseInstance(sheet, row, questionIdToQuestionDto,
                    columnIndexToQuestionId);

            if (instanceData == null) {
                break;
            }

            // Get all the parsed rows for md5 calculation
            List<Row> rows = new ArrayList<>();
            for (int r = row; r < row + instanceData.maxIterationsCount; r++) {
                rows.add(sheet.getRow(r));
            }
            String existingMd5Hash = sheet.getRow(row).getCell(md5Column)
                    .getStringCellValue();
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
     * @return InstanceData
     */
    public InstanceData parseInstance(Sheet sheet, int startRow,
            Map<Long, QuestionDto> questionIdToQuestionDto, Map<Long, Long> columnIndexToQuestionId) {

        // File layout
        // 0. SurveyedLocaleIdentifier
        // 7. Repeat
        // 1. SurveyedLocaleDisplayName
        // 2. DeviceIdentifier
        // 3. SurveyInstanceId
        // 4. CollectionDate
        // 5. SubmitterName
        // 6. SurveyalTime

        // 8 - N. Questions
        // N + 1. Digest

        Row baseRow = sheet.getRow(startRow);
        if (baseRow == null) {
            return null;
        }
        String surveyedLocaleIdentifier = ExportImportUtils.parseCellAsString(baseRow.getCell(0));
        String surveyedLocaleDisplayName = ExportImportUtils.parseCellAsString(baseRow.getCell(2));
        String deviceIdentifier = ExportImportUtils.parseCellAsString(baseRow.getCell(3));
        String surveyInstanceId = ExportImportUtils.parseCellAsString(baseRow.getCell(4));
        Date collectionDate = ExportImportUtils.parseDate(
                ExportImportUtils.parseCellAsString(baseRow.getCell(5)));
        String submitterName = ExportImportUtils.parseCellAsString(baseRow.getCell(6));
        String surveyalTime = ExportImportUtils.parseCellAsString(baseRow.getCell(7));

        int iterations = 1;

        // Count the maximum number of iterations for this instance
        while (true) {
            Row row = sheet.getRow(startRow + iterations);
            if (row == null || !ExportImportUtils.parseCellAsString(row.getCell(0)).equals("")) {
                break;
            }
            iterations++;
        }

        // question-id -> iteration -> response
        Map<Long, Map<Long, String>> responseMap = new HashMap<>();

        for (Entry<Long, Long> m : columnIndexToQuestionId.entrySet()) {
            long columnIndex = m.getKey();
            long questionId = m.getValue();

            boolean isGeoQuestion = questionIdToQuestionDto.get(questionId).getQuestionType() == QuestionType.GEO;

            for (int iter = 0; iter < iterations; iter++) {

                Row iterationRow = sheet.getRow(startRow + iter);

                long iteration = (long) iterationRow.getCell(1).getNumericCellValue();

                String val = "";

                Cell cell = iterationRow.getCell((int) columnIndex);

                if (cell != null) {
                    if (isGeoQuestion) {
                        String latitude = ExportImportUtils.parseCellAsString(cell);
                        String longitude = ExportImportUtils.parseCellAsString(iterationRow
                                .getCell((int) columnIndex + 2));
                        String elevation = ExportImportUtils.parseCellAsString(iterationRow
                                .getCell((int) columnIndex + 2));
                        String geoCode = ExportImportUtils.parseCellAsString(iterationRow
                                .getCell((int) columnIndex + 3));
                        val = latitude + "|" + longitude + "|" + elevation + "|" + geoCode;
                    } else {
                        val = ExportImportUtils.parseCellAsString(cell);
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
                            iterationToResponse.put(iteration, val);
                        }
                    }
                }
            }
        }

        SurveyInstanceDto surveyInstanceDto = new SurveyInstanceDto();
        surveyInstanceDto.setSurveyedLocaleIdentifier(surveyedLocaleIdentifier);
        surveyInstanceDto.setSurveyedLocaleDisplayName(surveyedLocaleDisplayName);
        surveyInstanceDto.setDeviceIdentifier(deviceIdentifier);
        surveyInstanceDto.setKeyId(Long.parseLong(surveyInstanceId));
        surveyInstanceDto.setCollectionDate(collectionDate);
        surveyInstanceDto.setSubmitterName(submitterName);
        surveyInstanceDto.setSurveyalTime((long) durationToSeconds(surveyalTime));

        InstanceData instanceData = new InstanceData(surveyInstanceDto, responseMap);
        instanceData.maxIterationsCount = (long) iterations;
        return instanceData;
    }

    /**
     * Return a map of column index -> question id
     *
     * @param sheet
     * @return A map from column index to question id.
     */
    private static Map<Long, Long> processHeader(Sheet sheet) {
        Map<Long, Long> columnIndexToQuestionId = new HashMap<>();
        Row headerRow = sheet.getRow(0);

        for (Cell cell : headerRow) {
            String cellValue = cell.getStringCellValue();
            if (cell.getStringCellValue().indexOf("|") > -1 && !cellValue.startsWith("--GEO")) {
                String[] parts = cell.getStringCellValue().split("\\|");
                if (parts[0].trim().length() > 0) {
                    columnIndexToQuestionId.put(Long.valueOf(cell.getColumnIndex()),
                            Long.valueOf(parts[0].trim()));
                }
            }
        }

        return columnIndexToQuestionId;
    }

    /**
     * @return map from question id to QuestionDto
     */
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
        sb.append(RawDataImportRequest.SURVEY_INSTANCE_ID_PARAM + "="
                + dto.getKeyId() + "&");

        // Collection date
        // TODO: null-check
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
            SortedMap<Long, String> iterations = entry.getValue();

            sb.append("&questionId=" + questionId);

            for (Entry<Long, String> iterationEntry : iterations.entrySet()) {
                Long iteration = iterationEntry.getKey();
                String response = iterationEntry.getValue();

                sb.append("|" + iteration + "=" + URLEncoder.encode(response, "UTF-8"));

            }

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
                    default:
                        break;
                }
            }

            sb.append("|type=" + typeString);

        }

        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void executeImport(File file, String serverBase,
            Map<String, String> criteria) {
        try {

            int rows = 0;
            errorIds = new ArrayList<String>();
            jobQueue = new LinkedBlockingQueue<Runnable>();
            threadPool = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS,
                    jobQueue);

            setSurveyId(criteria);

            Sheet sheet1 = getDataSheet(file);

            HashMap<Integer, String> questionIDColMap = new HashMap<Integer, String>();
            Object[] results = BulkDataServiceClient.loadQuestions(
                    getSurveyId().toString(), serverBase, criteria.get("apiKey"));
            Map<String, QuestionDto> questionMap = null;

            if (results != null) {
                questionMap = (Map<String, QuestionDto>) results[1];
            }

            boolean hasDurationCol = true;
            boolean setFirstQuestionColumnIdx = true;
            int firstQuestionCol = 0;

            MessageDigest digest = MessageDigest.getInstance("MD5");
            for (Row row : sheet1) {
                rows++;
                if (row.getRowNum() == 0) {
                    // Process headers
                    for (Cell cell : row) {
                        if (cell.getStringCellValue().indexOf("|") > -1) {
                            if (setFirstQuestionColumnIdx) {
                                firstQuestionCol = cell.getColumnIndex();
                                setFirstQuestionColumnIdx = false;
                            }

                            String[] parts = cell.getStringCellValue().split("\\|");
                            if (parts[0].trim().length() > 0) {
                                questionIDColMap.put(cell.getColumnIndex(), parts[0].trim());
                            }
                        }
                    }
                    continue; // move to next row (data)
                }
                digest.reset();

                String instanceId = null;
                String dateString = null;
                String submitter = null;
                String duration = null;
                String durationSeconds = null;
                StringBuilder sb = new StringBuilder();

                // Headers
                // [identifier, displayName, deviceIdentifier, instanceId, date, submitter,
                // duration, questions...]

                int instanceIdx = firstQuestionCol - 4;
                int dateIdx = firstQuestionCol - 3;
                int submitterIdx = firstQuestionCol - 2;
                int durationIdx = firstQuestionCol - 1;

                sb.append("action="
                        + RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION
                        + "&" + RawDataImportRequest.SURVEY_ID_PARAM + "="
                        + getSurveyId() + "&");
                boolean needUpload = true;
                String initialUrl = sb.toString();

                for (Cell cell : row) {
                    if (cell.getColumnIndex() == instanceIdx) {
                        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            instanceId = new Double(cell.getNumericCellValue())
                                    .intValue() + "";
                        } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            instanceId = cell.getStringCellValue();
                        }
                        if (instanceId != null) {
                            sb.append(RawDataImportRequest.SURVEY_INSTANCE_ID_PARAM
                                    + "=" + instanceId + "&");
                        }
                    }
                    if (cell.getColumnIndex() == dateIdx) {
                        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            dateString = cell.getStringCellValue();
                        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                            Date date = HSSFDateUtil.getJavaDate(cell
                                    .getNumericCellValue());
                            dateString = ExportImportUtils.formatDate(date);
                        }
                        if (dateString != null) {
                            sb.append(RawDataImportRequest.COLLECTION_DATE_PARAM
                                    + "="
                                    + URLEncoder.encode(dateString, "UTF-8")
                                    + "&");
                        }
                    }
                    if (cell.getColumnIndex() == submitterIdx) {
                        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            submitter = cell.getStringCellValue();
                            sb.append("submitter="
                                    + URLEncoder.encode(submitter, "UTF-8")
                                    + "&");
                        }
                    }
                    // Survey Duration
                    if (cell.getColumnIndex() == durationIdx) {
                        if (hasDurationCol) {
                            switch (cell.getCellType()) {
                            // if the cell type is string, we expect hh:mm:ss format
                                case Cell.CELL_TYPE_STRING:
                                    duration = cell.getStringCellValue();
                                    durationSeconds = String.valueOf(durationToSeconds(duration));
                                    digest.update(duration.getBytes());
                                    break;
                                // if the cell type if numeric, we expect a single seconds value
                                case Cell.CELL_TYPE_NUMERIC:
                                    durationSeconds = String.valueOf(cell.getNumericCellValue());
                                    digest.update(durationSeconds.getBytes());
                                    break;
                                default:
                                    durationSeconds = "0";
                                    // don't update the digest, because we want this value to be
                                    // saved.
                                    break;
                            }
                            sb.append("duration="
                                    + URLEncoder.encode(durationSeconds, "UTF-8")
                                    + "&");
                        }
                    }

                    boolean hasValue = false;
                    String qId = questionIDColMap.get(cell.getColumnIndex());

                    if (cell.getColumnIndex() >= firstQuestionCol
                            && qId != null && !qId.trim().equals("")) {
                        QuestionDto question = questionMap.get(questionIDColMap
                                .get(cell.getColumnIndex()));
                        QuestionType type = null;
                        // VALUE is default, it is valid for NUMBER, FREE_TEXT, SCAN, OPTION
                        String typeString = "VALUE";
                        if (question != null) {
                            type = question.getType();
                            if (QuestionType.GEO == type) {
                                typeString = "GEO";
                            } else if (QuestionType.PHOTO == type) {
                                typeString = "IMAGE";
                            } else if (QuestionType.VIDEO == type) {
                                typeString = "VIDEO";
                            } else if (QuestionType.DATE == type) {
                                typeString = "DATE";
                            }
                        } else if (questionIDColMap.get(cell.getColumnIndex())
                                .startsWith("--")) {
                            continue;
                        }

                        String cellVal = ExportImportUtils.parseCellAsString(cell);
                        if (cellVal != null) {
                            cellVal = cellVal.trim();

                            switch (question.getType()) {
                                case GEO:
                                case CASCADE:
                                    String[] parts = cellVal.split("\\|");
                                    for (int i = 0; i < parts.length; i++) {
                                        digest.update(parts[i].getBytes());
                                    }
                                    cellVal = cellVal.replaceAll("\\|", "^^");
                                    break;

                                case PHOTO:
                                case VIDEO:
                                    digest.update(cellVal.getBytes()); // compute before modifying
                                    if (cellVal.contains("/")) {
                                        cellVal = cellVal.substring(cellVal
                                                .lastIndexOf("/"));
                                    }
                                    cellVal = "/sdcard" + cellVal;
                                    break;

                                case DATE:
                                    digest.update(cellVal.getBytes());
                                    cellVal = ExportImportUtils.parseDate(cellVal).getTime() + "";
                                    break;

                                case GEOSHAPE:
                                case SCAN:
                                case NUMBER:
                                case FREE_TEXT:
                                case OPTION: // while exporting digest is computed with pipes
                                    digest.update(cellVal.getBytes());
                                    break;

                                default:
                                    break;
                            }
                        } else {
                            cellVal = "";
                        }

                        if (type != QuestionType.GEO) {
                            hasValue = true;
                            sb.append(
                                    "questionId="
                                            + questionIDColMap.get(cell
                                                    .getColumnIndex())
                                            + "|value=").append(
                                    cellVal != null ? URLEncoder.encode(
                                            cellVal, "UTF-8") : "");
                        } else {
                            hasValue = true;
                            sb.append("questionId="
                                    + questionIDColMap.get(cell
                                            .getColumnIndex()) + "|value=");
                            if (questionIDColMap.get(cell.getColumnIndex() + 1) != null
                                    && questionIDColMap.get(
                                            cell.getColumnIndex() + 1)
                                            .startsWith("--")) {

                                for (int i = 1; i < 4; i++) {
                                    String nextVal = ExportImportUtils.parseCellAsString(row
                                            .getCell(cell.getColumnIndex() + i));
                                    cellVal += "|"
                                            + (nextVal != null ? nextVal : "");
                                }
                                // if the length of the cellVal is too small, which means there is
                                // no valid info, skip.
                                if (cellVal.length() < 5) {
                                    cellVal = "";
                                }
                                sb.append(cellVal != null ? URLEncoder.encode(
                                        cellVal, "UTF-8") : "");
                            } else {
                                sb.append(cellVal != null ? URLEncoder.encode(
                                        cellVal, "UTF-8") : "");
                            }
                        }

                        if (hasValue) {
                            sb.append("|type=").append(typeString).append("&");
                        }
                    } else if (cell.getColumnIndex() >= firstQuestionCol) {
                        // we should only get here if we have a column that
                        // isn't in the header
                        // as long as the user hasn't messed up the sheet, this
                        // is the md5 digest of the original data
                        try {
                            String md5 = ExportImportUtils.parseCellAsString(cell);
                            String digestVal = StringUtil.toHexString(digest
                                    .digest());
                            if (md5 != null && md5.equals(digestVal)) {
                                needUpload = false;
                            } else if (md5 != null && log.isDebugEnabled()) {
                                log.debug("Row: " + row.getRowNum()
                                        + " MD5: " + digestVal + " orig md5: "
                                        + md5);
                            }
                        } catch (Exception e) {
                            // if we can't handle the md5, then just assume we
                            // need to update the row
                            log.error("Couldn't process md5 for row: "
                                    + row.getRowNum() + " - " + e.getMessage(), e);
                        }
                    }
                }

                // make sure row in sheet actually contained data
                boolean isEmptyRow = initialUrl.equals(sb.toString().trim());
                if (needUpload && !isEmptyRow) {
                    sendDataToServer(
                            serverBase,
                            null,
                            sb.toString(),
                            criteria.get(KEY_PARAM));
                }
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
            // now update the summaries
            if ((questionIDColMap.size() * rows) < SIZE_THRESHOLD) {
                invokeUrl(serverBase,
                        "action="
                                + RawDataImportRequest.UPDATE_SUMMARIES_ACTION
                                + "&" + RawDataImportRequest.SURVEY_ID_PARAM
                                + "=" + surveyId, true, criteria.get(KEY_PARAM));
            }

            invokeUrl(serverBase, "action="
                    + RawDataImportRequest.SAVE_MESSAGE_ACTION + "&"
                    + RawDataImportRequest.SURVEY_ID_PARAM + "=" + surveyId,
                    true, criteria.get(KEY_PARAM));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
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
     * handles calling invokeURL twice (once to reset the instance and again to save the new one) as
     * a separate job submitted to the thread pool
     *
     * @param serverBase
     * @param resetUrlString
     * @param saveUrlString
     */
    private void sendDataToServer(final String serverBase,
            final String resetUrlString, final String saveUrlString,
            final String key) {
        threadPool.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    if (resetUrlString != null) {
                        invokeUrl(serverBase, resetUrlString, true, key);
                    }
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

            for (Cell cell : headerRow) {
                String cellValue = cell.getStringCellValue();
                if (firstQuestionFound && !cellValue.matches(".+\\|.+")) {
                    errorMap.put(cell.getColumnIndex(),
                            String.format("The header \"%s\" can not be imported", cellValue));
                    break;
                } else {
                    if (!firstQuestionFound && cellValue.matches("[0-9]+\\|.+")) {
                        firstQuestionFound = true;
                        int idx = cell.getColumnIndex();
                        // idx == 4, non monitoring, old format
                        // idx == 6, monitoring, old format
                        // idx == 7, new format
                        // idx == 8, new format, with repeat column
                        if (!(idx == 4 || idx == 6 || idx == 7 || idx == 8)) {
                            errorMap.put(idx, "Found the first question at the wrong column index");
                            break;
                        }
                    }
                }
            }
            if (!firstQuestionFound) {
                errorMap.put(-1, "A question could not be found");
            }

        } catch (Exception e) {
            errorMap.put(-1, e.getMessage());
        }

        return errorMap;
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
        r.runImport(file, serverBaseArg, configMap);
        // r.executeImport(file, serverBaseArg, configMap);
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }
}
