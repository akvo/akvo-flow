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

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
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
	public void saveSurveyAssignment(SurveyAssignmentDto dto) {
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
		assignment = surveyAssignmentDao.save(assignment);

		generateDeviceJobQueueItems(assignment);
	}

	/**
	 * creates and saves DeviceSurveyJobQueue objects for each device/survey
	 * pair in the assignment. If this takes too long to do, may need to make it
	 * async
	 * 
	 * @param assignment
	 */
	private void generateDeviceJobQueueItems(SurveyAssignment assignment) {
		if (assignment.getDeviceIds() != null
				&& assignment.getSurveyIds() != null) {
			Map<Long, Survey> surveyMap = new HashMap<Long, Survey>();
			for (Long id : assignment.getDeviceIds()) {
				Device d = deviceDao.getByKey(id);
				for (Long sId : assignment.getSurveyIds()) {
					Survey survey = surveyMap.get(sId);
					if (survey == null) {
						survey = surveyDao.getByKey(sId);
						surveyMap.put(sId, survey);
					}
					DeviceSurveyJobQueue queueItem = new DeviceSurveyJobQueue();
					queueItem.setDevicePhoneNumber(d.getPhoneNumber());
					queueItem.setEffectiveStartDate(assignment.getStartDate());
					queueItem.setEffectiveEndDate(assignment.getEndDate());
					queueItem.setSurveyID(sId);
					queueItem.setName(survey.getName());
					queueItem.setLanguage(assignment.getLanguage());
					queueItem
							.setSurveyDistributionStatus(DeviceSurveyJobQueue.DistributionStatus.UNSENT);
					deviceSurveyJobQueueDAO.save(queueItem);
				}
			}
		}
	}

	/**
	 * lists all assignments TODO: move dto/domain conversion out
	 */
	@Override
	public SurveyAssignmentDto[] listSurveyAssignments() {
		List<SurveyAssignment> assignments = surveyAssignmentDao.list(null);
		SurveyAssignmentDto[] results = null;
		if (assignments != null) {
			results = new SurveyAssignmentDto[assignments.size()];
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
				results[i] = dto;
			}
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
				if (assignment.getDeviceIds() != null
						&& dto.getSurveys() != null) {
					for (Long deviceId : assignment.getDeviceIds()) {
						Device d = deviceDao.getByKey(deviceId);
						if (d != null) {
							for (SurveyDto survey : dto.getSurveys()) {
								deviceSurveyJobQueueDAO.deleteJob(d
										.getPhoneNumber(), survey.getKeyId());
							}
						}
					}
				}
				surveyAssignmentDao.delete(assignment);
			}
		}

	}

}
