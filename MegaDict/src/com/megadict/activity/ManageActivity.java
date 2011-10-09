package com.megadict.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.megadict.R;
import com.megadict.activity.base.AbstractListActivity;
import com.megadict.adapter.DictionaryAdapter;
import com.megadict.application.MegaDictApp;
import com.megadict.business.WikiAdder;
import com.megadict.business.scanning.DictionaryScanner;
import com.megadict.business.scanning.DictionaryScanner.OnCompleteRescanListener;
import com.megadict.model.ChosenModel;
import com.megadict.utility.DatabaseHelper;
import com.megadict.utility.Utility;

public class ManageActivity extends AbstractListActivity {
	private DictionaryScanner scanner;
	private WikiAdder wikiAdder;
	private Cursor listViewCursor;
	private ProgressDialog progressDialog;

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
		final DictionaryAdapter adapter =
				new DictionaryAdapter(this, listViewCursor);
		setListAdapter(adapter);

		// Set onCompleteRescanListener for scanner.
		scanner.setOnCompleteRescanListener(new OnCompleteRescanListener() {
			@Override
			public void onCompleteRescan() {
				// Update ListView when complete rescanning.
				listViewCursor.requery();
			}
		});

		// Create progressDialog.
		progressDialog = new ProgressDialog(this);

		// Create wikiAdder to add Wiki dictionary.
		wikiAdder = new WikiAdder(this, progressDialog, scanner);

		// Ask for updating models no matter whether the models change.
		final Intent returnedIntent = new Intent();
		returnedIntent.putExtra(DictionaryScanner.MODEL_CHANGED, true);
		setResult(Activity.RESULT_OK, returnedIntent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
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
		if(scanner.didAllRescanTasksFinish()) {
			scanner.rescan(progressDialog);
		} else {
			Utility.messageBox(this, R.string.scanning);
		}
	}
}