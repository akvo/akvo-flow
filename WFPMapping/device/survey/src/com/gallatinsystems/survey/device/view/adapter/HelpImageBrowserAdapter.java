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

	public HelpImageBrowserAdapter(Context ctx, ArrayList<String> imageUrls) {
		context = ctx;
		this.imageUrls = imageUrls;
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
				// TODO: check local cache first
				bitmap = HttpUtil.getRemoteImage(imageUrls.get(position));
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
