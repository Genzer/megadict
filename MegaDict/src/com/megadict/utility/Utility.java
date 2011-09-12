package com.megadict.utility;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.megadict.R;

public final class Utility {
	public static final String TAG = "Utility";

	private Utility() {}

	public static void messageBox(final Context context, final String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void messageBox(final Context context, final int resID) {
		Toast.makeText(context, context.getString(resID), Toast.LENGTH_SHORT).show();
	}

	public static void messageBoxLong(final Context context, final int resID) {
		Toast.makeText(context, context.getString(resID), Toast.LENGTH_LONG).show();
	}

	public static void startActivity(final Context context, final String className) {
		try {
			final Class<?> classObj = Class.forName(className);
			final Intent intent = new Intent(context, classObj);
			context.startActivity(intent);
		} catch (final ClassNotFoundException e) {
			messageBox(context, className + " class not found.");
		}
	}

	public static void startActivityForResult(final Activity activity, final String className, final int requestCode) {
		try {
			final Class<?> classObj = Class.forName(className);
			final Intent intent = new Intent(activity, classObj);
			activity.startActivityForResult(intent, requestCode);
		} catch (final ClassNotFoundException e) {
			messageBox(activity, className + " class not found.");
		}
	}

	public static void disableSoftKeyboard(final Context context, final EditText editText) {
		final InputMethodManager imm =
				(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	public static void updateLocale(final Context context, final String language) {
		final Locale locale = new Locale(language);
		Locale.setDefault(locale);
		final Configuration config = new Configuration();
		config.locale = locale;
		context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
	}

	public static void selectText(final Context context, final WebView resultView) {
		try {
			final KeyEvent shiftPressEvent =
					new KeyEvent(0, 0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT, 0, 0);
			shiftPressEvent.dispatch(resultView);
			Utility.messageBox(context, R.string.selectText);
		} catch (final Exception e) {
			Log.e(TAG, context.getString(R.string.canNotSelectText), e);
		}
	}

	public static boolean isOnline(final Context context) {
		final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo info = cm.getActiveNetworkInfo();
		return info == null ? false : info.isConnectedOrConnecting();
	}

}
