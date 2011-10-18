package org.waterforpeople.mapping.app.gwt.client.survey.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.dto.client.NamedObject;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.portlet.client.TreeDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
public class SurveyTree implements OpenHandler<TreeItem>,
		SelectionHandler<TreeItem> {

	private static final String DUMMY = "DUMMY";
	private static final String PLEASE_WAIT = "Loading...";
	private static final String PUBLISHED_STATUS = "PUBLISHED";
	private static final int MAX_Q_LENGTH = 20;
	private TreeItem surveyRootItem;
	private Tree surveyRootTree;
	private boolean rootedByItem;
	private SurveyServiceAsync surveyService;
	private boolean loadSurveyDetails;
	private TreeDragController dragController;
	private HashMap<Widget, BaseDto> surveyMap;
	private HashMap<SurveyGroupDto, ArrayList<SurveyDto>> surveys;
	private HashMap<Long, SurveyDto> unreleasedSurveys;
	private ArrayList<SurveyTreeListener> listeners;
	private TreeItem currentlySelectedItem;

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
		listeners = new ArrayList<SurveyTreeListener>();
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
			surveyRootItem.getTree().addSelectionHandler(this);
		} else {
			surveyRootTree.addOpenHandler(this);
			surveyRootTree.addSelectionHandler(this);
		}
		surveyService.listSurveyGroups("all", false, false, false,
				new AsyncCallback<ResponseDto<ArrayList<SurveyGroupDto>>>() {

					@Override
					public void onFailure(Throwable caught) {
						// no-op
					}

					@Override
					public void onSuccess(ResponseDto<ArrayList<SurveyGroupDto>> response) {
						ArrayList<SurveyGroupDto> result = response.getPayload();
						if (result != null) {
							for (int i = 0; i < result.size(); i++) {
								surveys.put(result.get(i), null);
								TreeItem tItem = new TreeItem(result.get(i)
										.getCode());
								tItem.setUserObject(result.get(i));
								addDummy(tItem);
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

	public void removeItem(TreeItem item) {
		if (item != null) {
			item.remove();
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
		loadChild((TreeItem) event.getTarget());
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
				addDummy(groupItem);
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
		TreeItem surveyItem = new TreeItem(new Label(survey.getDisplayName()));
		surveyItem.setUserObject(survey);
		if (loadSurveyDetails) {
			addDummy(surveyItem);
		}
		surveyMap.put(surveyItem.getWidget(), survey);
		if (dragController != null) {
			dragController.makeDraggable(surveyItem.getWidget());
		}
		parent.addItem(surveyItem);
	}

	/**
	 * reacts to clicks of tree items and loads the children.
	 */
	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		event.getSelectedItem().setSelected(true);	
		setCurrentlySelectedItem(event.getSelectedItem());
		loadChild(event.getSelectedItem());
		if (event.getSelectedItem().getUserObject() != null
				&& event.getSelectedItem().getUserObject() instanceof BaseDto) {
			// if the object in question is a question, we need special logic to
			// prevent it being displayed if it's not fully loaded
			if (event.getSelectedItem().getUserObject() instanceof QuestionDto) {
				QuestionDto q = (QuestionDto) event.getSelectedItem()
						.getUserObject();
				if ((QuestionDto.QuestionType.OPTION == q.getType() || QuestionDto.QuestionType.STRENGTH == q
						.getType())
						&& q.getOptionContainerDto() != null) {
					notifyListeners(q);
				} else if (QuestionDto.QuestionType.OPTION != q.getType()) {
					notifyListeners(q);
				}
			} else {
				notifyListeners((BaseDto) event.getSelectedItem()
						.getUserObject());
			}
		}
	}

	/**
	 * loads the children of the tree item passed in if it has not yet been
	 * loaded.
	 * 
	 * @param item
	 */
	private void loadChild(final TreeItem item) {
		if (item != null && !isLoaded(item)) {
			if (item.getUserObject() instanceof SurveyGroupDto) {
				SurveyGroupDto sg = (SurveyGroupDto) item.getUserObject();
				// Set up the callback object.
				AsyncCallback<ArrayList<SurveyDto>> surveyCallback = new AsyncCallback<ArrayList<SurveyDto>>() {
					public void onFailure(Throwable caught) {
						// no-op
					}

					public void onSuccess(ArrayList<SurveyDto> result) {
						surveys.put((SurveyGroupDto) item.getUserObject(),
								result);
						removeDummy(item);
						if (result != null) {
							for (int i = 0; i < result.size(); i++) {
								addSurveyToTree(item, result.get(i));
								if (!PUBLISHED_STATUS.equalsIgnoreCase(result
										.get(i).getStatus())) {
									unreleasedSurveys.put(result.get(i)
											.getKeyId(), result.get(i));
								}
							}
						}
					}
				};
				surveyService.listSurveysByGroup(sg.getKeyId().toString(),
						surveyCallback);

			} else if (loadSurveyDetails
					&& item.getUserObject() instanceof SurveyDto) {
				surveyService.listQuestionGroupsBySurvey(((SurveyDto) (item
						.getUserObject())).getKeyId().toString(),
						new AsyncCallback<ArrayList<QuestionGroupDto>>() {

							@Override
							public void onFailure(Throwable caught) {
								// no-op
							}

							@Override
							public void onSuccess(
									ArrayList<QuestionGroupDto> result) {
								removeDummy(item);
								if (result != null) {
									SurveyDto surveyItem = (SurveyDto) item
											.getUserObject();
									for (int i = 0; i < result.size(); i++) {
										surveyItem.addQuestionGroup(result
												.get(i));
										TreeItem qGroup = new TreeItem();
										String text = result.get(i).getCode();
										if (text != null
												&& text.trim().length() > MAX_Q_LENGTH) {
											text = text.substring(0,
													MAX_Q_LENGTH);
										}
										
										qGroup.setText(text);
										qGroup.setUserObject(result.get(i));
										item.addItem(qGroup);
										addDummy(qGroup);
									}
								}
							}

						});
			} else if (loadSurveyDetails
					&& item.getUserObject() instanceof QuestionGroupDto) {
				surveyService.listQuestionsByQuestionGroup(
						((QuestionGroupDto) (item.getUserObject())).getKeyId()
								.toString(), false,false,
						new AsyncCallback<ArrayList<QuestionDto>>() {

							@Override
							public void onFailure(Throwable caught) {
								// no-op
							}

							@Override
							public void onSuccess(ArrayList<QuestionDto> result) {
								removeDummy(item);
								if (result != null) {
									QuestionGroupDto qGroupItem = (QuestionGroupDto) item
											.getUserObject();
									for (int i = 0; i < result.size(); i++) {
										qGroupItem
												.addQuestion(result.get(i), i);
										TreeItem qGroup = new TreeItem();
										String text = result.get(i).getText();
										if (text != null
												&& text.trim().length() > MAX_Q_LENGTH) {
											text = text.substring(0,
													MAX_Q_LENGTH);
										}
										qGroup.setText((i + 1) + ":" + text);
										qGroup.setUserObject(result.get(i));
										item.addItem(qGroup);
									}
								}
							}
						});
			}
		} else if (item != null && item.getUserObject() != null
				&& item.getUserObject() instanceof QuestionDto) {
			QuestionDto dto = (QuestionDto) item.getUserObject();
			if (dto.getTranslationMap() == null
					|| (QuestionDto.QuestionType.OPTION == dto.getType() || QuestionDto.QuestionType.STRENGTH == dto
							.getType()) && dto.getOptionContainerDto() == null) {
				surveyService.loadQuestionDetails(dto.getKeyId(),
						new AsyncCallback<QuestionDto>() {
							@Override
							public void onFailure(Throwable caught) {
								// no-op
							}

							@Override
							public void onSuccess(QuestionDto result) {
								item.setUserObject(result);
								notifyListeners(result);
							}
						});

			}
		}
	}

	/**
	 * returns true if the tree item has already had it's immediate children
	 * loaded
	 * 
	 * @param item
	 * @return
	 */
	private boolean isLoaded(TreeItem item) {
		if (item.getChildCount() == 1
				&& item.getChild(0).getUserObject() != null
				&& item.getChild(0).getUserObject().equals(DUMMY)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * checks if the first child of the item is the DUMMY widget and, if so,
	 * removes it from the tree
	 * 
	 * @param item
	 */
	private void removeDummy(TreeItem item) {
		if (item.getChildCount() > 0
				&& item.getChild(0).getUserObject() != null
				&& item.getChild(0).getUserObject().equals(DUMMY)) {
			item.removeItem(item.getChild(0));
		}
	}

	/**
	 * adds a dummy item so we get the "PLUS" on the group tree item
	 * 
	 * @param parent
	 */
	private void addDummy(TreeItem parent) {
		TreeItem dummyItem = new TreeItem(PLEASE_WAIT);
		dummyItem.setUserObject(DUMMY);
		parent.addItem(dummyItem);
	}

	/**
	 * adds a listener to this class that will be notified each time the user
	 * selects a tree item
	 * 
	 * @param l
	 */
	public void addSurveyListener(SurveyTreeListener l) {
		listeners.add(l);
	}

	/**
	 * notifies all listeners with the selected object
	 * 
	 * @param userObject
	 */
	protected void notifyListeners(BaseDto userObject) {
		if (userObject != null && listeners != null) {
			for (SurveyTreeListener l : listeners) {
				l.onSurveyTreeSelection(userObject);
			}
		}
	}

	/**
	 * adds a child of the object passed in to the tree as a child of the node
	 * that has the parentUserObject and sets the user object for the new node
	 * 
	 * @param parentUserObject
	 * @param child
	 */
	public TreeItem addChild(BaseDto parentUserObject, NamedObject child) {
		TreeItem addedItem = null;
		if (parentUserObject != null) {
			TreeItem parentItem = findItemByUserObject(parentUserObject);
			if (parentItem != null) {
				String text = child.getDisplayName();
				if (text != null
						&& text.trim().length() > MAX_Q_LENGTH) {
					text = text.substring(0,
							MAX_Q_LENGTH);
				}
				addedItem = new TreeItem(text);
				addedItem.setUserObject(child);
				parentItem.addItem(addedItem);
			}
		} else {
			String text = child.getDisplayName();
			if (text != null
					&& text.trim().length() > MAX_Q_LENGTH) {
				text = text.substring(0,
						MAX_Q_LENGTH);
			}
			addedItem = new TreeItem(text);
			addedItem.setUserObject(child);
			surveyRootTree.addItem(addedItem);
		}
		return addedItem;
	}

	/**
	 * finds the tree node (if any) that has the user object passed in and
	 * replaces it with the new one
	 * 
	 * @param userObject
	 */
	public void replaceUserObject(BaseDto userObject, BaseDto newObject) {
		if (userObject != null) {
			TreeItem treeItem = findItemByUserObject(userObject);
			if (treeItem != null) {
				treeItem.setUserObject(newObject);
			}
		}
	}
	

	/**
	 * returns the parent user object (if any). If not found, returns null
	 * 
	 * @param userObject
	 * @return
	 */
	public BaseDto getParentUserObject(BaseDto userObject) {
		TreeItem item = findItemByUserObject(userObject);
		if (item.getParentItem() != null
				&& item.getParentItem().getUserObject() != null) {
			if (item.getParentItem().getUserObject() instanceof BaseDto) {
				return (BaseDto) item.getParentItem().getUserObject();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * does a depth-first search of the tree until all nodes are searched or the
	 * node containing userObject is found. If found, it returns the treeItem
	 * bound to the userObject
	 * 
	 * @param userObject
	 * @return
	 */
	protected TreeItem findItemByUserObject(BaseDto userObject) {
		TreeItem result = null;
		if (rootedByItem) {
			result = findItemByUserObject(surveyRootItem, userObject);
		} else {
			for (int i = 0; i < surveyRootTree.getItemCount(); i++) {
				result = findItemByUserObject(surveyRootTree.getItem(i),
						userObject);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	/**
	 * recursively searches the tree until all nodes are exhausted or the object
	 * is found
	 * 
	 * @param curItem
	 * @param userObject
	 * @return
	 */
	protected TreeItem findItemByUserObject(TreeItem curItem, BaseDto userObject) {
		TreeItem result = null;
		if (curItem.getUserObject() != null
				&& curItem.getUserObject().equals(userObject)) {
			result = curItem;
		} else {
			for (int i = 0; i < curItem.getChildCount(); i++) {
				result = findItemByUserObject(curItem.getChild(i), userObject);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	public void setCurrentlySelectedItem(TreeItem currentlySelectedItem) {
		this.currentlySelectedItem = currentlySelectedItem;
	}

	public TreeItem getCurrentlySelectedItem() {
		return currentlySelectedItem;
	}
}
