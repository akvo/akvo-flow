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

import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CompoundRulePopup extends PopupPanel {

	public CompoundRulePopup(String standardType,
			StandardScoringManagerServiceAsync svc, Boolean displayDetail) {
		super(true);

		if (displayDetail) {
			initDetail(standardType, svc);
		} else {
			initList(standardType, svc);
		}

	}

	VerticalPanel vp = new VerticalPanel();

	public void initDetail(String standardType,
			StandardScoringManagerServiceAsync svc) {
		setWidget(new Label("Compound Rule Manager"));
		CompoundStandardDetail csd = new CompoundStandardDetail(standardType,
				svc);

		vp.add(csd);
		vp.add(close);
		setWidget(vp);
		addCloseHandler();
	}

	Button close = new Button("Close");

	public void initList(String standardType,
			StandardScoringManagerServiceAsync svc) {
		setWidget(new Label("Compound Rule List View"));
		CompoundRuleListView crlv = new CompoundRuleListView(standardType, svc);
		RootPanel rp = RootPanel.get();

		vp.add(crlv);
		HorizontalPanel hPanel = new HorizontalPanel();
		addCloseHandler();
		hPanel.add(close);
		vp.add(hPanel);
		rp.add(vp);
	}

	private void addCloseHandler() {
		close.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

	}

}
