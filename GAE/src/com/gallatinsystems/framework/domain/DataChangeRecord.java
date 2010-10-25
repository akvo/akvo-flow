package com.gallatinsystems.framework.domain;

/**
 * this is a non-persistent class used to represent logical change records. It
 * will encapsulate an object type, a field value and an old/new value.
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataChangeRecord {

	private static final String DELIMITER = "#~#";
	private String type;
	private String oldVal;
	private String newVal;
	private String id;

	public DataChangeRecord(String t, String i, String o, String n) {
		type = t;
		id = i;
		oldVal = o;
		newVal = n;
	}

	public DataChangeRecord(String packedString) {
		String[] parts = packedString.split(DELIMITER);
		if (parts.length < 3) {
			throw new RuntimeException("Packed string in invalid format");
		} else {
			type = parts[0];
			id = parts[1];
			oldVal = parts[2];
			if (parts.length > 3) {
				newVal = parts[3];
			} else {
				newVal = "";
			}
		}
	}

	public String packString() {
		return type + DELIMITER + id + DELIMITER + oldVal + DELIMITER + newVal;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOldVal() {
		return oldVal;
	}

	public void setOldVal(String oldVal) {
		this.oldVal = oldVal;
	}

	public String getNewVal() {
		return newVal;
	}

	public void setNewVal(String newVal) {
		this.newVal = newVal;
	}

}
