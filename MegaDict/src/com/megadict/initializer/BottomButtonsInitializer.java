package com.megadict.initializer;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.megadict.R;
import com.megadict.bean.BusinessComponent;
import com.megadict.bean.DictionaryComponent;
import com.megadict.business.HistoryDisplayer;
import com.megadict.business.TextSelector;
import com.megadict.utility.Utility;

public class BottomButtonsInitializer extends AbstractInitializer {
	private final HistoryDisplayer historyDisplayer;
	private final TextSelector textSelector;
	private final Context context;
	private final List<ButtonExecutor> executors = new ArrayList<BottomButtonsInitializer.ButtonExecutor>();

	public BottomButtonsInitializer(final HistoryDisplayer historyDisplayer, final TextSelector textSelector, final BusinessComponent businessComponent, final DictionaryComponent dictionaryComponent) {
		super(businessComponent, dictionaryComponent);
		this.historyDisplayer = historyDisplayer;
		this.textSelector = textSelector;
		this.context = dictionaryComponent.getContext();
		// Init executors to execute items in AlertDialog.
		initExecutors();
	}

	private void initExecutors() {
		executors.add(new ButtonExecutor() {
			@Override
			public void execute() {
				Utility.startActivity(context, "com.megadict.activity.ManageActivity");
			}
		});
		executors.add(new ButtonExecutor() {
			@Override
			public void execute() {
				historyDisplayer.showHistoryDialog(businessComponent.getSearcher().getHistoryList());
			}
		});
		executors.add(new ButtonExecutor() {
			@Override
			public void execute() {
				textSelector.selectText(context, dictionaryComponent.getResultView());
			}
		});
		executors.add(new ButtonExecutor() {
			@Override
			public void execute() {
				Utility.startActivity(context, "com.megadict.activity.SettingActivity");
			}
		});
		executors.add(new ButtonExecutor() {
			@Override
			public void execute() {
				Utility.startActivity(context, "com.megadict.activity.AboutActivity");
			}
		});
	}

	@Override
	public void init() {
		final List<Button> bottomButtons = dictionaryComponent.getBottomButtons();
		for(final Button button : bottomButtons) {
			switch (button.getId()) {
			case R.id.manageButton:
				initBottomButtons(button, "com.megadict.activity.ManageActivity");
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
		final String []mainButtonLabels = context.getResources().getStringArray(R.array.mainButtons);

		new AlertDialog.Builder(context).
		setTitle(R.string.historyDialogTitle).
		setIcon(R.drawable.crystal_history).
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

	private void initBottomButtons(final Button button, final String activityClassName) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				Utility.startActivity(context, activityClassName);
			}
		});
	}

	private void initHistoryButton(final Button button) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				historyDisplayer.showHistoryDialog(businessComponent.getSearcher().getHistoryList());
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
				textSelector.selectText(context, dictionaryComponent.getResultView());
			}
		});
	}

	@Override
	public void doNothing() { /* Do nothing for no reason. */}

	private interface ButtonExecutor {
		void execute();
	}
}
