package com.megadict.activity;



import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.megadict.R;
import com.megadict.activity.base.BaseListActivity;
import com.megadict.adapter.ChosenDictionaryCheckBoxAdapter;
import com.megadict.application.MegaDictApp;
import com.megadict.bean.RescanComponent;
import com.megadict.business.DictionaryClient;
import com.megadict.model.ChosenModel;
import com.megadict.utility.DatabaseHelper;
import com.megadict.utility.Utility;

public class ManageActivity extends BaseListActivity {
	private final String TAG = "ManageActivity";
	private Cursor listViewCursor;
	private SQLiteDatabase database;
	private DictionaryClient dictionaryClient;
	private ProgressDialog progressDialog;
	private RescanComponent rescanComponent;

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

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Scanning storage... ");

		rescanComponent = new RescanComponent(progressDialog, database, listViewCursor);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if (item.getItemId() == R.id.rescanMenuItem) {
			doRescanning(database, listViewCursor);
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
	private void doRescanning(final SQLiteDatabase database, final Cursor cursor) {
		if(!dictionaryClient.rescan(rescanComponent)) {
			Utility.messageBox(this, getString(R.string.scanning));
		}
	}
}