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

package org.waterforpeople.mapping.app.gwt.server.accesspoint;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.analytics.dao.AccessPointMetricSummaryDao;
import org.waterforpeople.mapping.analytics.domain.AccessPointMetricSummary;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointMetricSummaryDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointMetricSummaryService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * service for retrieval of AccessPointMetricSummary objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointMetricSummaryServiceImpl extends RemoteServiceServlet
		implements AccessPointMetricSummaryService {

	private static final long serialVersionUID = -673518485392972951L;
	private AccessPointMetricSummaryDao summaryDao;

	public AccessPointMetricSummaryServiceImpl() {
		summaryDao = new AccessPointMetricSummaryDao();
	}

	/**
	 * lists all metric values with the given parent path and sub level
	 */
	@Override
	public List<AccessPointMetricSummaryDto> listAccessPointMetricSummary(
			String metricType, String countryCode, String name, Integer level) {
		AccessPointMetricSummary prototype = new AccessPointMetricSummary();
		prototype.setCountry(countryCode);
		prototype.setSubLevelName(name);
		prototype.setSubLevel(level);
		prototype.setMetricName(metricType);
		List<AccessPointMetricSummary> summaryList = summaryDao.listMetrics(
				prototype, false);
		List<AccessPointMetricSummaryDto> dtoList = new ArrayList<AccessPointMetricSummaryDto>();
		if (summaryList != null) {
			for (AccessPointMetricSummary sum : summaryList) {
				AccessPointMetricSummaryDto dto = new AccessPointMetricSummaryDto();
				DtoMarshaller.copyToDto(sum, dto);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

}
