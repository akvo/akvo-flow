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

package org.waterforpeople.mapping.app.web.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * dto that can hold surveyInstance data
 * 
 * @author Mark Westra
 * 
 */
public class SurveyInstanceDto implements Serializable {

	private static final long serialVersionUID = -850583183416882347L;

	private String uuid;
	private Long surveyId;
	private Long collectionDate;
	private List<QasDto> qasList;
//	private List<Long> questionIds;
//	private List<String> answerValues;

	public SurveyInstanceDto() {
		setQasList(new ArrayList<QasDto>());
//		setQuestionIds(new ArrayList<Long>());
//		setAnswerValues(new ArrayList<String>());
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Long collectionDate) {
		this.collectionDate = collectionDate;
	}

	public void addProperty(Long questionId, String answerValue, String deviceQuestionType) {
		QasDto qasDto = new QasDto();
		qasDto.setQ(questionId + "");
		qasDto.setA(answerValue);
		qasDto.setT(deviceQuestionType);
		this.qasList.add(qasDto);
	}

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public List<QasDto> getQasList() {
		return qasList;
	}

	public void setQasList(List<QasDto> qasList) {
		this.qasList = qasList;
	}
}