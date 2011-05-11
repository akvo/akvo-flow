package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

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
			String serverBase) {
		this.serverBase = serverBase;
		surveyId = criteria.get(SURVEY_ID);
		PrintWriter pw = null;

		try {
			Object[] results = BulkDataServiceClient.loadQuestions(surveyId,
					serverBase);
			if (results != null) {
				keyList = (List<String>) results[0];
				questionMap = (Map<String, String>) results[1];
				pw = new PrintWriter(fileName);
				writeHeader(pw, questionMap);
				exportInstances(pw, keyList);
			} else {
				System.out.println("Error getting questions");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void export(String serverBase, Long surveyIdentifier, PrintWriter pw) {
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

	private void writeHeader(PrintWriter pw, Map<String, String> questions) {
		pw.print("Instance\tSubmission Date\tSubmitter");
		if (keyList != null) {
			for (String key : keyList) {
				pw.print("\t");
				pw.write(key + "|" + questions.get(key).replace("\n", " "));
			}
		}
		pw.print("\n");
	}

	private void exportInstances(PrintWriter pw, List<String> idList)
			throws Exception {
		Map<String, String> instances = BulkDataServiceClient.fetchInstanceIds(
				surveyId, serverBase);
		if (instances != null) {
			int i = 0;
			for (Entry<String, String> instanceEntry : instances.entrySet()) {
				String instanceId = instanceEntry.getKey();
				String dateString = instanceEntry.getValue();
				if (instanceId != null && instanceId.trim().length() > 0) {
					try {
						Map<String, String> responses = BulkDataServiceClient
								.fetchQuestionResponses(instanceId, serverBase);
						if (responses != null) {
							pw.print(instanceId);
							pw.print("\t");
							pw.print(dateString);
							pw.print("\t");
							SurveyInstanceDto dto = BulkDataServiceClient
									.findSurveyInstance(
											Long.parseLong(instanceId.trim()),
											serverBase);
							if (dto != null) {
								String name = dto.getSubmitterName();
								if (name != null) {
									pw.print(dto.getSubmitterName()
											.replaceAll("\n", " ").trim());
								}
							}
							for (String key : idList) {
								String val = responses.get(key);
								pw.print("\t");
								if (val != null) {
									if (val.contains(SDCARD_PREFIX)) {
										val = IMAGE_PREFIX
												+ val.substring(val
														.indexOf(SDCARD_PREFIX)
														+ SDCARD_PREFIX
																.length());
									}
									val = val.replaceAll(",", " ");
									pw.print(val.replaceAll("\n", " ").trim());
								}
							}

							pw.print("\n");
							i++;
							System.out.println("Row: " + i);
						}
					} catch (Exception ex) {
						System.out.println("Swallow the exception for now and continue");
					}

				}
			}
		}
	}

}
