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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.akvo.flow.util.FlowJsonObjectReader;
import org.akvo.flow.util.FlowJsonObjectWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.geojson.GeoJsonObject;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.RawDataImportRequest;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;
import static org.waterforpeople.mapping.dataexport.ExportImportConstants.*;

import com.gallatinsystems.framework.dataexport.applet.DataImporter;

public class RawDataSpreadsheetImporter implements DataImporter {

    private static final Logger log = Logger.getLogger(RawDataSpreadsheetImporter.class.getName());

    private static final String SERVLET_URL = "/rawdatarestapi";
    public static final String SURVEY_CONFIG_KEY = "surveyId";
    protected static final String KEY_PARAM = "apiKey";
    private InputStream stream;
    private ThreadPoolExecutor threadPool;
    private BlockingQueue<Runnable> jobQueue;
    private List<String> errorIds;
    private static final FlowJsonObjectWriter JSON_OBJECT_WRITER = new FlowJsonObjectWriter();
    private static final FlowJsonObjectReader JSON_OBJECT_READER = new FlowJsonObjectReader();

    private boolean otherValuesInSeparateColumns = false; //until we find one

    public static final String DATAPOINT_IDENTIFIER_COLUMN_KEY = "dataPointIdentifier";
    private static final String DATAPOINT_APPROVAL_COLUMN_KEY = "dataPointApproval";
    public static final String REPEAT_COLUMN_KEY = "repeat";
    public static final String DATAPOINT_NAME_COLUMN_KEY = "dataPointDisplayName";
    public static final String DEVICE_IDENTIFIER_COLUMN_KEY = "deviceIdentifier";
    public static final String SURVEY_INSTANCE_COLUMN_KEY = "surveyInstanceId";
    public static final String COLLECTION_DATE_COLUMN_KEY = "collectionDate";
    public static final String SUBMITTER_COLUMN_KEY = "submitterName";
    public static final String DURATION_COLUMN_KEY = "surveyalTime";
    public static final String FORM_VER_COLUMN_KEY = "formVersion";

    public static final String NEW_DATA_PATTERN = "^[Nn]ew-\\d+"; // new- or New- followed by one or more digits
    public static final String VALID_QUESTION_HEADER_PATTERN = "[0-9]+\\|.+"; //digits followed by a vertical bar
    public static final String VALID_GEO_QUESTION_HEADER_PATTERN = "^(\\d+\\|Latitude|--GEOLON--\\|Longitude|--GEOELE--\\|Elevation)$";

    /**
     * opens a file input stream using the file passed in and tries to return the first worksheet in
     * that file.
     * Also called from uploader.clj.
     *
     * @param file
     * @return
     * @throws Exception
     */
    public Sheet getDataSheet(File file) {
        Workbook wb = null;
        try {
            stream = new PushbackInputStream(new FileInputStream(file));
            wb = WorkbookFactory.create(stream);
            return wb.getSheetAt(0); //Assumes raw data sheet is first
        } catch (Exception e) {
            log.severe("Workbook creation exception:" + e);
        }
        return null;
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
            Workbook wb = getDataSheet(file).getWorkbook();

            int headerRowIndex = 1; //Only support split sheets from now on

            Map<Sheet, Map<Integer, Long>> sheetMap = new HashMap<>();
            // Find all data sheets
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                String sn = sheet.getSheetName();
                if (i == 0 || sn != null && sn.matches(GROUP_DATA_SHEET_PATTERN)) { //Assume base sheet is 0
                    sheetMap.put(sheet, processHeader(sheet, headerRowIndex));
                    otherValuesInSeparateColumns |= separatedOtherValues(sheet, headerRowIndex);
                }
            }

            Map<Long, QuestionDto> questionIdToQuestionDto = fetchQuestions(serverBase, criteria);

            Map<Long, List<QuestionOptionDto>> optionNodes = fetchOptionNodes(serverBase,
                    criteria, questionIdToQuestionDto.values());

            List<InstanceData> instanceDataList = parseSplitSheets(wb.getSheetAt(0), //Assume base sheet is 0
                    sheetMap,
                    questionIdToQuestionDto,
                    optionNodes,
                    headerRowIndex);
            //Strip link-identifiers from new data
            for (InstanceData instanceData : instanceDataList) {
                if (instanceData.surveyInstanceDto.getSurveyedLocaleIdentifier().matches(NEW_DATA_PATTERN)) {
                    instanceData.surveyInstanceDto.setSurveyedLocaleIdentifier("");
                    //TODO maybe clear out instance id too, just in case?
                }
            }

            List<String> importUrls = new ArrayList<>();
            String surveyId = criteria.get("surveyId");
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
                log.severe("There were ERRORS: ");
                for (String line : errorIds) {
                    log.severe(line);
                }
            }

            Thread.sleep(5000);
            log.finest("Updating summaries");
            invokeUrl(serverBase, "action=" + RawDataImportRequest.UPDATE_SUMMARIES_ACTION
                    + "&" + RawDataImportRequest.SURVEY_ID_PARAM + "=" + surveyId, true,
                    criteria.get(KEY_PARAM));
            invokeUrl(serverBase, "action=" + RawDataImportRequest.SAVE_MESSAGE_ACTION + "&"
                    + RawDataImportRequest.SURVEY_ID_PARAM + "=" + surveyId, true,
                    criteria.get(KEY_PARAM));

        } catch (Exception e) {
            log.log(Level.SEVERE,"Failed to import raw data report", e);
        } finally {
            if (threadPool != null) {
                threadPool.shutdown();
            }
            cleanup();
        }

    }

    /**
     * Parse a raw data report file into a list of InstanceData
     */
    public List<InstanceData> parseSplitSheets(Sheet baseSheet,
            Map<Sheet, Map<Integer, Long>> sheetMap,
            Map<Long, QuestionDto> questionIdToQuestionDto,
            Map<Long, List<QuestionOptionDto>> optionNodes,
            int headerRowIndex)
            throws Exception {

        List<InstanceData> result = new ArrayList<>();
        Map<Sheet, Integer> sheetPosition = new HashMap<>();

        // Find the first empty/null cell in the base sheet header row. This is the position of the md5 hashes
        int md5Column = 0;
        while (true) {
            if (isEmptyCell(baseSheet.getRow(headerRowIndex).getCell(md5Column))) {
                break;
            }
            md5Column++;
        }

        //these are all for the base sheet
        Map<Integer, Long> columnIndexToQuestionId = sheetMap.get(baseSheet);
        int firstQuestionColumnIndex = -1;
        if (columnIndexToQuestionId.isEmpty()) { //Nothing but metadata
            firstQuestionColumnIndex = md5Column;
        } else {
            firstQuestionColumnIndex = Collections.min(columnIndexToQuestionId.keySet());
        }
        Map<String, Integer> metadataColumnHeaderIndex = getMetadataColumnIndex(baseSheet, firstQuestionColumnIndex, headerRowIndex);
        Map<String, Integer> repMetadataIndex = null; //lazy calc, done if needed; all rep sheets should be the same!

        int row = headerRowIndex + 1; //where the data starts
        while (true) {
            InstanceData instanceData = parseInstance(baseSheet, row, metadataColumnHeaderIndex,
                    firstQuestionColumnIndex, questionIdToQuestionDto, columnIndexToQuestionId,
                    optionNodes, false);

            if (instanceData == null) {
                break; //End of sheet
            }

            // Have to collect all the parsed rows for md5 calculation
            List<Row> allRows = new ArrayList<>();

            //Now add in the answers on any rqg sheets
            for (Sheet repSheet : sheetMap.keySet()) {
                if (repSheet != baseSheet) {
                    Map<Integer, Long> repQMap = sheetMap.get(repSheet);
                    int repFirstQIdx = Collections.min(repQMap.keySet());
                    if (repMetadataIndex == null) { //do this only once??
                        repMetadataIndex = getMetadataColumnIndex(repSheet, repFirstQIdx, headerRowIndex);
                    }
                    Integer pos = sheetPosition.get(repSheet);
                    if (pos == null) { //never scanned this one before; start at top
                        pos = Integer.valueOf(headerRowIndex + 1);
                    }
                    //May add data to instanceData and rows to allRows
                    pos = parseRepeatsForInstance(instanceData, repSheet,
                            pos, repMetadataIndex,
                            questionIdToQuestionDto, repQMap,
                            optionNodes, allRows);
                    sheetPosition.put(repSheet, pos); //replace with new pos; might be any value
                }
            }

            //Put base row last, just like in exporter, so digest matches
            allRows.add(baseSheet.getRow(row));

            String existingMd5Hash = "";
            Cell md5Cell = baseSheet.getRow(row).getCell(md5Column);
            // For new data the md5 hash column could be empty
            if (md5Cell != null) {
                existingMd5Hash = md5Cell.getStringCellValue();
            }
            String newMd5Hash = ExportImportUtils.md5Digest(allRows, md5Column - 1, baseSheet);

            if (!newMd5Hash.equals(existingMd5Hash)) {
                result.add(instanceData);
            }

            row++;
        }

        return result;
    }



    /**
     * returns a map of where the metadata columns are on this sheet
     * @param firstQuestionColumnIndex
     * @return
     */
    private Map<String, Integer> getMetadataColumnIndex(Sheet sheet, int firstQuestionColumnIndex, int headerRow) {
        Map<String, Integer> index = new HashMap<>();

        Row row = sheet.getRow(headerRow);
        for (int i = 0; i < firstQuestionColumnIndex; i++) {
            String header = row.getCell(i).getStringCellValue();
            if (header.equalsIgnoreCase(IDENTIFIER_LABEL)) {
                index.put(DATAPOINT_IDENTIFIER_COLUMN_KEY, i);
            } else if (header.equalsIgnoreCase(DATA_APPROVAL_STATUS_LABEL)) {
                index.put(DATAPOINT_APPROVAL_COLUMN_KEY, i);
            } else if (header.equalsIgnoreCase(REPEAT_LABEL)) {
                index.put(REPEAT_COLUMN_KEY, i);
            } else if (header.equalsIgnoreCase(DISPLAY_NAME_LABEL)) {
                index.put(DATAPOINT_NAME_COLUMN_KEY, i);
            } else if (header.equalsIgnoreCase(DEVICE_IDENTIFIER_LABEL)) {
                index.put(DEVICE_IDENTIFIER_COLUMN_KEY, i);
            } else if (header.equalsIgnoreCase(INSTANCE_LABEL)) {
                index.put(SURVEY_INSTANCE_COLUMN_KEY, i);
            } else if (header.equalsIgnoreCase(SUB_DATE_LABEL)) {
                index.put(COLLECTION_DATE_COLUMN_KEY, i);
            } else if (header.equalsIgnoreCase(SUBMITTER_LABEL)) {
                index.put(SUBMITTER_COLUMN_KEY, i);
            } else if (header.equalsIgnoreCase(DURATION_LABEL)) {
                index.put(DURATION_COLUMN_KEY, i);
            } else if (header.equalsIgnoreCase(FORM_VER_LABEL)) {
                index.put(FORM_VER_COLUMN_KEY, i);
            } else  {
                log.warning("Unknown column header '" + header + "'");
            }
        }
        return index;
    }

    private boolean checkCol(Map<String, Integer> index, String name) {
        if (!index.containsKey(name)) {
            log.warning("Required column '" + name + "' not found!");
            return false;
        }
        return true;
    }

    /**
     * @return
     */
    private Integer parseRepeatsForInstance(InstanceData instanceData,
            Sheet repSheet,
            int currentRowIndex,
            Map<String, Integer> metadataColumnHeaderIdx,
            Map<Long, QuestionDto> questionIdToQuestionDto,
            Map<Integer, Long> columnIndexToQuestionId,
            Map<Long, List<QuestionOptionDto>> optionNodes,
            List<Row> checksumRows) {
        // Rep sheet layout:
        // Cell [0,0] is "Metadata"
        //  the rest of row 0 is a group header. Question headers are on row 1
        //  and RQGs are on separate sheets.

        // 0. SurveyedLocaleIdentifier - link to base sheet
        // 1. Approval (if hasIterationColumn) - ignored duplicate
        // 2. Repeat, >= 1
        // 3. SurveyedLocaleDisplayName - ignored duplicate
        // 4. DeviceIdentifier - ignored duplicate
        // 5. SurveyInstanceId - link to base sheet?
        // 6. CollectionDate - ignored duplicate
        // 7. SubmitterName - ignored duplicate
        // 8. SurveyalTime - ignored duplicate
        // 9 - N. Questions

        int identifierColumnIndex = metadataColumnHeaderIdx.get(DATAPOINT_IDENTIFIER_COLUMN_KEY);
        int repeatIterationColumnIndex = metadataColumnHeaderIdx.get(REPEAT_COLUMN_KEY);

        String dataPointIdentifier = instanceData.surveyInstanceDto.getSurveyedLocaleIdentifier();
        //Find first row matching this, or give up
        int rowIx = currentRowIndex;
        Row row;
        while (true) {
            row = repSheet.getRow(rowIx);
            if (row != null
                    && row.getCell(identifierColumnIndex) != null
                    && row.getCell(identifierColumnIndex).getStringCellValue().equals(dataPointIdentifier)) { //found!
                break;
            } else {
                if (rowIx > repSheet.getLastRowNum()) { //fell off the end
                    rowIx = 1; //start over
                }
            }
            rowIx++;
            if (rowIx == currentRowIndex) { //back to where we started
                return rowIx; //not found; there were 0 iterations
            }
        }
        //Found one row for the instance, read them all

        Map<Long, Map<Long, String>> responseMap = new HashMap<>();

        while (row != null
                && row.getCell(identifierColumnIndex) != null
                && row.getCell(identifierColumnIndex).getStringCellValue().equals(dataPointIdentifier)
                && row.getCell(repeatIterationColumnIndex) != null
                && row.getCell(repeatIterationColumnIndex).getCellType() == Cell.CELL_TYPE_NUMERIC) {
            Long rep = (long) row.getCell(repeatIterationColumnIndex).getNumericCellValue(); //might throw on huge number
            //check repeat no for sanity
            if (rep < 1) { continue;}
            checksumRows.add(row);

            //loop over the data columns
            for (Entry<Integer, Long> m : columnIndexToQuestionId.entrySet()) {
                int columnIndex = m.getKey();
                long questionId = m.getValue();

                QuestionDto questionDto = questionIdToQuestionDto.get(questionId);
                if (questionDto != null) { //Somehow slipped by the Clojure validation
                    QuestionType questionType = questionDto.getType();
                    getIterationResponse(row, columnIndex, responseMap, questionType, questionId, questionDto, rep, optionNodes);
                }
            }

            rowIx++;
            row = repSheet.getRow(rowIx);
        }

        //TODO: Abort on overlap error? Some of this data will be lost.
        if (!instanceData.addResponses(responseMap)) {
            log.warning("Some questions have answers on more than one sheet!");
        }

        return rowIx;

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
    public InstanceData parseInstance(Sheet sheet,
            int startRow,
            Map<String, Integer> metadataColumnHeaderIndex,
            int firstQuestionColumnIndex,
            Map<Long, QuestionDto> questionIdToQuestionDto,
            Map<Integer, Long> columnIndexToQuestionId,
            Map<Long, List<QuestionOptionDto>> optionNodes,
            boolean singleOrRepSheet) {

        // Data sheet layout
        // If cell [0,0] is "Metadata"
        //  then the rest of row 0 is group headers and question headers are on row 1
        //  and RQGs are on separate sheets.
        // Otherwise question headers are on row 0.

        // 0. SurveyedLocaleIdentifier
        // 1. Approval (if hasIterationColumn)
        // 2. Repeat (if hasIterationColumn)
        // 3. SurveyedLocaleDisplayName
        // 4. DeviceIdentifier (if hasDeviceIdentifierColumn)
        // 5. SurveyInstanceId
        // 6. CollectionDate
        // 7. SubmitterName
        // 8. SurveyalTime

        // 9 - N. Questions (Possibly none)
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
        if (metadataColumnHeaderIndex.containsKey(DEVICE_IDENTIFIER_COLUMN_KEY)) {
            deviceIdentifier = getMetadataCellContent(baseRow,
                    metadataColumnHeaderIndex, DEVICE_IDENTIFIER_COLUMN_KEY);
        }

        String surveyInstanceId = getMetadataCellContent(baseRow, metadataColumnHeaderIndex,
                SURVEY_INSTANCE_COLUMN_KEY);
        Date collectionDate = ExportImportUtils.parseSpreadsheetDate(getMetadataCellContent(
                baseRow, metadataColumnHeaderIndex, COLLECTION_DATE_COLUMN_KEY));
        String submitterName = getMetadataCellContent(baseRow, metadataColumnHeaderIndex,
                SUBMITTER_COLUMN_KEY);
        String surveyalTime = getMetadataCellContent(baseRow, metadataColumnHeaderIndex,
                DURATION_COLUMN_KEY);

        Double formVer = null;
        if (metadataColumnHeaderIndex.containsKey(FORM_VER_COLUMN_KEY)) {
            String fvStr = getMetadataCellContent(baseRow, metadataColumnHeaderIndex,
                    FORM_VER_COLUMN_KEY);
            try
            {
                formVer = Double.valueOf(fvStr);
            }
            catch (NumberFormatException e) { /*ignore*/ }
        }


        int iterations = 1;
        int siColumnIndex = -1;

        // Count the maximum number of iterations for this instance
        if (singleOrRepSheet) {
            siColumnIndex = metadataColumnHeaderIndex.get(SURVEY_INSTANCE_COLUMN_KEY); //unsafe assignment
            while (true) {
                Row row = sheet.getRow(startRow + iterations);
                if (row == null // no row
                        || isEmptyCell(row.getCell(siColumnIndex))
                        || ExportImportUtils.parseCellAsString(
                                row.getCell(siColumnIndex)).equals(surveyInstanceId) // next q
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
            if (questionDto == null) { //no such question
                continue;
            }
            QuestionType questionType = questionDto.getType();

            for (int iter = 0; iter < iterations; iter++) {

                Row iterationRow = sheet.getRow(startRow + iter);

                long iteration = 1;
                if (singleOrRepSheet) {
                    Cell cell = iterationRow.getCell(siColumnIndex);
                    if (cell != null) {
                        iteration = (long) iterationRow.getCell(siColumnIndex)
                                .getNumericCellValue();
                    }
                }

                getIterationResponse(iterationRow, columnIndex, responseMap, questionType, questionId, questionDto, iteration, optionNodes);

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
        surveyInstanceDto.setFormVersion(formVer);

        InstanceData instanceData = new InstanceData(surveyInstanceDto, responseMap); //Copies and sorts the responseMap
        //instanceData.maxIterationsCount = iterations;
        return instanceData;
    }


    /**
     * gets one response for a single iteration from one or more columns
     * @param iterationRow
     * @param columnIndex
     * @param responseMap
     * @param questionType
     * @param questionId
     * @param questionDto
     * @param iteration
     * @param optionNodes
     *
     */
    private void getIterationResponse(Row iterationRow,
            int columnIndex,
            Map<Long, Map<Long, String>> responseMap,
            QuestionType questionType,
            Long questionId,
            QuestionDto questionDto,
            Long iteration,
            Map<Long, List<QuestionOptionDto>> optionNodes) {

        String val = "";

        Cell cell = iterationRow.getCell(columnIndex);

        if (cell != null //misses empty-but-has-other
                || (questionType == QuestionType.OPTION
                        && Boolean.TRUE.equals(questionDto.getAllowOtherFlag()
                        && otherValuesInSeparateColumns))) {
            switch (questionType) {
                case GEO:
                    String latitude = ExportImportUtils.parseCellAsString(cell);
                    String longitude = ExportImportUtils.parseCellAsString(iterationRow
                            .getCell(columnIndex + 1));
                    String elevation = ExportImportUtils.parseCellAsString(iterationRow
                            .getCell(columnIndex + 2));
                    if (!"".equals(latitude) && !"".equals(longitude)) { //We want both else ignore
                        val = latitude + "|" + longitude + "|" + elevation;
                    }
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
                            log.warning("Invalid cascade node: " + cascadeNode);
                        }
                        cascadeList.add(cascadeMap);
                    }
                    try {
                        val = JSON_OBJECT_WRITER.writeAsString(cascadeList);
                    } catch (IOException e) {
                        log.warning("Could not parse cascade string: " + cascadeString);
                    }
                    break;

                case OPTION:
                    // Two different possible formats:
                    // With codes: code1:val1|code2:val2|...
                    // Without codes: val1|val2|...
                    List<Map<String, Object>> optionList = new ArrayList<>();
                    String optionString = ExportImportUtils.parseCellAsString(cell);
                    if (!optionString.isEmpty()) {
                        String[] optionParts = optionString.split("\\|");
                        for (String optionNode : optionParts) {
                            optionList.add(parsedOptionValue(optionNode, false));
                        }
                    }

                    //Handle "other" data (even if there is nothing else)
                    if (Boolean.TRUE.equals(questionDto.getAllowOtherFlag())) {
                        if (otherValuesInSeparateColumns) { //2018-style
                            //get "other" from the next cell
                            Cell otherCell = iterationRow.getCell(columnIndex + 1);
                            String otherString = ExportImportUtils.parseCellAsString(otherCell);
                            if (otherString != null && !otherString.trim().isEmpty()) {
                                optionList.add(parsedOptionValue(otherString, true));
                            }
                        } else if (!optionList.isEmpty()) {
                            // could be the last entry in the cell
                            // unless the value matches one of the option names
                            Map<String, Object> lastNode = optionList.get(optionList.size() - 1);
                            String lastNodeText = (String) lastNode.get("text");
                            boolean isOther = true;
                            List<QuestionOptionDto> existingOptions = optionNodes.get(questionId);
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
                    }

                    try {
                        if (!optionList.isEmpty()) {
                            val = JSON_OBJECT_WRITER.writeAsString(optionList);
                        }
                    } catch (IOException e) {
                        log.log(Level.SEVERE, "Could not parse option string: " + optionString, e);
                    }

                    break;

                case DATE:
                    String dateString = ExportImportUtils.parseCellAsString(cell);
                    Date date = ExportImportUtils.parseSpreadsheetDate(dateString);
                    if (date != null) {
                        val = String.valueOf(date.getTime());
                    } else {
                        log.warning("Could not parse date string: " + dateString);
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

                case GEOSHAPE:
                    String geoshapeString = ExportImportUtils.parseCellAsString(cell);
                    if (validateGeoshape(geoshapeString, cell)) {
                        val = geoshapeString;
                    }
                    break;

                default:
                    val = ExportImportUtils.parseCellAsString(cell);
                    break;
            }

            if (val != null && !val.equals("")) {
                // Update response map
                // iteration -> response
                Map<Long, String> iterationToResponse = responseMap.get(questionId);

                if (iterationToResponse == null) { //first for this question
                    iterationToResponse = new HashMap<>();
                    iterationToResponse.put(iteration - 1, val);
                    responseMap.put(questionId, iterationToResponse);
                } else {
                    iterationToResponse.put(iteration - 1, val);
                }
            }
        }
    }

    protected boolean validateGeoshape(String geoShapeString, Cell cell) {
        try {
            GeoJsonObject geoJson = new ObjectMapper().readValue(geoShapeString, GeoJsonObject.class);
        } catch (JsonProcessingException e) {
            String cellAddress = null;
            if (cell != null) {
                cellAddress = cell.getAddress().toString();
            }
            log.warning("Invalid GeoJSON string in sheet. Cell: (" + cellAddress + "):" + e.getMessage());
            return false;
        }
        return true;
    }

    private Map<String, Object> parsedOptionValue(String optionNode, boolean other) {
        String[] codeAndText = optionNode.split(":", 2);
        Map<String, Object> optionMap = new HashMap<>();
        if (codeAndText.length == 1) {
            optionMap.put("text", codeAndText[0].trim());
        } else if (codeAndText.length == 2) {
            optionMap.put("code", codeAndText[0].trim());
            optionMap.put("text", codeAndText[1].trim());
        }
        if (other) {
            optionMap.put("isOther", true);
        }
        return optionMap;
    }

    private static String getMetadataCellContent(Row baseRow,
            Map<String, Integer> metadataColumnHeaderIndex,
            String metadataCellColumnKey) {
        Cell metadataCell = baseRow.getCell(metadataColumnHeaderIndex.get(metadataCellColumnKey));
        return ExportImportUtils.parseCellAsString(metadataCell);
    }

    private boolean separatedOtherValues(Sheet sheet, int headerRowIndex) {
        Row headerRow = sheet.getRow(headerRowIndex);
        for (Cell cell : headerRow) {
            String cellValue = cell.getStringCellValue();
            if (cell.getStringCellValue().indexOf("|") > -1
                    && cellValue.endsWith(OTHER_SUFFIX)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a map of column index -> question id on a sheet.
     * Metadata headers are assumed to never contain a "|".
     * @return A map from column index to question id.
     */
    public static Map<Integer, Long> processHeader(Sheet sheet, int headerRowIndex) {
        Map<Integer, Long> columnIndexToQuestionId = new HashMap<>();

        Row headerRow = sheet.getRow(headerRowIndex);

        for (Cell cell : headerRow) {
            String cellValue = cell.getStringCellValue();
            if (cell.getStringCellValue().indexOf("|") > -1
                    && !cellValue.startsWith(GEO_PREFIX)
                    && !cellValue.endsWith(OTHER_SUFFIX)
                    && !cellValue.startsWith(CADDISFLY_PREFIX)) {
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
     * TODO: fetch from latest published survey and not current data store.
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

        // run the survey instance import as a task
        sb.append(RawDataImportRequest.RUN_AS_TASK_PARAM).append("=1&");

        // Instance id
        if (dto.getKeyId() != null) {
            sb.append(RawDataImportRequest.SURVEY_INSTANCE_ID_PARAM + "="
                    + dto.getKeyId() + "&");
        }

        // Collection date
        String dateString = ExportImportUtils.formatDateTime(dto.getCollectionDate());

        sb.append(RawDataImportRequest.COLLECTION_DATE_PARAM + "="
                        + URLEncoder.encode(dateString, "UTF-8"));

        // Submitter
        sb.append("&submitter=" + URLEncoder.encode(dto.getSubmitterName(), "UTF-8"));

        // Duration
        sb.append("&duration=" + dto.getSurveyalTime());

        // Form version
        if (dto.getFormVersion() != null) {
        	sb.append("&formVersion=" + dto.getFormVersion());
        }

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
        if (duration == null || duration.length() == 0) {
            return 0;
        }

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
        if (tokens.length != 3) {
            return 0;
        }
        try {
            int hours = Integer.parseInt(tokens[0]);
            int minutes = Integer.parseInt(tokens[1]);
            int seconds = Integer.parseInt(tokens[2]);
            return 3600 * hours + 60 * minutes + seconds;
        } catch (Exception e) {
            return 0;
        }
    }

    private void sendDataToServer(final String serverBase, final String saveUrlString,
            final String key) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    invokeUrl(serverBase, saveUrlString, true, key);
                } catch (Exception e) {
                    errorIds.add(saveUrlString);
                    log.log(Level.SEVERE, "Could not invoke rest services: ", e);
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

    private String errorMessage(Cell cell, String cellValue) {
        return String.format(
                "Cannot import data from Column %s - \"%s\". Please check and/or fix the header cell",
                CellReference.convertNumToColString(cell.getColumnIndex()),
                cellValue);
    }

    /**
     * @param sheet
     * @param isBaseSheet
     * @param errorMap
     * @return number of questions columns found
     */
    private int validateSheet(Sheet sheet, boolean isBaseSheet, Map<Integer, String> errorMap) {
        //Each sheet should have a "Metadata" header, and on the next row, column headers
        //Verify that this is a 2017-style report w group headers and rqg's on separate sheets
        if (!safeCellCompare(sheet, 0, 0, METADATA_LABEL)) {
            errorMap.put(0, "First header cell on each sheet must contain '" + METADATA_LABEL + "'");
            return 0;
        }

        int questionCount = 0;
        final int headerRowIndex = 1; //Always, now
        Row headerRow = sheet.getRow(headerRowIndex);
        int firstQuestionColumnIndex = -1;
        int lastNonemptyHeaderColumnIndex = 0;

        for (Cell cell : headerRow) {
            String cellValue = cell.getStringCellValue();
            // if encountering a null cell make sure its only due to phantom cells at the end of
            // the row. If null or empty cell occurs in middle of header row report an error
            if (isEmptyCell(cell) && nonEmptyHeaderCellsAfter(cell)) {
                errorMap.put(
                        cell.getColumnIndex(),
                        errorMessage(cell, cellValue));
                break;
            }

            if (cellValue.contains("GEO") && !cellValue.matches(VALID_GEO_QUESTION_HEADER_PATTERN)) {
                errorMap.put(cell.getColumnIndex(), errorMessage(cell, cellValue));
                break;
            }

            if (!isEmptyCell(cell)) {
                lastNonemptyHeaderColumnIndex = cell.getColumnIndex();
            }
            if (cellValue.matches(VALID_QUESTION_HEADER_PATTERN)) {
                questionCount++;
                if (firstQuestionColumnIndex == -1) {
                    firstQuestionColumnIndex = cell.getColumnIndex();
                }
            }
        }

        if (firstQuestionColumnIndex == -1 && errorMap.isEmpty()) {
            //May be NO answers on base sheet if all groups are repeatable
            firstQuestionColumnIndex = lastNonemptyHeaderColumnIndex + 1;
        }

        String name = sheet.getSheetName();
        log.info("Sheet '" + name + "' first question column index: " + firstQuestionColumnIndex);

        //Check that all mandatory metadata columns exist
        //TODO: we might relax the set on group sheets
        Map<String, Integer> index = getMetadataColumnIndex(sheet,
                firstQuestionColumnIndex,
                headerRowIndex);
        if (!checkCol(index, DATAPOINT_IDENTIFIER_COLUMN_KEY)) {
            errorMap.put(-3, "Column header '" + IDENTIFIER_LABEL + "' missing on sheet " + name);
        }
        if (!checkCol(index, DATAPOINT_NAME_COLUMN_KEY)) {
            errorMap.put(-4, "Column header '" + DISPLAY_NAME_LABEL + "' missing on sheet " + name);
        }
        if (!checkCol(index, SURVEY_INSTANCE_COLUMN_KEY)) {
            errorMap.put(-5, "Column header '" + INSTANCE_LABEL + "' missing on sheet " + name);
        }
        if (!checkCol(index, COLLECTION_DATE_COLUMN_KEY)) {
            errorMap.put(-6, "Column header '" + SUB_DATE_LABEL + "' missing on sheet " + name);
        }
        if (!checkCol(index, SUBMITTER_COLUMN_KEY)) {
            errorMap.put(-7, "Column header '" + SUBMITTER_LABEL + "' missing on sheet " + name);
        }
        if (!checkCol(index, DURATION_COLUMN_KEY)) {
            errorMap.put(-8, "Column header '" + DURATION_LABEL + "' missing on sheet " + name);
        }
        if (!isBaseSheet && !checkCol(index, REPEAT_COLUMN_KEY)) {
            errorMap.put(-9, "Column header '" + REPEAT_LABEL + "' missing on sheet " + name);
        }

        return questionCount;
    }

    /*
     * validate
     * Called from Clojure code before executeImport()
     */
    @Override
    public Map<Integer, String> validate(File file) {
        Map<Integer, String> errorMap = new HashMap<Integer, String>();

        try {
            Workbook wb = getDataSheet(file).getWorkbook();

            //check each sheet in turn
            int questions = 0;
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                String name = sheet.getSheetName();
                if (i == 0) {
                    if (RAW_DATA_SHEET_LABEL.equals(name)) {
                        questions += validateSheet(sheet, true, errorMap);
                    } else {
                        errorMap.put(-12, "First sheet must be named '" + RAW_DATA_SHEET_LABEL + "'.");
                    }
                } else if (name != null && name.matches(GROUP_DATA_SHEET_PATTERN)) {
                    questions += validateSheet(sheet, false, errorMap);
                } else {
                    //Not an error; just ignore
                    log.info("Sheet '" + name + "' not validated for import.");
                }
            }
            if (questions == 0) {
                //“Judge a man by his questions rather than by his answers." -- Voltaire
                errorMap.put(-11, "No question columns found on any sheet.");
            }

        } catch (Exception e) {
            errorMap.put(-10, e.getMessage());
        }

        return errorMap;
    }

    /**
     * Check if a cell has a certain string value
     */
    private static boolean safeCellCompare(Sheet sheet, int row, int col, String value) {
        return (   sheet.getRow(row) != null
                && sheet.getRow(row).getCell(col) != null
                && sheet.getRow(row).getCell(col).getCellType() == Cell.CELL_TYPE_STRING
                && sheet.getRow(row).getCell(col).getStringCellValue().trim().equals(value)
                );
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
        for (int ix = row.getFirstCellNum(); ix < row.getLastCellNum(); ix++) {
            if (!isEmptyCell(row.getCell(ix))) {
                return false;
            }
        }
        return true;
    }

    /**
     * When a blank or null cell is incurred while processing, make sure that this is the last cell
     * in the row and ignore any other "phantom cells" that may occur. We only allow this for the
     * header row. If the blank cell occurs in between valid header cells we return true.
     *
     * @param cell
     * @return
     */
    private boolean nonEmptyHeaderCellsAfter(Cell cell) {
        assert cell.getRow().getRowNum() == 1; // only process header rows

        Row row = cell.getRow();
        for (int i = cell.getColumnIndex() + 1; i < row.getLastCellNum(); i++) {
            if (!isEmptyCell(row.getCell(i))) {
                return true;
            }
        }
        return false;
    }

    //This main() method is only for testing and debugging.
    //executeImport() is called from Clojure code in live deployment.
    public static void main(String[] args) throws Exception {

        if (args.length != 4) {
            log.severe("Error.\nUsage:\n\tjava org.waterforpeople.mapping.dataexport.RawDataSpreadsheetImporter <file> <serverBase> <surveyId> <apiKey>");
            System.exit(1);
        }
        File file = new File(args[0].trim());
        String serverBaseArg = args[1].trim();
        RawDataSpreadsheetImporter r = new RawDataSpreadsheetImporter();
        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put(SURVEY_CONFIG_KEY, args[2].trim());
        configMap.put("apiKey", args[3].trim());

        Map<Integer, String> validationErrors = r.validate(file);
        if (validationErrors.isEmpty()) {
            r.executeImport(file, serverBaseArg, configMap);
        }
    }
}
