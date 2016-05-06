package com.cube.storm.message.example;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.cube.storm.MessageSettings;
import com.cube.storm.message.lib.resolver.MessageResolver;

/**
 * // TODO: Add class description
 *
 * @author Callum Taylor
 * @project LightningMessage
 */
public class MainApplication extends Application
{
	@Override public void onCreate()
	{
		super.onCreate();

		new MessageSettings.Builder(this)
			.projectNumber("")
			.registerMessageResolver("test", new MessageResolver()
			{
				@Override public boolean resolve(Context context, Bundle data)
				{
					Log.e("MESSAGE", "Test Message received");
					return false;
				}
			})
			.build();
	}
}
