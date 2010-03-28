package org.waterforpeople.mapping.portal.client.widgets;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
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

	public SummaryPortlet() {
		super("System Summary", true, false, WIDTH, HEIGHT);
		setContent(constructTree());
	}

	private Tree constructTree() {
		// TODO: get items from DB
		Tree t = new Tree();
		TreeItem surveyItem = t.addItem("Surveys");
		surveyItem.addItem("Test Survey");
		surveyItem.addItem("Waterpoint Survey");
		TreeItem userItem = t.addItem("Users");
		userItem.addItem("Chris");
		userItem.addItem("Dru");
		TreeItem deviceItem = t.addItem("Devices");
		deviceItem.addItem("9175667663");
		deviceItem.addItem("3033359240");

		return t;

	}

	@Override
	public void handleEvent(PortletEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean getReadyForRemove() {

		return true;
	}

	@Override
	protected void handleConfigClick() {

	}

}
