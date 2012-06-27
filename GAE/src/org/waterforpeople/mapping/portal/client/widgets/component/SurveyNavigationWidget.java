/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;

/**
 * widget used for navigation within a survey while editing questions
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyNavigationWidget extends Composite implements ClickHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	private static final int PREFIX_LEN = 100;
	private static final String CLICKABLE_ITEM_CSS = "clickable-list-item";

	public enum MODE {
		QUESTION, QUESTION_LIST, QUESTION_GROUP_EDIT
	}

	private Integer curOrder;

	private QuestionGroupDto prevGroup;
	private QuestionGroupDto nextGroup;
	private SurveyDto survey;
	private MODE mode;
	private QuestionGroupDto questionGroup;
	private QuestionDto prevQuestion;
	private QuestionDto nextQuestion;

	private DockPanel contentPanel;
	private PageController controller;
	private ContextAware parent;

	private HTML prevLabel;
	private HTML nextLabel;

	public SurveyNavigationWidget(SurveyDto survey, QuestionGroupDto group,
			Integer order, boolean isMidInsert, MODE mode,
			PageController controller, ContextAware parent) {
		this.questionGroup = group;
		this.curOrder = order;
		this.survey = survey;
		this.mode = mode;
		this.parent = parent;
		this.controller = controller;

		contentPanel = new DockPanel();
		prevLabel = new HTML();

		prevLabel.setStylePrimaryName(CLICKABLE_ITEM_CSS);
		prevLabel.addClickHandler(this);
		nextLabel = new HTML();
		nextLabel.setStylePrimaryName(CLICKABLE_ITEM_CSS);

		nextLabel.addClickHandler(this);
		if (mode == MODE.QUESTION) {
			initializeNextPreviousQuestion(isMidInsert);
		} else {
			initializeNextPreviousGroup();
		}
		contentPanel.add(prevLabel, DockPanel.WEST);
		contentPanel.add(nextLabel, DockPanel.EAST);
		Grid surveyGrid = new Grid(2, 2);

		surveyGrid.setWidget(0, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.survey() + ": "));
		surveyGrid.setWidget(0, 1, ViewUtil.initLabel(survey.getName()));
		if (questionGroup != null) {
			surveyGrid.setWidget(1, 0,
					ViewUtil.initLabel(TEXT_CONSTANTS.questionGroup() + ": "));
			surveyGrid.setWidget(1, 1, ViewUtil.initLabel(group.getName()));
		}

		contentPanel.add(surveyGrid, DockPanel.CENTER);
		contentPanel.setCellHorizontalAlignment(surveyGrid,
				DockPanel.ALIGN_CENTER);

		contentPanel.setWidth("100%");
		contentPanel.setCellWidth(prevLabel, "25%");
		contentPanel.setCellWidth(nextLabel, "25%");
		contentPanel.setCellWidth(surveyGrid, "50%");
		initWidget(contentPanel);
	}

	private void initializeNextPreviousGroup() {
		QuestionGroupDto temp = null;
		boolean foundGroup = false;
		if (survey.getQuestionGroupList() != null) {
			for (QuestionGroupDto g : survey.getQuestionGroupList()) {
				if (g.getOrder() == curOrder) {
					foundGroup = true;
					if (temp != null) {
						prevGroup = temp;
					}
				} else if (foundGroup) {
					// if the group isn't the one we're editing AND we've
					// already found the previous, then this is the next
					nextGroup = g;
					break;
				} else {
					temp = g;
				}
			}
		}
		if (!foundGroup) {
			prevGroup = temp;
		}

		if (nextGroup == null) {
			nextLabel.setVisible(false);
		} else {
			nextLabel.setHTML(formDirectionLabelHtml(true, nextGroup.getOrder()
					+ ": " + nextGroup.getName()));
		}
		if (prevGroup == null) {
			prevLabel.setVisible(false);
		} else {
			prevLabel.setHTML(formDirectionLabelHtml(false,
					prevGroup.getOrder() + ": " + prevGroup.getName()));
		}
	}

	private void initializeNextPreviousQuestion(boolean isMidInsert) {
		QuestionDto temp = null;
		boolean foundQ = false;
		if (questionGroup.getQuestionMap() != null) {
			for (QuestionDto q : questionGroup.getQuestionMap().values()) {
				if (q.getOrder() == curOrder) {
					foundQ = true;
					if (temp != null) {
						prevQuestion = temp;
					}
					if (isMidInsert) {
						nextQuestion = q;
						break;
					}
				} else if (foundQ) {
					// if the question isn't the one we're editing AND we've
					// already found the previous, then this is the next
					nextQuestion = q;
					break;
				} else {
					temp = q;
				}
			}
		}
		if (!foundQ) {
			prevQuestion = temp;
		}

		if (nextQuestion == null) {
			nextLabel.setVisible(false);
		} else {
			nextLabel.setHTML(formDirectionLabelHtml(true,
					nextQuestion.getOrder() + ": " + nextQuestion.getText()));
		}
		if (prevQuestion == null) {
			prevLabel.setVisible(false);
		} else {
			prevLabel.setHTML(formDirectionLabelHtml(false,
					prevQuestion.getOrder() + ": " + prevQuestion.getText()));
		}
	}

	private String formDirectionLabelHtml(boolean isNext, String questionText) {
		String text = "<center><b><i>";
		if (isNext) {
			text += TEXT_CONSTANTS.next();
		} else {
			text += TEXT_CONSTANTS.previous();
		}
		text += "</b></i><br>";

		if (questionText.length() > PREFIX_LEN) {
			questionText = questionText.substring(0, PREFIX_LEN);

		}
		questionText = questionText.replaceAll("&", "&amp;");
		questionText = questionText.replaceAll("<", "&lt;");
		questionText = questionText.replaceAll(">", "&gt;");
		text += questionText;
		if (isNext) {
			text += "&gt;";
		}
		text += "</center>";
		return text;
	}

	@Override
	public void onClick(ClickEvent event) {
		Map<String, Object> bundle = parent.getContextBundle(false);
		if (event.getSource() == prevLabel) {
			if (mode == MODE.QUESTION) {
				bundle.put(BundleConstants.QUESTION_KEY, prevQuestion);
			} else {
				bundle.put(BundleConstants.QUESTION_GROUP_KEY, prevGroup);
			}
		} else if (event.getSource() == nextLabel) {
			if (mode == MODE.QUESTION) {
				bundle.put(BundleConstants.QUESTION_KEY, nextQuestion);
			} else {
				bundle.put(BundleConstants.QUESTION_GROUP_KEY, nextGroup);
			}
		}
		if (mode == MODE.QUESTION) {
			controller.openPage(QuestionEditWidget.class, false, bundle);
		} else if (mode == MODE.QUESTION_LIST) {
			controller.openPage(QuestionListWidget.class, false, bundle);
		} else if (mode == MODE.QUESTION_GROUP_EDIT) {
			controller.openPage(QuestionGroupEditWidget.class, false, bundle);
		}
	}

}
