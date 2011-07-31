package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
@PersistenceCapable
public class AccessPointScoreComputationItem extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5969112417112456855L;
	public AccessPointScoreComputationItem(Integer score, String item) {
		scoreItem = score;
		scoreDetailMessage = item;
	}

	private Integer scoreItem = null;
	private String scoreDetailMessage = null;
	public Integer getScoreItem() {
		return scoreItem;
	}
	public void setScoreItem(Integer scoreItem) {
		this.scoreItem = scoreItem;
	}
	public String getScoreDetailMessage() {
		return scoreDetailMessage;
	}
	public void setScoreDetailMessage(String scoreDetailMessage) {
		this.scoreDetailMessage = scoreDetailMessage;
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
			field.setAccessible(true);
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
