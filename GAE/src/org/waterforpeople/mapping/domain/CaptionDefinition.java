package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class CaptionDefinition extends BaseDomain{
	
	private String captionVariableName = null;
	private String captionValue = null;
	private Date captionEffectiveStartDate = null;
	private Date captionEffectiveEndDate = null;

	public String getCaptionVariableName() {
		return captionVariableName;
	}
	public void setCaptionVariableName(String captionVariableName) {
		this.captionVariableName = captionVariableName;
	}
	public String getCaptionValue() {
		return captionValue;
	}
	public void setCaptionValue(String captionValue) {
		this.captionValue = captionValue;
	}
	public Date getCaptionEffectiveStartDate() {
		return captionEffectiveStartDate;
	}
	public void setCaptionEffectiveStartDate(Date captionEffectiveStartDate) {
		this.captionEffectiveStartDate = captionEffectiveStartDate;
	}
	public Date getCaptionEffectiveEndDate() {
		return captionEffectiveEndDate;
	}
	public void setCaptionEffectiveEndDate(Date captionEffectiveEndDate) {
		this.captionEffectiveEndDate = captionEffectiveEndDate;
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
