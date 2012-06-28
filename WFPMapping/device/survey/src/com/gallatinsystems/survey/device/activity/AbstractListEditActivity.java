/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package com.gallatinsystems.survey.device.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * Abstract class that defines behaviors for simple list views used across the
 * survey application. Activities that subclass this activity will present a
 * simple list on the screen with a single text view containing instructions.
 * Long clicks on an item will launch an edit activity and the menu key will
 * display an "add new" button on the context menu.
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class AbstractListEditActivity extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;

	protected static final int INSERT_ID = Menu.FIRST;
	protected static final int EDIT_ID = Menu.FIRST + 1;

	protected SurveyDbAdapter databaseAdaptor;

	protected int instructionsStringId;
	protected int emptyStringId;
	protected int addStringId;
	protected int editStringId;
	protected String editActivityClassName;
	protected final static String TAG = "EditListActivity";
	private Cursor dataCursor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.itemlist);
		initializeFields();
		TextView inst = (TextView) findViewById(R.id.instructions);
		TextView emp = (TextView) findViewById(android.R.id.empty);
		inst.setText(instructionsStringId);
		emp.setText(emptyStringId);

		databaseAdaptor = new SurveyDbAdapter(this);
		registerForContextMenu(getListView());
	}

	public void onResume() {
		super.onResume();
		databaseAdaptor.open();
		fillData();
	}

	/**
	 * sets up the fields that will be used by the abstract class to hydrate the
	 * view
	 */
	protected abstract void initializeFields();

	/**
	 * implementations should fetch all the data that will be listed from the DB
	 * 
	 * @return
	 */
	protected abstract Cursor getData();

	/**
	 * loads all users from the database and binds it to the listView via a
	 * SimpleCursorAdaptor
	 */
	protected void fillData() {
		if (dataCursor != null) {
			try {
				dataCursor.close();
			} catch (Exception e) {
				Log.w(TAG, "Could not close old cursor", e);
			}
		}
		// Get all of the rows from the database and create the item list
		dataCursor = getData();
		// Create an array to specify the fields we want to display in the list
		String[] from = new String[] { SurveyDbAdapter.DISP_NAME_COL };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.itemheader };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.itemlistrow, dataCursor, from, to);
		setListAdapter(notes);
	}

	/**
	 * presents a single button ("Add") when the user clicks the menu key
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, addStringId);
		return true;
	}

	/**
	 * handles the button press for the "add" button on the menu
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			handleCreate(null);
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
		menu.add(0, EDIT_ID, 0, editStringId);
	}

	/**
	 * spawns an activity (configured in initializeFields) in Edit mode
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case EDIT_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			handleCreate(info.id + "");
			return true;

		}
		return super.onContextItemSelected(item);
	}

	/**
	 * spawns an activity (configured in initializeFields) in "create" mode
	 */
	private void handleCreate(String id) {
		try {
			Intent i = new Intent(this, Class.forName(editActivityClassName));
			if (id != null) {
				i.putExtra(ConstantUtil.ID_KEY, id);
			}
			startActivityForResult(i, ACTIVITY_CREATE);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "Could not find edit activity class", e);
		}
	}

	/**
	 * when a list item is clicked, get the user id and name of the selected
	 * item and return it to the calling activity.
	 */
	@Override
	protected void onListItemClick(ListView list, View view, int position,
			long id) {
		super.onListItemClick(list, view, position, id);
	}

	/**
	 * update the list from the database so it reflects the edit that was just
	 * completed
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
	}

	protected void onDestroy() {
		super.onDestroy();
		if (dataCursor != null) {
			try {
				dataCursor.close();
			} catch (Exception e) {

			}
		}
		if (databaseAdaptor != null) {
			databaseAdaptor.close();
		}
	}

	protected void onPause() {
		super.onPause();
	}
}
