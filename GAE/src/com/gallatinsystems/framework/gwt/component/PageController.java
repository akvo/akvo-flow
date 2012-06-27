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

package com.gallatinsystems.framework.gwt.component;

import java.util.Map;

/**
 * Interface that can be implemented by components that can serve as a master
 * controller, opening and closing other "pages"
 * 
 * @author Christopher Fagiani
 * 
 */
public interface PageController {
	/**
	 * opens a new "page" within the content area, replacing the main content
	 * with the new page
	 * 
	 * @param clazz
	 * @param bundle
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public void openPage(Class clazz, Map<String, Object> bundle);

	/**
	 * opens a new "page" within the content area, replacing the main content
	 * with the new page
	 * 
	 * @param clazz
	 * @param isForward
	 *            - indicates whether we're moving forward in a wizard or not
	 *            (so the controller knows whether to add a breadcrumb or not)
	 * @param bundle
	 */
	@SuppressWarnings("rawtypes")
	public void openPage(Class clazz, boolean isForward,
			Map<String, Object> bundle);

	/**
	 * sets the working flag
	 * 
	 * @param isWorking
	 */
	public void setWorking(boolean isWorking);

	/**
	 * returns the working flag
	 * 
	 * @return
	 */
	public boolean isWorking();
}
