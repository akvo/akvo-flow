package org.waterforpeople.mapping.dataexport.service;

/**
 * name value pair helper class for sorting.
 * 
 * 
 */
public class NameValuePair implements Comparable<NameValuePair> {
	private String name;
	private String value;

	public NameValuePair(String n, String v) {
		name = n;
		value = v;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int compareTo(NameValuePair o) {
		if (o == null) {
			return 0;
		} else if (o instanceof NameValuePair) {
			return name.compareTo(o.name);
		} else {
			return 0;
		}
	}
}
