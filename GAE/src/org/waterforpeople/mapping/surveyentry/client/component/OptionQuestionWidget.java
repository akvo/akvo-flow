package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;

/**
 * handles option questions
 * 
 * TODO: handle "other" question type
 * 
 * @author Christopher Fagiani
 * 
 */
public class OptionQuestionWidget extends QuestionWidget implements
		ChangeHandler {

	private static final String OTHER_TYPE = "OTHER";
	private static final String TYPE = "VALUE";
	private ListBox listBox;
	private QuestionAnswerListener listener;

	public OptionQuestionWidget(QuestionDto q, QuestionAnswerListener listener) {
		super(q);
		this.listener = listener;
	}

	@Override
	protected void constructResponseUi() {

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
		String response = getSelectionAsString();
		if (listener != null) {
			listener.answerUpdated(getQuestion().getKeyId(),
					response != null ? response : "");
		}
	}

	@Override
	protected void captureAnswer() {
		getAnswer().setType(TYPE);
		getAnswer().setValue(getSelectionAsString());

	}

	protected String getSelectionAsString() {
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
		if (count > 0 && buf.toString().trim().length() > 0) {
			return buf.toString();
		} else {
			return null;
		}
	}

	@Override
	protected void resetUi() {
		for (int i = 0; i < listBox.getItemCount(); i++) {
			if (listBox.isItemSelected(i)) {
				listBox.setItemSelected(i, false);
			}
		}

	}
}
