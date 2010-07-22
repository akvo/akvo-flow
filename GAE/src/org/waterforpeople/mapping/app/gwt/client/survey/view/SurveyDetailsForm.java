package org.waterforpeople.mapping.app.gwt.client.survey.view;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SurveyDetailsForm extends DetailForm {
	private TextBox surveyId = new TextBox();
	private TextBox surveyName = new TextBox();
	private TextBox version = new TextBox();
	private TextBox createDate = new TextBox();
	private TextBox description = new TextBox();
	private TextBox status = new TextBox();
	
	final VerticalPanel panel = new VerticalPanel();

	private String[] controlList = {"TextBox|surveyId|null","TextBox|surveyName|Survey Name"};
	
	private void loadControls() {
		panel.add(surveyId);
		surveyId.setVisible(false);
		panel.add(surveyName);
		panel.add(version);
		panel.add(description);
		panel.add(createDate);
	}

	private void bindControls(SurveyDto surveyDto) {
		if (surveyDto != null) {
			if (surveyDto.getKeyId() != null)
				surveyId.setText(surveyDto.getKeyId().toString());
			if (surveyDto.getName() != null)
				surveyName.setText(surveyDto.getName());
			if (surveyDto.getDescription() != null)
				description.setText(surveyDto.getDescription());
			if(surveyDto.getVersion()!=null)
				version.setText(surveyDto.getVersion());
			if(surveyDto.getStatus()!=null)
				status.setText(surveyDto.getStatus());
		}
	}

	public SurveyDetailsForm(SurveyDto surveyDto) {
		loadControls();
		bindControls(surveyDto);
	}
	
	public VerticalPanel bindControl(SurveyDto surveyDto){
		loadControls();
		bindControls(surveyDto);
		return getPanel();
	}

	public VerticalPanel getPanel() {
		return panel;
	}

}
