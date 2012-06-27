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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyMetricMappingServiceAsync {

	void listMappingsBySurvey(Long surveyId,
			AsyncCallback<List<SurveyMetricMappingDto>> callback);

	
	void saveMappings(Long questionGroupId,
			List<SurveyMetricMappingDto> mappings,
			AsyncCallback<List<SurveyMetricMappingDto>> callback);


	void listMappingsByQuestion(Long questionId,
			AsyncCallback<List<SurveyMetricMappingDto>> callback);


	void saveMapping(SurveyMetricMappingDto mapping,
			AsyncCallback<SurveyMetricMappingDto> callback);


	void deleteMetricMapping(Long questionId, AsyncCallback<Void> callback);

}
