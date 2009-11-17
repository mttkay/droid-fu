package com.github.droidfu.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.github.droidfu.DroidFuApplication;

public class BetterService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        ((DroidFuApplication) getApplication()).setActiveContext(getClass().getCanonicalName(),
            this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
