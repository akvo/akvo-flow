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
