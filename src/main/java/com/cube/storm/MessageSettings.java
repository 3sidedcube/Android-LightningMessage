package com.cube.storm;

import android.content.Context;

/**
 * This is the entry point class of the library. To enable the use of the library, you must instantiate
 * a new {@link com.cube.storm.MessageSettings.Builder} object in your {@link android.app.Application} singleton class.
 *
 * This class should not be directly instantiated.
 *
 * @author Callum Taylor
 * @project StormMessage
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
			throw new IllegalAccessError("You must build the Ui settings object first using MessageSettings$Builder");
		}

		return instance;
	}

	/**
	 * Default private constructor
	 */
	private MessageSettings(){}

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
		public Builder(Context context)
		{
			this.construct = new MessageSettings();
			this.context = context.getApplicationContext();
		}

		/**
		 * Builds the final settings object and sets its instance. Use {@link #getInstance()} to retrieve the settings
		 * instance.
		 *
		 * @return The newly set {@link com.cube.storm.MessageSettings} instance
		 */
		public MessageSettings build()
		{
			return (MessageSettings.instance = construct);
		}
	}
}
