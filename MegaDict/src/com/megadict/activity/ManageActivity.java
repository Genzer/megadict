package com.megadict.activity;



import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.megadict.R;
import com.megadict.adapter.ChosenDictionaryCheckBoxAdapter;
import com.megadict.application.MegaDictApp;
import com.megadict.business.DictionaryClient;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.model.ChosenModel;
import com.megadict.utility.DatabaseHelper;

public class ManageActivity extends BaseListActivity {
	private final String TAG = "ManageActivity";
	private Cursor listViewCursor;
	private SQLiteDatabase database;
	private DictionaryClient dictionaryClient;

	public ManageActivity() {
		super(R.layout.manage);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get application-scoped variables.
		dictionaryClient = ((MegaDictApp) getApplication()).dictionaryClient;

		// Create or open database.
		final DatabaseHelper databaseHelper = new DatabaseHelper(this);
		database = databaseHelper.getWritableDatabase();

		listViewCursor =
			database.query(ChosenModel.TABLE_NAME, null, null, null, null, null, null);
		startManagingCursor(listViewCursor);
		final ChosenDictionaryCheckBoxAdapter adapter =
			new ChosenDictionaryCheckBoxAdapter(this, listViewCursor, database);
		setListAdapter(adapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}

	@Override
	protected void onPause() {
		super.onPause();
		doScanningDatabase(this, database);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if (item.getItemId() == R.id.rescanMenuItem) {
			doScanningStorage(database);
			// Refresh list view.
			listViewCursor.requery();
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.manage_menu, menu);
		return true;
	}

	// ======================= Private functions =================== //
	private void doScanningStorage(final SQLiteDatabase database) {
		try {
			dictionaryClient.scanStorage(database);
		} catch (final IndexFileNotFoundException e) {
			Log.d(TAG, e.getMessage());
		} catch (final DataFileNotFoundException e) {
			Log.d(TAG, e.getMessage());
		}
	}

	private void doScanningDatabase(final Activity activity, final SQLiteDatabase database) {
		try {
			dictionaryClient.scanDatabase(activity, database);
		} catch (final IndexFileNotFoundException e) {
			Log.d(TAG, e.getMessage());
		} catch (final DataFileNotFoundException e) {
			Log.d(TAG, e.getMessage());
		}
	}
}