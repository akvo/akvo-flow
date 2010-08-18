package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceService;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceServiceAsync;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class RawDataViewPortlet extends LocationDrivenPortlet implements
		ClickHandler {
	public static final String NAME = "Raw Data Manager";
	public static final String DESCRIPTION = "Allows the management of raw imported survey data";
	private static final String EVEN_ROW_CSS = "gridCell-even";
	private static final String ODD_ROW_CSS = "gridCell-odd";
	private static final String SELECTED_ROW_CSS = "gridCell-selected";
	private static final String GRID_HEADER_CSS = "gridCell-header";

	private static Integer width = 1024;
	private static Integer height = 768;
	private SurveyInstanceServiceAsync svc;
	private Grid qasDetailGrid;
	private Grid instanceGrid;
	private Label statusLabel;
	private ArrayList<SurveyInstanceDto> currentDtoList;

	private int currentSelection = -1;
	private int currentPage;
	private VerticalPanel surveyInstancePanel;
	private Button nextButton;
	private Button previousButton;
	private HorizontalPanel contentPanel;
	private List<String> cursorArray;

	public RawDataViewPortlet() {
		super(NAME, true, false, width, height, null, false, null);
		currentPage = 0;
		cursorArray = new ArrayList<String>();
		svc = GWT.create(SurveyInstanceService.class);
		loadContentPanel();
	}

	public String getDescription() {
		return DESCRIPTION;
	}

	private void loadContentPanel() {
		instanceGrid = new Grid();
		instanceGrid.addClickHandler(this);
		qasDetailGrid = new Grid();
		contentPanel = new HorizontalPanel();
		surveyInstancePanel = new VerticalPanel();

		contentPanel.add(surveyInstancePanel);
		contentPanel.add(qasDetailGrid);
		statusLabel = new Label();
		surveyInstancePanel.add(statusLabel);
		surveyInstancePanel.add(instanceGrid);
		HorizontalPanel buttonPanel = new HorizontalPanel();
		nextButton = new Button("Next");
		nextButton.setVisible(false);
		nextButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				currentPage++;
				loadSurveyInstance(false);

			}
		});

		previousButton = new Button("Previous");
		previousButton.setVisible(false);
		previousButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				currentPage--;
				loadSurveyInstance(false);

			}
		});
		buttonPanel.add(previousButton);
		buttonPanel.add(nextButton);
		surveyInstancePanel.add(buttonPanel);

		loadSurveyInstance(true);
		setWidget(contentPanel);
	}

	@Override
	public String getName() {
		return NAME;
	}

	private void loadSurveyInstance(boolean isNew) {
		statusLabel.setText("Loading survey submissions. Please wait...");
		statusLabel.setVisible(true);
		svc.listSurveyInstance(null, getCursor(currentPage - 1),
				new AsyncCallback<ResponseDto<ArrayList<SurveyInstanceDto>>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<SurveyInstanceDto>> result) {
						statusLabel.setVisible(false);
						instanceGrid.clear();
						if (result != null && result.getPayload() != null) {
							instanceGrid.resize(result.getPayload().size() + 1,
									3);
							loadHeaderRow();
							setCursor(result.getCursorString());
							int count = 1;
							currentDtoList = result.getPayload();
							for (SurveyInstanceDto item : result.getPayload()) {
								instanceGrid.setWidget(count, 0, new Label(item
										.getKeyId().toString()));
								instanceGrid.setWidget(count, 1, new Label(item
										.getSurveyId().toString()));
								instanceGrid
										.setWidget(
												count,
												2,
												new Label(
														DateTimeFormat
																.getMediumDateTimeFormat()
																.format(
																		item
																				.getCollectionDate())));
								setGridRowStyle(instanceGrid, count, false);
								count++;
							}
							if(currentDtoList.size()>=20){
								nextButton.setVisible(true);
							}else{
								nextButton.setVisible(false);
							}
							if(currentPage>0){
								previousButton.setVisible(true);
							}else{
								previousButton.setVisible(false);
							}
						}else{
							nextButton.setVisible(false);
							previousButton.setVisible(false);
						}
					}

				});
	}

	private void populateQuestions(List<QuestionAnswerStoreDto> questions) {
		statusLabel.setVisible(false);
		if (questions != null) {
			qasDetailGrid.resize(questions.size() + 1, 4);
			qasDetailGrid.setWidget(0, 0, new Label("Question Id"));
			qasDetailGrid.setWidget(0, 1, new Label("Question Type"));
			qasDetailGrid.setWidget(0, 2, new Label("Answer Value"));
			qasDetailGrid.setWidget(0, 3, new Label("Collection Date"));
			Integer iRow = 0;
			for (QuestionAnswerStoreDto qasDto : questions) {
				bindQASRow(qasDto, ++iRow);
			}
		}
	}

	private void bindQASRow(QuestionAnswerStoreDto qasDto, Integer iRow) {
		TextBox qId = new TextBox();
		TextBox qType = new TextBox();
		TextBox qValue = new TextBox();
		TextBox qCollectionDate = new TextBox();

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
		addHeaderItem(0, "Submission Id");
		addHeaderItem(1, "Survey Id");
		addHeaderItem(2, "Collection Date");
		setGridRowStyle(instanceGrid, 0, false);
	}

	private void addHeaderItem(int col, final String text) {
		HorizontalPanel panel = new HorizontalPanel();
		Label temp = new Label(text);
		panel.add(temp);
		instanceGrid.setWidget(0, col, panel);
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

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() instanceof Grid) {
			Grid grid = (Grid) event.getSource();

			// if we already had a selection, deselect it
			if (currentSelection > 0) {
				setGridRowStyle(grid, currentSelection, false);
			}
			Cell clickedCell = grid.getCellForEvent(event);
			// the click may not have been in a cell
			if (clickedCell != null) {
				int newSelection = clickedCell.getRowIndex();
				// if the clicked row is already selected, deselect it
				if (currentSelection == newSelection) {
					currentSelection = -1;
					setGridRowStyle(grid, currentSelection, false);
				} else {
					currentSelection = newSelection;
				}
				// if the clicked cell is the header (row 0), don't change the
				// style
				if (currentSelection > 0
						&& currentSelection <= currentDtoList.size()) {
					setGridRowStyle(grid, currentSelection, true);
					statusLabel.setText("Loading responses. Please wait...");
					statusLabel.setVisible(true);
					svc.listQuestionsByInstance(currentDtoList.get(
							currentSelection - 1).getKeyId(),
							new AsyncCallback<List<QuestionAnswerStoreDto>>() {

								@Override
								public void onFailure(Throwable caught) {
									// no-op
								}

								@Override
								public void onSuccess(
										List<QuestionAnswerStoreDto> result) {
									populateQuestions(result);
								}

							});
				} else {
					currentSelection = -1;
				}
			}
		}
	}
}
