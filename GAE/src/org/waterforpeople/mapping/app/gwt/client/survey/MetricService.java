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

package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * service for manipulating metrics
 * 
 * @author Christopher Fagiani
 * 
 */
@RemoteServiceRelativePath("metricrpcservice")
public interface MetricService extends RemoteService {

	/**
	 * lists all metrics, optionally filtered by the parameters
	 * 
	 * @param name
	 * @param group
	 * @param valueType
	 * @param organizationName
	 * @param cursor
	 * @return
	 */
	public ResponseDto<ArrayList<MetricDto>> listMetrics(String name,
			String group, String valueType, String organizationName,
			String cursor);

	/**
	 * deletes the metric with the given ID.
	 * 
	 * @param id
	 */
	public void deleteMetric(Long id);

	/**
	 * saves/updates a metric. If it is a create (no key is set), the system
	 * will check for duplicates prior to saving. If a duplicate exists, the object returned will be the already present metric
	 * 
	 * @param metric
	 */
	public MetricDto saveMetric(MetricDto metric);
}
