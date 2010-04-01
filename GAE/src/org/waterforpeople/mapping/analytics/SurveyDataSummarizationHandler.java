package org.waterforpeople.mapping.analytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizationHandler;

/**
 * Summary processor for WFP data
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyDataSummarizationHandler extends DataSummarizationHandler {

	private static final long serialVersionUID = -6697211847598513025L;

	@Override
	protected void initializeSummarization() {
		queueName = "dataSummarization";
		summarizerPath = "/app_worker/datasummarization";
		summarizers = new HashMap<String, List<String>>();
		List<String> surveyQuestionSummarizers = new ArrayList<String>();
		surveyQuestionSummarizers
				.add("org.waterforpeople.mapping.analytics.SurveyQuestionSummarizer");
		summarizers.put("SurveyInstance", surveyQuestionSummarizers);
	}
}
