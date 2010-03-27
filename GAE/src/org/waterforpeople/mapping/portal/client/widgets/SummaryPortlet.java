package org.waterforpeople.mapping.portal.client.widgets;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
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
	private static final int HEIGHT= 300;
	
	public SummaryPortlet() {
		super("System Summary", false,WIDTH,HEIGHT);	
		setContent(constructTree());
	}

	private Tree constructTree() {
		// TODO: get items from DB
		TreeItem root = new TreeItem("Summary");
		TreeItem surveyItem = root.addItem("Surveys");
		surveyItem.addItem("Test Survey");
		surveyItem.addItem("Waterpoint Survey");
		TreeItem userItem = root.addItem("Users");
		userItem.addItem("Chris");
		userItem.addItem("Dru");
		TreeItem deviceItem = root.addItem("Devices");
		deviceItem.addItem("9175667663");
		deviceItem.addItem("3033359240");

		Tree t = new Tree();
		t.addItem(root);

		return t;
	}

}
