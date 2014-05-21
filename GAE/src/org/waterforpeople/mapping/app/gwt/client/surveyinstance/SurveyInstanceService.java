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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("surveyinstance")
@Deprecated // to be removed eventually
public interface SurveyInstanceService extends RemoteService {
	public ResponseDto<ArrayList<SurveyInstanceDto>> listSurveyInstance(
			Date beginDate, Date toDate, boolean unapprovedOnlyFlag, Long surveyId, String source,
			String cursorString);	

	public List<QuestionAnswerStoreDto> updateQuestions(
			List<QuestionAnswerStoreDto> dtoList, boolean isApproved);

	public ResponseDto<ArrayList<QuestionAnswerStoreDto>> listResponsesByQuestion(
			Long questionId, String cursorString);

	public void deleteSurveyInstance(Long instanceId);

	/**
	 * saves a new survey instance and triggers processing
	 * 
	 * @param instance
	 * @return
	 */
	public SurveyInstanceDto submitSurveyInstance(SurveyInstanceDto instance);

	/**
	 * handles marking surveys as approved. Will also save updates to any
	 * questions passed in
	 * 
	 * @param instance
	 */
	public void approveSurveyInstance(Long instanceId,
			List<QuestionAnswerStoreDto> changedQuestions);

	/**
	 * lists all surveyInstances associated with the surveyedLocaleId passed in.
	 * 
	 * @param localeId
	 * @return
	 */
	public ResponseDto<ArrayList<SurveyInstanceDto>> listInstancesByLocale(Long localeId, Date dateFrom, Date dateTo, String cursor);

	List<QuestionAnswerStoreDto> listQuestionsByInstance(Long instanceId);
}
