package com.gallatinsystems.device;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.gallatinsystems.device.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.framework.domain.DataChangeRecord;

/**
 * deletes unneeded DeviceSurveyJobQueue records based on an update to
 * SurveyAssignment
 * 
 * @author Christopher Fagiani
 * 
 */
public class DeviceSurveyJobQueueUpdater implements DataSummarizer {

	private DeviceSurveyJobQueueDAO deviceSurveyJobQueueDao;
	@SuppressWarnings("unused")
	private static Logger logger = Logger
			.getLogger(DeviceSurveyJobQueueUpdater.class.getName());

	public DeviceSurveyJobQueueUpdater() {
		deviceSurveyJobQueueDao = new DeviceSurveyJobQueueDAO();
	}

	@Override
	public String getCursor() {
		// no-op
		return null;
	}

	@Override
	public boolean performSummarization(String key, String type, String value,
			Integer offset, String cursor) {
		DataChangeRecord change = new DataChangeRecord(value);
		deleteRecords(change.getOldVal(), change.getId());
		return true;
	}

	/**
	 * unpacks the packed string an deletes the necessary records from the
	 * DeviceSurveyJobQueue table
	 * 
	 * @param packedString
	 * @param assignmentId
	 */
	private void deleteRecords(String packedString, String assignmentId) {
		if (packedString != null) {
			List<DeviceSurveyJobQueue> queueItems = deviceSurveyJobQueueDao
					.listJobByAssignment(new Long(assignmentId));
			List<DeviceSurveyJobQueue> itemsToDelete = new ArrayList<DeviceSurveyJobQueue>();
			if (packedString.contains("d")) {
				String deviceIds = packedString.substring(packedString
						.indexOf("d") + 1, packedString.indexOf("s"));
				String[] nums = { deviceIds };
				if (deviceIds.contains("xx")) {
					nums = deviceIds.split("xx");
				}
				for (int i = 0; i < nums.length; i++) {
					if (nums[i].trim().length() > 0) {
						for (DeviceSurveyJobQueue job : queueItems) {
							if (job.getDevicePhoneNumber().equals(nums[i])) {
								itemsToDelete.add(job);
								break;
							}
						}
					}
				}
			}
			if (packedString.contains("s")) {
				String surveyIds = packedString.substring(packedString
						.indexOf("s") + 1);
				String[] ids = { surveyIds };
				if (surveyIds.contains("xx")) {
					ids = surveyIds.split("xx");
				}
				for (int i = 0; i < ids.length; i++) {
					if (ids[i].trim().length() > 0) {
						for (DeviceSurveyJobQueue job : queueItems) {
							if (job.getSurveyID().toString().equals(ids[i])) {
								itemsToDelete.add(job);
								break;
							}
						}
					}
				}
			}
			if (itemsToDelete != null && itemsToDelete.size() > 0) {
				deviceSurveyJobQueueDao.delete(itemsToDelete);
			}
		}
	}
}
