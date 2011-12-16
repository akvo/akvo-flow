package org.waterforpeople.mapping.analytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizationHandler;

/**
 * Summary processor for WFP data. This class maintains a list of summarizer
 * classes that will be instantiated and invoked (by the parent class) each time
 * a summarization request is received.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyDataSummarizationHandler extends DataSummarizationHandler {

	private static final long serialVersionUID = -6697211847598513025L;

	/**
	 * installs the list of summarizers that will run for various data types
	 */
	@Override
	protected void initializeSummarization() {
		queueName = "dataSummarization";
		summarizerPath = "/app_worker/datasummarization";
		summarizers = new HashMap<String, List<String>>();

		List<String> surveyQuestionSummarizers = new ArrayList<String>();
		surveyQuestionSummarizers
				.add("org.waterforpeople.mapping.analytics.SurveyQuestionSummarizer");
		surveyQuestionSummarizers
				.add("org.waterforpeople.mapping.analytics.SurveyInstanceSummarizer");
		summarizers.put("SurveyInstance", surveyQuestionSummarizers);

		List<String> apSummarizers = new ArrayList<String>();
		apSummarizers
				.add("org.waterforpeople.mapping.analytics.CommunityLocationSummarizer");
		apSummarizers
				.add("org.waterforpeople.mapping.analytics.AccessPointStatusSummarizer");
		apSummarizers
				.add("org.waterforpeople.mapping.analytics.AccessPointMetricSummarizer");
		apSummarizers.add("org.waterforpeople.mapping.analytics.MapSummarizer");

		summarizers.put("AccessPoint", apSummarizers);

		List<String> featureSummarizers = new ArrayList<String>();
		featureSummarizers
				.add("org.waterforpeople.mapping.analytics.SubCountrySummarizer");
		summarizers.put("OGRFeature", featureSummarizers);
	}
}
