package com.cube.storm;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.cube.storm.message.lib.listener.RegisterListener;
import com.cube.storm.message.lib.receiver.MessageReceiver;
import com.cube.storm.message.lib.resolver.DefaultMessageResolver;
import com.cube.storm.message.lib.resolver.MessageResolver;
import com.cube.storm.message.lib.service.TokenService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
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
	 * Project number as defined in the Google console project page under "project number"
	 */
	@Getter @Setter private String projectNumber;

	/**
	 * Callback used once the device has been registered for a push token
	 */
	@Getter @Setter private RegisterListener registerListener;

	/**
	 * The gcm receiver class used to receive messages from Storm.
	 */
	@Getter @Setter private MessageReceiver receiver;

	/**
	 * List of resolvers for different types of GCM messages received
	 */
	@Getter private Map<String, MessageResolver> messageResolvers = new HashMap<>();

	/**
	 * The broadcast receiver class for receiving new tokens. Should be the same as defined in the application manifest
	 */
	@Getter @Setter private Class<? extends TokenService> tokenService;

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

		private Context context;

		/**
		 * Default constructor
		 */
		public Builder(@NonNull Context context)
		{
			this.construct = new MessageSettings();
			this.context = context.getApplicationContext();

			registerMessageResolver(MessageReceiver.TYPE_DEFAULT, new DefaultMessageResolver());

			messageReceiver(new MessageReceiver());
			tokenService(TokenService.class);
		}

		/**
		 * Sets the project number from the console
		 *
		 * @param projectNumber The project number
		 *
		 * @return The builder to allow for chaining
		 */
		public Builder projectNumber(@NonNull String projectNumber)
		{
			construct.projectNumber = projectNumber;
			return this;
		}

		/**
		 * Sets the callback listener for when the device has been registered for a push token
		 *
		 * @param listener The listener to use
		 *
		 * @return The builder to allow for chaining
		 */
		public Builder registerListener(@Nullable RegisterListener listener)
		{
			construct.registerListener = listener;
			return this;
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
		 * Sets the service for getting the GCM token for the module. You must also set this in your manifest for the framework
		 * to use correctly.
		 * <p/>
		 * <pre>
		 * 	&lt;service android:name="com.cube.storm.message.lib.service.TokenService" android:exported="false" /&gt;
		 * </pre>
		 *
		 * @param tokenService The service to use
		 *
		 * @return The builder to allow for chaining
		 */
		public Builder tokenService(@NonNull Class<? extends TokenService> tokenService)
		{
			construct.tokenService = tokenService;
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
		 * @throws RuntimeException if the GcmSenderId option for the {@link FirebaseApp} is empty
		 */
		public MessageSettings build()
		{
			MessageSettings.instance = construct;

			FirebaseApp firebaseApp = null;

			try
			{
				firebaseApp = FirebaseApp.getInstance();
			}
			catch (Exception instanceException)
			{
				try
				{
					// Initialise Firebase
					firebaseApp = FirebaseApp.initializeApp(context, new FirebaseOptions.Builder()
						.setApplicationId(context.getPackageName())
						.setGcmSenderId(MessageSettings.getInstance().getProjectNumber())
						.build());
				}
				catch (Exception initialiseException)
				{
					instanceException.printStackTrace();
					initialiseException.printStackTrace();
				}
			}

			if (firebaseApp == null)
			{
				throw new RuntimeException("Failed to initialise or reuse existing Firebase app");
			}

			// Already has an instance of Firebase
			FirebaseOptions options = firebaseApp.getOptions();

			if (options == null || TextUtils.isEmpty(options.getGcmSenderId()))
			{
				throw new RuntimeException("Missing GcmSenderId from Firebase instance!");
			}

			if (construct.tokenService != null)
			{
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
				{
					context.startService(new Intent(context, construct.tokenService));
				}
			}

			// check if token already is registered
			String token = FirebaseInstanceId.getInstance().getToken();
			if (!TextUtils.isEmpty(token) && MessageSettings.getInstance().getRegisterListener() != null)
			{
				MessageSettings.getInstance().getRegisterListener().onDeviceRegistered(context, token);
			}

			return construct;
		}
	}
}
