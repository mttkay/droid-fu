package com.github.droidfu.activities;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.ListAdapter;

import com.github.droidfu.DroidFuApplication;
import com.github.droidfu.adapters.ListAdapterWithProgress;
import com.github.droidfu.dialogs.DialogClickListener;

public class BetterListActivity extends ListActivity implements BetterActivity {

    private static final String IS_BUSY_EXTRA = "is_busy";

    private boolean wasCreated, wasInterrupted;

    private int progressDialogTitleId;

    private int progressDialogMsgId;

    private Intent currentIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.wasCreated = true;
        this.currentIntent = getIntent();

        ((DroidFuApplication) getApplication()).setActiveContext(getClass().getCanonicalName(),
            this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ((DroidFuApplication)
        // getApplication()).resetActiveContext(getClass().getCanonicalName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ListAdapter adapter = getListAdapter();
        if (adapter instanceof ListAdapterWithProgress<?>) {
            boolean isLoading = ((ListAdapterWithProgress<?>) adapter).isLoadingData();
            outState.putBoolean(IS_BUSY_EXTRA, isLoading);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ListAdapter adapter = getListAdapter();
        if (adapter instanceof ListAdapterWithProgress<?>) {
            boolean isLoading = savedInstanceState.getBoolean(IS_BUSY_EXTRA);
            ((ListAdapterWithProgress<?>) adapter).setIsLoadingData(isLoading);
        }
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

    public AlertDialog newErrorDialog(int titleResourceId, Exception error) {
        return BetterActivityHelper.newMessageDialog(this, getString(titleResourceId), error);
    }

    public AlertDialog newErrorDialog(Exception error) {
        return newErrorDialog(getResources().getIdentifier(
            BetterActivityHelper.ERROR_DIALOG_TITLE_RESOURCE, "string", getPackageName()), error);
    }

    public <T> Dialog newListDialog(List<T> elements, DialogClickListener<T> listener,
            boolean closeOnSelect) {
        return BetterActivityHelper.newListDialog(this, elements, listener, closeOnSelect);
    }
}
