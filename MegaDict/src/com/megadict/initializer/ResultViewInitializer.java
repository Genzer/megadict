package com.megadict.initializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.megadict.R;
import com.megadict.business.AbstractWorkerTask;
import com.megadict.business.AbstractWorkerTask.OnPostExecuteListener;
import com.megadict.business.DictionaryClient;
import com.megadict.business.ResultTextMaker;
import com.megadict.widget.ResultView;
import com.megadict.widget.ResultView.OnSelectTextListener;

public final class ResultViewInitializer implements Initializer {
	private final DictionaryClient dictionaryClient;

	public ResultViewInitializer(final DictionaryClient dictionaryClient) {
		this.dictionaryClient = dictionaryClient;
	}

	@Override
	public void init() {
		// Prepare components.
		final ClipboardManager clipboardManager = (ClipboardManager) dictionaryClient.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		final ResultView resultView = dictionaryClient.getResultView();

		// Clear clipboard at intial time.
		clipboardManager.setText(null);

		// Set transparent color.
		resultView.setBackgroundColor(0x00000000);

		// Set custom WebViewClient.
		resultView.setWebViewClient(new ResultViewClient());

		setOnSelectTextListener(clipboardManager, resultView);
	}

	private void setOnSelectTextListener(final ClipboardManager clipboardManager, final ResultView resultView) {
		resultView.setOnSelectTextListener(new OnSelectTextListener() {
			@Override
			public void onSelectText() {
				final String text = clipboardManager.getText().toString();
				// Show question dialog if it is multiple words.
				if(text.contains(" ")) {
					showQuestionDialog(text);
				} else {
					dictionaryClient.searchWithUI();
				}
			} // End onSelectText().
		});
	}

	public void loadWelcomeStr() {
		// If welcome string is showing, refresh it.
		final String welcomeStr = ResultTextMaker.getWelcomeHTML(dictionaryClient.getContext().getString(R.string.welcome));
		dictionaryClient.getResultView().loadDataWithBaseURL(ResultTextMaker.ASSET_URL, welcomeStr, "text/html", "utf-8", null);
	}

	private void showQuestionDialog(final String text) {
		new AlertDialog.Builder(dictionaryClient.getContext()).
		setTitle(R.string.searchOrSplitDialogTitle).
		setPositiveButton(R.string.search, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dictionaryClient.searchWithUI();
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
		new AlertDialog.Builder(dictionaryClient.getContext()).
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
				dictionaryClient.searchWithUI(words.get(which));
			}
		}).show();
	}

	private class ResultViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
			if(url.startsWith("http://")) {
				showLaunchURIDialog(view, url);
			} else {
				final int slashIndex = url.lastIndexOf("/");
				if(slashIndex != -1 && slashIndex + 1 < url.length()) {
					final String searchedWord = url.substring(slashIndex + 1);
					dictionaryClient.searchWithUI(searchedWord);
				}
			}
			return true;
		}

		private void showLaunchURIDialog(final WebView view, final String url) {
			new AlertDialog.Builder(dictionaryClient.getContext())
			.setMessage(R.string.launchURI)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.cancel();

					// Open the link by using other Activity.
					final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					dictionaryClient.getContext().startActivity(browserIntent);
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int id) {
					dialog.cancel();
				}
			}).create().show();
		}
	} // End ResultViewClient class.

	private static class WordListTask extends AbstractWorkerTask<String, Void, List<String>> {
		private final static String SPLIT_REGEX = "[^\\w]+";

		@Override
		protected List<String> doInBackground(final String... params) {
			final String []items = params[0].split(SPLIT_REGEX);
			final Set<String> words = new HashSet<String>(Arrays.asList(items));
			return new ArrayList<String>(words);
		}
	}
}
