package org.waterforpeople.mapping.app.gwt.client.standardscoring;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;


public class CompoundStandardDto extends BaseDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1642166127044477715L;
	
	public enum Operator {
		AND, OR, NOT
	}

	/**
	 * 
	 */
	private Long standardIdLeft = null;
	private Long standardIdRight = null;
	private String standardLeftDesc = null;
	private String standardRightDesc = null;
	
	private Operator operator = null;
	public enum StandardType {
		WaterPointLevelOfService, WaterPointSustainability
	};
	
	public enum StandardValueType{
		Number, String, Boolean
	}

	public enum StandardComparisons{
		equal, notequal, lessthan, greaterthan, greaterthanorequal, lessthanorequal
	}
	private StandardType standardType = null;
	

	public StandardType getStandardType() {
		return standardType;
	}

	public void setStandardType(StandardType standardType) {
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

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public void setStandardLeftDesc(String standardLeftDesc) {
		this.standardLeftDesc = standardLeftDesc;
	}

	public String getStandardLeftDesc() {
		return standardLeftDesc;
	}

	public void setStandardRightDesc(String standardRightDesc) {
		this.standardRightDesc = standardRightDesc;
	}

	public String getStandardRightDesc() {
		return standardRightDesc;
	}
}
