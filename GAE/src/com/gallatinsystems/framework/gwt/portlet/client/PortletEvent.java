package com.gallatinsystems.framework.gwt.portlet.client;

/**
 * Events that may be broadcast by portlets to notify other portlets
 * participating in the view that something has changed.
 *  
 * 
 * @author Christopher Fagiani
 * 
 */
public class PortletEvent {

	private String type;
	private Object payload;
	private Portlet source;

	/**
	 * returns the portlet that raised the event
	 * 
	 * @return
	 */
	public Portlet getSource() {
		return source;
	}

	public void setSource(Portlet source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public PortletEvent(String t, Object p, Portlet s) {
		type = t;
		payload = p;
		source = s;
	}
}
