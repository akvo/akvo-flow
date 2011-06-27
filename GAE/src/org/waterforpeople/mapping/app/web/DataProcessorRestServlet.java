package org.waterforpeople.mapping.app.web;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dataexport.SurveyReplicationImporter;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.operations.dao.ProcessingStatusDao;
import com.gallatinsystems.operations.domain.ProcessingStatus;
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
	private static final String REBUILD_Q_SUM_STATUS_KEY = "rebuildQuestionSummary";
	private static final String VALUE_TYPE = "VALUE";
	private static final Integer QAS_PAGE_SIZE = 300;

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
		} else if (DataProcessorRequest.REBUILD_QUESTION_SUMMARY_ACTION
				.equalsIgnoreCase(dpReq.getAction())) {
			rebuildQuestionSummary();
		} else if (DataProcessorRequest.IMPORT_REMOTE_SURVEY_ACTION
				.equalsIgnoreCase(dpReq.getAction())) {
			SurveyReplicationImporter sri = new SurveyReplicationImporter();
			sri.executeImport(dpReq.getSource(), dpReq.getSurveyId());
		} else if (DataProcessorRequest.RESCORE_AP_ACTION
				.equalsIgnoreCase(dpReq.getAction())) {
			rescoreAp(dpReq.getCountry());
		}
		return new RestResponse();
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
	}

	/**
	 * this method re-runs scoring on all access points for a country
	 * 
	 * @param country
	 */
	private void rescoreAp(String country) {
		AccessPointDao apDao = new AccessPointDao();
		String cursor = null;
		List<AccessPoint> apList = null;
		do {
			apList = apDao.listAccessPointByLocation(country, null, null, null,
					cursor, new Integer(200));
			if (apList != null) {
				cursor = AccessPointDao.getCursor(apList);
			}
			for(AccessPoint ap:apList){
				apDao.save(ap);
			}
		} while (apList != null && apList.size() == 200);
	}

	/**
	 * rebuilds the SurveyQuestionSummary object for ALL data in the system.
	 * This method should only be run on a Backend instance as it is unlikely to
	 * complete within the task duration limits on other instances.
	 */
	private void rebuildQuestionSummary() {
		ProcessingStatusDao statusDao = new ProcessingStatusDao();
		ProcessingStatus status = statusDao
				.getStatusByCode(REBUILD_Q_SUM_STATUS_KEY);

		Map<String, Map<String, Long>> summaryMap = summarizeQuestionAnswerStore(null);
		if (summaryMap != null) {
			saveSummaries(summaryMap);
		}
		// now update the status so we can know it last ran
		if (status == null) {
			status = new ProcessingStatus();
			status.setCode(REBUILD_Q_SUM_STATUS_KEY);
		}
		status.setInError(false);
		status.setLastEventDate(new Date());
		statusDao.save(status);
	}

	/**
	 * iterates over the new summary counts and updates the records in the
	 * datastore. Where appropriate, new records will be created and defunct
	 * records will be removed.
	 * 
	 * @param summaryMap
	 */
	private void saveSummaries(Map<String, Map<String, Long>> summaryMap) {
		SurveyQuestionSummaryDao summaryDao = new SurveyQuestionSummaryDao();
		for (Entry<String, Map<String, Long>> summaryEntry : summaryMap
				.entrySet()) {
			List<SurveyQuestionSummary> summaryList = summaryDao
					.listByQuestion(summaryEntry.getKey());
			// iterate over all the counts and update the summaryList with the
			// count values. Create any missing elements and remove defunct
			// entries as we go
			List<SurveyQuestionSummary> toDeleteList = new ArrayList<SurveyQuestionSummary>(
					summaryList);
			List<SurveyQuestionSummary> toCreateList = new ArrayList<SurveyQuestionSummary>(
					summaryList);
			for (Entry<String, Long> valueEntry : summaryEntry.getValue()
					.entrySet()) {
				String val = valueEntry.getKey();
				boolean found = false;
				for (SurveyQuestionSummary sum : summaryList) {
					if (sum.getResponse() != null
							&& sum.getResponse().equals(val)) {
						// since it's still valid, remove it from toDeleteList
						toDeleteList.remove(sum);
						// update the count. Since we still have the
						// persistenceContext open, this will automatically be
						// flushed to the datastore without an explicit call to
						// save
						sum.setCount(valueEntry.getValue());
						found = true;
					}
				}
				if (!found) {
					// need to create it
					SurveyQuestionSummary s = new SurveyQuestionSummary();
					s.setCount(valueEntry.getValue());
					s.setQuestionId(summaryEntry.getKey());
					s.setResponse(val);
					toCreateList.add(s);
				}
			}
			// delete the unseen entities
			if (toDeleteList.size() > 0) {
				summaryDao.delete(toDeleteList);
			}
			// save the new items
			if (toCreateList.size() > 0) {
				summaryDao.save(toCreateList);
			}
			// flush the datastore operation
			summaryDao.flushBatch();
		}
	}

	/**
	 * loads all the summarizable QuestionAnswerStore instances from the data
	 * store and accrues counts by value occurrence in a map keyed on the
	 * questionId
	 * 
	 * @param sinceDate
	 * @return
	 */
	private Map<String, Map<String, Long>> summarizeQuestionAnswerStore(
			Date sinceDate) {
		QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
		String cursor = null;
		Map<String, Map<String, Long>> summaryMap = new HashMap<String, Map<String, Long>>();
		List<QuestionAnswerStore> qasList = null;
		do {
			qasList = qasDao.listByTypeAndDate(VALUE_TYPE, sinceDate, cursor,
					QAS_PAGE_SIZE);
			if (qasList != null && qasList.size() > 0) {
				cursor = QuestionAnswerStoreDao.getCursor(qasList);
				for (QuestionAnswerStore qas : qasList) {
					String val = qas.getValue();
					// skip images since the summary is meaningless in those
					// cases
					if (val == null
							|| val.toLowerCase().trim().endsWith(".jpg")) {
						continue;
					}
					Map<String, Long> countMap = summaryMap.get(qas
							.getQuestionID());
					if (countMap == null) {
						countMap = new HashMap<String, Long>();
						summaryMap.put(qas.getQuestionID(), countMap);
					}
					Long count = countMap.get(val);
					if (count == null) {
						count = new Long(1);
					} else {
						count = count + 1;
					}
					countMap.put(val, count);
				}
			}
		} while (qasList != null && qasList.size() > 0);
		return summaryMap;
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
					if (ap.getCommunityCode().startsWith("IL")) {
						ap.setWaterForPeopleProjectFlag(false);
					} else {
						ap.setWaterForPeopleProjectFlag(true);
					}

				} else if ("IN".equalsIgnoreCase(ap.getCountryCode())) {
					if (ap.getWaterForPeopleProjectFlag() == null) {
						ap.setWaterForPeopleProjectFlag(true);
					}
				} else if ("GT".equalsIgnoreCase(ap.getCountryCode())) {
					if (ap.getWaterForPeopleProjectFlag() == null) {
						ap.setWaterAvailableDayVisitFlag(true);
					}
				} else {
					// handles BO, DO, SV
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
