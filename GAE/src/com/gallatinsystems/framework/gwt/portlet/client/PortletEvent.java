/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

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
