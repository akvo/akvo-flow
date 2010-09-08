package com.gallatinsystems.common.ui.panel;

import java.util.TreeMap;

import com.gallatinsystems.common.util.ClassAttributeUtil;
import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class DetailPanelConfiguration<T extends BaseDto> {
	private Class<T> concreteClass;
	
	public DetailPanelConfiguration(Class<T> e) {
		setDomainClass(e);

	}

	public void setDomainClass(Class<T> e) {
		this.concreteClass = e;
	}

	public void setAnnotationsMap(TreeMap<String, String> annotationsMap) {
		if (annotationsMap == null) {
			annotationsMap=ClassAttributeUtil.listObjectAttributes(concreteClass.getName());
		}
		this.annotationsMap = annotationsMap;
	}

	public TreeMap<String, String> getAnnotationsMap() {
		return annotationsMap;
	}

	private TreeMap<String, String> annotationsMap = null;

}
