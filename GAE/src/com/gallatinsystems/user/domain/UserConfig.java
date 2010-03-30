package com.gallatinsystems.user.domain;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Data structure for user configuration/personalization information.
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class UserConfig extends BaseDomain {

	private static final long serialVersionUID = 515991819240493160L;
	@Persistent
	private String group;
	@Persistent
	private String name;
	@Persistent
	private String value;

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
