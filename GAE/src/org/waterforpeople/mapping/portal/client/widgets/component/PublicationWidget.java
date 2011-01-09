package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.wizard.client.AutoAdvancing;
import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PublicationWidget extends Composite implements ContextAware,
		AutoAdvancing {

	private VerticalPanel panel;
	private Label statusLabel;
	private Map<String, Object> bundle;
	private SurveyServiceAsync surveyService;
	private SurveyDto survey;

	public PublicationWidget() {
		surveyService = GWT.create(SurveyService.class);
		panel = new VerticalPanel();
		statusLabel = new Label("Publishing. Please wait");
		panel.add(statusLabel);
		initWidget(panel);
	}

	@Override
	public Map<String, Object> getContextBundle() {
		if (bundle == null) {
			bundle = new HashMap<String, Object>();
		}
		bundle.put(BundleConstants.AUTO_ADVANCE_FLAG,Boolean.TRUE);
		return bundle;
	}

	@Override
	public void persistContext(CompletionListener listener) {

	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		survey = (SurveyDto) bundle.get(BundleConstants.SURVEY_KEY);
	}

	@Override
	public void advance(final CompletionListener listener) {
		surveyService.publishSurveyAsync(survey.getKeyId(),
				new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						listener.operationComplete(false, getContextBundle());
					}

					@Override
					public void onSuccess(Void result) {
						listener.operationComplete(true, getContextBundle());
					}
				});
	}
}
