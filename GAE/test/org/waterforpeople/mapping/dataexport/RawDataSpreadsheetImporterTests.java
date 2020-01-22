/*
 *  Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
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
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import com.gallatinsystems.framework.dataexport.applet.DataImporter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RawDataSpreadsheetImporterTests {

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
            row1.createCell(i + j).setCellValue((123000 + j) + "|Question" + j);
        }
        return sheet;
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

    private File createValidTestSpreadsheetWithData(String fileName, xyzzy questions) throws IOException {
        Workbook wb = new SXSSFWorkbook(10); //Only need a tiny window
        Sheet baseSheet = createSheet(wb, "Raw Data", "Metadata", false, false, 1); //Right name, right index for base
        Row row = baseSheet.createRow(2);

        Sheet groupSheet = createSheet(wb, "Group 2", "Metadata", false, true, 1); //Right name for group sheet

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
    public void testValidSheetsWithData() throws IOException {
        DataImporter dimp = new RawDataSpreadsheetImporter();

        //Check that a canonical file passes validation
        File file1 = createValidTestSpreadsheet("/tmp/valid1.xlsx");
        Map<Integer,String> errors = dimp.validate(file1);
        assertEquals(0, errors.size());

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
        new File("/tmp/invalid1.xlsx").delete();

    }

}
