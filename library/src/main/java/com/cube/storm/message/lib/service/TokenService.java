package com.cube.storm.message.lib.service;

import android.text.TextUtils;

import com.cube.storm.MessageSettings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * This class registers the application for a GCM token. Must be defined in your Application manifest, and MessageSettings
 * class.
 * <pre>
 * 	&lt;service android:name="com.cube.storm.message.lib.service.TokenService" android:exported="false" /&gt;
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
public class TokenService extends FirebaseInstanceIdService
{
	@Override public void onTokenRefresh()
	{
		super.onTokenRefresh();

		if (TextUtils.isEmpty(MessageSettings.getInstance().getProjectNumber()))
		{
			throw new IllegalArgumentException("Project number can not be empty");
		}

		String token = FirebaseInstanceId.getInstance().getToken();
		onTokenReceived(token);
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
