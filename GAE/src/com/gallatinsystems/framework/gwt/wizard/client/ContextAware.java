package com.gallatinsystems.framework.gwt.wizard.client;

import java.util.Map;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;

/**
 * interface that indicates a wizard widget is aware of some state (basically a
 * map of name/value pairs passed in/out of it). Similarly, being context aware
 * means that the wizard manager will call persistContext when the wizard is
 * being unloaded so no user changes are lost as they navigate within the
 * wizard.
 * 
 * @author Christopher Fagiani
 * 
 */
public interface ContextAware {
	public void setContextBundle(Map<String, Object> bundle);

	public Map<String, Object> getContextBundle(boolean doPopulation);

	public void persistContext(String buttonText, CompletionListener listener);

	public void flushContext();
}
