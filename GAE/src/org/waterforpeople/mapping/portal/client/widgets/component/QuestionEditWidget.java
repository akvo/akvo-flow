package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;

import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class QuestionEditWidget extends Composite implements ContextAware,
		ChangeHandler, ClickHandler {

	private static final String INPUT_LABEL_CSS = "input-label";
	private VerticalPanel panel;
	private TextArea questionTextArea;
	private ListBox questionTypeSelector;
	private TextArea tooltipArea;
	private TextBox validationRuleBox;
	private CheckBox mandatoryBox;
	private CheckBox dependentBox;
	private ListBox dependentQuestionSelector;
	private ListBox dependentAnswerSelector;
	private CaptionPanel dependencyPanel;

	private CheckBox allowOtherBox;
	private CheckBox allowMultipleBox;
	private Button addOptionButton;
	private CaptionPanel optionPanel;

	public QuestionEditWidget() {
		installWidgets();
		initWidget(panel);
	}

	private void installWidgets() {
		panel = new VerticalPanel();
		questionTextArea = new TextArea();
		tooltipArea = new TextArea();
		validationRuleBox = new TextBox();
		mandatoryBox = new CheckBox();
		dependentBox = new CheckBox();
		dependentBox.addClickHandler(this);
		questionTypeSelector = new ListBox();
		questionTypeSelector.addItem("Free Text",
				QuestionDto.QuestionType.FREE_TEXT.toString());
		questionTypeSelector.addItem("Option", QuestionDto.QuestionType.OPTION
				.toString());
		questionTypeSelector.addItem("Number", QuestionDto.QuestionType.NUMBER
				.toString());
		questionTypeSelector.addItem("Geo", QuestionDto.QuestionType.GEO
				.toString());
		questionTypeSelector.addItem("Photo", QuestionDto.QuestionType.PHOTO
				.toString());
		questionTypeSelector.addItem("Video", QuestionDto.QuestionType.VIDEO
				.toString());
		questionTypeSelector.addItem("Strength",
				QuestionDto.QuestionType.STRENGTH.toString());
		questionTypeSelector.addChangeHandler(this);
		CaptionPanel basePanel = new CaptionPanel("Question Basics:");

		Grid grid = new Grid(7, 2);
		basePanel.add(grid);

		installRow("Question Text", questionTextArea, grid, 0);
		installRow("Question Type", questionTypeSelector, grid, 1);
		installRow("Tooltip", tooltipArea, grid, 2);
		installRow("Validation Rule", validationRuleBox, grid, 3);
		installRow("Mandatory", mandatoryBox, grid, 4);
		installRow("Dependent", dependentBox, grid, 5);

		dependencyPanel = new CaptionPanel("Dependency Details:");
		dependentQuestionSelector = new ListBox();
		dependentQuestionSelector.addChangeHandler(this);
		dependentQuestionSelector.addItem("Select...");

		dependentAnswerSelector = new ListBox();
		dependentAnswerSelector.addItem("Select...");

		Grid depGrid = new Grid(2, 2);
		dependencyPanel.add(depGrid);
		installRow(null, dependencyPanel, grid, 6, 1);
		dependencyPanel.setVisible(false);
		installRow("Question", dependentQuestionSelector, depGrid, 0);
		installRow("Response", dependentAnswerSelector, depGrid, 1);

		panel.add(basePanel);

		allowMultipleBox = new CheckBox();
		allowOtherBox = new CheckBox();
		addOptionButton = new Button("Add Option");

		optionPanel = new CaptionPanel("Option Details:");
		Grid optGrid = new Grid(2, 4);
		installRow("Allow Multiple", allowMultipleBox, optGrid, 0, 0);
		installRow("Allow 'Other'", allowOtherBox, optGrid, 0, 2);
		installRow(null, addOptionButton, optGrid, 1);

		optionPanel.add(optGrid);
		optionPanel.setVisible(false);
		panel.add(optionPanel);
	}

	private void installRow(String labelText, Widget widget, Grid parent,
			int row) {
		installRow(labelText, widget, parent, row, 0);
	}

	private void installRow(String labelText, Widget widget, Grid parent,
			int row, int colOffset) {
		if (labelText != null) {
			Label label = new Label();
			label.setStylePrimaryName(INPUT_LABEL_CSS);
			label.setText(labelText);
			parent.setWidget(row, colOffset, label);
			parent.setWidget(row, colOffset + 1, widget);

		} else {
			parent.setWidget(row, colOffset, widget);
		}

	}

	@Override
	public Map<String, Object> getContextBundle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persistContext(CompletionListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == questionTypeSelector) {
			if (QuestionDto.QuestionType.OPTION.toString().equals(
					questionTypeSelector.getValue(questionTypeSelector
							.getSelectedIndex()))) {
				optionPanel.setVisible(true);
			}else{
				optionPanel.setVisible(false);
			}
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == dependentBox) {
			if(dependentBox.getValue()){
				dependencyPanel.setVisible(true);
			}else{
				dependencyPanel.setVisible(false);
			}
		}

	}
}