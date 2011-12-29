package com.gallatinsystems.standards.domain;

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
	private Long standardIdLeft = null;
	private Long standardIdRight = null;
	@NotPersistent
	private Standard standardLeft = null;
	@NotPersistent
	private Standard standardRight = null;

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

	public Standard getStandardLeft() {
		return standardLeft;
	}

	public void setStandardLeft(Standard standardScoreLeft) {
		this.standardLeft = standardScoreLeft;
		this.standardLeft.setPartOfCompoundRule(true);
	}

	public Standard getStandardRight() {
		return standardRight;
	}

	public void setStandardRight(Standard standardScoreRight) {
		this.standardRight = standardScoreRight;
		this.standardRight.setPartOfCompoundRule(true);
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}
}
