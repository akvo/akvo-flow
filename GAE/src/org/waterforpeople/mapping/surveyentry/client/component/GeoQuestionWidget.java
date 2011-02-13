package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * handles geo questions
 * 
 * @author Christopher Fagiani
 * 
 */
public class GeoQuestionWidget extends QuestionWidget {
	private static final String TYPE = "GEO";
	private static final String DELIM = "|";
	private TextBox lat;
	private TextBox lon;
	private TextBox alt;
	private TextBox code;

	public GeoQuestionWidget(QuestionDto q) {
		super(q);
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

}
