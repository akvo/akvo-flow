package com.gallatinsystems.framework.gwt.wizard.client;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;

/**
 * classes that implement this interface represent automated steps within a
 * wizard workflow. This allows a way to componentize workflow operations and
 * integrate with the core UI wizard manager without having to display any
 * content other than a "working" label that is automatically dismissed after
 * completion.
 * 
 * @author Christopher Fagiani
 * 
 */
public interface AutoAdvancing {

	/**
	 * invokes the "business logic" of the widget, passing in a listener that
	 * will be notified when its complete and the widget can be unloaded in
	 * favor of the next step in the workflow.
	 * 
	 * @param listener
	 */
	public void advance(CompletionListener listener);
}
