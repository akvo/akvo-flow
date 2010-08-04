package com.gallatinsystems.survey.domain;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class QuestionOption extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2794521663923141747L;
	private String code = null;
	@Persistent(serialized = "true")
	private List<String> optionList = null;
	@Persistent(serialized = "true")
	private List<Key> altOptionKeyList = null;

	public void addOption(String text) {
		if (optionList == null)
			optionList = new ArrayList<String>();
		optionList.add(text);
	}

	public void addAltOptionKey(Key altOptionKey) {
		if (getAltOptionKeyList() == null)
			setAltOptionKeyList(new ArrayList<Key>());
		getAltOptionKeyList().add(altOptionKey);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setOptionList(List<String> optionList) {
		this.optionList = optionList;
	}

	public List<String> getOptionList() {
		return optionList;
	}

	public void setAltOptionKeyList(List<Key> altOptionKeyList) {
		this.altOptionKeyList = altOptionKeyList;
	}

	public List<Key> getAltOptionKeyList() {
		return altOptionKeyList;
	}

	

}
