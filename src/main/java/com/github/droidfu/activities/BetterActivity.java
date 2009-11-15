package com.github.droidfu.activities;

import java.util.List;

import android.app.Dialog;

import com.github.droidfu.dialogs.DialogClickListener;

public interface BetterActivity {

    public int getWindowFeatures();

    public void setProgressDialogTitleId(int progressDialogTitleId);

    public void setProgressDialogMsgId(int progressDialogMsgId);

    public boolean isRestoring();

    public boolean isResuming();

    public boolean isLaunching();

    public void showInfoDialog(int titleResourceId, int messageResourceId);

    public void showInfoDialog(int messageResourceId);

    public void showAlertDialog(int titleResourceId, int messageResourceId);

    public void showAlertDialog(int messageResourceId);

    public void showErrorDialog(int titleResourceId, Exception error);

    public void showErrorDialog(Exception error);

    public <T> Dialog newListDialog(final List<T> elements,
            final DialogClickListener<T> listener, boolean closeOnSelect);
}
