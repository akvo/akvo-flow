/*
 *  Copyright (C) 2012 Stichting Akvo (Akvo Foundation)
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
package org.waterforpeople.mapping.app.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.MetricPayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.metric.dao.MetricDao;
import com.gallatinsystems.metric.dao.SurveyMetricMappingDao;
import com.gallatinsystems.metric.domain.Metric;

@Controller
@RequestMapping("/metrics")
public class MetricRestService {

	@Inject
	private MetricDao metricDao;
	
	@Inject
	private SurveyMetricMappingDao surveyMetricMappingDao;

	// TODO put in meta information?
	// list all metrics
	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public Map<String, List<MetricDto>> listMetrics() {
		final Map<String, List<MetricDto>> response = new HashMap<String, List<MetricDto>>();
		List<MetricDto> results = new ArrayList<MetricDto>();
		List<Metric> metrics = metricDao
				.list(Constants.ALL_RESULTS);
		if (metrics != null) {
			for (Metric s : metrics) {
				MetricDto dto = new MetricDto();
				DtoMarshaller.copyToDto(s, dto);

				results.add(dto);
			}
		}
		response.put("metrics", results);
		return response;
	}

	// find a single metric by the metricId
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public Map<String, MetricDto> findMetric(
			@PathVariable("id") Long id) {
		final Map<String, MetricDto> response = new HashMap<String, MetricDto>();
		Metric s = metricDao.getByKey(id);
		MetricDto dto = null;
		if (s != null) {
			dto = new MetricDto();
			DtoMarshaller.copyToDto(s, dto);
		}
		response.put("metric", dto);
		return response;

	}

	// delete metric by id
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	@ResponseBody
	public Map<String, RestStatusDto> deleteMetricById(
			@PathVariable("id") Long id) {
		final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
		Metric s = metricDao.getByKey(id);
		RestStatusDto statusDto = null;
		statusDto = new RestStatusDto();
		statusDto.setStatus("failed");

		// check if metric exists in the datastore
		if (s != null) {
			// delete metric group
			metricDao.delete(s);
			
			// delete associated surveyMetricMappings
			surveyMetricMappingDao.deleteMetricMappingByMetric(id);
			statusDto.setStatus("ok");
		}
		response.put("meta", statusDto);
		return response;
	}

	// update existing metric
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@ResponseBody
	public Map<String, Object> saveExistingMetric(
			@RequestBody MetricPayload payLoad) {
		final MetricDto metricDto = payLoad.getMetric();
		final Map<String, Object> response = new HashMap<String, Object>();
		MetricDto dto = null;

		RestStatusDto statusDto = new RestStatusDto();
		statusDto.setStatus("failed");

		// if the POST data contains a valid metricDto, continue.
		// Otherwise,
		// server will respond with 400 Bad Request
		if (metricDto != null) {
			Long keyId = metricDto.getKeyId();
			Metric s;

			// if the metricDto has a key, try to get the metric.
			if (keyId != null) {
				s = metricDao.getByKey(keyId);
				// if we find the metric, update it's properties
				if (s != null) {
					// copy the properties, except the createdDateTime property,
					// because it is set in the Dao.
					BeanUtils.copyProperties(metricDto, s,
							new String[] { "createdDateTime" });
					s = metricDao.save(s);
					dto = new MetricDto();
					DtoMarshaller.copyToDto(s, dto);
					statusDto.setStatus("ok");
				}
			}
		}
		response.put("meta", statusDto);
		response.put("metric", dto);
		return response;
	}

	// create new metric
	@RequestMapping(method = RequestMethod.POST, value = "")
	@ResponseBody
	public Map<String, Object> saveNewMetric(
			@RequestBody MetricPayload payLoad) {
		final MetricDto metricDto = payLoad.getMetric();
		final Map<String, Object> response = new HashMap<String, Object>();
		MetricDto dto = null;

		RestStatusDto statusDto = new RestStatusDto();
		statusDto.setStatus("failed");

		// if the POST data contains a valid metricDto, continue.
		// Otherwise,
		// server will respond with 400 Bad Request
		if (metricDto != null) {
			Metric s = new Metric();

			// copy the properties, except the createdDateTime property, because
			// it is set in the Dao.
			BeanUtils.copyProperties(metricDto, s,
					new String[] { "createdDateTime" });
			s = metricDao.save(s);

			dto = new MetricDto();
			DtoMarshaller.copyToDto(s, dto);
			statusDto.setStatus("ok");
		}

		response.put("meta", statusDto);
		response.put("metric", dto);
		return response;
	}
}
