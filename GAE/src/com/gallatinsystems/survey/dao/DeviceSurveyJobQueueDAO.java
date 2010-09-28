package com.gallatinsystems.survey.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

public class DeviceSurveyJobQueueDAO {

	@SuppressWarnings("unchecked")
	public List<DeviceSurveyJobQueue> get(String devicePhoneNumber) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
		query.setFilter("devicePhoneNumber == devicePhoneNumberParam");
		query.declareParameters("String devicePhoneNumberParam");
		List<DeviceSurveyJobQueue> results = (List<DeviceSurveyJobQueue>) query
				.execute(devicePhoneNumber);
		return results;
	}

	public Long save(DeviceSurveyJobQueue deviceSurveyJobQueue) {
		PersistenceManager pm = PersistenceFilter.getManager();
		pm.makePersistent(deviceSurveyJobQueue);
		return deviceSurveyJobQueue.getId();
	}

	@SuppressWarnings("unchecked")
	public List<DeviceSurveyJobQueue> listAllJobsInQueue() {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
		List<DeviceSurveyJobQueue> results = (List<DeviceSurveyJobQueue>) query
				.execute();
		return results;
	}

	/**
	 * delete a job with the phone number and survey id passed in
	 * 
	 * @param phoneNumbers
	 * @param surveyId
	 */
	public void deleteJob(String phone, Long surveyId) {
		PersistenceManager pm = PersistenceFilter.getManager();
		if (phone != null && surveyId != null) {

			javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
			String filterString = "devicePhoneNumber == devicePhoneParam && surveyId == surveyIdParam";
			String paramString = "String devicePhoneParam, Long surveyIdParam";

			query.setFilter(filterString);
			query.declareParameters(paramString);
			DeviceSurveyJobQueue job = (DeviceSurveyJobQueue) query.execute(
					phone, surveyId);
			if (job != null) {
				pm.deletePersistent(job);
			}
		}
	}
}
