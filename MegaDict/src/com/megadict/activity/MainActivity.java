package com.megadict.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.megadict.R;
import com.megadict.utility.Utility;

@Deprecated
public class MainActivity extends ActivityGroup implements OnClickListener {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setOnClickListenerForAllButtons(getButtonList());
	}

	private List<Button> getButtonList() {
		final List<Button> buttons = new ArrayList<Button>();
		buttons.add((Button) findViewById(R.id.dictionaryButton));
		buttons.add((Button) findViewById(R.id.manageButton));
		buttons.add((Button) findViewById(R.id.soundManageButton));
		buttons.add((Button) findViewById(R.id.settingButton));
		buttons.add((Button) findViewById(R.id.helpButton));
		buttons.add((Button) findViewById(R.id.aboutButton));
		return buttons;
	}

	private void setOnClickListenerForAllButtons(final List<Button> buttons) {
		for (final Button button : buttons) {
			button.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(final View v) {
		final String packageName = getCommonPackageName();
		switch (v.getId()) {
		case R.id.dictionaryButton:
			runActivity(packageName + ".DictionaryActivity");
			break;
		case R.id.aboutButton:
			runActivity(packageName + ".AboutActivity");
			break;
		case R.id.settingButton:
			runActivity(packageName + ".SettingActivity");
			break;
		case R.id.manageButton:
			runActivity(packageName + ".ManageActivity");
			break;
		default:
			break;
		}
	}

	private void runActivity(final String activityClassName) {
		try {

			final Class<?> activityClass = Class.forName(activityClassName);
			final Intent intent = new Intent(MainActivity.this, activityClass);
			startActivity(intent);
		} catch (final ClassNotFoundException e) {
			Utility.messageBox(this, "Lỗi hệ thống.");
		}
	}

	private String getCommonPackageName() {
		final Package pack = MainActivity.this.getClass().getPackage();
		return pack.getName();
	}
}