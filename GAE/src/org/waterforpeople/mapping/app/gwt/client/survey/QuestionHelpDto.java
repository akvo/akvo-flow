package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.Map;
import java.util.TreeMap;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class QuestionHelpDto extends BaseDto {
	
	private static final long serialVersionUID = 1563528591253495401L;
	private String text;
	private String resourceUrl;
	private Type type;
	private Long questionId;
	private Map<String, TranslationDto> translationMap;

	public Map<String, TranslationDto> getTranslationMap() {
		return translationMap;
	}

	public void setTranslationMap(Map<String, TranslationDto> translationMap) {
		this.translationMap = translationMap;
	}

	public void addTranslation(TranslationDto trans){
		if(translationMap == null){
			translationMap = new TreeMap<String,TranslationDto>();			
		}
		translationMap.put(trans.getLangCode(), trans);
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public enum Type {
		PHOTO, VIDEO, TEXT, ACTIVITY
	}
}
