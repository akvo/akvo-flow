package com.gallatinsystems.framework.analytics.summarization;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

/**
 * 
 * Performs data summarization based on a statically defined list of summarizers
 * (configured via the init params)
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class DataSummarizationHandler extends AbstractRestApiServlet {
	private static final long serialVersionUID = 1689300636699166004L;
	protected List<String> summarizers;
	protected String queueName;
	protected String summarizerPath;

	public DataSummarizationHandler() {
		super();
		initializeSummarization();
	}

	/**
	 * initializes the list of summarizers and the queue names/paths
	 */
	protected abstract void initializeSummarization();

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		DataSummarizationRequest restRequest = new DataSummarizationRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected RestResponse handleRequest(RestRequest request) throws Exception {
		RestResponse response = new RestResponse();
		DataSummarizationRequest summarizationRequest = (DataSummarizationRequest) request;

		int idx = 0;
		if (request.getAction() != null) {
			while (idx < summarizers.size()) {
				if (summarizers.get(idx).trim().equals(
						summarizationRequest.getAction())) {
					break;
				}
				idx++;
			}
		}
		if (idx < summarizers.size()) {
			try {
				Class cls = Class.forName(summarizers.get(idx));
				DataSummarizer summarizer = (DataSummarizer) cls.newInstance();
				summarizer.performSummarization(summarizationRequest
						.getObjectKey(), summarizationRequest.getType());
			} catch (Exception e) {
				log("Could not invoke summarizer", e);
			}
			if (idx < summarizers.size() - 1) {
				summarizationRequest.setAction(summarizers.get(idx + 1));
				// put the item back on the queue with the action updated to the
				// next summarization in the chain
				Queue queue = QueueFactory.getQueue(queueName);
				queue.add(url(summarizerPath).param(
						DataSummarizationRequest.ACTION_PARAM,
						summarizationRequest.getAction()).param(
						DataSummarizationRequest.OBJECT_KEY,
						summarizationRequest.getObjectKey()).param(
						DataSummarizationRequest.OBJECT_TYPE,
						summarizationRequest.getType()));
			}
		}
		return response;

	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// TODO Auto-generated method stub

	}
}
