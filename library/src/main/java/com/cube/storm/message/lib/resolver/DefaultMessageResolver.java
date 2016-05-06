package com.cube.storm.message.lib.resolver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.cube.storm.message.R;

import java.util.Random;

/**
 * Class for resolving and handling messages sent via GCM
 *
 * @author Callum Taylor
 * @project LightningMessage
 */
public class DefaultMessageResolver extends MessageResolver
{
	public boolean resolve(Context context, Bundle data)
	{
		String message = data.getString("message");

		NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
		style.bigText(message);

		Intent startingIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setContentIntent(PendingIntent.getActivity(context, 0, startingIntent, PendingIntent.FLAG_CANCEL_CURRENT));

		ApplicationInfo ai;
		PackageManager pm = context.getPackageManager();
		try
		{
			ai = pm.getApplicationInfo(context.getPackageName(), 0);
		}
		catch (final PackageManager.NameNotFoundException e)
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

		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(new Random().nextInt(), builder.build());

		return true;
	}
}
