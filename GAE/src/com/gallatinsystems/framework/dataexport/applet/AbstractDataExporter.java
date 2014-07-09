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

package com.gallatinsystems.framework.dataexport.applet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

/**
 * base class with utilities for interacting with remote services to fetch data for export
 * 
 * @author Christopher Fagiani
 */
public abstract class AbstractDataExporter implements DataExporter {

    protected String fetchDataFromServer(String fullUrl) throws Exception {
        BufferedReader reader = null;
        String result = null;
        try {
            URL url = new URL(fullUrl);
            System.out.println("Calling: " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            reader = new BufferedReader(new InputStreamReader(conn
                    .getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }

    /**
     * converts the criteria map to server parameters. only non-null, non-blank parameters are
     * included.
     */
    protected String formParams(Map<String, String> criteria) {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (Entry<String, String> crit : criteria.entrySet()) {
            if (crit.getValue() != null && crit.getValue().trim().length() > 0) {
                if (count > 0) {
                    builder.append("&");
                }
                builder.append(crit.getKey().trim()).append("=").append(
                        crit.getValue().trim());
                count++;
            }
        }
        return builder.toString();
    }
}
