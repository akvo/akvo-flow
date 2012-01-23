package com.gallatinsystems.standards.domain;

import java.lang.reflect.Field;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
@PersistenceCapable
public class CompoundStandard extends BaseDomain {
	public enum Operator {
		AND, OR, NOT
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5812262258895279483L;
	private String name = null;
	private Long standardIdLeft = null;
	private Long standardIdRight = null;
	@NotPersistent
	private StandardDef standardLeft = null;
	@NotPersistent
	private StandardDef standardRight = null;

	private Operator operator = null;
	
	private Standard.StandardType standardType = null;
	

	public Standard.StandardType getStandardType() {
		return standardType;
	}

	public void setStandardType(Standard.StandardType standardType) {
		this.standardType = standardType;
	}
	
	public Long getStandardIdLeft() {
		return standardIdLeft;
	}

	public void setStandardIdLeft(Long standardIdLeft) {
		this.standardIdLeft = standardIdLeft;
	}

	public Long getStandardIdRight() {
		return standardIdRight;
	}

	public void setStandardIdRight(Long standardIdRight) {
		this.standardIdRight = standardIdRight;
	}

	public StandardDef getStandardLeft() {
		return standardLeft;
	}

	public void setStandardLeft(StandardDef standardScoreLeft) {
		this.standardLeft = standardScoreLeft;
		this.standardLeft.setPartOfCompoundRule(true);
	}

	public StandardDef getStandardRight() {
		return standardRight;
	}

	public void setStandardRight(StandardDef standardScoreRight) {
		this.standardRight = standardScoreRight;
		this.standardRight.setPartOfCompoundRule(true);
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
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

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
