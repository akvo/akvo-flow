package com.gallatinsystems.standards.domain;

import javax.jdo.annotations.NotPersistent;

import com.gallatinsystems.framework.domain.BaseDomain;

public class CompoundStandardScoringRule extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6962070268251738131L;

	private Long standardScoreIdLeft = null;
	private Long standardScoreIdRight = null;
	@NotPersistent
	private StandardScoring standardScoreLeft = null;
	@NotPersistent
	private StandardScoring standardScoreRight = null;

	private Operator operator = null;

	public enum Operator {
		AND, OR
	}

	public StandardScoring getStandardScoreLeft() {
		return standardScoreLeft;
	}

	public void setStandardScoreLeft(StandardScoring standardScoreLeft) {
		this.standardScoreLeft = standardScoreLeft;
		if(standardScoreLeft.getKey()!=null)
			this.standardScoreIdLeft = standardScoreLeft.getKey().getId();
	}

	public StandardScoring getStandardScoreRight() {
		return standardScoreRight;
	}

	public void setStandardScoreRight(StandardScoring standardScoreRight) {
		this.standardScoreRight = standardScoreRight;
		if (standardScoreRight.getKey() != null)
			this.standardScoreIdRight = standardScoreRight.getKey().getId();
	}

	public Long getStandardScoreIdLeft() {
		return standardScoreIdLeft;
	}

	public void setStandardScoreIdLeft(Long standardScoreIdLeft) {
		this.standardScoreIdLeft = standardScoreIdLeft;
	}

	public Long getStandardScoreIdRight() {
		return standardScoreIdRight;
	}

	public void setStandardScoreIdRight(Long standardScoreIdRight) {
		this.standardScoreIdRight = standardScoreIdRight;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	};
}
