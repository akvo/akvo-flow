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
	private TextBox lat;
	private TextBox lon;
	private TextBox alt;
	private TextBox code;

	public GeoQuestionWidget(QuestionDto q) {
		super(q);
	}

	@Override
	protected void bindResponseSection() {
		lat = new TextBox();
		ViewUtil.installFieldRow(getPanel(), "Latitude", lat, null);
		lon = new TextBox();
		ViewUtil.installFieldRow(getPanel(), "Longitude", lon, null);
		alt = new TextBox();
		ViewUtil.installFieldRow(getPanel(), "Altitude", alt, null);
		code = new TextBox();
		ViewUtil.installFieldRow(getPanel(), "Code", code, null);
	}

}
