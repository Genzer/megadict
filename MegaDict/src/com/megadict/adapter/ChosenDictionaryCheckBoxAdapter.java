package com.megadict.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.megadict.R;
import com.megadict.model.ChosenModel;

public class ChosenDictionaryCheckBoxAdapter extends ResourceCursorAdapter {
	private final LayoutInflater inflater;
	private final SQLiteDatabase database;

	public ChosenDictionaryCheckBoxAdapter(final Context context, final Cursor cursor, final SQLiteDatabase database) {
		super(context, R.layout.textview_checkbox_row, cursor, true);
		this.database = database;
		this.inflater = LayoutInflater.from(context);
	}

	private static class ViewHolder {
		private TextView dictName;
		private CheckBox dictEnabled;
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		final View view =
			inflater.inflate(R.layout.textview_checkbox_row, parent, false);

		// Create view holder and store it.
		final ViewHolder viewHolder = new ViewHolder();
		viewHolder.dictName = (TextView) view.findViewById(R.id.dictionaryName);
		viewHolder.dictEnabled =
			(CheckBox) view.findViewById(R.id.dictionaryEnabled);
		view.setTag(viewHolder);

		viewHolder.dictEnabled.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				final int dictID = (Integer) buttonView.getTag();

				// Update database.
				final ContentValues value = new ContentValues();
				value.put(ChosenModel.ENABLED_COLUMN, isChecked ? 1 : 0);
				final String where = ChosenModel.ID_COLUMN + " = " + dictID;
				database.update(ChosenModel.TABLE_NAME, value, where, null);

				// NEED REVIEWING THIS USE // THIS IS NOT GOOD //
				cursor.requery();
			}
		});
		return view;
	}

	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		final ViewHolder viewHolder = (ViewHolder) view.getTag();

		// Populate the elements of list item.
		viewHolder.dictEnabled.setTag(cursor.getInt(cursor.getColumnIndex(ChosenModel.ID_COLUMN)));
		viewHolder.dictName.setText(cursor.getString(cursor.getColumnIndex(ChosenModel.DICTIONARY_NAME_COLUMN)));
		viewHolder.dictEnabled.setChecked(cursor.getInt(cursor.getColumnIndex(ChosenModel.ENABLED_COLUMN)) == 0
				? false : true);
	}

}
