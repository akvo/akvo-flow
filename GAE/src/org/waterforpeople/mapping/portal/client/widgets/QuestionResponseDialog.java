package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceService;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * dialog box for viewing and editing responses to a single question
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionResponseDialog extends DialogBox implements ClickHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static final String EVEN_ROW_CSS = "gridCell-even";
	private static final String ODD_ROW_CSS = "gridCell-odd";
	private static final String EDITED_ROW_CSS = "gridCell-edited";
	private static final String SELECTED_ROW_CSS = "gridCell-selected";
	private static final String GRID_HEADER_CSS = "gridCell-header";
	private List<String> cursorArray;
	private int currentPage;
	private Grid dataGrid;
	private Label statusLabel;
	private SurveyInstanceServiceAsync surveyInstanceSvc;
	private Long questionId;
	private Button nextButton;
	private Button previousButton;
	private Button saveButton;
	private Button clearButton;
	private Button doneButton;
	private VerticalPanel contentPanel;
	private HorizontalPanel paginationPanel;
	private Map<Long, QuestionAnswerStoreDto> changedAnswers;
	private List<QuestionAnswerStoreDto> currentAnswers;
	private List<TextBox> currentTextboxes;
	private static final DateTimeFormat DATE_FMT = DateTimeFormat
			.getFormat(DateTimeFormat.PredefinedFormat.DATE_SHORT);

	public QuestionResponseDialog(Long questionId) {
		super();
		this.questionId = questionId;
		// Set the dialog box's caption.
		setText(TEXT_CONSTANTS.questionResponses());
		setAnimationEnabled(true);
		setGlassEnabled(true);
		currentAnswers = new ArrayList<QuestionAnswerStoreDto>();
		changedAnswers = new HashMap<Long, QuestionAnswerStoreDto>();
		currentTextboxes = new ArrayList<TextBox>();
		cursorArray = new ArrayList<String>();
		statusLabel = new Label();
		surveyInstanceSvc = GWT.create(SurveyInstanceService.class);
		nextButton = new Button(TEXT_CONSTANTS.next());
		nextButton.setVisible(true);
		currentPage = 0;
		nextButton.addClickHandler(this);
		previousButton = new Button(TEXT_CONSTANTS.previous());
		previousButton.setVisible(false);
		previousButton.addClickHandler(this);
		paginationPanel = new HorizontalPanel();
		paginationPanel.add(previousButton);
		paginationPanel.add(nextButton);
		dataGrid = new Grid();
		contentPanel = new VerticalPanel();
		contentPanel.add(statusLabel);
		contentPanel.add(dataGrid);
		doneButton = new Button(TEXT_CONSTANTS.done());
		saveButton = new Button(TEXT_CONSTANTS.save());
		saveButton.addClickHandler(this);
		saveButton.setEnabled(false);
		clearButton = new Button(TEXT_CONSTANTS.discardChanges());
		clearButton.addClickHandler(this);
		clearButton.setEnabled(false);
		contentPanel.add(doneButton);
		doneButton.addClickHandler(this);
		setWidget(new ScrollPanel(contentPanel));
		loadResponses();
		setPopupPosition(Window.getClientWidth() / 5,
				Window.getClientHeight() / 5);
	}

	@Override
	public boolean onKeyDownPreview(char key, int modifiers) {
		switch (key) {
		case KeyCodes.KEY_ESCAPE:
			dismissDialog();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(final ClickEvent event) {
		if (event.getSource() == nextButton) {
			currentPage++;
			loadResponses();
		} else if (event.getSource() == previousButton) {
			currentPage--;
			loadResponses();
		} else if (event.getSource() == clearButton) {
			populateData(currentAnswers);
		} else if (event.getSource() == saveButton) {
			if (changedAnswers != null && changedAnswers.size() > 0) {
				statusLabel.setText(TEXT_CONSTANTS.pleaseWait());
				statusLabel.setVisible(true);
				surveyInstanceSvc.updateQuestions(
						new ArrayList<QuestionAnswerStoreDto>(changedAnswers
								.values()), true,
						new AsyncCallback<List<QuestionAnswerStoreDto>>() {

							@Override
							public void onFailure(Throwable caught) {
								MessageDialog errDia = new MessageDialog(
										TEXT_CONSTANTS.error(), TEXT_CONSTANTS
												.errorTracePrefix()
												+ " "
												+ caught.getLocalizedMessage());
								errDia.showCentered();
								statusLabel.setVisible(false);
							}

							@Override
							public void onSuccess(
									List<QuestionAnswerStoreDto> result) {
								statusLabel.setVisible(false);
								if (result != null) {
									// update the value in the
									// questionsList so we can
									// keep the data consistent
									// if the user presses clear
									for (QuestionAnswerStoreDto dto : result) {
										for (int i = 0; i < currentAnswers
												.size(); i++) {
											if (currentAnswers.get(i)
													.getKeyId().equals(
															dto.getKeyId())) {
												currentAnswers.get(i).setValue(
														dto.getValue());
											}
										}
									}
								}
								populateData(currentAnswers);
							}
						});
			}
		} else if (event.getSource() == doneButton) {
			dismissDialog();
		}
	}

	/**
	 * sets the enabled flag of the saveButton and clearButton
	 * 
	 * @param enabled
	 */
	private void updateSaveButtonStatus(boolean enabled) {
		saveButton.setEnabled(enabled);
		clearButton.setEnabled(enabled);
	}

	/**
	 * clears all cached objects (to free memory) and hides the dialog
	 */
	private void dismissDialog() {
		currentAnswers.clear();
		changedAnswers.clear();
		cursorArray.clear();
		hide();
	}

	private void loadResponses() {
		statusLabel.setText(TEXT_CONSTANTS.pleaseWait());
		statusLabel.setVisible(true);

		surveyInstanceSvc
				.listResponsesByQuestion(
						questionId,
						getCursor(currentPage - 1),
						new AsyncCallback<ResponseDto<ArrayList<QuestionAnswerStoreDto>>>() {
							@Override
							public void onFailure(Throwable caught) {
								MessageDialog errDia = new MessageDialog(
										TEXT_CONSTANTS.error(), TEXT_CONSTANTS
												.errorTracePrefix()
												+ " "
												+ caught.getLocalizedMessage());
								errDia.showCentered();
							}

							@Override
							public void onSuccess(
									ResponseDto<ArrayList<QuestionAnswerStoreDto>> result) {
								currentAnswers = result.getPayload();
								setCursor(result.getCursorString());
								populateData(result.getPayload());
							}
						});
	}

	private void populateData(final List<QuestionAnswerStoreDto> data) {
		dataGrid.clear(true);
		updateSaveButtonStatus(false);
		if (data != null && data.size() > 0) {
			statusLabel.setVisible(false);
			dataGrid.resize(data.size() + 2, 3);
			loadHeaderRow();
			for (int i = 0; i < data.size(); i++) {
				final int idx = i;
				dataGrid.setWidget(i + 1, 0, new Label(data.get(i)
						.getSurveyInstanceId().toString()));
				TextBox temp = new TextBox();
				temp.setText(data.get(i).getValue());
				currentTextboxes.add(temp);
				temp.addChangeHandler(new ChangeHandler() {

					@Override
					public void onChange(ChangeEvent event) {
						String oldVal = data.get(idx).getValue();
						String newVal = ((TextBox) event.getSource())
								.getValue();
						if (!newVal.trim().equals(oldVal)) {
							dataGrid.getCellFormatter().setStyleName(idx + 1,
									1, EDITED_ROW_CSS);
							// create a new copy of the answer so we don't
							// overwrite the old value
							QuestionAnswerStoreDto newAnswer = new QuestionAnswerStoreDto();
							newAnswer.setKeyId(data.get(idx).getKeyId());
							newAnswer.setArbitratyNumber(data.get(idx)
									.getArbitratyNumber());
							newAnswer.setCollectionDate(data.get(idx)
									.getCollectionDate());
							newAnswer.setType(data.get(idx).getType());
							newAnswer.setQuestionID(data.get(idx)
									.getQuestionID());
							newAnswer.setValue(newVal.trim());
							newAnswer.setSurveyId(data.get(idx).getSurveyId());
							newAnswer.setSurveyInstanceId(data.get(idx)
									.getSurveyInstanceId());
							newAnswer.setOldValue(data.get(idx).getValue());
							changedAnswers.put(newAnswer.getKeyId(), newAnswer);

						} else {
							dataGrid.getCellFormatter().setStyleName(idx + 1,
									1, "");
							changedAnswers.remove(data.get(idx).getKeyId());
						}
						if (changedAnswers.size() > 0) {
							updateSaveButtonStatus(true);
						} else {
							updateSaveButtonStatus(false);
						}
					}
				});
				dataGrid.setWidget(i + 1, 1, temp);
				dataGrid.getCellFormatter().setStyleName(idx + 1, 1, "");
				dataGrid.setWidget(i + 1, 2, new Label(DATE_FMT.format(data
						.get(i).getCollectionDate())));
			}

			if (data.size() >= 20) {
				nextButton.setVisible(true);
			} else {
				nextButton.setVisible(false);
			}
			if (currentPage > 0) {
				previousButton.setVisible(true);
			} else {
				previousButton.setVisible(false);
			}
			dataGrid.setWidget(data.size() + 1, 0, paginationPanel);
			dataGrid.setWidget(data.size() + 1, 1, saveButton);
			dataGrid.setWidget(data.size() + 1, 2, clearButton);

		} else {
			dataGrid.resize(1, 1);
			statusLabel.setText(TEXT_CONSTANTS.noResponsesForQuestion());
			statusLabel.setVisible(true);
		}
	}

	private String getCursor(int page) {
		if (page >= 0) {
			if (page < cursorArray.size()) {
				if (cursorArray.get(page) != null
						&& cursorArray.get(page).trim().length() == 0) {
					return null;
				} else {
					return cursorArray.get(page);
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private void setCursor(String cursor) {
		if (currentPage < cursorArray.size()) {
			cursorArray.set(currentPage, cursor);
		} else {
			cursorArray.add(cursor);
		}
	}

	private void loadHeaderRow() {
		addHeaderItem(0, TEXT_CONSTANTS.id());
		addHeaderItem(1, TEXT_CONSTANTS.value());
		addHeaderItem(2, TEXT_CONSTANTS.collectionDate());
		setGridRowStyle(dataGrid, 0, false);
	}

	private void addHeaderItem(int col, final String text) {
		HorizontalPanel panel = new HorizontalPanel();
		Label temp = new Label(text);
		panel.add(temp);
		dataGrid.setWidget(0, col, panel);
	}

	/**
	 * sets the css for a row in a grid. the top row will get the header style
	 * and other rows get either the even or odd style.
	 * 
	 * @param grid
	 * @param row
	 * @param selected
	 */
	private void setGridRowStyle(Grid grid, int row, boolean selected) {
		String style = "";
		if (row > 0) {
			if (selected) {
				style = SELECTED_ROW_CSS;
			} else {
				if (row % 2 == 0) {
					style = EVEN_ROW_CSS;
				} else {
					style = ODD_ROW_CSS;
				}
			}
		} else {
			style = GRID_HEADER_CSS;
		}
		for (int i = 0; i < grid.getColumnCount(); i++) {
			grid.getCellFormatter().setStyleName(row, i, style);
		}
	}

}