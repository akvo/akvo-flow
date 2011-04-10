package org.waterforpeople.mapping.app.web;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.DeleteTaskRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

public class DeleteObjectTaskServlet extends AbstractRestApiServlet {

	private static final String DELETE_OBJECT_TASK_URL = "/app_worker/deleteobjecttask";
	private static final String DELETE_QUEUE_NAME = "deletequeue";
	/**
	 * 
	 */
	private static final long serialVersionUID = -7978453807761868626L;

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
		DeleteTaskRequest dtReq = (DeleteTaskRequest) convertRequest();
		if (dtReq.getKey().equals("secret")) {
			final String kind = dtReq.getObjectName();

			int deleted_count = 0;
			boolean is_finished = false;

			final DatastoreService dss = DatastoreServiceFactory
					.getDatastoreService();
			final long start = System.currentTimeMillis();
			while (System.currentTimeMillis() - start < 16384) {

				final Query query = new Query(kind);

				query.setKeysOnly();

				final ArrayList<Key> keys = new ArrayList<Key>();

				for (final Entity entity : dss.prepare(query).asIterable(
						FetchOptions.Builder.withLimit(128))) {
					keys.add(entity.getKey());
				}

				keys.trimToSize();

				if (keys.size() == 0) {
					is_finished = true;
					break;
				}

				while (System.currentTimeMillis() - start < 16384) {

					try {
						dss.delete(keys);
						deleted_count += keys.size();
						break;
					} catch (Throwable ignore) {
						continue;
					}
				}
			}
			System.err.println("*** deleted " + deleted_count
					+ " entities form " + kind);

			if (is_finished) {
				System.err.println("*** deletion job for " + kind
						+ " is completed.");
			} else {
				final Integer taskcount;
				final String tcs = dtReq.getTaskCount();
				if (tcs == null) {
					taskcount = 0;
				} else {
					taskcount = Integer.parseInt(tcs) + 1;
				}

				Queue deleteQueue = QueueFactory.getQueue(DELETE_QUEUE_NAME);
				deleteQueue.add(url(DELETE_OBJECT_TASK_URL)
						.param(DeleteTaskRequest.OBJECT_PARAM, kind + "")
						.param(DeleteTaskRequest.KEY_PARAM, dtReq.getKey())
						.param(DeleteTaskRequest.TASK_COUNT_PARAM,
								taskcount.toString()));

				System.err.println("*** deletion task # " + taskcount + " for "
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
