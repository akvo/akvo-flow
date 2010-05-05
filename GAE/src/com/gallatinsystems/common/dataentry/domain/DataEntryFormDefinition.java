package com.gallatinsystems.common.dataentry.domain;

import java.io.Serializable;
import java.util.ArrayList;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DataEntryFormDefinition extends BaseDomain implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4788217560598651899L;
	private String name = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<DataEntryFieldDefinition> getFieldDefList() {
		return fieldDefList;
	}

	public void setFieldDefList(ArrayList<DataEntryFieldDefinition> fieldDefList) {
		this.fieldDefList = fieldDefList;
	}

	private ArrayList<DataEntryFieldDefinition> fieldDefList = null;
}
