package com.megadict.utility;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class Utility {
	private static boolean localChanged;
	private static final String MAIN_PREF = "main_preferences";
	private static final String LANGUAGE_KEY = "language";
	private static final String DEFAULT_LANGUAGE = "en";

	public static void messageBox(final Context context, final String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void startActivity(final Context context, final String className) {
		try {
			final Class<?> classObj = Class.forName(className);
			final Intent intent =
				new Intent(context, classObj);
			context.startActivity(intent);
		} catch (final ClassNotFoundException e) {
			messageBox(context, className + " class not found.");
		}
	}

	public static void setLocale(final Context context, final String value) {
		// Store to shared preferences.
		context.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE).edit().putString(LANGUAGE_KEY, value).commit();
		// Update configuration.
		final Locale locale = new Locale(value);
		Locale.setDefault(locale);
		final Configuration config = new Configuration();
		config.locale = locale;
		context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
	}

	public static String getLocale(final Context context) {
		return context.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE).getString(LANGUAGE_KEY, DEFAULT_LANGUAGE);
	}

	public static void setLocaleChanged(final boolean b) {
		localChanged = b;
	}

	public static boolean isLocaleChanged() {
		return localChanged;
	}

	public static void disableSoftKeyboard(final Context context, final EditText editText) {
		final InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}
