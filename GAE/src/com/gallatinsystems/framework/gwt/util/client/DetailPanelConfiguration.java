package com.gallatinsystems.framework.gwt.util.client;

import java.util.TreeMap;
import java.util.logging.Logger;

import com.gallatinsystems.common.util.ClassAttributeUtil;
import com.gallatinsystems.framework.domain.BaseDomain;

public class DetailPanelConfiguration<T extends BaseDomain> {
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
