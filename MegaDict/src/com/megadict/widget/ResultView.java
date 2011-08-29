package com.megadict.widget;

import android.content.Context;
import android.text.ClipboardManager;
import android.webkit.WebView;

public class ResultView extends WebView {
	private final ClipboardManager manager;
	protected OnSelectTextListener selectTextListner;

	public ResultView(final Context context) {
		super(context);
		manager =
				(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
	}

	@Override
	public void invalidate() {
		super.invalidate();

		if (manager != null && manager.hasText() && selectTextListner != null) {
			selectTextListner.onSelectText();
			manager.setText(null);
		}
	}

	public void setOnSelectTextListener(final OnSelectTextListener listner) {
		this.selectTextListner = listner;
	}

	public interface OnSelectTextListener {
		void onSelectText();
	}
}
