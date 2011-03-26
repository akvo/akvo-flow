package org.waterforpeople.mapping.surveyentry.client.component;

import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.util.client.WidgetDialog;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * handles geo questions
 * 
 * @author Christopher Fagiani
 * 
 */
public class GeoQuestionWidget extends QuestionWidget implements ClickHandler,
		CompletionListener {
	
	private static final String TYPE = "GEO";
	private static final String DELIM = "|";
	private static final String DELIM_REGEX = "\\|";
	private TextBox lat;
	private TextBox lon;
	private TextBox alt;
	private TextBox code;
	private Button locateExisting;
	private Button mapButton;
	private boolean isApprox;

	public GeoQuestionWidget(QuestionDto q, QuestionAnswerStoreDto a) {
		super(q, a);
	}

	@Override
	protected void constructResponseUi() {
		isApprox = false;
		lat = new TextBox();
		ViewUtil.installFieldRow(getPanel(), TEXT_CONSTANTS.latitude(), lat, null);
		lon = new TextBox();
		ViewUtil.installFieldRow(getPanel(), TEXT_CONSTANTS.longitude(), lon, null);
		alt = new TextBox();
		ViewUtil.installFieldRow(getPanel(), TEXT_CONSTANTS.altitude(), alt, null);
		code = new TextBox();
		ViewUtil.installFieldRow(getPanel(), TEXT_CONSTANTS.code(), code, null);
		if (getAnswer().getKeyId() != null) {
			// if we're initializing and key id is not null, pre-populate
			String val = getAnswer().getValue();
			if (val != null) {
				String[] valParts = val.split(DELIM_REGEX);
				if (valParts.length >= 2) {
					lat.setValue(valParts[0]);
					lon.setValue(valParts[1]);
				}
				if (valParts.length >= 3) {
					alt.setValue(valParts[2]);
				}
				if (valParts.length >= 4) {
					code.setValue(valParts[3]);
				}
			}

		}
		locateExisting = new Button(TEXT_CONSTANTS.findAccessPoint());
		locateExisting.addClickHandler(this);
		getPanel().add(locateExisting);
		mapButton = new Button(TEXT_CONSTANTS.viewMap());
		mapButton.addClickHandler(this);
		getPanel().add(mapButton);
	}

	public boolean isApproximate() {
		return isApprox;
	}

	public void captureAnswer() {
		getAnswer().setType(TYPE);
		StringBuilder value = new StringBuilder();
		// answers don't count unless lat and lon are captured
		if (ViewUtil.isTextPopulated(lat) && ViewUtil.isTextPopulated(lon)) {
			value.append(lat.getText().trim()).append(DELIM).append(
					lon.getText().trim()).append(DELIM);
			value.append(
					ViewUtil.isTextPopulated(alt) ? alt.getText().trim() : "")
					.append(DELIM);
			value.append(ViewUtil.isTextPopulated(code) ? code.getText().trim()
					: "");
			getAnswer().setValue(value.toString());
		}
	}

	@Override
	protected void resetUi() {
		lat.setText("");
		lon.setText("");
		alt.setText("");
		code.setText("");
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == locateExisting) {
			AccessPointLocatorDialog dia = new AccessPointLocatorDialog(this);
			dia.showCentered();
		} else if (event.getSource() == mapButton) {
			// TODO: parameterize
			final WidgetDialog dia = new WidgetDialog(TEXT_CONSTANTS.selectLocation(),null);
			LatLng point = LatLng.newInstance(6.571, -9.351);
			MapWidget map = new MapWidget(point, 10);
			map.setWidth("400px");
			map.setHeight("400px");
			map.addMapClickHandler(new MapClickHandler() {
				@Override
				public void onClick(MapClickEvent event) {
					if (event.getLatLng() != null) {
						populateFromMap(event.getLatLng().getLatitude(), event
								.getLatLng().getLongitude());
						dia.hide();
					}

				}
			});
			dia.setContentWidget(map);
			dia.showCentered();
		}
	}

	private void populateFromMap(double latVal, double lonVal) {
		lat.setText("" + latVal);
		lon.setText("" + lonVal);
		isApprox = true;
	}

	@Override
	public void operationComplete(boolean wasSuccessful,
			Map<String, Object> payload) {
		if (payload != null
				&& payload.get(AccessPointLocatorDialog.SELECTED_AP_KEY) != null) {
			isApprox = false;
			AccessPointDto ap = (AccessPointDto) payload
					.get(AccessPointLocatorDialog.SELECTED_AP_KEY);
			if (ap.getLatitude() != null) {
				lat.setText(ap.getLatitude().toString());
			}
			if (ap.getLongitude() != null) {
				lon.setText(ap.getLongitude().toString());
			}
			if (ap.getCommunityCode() != null) {
				code.setText(ap.getCommunityCode());
			}
			if (ap.getAltitude() != null) {
				alt.setText(ap.getAltitude().toString());
			}
		}
	}
}
