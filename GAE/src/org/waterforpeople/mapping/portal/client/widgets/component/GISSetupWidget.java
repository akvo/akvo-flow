package org.waterforpeople.mapping.portal.client.widgets.component;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.Orientation;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.TerminalType;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GISSetupWidget extends Composite implements ChangeHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static final String LABEL_STYLE = "input-label-padded";
	private static final int DEFAULT_ITEM_COUNT = 5;
	private Panel contentPanel;
	private MessageDialog loadingDialog;
	private TerminalType termType;
	private ListBox coordinateType = new ListBox();
	private ListBox utmZone = new ListBox();

	public enum Orientation {
		VERTICAL, HORIZONTAL
	};

	public enum TerminalType {
		SURVEY, QUESTIONGROUP
	};

	public GISSetupWidget(
			Orientation orient,
			org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.TerminalType survey) {
		if (Orientation.HORIZONTAL == orient) {
			contentPanel = new HorizontalPanel();
		} else {
			contentPanel = new VerticalPanel();
		}
		coordinateType.addItem("LAT/LNG");
		coordinateType.addItem("UTM");
		for (Integer i = 1; i < 60; i++) {
			utmZone.addItem(i.toString());
		}
		ViewUtil.installFieldRow(contentPanel, TEXT_CONSTANTS.selectCoordinateSystem(),
				coordinateType, LABEL_STYLE);
		initWidget(contentPanel);
	}

	@Override
	public void onChange(ChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
