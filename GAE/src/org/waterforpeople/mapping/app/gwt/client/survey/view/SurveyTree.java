package org.waterforpeople.mapping.app.gwt.client.survey.view;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * reusable tree widget for loading and navigating surveys
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyTree extends Widget implements OpenHandler<TreeItem> {

	private static final String DUMMY = "DUMMY";
	private static final String PLEASE_WAIT = "Loading...";
	private TreeItem surveyRoot;
	private SurveyServiceAsync surveyService;
	private boolean loadSurveyDetails;

	public SurveyTree(TreeItem root, boolean loadDetails) {
		loadSurveyDetails = loadDetails;
		surveyService = GWT.create(SurveyService.class);
		surveyRoot = root;
		root.getTree().addOpenHandler(this);
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
								TreeItem tItem = new TreeItem(result.get(i)
										.getCode());
								tItem.setUserObject(result.get(i));
								// add a dummy item so we get the "PLUS" on the
								// group tree item
								TreeItem dummyItem = new TreeItem(PLEASE_WAIT);
								dummyItem.setUserObject(DUMMY);
								tItem.addItem(dummyItem);
								surveyRoot.addItem(tItem);

							}
						}

					}
				});
	}

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
							// remove the dummy
							if (item.getChild(0).getUserObject().equals(DUMMY)) {
								item.removeItem(item.getChild(0));
							}
							if (result != null) {
								for (int i = 0; i < result.size(); i++) {
									String name = result.get(i).getName();
									if (name == null
											|| name.trim().length() == 0) {
										name = result.get(i).getKeyId()
												.toString();
									}
									item.addItem(name + " - v."
											+ result.get(i).getVersion());
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

}
