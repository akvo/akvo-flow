package com.gallatinsystems.framework.gwt.util.client;

import java.util.Map;

/**
 * interface used to notify clients when an async operation is completed
 * 
 * @author Christopher Fagiani
 * 
 */
public interface CompletionListener {

	/**
	 * called when an operation is complete and the client has received the
	 * callback from the server
	 * 
	 * @param wasSuccessful
	 *            - boolean indicator showing whether the operation was ok
	 * @param payload
	 */
	public void operationComplete(boolean wasSuccessful, Map<String,Object> payload);
}
