package com.github.droidfu.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectionChangedBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("CONN CHANGE");
		BetterHttpRequest.updateProxySettings(context);
	}

}
