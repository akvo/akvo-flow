/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto.AccessPointType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAttributeMappingServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * portlet that can be used to map a survey question to a access point attribute
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyAttributeMappingPortlet extends Portlet implements
		ChangeHandler, ClickHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	public static final String NAME = TEXT_CONSTANTS
			.surveyAttributeMappingPortletTitle();
	public static final String DESCRIPTION = TEXT_CONSTANTS
			.surveyAttributeMappingPortletDescription();
	private static final String MAP_TARGET_OBJECT_NAME = "org.waterforpeople.mapping.domain.AccessPoint";
	private static final String EVEN_ROW_CSS = "gridCell-even";
	private static final String ODD_ROW_CSS = "gridCell-odd";
	private static final String SELECTED_ROW_CSS = "gridCell-selected";
	private static final String GRID_HEADER_CSS = "gridCell-header";
	private static final int HEIGHT = 800;
	private static final int WIDTH = 800;

	private ListBox surveyGroup;
	private ListBox surveyListbox;
	private ListBox questionGroupListbox;

	private HashMap<String, ListBox> attributeListboxes;
	private HashMap<String, ListBox> typeListboxes;

	private TreeMap<String, String> attributes;

	private DockPanel contentPanel;

	private SurveyServiceAsync surveyService;
	private SurveyAttributeMappingServiceAsync mappingService;

	private Button saveButton;
	private Button resetButton;
	private Label statusLabel;

	private DockPanel inputPanel;
	private VerticalPanel gridPanel;
	private ArrayList<SurveyDto> currentSurveyDtoList;
	private ArrayList<QuestionGroupDto> currentQuestionGroupDtoList;
	private ArrayList<QuestionDto> currentQuestionList;
	private QuestionGroupDto currentQuestionGroupSelection = null;
	private SurveyDto currentSurveySelection = null;

	private MessageDialog loadingDialog;
	private HashMap<String, ArrayList<SurveyDto>> surveys;

	public SurveyAttributeMappingPortlet() {
		super(NAME, true, false, WIDTH, HEIGHT);
		loadingDialog = new MessageDialog(TEXT_CONSTANTS.loading(), TEXT_CONSTANTS.pleaseWait(),true);
		surveys = new HashMap<String, ArrayList<SurveyDto>>();
		inputPanel = new DockPanel();
		contentPanel = new DockPanel();
		inputPanel = new DockPanel();
		statusLabel = new Label();
		statusLabel.setVisible(false);
		inputPanel.add(statusLabel, DockPanel.NORTH);
		inputPanel.add(createInputControls(), DockPanel.NORTH);

		mappingService = GWT.create(SurveyAttributeMappingService.class);
		surveyService = GWT.create(SurveyService.class);
		HorizontalPanel treeHost = new HorizontalPanel();

		inputPanel.add(treeHost, DockPanel.CENTER);

		resetButton = new Button(TEXT_CONSTANTS.clear());
		resetButton.addClickHandler(this);

		saveButton = new Button(TEXT_CONSTANTS.save());
		saveButton.addClickHandler(this);

		HorizontalPanel inputButtonPanel = new HorizontalPanel();
		inputButtonPanel.add(resetButton);
		inputButtonPanel.add(saveButton);
		inputPanel.add(inputButtonPanel, DockPanel.SOUTH);
		contentPanel.add(inputPanel, DockPanel.NORTH);
		gridPanel = new VerticalPanel();
		contentPanel.add(gridPanel, DockPanel.CENTER);
		setContent(contentPanel);

		loadAttributes();
		loadSurveyGroups();

	}

	private void loadAttributes() {
		mappingService.listObjectAttributes(MAP_TARGET_OBJECT_NAME,
				new AsyncCallback<TreeMap<String, String>>() {

					@Override
					public void onSuccess(TreeMap<String, String> result) {
						if (result != null) {
							attributes = result;
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
						errDia.showRelativeTo(saveButton);

					}
				});
	}

	/**
	 * loads the survey groups
	 */
	private void loadSurveyGroups() {
		loadingDialog.showCentered();
		surveyService.listSurveyGroups("all", false, false, false,
				new AsyncCallback<ResponseDto<ArrayList<SurveyGroupDto>>>() {

					@Override
					public void onFailure(Throwable caught) {
						loadingDialog.hide(true);
						MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
						errDia.showRelativeTo(saveButton);
					}

					@Override
					public void onSuccess(ResponseDto<ArrayList<SurveyGroupDto>> response) {
						ArrayList<SurveyGroupDto> result = response.getPayload();
						loadingDialog.hide(true);
						surveyGroup.addItem("", "");
						if (result != null) {
							for (SurveyGroupDto dto : result) {
								surveyGroup.addItem(dto.getCode(), dto
										.getKeyId().toString());
							}
						}
					}
				});
	}

	private void updateDataGrid(ArrayList<QuestionDto> questions) {
		gridPanel.clear();
		if (questions != null) {

			attributeListboxes = new HashMap<String, ListBox>();
			typeListboxes = new HashMap<String, ListBox>();

			Grid grid = new Grid(questions.size() + 1, 3);
			// build headers
			grid.setText(0, 0, TEXT_CONSTANTS.questionText());
			grid.setText(0, 1, TEXT_CONSTANTS.attribute());
			grid.setText(0, 2, TEXT_CONSTANTS.pointType());
			setGridRowStyle(grid, 0, false);

			int count = 1;

			if (questions != null) {
				for (QuestionDto q : questions) {

					grid.setWidget(count, 0, new Label(q.getText()));
					ListBox attrListbox = new ListBox();
					attrListbox.addItem("", "");
					for (Entry<String, String> entry : attributes.entrySet()) {
						String text = entry.getValue();
						if (text == null || text.trim().length() == 0) {
							text = entry.getKey();
						}
						attrListbox.addItem(text, entry.getKey());
					}
					grid.setWidget(count, 1, attrListbox);
					attributeListboxes.put(currentSurveySelection.getKeyId()
							+ ":" + q.getKeyId(), attrListbox);
					ListBox typeListbox = new ListBox(true);
					for (int i = 0; i < AccessPointType.values().length; i++) {
						String val = AccessPointType.values()[i].toString();
						typeListbox.addItem(val, val);
					}
					grid.setWidget(count, 2, typeListbox);
					typeListboxes.put(currentSurveySelection.getKeyId() + ":"
							+ q.getKeyId(), typeListbox);
					setGridRowStyle(grid, count, false);
					count++;
				}
			}

			gridPanel.add(grid);
		} else {
			gridPanel.add(new Label(TEXT_CONSTANTS.noQuestions()));
		}
	}

	/**
	 * sets the css for a row in a grid. the top row will get the header style
	 * and other rows get either the even or odd style. If selected is true and
	 * row > 0 then it will set the selected style
	 * 
	 * @param grid
	 * @param row
	 * @param selected
	 */
	private void setGridRowStyle(Grid grid, int row, boolean selected) {
		// if we already had a selection, unselect it
		String style = "";
		if (selected) {
			style = SELECTED_ROW_CSS;
		} else {
			if (row > 0) {
				if (row % 2 == 0) {
					style = EVEN_ROW_CSS;
				} else {
					style = ODD_ROW_CSS;
				}
			} else {
				style = GRID_HEADER_CSS;
			}
		}
		for (int i = 0; i < grid.getColumnCount(); i++) {
			grid.getCellFormatter().setStyleName(row, i, style);
		}
	}

	private void reset() {
		surveyGroup.setSelectedIndex(0);
		surveyListbox.setSelectedIndex(0);
		surveyListbox.clear();
		questionGroupListbox.setSelectedIndex(0);
		questionGroupListbox.clear();
		gridPanel.clear();
		currentSurveySelection = null;
		currentQuestionGroupDtoList = null;
		currentQuestionList = null;
		currentQuestionGroupSelection = null;
		statusLabel.setVisible(false);

	}

	private void getSurveys() {
		if (surveyGroup.getSelectedIndex() > 0) {
			final String selectedGroupId = surveyGroup.getValue(surveyGroup
					.getSelectedIndex());

			if (selectedGroupId != null) {
				if (surveys.get(selectedGroupId) != null) {
					populateSurveyList(surveys.get(selectedGroupId));
				} else {
					loadingDialog.showCentered();
					// Set up the callback object.
					AsyncCallback<ArrayList<SurveyDto>> surveyCallback = new AsyncCallback<ArrayList<SurveyDto>>() {
						public void onFailure(Throwable caught) {
							loadingDialog.hide(true);
							MessageDialog errDia = new MessageDialog(
									TEXT_CONSTANTS.error(), TEXT_CONSTANTS
											.errorTracePrefix()
											+ " "
											+ caught.getLocalizedMessage());
							errDia.showRelativeTo(saveButton);
						}

						public void onSuccess(ArrayList<SurveyDto> result) {
							loadingDialog.hide(true);
							if (result != null) {
								surveys.put(selectedGroupId, result);
								populateSurveyList(result);
							}
						}
					};
					surveyService.listSurveysByGroup(selectedGroupId,
							surveyCallback);
				}
			} else {
				MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
						.inputError(), TEXT_CONSTANTS.selectGroupFirst());
				errDia.showRelativeTo(saveButton);
			}
		} else {
			surveyListbox.clear();
		}
	}

	/**
	 * calls the service to load the survey question groups
	 * 
	 * @param id
	 * @return
	 */
	private void loadSurveyQuestionGroups(final Long id) {
		statusLabel.setText(TEXT_CONSTANTS.loading());
		statusLabel.setVisible(true);
		loadingDialog.showCentered();
		AsyncCallback<ArrayList<QuestionGroupDto>> surveyCallback = new AsyncCallback<ArrayList<QuestionGroupDto>>() {
			public void onFailure(Throwable caught) {
				loadingDialog.hide(true);
				statusLabel.setVisible(false);
				MessageDialog errDia = new MessageDialog(
						TEXT_CONSTANTS.error(), TEXT_CONSTANTS
								.errorTracePrefix()
								+ " "
								+ caught.getLocalizedMessage());
				errDia.showRelativeTo(saveButton);
			}

			public void onSuccess(ArrayList<QuestionGroupDto> result) {
				loadingDialog.hide(true);
				statusLabel.setVisible(false);
				if (result != null) {
					currentQuestionGroupDtoList = result;
					populateQuestionGroupList(result);
				}
			}
		};
		surveyService.listQuestionGroupsBySurvey(id.toString(), surveyCallback);
	}

	private void loadQuestions(final Long groupId) {
		loadingDialog.showCentered();
		statusLabel.setText(TEXT_CONSTANTS.loading());
		statusLabel.setVisible(true);
		AsyncCallback<ArrayList<QuestionDto>> questionCallback = new AsyncCallback<ArrayList<QuestionDto>>() {
			public void onFailure(Throwable caught) {
				loadingDialog.hide(true);
				statusLabel.setVisible(false);
				MessageDialog errDia = new MessageDialog(
						TEXT_CONSTANTS.error(), TEXT_CONSTANTS
								.errorTracePrefix()
								+ " "
								+ caught.getLocalizedMessage());
				errDia.showRelativeTo(saveButton);
			}

			public void onSuccess(ArrayList<QuestionDto> result) {
				loadingDialog.hide(true);
				statusLabel.setVisible(false);
				if (result != null) {
					currentQuestionList = result;
					loadExistingMappings(currentSurveySelection.getKeyId());
					updateDataGrid(result);
				}
			}
		};
		surveyService.listQuestionsByQuestionGroup(groupId.toString(), false,false,
				questionCallback);

	}

	private void loadExistingMappings(final Long id) {
		loadingDialog.showCentered();
		mappingService.listMappingsBySurvey(id,
				new AsyncCallback<ArrayList<SurveyAttributeMappingDto>>() {
					@Override
					public void onFailure(Throwable caught) {
						loadingDialog.hide(true);
						MessageDialog errDia = new MessageDialog(
								TEXT_CONSTANTS.error(), TEXT_CONSTANTS
										.errorTracePrefix()
										+ " "
										+ caught.getLocalizedMessage());
						errDia.showRelativeTo(saveButton);
					}

					@Override
					public void onSuccess(
							ArrayList<SurveyAttributeMappingDto> result) {
						loadingDialog.hide(true);
						if (result != null) {
							for (SurveyAttributeMappingDto dto : result) {
								ListBox box = findListBox(dto,
										attributeListboxes);
								if (box != null) {
									selectBoxItem(dto.getAttributeName(), box);
								}
								if (dto.getApTypes() != null
										&& dto.getApTypes().size() > 0) {
									ListBox typeBox = findListBox(dto,
											typeListboxes);
									if (typeBox != null) {
										for (String type : dto.getApTypes()) {
											selectBoxItem(type, typeBox);
										}
									}
								}
							}
						}
					}
				});
	}

	/**
	 * iterates over contents of a list box and selects the item in it if it
	 * matches the value
	 * 
	 * @param val
	 * @param box
	 */
	private void selectBoxItem(String val, ListBox box) {
		for (int i = 0; i < box.getItemCount(); i++) {
			if (box.getValue(i) != null && box.getValue(i).equals(val)) {
				box.setItemSelected(i, true);
				break;
			}
		}
	}

	/**
	 * finds a list box in the map passed in using the composite key formed from
	 * the dto
	 * 
	 * @param dto
	 * @param boxMap
	 * @return
	 */
	private ListBox findListBox(SurveyAttributeMappingDto dto,
			HashMap<String, ListBox> boxMap) {
		return boxMap.get(dto.getSurveyId() + ":" + dto.getSurveyQuestionId());
	}

	private void populateSurveyList(ArrayList<SurveyDto> surveyItems) {
		surveyListbox.clear();
		currentSurveyDtoList = surveyItems;
		if (surveyItems != null) {
			surveyListbox.addItem("", "");
			for (SurveyDto survey : surveyItems) {
				surveyListbox.addItem(survey.getName() != null ? survey
						.getName() : TEXT_CONSTANTS.survey()+ " " + survey.getKeyId().toString(),
						survey.getKeyId().toString());
			}
		}
	}

	private void populateQuestionGroupList(ArrayList<QuestionGroupDto> groups) {
		questionGroupListbox.clear();
		currentQuestionGroupDtoList = groups;
		if (groups != null) {
			questionGroupListbox.addItem("", "");
			for (QuestionGroupDto group : groups) {
				questionGroupListbox.addItem(group.getDisplayName(), group
						.getKeyId().toString());
			}
		}
	}

	/**
	 * creates the controls for header-level information (dates, names, etc)
	 * 
	 * @return
	 */
	private Widget createInputControls() {
		VerticalPanel controlPanel = new VerticalPanel();

		HorizontalPanel labelPanel = new HorizontalPanel();
		labelPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.surveyGroup()));
		surveyGroup = new ListBox();
		labelPanel.add(surveyGroup);
		controlPanel.add(labelPanel);
		surveyGroup.addChangeHandler(this);

		labelPanel = new HorizontalPanel();
		labelPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.survey()));
		surveyListbox = new ListBox();
		surveyListbox.addChangeHandler(this);
		labelPanel.add(surveyListbox);
		controlPanel.add(labelPanel);

		labelPanel = new HorizontalPanel();
		labelPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.questionGroup()));
		questionGroupListbox = new ListBox();
		questionGroupListbox.addChangeHandler(this);
		labelPanel.add(questionGroupListbox);
		controlPanel.add(labelPanel);

		return controlPanel;
	}

	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * populates a dto using the values currently set in the UI widgets and
	 * persists it to the server
	 */
	private void saveMapping() {

		if (currentSurveySelection != null && currentQuestionList != null) {
			ArrayList<SurveyAttributeMappingDto> mappingDtoList = new ArrayList<SurveyAttributeMappingDto>();

			for (QuestionDto q : currentQuestionList) {
				SurveyAttributeMappingDto dto = new SurveyAttributeMappingDto();
				dto
						.setQuestionGroupId(currentQuestionGroupSelection
								.getKeyId());
				dto.setSurveyId(currentSurveySelection.getKeyId());
				dto.setSurveyQuestionId(q.getKeyId().toString());
				ListBox attrBox = findListBox(dto, attributeListboxes);
				if (attrBox != null) {
					String val = attrBox.getValue(attrBox.getSelectedIndex());
					if (val != null && val.trim().length() > 0) {

						dto.setObjectName(MAP_TARGET_OBJECT_NAME);
						dto.setAttributeName(val);
						mappingDtoList.add(dto);
					}
					ListBox typeBox = findListBox(dto, typeListboxes);
					if (typeBox != null) {
						List<String> typeList = new ArrayList<String>();
						for (int i = 0; i < typeBox.getItemCount(); i++) {
							if (typeBox.isItemSelected(i)) {
								typeList.add(typeBox.getValue(i));
							}
						}
						dto.setApTypes(typeList);
					}
				}
			}

			mappingService.saveMappings(currentQuestionGroupSelection
					.getKeyId(), mappingDtoList,
					new AsyncCallback<ArrayList<SurveyAttributeMappingDto>>() {

						@Override
						public void onSuccess(
								ArrayList<SurveyAttributeMappingDto> result) {
							statusLabel.setText(TEXT_CONSTANTS.saveComplete());
							statusLabel.setVisible(true);
						}

						@Override
						public void onFailure(Throwable caught) {
							statusLabel.setText(TEXT_CONSTANTS.errorTracePrefix()+" "
									+ caught.getLocalizedMessage());
							statusLabel.setVisible(true);
						}
					});
		}
	}

	/**
	 * handles all the button clicks for this portlet
	 */
	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == resetButton) {
			reset();
			currentSurveySelection = null;
		} else if (event.getSource() == saveButton) {
			saveMapping();
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == surveyGroup) {
			getSurveys();
		} else if (event.getSource() == surveyListbox) {
			currentSurveySelection = currentSurveyDtoList.get(surveyListbox
					.getSelectedIndex() - 1);
			loadSurveyQuestionGroups(currentSurveySelection.getKeyId());
		} else if (event.getSource() == questionGroupListbox) {
			currentQuestionGroupSelection = currentQuestionGroupDtoList
					.get(questionGroupListbox.getSelectedIndex() - 1);
			loadQuestions(currentQuestionGroupSelection.getKeyId());
		} else if (event.getSource() == saveButton) {
			saveMapping();
		} else if (event.getSource() == resetButton) {
			reset();
		}
	}
}
