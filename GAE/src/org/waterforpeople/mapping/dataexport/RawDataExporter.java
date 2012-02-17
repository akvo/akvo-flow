package org.waterforpeople.mapping.dataexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.dataexport.applet.AbstractDataExporter;

/**
 * exports raw data based on a date
 * 
 * @author Christopher Fagiani
 * 
 */
public class RawDataExporter extends AbstractDataExporter {
	private static final String IMAGE_PREFIX = "http://waterforpeople.s3.amazonaws.com/images/";
	private static final String SDCARD_PREFIX = "/sdcard/";

	private String serverBase;
	private String surveyId;
	public static final String SURVEY_ID = "surveyId";
	private Map<String, String> questionMap;
	private List<String> keyList;

	@Override
	@SuppressWarnings("unchecked")
	public void export(Map<String, String> criteria, File fileName,
			String serverBase, Map<String, String> options) {
		this.serverBase = serverBase;
		surveyId = criteria.get(SURVEY_ID);
		Writer pw = null;

		try {
			Object[] results = BulkDataServiceClient.loadQuestions(surveyId,
					serverBase);
			if (results != null) {
				keyList = (List<String>) results[0];
				questionMap = (Map<String, String>) results[1];
				pw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(fileName), "UTF8"));
				writeHeader(pw, questionMap);
				exportInstances(pw, keyList);
			} else {
				System.out.println("Error getting questions");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				try {
					pw.close();
				} catch (IOException e) {
					System.err.println("Could not close writer: " + e);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void export(String serverBase, Long surveyIdentifier, Writer pw) {
		try {
			this.surveyId = surveyIdentifier.toString();
			this.serverBase = serverBase;
			Object[] results = BulkDataServiceClient.loadQuestions(surveyId,
					serverBase);
			if (results != null) {
				keyList = (List<String>) results[0];
				questionMap = (Map<String, String>) results[1];
				writeHeader(pw, questionMap);
				exportInstances(pw, keyList);
			} else {
				System.out.println("Error getting questions");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeHeader(Writer pw, Map<String, String> questions)
			throws Exception {
		pw.write("Instance\tSubmission Date\tSubmitter");
		if (keyList != null) {
			for (String key : keyList) {
				pw.write("\t");
				String questionText = questions.get(key).replaceAll("\n", " ")
						.trim();
				questionText = questionText.replaceAll("\r", " ").trim();
				pw.write(key + "|" + questionText);
			}
		}
		pw.write("\n");
	}

	private void exportInstances(Writer pw, List<String> idList)
			throws Exception {
		Map<String, String> instances = BulkDataServiceClient.fetchInstanceIds(
				surveyId, serverBase);
		if (instances != null) {
			String imagePrefix = IMAGE_PREFIX;
			try {
				imagePrefix = PropertyUtil.getProperty("photo_url_root");
			} catch (Exception e) {
				imagePrefix = IMAGE_PREFIX;
			}
			int i = 0;
			for (Entry<String, String> instanceEntry : instances.entrySet()) {
				String instanceId = instanceEntry.getKey();
				String dateString = instanceEntry.getValue();
				if (instanceId != null && instanceId.trim().length() > 0) {
					try {
						Map<String, String> responses = BulkDataServiceClient
								.fetchQuestionResponses(instanceId, serverBase);
						if (responses != null && responses.size() > 0) {
							pw.write(instanceId);
							pw.write("\t");
							pw.write(dateString);
							pw.write("\t");
							SurveyInstanceDto dto = BulkDataServiceClient
									.findSurveyInstance(
											Long.parseLong(instanceId.trim()),
											serverBase);
							if (dto != null) {
								String name = dto.getSubmitterName();
								if (name != null) {
									pw.write(dto.getSubmitterName()
											.replaceAll("\n", " ").trim());
								}
							}
							for (String key : idList) {
								String val = responses.get(key);
								pw.write("\t");
								if (val != null) {
									if (val.contains(SDCARD_PREFIX)) {
										val = imagePrefix
												+ val.substring(val
														.indexOf(SDCARD_PREFIX)
														+ SDCARD_PREFIX
																.length());
									}
									pw.write(val.replaceAll("\n", " ").trim());
								}
							}

							pw.write("\n");
							i++;
							System.out.println("Row: " + i);
						}
					} catch (Exception ex) {
						System.out
								.println("Swallow the exception for now and continue");
					}

				}
			}
		}
	}
}
