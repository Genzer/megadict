package com.megadict.widget;

import android.content.Context;
import android.text.ClipboardManager;
import android.util.AttributeSet;
import android.webkit.WebView;


public class ResultView extends WebView {
	private final ClipboardManager manager;
	protected OnSelectTextListener selectTextListner;

	public ResultView(final Context context, final ClipboardManager manager) {
		this(context, null, manager);
	}

	public ResultView(final Context context, final AttributeSet attribs, final ClipboardManager manager) {
		this(context, attribs, 0, manager);
	}

	public ResultView(final Context context, final AttributeSet attrs, final int defStyle, final ClipboardManager manager) {
		super( context, attrs, defStyle );
		if(manager != null) {
			this.manager = manager;
		} else {
			this.manager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
			this.manager.setText(null);
		}
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
