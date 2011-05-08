package com.gallatinsystems.framework.gwt.wizard.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gallatinsystems.framework.gwt.component.Breadcrumb;
import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.WizardBundleConstants;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.FrameworkTextConstants;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget that can act as a flow controller for a wizard-like interface. It
 * handles rendering a main portlet, breadcrumbs and navigational buttons.
 * 
 * 
 * The wizard is configured by subclassing this class and creating a
 * WizardWorkflow object in the getWizardWorkflow method. The wizardWorkflow is
 * a DSL that defines the series of steps available from each node in the
 * wizard.
 * 
 * As the user clicks buttons in the wizard, this class will call a method to
 * flush the state of the loaded wizard node (if it implements ContextualAware)
 * and then will load the next node. State context will also be associated with
 * breadcrumbs so the correct page can be loaded on click.
 * 
 * 
 * 
 * This component uses the following CSS styles: wizard-back-navbutton - for
 * "backwards" flow control buttons wizard-fwd-navbutton - for "forward" flow
 * control buttons wizard-navbutton - default button look and feel
 * 
 * @author Christopher Fagiani
 */
@SuppressWarnings("unchecked")
public abstract class AbstractWizardPortlet extends Portlet implements
		ClickHandler, PageController, CompletionListener {

	private static FrameworkTextConstants TEXT_CONSTANTS = GWT
			.create(FrameworkTextConstants.class);
	private static final String NAV_BUTTON_STYLE = "wizard-navbutton";
	private static final String BACK_NAV_BUTTON_STYLE = "wizard-back-navbutton";
	private static final String FWD_NAV_BUTTON_STYLE = "wizard-fwd-navbutton";
	private static final String BREADCRUMB_PANEL_STYLE = "wizard-breadcrumb-panel";

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
	private ContextAware pendingPage;
	private WizardNode pageToLoad;
	private MessageDialog waitDialog;
	private Map<String, String> buttonMapping;
	private boolean working;

	protected AbstractWizardPortlet(String name, int width, int height) {
		super(name, true, false, false, width, height);
		working = false;
		contentPane = new VerticalPanel();
		breadcrumbPanel = new HorizontalPanel();
		breadcrumbPanel.setStylePrimaryName(BREADCRUMB_PANEL_STYLE);
		buttonPanel = new HorizontalPanel();
		widgetPanel = new VerticalPanel();
		buttonMapping = new HashMap<String, String>();

		contentPane.add(breadcrumbPanel);
		contentPane.add(widgetPanel);
		contentPane.add(buttonPanel);

		forwardNavButtons = new ArrayList<Button>();
		backwardNavButtons = new ArrayList<Button>();

		breadcrumbList = new ArrayList<String>();
		breadcrumbWidgets = new HashMap<String, Widget>();
		workflow = getWizardWorkflow();
	}

	protected void init() {
		pageToLoad = workflow.getStartNode();
		renderWizardPage(pageToLoad, true, null);
		setContent(contentPane);
		waitDialog = new MessageDialog(TEXT_CONSTANTS.saving(), TEXT_CONSTANTS
				.pleaseWait(), true);
	}

	/**
	 * Clears current buttons and replaces them with the buttons dictated by the
	 * WizardNode passed in
	 * 
	 */
	protected void resetNav(WizardNode node) {
		buttonPanel.clear();
		forwardNavButtons.clear();
		backwardNavButtons.clear();
		buttonMapping.clear();
		installButtons(backwardNavButtons, node.getPrevNodes(),
				BACK_NAV_BUTTON_STYLE);
		installButtons(forwardNavButtons, node.getNextNodes(),
				FWD_NAV_BUTTON_STYLE);

	}

	/**
	 * Adds the buttons passed in to the button panel and attaches their
	 * listeners
	 * 
	 */
	private void installButtons(List<Button> buttonList,
			WizardButton[] buttonDefinitions, String style) {
		if (buttonDefinitions != null) {
			for (int i = 0; i < buttonDefinitions.length; i++) {
				Button button = new Button();
				if (style == null) {
					button.setStylePrimaryName(NAV_BUTTON_STYLE);
				} else {
					button.setStylePrimaryName(style);
				}
				button.setText(buttonDefinitions[i].getLabel());
				button.addClickHandler(this);
				buttonList.add(button);
				buttonPanel.add(button);
				buttonMapping.put(buttonDefinitions[i].getLabel(),
						buttonDefinitions[i].getNodeName());
			}
		}
	}

	/*
	 * This method handles the majority of the page loading logic. It will do
	 * the following: Calls the prePageUnload method Clear the current widget If
	 * the page is "forward" (i.e. not a click of a breadcrumb or a back button)
	 * and the old page is ContextAware: calls persistContext on the old page.
	 * Since the save is async, the remainder of initialization is performed in
	 * the operationComplete callback Initializes the widget for the new page If
	 * the page is "forward" (i.e. not a click of a breadcrumb or a back button)
	 * install the new breadcrumb for the new page (if its WizardNode object
	 * contains a breadcrumb name) Add the new page to the display Call the
	 * onLoadComplete hook
	 */
	protected void renderWizardPage(WizardNode page, boolean isForward,
			Map<String, Object> bundle) {
		boolean calledSave = false;
		prePageUnload(page);
		widgetPanel.clear();
		pageToLoad = page;
		if (isForward && currentPage instanceof ContextAware) {
			pendingPage = (ContextAware) currentPage;
			// need to update current page first since we don't know when the
			// callback to operationComplete will occur and currentPage needs to
			// point to the new page at that point
			currentPage = initializeNode(page);
			if (!(pendingPage instanceof AutoAdvancing)) {
				Window.scrollTo(0, 0);
				waitDialog.showCentered();
				pendingPage.persistContext(this);
				calledSave = true;
			}
		}
		if (!isForward && currentPage instanceof ContextAware) {
			bundle = ((ContextAware) currentPage).getContextBundle(isForward);
			((ContextAware) currentPage).flushContext();

		}
		if (isForward && page.getBreadcrumb() != null) {
			if (currentPage instanceof ContextAware) {
				addBreadcrumb(page, ((ContextAware) currentPage)
						.getContextBundle(isForward));
			} else {
				addBreadcrumb(page, null);
			}
		} else if (!isForward && page != null && page.getBreadcrumb() != null) {
			removeBreadcrumb(page);
		}
		if (!calledSave) {
			Window.scrollTo(0, 0);
			currentPage = initializeNode(page);
			// since there is nothing being saved, we can populate the bundle
			// immediately (in the case of save being called, this happens in
			// the callback)
			populateBundle(bundle);
			widgetPanel.add(currentPage);
			resetNav(page);
			onLoadComplete(page);
			if (currentPage instanceof AutoAdvancing) {
				((AutoAdvancing) currentPage).advance(this);
			}
		}
	}

	/**
	 *Populates the bundle in the current page
	 * 
	 */
	private void populateBundle(Map<String, Object> bundle) {
		if (currentPage instanceof ContextAware) {
			if (bundle == null) {
				bundle = new HashMap<String, Object>();
			}
			((ContextAware) currentPage).setContextBundle(bundle);
		}
	}

	/**
	 * Callback received when persistContext is completed
	 * 
	 */
	public void operationComplete(boolean isSuccessful,
			Map<String, Object> bundle) {
		waitDialog.hide();
		if (isSuccessful) {
			widgetPanel.add(currentPage);
			if (pendingPage != null
					&& currentPage.getClass() == pendingPage.getClass()) {
				if (pendingPage instanceof ContextAware) {
					((ContextAware) pendingPage).flushContext();
					bundle = ((ContextAware) pendingPage)
							.getContextBundle(false);					
				}
				populateBundle(bundle);
			} else {
				populateBundle(bundle);
			}
			if (bundle.get(WizardBundleConstants.AUTO_ADVANCE_FLAG) != null) {
				bundle.remove(WizardBundleConstants.AUTO_ADVANCE_FLAG);
				renderWizardPage(workflow.getWorkflowNode(pageToLoad
						.getNextNodes()[0].getNodeName()), true, bundle);
			} else {
				if (currentPage instanceof AutoAdvancing) {
					((AutoAdvancing) currentPage).advance(this);
				} else {
					resetNav(pageToLoad);
				}
			}
			onLoadComplete(pageToLoad);
		} else {
			widgetPanel.clear();
			currentPage = (Widget) pendingPage;
			widgetPanel.add(currentPage);
		}
	}

	/**
	 * Adds a breadcrumb to the UI and installs click listeners
	 * 
	 */
	protected Breadcrumb addBreadcrumb(WizardNode node,
			Map<String, Object> bundle) {
		Breadcrumb bc = new Breadcrumb(node.getBreadcrumb(), node.getName(),
				bundle);
		if (!breadcrumbList.contains(node.getBreadcrumb())) {
			bc.addClickHandler(this);
			breadcrumbList.add(node.getBreadcrumb());
			breadcrumbWidgets.put(node.getBreadcrumb(), bc);
			breadcrumbPanel.add(bc);
			return bc;
		} else {
			// if the breadcrumb is already there, we actually should remove the
			// last breadcrumb since it is a case of a "forward" page that is
			// actually going back in the page flow
			if (breadcrumbList.size() > 0) {
				removeBreadcrumb(breadcrumbList.size() - 1);
			}
		}
		return null;

	}

	/**
	 * removes breadcrumb from the UI
	 * 
	 */
	protected void removeBreadcrumb(WizardNode node) {
		removeBreadcrumb(breadcrumbList.indexOf(node.getBreadcrumb()));
	}

	/**
	 * removes the breadcrumb at the index passed in as well as all the crumbs
	 * that follow it
	 * 
	 * @param index
	 */
	private void removeBreadcrumb(int index) {
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

	/**
	 * Handles clicks of navigational buttons and breadcrumbs
	 * 
	 */
	public void onClick(ClickEvent event) {
		if (!working) {
			if (forwardNavButtons.contains(event.getSource())) {
				renderWizardPage(workflow.getWorkflowNode(buttonMapping
						.get(((Button) event.getSource()).getText())), true,
						null);
			} else if (backwardNavButtons.contains(event.getSource())) {
				renderWizardPage(workflow.getWorkflowNode(buttonMapping
						.get(((Button) event.getSource()).getText())), false,
						null);
			} else if (event.getSource() instanceof Breadcrumb) {
				// if it is a breadcrumb
				renderWizardPage(workflow.getWorkflowNode(((Breadcrumb) event
						.getSource()).getTargetNode()), false,
						((Breadcrumb) event.getSource()).getBundle());
			}
		}
	}

	/**
	 * Opens a page. This can be called from wizard nodes to open a new page
	 * that doesn't directly correspond to a back/forward/breadcrumb click
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public void openPage(Class clazz, Map<String, Object> bundle) {
		if (!working) {
			if (clazz != null) {
				WizardNode node = workflow.findNode(clazz);
				if (node != null) {
					renderWizardPage(node, true, bundle);
				}
			}
		}
	}

	public void setWorking(boolean isWorking) {
		working = isWorking;
	}

	public boolean isWorking() {
		return working;
	}

	/**
	 * This method will return a populated WizardWorkflow object defining the
	 * workflow for a wizard
	 */
	protected abstract WizardWorkflow getWizardWorkflow();

	/**
	 * 
	 * called to instantiate a widget that corresponds to the wizardNode that is
	 * passed in
	 */
	protected abstract Widget initializeNode(WizardNode node);

	/**
	 * called when all loading is complete and the widget has been added to the
	 * display
	 */
	protected abstract void onLoadComplete(WizardNode node);

	/**
	 * called before a page is removed from the UI
	 */
	protected abstract void prePageUnload(WizardNode nextNode);

	/**
	 * Defines a workflow for a wizard. It must contain a start node
	 */
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

		@SuppressWarnings("rawtypes")
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

	/**
	 * Defines a node (page) within a wizard. Each object defines the node name,
	 * the class to be used for the widget, the breadcrumb name (null if no
	 * breadcrumb) and 2 arrays of node names corresponding to the "forard" and
	 * "backward" buttons.
	 * 
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public class WizardNode {
		private Class widgetClass;
		private String name;
		private String breadcrumb;
		private WizardButton[] nextNodes;
		private WizardButton[] prevNodes;

		public WizardNode(String name, String breadcrumb, Class clazz,
				WizardButton next, WizardButton prev) {
			this.name = name;
			widgetClass = clazz;
			if (next != null) {
				nextNodes = new WizardButton[1];
				nextNodes[0] = next;
			} else {
				nextNodes = new WizardButton[0];
			}
			if (prev != null) {
				prevNodes = new WizardButton[1];
				prevNodes[0] = prev;
			} else {
				prevNodes = new WizardButton[0];
			}
			this.breadcrumb = breadcrumb;
		}

		public WizardNode(String name, String breadcrumb, Class clazz,
				WizardButton[] next, WizardButton[] prev) {
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

		public WizardButton[] getNextNodes() {
			return nextNodes;
		}

		public WizardButton[] getPrevNodes() {
			return prevNodes;
		}

		public String getBreadcrumb() {
			return breadcrumb;
		}
	}

	public class WizardButton {
		private String nodeName;
		private String label;

		public WizardButton(String node) {
			nodeName = node;
			label = node;
		}

		public WizardButton(String node, String label) {
			nodeName = node;
			this.label = label;
		}

		public String getNodeName() {
			return nodeName;
		}

		public void setNodeName(String nodeName) {
			this.nodeName = nodeName;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

	}
}
