package com.github.droidfu.activities;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import com.github.droidfu.DroidFuApplication;
import com.github.droidfu.dialogs.DialogClickListener;

public class BetterDefaultActivity extends Activity implements BetterActivity {

    private boolean wasCreated, wasInterrupted;

    private int progressDialogTitleId;

    private int progressDialogMsgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wasCreated = true;
        ((DroidFuApplication) getApplication()).setActiveContext(
                getClass().getCanonicalName(), this);
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
    protected Dialog onCreateDialog(int id) {
        return BetterActivityHelper.createProgressDialog(this,
                progressDialogTitleId, progressDialogMsgId);
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

    public void showInfoDialog(int titleResourceId, int messageResourceId) {
        BetterActivityHelper.showMessageDialog(this,
                getString(titleResourceId), getString(messageResourceId),
                android.R.drawable.ic_dialog_info);
    }

    public void showInfoDialog(int messageResourceId) {
        showInfoDialog(getResources().getIdentifier(
                BetterActivityHelper.INFO_DIALOG_TITLE_RESOURCE, "string",
                getPackageName()), messageResourceId);
    }

    public void showAlertDialog(int titleResourceId, int messageResourceId) {
        BetterActivityHelper.showMessageDialog(this,
                getString(titleResourceId), getString(messageResourceId),
                android.R.drawable.ic_dialog_alert);
    }

    public void showAlertDialog(int messageResourceId) {
        showAlertDialog(getResources().getIdentifier(
                BetterActivityHelper.ALERT_DIALOG_TITLE_RESOURCE, "string",
                getPackageName()), messageResourceId);
    }

    public void showErrorDialog(int titleResourceId, Exception error) {
        BetterActivityHelper.showMessageDialog(this,
                getString(titleResourceId), error);
    }

    public void showErrorDialog(Exception error) {
        showErrorDialog(getResources().getIdentifier(
                BetterActivityHelper.ERROR_DIALOG_TITLE_RESOURCE, "string",
                getPackageName()), error);
    }

    public <T> Dialog newListDialog(List<T> elements,
            DialogClickListener<T> listener, boolean closeOnSelect) {
        return BetterActivityHelper.newListDialog(this, elements, listener,
                closeOnSelect);
    }

}
