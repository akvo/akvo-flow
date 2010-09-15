package org.waterforpeople.mapping.dataexport.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.web.dto.DataBackoutRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;

/**
 * client code for calling the apis for data processing on the server
 * 
 * @author Christopher Fagiani
 * 
 */
public class BulkDataServiceClient {
	private static final String DATA_SERVLET_PATH = "/databackout?action=";
	public static final String RESPONSE_KEY = "dtoList";
	private static final String SURVEY_SERVLET_PATH = "/surveyrestapi?action=";

	/**
	 * lists all responses from the server for a surveyInstance submission as a
	 * map of values keyed on questionId
	 * 
	 * @param instanceId
	 * @param serverBase
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> fetchQuestionResponses(String instanceId,
			String serverBase) throws Exception {
		String instanceValues = fetchDataFromServer(serverBase
				+ DATA_SERVLET_PATH
				+ DataBackoutRequest.LIST_INSTANCE_RESPONSE_ACTION + "&"
				+ DataBackoutRequest.SURVEY_INSTANCE_ID_PARAM + "="
				+ instanceId);
		return parseInstanceValues(instanceValues);
	}

	/**
	 * survey instance ids and their submission dates. Map keys are the
	 * instances and values are the dates.
	 * 
	 * @param surveyId
	 * @param serverBase
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> fetchInstanceIds(String surveyId,
			String serverBase) throws Exception {
		Map<String, String> values = new HashMap<String, String>();
		String instanceString = fetchDataFromServer(serverBase
				+ DATA_SERVLET_PATH + DataBackoutRequest.LIST_INSTANCE_ACTION
				+ "&" + DataBackoutRequest.SURVEY_ID_PARAM + "=" + surveyId
				+ "&" + DataBackoutRequest.INCLUDE_DATE_PARAM + "=true");
		if (instanceString != null) {
			StringTokenizer strTok = new StringTokenizer(instanceString, ",");
			while (strTok.hasMoreTokens()) {
				String instanceId = strTok.nextToken();
				String dateString = "";
				if (instanceId.contains("|")) {
					dateString = instanceId
							.substring(instanceId.indexOf("|") + 1);
					instanceId = instanceId.substring(0, instanceId
							.indexOf("|"));
				}
				values.put(instanceId, dateString);
			}
		}
		return values;
	}

	/**
	 * method to parse SurveyInstance response values
	 * 
	 * @param data
	 * @return
	 */
	private static Map<String, String> parseInstanceValues(String data) {
		Map<String, String> responseMap = new HashMap<String, String>();
		if (data != null) {
			StringTokenizer strTok = new StringTokenizer(data, ",\n");
			while (strTok.hasMoreTokens()) {
				String key = strTok.nextToken();
				String val = strTok.nextToken();
				String oldVal = responseMap.get(key);
				if (oldVal != null) {
					if (val != null) {
						if (oldVal.trim().length() < val.trim().length()) {
							responseMap.put(key, val);
						}
					}
				} else {
					responseMap.put(key, val);
				}
			}
		}
		return responseMap;
	}

	/**
	 * loads full details for a single question (options, translations, etc)
	 * 
	 * @param serverBase
	 * @param questionId
	 * @return
	 */
	public static QuestionDto loadQuestionDetails(String serverBase,
			Long questionId) throws Exception {
		List<QuestionDto> dtoList = parseQuestions(fetchDataFromServer(serverBase
				+ SURVEY_SERVLET_PATH
				+ SurveyRestRequest.GET_QUESTION_DETAILS_ACTION
				+ "&"
				+ SurveyRestRequest.QUESTION_ID_PARAM + "=" + questionId));
		if (dtoList != null && dtoList.size() > 0) {
			return dtoList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * returns an array containing 2 elements: the first is an ordered list of
	 * questionIds (in the order they appear in the survey) and the second
	 * element is a map of questions (keyed on id)
	 * 
	 * @param surveyId
	 * @param serverBase
	 * @return
	 * @throws Exception
	 */
	public static Object[] loadQuestions(String surveyId, String serverBase)
			throws Exception {
		Object[] results = new Object[2];
		Map<String, String> questions = new HashMap<String, String>();
		List<QuestionGroupDto> groups = fetchQuestionGroups(serverBase,
				surveyId);
		List<String> keyList = new ArrayList<String>();
		if (groups != null) {
			for (QuestionGroupDto group : groups) {
				List<QuestionDto> questionDtos = fetchQuestions(serverBase,
						group.getKeyId());
				if (questionDtos != null) {
					for (QuestionDto question : questionDtos) {
						keyList.add(question.getKeyId().toString());
						questions.put(question.getKeyId().toString(), question
								.getText());
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
			Long groupId) throws Exception {
		return parseQuestions(fetchDataFromServer(serverBase
				+ SURVEY_SERVLET_PATH + SurveyRestRequest.LIST_QUESTION_ACTION
				+ "&" + SurveyRestRequest.QUESTION_GROUP_ID_PARAM + "="
				+ groupId));
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
			String surveyId) throws Exception {
		return parseQuestionGroups(fetchDataFromServer(serverBase
				+ SURVEY_SERVLET_PATH + SurveyRestRequest.LIST_GROUP_ACTION
				+ "&" + SurveyRestRequest.SURVEY_ID_PARAM + "=" + surveyId));
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
						if (json.has("code")) {
							dto.setCode(json.getString("code"));
						}
						if (json.has("keyId")) {
							dto.setKeyId(json.getLong("keyId"));
						}
						dtoList.add(dto);
					} catch (Exception e) {
						System.out.println("Error in json parsing: " + e);
						e.printStackTrace();
					}
				}
			}
		}
		return dtoList;
	}

	/**
	 * parses question responses into QuesitonDto objects
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private static List<QuestionDto> parseQuestions(String response)
			throws Exception {
		List<QuestionDto> dtoList = new ArrayList<QuestionDto>();
		JSONArray arr = getJsonArray(response);
		if (arr != null) {
			for (int i = 0; i < arr.length(); i++) {
				JSONObject json = arr.getJSONObject(i);
				if (json != null) {
					QuestionDto dto = new QuestionDto();
					try {
						if (json.has("text")) {
							dto.setText(json.getString("text"));
						}
						if (json.has("keyId")) {
							dto.setKeyId(json.getLong("keyId"));
						}
						if (json.has("translationMap")
								&& !JSONObject.NULL.equals(json
										.get("translationMap"))) {
							dto.setTranslationMap(parseTranslations(json
									.getJSONObject("translationMap")));
						}
						if (json.has("optionContainerDto")
								&& !JSONObject.NULL.equals(json
										.get("optionContainerDto"))) {
							OptionContainerDto container = new OptionContainerDto();
							JSONObject contJson = json
									.getJSONObject("optionContainerDto");
							JSONArray optArray = contJson
									.getJSONArray("optionsList");
							if (optArray != null) {
								for (int j = 0; j < optArray.length(); j++) {
									JSONObject optJson = optArray
											.getJSONObject(j);
									QuestionOptionDto opt = new QuestionOptionDto();
									opt.setKeyId(optJson.getLong("keyId"));
									opt.setText(optJson.getString("text"));
									if (optJson.has("translationMap")
											&& !JSONObject.NULL.equals(json
													.get("translationMap"))) {
										opt
												.setTranslationMap(parseTranslations(json
														.getJSONObject("translationMap")));
									}
									container.addQuestionOption(opt);
								}
							}
							dto.setOptionContainerDto(container);
						}

						dtoList.add(dto);
					} catch (Exception e) {
						System.out.println("Error in json parsing: " + e);
						e.printStackTrace();
					}
				}
			}
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
				tDto.setLangCode(lang);
				tDto.setText(transObj.getString("langCode"));
				translationMap.put(lang, tDto);
			}
		}
		return translationMap;
	}

	/**
	 * invokes a remote REST api
	 * 
	 * @param fullUrl
	 * @return
	 * @throws Exception
	 */
	protected static String fetchDataFromServer(String fullUrl)
			throws Exception {
		BufferedReader reader = null;
		String result = null;
		try {
			URL url = new URL(fullUrl);
			System.out.println("Calling: " + url.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);

			reader = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
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
	 * converts the string into a JSON array object.
	 */
	private static JSONArray getJsonArray(String response) throws Exception {
		System.out.println("response: " + response);
		if (response != null) {
			JSONObject json = new JSONObject(response);
			if (json != null) {
				return json.getJSONArray(RESPONSE_KEY);
			}
		}
		return null;
	}

}
