package com.github.droidfu.activities;

import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
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
                "droidfu_info_dialog_title", "string", getPackageName()),
                messageResourceId);
    }

    public void showAlertDialog(int titleResourceId, int messageResourceId) {
        BetterActivityHelper.showMessageDialog(this,
                getString(titleResourceId), getString(messageResourceId),
                android.R.drawable.ic_dialog_alert);
    }

    public void showAlertDialog(int messageResourceId) {
        showAlertDialog(getResources().getIdentifier(
                "droidfu_alert_dialog_title", "string", getPackageName()),
                messageResourceId);
    }

    public void showErrorDialog(int titleResourceId, Exception error) {
        BetterActivityHelper.showMessageDialog(this,
                getString(titleResourceId), error);
    }

    public void showErrorDialog(Exception error) {
        showErrorDialog(getResources().getIdentifier(
                "droidfu_error_dialog_title", "string", getPackageName()),
                error);
    }

    public <T> Dialog newListDialog(List<T> elements,
            DialogClickListener<T> listener, boolean closeOnSelect) {
        return BetterActivityHelper.newListDialog(this, elements, listener,
                closeOnSelect);
    }
}
