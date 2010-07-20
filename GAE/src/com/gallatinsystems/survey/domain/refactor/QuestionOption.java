package com.gallatinsystems.survey.domain.refactor;

import java.util.HashMap;

import com.gallatinsystems.framework.domain.BaseDomain;

public class QuestionOption extends BaseDomain{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2794521663923141747L;
	private String code =null;
	private HashMap<String,String> optionMap=null;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public HashMap<String, String> getOptionMap() {
		return optionMap;
	}
	public void setOptionMap(HashMap<String, String> optionMap) {
		this.optionMap = optionMap;
	}
	public void addOptionMap(String langCode, String text){
		if(optionMap==null)
			optionMap= new HashMap<String,String>();
		optionMap.put(langCode, text);
	}
}
