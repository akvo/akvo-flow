package org.waterforpeople.mapping.app.web;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

/**
 * Restful servlet to do bulk data update operations
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataProcessorRestServlet extends AbstractRestApiServlet {

	private static final long serialVersionUID = -7902002525342262821L;

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new DataProcessorRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		DataProcessorRequest dpReq = (DataProcessorRequest) req;
		if (DataProcessorRequest.PROJECT_FLAG_UPDATE_ACTION
				.equalsIgnoreCase(dpReq.getAction())) {
			updateAccessPointProjectFlag(dpReq.getCountry(), dpReq.getCursor());
		}
		return new RestResponse();
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
	}

	/**
	 * iterates over all AccessPoints in a country and applies a static set of
	 * rules to determine the proper value of the WFPProjectFlag
	 * 
	 * @param country
	 * @param cursor
	 */
	private void updateAccessPointProjectFlag(String country, String cursor) {
		AccessPointDao apDao = new AccessPointDao();
		Integer pageSize = 200;
		List<AccessPoint> apList = apDao.listAccessPointByLocation(country,
				null, null, null, cursor, pageSize);
		if (apList != null) {
			for (AccessPoint ap : apList) {

				if ("PE".equalsIgnoreCase(ap.getCountryCode())) {
					ap.setWaterForPeopleProjectFlag(false);
				} else if ("RW".equalsIgnoreCase(ap.getCountryCode())) {
					ap.setWaterForPeopleProjectFlag(false);
				} else if ("MW".equalsIgnoreCase(ap.getCountryCode())) {
					if (ap.getCommunityName().trim()
							.equalsIgnoreCase("Kachere/Makhetha/Nkolokoti")) {
						ap.setCommunityName("Kachere/Makhetha/Nkolokoti");
						if (ap.getWaterForPeopleProjectFlag() == null) {
							ap.setWaterForPeopleProjectFlag(true);
						}
					} else if (ap.getWaterForPeopleProjectFlag() == null) {
						ap.setWaterForPeopleProjectFlag(false);
					}
				} else if ("HN".equalsIgnoreCase(ap.getCountryCode())) {
					if(ap.getCommunityCode().startsWith("IL")){
						ap.setWaterForPeopleProjectFlag(false);
					}else{
						ap.setWaterForPeopleProjectFlag(true);
					}
					
				} else if ("IN".equalsIgnoreCase(ap.getCountryCode())){
					if(ap.getWaterForPeopleProjectFlag() == null){
						ap.setWaterForPeopleProjectFlag(true);
					}
				}else if ("GT".equalsIgnoreCase(ap.getCountryCode())){
					if(ap.getWaterForPeopleProjectFlag()==null){
						ap.setWaterAvailableDayVisitFlag(true);
					}
				}else {
					// handles  BO, DO, SV
					if (ap.getWaterForPeopleProjectFlag() == null) {
						ap.setWaterForPeopleProjectFlag(false);
					}
				}
			}

			if (apList.size() == pageSize) {
				// check for more
				sendProjectUpdateTask(country, AccessPointDao.getCursor(apList));
			}
		}
	}

	/**
	 * Sends a message to a task queue to start or continue the processing of
	 * the AP Project Flag
	 * 
	 * @param country
	 * @param cursor
	 */
	public static void sendProjectUpdateTask(String country, String cursor) {
		Queue queue = QueueFactory.getDefaultQueue();

		queue.add(url("/app_worker/dataprocessor")
				.param(DataProcessorRequest.ACTION_PARAM,
						DataProcessorRequest.PROJECT_FLAG_UPDATE_ACTION)
				.param(DataProcessorRequest.COUNTRY_PARAM, country)
				.param(DataProcessorRequest.CURSOR_PARAM,
						cursor != null ? cursor : ""));
	}

}
