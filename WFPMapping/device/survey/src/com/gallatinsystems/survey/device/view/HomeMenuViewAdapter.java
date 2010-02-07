package com.gallatinsystems.survey.device.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;

public class HomeMenuViewAdapter extends BaseAdapter {
	private LayoutInflater inflater;

	public HomeMenuViewAdapter(Context c) {
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

	// create a new ImageView for each item referenced by the Adapter
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
		text.setText(buttonLabels[position]);
		return item;
	}

	// references to our buttons
	private Integer[] buttonImages = { R.drawable.users, R.drawable.checklist,
			R.drawable.checklist, R.drawable.checklist, R.drawable.map,
			R.drawable.config

	};

	private Integer[] buttonLabels = { R.string.userlabel, R.string.wplabel,
			R.string.hhlabel, R.string.publabel, R.string.maplabel,
			R.string.settingslabel };

	public static final String USER_OP = "USER";
	public static final String WPS_OP = "WP";
	public static final String HHS_OP = "HH";
	public static final String PUBS_OP = "PUB";
	public static final String MAP_OP = "MAP";
	public static final String CONF_OP = "CONF";
	public static final String[] operations = { USER_OP, WPS_OP, HHS_OP,
			PUBS_OP, MAP_OP, CONF_OP };

}
