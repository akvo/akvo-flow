package org.waterforpeople.mapping.portal.client.widgets.component.standardscoring;

import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CompoundRulePopup extends PopupPanel {
	private String standardType=null;
	private StandardScoringManagerServiceAsync svc =null;
	public CompoundRulePopup(String standardType,
			StandardScoringManagerServiceAsync svc, Boolean displayDetail) {
		super(true);
		this.standardType = standardType;
		this.svc = svc;
		if (displayDetail) {
			initDetail(standardType, svc);
		}else{
			initList(standardType, svc);
		}

	}
	VerticalPanel vp = new VerticalPanel();
	public void initDetail(String standardType, StandardScoringManagerServiceAsync svc) {
		setWidget(new Label("Compound Rule Manager"));
		CompoundStandardDetail csd = new CompoundStandardDetail(standardType,
				svc);
		this.standardType = standardType;
		this.svc = svc;
		vp.add(csd);
		vp.add(close);
		setWidget(vp);
		addCloseHandler();
	}
	Button addnew = new Button("Add");
	Button close = new Button("Close");
	
	public void initList(String standardType, StandardScoringManagerServiceAsync svc){
		setWidget(new Label("Compound Rule List View"));
		CompoundRuleListView crlv = new CompoundRuleListView(standardType, svc);
		RootPanel rp = RootPanel.get();
		
		vp.add(crlv);
		HorizontalPanel hPanel = new HorizontalPanel();
		addCloseHandler();
		addNewHandler();
		hPanel.add(addnew);
		hPanel.add(close);
		vp.add(hPanel);
		rp.add(vp);	
	}
	private void addNewHandler(){
		addnew.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				initDetail(standardType,svc);
				
			}});
	}
	private void addCloseHandler(){
		close.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}});

	}

}
