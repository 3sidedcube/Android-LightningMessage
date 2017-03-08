package com.cube.storm.message.lib.receiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.cube.storm.MessageSettings;

/**
 * The base receiver class for receiving GCM notifications. You must implement this class in order for notifications
 * to correctly wake the app. You must also have set {@link MessageSettings#receiver} class to wake the correct
 * receiver class.
 * </p>
 * <pre>
 * &lt;receiver
 * 		android:name="com.cube.storm.message.lib.receiver.GcmBroadcastReceiver"
 * 		android:exported="true"
 * &gt;
 * 		&lt;intent-filter&gt;
 * 			&lt;action android:name="com.google.firebase.MESSAGING_EVENT" /&gt;
 * 		&lt;/intent-filter&gt;
 * &lt;/receiver&gt;
 * </pre>
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver
{
	@Override public void onReceive(Context context, Intent intent)
	{
		ComponentName comp = new ComponentName(context, MessageSettings.getInstance().getReceiver().getClass().getName());
		startWakefulService(context, intent.setComponent(comp));
		setResultCode(Activity.RESULT_OK);
	}
}
