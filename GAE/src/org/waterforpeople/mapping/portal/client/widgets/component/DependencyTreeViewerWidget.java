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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This component does a reverse traversal of the dependency tree for a given
 * question and shows the path
 * 
 * @author Christopher Fagiani
 * 
 */
public class DependencyTreeViewerWidget extends Composite {
	private static final TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private Tree depTree;
	private Map<Long, QuestionDto> questionMap;
	private Panel contentPanel;

	public DependencyTreeViewerWidget(QuestionDto question,
			Map<Long, List<QuestionDto>> optionQuestions) {
		contentPanel = new VerticalPanel();
		depTree = new Tree();
		questionMap = new HashMap<Long, QuestionDto>();
		if (optionQuestions != null) {
			for (List<QuestionDto> qList : optionQuestions.values()) {
				for (QuestionDto q : qList) {
					questionMap.put(q.getKeyId(), q);
				}
			}
		}

		TreeItem root = new TreeItem(TEXT_CONSTANTS.dependentOnQuestion());
		depTree.addItem(root);
		if (question.getQuestionDependency() != null) {
			installDependency(question.getQuestionDependency().getQuestionId(),
					question.getQuestionDependency().getAnswerValue(), root);
			contentPanel.add(depTree);
		} else {
			contentPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.noData()));
		}
		initWidget(contentPanel);
	}

	private TreeItem installDependency(Long questionId, String ans,
			TreeItem parent) {
		QuestionDto q = questionMap.get(questionId);
		if (q.getQuestionDependency() != null) {
			parent = installDependency(q.getQuestionDependency()
					.getQuestionId(), q.getQuestionDependency()
					.getAnswerValue(), parent);

		}
		TreeItem newItem = new TreeItem(q.getOrder() + ": " + q.getText()
				+ (ans != null ? " - " + ans : ""));
		parent.addItem(newItem);
		parent.setState(true);
		return newItem;

	}
}
