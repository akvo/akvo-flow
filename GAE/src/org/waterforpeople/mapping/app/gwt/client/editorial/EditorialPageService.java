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

package org.waterforpeople.mapping.app.gwt.client.editorial;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * service for persisting/finding editorial content. this can be used with
 * velocity templates to generate static html pages using data in the system.
 * 
 * @author Christopher Fagiani
 * 
 */
@RemoteServiceRelativePath("editorialcontentrpcservice")
public interface EditorialPageService extends RemoteService {

	/**
	 * persists a page and any PageCotnentDto's contained therein
	 * 
	 * @param content
	 * @return
	 */
	public EditorialPageDto saveEditorialPage(EditorialPageDto content);

	/**
	 * finds all editorial pages
	 * 
	 * @param cursor
	 * @return
	 */
	public List<EditorialPageDto> listEditorialPage(String cursor);

	/**
	 * finds all EditorialPageContentDto items on a given page
	 * 
	 * @param pageId
	 * @return
	 */
	public List<EditorialPageContentDto> listContentByPage(Long pageId);
}
