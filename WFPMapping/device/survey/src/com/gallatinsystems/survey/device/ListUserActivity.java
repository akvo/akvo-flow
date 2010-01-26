package com.gallatinsystems.survey.device;



import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;

public class ListUserActivity extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private static final int INSERT_ID = Menu.FIRST;
	

	private SurveyDbAdapter databaseAdaptor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userlist);
		databaseAdaptor = new SurveyDbAdapter(this);
		databaseAdaptor.open();
		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {
		// Get all of the rows from the database and create the item list
		Cursor userCursor = databaseAdaptor.fetchUsers();
		startManagingCursor(userCursor);

		// Create an array to specify the fields we want to display in the list

		String[] from = new String[] { SurveyDbAdapter.DISP_NAME_COL};

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.userdisp };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.userow, userCursor, from, to);
		setListAdapter(notes);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.adduser);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createUser();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}
	

	private void createUser() {
		Intent i = new Intent(this, UserEditActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, UserEditActivity.class);
		i.putExtra(SurveyDbAdapter.USER_ID_COL, id);		
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();

	}
}
