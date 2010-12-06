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
import org.waterforpeople.mapping.app.gwt.client.devicefiles.DeviceFilesDto;
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
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;

import com.google.appengine.api.datastore.Text;

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
	private static final String DEVICE_FILES_SERVLET_PATH = "/devicefilesrestapi?action=";

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

	public static List<DeviceFilesDto> fetchDeviceFiles(String statusCode,
			String serverBase) throws Exception {
		List<DeviceFilesDto> dfDto = new ArrayList<DeviceFilesDto>();
		String queryString = serverBase + DEVICE_FILES_SERVLET_PATH
				+ DeviceFileRestRequest.LIST_DEVICE_FILES_ACTION + "&"
				+ DeviceFileRestRequest.PROCESSED_STATUS_PARAM + "="
				+ statusCode;
		for(DeviceFilesDto dto :parseDeviceFiles(fetchDataFromServer(queryString))){
			dfDto.add(dto);
		}

		String cursor = null;

		return dfDto;
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
					instanceId = instanceId.substring(0,
							instanceId.indexOf("|"));
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
			StringTokenizer lines = new StringTokenizer(data, "\n");
			if (lines != null) {
				while (lines.hasMoreTokens()) {
					StringTokenizer strTok = new StringTokenizer(
							lines.nextToken(), ",");
					String key = null;
					String val = "";
					if (strTok.hasMoreTokens()) {
						key = strTok.nextToken();
					}
					if (strTok.hasMoreTokens()) {
						val = strTok.nextToken();
					}
					if (key != null && key.trim().length() > 0) {
						String oldVal = responseMap.get(key);
						if (oldVal != null) {
							if (val != null) {
								if (oldVal.trim().length() < val.trim()
										.length()) {
									responseMap.put(key, val);
								}
							}
						} else {
							responseMap.put(key, val);
						}
					}
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

		List<QuestionDto> dtoList = null;

		dtoList = parseQuestions(fetchDataFromServer(serverBase
				+ SURVEY_SERVLET_PATH
				+ SurveyRestRequest.GET_QUESTION_DETAILS_ACTION + "&"
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
						questions.put(question.getKeyId().toString(),
								question.getText());
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
	 * gets a surveyInstance from the server for a specific id
	 * 
	 * @param id
	 * @param serverBase
	 * @return
	 * @throws Exception
	 */
	public static SurveyInstanceDto findSurveyInstance(Long id,
			String serverBase) throws Exception {
		return parseSurveyInstance(fetchDataFromServer(serverBase
				+ SURVEY_SERVLET_PATH
				+ SurveyRestRequest.GET_SURVEY_INSTANCE_ACTION + "&"
				+ SurveyRestRequest.INSTANCE_PARAM + "=" + id));
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
	 * gets question groups from the server for a specific survey
	 * 
	 * @param serverBase
	 * @param surveyId
	 * @return
	 * @throws Exception
	 */
	public static List<SurveyGroupDto> fetchSurveyGroups(String serverBase)
			throws Exception {
		return parseSurveyGroups(fetchDataFromServer(serverBase
				+ SURVEY_SERVLET_PATH
				+ SurveyRestRequest.LIST_SURVEY_GROUPS_ACTION));
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
			String serverBase) throws Exception {
		return parseSurveys(fetchDataFromServer(serverBase
				+ SURVEY_SERVLET_PATH + SurveyRestRequest.LIST_SURVEYS_ACTION
				+ "&" + SurveyRestRequest.SURVEY_GROUP_ID_PARAM + "="
				+ surveyGroupId));
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
					if (json.has("userID")) {
						dto.setUserID(json.getLong("userID"));
					}
					if (json.has("submitterName")) {
						dto.setSubmitterName(json.getString("submitterName"));
					}
					if (json.has("deviceIdentifier")) {
						dto.setDeviceIdentifier(json
								.getString("deviceIdentifier"));
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
						if (json.has("code")) {
							dto.setCode(json.getString("code"));
						}
						if (json.has("keyId")) {
							dto.setKeyId(json.getLong("keyId"));
						}
						if (json.has("displayName")) {
							dto.setName(json.getString("displayName"));
						}
						if (json.has("description")) {
							dto.setDescription(json.getString("description"));
						}
						if (json.has("order")) {
							dto.setOrder(json.getInt("order"));
						}
						if (json.has("path")) {
							dto.setPath(json.getString("path"));
						}
						if (json.has("surveyId")) {
							dto.setSurveyId(json.getLong("surveyId"));
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
						if (json.has("code")) {
							dto.setCode(json.getString("code"));
						}
						if (json.has("keyId")) {
							dto.setKeyId(json.getLong("keyId"));
						}
						if (json.has("displayName")) {
							dto.setName(json.getString("displayName"));
						}
						if (json.has("description")) {
							dto.setDescription(json.getString("description"));
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
						if (json.has("code")) {
							dto.setCode(json.getString("code"));
						}
						if (json.has("keyId")) {
							dto.setKeyId(json.getLong("keyId"));
						}
						if (json.has("name")) {
							dto.setName(json.getString("name"));
						}
						if (json.has("description")) {
							dto.setDescription(json.getString("description"));
						}
						if (json.has("status")) {
							dto.setStatus(json.getString("status"));
						}
						if (json.has("path")) {
							dto.setPath(json.getString("path"));
						}
						if (json.has("surveyGroupId")) {
							dto.setSurveyGroupId(json.getLong("surveyGroupId"));
						}
						if (json.has("version")) {
							dto.setVersion(json.getString("version"));
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

	private static List<DeviceFilesDto> parseDeviceFiles(String response)
			throws Exception {
		if (response.startsWith("{")) {
			List<DeviceFilesDto> dtoList = new ArrayList<DeviceFilesDto>();
			JSONArray arr = getJsonArray(response);
			if (arr != null) {
				
				for (int i = 0; i < arr.length(); i++) {
					DeviceFilesDto dto = new DeviceFilesDto();
					JSONObject json = arr.getJSONObject(i);
					if (json != null) {
						if(json.has("processingMessage")){
							String x = json.getString("processingMessage");
							dto.setProcessingMessage(x);
						}
						if(json.has("phoneNumber")){
							String x = json.getString("phoneNumber");
							dto.setPhoneNumber(x);
						}
						if(json.has("processedStatus")){
							String x = json.getString("processedStatus");
							dto.setProcessedStatus(x);
						}
						if(json.has("checksum")){
							String x = json.getString("checksum");
							dto.setChecksum(x);
						}
						if(json.has("processDate")){
							String x = json.getString("processDate");
							dto.setProcessDate(x);
						}
						if(json.has("URI")){
							String x = json.getString("URI");
							dto.setURI(x);
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
	 * parses question responses into QuesitonDto objects
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

							if (json.has("mandatoryFlag")) {
								if (json.getString("mandatoryFlag") != null)
									dto.setMandatoryFlag(Boolean.parseBoolean(json
											.getString("mandatoryFlag")));
								else
									dto.setMandatoryFlag(false);
							}
							if (json.has("allowMultipleFlag")) {
								if (json.getString("allowMultipleFlag") != null)
									dto.setAllowMultipleFlag(Boolean.parseBoolean(json
											.getString("allowMultipleFlag")));
								else
									dto.setAllowMultipleFlag(false);
							}
							if (json.has("allowOtherFlag")) {
								if (json.getString("allowOtherFlag") != null)
									dto.setAllowOtherFlag(Boolean.parseBoolean(json
											.getString("allowOtherFlag")));
								else
									dto.setAllowOtherFlag(null);
							}
							if (json.has("order")) {
								dto.setOrder(json.getInt("order"));
							}
							if (json.has("questionGroupId")) {
								dto.setQuestionGroupId(json
										.getLong("questionGroupId"));
							}
							if (json.has("tip")) {
								dto.setTip(json.getString("tip"));
							}
							if (json.has("path")) {
								dto.setPath(json.getString("path"));
							}
							if (json.has("text")) {
								dto.setText(json.getString("text"));
							}
							if (json.has("keyId")) {
								dto.setKeyId(json.getLong("keyId"));
							}
							if (json.has("mandatoryFlag")) {
								dto.setMandatoryFlag(Boolean.parseBoolean(json
										.getString("mandatoryFlag")));
							}
							if (json.has("validationRule")) {
								dto.setValidationRule(json
										.getString("validationRule"));
							}
							if (json.has("translationMap")
									&& !JSONObject.NULL.equals(json
											.get("translationMap"))) {
								dto.setTranslationMap(parseTranslations(json
										.getJSONObject("translationMap")));
							}
							if (json.has("questionTypeString")
									&& json.getString("questionTypeString") != null) {
								dto.setType(QuestionDto.QuestionType.valueOf(json
										.getString("questionTypeString")));
							}

							if (json.has("optionContainerDto")
									&& !JSONObject.NULL.equals(json
											.get("optionContainerDto"))) {
								OptionContainerDto container = new OptionContainerDto();
								JSONObject contJson = json
										.getJSONObject("optionContainerDto");
								if (contJson.has("optionsList")
										&& !JSONObject.NULL.equals(contJson
												.get("optionsList"))) {
									JSONArray optArray = contJson
											.getJSONArray("optionsList");
									if (optArray != null) {
										for (int j = 0; j < optArray.length(); j++) {
											JSONObject optJson = optArray
													.getJSONObject(j);
											QuestionOptionDto opt = new QuestionOptionDto();
											opt.setKeyId(optJson
													.getLong("keyId"));
											opt.setText(optJson
													.getString("text"));
											opt.setOrder(optJson
													.getInt("order"));
											if (optJson.has("translationMap")
													&& !JSONObject.NULL
															.equals(optJson
																	.get("translationMap"))) {
												opt.setTranslationMap(parseTranslations(optJson
														.getJSONObject("translationMap")));
											}
											container.addQuestionOption(opt);
										}
									}
									dto.setOptionContainerDto(container);
								}
								if (json.has("questionDependency")
										&& !JSONObject.NULL.equals(json
												.get("questionDependency"))) {
									QuestionDependencyDto dep = new QuestionDependencyDto();
									JSONObject depJson = json
											.getJSONObject("questionDependency");
									dep.setQuestionId(depJson
											.getLong("questionId"));
									dep.setAnswerValue(depJson
											.getString("answerValue"));
									dto.setQuestionDependency(dep);
								}
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
		} else
			return null;
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

			reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
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
