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
import java.util.Date;


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

//	public List<Long> getQuestionIds() {
//		return questionIds;
//	}
//
//	public void setQuestionIds(List<Long> questionIds) {
//		this.questionIds = questionIds;
//	}
//
//	public List<String> getAnswerValues() {
//		return answerValues;
//	}
//
//	public void setAnswerValues(List<String> answerValues) {
//		this.answerValues = answerValues;
//	}
//
	public void addProperty(Long questionId, String answerValue) {
		QasDto qasDto = new QasDto();
		qasDto.setQ(questionId + "");
		qasDto.setA(answerValue);
		this.qasList.add(qasDto);
	//	questionIds.add(questionId);
	//	answerValues.add(answerValue != null ? answerValue : "");
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