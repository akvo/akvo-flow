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

package com.gallatinsystems.survey.device.view.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.gallatinsystems.survey.device.util.HttpUtil;

/**
 * Adapter that can be used to populate lists or galleries with a list of help
 * images passed in
 * 
 * @author Christopher Fagiani
 * 
 */
public class HelpImageBrowserAdapter extends BaseAdapter {

	private static final String TAG = "HelpImageBrowserAdapter";
	private ArrayList<String> imageUrls;
	private HashMap<Integer, Bitmap> bitmaps;
	private Context context;
	private String cacheDir;

	public HelpImageBrowserAdapter(Context ctx, ArrayList<String> imageUrls,
			String cacheDir) {
		context = ctx;
		this.imageUrls = imageUrls;
		this.cacheDir = cacheDir;
		bitmaps = new HashMap<Integer, Bitmap>();
	}

	@Override
	public int getCount() {
		return imageUrls.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * returns the bitmap associated with the url at the position indicated by
	 * the position argument. If the bitmap has not yet been downloaded, this
	 * will download it and cache it for subsequent lookups
	 * 
	 * @param position
	 * @return
	 */
	public Bitmap getImageBitmap(int position) {
		Bitmap bitmap = bitmaps.get(position);
		if (bitmap == null) {
			try {
				bitmap = HttpUtil.getRemoteImage(imageUrls.get(position),
						cacheDir);
				bitmaps.put(position, bitmap);
			} catch (Exception e) {
				Log.e(TAG, "Could not load image into bitmap", e);
			}
		}
		return bitmap;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView i = new ImageView(context);
		i.setImageBitmap(getImageBitmap(position));
		i.setLayoutParams(new Gallery.LayoutParams(150, 100));
		i.setScaleType(ImageView.ScaleType.FIT_XY);

		return i;
	}

}
