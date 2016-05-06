package com.cube.storm.message.lib.receiver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.cube.storm.MessageSettings;
import com.cube.storm.message.lib.resolver.MessageResolver;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Receiver class for receiving messages from Storm CMS. You should register a resolver via {@link MessageSettings#messageResolvers} using type
 * rather than override this class.
 * <p>
 * Messages sent with the same "id" are ignored within the lifecycle of the application.
 * <p>
 * You must include this class (or subclass of this class) in your application manifest to receive notifications.
 * <pre>
 *	&lt;service
 *		android:name="com.cube.storm.message.lib.receiver.MessageReceiver"
 *		android:exported="false"
 *	&gt;
 *		&lt;intent-filter&gt;
 *			&lt;action android:name="com.google.android.c2dm.intent.RECEIVE" /&gt;
 *		&lt;/intent-filter&gt;
 *	&lt;/service&gt;
 * </pre>
 *
 * @author Callum Taylor
 * @project LightningMessage
 */
public class MessageReceiver extends GcmListenerService
{
	/**
	 * List of previously sent notification IDs to prevent duplicates from being shown
	 */
	protected static final Set<String> RECEIVED_IDS = new LinkedHashSet<String>();

	/**
	 * Default notification type with a simple message
	 */
	public static final String TYPE_DEFAULT = "default";

	@Override public void onMessageReceived(String from, Bundle data)
	{
		super.onMessageReceived(from, data);

		String type = data.getString("type");
		handleNotification(type, data);
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
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

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

		MessageResolver messageResolver = MessageSettings.getInstance().getMessageResolvers().get(type);

		if (messageResolver != null)
		{
			return messageResolver.resolve(getApplicationContext(), data);
		}

		return false;
	}
}
