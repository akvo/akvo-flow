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
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SurveyedLocaleServiceAsync {

	void deleteLocale(Long localeId, AsyncCallback<Void> callback);

	void listLocales(AccessPointSearchCriteriaDto searchCriteria,
			String cursorString,
			AsyncCallback<ResponseDto<ArrayList<SurveyedLocaleDto>>> callback);

	void listSurveyalValuesByInstance(Long surveyInstanceId,
			AsyncCallback<List<SurveyalValueDto>> callback);

	void saveSurveyedLocale(SurveyedLocaleDto locale,
			AsyncCallback<SurveyedLocaleDto> callback);

}
