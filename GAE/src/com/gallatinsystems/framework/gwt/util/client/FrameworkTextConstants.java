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

package com.gallatinsystems.framework.gwt.util.client;

import com.google.gwt.i18n.client.Constants;

/**
 * text constants used by the framework classes. This interface is used so
 * framework ui strings can be localized. 
 * 
 * @author Christopher Fagiani
 * 
 */
public interface FrameworkTextConstants extends Constants {
	public String loading();

	public String previous();

	public String next();

	public String pleaseWait();

	public String noMatches();

	public String ok();

	public String cancel();

	public String close();

	public String saving();
}
