package com.github.droidfu.activities;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;

import com.github.droidfu.dialogs.DialogClickListener;

public interface BetterActivity {

    public int getWindowFeatures();

    public void setProgressDialogTitleId(int progressDialogTitleId);

    public void setProgressDialogMsgId(int progressDialogMsgId);

    public boolean isRestoring();

    public boolean isResuming();

    public boolean isLaunching();

    public AlertDialog newYesNoDialog(int titleResourceId, int messageResourceId,
            OnClickListener listener);

    public AlertDialog newInfoDialog(int titleResourceId, int messageResourceId);

    public AlertDialog newAlertDialog(int titleResourceId, int messageResourceId);

    public AlertDialog newErrorDialog(int titleResourceId, Exception error);

    public AlertDialog newErrorDialog(Exception error);

    public <T> Dialog newListDialog(final List<T> elements, final DialogClickListener<T> listener,
            boolean closeOnSelect);
}
