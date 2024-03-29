/*
 *  Copyright (C) 2019-2020 Stichting Akvo (Akvo Foundation)
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.framework.dataexport.applet.DataExporter;
import com.gallatinsystems.survey.domain.SurveyGroup.ProjectType;

/**
 * Data exporter to write excel files containing survey statistics
 */
public class StatisticsExporter implements DataExporter {

    private static final Logger log = Logger.getLogger(StatisticsExporter.class.getName());

    private static final String INSTANCE_COUNT_SHEET_NAME = "Form submissions";
    private static final String SURVEY_STATS_SHEET_NAME = "Survey statistics";
    private static final String SURVEY_STATS_HEADER1 = "Items";
    private static final String SURVEY_STATS_HEADER2 = "Count";

    private static final String SURVEY_HEADER = "Survey";
    private static final String FORM_HEADER = "Form name";
    private static final String FORM_ID_HEADER = "Form Id";
    private static final String COUNT_HEADER = "Submissions";

    private static final String SURVEY_GROUP_COUNT_LABEL = "Folders";
    private static final String SURVEY_COUNT_LABEL = "Surveys";
    private static final String FORM_COUNT_LABEL = "Total Forms";
    private static final String PUBLISHED_COUNT_LABEL = "Published Forms";

    private static final int COL_WIDTH = 10000;
    private static final String FROM_OPT = "from";
    private static final String TO_OPT = "to";

    private static final String PATH_DELIM = " / ";
    private static final SimpleDateFormat idf = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat odf = new SimpleDateFormat("yyyy-MM-dd");  //Could be different, if desired

    @Override
    public void export(Map<String, String> criteria,
            File fileName,
            String serverBase,
            Map<String, String> options) {
        try {
            //get parameters
            String apiKey = criteria.get("apiKey");
            String from = options.get(FROM_OPT);
            String to = options.get(TO_OPT);

            //Get forms, surveys and instance counts data
            List<SurveyGroupDto> groupList = BulkDataServiceClient.fetchSurveyGroups(serverBase, apiKey);
            Map<Long, SurveyGroupDto> groupMap = new HashMap<>();
            Map<SurveyGroupDto, List<SurveyDto>> formMap = new HashMap<>();
            Map<SurveyDto, Long> instanceCounts = new HashMap<>();

            for (SurveyGroupDto sg: groupList) {
                groupMap.put(sg.getKeyId(), sg);
                if (ProjectType.PROJECT.equals(sg.getProjectType())) {
                    List<SurveyDto> formList = BulkDataServiceClient.fetchSurveys(sg.getKeyId(), serverBase, apiKey);
                    formMap.put(sg, formList);
                    for (SurveyDto form: formList) {
                        Long c = BulkDataServiceClient.fetchInstanceCount(
                                form.getKeyId().toString(), serverBase, apiKey, from, to);
                        instanceCounts.put(form, c);
                    }
                }
            }

            log.finest("Surveys and Groups: " + groupMap.size());
            log.finest("Surveys: " + formMap.size());
            log.finest("Instance Counts: " + instanceCounts.size());
            String title = "Form submissions";
            if (from != null && !"".equals(from.trim())) {
                Date fromDate = idf.parse(from);  //Removes any ISO8601 "time" part
                title += " from " + odf.format(fromDate);
            }
            if (to != null && !"".equals(to.trim())) {
                Date toDate = idf.parse(to);
                title += " to " + odf.format(toDate);
            }
            writeStats(title, fileName, groupMap, formMap, instanceCounts);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not write stats", e);
        }
    }


    /**
     * Writes the stats in an XLSX document
     */
    private void writeStats(String title,
            File fileName,
            Map<Long, SurveyGroupDto> groupMap,
            Map<SurveyGroupDto, List<SurveyDto>> formMap,
            Map<SurveyDto, Long> instanceCounts) throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook();

        XSSFCellStyle headerStyle = wb.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        XSSFFont headerFont = wb.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        XSSFCellStyle questionStyle = wb.createCellStyle();
        questionStyle.setVerticalAlignment(VerticalAlignment.TOP);
        questionStyle.setWrapText(true);

        writeInstanceSheet(title, groupMap, formMap, instanceCounts, wb, headerStyle, questionStyle);
        writeSurveyStatsSheet(SURVEY_STATS_HEADER1, SURVEY_STATS_HEADER2, groupMap, formMap, wb, headerStyle, questionStyle);

        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.write(fileOut);
        fileOut.close();
    }

    /**
     * Writes the sheet with the list of forms and their instance counts
     */
    private void writeInstanceSheet(String title,
            Map<Long, SurveyGroupDto> groupMap,
            Map<SurveyGroupDto, List<SurveyDto>> formMap,
            Map<SurveyDto, Long> instanceCounts,
            XSSFWorkbook wb,
            XSSFCellStyle headerStyle,
            XSSFCellStyle questionStyle) {

        XSSFSheet sheet = wb.createSheet(INSTANCE_COUNT_SHEET_NAME);

        sheet.setColumnWidth(0, COL_WIDTH);
        sheet.setColumnWidth(1, COL_WIDTH);

        int curRow = 0;
        XSSFRow row = sheet.createRow(curRow++);
        sheet.addMergedRegion(new CellRangeAddress(curRow - 1, curRow - 1, 0, 3));
        createCell(row, 0, title, headerStyle);
        row = sheet.createRow(curRow++);
        createCell(row, 0, SURVEY_HEADER, headerStyle);
        createCell(row, 1, FORM_HEADER, headerStyle);
        createCell(row, 2, FORM_ID_HEADER, headerStyle);
        createCell(row, 3, COUNT_HEADER, headerStyle);

        //Loop over surveys, saving the generated pathname as the key in a sorted map
        TreeMap<String, SurveyGroupDto> surveyMap = new TreeMap<>();
        for (SurveyGroupDto s: groupMap.values()) {
            if (ProjectType.PROJECT.equals(s.getProjectType())) {
                String name = s.getCode();
                SurveyGroupDto parent = groupMap.get(s.getParentId());
                int loops = 0;
                while (parent != null && loops < 100) { //No infinite loops!
                    loops++;
                    name = parent.getCode() + PATH_DELIM + name;
                    parent = groupMap.get(parent.getParentId());
                }
                if (loops == 100) {
                    log.severe("Infinite Survey Group loop for " + s.getKeyId());
                    name = "### structure error - infinite loop ###";
                }
                surveyMap.put(name, s);
            }
        }

        //Loop over the survey path->dto map
        for (Entry<String, SurveyGroupDto> e: surveyMap.entrySet()) {
            List<SurveyDto> formList = formMap.get(e.getValue());
            for (SurveyDto form: formList) {
                row = sheet.createRow(curRow++);
                createCell(row, 0, e.getKey(), questionStyle);
                createCell(row, 1, form.getCode(), questionStyle);
                createCell(row, 2, form.getKeyId(), questionStyle);
                createCell(row, 3, instanceCounts.get(form), questionStyle);
            }
        }

    }

    /**
     * Writes a sheet with counts of survey folders, surveys, forms, etc
     */
    private void writeSurveyStatsSheet(String title1,
            String title2,
            Map<Long, SurveyGroupDto> groupMap,
            Map<SurveyGroupDto, List<SurveyDto>> formMap,
            XSSFWorkbook wb,
            XSSFCellStyle headerStyle,
            XSSFCellStyle bodyStyle) {

        XSSFSheet sheet = wb.createSheet(SURVEY_STATS_SHEET_NAME);

        //Sum up all the forms
        long formCount = 0;
        long publishedCount = 0;
        for (List<SurveyDto> formList : formMap.values()) {
            formCount += formList.size();
            for (SurveyDto form: formList) {
                if ("PUBLISHED".equals(form.getStatus())) {
                    publishedCount++;
                }
            }
        }

        sheet.setColumnWidth(0, COL_WIDTH);
        sheet.setColumnWidth(1, COL_WIDTH);

        int curRow = 0;
        XSSFRow row = sheet.createRow(curRow++);
        createCell(row, 0, title1, headerStyle);
        createCell(row, 1, title2, headerStyle);
        row = sheet.createRow(curRow++);
        createCell(row, 0, SURVEY_GROUP_COUNT_LABEL, bodyStyle);
        createCell(row, 1, Long.valueOf((groupMap.size() - formMap.size())), bodyStyle);
        row = sheet.createRow(curRow++);
        createCell(row, 0, SURVEY_COUNT_LABEL, bodyStyle);
        createCell(row, 1, Long.valueOf(formMap.size()), bodyStyle);
        row = sheet.createRow(curRow++);
        createCell(row, 0, FORM_COUNT_LABEL, bodyStyle);
        createCell(row, 1, formCount, bodyStyle);
        row = sheet.createRow(curRow++);
        createCell(row, 0, PUBLISHED_COUNT_LABEL, bodyStyle);
        createCell(row, 1, publishedCount, bodyStyle);
    }

    /**
     * creates a cell in the row passed in and sets the style and value (if non-null)
     */
    private XSSFCell createCell(XSSFRow row, int col, String value, XSSFCellStyle headerStyle) {
        XSSFCell cell = row.createCell(col);
        if (headerStyle != null) {
            cell.setCellStyle(headerStyle);
        }
        if (value != null) {
            cell.setCellValue(value);
        }
        return cell;
    }

    /**
     * creates a cell in the row passed in and sets the style and value (if non-null)
     */
    private XSSFCell createCell(XSSFRow row, int col, Double value, XSSFCellStyle style) {
        XSSFCell cell = row.createCell(col);
        if (style != null) {
            cell.setCellStyle(style);
        }
        if (value != null) {
            cell.setCellValue(value);
        }
        return cell;
    }

    /**
     * creates a cell in the row passed in and sets the style and value (if non-null)
     */
    private XSSFCell createCell(XSSFRow row, int col, Long value, XSSFCellStyle style) {
        XSSFCell cell = row.createCell(col);
        if (style != null) {
            cell.setCellStyle(style);
        }
        if (value != null) {
            cell.setCellValue(value);
        }
        return cell;
    }

    /**
     * creates a cell in the row passed in and sets the style and value (if non-null)
     */
    private XSSFCell createCell(XSSFRow row, int col, Boolean value, XSSFCellStyle style) {
        XSSFCell cell = row.createCell(col);
        if (style != null) {
            cell.setCellStyle(style);
        }
        if (value != null && value) {
            cell.setCellValue("Yes");
        }
        return cell;
    }

    /**
     * creates a cell in the row passed in, makes it 'width' columns wide
     *  and sets the style and value (if non-null)
     */
    private XSSFCell createCellBlock(XSSFRow row, int col, String value, XSSFCellStyle style, int width) {
        XSSFCell cell = row.createCell(col);
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




    // This main() method is only used for debugging;
    // when deployed on server, export() is called from Clojure code
    public static void main(String[] args) {

        StatisticsExporter exporter = new StatisticsExporter();
        Map<String, String> criteria = new HashMap<String, String>();
        Map<String, String> options = new HashMap<String, String>();

        criteria.put("apiKey", args[2]);
        if (args.length > 3) options.put(FROM_OPT, args[3]);
        if (args.length > 4) options.put(TO_OPT, args[4]);

        exporter.export(criteria, new File(args[0]), args[1], options);
    }
}
