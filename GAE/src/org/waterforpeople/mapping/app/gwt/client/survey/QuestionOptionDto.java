package org.waterforpeople.mapping.app.gwt.client.survey;

import java.io.Serializable;
import java.util.TreeMap;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class QuestionOptionDto extends BaseDto implements Serializable {

	private static final long serialVersionUID = 6237222655812167675L;

	private String text;
	private String code;
	private Integer order;
	private TreeMap<String, TranslationDto> translationMap;

	public TreeMap<String, TranslationDto> getTranslationMap() {
		return translationMap;
	}

	public void setTranslationMap(TreeMap<String, TranslationDto> translationMap) {
		this.translationMap = translationMap;
	}

	/**
	 * adds the translation to the translation map. If a translation already
	 * exists (based on language code), it will be replaced
	 * 
	 * @param trans
	 */
	public void addTranslation(TranslationDto trans) {
		if (translationMap == null) {
			translationMap = new TreeMap<String, TranslationDto>();
		}
		translationMap.put(trans.getLangCode(), trans);
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
