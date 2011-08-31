package com.megadict.initializer;

import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.megadict.R;
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;
import com.megadict.utility.Utility;

public class BottomButtonsInitializer extends AbstractInitializer {

	public BottomButtonsInitializer(final BusinessComponent businessComponent, final DictionaryComponent dictionaryComponent) {
		super(businessComponent, dictionaryComponent);
	}

	@Override
	protected void init() {
		final List<Button> bottomButtons = dictionaryComponent.getBottomButtons();
		for(final Button button : bottomButtons) {
			switch (button.getId()) {
			case R.id.manageButton:
				initBottomButtons(button, "com.megadict.activity.ManageActivity");
				break;
			case R.id.historyButton:
				//initBottomButtons(button, "com.megadict.activity.ManageActivity");
				break;
			case R.id.settingButton:
				initBottomButtons(button, "com.megadict.activity.SettingActivity");
				break;
			case R.id.moreButton:
				break;
			default:
				break;
			}
		}
	}

	private void initBottomButtons(final Button button, final String activityClassName) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				Utility.startActivity(dictionaryComponent.getContext(), activityClassName);
			}
		});
	}

	@Override
	public void doNothing() { /* Do nothing for no reason. */}
}
