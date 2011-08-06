package com.megadict.task;

import java.util.List;

import com.megadict.R;
import com.megadict.bean.ScanStorageComponent;
import com.megadict.business.DictionaryScanner;
import com.megadict.business.ResultTextMaker;
import com.megadict.format.dict.DICTDictionary;
import com.megadict.format.dict.index.IndexFile;
import com.megadict.format.dict.reader.DictionaryFile;
import com.megadict.model.Dictionary;
import com.megadict.model.DictionaryInformation;
import com.megadict.task.base.BaseScanAllTask;

public class ScanStorageAllTask extends BaseScanAllTask {
	private final ScanStorageComponent scanStorageComponent;

	public ScanStorageAllTask(final List<DictionaryInformation> infos, final ScanStorageComponent scanStorageComponent) {
		super(infos);
		this.scanStorageComponent = scanStorageComponent;
	}

	@Override
	protected Void doInBackground(final Void... params) {
		for(final DictionaryInformation info : infos) {
			// Create necessary files.
			final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
			final DictionaryFile dictFile = DictionaryFile.makeRandomAccessFile( info.getDataFile());
			// Create model and add it to list.
			final Dictionary model = new DICTDictionary(indexFile, dictFile);
			DictionaryScanner.addModel(model);
		}
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);

		// Set start page.
		setStartPage();
	}

	private void setStartPage() {
		final int dictCount = DictionaryScanner.getDictionaryCount();
		try {
			final String welcomeStr = (dictCount > 1 ? scanStorageComponent.context.getString(R.string.usingDictionaryPlural, dictCount) : scanStorageComponent.context.getString(R.string.usingDictionary, dictCount));
			final String welcomeHTML = scanStorageComponent.resultTextMaker.getWelcomeHTML(welcomeStr);
			scanStorageComponent.resultView.loadDataWithBaseURL(ResultTextMaker.ASSET_URL, welcomeHTML, "text/html", "utf-8", null);
		} catch (final Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
