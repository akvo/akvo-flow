package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.StyleUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog box used for setting all the translation items for a survey question.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyQuestionTranslationDialog extends DialogBox {

	private static final String TITLE = "Edit Translations";
	private static final String QUESTION_PARENT_TYPE = "QUESTION_TEXT";
	private static final String OPTION_PARENT_TYPE = "QUESTION_OPTION";
	private static final String HELP_PARENT_TYPE = "QUESTION_HELP_MEDIA_TEXT";

	private static final Map<String, String> LANGUAGES = new TreeMap<String, String>() {
		private static final long serialVersionUID = -5226209579099503771L;
		{
			put("French", "fr");
			put("Spanish", "es");
			put("Kinyarwanda", "rw");
		}
	};
	private QuestionDto questionDto;
	private Map<TextBox, TranslationDto> inputToTranslationMap;
	private SurveyServiceAsync surveyService;
	private TranslationChangeListener listener;
	private Label loadingLabel;
	private DockPanel contentPane;

	/**
	 * instantiates and displays the dialog box using the translations present
	 * in the questionDto. If the question has options, the translations for the
	 * options will be shown as well.
	 * 
	 * @param dto
	 * @param listener
	 */
	public SurveyQuestionTranslationDialog(QuestionDto dto,
			TranslationChangeListener listener) {
		setText(TITLE);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		questionDto = dto;
		this.listener = listener;
		surveyService = GWT.create(SurveyService.class);
		inputToTranslationMap = new HashMap<TextBox, TranslationDto>();
		contentPane = new DockPanel();
		setPopupPosition(Window.getClientWidth() / 4,
				Window.getClientHeight() / 4);
		loadingLabel = new Label("Loading...");
		contentPane.add(loadingLabel, DockPanel.CENTER);

		HorizontalPanel buttonPanel = new HorizontalPanel();

		Button ok = new Button("Save");
		Button cancel = new Button("Discard Changes");
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		contentPane.add(buttonPanel, DockPanel.SOUTH);
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				saveTranslations();
			}
		});
		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		setWidget(contentPane);
		if(dto.getTranslationMap() == null){
			surveyService.listTranslations(dto.getKeyId(), QUESTION_PARENT_TYPE, 			
				new AsyncCallback<Map<String,TranslationDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						loadingLabel.setText("Error while loading data: "
								+ caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(Map<String,TranslationDto> result) {						
						questionDto.setTranslationMap(result);
						loadHelpTranslations();
					}
				});
		}else{
			loadHelpTranslations();
		}			
	}
	
	private void loadHelpTranslations(){
		if(questionDto.getQuestionHelpList() == null){
			surveyService.listHelpByQuestion(questionDto.getKeyId(),
				new AsyncCallback<List<QuestionHelpDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						loadingLabel.setText("Error while loading data: "
								+ caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(List<QuestionHelpDto> result) {						
						questionDto.setQuestionHelpList(result);
						displayContent();
					}
				});
		}else{
			displayContent();
		}	
	}
	
	private void displayContent(){
		contentPane.remove(loadingLabel);
		contentPane.add(buildContent(), DockPanel.CENTER);
	
	}

	/**
	 * figures out which items need to be saved and calls the service to persist
	 * them. Any pre-existing translations are saved no matter what (i.e. if
	 * they have a keyId, they're updated). New translations (i.e. no key) are
	 * only created if the translation isn't blank.
	 * 
	 * After save, notify the listeners (if any) so they can update their saved
	 * list of translations if needed.
	 */
	private void saveTranslations() {
		List<TranslationDto> dtosToSave = new ArrayList<TranslationDto>();
		for (Entry<TextBox, TranslationDto> entry : inputToTranslationMap
				.entrySet()) {
			TranslationDto dto = entry.getValue();
			// if the id is present, we update the text no matter what
			if (dto.getKeyId() != null) {
				dto.setText(entry.getKey().getText());
				dtosToSave.add(dto);
			} else {
				// otherwise, we only save it if the text has been populated
				String text = entry.getKey().getText();
				if (text != null && text.trim().length() > 0) {
					dto.setText(text.trim());
					dtosToSave.add(dto);
				}
			}
		}
		surveyService.saveTranslations(dtosToSave,
				new AsyncCallback<List<TranslationDto>>() {

					@Override
					public void onSuccess(List<TranslationDto> result) {
						if (listener != null) {
							listener.translationsUpdated(result);
						}
						hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(
								"Error saving translations",
								"The application could not save the translation values. Please try again. If the problem persists, contact an administrator.");
						errDia.showRelativeTo(getWidget());
					}
				});
	}

	/**
	 * builds the UI. Right now, all translations (for all supported languages)
	 * are shown in a grid. This may need to be refactored if we support a lot
	 * of languages (unless all languages are to be used for a survey).
	 * 
	 * @return
	 */
	private Widget buildContent() {
		VerticalPanel vPanel = new VerticalPanel();
		int rowCount = 2;
		int colCount = LANGUAGES.size() + 1;
		List<QuestionOptionDto> options = null;
		if (questionDto.getOptionContainerDto() != null
				&& questionDto.getOptionContainerDto().getOptionsList() != null
				&& questionDto.getOptionContainerDto().getOptionsList().size() > 0) {
			options = questionDto.getOptionContainerDto().getOptionsList();
			rowCount += (1 + options.size());
		}
		if (questionDto.getQuestionHelpList() != null) {
			rowCount += questionDto.getQuestionHelpList().size();
		}
		Grid grid = new Grid(rowCount, colCount);
		// set up the headers
		grid.setWidget(0, 0, new Label("Text"));
		int curCol = 1;
		for (String lang : LANGUAGES.keySet()) {
			grid.setWidget(0, curCol++, new Label(lang));
		}
		StyleUtil.setGridRowStyle(grid, 0, false);
		grid.setWidget(1, 0, new Label(questionDto.getText()));
		populateTranslationControl(questionDto.getTranslationMap(), questionDto
				.getKeyId(), QUESTION_PARENT_TYPE, grid, 1, 1);
		StyleUtil.setGridRowStyle(grid, 1, false);
		int curRow = 2;
		if (options != null) {
			for (QuestionOptionDto opt : options) {
				grid.setWidget(curRow, 0, new Label(opt.getText()));
				populateTranslationControl(opt.getTranslationMap(), opt
						.getKeyId(), OPTION_PARENT_TYPE, grid, curRow, 1);
				StyleUtil.setGridRowStyle(grid, curRow++, false);
			}
		}
		if (questionDto.getQuestionHelpList() != null) {
			for (QuestionHelpDto help : questionDto.getQuestionHelpList()) {
				grid.setWidget(curRow, 0, new Label(help.getText()));
				populateTranslationControl(help.getTranslationMap(), help
						.getKeyId(), HELP_PARENT_TYPE, grid, curRow, 1);
				StyleUtil.setGridRowStyle(grid, curRow++, false);
			}
		}

		vPanel.add(grid);
		return vPanel;
	}

	/**
	 * constructs a text box for each supported lanaguge and, if there is
	 * already a translation present for the question dto, populates the current
	 * text in the control.
	 * 
	 * @param translationMap
	 * @param parentId
	 * @param parentType
	 * @param grid
	 * @param row
	 * @param startCol
	 */
	@SuppressWarnings("unchecked")
	private void populateTranslationControl(
			Map<String, TranslationDto> translationMap, Long parentId,
			String parentType, Grid grid, int row, int startCol) {
		if (translationMap == null) {
			translationMap = new TreeMap<String, TranslationDto>();
		} else if (!(translationMap instanceof TreeMap)) {
			translationMap = new TreeMap<String, TranslationDto>(translationMap);
		}
		for (String lang : LANGUAGES.values()) {
			TextBox inputBox = new TextBox();
			grid.setWidget(row, startCol++, inputBox);
			TranslationDto trans = translationMap.get(lang);
			if (trans != null) {
				inputBox.setValue(trans.getText());
			} else {
				trans = new TranslationDto();
				trans.setParentId(parentId);
				trans.setParentType(parentType);
				trans.setLangCode(lang);
			}
			inputToTranslationMap.put(inputBox, trans);
		}
	}

	/**
	 * allow the user to press escape to close
	 */
	@Override
	public boolean onKeyDownPreview(char key, int modifiers) {
		switch (key) {
		case KeyCodes.KEY_ESCAPE:
			hide();
			return true;
		}
		return false;
	}
}