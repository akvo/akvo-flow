package com.gallatinsystems.editorial.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

/**
 * persistent class that is used to represent an editorial page. An editorial
 * page can have a template (usually a velocity template) that can be evaluated
 * to generate content. Editorial pages can also have child EditorialPageItems
 * that are referenced using the page's id.
 * 
 * @author Christopher Fagiani
 * 
 */
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
