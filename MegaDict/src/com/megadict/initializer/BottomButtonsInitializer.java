package com.megadict.initializer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.megadict.R;
import com.megadict.business.DictionaryClient;
import com.megadict.business.HistoryDisplayer;
import com.megadict.utility.ActivityHelper;
import com.megadict.utility.Utility;

public class BottomButtonsInitializer implements Initializer {
	private final HistoryDisplayer historyDisplayer;
	private final List<ButtonExecutor> executors = new ArrayList<BottomButtonsInitializer.ButtonExecutor>();
	private final DictionaryClient dictionaryClient;

	public BottomButtonsInitializer(final HistoryDisplayer historyDisplayer, final DictionaryClient dictionaryClient) {
		this.dictionaryClient = dictionaryClient;
		this.historyDisplayer = historyDisplayer;
		// Init executors to execute items in AlertDialog.
		initExecutors();
	}

	private void initExecutors() {
		executors.add(new ButtonExecutor() {
			@Override
			public void execute() {
				Utility.startActivityForResult((Activity)dictionaryClient.getContext(), ActivityHelper.MANAGE_ACTIVITY, ActivityHelper.MANAGE_REQUEST);
			}
		});
		executors.add(new ButtonExecutor() {
			@Override
			public void execute() {
				historyDisplayer.showHistoryDialog(dictionaryClient.getHistoryList());
			}
		});
		executors.add(new ButtonExecutor() {
			@Override
			public void execute() {
				Utility.selectText(dictionaryClient.getContext(), dictionaryClient.getResultView());
			}
		});
		executors.add(new ButtonExecutor() {
			@Override
			public void execute() {
				// Downcasting Context to Activity because I ensure this context is an activity,
				/// but this seems to be a bad practice AFAIK.
				Utility.startActivityForResult((Activity)dictionaryClient.getContext(), ActivityHelper.SETTING_ACTIVITY, ActivityHelper.SETTING_REQUEST);
			}
		});
		executors.add(new ButtonExecutor() {
			@Override
			public void execute() {
				Utility.startActivity(dictionaryClient.getContext(), ActivityHelper.ABOUT_ACTIVITY);
			}
		});
	}

	@Override
	public void init() {
		final List<Button> bottomButtons = dictionaryClient.getBottomButtons();
		for(final Button button : bottomButtons) {
			switch (button.getId()) {
			case R.id.manageButton:
				initManageButton(button);
				break;
			case R.id.historyButton:
				initHistoryButton(button);
				break;
			case R.id.selectButton:
				initSelectButton(button);
				break;
			case R.id.moreButton:
				initMoreButton(button);
				break;
			default:
				break;
			}
		}
	}

	private void showMoreList() {
		// Init label array to set items for AlertDialog
		final String []mainButtonLabels = dictionaryClient.getContext().getResources().getStringArray(R.array.mainButtons);

		new AlertDialog.Builder(dictionaryClient.getContext()).
		setTitle(R.string.operationDialogTitle).
		setIcon(R.drawable.crystal_keyboard).
		setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		}).setItems(mainButtonLabels, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				executors.get(which).execute();
			}
		}).create().show();
	}

	private void initManageButton(final Button button) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				Utility.startActivityForResult((Activity)dictionaryClient.getContext(), ActivityHelper.MANAGE_ACTIVITY, ActivityHelper.MANAGE_REQUEST);
			}
		});
	}

	private void initHistoryButton(final Button button) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				historyDisplayer.showHistoryDialog(dictionaryClient.getHistoryList());
			}
		});
	}

	private void initMoreButton(final Button button) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				showMoreList();
			}
		});
	}

	private void initSelectButton(final Button button) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				Utility.selectText(dictionaryClient.getContext(), dictionaryClient.getResultView());
			}
		});
	}

	private interface ButtonExecutor {
		void execute();
	}
}
