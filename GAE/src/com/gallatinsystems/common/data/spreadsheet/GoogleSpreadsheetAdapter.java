package com.gallatinsystems.common.data.spreadsheet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.gallatinsystems.common.data.spreadsheet.domain.ColumnContainer;
import com.gallatinsystems.common.data.spreadsheet.domain.RowContainer;
import com.gallatinsystems.common.data.spreadsheet.domain.SpreadsheetContainer;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.spreadsheet.Column;
import com.google.gdata.data.spreadsheet.Data;
import com.google.gdata.data.spreadsheet.Header;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.TableEntry;
import com.google.gdata.data.spreadsheet.Worksheet;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class GoogleSpreadsheetAdapter {
	private static final Logger log = Logger
			.getLogger(GoogleSpreadsheetAdapter.class.getName());

	Properties props;
	private String google_user_name;
	private String google_password;
	private String google_spreadsheet_url;
	private static final String RANDOM_SPREADSHEET_NAME = "exampleCo-exampleApp-1.0";

	public GoogleSpreadsheetAdapter() {
		props = System.getProperties();
		google_user_name = props.getProperty("google_user_name");
		google_password = props.getProperty("google_password");
		google_spreadsheet_url = props.getProperty("google_spreadsheet_url");
	}

	SpreadsheetService service;

	public static void main(String[] args) throws IOException, ServiceException {
		String spreadsheetName = args[0];
		String googleUserName = args[1];
		String googlePassword = args[2];

		new GoogleSpreadsheetAdapter().loadSpreadsheet(spreadsheetName,
				googleUserName, googlePassword);
	}

	public SpreadsheetContainer getSpreadsheetContents(String spreadsheetName)
			throws IOException, ServiceException {
		return loadSpreadsheet(spreadsheetName, google_user_name,
				google_password);
	}
	
	public ArrayList<String> listColumns(String spreadsheetName) throws IOException, ServiceException{
		return listColumns(spreadsheetName,google_user_name,google_password);
	}

	private ArrayList<String> listColumns(String spreadsheetName, String googleUserName, String googlePassword) throws IOException, ServiceException {
		service = new SpreadsheetService(RANDOM_SPREADSHEET_NAME);
		service.setUserCredentials(googleUserName, googlePassword);

		URL metafeedUrl = new URL(google_spreadsheet_url);
		SpreadsheetFeed feed = service.getFeed(metafeedUrl,
				SpreadsheetFeed.class);
		List<SpreadsheetEntry> spreadsheets = feed.getEntries();
		for (int i = 0; i < spreadsheets.size(); i++) {
			SpreadsheetEntry entry = spreadsheets.get(i);
			if (entry.getTitle().getPlainText().equals(spreadsheetName)) {
				List<WorksheetEntry> worksheets = entry.getWorksheets();
				for (int j = 0; i < worksheets.size(); i++) {
					WorksheetEntry worksheet = worksheets.get(j);
					String title = worksheet.getTitle().getPlainText();
					int rowCount = worksheet.getRowCount();
					int colCount = worksheet.getColCount();

					return listColumns(worksheet);
				}
			}
		}
		return null;

	}

	private ArrayList<String> listColumns(WorksheetEntry worksheetEntry)
			throws IOException, ServiceException {
		URL listFeedUrl = worksheetEntry.getListFeedUrl();
		ListFeed feed = service.getFeed(listFeedUrl, ListFeed.class);
		ArrayList<String> columns = new ArrayList<String>();
		int i = 0;
		for (ListEntry entry : feed.getEntries()) {
			// row
			for (String tag : entry.getCustomElements().getTags()) {
				columns.add(tag);
			}
			break;
		}
		return columns;
	}

	private SpreadsheetContainer loadSpreadsheet(String spreadsheetName,
			String googleUserName, String googlePassword) throws IOException,
			ServiceException {
		service = new SpreadsheetService(RANDOM_SPREADSHEET_NAME);
		service.setUserCredentials(googleUserName, googlePassword);

		URL metafeedUrl = new URL(google_spreadsheet_url);
		SpreadsheetFeed feed = service.getFeed(metafeedUrl,
				SpreadsheetFeed.class);
		List<SpreadsheetEntry> spreadsheets = feed.getEntries();
		for (int i = 0; i < spreadsheets.size(); i++) {
			SpreadsheetEntry entry = spreadsheets.get(i);
			if (entry.getTitle().getPlainText().equals(spreadsheetName)) {
				List<WorksheetEntry> worksheets = entry.getWorksheets();
				for (int j = 0; i < worksheets.size(); i++) {
					WorksheetEntry worksheet = worksheets.get(j);
					String title = worksheet.getTitle().getPlainText();
					int rowCount = worksheet.getRowCount();
					int colCount = worksheet.getColCount();

					return getListFeed(worksheet);
				}
			}
		}
		return null;
	}

	private SpreadsheetContainer getListFeed(WorksheetEntry worksheetEntry)
			throws IOException, ServiceException {
		URL listFeedUrl = worksheetEntry.getListFeedUrl();
		ListFeed feed = service.getFeed(listFeedUrl, ListFeed.class);
		SpreadsheetContainer sbc = new SpreadsheetContainer();
		for (ListEntry entry : feed.getEntries()) {
			// row
			StringBuilder sb = new StringBuilder();
			RowContainer row = new RowContainer();
			ArrayList<ColumnContainer> colList = new ArrayList<ColumnContainer>();
			for (String tag : entry.getCustomElements().getTags()) {
				// col in row
				ColumnContainer col = new ColumnContainer();

				col.setColName(tag);
				col.setColContents(entry.getCustomElements().getValue(tag));
				colList.add(col);
			}
			row.setColumnContainersList(colList);
			sbc.addRowContainer(row);
		}
		return sbc;
	}

	private TableEntry createTableFeed(SpreadsheetEntry spreadsheetEntry)
			throws IOException, ServiceException {
		TableEntry tableEntry = new TableEntry();

		FeedURLFactory factory = FeedURLFactory.getDefault();
		URL tableFeedUrl = factory.getTableFeedUrl(spreadsheetEntry.getKey());

		// Specify a basic table:
		tableEntry.setTitle(new PlainTextConstruct("New Table"));
		tableEntry.setWorksheet(new Worksheet("Sheet1"));
		tableEntry.setHeader(new Header(1));

		// Specify columns in the table, start row, number of rows.
		Data tableData = new Data();
		tableData.setNumberOfRows(0);
		// Start row index cannot overlap with header row.
		tableData.setStartIndex(2);
		// This table has only one column.
		tableData.addColumn(new Column("A", "Column A"));

		tableEntry.setData(tableData);
		service.insert(tableFeedUrl, tableEntry);
		return tableEntry;
	}

	private void getTableFeed(SpreadsheetEntry spreadsheetEntry)
			throws IOException, ServiceException {
		TableEntry tableEntry = createTableFeed(spreadsheetEntry);
		// URL recordFeedUrl = tableEntry.getRecordFeedUrl();
		// RecordFeed feed = service.getFeed(recordFeedUrl, RecordFeed.class);
		// for (RecordEntry entry : feed.getEntries()) {
		// log.info("Title: " + entry.getTitle().getPlainText());
		// for (Field field : entry.getFields()) {
		// log.info("<field name=" + field.getName() + ">"
		// + field.getValue() + "</field>");
		// }
		// }
	}
}
