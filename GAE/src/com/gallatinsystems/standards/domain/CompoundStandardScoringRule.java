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
        if (standardScoreLeft.getKey() != null)
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
