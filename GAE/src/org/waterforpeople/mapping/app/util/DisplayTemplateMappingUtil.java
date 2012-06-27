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

package org.waterforpeople.mapping.app.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.displaytemplate.DisplayTemplateMappingDto;
import org.waterforpeople.mapping.domain.DisplayTemplateMapping;

public class DisplayTemplateMappingUtil {

	public static void  copyCanonicalToDto(
			DisplayTemplateMapping orig, DisplayTemplateMappingDto dest) throws IllegalAccessException, InvocationTargetException {
		BeanUtils.copyProperties(dest, orig);
		BeanUtils.copyProperty(dest, "keyId", orig.getKey().getId());
	}

	public static void copyDtoToCanonical(
			DisplayTemplateMappingDto orig, DisplayTemplateMapping dest) throws IllegalAccessException, InvocationTargetException {
		BeanUtils.copyProperties(dest, orig);
		BeanUtils.copyProperty(dest, "Key.Id", orig.getKeyId());
	}

}
