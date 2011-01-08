package com.gallatinsystems.framework.gwt.component;

import java.util.Map;

public interface PageController {
	@SuppressWarnings("unchecked")
	public void openPage(Class clazz, Map<String,Object> bundle);
}
