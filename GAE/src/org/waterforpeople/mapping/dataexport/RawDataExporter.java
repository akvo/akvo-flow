package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.web.dto.DataBackoutRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;

/**
 * exports raw data based on a date
 * 
 * @author Christopher Fagiani
 * 
 */
public class RawDataExporter extends AbstractDataExporter {
	private static final String DATA_SERVLET_PATH = "/databackout?action=";
	public static final String RESPONSE_KEY = "dtoList";
	private static final String SURVEY_SERVLET_PATH = "/surveyrestapi?action=";

	private static final String IMAGE_PREFIX = "http://waterforpeople.s3.amazonaws.com/images/";
	private static final String SDCARD_PREFIX = "/sdcard/";

	private String serverBase;
	private String surveyId;
	public static final String SURVEY_ID = "surveyId";
	private Map<String, String> questionMap;
	private List<String> keyList;

	@Override
	public void export(Map<String, String> criteria, File fileName,
			String serverBase) {
		this.serverBase = serverBase;
		surveyId = criteria.get(SURVEY_ID);
		PrintWriter pw = null;

		try {
			questionMap = loadQuestions();
			pw = new PrintWriter(fileName);
			writeHeader(pw, questionMap);
			exportInstances(pw, keyList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	private void writeHeader(PrintWriter pw, Map<String, String> questions) {
		pw.print("Instance\tSubmission Date");
		if (keyList != null) {
			for (String key : keyList) {
				pw.print("\t");
				pw.write(questions.get(key));
			}
		}
		pw.print("\n");
	}

	private Map<String, String> loadQuestions() throws Exception {
		Map<String, String> questions = new HashMap<String, String>();
		List<QuestionGroupDto> groups = fetchQuestionGroups(serverBase,
				surveyId);
		keyList = new ArrayList<String>();
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
		return questions;
	}

	private List<QuestionDto> fetchQuestions(String serverBase, Long groupId)
			throws Exception {
		return parseQuestions(fetchDataFromServer(serverBase
				+ SURVEY_SERVLET_PATH + SurveyRestRequest.LIST_QUESTION_ACTION
				+ "&" + SurveyRestRequest.QUESTION_GROUP_ID_PARAM + "="
				+ groupId));
	}

	private List<QuestionGroupDto> fetchQuestionGroups(String serverBase,
			String surveyId) throws Exception {
		return parseQuestionGroups(fetchDataFromServer(serverBase
				+ SURVEY_SERVLET_PATH + SurveyRestRequest.LIST_GROUP_ACTION
				+ "&" + SurveyRestRequest.SURVEY_ID_PARAM + "=" + surveyId));
	}

	private List<QuestionGroupDto> parseQuestionGroups(String response)
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

	private List<QuestionDto> parseQuestions(String response) throws Exception {
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

	private void exportInstances(PrintWriter pw, List<String> idList)
			throws Exception {
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
				if (instanceId != null && instanceId.trim().length() > 0) {
					String instanceValues = fetchDataFromServer(serverBase
							+ DATA_SERVLET_PATH
							+ DataBackoutRequest.LIST_INSTANCE_RESPONSE_ACTION
							+ "&" + DataBackoutRequest.SURVEY_INSTANCE_ID_PARAM
							+ "=" + instanceId);
					Map<String, String> responses = parseInstanceValues(instanceValues);
					if (responses != null) {
						pw.print(instanceId);
						pw.print("\t");
						pw.print(dateString);
						for (String key : idList) {
							String val = responses.get(key);
							pw.print("\t");
							if (val != null) {
								if (val.contains(SDCARD_PREFIX)) {
									val = IMAGE_PREFIX
											+ val.substring(val
													.indexOf(SDCARD_PREFIX)
													+ SDCARD_PREFIX.length());
								}
								pw.print(val.trim());
							}
						}
						pw.print("\n");
					}
				}
			}
		}
	}

	private Map<String, String> parseInstanceValues(String data) {
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
	 * converts the string into a JSON array object.
	 */
	private JSONArray getJsonArray(String response) throws Exception {
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
