package com.megadict.business;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;

import com.megadict.R;
import com.megadict.utility.Utility;

public class HistoryDisplayer {
	private final DictionaryClient dictionaryClient;

	// Composition variables.
	public HistoryDisplayer(final DictionaryClient dictionaryClient) {
		this.dictionaryClient = dictionaryClient;
	}

	public void showHistoryDialog(final List<String> list) {
		if(list.isEmpty()) {
			Utility.messageBox(dictionaryClient.getContext(), R.string.emptyHistory);
			return;
		}
		new AlertDialog.Builder(dictionaryClient.getContext()).
		setTitle(R.string.historyDialogTitle).setIcon(R.drawable.crystal_history).
		setPositiveButton(R.string.deleteEllipsis, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				showDeleteDialog(list);
			}
		}).setNeutralButton(R.string.clear, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dictionaryClient.clearHistory();
			}
		}).setNegativeButton(R.string.close, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		}).setItems(list.toArray(new String[list.size()]), new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dictionaryClient.searchWithUI(list.get(which));
			}
		}).create().show();
	}

	private void showDeleteDialog(final List<String> list) {
		final List<String> removedItems = new ArrayList<String>();

		new AlertDialog.Builder(dictionaryClient.getContext()).setTitle(R.string.historyDialogTitle).setIcon(R.drawable.crystal_history).setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dictionaryClient.removeWordFromHistory(removedItems);
			}
		}).setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		}).setMultiChoiceItems(list.toArray(new String[list.size()]), null, new OnMultiChoiceClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
				if (isChecked) {
					removedItems.add(list.get(which));
				} else {
					final String item = list.get(which);
					if (removedItems.contains(item)) {
						removedItems.remove(item);
					}
				}
			}
		}).create().show();
	}
}
