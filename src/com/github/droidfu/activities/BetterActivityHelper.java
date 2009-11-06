package com.github.droidfu.activities;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Window;

import com.github.droidfu.dialogs.DialogClickListener;
import com.github.droidfu.exception.ResourceMessageException;

class BetterActivityHelper {

    private static final String PROGRESS_DIALOG_TITLE_RESOURCE = "droidfu_progress_dialog_title";

    private static final String PROGRESS_DIALOG_MESSAGE_RESOURCE = "droidfu_progress_dialog_message";

    public static final String ERROR_DIALOG_TITLE_RESOURCE = "droidfu_error_dialog_title";

    public static final String ALERT_DIALOG_TITLE_RESOURCE = "droidfu_alert_dialog_title";

    public static final String INFO_DIALOG_TITLE_RESOURCE = "droidfu_info_dialog_title";

    public static int getWindowFeatures(Activity activity) {
        Window window = activity.getWindow();
        if (window == null) {
            return 0;
        }
        try {
            // Method m =
            // activity.getWindow().getClass().getMethod("getFeatures");
            Method[] m = window.getClass().getMethods();
            // m.setAccessible(true);
            // return (Integer) m.invoke(window);
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static ProgressDialog createProgressDialog(Activity activity,
            int progressDialogTitleId, int progressDialogMsgId) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        if (progressDialogTitleId > 0) {
            progressDialog.setTitle(progressDialogTitleId);
        } else {
            progressDialog.setTitle(activity.getResources().getIdentifier(
                    PROGRESS_DIALOG_TITLE_RESOURCE, "string",
                    activity.getPackageName()));
        }
        if (progressDialogMsgId > 0) {
            progressDialog.setMessage(activity.getString(progressDialogMsgId));
        } else {
            progressDialogMsgId = activity.getResources().getIdentifier(
                    PROGRESS_DIALOG_MESSAGE_RESOURCE, "string",
                    activity.getPackageName());
            progressDialog.setMessage(activity.getString(progressDialogMsgId));
        }
        progressDialog.setIndeterminate(true);
        // progressDialog.setInverseBackgroundForced(true);
        return progressDialog;
    }

    public static void showMessageDialog(final Activity activity,
            String dialogTitle, String screenMessage, int iconResourceId) {
        try {
            Log.e("ERROR", screenMessage);

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            builder.setPositiveButton("Okay", new OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (activity != null && !activity.isFinishing()) {
                        activity.setResult(Activity.RESULT_CANCELED);
                    }
                }
            });

            builder.setTitle(dialogTitle);
            builder.setMessage(screenMessage);
            builder.setIcon(iconResourceId);

            AlertDialog dialog = builder.create();
            dialog.show();

        } catch (Throwable e) {
            e.printStackTrace();
            // this can happen if the context the dialog was attached to has
            // become invalid. we just swallow this error and ignore it,
            // because there is no window anymore which could display the
            // message
        }
    }

    public static void showMessageDialog(Activity activity, String dialogTitle,
            Exception error) {
        error.printStackTrace();
        String screenMessage = "";
        if (error instanceof ResourceMessageException) {
            screenMessage = activity.getString(((ResourceMessageException) error).getClientMessageResourceId());
        } else {
            screenMessage = error.getLocalizedMessage();
        }
        showMessageDialog(activity, dialogTitle, screenMessage,
                android.R.drawable.ic_dialog_alert);
    }

    public static <T> Dialog newListDialog(Context context,
            final List<T> elements, final DialogClickListener<T> listener,
            final boolean closeOnSelect) {

        String[] entries = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            entries[i] = elements.get(i).toString();
        }

        Builder builder = new AlertDialog.Builder(context);
        builder.setSingleChoiceItems(entries, 0,
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if (closeOnSelect)
                            dialog.dismiss();
                        listener.onClick(which, elements.get(which));
                    }
                });

        return builder.create();
    }

}
