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

package org.waterforpeople.mapping.app.gwt.client.surveyinstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

@Deprecated // TODO: eventually remove class
public interface SurveyInstanceServiceAsync {

	void listSurveyInstance(Date beginDate, Date endDate,
			boolean unapprovedOnlyFlag, Long surveyId, String source,
			String cursorString,
			AsyncCallback<ResponseDto<ArrayList<SurveyInstanceDto>>> callback);

	void listQuestionsByInstance(Long instanceId,
			AsyncCallback<List<QuestionAnswerStoreDto>> callback);

	void updateQuestions(List<QuestionAnswerStoreDto> dtoList,
			boolean isApproved,
			AsyncCallback<List<QuestionAnswerStoreDto>> callback);

	void listResponsesByQuestion(
			Long questionId,
			String cursorString,
			AsyncCallback<ResponseDto<ArrayList<QuestionAnswerStoreDto>>> callback);

	void deleteSurveyInstance(Long instanceId, AsyncCallback<Void> callback);

	void submitSurveyInstance(SurveyInstanceDto instance,
			AsyncCallback<SurveyInstanceDto> callback);

	void approveSurveyInstance(Long instanceId,
			List<QuestionAnswerStoreDto> changedQuestions,
			AsyncCallback<Void> callback);

	void listInstancesByLocale(Long localeId, Date dateFrom, Date dateTo,
			String cursor,
			AsyncCallback<ResponseDto<ArrayList<SurveyInstanceDto>>> callback);

}
