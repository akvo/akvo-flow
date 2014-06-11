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

package com.gallatinsystems.survey;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SurveyHarness {

    public static void main(String[] args) {
        SurveyHarness sh = new SurveyHarness();
        // sh.sendSuveyXML(args[0]);
        sh.sendAccessPoint();
    }

    @SuppressWarnings("unused")
    public void sendSuveyXML(String filePath) {
        try {
            // Construct data
            String data = URLEncoder.encode("action", "UTF-8") + "="
                    + URLEncoder.encode("value1", "UTF-8");
            data += "&" + URLEncoder.encode("surveyDoc", "UTF-8") + "="
                    + URLEncoder.encode(readFileAsString(filePath), "UTF-8");

            // Send data
            URL url = new URL("http://localhost:8888/surveymanager");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn
                    .getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Process line...
            }
            wr.close();
            rd.close();
        } catch (Exception e) {
        }
    }

    public void sendAccessPoint() {
        try {
            // Construct data
            String data = URLEncoder.encode("action", "UTF-8") + "="
                    + URLEncoder.encode("addAccessPoints", "UTF-8");
            data += "&" + URLEncoder.encode("surveyId", "UTF-8") + "="
                    + URLEncoder.encode("2025", "UTF-8");

            // Send data
            URL url = new URL("http://watermappingmonitoring.appspot.com/app_worker/task");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn
                    .getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                    .getInputStream()));
            @SuppressWarnings("unused")
            String line;
            while ((line = rd.readLine()) != null) {
                // Process line...
            }
            wr.close();
            rd.close();
        } catch (Exception e) {
        }
    }

    private static String readFileAsString(String filePath)
            throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }
}
