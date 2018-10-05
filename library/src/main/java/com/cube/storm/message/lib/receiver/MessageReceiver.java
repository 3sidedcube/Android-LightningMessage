package com.cube.storm.message.lib.receiver;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.cube.storm.MessageSettings;
import com.cube.storm.message.lib.resolver.MessageResolver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.LinkedHashSet;
import java.util.Map;
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
 *			&lt;action android:name="com.google.firebase.MESSAGING_EVENT" /&gt;
 *		&lt;/intent-filter&gt;
 *	&lt;/service&gt;
 * </pre>
 *
 * @author Callum Taylor
 * @project LightningMessage
 */
public class MessageReceiver extends FirebaseMessagingService
{
	/**
	 * List of previously sent notification IDs to prevent duplicates from being shown
	 */
	protected static final Set<String> RECEIVED_IDS = new LinkedHashSet<String>();

	/**
	 * Default notification type with a simple message
	 */
	public static final String TYPE_DEFAULT = "default";

	@Override public void onMessageReceived(RemoteMessage remoteMessage)
	{
		super.onMessageReceived(remoteMessage);

		String type = remoteMessage.getData().get("type");
		handleNotification(type, remoteMessage.getData());
	}

	/**
	 * Implement this to handle the notifications sent from the server
	 *
	 * @param type The type of alert
	 * @param data The bundle data with the alert
	 *
	 * @return True if the notification was handled, false if not
	 */
	public boolean handleNotification(String type, Map<String, String> data)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		if (prefs.contains("notifications_enabled") && !prefs.getBoolean("notifications_enabled", true))
		{
			return true;
		}

		if (data.containsKey("id"))
		{
			if (RECEIVED_IDS.contains(data.get("id")))
			{
				return true;
			}

			RECEIVED_IDS.add(data.get("id"));
		}

		try
		{
			MessageResolver messageResolver = MessageSettings.getInstance().getMessageResolvers().get(type);

			if (messageResolver != null)
			{
				return messageResolver.resolve(getApplicationContext(), data);
			}
		}
		catch (IllegalAccessError iar)
		{
			return false;
		}

		return false;
	}

	@Override
	public void onNewToken(String s)
	{
		super.onNewToken(s);

		if (TextUtils.isEmpty(MessageSettings.getInstance().getProjectNumber()))
		{
			throw new IllegalArgumentException("Project number can not be empty");
		}

		if (MessageSettings.getInstance().getRegisterListener() != null)
		{
			MessageSettings.getInstance().getRegisterListener().onDeviceRegistered(this, s);
		}
	}
}
