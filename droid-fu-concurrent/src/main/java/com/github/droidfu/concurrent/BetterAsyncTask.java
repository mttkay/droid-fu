/* Copyright (c) 2009 Matthias Käppler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.droidfu.concurrent;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;

import com.github.droidfu.DroidFuApplication;
import com.github.droidfu.activities.BetterActivity;

/**
 * Works in a similar way to AsyncTask but provides extra functionality.
 *
 * 1) It keeps track of the active instance of each Context, ensuring that the
 * correct instance is reported to. This is very useful if your Activity is
 * forced into the background, or the user rotates his device.
 *
 * 2) A progress dialog is automatically shown. See useCustomDialog()
 * disableDialog()
 *
 * 3) If an Exception is thrown from inside doInBackground, this is now handled
 * by the handleError method.
 *
 * 4) You should now longer override onPreExecute(), doInBackground() and
 * onPostExecute(), instead you should use before(), doCheckedInBackground() and
 * after() respectively.
 *
 * These features require that the Application extends DroidFuApplication.
 *
 * @param <ParameterT>
 * @param <ProgressT>
 * @param <ReturnT>
 */
public abstract class BetterAsyncTask<ParameterT, ProgressT, ReturnT> extends
        AsyncTask<ParameterT, ProgressT, ReturnT> {

    private final DroidFuApplication appContext;
    private final boolean contextIsDroidFuActivity;

    private Exception error;

    private boolean isTitleProgressEnabled,
            isTitleProgressIndeterminateEnabled = true;

    private final String callerId;

    private BetterAsyncTaskCallable<ParameterT, ProgressT, ReturnT> callable;

    private int dialogId = 0;

    /**
     * Creates a new BetterAsyncTask who displays a progress dialog on the specified Context.
     *
     * @param context
     */
    public BetterAsyncTask(Context context) {

        if (!(context.getApplicationContext() instanceof DroidFuApplication)) {
            throw new IllegalArgumentException(
                    "context bound to this task must be a DroidFu context (DroidFuApplication)");
        }
        this.appContext = (DroidFuApplication) context.getApplicationContext();
        this.callerId = context.getClass().getCanonicalName();
        this.contextIsDroidFuActivity = context instanceof BetterActivity;

        appContext.setActiveContext(callerId, context);

        if (contextIsDroidFuActivity) {
            int windowFeatures = ((BetterActivity) context).getWindowFeatures();
            if (Window.FEATURE_PROGRESS == (Window.FEATURE_PROGRESS & windowFeatures)) {
                this.isTitleProgressEnabled = true;
            } else if (Window.FEATURE_INDETERMINATE_PROGRESS == (Window.FEATURE_INDETERMINATE_PROGRESS & windowFeatures)) {
                this.isTitleProgressIndeterminateEnabled = true;
            }
        }
    }

    /**
     * Gets the most recent instance of this Context.
     * This may not be the Context used to construct this BetterAsyncTask as that Context might have been destroyed
     * when a incoming call was received, or the user rotated the screen.
     *
     * @return The current Context, or null if the current Context has ended, and a new one has not spawned.
     */
    protected Context getCallingContext() {
        try {
            Context caller = (Context) appContext.getActiveContext(callerId);
            if (caller == null || !this.callerId.equals(caller.getClass().getCanonicalName())
                    || (caller instanceof Activity && ((Activity) caller).isFinishing())) {
                // the context that started this task has died and/or was
                // replaced with a different one
                return null;
            }
            return caller;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected final void onPreExecute() {
        Context context = getCallingContext();
        if (context == null) {
            Log.d(BetterAsyncTask.class.getSimpleName(), "skipping pre-exec handler for task "
                    + hashCode() + " (context is null)");
            cancel(true);
            return;
        }

        if (contextIsDroidFuActivity) {
            Activity activity = (Activity) context;
            if (dialogId > -1) {
                activity.showDialog(dialogId);
            }
            if (isTitleProgressEnabled) {
                activity.setProgressBarVisibility(true);
            } else if (isTitleProgressIndeterminateEnabled) {
                activity.setProgressBarIndeterminateVisibility(true);
            }
        }
        before(context);
    }

    /**
     * Override to run code in the UI thread before this Task is run.
     *
     * @param context
     */
    protected void before(Context context) {
    }

    @Override
    protected final ReturnT doInBackground(ParameterT... params) {
        ReturnT result = null;
        Context context = getCallingContext();
        try {
            result = doCheckedInBackground(context, params);
        } catch (Exception e) {
            this.error = e;
        }
        return result;
    }

    /**
     * Override to perform computation in a background thread
     *
     * @param context
     * @param params
     * @return
     * @throws Exception
     */
    protected ReturnT doCheckedInBackground(Context context, ParameterT... params) throws Exception {
        if (callable != null) {
            return callable.call(this);
        }
        return null;
    }

    /**
     * Runs in the UI thread if there was an exception throw from doCheckedInBackground
     *
     * @param context The most recent instance of the Context that executed this BetterAsyncTask
     * @param error The thrown exception.
     */
    protected abstract void handleError(Context context, Exception error);

    @Override
    protected final void onPostExecute(ReturnT result) {
        Context context = getCallingContext();
        if (context == null) {
            Log.d(BetterAsyncTask.class.getSimpleName(), "skipping post-exec handler for task "
                    + hashCode() + " (context is null)");
            return;
        }

        if (contextIsDroidFuActivity) {
            Activity activity = (Activity) context;
            if (dialogId > -1) {
                activity.removeDialog(dialogId);
            }
            if (isTitleProgressEnabled) {
                activity.setProgressBarVisibility(false);
            } else if (isTitleProgressIndeterminateEnabled) {
                activity.setProgressBarIndeterminateVisibility(false);
            }
        }

        if (failed()) {
            handleError(context, error);
        } else {
            after(context, result);
        }
    }

    /**
     * A replacement for onPostExecute. Runs in the UI thread after doCheckedInBackground returns.
     *
     * @param context The most recent instance of the Context that executed this BetterAsyncTask
     * @param result The result returned from doCheckedInBackground
     */
    protected abstract void after(Context context, ReturnT result);

    /**
     * Has an exception been thrown inside doCheckedInBackground()
     * @return
     */
    public boolean failed() {
        return error != null;
    }

    /**
     * Use a BetterAsyncTaskCallable instead of overriding doCheckedInBackground()
     *
     * @param callable
     */
    public void setCallable(BetterAsyncTaskCallable<ParameterT, ProgressT, ReturnT> callable) {
        this.callable = callable;
    }

    /**
     * Use a custom resource ID for the progress dialog
     *
     * @param dialogId
     */
    public void useCustomDialog(int dialogId) {
        this.dialogId = dialogId;
    }

    /**
     * Disable the display of a dialog during the execution of this task.
     */
    public void disableDialog() {
        this.dialogId = -1;
    }
}
