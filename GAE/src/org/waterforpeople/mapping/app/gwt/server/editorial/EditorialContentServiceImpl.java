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

package org.waterforpeople.mapping.app.gwt.server.editorial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheManager;

import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageContentDto;
import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageDto;
import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.common.util.VelocityUtil;
import com.gallatinsystems.editorial.dao.EditorialPageDao;
import com.gallatinsystems.editorial.domain.EditorialPage;
import com.gallatinsystems.editorial.domain.EditorialPageContent;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Service for saving/finding editorial page objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class EditorialContentServiceImpl extends RemoteServiceServlet implements
		EditorialPageService {
	private static final Logger log = Logger
			.getLogger(EditorialContentServiceImpl.class.getName());

	private static final long serialVersionUID = 1631722278637197282L;
	
	private EditorialPageDao editorialDao;
	private Cache cache;

	public EditorialContentServiceImpl() {
		super();
		editorialDao = new EditorialPageDao();
		try {
			cache = CacheManager.getInstance().getCacheFactory()
					.createCache(Collections.EMPTY_MAP);
		} catch (CacheException e) {
			log.log(Level.SEVERE, "Could not initialize cache", e);

		}
	}

	/**
	 * lists all the EditorialPageContent itms for a given page
	 */
	@Override
	public List<EditorialPageContentDto> listContentByPage(Long pageId) {
		List<EditorialPageContent> content = editorialDao
				.listContentByPage(pageId);
		List<EditorialPageContentDto> dtoList = null;
		if (content != null) {
			dtoList = new ArrayList<EditorialPageContentDto>();
			for (EditorialPageContent contentItem : content) {
				EditorialPageContentDto dto = new EditorialPageContentDto();
				DtoMarshaller.copyToDto(contentItem, dto);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	/**
	 * paginated list of all editorial pages
	 */
	@Override
	public List<EditorialPageDto> listEditorialPage(String cursor) {
		List<EditorialPage> pages = editorialDao.list(cursor);
		List<EditorialPageDto> dtoList = null;
		if (pages != null) {
			dtoList = new ArrayList<EditorialPageDto>();
			for (EditorialPage page : pages) {
				EditorialPageDto dto = new EditorialPageDto();
				DtoMarshaller.copyToDto(page, dto);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	@Override
	public EditorialPageDto saveEditorialPage(EditorialPageDto content) {
		EditorialPage page = new EditorialPage();
		DtoMarshaller.copyToCanonical(page, content);
		page = editorialDao.save(page);
		// update the cache with the new value too
		cache.put(VelocityUtil.CACHE_KEY_PREFIX + content.getTargetFileName(),
				content.getTemplate());
		content.setKeyId(page.getKey().getId());
		return content;
	}

}
