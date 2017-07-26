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

package org.akvo.flow.domain;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * Utilities class for performing transformations on survey data, i.e. response values
 */
public class DataUtils {

    private static final Logger log = Logger.getLogger(DataUtils.class);

    public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

    public static String[] optionResponsesTextArray(String optionResponse) {
        String[] responseArray = null;

        if (optionResponse == null || optionResponse.trim().equals("")) {
            return new String[0];
        }

        List<Map<String, String>> optionNodes = jsonStringToList(optionResponse);
        if (optionNodes != null) {
            responseArray = new String[optionNodes.size()];
            for (int i = 0; i < responseArray.length; i++) {
                String text = optionNodes.get(i).get("text");
                if (text != null && text.trim() != "") {
                    responseArray[i] = text.trim();
                }
            }
        } else {
            responseArray = optionResponse.split("\\|");
        }

        return responseArray;
    }

    public static String[] cascadeResponseValues(String data) {
        String[] values = null;

        if (data == null || data.trim().equals("")) {
            return new String[0];
        }

        List<Map<String, String>> nodes = jsonStringToList(data);
        if (nodes != null) {
            values = new String[nodes.size()];
            for (int i = 0; i < values.length; i++) {
                String text = nodes.get(i).get("name");
                if (text != null && text.trim() != "") {
                    values[i] = text.trim();
                }
            }
        } else {
            values = data.split("\\|", -1);
        }

        return values;
    }

    /**
     * Convert a JSON string response for OPTION type questions to the legacy pipe separated format
     *
     * @param jsonResponse
     * @return
     */
    public static String jsonResponsesToPipeSeparated(String optionResponses) {
        StringBuilder pipeSeparated = new StringBuilder();
        List<Map<String, String>> options = jsonStringToList(optionResponses);
        if (options != null) {
            for (Map<String, String> option : options) {
                if (option.get("text") != null) { //try OPTION answer
                    pipeSeparated.append("|").append(option.get("text"));
                } else if (option.get("name") != null) { //try CASCADE answer
                    pipeSeparated.append("|").append(option.get("name"));
                }
            }
        }
        if (pipeSeparated.length() > 0) {
            pipeSeparated.deleteCharAt(0);
        }
        return pipeSeparated.toString();
    }

    /**
     * Convert a JSON string response to a list containing corresponding maps
     *
     * @param data String containing the JSON-formatted response
     * @return List of maps with response properties
     */
    public static List<Map<String, String>> jsonStringToList(String data) {
        try {
            return JSON_OBJECT_MAPPER.readValue(data,
                    new TypeReference<List<Map<String, String>>>() {});
        } catch (IOException e) {
            // Data is not JSON-formatted
        }

        return null;
    }

    /**
     * Process the JSON formatted string value of a signature question and return the string
     * representing the signatory. A blank string is returned
     *
     * @param value
     * @return
     */
    public static String parseSignatory(String value) {
        String signatory = null;
        Map<String, String> signatureResponse = null;
        try {
            signatureResponse = JSON_OBJECT_MAPPER.readValue(value,
                    new TypeReference<Map<String, String>>() {
                    });
            signatory = signatureResponse.get("name");
        } catch (IOException e) {
            // ignore
        }
        return signatory;
    }

    /**
     * Parse a caddisfly response string and return a corresponding map
     *
     * @param caddisflyValue
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseCaddisflyResponseValue(String caddisflyValue) {
        Map<String, Object> caddisflyResponseMap = new HashMap<>();
        try {
            caddisflyResponseMap = JSON_OBJECT_MAPPER.readValue(caddisflyValue, Map.class);
        } catch (IOException e) {
            log.error("Failed to parse the caddisfly response");
        }
        if (caddisflyResponseMap != null) {
            return caddisflyResponseMap;
        } else {
            return Collections.emptyMap();
        }
    }
}
