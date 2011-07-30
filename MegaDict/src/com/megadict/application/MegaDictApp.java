/**
 * IDICTIONARY APPLICATION.
 * 
 * CREATION DATE 10-April-2011
 * 
 * DEVELOPER TEAM :PREPOOLK11 - IDICTGROUP
 */

package com.megadict.application;

import java.io.File;

import android.app.Application;

import com.megadict.business.DictionaryClient;
import com.megadict.business.ExternalStorage;

public class MegaDictApp extends Application {
	public DictionaryClient dictionaryClient = new DictionaryClient();
	public final File externalStorage = ExternalStorage.getExternalDirectory();;

	@Override
	public void onCreate()
	{
		super.onCreate();
	}
}
