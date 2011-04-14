package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.SelectionMode;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.TerminalType;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Widget that allows the user to rerun the Access Point mapping
 * 
 * @author Christopher Fagiani
 * 
 */
public class RerunMappingWidget extends Composite implements ClickHandler {

	private static final TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private SurveyServiceAsync surveyService;
	private SurveySelectionWidget surveySelector;
	private Panel contentPanel;
	private Button runMappingButton;

	public RerunMappingWidget() {
		surveyService = GWT.create(SurveyService.class);
		contentPanel = new VerticalPanel();
		surveySelector = new SurveySelectionWidget(
				SurveySelectionWidget.Orientation.HORIZONTAL,
				TerminalType.SURVEY, SelectionMode.SINGLE);
		contentPanel.add(surveySelector);
		runMappingButton = new Button(TEXT_CONSTANTS.remapToAccessPoint());
		runMappingButton.addClickHandler(this);
		contentPanel.add(runMappingButton);
		initWidget(contentPanel);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == runMappingButton) {
			List<Long> ids = surveySelector.getSelectedSurveyIds();
			if (ids != null && ids.size() > 0) {
				surveyService.rerunAPMappings(ids.get(0),
						new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								MessageDialog dia = new MessageDialog(
										TEXT_CONSTANTS.remapToAccessPoint(),
										TEXT_CONSTANTS.remapSubmitted());
								dia.showCentered();

							}

							@Override
							public void onFailure(Throwable caught) {
								MessageDialog dia = new MessageDialog(
										TEXT_CONSTANTS.error(), TEXT_CONSTANTS
												.errorTracePrefix()
												+ " "
												+ caught.getLocalizedMessage());
								dia.showCentered();
							}
						});
			} else {
				MessageDialog dia = new MessageDialog(
						TEXT_CONSTANTS.inputError(),
						TEXT_CONSTANTS.mustSelectSurvey());
				dia.showCentered();
			}
		}
	}
}
