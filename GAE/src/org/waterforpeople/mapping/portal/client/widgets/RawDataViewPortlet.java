package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
//import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceService;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceServiceAsync;
import org.waterforpeople.mapping.portal.client.widgets.component.DataTableBinder;
import org.waterforpeople.mapping.portal.client.widgets.component.DataTableListener;
import org.waterforpeople.mapping.portal.client.widgets.component.PaginatedDataTable;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Portlet that allows the user to browse and edit the last 90 days of survey
 * submissions.
 * 
 * @author Christopher Fagiani
 * 
 */
public class RawDataViewPortlet extends LocationDrivenPortlet implements
		DataTableBinder<SurveyInstanceDto>,
		DataTableListener<SurveyInstanceDto> {
	public static final String NAME = "Raw Data Manager";
	public static final String DESCRIPTION = "Allows the management of raw imported survey data";
	private static final String EDITED_ROW_CSS = "gridCell-edited";

	private static Integer width = 1024;
	private static Integer height = 768;
	private static final String TABLE_HEADERS[] = { "Submission Id",
			"Survey Id", "Collection Date" };

	private SurveyInstanceServiceAsync svc;
	private Grid qasDetailGrid;
	private Label statusLabel;
	private Date dateForQuery;

	private VerticalPanel surveyInstancePanel;
	private PaginatedDataTable<SurveyInstanceDto> surveyInstanceTable;
	private HorizontalPanel finderPanel;
	private TextBox instanceIdBox;
	private HorizontalPanel contentPanel;
	private Map<Long, QuestionAnswerStoreDto> changedAnswers;

	public RawDataViewPortlet() {
		super(NAME, true, false, width, height, null, false, null);
		svc = GWT.create(SurveyInstanceService.class);
		loadContentPanel();
	}

	public String getDescription() {
		return DESCRIPTION;
	}

	private void loadContentPanel() {
		finderPanel = new HorizontalPanel();
		instanceIdBox = new TextBox();
		finderPanel.add(new Label("Instance Id: "));
		finderPanel.add(instanceIdBox);
		Button findButton = new Button("Find");
		finderPanel.add(findButton);
		findButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (instanceIdBox.getText() != null
						&& instanceIdBox.getText().trim().length() > 0) {
					loadInstanceResponses(new Long(instanceIdBox.getText()
							.trim()));
				}
			}
		});
		surveyInstanceTable = new PaginatedDataTable<SurveyInstanceDto>(
				"Collection Date", this, this, true);
		qasDetailGrid = new Grid();
		contentPanel = new HorizontalPanel();

		surveyInstancePanel = new VerticalPanel();
		surveyInstancePanel.add(finderPanel);
		contentPanel.add(surveyInstancePanel);
		contentPanel.add(qasDetailGrid);
		statusLabel = new Label();
		surveyInstancePanel.add(statusLabel);
		surveyInstancePanel.add(surveyInstanceTable);

		requestData(null);
		ScrollPanel sp = new ScrollPanel(contentPanel);
		sp.setHeight(height.toString());
		setWidget(sp);
	}

	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * loads the questions passed in into the view and sets them up with
	 * listeners that will change the UI state on edit.
	 * 
	 * @param questions
	 */
	private void populateQuestions(final List<QuestionAnswerStoreDto> questions) {
		statusLabel.setVisible(false);
		changedAnswers = new HashMap<Long, QuestionAnswerStoreDto>();
		if (questions != null) {
			qasDetailGrid.resize(questions.size() + 2, 4);
			qasDetailGrid.setWidget(0, 0, new Label("Question Id"));
			qasDetailGrid.setWidget(0, 1, new Label("Question Type"));
			qasDetailGrid.setWidget(0, 2, new Label("Answer Value"));
			qasDetailGrid.setWidget(0, 3, new Label("Collection Date"));
			Integer iRow = 0;
			for (QuestionAnswerStoreDto qasDto : questions) {
				bindQASRow(qasDto, ++iRow);
			}
			final Button saveButton = new Button("Save Changes");
			saveButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (changedAnswers != null && changedAnswers.size() > 0) {
						statusLabel.setText("Saving. Please wait...");
						statusLabel.setVisible(true);
						svc
								.updateQuestions(
										new ArrayList<QuestionAnswerStoreDto>(
												changedAnswers.values()),
										new AsyncCallback<List<QuestionAnswerStoreDto>>() {

											@Override
											public void onFailure(
													Throwable caught) {
												MessageDialog errDia = new MessageDialog(
														"Application Error",
														"Cannot update responses");
												errDia
														.showRelativeTo(saveButton);
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
														for (int i = 0; i < questions
																.size(); i++) {
															if (questions
																	.get(i)
																	.getKeyId()
																	.equals(
																			dto
																					.getKeyId())) {
																questions
																		.get(i)
																		.setValue(
																				dto
																						.getValue());
															}
														}
													}
												}
												populateQuestions(questions);
											}
										});
					}
				}
			});
			Button clearButton = new Button("Clear Changes");
			clearButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					populateQuestions(questions);
				}
			});
			qasDetailGrid.setWidget(iRow + 1, 0, saveButton);
			qasDetailGrid.setWidget(iRow + 1, 1, clearButton);
		}
	}

	/**
	 * binds a questionAnswerStoreDto object to the ui
	 * 
	 * @param qasDto
	 * @param iRow
	 */
	private void bindQASRow(final QuestionAnswerStoreDto qasDto,
			final Integer iRow) {
		TextBox qId = new TextBox();
		qId.setReadOnly(true);
		qId.setTabIndex(-1);
		TextBox qType = new TextBox();
		qType.setReadOnly(true);
		qType.setTabIndex(-1);
		TextBox qValue = new TextBox();
		qValue.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				String oldVal = qasDto.getValue();
				String newVal = ((TextBox) event.getSource()).getValue();
				if (!newVal.trim().equals(oldVal)) {
					qasDetailGrid.getCellFormatter().setStyleName(iRow, 2,
							EDITED_ROW_CSS);
					// create a new copy of the answer so we don't overwrite the
					// old value
					QuestionAnswerStoreDto newAnswer = new QuestionAnswerStoreDto();
					newAnswer.setKeyId(qasDto.getKeyId());
					newAnswer.setArbitratyNumber(qasDto.getArbitratyNumber());
					newAnswer.setCollectionDate(qasDto.getCollectionDate());
					newAnswer.setType(qasDto.getType());
					newAnswer.setQuestionID(qasDto.getQuestionID());
					newAnswer.setValue(newVal.trim());
					newAnswer.setSurveyId(qasDto.getSurveyId());
					newAnswer.setSurveyInstanceId(qasDto.getSurveyInstanceId());
					newAnswer.setOldValue(qasDto.getValue());
					changedAnswers.put(newAnswer.getKeyId(), newAnswer);

				} else {
					qasDetailGrid.getCellFormatter().setStyleName(iRow, 2, "");
					changedAnswers.remove(qasDto.getKeyId());
				}

			}
		});
		TextBox qCollectionDate = new TextBox();
		qCollectionDate.setReadOnly(true);
		qCollectionDate.setTabIndex(-1);
		if (qasDto != null) {
			if (qasDto.getKeyId() != null)
				qId.setText(qasDto.getQuestionID());
			if (qasDto.getValue() != null)
				qValue.setText(qasDto.getValue());
			if (qasDto.getType() != null)
				qType.setText(qasDto.getType());
			if (qasDto.getCollectionDate() != null)
				qCollectionDate.setText(DateTimeFormat.getMediumDateFormat()
						.format(qasDto.getCollectionDate()));

		}
		qasDetailGrid.setWidget(iRow, 0, qId);
		qasDetailGrid.setWidget(iRow, 1, qType);
		qasDetailGrid.setWidget(iRow, 2, qValue);
		qasDetailGrid.setWidget(iRow, 3, qCollectionDate);
		for (int j = 0; j < qasDetailGrid.getCellCount(iRow); j++) {
			qasDetailGrid.getCellFormatter().setStyleName(iRow, j, "");
		}
	}

	/**
	 * calls the server to get back instance response details
	 * 
	 * @param instanceId
	 */
	private void loadInstanceResponses(Long instanceId) {
		statusLabel.setText("Loading responses. Please wait...");
		statusLabel.setVisible(true);
		svc.listQuestionsByInstance(instanceId,
				new AsyncCallback<List<QuestionAnswerStoreDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						// no-op
					}

					@Override
					public void onSuccess(List<QuestionAnswerStoreDto> result) {
						populateQuestions(result);
					}
				});
	}

	/**
	 * binds a SurveyInstanceDto to the grid at the row passed in.
	 */
	@Override
	public void bindRow(Grid grid, SurveyInstanceDto item, int row) {
		grid.setWidget(row, 0, new Label(item.getKeyId().toString()));
		grid.setWidget(row, 1, new Label(item.getSurveyId().toString()));
		grid.setWidget(row, 2, new Label(DateTimeFormat
				.getMediumDateTimeFormat().format(item.getCollectionDate())));
	}

	@Override
	public String[] getHeaders() {

		return TABLE_HEADERS;
	}

	@Override
	public void onItemSelected(SurveyInstanceDto item) {
		loadInstanceResponses(item.getKeyId());
	}

	@Override
	public void resort(String field, String direction) {
		// no-op. We don't support sorting in this view
	}

	/**
	 * call the server to get more data. We need to cache the date used in the
	 * query or else it won't match the cursor on subsequent requests (since
	 * sysdate is different)
	 */
	@Override
	public void requestData(String cursor) {
		final boolean isNew = (cursor == null);
		if (isNew) {
			//Calendar c = Calendar.getInstance();
			//c.add(Calendar.DAY_OF_MONTH, -90);
			//dateForQuery = c.getTime();
		}
		svc.listSurveyInstance(dateForQuery, cursor,
				new AsyncCallback<ResponseDto<ArrayList<SurveyInstanceDto>>>() {

					@Override
					public void onFailure(Throwable caught) {
						// no-op
					}

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<SurveyInstanceDto>> result) {
						surveyInstanceTable.bindData(result.getPayload(),
								result.getCursorString(), isNew);
					}
				});
	}
}
