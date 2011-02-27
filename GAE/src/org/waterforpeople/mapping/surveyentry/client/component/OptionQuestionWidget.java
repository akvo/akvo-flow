package org.waterforpeople.mapping.surveyentry.client.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * handles option questions
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class OptionQuestionWidget extends QuestionWidget implements
		ChangeHandler {

	private static final String OTHER_TYPE = "OTHER";
	private static final String TYPE = "VALUE";
	private static final String OTHER_TEXT = "Other...";
	private static final String DELIM = "|";
	private static final String DELIM_REGEX = "\\|";
	private ListBox listBox;
	private TextBox otherBox;
	private Panel contentPanel;
	private QuestionAnswerListener listener;
	private boolean otherIsSelected;

	public OptionQuestionWidget(QuestionDto q, QuestionAnswerStoreDto a, QuestionAnswerListener listener) {
		super(q,a);
		otherIsSelected = false;
		this.listener = listener;
	}

	@Override
	protected void constructResponseUi() {
		contentPanel = new VerticalPanel();
		if (getQuestion().getOptionContainerDto() != null) {
			listBox = new ListBox(getQuestion().getOptionContainerDto()
					.getAllowMultipleFlag());
			contentPanel.add(listBox);
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
			if (getQuestion().getOptionContainerDto().getAllowOtherFlag()) {
				listBox.addItem(OTHER_TEXT, OTHER_TEXT);
				otherBox = new TextBox();
				otherBox.setVisible(false);
				contentPanel.add(otherBox);
			}
			listBox.addChangeHandler(this);
		}

		if (getAnswer().getKeyId() != null) {
			// if we're initializing and key id is not null, prepopulate
			if (getAnswer().getValue() != null) {
				String[] ans = getAnswer().getValue().split(DELIM_REGEX); 
				StringBuilder otherText = new StringBuilder();
				for(int i =0; i < ans.length; i++){					
					boolean found = false;
					for (int j = 0; j < listBox.getItemCount(); j++) {
						if(listBox.getItemText(j).equalsIgnoreCase(ans[i])){
							listBox.setItemSelected(j, true);
							found = true;
						}
					}
					if(!found){						
						otherText.append(ans);												
					}
				}
				if(OTHER_TYPE.equals(getAnswer().getType())){
					otherBox.setVisible(true);
					otherBox.setValue(otherText.toString());							
				}
			}
		}
		getPanel().add(contentPanel);
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (otherBox != null) {
			for (int i = 0; i < listBox.getItemCount(); i++) {
				if (OTHER_TEXT.equals(listBox.getValue(i))) {
					if (otherIsSelected && !listBox.isItemSelected(i)) {
						otherBox.setText("");
						otherBox.setVisible(false);
						otherIsSelected = false;
					} else if (!otherIsSelected && listBox.isItemSelected(i)) {
						otherIsSelected = true;
						otherBox.setVisible(true);
					}
				}
			}
		}
		String response = getSelectionAsString();
		if (listener != null) {
			listener.answerUpdated(getQuestion().getKeyId(),
					response != null ? response : "");
		}
	}

	@Override
	protected void captureAnswer() {
		if (otherIsSelected) {
			getAnswer().setType(OTHER_TYPE);
		} else {
			getAnswer().setType(TYPE);
		}
		getAnswer().setValue(getSelectionAsString());

	}

	protected String getSelectionAsString() {
		StringBuilder buf = new StringBuilder();
		int count = 0;
		for (int i = 0; i < listBox.getItemCount(); i++) {
			if (listBox.isItemSelected(i)) {
				if (!listBox.getValue(i).equals(OTHER_TEXT)) {
					if (count > 0) {
						buf.append(DELIM);
					}
					buf.append(listBox.getValue(i));
					count++;
				}
			}
			if (otherIsSelected) {
				if (count > 0) {
					buf.append(DELIM);
				}
				if (ViewUtil.isTextPopulated(otherBox)) {
					buf.append(otherBox.getText());
				} else {
					buf.append(OTHER_TEXT);
				}
			}
		}
		String result = buf.toString().trim();
		if (count > 0 && result.length() > 0) {
			return result;
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
		if (otherBox != null) {
			otherBox.setText("");
			otherBox.setVisible(false);
			otherIsSelected = false;
		}
	}
}
