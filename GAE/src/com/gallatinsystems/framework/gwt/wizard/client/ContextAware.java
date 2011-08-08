package com.gallatinsystems.framework.gwt.wizard.client;

import java.util.Map;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;

public interface ContextAware {
	public void setContextBundle(Map<String,Object> bundle);
	public Map<String,Object> getContextBundle(boolean doPopulation);
	public void persistContext(String buttonText,CompletionListener listener);
	public void flushContext();
}
