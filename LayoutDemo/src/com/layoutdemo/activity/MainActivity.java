package com.layoutdemo.activity;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.layoutdemo.R;

public class MainActivity extends ActivityGroup {
	private LinearLayout searchBar;

	private void initLayout()
	{
		searchBar = (LinearLayout)findViewById(R.id.searchBar);

		// Get search view, then add it to search bar
		final LocalActivityManager manager = getLocalActivityManager();
		final Intent i = new Intent(MainActivity.this, SearchActivity.class);
		final Window w = manager.startActivity("SearchActivity", i);
		final View v = w != null ? w.getDecorView() : null;

		searchBar.addView(v);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initLayout();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
}