package com.cube.storm.message.lib.resolver;

import android.content.Context;

import java.util.Map;

/**
 * Class for resolving and handling messages sent via GCM
 *
 * @author Callum Taylor
 * @project LightningMessage
 */
public abstract class MessageResolver
{
	/**
	 * Called when a message is received
	 *
	 * @param context The application context
	 * @param data The map of data sent from the message
	 *
	 * @return True if the notification was handled, false if not
	 */
	public abstract boolean resolve(Context context, Map<String, String> data);
}
