package com.megadict.activity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.megadict.R;
import com.megadict.activity.base.AbstractListActivity;
import com.megadict.adapter.ChosenDictionaryCheckBoxAdapter;
import com.megadict.application.MegaDictApp;
import com.megadict.bean.RescanComponent;
import com.megadict.business.WikiAdder;
import com.megadict.business.scanning.DictionaryScanner;
import com.megadict.model.ChosenModel;
import com.megadict.utility.DatabaseHelper;

public class ManageActivity extends AbstractListActivity {
	private DictionaryScanner scanner;
	private RescanComponent rescanComponent;
	private WikiAdder wikiAdder;
	private Cursor listViewCursor;

	public ManageActivity() {
		super(R.layout.manage);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get application-scoped variables.
		scanner = ((MegaDictApp) getApplication()).scanner;

		// Create or open database.
		final SQLiteDatabase database = DatabaseHelper.getDatabase(this);
		listViewCursor =
				ChosenModel.selectChosenDictionaryIDsNameAndEnabled(database);
		final ChosenDictionaryCheckBoxAdapter adapter =
				new ChosenDictionaryCheckBoxAdapter(this, listViewCursor);
		setListAdapter(adapter);

		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("Scanning storage... ");

		rescanComponent =
				new RescanComponent(this, progressDialog, listViewCursor);
		wikiAdder = new WikiAdder(this, rescanComponent, scanner);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		listViewCursor.close();
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if (item.getItemId() == R.id.rescanMenuItem) {
			doRescanning();
		} else if (item.getItemId() == R.id.wikiMenuItem) {
			wikiAdder.showDialog();
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
	private void doRescanning() {
		scanner.rescan(rescanComponent);
	}
}