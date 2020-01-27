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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;

import com.gallatinsystems.framework.dataexport.applet.DataImporter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RawDataSpreadsheetImporterTests {

    private static final long QID_START = 123000;
    private static final long IID_START = 4711;

    private Sheet createSheet(Workbook wb, String name, String topleft, boolean app, boolean rep, int questionColumns) {
        Sheet sheet = wb.createSheet(name);

        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue(topleft);

        Row row1 = sheet.createRow(1);
        int i = 0;
        row1.createCell(i++).setCellValue("Identifier");
        if (app) {row1.createCell(i++).setCellValue("Data approval status");}
        row1.createCell(i++).setCellValue("Display Name");
        if (rep) {row1.createCell(i++).setCellValue("Repeat no");}
        row1.createCell(i++).setCellValue("Device identifier");
        row1.createCell(i++).setCellValue("Instance");
        row1.createCell(i++).setCellValue("Submission Date");
        row1.createCell(i++).setCellValue("Submitter");
        row1.createCell(i++).setCellValue("Duration");
        row1.createCell(i++).setCellValue("Form version");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, i - 1));

        for (int j = 0; j < questionColumns; j++) {
            row1.createCell(i + j).setCellValue((QID_START + j) + "|Question" + j);
        }
        return sheet;
    }

    private void createDataRow(Sheet sheet, int rowIndex, int repIndex, boolean app, boolean rep, List<String> answers) {
        Row row2 = sheet.createRow(rowIndex);
        int i = 0;
        row2.createCell(i++).setCellValue("aaaa-bbbb-cccc");
        if (app) {row2.createCell(i++).setCellValue("Data approval status");}
        row2.createCell(i++).setCellValue("ADisplayName");
        if (rep) {row2.createCell(i++).setCellValue(repIndex);}
        row2.createCell(i++).setCellValue("ADeviceIdentifier");
        row2.createCell(i++).setCellValue(Long.toString(IID_START));
        row2.createCell(i++).setCellValue("27-01-2020 11:32:22 CET");
        row2.createCell(i++).setCellValue("Someone");
        row2.createCell(i++).setCellValue("00:11:22");
        row2.createCell(i++).setCellValue(17.0);

        for (String answer: answers) {
            row2.createCell(i++).setCellValue(answer);
        }
    }

    private File createValidTestSpreadsheet(String fileName) throws IOException {
        Workbook wb = new SXSSFWorkbook(10); //Only need a tiny window
        createSheet(wb, "Raw Data", "Metadata", false, false, 0); //Right name, right index for base
        createSheet(wb, "Group 2", "Metadata", false, true, 1); //Right name for group sheet
        createSheet(wb, "Foobar", "Not metadata", false, true, 0); //Should be completely ignored

        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.setActiveSheet(0);
        wb.write(fileOut);
        fileOut.close();

        return new File(fileName);
    }

    private File createValidTestSpreadsheetWithNonRepData(String fileName, List<String> answers) throws IOException {
        Workbook wb = new SXSSFWorkbook(10); //Only need a tiny window
        Sheet baseSheet = createSheet(wb, "Raw Data", "Metadata", false, false, 1); //Right name, right index for base
        createDataRow(baseSheet, 2, 0, false, false, answers);

        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.setActiveSheet(0);
        wb.write(fileOut);
        fileOut.close();

        return new File(fileName);
    }

    private File createValidTestSpreadsheetWithRepData(String fileName, List<String> answers) throws IOException {
        Workbook wb = new SXSSFWorkbook(10); //Only need a tiny window
        Sheet baseSheet = createSheet(wb, "Raw Data", "Metadata", false, false, 0); //Right name, right index for base
        createDataRow(baseSheet, 2, 0, false, false, new ArrayList<String>()); //no data on the base sheet

        Sheet groupSheet = createSheet(wb, "Group 1", "Metadata", false, true, 1); //Right name for a RQG sheet
        createDataRow(groupSheet, 2, 1, false, true, answers);
        //Make a gap in the repeat index
        createDataRow(groupSheet, 3, 3, false, true, answers);

        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.setActiveSheet(0);
        wb.write(fileOut);
        fileOut.close();

        return new File(fileName);
    }

    private File createNoQuestionsSpreadsheet(String fileName) throws IOException {
        Workbook wb = new SXSSFWorkbook(10); //Only need a tiny window
        createSheet(wb, "Raw Data", "Metadata", false, false, 0);
        createSheet(wb, "Group 2", "Metadata", false, true, 0);

        FileOutputStream fileOut = new FileOutputStream(fileName);
        wb.setActiveSheet(0);
        wb.write(fileOut);
        fileOut.close();

        return new File(fileName);
    }

    @Test
    public void testValidSheets() throws IOException {
        DataImporter dimp = new RawDataSpreadsheetImporter();

        //Check that a canonical file passes validation
        File file1 = createValidTestSpreadsheet("/tmp/valid1.xlsx");
        Map<Integer,String> errors = dimp.validate(file1);
        assertEquals(0, errors.size());

    }

    @Test
    public void testValidBaseSheetWithData() throws Exception {
        RawDataSpreadsheetImporter dimp = new RawDataSpreadsheetImporter();

        List<String> dataList = new ArrayList<>();
        dataList.add("testData");
        File file1 = createValidTestSpreadsheetWithNonRepData("/tmp/valid2.xlsx", dataList);

        //Import it to check that data is found
        InputStream stream = new PushbackInputStream(new FileInputStream(file1));
        Workbook wb = WorkbookFactory.create(stream);
        assertEquals(wb.getNumberOfSheets(), 1);

        final int headerRowIndex = 1; //Only test modern (split) sheets

        Sheet sheet = wb.getSheetAt(0);
        Map<Integer, Long> headerMap = RawDataSpreadsheetImporter.processHeader(sheet, headerRowIndex);
        assertEquals(1, headerMap.size()); //one data

        Map<Sheet, Map<Integer, Long>> sheetMap = new HashMap<>();
        sheetMap.put(sheet, headerMap);

        //Mock up a DTO
        QuestionDto dto = new QuestionDto();
        dto.setType(QuestionType.FREE_TEXT);
        dto.setText("Question0");
        dto.setKeyId(QID_START);

        //Put it in a map
        Map<Long, QuestionDto> questionIdToQuestionDto = new HashMap<>();
        questionIdToQuestionDto.put(QID_START, dto);
        //Make an empty map for options
        Map<Long, List<QuestionOptionDto>> optionNodes = new HashMap<>();

        List<InstanceData> instanceDataList = dimp.parseSplitSheets(sheet,
                sheetMap,
                questionIdToQuestionDto,
                optionNodes,
                headerRowIndex);
        assertNotNull(instanceDataList);
        assertEquals(1, instanceDataList.size());
        assertNotNull(instanceDataList.get(0));
        assertNotNull(instanceDataList.get(0).responseMap);
        assertNotNull(instanceDataList.get(0).responseMap.get(QID_START));
        assertNotNull(instanceDataList.get(0).responseMap.get(QID_START).get(0L));
        assertEquals("testData", instanceDataList.get(0).responseMap.get(QID_START).get(0L));
    }


    @Test
    public void testValidSheetsWithRepeatData() throws Exception {
        RawDataSpreadsheetImporter dimp = new RawDataSpreadsheetImporter();

        List<String> dataList = new ArrayList<>();
        dataList.add("testDataSplit");
        File file1 = createValidTestSpreadsheetWithRepData("/tmp/valid3.xlsx", dataList);

        //Import it to check that data is found
        InputStream stream = new PushbackInputStream(new FileInputStream(file1));
        Workbook wb = WorkbookFactory.create(stream);
        assertEquals(2, wb.getNumberOfSheets());

        final int headerRowIndex = 1; //Only test modern (split) sheets

        Map<Sheet, Map<Integer, Long>> sheetMap = new HashMap<>();

        Sheet baseSheet = wb.getSheetAt(0);
        assertNotNull(baseSheet);
        Map<Integer, Long> headerMap = RawDataSpreadsheetImporter.processHeader(baseSheet, headerRowIndex);
        assertEquals(0, headerMap.size()); //no data here
        sheetMap.put(baseSheet, headerMap);

        Sheet groupSheet = wb.getSheetAt(1);
        assertNotNull(groupSheet);
        headerMap = RawDataSpreadsheetImporter.processHeader(groupSheet, headerRowIndex);
        assertEquals(1, headerMap.size()); //one data column
        sheetMap.put(groupSheet, headerMap);

        //Mock up a DTO
        QuestionDto dto = new QuestionDto();
        dto.setType(QuestionType.FREE_TEXT);
        dto.setText("Question0");
        dto.setKeyId(QID_START);

        //Put it in a map
        Map<Long, QuestionDto> questionIdToQuestionDto = new HashMap<>();
        questionIdToQuestionDto.put(QID_START, dto);
        //Make an empty map for options
        Map<Long, List<QuestionOptionDto>> optionNodes = new HashMap<>();

        List<InstanceData> instanceDataList = dimp.parseSplitSheets(baseSheet,
                sheetMap,
                questionIdToQuestionDto,
                optionNodes,
                headerRowIndex);
        assertNotNull(instanceDataList);
        assertEquals(1, instanceDataList.size());
        assertNotNull(instanceDataList.get(0));
        assertNotNull(instanceDataList.get(0).responseMap);
        assertNotNull(instanceDataList.get(0).responseMap.get(QID_START));
        assertNotNull(instanceDataList.get(0).responseMap.get(QID_START).get(0L));
        assertEquals("testDataSplit", instanceDataList.get(0).responseMap.get(QID_START).get(0L));
        assertEquals("testDataSplit", instanceDataList.get(0).responseMap.get(QID_START).get(2L));
        assertNull(instanceDataList.get(0).responseMap.get(QID_START).get(1L));
        assertNull(instanceDataList.get(0).responseMap.get(QID_START).get(3L));
    }

    @Test
    public void testNoQuestionColumns() throws IOException {
        DataImporter dimp = new RawDataSpreadsheetImporter();

        //Check that a file without any questions fail on "row" -11
        File file = createNoQuestionsSpreadsheet("/tmp/invalid1.xlsx");
        Map<Integer,String> errors = dimp.validate(file);
        assertEquals(1, errors.size());
        String err1 = errors.get(-11);
        assertEquals("No question columns found on any sheet.", err1);

    }

    @AfterAll
    public static void removeTestFiles() {
        new File("/tmp/valid1.xlsx").delete();
        new File("/tmp/valid2.xlsx").delete();
        new File("/tmp/valid3.xlsx").delete();
        new File("/tmp/invalid1.xlsx").delete();

    }

}
