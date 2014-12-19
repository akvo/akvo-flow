/*
 *  Copyright (C) 2010-2014 Stichting Akvo (Akvo Foundation)
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
import java.util.Map;
import java.util.TreeMap;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.dto.client.NamedObject;

public class QuestionDto extends BaseDto implements NamedObject {

    private static final long serialVersionUID = -4708385830894435407L;
    public static final String ANS_DELIM = "|";
    public static final String ANS_DELIM_REGEX = "\\|";

    private String text;

    private QuestionType type;
    private OptionContainerDto optionContainerDto = null;
    private List<QuestionHelpDto> questionHelpList;
    private String tip = null;
    private String optionList = null;
    private List<Long> questionOptions = null;
    private Boolean mandatoryFlag = null;
    private Boolean dependentFlag = null;
    private Boolean localeNameFlag;
    private Boolean localeLocationFlag;
    private Boolean geoLocked = null;
    private Boolean requireDoubleEntry = null;
    private Long dependentQuestionId;
    private String dependentQuestionAnswer;
    private Long cascadeResourceId;
    private Long metricId;
    private QuestionDependencyDto questionDependency = null;
    private Long surveyId;
    private String questionId;
    private Long questionGroupId;
    private Boolean collapseable;
    private Boolean immutable;
    private Map<String, TranslationDto> translationMap;
    private String path;
    private Integer order;
    private Boolean allowMultipleFlag = null;
    private Boolean allowOtherFlag = null;
    private Boolean allowDecimal;
    private Boolean allowSign;
    private Boolean allowExternalSources;
    private Double minVal;
    private Double maxVal;
    private Boolean isName;
    private Long sourceId = null;
    private List<String> levelNames = null;

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

    public String getOptionList() {
        return optionList;
    }

    public void setOptionList(String optionList) {
        this.optionList = optionList;
    }

    public Boolean getIsName() {
        return isName;
    }

    public void setIsName(Boolean isName) {
        this.isName = isName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQuestionTypeString() {
        return type.toString();
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Map<String, TranslationDto> getTranslationMap() {
        return translationMap;
    }

    public void setTranslationMap(Map<String, TranslationDto> translationMap) {
        this.translationMap = translationMap;
    }

    /**
     * adds the translation to the translation map. If a translation already exists (based on
     * language code), it will be replaced
     *
     * @param trans
     */
    public void addTranslation(TranslationDto trans) {
        if (translationMap == null) {
            translationMap = new TreeMap<String, TranslationDto>();
        }
        translationMap.put(trans.getLangCode(), trans);
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getQuestionGroupId() {
        return questionGroupId;
    }

    public void setQuestionGroupId(Long questionGroupId) {
        this.questionGroupId = questionGroupId;
    }

    public void addQuestionHelp(QuestionHelpDto questionHelp) {
        if (questionHelpList == null) {
            questionHelpList = new ArrayList<QuestionHelpDto>();
        }
        questionHelpList.add(questionHelp);
    }

    public String getText() {
        return text;
    }

    /**
     * returns the translated version of the text for the locale specified (if present). If no
     * translation exists, it will return the default text.
     *
     * @param locale
     * @return
     */
    public String getLocalizedText(String locale) {
        if (locale != null && translationMap != null) {
            TranslationDto trans = translationMap.get(locale);
            String txt = null;
            if (trans != null) {
                txt = trans.getText();
            }
            if (txt != null && txt.trim().length() > 0) {
                return txt;
            } else {
                return this.text;
            }
        } else {
            return this.text;
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public List<QuestionHelpDto> getQuestionHelpList() {
        return questionHelpList;
    }

    public void setQuestionHelpList(List<QuestionHelpDto> questionHelpList) {
        this.questionHelpList = questionHelpList;
    }

    public void setOptionContainerDto(OptionContainerDto optionContainer) {
        this.optionContainerDto = optionContainer;
    }

    public OptionContainerDto getOptionContainerDto() {
        return optionContainerDto;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getTip() {
        return tip;
    }

    public void setMandatoryFlag(Boolean mandatoryFlag) {
        this.mandatoryFlag = mandatoryFlag;
    }

    public Boolean getMandatoryFlag() {
        return mandatoryFlag;
    }

    public void setQuestionDependency(QuestionDependencyDto questionDependency) {
        this.questionDependency = questionDependency;
    }

    public QuestionDependencyDto getQuestionDependency() {
        return questionDependency;
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

    public Boolean getDependentFlag() {
        return dependentFlag;
    }

    public void setDependentFlag(Boolean dependentFlag) {
        this.dependentFlag = dependentFlag;
    }

    public enum QuestionType {
        FREE_TEXT, OPTION, NUMBER, GEO, PHOTO, VIDEO, SCAN, TRACK, NAME, STRENGTH, DATE, CASCADE
    }

    @Override
    public String getDisplayName() {
        return getText();
    }

    public void setAllowMultipleFlag(Boolean allowMultipleFlag) {
        this.allowMultipleFlag = allowMultipleFlag;
    }

    public Boolean getAllowMultipleFlag() {
        return allowMultipleFlag;
    }

    public void setAllowOtherFlag(Boolean allowOtherFlag) {
        this.allowOtherFlag = allowOtherFlag;
    }

    public Boolean getAllowOtherFlag() {
        return allowOtherFlag;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof QuestionDto)) {
            return false;
        }
        QuestionDto otherQ = (QuestionDto) other;
        if (getKeyId() != null && otherQ.getKeyId().equals(getKeyId())) {
            return true;
        } else if (getKeyId() == null && otherQ.getKeyId() == null) {
            if (getText() != null && getText().equals(otherQ.getText())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (getKeyId() != null) {
            return getKeyId().hashCode();
        } else if (getText() != null) {
            return getText().hashCode();
        } else {
            return 0;
        }
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

    public List<Long> getQuestionOptions() {
        return questionOptions;
    }

    public void setQuestionOptions(List<Long> questionOption_ids) {
        this.questionOptions = questionOption_ids;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Boolean getLocaleNameFlag() {
        return localeNameFlag;
    }

    public Boolean getGeoLocked() {
        return geoLocked;
    }

    public void setGeoLocked(Boolean geoLocked) {
        this.geoLocked = geoLocked;
    }

    public void setLocaleNameFlag(Boolean localeNameFlag) {
        this.localeNameFlag = localeNameFlag;
    }

    public Boolean getAllowExternalSources() {
        return allowExternalSources;
    }

    public void setAllowExternalSources(Boolean allowExternalSources) {
        this.allowExternalSources = allowExternalSources;
    }

    public Boolean getLocaleLocationFlag() {
        return localeLocationFlag;
    }

    public void setLocaleLocationFlag(Boolean localeLocationFlag) {
        this.localeLocationFlag = localeLocationFlag;
    }

    public Boolean getRequireDoubleEntry() {
        return requireDoubleEntry;
    }

    public void setRequireDoubleEntry(Boolean requireDoubleEntry) {
        this.requireDoubleEntry = requireDoubleEntry;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        // Missing questionId is represented as null
        if (questionId != null && questionId.matches("\\s*")) {
            this.questionId = null;
        } else {
            this.questionId = questionId;
        }
    }

    public Long getCascadeResourceId() {
        return cascadeResourceId;
    }

    public void setCascadeResourceId(Long cascadeResourceId) {
        this.cascadeResourceId = cascadeResourceId;
    }

    public List<String> getLevelNames() {
        return levelNames;
    }

    public void setLevelNames(List<String> levelNames) {
        this.levelNames = levelNames;
    }
}
