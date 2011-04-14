package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.gis.app.gwt.client.GISSupportService;
import com.gallatinsystems.gis.app.gwt.client.GISSupportServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GISSetupWidget extends Composite implements ChangeHandler {
	private static final String UTM = "utm";
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static final String LABEL_STYLE = "input-label-padded";
	private static final int DEFAULT_ITEM_COUNT = 5;
	private Panel contentPanel;
	private MessageDialog loadingDialog;
	private TerminalType termType;
	private ListBox coordinateSystemType = new ListBox();
	private ListBox utmZones = new ListBox();
	private TextBox centralMeridian = new TextBox();
	private ListBox countryCode = new ListBox();
	private ListBox featureType = new ListBox();
	private GISSupportServiceAsync gisSupportService;

	public enum Orientation {
		VERTICAL, HORIZONTAL
	};

	public enum TerminalType {
		SURVEY, QUESTIONGROUP
	};

	public GISSetupWidget(
			Orientation orient,
			org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.TerminalType survey) {
		gisSupportService = GWT.create(GISSupportService.class);
		if (Orientation.HORIZONTAL == orient) {
			contentPanel = new HorizontalPanel();
		} else {
			contentPanel = new VerticalPanel();
		}
		populateCountryList();
		populateCoordinateSystem();
		populateFeatureType();
		installChangeHandler();
		ViewUtil.installFieldRow(contentPanel,
				TEXT_CONSTANTS.setCentralMeridian(),
				centralMeridian, LABEL_STYLE);
		initWidget(contentPanel);
	}

	public String getCountryCode() {
		if (countryCode.getSelectedIndex() > -1)
			return countryCode.getItemText(countryCode.getSelectedIndex());
		else
			return null;
	}

	public String getCoordinateType() {
		if (coordinateSystemType.getSelectedIndex() > -1)
			return coordinateSystemType.getItemText(coordinateSystemType
					.getSelectedIndex());
		else
			return null;
	}

	public Integer getUTMZone() {
		if (utmZones.getSelectedIndex() > -1)
			return Integer.parseInt(utmZones.getItemText(utmZones
					.getSelectedIndex()));
		else
			return null;
	}

	public String getGISFeatureType() {
		if (featureType.getSelectedIndex() > -1)
			return featureType.getItemText(featureType.getSelectedIndex());
		else
			return null;
	}

	@Override
	public void onChange(ChangeEvent event) {
	}

	private void populateCoordinateSystem() {
		gisSupportService
				.listCoordinateTypes(new AsyncCallback<TreeMap<String, String>>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(TreeMap<String, String> result) {
						for (Entry<String, String> item : result.entrySet()) {
							coordinateSystemType.addItem(item.getKey(),
									item.getValue());
						}
						ViewUtil.installFieldRow(contentPanel,
								TEXT_CONSTANTS.selectCoordinateSystem(),
								coordinateSystemType, LABEL_STYLE);
					}

				});

	}

	private Boolean loadedUTMZones = false;

	private void installChangeHandler() {
		coordinateSystemType.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				if (coordinateSystemType.getItemText(
						coordinateSystemType.getSelectedIndex()).equals(UTM)) {
					if (!loadedUTMZones) {
						populateUTMZones();
						loadedUTMZones = true;
					} else {
						utmZones.setVisible(true);
					}
				} else {
					if (utmZones.isVisible()) {
						utmZones.setVisible(false);
					}
				}

			}
		});
	}

	private void populateUTMZones() {
		gisSupportService.listUTMZones(new AsyncCallback<ArrayList<Integer>>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(ArrayList<Integer> result) {
				for (Integer item : result) {
					utmZones.addItem(item.toString());
				}
				ViewUtil.installFieldRow(contentPanel,
						TEXT_CONSTANTS.selectUtmZone(), utmZones, LABEL_STYLE);
			}

		});
	}

	private void populateFeatureType() {
		gisSupportService
				.listFeatureTypes(new AsyncCallback<TreeMap<String, String>>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(TreeMap<String, String> result) {
						for (Entry<String, String> item : result.entrySet()) {
							featureType.addItem(item.getKey(), item.getValue());
						}
						ViewUtil.installFieldRow(contentPanel,
								TEXT_CONSTANTS.selectGISFeatureType(),
								featureType, LABEL_STYLE);
					}

				});
	}

	private void populateCountryList() {

		gisSupportService
				.listCountryCodes(new AsyncCallback<TreeMap<String, String>>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(TreeMap<String, String> result) {
						for (Entry<String, String> item : result.entrySet()) {
							countryCode.addItem(item.getKey(), item.getValue());
						}
						ViewUtil.installFieldRow(contentPanel,
								TEXT_CONSTANTS.selectCountry(), countryCode,
								LABEL_STYLE);
					}

				});
	}

	public Double getCentralMeridian() {
		if (centralMeridian.getText() != null
				&& centralMeridian.getText().trim() != "")
			return Double.parseDouble(centralMeridian.getText());
		else
			return null;
	}
}
