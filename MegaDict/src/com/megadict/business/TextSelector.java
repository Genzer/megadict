package com.megadict.business;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.megadict.R;
import com.megadict.utility.Utility;

public class TextSelector {
	private final String TAG = "TextSelector";

	public void selectText(final Context context, final WebView resultView) {
		try {
			final KeyEvent shiftPressEvent =
					new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
			shiftPressEvent.dispatch(resultView);
			Utility.messageBox(context, R.string.selectText);
		} catch (final Exception e) {
			Log.e(TAG, context.getString(R.string.canNotSelectText), e);
		}
	}
}
