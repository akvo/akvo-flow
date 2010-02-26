package com.gallatinsystems.survey.device.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.util.ArrayUtil;

public class HomeMenuViewAdapter extends BaseAdapter {

	// TODO: move constants out into ConstantUtil class
	private static final String SURVEY_TYPE = "survey";

	// references to our buttons
	private Integer[] buttonImages;

	// this is of type Object since the values can be Integers or Strings
	private Object[] buttonLabels;

	private ArrayList<String> surveyIds;

	private static final Integer[] preSurveyButtons = { R.drawable.users };
	private static final Integer[] preSurveyLabels = { R.string.userlabel };
	private static final Integer[] postSurveyButtons = { R.drawable.plotting,
			R.drawable.settings };
	private static final Integer[] postSurveyLabels = { R.string.plottinglabel,
			R.string.settingslabel };

	public static final String USER_OP = "USER";
	public static final String WPS_OP = "WP";
	public static final String HHS_OP = "HH";
	public static final String PUBS_OP = "PUB";
	public static final String MAP_OP = "MAP";
	public static final String SURVEY_OP = "SURVEY";
	public static final String CONF_OP = "CONF";
	public static final String PLOT_OP = "PLOT";
	// public static final String[] operations = { USER_OP, WPS_OP, HHS_OP,
	// PUBS_OP, MAP_OP, PLOT_OP, CONF_OP };

	private ArrayList<String> operations;

	private LayoutInflater inflater;

	public HomeMenuViewAdapter(Context c) {
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initializeValues(c);

	}

	private void initializeValues(Context c) {
		operations = new ArrayList<String>();
		surveyIds = new ArrayList<String>();
		SurveyDbAdapter database = new SurveyDbAdapter(c);
		database.open();
		ArrayList<Survey> surveys = database.listSurveys();
		database.close();

		buttonImages = new Integer[surveys.size() + preSurveyButtons.length
				+ postSurveyButtons.length];
		buttonLabels = new Object[surveys.size() + preSurveyLabels.length
				+ postSurveyLabels.length];
		ArrayUtil.combineArrays(buttonImages, preSurveyButtons, 0);
		ArrayUtil.combineArrays(buttonLabels, preSurveyLabels, 0);

		operations.add(USER_OP);

		for (int i = 0; i < surveys.size(); i++) {
			surveyIds.add(surveys.get(i).getId());
			if (SURVEY_TYPE.equalsIgnoreCase(surveys.get(i).getType())) {
				buttonImages[preSurveyButtons.length + i] = R.drawable.checklist;
			} else {
				buttonImages[preSurveyButtons.length + i] = R.drawable.map;
			}
			buttonLabels[preSurveyLabels.length + i] = surveys.get(i).getName();
			operations.add(SURVEY_OP);
		}

		ArrayUtil.combineArrays(buttonImages, postSurveyButtons,
				preSurveyButtons.length + surveys.size());
		ArrayUtil.combineArrays(buttonLabels, postSurveyLabels,
				preSurveyButtons.length + surveys.size());

		operations.add(PLOT_OP);
		operations.add(CONF_OP);

	}

	public int getCount() {
		return buttonImages.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	/**
	 * creates a new ImageView for each item referenced by the Adapter
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		View item = null;
		if (convertView == null) { // if it's not recycled, initialize some
			item = inflater.inflate(R.layout.homebuttonlayout, parent, false);
		} else {
			item = convertView;
		}

		ImageView button = (ImageView) item.findViewById(R.id.homebuttonimg);
		TextView text = (TextView) item.findViewById(R.id.buttonText);

		button.setImageResource(buttonImages[position]);
		if (buttonLabels[position] instanceof String) {
			text.setText((String) buttonLabels[position]);
		} else {
			text.setText((Integer) buttonLabels[position]);
		}
		return item;
	}

	/**
	 * returns the survey id of the selected survey
	 * 
	 * @param idx
	 * @return
	 */
	public String getSelectedSurveyId(int idx) {
		return surveyIds.get(idx - preSurveyButtons.length);
	}

	/**
	 * returns the operation code for the selected index
	 * 
	 * @param index
	 * @return
	 */
	public String getSelectedOperation(int index) {
		return operations.get(index);
	}
}
