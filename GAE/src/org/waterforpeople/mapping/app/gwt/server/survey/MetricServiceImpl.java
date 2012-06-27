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
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.MetricDto;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.metric.dao.MetricDao;
import com.gallatinsystems.metric.domain.Metric;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MetricServiceImpl extends RemoteServiceServlet implements
		MetricService {
	private static final String DEFAULT_ORG_PROP = "defaultOrg";
	private static final long serialVersionUID = -7385390184438218799L;
	private MetricDao metricDao;

	public MetricServiceImpl() {
		metricDao = new MetricDao();
	}

	/**
	 * lists all metrics, optionally filtered by organization, valueType and
	 * name and group
	 * 
	 * 
	 * @param organizationName
	 * @return
	 */
	@Override
	public ResponseDto<ArrayList<MetricDto>> listMetrics(String name,
			String group, String valueType, String organizationName,
			String cursor) {
		List<Metric> metrics = metricDao.listMetrics(name, group, valueType,
				organizationName, cursor);
		ResponseDto<ArrayList<MetricDto>> resp = new ResponseDto<ArrayList<MetricDto>>();
		ArrayList<MetricDto> dtoList = new ArrayList<MetricDto>();
		if (metrics != null) {
			for (Metric m : metrics) {
				MetricDto dto = new MetricDto();
				DtoMarshaller.copyToDto(m, dto);
				dtoList.add(dto);
			}
			resp.setCursorString(MetricDao.getCursor(metrics));
		}
		resp.setPayload(dtoList);
		return resp;
	}

	/**
	 * deletes the metric with the given id. this method does NOT remove orphans
	 */
	@Override
	public void deleteMetric(Long id) {
		Metric m = metricDao.getByKey(id);
		if (m != null) {
			metricDao.delete(m);
		}
	}

	/**
	 * saves or updates a metric. If the metric passed in does not have a key,
	 * this method will first check for duplicates before saving. if the metric
	 * already exists, the existing metric will be returned
	 */
	@Override
	public MetricDto saveMetric(MetricDto metric) {
		Metric mToSave = new Metric();
		DtoMarshaller.copyToCanonical(mToSave, metric);
		if (metric.getKeyId() == null) {
			if (mToSave.getOrganization() == null
					|| mToSave.getOrganization().trim().length() == 0) {
				mToSave.setOrganization(PropertyUtil
						.getProperty(DEFAULT_ORG_PROP));
			}
			List<Metric> mList = metricDao.listMetrics(metric.getName(),
					metric.getGroup(), metric.getValueType(),
					mToSave.getOrganization(), null);
			if (mList != null && mList.size() > 0) {
				DtoMarshaller.copyToDto(mList.get(0), metric);
				return metric;
			}
		}
		// if we get here, we need to perform the save
		mToSave = metricDao.save(mToSave);
		metric.setKeyId(mToSave.getKey().getId());
		return metric;
	}

}
