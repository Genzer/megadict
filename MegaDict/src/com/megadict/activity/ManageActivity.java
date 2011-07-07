package com.megadict.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.megadict.R;
import com.megadict.adapter.ChosenDictionaryCheckBoxAdapter;
import com.megadict.application.MegaDictApp;
import com.megadict.bean.DictionaryBean;
import com.megadict.business.DictionaryClient;
import com.megadict.business.ExternalReader;
import com.megadict.business.ExternalStorage;
import com.megadict.model.ChosenModel;
import com.megadict.model.DictionaryInformation;
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
		//setContentView(R.layout.manage);

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
	protected void onStop() {
		super.onStop();
		rescanStorage();
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if (item.getItemId() == R.id.rescanMenuItem) {
			rescanDatabase();
			rescanStorage();
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.manage_menu, menu);
		return true;
	}

	// //========================= Private functions ======================= ////
	public void rescanStorage() {
		final Cursor cursor = ChosenModel.selectChosenDictionaries(database);
		startManagingCursor(cursor);

		final List<Pair<String, String>> list =
			new ArrayList<Pair<String, String>>();

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			final Pair<String, String> pair =
				new Pair<String, String>(cursor.getString(cursor.getColumnIndex(ChosenModel.INDEX_PATH_COLUMN)), cursor.getString(cursor.getColumnIndex(ChosenModel.DICT_PATH_COLUMN)));
			list.add(pair);
		}

		dictionaryClient.scanChosenDictionaries(list);
	}

	private void rescanDatabase() {
		// Remove old chosen dictionaries.
		database.delete(ChosenModel.TABLE_NAME, null, null);

		// Storage information of available dictionaries.
		final List<DictionaryInformation> infos = getItems();
		final ContentValues value = new ContentValues();
		for (final DictionaryInformation info : infos) {
			final DictionaryBean bean = info.getBean();
			value.put(ChosenModel.ID_COLUMN, bean.getId());
			value.put(ChosenModel.DICTIONARY_NAME_COLUMN, bean.getName());
			value.put(ChosenModel.INDEX_PATH_COLUMN, info.getIndexFile().getAbsolutePath());
			value.put(ChosenModel.DICT_PATH_COLUMN, info.getDataFile().getAbsolutePath());
			value.put(ChosenModel.ENABLED_COLUMN, 0);
			database.insert(ChosenModel.TABLE_NAME, null, value);
		}

		// This will refresh list view.
		listViewCursor.requery();
	}

	private List<DictionaryInformation> getItems() {
		final ExternalReader reader =
			new ExternalReader(ExternalStorage.getExternalDirectory());

		// Print info after reading.
		final List<String> logger = reader.getLogger();
		for(final String message : logger) {
			Log.e(TAG, message);
		}

		return reader.getInfos();
	}
}