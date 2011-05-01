package com.gallatinsystems.flow.foundry.client;

import java.util.List;

import com.gallatinsystems.flow.foundry.client.FlowInstanceService;
import com.gallatinsystems.flow.foundry.client.FlowInstanceServiceAsync;
import com.gallatinsystems.flow.foundry.domain.FlowInstance;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FlowFoundry implements EntryPoint {

	private FlowInstanceServiceAsync flowInstanceService;

	@Override
	public void onModuleLoad() {
		flowInstanceService = GWT.create(FlowInstanceService.class);
		FlowInstance test = new FlowInstance();
		test.setName("test");
		flowInstanceService.saveInstance(test,
				new AsyncCallback<FlowInstance>() {

					@Override
					public void onSuccess(FlowInstance result) {
						flowInstanceService.listInstances(null,
								new AsyncCallback<List<FlowInstance>>() {

									@Override
									public void onFailure(Throwable caught) {
										Window.alert(caught
												.getLocalizedMessage());

									}

									@Override
									public void onSuccess(
											List<FlowInstance> result) {
										VerticalPanel p = new VerticalPanel();
										for (FlowInstance i : result) {
											p.add(new Label(i.getName()));
										}
										RootPanel.get().setPixelSize(1024, 768);
										RootPanel.get().add(p);
									}
								});
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getLocalizedMessage());
					}
				});

	}

}
