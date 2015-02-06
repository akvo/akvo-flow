/*
 *  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.events;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnifiedLog {
    public enum EventTopic {
        DATA
    };

    private static final Logger log = Logger
            .getLogger(UnifiedLog.class.getName());

    public static void dispatch(EventTopic topic, String message) {
        try {
            URL url = new URL("http://flowdev1.akvo.org:3030/event");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(message);
            writer.close();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.log(Level.SEVERE, "Unified log message failed");
            }
        } catch (MalformedURLException e) {
            log.log(Level.SEVERE, "Unified log message failed with malformed URL exception");
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unified log message failed with IO exception");
        }
    }
}