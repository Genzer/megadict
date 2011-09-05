package com.megadict.activity.base;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Window;

import com.megadict.R;

public abstract class AbstractListActivity extends ListActivity {
	private final int layoutID;

	public AbstractListActivity(final int layoutID) {
		super();
		this.layoutID = layoutID;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(layoutID);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
	}
}
