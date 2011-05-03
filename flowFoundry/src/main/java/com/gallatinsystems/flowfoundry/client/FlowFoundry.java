package com.gallatinsystems.flowfoundry.client;

import java.util.List;

import com.gallatinsystems.flowfoundry.shared.dto.FlowInstanceDto;
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
		FlowInstanceDto test = new FlowInstanceDto();
		test.setName("test");
		flowInstanceService.saveInstance(test,
				new AsyncCallback<FlowInstanceDto>() {

					@Override
					public void onSuccess(FlowInstanceDto result) {
						flowInstanceService.listInstances(null,
								new AsyncCallback<List<FlowInstanceDto>>() {

									@Override
									public void onFailure(Throwable caught) {
										Window.alert(caught
												.getLocalizedMessage());

									}

									@Override
									public void onSuccess(
											List<FlowInstanceDto> result) {
										VerticalPanel p = new VerticalPanel();
										for (FlowInstanceDto i : result) {
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
