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

package com.gallatinsystems.survey.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * 
 * Represents a Question on a survey. A question belongs to exactly 1 questionGroup.
 *
 */
@PersistenceCapable
public class Question extends BaseDomain {

	private static final long serialVersionUID = -9123426646238761996L;

	public enum Type {
		FREE_TEXT, OPTION, NUMBER, GEO, PHOTO, VIDEO, SCAN, TRACK, NAME, STRENGTH, DATE
	};

	private Type type = null;
	private String tip = null;
	private String text = null;
	@NotPersistent
	private Map<String, Translation> translationMap;
	private Boolean dependentFlag = null;
	private Boolean allowMultipleFlag = null;
	private Boolean allowOtherFlag = null;
	private Boolean collapseable = false;
	private Boolean geoLocked = null;
	private Boolean requireDoubleEntry = null;
	private Boolean immutable = false;
	private Long dependentQuestionId;
	private String dependentQuestionAnswer;
	private Long metricId;
	@NotPersistent
	private TreeMap<Integer, QuestionOption> questionOptionMap = null;
	
	@NotPersistent
	private TreeMap<Integer, QuestionHelpMedia> questionHelpMediaMap = null;
	private Long questionGroupId;
	private Long surveyId;
	private Integer order = null;
	private Boolean mandatoryFlag = null;
	private String path = null;
	private String referenceId;
	@NotPersistent
	private List<ScoringRule> scoringRules = null;
	private Boolean allowDecimal;
	private Boolean allowSign;
	private Double minVal;
	private Double maxVal;
	private Boolean isName;

	public Boolean getAllowDecimal() {
		return allowDecimal;
	}

	public void setAllowDecimal(Boolean allowDecimal) {
		this.allowDecimal = allowDecimal;
	}

	public Boolean getAllowSign() {
		return allowSign;
	}

	public void setAllowSign(Boolean allowSign) {
		this.allowSign = allowSign;
	}

	public Double getMinVal() {
		return minVal;
	}

	public void setMinVal(Double minVal) {
		this.minVal = minVal;
	}

	public Double getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(Double maxVal) {
		this.maxVal = maxVal;
	}

	public Boolean getIsName() {
		return isName;
	}

	public void setIsName(Boolean isName) {
		this.isName = isName;
	}

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getDependentQuestionAnswer() {
		return dependentQuestionAnswer;
	}

	public void setDependentQuestionAnswer(String dependentQuestionAnswer) {
		this.dependentQuestionAnswer = dependentQuestionAnswer;
	}

	public Long getDependentQuestionId() {
		return dependentQuestionId;
	}

	public void setDependentQuestionId(Long dependentQuestionId) {
		this.dependentQuestionId = dependentQuestionId;
	}

	public Long getMetricId() {
		return metricId;
	}

	public void setMetricId(Long metricId) {
		this.metricId = metricId;
	}
	
	public Long getQuestionGroupId() {
		return questionGroupId;
	}

	public void setQuestionGroupId(Long questionGroupId) {
		this.questionGroupId = questionGroupId;
	}

	public Map<String, Translation> getTranslationMap() {
		return translationMap;
	}

	public void setTranslationMap(Map<String, Translation> translationMap) {
		this.translationMap = translationMap;
	}

	public void setTranslationMap(HashMap<String, Translation> transMap) {
		if (transMap != null) {
			translationMap = new TreeMap<String, Translation>(transMap);
		}
	}

	public void addQuestionOption(QuestionOption questionOption) {
		if (getQuestionOptionMap() == null)
			setQuestionOptionMap(new TreeMap<Integer, QuestionOption>());
		getQuestionOptionMap().put(
				questionOption.getOrder() != null ? questionOption.getOrder()
						: getQuestionOptionMap().size() + 1, questionOption);
	}

	public void addHelpMedia(Integer order, QuestionHelpMedia questionHelpMedia) {
		if (getQuestionHelpMediaMap() == null)
			setQuestionHelpMediaMap(new TreeMap<Integer, QuestionHelpMedia>());
		getQuestionHelpMediaMap().put(order, questionHelpMedia);
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Boolean getDependentFlag() {
		return dependentFlag;
	}

	public void setDependentFlag(Boolean dependentFlag) {
		this.dependentFlag = dependentFlag;
	}

	public Boolean getAllowMultipleFlag() {
		return allowMultipleFlag;
	}

	public void setAllowMultipleFlag(Boolean allowMultipleFlag) {
		this.allowMultipleFlag = allowMultipleFlag;
	}

	public Boolean getAllowOtherFlag() {
		return allowOtherFlag;
	}

	public void setAllowOtherFlag(Boolean allowOtherFlag) {
		this.allowOtherFlag = allowOtherFlag;
	}

	
	public void setQuestionOptionMap(
			TreeMap<Integer, QuestionOption> questionOptionMap) {
		this.questionOptionMap = questionOptionMap;
	}

	public TreeMap<Integer, QuestionOption> getQuestionOptionMap() {
		return questionOptionMap;
	}

	public void setQuestionHelpMediaMap(
			TreeMap<Integer, QuestionHelpMedia> questionHelpMediaMap) {
		this.questionHelpMediaMap = questionHelpMediaMap;
	}

	public TreeMap<Integer, QuestionHelpMedia> getQuestionHelpMediaMap() {
		return questionHelpMediaMap;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getOrder() {
		return order;
	}

	public void setMandatoryFlag(Boolean mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}

	public Boolean getMandatoryFlag() {
		return mandatoryFlag;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	/**
	 * use helpMedia instead
	 * 
	 * @return
	 */
	@Deprecated
	public String getTip() {
		return tip;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void addTranslation(Translation t) {
		if (translationMap == null) {
			translationMap = new TreeMap<String, Translation>();
		}
		translationMap.put(t.getLanguageCode(), t);
	}

	public List<ScoringRule> getScoringRules() {
		return scoringRules;
	}

	public void addScoringRule(ScoringRule rule) {
		if (scoringRules == null) {
			scoringRules = new ArrayList<ScoringRule>();
		}
		scoringRules.add(rule);
	}

	public void setScoringRules(List<ScoringRule> scoringRules) {
		this.scoringRules = scoringRules;
	}

	public void setCollapseable(Boolean collapseable) {
		this.collapseable = collapseable;
	}

	public Boolean getCollapseable() {
		return collapseable;
	}

	public void setImmutable(Boolean immutable) {
		this.immutable = immutable;
	}

	public Boolean getImmutable() {
		return immutable;
	}

	public Boolean getGeoLocked() {
		return geoLocked;
	}

	public void setGeoLocked(Boolean geoLocked) {
		this.geoLocked = geoLocked;
	}

	public Boolean getRequireDoubleEntry() {
		return requireDoubleEntry;
	}

	public void setRequireDoubleEntry(Boolean requireDoubleEntry) {
		this.requireDoubleEntry = requireDoubleEntry;
	}
}
