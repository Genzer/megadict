package com.megadict.activity;

import android.app.Activity;
import android.os.Bundle;

import com.megadict.R;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
	}

	@Override
	protected void onStart() {
		super.onStart();
		setTitle(R.string.aboutDialogLabel);
	}
}
