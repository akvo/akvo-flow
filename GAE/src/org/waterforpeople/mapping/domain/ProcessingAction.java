package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ProcessingAction {
	private String action;
	private String dispatchURL;
	private HashMap<String, String> params;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDispatchURL() {
		return dispatchURL;
	}

	public void setDispatchURL(String dispatchURL) {
		this.dispatchURL = dispatchURL;
	}

	public HashMap<String,String> getParams() {
		return params;
	}

	public void setParams(HashMap<String,String> params) {
		this.params = params;
	}

	public void addParam(String key, String value) {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		params.put(key, value);
	}
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(this.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		// determine fields declared in this class only (no fields of
		// superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				// requires access to private field:
				result.append(field.get(this));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}

}
