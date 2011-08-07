//package com.megadict.singletask;
//
//import android.app.ProgressDialog;
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.AsyncTask;
//
//import com.megadict.business.DictionaryScanner;
//import com.megadict.format.dict.DICTDictionary;
//import com.megadict.format.dict.index.IndexFile;
//import com.megadict.format.dict.reader.DictionaryFile;
//import com.megadict.model.ChosenModel;
//import com.megadict.model.Dictionary;
//import com.megadict.model.DictionaryInformation;
//
//public class ScanTask extends AsyncTask<Void, Void, Void> {
//	private final DictionaryInformation info;
//	private final ProgressDialog progressDialog;
//	private final SQLiteDatabase database;
//	private final Cursor cursor;
//	private boolean scanning;
//
//	public ScanTask(final DictionaryInformation info, final ProgressDialog progressDialog, final SQLiteDatabase database, final Cursor cursor) {
//		this.info = info;
//		this.progressDialog = progressDialog;
//		this.database = database;
//		this.cursor = cursor;
//	}
//
//	public ScanTask(final DictionaryInformation info) {
//		this(info, null, null, null);
//	}
//
//	@Override
//	protected void onPreExecute() {
//		scanning = true;
//		if(progressDialog != null)
//			progressDialog.show();
//	}
//
//	@Override
//	protected Void doInBackground(final Void... params) {
//		// Create necessary files.
//		final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
//		final DictionaryFile dictFile = DictionaryFile.makeRandomAccessFile( info.getDataFile());
//		// Create model and add it to list.
//		final Dictionary model = new DICTDictionary(indexFile, dictFile);
//		DictionaryScanner.addModel(model);
//
//		// Why must using synchronized block here? I don't know, it just works. @_@
//		final String name;
//		synchronized(this) {
//			name = model.getName();
//		}
//
//		if(database != null) {
//			// Insert dictionary infos to database.
//			final ContentValues value = new ContentValues();
//			value.put(ChosenModel.DICTIONARY_NAME_COLUMN, name);
//			value.put(ChosenModel.DICTIONARY_PATH_COLUMN, info.getParentFile().getAbsolutePath());
//			value.put(ChosenModel.ENABLED_COLUMN, 0);
//			database.insert(ChosenModel.TABLE_NAME, null, value);
//		}
//		return null;
//	}
//
//	@Override
//	protected void onPostExecute(final Void result) {
//		scanning = false;
//
//		// Hide progress bar if all tasks finished.
//		if(DictionaryScanner.didAllTasksFinish()) {
//			// Requery the cursor to update list view.
//			if(database != null) {
//				cursor.requery();
//			}
//			// Close progress dialog
//			if(progressDialog != null)
//				progressDialog.dismiss();
//		}
//	}
//
//	public boolean isScanning() {
//		return scanning;
//	}
//}
