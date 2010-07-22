package com.gallatinsystems.common.data.spreadsheet;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.gallatinsystems.common.data.spreadsheet.domain.ColumnContainer;
import com.gallatinsystems.common.data.spreadsheet.domain.RowContainer;
import com.gallatinsystems.common.data.spreadsheet.domain.SpreadsheetContainer;
import com.gallatinsystems.common.util.PropertyUtil;
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
import com.google.gdata.util.ServiceException;

public class GoogleSpreadsheetAdapter {
	private static final Logger log = Logger
			.getLogger(GoogleSpreadsheetAdapter.class.getName());

	private String google_spreadsheet_url;
	private static final String RANDOM_SPREADSHEET_NAME = "exampleCo-exampleApp-1.0";

	SpreadsheetFeed feed = null;

	public GoogleSpreadsheetAdapter(String sessionToken, PrivateKey privateKey)
			throws IOException, ServiceException {
		google_spreadsheet_url = new PropertyUtil()
				.getProperty("google_spreadsheet_url");
		if (service == null) {
			service = new SpreadsheetService(RANDOM_SPREADSHEET_NAME);
			service.setAuthSubToken(sessionToken, privateKey);
		}
		if (metafeedUrl == null) {
			metafeedUrl = new URL(google_spreadsheet_url);
		}
		if (feed == null) {
			try {
				feed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
			} catch (IOException iex) {
				try {
					wait(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				feed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
			}
		}

	}

	SpreadsheetService service;

	public static void main(String[] args) throws IOException, ServiceException {
		String spreadsheetName = args[0];
		String googleUserName = args[1];
		String googlePassword = args[2];

		new GoogleSpreadsheetAdapter(null, null)
				.loadSpreadsheet(spreadsheetName);
	}

	public SpreadsheetContainer getSpreadsheetContents(String spreadsheetName)
			throws IOException, ServiceException {
		return loadSpreadsheet(spreadsheetName);
	}

	public ArrayList<String> listColumns(String spreadsheetName)
			throws IOException, ServiceException {
		SpreadsheetFeed feed = service.getFeed(metafeedUrl,
				SpreadsheetFeed.class);

		List<SpreadsheetEntry> spreadsheets = feed.getEntries();
		for (int i = 0; i < spreadsheets.size(); i++) {
			SpreadsheetEntry entry = spreadsheets.get(i);
			if (entry.getTitle().getPlainText().equals(spreadsheetName)) {
				List<WorksheetEntry> worksheets = entry.getWorksheets();
				for (int j = 0; j < worksheets.size(); j++) {
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

	public ArrayList<String> listSpreasheets(String feedURL)
			throws IOException, ServiceException, GeneralSecurityException {

		log.info("created spreadsheetfeed");

		List<SpreadsheetEntry> spreadsheets = feed.getEntries();
		log.info("got spreadsheet entry list: " + spreadsheets.size());
		ArrayList<String> spreadsheetNamesList = new ArrayList<String>();
		for (int i = 0; i < spreadsheets.size(); i++) {
			SpreadsheetEntry entry = spreadsheets.get(i);
			String title = entry.getTitle().getPlainText();
			spreadsheetNamesList.add(title);
		}
		return spreadsheetNamesList;
	}

	private URL metafeedUrl;

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

	private SpreadsheetContainer loadSpreadsheet(String spreadsheetName)
			throws IOException, ServiceException {
		log.info("Inside loadSpreadsheet");
		List<SpreadsheetEntry> spreadsheets = feed.getEntries();

		for (int i = 0; i < spreadsheets.size(); i++) {
			SpreadsheetEntry entry = spreadsheets.get(i);			
			if (entry.getTitle().getPlainText().equals(spreadsheetName)) {
				log.info("Found spreadsheet");
				List<WorksheetEntry> worksheets = entry.getWorksheets();
				log.info("Got worksheet");
				//for (int j = 0; j < worksheets.size(); j++) {
					WorksheetEntry worksheet = worksheets.get(0);
					log.info("got 0 worksheet contents.");
					return getListFeed(worksheet);
				//}
			}
		}
		return null;
	}

	private SpreadsheetContainer getListFeed(WorksheetEntry worksheetEntry)
			throws IOException, ServiceException {
		URL listFeedUrl = worksheetEntry.getListFeedUrl();
		ListFeed feed = service.getFeed(listFeedUrl, ListFeed.class);
		SpreadsheetContainer sbc = new SpreadsheetContainer();
		int i = 0;
		for (ListEntry entry : feed.getEntries()) {
			// row			
			RowContainer row = new RowContainer();
			ArrayList<ColumnContainer> colList = new ArrayList<ColumnContainer>();
			for (String tag : entry.getCustomElements().getTags()) {
				// col in row
				ColumnContainer col = new ColumnContainer();

				col.setColName(tag);
				String val = entry.getCustomElements().getValue(tag);
				if(val != null && val.length()>500){
					val = val.substring(0, 500);
				}
				col.setColContents(val);
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
