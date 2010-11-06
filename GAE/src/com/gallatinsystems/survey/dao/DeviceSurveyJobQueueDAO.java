package com.gallatinsystems.survey.dao;

import java.util.Date;
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
	
	public void save(List<DeviceSurveyJobQueue> itemList){
		PersistenceManager pm = PersistenceFilter.getManager();
		pm.makePersistentAll(itemList);		
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
	@SuppressWarnings("unchecked")
	public void deleteJob(String phone, Long surveyId) {
		PersistenceManager pm = PersistenceFilter.getManager();
		if (phone != null && surveyId != null) {

			javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
			String filterString = "devicePhoneNumber == devicePhoneParam && surveyID == surveyIdParam";
			String paramString = "String devicePhoneParam, Long surveyIdParam";

			query.setFilter(filterString);
			query.declareParameters(paramString);
			List<DeviceSurveyJobQueue> results = (List<DeviceSurveyJobQueue>) query
					.execute(phone, surveyId);
			if (results != null) {
				pm.deletePersistentAll(results);
			}

		}
	}

	/**
	 * deletes all jobs for a given assignment
	 * 
	 * @param assignmentId
	 */
	@SuppressWarnings("unchecked")
	public void deleteJob(Long assignmentId) {
		PersistenceManager pm = PersistenceFilter.getManager();
		if (assignmentId != null) {

			javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
			String filterString = "assignmentId == assignmentIdParam";
			String paramString = "Long assignmentIdParam";

			query.setFilter(filterString);
			query.declareParameters(paramString);
			List<DeviceSurveyJobQueue> results = (List<DeviceSurveyJobQueue>) query
					.execute(assignmentId);
			if (results != null) {
				pm.deletePersistentAll(results);
			}
		}
	}

	/**
	 * populates the assignment id for all items with the survey id specified
	 * THIS SHOULD NOT BE USED IN NORMAL OPERATION
	 * 
	 * @param surveyId
	 * @param assignmentId
	 */
	@SuppressWarnings("unchecked")
	public void updateAssignmentIdForSurvey(Long surveyId, Long assignmentId) {
		PersistenceManager pm = PersistenceFilter.getManager();

		javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
		String filterString = "surveyID == surveyIdParam";
		String paramString = "Long surveyIdParam";

		query.setFilter(filterString);
		query.declareParameters(paramString);
		List<DeviceSurveyJobQueue> results = (List<DeviceSurveyJobQueue>) query
				.execute(surveyId);
		if (results != null) {
			for (DeviceSurveyJobQueue job : results) {
				job.setAssignmentId(assignmentId);
			}
			pm.makePersistentAll(results);
		}
	}

	@SuppressWarnings("unchecked")
	public List<DeviceSurveyJobQueue> listAssignmentsWithEarlierExpirationDate(
			Date expirationDate) {
		PersistenceManager pm = PersistenceFilter.getManager();

		javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
		String filterString = "effectiveEndDate < expirationDateParam";
		String paramString = "java.util.Date expirationDateParam";
		query.declareImports("import java.util.Date");
		query.setFilter(filterString);
		query.declareParameters(paramString);
		return (List<DeviceSurveyJobQueue>) query.execute(expirationDate);
	}
}
