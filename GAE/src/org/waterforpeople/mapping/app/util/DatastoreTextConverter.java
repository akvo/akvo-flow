package org.waterforpeople.mapping.app.util;

import org.apache.commons.beanutils.converters.AbstractConverter;

import com.google.appengine.api.datastore.Text;

/**
 * converts from datastore Text objects to strings & vice versa
 * 
 * @author Christopher Fagiani
 * 
 */
@SuppressWarnings("rawtypes")
public class DatastoreTextConverter extends AbstractConverter {

	@Override
	protected Object convertToType(Class type, Object value) throws Throwable {
		if (value != null) {
			if (type == String.class) {
				if (value instanceof Text) {
					return ((Text) value).getValue();
				} else {
					return value.toString();
				}
			} else if (type == Text.class) {
				if (value instanceof String) {
					return new Text((String) value);
				} else if (value instanceof Text) {
					return value;
				} else {
					return new Text(value.toString());
				}
			}
		}
		return null;
	}

	@Override
	protected String convertToString(Object value) throws Throwable {
		return (String) convertToType(String.class, value);
	}

	@Override
	public Object handleMissing(Class type) {
		return null;
	}

	@Override
	protected Class getDefaultType() {
		return Text.class;
	}

}
