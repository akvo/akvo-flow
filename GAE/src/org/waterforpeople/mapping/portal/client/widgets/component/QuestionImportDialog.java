package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.Orientation;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.SelectionMode;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.TerminalType;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * dialog box to handle importing questions from another source into a survey.
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionImportDialog extends DialogBox implements ChangeHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private VerticalPanel controlPanel;
	private DockPanel contentPane;
	private static final String FILE_SOURCE = "FILE";
	private static final String SURVEY_SOURCE = "SURVEY";

	private QuestionGroupDto questionGroup;
	private ListBox questionBox;
	private ListBox sourceBox;
	private SurveyServiceAsync surveyService;
	private Panel appletPanel;
	private Button ok;
	private CaptionPanel sourceSurveySelctionPanel;
	private SurveySelectionWidget surveySelector;
	private CheckBox includeDepBox;
	private int saveCompleteCount;
	private int orderIncrement;
	private Integer startingOrder;
	private int discovered;
	private QuestionDto newParent;
	private Set<Long> newQuestionIds;

	public QuestionImportDialog(QuestionGroupDto group) {
		questionGroup = group;
		surveyService = GWT.create(SurveyService.class);
		contentPane = new DockPanel();
		setAnimationEnabled(true);
		setGlassEnabled(true);
		contentPane = new DockPanel();
		controlPanel = new VerticalPanel();
		appletPanel = new HorizontalPanel();
		setPopupPosition(Window.getClientWidth() / 4,
				Window.getClientHeight() / 4);
		questionBox = new ListBox();
		ViewUtil.installFieldRow(controlPanel, TEXT_CONSTANTS.questionBefore(),
				questionBox, ViewUtil.DEFAULT_INPUT_LABEL_CSS);
		sourceBox = new ListBox();
		sourceBox.addItem(TEXT_CONSTANTS.file(), FILE_SOURCE);
		sourceBox.addItem(TEXT_CONSTANTS.otherSurvey(), SURVEY_SOURCE);
		sourceBox.addChangeHandler(this);
		ViewUtil.installFieldRow(controlPanel, TEXT_CONSTANTS.source(),
				sourceBox, ViewUtil.DEFAULT_INPUT_LABEL_CSS);
		ViewUtil.installFieldRow(controlPanel, "", appletPanel, null);
		contentPane.add(controlPanel, DockPanel.CENTER);

		HorizontalPanel buttonPanel = new HorizontalPanel();

		ok = new Button(TEXT_CONSTANTS.importQuestions());
		ok.setEnabled(false);
		Button cancel = new Button(TEXT_CONSTANTS.close());
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		initSourceSurveyPanel();
		contentPane.add(buttonPanel, DockPanel.SOUTH);
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (SURVEY_SOURCE.equals(ViewUtil.getListBoxSelection(
						sourceBox, false))) {
					Long sourceQuestionId = surveySelector
							.getSelectedQuestionId();
					if (sourceQuestionId != null) {
						saveCompleteCount = 0;
						orderIncrement = 0;
						discovered = 1;
						newQuestionIds = new HashSet<Long>();
						performQuestionCopy(sourceQuestionId,
								includeDepBox.getValue());
					} else {
						MessageDialog dia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.questionMandatory());
						dia.showCentered();
					}

				} else {
					launchApplet();
				}
			}
		});
		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				appletPanel.clear();
				appletPanel.removeFromParent();
				hide();
			}
		});
		setWidget(contentPane);
		loadData();
	}

	/**
	 * copies a question (and possibly its dependency descendants) to a new
	 * survey
	 */
	private void performQuestionCopy(final Long sourceQuestionId,
			final Boolean includeDep) {

		final Long targetQuestion = new Long(ViewUtil.getListBoxSelection(
				questionBox, false));
		final Label statusLabel = ViewUtil.initLabel(TEXT_CONSTANTS.copying());
		controlPanel.add(statusLabel);
		surveyService.loadQuestionDetails(targetQuestion,
				new AsyncCallback<QuestionDto>() {

					@Override
					public void onFailure(Throwable caught) {
						statusLabel.setText(TEXT_CONSTANTS.error() + ": "
								+ caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(QuestionDto result) {
						startingOrder = result.getOrder();
						surveyService.loadQuestionDetails(sourceQuestionId,
								new AsyncCallback<QuestionDto>() {

									@Override
									public void onFailure(Throwable caught) {
										statusLabel.setText(TEXT_CONSTANTS
												.error()
												+ ": "
												+ caught.getLocalizedMessage());
									}

									@Override
									public void onSuccess(QuestionDto result) {
										copyQuestion(result, includeDep,
												statusLabel);
									}
								});
					}
				});
	}

	/**
	 * copies a question to a new survey
	 * 
	 * @param source
	 * @param includeDep
	 * @param statusLabel
	 */
	private void copyQuestion(final QuestionDto source,
			final Boolean includeDep, final Label statusLabel) {
		source.setOrder(startingOrder + orderIncrement);
		orderIncrement++;
		surveyService.copyQuestion(source, questionGroup,
				new AsyncCallback<QuestionDto>() {

					@Override
					public void onFailure(Throwable caught) {
						statusLabel.setText(TEXT_CONSTANTS.error() + ": "
								+ caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(final QuestionDto savedParent) {
						saveCompleteCount++;
						newQuestionIds.add(savedParent.getKeyId());
						if (newParent == null) {
							newParent = savedParent;
						}
						if (!includeDep) {
							adjustQuestionOrdering(newParent, statusLabel);
						} else {
							statusLabel.setText(TEXT_CONSTANTS
									.copyingDependencies());
							surveyService.listQuestionsDependentOnQuestion(
									source.getKeyId(),
									new AsyncCallback<ArrayList<QuestionDto>>() {

										@Override
										public void onFailure(Throwable caught) {
											statusLabel.setText(TEXT_CONSTANTS
													.error()
													+ ": "
													+ caught.getLocalizedMessage());
										}

										@Override
										public void onSuccess(
												ArrayList<QuestionDto> result) {
											if (result != null) {
												discovered += result.size();
												for (QuestionDto dto : result) {
													surveyService
															.loadQuestionDetails(
																	dto.getKeyId(),
																	new AsyncCallback<QuestionDto>() {
																		@Override
																		public void onFailure(
																				Throwable caught) {
																			statusLabel
																					.setText(TEXT_CONSTANTS
																							.error()
																							+ ": "
																							+ caught.getLocalizedMessage());
																		}

																		@Override
																		public void onSuccess(
																				QuestionDto result) {
																			result.getQuestionDependency()
																					.setQuestionId(
																							savedParent
																									.getKeyId());
																			copyQuestion(
																					result,
																					includeDep,
																					statusLabel);
																		}
																	});
												}
											}
											if (discovered == saveCompleteCount) {
												adjustQuestionOrdering(
														newParent, statusLabel);
											}
										}
									});
						}
					}
				});
	}

	/**
	 * finds all questions that need to be moved in the current question group
	 * (to prevent duplicate orders) and bumps up their ordering by
	 * newQuestionCount
	 * 
	 * @param newlyInsertedQuestion
	 * @param statusLabel
	 */
	private void adjustQuestionOrdering(
			final QuestionDto newlyInsertedQuestion, final Label statusLabel) {

		surveyService.listQuestionsByQuestionGroup(questionGroup.getKeyId()
				.toString(), false, false,
				new AsyncCallback<ArrayList<QuestionDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						statusLabel.setText(TEXT_CONSTANTS.error() + ": "
								+ caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(ArrayList<QuestionDto> result) {
						if (result != null) {
							List<QuestionDto> qList = new ArrayList<QuestionDto>();
							for (QuestionDto q : result) {
								if (!newQuestionIds.contains(q.getKeyId())
										&& q.getOrder() >= startingOrder) {
									q.setOrder(q.getOrder() + discovered);
									qList.add(q);
								}
							}
							if (qList.size() > 0) {
								surveyService.updateQuestionOrder(qList,
										new AsyncCallback<Void>() {

											@Override
											public void onFailure(
													Throwable caught) {
												statusLabel.setText(TEXT_CONSTANTS
														.error()
														+ ": "
														+ caught.getLocalizedMessage());
											}

											@Override
											public void onSuccess(Void result) {
												statusLabel.setText(TEXT_CONSTANTS
														.copyComplete());
											}
										});
							} else {
								statusLabel.setText(TEXT_CONSTANTS
										.copyComplete());
							}
						}

					}
				});
	}

	/**
	 * launches the import applet
	 */
	private void launchApplet() {
		String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataImportAppletImpl width=256 height=256 archive='exporterapplet.jar,poi-3.5-signed.jar,json.jar'>";
		appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
		appletString += "<PARAM name='importType' value='SURVEY_SPREADSHEET'>";
		appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
		appletString += "<PARAM name='criteria' value='isWholeSurvey:=false;beforeQuestionId:="
				+ ViewUtil.getListBoxSelection(questionBox, false) + "'>";
		appletString += "</applet>";
		HTML html = new HTML();
		html.setHTML(appletString);
		appletPanel.add(html);
		controlPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.doNotClose()));
	}

	private void loadData() {
		surveyService.listQuestionsByQuestionGroup(questionGroup.getKeyId()
				.toString(), false, false,
				new AsyncCallback<ArrayList<QuestionDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog dia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
						dia.showCentered();
					}

					@Override
					public void onSuccess(ArrayList<QuestionDto> result) {
						if (result != null && result.size() > 0) {
							for (QuestionDto q : result) {
								questionBox.addItem(q.getText(), q.getKeyId()
										.toString());
							}
							ok.setEnabled(true);
						}
					}
				});
	}

	public void showCentered() {
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getScrollLeft() + Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getScrollTop() + Window.getClientHeight() - offsetHeight) / 2) >> 0;
				setPopupPosition(left, top);
			}
		});
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == sourceBox) {
			if (SURVEY_SOURCE.equals(ViewUtil.getListBoxSelection(sourceBox,
					false))) {

				sourceSurveySelctionPanel.setVisible(true);
			} else {
				sourceSurveySelctionPanel.setVisible(false);
			}
		}
	}

	private void initSourceSurveyPanel() {
		sourceSurveySelctionPanel = new CaptionPanel(
				TEXT_CONSTANTS.otherSurvey());
		surveySelector = new SurveySelectionWidget(Orientation.HORIZONTAL,
				TerminalType.QUESTION, SelectionMode.SINGLE);
		Panel content = new VerticalPanel();
		content.add(surveySelector);
		includeDepBox = new CheckBox();
		ViewUtil.installFieldRow(content,
				TEXT_CONSTANTS.includeDependentQuestions(), includeDepBox,
				ViewUtil.DEFAULT_INPUT_LABEL_CSS);
		sourceSurveySelctionPanel.add(content);
		sourceSurveySelctionPanel.setVisible(false);
		controlPanel.add(sourceSurveySelctionPanel);
	}

}
