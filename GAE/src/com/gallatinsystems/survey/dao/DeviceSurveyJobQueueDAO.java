package com.gallatinsystems.survey.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.db.PMF;

import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

public class DeviceSurveyJobQueueDAO {

	@SuppressWarnings("unchecked")
	public List<DeviceSurveyJobQueue> get(String devicePhoneNumber) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
		query.setFilter("devicePhoneNumber == devicePhoneNumberParam");
		query.declareParameters("Long idParam");
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
}
