package com.megadict.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.TextView;

public class ResultTextView extends TextView {

	public ResultTextView(final Context context) {
		super(context);
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		final Rect rect = new Rect();
		final Paint borderPaint = new Paint();
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(1);
		borderPaint.setAntiAlias(true);

		// Draw border.
		getLocalVisibleRect(rect);
		final RectF rectF = new RectF(rect);
		rectF.right = rectF.right - 5;
		canvas.drawRoundRect(rectF, 20, 20, borderPaint);

		super.onDraw(canvas);
	}
}
