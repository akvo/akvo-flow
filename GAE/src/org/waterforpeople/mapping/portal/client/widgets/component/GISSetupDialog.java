package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.TerminalType;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.WidgetDialog;
import com.gallatinsystems.gis.app.gwt.client.GISSupportConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GISSetupDialog extends WidgetDialog implements ClickHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
	.create(TextConstants.class);	
	public static final String COORDINATE_SYSTEM_TYPE_KEY = "coordinateSystemType";
	private GISSetupWidget selector;
	private Button okButton;
	private Button cancelButton;	
	private Label messageLabel;
	
	
	public GISSetupDialog(CompletionListener listener) {
		super(TEXT_CONSTANTS.selectCoordinateSystem(), null, true, listener);
		Panel panel = new VerticalPanel();
		messageLabel = new Label();
		Panel buttonPanel = new HorizontalPanel();
		selector = new GISSetupWidget(GISSetupWidget.Orientation.HORIZONTAL,
				TerminalType.SURVEY);
		panel.add(selector);
		panel.add(messageLabel);
		messageLabel.setVisible(false);
		okButton = new Button(TEXT_CONSTANTS.ok());
		okButton.addClickHandler(this);
		cancelButton = new Button(TEXT_CONSTANTS.cancel());
		cancelButton.addClickHandler(this);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		panel.add(buttonPanel);
		setContentWidget(panel);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == cancelButton) {
			hide(true);
		} else if (event.getSource() == okButton) {
			hide(true);
			Map<String, Object> payload = new HashMap<String, Object>();
			payload.put(GISSupportConstants.COORDINATE_SYSTEM_TYPE_PARAM,selector.getCoordinateType());
			payload.put(GISSupportConstants.COUNTRY_CODE_PARAM, selector.getCountryCode());
			payload.put(GISSupportConstants.UTM_ZONE_PARAM, selector.getUTMZone());
			payload.put(GISSupportConstants.CENTRAL_MERIDIAN_PARAM, selector.getCentralMeridian());
			payload.put(GISSupportConstants.GIS_FEATURE_TYPE_PARAM, selector.getGISFeatureType());
			notifyListener(true, payload);
		}
	}

}
