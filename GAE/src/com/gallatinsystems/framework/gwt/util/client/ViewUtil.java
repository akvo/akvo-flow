package com.gallatinsystems.framework.gwt.util.client;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;

/**
 * simple utility for use client-side via GWT
 * 
 * @author Christopher Fagiani
 * 
 */
public class ViewUtil {

	private static final String DEFAULT_INPUT_LABEL_CSS = "input-label";

	/**
	 * returns true if the text box passed in contains at least 1 non-whitespace
	 * character
	 * 
	 * @param box
	 * @return
	 */
	public static boolean isTextPopulated(TextBoxBase box) {
		boolean populated = true;
		if (box == null || box.getText() == null
				|| box.getText().trim().length() <= 0) {
			populated = false;
		}
		return populated;
	}

	/**
	 * constructs a new label and sets its style. If no style is supplised, it
	 * will be set to the default label style.
	 * 
	 * @param text
	 * @param style
	 * @return
	 */
	public static Label initLabel(String text, String style) {
		Label l = new Label(text);
		if (style != null) {
			l.setStylePrimaryName(style);
		} else {
			l.setStyleName(DEFAULT_INPUT_LABEL_CSS);
		}
		return l;
	}
	
	/**
	 * constructs a new label using the default style
	 * @param text
	 * @return
	 */
	public static Label initLabel(String text){
		return initLabel(text,null);
	}

	/**
	 * installs a field with a Label containing text as the field label
	 * 
	 * @param container
	 * @param text
	 * @param field
	 * @param labelStyle
	 */
	public static void installFieldRow(HasWidgets container, String text,
			Widget field, String labelStyle) {
		HorizontalPanel hp = new HorizontalPanel();
		Label l = new Label(text);
		if (labelStyle != null) {
			l.setStylePrimaryName(labelStyle);
		}
		hp.add(l);
		hp.add(field);
		container.add(hp);
	}

	/**
	 * helper method for installing a row in a grid that consists of a label and
	 * a widget
	 * 
	 * @param labelText
	 * @param widget
	 * @param parent
	 * @param row
	 */
	public static void installGridRow(String labelText, Widget widget,
			Grid parent, int row) {
		installGridRow(labelText, widget, parent, row, 0,
				DEFAULT_INPUT_LABEL_CSS);
	}

	/**
	 * helper method for installing a row in a grid consisting of a label and a
	 * widget. This version will insert the widgets (label and widget) starting
	 * at colOffset rather than at position 0 within the row
	 * 
	 * @param labelText
	 * @param widget
	 * @param parent
	 * @param row
	 * @param colOffset
	 */
	public static void installGridRow(String labelText, Widget widget,
			Grid parent, int row, int colOffset, String style) {
		if (labelText != null) {
			Label label = new Label();
			if (style != null) {
				label.setStylePrimaryName(style);
			}
			label.setText(labelText);
			parent.setWidget(row, colOffset, label);
			parent.setWidget(row, colOffset + 1, widget);

		} else {
			parent.setWidget(row, colOffset, widget);
		}
	}

	/**
	 * selects the value in a list box
	 */
	public static boolean setListboxSelection(ListBox list, String value) {
		if (list != null && value != null) {
			for (int i = 0; i < list.getItemCount(); i++) {
				if (list.getValue(i).equals(value)) {
					list.setSelectedIndex(i);
					return true;
				}
			}
		}
		return false;
	}
}
