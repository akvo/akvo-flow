package com.gallatinsystems.framework.gwt.wizard.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gallatinsystems.framework.gwt.component.Breadcrumb;
import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractWizardPortlet extends Portlet implements
		ClickHandler, PageController {

	private static final String NAV_BUTTON_STYLE = "wizard-navbutton";
	private static final String BACK_NAV_BUTTON_STYLE = "wizard-back-navbutton";
	private static final String FWD_NAV_BUTTON_STYLE = "wizard-fwd-navbutton";

	private VerticalPanel contentPane;

	private HorizontalPanel breadcrumbPanel;
	private HorizontalPanel buttonPanel;
	private VerticalPanel widgetPanel;
	private List<Button> forwardNavButtons;
	private List<Button> backwardNavButtons;

	private ArrayList<String> breadcrumbList;
	private Map<String, Widget> breadcrumbWidgets;

	private WizardWorkflow workflow;
	private Widget currentPage;
	private Breadcrumb currentBreadcrumb;

	protected AbstractWizardPortlet(String name, int width, int height) {
		super(name, true, false, false, width, height);
		contentPane = new VerticalPanel();
		breadcrumbPanel = new HorizontalPanel();
		buttonPanel = new HorizontalPanel();
		widgetPanel = new VerticalPanel();

		contentPane.add(breadcrumbPanel);
		widgetPanel.setBorderWidth(1);
		contentPane.add(widgetPanel);
		contentPane.add(buttonPanel);

		forwardNavButtons = new ArrayList<Button>();
		backwardNavButtons = new ArrayList<Button>();

		breadcrumbList = new ArrayList<String>();
		breadcrumbWidgets = new HashMap<String, Widget>();
		workflow = getWizardWorkflow();

		renderWizardPage(workflow.getStartNode(), true,null);
		setContent(contentPane);

	}

	protected void resetNav(WizardNode node) {
		buttonPanel.clear();
		forwardNavButtons.clear();
		backwardNavButtons.clear();
		installButtons(backwardNavButtons, node.getPrevNodes(),
				BACK_NAV_BUTTON_STYLE);
		installButtons(forwardNavButtons, node.getNextNodes(),
				FWD_NAV_BUTTON_STYLE);

	}

	private void installButtons(List<Button> buttonList, String[] buttonNames,
			String style) {
		if (buttonNames != null) {
			for (int i = 0; i < buttonNames.length; i++) {
				Button button = new Button();
				if (style == null) {
					button.setStylePrimaryName(NAV_BUTTON_STYLE);
				} else {
					button.setStylePrimaryName(style);
				}
				button.setText(buttonNames[i]);
				button.addClickHandler(this);
				buttonList.add(button);
				buttonPanel.add(button);
			}
		}
	}

	protected Widget renderWizardPage(WizardNode page, boolean isForward, Map<String,Object> bundle) {

		prePageUnload(page);
		widgetPanel.clear();
		if(isForward && currentPage instanceof ContextAware){
			ContextAware oldPage = (ContextAware)currentPage;
			oldPage.persistContext();
			if(currentBreadcrumb != null ){
				currentBreadcrumb.setBundle(oldPage.getContextBundle());
			}
		}
		currentPage = initializeNode(page);
		if(bundle != null && currentPage instanceof ContextAware){
			((ContextAware) currentPage).setContextBundle(bundle);
		}
		if (isForward && page.getBreadcrumb() != null) {
			if (currentPage instanceof ContextAware) {
				currentBreadcrumb = addBreadcrumb(page, ((ContextAware) currentPage).getContextBundle());
			} else {
				currentBreadcrumb = addBreadcrumb(page, null);
			}
		} else if (!isForward && page != null && page.getBreadcrumb() != null) {
			removeBreadcrumb(page);
		}
		widgetPanel.add(currentPage);
		resetNav(page);
		onLoadComplete(page);
		return currentPage;
	}

	protected Breadcrumb addBreadcrumb(WizardNode node, Map<String, Object> bundle) {
		Breadcrumb bc = new Breadcrumb(node.getBreadcrumb(), node.getName(),
				bundle);
		if (!breadcrumbList.contains(node.getBreadcrumb())) {
			bc.addClickHandler(this);
			breadcrumbList.add(node.getBreadcrumb());
			breadcrumbWidgets.put(node.getBreadcrumb(), bc);
			breadcrumbPanel.add(bc);
			return bc;
		}
		return null;

	}

	protected void removeBreadcrumb(WizardNode node) {
		int index = breadcrumbList.indexOf(node.getBreadcrumb());
		if (index >= 0) {
			List<String> crumbsToNix = new ArrayList<String>();
			for (int i = index + 1; i < breadcrumbList.size(); i++) {
				crumbsToNix.add(breadcrumbList.get(i));
				Widget w = breadcrumbWidgets.remove(breadcrumbList.get(i));
				if (w != null) {
					breadcrumbPanel.remove(w);
				}
			}
			breadcrumbList.removeAll(crumbsToNix);
		}

	}

	public void onClick(ClickEvent event) {
		if (forwardNavButtons.contains(event.getSource())) {
			renderWizardPage(workflow.getWorkflowNode(((Button) event
					.getSource()).getText()), true,null);
		} else if (backwardNavButtons.contains(event.getSource())) {
			renderWizardPage(workflow.getWorkflowNode(((Button) event
					.getSource()).getText()), false,null);
		} else if (event.getSource() instanceof Breadcrumb) {
			// if it is a breadcrumb
			renderWizardPage(workflow.getWorkflowNode(((Breadcrumb) event
					.getSource()).getTargetNode()), false,((Breadcrumb) event
							.getSource()).getBundle());
		}
	}

	public void openPage(Class clazz, Map<String, Object> bundle) {
		if (clazz != null) {
			WizardNode node = workflow.findNode(clazz);
			if (node != null) {
				Widget w = renderWizardPage(node, true,bundle);
			}
		}
	}

	protected abstract WizardWorkflow getWizardWorkflow();

	protected abstract Widget initializeNode(WizardNode node);

	protected abstract void onLoadComplete(WizardNode node);

	protected abstract void prePageUnload(WizardNode nextNode);

	public class WizardWorkflow {
		private WizardNode startNode;
		private Map<String, WizardNode> allNodes;

		public WizardWorkflow() {
			allNodes = new HashMap<String, WizardNode>();
		}

		public void setStartNode(WizardNode n) {
			startNode = n;
			addInternalNode(n);
		}

		public WizardNode getStartNode() {
			return startNode;
		}

		public void addInternalNode(WizardNode n) {
			allNodes.put(n.getName(), n);
		}

		public WizardNode getWorkflowNode(String name) {
			return allNodes.get(name);
		}

		public WizardNode findNode(Class className) {
			if (allNodes != null) {
				for (WizardNode n : allNodes.values()) {
					if (n.getWidgetClass().equals(className)) {
						return n;
					}
				}
			}
			return null;
		}
	}

	public class WizardNode {
		private Class widgetClass;
		private String name;
		private String breadcrumb;
		private String[] nextNodes;
		private String[] prevNodes;

		public WizardNode(String name, String breadcrumb, Class clazz,
				String next, String prev) {
			this.name = name;
			widgetClass = clazz;
			if (next != null) {
				nextNodes = new String[1];
				nextNodes[0] = next;
			} else {
				nextNodes = new String[0];
			}
			if (prev != null) {
				prevNodes = new String[1];
				prevNodes[0] = prev;
			} else {
				prevNodes = new String[0];
			}
			this.breadcrumb = breadcrumb;
		}

		public WizardNode(String name, String breadcrumb, Class clazz,
				String[] next, String[] prev) {
			this.name = name;
			this.breadcrumb = breadcrumb;
			widgetClass = clazz;
			nextNodes = next;
			prevNodes = prev;
		}

		public String getName() {
			return name;
		}

		public Class getWidgetClass() {
			return widgetClass;
		}

		public String[] getNextNodes() {
			return nextNodes;
		}

		public String[] getPrevNodes() {
			return prevNodes;
		}

		public String getBreadcrumb() {
			return breadcrumb;
		}
	}

}
