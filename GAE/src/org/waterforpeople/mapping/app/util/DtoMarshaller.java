package org.waterforpeople.mapping.app.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.framework.BaseDto;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.KeyFactory;

public class DtoMarshaller<T extends BaseDomain, U extends BaseDto> {
	private T canonical;
	public T getCanonical() {
		return canonical;
	}

	public void setCanonical(T canonical) {
		this.canonical = canonical;
	}

	public U getDto() {
		return dto;
	}

	public void setDto(U dto) {
		this.dto = dto;
	}

	private U dto;

	public DtoMarshaller(T canonical, U dto) {
		this.canonical = canonical;
		this.dto = dto;
	}

	public void copyToCanonical() {
		try {
			BeanUtils.copyProperties(canonical, dto);
			canonical.setKey(KeyFactory.createKey(canonical.getClass()
					.getName(), dto.getKeyId()));

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void copyToDto() {
		try {
			BeanUtils.copyProperties(dto, canonical);
			dto.setKeyId(canonical.getKey().getId());

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
