package com.gallatinsystems.launcher.device.view;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gallatinsystems.launcher.device.R;
import com.gallatinsystems.launcher.device.domain.AppDescriptor;

/**
 * array adaptor used to bind AppDescriptor objects to a grid view. this class
 * was adapted from code in the "Home" Android sample application:
 * <code>http://developer.android.com/resources/samples/Home/src/com/example/android/home/Home.html</code>
 * 
 * @author Christopher Fagiani
 * 
 */
public class ApplicationArrayAdapter extends ArrayAdapter<AppDescriptor> {
	private Rect oldIconBoundary = new Rect();
	private Activity parentActivity;
	private ArrayList<AppDescriptor> apps;
	private static final int DEFAULT_ICON_SIZE = 42;

	public ApplicationArrayAdapter(Activity context,
			ArrayList<AppDescriptor> apps) {
		super(context, 0, apps);
		this.apps = apps;
		parentActivity = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final AppDescriptor app = apps.get(position);

		if (convertView == null) {
			final LayoutInflater inflater = parentActivity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.applicationitem, parent,
					false);
		}

		Drawable icon = app.getIcon();

		if (!app.isScaled()) {
			// if the app isn't scaled yet, resize the drawable icon
			int height = DEFAULT_ICON_SIZE;
			int width = DEFAULT_ICON_SIZE;

			int iconHeight = icon.getIntrinsicHeight();
			int iconWidth = icon.getIntrinsicWidth();

			if (icon instanceof PaintDrawable) {
				PaintDrawable drawable = (PaintDrawable) icon;
				drawable.setIntrinsicHeight(height);
				drawable.setIntrinsicWidth(width);
			}

			if (width > 0 && height > 0
					&& (width < iconWidth || height < iconHeight)) {
				double aspectRatio = (double) iconWidth / (double) iconHeight;

				if (iconWidth > iconHeight) {
					height = (int) (width / aspectRatio);
				} else if (iconHeight > iconWidth) {
					width = (int) (height * aspectRatio);
				}

				Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
						: Bitmap.Config.RGB_565;
				Bitmap thumb = Bitmap.createBitmap(width, height, c);
				Canvas canvas = new Canvas(thumb);
				canvas.setDrawFilter(new PaintFlagsDrawFilter(
						Paint.DITHER_FLAG, 0));
				oldIconBoundary.set(icon.getBounds());
				icon.setBounds(0, 0, width, height);
				icon.draw(canvas);
				icon.setBounds(oldIconBoundary);
				app.setIcon(new BitmapDrawable(thumb));
				icon = app.getIcon();
				app.setIsScaled(true);
			}
		}

		TextView textView = (TextView) convertView.findViewById(R.id.applabel);
		textView
				.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
		textView.setText(app.getName());
		return convertView;
	}
}
