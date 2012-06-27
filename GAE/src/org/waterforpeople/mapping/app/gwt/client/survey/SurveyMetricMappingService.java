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

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * RPC service for mapping survey questions to Metric objects
 * 
 * @author Christopher Fagiani
 * 
 */
@RemoteServiceRelativePath("surveymetricmaprpcservice")
public interface SurveyMetricMappingService extends RemoteService {

	/**
	 * lists all mappings for a single survey
	 */
	public List<SurveyMetricMappingDto> listMappingsBySurvey(Long surveyId);

	
	/**
	 * lists all mappings for a single question
	 * @param questionId
	 * @return
	 */
	public List<SurveyMetricMappingDto> listMappingsByQuestion(Long questionId);

	/**
	 * saves all mappings within a single quesiton group. Existing mappings for
	 * the same question group will be deleted prior to saving.
	 * 
	 * @param questionGroupId
	 * @param mappings
	 * @return
	 */
	public List<SurveyMetricMappingDto> saveMappings(Long questionGroupId,
			List<SurveyMetricMappingDto> mappings);
	
	/**
	 * saves the new mapping (replacing the old ones, if needed)
	 * @param mapping
	 * @return
	 */
	public SurveyMetricMappingDto saveMapping(SurveyMetricMappingDto mapping);
	
	/**
	 * deletes all mappings for a single question
	 * 
	 */
	public void deleteMetricMapping(Long questionId);

}
