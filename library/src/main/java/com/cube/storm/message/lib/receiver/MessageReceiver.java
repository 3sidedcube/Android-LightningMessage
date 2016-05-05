package com.cube.storm.message.lib.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;

import com.cube.storm.MessageSettings;
import com.cube.storm.message.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import lombok.Getter;

/**
 * Receiver class for receiving messages from Storm CMS.
 * <p/>
 * Override this class to handle messages sent via storm
 *
 * @author Callum Taylor
 * @project LightningMessage
 */
public class MessageReceiver extends BroadcastReceiver
{
	/**
	 * List of previously sent notification IDs to prevent duplicates from being shown
	 */
	protected static final Set<String> RECEIVED_IDS = new LinkedHashSet<String>();

	/**
	 * Default notification type with a simple message
	 */
	public static final String TYPE_DEFAULT = "default";

	@Getter private Context context;

	@Override public final void onReceive(Context context, Intent intent)
	{
		this.context = context.getApplicationContext();

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
		String messageType = gcm.getMessageType(intent);

		if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
		{
			// error
		}
		else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
		{
			// deleted (??)
		}
		else
		{
			String type = intent.getExtras().getString("type");
			handleNotification(type, intent.getExtras());
		}
	}

	/**
	 * Implement this to handle the notifications sent from the server
	 *
	 * @param type The type of alert
	 * @param data The bundle data with the alert
	 *
	 * @return True if the notification was handled, false if not
	 */
	public boolean handleNotification(String type, Bundle data)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

		if (prefs.contains("notifications_enabled") && !prefs.getBoolean("notifications_enabled", true))
		{
			return true;
		}

		if (data.containsKey("id"))
		{
			if (RECEIVED_IDS.contains(data.getString("id")))
			{
				return true;
			}

			RECEIVED_IDS.add(data.getString("id"));
		}

		// default notification
		if (TYPE_DEFAULT.equals(type))
		{
			String message = data.getString("message");

			BigTextStyle style = new BigTextStyle();
			style.bigText(message);

			Intent startingIntent = getContext().getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());

			Builder builder = new Builder(getContext());
			builder.setContentIntent(PendingIntent.getActivity(getContext(), 0, startingIntent, PendingIntent.FLAG_CANCEL_CURRENT));

			ApplicationInfo ai;
			PackageManager pm = context.getPackageManager();
			try
			{
				ai = pm.getApplicationInfo(context.getPackageName(), 0);
			}
			catch (final NameNotFoundException e)
			{
				ai = null;
			}

			String applicationName = (ai != null ? String.valueOf(pm.getApplicationLabel(ai)) : "(unknown)");
			builder.setContentTitle(applicationName);

			builder.setTicker(message);
			builder.setContentText(message);
			builder.setStyle(style);
			builder.setSmallIcon(R.drawable.ic_notification);
			builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setAutoCancel(true);

			NotificationManager manager = (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);
			manager.notify(new Random().nextInt(), builder.build());

			return true;
		}

		return false;
	}

	/**
	 * Implement this to handle registration callback
	 *
	 * @param token The token of the push device
	 */
	public void registerCallback(@Nullable String token)
	{
		if (MessageSettings.getInstance().getRegisterListener() != null)
		{
			MessageSettings.getInstance().getRegisterListener().onDeviceRegistered(getContext(), token);
		}
	}
}
