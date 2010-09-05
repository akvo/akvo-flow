package org.waterforpeople.mapping.analytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizationHandler;

/**
 * Task servlet to handle bulk updates of survey data.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyDataUpdateHandler extends DataSummarizationHandler {
	private static final long serialVersionUID = 3873215496111195139L;

	/**
	 * installs the list of summarizers that will run for various data types
	 */
	@Override
	protected void initializeSummarization() {
		queueName = "dataUpdate";
		summarizerPath = "/app_worker/dataupdate";
		summarizers = new HashMap<String, List<String>>();
		List<String> nameUpdateSummarizers = new ArrayList<String>();
		nameUpdateSummarizers
				.add("org.waterforpeople.mapping.analytics.NameQuestionDataCleanser");
		summarizers.put("NameQuestionFix", nameUpdateSummarizers);

		List<String> questionUpdateSummarizers = new ArrayList<String>();
		questionUpdateSummarizers
				.add("org.waterforpeople.mapping.analytics.SurveyQuestionSummaryUpdater");
		summarizers.put("QuestionDataChange", questionUpdateSummarizers);

		List<String> accessPointUpdaters = new ArrayList<String>();
		accessPointUpdaters
				.add("org.waterforpeople.mapping.analytics.AccessPointUpdater");
		summarizers.put("AccessPointChange", accessPointUpdaters);

		List<String> accessPointSummaryUpdaters = new ArrayList<String>();
		accessPointSummaryUpdaters
				.add("org.waterforpeople.mapping.analytics.AccessPointStatusUpdater");
		summarizers.put("AccessPointSummaryChange", accessPointSummaryUpdaters);

	}
}
