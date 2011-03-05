package org.waterforpeople.mapping.surveyentry.client.component;

import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

	public GeoQuestionWidget(QuestionDto q, QuestionAnswerStoreDto a) {
		super(q, a);
	}

	@Override
	protected void constructResponseUi() {
		lat = new TextBox();
		ViewUtil.installFieldRow(getPanel(), "Latitude", lat, null);
		lon = new TextBox();
		ViewUtil.installFieldRow(getPanel(), "Longitude", lon, null);
		alt = new TextBox();
		ViewUtil.installFieldRow(getPanel(), "Altitude", alt, null);
		code = new TextBox();
		ViewUtil.installFieldRow(getPanel(), "Code", code, null);
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
		locateExisting = new Button("Find Existing Point");
		locateExisting.addClickHandler(this);
		getPanel().add(locateExisting);
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
		}

	}

	@Override
	public void operationComplete(boolean wasSuccessful,
			Map<String, Object> payload) {
		if (payload != null
				&& payload.get(AccessPointLocatorDialog.SELECTED_AP_KEY) != null) {
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
