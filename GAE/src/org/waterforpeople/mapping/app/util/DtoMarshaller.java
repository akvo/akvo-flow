package org.waterforpeople.mapping.app.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.framework.BaseDto;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.KeyFactory;

public class DtoMarshaller {

	public static <T extends BaseDomain, U extends BaseDto> void copyToCanonical(
			T canonical, U dto) {
		try {
			BeanUtils.copyProperties(canonical, dto);
			if (dto.getKeyId() != null) {
				canonical.setKey(KeyFactory.createKey(canonical.getClass()
						.getName(), dto.getKeyId()));
			}

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static <T extends BaseDomain, U extends BaseDto> void copyToDto(
			T canonical, U dto) {
		try {
			BeanUtils.copyProperties(dto, canonical);
			if (canonical.getKey() != null) {
				dto.setKeyId(canonical.getKey().getId());
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
