package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
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

	public SurveyQuestionTranslationDialog(QuestionDto dto,
			TranslationChangeListener listener) {
		setText(TITLE);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		questionDto = dto;
		this.listener = listener;
		surveyService = GWT.create(SurveyService.class);
		inputToTranslationMap = new HashMap<TextBox, TranslationDto>();
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
	}

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

		vPanel.add(grid);
		return vPanel;
	}

	private void populateTranslationControl(
			TreeMap<String, TranslationDto> translationMap, Long parentId,
			String parentType, Grid grid, int row, int startCol) {
		if (translationMap == null) {
			translationMap = new TreeMap<String, TranslationDto>();
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