/*
 *  Copyright (C) 2010-2015, 2018 Stichting Akvo (Akvo Foundation)
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.framework.dataexport.applet.DataExporter;

/**
 * Data exporter to write excel files containing the survey questions
 *
 */
public class SurveyFormExporterFull implements DataExporter {

    private static final Logger log = Logger.getLogger(SurveyFormExporterFull.class);

    private static final int COL_WIDTH = 10000;
    private static final String LANG_DELIM = " / ";
    private static final String SURVEY_ID_KEY = "surveyId";
    private Map<Long, Long> idToNumberMap;
    private List<QuestionGroupDto> groupList;
    private String surveyTitle;
    private Map<QuestionGroupDto, List<QuestionDto>> questionMap;

    @Override
    public void export(Map<String, String> criteria, File fileName, String serverBase, Map<String, String> options) {
        try {
            String surveyId = criteria.get(SURVEY_ID_KEY);
            String apiKey = criteria.get("apiKey");
            populateQuestionMap(surveyId, serverBase, apiKey);
            List<SurveyDto> surveys = BulkDataServiceClient.fetchSurvey(Long.parseLong(surveyId), serverBase, apiKey);
            if (surveys == null || surveys.isEmpty()) {
                surveyTitle = "";
            } else {
                SurveyDto surveyDto = surveys.get(0);
                surveyTitle = String.format("%s (v. %s)", surveyDto.getName(), surveyDto.getVersion());
            }

            writeSurvey(surveyTitle, fileName, groupList, questionMap);
        } catch (Exception e) {
            log.error("Could not write survey", e);
        }
    }

    /**
     * Calls the server to fetch all question groups for the survey and then will
     * iterate over all the questions for the group and call loadQuestionDetails for
     * each one to get the fully hydrated object. This will populate a number of
     * member variables to store the results.
     */
    private void populateQuestionMap(String surveyId, String serverBase, String apiKey) throws Exception {
        groupList = BulkDataServiceClient.fetchQuestionGroups(serverBase, surveyId, apiKey);
        questionMap = new HashMap<QuestionGroupDto, List<QuestionDto>>();
        idToNumberMap = new HashMap<Long, Long>();
        if (groupList != null) {
            Long count = 1L;
            for (QuestionGroupDto group : groupList) {
                List<QuestionDto> questions = BulkDataServiceClient.fetchQuestions(serverBase, group.getKeyId(),
                        apiKey);
                if (questions != null) {
                    List<QuestionDto> fullQuestions = new ArrayList<QuestionDto>();
                    for (QuestionDto q : questions) {
                        QuestionDto fullQ = BulkDataServiceClient.loadQuestionDetails(serverBase, q.getKeyId(), apiKey);
                        if (fullQ != null) {
                            fullQuestions.add(fullQ);
                            idToNumberMap.put(fullQ.getKeyId(), count++);
                        }
                    }
                    questionMap.put(group, fullQuestions);
                }
            }
        }
    }

    /**
     * Writes the survey as an XLS document
     */
    private void writeSurvey(String title, File fileName, List<QuestionGroupDto> groupList,
            Map<QuestionGroupDto, List<QuestionDto>> questions) throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();

        HSSFCellStyle headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        HSSFFont headerFont = wb.createFont();
        headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headerStyle.setFont(headerFont);

        HSSFCellStyle questionStyle = wb.createCellStyle();
        questionStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
        questionStyle.setWrapText(true);

        HSSFCellStyle depStyle = wb.createCellStyle();
        depStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        HSSFFont depFont = wb.createFont();
        depFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        depFont.setItalic(true);
        depStyle.setFont(depFont);

        final int startRow = createHeader(sheet, 0, headerStyle);

        int count = 0; // running count of all questions
        if (questions != null) {
            // So we can output dependent question text
            Map<Long, String> qTextFromId = new HashMap<>();
            for (int i = 0; i < groupList.size(); i++) {
                for (QuestionDto q : questions.get(groupList.get(i))) {
                    qTextFromId.put(q.getKeyId(), q.getText());
                }
            }
            for (int i = 0; i < groupList.size(); i++) {
                int firstRowInGroup = startRow + count;
                for (QuestionDto q : questions.get(groupList.get(i))) {
                    // create the row
                    String optionsList = null; //TODO
                    int r = startRow + count;
                    count++;
                    HSSFRow row = sheet.createRow(r);
                    if (r == firstRowInGroup) { // only once per group
                        createCell(row, 0, Long.valueOf(i), headerStyle);
                        createCell(row, 1, groupList.get(i).getDisplayName(), headerStyle);
                        createCell(row, 2, groupList.get(i).getRepeatable(), headerStyle);
                    }
                    createCell(row, 3, Long.valueOf(q.getOrder()), headerStyle);
                    createCell(row, 4, Long.valueOf(count), headerStyle);
                    createCell(row, 5, q.getText(), headerStyle); //TODO translations
                    // Scrolling part(?)
                    createCell(row, 6, q.getTip(), null);
                    createCell(row, 7, q.getVariableName(), null);
                    createCell(row, 8, q.getType().toString(), null);
                    createCell(row, 9, q.getMandatoryFlag(), null);
                    createCell(row, 10, q.getLocaleNameFlag(), null);
                    // Text and number
                    createCell(row, 11, q.getAllowExternalSources(), null);
                    createCell(row, 12, q.getRequireDoubleEntry(), null);
                    createCell(row, 13, q.getAllowSign(), null);
                    createCell(row, 14, q.getAllowDecimal(), null);
                    createCell(row, 15, q.getMinVal(), null);
                    createCell(row, 16, q.getMaxVal(), null);
                    createCell(row, 17, optionsList, null);
                    createCell(row, 18, q.getAllowMultipleFlag(), null);
                    createCell(row, 19, q.getAllowOtherFlag(), null);
                    // GEO
                    createCell(row, 20, q.getGeoLocked(), null);
                    // CASCADE
                    createCell(row, 21, q.getCascadeResourceId(), null);// TODO: name
                    // Dependency
                    createCell(row, 22, q.getDependentFlag(), null);
                    createCell(row, 23, qTextFromId.get(q.getDependentQuestionId()), null);
                    createCell(row, 24, q.getDependentQuestionAnswer(), null);
                    // TODO: geoshapes

                    /*
                     * createCell(tempRow, 0, (count++) + ". " + formText(q.getText(),
                     * q.getTranslationMap()), questionStyle); if (q.getOptionContainerDto() != null
                     * && q.getOptionContainerDto().getOptionsList() != null) { for
                     * (QuestionOptionDto opt : q.getOptionContainerDto() .getOptionsList()) {
                     * tempRow = sheet.createRow(curRow++); createCell(tempRow, 1,
                     * formText(opt.getText(), opt .getTranslationMap()) + SMALL_BLANK, null); }
                     * sheet.addMergedRegion(new CellRangeAddress( questionStartRow, curRow - 1, 0,
                     * 0)); } else { createCell(tempRow, 1, BLANK, null); }
                     */
                }
                // all rows created; merge all-group cells vertically
                sheet.addMergedRegion(new CellRangeAddress(firstRowInGroup, startRow + count - 1, 0, 0));
                sheet.addMergedRegion(new CellRangeAddress(firstRowInGroup, startRow + count - 1, 1, 1));
                sheet.addMergedRegion(new CellRangeAddress(firstRowInGroup, startRow + count - 1, 2, 2));
            }
        }

        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.write(fileOut);
        fileOut.close();
    }

    private int createHeader(HSSFSheet sheet, int startRow, HSSFCellStyle style) {
        int r = startRow, c = 0;
        HSSFRow row = sheet.createRow(r++);
        createCell(row, c++, "Form version", style);
        createCell(row, c++, 1.0, style); // TODO: from where?
        createCell(row, c++, "Languages", style);
        createCell(row, c++, "EN/FR/ES", style); // TODO: from where?

        row = sheet.createRow(r++);
        c = 0;
        createCell(row, c, "Group no", style);
        createCell(row, ++c, "Group title", style);
        createCell(row, ++c, "Repeatable", style);
        createCell(row, ++c, "Question # in group", style);
        createCell(row, ++c, "Question #", style);
        createCell(row, ++c, "Question text", style);
        createCell(row, ++c, "Question help", style);
        createCell(row, ++c, "Variable name", style);
        createCell(row, ++c, "Question type", style);
        createCell(row, ++c, "Mandatory", style);
        createCell(row, ++c, "Use for DP name", style);
        createCellBlock(row, ++c, "Text and numbers", style, 6); // 6 wide
        createCellBlock(row, c += 6, "Options", style, 3); // 3 wide
        createCell(row, c += 3, "Geolocation", style);
        createCell(row, ++c, "Cascade", style);
        createCellBlock(row, ++c, "Dependency", style, 3); // 3 wide

        row = sheet.createRow(r++);
        c = 10;
        // Text and numbers
        createCell(row, ++c, "Allow external source", style);
        createCell(row, ++c, "Confirmation required", style);
        createCell(row, ++c, "Allow unit", style);
        createCell(row, ++c, "Allow decimal", style);
        createCell(row, ++c, "Max value", style);
        createCell(row, ++c, "Min value", style);
        // OPTION
        createCell(row, ++c, "Selections", style);
        createCell(row, ++c, "Allow several", style);
        createCell(row, ++c, "Allow other", style);
        // GEO
        createCell(row, ++c, "Disable manual entry", style);// current?
        // CASCADE
        createCell(row, ++c, "Resource", style);
        // Dependency
        createCell(row, ++c, "Dependent", style);
        createCell(row, ++c, "Question", style);
        createCell(row, ++c, "Answer(s)", style);
        // TODO: geoshapes

        // set these (3) rows non-scrolling
        // set the first 6 column non-scrolling
        sheet.createFreezePane(6, 3);

        return r;
    }

    /**
     * creates a cell in the row passed in and sets the style and value (if
     * non-null)
     */
    private HSSFCell createCell(HSSFRow row, int col, String value, HSSFCellStyle style) {
        HSSFCell cell = row.createCell(col);
        if (style != null) {
            cell.setCellStyle(style);
        }
        if (value != null) {
            cell.setCellValue(value);
        }
        return cell;
    }

    /**
     * creates a cell in the row passed in and sets the style and value (if
     * non-null)
     */
    private HSSFCell createCell(HSSFRow row, int col, Double value, HSSFCellStyle style) {
        HSSFCell cell = row.createCell(col);
        if (style != null) {
            cell.setCellStyle(style);
        }
        if (value != null) {
            cell.setCellValue(value);
        }
        return cell;
    }

    /**
     * creates a cell in the row passed in and sets the style and value (if
     * non-null)
     */
    private HSSFCell createCell(HSSFRow row, int col, Long value, HSSFCellStyle style) {
        HSSFCell cell = row.createCell(col);
        if (style != null) {
            cell.setCellStyle(style);
        }
        if (value != null) {
            cell.setCellValue(value);
        }
        return cell;
    }

    /**
     * creates a cell in the row passed in and sets the style and value (if
     * non-null)
     */
    private HSSFCell createCell(HSSFRow row, int col, Boolean value, HSSFCellStyle style) {
        HSSFCell cell = row.createCell(col);
        if (style != null) {
            cell.setCellStyle(style);
        }
        if (value != null && value) {
            cell.setCellValue("Yes");
        }
        return cell;
    }

    /**
     * creates a cell in the row passed in and sets the style and value (if
     * non-null)
     */
    private HSSFCell createCellBlock(HSSFRow row, int col, String value, HSSFCellStyle style, int width) {
        HSSFCell cell = row.createCell(col);
        if (width > 1) {
            row.getSheet().addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), col, col + width - 1));
        }
        if (style != null) {
            cell.setCellStyle(style);
        }
        if (value != null) {
            cell.setCellValue(value);
        }
        return cell;
    }

    /**
     * forms a string that has all languages in the translation map delimited by the
     * LANG_DELIM
     */
    private String formText(String text, Map<String, TranslationDto> translationMap) {

        StringBuilder buff = new StringBuilder();
        buff.append(text);
        if (translationMap != null) {
            TreeMap<String, TranslationDto> sortedMap = null;
            if (translationMap instanceof TreeMap) {
                sortedMap = (TreeMap<String, TranslationDto>) translationMap;
            } else {
                sortedMap = new TreeMap<String, TranslationDto>(translationMap);
            }
            for (Entry<String, TranslationDto> trans : sortedMap.entrySet()) {
                if (trans.getValue() != null && trans.getValue().getText() != null) {
                    if (!trans.getValue().getText().trim().equalsIgnoreCase("null")) {
                        buff.append(LANG_DELIM);
                        buff.append(trans.getValue().getText());
                    }
                }
            }
        }
        return buff.toString();
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

        SurveyFormExporterFull exporter = new SurveyFormExporterFull();
        Map<String, String> criteria = new HashMap<String, String>();
        Map<String, String> options = new HashMap<String, String>();

        criteria.put(SurveyRestRequest.SURVEY_ID_PARAM, args[2]);
        criteria.put("apiKey", args[3]);
        exporter.export(criteria, new File(args[0]), args[1], options);
    }
}
