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
