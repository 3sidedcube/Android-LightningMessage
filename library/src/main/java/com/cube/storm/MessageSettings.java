package com.cube.storm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.cube.storm.message.lib.receiver.MessageReceiver;
import com.cube.storm.message.lib.resolver.DefaultMessageResolver;
import com.cube.storm.message.lib.resolver.MessageResolver;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the entry point class of the library. To enable the use of the library, you must instantiate
 * a new {@link com.cube.storm.MessageSettings.Builder} object in your {@link android.app.Application} singleton class.
 *
 * This class should not be directly instantiated.
 *
 * @author Callum Taylor
 * @project LightningMessage
 */
public class MessageSettings
{
	/**
	 * The singleton instance of the settings
	 */
	private static MessageSettings instance;

	/**
	 * Gets the instance of the {@link com.cube.storm.MessageSettings} class
	 * Throws a {@link IllegalAccessError} if the singleton has not been instantiated properly
	 *
	 * @return The instance
	 */
	public static MessageSettings getInstance()
	{
		if (instance == null)
		{
			throw new IllegalAccessError("You must build the Message settings object first using MessageSettings$Builder");
		}

		return instance;
	}

	/**
	 * Default private constructor
	 */
	private MessageSettings(){}

	/**
	 * The gcm receiver class used to receive messages from Storm.
	 */
	@Getter @Setter private MessageReceiver receiver;

	/**
	 * List of resolvers for different types of GCM messages received
	 */
	@Getter private Map<String, MessageResolver> messageResolvers = new HashMap<>();

	/**
	 * The builder class for {@link com.cube.storm.MessageSettings}. Use this to create a new {@link com.cube.storm.MessageSettings} instance
	 * with the customised properties specific for your project.
	 *
	 * Call {@link #build()} to build the settings object.
	 */
	public static class Builder
	{
		/**
		 * The temporary instance of the {@link com.cube.storm.MessageSettings} object.
		 */
		private MessageSettings construct;

		/**
		 * Default constructor
		 */
		public Builder()
		{
			this.construct = new MessageSettings();

			registerMessageResolver(MessageReceiver.TYPE_DEFAULT, new DefaultMessageResolver());

			messageReceiver(new MessageReceiver());
		}

		/**
		 * Registers a resolver for a type of notification
		 *
		 * @param type The type of message received
		 * @param resolver The handler for the receiver
		 *
		 * @return The builder to allow for chaining
		 */
		public Builder registerMessageResolver(String type, @NonNull MessageResolver resolver)
		{
			construct.messageResolvers.put(type, resolver);
			return this;
		}

		/**
		 * Sets the receiver for the module. You must also set this in your manifest for the framework
		 * to use correctly.
		 * <p/>
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
		 * @param receiver The receiver to use
		 *
		 * @return The builder to allow for chaining
		 */
		public Builder messageReceiver(@Nullable MessageReceiver receiver)
		{
			construct.receiver = receiver;
			return this;
		}

		/**
		 * Builds the final settings object and sets its instance. Use {@link #getInstance()} to retrieve the settings
		 * instance.
		 * <p/>
		 * This method will also register the receiver to the given context in {@link #Builder(android.content.Context)} if
		 * the receiver is not null.
		 *
		 * @return The newly set {@link com.cube.storm.MessageSettings} instance
		 */
		public MessageSettings build()
		{
			MessageSettings.instance = construct;
			return construct;
		}
	}
}
