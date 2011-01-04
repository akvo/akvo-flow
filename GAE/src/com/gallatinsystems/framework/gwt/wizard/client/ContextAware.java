package com.gallatinsystems.framework.gwt.wizard.client;

import java.util.Map;

public interface ContextAware {
	public void setContextBundle(Map<String,Object> bundle);
	public Map<String,Object> getContextBundle();
	public void persistContext(CompletionListener listener);
}
