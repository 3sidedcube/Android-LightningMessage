package com.cube.storm.message.lib.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.cube.storm.MessageSettings;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * This class registers the application for a GCM token. Must be defined in your Application manifest, and MessageSettings
 * class.
 * <pre>
 * 	<service android:name="com.cube.storm.message.lib.service.TokenService" android:exported="false" />
 * </pre>
 * <pre>
 *	new MessageSettings.Builder(this)
 *		.tokenService(TokenService.class)
 *		.build()
 * </pre>
 *
 * @author Callum Taylor
 * @project LightningMessage
 */
public class TokenService extends IntentService
{
	private static final String TAG = "TokenService";

	public TokenService()
	{
		super(TAG);
	}

	@Override protected void onHandleIntent(Intent intent)
	{
		InstanceID instanceID = InstanceID.getInstance(this);

		try
		{
			if (TextUtils.isEmpty(MessageSettings.getInstance().getProjectNumber()))
			{
				throw new IllegalArgumentException("Project number can not be empty");
			}

			String token = instanceID.getToken(MessageSettings.getInstance().getProjectNumber(), GoogleCloudMessaging.INSTANCE_ID_SCOPE);

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			prefs.edit().putString("push_id", token).putLong("push_id_time", System.currentTimeMillis()).apply();

			onTokenReceived(token);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Called when a token has been retrieved from the GCM provider
	 * @param token The token received
	 */
	public void onTokenReceived(String token)
	{
		if (MessageSettings.getInstance().getRegisterListener() != null)
		{
			MessageSettings.getInstance().getRegisterListener().onDeviceRegistered(this, token);
		}
	}
}
