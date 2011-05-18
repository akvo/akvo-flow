package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.StandardScoring.StandardScoringDto;
import org.waterforpeople.mapping.app.gwt.client.StandardScoring.StandardScoringManagerService;
import org.waterforpeople.mapping.app.gwt.client.StandardScoring.StandardScoringManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class StandardScoringManagerPortlet extends UserAwarePortlet implements
		DataTableBinder<StandardScoringDto>,
		DataTableListener<StandardScoringDto> {

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static String title = "Scoring Manager";
	private static Boolean scrollable = true;
	private static Boolean configurable = false;
	private static Boolean snapable = false;
	private static Integer width = 800;
	private static Integer height = 1024;
	private DateTimeFormat dateFormat =null;
	private static Boolean errorMode =null;
	private StandardScoringManagerServiceAsync svc;

	PaginatedDataTable<StandardScoringDto> scoringTable;
	private VerticalPanel mainVPanel = new VerticalPanel();

	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader(TEXT_CONSTANTS.id(), "key", true),
			new DataTableHeader(TEXT_CONSTANTS.communityCode(),
					"communityCode", true),
			new DataTableHeader(TEXT_CONSTANTS.latitude(), "latitude", true),
			new DataTableHeader(TEXT_CONSTANTS.longitude(), "longitude", true),
			new DataTableHeader(TEXT_CONSTANTS.pointType(), "pointType", true),
			new DataTableHeader(TEXT_CONSTANTS.collectionDate(),
					"collectionDate", true),
			new DataTableHeader(TEXT_CONSTANTS.editDelete()) };
	private static final String DEFAULT_SORT_FIELD = null;

	private VerticalPanel contentPane;

	public StandardScoringManagerPortlet(String title, boolean scrollable,
			boolean configurable, boolean snapable, int width, int height,
			UserDto user) {
		super(title, scrollable, configurable, snapable, width, height, user);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		scoringTable = new PaginatedDataTable<StandardScoringDto>(DEFAULT_SORT_FIELD,
				this, this, true);
		dateFormat = DateTimeFormat.getShortDateFormat();
		contentPane.add(header);
		setContent(contentPane);
		errorMode = false;
		svc = GWT.create(StandardScoringManagerService.class);
		scoringTable.setVisible(false);
		mainVPanel.add(scoringTable);
	}

	private Widget buildHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	public StandardScoringManagerPortlet(UserDto user) {
		super(title, scrollable, configurable, snapable, width, height, user);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onItemSelected(StandardScoringDto item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestData(String cursor, boolean isResort) {
		// TODO Auto-generated method stub

	}

	@Override
	public DataTableHeader[] getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bindRow(Grid grid, StandardScoringDto item, int row) {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer getPageSize() {
		// TODO Auto-generated method stub
		return null;
	}

	private void loadTable() {

	}

}
