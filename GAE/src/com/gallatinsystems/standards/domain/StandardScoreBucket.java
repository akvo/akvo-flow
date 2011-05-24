package com.gallatinsystems.standards.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
@PersistenceCapable
public class StandardScoreBucket extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6108005961412862995L;
	
	private String name = null;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
