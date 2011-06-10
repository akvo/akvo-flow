package com.gallatinsystems.survey.device.activity;

import android.content.Intent;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * This activity will list all the users in the database and present them in
 * list form. From the list they can be either edited or selected for use as the
 * "current user". New users can also be added to the system using this activity
 * by activating the menu.
 * 
 * @author Christopher Fagiani
 * 
 */
public class ListUserActivity extends AbstractListEditActivity {

	private static final String EDIT_USER_ACTIVITY_CLASS = "com.gallatinsystems.survey.device.activity.UserEditActivity";
	protected static final int DELETE_ID = Menu.FIRST + 2;
	private int deleteStringId;

	/**
	 * when a list item is clicked, get the user id and name of the selected
	 * item and return it to the calling activity.
	 */
	@Override
	protected void onListItemClick(ListView list, View view, int position,
			long id) {
		super.onListItemClick(list, view, position, id);
		Intent intent = new Intent();
		Cursor user = databaseAdaptor.findUser(id);
		startManagingCursor(user);
		intent.putExtra(ConstantUtil.ID_KEY, user.getString(user
				.getColumnIndexOrThrow(SurveyDbAdapter.PK_ID_COL)));
		intent.putExtra(ConstantUtil.DISPLAY_NAME_KEY, user.getString(user
				.getColumnIndexOrThrow(SurveyDbAdapter.DISP_NAME_COL)));
		intent.putExtra(ConstantUtil.EMAIL_KEY, user.getString(user
				.getColumnIndexOrThrow(SurveyDbAdapter.EMAIL_COL)));
		// save the user to the prefs table
		databaseAdaptor.savePreference(ConstantUtil.LAST_USER_SETTING_KEY, id
				+ "");
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * fetches the data for this view (users) from the database. This method
	 * assumes that the database has been opened.
	 */
	@Override
	protected Cursor getData() {
		return databaseAdaptor.listUsers();

	}

	@Override
	protected void initializeFields() {
		instructionsStringId = R.string.userinstructions;
		emptyStringId = R.string.nouser;
		addStringId = R.string.adduser;
		editStringId = R.string.editmenu;
		deleteStringId = R.string.deleteusermenu;
		editActivityClassName = EDIT_USER_ACTIVITY_CLASS;

	}

	/**
	 * presents an edit and "delete" option when the user long-clicks a list
	 * item
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// the parent method adds the "edit" button
		menu.add(1, DELETE_ID, 0, deleteStringId);
	}

	/**
	 * spawns an activity (configured in initializeFields) in Edit mode
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (!super.onContextItemSelected(item)) {
			switch (item.getItemId()) {
			case DELETE_ID:
				AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
						.getMenuInfo();
				handleDelete(info.id + "");
				return true;

			}
		}
		return false;
	}
	
	private void handleDelete(String id){
		databaseAdaptor.deleteUser(new Long(id));
		fillData();
	}
}
