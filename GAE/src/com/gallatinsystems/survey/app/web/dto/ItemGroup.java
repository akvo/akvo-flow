package com.gallatinsystems.survey.app.web.dto;

import java.io.Serializable;
import java.util.List;

public class ItemGroup implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6820387017278736524L;
	private String name;
	private String code;
	private String desc;
	private List<ItemDetail> items;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public List<ItemDetail> getItems() {
		return items;
	}
	public void setItems(List<ItemDetail> items) {
		this.items = items;
	}
	
}
