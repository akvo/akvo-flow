/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.app.gwt.server.survey;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.domain.SurveyAssignment;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.analytics.summarization.DataSummarizationRequest;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.domain.DataChangeRecord;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Service for assignment of surveys to devices
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyAssignmentServiceImpl extends RemoteServiceServlet implements
		SurveyAssignmentService {
	private BaseDAO<SurveyAssignment> surveyAssignmentDao;
	private DeviceDAO deviceDao;
	private SurveyDAO surveyDao;
	private DeviceSurveyJobQueueDAO deviceSurveyJobQueueDAO;
	private static final long serialVersionUID = 3956064184547647245L;
	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(SurveyAssignmentServiceImpl.class.getName());

	public SurveyAssignmentServiceImpl() {
		surveyAssignmentDao = new BaseDAO<SurveyAssignment>(
				SurveyAssignment.class);
		deviceDao = new DeviceDAO();
		surveyDao = new SurveyDAO();
		deviceSurveyJobQueueDAO = new DeviceSurveyJobQueueDAO();
	}

	/**
	 * saves a surveyAssignment to the datastore
	 */
	@Override
	public SurveyAssignmentDto saveSurveyAssignment(SurveyAssignmentDto dto) {
		SurveyAssignment assignment = new SurveyAssignment();
		DtoMarshaller.copyToCanonical(assignment, dto);
		if (dto.getDevices() != null) {
			List<Long> devIds = new ArrayList<Long>();
			for (DeviceDto dev : dto.getDevices()) {
				devIds.add(dev.getKeyId());
			}
			assignment.setDeviceIds(devIds);
		}
		if (dto.getSurveys() != null) {
			List<Long> surveyIds = new ArrayList<Long>();
			for (SurveyDto s : dto.getSurveys()) {
				surveyIds.add(s.getKeyId());
			}
			assignment.setSurveyIds(surveyIds);
		}
		SurveyAssignment oldAssignment = null;
		if (assignment.getKey() != null) {
			oldAssignment = surveyAssignmentDao.getByKey(assignment.getKey());
		}
		assignment = surveyAssignmentDao.save(assignment);
		dto.setKeyId(assignment.getKey().getId());

		generateDeviceJobQueueItems(assignment, oldAssignment);
		return dto;
	}

	/**
	 * creates and saves DeviceSurveyJobQueue objects for each device/survey
	 * pair in the assignment. If this takes too long to do, may need to make it
	 * async
	 * 
	 * @param assignment
	 */
	private void generateDeviceJobQueueItems(SurveyAssignment assignment,
			SurveyAssignment oldAssignment) {
		List<Long> surveyIdsToSave = new ArrayList<Long>(
				assignment.getSurveyIds());
		List<Long> deviceIdsToSave = new ArrayList<Long>(
				assignment.getDeviceIds());
		List<Long> surveyIdsToDelete = new ArrayList<Long>();
		List<Long> deviceIdsToDelete = new ArrayList<Long>();

		if (oldAssignment != null) {
			if (oldAssignment.getSurveyIds() != null) {
				surveyIdsToSave.removeAll(oldAssignment.getSurveyIds());
				surveyIdsToDelete = new ArrayList<Long>(
						oldAssignment.getSurveyIds());
				surveyIdsToDelete.removeAll(assignment.getSurveyIds());
			}
			if (oldAssignment.getDeviceIds() != null) {
				deviceIdsToSave.removeAll(oldAssignment.getDeviceIds());
				deviceIdsToDelete = new ArrayList<Long>(
						oldAssignment.getDeviceIds());
				deviceIdsToDelete.removeAll(assignment.getDeviceIds());
			}
		}
		List<DeviceSurveyJobQueue> queueList = new ArrayList<DeviceSurveyJobQueue>();
		Map<Long, Survey> surveyMap = new HashMap<Long, Survey>();
		Map<Long, Device> deviceMap = new HashMap<Long, Device>();
		if (deviceIdsToSave != null) {
			// for each new device, we need to save a record for ALL survey IDs
			// in the assignment
			for (Long id : deviceIdsToSave) {
				Device d = deviceMap.get(id);
				if (d == null) {
					d = deviceDao.getByKey(id);
					deviceMap.put(d.getKey().getId(), d);
				}
				for (Long sId : assignment.getSurveyIds()) {
					Survey survey = surveyMap.get(sId);
					if (survey == null) {
						survey = surveyDao.getByKey(sId);
						surveyMap.put(sId, survey);
					}
					queueList.add(constructQueueObject(d, survey, assignment));
				}
			}
		}
		// if we added any surveys, we need to save a record for ALL the devices
		// BUT we don't need to process the items that we already saved above
		if (surveyIdsToSave != null) {
			for (Long sId : surveyIdsToSave) {
				Survey survey = surveyMap.get(sId);
				if (survey == null) {
					survey = surveyDao.getByKey(sId);
					surveyMap.put(sId, survey);
				}
				for (Long id : assignment.getDeviceIds()) {
					// only proceed if we haven't already saved the record above
					if (!deviceIdsToSave.contains(id)) {
						Device d = deviceMap.get(id);
						if (d == null) {
							d = deviceDao.getByKey(id);
							deviceMap.put(d.getKey().getId(), d);
						}
						queueList.add(constructQueueObject(d, survey,
								assignment));
					}
				}
			}
		}

		if (queueList.size() > 0) {
			deviceSurveyJobQueueDAO.save(queueList);
		}
		if (deviceIdsToDelete.size() > 0 || surveyIdsToDelete.size() > 0) {
			StringBuilder builder = new StringBuilder("d");
			for (int i = 0; i < deviceIdsToDelete.size(); i++) {
				if (i > 0) {
					builder.append("xx");
				}
				Device d = deviceMap.get(deviceIdsToDelete.get(i));
				if (d == null) {
					d = deviceDao.getByKey(deviceIdsToDelete.get(i));
					deviceMap.put(d.getKey().getId(), d);
				}
				builder.append(d.getPhoneNumber());
			}
			builder.append("s");
			for (int i = 0; i < surveyIdsToDelete.size(); i++) {
				if (i > 0) {
					builder.append("xx");
				}
				builder.append(surveyIdsToDelete.get(i).toString());
			}

			DataChangeRecord change = new DataChangeRecord(
					SurveyAssignment.class.getName(), assignment.getKey()
							.getId() + "", builder.toString(), "n/");
			Queue queue = QueueFactory.getQueue("dataUpdate");
			queue.add(TaskOptions.Builder.withUrl("/app_worker/dataupdate")
					.param(DataSummarizationRequest.OBJECT_KEY,
							assignment.getKey().getId() + "")
					.param(DataSummarizationRequest.OBJECT_TYPE,
							"DeviceSurveyJobQueueChange")
					.param(DataSummarizationRequest.VALUE_KEY,
							change.packString()));
		}
	}

	private DeviceSurveyJobQueue constructQueueObject(Device d, Survey survey,
			SurveyAssignment assignment) {
		DeviceSurveyJobQueue queueItem = new DeviceSurveyJobQueue();
		queueItem.setDevicePhoneNumber(d.getPhoneNumber());
		queueItem.setEffectiveStartDate(assignment.getStartDate());
		queueItem.setEffectiveEndDate(assignment.getEndDate());
		queueItem.setSurveyID(survey.getKey().getId());
		queueItem.setName(survey.getName());
		queueItem.setLanguage(assignment.getLanguage());
		queueItem.setAssignmentId(assignment.getKey().getId());
		queueItem
				.setSurveyDistributionStatus(DeviceSurveyJobQueue.DistributionStatus.UNSENT);
		return queueItem;
	}

	/**
	 * lists all assignments
	 */
	@Override
	public SurveyAssignmentDto[] listSurveyAssignments() {
		List<SurveyAssignment> assignments = surveyAssignmentDao
				.list(Constants.ALL_RESULTS);
		ArrayList<SurveyAssignmentDto> dtoList = convertToDto(assignments);
		SurveyAssignmentDto[] results = null;
		if (dtoList != null) {
			results = new SurveyAssignmentDto[dtoList.size()];
			results = (SurveyAssignmentDto[]) dtoList.toArray(results);
		}

		return results;
	}

	/**
	 * deletes an assignment from the datastore
	 */
	@Override
	public void deleteSurveyAssignment(SurveyAssignmentDto dto) {
		if (dto != null) {
			SurveyAssignment assignment = surveyAssignmentDao.getByKey(dto
					.getKeyId());
			if (assignment != null) {
				deviceSurveyJobQueueDAO.deleteJob(assignment.getKey().getId());
				surveyAssignmentDao.delete(assignment);
			}
		}
	}

	/**
	 * return all survey assignments paginated
	 */
	@Override
	public ResponseDto<ArrayList<SurveyAssignmentDto>> listSurveyAssignments(
			String cursor) {
		List<SurveyAssignment> assignments = surveyAssignmentDao.list(cursor);
		ArrayList<SurveyAssignmentDto> dtoList = convertToDto(assignments);
		ResponseDto<ArrayList<SurveyAssignmentDto>> response = new ResponseDto<ArrayList<SurveyAssignmentDto>>();
		response.setCursorString(BaseDAO.getCursor(assignments));
		response.setPayload(dtoList);
		return response;
	}

	private ArrayList<SurveyAssignmentDto> convertToDto(
			List<SurveyAssignment> assignments) {
		ArrayList<SurveyAssignmentDto> results = null;
		if (assignments != null) {
			results = new ArrayList<SurveyAssignmentDto>();
			for (int i = 0; i < assignments.size(); i++) {
				SurveyAssignmentDto dto = new SurveyAssignmentDto();
				dto.setKeyId(assignments.get(i).getKey().getId());
				dto.setName(assignments.get(i).getName());
				dto.setStartDate(assignments.get(i).getStartDate());
				dto.setEndDate(assignments.get(i).getEndDate());
				dto.setLanguage(assignments.get(i).getLanguage());
				if (assignments.get(i).getDeviceIds() != null) {
					ArrayList<DeviceDto> devices = new ArrayList<DeviceDto>();
					for (Long id : assignments.get(i).getDeviceIds()) {
						Device dev = deviceDao.getByKey(id);
						if (dev != null) {
							DeviceDto devDto = new DeviceDto();
							devDto.setPhoneNumber(dev.getPhoneNumber());
							devDto.setKeyId(dev.getKey().getId());
							devDto.setDeviceIdentifier(dev
									.getDeviceIdentifier());
							devices.add(devDto);
						}
					}
					dto.setDevices(devices);
				}
				if (assignments.get(i).getSurveyIds() != null) {
					ArrayList<SurveyDto> surveys = new ArrayList<SurveyDto>();
					for (Long id : assignments.get(i).getSurveyIds()) {
						Survey survey = surveyDao.getByKey(id);
						if (survey != null) {
							SurveyDto sDto = new SurveyDto();
							sDto.setName(survey.getName());
							sDto.setKeyId(survey.getKey().getId());
							surveys.add(sDto);
						}
					}
					dto.setSurveys(surveys);
				}
				results.add(dto);
			}
		}
		return results;
	}
}
