package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ReportType {
	private String code;
	private	HashMap params;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public HashMap getParams() {
		return params;
	}
	public void setParams(HashMap params) {
		this.params = params;
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
