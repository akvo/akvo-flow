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

    public enum RuleType {
        DISTANCE, NONDISTANCE
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 5812262258895279483L;
    private String name = null;
    private Long standardIdLeft = null;
    private RuleType standardLeftRuleType = null;
    private Long standardIdRight = null;
    private RuleType standardRightRuleType = null;
    @NotPersistent
    private StandardDef standardLeft = null;
    @NotPersistent
    private StandardDef standardRight = null;

    private Operator operator = null;

    private Standard.StandardType standardType = null;

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
