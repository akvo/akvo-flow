package org.waterforpeople.mapping.dataexport;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

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
	private static final String SECTOR_TXT = "Sector/Cell";
	private List<QuestionGroupDto> orderedGroupList;
	private QuestionDto sectorQuestion;

	@Override
	public void export(Map<String, String> criteria, File fileName,
			String serverBase) {
		InputDialog dia = new InputDialog();
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(fileName);
			writeHeader(pw, dia.getDoRollup());
			Map<QuestionGroupDto, List<QuestionDto>> questionMap = loadAllQuestions(
					criteria.get(SurveyRestRequest.SURVEY_ID_PARAM), serverBase);
			if (questionMap.size() > 0) {
				SummaryModel model = buildDataModel(criteria
						.get(SurveyRestRequest.SURVEY_ID_PARAM), serverBase);
				for (QuestionGroupDto group : orderedGroupList) {
					for (QuestionDto question : questionMap.get(group)) {
						pw.print(model.outputQuestion(group.getDisplayName()
								.trim(), question.getText().trim(), question
								.getKeyId().toString(), dia.getDoRollup()));
					}
				}
			} else {
				System.out.println("No questions for survey");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private SummaryModel buildDataModel(String surveyId, String serverBase)
			throws Exception {
		SummaryModel model = new SummaryModel();
		Map<String, String> instanceMap = BulkDataServiceClient
				.fetchInstanceIds(surveyId, serverBase);
		for (String instanceId : instanceMap.keySet()) {
			Map<String, String> responseMap = BulkDataServiceClient
					.fetchQuestionResponses(instanceId, serverBase);
			String sector = "";
			if (sectorQuestion != null) {
				sector = responseMap.get(sectorQuestion.getKeyId().toString());
			}
			for (Entry<String, String> entry : responseMap.entrySet()) {
				model.tallyResponse(entry.getKey(), sector, entry.getValue());
			}
		}
		return model;
	}

	private Map<QuestionGroupDto, List<QuestionDto>> loadAllQuestions(
			String surveyId, String serverBase) throws Exception {
		Map<QuestionGroupDto, List<QuestionDto>> questionMap = new HashMap<QuestionGroupDto, List<QuestionDto>>();
		orderedGroupList = fetchQuestionGroups(serverBase, surveyId);
		for (QuestionGroupDto group : orderedGroupList) {
			List<QuestionDto> questions = fetchQuestions(serverBase, group
					.getKeyId());
			if (questions != null) {
				for (QuestionDto q : questions) {
					if (SECTOR_TXT.equalsIgnoreCase(q.getText())) {
						sectorQuestion = q;
						break;
					}
				}
			}
			questionMap.put(group, questions);
		}
		return questionMap;
	}

	private void writeHeader(PrintWriter pw, boolean isRolledUp) {
		if (isRolledUp) {
			pw
					.println("Question Group\tQuestion\tSector\tResponse\tFrequency\tPercent");
		} else {
			pw
					.println("Question Group\tQuestion\tResponse\tFrequency\tPercent");
		}
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

	private class InputDialog extends JDialog implements ActionListener {

		private static final long serialVersionUID = -2875321125734363515L;

		private JButton yesButton;
		private JButton noButton;
		private JLabel label;
		private boolean doRollup;

		public InputDialog() {
			super();
			yesButton = new JButton("Yes");
			noButton = new JButton("No");
			label = new JLabel("Roll-up by Sector/Cell?");

			JPanel contentPane = new JPanel();
			contentPane.add(label);
			JPanel buttonPane = new JPanel(new GridLayout(1, 2));
			buttonPane.add(yesButton);
			buttonPane.add(noButton);
			contentPane.add(buttonPane);
			yesButton.addActionListener(this);
			noButton.addActionListener(this);
			setContentPane(contentPane);
			setSize(300, 200);
			setTitle("Select Export Options");
			setModal(true);
			setVisible(true);
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == noButton) {
				doRollup = false;
			} else {
				doRollup = true;
			}
			setVisible(false);
		}

		public boolean getDoRollup() {
			return doRollup;
		}
	}

	private class SummaryModel {
		// contains the map of questionIds to all valid responses
		private Map<String, List<String>> responseMap;
		// list of all sectors encountered
		private List<String> sectorList;
		// map of frequency counts of a response. the key is the packed value of
		// questionId+sector+response
		private Map<String, Long> sectorCountMap;
		// map of totals for all responses in a quesitonId+sector
		private Map<String, Long> sectorTotalMap;
		private Map<String, Long> responseCountMap;
		private Map<String, Long> responseTotalMap;

		public SummaryModel() {
			responseMap = new HashMap<String, List<String>>();
			sectorList = new ArrayList<String>();
			sectorCountMap = new HashMap<String, Long>();
			sectorTotalMap = new HashMap<String, Long>();
			responseCountMap = new HashMap<String, Long>();
			responseTotalMap = new HashMap<String, Long>();

		}

		public void tallyResponse(String questionId, String sector,
				String response) {
			addResponse(questionId, response);
			addSector(sector);
			incrementCount(questionId, sector, response);
		}

		private void incrementCount(String questionId, String sector,
				String response) {
			incrementValue(questionId + sector + response, sectorCountMap);
			incrementValue(questionId + sector, sectorTotalMap);
			incrementValue(questionId + response, responseCountMap);
			incrementValue(questionId, responseTotalMap);
		}

		private void incrementValue(String key, Map<String, Long> map) {
			Long val = map.get(key);
			if (val == null) {
				val = new Long(1);
			} else {
				val++;
			}
			map.put(key, val);
		}

		private void addSector(String sector) {
			if (!sectorList.contains(sector)) {
				sectorList.add(sector);
			}
		}

		private void addResponse(String questionId, String response) {
			List<String> responses = responseMap.get(questionId);
			if (responses == null) {
				responses = new ArrayList<String>();
				responseMap.put(questionId, responses);
			}
			if (!responses.contains(response)) {
				responses.add(response);
			}
		}

		public String outputQuestion(String groupName, String questionText,
				String questionId, boolean isRolledUp) {
			String result = null;

			if (isRolledUp) {
				StringBuilder buffer = new StringBuilder();
				for (String sector : sectorList) {
					buffer.append(outputResponses(groupName, questionText,
							sector, questionId, isRolledUp));
				}
				result = buffer.toString();
			} else {
				result = outputResponses(groupName, questionText, "",
						questionId, isRolledUp);
			}
			return result;
		}

		private String outputResponses(String groupName, String questionText,
				String sector, String questionId, boolean isRolledUp) {
			StringBuilder buffer = new StringBuilder();
			if (responseMap.get(questionId) != null) {
				for (String response : responseMap.get(questionId)) {
					Long count = null;
					if (isRolledUp) {
						count = sectorCountMap.get(questionId + sector + response);
					} else {
						count = responseCountMap.get(questionId + response);
					}
					String countString = "0";
					String pctString = "0";
					if (count != null) {
						Long total = null;
						if (isRolledUp) {
							total = sectorTotalMap.get(questionId + sector);
						} else {
							total = responseTotalMap.get(questionId);
						}
						pctString = PCT_FMT.format((double) count
								/ (double) total);
						countString = count.toString();
					}
					buffer.append(groupName).append("\t").append(questionText)
							.append("\t");
					if (isRolledUp) {
						buffer.append(sector).append("\t");
					}
					buffer.append(response).append("\t").append(countString)
							.append("\t").append(pctString).append("\n");
				}
			}
			return buffer.toString();
		}
	}

}
