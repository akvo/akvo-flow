package org.waterforpeople.mapping.analytics;

import java.util.ArrayList;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizationHandler;

public class SurveyDataSummarizationHandler extends DataSummarizationHandler {

	private static final long serialVersionUID = -6697211847598513025L;

	@Override
	protected void initializeSummarization() {
		queueName = "dataSummarization";
		summarizerPath = "/app_worker/datasummarization";
		summarizers = new ArrayList<String>();
		summarizers
				.add("org.waterforpeople.mapping.analytics.SurveyQuestionSummarizer");
	}
}
