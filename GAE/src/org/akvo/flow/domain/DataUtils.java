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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * Utilities class for performing transformations on survey data, i.e. response values
 */
public class DataUtils {

    public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

    public static String[] optionResponsesTextArray(String optionResponse) {
        String[] responseArray = null;

        if (optionResponse == null || optionResponse.trim() == "") {
            return new String[0];
        }

        List<Map<String, String>> optionNodes = new ArrayList<>();
        if (optionResponse.startsWith("[")) {
            optionNodes = jsonStringToOptionList(optionResponse);
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

    /**
     * Convert a JSON string response for OPTION type questions to the legacy pipe separated format
     *
     * @param jsonResponse
     * @return
     */
    public static String jsonResponsesToPipeSeparated(String optionResponses) {
        StringBuilder pipeSeparated = new StringBuilder();
        for (Map<String, String> option : jsonStringToOptionList(optionResponses)) {
            if (option.get("text") != null) {
                pipeSeparated.append("|").append(option.get("text"));
            }
        }
        if (pipeSeparated.length() > 0) {
            pipeSeparated.deleteCharAt(0);
        }
        return pipeSeparated.toString();
    }

    /**
     * Convert a JSON string response for OPTION type questions to a list containing corresponding
     * maps
     *
     * @param optionResponse
     * @return
     */
    public static List<Map<String, String>> jsonStringToOptionList(String optionResponse) {
        List<Map<String, String>> optionNodes = new ArrayList<>();
        if (optionResponse.startsWith("[")) {
            try {
                optionNodes = JSON_OBJECT_MAPPER.readValue(optionResponse,
                        new TypeReference<List<Map<String, String>>>() {
                        });
            } catch (IOException e) {
                // ignore
            }
        }
        return optionNodes;
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
}
