/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;

/**
 * This utility will produce a RawDateReport using data contained a set of data.txt files from the
 * phone. The input is a directory housing 1 or more zip files obtained from the FieldSurvey
 * application. This utility will merge the data file from all of the zips into a single file and
 * will then iterate over the merged text, writing the responses to an excel raw data report. This
 * utility needs connectivity to the server to run ONLY to get the list of questions and their IDs.
 * The data will NOT be loaded into the server. This utility will only work with data files
 * generated via version 1.7 or later of the FieldSurvey application
 * 
 * @author Christopher Fagiani
 */
public class OfflineExport extends GraphicalSurveySummaryExporter {

    private static final ThreadLocal<DateFormat> DF = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("MM-dd-yyyy HH:mm:ss z");
        };
    };

    @SuppressWarnings("unchecked")
    public void export(Map<String, String> criteria, File outputFile,
            String serverBase, Map<String, String> options) {
        try {
            SurveyBulkUploader up = new SurveyBulkUploader();
            criteria.put(SurveyBulkUploader.MODE_KEY,
                    SurveyBulkUploader.MERGE_ONLY_MODE);

            up.executeImport(getSelectedInputDir(), "", criteria);
            List<File> inputFiles = up.getFilesToUpload();
            if (inputFiles != null && inputFiles.size() > 0) {
                Map<QuestionGroupDto, List<QuestionDto>> questionMap = loadAllQuestions(
                        criteria.get(SurveyRestRequest.SURVEY_ID_PARAM), false,
                        serverBase, criteria.get("apiKey"));

                Workbook wb = new XSSFWorkbook();

                Sheet sheet = wb.createSheet();
                processOptions(options);
                List<String> questionIds = (List<String>) createRawDataHeader(
                        wb, sheet, questionMap)[0];
                // now read in the files
                for (File f : inputFiles) {
                    if (f.getName().toLowerCase().endsWith(".zip")) {
                        String content = SurveyBulkUploader
                                .getContentFromZip(f);
                        int row = 1;
                        String line = null;
                        String lastUUID = "";
                        Row r = null;
                        StringTokenizer strTok = new StringTokenizer(content,
                                "\n");
                        while (strTok.hasMoreTokens()) {
                            line = strTok.nextToken();
                            String[] parts = line.split("\t");
                            boolean isForThisSurvey = (parts.length > 2 && questionIds
                                    .contains(parts[2]));
                            if (isForThisSurvey) {
                                if (!lastUUID.equals(parts[parts.length - 1])) {
                                    // this is the first row of new instance so
                                    // write the response row
                                    r = sheet.createRow(row++);
                                    lastUUID = parts[parts.length - 1];
                                    createCell(r, 0, lastUUID, null);
                                    createCell(
                                            r,
                                            1,
                                            DF.get()
                                                    .format(new Date(
                                                            Long.parseLong(parts[7]))),
                                            null);
                                    createCell(r, 2, parts[5], null);
                                }
                                int idx = questionIds.indexOf(parts[2]) + 3;
                                if (idx >= 3) {
                                    String val = parts[4];
                                    if (val != null) {
                                        if ("IMAGE".equals(parts[3])) {
                                            val = val.replace("/mnt/sdcard",
                                                    getImagePrefix());
                                        }
                                        createCell(r, idx, val, null);
                                    }
                                }
                            }
                        }
                    }
                }
                FileOutputStream fileOut = new FileOutputStream(outputFile);
                wb.write(fileOut);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getSelectedInputDir() {
        try {
            JFileChooser chooser = new JFileChooser();

            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            chooser.showOpenDialog(null);
            if (chooser.getSelectedFile() != null) {
                return chooser.getSelectedFile();
            } else {
                return getSelectedInputDir();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getSelectedInputDir();
        }
    }
}
