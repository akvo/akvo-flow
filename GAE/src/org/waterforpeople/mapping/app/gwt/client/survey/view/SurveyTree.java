package org.waterforpeople.mapping.app.gwt.client.survey.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.portlet.client.TreeDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * reusable tree widget for loading and navigating surveys
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyTree implements OpenHandler<TreeItem> {

	private static final String DUMMY = "DUMMY";
	private static final String PLEASE_WAIT = "Loading...";
	private static final String PUBLISHED_STATUS = "PUBLISHED";
	private TreeItem surveyRootItem;
	private Tree surveyRootTree;
	private boolean rootedByItem;
	private SurveyServiceAsync surveyService;
	private boolean loadSurveyDetails;
	private TreeDragController dragController;
	private HashMap<Widget, BaseDto> surveyMap;
	private HashMap<SurveyGroupDto, ArrayList<SurveyDto>> surveys;
	private HashMap<Long, SurveyDto> unreleasedSurveys;

	/**
	 * constructs a survey tree rooted at the tree level (survey groups have no
	 * parent)
	 * 
	 * @param root
	 * @param dragController
	 * @param loadDetails
	 */
	public SurveyTree(Tree root, TreeDragController dragController,
			boolean loadDetails) {
		rootedByItem = false;
		surveyRootTree = root;
		initialize(dragController, loadDetails);
	}

	/**
	 * creates a survey tree rooted at an item already installed in a tree (all
	 * survey groups have a common parent)
	 * 
	 * @param root
	 * @param dragController
	 * @param loadDetails
	 */
	public SurveyTree(TreeItem root, TreeDragController dragController,
			boolean loadDetails) {
		rootedByItem = true;
		surveyRootItem = root;
		initialize(dragController, loadDetails);
	}

	/**
	 * constructs local members and adds handlers.
	 * 
	 * @param dragController
	 * @param loadDetails
	 */
	private void initialize(TreeDragController dragController,
			boolean loadDetails) {
		unreleasedSurveys = new HashMap<Long, SurveyDto>();
		loadSurveyDetails = loadDetails;
		surveyService = GWT.create(SurveyService.class);
		surveyMap = new HashMap<Widget, BaseDto>();
		surveys = new HashMap<SurveyGroupDto, ArrayList<SurveyDto>>();
		this.dragController = dragController;
		if (rootedByItem) {
			surveyRootItem.getTree().addOpenHandler(this);
		} else {
			surveyRootTree.addOpenHandler(this);
		}
		surveyService.listSurveyGroups("all", false, false, false,
				new AsyncCallback<ArrayList<SurveyGroupDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						// no-op

					}

					@Override
					public void onSuccess(ArrayList<SurveyGroupDto> result) {
						if (result != null) {
							for (int i = 0; i < result.size(); i++) {
								surveys.put(result.get(i), null);
								TreeItem tItem = new TreeItem(result.get(i)
										.getCode());
								tItem.setUserObject(result.get(i));
								// add a dummy item so we get the "PLUS" on the
								// group tree item
								TreeItem dummyItem = new TreeItem(PLEASE_WAIT);
								dummyItem.setUserObject(DUMMY);
								tItem.addItem(dummyItem);
								if (rootedByItem) {
									surveyRootItem.addItem(tItem);
								} else {
									surveyRootTree.addItem(tItem);
								}
							}
						}
					}
				});
	}

	/**
	 * removes the survey with the key passed in from the tree
	 * 
	 * @param surveyKeyId
	 */
	public void removeItem(Long surveyKeyId) {
		if (rootedByItem) {
			if (surveyKeyId != null && surveyRootItem != null) {
				for (int i = 0; i < surveyRootItem.getChildCount(); i++) {
					if (removeItem(surveyKeyId, surveyRootItem.getChild(i))) {
						break;
					}
				}
			}
		} else {
			if (surveyKeyId != null && surveyRootTree != null) {
				for (int i = 0; i < surveyRootTree.getItemCount(); i++) {
					if (removeItem(surveyKeyId, surveyRootTree.getItem(i))) {
						break;
					}
				}
			}
		}
	}

	/**
	 * recurses through the tree until the victim item is found or until all
	 * nodes are searched. If found, the victim is removed from the tree and
	 * execution terminates.
	 * 
	 * @param surveyKeyId
	 * @param treeItem
	 * @return
	 */
	private boolean removeItem(Long surveyKeyId, TreeItem treeItem) {
		SurveyDto dto = (SurveyDto) surveyMap.get(treeItem.getWidget());
		if (dto != null && dto.getKeyId().equals(surveyKeyId)) {
			treeItem.remove();
			return true;
		} else if (treeItem.getChildCount() > 0) {
			for (int i = 0; i < treeItem.getChildCount(); i++) {
				if (removeItem(surveyKeyId, treeItem.getChild(i))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * handles the open even for a tree node. If the node is a group node and it
	 * has not yet been loaded, this will call the server to get the surveys in
	 * that group. If the node has already been loaded, then the cached copy is
	 * used instead.
	 */
	@Override
	public void onOpen(OpenEvent<TreeItem> event) {
		if (event.getTarget() instanceof TreeItem) {
			final TreeItem item = (TreeItem) event.getTarget();
			if (item.getUserObject() instanceof SurveyGroupDto) {
				SurveyGroupDto sg = (SurveyGroupDto) item.getUserObject();
				// if we haven't yet loaded the surveys, load them
				if (item.getChildCount() == 1
						&& item.getChild(0).getUserObject().equals(DUMMY)) {
					// Set up the callback object.
					AsyncCallback<ArrayList<SurveyDto>> surveyCallback = new AsyncCallback<ArrayList<SurveyDto>>() {
						public void onFailure(Throwable caught) {
							// no-op
						}

						public void onSuccess(ArrayList<SurveyDto> result) {
							surveys.put((SurveyGroupDto) item.getUserObject(),
									result);
							// remove the dummy
							if (item.getChild(0).getUserObject().equals(DUMMY)) {
								item.removeItem(item.getChild(0));
							}
							if (result != null) {
								for (int i = 0; i < result.size(); i++) {
									addSurveyToTree(item, result.get(i));
									if (!PUBLISHED_STATUS
											.equalsIgnoreCase(result.get(i)
													.getStatus())) {
										unreleasedSurveys.put(result.get(i)
												.getKeyId(), result.get(i));
									}
								}
							}
						}
					};
					surveyService.listSurveysByGroup(sg.getKeyId().toString(),
							surveyCallback);
				}
			} else if (loadSurveyDetails
					&& item.getUserObject() instanceof SurveyDto) {
				// TODO - load question groups
			}
		}
	}

	/**
	 * returns true if the survey identified by the id passed in is released.
	 * 
	 * @param id
	 * @return
	 */
	public boolean isReleased(Long id) {
		if (unreleasedSurveys.get(id) != null) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * returns a map keyed on the tree widget labels values are the SurveyDto
	 * objects.
	 * 
	 * @return
	 */
	public HashMap<Widget, BaseDto> getItemMap() {
		return surveyMap;
	}

	/**
	 * resets the visual state of the tree by collapsing all nodes and re-adding
	 * any removed survey items.
	 */
	public void reset() {
		if (rootedByItem) {
			surveyRootItem.removeItems();
		} else {
			surveyRootTree.removeItems();
		}
		surveyMap.clear();
		for (Entry<SurveyGroupDto, ArrayList<SurveyDto>> groupEntry : surveys
				.entrySet()) {
			TreeItem groupItem = new TreeItem(groupEntry.getKey().getCode());
			groupItem.setUserObject(groupEntry.getKey());
			if (groupEntry.getValue() == null) {
				TreeItem dummyItem = new TreeItem(PLEASE_WAIT);
				dummyItem.setUserObject(DUMMY);
				groupItem.addItem(dummyItem);
			} else {
				for (SurveyDto survey : groupEntry.getValue()) {
					addSurveyToTree(groupItem, survey);
				}
			}
			if (rootedByItem) {
				surveyRootItem.addItem(groupItem);
			} else {
				surveyRootTree.addItem(groupItem);
			}
		}

	}

	/**
	 * adds an item to the tree and installs the drag controller (if non-null)
	 * 
	 * @param parent
	 * @param survey
	 */
	private void addSurveyToTree(TreeItem parent, SurveyDto survey) {
		TreeItem surveyItem = new TreeItem(new Label(getSurveyName(survey)));
		surveyMap.put(surveyItem.getWidget(), survey);
		if (dragController != null) {
			dragController.makeDraggable(surveyItem.getWidget());
		}
		parent.addItem(surveyItem);
	}

	/**
	 * forms the display name of a survey
	 * 
	 * @param survey
	 * @return
	 */
	private String getSurveyName(SurveyDto survey) {
		String name = survey.getName();
		if (name == null || name.trim().length() == 0) {
			name = survey.getKeyId().toString();
		}
		name = name + " - v." + survey.getVersion();
		return name;
	}
}
