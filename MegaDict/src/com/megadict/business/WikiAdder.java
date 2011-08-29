package com.megadict.business;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.megadict.R;
import com.megadict.bean.RescanComponent;
import com.megadict.business.scanning.DictionaryScanner;
import com.megadict.wiki.Wiki;

public class WikiAdder {
	private final Context context;
	private final RescanComponent rescanComponent;
	private final DictionaryScanner scanner;
	private final List<String> languageNames = Wiki.getLanguageNames();
	private final List<String> countryCodes = Wiki.getCountryCodes();

	public WikiAdder(final Context context, final RescanComponent rescanComponent, final DictionaryScanner scanner) {
		this.context = context;
		this.rescanComponent = rescanComponent;
		this.scanner = scanner;
	}

	public void showDialog() {
		final List<String> addedCodes = new ArrayList<String>();

		new AlertDialog.Builder(context).setIcon(R.drawable.crystal_add).setTitle(R.string.wikiDialogTitle).setMultiChoiceItems(languageNames.toArray(new String[languageNames.size()]), null, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
				addOrRemoveCountryCode(addedCodes, isChecked, countryCodes.get(which));
			}
		}).setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				scanner.addWikiDictionaries(addedCodes, rescanComponent);
			}
		}).setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		}).create().show();
	}

	private void addOrRemoveCountryCode(final List<String> addedCodes, final boolean isChecked, final String code) {
		if (isChecked) {
			addedCodes.add(code);
		} else {
			if (addedCodes.contains(code)) {
				addedCodes.remove(code);
			}
		}
	}
}
