#Storm Library - Module Message

Storm is a collection of libraries that helps make mobile and desktop applications easy to create using a high quality WYSIACATWYG editor.

This module's purpose is to handle Google Cloud Messages sent via the Storm CMS.

#Usage

##Gradle

Simply include the following for your gradle dependencies `com.3sidedcube.storm:message:0.1a`.

**Note** The versioning of the library will always be as follows:

`Major version.Minor version.Bug fix`

It is safe to use `+` in part of of the `Bug fix` version, but do not trust it 100%. Always use a *specific* version to prevent regression errors.

##Code

In your application singleton, add the following code

```java

MessageSettings messageSettings = new MessageSettings.Builder(this)
	.projectNumber("xxxxxxxxxxx")
	.registerListener(new RegisterListener()
	{
		@Override public void onDeviceRegistered(@NonNull Context context, @Nullable String token)
		{
			if (!TextUtils.isEmpty(token))
			{
				AsyncHttpClient client = new AsyncHttpClient(contentSettings.getContentBaseUrl());

				try
				{
					String appId = contentSettings.getAppId();
					String[] appIdParts = appId.split("-");

					JsonObject jsonData = new JsonObject();
					jsonData.addProperty("appId", appIdParts[2]);
					jsonData.addProperty("token", token);
					jsonData.addProperty("idiom", "android");

					JsonEntity postData = new JsonEntity(jsonData);

					client.post(contentSettings.getContentVersion() + "/push/token", postData, null);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	})
	.build();
```

Then in your manifest add the receiver filter

```java
<!-- Push Notifications -->
<receiver
	android:name="com.cube.storm.message.lib.receiver.MessageReceiver"
	android:permission="com.google.android.c2dm.permission.SEND"
>
	<intent-filter>
		<action android:name="com.google.android.c2dm.intent.RECEIVE" />
		<category android:name="your.package.name" />
	</intent-filter>
</receiver>
```

#Documentation

See the [Javadoc](http://3sidedcube.github.io/Android-LightningMessage/) for full in-depth code-level documentation

#Contributors

[Callum Taylor (9A8BAD)](http://keybase.io/scruffyfox), [Tim Mathews (5C4869)](https://keybase.io/timxyz), [Matt Allen (DB74F5)](https://keybase.io/mallen), [Alan Le Fournis (067EA0)](https://keybase.io/alan3sc)

#License

See LICENSE.md
