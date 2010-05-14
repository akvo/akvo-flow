package org.waterforpeople.mapping.app.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.google.appengine.api.datastore.KeyFactory;

public class DtoMarshaller {

	public static <T extends BaseDomain, U extends BaseDto> void copyToCanonical(
			T canonical, U dto) {
		try {
			String pattern = "MM/dd/yy";
			Locale locale = Locale.getDefault();
			DateLocaleConverter converter = new DateLocaleConverter(locale,pattern);
			converter.setLenient(true);
			ConvertUtils.register(converter, java.util.Date.class);
			BeanUtils.copyProperties(canonical, dto);
			if (dto.getKeyId() != null) {
				// by default, the JDO key kind uses the Simple name
				canonical.setKey(KeyFactory.createKey(canonical.getClass()
						.getSimpleName(), dto.getKeyId()));
			}

		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(ConversionException e){
			e.printStackTrace();
		}
	}

	public static <T extends BaseDomain, U extends BaseDto> void copyToDto(
			T canonical, U dto) {
		try {
			String pattern = "MM/dd/yy";
			Locale locale = Locale.getDefault();
			DateLocaleConverter converter = new DateLocaleConverter(locale,pattern);
			converter.setLenient(true);
			ConvertUtils.register(converter, java.util.Date.class);
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
