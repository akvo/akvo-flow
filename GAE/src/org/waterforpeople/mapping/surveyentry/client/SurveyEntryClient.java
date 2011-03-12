package org.waterforpeople.mapping.surveyentry.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.auth.WebActivityAuthorizationDto;
import org.waterforpeople.mapping.app.gwt.client.auth.WebActivityAuthorizationService;
import org.waterforpeople.mapping.app.gwt.client.auth.WebActivityAuthorizationServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.surveyentry.client.component.SurveyEntryWidget;
import org.waterforpeople.mapping.surveyentry.client.component.WebSurveySelectorDialog;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Web-based client for responding to surveys. This page expects the surveyId as
 * a query parameter (sid).
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyEntryClient implements EntryPoint, CompletionListener {

	private static final String TOKEN_PARAM = "token";
	private static final String ACTIVITY_NAME = "WebSurvey";
	private SurveyEntryWidget entryWidget;
	private WebActivityAuthorizationServiceAsync authService;
	private WebActivityAuthorizationDto currentAuth;

	@Override
	public void onModuleLoad() {

		authService = GWT.create(WebActivityAuthorizationService.class);
		String token = Window.Location.getParameter(TOKEN_PARAM);
		RootPanel.get().setPixelSize(1024, 768);
		RootPanel.get().getElement().getStyle().setProperty("position",
				"relative");
		RootPanel.get().add(new Image("images/wfp-logo.gif"));
		final MessageDialog authDia = new MessageDialog("Authenticating...",
				"Validating authentication. Please wait", true);
		authDia.showCentered();
		if (token != null) {
			authService.isAuthorized(token, ACTIVITY_NAME,
					new AsyncCallback<WebActivityAuthorizationDto>() {

						@Override
						public void onSuccess(WebActivityAuthorizationDto result) {
							authDia.hide();
							if (result != null) {

								authorizationComplete(result);
							} else {
								authorizationFailed();
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							MessageDialog dia = new MessageDialog("Error",
									"Could not validate authentication: "
											+ caught.getLocalizedMessage());
							dia.showCentered();
						}
					});
		} else if (Window.Location.getPath() != null
				&& Window.Location.getPath().toLowerCase().contains("secure")) {
			authService
					.listUserAuthorizations(
							ACTIVITY_NAME,
							new AsyncCallback<ResponseDto<HashMap<BaseDto, WebActivityAuthorizationDto>>>() {

								@Override
								public void onFailure(Throwable caught) {
									MessageDialog dia = new MessageDialog(
											"Error",
											"Could not validate authentication: "
													+ caught
															.getLocalizedMessage());
									dia.showCentered();
								}

								@Override
								public void onSuccess(
										final ResponseDto<HashMap<BaseDto, WebActivityAuthorizationDto>> result) {
									authDia.hide();
									if (result.getPayload() != null
											&& result.getPayload().size() > 0) {
										if (result.getPayload().size() > 1) {
											ArrayList<SurveyDto> surveys = new ArrayList<SurveyDto>();
											for (BaseDto d : result
													.getPayload().keySet()) {
												if (d instanceof SurveyDto) {
													surveys.add((SurveyDto) d);
												}
											}
											WebSurveySelectorDialog selector = new WebSurveySelectorDialog(
													surveys,
													new CompletionListener() {

														@Override
														public void operationComplete(
																boolean wasSuccessful,
																Map<String, Object> payload) {
															SurveyDto s = (SurveyDto) payload
																	.get(WebSurveySelectorDialog.SELECTED_SURVEY_KEY);
															if (s != null) {
																authorizationComplete(result
																		.getPayload()
																		.get(s));
															}

														}
													});
											selector.showCentered();
										} else {
											for (Entry<BaseDto, WebActivityAuthorizationDto> e : result
													.getPayload().entrySet()) {
												if (e.getValue() != null) {
													authorizationComplete(e
															.getValue());
													break;
												}
											}
										}
									} else {
										MessageDialog dia = new MessageDialog(
												"No Surveys",
												"You do not have access to submit any surveys at this time");
										dia.showCentered();
									}
								}
							});
		}
	}

	private void authorizationComplete(WebActivityAuthorizationDto auth) {
		currentAuth = auth;
		entryWidget = new SurveyEntryWidget(auth.getPayload());
		entryWidget.setListener(this);
		RootPanel.get().add(entryWidget);
		entryWidget.initialize();
	}

	private void authorizationFailed() {
		Label l = new Label("Authorization failed. Please check your token.");
		RootPanel.get().add(l);
	}

	@Override
	public void operationComplete(boolean wasSuccessful,
			Map<String, Object> payload) {
		if (currentAuth != null) {
			Long count = currentAuth.getUsageCount();
			if (count == null) {
				count = new Long(1);
			} else {
				count = count + 1;
			}
			currentAuth.setUsageCount(count);
			authService.saveAuthorization(currentAuth,
					new AsyncCallback<WebActivityAuthorizationDto>() {

						@Override
						public void onFailure(Throwable caught) {
							// no-op
						}

						@Override
						public void onSuccess(WebActivityAuthorizationDto result) {
							if (result != null) {
								if (!result.isValidForAuth()) {
									entryWidget.setVisible(false);
									MessageDialog dia = new MessageDialog(
											"Thank you",
											"Thank you for your submission.");
									dia.showCentered();
								}
							}
						}
					});
		}
	}
}
