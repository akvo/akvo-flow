package com.gallatinsystems.survey.device;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;

/**
 * Lists out all the plots currently in the DB and allows the user to
 * edit/create a new one
 * 
 * TODO: see if we can refactor this and the UserListActivity into a generic
 * class. they're almost identical. Probably some sort of delegate (since we
 * can't control Activity construction)
 * 
 * @author Christopher Fagiani
 * 
 */
public class ListPlotActivity extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int EDIT_ID = Menu.FIRST + 1;

	private SurveyDbAdapter databaseAdaptor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.itemlist);

		TextView inst = (TextView) findViewById(R.id.instructions);
		TextView emp = (TextView) findViewById(android.R.id.empty);
		inst.setText(R.string.plotinstructions);
		emp.setText(R.string.noplots);

		databaseAdaptor = new SurveyDbAdapter(this);
		databaseAdaptor.open();
		fillData();
		registerForContextMenu(getListView());
	}

	/**
	 * loads all plots from the database and binds it to the listView via a
	 * SimpleCursorAdaptor
	 */
	private void fillData() {
		// Get all of the rows from the database and create the item list
		Cursor plotCursor = databaseAdaptor.listPlots();
		startManagingCursor(plotCursor);

		// Create an array to specify the fields we want to display in the list

		String[] from = new String[] { SurveyDbAdapter.DISP_NAME_COL };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.itemheader };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.itemlistrow, plotCursor, from, to);
		setListAdapter(notes);
	}

	/**
	 * presents a single button ("Add Plot") when the user clicks the menu key
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.addplot);
		return true;
	}

	/**
	 * handles the button press for the "add" button on the menu
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createPlot();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * presents a single "edit" option when the user long-clicks a list item
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, EDIT_ID, 0, R.string.editplot);
	}

	/**
	 * spawns the UserEditActivity in Edit mode
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case EDIT_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			Intent i = new Intent(this, UserEditActivity.class);
			i.putExtra(SurveyDbAdapter.PK_ID_COL, info.id);
			startActivityForResult(i, ACTIVITY_EDIT);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * spawn the editPlotActivity in "create" mode
	 */
	private void createPlot() {
		Intent i = new Intent(this, PlotEditActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	/**
	 * when a list item is clicked, get the user id and name of the selected
	 * item and return it to the calling activity.
	 */
	@Override
	protected void onListItemClick(ListView list, View view, int position,
			long id) {
		super.onListItemClick(list, view, position, id);
		Intent intent = new Intent();
		Cursor plot = databaseAdaptor.findPlot(id);
		startManagingCursor(plot);
		intent.putExtra(SurveyDbAdapter.PK_ID_COL, plot.getString(plot
				.getColumnIndexOrThrow(SurveyDbAdapter.PK_ID_COL)));
		intent.putExtra(SurveyDbAdapter.DISP_NAME_COL, plot.getString(plot
				.getColumnIndexOrThrow(SurveyDbAdapter.DISP_NAME_COL)));
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * update the list from the database so it reflects the edit that was just
	 * completed
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	protected void onDestroy() {
		super.onDestroy();
		if (databaseAdaptor != null) {
			databaseAdaptor.close();
		}
	}
}
