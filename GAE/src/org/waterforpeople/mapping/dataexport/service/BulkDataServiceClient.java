/*
 *  Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.dataexport.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import com.gallatinsystems.common.util.MD5Util;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.survey.domain.SurveyGroup.PrivacyLevel;
import com.gallatinsystems.survey.domain.SurveyGroup.ProjectType;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.devicefiles.DeviceFilesDto;
import org.waterforpeople.mapping.app.gwt.client.location.PlacemarkDto;
import org.waterforpeople.mapping.app.gwt.client.location.PlacemarkDtoResponse;
import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDependencyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.DataBackoutRequest;
import org.waterforpeople.mapping.app.web.dto.DeviceFileRestRequest;
import org.waterforpeople.mapping.app.web.dto.InstanceDataDto;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;

import static org.waterforpeople.mapping.app.web.dto.SurveyInstanceRequest.*;

/**
 * client code for calling the apis for data processing on the server
 *
 * @author Christopher Fagiani
 */
public class BulkDataServiceClient {

    private static final Logger log = Logger.getLogger(BulkDataServiceClient.class);

    private static final String DATA_SERVLET_PATH = "/databackout";
    public static final String RESPONSE_KEY = "dtoList";
    private static final String SURVEY_SERVLET_PATH = "/surveyrestapi";
    private static final String INSTANCE_DATA_SERVLET_PATH = "/instancedata";
    private static final String DEVICE_FILES_SERVLET_PATH = "/devicefilesrestapi?action=";
    private static final ObjectMapper JSON_RESPONSE_PARSER = new ObjectMapper();

    /**
     * lists all responses from the server for a surveyInstance submission as a map of values keyed
     * on questionId and iteration
     *
     * @param instanceId
     * @param serverBase
     * @return
     * @throws Exception
     */
    public static Map<Long, Map<Long, String>> fetchQuestionResponses(String instanceId,
            String serverBase, String apiKey) throws Exception {
        String instanceValues = fetchDataFromServer(serverBase
                + DATA_SERVLET_PATH, "?action="
                + DataBackoutRequest.LIST_INSTANCE_RESPONSE_ACTION + "&"
                + DataBackoutRequest.SURVEY_INSTANCE_ID_PARAM + "="
                + instanceId, true, apiKey);
        return parseSurveyInstanceResponse(instanceValues);
    }

    public static List<DeviceFilesDto> fetchDeviceFiles(String statusCode,
            String serverBase) throws Exception {
        return fetchData(null, serverBase, statusCode);
    }

    private static List<DeviceFilesDto> fetchData(String cursor,
            String serverBase, String statusCode) throws Exception {

        String queryString = null;
        String response = null;
        ArrayList<DeviceFilesDto> dfDto = new ArrayList<DeviceFilesDto>();
        queryString = serverBase + DEVICE_FILES_SERVLET_PATH
                + DeviceFileRestRequest.LIST_DEVICE_FILES_ACTION + "&"
                + DeviceFileRestRequest.PROCESSED_STATUS_PARAM + "="
                + statusCode;
        if (cursor != null) {
            queryString = queryString + "&cursor=" + cursor;
        }
        response = fetchDataFromServer(queryString);
        List<DeviceFilesDto> list = parseDeviceFiles(response);
        if (list == null || list.size() == 0) {
            return null;
        }
        for (DeviceFilesDto dto : list) {
            dfDto.add(dto);
        }

        JSONObject jsonOuter = new JSONObject(response);
        if (jsonOuter.has("cursor")) {
            cursor = jsonOuter.getString("cursor");
            List<DeviceFilesDto> dfDtoTemp = fetchData(cursor, serverBase,
                    statusCode);
            if (dfDtoTemp != null)
                for (DeviceFilesDto item : dfDtoTemp) {
                    dfDto.add(item);
                }
        }

        return dfDto;
    }

    public static PlacemarkDtoResponse fetchPlacemarks(String countryCode,
            String serverBase, String cursor) throws Exception {
        try {
            return fetchPlacemarkData(cursor, serverBase, countryCode);
        } catch (Exception ex) {
            return fetchPlacemarkData(cursor, serverBase, countryCode);
        }
    }

    private static PlacemarkDtoResponse fetchPlacemarkData(String cursor,
            String serverBase, String countryCode) throws Exception {
        String queryString = null;
        String response = null;
        ArrayList<PlacemarkDto> pmDto = new ArrayList<PlacemarkDto>();
        queryString = serverBase + "/placemarkrestapi?"
                + "needDetailsFlag=true" + "&country=" + countryCode
                + "&display=googleearth&ignoreCache=true";
        if (cursor != null) {
            queryString = queryString + "&cursor=" + cursor;
        }
        response = fetchDataFromServer(queryString);
        List<PlacemarkDto> list = null;
        try {
            list = parsePlacemarks(response);
        } catch (Exception ex) {
            log.error("Caught Exception skipping this response");
        }
        if (list == null || list.size() == 0) {
            return null;
        }
        for (PlacemarkDto dto : list) {
            pmDto.add(dto);
        }

        PlacemarkDtoResponse pdr = new PlacemarkDtoResponse();
        pdr.setDtoList(pmDto);
        JSONObject jsonOuter = new JSONObject(response);
        if (jsonOuter.has("cursor")) {
            cursor = jsonOuter.getString("cursor");
            pdr.setCursor(cursor);
        } else {
            pdr.setCursor(null);
        }
        return pdr;
    }

    private static List<PlacemarkDto> parsePlacemarks(String response)
            throws Exception {
        JSONArray arr = null;
        if (response != null && response.startsWith("{")) {
            List<PlacemarkDto> dtoList = new ArrayList<PlacemarkDto>();

            JSONObject json = new JSONObject(response);
            if (json != null) {
                if (json.has("placemarks")) {
                    try {
                        if (!json.getString("placemarks").equals("null"))
                            arr = json.getJSONArray("placemarks");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        arr = null;
                    }
                } else
                    return null;
            }

            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    PlacemarkDto dto = new PlacemarkDto();
                    JSONObject jsonMark = arr.getJSONObject(i);
                    if (jsonMark != null) {
                        if (jsonMark.has("communityCode")) {
                            String x = jsonMark.getString("communityCode");
                            dto.setCommunityCode(x);
                        }
                        if (jsonMark.has("markType")) {
                            String x = jsonMark.getString("markType");
                            dto.setMarkType(x);
                        }
                        if (jsonMark.has("iconUrl")) {
                            String x = jsonMark.getString("iconUrl");
                            dto.setIconUrl(x);
                        }
                        if (jsonMark.has("longitude")) {
                            String x = jsonMark.getString("longitude");
                            try {
                                dto.setLongitude(new Double(x));
                            } catch (NumberFormatException nex) {
                                log.error("Couldn't parse Longitude for"
                                        + dto.getCommunityCode(), nex);
                                dto.setLongitude(null);
                            }
                        }
                        if (jsonMark.has("latitude")) {
                            String x = jsonMark.getString("latitude");
                            try {
                                dto.setLatitude(new Double(x));
                            } catch (NumberFormatException nex) {
                                log.error("Couldn't parse Latitude for"
                                        + dto.getCommunityCode(), nex);
                                dto.setLatitude(null);
                            }
                        }
                        if (jsonMark.has("collectionDate")) {
                            String x = jsonMark.getString("collectionDate");
                            if (x != null) {
                                try {
                                    dto.setCollectionDate(new Date(x));
                                } catch (IllegalArgumentException iae) {
                                    // log it and ignore it
                                    log.error("Couldn't parse date for"
                                            + dto.getCommunityCode(), iae);
                                    dto.setCollectionDate(null);
                                }
                            }
                        }
                        if (jsonMark.has("placemarkContents")) {
                            String x = jsonMark.getString("placemarkContents");
                            dto.setPlacemarkContents(x);
                        }
                        if (jsonMark.has("pinStyle")) {
                            dto.setPinStyle(jsonMark.getString("pinStyle"));
                        }
                    }
                    dtoList.add(dto);
                }
                return dtoList;
            }
            return null;
        }
        return null;
    }

    /**
     * survey instance ids and their submission dates. Map keys are the instances and values are the
     * dates.
     *
     * @param surveyId
     * @param serverBase
     * @return
     * @throws Exception
     */
    public static Map<String, String> fetchInstanceIds(String surveyId,
            String serverBase, String apiKey, boolean lastCollection,
            String from, String to, String limit) throws Exception {
        Map<String, String> values = new HashMap<String, String>();

        String instanceString = fetchDataFromServer(serverBase
                + DATA_SERVLET_PATH, "?action="
                + DataBackoutRequest.LIST_INSTANCE_ACTION + "&"
                + DataBackoutRequest.SURVEY_ID_PARAM + "=" + surveyId + "&"
                + DataBackoutRequest.INCLUDE_DATE_PARAM + "=true" + "&"
                + DataBackoutRequest.LAST_COLLECTION_PARAM + "="
                + lastCollection + "&"
                + DataBackoutRequest.FROM_DATE_PARAM + "=" + from + "&"
                + DataBackoutRequest.TO_DATE_PARAM + "=" + to + "&"
                + DataBackoutRequest.LIMIT_PARAM + "=" + limit, true, apiKey);

        if (instanceString != null && instanceString.trim().length() != 0) {
            StringTokenizer strTok = new StringTokenizer(instanceString, ",");
            while (strTok.hasMoreTokens()) {
                String instanceId = strTok.nextToken();
                String dateString = "";
                if (instanceId.contains("|")) {
                    dateString = instanceId
                            .substring(instanceId.indexOf("|") + 1);
                    instanceId = instanceId.substring(0,
                            instanceId.indexOf("|"));
                }
                values.put(instanceId, dateString.replaceAll("\n", " ").trim());
            }
        }
        return values;
    }

    public static void main(String[] args) {
        try {
            Map<String, String> results = BulkDataServiceClient
                    .fetchInstanceIds(args[1], args[0], args[2], false, null, null, null);
            if (results != null) {
                log.info(results);
            }
        } catch (Exception e) {
            log.error("Error: " + e.getMessage(), e);
        }
    }

    /**
     * Parse a survey instance response into a map of answers keyed first by question id and then by
     * iteration
     *
     * @param responseData
     * @return
     */
    private static final Map<Long, Map<Long, String>> parseSurveyInstanceResponse(
            String responseData) {

        Map<Long, Map<Long, String>> result = new HashMap<>();
        StringTokenizer lines = new StringTokenizer(responseData, "\n");

        while (lines.hasMoreTokens()) {
            String line = lines.nextToken();
            String[] tokens = line.split(",", 3);

            Long questionId = Long.valueOf(tokens[0]);
            Long iteration = Long.valueOf(tokens[1]);
            String value = new String(Base64.decodeBase64(tokens[2]), StandardCharsets.UTF_8)
                    .trim();

            Map<Long, String> iterationMap = result.get(questionId);
            if (iterationMap != null) {
                assert iterationMap.get(iteration) == null;
                iterationMap.put(iteration, value);
            } else {
                Map<Long, String> newIterationMap = new HashMap<>();
                newIterationMap.put(iteration, value);
                result.put(questionId, newIterationMap);
            }

        }
        return result;
    }

    /**
     * loads full details for a single question (options, translations, etc)
     *
     * @param serverBase
     * @param questionId
     * @return
     */
    public static QuestionDto loadQuestionDetails(String serverBase,
            Long questionId, String apiKey) throws Exception {

        List<QuestionDto> dtoList = null;

        dtoList = parseQuestions(fetchDataFromServer(serverBase
                + SURVEY_SERVLET_PATH, "?action="
                + SurveyRestRequest.GET_QUESTION_DETAILS_ACTION + "&"
                + SurveyRestRequest.QUESTION_ID_PARAM + "=" + questionId, true,
                apiKey));

        if (dtoList != null && dtoList.size() > 0) {
            return dtoList.get(0);
        } else {
            return null;
        }

    }

    /**
     * returns an array containing 2 elements: the first is an ordered list of questionIds (in the
     * order they appear in the survey) and the second element is a map of questions (keyed on id)
     *
     * @param surveyId
     * @param serverBase
     * @return
     * @throws Exception
     */
    public static Object[] loadQuestions(String surveyId, String serverBase, String apiKey)
            throws Exception {
        Object[] results = new Object[2];
        Map<String, QuestionDto> questions = new HashMap<String, QuestionDto>();
        List<QuestionGroupDto> groups = fetchQuestionGroups(serverBase,
                surveyId, apiKey);
        List<String> keyList = new ArrayList<String>();
        if (groups != null) {
            for (QuestionGroupDto group : groups) {
                List<QuestionDto> questionDtos = fetchQuestions(serverBase,
                        group.getKeyId(), apiKey);
                if (questionDtos != null) {
                    for (QuestionDto question : questionDtos) {
                        keyList.add(question.getKeyId().toString());
                        questions.put(question.getKeyId().toString(),
                                question);
                    }
                }
            }
        }
        results[0] = keyList;
        results[1] = questions;
        return results;
    }

    /**
     * gets questions from the server for a specific question group
     *
     * @param serverBase
     * @param groupId
     * @return
     * @throws Exception
     */
    public static List<QuestionDto> fetchQuestions(String serverBase,
            Long groupId, String apiKey) throws Exception {
        return parseQuestions(fetchDataFromServer(serverBase
                + SURVEY_SERVLET_PATH, "?action="
                + SurveyRestRequest.LIST_QUESTION_ACTION + "&"
                + SurveyRestRequest.QUESTION_GROUP_ID_PARAM + "=" + groupId,
                true, apiKey));
    }

    /**
     * gets question options for a list of questions
     * @param surveyId
     * @param serverBase
     * @param apiKey
     * @param questionIds
     * @return
     * @throws Exception
     */
    public static Map<Long, List<QuestionOptionDto>> fetchOptionNodes(String surveyId, String
            serverBase, String apiKey, List<Long> questionIds) throws Exception {
        Map<Long, List<QuestionOptionDto>> result = new HashMap<>();
        //this loop is inefficient when there are many option questions (possibly hundreds)
        //if all options for a survey are needed, use fetchSurveyQuestionOptions()
        for (Long questionId : questionIds) {
            List<QuestionOptionDto> questionOptions =
                    parseQuestionOptions(fetchDataFromServer(serverBase + SURVEY_SERVLET_PATH,
                            "?action=" + SurveyRestRequest.LIST_QUESTION_OPTIONS_ACTION + "&"
                                    + SurveyRestRequest.QUESTION_ID_PARAM + "=" + questionId, true,
                            apiKey));
            result.put(questionId, questionOptions);
        }
        return result;
    }

    /**
     * gets all question options for an entire survey
     * @param surveyId
     * @param serverBase
     * @param apiKey
     * @return list of option nodes
     * @throws Exception
     */
    public static List<QuestionOptionDto> fetchSurveyQuestionOptions(
            String surveyId, String serverBase, String apiKey) throws Exception {
        return parseQuestionOptions(
                fetchDataFromServer(serverBase + SURVEY_SERVLET_PATH,
                                            "?action="
                                            + SurveyRestRequest.LIST_SURVEY_QUESTION_OPTIONS_ACTION + "&"
                                            + SurveyRestRequest.SURVEY_ID_PARAM + "=" + surveyId,
                                    true,
                                    apiKey));
    }

    /**
     * gets a surveyInstance from the server for a specific id
     *
     * @param id
     * @param serverBase
     * @return
     * @throws Exception
     */
    public static SurveyInstanceDto findSurveyInstance(Long id,
            String serverBase, String apiKey) throws Exception {
        return parseSurveyInstance(fetchDataFromServer(serverBase
                + SURVEY_SERVLET_PATH, "?action="
                + SurveyRestRequest.GET_SURVEY_INSTANCE_ACTION + "&"
                + SurveyRestRequest.INSTANCE_PARAM + "=" + id, true,
                apiKey));
    }

    public static InstanceDataDto fetchInstanceData(Long surveyInstanceId, String serverBase,
            String apiKey) throws Exception {

        final String baseUrl = serverBase + INSTANCE_DATA_SERVLET_PATH;

        final String urlQueryString = new StringBuilder()
                .append("?action=").append(GET_INSTANCE_DATA_ACTION)
                .append("&")
                .append(SURVEY_INSTANCE_ID_PARAM).append("=").append(surveyInstanceId)
                .toString();

        final String instanceDataResponse = fetchDataFromServer(baseUrl, urlQueryString, true,
                apiKey);

        return parseInstanceData(instanceDataResponse);
    }

    private static InstanceDataDto parseInstanceData(String instanceDataResponse) {
        try {
            InstanceDataDto instanceData = JSON_RESPONSE_PARSER.readValue(instanceDataResponse,
                    InstanceDataDto.class);
            return instanceData;
        } catch (JsonParseException | JsonMappingException e) {
            log.warn("Failed to parse the InstanceDataDto string: " + e);
        } catch (IOException e) {
            log.equals(e);
        }

        return new InstanceDataDto();
    }

    /**
     * gets question groups from the server for a specific survey
     *
     * @param serverBase
     * @param surveyId
     * @return
     * @throws Exception
     */
    public static List<QuestionGroupDto> fetchQuestionGroups(String serverBase,
            String surveyId, String apiKey) throws Exception {
        return parseQuestionGroups(fetchDataFromServer(serverBase
                + SURVEY_SERVLET_PATH, "?action="
                + SurveyRestRequest.LIST_GROUP_ACTION + "&"
                + SurveyRestRequest.SURVEY_ID_PARAM + "=" + surveyId, true,
                apiKey));
    }

    /**
     * Fetch a single SurveyGroup based for the surveyId provided
     *
     * @param surveyId
     * @param serverBase
     * @param apiKey
     * @return
     * @throws Exception
     */
    public static SurveyGroupDto fetchSurveyGroup(String surveyId, String serverBase,
            String apiKey) {
        SurveyGroupDto surveyGroupDto = null;
        try {
            final String surveyGroupResponse = fetchDataFromServer(
                    serverBase + SURVEY_SERVLET_PATH, "action="
                            + SurveyRestRequest.GET_SURVEY_GROUP_ACTION + "&"
                            + SurveyRestRequest.SURVEY_ID_PARAM + "="
                            + surveyId, true, apiKey);

            log.debug("response: " + surveyGroupResponse);

            final JsonNode surveyGroupListNode = JSON_RESPONSE_PARSER.readTree(surveyGroupResponse)
                    .get("dtoList");
            final List<SurveyGroupDto> surveyGroupList = JSON_RESPONSE_PARSER.readValue(
                    surveyGroupListNode, new TypeReference<List<SurveyGroupDto>>() {
                    });
            if (surveyGroupList != null && !surveyGroupList.isEmpty()) {
                surveyGroupDto = surveyGroupList.get(0);
            }
        } catch (Exception e) {
            log.error(e);
        }

        return surveyGroupDto;
    }

    /**
     * gets question groups from the server for a specific survey
     *
     * @param serverBase
     * @param surveyId
     * @return
     * @throws Exception
     */
    public static List<SurveyGroupDto> fetchSurveyGroups(String serverBase,
            String apiKey) throws Exception {

        final List<SurveyGroupDto> result = new ArrayList<SurveyGroupDto>();
        String cursor = null;

        do {

            String qs = "?action="
                    + SurveyRestRequest.LIST_SURVEY_GROUPS_ACTION;

            if (cursor != null && !"".equals(cursor)) {
                qs = qs + "&cursor=" + cursor;
            }

            String resp = fetchDataFromServer(serverBase + SURVEY_SERVLET_PATH,
                    qs, true, apiKey);

            try {
                JSONObject jsonResp = new JSONObject(resp);
                cursor = jsonResp.isNull("cursor") ? null : jsonResp
                        .getString("cursor");
            } catch (JSONException e) {
                cursor = null;
            }

            result.addAll(parseSurveyGroups(resp));

        } while (cursor != null);

        return result;
    }

    /**
     * gets survey list from the server for a specific survey
     *
     * @param serverBase
     * @param surveyId
     * @return
     * @throws Exception
     */
    public static List<SurveyDto> fetchSurveys(Long surveyGroupId,
            String serverBase, String apiKey) throws Exception {
        return parseSurveys(fetchDataFromServer(serverBase
                + SURVEY_SERVLET_PATH,
                "?action=" + SurveyRestRequest.LIST_SURVEYS_ACTION + "&"
                        + SurveyRestRequest.SURVEY_GROUP_ID_PARAM + "="
                        + surveyGroupId, true, apiKey));
    }

    public static List<SurveyDto> fetchSurvey(Long surveyId,
            String serverBase, String apiKey) throws Exception {
        return parseSurveys(fetchDataFromServer(serverBase
                + SURVEY_SERVLET_PATH,
                "?action=" + SurveyRestRequest.GET_SURVEY_ACTION + "&"
                        + SurveyRestRequest.SURVEY_ID_PARAM + "="
                        + surveyId, true, apiKey));
    }

    /**
     * parses a single SurveyInstanceDto from a json response string
     *
     * @param response
     * @return
     * @throws Exception
     */
    private static SurveyInstanceDto parseSurveyInstance(String response)
            throws Exception {
        SurveyInstanceDto dto = null;
        if (response != null) {
            JSONArray arr = getJsonArray(response);
            if (arr != null && arr.length() > 0) {
                JSONObject json = arr.getJSONObject(0);
                if (json != null) {
                    dto = new SurveyInstanceDto();
                    if (json.has("keyId")) {
                        dto.setKeyId(json.getLong("keyId"));
                    }
                    if (json.has("surveyId")) {
                        dto.setSurveyId(json.getLong("surveyId"));
                    }
                    if (json.has("userID") && !json.isNull("userID")) {
                        dto.setUserID(json.getLong("userID"));
                    }
                    if (json.has("surveyalTime") && !json.isNull("surveyalTime")) {
                        dto.setSurveyalTime(json.getLong("surveyalTime"));
                    }
                    if (json.has("submitterName")) {
                        dto.setSubmitterName(json.getString("submitterName"));
                    }
                    if (json.has("approvedFlag")) {
                        dto.setApprovedFlag(json.getString("approvedFlag"));
                    }
                    if (json.has("deviceIdentifier")) {
                        dto.setDeviceIdentifier(json
                                .getString("deviceIdentifier"));
                    }
                    if (json.has("surveyedLocaleId") && !json.isNull("surveyedLocaleId")) {
                        dto.setSurveyedLocaleId(json.getLong("surveyedLocaleId"));
                    }
                    if (json.has("surveyedLocaleDisplayName")
                            && !json.isNull("surveyedLocaleDisplayName")) {
                        dto.setSurveyedLocaleDisplayName(json
                                .getString("surveyedLocaleDisplayName"));
                    }
                    if (json.has("surveyedLocaleIdentifier")
                            && !json.isNull("surveyedLocaleIdentifier")) {
                        dto.setSurveyedLocaleIdentifier(json.getString("surveyedLocaleIdentifier"));
                    }

                    if (json.has("collectionDate")) {
                        dto.setCollectionDate(new Date(json.getLong("collectionDate")));
                    }
                }
            }
        }
        return dto;
    }

    /**
     * parses the question group response and forms DTOs
     *
     * @param response
     * @return
     * @throws Exception
     */
    private static List<QuestionGroupDto> parseQuestionGroups(String response)
            throws Exception {
        List<QuestionGroupDto> dtoList = new ArrayList<QuestionGroupDto>();
        JSONArray arr = getJsonArray(response);
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject json = arr.getJSONObject(i);
                if (json != null) {
                    QuestionGroupDto dto = new QuestionGroupDto();
                    try {
                        if (!json.isNull("code")) {
                            dto.setCode(json.getString("code"));
                        }
                        if (!json.isNull("keyId")) {
                            dto.setKeyId(json.getLong("keyId"));
                        }
                        if (!json.isNull("displayName")) {
                            dto.setName(json.getString("displayName"));
                        }
                        if (!json.isNull("order")) {
                            dto.setOrder(json.getInt("order"));
                        }
                        if (!json.isNull("path")) {
                            dto.setPath(json.getString("path"));
                        }
                        if (!json.isNull("surveyId")) {
                            dto.setSurveyId(json.getLong("surveyId"));
                        }
                        if (!json.isNull("repeatable")) {
                            dto.setRepeatable(json.getBoolean("repeatable"));
                        }

                        dtoList.add(dto);
                    } catch (Exception e) {
                        log.error("Error in json parsing: " + e.getMessage(), e);
                    }
                }
            }
        }
        return dtoList;
    }

    /**
     * parses the survey group response and forms DTOs
     *
     * @param response
     * @return
     * @throws Exception
     */
    private static List<SurveyGroupDto> parseSurveyGroups(String response)
            throws Exception {
        List<SurveyGroupDto> dtoList = new ArrayList<SurveyGroupDto>();
        JSONArray arr = getJsonArray(response);
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject json = arr.getJSONObject(i);
                if (json != null) {
                    SurveyGroupDto dto = new SurveyGroupDto();
                    try {
                        if (!json.isNull("code")) {
                            dto.setCode(json.getString("code"));
                        }
                        if (!json.isNull("keyId")) {
                            dto.setKeyId(json.getLong("keyId"));
                        }
                        if (!json.isNull("displayName")) {
                            dto.setName(json.getString("displayName"));
                        }
                        if (!json.isNull("description")) {
                            dto.setDescription(json.getString("description"));
                        }
                        if (!json.isNull("projectType")) {
                            dto.setProjectType(ProjectType.valueOf(json.getString("projectType")));
                        }
                        if (!json.isNull("parentId")) {
                            dto.setParentId(json.getLong("parentId"));
                        }
                        if (!json.isNull("path")) {
                            dto.setPath(json.getString("path"));
                        }
                        if (!json.isNull("ancestorIds")) {
                            JSONArray idArr = json.getJSONArray("ancestorIds");
                            List<Long> ancestorIds = new ArrayList<Long>();
                            for (int ix = 0; ix < idArr.length(); ix++) {
                                ancestorIds.add(idArr.getLong(ix));
                            }
                            dto.setAncestorIds(ancestorIds);
                        }
                        if (!json.isNull("defaultLanguageCode")) {
                            dto.setDefaultLanguageCode(json.getString("defaultLanguageCode"));
                        }
                        if (!json.isNull("privacyLevel")) {
                            dto.setPrivacyLevel(PrivacyLevel.valueOf(json.getString("privacyLevel")));
                        }
                        if (!json.isNull("monitoringGroup")) {
                            dto.setMonitoringGroup(json.getBoolean("monitoringGroup"));
                        }
                        if (!json.isNull("newLocaleSurveyId")) {
                            dto.setNewLocaleSurveyId(json.getLong("newLocaleSurveyId"));
                        }
                        dtoList.add(dto);
                    } catch (Exception e) {
                        log.error("Error in json parsing: " + e.getMessage(), e);
                    }
                }
            }
        }
        return dtoList;
    }

    /**
     * parses the survey group response and forms DTOs
     *
     * @param response
     * @return
     * @throws Exception
     */
    private static List<SurveyDto> parseSurveys(String response)
            throws Exception {
        List<SurveyDto> dtoList = new ArrayList<SurveyDto>();
        JSONArray arr = getJsonArray(response);
        if (arr != null) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject json = arr.getJSONObject(i);
                if (json != null) {
                    SurveyDto dto = new SurveyDto();
                    try {
                        if (!json.isNull("code")) {
                            dto.setCode(json.getString("code"));
                        }
                        if (!json.isNull("defaultLanguageCode")) {
                            dto.setDefaultLanguageCode(json.getString("defaultLanguageCode"));
                        }
                        if (!json.isNull("description")) {
                            dto.setDescription(json.getString("description"));
                        }
                        if (!json.isNull("instanceCount")) {
                            dto.setInstanceCount(json.getLong("instanceCount"));
                        }
                        if (!json.isNull("keyId")) {
                            dto.setKeyId(json.getLong("keyId"));
                        }
                        if (!json.isNull("name")) {
                            dto.setName(json.getString("name"));
                        }
                        if (!json.isNull("path")) {
                            dto.setPath(json.getString("path"));
                        }
                        if (!json.isNull("pointType")) {
                            dto.setPointType(json.getString("pointType"));
                        }
                        if (!json.isNull("requireApproval")) {
                            dto.setRequireApproval(json.getBoolean("requireApproval"));
                        }
                        if (!json.isNull("sector")) {
                            dto.setSector(json.getString("sector"));
                        }
                        if (!json.isNull("status")) {
                            dto.setStatus(json.getString("status"));
                        }
                        if (!json.isNull("surveyGroupId")) {
                            dto.setSurveyGroupId(json.getLong("surveyGroupId"));
                        }
                        if (!json.isNull("version")) {
                            dto.setVersion(json.getString("version"));
                        }
                        if (!json.isNull("ancestorIds")) {
                            JSONArray idArr = json.getJSONArray("ancestorIds");
                            List<Long> ancestorIds = new ArrayList<Long>();
                            for (int ix = 0; ix < idArr.length(); ix++) {
                                ancestorIds.add(idArr.getLong(ix));
                            }
                            dto.setAncestorIds(ancestorIds);
                        }
                        dtoList.add(dto);
                    } catch (Exception e) {
                        log.error("Error in json parsing: " + e.getMessage(), e);
                    }
                }
            }
        }
        return dtoList;
    }

    private static List<DeviceFilesDto> parseDeviceFiles(String response)
            throws Exception {
        if (response.startsWith("{")) {
            List<DeviceFilesDto> dtoList = new ArrayList<DeviceFilesDto>();
            JSONArray arr = getJsonArray(response);

            if (arr != null) {

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject json = arr.getJSONObject(i);
                    dtoList.add(parseDeviceFile(json));
                }
                return dtoList;
            }
            return null;
        }
        return null;
    }

    public static DeviceFilesDto parseDeviceFile(JSONObject json)
            throws JSONException {
        DeviceFilesDto dto = new DeviceFilesDto();
        if (json != null) {
            if (json.has("processingMessage")) {
                String x = json.getString("processingMessage");
                dto.setProcessingMessage(x);
            }
            if (json.has("phoneNumber")) {
                String x = json.getString("phoneNumber");
                dto.setPhoneNumber(x);
            }
            if (json.has("processedStatus")) {
                String x = json.getString("processedStatus");
                dto.setProcessedStatus(x);
            }
            if (json.has("checksum")) {
                String x = json.getString("checksum");
                dto.setChecksum(x);
            }
            if (json.has("processDate")) {
                String x = json.getString("processDate");
                dto.setProcessDate(x);
            }
            if (json.has("URI")) {
                String x = json.getString("URI");
                dto.setURI(x);
            }
            if (json.has("surveyInstanceId")) {
                String x = json.getString("surveyInstanceId");
                dto.setSurveyInstanceId(Long.parseLong(x));
            }
        }
        return dto;
    }

    /**
     * parses question responses into QuestionDto objects
     *
     * @param response
     * @return
     * @throws Exception
     */
    private static List<QuestionDto> parseQuestions(String response)
            throws Exception {
        if (response.startsWith("{")) {
            List<QuestionDto> dtoList = new ArrayList<QuestionDto>();
            JSONArray arr = getJsonArray(response);
            if (arr != null) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject json = arr.getJSONObject(i);
                    if (json != null) {
                        QuestionDto dto = new QuestionDto();
                        try {
                            if (json.has("surveyId")) {
                                if (json.getString("surveyId") != null) {
                                    String numberC = json.getString("surveyId");
                                    try {
                                        dto.setSurveyId(Long.parseLong(numberC));
                                    } catch (NumberFormatException nex) {
                                        dto.setSurveyId(null);
                                    }
                                }
                            }

                            if (!json.isNull("allowMultipleFlag")) {
                                dto.setAllowMultipleFlag(json.getBoolean("allowMultipleFlag"));
                            }
                            if (!json.isNull("allowOtherFlag")) {
                                dto.setAllowOtherFlag(json.getBoolean("allowOtherFlag"));
                            }
                            if (!json.isNull("order")) {
                                dto.setOrder(json.getInt("order"));
                            }
                            if (!json.isNull("questionGroupId")) {
                                dto.setQuestionGroupId(json.getLong("questionGroupId"));
                            }
                            if (!json.isNull("tip")) {
                                dto.setTip(json.optString("tip"));
                            }
                            if (!json.isNull("questionId")) {
                                dto.setQuestionId(json.optString("questionId"));
                            }
                            if (!json.isNull("path")) {
                                dto.setPath(json.getString("path"));
                            }
                            if (!json.isNull("text")) {
                                dto.setText(json.getString("text"));
                            }
                            if (!json.isNull("keyId")) {
                                dto.setKeyId(json.getLong("keyId"));
                            }
                            if (!json.isNull("collapseable")) {
                                dto.setCollapseable(json.getBoolean("collapseable"));
                            }
                            if (!json.isNull("dependentFlag")) {
                                dto.setDependentFlag(json.getBoolean("dependentFlag"));
                            }
                            if (!json.isNull("dependentQuestionAnswer")) {
                                dto.setDependentQuestionAnswer(json
                                        .optString("dependentQuestionAnswer"));
                            }
                            if (json.has("dependentQuestionId")) {
                                try {
                                    dto.setDependentQuestionId(json.getLong("dependentQuestionId"));
                                } catch (Exception e) {
                                    dto.setDependentQuestionId(null);
                                }
                            }
                            if (!json.isNull("geoLocked")) {
                                dto.setGeoLocked(json.getBoolean("geoLocked"));
                            }
                            if (json.has("caddisflyResourceUuid")
                                    && json.getString("caddisflyResourceUuid") != null) {
                                dto.setCaddisflyResourceUuid(json
                                        .getString("caddisflyResourceUuid"));
                            }
                            if (!json.isNull("immutable")) {
                                dto.setImmutable(json.getBoolean("immutable"));
                            }
                            if (!json.isNull("isName")) {
                                dto.setName(json.getBoolean("isName"));
                            }
                            if (!json.isNull("localeNameFlag")) {
                                dto.setLocaleNameFlag(json.getBoolean("localeNameFlag"));
                            }
                            if (!json.isNull("mandatoryFlag")) {
                                dto.setMandatoryFlag(json.getBoolean("mandatoryFlag"));
                            }
                            if (json.has("metricId")) {
                                try {
                                    dto.setMetricId(json.getLong("metricId"));
                                } catch (Exception e) {
                                    dto.setMetricId(null);
                                }
                            }
                            if (!json.isNull("requireDoubleEntry")) {
                                dto.setRequireDoubleEntry(json.getBoolean("requireDoubleEntry"));
                            }
                            if (json.has("sourceId")) {
                                try {
                                    dto.setSourceId(json.getLong("sourceId"));
                                } catch (Exception e) {
                                    dto.setSourceId(null);
                                }
                            }
                            if (!json.isNull("allowDecimal")) {
                                dto.setAllowDecimal(json.getBoolean("allowDecimal"));
                            }
                            if (!json.isNull("allowSign")) {
                                dto.setAllowSign(json.getBoolean("allowSign"));
                            }
                            if (json.has("minVal")) {
                                try {
                                    dto.setMinVal(json.getDouble("minVal"));
                                } catch (Exception e) {
                                    dto.setMinVal(null);
                                }
                            }
                            if (json.has("maxVal")) {
                                try {
                                    dto.setMaxVal(json.getDouble("maxVal"));
                                } catch (Exception e) {
                                    dto.setMaxVal(null);
                                }
                            }

                            if (!json.isNull("translationMap")) {
                                dto.setTranslationMap(parseTranslations(json
                                        .getJSONObject("translationMap")));
                            }
                            if (!json.isNull("type")) {
                                dto.setType(QuestionDto.QuestionType.valueOf(json
                                        .getString("type")));
                            }
                            if (!json.isNull("allowPoints")) {
                                dto.setAllowPoints(json.getBoolean("allowPoints"));
                            }
                            if (!json.isNull("allowLine")) {
                                dto.setAllowLine(json.getBoolean("allowLine"));
                            }
                            if (!json.isNull("allowPolygon")) {
                                dto.setAllowPolygon(json.getBoolean("allowPolygon"));
                            }
                            if (!json.isNull("optionContainerDto")) {
                                OptionContainerDto container = new OptionContainerDto();
                                JSONObject contJson = json.getJSONObject("optionContainerDto");
                                if (!contJson.isNull("optionsList")) {
                                    JSONArray optArray = contJson.getJSONArray("optionsList");
                                    if (optArray != null) {
                                        for (int j = 0; j < optArray.length(); j++) {
                                            JSONObject optJson = optArray.getJSONObject(j);
                                            QuestionOptionDto opt = new QuestionOptionDto();
                                            opt.setKeyId(optJson.getLong("keyId"));
                                            opt.setText(optJson.getString("text"));
                                            if (!optJson.isNull("code")) {
                                                // getString on null gives String "null"
                                                opt.setCode(optJson.getString("code"));
                                            }
                                            opt.setOrder(optJson.getInt("order"));
                                            if (!optJson.isNull("translationMap")) {
                                                opt.setTranslationMap(parseTranslations(optJson
                                                        .getJSONObject("translationMap")));
                                            }
                                            container.addQuestionOption(opt);
                                        }
                                    }
                                    dto.setOptionContainerDto(container);
                                }
                            }

                            // The questionDependency check below is related to the
                            // previous if statements dependentFlag, dependentQuestionId,
                            // dependentQuestionAnswer i.e. checks whether question is
                            // dependent on another
                            if (!json.isNull("questionDependency")) {
                                QuestionDependencyDto dep = new QuestionDependencyDto();
                                JSONObject depJson = json.getJSONObject("questionDependency");
                                dep.setQuestionId(depJson.getLong("questionId"));
                                dep.setAnswerValue(depJson.getString("answerValue"));
                                dto.setQuestionDependency(dep);
                            }

                            if (!json.isNull("levelNames")) {
                                final List<String> levelNames = new ArrayList<String>();
                                final JSONArray array = json.getJSONArray("levelNames");
                                for (int c = 0; c < array.length(); c++) {
                                    levelNames.add(array.getString(c));
                                }
                                dto.setLevelNames(levelNames);
                            }
                            dtoList.add(dto);
                        } catch (Exception e) {
                            log.error("Error in json parsing: " + e.getMessage(), e);
                        }
                    }
                }
            }
            return dtoList;
        } else
            return null;
    }

    /**
     * @param response
     * @return
     */
    private static List<QuestionOptionDto> parseQuestionOptions(String response) {

        List<QuestionOptionDto> dtoList = new ArrayList<>();
        try {
            JSONArray jsonArray = getJsonArray(response);

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json != null) {
                        QuestionOptionDto dto = new QuestionOptionDto();
                        dto.setQuestionId(json.getLong("questionId"));
                        dto.setText(json.optString("text", null));
                        dto.setCode(json.optString("code", null));
                        dtoList.add(dto);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not parse question options: " + response, e);
        }
        return dtoList;
    }

    @SuppressWarnings("unchecked")
    private static TreeMap<String, TranslationDto> parseTranslations(
            JSONObject translationMapJson) throws Exception {
        Iterator<String> keyIter = translationMapJson.keys();
        TreeMap<String, TranslationDto> translationMap = null;
        if (keyIter != null) {
            translationMap = new TreeMap<String, TranslationDto>();
            String lang = keyIter.next();
            JSONObject transObj = translationMapJson.getJSONObject(lang);
            if (transObj != null) {
                TranslationDto tDto = new TranslationDto();
                tDto.setKeyId(transObj.getLong("keyId"));
                tDto.setParentId(transObj.getLong(("parentId")));
                tDto.setParentType(transObj.getString("parentType"));
                tDto.setLangCode(lang);
                tDto.setText(transObj.getString("text"));
                translationMap.put(lang, tDto);
            }
        }
        return translationMap;
    }

    /**
     * invokes a remote REST api using the base and query string passed in. If shouldSign is true,
     * the queryString will be augmented with a timestamp and hash parameter.
     *
     * @param baseUrl
     * @param queryString
     * @param shouldSign
     * @param key
     * @return
     * @throws Exception
     */
    public static String fetchDataFromServer(String baseUrl,
            String queryString, boolean shouldSign, String apiKey)
            throws Exception {
        if (shouldSign && apiKey != null) {
            if (queryString == null) {
                queryString = new String();
            } else {
                if (queryString.trim().startsWith("?")) {
                    queryString = queryString.trim().substring(1);
                }
                queryString += "&";
            }
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            queryString += RestRequest.TIMESTAMP_PARAM + "="
                    + URLEncoder.encode(df.format(new Date()), "UTF-8");
            queryString = sortQueryString(queryString);
            queryString += "&" + RestRequest.HASH_PARAM + "="
                    + MD5Util.generateHMAC(queryString, apiKey);
        }
        return fetchDataFromServer(baseUrl
                + ((queryString != null && queryString.trim().length() > 0) ? "?"
                        + queryString
                        : ""));

    }

    /**
     * invokes a remote REST api. If the url is longer than 1900 characters, this method will use
     * POST since that is too long for a GET
     *
     * @param fullUrl
     * @return
     * @throws Exception
     */
    public static String fetchDataFromServer(String fullUrl) throws Exception {
        if (fullUrl != null) {
            if (fullUrl.length() > 1900) {
                return fetchDataFromServerPOST(fullUrl);
            } else {
                return fetchDataFromServerGET(fullUrl);
            }
        } else {
            return null;
        }
    }

    /**
     * executes a post to invoke a rest api
     */
    private static String fetchDataFromServerPOST(String fullUrl)
            throws Exception {
        BufferedReader reader = null;
        String result = null;
        try {
            String baseUrl = fullUrl;
            String queryString = null;
            if (fullUrl.contains("?")) {
                baseUrl = fullUrl.substring(0, fullUrl.indexOf("?"));
                queryString = fullUrl.substring(fullUrl.indexOf("?") + 1);
            }
            URL url = new URL(baseUrl);
            log.debug("Calling: " + baseUrl + " with params: "
                    + queryString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(30000);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Length",
                    "" + Integer.toString(queryString.getBytes().length));
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.addRequestProperty("Accept-Encoding", "gzip");
            conn.addRequestProperty("User-Agent", "gzip");

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(queryString);
            wr.flush();
            wr.close();
            InputStream instream = conn.getInputStream();
            String contentEncoding = conn.getHeaderField("Content-Encoding");

            if (contentEncoding != null
                    && contentEncoding.equalsIgnoreCase("gzip")) {
                reader = new BufferedReader(new InputStreamReader(
                        new GZIPInputStream(instream), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(instream,
                        "UTF-8"));
            }

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
     * executes a GET to invoke a rest api
     */
    private static String fetchDataFromServerGET(String fullUrl)
            throws Exception {
        BufferedReader reader = null;
        String result = null;
        try {
            URL url = new URL(fullUrl);
            log.debug("Calling: " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(30000);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.addRequestProperty("Accept-Encoding", "gzip");
            conn.addRequestProperty("User-Agent", "gzip");
            InputStream instream = conn.getInputStream();
            String contentEncoding = conn.getHeaderField("Content-Encoding");

            if (contentEncoding != null
                    && contentEncoding.equalsIgnoreCase("gzip")) {
                reader = new BufferedReader(new InputStreamReader(
                        new GZIPInputStream(instream), "UTF-8"));
            } else {
                reader = new BufferedReader(new InputStreamReader(instream,
                        "UTF-8"));
            }

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

    private static String sortQueryString(String queryString) throws UnsupportedEncodingException {
        String[] parts = queryString.split("&");
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (int i = 0; i < parts.length; i++) {
            String[] nvp = parts[i].split("=");
            if (nvp.length > 1) {
                if (nvp.length == 2) {
                    pairs.add(new NameValuePair(nvp[0], nvp[1]));
                } else {
                    // if we're here, we have multiple "=" so we need to merge
                    // parts 1..n
                    StringBuilder builder = new StringBuilder();
                    for (int j = 1; j < nvp.length; j++) {
                        if (builder.length() > 0) {
                            builder.append("=");
                        }
                        builder.append(nvp[j]);
                    }
                    pairs.add(new NameValuePair(nvp[0], builder.toString()));
                }
            }
        }
        // now sort the names

        Collections.sort(pairs);
        StringBuilder result = new StringBuilder();
        for (NameValuePair nvp : pairs) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(nvp.getName()).append("=");
            if (nvp.getName().equals(RestRequest.TIMESTAMP_PARAM)) {
                result.append(nvp.getValue());
            } else {
                result.append(URLEncoder.encode(nvp.getValue(), "UTF-8"));
            }
        }
        return result.toString();
    }

    /**
     * converts the string into a JSON array object.
     */
    public static JSONArray getJsonArray(String response) throws Exception {
        log.debug("response: " + response);
        if (response != null) {
            JSONObject json = new JSONObject(response);
            if (json != null) {
                return json.getJSONArray(RESPONSE_KEY);
            }
        }
        return null;
    }
}
