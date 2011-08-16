package com.megadict.business;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;

import com.megadict.R;
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;
import com.megadict.initializer.AbstractInitializer;

public class HistoryDisplayer extends AbstractInitializer {
	// Aggregation variables.

	// Composition variables.
	public HistoryDisplayer(final Context context, final BusinessComponent businessComponent, final DictionaryComponent dictionaryComponent) {
		super(context, businessComponent, dictionaryComponent);
	}

	@Override
	protected void init() {
	}

	public void showHistoryDialog(final List<String> list) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.historyDialogTitle);
		builder.setIcon(R.drawable.crystal_history);


		builder.setPositiveButton(R.string.deleteEllipsis, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				showDeleteDialog(list);
			}
		});
		builder.setNeutralButton(R.string.clear, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				businessComponent.getSearcher().clearHistory();
			}
		});
		builder.setNegativeButton(R.string.close, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		});

		builder.setItems(list.toArray(new String[list.size()]), new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				final String chosenWord = list.get(which);
				dictionaryComponent.getSearchBar().setText(chosenWord);
				doSearching(chosenWord);
				preventRecommending();
			}
		});

		final Dialog dialog = builder.create();
		dialog.show();
	}

	private void showDeleteDialog(final List<String> list) {
		final List<String> removedItems = new ArrayList<String>();
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.historyDialogTitle);
		builder.setIcon(R.drawable.crystal_history);

		builder.setPositiveButton(R.string.ok, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				businessComponent.getSearcher().removeWordFromHistory(removedItems);
			}
		});
		builder.setNegativeButton(R.string.cancel, new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		});
		builder.setMultiChoiceItems(list.toArray(new String[list.size()]), null, new OnMultiChoiceClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which, final boolean isChecked) {
				if(isChecked) {
					removedItems.add(list.get(which));
				} else {
					final String item = list.get(which);
					if(removedItems.contains(item)) {
						removedItems.remove(item);
					}
				}
			}
		});

		final Dialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void doNothing() {
	}
}
