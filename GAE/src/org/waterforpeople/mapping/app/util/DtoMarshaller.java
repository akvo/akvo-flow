package org.waterforpeople.mapping.app.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.framework.BaseDto;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.KeyFactory;

public class DtoMarshaller<T extends BaseDomain, U extends BaseDto> {
	private T canonical;
	private U dto;

	public DtoMarshaller(T canonical, U dto) {
		this.canonical = canonical;
		this.dto = dto;
	}

	public void copyToCanonical(T dest, U orig, Class<T> clazz) {
		try {
			BeanUtils.copyProperties(dest, orig);
			dest.setKey(KeyFactory.createKey(clazz.getName(), orig.getKeyId()));

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void copyToDto(U dest,
			T orig) {
		try {
			BeanUtils.copyProperties(dest, orig);
			dest.setKeyId(orig.getKey().getId());

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
