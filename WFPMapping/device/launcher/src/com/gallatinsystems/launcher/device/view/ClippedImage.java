package com.gallatinsystems.launcher.device.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

/**
 * clips an image so we don't incur a performance hit when rendering images that
 * are bigger than the screen (since the system will resize them dynamically).
 * 
 * @author Christopher Fagiani
 * 
 */
public class ClippedImage extends Drawable {
	private final Drawable image;

	public ClippedImage(Drawable image) {
		this.image = image;
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
		image.setBounds(left, top, left + image.getIntrinsicWidth(), top
				+ image.getIntrinsicHeight());
	}

	public int getOpacity() {
		return image.getOpacity();
	}

	public void draw(Canvas canvas) {
		image.draw(canvas);
	}

	public void setColorFilter(ColorFilter filter) {
		image.setColorFilter(filter);
	}

	public void setAlpha(int alpha) {
		image.setAlpha(alpha);
	}

}