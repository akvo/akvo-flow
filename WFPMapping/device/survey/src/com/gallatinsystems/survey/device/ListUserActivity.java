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
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;

/**
 * This activity will list all the users in the database and present them in
 * list form. From the list they can be either edited or selected for use as the
 * "current user". New users can also be added to the system using this activity
 * by activating the menu.
 * 
 * @author Christopher Fagiani
 * 
 */
public class ListUserActivity extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int EDIT_ID = Menu.FIRST + 1;

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

	/**
	 * loads all users from the database and binds it to the listView via a
	 * SimpleCursorAdaptor
	 */
	private void fillData() {
		// Get all of the rows from the database and create the item list
		Cursor userCursor = databaseAdaptor.fetchUsers();
		startManagingCursor(userCursor);

		// Create an array to specify the fields we want to display in the list

		String[] from = new String[] { SurveyDbAdapter.DISP_NAME_COL };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.userdisp };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.userow, userCursor, from, to);
		setListAdapter(notes);
	}

	/**
	 * presents a single button ("Add User") when the user clicks the menu key
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.adduser);
		return true;
	}

	/**
	 * handles the button press for the "add" button on the menu
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createUser();
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
		menu.add(0, EDIT_ID, 0, R.string.editmenu);
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
			i.putExtra(SurveyDbAdapter.USER_ID_COL, info.id);
			startActivityForResult(i, ACTIVITY_EDIT);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * spawn the editUserActivity in "create" mode
	 */
	private void createUser() {
		Intent i = new Intent(this, UserEditActivity.class);
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
		Cursor user = databaseAdaptor.fetchUser(id);
		startManagingCursor(user);
		intent.putExtra(SurveyDbAdapter.USER_ID_COL, user.getString(user
				.getColumnIndexOrThrow(SurveyDbAdapter.USER_ID_COL)));
		intent.putExtra(SurveyDbAdapter.DISP_NAME_COL, user.getString(user
				.getColumnIndexOrThrow(SurveyDbAdapter.DISP_NAME_COL)));
		intent.putExtra(SurveyDbAdapter.EMAIL_COL, user.getString(user
				.getColumnIndexOrThrow(SurveyDbAdapter.EMAIL_COL)));
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
	
	protected void onDestroy(){
		super.onDestroy();
		if(databaseAdaptor != null){
			databaseAdaptor.close();
		}
	}
}
