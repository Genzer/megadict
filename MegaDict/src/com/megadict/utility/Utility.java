package com.megadict.utility;

import android.content.Context;
import android.widget.Toast;

public class Utility {
	public static void messageBox(final Context context, final String message)
	{
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
