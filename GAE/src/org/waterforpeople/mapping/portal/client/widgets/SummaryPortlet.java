package org.waterforpeople.mapping.portal.client.widgets;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Displays summary information
 * 
 * @author Christopher Fagiani
 * 
 */
public class SummaryPortlet extends Portlet {

	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	private static final String TREE_ITEM_HEIGHT = "25";
	private static final String USER_IMAGE = "images/users.png";
	private static final String SURVEY_IMAGE = "images/surveys.png";
	private static final String DEVICE_IMAGE = "images/device.png";

	public SummaryPortlet() {
		super("System Summary", true, false, WIDTH, HEIGHT);
		setContent(constructTree());
	}

	private Tree constructTree() {
		// TODO: get items from DB
		// TODO: get icons
		Tree t = new Tree();
		HorizontalPanel panel = new HorizontalPanel();
		panel.setHeight(TREE_ITEM_HEIGHT);
		panel.add(new Image(SURVEY_IMAGE));
		panel.add(new Label("Surveys"));
		TreeItem surveyItem = t.addItem(panel);
		surveyItem.addItem("Test Survey");
		surveyItem.addItem("Waterpoint Survey");

		panel = new HorizontalPanel();
		panel.setHeight(TREE_ITEM_HEIGHT);
		panel.add(new Image(USER_IMAGE));
		panel.add(new Label("Users"));
		TreeItem userItem = t.addItem(panel);
		userItem.addItem("Chris");
		userItem.addItem("Dru");

		panel = new HorizontalPanel();
		panel.setHeight(TREE_ITEM_HEIGHT);
		panel.add(new Image(DEVICE_IMAGE));
		panel.add(new Label("Devices"));
		TreeItem deviceItem = t.addItem(panel);
		deviceItem.addItem("9175667663");
		deviceItem.addItem("3033359240");

		return t;

	}

	@Override
	public void handleEvent(PortletEvent e) {
		// no-op
	}

	@Override
	protected boolean getReadyForRemove() {
		// no-op. nothing to do before remove
		return true;
	}

	@Override
	protected void handleConfigClick() {
		// no-op. this portlet does not support config
	}

}
