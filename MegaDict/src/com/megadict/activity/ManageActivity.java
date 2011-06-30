package com.megadict.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.megadict.R;
import com.megadict.adapter.ChosenDictionaryCheckBoxAdapter;
import com.megadict.bean.DictionaryBean;
import com.megadict.business.ExternalReader;
import com.megadict.business.ExternalStorage;
import com.megadict.exception.ConfigurationFileNotFoundException;
import com.megadict.exception.CouldNotCreateExternalDirectoryException;
import com.megadict.exception.DictionaryNotFoundException;
import com.megadict.exception.InvalidConfigurationFileException;
import com.megadict.model.ChosenModel;
import com.megadict.utility.DatabaseHelper;

public class ManageActivity extends ListActivity {
	private final String TAG = "ManageActivity";
	private Cursor cursor;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final DatabaseHelper databaseHelper = new DatabaseHelper(this);
		final SQLiteDatabase database = databaseHelper.getWritableDatabase();
		cursor = database.query(ChosenModel.TABLE_NAME, null, null, null, null, null, null);
		final ChosenDictionaryCheckBoxAdapter adapter = new ChosenDictionaryCheckBoxAdapter(this, cursor, database);

		setContentView(R.layout.manage);
		setListAdapter(adapter);
	}

	private List<DictionaryBean> getItems() {
		List<DictionaryBean> beans = null;
		try {
			beans = ExternalReader.getDictionaryBeanList(ExternalStorage.getExternalDirectory());
		} catch (final SecurityException e) {
			Log.e(TAG, e.getMessage());
		} catch (final InvalidConfigurationFileException e) {
			Log.e(TAG, e.getMessage());
		} catch (final DictionaryNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (final ConfigurationFileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		} catch (final CouldNotCreateExternalDirectoryException e) {
			Log.e(TAG, e.getMessage());
		}
		return beans == null ? new ArrayList<DictionaryBean>() : beans;
	}

	private void rescanStorage()
	{
		final DatabaseHelper databaseHelper = new DatabaseHelper(this);
		final SQLiteDatabase database = databaseHelper.getWritableDatabase();
		database.delete(ChosenModel.TABLE_NAME, null, null);
		final List<DictionaryBean> beans = getItems();
		final ContentValues value = new ContentValues();
		for(final DictionaryBean bean : beans) {
			value.put(ChosenModel.ID_COLUMN, bean.getId());
			value.put(ChosenModel.DICTIONARY_NAME_COLUMN, bean.getName());
			value.put(ChosenModel.ENABLED_COLUMN, 0);
			database.insert(ChosenModel.TABLE_NAME, null, value);
		}
		cursor.requery();
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if(item.getItemId() == R.id.rescan) {
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
}