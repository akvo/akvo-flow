/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

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
	public enum RuleType {
		DISTANCE,NONDISTANCE
	}

	/**
	 * 
	 */
	private String name = null;
	private Long standardIdLeft = null;
	private RuleType standardLeftRuleType = null;
	private RuleType standardRightRuleType = null;
	private Long standardIdRight = null;
	private String standardLeftDesc = null;
	private String standardRightDesc = null;

	private Operator operator = null;

	public enum StandardType {
		WaterPointLevelOfService, WaterPointSustainability, PublicInstitutionLevelOfService, PublicInstitutionSustainability
	};

	public enum StandardValueType {
		Number, String, Boolean
	}

	public enum StandardComparisons {
		equal, notequal, lessthan, greaterthan, greaterthanorequal, lessthanorequal
	}

	private StandardType standardType = null;

	public RuleType getStandardLeftRuleType() {
		return standardLeftRuleType;
	}

	public void setStandardLeftRuleType(RuleType standardLeftRuleType) {
		this.standardLeftRuleType = standardLeftRuleType;
	}

	public RuleType getStandardRightRuleType() {
		return standardRightRuleType;
	}

	public void setStandardRightRuleType(RuleType standardRightRuleType) {
		this.standardRightRuleType = standardRightRuleType;
	}

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

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
