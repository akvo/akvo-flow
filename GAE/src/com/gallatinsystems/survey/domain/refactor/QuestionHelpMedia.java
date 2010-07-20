package com.gallatinsystems.survey.domain.refactor;

import java.util.HashMap;

import com.gallatinsystems.framework.domain.BaseDomain;

public class QuestionHelpMedia extends BaseDomain{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7035250558880867571L;
	private String url=null;
	private Type type=null;
	HashMap<String,String> textMap =null;
	public enum Type{PHOTO,VIDEO}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public HashMap<String, String> getTextMap() {
		return textMap;
	}
	public void setTextMap(HashMap<String, String> textMap) {
		this.textMap = textMap;
	};
}
