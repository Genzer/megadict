//package com.megadict.singletask;
//
//import java.util.List;
//
//import com.megadict.format.dict.DICTDictionary;
//import com.megadict.format.dict.index.IndexFile;
//import com.megadict.format.dict.reader.DictionaryFile;
//import com.megadict.model.Dictionary;
//import com.megadict.model.DictionaryInformation;
//import com.megadict.singletask.base.BaseScanTask;
//
//public class ScanStorageTask extends BaseScanTask {
//
//	public ScanStorageTask(final DictionaryInformation info, final List<Dictionary> dictionaryModels) {
//		super(info, dictionaryModels);
//	}
//
//	@Override
//	protected Void doInBackground(final Void... params) {
//		// Create necessary files.
//		final IndexFile indexFile = IndexFile.makeFile(info.getIndexFile());
//		final DictionaryFile dictFile = DictionaryFile.makeRandomAccessFile( info.getDataFile());
//		// Create model and add it to list.
//		final Dictionary model = new DICTDictionary(indexFile, dictFile);
//		dictionaryModels.add(model);
//		return null;
//	}
//}
