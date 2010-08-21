package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryDto;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;

/**
 * 
 * This exporter will write the survey "descriptive statistics" report to a
 * file. These stats include a breakdown of question response frequencies for
 * each question in a survey.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveySummaryExporter extends AbstractDataExporter {

	public static final String RESPONSE_KEY = "dtoList";
	private static final String SERVLET_URL = "/surveyrestapi?action=";
	private static final NumberFormat PCT_FMT = new DecimalFormat("0.00");

	@Override
	public void export(Map<String, String> criteria, File fileName,
			String serverBase) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(fileName);
			writeHeader(pw);

			if (serverBase.trim().endsWith("/")) {
				serverBase = serverBase.trim().substring(0,
						serverBase.lastIndexOf("/"));
			}

			List<QuestionGroupDto> groups = fetchQuestionGroups(serverBase,
					criteria.get(SurveyRestRequest.SURVEY_ID_PARAM));
			if (groups != null) {
				for (QuestionGroupDto group : groups) {
					List<QuestionDto> questions = fetchQuestions(serverBase,
							group.getKeyId());
					if (questions != null) {
						for (QuestionDto question : questions) {
							List<SurveySummaryDto> summaries = fetchSurveySummaries(
									serverBase, question.getKeyId());
							if (summaries != null) {
								int total = 0;
								for (int i = 0; i < summaries.size(); i++) {
									if (summaries.get(i).getCount() != null) {
										total += summaries.get(i).getCount()
												.intValue();
									}
								}
								// now that we have the totals, we can calculate
								// the percent. So write the data
								for (SurveySummaryDto summary : summaries) {
									int count = 0;
									if (summary.getCount() != null) {
										count = summary.getCount().intValue();
									}
									double pct = (((double) count) / ((double) total)) * 100d;
									pw
											.println((group.getDisplayName() != null ? group
													.getDisplayName().trim()
													: "")
													+ "\t"
													+ (question.getText() != null ? question
															.getText().trim()
															: "")
													+ "\t"
													+ (summary
															.getResponseText() != null ? summary
															.getResponseText()
															.trim()
															: "")
													+ "\t"
													+ count
													+ "\t"
													+ PCT_FMT.format(pct));
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private void writeHeader(PrintWriter pw) {
		pw.println("Question Group\tQuestion\tResponse\tFrequency\tPercent");
	}

	private List<SurveySummaryDto> fetchSurveySummaries(String serverBase,
			Long questionId) throws Exception {
		return parseSummaries(fetchDataFromServer(serverBase + SERVLET_URL
				+ SurveyRestRequest.GET_SUMMARY_ACTION + "&"
				+ SurveyRestRequest.QUESTION_ID_PARAM + "=" + questionId));
	}

	private List<QuestionDto> fetchQuestions(String serverBase, Long groupId)
			throws Exception {
		return parseQuestions(fetchDataFromServer(serverBase + SERVLET_URL
				+ SurveyRestRequest.LIST_QUESTION_ACTION + "&"
				+ SurveyRestRequest.QUESTION_GROUP_ID_PARAM + "=" + groupId));
	}

	private List<QuestionGroupDto> fetchQuestionGroups(String serverBase,
			String surveyId) throws Exception {
		return parseQuestionGroups(fetchDataFromServer(serverBase + SERVLET_URL
				+ SurveyRestRequest.LIST_GROUP_ACTION + "&"
				+ SurveyRestRequest.SURVEY_ID_PARAM + "=" + surveyId));
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

	private List<SurveySummaryDto> parseSummaries(String response)
			throws Exception {
		List<SurveySummaryDto> dtoList = new ArrayList<SurveySummaryDto>();
		JSONArray arr = getJsonArray(response);
		if (arr != null) {
			for (int i = 0; i < arr.length(); i++) {
				JSONObject json = arr.getJSONObject(i);
				if (json != null) {
					SurveySummaryDto dto = new SurveySummaryDto();
					try {
						if (json.has("responseText")) {
							dto.setResponseText(json.getString("responseText"));
						}
						if (json.has("count")) {
							dto.setCount(json.getLong("count"));
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
