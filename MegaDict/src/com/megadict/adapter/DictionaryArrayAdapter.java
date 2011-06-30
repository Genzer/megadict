package com.megadict.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.megadict.R;

@Deprecated
public class DictionaryArrayAdapter extends ArrayAdapter<DictionaryListItem> {

	private final List<DictionaryListItem> list;
	private final Activity context;

	public DictionaryArrayAdapter(final Activity context, final List<DictionaryListItem> list) {
		super(context, R.layout.textview_checkbox_row, list);
		this.context = context;
		this.list = list;
	}

	static class ViewHolder {
		protected TextView text;
		protected CheckBox checkbox;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view;
		if (convertView == null) {
			final LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.textview_checkbox_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) view.findViewById(R.id.dictionaryName);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.dictionaryEnabled);

			viewHolder.checkbox
			.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(final CompoundButton buttonView,
						final boolean isChecked) {
					final DictionaryListItem element = (DictionaryListItem) viewHolder.checkbox
					.getTag();
					element.setDictionaryEnabled(buttonView.isChecked());
				}

			});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		final ViewHolder holder = (ViewHolder) view.getTag();
		holder.text.setText(list.get(position).getDictionaryBean().getName());
		holder.checkbox.setChecked(list.get(position).isDictionaryEnabled());
		return view;
	}
}