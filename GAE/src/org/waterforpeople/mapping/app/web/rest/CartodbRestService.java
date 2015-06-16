
package org.waterforpeople.mapping.app.web.rest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gallatinsystems.common.util.PropertyUtil;

@Controller
@RequestMapping("/cartodb")
public class CartodbRestService {

    private static final String API_KEY = PropertyUtil.getProperty("cartodbApiKey");
    private static final String SQL_API = PropertyUtil.getProperty("cartodbSqlApi");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.GET, value = "answers")
    @ResponseBody
    public Map<String, Object> getAnswers(@RequestParam("dataPointId") Long dataPointId,
            @RequestParam("surveyId") Long surveyId) {

        Map<String, Object> response = new HashMap<>();
        response.put("answers", null);

        try {
            String formIdQuery = String.format("SELECT id FROM form WHERE survey_id=%d", surveyId);
            List<Map<String, Object>> formIdResponse = queryCartodb(formIdQuery);
            if (!formIdResponse.isEmpty()) {
                Integer formId = (Integer) formIdResponse.get(0).get("id");
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

    @RequestMapping(method = RequestMethod.GET, value = "point_data")
    @ResponseBody
    public Map<String, Object> getPointData(@RequestParam("dataPointId") Long dataPointId,
            @RequestParam("formId") Long formId) {

        Map<String, Object> response = new HashMap<>();
        response.put("answers", null);

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

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> queryCartodb(String query) throws IOException {
        String urlString = String.format(SQL_API + "?q=%s&api_key=%s",
                URLEncoder.encode(query, "UTF-8"), API_KEY);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        JsonNode jsonNode = objectMapper.readTree(connection.getInputStream());

        return objectMapper.convertValue(jsonNode.get("rows"), List.class);
    }
}
