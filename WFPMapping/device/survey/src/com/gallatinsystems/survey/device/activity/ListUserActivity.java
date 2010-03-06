package com.gallatinsystems.survey.device.activity;

import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ListView;

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

	private static final String EDIT_USER_ACTIVITY_CLASS = "com.gallatinsystems.survey.device.UserEditActivity";

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
		//save the user to the prefs table
		databaseAdaptor.savePreference(ConstantUtil.LAST_USER_SETTING_KEY, id+"");		
		setResult(RESULT_OK, intent);
		finish();
	}

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
		editActivityClassName = EDIT_USER_ACTIVITY_CLASS;

	}
}
