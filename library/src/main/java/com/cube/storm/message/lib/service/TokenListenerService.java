package com.cube.storm.message.lib.service;

import android.content.Intent;

import com.cube.storm.MessageSettings;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * This class listens for any token changes and re-calls the token service to fetch the new token. Should be registered
 * via your manifest. Not required, but recommended.
 * <pre>
 * 	<service
 *		android:name="com.cube.storm.message.lib.service.TokenListenerService"
 *		android:exported="false"
 *	>
 *		<intent-filter>
 *			<action android:name="com.google.android.c2dm.intent.REGISTRATION" />
 *			<action android:name="com.google.android.gms.iid.InstanceID" />
 *		</intent-filter>
 *	</service>
 * </pre>
 *
 * @author Callum Taylor
 * @project LightningMessage
 */
public class TokenListenerService extends InstanceIDListenerService
{
	@Override public void onTokenRefresh()
	{
		Intent intent = new Intent(this, MessageSettings.getInstance().getTokenService());
		startService(intent);
	}
}
