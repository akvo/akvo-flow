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

package org.waterforpeople.mapping.app.gwt.client.survey;

import java.io.Serializable;
import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class OptionContainerDto extends BaseDto implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = -4002853404925002791L;
    private ArrayList<QuestionOptionDto> optionsList = null;
    private Boolean allowMultipleFlag = null;

    public Boolean getAllowOtherFlag() {
        return allowOtherFlag;
    }

    public void setAllowOtherFlag(Boolean allowOtherFlag) {
        this.allowOtherFlag = allowOtherFlag;
    }

    public void setOptionsList(ArrayList<QuestionOptionDto> optionsList) {
        this.optionsList = optionsList;
    }

    public ArrayList<QuestionOptionDto> getOptionsList() {
        return optionsList;
    }

    private Boolean allowOtherFlag = null;

    public void addQuestionOption(QuestionOptionDto questionOption) {
        if (optionsList == null)
            optionsList = new ArrayList<QuestionOptionDto>();
        optionsList.add(questionOption);
    }

    public void setAllowMultipleFlag(Boolean allowMultipleFlag) {
        this.allowMultipleFlag = allowMultipleFlag;
    }

    public Boolean getAllowMultipleFlag() {
        return allowMultipleFlag;
    }
}
