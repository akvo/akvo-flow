package org.waterforpeople.mapping.portal.client.widgets.component.standardscoring;

import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerServiceAsync;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class CompoundRulePopup extends PopupPanel {
	public CompoundRulePopup(String standardType,
			StandardScoringManagerServiceAsync svc) {
		super(true);
		init(standardType, svc);

	}

	public void init(String standardType, StandardScoringManagerServiceAsync svc) {
		setWidget(new Label("Compound Rule Manager"));
		CompoundStandardDetail csd = new CompoundStandardDetail(standardType,
				svc);
		setWidget(csd);
	}

}
