package com.gallatinsystems.survey.domain;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class QuestionHelpMedia extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7035250558880867571L;
	private String url = null;
	private Type type = null;
	private String text = null;
	@Persistent(serialized = "true")
	private List<Key> altTextKeyList = null;

	public enum Type {
		PHOTO, VIDEO
	}

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

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	
	public void addAltTextKey(Key altTextKey) {
		if (getAltTextKeyList() == null)
			setAltTextKeyList(new ArrayList<Key>());
		getAltTextKeyList().add(altTextKey);
	}

	public void setAltTextKeyList(List<Key> altTextKeyList) {
		this.altTextKeyList = altTextKeyList;
	}

	public List<Key> getAltTextKeyList() {
		return altTextKeyList;
	}
}
