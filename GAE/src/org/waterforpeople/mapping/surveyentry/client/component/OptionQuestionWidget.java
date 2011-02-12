package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;

import com.google.gwt.user.client.ui.ListBox;

/**
 * handles option questions
 * 
 * @author Christopher Fagiani
 * 
 */
public class OptionQuestionWidget extends QuestionWidget {

	private ListBox listBox;

	public OptionQuestionWidget(QuestionDto q) {
		super(q);
	}

	@Override
	protected void bindResponseSection() {

		if (getQuestion().getOptionContainerDto() != null) {
			listBox = new ListBox(getQuestion().getOptionContainerDto()
					.getAllowMultipleFlag());
			if(getQuestion().getOptionContainerDto()
					.getAllowMultipleFlag()){
				listBox.setVisibleItemCount(getQuestion()
						.getOptionContainerDto().getOptionsList().size());	
			}else{
				listBox.addItem("","");
			}
			for (QuestionOptionDto opt : getQuestion().getOptionContainerDto()
					.getOptionsList()) {
				listBox.addItem(opt.getText(), opt.getText());
			}			

		}
		getPanel().add(listBox);
	}
}
