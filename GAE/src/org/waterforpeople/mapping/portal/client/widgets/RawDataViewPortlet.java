package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.surveyinstance.QuestionAnswerStoreDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceService;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.user.UserDto;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.formatters.DateFormat;

public class RawDataViewPortlet extends LocationDrivenPortlet {
	public static final String NAME = "Raw Data Manager";
	public static final String DESCRIPTION = "Allows the management of raw imported survey data";
	private static String title;
	private static String description;
	private static Boolean scrollable;
	private static Boolean configurable;
	private static Integer width = 1024;
	private static Integer height = 768;
	private static Boolean useCommunity;
	private static String specialOption;
	private static UserDto user;

	private Tree surveyImportedTree = new Tree();

	public RawDataViewPortlet(String title, boolean scrollable,
			boolean configurable, int width, int height, UserDto user,
			boolean useCommunity, String specialOption) {
		super(NAME, true, false, width, height, null, false, null);
		setupPortlet();
	}

	public RawDataViewPortlet() {
		super(NAME, true, false, width, height, null, false, null);
		setupPortlet();
	}

	private SurveyInstanceServiceAsync svc = null;
	private ServiceDefTarget endpoint = null;

	private void setupPortlet() {
		bindSvc();
		loadContentPanel();

	}

	public String getDescription() {
		return description;
	}

	private void bindSvc() {
		svc = GWT.create(SurveyInstanceService.class);
		endpoint = (ServiceDefTarget) svc;
		endpoint
				.setServiceEntryPoint("/org.waterforpeople.mapping.portal.portal/surveyinstance");
	}

	private void loadContentPanel() {
		loadSurveyImportTree();
		contentPanel.add(mainHPanel);
		setWidget(contentPanel);
	}

	@Override
	public String getName() {
		return title;
	}

	Grid qasDetailGrid = null;

	private void loadSurveyImportTree() {
		mainHPanel.add(new Label("Imported Surveys"));
		svc.listSurveyInstance(null,
				new AsyncCallback<ArrayList<SurveyInstanceDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(ArrayList<SurveyInstanceDto> result) {
						if (result != null) {
							for (SurveyInstanceDto item : result) {
								bindItemToTree(item);
							}

							mainHPanel.add(surveyImportedTree);
							surveyImportedTree
									.addSelectionHandler(new SelectionHandler<TreeItem>() {

										@Override
										public void onSelection(
												SelectionEvent<TreeItem> event) {
											SurveyInstanceDto siDto = (SurveyInstanceDto) event
													.getSelectedItem()
													.getUserObject();

											qasDetailGrid = new Grid(siDto
													.getQuestionAnswersStore()
													.size() + 1, 4);
											qasDetailGrid.setWidget(0, 0,
													new Label("Row Id"));
											qasDetailGrid.setWidget(0, 1,
													new Label("Question Type"));
											qasDetailGrid.setWidget(0, 2,
													new Label("Answer Value"));
											qasDetailGrid
													.setWidget(0, 3, new Label(
															"Collection Date"));
											Integer iRow = 0;
											for (QuestionAnswerStoreDto qasDto : siDto
													.getQuestionAnswersStore()) {
												qasDto.setCollectionDate(siDto
														.getCollectionDate());
												bindQASRow(qasDto, ++iRow);
												mainHPanel.add(qasDetailGrid);
											}
										}
									});
						}
					}

				});
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

	private HorizontalPanel mainHPanel = new HorizontalPanel();
	private VerticalPanel contentPanel = new VerticalPanel();

	private void bindItemToTree(SurveyInstanceDto item) {
		TreeItem treeItem = new TreeItem();
		treeItem.setText(item.getKeyId()
				+ ":"
				+ DateTimeFormat.getMediumDateTimeFormat().format(
						item.getCollectionDate()));
		treeItem.setUserObject(item);
		surveyImportedTree.addItem(treeItem);
	}

}
