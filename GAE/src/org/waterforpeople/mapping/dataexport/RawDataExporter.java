package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

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

	private void writeHeader(PrintWriter pw, Map<String, String> questions) {
		pw.print("Instance\tSubmission Date");
		if (keyList != null) {
			for (String key : keyList) {
				pw.print("\t");
				pw.write(key + "|" + questions.get(key));
			}
		}
		pw.print("\n");
	}

	private void exportInstances(PrintWriter pw, List<String> idList)
			throws Exception {
		Map<String, String> instances = BulkDataServiceClient.fetchInstanceIds(
				surveyId, serverBase);
		if (instances != null) {
			for (Entry<String, String> instanceEntry : instances.entrySet()) {
				String instanceId = instanceEntry.getKey();
				String dateString = instanceEntry.getValue();
				if (instanceId != null && instanceId.trim().length() > 0) {
					Map<String, String> responses = BulkDataServiceClient
							.fetchQuestionResponses(instanceId, serverBase);
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

}
