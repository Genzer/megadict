package com.megadict.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.megadict.R;
import com.megadict.business.DictionaryCenter;
import com.megadict.business.ExternalDirectoryCreator;
import com.megadict.exception.ConfigurationFileNotFoundException;
import com.megadict.exception.CouldNotCreateExternalDirectoryException;
import com.megadict.exception.DataFileNotFoundException;
import com.megadict.exception.DictionaryNotFoundException;
import com.megadict.exception.IndexFileNotFoundException;
import com.megadict.exception.InvalidConfigurationFileException;

public class DictionaryActivity extends Activity implements OnClickListener {
	private static final String TAG = "DictionaryActivity";

	private DictionaryCenter dictionaryCenter;
	private File externalDirectory;
	private Button searchButton;
	private EditText searchEditText;
	private TextView resultTextView;
	private final int currentDictID;

	public DictionaryActivity() {
		currentDictID = 123;
	}

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		initLayout();
		initMegaDictDirectory();
		initDictionaryCenter();
	}

	/**
	 * Called when the menu button is clicked.
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	/**
	 * Called when the menu item is chosen.
	 */
	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		if (item.getItemId() == R.id.setting_menu_item) {
			startSettingActivity();
		}
		return true;
	}

	/**
	 *  Start setting activity.
	 */
	private void startSettingActivity() {
		Log.d(TAG, "startSettingActivity");
		final Intent intent = new Intent(DictionaryActivity.this, SettingActivity.class);
		startActivity(intent);
	}

	/**
	 * Called when the search button is clicked.
	 */
	public void onClick(final View v) {
		if (v.getId() == R.id.searchButton) {
			//Utility.messageBox(this, "Hi man");
			searchWord(searchEditText.getText().toString());
		}
	}

	private void searchWord(final String word)
	{
		try {
			dictionaryCenter.chooseDictionary(currentDictID);
			dictionaryCenter.lookup(word);
		} catch (final DictionaryNotFoundException e) {
			resultTextView.setText("Chưa có từ điển nào trong thư mục \"megadict\"");
		}
	}

	/**
	 * Init layout.
	 */
	private void initLayout() {
		searchButton = (Button) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(this);
		searchEditText = (EditText) findViewById(R.id.searchEditText);
		resultTextView = (TextView) findViewById(R.id.resultTextView);
	}

	/**
	 * Init external directory. The external directory is used for storing data of this app.
	 */
	private void initMegaDictDirectory() {
		try {
			externalDirectory = ExternalDirectoryCreator.createDirectory();
		} catch (final SecurityException e) {
			resultTextView.setTextColor(Color.RED);
			resultTextView.setText("Không tạo được thư mục. Thư mục không có quyền ghi.");
		} catch (final CouldNotCreateExternalDirectoryException e) {
			resultTextView.setTextColor(Color.RED);
			resultTextView.setText("Không tạo được thư mục. Lỗi nào đó trong phần mềm");
		}
	}

	/**
	 * Init DictCenter. DictCenter is the searching processor of SearchActivity.
	 */
	private void initDictionaryCenter() {
		try {
			dictionaryCenter = new DictionaryCenter();
			dictionaryCenter.scanDictionaries(externalDirectory);
		} catch (final InvalidConfigurationFileException e) {
			resultTextView.setText("Lỗi file cấu hình. Làm ơn kiểm tra lại định dạng file \"conf.xml\"");
		} catch (final DictionaryNotFoundException e) {
			resultTextView.setText("Chưa có từ điển nào trong thư mục \"megadict\"");
		} catch (final ConfigurationFileNotFoundException e) {
			resultTextView.setText("Lỗi file cấu hình. Làm ơn kiểm tra lại và chắc chắn là có file \"conf.xml\"");
		} catch (final IndexFileNotFoundException e) {
			resultTextView.setText("Lỗi file chỉ mục. Làm ơn kiểm tra lại và chắc chắn là có file index với định dạng <ngôn ngữ nguồn>_<ngôn ngữ đích>.index.");
		} catch (final DataFileNotFoundException e) {
			resultTextView.setText("Lỗi file dữ liệu. Làm ơn kiểm tra lại và chắc chắn là có file data với định dạng <ngôn ngữ nguồn>_<ngôn ngữ đích>.dict.");
		}

	}
}
