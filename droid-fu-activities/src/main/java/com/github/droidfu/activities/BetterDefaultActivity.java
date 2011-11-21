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

package com.github.droidfu.activities;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;

import com.github.droidfu.DroidFuApplication;
import com.github.droidfu.dialogs.DialogClickListener;

public class BetterDefaultActivity extends Activity implements BetterActivity {

    private boolean wasCreated, wasInterrupted;

    private int progressDialogTitleId;

    private int progressDialogMsgId;

    private Intent currentIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.wasCreated = true;
        this.currentIntent = getIntent();

        Application application = getApplication();
        if (application instanceof DroidFuApplication) {
            ((DroidFuApplication) application).setActiveContext(getClass().getCanonicalName(), this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ((DroidFuApplication)
        // getApplication()).resetActiveContext(getClass().getCanonicalName());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        wasInterrupted = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasCreated = wasInterrupted = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.currentIntent = intent;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return BetterActivityHelper.createProgressDialog(this, progressDialogTitleId,
            progressDialogMsgId);
    }

    public void setProgressDialogTitleId(int progressDialogTitleId) {
        this.progressDialogTitleId = progressDialogTitleId;
    }

    public void setProgressDialogMsgId(int progressDialogMsgId) {
        this.progressDialogMsgId = progressDialogMsgId;
    }

    public int getWindowFeatures() {
        return BetterActivityHelper.getWindowFeatures(this);
    }

    public boolean isRestoring() {
        return wasInterrupted;
    }

    public boolean isResuming() {
        return !wasCreated;
    }

    public boolean isLaunching() {
        return !wasInterrupted && wasCreated;
    }

    public boolean isApplicationBroughtToBackground() {
        return BetterActivityHelper.isApplicationBroughtToBackground(this);
    }

    public Intent getCurrentIntent() {
        return currentIntent;
    }

    public boolean isLandscapeMode() {
        return getWindowManager().getDefaultDisplay().getOrientation() == 1;
    }

    public boolean isPortraitMode() {
        return !isLandscapeMode();
    }

    public AlertDialog newYesNoDialog(int titleResourceId, int messageResourceId,
            OnClickListener listener) {
        return BetterActivityHelper.newYesNoDialog(this, getString(titleResourceId),
            getString(messageResourceId), android.R.drawable.ic_dialog_info, listener);
    }

    public AlertDialog newInfoDialog(int titleResourceId, int messageResourceId) {
        return BetterActivityHelper.newMessageDialog(this, getString(titleResourceId),
            getString(messageResourceId), android.R.drawable.ic_dialog_info);
    }

    public AlertDialog newAlertDialog(int titleResourceId, int messageResourceId) {
        return BetterActivityHelper.newMessageDialog(this, getString(titleResourceId),
            getString(messageResourceId), android.R.drawable.ic_dialog_alert);
    }

    public AlertDialog newErrorHandlerDialog(int titleResourceId, Exception error) {
        return BetterActivityHelper.newErrorHandlerDialog(this, getString(titleResourceId), error);
    }

    public AlertDialog newErrorHandlerDialog(Exception error) {
        return newErrorHandlerDialog(getResources().getIdentifier(
            BetterActivityHelper.ERROR_DIALOG_TITLE_RESOURCE, "string", getPackageName()), error);
    }

    public <T> Dialog newListDialog(String title, List<T> elements,
            DialogClickListener<T> listener,
            boolean closeOnSelect) {
        return BetterActivityHelper.newListDialog(this, title, elements, listener, closeOnSelect);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        BetterActivityHelper.handleApplicationClosing(this, keyCode);
        return super.onKeyDown(keyCode, event);
    }
}
