package org.waterforpeople.mapping.portal.client.widgets.component;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog box used for adding help media to survey questions
 * 
 * @author Christopher Fagiani
 *
 */
public class QuestionHelpDialog extends DialogBox {
	private static final String TITLE = "Edit Help Media";	
	private QuestionDto questionDto;
	private SurveyServiceAsync surveyService;
	private CompletionListener listener;
	
	
	
	/**
	 * instantiates and displays the dialog box using the translations present
	 * in the questionDto. If the question has options, the translations for the
	 * options will be shown as well.
	 * 
	 * @param dto
	 * @param listener
	 */
	public QuestionHelpDialog(QuestionDto dto,
			CompletionListener listener) {
		setText(TITLE);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		questionDto = dto;
		this.listener = listener;
		surveyService = GWT.create(SurveyService.class);
		
		DockPanel contentPane = new DockPanel();
		setPopupPosition(Window.getClientWidth() / 4,
				Window.getClientHeight() / 4);
		contentPane.add(buildContent(), DockPanel.CENTER);

		HorizontalPanel buttonPanel = new HorizontalPanel();

		Button ok = new Button("Save");
		Button cancel = new Button("Discard Changes");
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		contentPane.add(buttonPanel, DockPanel.SOUTH);
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				saveHelp();
			}
		});
		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		setWidget(contentPane);
	}
	
	private void saveHelp(){
		//TODO: implement
	}
	
	/**
	 * builds the UI. 
	 * 
	 * @return
	 */
	private Widget buildContent() {
		VerticalPanel vPanel = new VerticalPanel();
		HelpMediaWidget helpWidget = new HelpMediaWidget(null);
		vPanel.add(helpWidget);
		
		return vPanel;
	}

}
