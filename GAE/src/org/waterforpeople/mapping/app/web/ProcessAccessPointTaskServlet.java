package org.waterforpeople.mapping.app.web;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.analytics.AccessPointMetricSummarizer;
import org.waterforpeople.mapping.app.web.dto.DeleteTaskRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

public class ProcessAccessPointTaskServlet extends AbstractRestApiServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5116204674096200848L;
	private static final String OBJECT_TASK_URL = "/app_worker/processaccesspointtaskservlet";
	private static final String ACCESSPOINT_QUEUE_NAME = "accesspointqueue";

	/**
	 * 
	 */

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new DeleteTaskRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		DeleteTaskRequest dtReq = (DeleteTaskRequest) req;
		String newCursor = null;
		if (dtReq.getKey().equals("secret")) {
			final String kind = dtReq.getObjectName();

			int deleted_count = 0;
			boolean is_finished = false;

			final DatastoreService dss = DatastoreServiceFactory
					.getDatastoreService();
			final long start = System.currentTimeMillis();

			AccessPointMetricSummarizer apms = new AccessPointMetricSummarizer();
			final Query query = new Query(kind);
			int limit = 10;
			if(dtReq.getCursor()!=null){
				limit=150;
			}
			
			FetchOptions fetchOptions = FetchOptions.Builder.withLimit(limit);
			if (dtReq.getCursor() != null) {
				fetchOptions.startCursor(Cursor.fromWebSafeString(dtReq
						.getCursor()));
			}
			
			query.setKeysOnly();

			ArrayList<Key> keys = new ArrayList<Key>();
			QueryResultList<Entity> results = dss.prepare(query)
					.asQueryResultList(fetchOptions);
			newCursor = results.getCursor().toWebSafeString();

			if (results.isEmpty() || results == null) {
				is_finished = true;
			} else {
				for (Entity entity : results) {
					apms.performSummarization(
							String.valueOf(entity.getKey().getId()), null,
							null, null, null);
					++deleted_count;
				}
			}
			System.err.println("*** processed " + deleted_count
					+ " entities form " + kind);

			if (is_finished) {
				System.err.println("*** process job for " + kind
						+ " is completed.");
			} else {
				final Integer taskcount;
				final String tcs = dtReq.getTaskCount();
				if (tcs == null) {
					taskcount = 0;
				} else {
					taskcount = Integer.parseInt(tcs) + 1;
				}

				Queue deleteQueue = QueueFactory
						.getQueue(ACCESSPOINT_QUEUE_NAME);
				deleteQueue.add(url(OBJECT_TASK_URL)
						.param(DeleteTaskRequest.OBJECT_PARAM, kind + "")
						.param(DeleteTaskRequest.KEY_PARAM, dtReq.getKey())
						.param(DeleteTaskRequest.CURSOR_PARAM, newCursor)
						.param(DeleteTaskRequest.TASK_COUNT_PARAM,
								taskcount.toString()));

				System.err.println("*** process task # " + taskcount + " for "
						+ kind + " is queued.");

			}
		}

		return null;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		if (resp != null) {
			getResponse().getWriter().println("ok");
		}
	}

}
