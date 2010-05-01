package org.waterforpeople.mapping.app.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.framework.BaseDto;

import com.gallatinsystems.framework.domain.BaseDomain;

public class DtoMarshaller<T extends BaseDto> {
	
	public void copyDtoToCanonical(Class<T> source, Class<T> dest) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		
		BeanUtils.copyProperties(dest, source);
		
	}

}
