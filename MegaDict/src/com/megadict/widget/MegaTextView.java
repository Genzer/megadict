package com.megadict.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

public class MegaTextView extends TextView {
	public MegaTextView(final Context context) {
		super(context);
	}

	public MegaTextView(final Context context, final AttributeSet set) {
		super(context, set);
	}


	@Override
	protected void onFocusChanged(final boolean focused, final int direction, final Rect previouslyFocusedRect) {
		if(focused)
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

	@Override
	public void onWindowFocusChanged(final boolean hasWindowFocus) {
		if(hasWindowFocus)
			super.onWindowFocusChanged(hasWindowFocus);
	}


	@Override
	public boolean isFocused() {
		return true;
	}

}
