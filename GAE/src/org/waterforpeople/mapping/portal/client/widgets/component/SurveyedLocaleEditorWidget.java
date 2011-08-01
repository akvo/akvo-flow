package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyMetricMappingService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyMetricMappingServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyalValueDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyedLocaleDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyedLocaleService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyedLocaleServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceService;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
		ChangeHandler, ClickHandler {
	public static final String LOCALE_KEY = "locale";
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private CompletionListener saveCompleteListener;
	private Panel contentPanel;
	private Label statusLabel;
	private boolean readWriteMode;
	private Button saveButton;
	private SurveyInstanceServiceAsync surveyInstanceService;
	private SurveyedLocaleServiceAsync surveyedLocaleService;
	private SurveyMetricMappingServiceAsync metricService;
	private List<SurveyInstanceDto> surveyInstances;
	private Map<Long, List<SurveyalValueDto>> surveyalValueMap;
	private SurveyedLocaleDto localeDto;
	private ListBox instanceListBox;
	private DateTimeFormat dateFormat;
	private DisclosurePanel filterPanel;

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
	private TextBox pointTypeBox;
	private TextBox statusBox;
	private TextBox organizationBox;
	private TextBox systemBox;
	private MapWidget localeMap;
	private FlexTable instanceTable;
	private Map<Widget, SurveyalValueDto> widgetToValueMap;

	public SurveyedLocaleEditorWidget(boolean allowEdit,
			SurveyedLocaleDto surveyedLocale,
			CompletionListener saveCompleteListener) {
		readWriteMode = allowEdit;
		this.saveCompleteListener = saveCompleteListener;
		surveyalValueMap = new HashMap<Long, List<SurveyalValueDto>>();
		localeDto = surveyedLocale;
		surveyInstanceService = GWT.create(SurveyInstanceService.class);
		surveyedLocaleService = GWT.create(SurveyedLocaleService.class);
		metricService = GWT.create(SurveyMetricMappingService.class);

		dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
		initializeUi();
	}

	private void initializeUi() {
		contentPanel = new VerticalPanel();
		statusLabel = ViewUtil.initLabel(TEXT_CONSTANTS.pleaseWait());

		saveButton = new Button(TEXT_CONSTANTS.save());
		saveButton.addClickHandler(this);
		instanceTable = new FlexTable();
		TabPanel tp = new TabPanel();
		tp.add(constructGeneralTab(localeDto), TEXT_CONSTANTS.general());
		tp.add(constructInstanceTab(), TEXT_CONSTANTS.attributes());
		tp.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if (event.getSelectedItem() == 1) {
					if (surveyInstances == null && localeDto != null) {
						loadInstances(localeDto.getKeyId(), null, null);
					}
				}
			}
		});
		tp.selectTab(0);
		contentPanel.add(tp);
		if (readWriteMode) {
			contentPanel.add(saveButton);
		}
		initWidget(contentPanel);
	}

	/**
	 * load the survey instances for the given locale and populate them in the
	 * UI control
	 * 
	 * @param localeId
	 */
	protected void loadInstances(Long localeId, Date from, Date to) {
		instanceListBox.clear();
		instanceTable.clear(true);
		surveyInstanceService.listInstancesByLocale(localeId, from, to, null,
				new AsyncCallback<ResponseDto<ArrayList<SurveyInstanceDto>>>() {
					@Override
					public void onFailure(Throwable caught) {
						MessageDialog dia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
						dia.showCentered();
					}

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<SurveyInstanceDto>> result) {
						surveyInstances = result.getPayload();
						statusLabel.setVisible(false);
						if (result.getPayload() != null
								&& result.getPayload().size() > 0
								&& result.getCursorString() != null) {
							filterPanel.setVisible(true);
						} else {
							filterPanel.setVisible(false);
						}
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
			widgetToValueMap = new HashMap<Widget, SurveyalValueDto>();

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
				widgetToValueMap.put(input, val);
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
		Panel controlPanel = new VerticalPanel();

		ViewUtil.installFieldRow(controlPanel, TEXT_CONSTANTS.instance(),
				instanceListBox, null);
		filterPanel = new DisclosurePanel(TEXT_CONSTANTS.showingMostRecent());
		final DateBox dateFrom = new DateBox();
		final DateBox dateTo = new DateBox();

		Button filterButton = new Button(TEXT_CONSTANTS.filterResults());
		filterButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (dateFrom.getValue() != null || dateTo.getValue() != null) {
					loadInstances(localeDto.getKeyId(), dateFrom.getValue(),
							dateTo.getValue());
				} else {
					MessageDialog dia = new MessageDialog(TEXT_CONSTANTS
							.inputError(), TEXT_CONSTANTS.dateMandatory());
					dia.showCentered();
				}
			}
		});
		filterPanel.setVisible(false);
		HorizontalPanel dateContainer = new HorizontalPanel();
		dateContainer.add(ViewUtil.initLabel(TEXT_CONSTANTS
				.collectionDateFrom()));
		dateContainer.add(dateFrom);
		dateContainer.add(ViewUtil.initLabel(TEXT_CONSTANTS.to()));
		dateContainer.add(dateTo);
		VerticalPanel filterControl = new VerticalPanel();
		filterControl.add(dateContainer);
		filterControl.add(filterButton);
		filterPanel.add(filterControl);
		controlPanel.add(filterPanel);
		cap.add(controlPanel);
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
		int row = 0;

		grid.setWidget(row, 0, ViewUtil.initLabel(TEXT_CONSTANTS.id()));
		identifierTextBox = new TextBox();
		grid.setWidget(row, 1, identifierTextBox);
		row++;

		grid.setWidget(row, 0, ViewUtil.initLabel(TEXT_CONSTANTS.countryCode()));
		countryTextBox = new TextBox();
		grid.setWidget(row, 1, countryTextBox);
		row++;

		grid.setWidget(row, 0, ViewUtil.initLabel(TEXT_CONSTANTS.pointType()));
		pointTypeBox = new TextBox();
		grid.setWidget(row, 1, pointTypeBox);
		row++;

		grid.setWidget(row, 0, ViewUtil.initLabel(TEXT_CONSTANTS.status()));
		statusBox = new TextBox();
		grid.setWidget(row, 1, statusBox);
		row++;

		grid.setWidget(row, 0, ViewUtil.initLabel(TEXT_CONSTANTS.latitude()));
		latTextBox = ViewUtil.constructNumericTextBox();
		grid.setWidget(row, 1, latTextBox);
		row++;

		grid.setWidget(row, 0, ViewUtil.initLabel(TEXT_CONSTANTS.longitude()));
		lonTextBox = ViewUtil.constructNumericTextBox();
		grid.setWidget(row, 1, lonTextBox);
		row++;

		localeMap = new MapWidget();
		localeMap.setSize("180px", "180px");
		localeMap.addControl(new SmallZoomControl());
		grid.setWidget(0, 2, localeMap);
		grid.getFlexCellFormatter().setRowSpan(0, 2, 5);

		grid.setWidget(row, 0, ViewUtil.initLabel(TEXT_CONSTANTS.lastUpdated()));
		lastSurveyedDateBox = new DateBox();
		grid.setWidget(row, 1, lastSurveyedDateBox);
		row++;

		grid.setWidget(row, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 1));
		sub1Box = new TextBox();
		grid.setWidget(row, 1, sub1Box);
		row++;

		grid.setWidget(row, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 2));
		sub2Box = new TextBox();
		grid.setWidget(row, 1, sub2Box);
		row++;

		grid.setWidget(row, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 3));
		sub3Box = new TextBox();
		grid.setWidget(row, 1, sub3Box);
		row++;

		grid.setWidget(row, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 4));
		sub4Box = new TextBox();
		grid.setWidget(row, 1, sub4Box);
		row++;

		grid.setWidget(row, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 5));
		sub5Box = new TextBox();
		grid.setWidget(row, 1, sub5Box);
		row++;

		grid.setWidget(row, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 6));
		sub6Box = new TextBox();
		grid.setWidget(row, 1, sub6Box);
		row++;

		grid.setWidget(row, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.organization()));
		organizationBox = new TextBox();
		grid.setWidget(row, 1, organizationBox);
		row++;

		grid.setWidget(row, 0, ViewUtil.initLabel(TEXT_CONSTANTS.system()));
		systemBox = new TextBox();
		grid.setWidget(row, 1, systemBox);

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
			if (dto.getLatitude() != null && dto.getLongitude() != null) {
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
			if (dto.getOrganization() != null) {
				organizationBox.setText(dto.getOrganization());
			}
			if (dto.getSystemIdentifier() != null) {
				systemBox.setText(dto.getSystemIdentifier());
			}
			if (dto.getLocaleType() != null) {
				pointTypeBox.setText(dto.getLocaleType());
			}
			if (dto.getCurrentStatus() != null) {
				statusBox.setText(dto.getCurrentStatus());
			}
		}
		return grid;
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == instanceListBox) {
			loadInstanceValues(new Long(
					instanceListBox.getValue(instanceListBox.getSelectedIndex())));
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == saveButton) {
			if (updateDtoValues()) {
				surveyedLocaleService.saveSurveyedLocale(localeDto,
						new AsyncCallback<SurveyedLocaleDto>() {

							@Override
							public void onFailure(Throwable caught) {
								MessageDialog dia = new MessageDialog(
										TEXT_CONSTANTS.error(), TEXT_CONSTANTS
												.errorTracePrefix()
												+ " "
												+ caught.getLocalizedMessage());
								dia.showCentered();
							}

							@Override
							public void onSuccess(SurveyedLocaleDto result) {
								if (saveCompleteListener != null) {
									Map<String, Object> payload = new HashMap<String, Object>();
									payload.put(LOCALE_KEY, result);
									saveCompleteListener.operationComplete(
											true, payload);
								}
							}
						});
			}
		}
	}

	private boolean updateDtoValues() {
		if (localeDto == null) {
			localeDto = new SurveyedLocaleDto();
		}
		if (ViewUtil.isTextPopulated(latTextBox)
				&& ViewUtil.isTextPopulated(lonTextBox)) {

			localeDto.setCountryCode(countryTextBox.getText());
			localeDto.setSublevel1(sub1Box.getText());
			localeDto.setSublevel2(sub2Box.getText());
			localeDto.setSublevel3(sub3Box.getText());
			localeDto.setSublevel4(sub4Box.getText());
			localeDto.setSublevel5(sub5Box.getText());
			localeDto.setSublevel6(sub6Box.getText());
			localeDto.setIdentifier(identifierTextBox.getText());
			if (lastSurveyedDateBox.getValue() == null
					&& localeDto.getLastSurveyedDate() == null) {
				localeDto.setLastSurveyedDate(new Date());
			} else if (lastSurveyedDateBox.getValue() != null) {
				localeDto.setLastSurveyedDate(lastSurveyedDateBox.getValue());
			}
			localeDto.setAmbiguous(false);
			// even though we have numeric only text fields, people can bypass
			// the checks via copy/paste so we still need to treat the input as
			// potentially wrong
			try {
				localeDto.setLatitude(new Double(latTextBox.getValue()));
				localeDto.setLongitude(new Double(lonTextBox.getValue()));
			} catch (NumberFormatException e) {
				MessageDialog dia = new MessageDialog(
						TEXT_CONSTANTS.inputError(),
						TEXT_CONSTANTS.latLonNumeric());
				dia.showCentered();
				// stop processing
				return false;
			}
			localeDto.setLocaleType(pointTypeBox.getText());
			localeDto.setCurrentStatus(statusBox.getText());
			localeDto.setOrganization(organizationBox.getText());
			localeDto.setSystemIdentifier(systemBox.getText());
			if (widgetToValueMap != null) {
				List<SurveyalValueDto> valueList = new ArrayList<SurveyalValueDto>();
				for (Entry<Widget, SurveyalValueDto> valEntry : widgetToValueMap
						.entrySet()) {
					if (valEntry.getKey() instanceof TextBox) {
						String newVal = ((TextBox) valEntry.getKey()).getText();
						String oldVal = valEntry.getValue().getStringValue();
						// if they're both the same object (or null) we don't
						// want them
						if (newVal != oldVal) {
							if (newVal == null || !newVal.equals(oldVal)) {
								valEntry.getValue().setStringValue(newVal);
								try {
									valEntry.getValue().setNumericValue(
											new Double(newVal));

								} catch (Exception e) {
									// swallow
								}
								// since the value has changed, add it to the
								// list to be saved
								valueList.add(valEntry.getValue());
							}
						}
					}
				}
				localeDto.setValues(valueList);
			}
			return true;
		} else {
			MessageDialog dia = new MessageDialog(TEXT_CONSTANTS.inputError(),
					TEXT_CONSTANTS.latLonMandatory());
			dia.showCentered();
			return false;
		}

	}
}
