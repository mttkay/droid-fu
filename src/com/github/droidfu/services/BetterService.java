package com.github.droidfu.services;

import java.lang.ref.WeakReference;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BetterService extends Service {

    private static volatile WeakReference<BetterService> currentInstance;

    public static synchronized BetterService getCurrentInstance() {
        return currentInstance.get();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        currentInstance = new WeakReference<BetterService>(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        currentInstance.clear();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
