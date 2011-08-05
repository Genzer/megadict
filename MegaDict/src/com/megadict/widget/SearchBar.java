package com.megadict.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.megadict.activity.DictionaryActivity;
import com.megadict.utility.Utility;

public class SearchBar extends AutoCompleteTextView {
	private final Context context;
	public SearchBar(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public SearchBar(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SearchBar(final Context context) {
		this(context, null);
	}

	private void init() {
		Utility.disableSoftKeyboard(context, this);
		setThreshold(1);



		setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					((DictionaryActivity)context).doSearching(getText().toString());
				}
				return true;
			}
		});

		addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

			}

			@Override
			public void afterTextChanged(final Editable s) {
			}
		});
	}

}
