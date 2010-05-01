package org.waterforpeople.mapping.app.util;

import java.lang.reflect.Field;
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
