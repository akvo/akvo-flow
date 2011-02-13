package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

/**
 * handles option questions
 * 
 * @author Christopher Fagiani
 * 
 */
public class OptionQuestionWidget extends QuestionWidget implements
		ChangeHandler {

	private ListBox listBox;
	private QuestionAnswerListener listener;

	public OptionQuestionWidget(QuestionDto q, QuestionAnswerListener listener) {
		super(q);
		this.listener = listener;
	}

	@Override
	protected void bindResponseSection() {

		if (getQuestion().getOptionContainerDto() != null) {
			listBox = new ListBox(getQuestion().getOptionContainerDto()
					.getAllowMultipleFlag());
			if (getQuestion().getOptionContainerDto().getAllowMultipleFlag()) {
				listBox.setVisibleItemCount(getQuestion()
						.getOptionContainerDto().getOptionsList().size());
			} else {
				listBox.addItem("", "");
			}
			for (QuestionOptionDto opt : getQuestion().getOptionContainerDto()
					.getOptionsList()) {
				listBox.addItem(opt.getText(), opt.getText());
			}
			listBox.addChangeHandler(this);
		}
		getPanel().add(listBox);
	}

	@Override
	public void onChange(ChangeEvent event) {
		StringBuilder buf = new StringBuilder();
		int count = 0;
		for (int i = 0; i < listBox.getItemCount(); i++) {
			if (listBox.isItemSelected(i)) {
				if (count > 0) {
					buf.append("|");
				}
				buf.append(listBox.getValue(i));
				count++;
			}
		}
		if (listener != null) {
			listener.answerUpdated(getQuestion().getKeyId(), buf.toString());
		}
	}
}
