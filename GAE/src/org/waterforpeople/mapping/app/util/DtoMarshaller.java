package org.waterforpeople.mapping.app.util;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.waterforpeople.mapping.app.gwt.client.formdefinition.DataEntryFormDefinitionDto;

import com.gallatinsystems.common.dataentry.domain.DataEntryFormDefinition;
import com.google.appengine.api.datastore.KeyFactory;


public class DtoMarshaller {

	public static void copyToCanonical(DataEntryFormDefinition dest, DataEntryFormDefinitionDto orig){
		try {
			BeanUtils.copyProperties(dest, orig);
			dest.setKey(KeyFactory.createKey(dest.getName(),orig.getKeyId() ));
			
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void copyToDto(DataEntryFormDefinitionDto dest, DataEntryFormDefinition orig ){
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
