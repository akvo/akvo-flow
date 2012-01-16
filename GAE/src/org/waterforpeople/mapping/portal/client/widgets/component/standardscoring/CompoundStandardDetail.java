package org.waterforpeople.mapping.portal.client.widgets.component.standardscoring;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerServiceAsync;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CompoundStandardDetail extends Composite implements HasText {
	private String standardType = null;
	private StandardScoringManagerServiceAsync svc = null;

	private static CompoundStandardDetailUiBinder uiBinder = GWT
			.create(CompoundStandardDetailUiBinder.class);

	interface CompoundStandardDetailUiBinder extends
			UiBinder<Widget, CompoundStandardDetail> {
	}

	public CompoundStandardDetail() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public CompoundStandardDetail(String standardType,
			StandardScoringManagerServiceAsync svc) {
		setStandardType(standardType);
		initWidget(uiBinder.createAndBindUi(this));
		this.svc = svc;
		loadStandards();
	}

	@UiField
	Button saveButton;
	@UiField
	Button cancelButton;
	@UiField
	ListBox lbLeftHandRule;
	@UiField
	ListBox lbRightHandRule;
	@UiField
	Label labelOperator;
	@UiField
	ListBox operator;
	@UiField
	TextBox compoundRuleID;
	
	@UiHandler("saveButton")
	void onSaveClick(ClickEvent e) {
		Long compoundRuleIDValue=null;
		String value = compoundRuleID.getText().trim();
		if(!value.equals("")){
			compoundRuleIDValue = Long.parseLong(compoundRuleID.getText());
		}
		Long leftRuleId = Integer.valueOf(lbLeftHandRule.getValue(lbLeftHandRule.getSelectedIndex())).longValue();
		Long rightRuleId = Integer.valueOf(lbRightHandRule.getValue(lbRightHandRule.getSelectedIndex())).longValue();
		String operatorValue = operator.getValue(operator.getSelectedIndex());
		
		svc.saveCompoundRule(compoundRuleIDValue, standardType, leftRuleId, rightRuleId, operatorValue, new AsyncCallback<Long>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Long result) {
				compoundRuleID.setText(result.toString());
				Window.alert("Saved Compound Rule");
			}} );
	}

	@UiHandler("lbLeftHandRule")
	void onLbLeftHandRuleChange(ChangeEvent e){
		Window.alert(lbLeftHandRule.getValue(lbLeftHandRule.getSelectedIndex()));
		setupOperatorControl();
	}
	
	@UiHandler("lbRightHandRule")
	void onLbRightHandRuleChange(ChangeEvent e){
		Window.alert(lbRightHandRule.getValue(lbRightHandRule.getSelectedIndex()));
		setupOperatorControl();
	}
	
	private void setupOperatorControl(){
		labelOperator.setVisible(true);
		operator.addItem("And");
		operator.addItem("Or");
		operator.setVisible(true);
		saveButton.setEnabled(true);
	}
	
	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setText(String text) {
		// TODO Auto-generated method stub

	}

	private void loadStandards() {
		Long standardKey = null;
		if (standardType.equalsIgnoreCase("waterpointlevelofservice")) {
			standardKey = 0L;
		} else {
			standardKey = 1L;
		}
		svc.listStandardScoring(
				standardKey,
				null,
				new AsyncCallback<ResponseDto<ArrayList<StandardScoringDto>>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<StandardScoringDto>> result) {
						for (StandardScoringDto item : result.getPayload()) {
							if (item != null
									&& item.getCriteriaType() != null
									&& item.getCriteriaType().equals(
											"Distance")) {
								lbLeftHandRule.addItem(
										"Distance Rule for "
												+ item.getCountryCode() + ":"
												+ item.getDisplayName(), item
												.getKeyId().toString());
								lbRightHandRule.addItem(
										"Distance Rule for "
												+ item.getCountryCode() + ":"
												+ item.getDisplayName(), item
												.getKeyId().toString());
							} else {
								lbLeftHandRule.addItem(item.getDisplayName(),
										item.getKeyId().toString());
								lbRightHandRule.addItem(item.getDisplayName(),
										item.getKeyId().toString());
							}
						}
						lbLeftHandRule.setVisible(true);
						lbRightHandRule.setVisible(true);
					}
				});
	}

	public void setStandardType(String standardType) {
		this.standardType = standardType;
	}

	public String getStandardType() {
		return standardType;
	}
}
