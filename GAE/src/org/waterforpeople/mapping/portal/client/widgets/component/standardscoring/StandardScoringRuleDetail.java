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

package org.waterforpeople.mapping.portal.client.widgets.component.standardscoring;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;

public class StandardScoringRuleDetail extends Composite implements
		ClickHandler {

	/*
	 * new DataTableHeader(TEXT_CONSTANTS.globalStandard(), "globalStandard",
	 * true), new DataTableHeader(TEXT_CONSTANTS.countryCode(), "countryCode",
	 * true), new DataTableHeader(TEXT_CONSTANTS.subValue(), "subValue", true),
	 * new DataTableHeader(TEXT_CONSTANTS.pointType(), "pointType", true), new
	 * DataTableHeader(TEXT_CONSTANTS.description(), "displayName", true), new
	 * DataTableHeader(TEXT_CONSTANTS.evaluateField(), "evaluateField", true),
	 * new DataTableHeader(TEXT_CONSTANTS.criteriaType(), "criteriaType", true),
	 * new DataTableHeader(TEXT_CONSTANTS.positiveCriteria(),
	 * "positiveCriteria", true), new
	 * DataTableHeader(TEXT_CONSTANTS.positiveOperator(), "positiveOperator",
	 * true), new DataTableHeader(TEXT_CONSTANTS.positiveScore(),
	 * "positiveCriteria", true), new
	 * DataTableHeader(TEXT_CONSTANTS.positiveMessage(), "positiveMessage",
	 * true), new DataTableHeader(TEXT_CONSTANTS.negativeCriteria(),
	 * "negativeCriteria", true), new
	 * DataTableHeader(TEXT_CONSTANTS.negativeOperator(), "negativeOperator",
	 * true), new DataTableHeader(TEXT_CONSTANTS.negativeScore(),
	 * "negativeScore", true), new
	 * DataTableHeader(TEXT_CONSTANTS.negativeOverride(), "negativeOverride",
	 * true), new DataTableHeader(TEXT_CONSTANTS.negativeMessage(),
	 * "negativeMessage", true), new
	 * DataTableHeader(TEXT_CONSTANTS.effectiveStartDate(),
	 * "effectiveStartDate", true), new
	 * DataTableHeader(TEXT_CONSTANTS.effectiveEndDate(), "effectiveEndDate",
	 * true), new DataTableHeader("ID", "key", false), new
	 * DataTableHeader(TEXT_CONSTANTS.editDelete()) };
	 */

	TextBox id = new TextBox();
	ListBox globalStandard = new ListBox();
	ListBox countryCode = new ListBox();
	// Add Custom Sub Value FlexTable
	ListBox pointType = new ListBox();
	TextBox displayName = new TextBox();
	//Add Custom field to eval flextable
	TextBox positiveScore = new TextBox();
	CheckBox negativeOverride = new CheckBox();
	DateBox effectiveStartDate = new DateBox();
	DateBox effectiveEndDate =new DateBox();
	Button saveButton = new Button();
	Button deleteButton = new Button();
	Button cancelButton =new Button();
	
	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}

}
