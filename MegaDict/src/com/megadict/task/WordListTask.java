package com.megadict.task;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.megadict.R;

public class WordListTask extends AsyncTask<Void, Void, String[]> {
	private final String text;
	protected OnClickWordListener clickWordListener;
	private String word;
	private AlertDialog.Builder builder;

	public WordListTask(final Context context, final String text) {
		super();
		this.text = text;
		builder = createBuilder(context);
	}

	private AlertDialog.Builder createBuilder(final Context context) {
		builder = new AlertDialog.Builder(context);
		builder.setTitle("Pick a word");
		builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		});
		return builder;
	}

	@Override
	protected String[] doInBackground(final Void... params) {
		return text.split("[^\\w]+");
	}

	@Override
	protected void onPostExecute(final String[] items) {
		final List<String> words = new ArrayList<String>();
		for(final String item : items) {
			if(!"".equals(item)) {
				words.add(item);
			}
		}

		builder.setItems(words.toArray(new CharSequence[words.size()]), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				if(clickWordListener != null) {
					word = words.get(which);
					clickWordListener.onClickWord();
				}
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	public void setOnClickWordListener(final OnClickWordListener listener) {
		this.clickWordListener = listener;
	}

	public interface OnClickWordListener {
		void onClickWord();
	}

	public String getWord() {
		return word;
	}
}
