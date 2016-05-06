package com.cube.storm.message.lib.listener;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Listener interface called after the GCM registration has been completed
 *
 * @author Callum Taylor
 * @project StormMessage
 */
public interface RegisterListener
{
	public void onDeviceRegistered(@NonNull Context context, @Nullable String token);
}
