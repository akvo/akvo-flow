package com.gallatinsystems.survey.device.view.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
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
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * 
 * Adapter class to populate the grid view on the survey home page. It will
 * display a combination of statically defined menu options as well as surveys
 * buttons based on values in the database.
 * 
 * @author Christopher Fagiani
 * 
 */
public class HomeMenuViewAdapter extends BaseAdapter {

	private static final String TAG = "Home Menu View Adapter";	
	
	/**
	 * statically defined menu options
	 */
	private static final Integer[] preSurveyButtons = { R.drawable.users };
	private static final Integer[] preSurveyLabels = { R.string.userlabel };
	private static final Integer[] postSurveyButtons = { R.drawable.disk, R.drawable.plotting,
			R.drawable.settings};//, R.drawable.infoactivity};
	private static final Integer[] postSurveyLabels = {  R.string.reviewlabel,R.string.plottinglabel,
			R.string.settingslabel};//, R.string.nearbylabel };


	// references to our buttons
	private Integer[] buttonImages = new Integer[0];
	// this is of type Object since the values can be Integers or Strings
	private Object[] buttonLabels;
	private ArrayList<Survey> surveys;	
	private ArrayList<String> operations;	
	private LayoutInflater inflater;

	/**
	 * initializes the view inflater
	 * 
	 * @param c
	 */
	public HomeMenuViewAdapter(Context c) {
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		

	}

	
	/**
	 * gets all surveys from the db and uses them to instantiate the views
	 * 
	 * @param c
	 */
	public void loadData(Context c) {
		fetchSurveys(c);
		initializeValues();
	}

	/**
	 * builds internal data structures to facilitate the combination of both
	 * static and dynamic menu options
	 */
	private void initializeValues() {
		operations = new ArrayList<String>();

		buttonImages = new Integer[surveys.size() + preSurveyButtons.length
				+ postSurveyButtons.length];
		buttonLabels = new Object[surveys.size() + preSurveyLabels.length
				+ postSurveyLabels.length];
		ArrayUtil.combineArrays(buttonImages, preSurveyButtons, 0);
		ArrayUtil.combineArrays(buttonLabels, preSurveyLabels, 0);

		operations.add(ConstantUtil.USER_OP);

		for (int i = 0; i < surveys.size(); i++) {
			if (ConstantUtil.SURVEY_TYPE.equalsIgnoreCase(surveys.get(i).getType())) {
				buttonImages[preSurveyButtons.length + i] = R.drawable.checklist;
			} else {
				buttonImages[preSurveyButtons.length + i] = R.drawable.map;
			}
			buttonLabels[preSurveyLabels.length + i] = surveys.get(i).getName();
			operations.add(ConstantUtil.SURVEY_OP);
		}

		ArrayUtil.combineArrays(buttonImages, postSurveyButtons,
				preSurveyButtons.length + surveys.size());
		ArrayUtil.combineArrays(buttonLabels, postSurveyLabels,
				preSurveyButtons.length + surveys.size());
		
		operations.add(ConstantUtil.REVIEW_OP);
		operations.add(ConstantUtil.PLOT_OP);
		operations.add(ConstantUtil.CONF_OP);
		//operations.add(ConstantUtil.NEARBY_OP);		
		notifyDataSetChanged();
	}

	/**
	 * gets surveys from the database and closes the db adaptor
	 * 
	 * @param c
	 * @return
	 */
	private ArrayList<Survey> fetchSurveys(Context c) {
		SurveyDbAdapter database = new SurveyDbAdapter(c);
		database.open();
		surveys = database.listSurveys(null);
		database.close();
		return surveys;
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
		item.setTag(operations.get(position));
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
	 * determines the survey that corresponds to the position within the grid
	 * and calls delete on the database for that survey. It then calls the
	 * initialize method to reset internal structures
	 * 
	 * @param position
	 * @param c
	 */
	public void deleteItem(int position, Context c) {
		SurveyDbAdapter database = new SurveyDbAdapter(c);
		database.open();
		Survey itemToDelete = getSelectedSurvey(position);
		if (itemToDelete != null) {
			database.deleteSurvey(itemToDelete.getId(),false);
			database.close();
			surveys.remove(itemToDelete);
			// update the view
			initializeValues();			
		}
	}

	/**
	 * returns the survey id of the selected survey
	 * 
	 * @param idx
	 * @return
	 */
	public Survey getSelectedSurvey(int idx) {
		if (idx - preSurveyButtons.length < surveys.size()) {
			return surveys.get(idx - preSurveyButtons.length);
		} else {
			Log.e(TAG, "Selected item exceeds survey list size");
			return null;
		}
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
