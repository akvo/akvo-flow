package com.gallatinsystems.editorial.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class EditorialPage extends BaseDomain {

	private static final long serialVersionUID = 1907982353218334734L;

	private Text template;
	private String type;	
	private String targetFileName;

	public Text getTemplate() {
		return template;
	}

	public void setTemplate(Text template) {
		this.template = template;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTargetFileName() {
		return targetFileName;
	}

	public void setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
	}

}
