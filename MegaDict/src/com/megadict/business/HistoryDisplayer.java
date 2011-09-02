package com.megadict.business;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;

import com.megadict.R;
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;
import com.megadict.initializer.AbstractInitializer;
import com.megadict.utility.Utility;

public class HistoryDisplayer extends AbstractInitializer {
	// Composition variables.
	public HistoryDisplayer(final BusinessComponent businessComponent, final DictionaryComponent dictionaryComponent) {
		super(businessComponent, dictionaryComponent);
	}

	public void showHistoryDialog(final List<String> list) {
		if(list.isEmpty()) {
			Utility.messageBox(dictionaryComponent.getContext(), R.string.emptyHistory);
			return;
		}
		new AlertDialog.Builder(dictionaryComponent.getContext()).
		setTitle(R.string.historyDialogTitle).setIcon(R.drawable.crystal_history).
		setPositiveButton(R.string.deleteEllipsis, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				showDeleteDialog(list);
			}
		}).setNeutralButton(R.string.clear, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				businessComponent.getSearcher().clearHistory();
			}
		}).setNegativeButton(R.string.close, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		}).setItems(list.toArray(new String[list.size()]), new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				final String chosenWord = list.get(which);
				dictionaryComponent.getSearchBar().setText(chosenWord);
				preventRecommending();
				doSearching(chosenWord);
			}
		}).create().show();
	}

	private void showDeleteDialog(final List<String> list) {
		final List<String> removedItems = new ArrayList<String>();

		new AlertDialog.Builder(dictionaryComponent.getContext()).setTitle(R.string.historyDialogTitle).setIcon(R.drawable.crystal_history).setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				businessComponent.getSearcher().removeWordFromHistory(removedItems);
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

	@Override
	public void init() { /* Empty for no reason, ok? */ }

	@Override
	public void doNothing() { /* Empty for no reason, ok? */
	}
}
