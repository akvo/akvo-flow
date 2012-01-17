package org.waterforpeople.mapping.portal.client.widgets.component.standardscoring;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.standardscoring.CompoundStandardDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class CompoundRuleListView extends Composite {
	private FlexTable ft = new FlexTable();
    private String standardType=null;
	private StandardScoringManagerServiceAsync svc=null;
	
	public void setFt(FlexTable ft) {
		this.ft = ft;
	}

	public FlexTable getFt() {
		return ft;
	}
	
	public CompoundRuleListView(String standardType,
			StandardScoringManagerServiceAsync svc){
		this.standardType = standardType;
		this.svc = svc;
		initWidget(ft);
		init();
	};
	
	private void init(){
		loadCompoundRules();
	}

	private void loadCompoundRules() {
		svc.listCompoundRule(standardType, new AsyncCallback<ArrayList<CompoundStandardDto>>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(ArrayList<CompoundStandardDto> result) {
				int i=1;
				buildHeaderRow();
				for(CompoundStandardDto item: result){
					bindRow(item, i);
					i++;
				}
			}

			});
	}
	private void buildHeaderRow() {
		//ft.insertRow(0);
		ft.add(new Label("ID"));
		ft.add(new Label("Name"));
		ft.add(new Label("Left Rule Description"));
		ft.add(new Label("Operator"));
		ft.add(new Label("Right Rule Description"));
		ft.add(new Label("Action"));
	}
	public void setStandardType(String standardType) {
		this.standardType = standardType;
	}
	
	private void bindRow(CompoundStandardDto item, final int row){
		//ft.insertRow(row);
		TextBox id = new TextBox();
		if(item.getKeyId()!=null)
			id.setText(item.getKeyId().toString());
		ft.setWidget(row,0, id);
		TextBox leftHandRuleDesc = new TextBox();
		if(item.getStandardLeftDesc()!=null)
			leftHandRuleDesc.setText(item.getStandardLeftDesc());
		ft.setWidget(row, 1, leftHandRuleDesc);
		TextBox operator = new TextBox();
		if(item.getOperator()!=null)
			operator.setText(item.getOperator().toString());
		ft.setWidget(row, 2, operator);
		TextBox rightHandRuleDesc = new TextBox();
		if(item.getStandardRightDesc()!=null)
			rightHandRuleDesc.setText(item.getStandardRightDesc());
		ft.setWidget(row, 3, rightHandRuleDesc);
		HorizontalPanel hpanel =new HorizontalPanel();
		Button editRow = new Button("Edit");
		editRow.setTitle(new Integer(row).toString());
		Button deleteRow = new Button("Delete");
		deleteRow.setTitle(new Integer(row).toString());
		hpanel.add(editRow);
		hpanel.add(deleteRow);
		ft.setWidget(row, 4, hpanel);
	}

	public String getStandardType() {
		return standardType;
	}

	public void setSvc(StandardScoringManagerServiceAsync svc) {
		this.svc = svc;
	}

	public StandardScoringManagerServiceAsync getSvc() {
		return svc;
	}
	

}
