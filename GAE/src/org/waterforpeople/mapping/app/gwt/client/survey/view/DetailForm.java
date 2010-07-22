package org.waterforpeople.mapping.app.gwt.client.survey.view;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public abstract class DetailForm extends Composite {
	public TextBox addTextBox(String textValue, String width, String height) {
		TextBox tb = new TextBox();
		if (textValue != null)
			tb.setText(textValue);
		if (width != null)
			tb.setWidth(width);
		if (height != null)
			tb.setHeight(height);

		return tb;
	}

	public ListBox addListBox(String[] options, String width, String height) {
		ListBox lb = new ListBox();
		if (options != null)
			for (String item : options)
				lb.addItem(item.split("|")[0], item.split("|")[1]);
		return lb;
	}

	public Label addLable(String text, String width, String height) {
		Label lb = new Label();
		if (text != null)
			lb.setText(text);
		return lb;
	}
	
}
