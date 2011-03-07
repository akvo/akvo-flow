package org.waterforpeople.mapping.surveyentry.client;

import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.auth.WebActivityAuthorizationDto;
import org.waterforpeople.mapping.app.gwt.client.auth.WebActivityAuthorizationService;
import org.waterforpeople.mapping.app.gwt.client.auth.WebActivityAuthorizationServiceAsync;
import org.waterforpeople.mapping.surveyentry.client.component.SurveyEntryWidget;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
		final MessageDialog authDia = new MessageDialog(
				"Authenticating...", "Validating authentication. Please wait",true);
		authDia.showCentered();
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
