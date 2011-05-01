package com.gallatinsystems.flow.foundry.domain;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

/**
 * This class may either contain template text or contain a URL to a remote
 * template. Templates are assumed to be using Apache Velocity.
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class Template extends BaseDomain {
	private static final long serialVersionUID = -4764166788944698283L;
	private String name;
	private String type;
	private String url;
	private Text tempateData;
	private String targetFileName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Text getTempateData() {
		return tempateData;
	}

	public void setTempateData(Text tempateData) {
		this.tempateData = tempateData;
	}

	public String getTargetFileName() {
		return targetFileName;
	}

	public void setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
	}

	@NotPersistent
	public String getTemplateDataAsString(){
		String val = null;
		if(tempateData != null){
			val = tempateData.getValue();
		}
		return val;
	}
}
