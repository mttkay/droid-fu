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

public abstract class BetterAsyncTask<ParameterT, ProgressT, ReturnT> extends
        AsyncTask<ParameterT, ProgressT, ReturnT> {

    private DroidFuApplication appContext;

    private Exception error;

    private boolean contextIsDroidFuActivity, isTitleProgressEnabled,
            isTitleProgressIndeterminateEnabled = true;

    private String callerId;

    private BetterAsyncTaskCallable<ParameterT, ProgressT, ReturnT> callable;

    private int dialogId = 0;

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

    protected ReturnT doCheckedInBackground(Context context, ParameterT... params) throws Exception {
        if (callable != null) {
            return callable.call(this);
        }
        return null;
    }

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

    protected abstract void after(Context context, ReturnT result);

    public boolean failed() {
        return error != null;
    }

    public void setCallable(BetterAsyncTaskCallable<ParameterT, ProgressT, ReturnT> callable) {
        this.callable = callable;
    }

    public void useCustomDialog(int dialogId) {
        this.dialogId = dialogId;
    }

    public void disableDialog() {
        this.dialogId = -1;
    }
}
