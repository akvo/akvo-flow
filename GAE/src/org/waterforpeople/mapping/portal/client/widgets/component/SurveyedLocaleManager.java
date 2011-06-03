package org.waterforpeople.mapping.portal.client.widgets.component;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyedLocaleDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.AccessPointSearchControl.Mode;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * UI component for searching/editing/creating SurveyedLocale objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyedLocaleManager extends Composite implements
		DataTableBinder<SurveyedLocaleDto>,
		DataTableListener<SurveyedLocaleDto>, ClickHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	private static final String DEFAULT_SORT_FIELD = "key";
	private static final Integer PAGE_SIZE = 20;
	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader(TEXT_CONSTANTS.id(), "key", true),
			new DataTableHeader(TEXT_CONSTANTS.communityCode(), "identifier",
					true),
			new DataTableHeader(TEXT_CONSTANTS.latitude(), "latitude", true),
			new DataTableHeader(TEXT_CONSTANTS.longitude(), "longitude", true),
			new DataTableHeader(TEXT_CONSTANTS.pointType(), "localeType", true),
			new DataTableHeader(TEXT_CONSTANTS.lastUpdated(),
					"lastSurveyedDate", true),
			new DataTableHeader(TEXT_CONSTANTS.editDelete()) };

	private Panel contentPanel;
	private PaginatedDataTable<SurveyedLocaleDto> dataTable;
	private AccessPointSearchControl searchControl;
	private Button searchButton;

	public SurveyedLocaleManager() {
		contentPanel = new VerticalPanel();
		contentPanel.add(constructSearchPanel());
		dataTable = new PaginatedDataTable<SurveyedLocaleDto>(
				DEFAULT_SORT_FIELD, this, this, false);
		contentPanel.add(dataTable);
	}

	/**
	 * constructs the search control and binds the button listeners to the
	 * search button.
	 * 
	 * @return
	 */
	private Composite constructSearchPanel() {
		CaptionPanel cap = new CaptionPanel(TEXT_CONSTANTS.searchCriteria());
		Panel content = new VerticalPanel();
		searchControl = new AccessPointSearchControl(Mode.LOCALE);
		searchButton = new Button(TEXT_CONSTANTS.search());
		content.add(searchControl);
		content.add(searchButton);
		cap.add(content);
		searchButton.addClickHandler(this);
		return cap;
	}

	@Override
	public void onItemSelected(SurveyedLocaleDto item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestData(String cursor, boolean isResort) {
		// TODO Auto-generated method stub

	}

	@Override
	public DataTableHeader[] getHeaders() {
		return HEADERS;
	}

	@Override
	public void bindRow(Grid grid, SurveyedLocaleDto item, int row) {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE;
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == searchButton) {
			// TODO: perform search
		}

	}

}
