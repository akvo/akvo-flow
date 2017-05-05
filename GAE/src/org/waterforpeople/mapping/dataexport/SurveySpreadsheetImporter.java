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
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.framework.dataexport.applet.DataImporter;

/**
 * this data importer will read a local excel spreadsheet file using the POI library and will then
 * save it to the server via the rest api. This importer supports 2 modes: the default
 * (isWholeSurvey =true) assumes we're loading an entire survey. If isWholeSurvey is false, then it
 * assumes we're inserting questions into an existing survey above the question denoted by the
 * beforeQuestionId param. before loading the questions, the entire survey is reordered to adjust
 * the orderings for the to-be inserted items
 *
 * @author Christopher Fagiani
 */
public class SurveySpreadsheetImporter implements DataImporter {

    private static final Logger log = Logger.getLogger(SurveySpreadsheetImporter.class);

    private static final String SERVLET_URL = "/surveyrestapi";
    private static final String BEFORE_QUESTION_ID_PARAM = "beforeQuestionId";
    private static final String WHOLE_SURVEY_PARAM = "isWholeSurvey";
    private static final String KEY_PARAM = "k";

    @Override
    public void executeImport(File file, String serverBase,
            Map<String, String> criteria) {
        InputStream inp = null;
        Sheet sheet1 = null;
        Integer startRow = 1;
        Long beforeQuestionId = null;
        boolean isWholeSurvey = true;
        if (criteria != null) {
            if (criteria.get(BEFORE_QUESTION_ID_PARAM) != null) {
                beforeQuestionId = new Long(
                        criteria.get(BEFORE_QUESTION_ID_PARAM));
            }
            if (criteria.get(WHOLE_SURVEY_PARAM) != null) {
                if ("false".equalsIgnoreCase(criteria.get(WHOLE_SURVEY_PARAM))) {
                    isWholeSurvey = false;
                }
            }
        }
        try {
            inp = new FileInputStream(file);
            HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
            sheet1 = wb.getSheetAt(0);
            String apiKey = criteria != null ? criteria.get("apiKey") : null;
            if (!isWholeSurvey) {
                // even though there is a header row, we want lastRowNum since
                // rows are 0 indexed
                int questionCount = sheet1.getLastRowNum();
                // figure out the starting order
                QuestionDto startingQuestion = BulkDataServiceClient
                        .loadQuestionDetails(serverBase, beforeQuestionId, apiKey);
                startRow = startingQuestion.getOrder();
                // now get all the questions
                List<QuestionDto> questionsInGroup = BulkDataServiceClient
                        .fetchQuestions(serverBase,
                                startingQuestion.getQuestionGroupId(), apiKey);

                if (questionsInGroup != null) {
                    // we only need to reorder the group into which we're
                    // importing

                    for (QuestionDto q : questionsInGroup) {
                        if (q.getOrder() >= startRow) {
                            StringBuilder reorderBuffer = new StringBuilder();
                            reorderBuffer
                                    .append("?")
                                    .append(SurveyRestRequest.ACTION_PARAM)
                                    .append("=")
                                    .append(SurveyRestRequest.UPDATE_QUESTION_ORDER_ACTION)
                                    .append("&")
                                    .append(SurveyRestRequest.QUESTION_ID_PARAM)
                                    .append("=")
                                    .append(q.getKeyId())
                                    .append("&")
                                    .append(SurveyRestRequest.QUESTION_ORDER_PARAM)
                                    .append("=")
                                    .append((q.getOrder() + questionCount));
                            String result = BulkDataServiceClient
                                    .fetchDataFromServer(serverBase
                                            + SERVLET_URL,
                                            reorderBuffer.toString(), true,
                                            criteria.get(KEY_PARAM));
                            log.debug(result);
                        }
                    }
                }
            }

            for (Row row : sheet1) {
                if (row.getRowNum() >= 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("?").append(SurveyRestRequest.ACTION_PARAM)
                            .append("=")
                            .append(SurveyRestRequest.SAVE_QUESTION_ACTION)
                            .append("&");
                    for (Cell cell : row) {
                        switch (cell.getColumnIndex()) {
                            case 0:
                                sb.append(SurveyRestRequest.SURVEY_GROUP_NAME_PARAM)
                                        .append("=")
                                        .append(URLEncoder.encode(
                                                parseCellAsString(cell).trim(),
                                                "UTF-8")).append("&");
                                break;
                            case 1:
                                sb.append(SurveyRestRequest.SURVEY_NAME_PARAM)
                                        .append("=")
                                        .append(URLEncoder.encode(
                                                parseCellAsString(cell).trim(),
                                                "UTF-8")).append("&");
                                break;
                            case 2:
                                sb.append(
                                        SurveyRestRequest.QUESTION_GROUP_ORDER_PARAM)
                                        .append("=")
                                        .append(new Double(cell
                                                .getNumericCellValue()).intValue())
                                        .append("&");
                                break;

                            case 3:
                                sb.append(
                                        SurveyRestRequest.QUESTION_GROUP_NAME_PARAM)
                                        .append("=")
                                        .append(URLEncoder.encode(
                                                parseCellAsString(cell).trim(),
                                                "UTF-8")).append("&");
                                break;

                            case 4:
                                int order = new Double(cell.getNumericCellValue())
                                        .intValue();
                                if (!isWholeSurvey) {
                                    order += (startRow - 1);
                                }
                                sb.append(SurveyRestRequest.QUESTION_ORDER_PARAM)
                                        .append("=").append(order).append("&");
                                break;

                            case 5:
                                sb.append(SurveyRestRequest.QUESTION_TEXT_PARAM)
                                        .append("=")
                                        .append(URLEncoder.encode(
                                                parseCellAsString(cell).trim(),
                                                "UTF-8")).append("&");
                                break;
                            case 6:
                                sb.append(SurveyRestRequest.QUESTION_TYPE_PARAM)
                                        .append("=")
                                        .append(URLEncoder.encode(
                                                parseCellAsString(cell).trim(),
                                                "UTF-8")).append("&");
                                break;
                            case 7:
                                sb.append(SurveyRestRequest.OPTIONS_PARAM)
                                        .append("=")
                                        .append(URLEncoder.encode(
                                                parseCellAsString(cell).trim(),
                                                "UTF-8")).append("&");
                                break;
                            case 8:
                                String valString = parseCellAsString(cell);
                                if (valString != null
                                        && valString.trim().length() > 0) {
                                    String[] parts = valString.split("\\|");
                                    int depOrder = new Integer(parts[0].trim());
                                    if (!isWholeSurvey) {
                                        depOrder += (startRow - 1);
                                    }
                                    sb.append(
                                            SurveyRestRequest.DEPEND_QUESTION_PARAM)
                                            .append("=")
                                            .append(URLEncoder.encode(depOrder
                                                    + "|" + parts[1], "UTF-8"))
                                            .append("&");
                                }
                                break;
                            case 9:
                                sb.append(SurveyRestRequest.ALLOW_OTHER_PARAM)
                                        .append("=")
                                        .append(parseCellAsString(cell))
                                        .append("&");
                                break;
                            case 10:
                                sb.append(SurveyRestRequest.ALLOW_MULTIPLE_PARAM)
                                        .append("=")
                                        .append(parseCellAsString(cell))
                                        .append("&");
                                break;
                            case 11:
                                sb.append(SurveyRestRequest.MANDATORY_PARAM)
                                        .append("=")
                                        .append(parseCellAsString(cell))
                                        .append("&");
                                break;
                            case 12:
                                sb.append(SurveyRestRequest.SCORING_PARAM)
                                        .append("=")
                                        .append(parseCellAsString(cell));
                                break;
                            case 13:
                                // min val
                                String minVal = parseCellAsString(cell);
                                if (minVal != null && minVal.trim().length() > 0) {
                                    sb.append("&")
                                            .append(SurveyRestRequest.VALIDATION_MIN_PARAM)
                                            .append("=").append(minVal);
                                }
                                break;
                            case 14:
                                // max val
                                String maxVal = parseCellAsString(cell);
                                if (maxVal != null && maxVal.trim().length() > 0) {
                                    sb.append("&")
                                            .append(SurveyRestRequest.VALIDATION_MAX_PARAM)
                                            .append("=").append(maxVal);
                                }
                                break;
                            case 15:
                                // allow sign
                                String signVal = parseCellAsString(cell);
                                if (signVal != null && signVal.trim().length() > 0) {
                                    sb.append("&")
                                            .append(SurveyRestRequest.VALIDATION_ALLOW_SIGN_PARAM)
                                            .append("=").append(signVal);
                                }
                                break;
                            case 16:
                                // allow decimal
                                String decimalVal = parseCellAsString(cell);
                                if (decimalVal != null
                                        && decimalVal.trim().length() > 0) {
                                    sb.append("&")
                                            .append(SurveyRestRequest.VALIDATION_ALLOW_DECIMAL_PARAM)
                                            .append("=").append(decimalVal);
                                }
                                break;
                            case 17:
                                // is name
                                String isNameVal = parseCellAsString(cell);
                                if (isNameVal != null
                                        && isNameVal.trim().length() > 0) {
                                    sb.append("&")
                                            .append(SurveyRestRequest.VALIDATION_IS_NAME_PARAM)
                                            .append("=").append(isNameVal);
                                }
                                break;

                            case 18:
                                String metricName = parseCellAsString(cell);
                                if (metricName != null
                                        && metricName.trim().length() > 0) {
                                    sb.append("&")
                                            .append(SurveyRestRequest.METRIC_NAME_PARAM)
                                            .append("=").append(metricName);
                                }
                                break;
                            case 19:
                                String metricGroup = parseCellAsString(cell);
                                if (metricGroup != null
                                        && metricGroup.trim().length() > 0) {
                                    sb.append("&")
                                            .append(SurveyRestRequest.METRIC_GROUP_PARAM)
                                            .append("=").append(metricGroup);
                                }
                                break;
                        }
                    }
                    try {
                        String result = BulkDataServiceClient
                                .fetchDataFromServer(serverBase + SERVLET_URL,
                                        sb.toString(), true,
                                        criteria.get(KEY_PARAM));
                        log.debug(result);
                    } catch (Throwable t) {
                        log.error("Error: " + t.getMessage(), t);
                        log.info("Trying again");
                        try {
                            BulkDataServiceClient
                            .fetchDataFromServer(serverBase + SERVLET_URL,
                                    sb.toString(), true,
                                    criteria.get(KEY_PARAM));
                        } catch (Exception e) {
                            log.error("Error:" + e.getMessage(), e);
                            // giving up
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inp != null) {
                try {
                    inp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String parseCellAsString(Cell cell) {
        String val = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    val = cell.getBooleanCellValue() + "";
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    val = cell.getNumericCellValue() + "";
                    break;
                default:
                    val = cell.getStringCellValue();
                    break;
            }
        }
        return val;
    }

    @Override
    public Map<Integer, String> validate(File file) {
        InputStream inp = null;
        Sheet sheet1 = null;
        Map<Integer, String> errorMap = new TreeMap<Integer, String>();

        try {
            inp = new FileInputStream(file);
            HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
            sheet1 = wb.getSheetAt(0);
            for (Row row : sheet1) {
                StringBuffer rowError = new StringBuffer();
                if (row.getRowNum() >= 1) {
                    String type = null;
                    for (Cell cell : row) {
                        try {
                            switch (cell.getColumnIndex()) {
                                case 0:
                                    if (cell.getStringCellValue().trim().length() == 0) {
                                        rowError.append("Survey Group Name is missing\n");
                                    }
                                    break;
                                case 1:
                                    if (cell.getStringCellValue().trim().length() == 0) {
                                        rowError.append("Survey Name is missing\n");
                                    }
                                    break;
                                case 2:
                                    try {
                                        if (cell.getNumericCellValue() < 0) {
                                            rowError.append("Question Group Order must be a positive integer\n");
                                        }
                                    } catch (Exception e) {
                                        rowError.append("Question group order must be a number\n");
                                    }
                                    break;
                                case 3:
                                    if (cell.getStringCellValue().trim().length() == 0) {
                                        rowError.append("Question Group Name is missing\n");
                                    }
                                    break;
                                case 4:
                                    try {
                                        if (cell.getNumericCellValue() < 0) {
                                            rowError.append("Question Id Order must be a positive integer\n");
                                        }
                                    } catch (Exception e) {
                                        rowError.append("Question Id order must be a number\n");
                                    }
                                    break;
                                case 5:
                                    if (cell.getStringCellValue().trim().length() == 0) {
                                        rowError.append("Question Text is missing\n");
                                    }
                                    break;
                                case 6:
                                    type = cell.getStringCellValue().trim();
                                    if (type.length() == 0) {
                                        rowError.append("Question Type is missing\n");
                                    } else {
                                        if (!(type
                                                .equals(QuestionDto.QuestionType.FREE_TEXT
                                                        .toString())
                                                || type.equals(QuestionDto.QuestionType.PHOTO
                                                        .toString())
                                                || type.equals(QuestionDto.QuestionType.VIDEO
                                                        .toString())
                                                || type.equals(QuestionDto.QuestionType.GEO
                                                        .toString())
                                                || type.equals(QuestionDto.QuestionType.SCAN
                                                        .toString())
                                                || type.equals(QuestionDto.QuestionType.TRACK
                                                        .toString())
                                                || type.equals(QuestionDto.QuestionType.NUMBER
                                                        .toString()) || type
                                                    .equals(QuestionDto.QuestionType.OPTION
                                                            .toString()))
                                                || type.equals(QuestionDto.QuestionType.STRENGTH
                                                        .toString())) {
                                            rowError.append("Invalid question type. Must be either: FREE_TEXT, PHOTO, VIDEO, GEO, NUMBER, OPTION, SCAN, TRACK, NAME, STRENGTH\n");
                                        }
                                    }
                                    break;
                                case 7:
                                    if (QuestionType.OPTION.toString().equals(type)
                                            || QuestionType.STRENGTH.toString()
                                                    .equals(type)) {
                                        if (cell.getStringCellValue().trim()
                                                .length() == 0) {
                                            rowError.append("Options are missing\n");
                                        }
                                    }
                                    // TODO: validate language codes
                                    break;
                                case 8:
                                    // TODO: validate dependency
                                    break;
                                case 9:
                                    if (!validateBooleanField(cell)) {
                                        rowError.append("Allow Other must be either TRUE or FALSE\n");
                                    }
                                    break;
                                case 10:
                                    if (!validateBooleanField(cell)) {
                                        rowError.append("Allow Multiple must be either TRUE or FALSE\n");
                                    }
                                    break;
                                case 11:
                                    if (!validateBooleanField(cell)) {
                                        rowError.append("Manditory must be either TRUE or FALSE\n");
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            rowError.append(e.toString());
                        } finally {
                            if (rowError.toString().trim().length() > 0) {
                                errorMap.put(row.getRowNum() + 1, rowError
                                        .toString().trim());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inp != null) {
                try {
                    inp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return errorMap;
    }

    /**
     * validates a boolean field. We have to try reading it as both a boolean and a string column
     * because once we encounter 1 non-boolean, it changes the underlying model for the remainder of
     * the spreadsheet.
     *
     * @param cell
     * @return
     */
    private boolean validateBooleanField(Cell cell) {
        try {
            cell.getBooleanCellValue();
        } catch (Exception e) {
            try {
                if (cell.getStringCellValue().trim().length() > 0) {
                    if (!("TRUE".equalsIgnoreCase(cell.getStringCellValue()
                            .trim()) || "FALSE".equalsIgnoreCase(cell
                            .getStringCellValue().trim()))) {
                        return false;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

}
