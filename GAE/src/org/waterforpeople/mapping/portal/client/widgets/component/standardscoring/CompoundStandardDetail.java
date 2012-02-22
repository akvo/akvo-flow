package org.waterforpeople.mapping.portal.client.widgets.component.standardscoring;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.standardscoring.CompoundStandardDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.CompoundStandardDto.Operator;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CompoundStandardDetail extends Composite implements HasText,
		DataTableBinder<CompoundStandardDto>,
		DataTableListener<CompoundStandardDto> {

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
		loadPanel();
	}

	@UiField
	Button saveButton;
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
	@UiField
	VerticalPanel listPanel;
	@UiField
	VerticalPanel detailPanel;
	@UiField
	TextBox tbRuleName;

	void loadPanel() {
		init();
		listPanel.add(vp);

	}

	@UiHandler("saveButton")
	void onSaveClick(ClickEvent e) {
		Long compoundRuleIDValue = null;
		String value = compoundRuleID.getText().trim();
		if (!value.equals("")) {
			compoundRuleIDValue = Long.parseLong(compoundRuleID.getText());
		}

		String name = tbRuleName.getText();
		Long leftRuleId = Integer.valueOf(
				lbLeftHandRule.getValue(lbLeftHandRule.getSelectedIndex()))
				.longValue();
		Long rightRuleId = Integer.valueOf(
				lbRightHandRule.getValue(lbRightHandRule.getSelectedIndex()))
				.longValue();
		String leftRuleType = lbLeftHandRule.getItemText(lbLeftHandRule
				.getSelectedIndex());
		if (leftRuleType.startsWith("Distance")) {
			leftRuleType = "DISTANCE";
		} else {
			leftRuleType = "NONDISTANCE";
		}
		String rightRuleType = lbRightHandRule.getItemText(lbRightHandRule
				.getSelectedIndex());
		if (rightRuleType.startsWith("Distance")) {
			rightRuleType = "DISTANCE";
		} else {
			rightRuleType = "NONDISTANCE";
		}
		String operatorValue = operator.getValue(operator.getSelectedIndex());

		svc.saveCompoundRule(compoundRuleIDValue, name, standardType,
				leftRuleId, leftRuleType, rightRuleId, leftRuleType,
				operatorValue, new AsyncCallback<Long>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Long result) {
						compoundRuleID.setText(result.toString());
						Window.alert("Saved Compound Rule");
					}
				});
	}

	@UiHandler("lbLeftHandRule")
	void onLbLeftHandRuleChange(ChangeEvent e) {
		setupOperatorControl();
	}

	@UiHandler("lbRightHandRule")
	void onLbRightHandRuleChange(ChangeEvent e) {
		setupOperatorControl();
	}

	private void setupOperatorControl() {
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

	private void loadStandards(final CompoundStandardDto csItem) {
		Long standardKey = null;
		operator.addItem("and");
		operator.addItem("or");
		if (csItem != null) {
			compoundRuleID.setText(csItem.getKeyId().toString());
			tbRuleName.setText(csItem.getName());
			if (csItem.getOperator().equals(Operator.AND)) {
				operator.setSelectedIndex(0);
			} else {
				operator.setSelectedIndex(1);
			}
		}
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
						int i = 0;
						for (StandardScoringDto item : result.getPayload()) {
							if (item != null
									&& item.getCriteriaType() != null
									&& item.getCriteriaType()
											.equals("Distance")) {
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
								if (csItem != null) {
									if (item.getDisplayName().equalsIgnoreCase(
											csItem.getStandardLeftDesc())) {
										lbLeftHandRule.setSelectedIndex(i);
									}
								}
								lbRightHandRule.addItem(item.getDisplayName(),
										item.getKeyId().toString());
								if (csItem != null) {
									if (item.getDisplayName().equalsIgnoreCase(
											csItem.getStandardRightDesc())) {
										lbRightHandRule.setSelectedIndex(i);
									}

								}

							}
							i++;

						}
						lbLeftHandRule.setVisible(true);
						lbRightHandRule.setVisible(true);
						detailPanel.setVisible(true);
					}
				});
	}

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private VerticalPanel vp = new VerticalPanel();
	private PaginatedDataTable<CompoundStandardDto> ft = null;
	private String standardType = null;
	private StandardScoringManagerServiceAsync svc = null;

	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader("Id", "key", true),
			new DataTableHeader(TEXT_CONSTANTS.name(), "name", true),
			new DataTableHeader("Left Hand Rule ", "standardLeftDesc", true),
			new DataTableHeader(TEXT_CONSTANTS.positiveOperator(), "operator",
					true),
			new DataTableHeader("Right Hand Rule", "standardRightDesc", true),
			new DataTableHeader("Action") };
	private static final String DEFAULT_SORT_FIELD = "name";
	private static final Integer PAGE_SIZE = 20;
	private Button addNewItem = new Button("Add New Compound Rule");

	private void init() {
		ft = new PaginatedDataTable<CompoundStandardDto>(DEFAULT_SORT_FIELD,
				this, this, true, true);

		getVp().add(ft);
		getVp().add(addNewItem);
		addNewHandler();
		requestData(null, false);
	}

	public void setStandardType(String standardType) {
		this.standardType = standardType;
	}

	public void addNewHandler() {
		addNewItem.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				loadStandards(null);
			}
		});
	}

	@Override
	public void bindRow(final Grid grid, CompoundStandardDto item, final int row) {
		// ft.insertRow(row);
		TextBox id = new TextBox();
		if (item.getKeyId() != null)
			id.setText(item.getKeyId().toString());
		grid.setWidget(row, 0, id);
		TextBox name = new TextBox();
		if (item.getName() != null)
			name.setText(item.getName());
		grid.setWidget(row, 1, name);
		TextBox leftHandRuleDesc = new TextBox();
		if (item.getStandardLeftDesc() != null)
			leftHandRuleDesc.setText(item.getStandardLeftDesc());
		grid.setWidget(row, 2, leftHandRuleDesc);
		TextBox operator = new TextBox();
		if (item.getOperator() != null)
			operator.setText(item.getOperator().toString());
		grid.setWidget(row, 3, operator);
		TextBox rightHandRuleDesc = new TextBox();
		if (item.getStandardRightDesc() != null)
			rightHandRuleDesc.setText(item.getStandardRightDesc());
		grid.setWidget(row, 4, rightHandRuleDesc);
		HorizontalPanel hpanel = new HorizontalPanel();
		Button editRow = new Button("Edit");
		editRow.setTitle(new Integer(row).toString());
		Button deleteRow = new Button("Delete");
		deleteRow.setTitle(new Integer(row).toString());
		hpanel.add(editRow);
		editRow.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				CompoundStandardDto item = formStandardScoringDto(row);
				loadStandards(item);
			}
		});
		hpanel.add(deleteRow);
		deleteRow.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Button deletebutton = (Button) event.getSource();
				final Integer row = Integer.parseInt(deletebutton.getTitle());
				TextBox id = (TextBox) grid.getWidget(row, 0);
				final Long keyId = Long.parseLong(id.getText());
				svc.deleteCompoundStandard(keyId, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Void result) {
						grid.removeRow(row);
					}
				});
			}
		});
		grid.setWidget(row, 5, hpanel);
	}

	public CompoundStandardDto formStandardScoringDto(Integer row) {
		CompoundStandardDto item = new CompoundStandardDto();
		Grid grid = ft.getGrid();
		TextBox id = (TextBox) grid.getWidget(row, 0);
		TextBox name = (TextBox) grid.getWidget(row, 1);
		TextBox leftHandRuleDesc = (TextBox) grid.getWidget(row, 2);
		TextBox operator = (TextBox) grid.getWidget(row, 3);
		TextBox rightHandRuleDesc = (TextBox) grid.getWidget(row, 4);
		if (id.getText() != null)
			item.setKeyId(Long.parseLong(id.getText()));
		if (name.getText() != null)
			item.setName(name.getText().trim());
		if (leftHandRuleDesc.getText() != null)
			item.setStandardLeftDesc(leftHandRuleDesc.getText().trim());
		if (rightHandRuleDesc.getText() != null)
			item.setStandardRightDesc(rightHandRuleDesc.getText().trim());
		if (operator.getText() != null) {
			if (operator.getText().equalsIgnoreCase("and")) {
				item.setOperator(Operator.AND);
			} else {
				item.setOperator(Operator.OR);
			}
		}
		return item;
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
	public void onItemSelected(CompoundStandardDto item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestData(String cursor, final boolean isResort) {
		final boolean isNew = (cursor == null);
		svc.listCompoundRule(
				standardType,
				new AsyncCallback<ResponseDto<ArrayList<CompoundStandardDto>>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<CompoundStandardDto>> result) {
						if (result.getPayload().size() > 0) {
							ft.bindData(result.getPayload(),
									result.getCursorString(), isNew, isResort);
						} else {
							loadStandards(null);
						}
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
