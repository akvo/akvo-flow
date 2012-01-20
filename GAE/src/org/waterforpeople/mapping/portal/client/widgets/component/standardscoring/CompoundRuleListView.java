package org.waterforpeople.mapping.portal.client.widgets.component.standardscoring;

import java.util.ArrayList;
import java.util.Iterator;

import org.waterforpeople.mapping.app.gwt.client.standardscoring.CompoundStandardDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CompoundRuleListView extends Composite implements HasWidgets,
		DataTableBinder<CompoundStandardDto>,
		DataTableListener<CompoundStandardDto> {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private VerticalPanel vp = new VerticalPanel();
	private PaginatedDataTable<CompoundStandardDto> ft = null;
	private String standardType = null;
	private StandardScoringManagerServiceAsync svc = null;
	private static final String EDITED_ROW_CSS = "gridCell-edited";

	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader("Id", "key", true),
			new DataTableHeader(TEXT_CONSTANTS.name(), "name", true),
			new DataTableHeader("Left Hand Rule ","standardLeftDesc",
					true),
			new DataTableHeader(TEXT_CONSTANTS.positiveOperator(), "operator",
					true),
			new DataTableHeader("Right Hand Rule",
					"standardRightDesc", true),
			new DataTableHeader("Action") };
	private static final String DEFAULT_SORT_FIELD = "name";
	private static final Integer PAGE_SIZE = 20;

	public CompoundRuleListView() {
		initWidget(vp);
	}

	public CompoundRuleListView(String standardType,
			StandardScoringManagerServiceAsync svc) {
		this.standardType = standardType;
		this.svc = svc;
		initWidget(vp);
		init();
	};

	private void init() {
		ft = new PaginatedDataTable<CompoundStandardDto>(DEFAULT_SORT_FIELD,
				this, this, true,true);
		getVp().add(ft);
		requestData(null,false);
	}

	

	public void setStandardType(String standardType) {
		this.standardType = standardType;
	}
	@Override
	public void bindRow(final Grid grid, CompoundStandardDto item, final int row) {
		// ft.insertRow(row);
		TextBox id = new TextBox();
		if (item.getKeyId() != null)
			id.setText(item.getKeyId().toString());
		grid.setWidget(row, 0, id);
		TextBox leftHandRuleDesc = new TextBox();
		if (item.getStandardLeftDesc() != null)
			leftHandRuleDesc.setText(item.getStandardLeftDesc());
		grid.setWidget(row, 1, leftHandRuleDesc);
		TextBox operator = new TextBox();
		if (item.getOperator() != null)
			operator.setText(item.getOperator().toString());
		grid.setWidget(row, 2, operator);
		TextBox rightHandRuleDesc = new TextBox();
		if (item.getStandardRightDesc() != null)
			rightHandRuleDesc.setText(item.getStandardRightDesc());
		grid.setWidget(row, 3, rightHandRuleDesc);
		HorizontalPanel hpanel = new HorizontalPanel();
		Button editRow = new Button("Edit");
		editRow.setTitle(new Integer(row).toString());
		Button deleteRow = new Button("Delete");
		deleteRow.setTitle(new Integer(row).toString());
		hpanel.add(editRow);
		editRow.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}});
		hpanel.add(deleteRow);
		grid.setWidget(row, 4, hpanel);
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

	public void setVp(VerticalPanel vp) {
		this.vp = vp;
	}

	public VerticalPanel getVp() {
		return vp;
	}

	@Override
	public void add(Widget w) {
		vp.add(w);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public Iterator<Widget> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Widget w) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemSelected(CompoundStandardDto item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestData(String cursor, final boolean isResort) {
		final boolean isNew = (cursor == null);
		svc.listCompoundRule(standardType,
				new AsyncCallback<ResponseDto<ArrayList<CompoundStandardDto>>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(ResponseDto<ArrayList<CompoundStandardDto>> result) {
						ft.bindData(result.getPayload(),result.getCursorString(), isNew, isResort);
					}

				});
	}

	@Override
	public DataTableHeader[] getHeaders() {
		return HEADERS;
	}

	

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE;
	}
}
