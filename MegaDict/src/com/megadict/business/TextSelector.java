package com.megadict.business;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;

import com.megadict.R;
import com.megadict.utility.Utility;
import com.megadict.widget.ResultView;

public class TextSelector {
	public void selectText(final Context context, final ResultView resultView, final String tag) {
		try
		{
			final KeyEvent shiftPressEvent = new KeyEvent(0, 0, KeyEvent.ACTION_DOWN,
					KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
			shiftPressEvent.dispatch(resultView);
			Utility.messageBox(context, R.string.selectText);
		}
		catch (final Exception e) {
			Log.e(tag, context.getString(R.string.canNotSelectText), e);
		}
	}
}
