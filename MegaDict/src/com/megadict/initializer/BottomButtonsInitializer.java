package com.megadict.initializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private final String []mainButtonLabels;
	private final Map<String, ButtonExecutor> executors = new HashMap<String, BottomButtonsInitializer.ButtonExecutor>();

	public BottomButtonsInitializer(final HistoryDisplayer historyDisplayer, final TextSelector textSelector, final BusinessComponent businessComponent, final DictionaryComponent dictionaryComponent) {
		super(businessComponent, dictionaryComponent);
		this.historyDisplayer = historyDisplayer;
		this.textSelector = textSelector;
		// Init label array to set items for AlertDialog
		mainButtonLabels = dictionaryComponent.getContext().getResources().getStringArray(R.array.mainButtons);
		// Init executors to execute items in AlertDialog.
		initExecutors();
	}

	private void initExecutors() {
		final Context context = dictionaryComponent.getContext();
		executors.put(context.getString(R.string.manageButtonLabel) ,new ButtonExecutor() {
			@Override
			public void execute() {
				Utility.startActivity(context, "com.megadict.activity.ManageActivity");
			}
		});
		executors.put(context.getString(R.string.historyButtonLabel), new ButtonExecutor() {
			@Override
			public void execute() {
				historyDisplayer.showHistoryDialog(businessComponent.getSearcher().getHistoryList());
			}
		});
		executors.put(context.getString(R.string.selectButtonLabel), new ButtonExecutor() {
			@Override
			public void execute() {
				textSelector.selectText(context, dictionaryComponent.getResultView());
			}
		});
		executors.put(context.getString(R.string.settingButtonLabel), new ButtonExecutor() {
			@Override
			public void execute() {
				Utility.startActivity(context, "com.megadict.activity.SettingActivity");
			}
		});
		executors.put(context.getString(R.string.aboutButtonLabel), new ButtonExecutor() {
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
		new AlertDialog.Builder(dictionaryComponent.getContext()).
		setTitle(R.string.historyDialogTitle).
		setIcon(R.drawable.crystal_history).
		setPositiveButton(R.string.deleteEllipsis, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {

			}
		}).setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.cancel();
			}
		}).setItems(mainButtonLabels, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				initDialogItems(which);
			}
		}).create().show();
	}

	private void initDialogItems(final int which) {
		executors.get(mainButtonLabels[which]).execute();
	}

	private void initBottomButtons(final Button button, final String activityClassName) {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				Utility.startActivity(dictionaryComponent.getContext(), activityClassName);
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
				textSelector.selectText(dictionaryComponent.getContext(), dictionaryComponent.getResultView());
			}
		});
	}

	@Override
	public void doNothing() { /* Do nothing for no reason. */}

	private interface ButtonExecutor {
		void execute();
	}
}
