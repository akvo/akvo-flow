package org.waterforpeople.mapping.surveyentry.client.component;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * handles option questions
 * 
 * @author Christopher Fagiani
 * 
 */
public class OptionQuestionWidget extends QuestionWidget {

	private List<CheckBox> checkBoxes;
	private List<RadioButton> radioButtons;
	private ListBox listBox;
	private Panel optPanel;

	public OptionQuestionWidget(QuestionDto q) {
		super(q);
		optPanel = new VerticalPanel();
	}

	@Override
	protected void bindResponseSection() {
		if (getQuestion().getOptionContainerDto() != null) {
			if (getQuestion().getOptionContainerDto().getAllowMultipleFlag()) {
				checkBoxes = new ArrayList<CheckBox>();
				for (QuestionOptionDto opt : getQuestion()
						.getOptionContainerDto().getOptionsList()) {
					CheckBox box = new CheckBox(opt.getText());
					optPanel.add(box);

				}
			} else {
				radioButtons = new ArrayList<RadioButton>();
				for (QuestionOptionDto opt : getQuestion()
						.getOptionContainerDto().getOptionsList()) {
					RadioButton rad = new RadioButton(getQuestion().getKeyId()
							.toString(), opt.getText());
					optPanel.add(rad);
				}
			}
		}
		getPanel().add(optPanel);
	}
}
