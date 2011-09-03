package com.megadict.utility;

import android.content.Context;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class Utility {
	public static void messageBox(final Context context, final String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void messageBox(final Context context, final int resID) {
		Toast.makeText(context, context.getString(resID), Toast.LENGTH_SHORT).show();
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

	public static void disableSoftKeyboard(final Context context, final EditText editText) {
		final InputMethodManager imm =
				(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}
