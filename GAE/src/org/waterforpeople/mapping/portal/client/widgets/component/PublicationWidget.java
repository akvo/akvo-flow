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

/**
 * This is a HEADLESS (no UI) widget that will publish the survey indicated by
 * the ContextBundle. Since this widget implements AutoAdvancing, the Wizard
 * controller will load the next node in the workflow after calling the advance
 * method. Due to this behavior, WizardWorkflows that make use of this component
 * should only have a single "forward" transition coming out of the the
 * publication node.
 * 
 * @author Christopher Fagiani
 * 
 */
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
	public Map<String, Object> getContextBundle(boolean doPopulation) {
		if (bundle == null) {
			bundle = new HashMap<String, Object>();
		}
		if (doPopulation) {
			bundle.put(BundleConstants.AUTO_ADVANCE_FLAG, Boolean.TRUE);
		}
		return bundle;
	}

	@Override
	public void flushContext(){
		if(bundle!= null){
			bundle.remove(BundleConstants.AUTO_ADVANCE_FLAG);
		}
	}
	
	@Override
	public void persistContext(CompletionListener listener) {
		// no-op. This is handled by advance in this case
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		survey = (SurveyDto) bundle.get(BundleConstants.SURVEY_KEY);
		flushContext();
	}

	/**
	 * sends the publication message and then reports the status to the
	 * completion listener
	 */
	@Override
	public void advance(final CompletionListener listener) {
		surveyService.publishSurveyAsync(survey.getKeyId(),
				new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						listener.operationComplete(false, getContextBundle(true));
					}

					@Override
					public void onSuccess(Void result) {
						listener.operationComplete(true, getContextBundle(true));
					}
				});
	}
}
