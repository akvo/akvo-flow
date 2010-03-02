package com.gallatinsystems.survey.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.db.PMF;

import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;

public class DeviceSurveyJobQueueDAO {
	PersistenceManager pm;

	public List<DeviceSurveyJobQueue> get(String devicePhoneNumber) {
		DeviceSurveyJobQueue deviceSurveyJobQueue = null;
		javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
		query.setFilter("devicePhoneNumber == devicePhoneNumberParam");
		query.declareParameters("Long idParam");
		List<DeviceSurveyJobQueue> results = (List<DeviceSurveyJobQueue>) query.execute(devicePhoneNumber);
		return results;
	}
	
	public Long save(DeviceSurveyJobQueue deviceSurveyJobQueue){
		pm.makePersistent(deviceSurveyJobQueue);
		return deviceSurveyJobQueue.getId();
	}
	
	public List<DeviceSurveyJobQueue> listAllJobsInQueue(){
		javax.jdo.Query query = pm.newQuery(DeviceSurveyJobQueue.class);
		List<DeviceSurveyJobQueue> results = (List<DeviceSurveyJobQueue>) query.execute();
		return results;
	}
	
	private void init() {
		pm = PMF.get().getPersistenceManager();
	}

	public DeviceSurveyJobQueueDAO() {
		init();
	}
}
