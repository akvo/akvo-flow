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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.waterforpeople.mapping.analytics.dao.AccessPointStatusSummaryDao;
import org.waterforpeople.mapping.analytics.domain.AccessPointStatusSummary;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * This service is responsible for returning access point summarization objects.
 * The fields within the summary DTO may be partially populated based on the
 * type of summarization being returned.
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointSummaryServiceImpl extends RemoteServiceServlet
		implements AccessPointSummaryService {

	private static final long serialVersionUID = -5722103696712574220L;
	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(AccessPointSummaryServiceImpl.class.getName());

	/**
	 * returns an array of AccessPointSummaryDto objects that match the criteria
	 * passed in. The summary objects will have the status and count populated.
	 */
	public AccessPointSummaryDto[] listAccessPointStatusSummary(String country,
			String community, String type, String year, String status) {
		AccessPointStatusSummaryDao summaryDao = new AccessPointStatusSummaryDao();
		List<AccessPointStatusSummary> summaries = summaryDao
				.listByLocationAndYear(country, community, type, year, status);
		AccessPointSummaryDto[] dtoList = null;

		if (summaries != null) {
			Map<String, Long> countMap = new HashMap<String, Long>();
			if (status == null) {
				// if we're not selecting by status, collapse based on it
				for (int i = 0; i < summaries.size(); i++) {
					if (summaries.get(i).getStatus() != null) {
						Long curVal = countMap.get(summaries.get(i).getStatus()
								.toString());
						if (curVal == null) {
							curVal = summaries.get(i).getCount() != null ? summaries
									.get(i).getCount()
									: new Long(1);
						} else {
							curVal = curVal
									+ (summaries.get(i).getCount() != null ? summaries
											.get(i).getCount()
											: new Long(1));
						}
						countMap.put(summaries.get(i).getStatus().toString(),
								curVal);
					}
				}
			} else {
				// if we're selecting by status, collapse based on Country
				for (int i = 0; i < summaries.size(); i++) {
					if (summaries.get(i).getCountry() != null) {
						Long curVal = countMap.get(summaries.get(i)
								.getCountry());
						if (curVal == null) {
							curVal = summaries.get(i).getCount();
						} else {
							curVal = curVal + summaries.get(i).getCount();
						}
						countMap.put(summaries.get(i).getCountry(), curVal);
					}
				}
			}
			dtoList = new AccessPointSummaryDto[countMap.keySet().size()];
			int i = 0;
			for (String key : countMap.keySet()) {
				AccessPointSummaryDto dto = new AccessPointSummaryDto();
				if (status == null) {
					dto.setStatus(key);
					dto.setCountryCode(country);
				} else {
					dto.setCountryCode(key);
					dto.setStatus(status);
				}
				dto.setCommunityCode(community);
				dto.setType(type);
				dto.setYear(year);
				dto.setCount(countMap.get(key));
				dtoList[i++] = dto;
			}
		}
		return dtoList;
	}
}