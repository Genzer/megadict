package com.megadict.utility;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Utility {
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
}
