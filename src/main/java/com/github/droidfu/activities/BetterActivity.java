package com.github.droidfu.activities;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;

import com.github.droidfu.dialogs.DialogClickListener;

public interface BetterActivity {

    public int getWindowFeatures();

    public void setProgressDialogTitleId(int progressDialogTitleId);

    public void setProgressDialogMsgId(int progressDialogMsgId);

    /**
     * @return true, if the activity is recovering from in interruption (i.e.
     *         {@link Activity#onRestoreInstanceState()} was called.
     */
    public boolean isRestoring();

    /**
     * @return true, if the activity is "soft-resuming", i.e.
     *         {@link Activity#onResume()} has been called without a prior call
     *         to {@link Activity#onCreate()}
     */
    public boolean isResuming();

    /**
     * @return true, if the activity is launching, i.e. is going through
     *         {@link Activity#onCreate()} but is not restoring.
     */
    public boolean isLaunching();

    /**
     * Retrieves the current intent that was used to create or resume this
     * activity. If the activity received a call to
     * {@link Activity#onNewIntent()} (e.g. because it was launched in singleTop
     * mode), then the Intent passed to that method is returned. Otherwise the
     * returned Intent is the intent returned by getIntent() (which is the
     * Intent which was used to initially launch this activity).
     * 
     * @return the current {@link Intent}
     */
    public Intent getCurrentIntent();

    public boolean isLandscapeMode();

    public boolean isPortraitMode();

    public AlertDialog newYesNoDialog(int titleResourceId, int messageResourceId,
            OnClickListener listener);

    public AlertDialog newInfoDialog(int titleResourceId, int messageResourceId);

    public AlertDialog newAlertDialog(int titleResourceId, int messageResourceId);

    public AlertDialog newErrorDialog(int titleResourceId, Exception error);

    public AlertDialog newErrorDialog(Exception error);

    public <T> Dialog newListDialog(final List<T> elements, final DialogClickListener<T> listener,
            boolean closeOnSelect);
}
