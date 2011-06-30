package com.megadict.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.megadict.R;
import com.megadict.bean.DictionaryBean;

public class ChosenDictionaryRadioButtonAdapter extends ArrayAdapter<DictionaryBean> {

	private final List<DictionaryBean> list;
	private int selectedIndex;
	private final LayoutInflater inflater;

	public ChosenDictionaryRadioButtonAdapter(final Context context, final List<DictionaryBean> list, final int selectedIndex) {
		super(context, R.layout.textview_radiobutton_row, list);
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.selectedIndex = selectedIndex;
	}

	static class ViewHolder {
		protected TextView text;
		protected RadioButton radio;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view;
		if (convertView == null) {
			view = inflater.inflate(R.layout.textview_radiobutton_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) view.findViewById(R.id.dictionaryName);
			viewHolder.radio =
				(RadioButton) view.findViewById(R.id.chosenDictionary);


			viewHolder.radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
					selectedIndex = position;
				}
			});
			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		// Populate the list item.
		final ViewHolder holder = (ViewHolder) view.getTag();
		holder.text.setText(list.get(position).getName());
		// Check the chosen dictionary.
		if (selectedIndex == position) {
			holder.radio.setChecked(true);
		}
		return view;
	}
}