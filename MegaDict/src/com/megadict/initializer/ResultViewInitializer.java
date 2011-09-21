package com.megadict.initializer;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.ClipboardManager;
import android.widget.EditText;

import com.megadict.R;
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.AbstractWorkerTask.OnPostExecuteListener;
import com.megadict.business.WordListTask;
import com.megadict.widget.ResultView;
import com.megadict.widget.ResultView.OnSelectTextListener;

public final class ResultViewInitializer extends AbstractInitializer {

	public ResultViewInitializer(final Context context, final BusinessComponent businessComponent, final DictionaryComponent dictionaryComponent) {
		super(context, businessComponent, dictionaryComponent);
	}

	@Override
	public void init() {
		// Prepare components.
		final ClipboardManager clipboardManager =
				(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		final ResultView resultView = dictionaryComponent.getResultView();
		resultView.setBackgroundColor(0x00000000);
		resultView.setOnSelectTextListener(new OnSelectTextListener() {
			@Override
			public void onSelectText() {
				final String text = clipboardManager.getText().toString();
				// Search if text is not multiple words.
				if(text.contains(" ")) {
					showQuestionDialog(text);
				} else {
					searchWithUI(text);
				}
			} // End onSelectText().
		});
	}

	private void showQuestionDialog(final String text) {
		new AlertDialog.Builder(context).
		setTitle(R.string.searchOrSplitDialogTitle).
		setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				searchWithUI(text);
			}
		}).
		setNeutralButton(R.string.split, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				//Utility.messageBox(context, "Something");
				final WordListTask task = new WordListTask();
				task.setOnPostExecuteListener(new OnPostExecuteListener<List<String>>() {
					@Override
					public void onPostExecute(final List<String> words) {
						showWordListDialog(words);
					}
				});
				task.execute(text);
			}
		}).
		setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		}).show();
	}

	private void showWordListDialog(final List<String> words) {
		new AlertDialog.Builder(context).
		setTitle(R.string.wordListDialogTitle).
		setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		}).
		setItems(words.toArray(new CharSequence[words.size()]), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				searchWithUI(words.get(which));
			}
		}).show();
	}


	/**
	 * This function does three jobs: Insert word to search bar, prevent recommending and search.
	 * @param word
	 */
	private void searchWithUI(final String word) {
		final EditText searchBar = dictionaryComponent.getSearchBar();
		searchBar.setText(word);
		preventRecommending();
		doSearching(searchBar.getText().toString());
	}
}
