package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyalValueDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyedLocaleDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyedLocaleService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyedLocaleServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceService;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.SmallZoomControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * UI Widget to edit/created SurveydLocalse objects. This widget will also
 * support editing/adding metrics for a given locale
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyedLocaleEditorWidget extends Composite implements
		ChangeHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private Panel contentPanel;
	private Label statusLabel;
	private boolean readWriteMode;
	private Button saveButton;
	private SurveyInstanceServiceAsync surveyInstanceService;
	private SurveyedLocaleServiceAsync surveyedLocaleService;
	private List<SurveyInstanceDto> surveyInstances;
	private Map<Long, List<SurveyalValueDto>> surveyalValueMap;
	private SurveyedLocaleDto localeDto;
	private ListBox instanceListBox;
	private DateTimeFormat dateFormat;

	private TextBox countryTextBox;
	private TextBox identifierTextBox;
	private TextBox latTextBox;
	private TextBox lonTextBox;
	private DateBox lastSurveyedDateBox;
	private TextBox sub1Box;
	private TextBox sub2Box;
	private TextBox sub3Box;
	private TextBox sub4Box;
	private TextBox sub5Box;
	private TextBox sub6Box;
	private MapWidget localeMap;
	private FlexTable instanceTable;
	private Map<Widget, Long> widgetToIdMap;

	public SurveyedLocaleEditorWidget(boolean allowEdit,
			SurveyedLocaleDto surveyedLocale) {
		readWriteMode = allowEdit;
		surveyalValueMap = new HashMap<Long, List<SurveyalValueDto>>();
		localeDto = surveyedLocale;
		surveyInstanceService = GWT.create(SurveyInstanceService.class);
		surveyedLocaleService = GWT.create(SurveyedLocaleService.class);

		dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
		initializeUi();
	}

	private void initializeUi() {
		contentPanel = new VerticalPanel();
		statusLabel = ViewUtil.initLabel(TEXT_CONSTANTS.pleaseWait());

		saveButton = new Button(TEXT_CONSTANTS.save());
		instanceTable = new FlexTable();
		TabPanel tp = new TabPanel();
		tp.add(constructGeneralTab(localeDto), TEXT_CONSTANTS.general());
		tp.add(constructInstanceTab(), TEXT_CONSTANTS.attributes());
		tp.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if (event.getSelectedItem() == 1) {
					if (surveyInstances == null && localeDto != null) {
						loadInstances(localeDto.getKeyId());
					}
				}
			}
		});
		tp.selectTab(0);
		contentPanel.add(tp);
		initWidget(contentPanel);
	}

	/**
	 * load the survey instances for the given locale and populate them in the
	 * UI control
	 * 
	 * @param localeId
	 */
	protected void loadInstances(Long localeId) {
		surveyInstanceService.listInstancesByLocale(localeId,
				new AsyncCallback<List<SurveyInstanceDto>>() {
					@Override
					public void onFailure(Throwable caught) {
						MessageDialog dia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
						dia.showCentered();
					}

					@Override
					public void onSuccess(List<SurveyInstanceDto> result) {
						surveyInstances = result;
						statusLabel.setVisible(false);
						if (surveyInstances != null) {
							for (SurveyInstanceDto d : surveyInstances) {
								instanceListBox.addItem(dateFormat.format(d
										.getCollectionDate()), d.getKeyId()
										.toString());
							}
							if (surveyInstances.size() > 0) {
								loadInstanceValues(surveyInstances.get(0)
										.getKeyId());
							}
						}

					}
				});
	}

	/**
	 * loads the selected instances into the ui
	 * 
	 * @param instanceId
	 */
	private void loadInstanceValues(final Long instanceId) {
		instanceTable.clear(true);
		if (!surveyalValueMap.containsKey(instanceId)) {
			statusLabel.setVisible(true);
			surveyedLocaleService.listSurveyalValuesByInstance(instanceId,
					new AsyncCallback<List<SurveyalValueDto>>() {

						@Override
						public void onFailure(Throwable caught) {
							MessageDialog dia = new MessageDialog(
									TEXT_CONSTANTS.error(), TEXT_CONSTANTS
											.errorTracePrefix()
											+ " "
											+ caught.getLocalizedMessage());
							dia.showCentered();
							statusLabel.setVisible(false);
						}

						@Override
						public void onSuccess(List<SurveyalValueDto> result) {
							surveyalValueMap.put(instanceId, result);
							populateValueUi(result);

						}
					});
		} else {
			populateValueUi(surveyalValueMap.get(instanceId));
		}

	}

	/**
	 * populates the instanceTable with the values from the list TODO: handle
	 * adding remaining metrics to the UI so we can populate new items
	 * 
	 * @param valList
	 */
	private void populateValueUi(List<SurveyalValueDto> valList) {
		statusLabel.setVisible(false);
		if (valList != null) {
			widgetToIdMap = new HashMap<Widget, Long>();
			int count = 0;
			for (SurveyalValueDto val : valList) {
				String label = val.getMetricName();
				if (label == null || label.trim().length() == 0) {
					label = val.getQuestionText();
				}
				if (label == null) {
					label = val.getKeyId().toString();
				}

				instanceTable.setWidget(count, 0, ViewUtil.initLabel(label));
				TextBox input = new TextBox();
				if (val.getStringValue() != null) {
					input.setValue(val.getStringValue());
				}
				widgetToIdMap.put(input, val.getKeyId());
				instanceTable.setWidget(count, 1, input);
				count++;
			}
		}
	}

	/**
	 * constructs a tab to be used to display all the SurveyalValues for a
	 * single survey instance.
	 * 
	 * @return
	 */
	private Widget constructInstanceTab() {
		Panel tabContent = new VerticalPanel();
		statusLabel = ViewUtil.initLabel(TEXT_CONSTANTS.loading());
		instanceTable = new FlexTable();
		instanceListBox = new ListBox(false);
		CaptionPanel cap = new CaptionPanel(TEXT_CONSTANTS.selectInstance());
		ViewUtil.installFieldRow(cap, TEXT_CONSTANTS.instance(),
				instanceListBox, null);
		tabContent.add(cap);
		tabContent.add(statusLabel);
		tabContent.add(instanceTable);
		return tabContent;
	}

	/**
	 * constructs the widgets needed to display the "general" tab within the
	 * locale editor. If the dto passed in is non-null, the values contained
	 * therein will be used to populate the controls.
	 * 
	 * TODO: add click handler on map to update lat/lon based on new position
	 * (may need to prompt if for existing items)
	 * 
	 * @param dto
	 * @return
	 */
	private Widget constructGeneralTab(SurveyedLocaleDto dto) {
		FlexTable grid = new FlexTable();

		grid.setWidget(0, 0, ViewUtil.initLabel(TEXT_CONSTANTS.id()));
		identifierTextBox = new TextBox();
		grid.setWidget(0, 1, identifierTextBox);

		grid.setWidget(1, 0, ViewUtil.initLabel(TEXT_CONSTANTS.countryCode()));
		countryTextBox = new TextBox();
		grid.setWidget(1, 1, countryTextBox);

		grid.setWidget(2, 0, ViewUtil.initLabel(TEXT_CONSTANTS.latitude()));
		latTextBox = ViewUtil.constructNumericTextBox();
		grid.setWidget(2, 1, latTextBox);

		grid.setWidget(3, 0, ViewUtil.initLabel(TEXT_CONSTANTS.longitude()));
		lonTextBox = ViewUtil.constructNumericTextBox();
		grid.setWidget(3, 1, lonTextBox);

		localeMap = new MapWidget();
		localeMap.setSize("180px", "180px");
		localeMap.addControl(new SmallZoomControl());
		grid.setWidget(0, 2, localeMap);
		grid.getFlexCellFormatter().setRowSpan(0, 2, 5);

		grid.setWidget(4, 0, ViewUtil.initLabel(TEXT_CONSTANTS.lastUpdated()));
		lastSurveyedDateBox = new DateBox();
		grid.setWidget(4, 1, lastSurveyedDateBox);

		grid.setWidget(5, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 1));
		sub1Box = new TextBox();

		grid.setWidget(5, 1, sub1Box);

		grid.setWidget(6, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 2));
		sub2Box = new TextBox();
		grid.setWidget(6, 1, sub2Box);

		grid.setWidget(7, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 3));
		sub3Box = new TextBox();
		grid.setWidget(7, 1, sub3Box);

		grid.setWidget(8, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 4));
		sub4Box = new TextBox();
		grid.setWidget(8, 1, sub4Box);

		grid.setWidget(9, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 5));
		sub5Box = new TextBox();
		grid.setWidget(9, 1, sub5Box);

		grid.setWidget(10, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 6));
		sub6Box = new TextBox();
		grid.setWidget(10, 1, sub6Box);

		if (dto != null) {
			if (dto.getIdentifier() != null) {
				identifierTextBox.setText(dto.getIdentifier());
			}
			if (dto.getCountryCode() != null) {
				countryTextBox.setText(dto.getCountryCode());
			}
			if (dto.getSublevel1() != null) {
				sub1Box.setText(dto.getSublevel1());
			}
			if (dto.getSublevel2() != null) {
				sub2Box.setText(dto.getSublevel2());
			}
			if (dto.getSublevel3() != null) {
				sub3Box.setText(dto.getSublevel3());
			}
			if (dto.getSublevel4() != null) {
				sub4Box.setText(dto.getSublevel4());
			}
			if (dto.getSublevel5() != null) {
				sub5Box.setText(dto.getSublevel5());
			}
			if (dto.getSublevel6() != null) {
				sub6Box.setText(dto.getSublevel6());
			}
			if (dto != null && dto.getLatitude() != null
					&& dto.getLongitude() != null) {
				latTextBox.setText(dto.getLatitude().toString());
				lonTextBox.setText(dto.getLongitude().toString());
				try {
					LatLng point = LatLng.newInstance(dto.getLatitude(),
							dto.getLongitude());
					localeMap.addOverlay(new Marker(point));
					localeMap.setZoomLevel(12);
					localeMap.setCenter(point);
				} catch (Throwable e) {
					// swallow
				}
			}
		}
		return grid;
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == instanceListBox) {

		}

	}
}
