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

package com.gallatinsystems.survey.device.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Simple transparent panel adapted from code found here:
 * <code> http://blog.pocketjourney.com/2008/03/15/tutorial-1-transparent-panel-linear-layout-on-mapview-google-map/</code>
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class TransparentPanel extends LinearLayout {

	private static final int LINE_WIDTH = 2;
	private Paint innerColor;
	private Paint borderColor;

	public TransparentPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupColors();
	}

	public TransparentPanel(Context context) {
		super(context);
		setupColors();
	}

	private void setupColors() {
		innerColor = new Paint();
		innerColor.setARGB(225, 75, 75, 75);
		innerColor.setAntiAlias(true);

		borderColor = new Paint();
		borderColor.setARGB(255, 255, 255, 255);
		borderColor.setAntiAlias(true);
		borderColor.setStyle(Style.STROKE);
		borderColor.setStrokeWidth(LINE_WIDTH);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		RectF drawRect = new RectF();
		drawRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
		// draw the panel
		canvas.drawRoundRect(drawRect, 5, 5, innerColor);
		canvas.drawRoundRect(drawRect, 5, 5, borderColor);
		super.dispatchDraw(canvas);
	}
}
