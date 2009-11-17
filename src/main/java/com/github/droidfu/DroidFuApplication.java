package com.github.droidfu;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

public class DroidFuApplication extends Application {

    private HashMap<String, WeakReference<Context>> contextObjects = new HashMap<String, WeakReference<Context>>();

    public synchronized Context getActiveContext(String className) {
        WeakReference<Context> ref = contextObjects.get(className);
        if (ref == null) {
            return null;
        }
        return ref.get();
    }

    public synchronized void setActiveContext(String className, Context context) {
        WeakReference<Context> ref = new WeakReference<Context>(context);
        this.contextObjects.put(className, ref);
    }

    public synchronized void resetActiveContext(String className) {
        contextObjects.remove(className);
    }

    /**
     * Checks whether there are applications installed which are able to handle
     * the given action/data.
     * 
     * @param action
     *        the action to check
     * @param the
     *        data URI to check
     * @return true if there are apps which will respond to this action/data
     */
    public boolean isIntentAvailable(String action, Uri uri) {
        final Intent intent = (uri != null) ? new Intent(action, uri) : new Intent(action);
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
            PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * Checks whether there are applications installed which are able to handle
     * the given intent.
     * 
     * @param intent
     *        the intent to check
     * @return true if there are apps which will respond to this intent
     */
    public boolean isIntentAvailable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
            PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
}
