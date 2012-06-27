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