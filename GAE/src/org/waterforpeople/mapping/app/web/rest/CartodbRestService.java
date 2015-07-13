
package org.waterforpeople.mapping.app.web.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.rest.dto.NamedMapPayload;

import com.gallatinsystems.common.util.PropertyUtil;

@Controller
@RequestMapping("/cartodb")
public class CartodbRestService {

    private static final String CDB_API_KEY = PropertyUtil.getProperty("cartodbApiKey");
    private static final String SQL_API = PropertyUtil.getProperty("cartodbSqlApi");

    private static final String CDB_ACCOUNT_NAME = PropertyUtil.getProperty("cartodbAccountName");
    private static final String CDB_HOST = PropertyUtil.getProperty("cartodbHost");
    private static final String CDB_TILER_PORT = PropertyUtil.getProperty("cartodbTilerPort");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.GET, value = "answers")
    @ResponseBody
    public Map<String, Object> getAnswers(@RequestParam("dataPointId") Long dataPointId,
            @RequestParam("surveyId") Long surveyId) {

        Map<String, Object> response = new HashMap<>();
        response.put("answers", null);
        response.put("formId", null);

        try {
            String formIdQuery = String.format("SELECT id FROM form WHERE survey_id=%d", surveyId);
            List<Map<String, Object>> formIdResponse = queryCartodb(formIdQuery);
            if (!formIdResponse.isEmpty()) {
                Integer formId = (Integer) formIdResponse.get(0).get("id");
                response.put("formId", formId);

                String rawDataQuery = String.format(
                        "SELECT * FROM raw_data_%s WHERE data_point_id=%d",
                        formId, dataPointId);
                List<Map<String, Object>> rawDataResponse = queryCartodb(rawDataQuery);
                if (!rawDataResponse.isEmpty()) {
                    response.put("answers", rawDataResponse.get(0));
                }
            }
            return response;
        } catch (IOException e) {
            return response;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "raw_data")
    @ResponseBody
    public Map<String, Object> getPointData(@RequestParam("dataPointId") Long dataPointId,
            @RequestParam("formId") Long formId) {

        Map<String, Object> response = new HashMap<>();
        response.put("answers", null);
        response.put("formId", formId);

        try {
            String rawDataQuery = String.format(
                    "SELECT * FROM raw_data_%s WHERE data_point_id=%d",
                    formId, dataPointId);
            List<Map<String, Object>> rawDataResponse = queryCartodb(rawDataQuery);
            if (!rawDataResponse.isEmpty()) {
                response.put("answers", rawDataResponse.get(0));
            }
            return response;
        } catch (IOException e) {
            return response;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "questions")
    @ResponseBody
    public Map<String, Object> getQuestions(
            @RequestParam(value = "form_id", required = true) Long formId) {

        Map<String, Object> response = new HashMap<>();
        response.put("questions", null);
        try {
            response.put(
                    "questions",
                    queryCartodb(String.format("SELECT * FROM question WHERE form_id = %d", formId)));
            return response;
        } catch (IOException e) {
            return response;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "columns")
    @ResponseBody
    public Map<String, Object> getColumns(
            @RequestParam(value = "form_id", required = true) Long formId) {

        Map<String, Object> response = new HashMap<>();
        response.put("column_names", null);
        try {
            response.put(
                    "column_names",
                    queryCartodb(String.format("SELECT column_name from information_schema.columns where table_name='raw_data_%d'", formId)));
            return response;
        } catch (IOException e) {
            return response;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "surveys")
    @ResponseBody
    public Map<String, Object> listSurveys() {
        Map<String, Object> response = new HashMap<>();
        response.put("surveys", null);
        try {
            response.put("surveys", queryCartodb("SELECT * FROM survey"));
            return response;
        } catch (IOException e) {
            return response;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "forms")
    @ResponseBody
    public Map<String, Object> getForms(@RequestParam("surveyId") Long surveyId) {
        Map<String, Object> response = new HashMap<>();
        response.put("forms", null);
        try {
            response.put("forms",
                    queryCartodb(String.format("SELECT * FROM form WHERE survey_id=%d", surveyId)));
            return response;
        } catch (IOException e) {
            return response;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "distinct")
    @ResponseBody
    public Map<String, Object> getDistinctValues(@RequestParam("question_name") String questionName, @RequestParam("form_id") Long formId) {
        Map<String, Object> response = new HashMap<>();
        response.put("distinct_values", null);
        try {
            response.put("distinct_values",
                    queryCartodb(String.format("SELECT DISTINCT %s FROM raw_data_%d",questionName , formId)));
            return response;
        } catch (IOException e) {
            return response;
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET, value = "named_maps")
    @ResponseBody
    public Map<String, Object> getNamedMaps() throws IOException {
        HttpURLConnection connection = getConnection("GET", mapsApiURL());
        return objectMapper.readValue(connection.getInputStream(), Map.class);
    }

    @RequestMapping(method = RequestMethod.POST, value = "named_maps")
    @ResponseBody
    public Map<String, Object> createNamedMaps(
            @RequestBody NamedMapPayload payload)
            throws IOException {

        HttpURLConnection connection = getConnection("POST", mapsApiURL());
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        objectMapper.writeValue(writer, buildNamedMap(payload));
        writer.close();

        InputStream result = connection.getInputStream();

        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = objectMapper.readValue(result, Map.class);

        return resultMap;
    }

    @RequestMapping(method = RequestMethod.POST, value = "update_map")
    @ResponseBody
    public Map<String, Object> updateNamedMap(
            @RequestBody NamedMapPayload payload)
            throws IOException {
        URL url = new URL(String.format("http://%s.%s:%s/api/v1/map/named/%s?api_key=%s",
                CDB_ACCOUNT_NAME, CDB_HOST, CDB_TILER_PORT, payload.getName(), CDB_API_KEY));

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");

        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());

        objectMapper.writeValue(writer, buildNamedMap(payload));
        writer.close();

        InputStream result = connection.getInputStream();

        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = objectMapper.readValue(result, Map.class);

        return resultMap;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> queryCartodb(String query) throws IOException {
        HttpURLConnection connection = getConnection("GET", sqlApiURL(query));
        JsonNode jsonNode = objectMapper.readTree(connection.getInputStream());
        return objectMapper.convertValue(jsonNode.get("rows"), List.class);
    }

    private static HttpURLConnection getConnection(String method, URL url)
            throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;

    }

    private static final URL mapsApiURL() throws MalformedURLException {
        return new URL(String.format("http://%s.%s:%s/api/v1/map/named?api_key=%s",
                CDB_ACCOUNT_NAME, CDB_HOST, CDB_TILER_PORT, CDB_API_KEY));
    }

    private static final URL updateMapsApiURL(String named_map) throws MalformedURLException {
        return new URL(String.format("http://%s.%s:%s/api/v1/map/named/%s?api_key=%s",
                CDB_ACCOUNT_NAME, CDB_HOST, CDB_TILER_PORT, named_map, CDB_API_KEY));
    }

    private static final URL sqlApiURL(String query) throws MalformedURLException,
            UnsupportedEncodingException {
        // TODO: Build URL properly without SQL_API
        String urlString = String.format(SQL_API + "?q=%s&api_key=%s",
                URLEncoder.encode(query, "UTF-8"), CDB_API_KEY);
        return new URL(urlString);
    }

    private static final Map<String, Object> buildNamedMap(NamedMapPayload namedMapPayload) {

        Map<String, Object> result = new HashMap<>();
        result.put("name", namedMapPayload.getName());
        result.put("version", "0.0.1");
        Map<String, String> authMap = new HashMap<>();
        authMap.put("method", "open");
        result.put("auth", authMap);
        Map<String, Object> layerGroupMap = new HashMap<>();
        Map<String, Object> optionsMap = new HashMap<>();
        optionsMap.put("cartocss_version", "2.1.1");
        optionsMap.put("cartocss", namedMapPayload.getCartocss());
        optionsMap.put("sql", namedMapPayload.getSql());
        optionsMap.put("interactivity", namedMapPayload.getInteractivity());
        Map<String, Object> layerMap = new HashMap<>();
        layerMap.put("type", "cartodb");
        layerMap.put("options", optionsMap);
        List<Object> layersList = new ArrayList<>();
        layersList.add(layerMap);
        layerGroupMap.put("layers", layersList);
        result.put("layergroup", layerGroupMap);

        return result;
    }
}
