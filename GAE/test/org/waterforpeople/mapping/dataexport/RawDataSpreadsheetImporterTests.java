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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
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

import static org.junit.jupiter.api.Assertions.*;

class RawDataSpreadsheetImporterTests {

    private static final long QID_START = 123000;
    private static final long IID_START = 4711;

    private static final String VALID_GEOSHAPE = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"stroke\":\"#555555\",\"stroke-width\":2,\"stroke-opacity\":1,\"fill\":\"#555555\",\"fill-opacity\":0.5},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[16.510412693023696,45.859815603122634],[16.589891910552993,45.787322451651534],[16.601865291595477,45.78857932474319],[16.60362482070925,45.78911797596399],[16.603882312774676,45.78965662197942],[16.605427265167254,45.79157176565478],[16.605684757232684,45.792110387956804],[16.605513095855727,45.794504201876144],[16.604740619659438,45.79552154165609],[16.602938175201434,45.796179810440634],[16.60242319107057,45.79665854649084],[16.600964069366473,45.79977023056605],[16.600620746612563,45.802762070613],[16.60070657730104,45.805634085911095],[16.600878238677996,45.80736919007074],[16.600964069366473,45.80802731889003],[16.601564884185805,45.80946320932351],[16.601307392120376,45.81155714985443],[16.601307392120376,45.812693827461615],[16.600534915924086,45.81406977457891],[16.595385074615493,45.81502693513234],[16.591866016387954,45.81406977457891],[16.588604450225844,45.814668001852674],[16.585428714752215,45.81634300403169],[16.58499956130983,45.81771886098886],[16.586029529571547,45.8203508404454],[16.590321063995376,45.82142752345573],[16.59246683120729,45.82178641316565],[16.595985889434832,45.82256399960079],[16.597187519073504,45.824059327605525],[16.597616672515887,45.82561442612487],[16.597101688385028,45.82693024478798],[16.59667253494264,45.82770775937849],[16.595900058746356,45.8284852631088],[16.59555673599245,45.82890391446523],[16.595385074615493,45.8300402379888],[16.59607172012331,45.83111673361205],[16.596758365631118,45.83207360113143],[16.599161624908465,45.83279124097508],[16.599934101104754,45.83548230797369],[16.599590778350848,45.83709688571111],[16.598389148712172,45.83918978712909],[16.597187519073504,45.84038569545306],[16.59444093704225,45.84289701926595],[16.59358263015749,45.84337535380672],[16.588776111602797,45.84373410201337],[16.587917804718032,45.84247847316903],[16.58800363540651,45.841701164920956],[16.58448457717897,45.840266105777346],[16.581909656524676,45.84038569545306],[16.58002138137819,45.84313618705038],[16.579506397247332,45.84433201055031],[16.578476428985613,45.84624527468546],[16.575043201446547,45.848875905428045],[16.57349824905397,45.84995203669283],[16.570236682891863,45.8512672799642],[16.567318439483657,45.848875905428045],[16.561996936798113,45.85078901328227],[16.558392047882094,45.85509326536483],[16.55633211135866,45.85628883182431],[16.552212238311785,45.856647496749865],[16.547920703887954,45.85951673287406],[16.548950672149672,45.862505363067775],[16.544401645660418,45.86441800208355],[16.538565158844012,45.86591220553003],[16.53453111648561,45.86579267073226],[16.52337312698366,45.86352146073798],[16.520626544952407,45.86035356552167],[16.516678333282485,45.85921786101903],[16.510412693023696,45.859815603122634]]]}}]}";

    private static final String INVALID_GEOSHAPE = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":{\"stroke\":\"#555555\",\"stroke-width\":2,\"stroke-opacity\":1,\"fill\":\"#555555\",\"fill-opacity\":0.5},\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[16.510412693023696,45.859815603122634],[16.589891910552993,45.787322451651534],[16.601865291595477,45.78857932474319],[16.60362482070925,45.78911797596399],[16.603882312774676,45.78965662197942],[16.605427265167254,45.79157176565478],[16.605684757232684,45.792110387956804],[16.605513095855727,45.794504201876144],[16.604740619659438,45.79552154165609],[16.602938175201434,45.796179810440634],[16.60242319107057,45.79665854649084],[16.600964069366473,45.79977023056605],[16.600620746612563,45.802762070613],[16.60070657730104,45.805634085911095],[16.600878238677996,45.80736919007074],[16.600964069366473,45.80802731889003],[16.601564884185805,45.80946320932351],[16.601307392120376,45.81155714985443],[16.601307392120376,45.812693827461615],[16.600534915924086,45.81406977457891],[16.595385074615493,45.81502693513234],[16.591866016387954,45.81406977457891],[16.588604450225844,45.814668001852674],[16.585428714752215,45.81634300403169],[16.58499956130983,45.81771886098886],[16.586029529571547,45.8203508404454],[16.590321063995376,45.82142752345573],[16.59246683120729,45.82178641316565],[16.595985889434832,45.82256399960079],[16.597187519073504,45.824059327605525],[16.597616672515887,45.82561442612487],[16.597101688385028,45.82693024478798],[16.59667253494264,45.82770775937849],[16.595900058746356,45.8284852631088],[16.59555673599245,45.82890391446523],[16.595385074615493,45.8300402379888],[16.59607172012331,45.83111673361205],[16.596758365631118,45.83207360113143],[16.599161624908465,45.83279124097508],[16.599934101104754,45.83548230797369],[16.599590778350848,45.83709688571111],[16.598389148712172,45.83918978712909],[16.597187519073504,45.84038569545306],[16.59444093704225,45.84289701926595],[16.59358263015749,45.84337535380672],[16.588776111602797,45.84373410201337],[16.587917804718032,45.84247847316903],[16.58800363540651,45.841701164920956],[16.58448457717897,45.840266105777346],[16.581909656524676,45.84038569545306],[16.58002138137819,45.84313618705038],[16.579506397247332,45.84433201055031],[16.578476428985613,45.84624527468546],[16.575043201446547,45.848875905428045],[16.57349824905397,45.84995203669283],[16.570236682891863,45.8512672799642],[16.567318439483657,45.848875905428045],[16.561996936798113,45.85078901328227],[16.558392047882094,45.85509326536483],[16.55633211135866,45.85628883182431],[16.552212238311785,45.856647496749865],[16.547920703887954,45.85951673287406],[16.548950672149672,45.862505363067775],[16.544401645660418,45.86441800208355],[16.538565158844012,45.86591220553003],[16.53453111648561,45.86579267073226]";

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

    private File createInvalidGeoColumnSpreadsheet(String fileName) throws IOException {
        Workbook wb = new SXSSFWorkbook(10);
        Sheet sheet = createSheet(wb, "Raw Data", "Metadata", false, true, 1);
        Row row1 = sheet.getRow(1);
        row1.createCell(row1.getLastCellNum(), Cell.CELL_TYPE_STRING).setCellValue("9999|Latitude");
        row1.createCell(row1.getLastCellNum(), Cell.CELL_TYPE_STRING).setCellValue("- - GEOLON - -");

        wb.setActiveSheet(0);

        FileOutputStream out = new FileOutputStream(fileName);
        wb.write(out);
        out.close();

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

    @Test
    public void testWellFormedGeoHeaderReturnsTrue() {
        String p = RawDataSpreadsheetImporter.VALID_GEO_QUESTION_HEADER_PATTERN;
        int questionId = ThreadLocalRandom.current().nextInt(999999);

        assertTrue((questionId + "|Latitude").matches(p));
        assertTrue("--GEOLON--|Longitude".matches(p));
        assertTrue("--GEOELE--|Elevation".matches(p));
    }

    @Test
    public void testWrongGeoHeaderDataReturnsFalse() {
        String p = RawDataSpreadsheetImporter.VALID_GEO_QUESTION_HEADER_PATTERN;

        assertFalse("- - GEOLON - -".matches(p));
        assertFalse("--GEOLON--".matches(p));
        assertFalse("--GEOELE--".matches(p));
    }

    @Test
    public void testInvalidGeolocationHeaderReturnsValidationError() throws IOException {
        DataImporter importer = new RawDataSpreadsheetImporter();
        String file = Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID() + ".xslx").toString();
        File spreadsheet = createInvalidGeoColumnSpreadsheet(file);

        Map<Integer, String> errors = importer.validate(spreadsheet);

        assertEquals(1, errors.entrySet().size());
        assertTrue(errors.values().stream().collect(Collectors.toList()).get(0).contains("- - GEOLON - -"));
    }


    @Test
    void testValidateGeoshapes() {
        RawDataSpreadsheetImporter importer = new RawDataSpreadsheetImporter();
        assertFalse(importer.validateGeoshape(INVALID_GEOSHAPE));
        assertTrue(importer.validateGeoshape(VALID_GEOSHAPE));
    }

    @AfterAll
    public static void removeTestFiles() {
        new File("/tmp/valid1.xlsx").delete();
        new File("/tmp/valid2.xlsx").delete();
        new File("/tmp/valid3.xlsx").delete();
        new File("/tmp/invalid1.xlsx").delete();

    }

}
