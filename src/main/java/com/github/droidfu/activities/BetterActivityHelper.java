package com.github.droidfu.activities;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.Window;

import com.github.droidfu.dialogs.DialogClickListener;
import com.github.droidfu.exception.ResourceMessageException;
import com.github.droidfu.support.DiagnosticSupport;
import com.github.droidfu.support.IntentSupport;

public class BetterActivityHelper {

    private static final String PROGRESS_DIALOG_TITLE_RESOURCE = "droidfu_progress_dialog_title";

    private static final String PROGRESS_DIALOG_MESSAGE_RESOURCE = "droidfu_progress_dialog_message";

    public static final String ERROR_DIALOG_TITLE_RESOURCE = "droidfu_error_dialog_title";

    // FIXME: this method currently doesn't work as advertised
    public static int getWindowFeatures(Activity activity) {
        Window window = activity.getWindow();
        if (window == null) {
            return 0;
        }
        try {
            // Method m =
            // activity.getWindow().getClass().getMethod("getFeatures");
            // Method[] m = window.getClass().getMethods();
            // m.setAccessible(true);
            // return (Integer) m.invoke(window);
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static ProgressDialog createProgressDialog(final Activity activity,
            int progressDialogTitleId, int progressDialogMsgId) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        if (progressDialogTitleId > 0) {
            progressDialog.setTitle(progressDialogTitleId);
        } else {
            progressDialog.setTitle(activity.getResources().getIdentifier(
                PROGRESS_DIALOG_TITLE_RESOURCE, "string", activity.getPackageName()));
        }
        if (progressDialogMsgId > 0) {
            progressDialog.setMessage(activity.getString(progressDialogMsgId));
        } else {
            progressDialogMsgId = activity.getResources().getIdentifier(
                PROGRESS_DIALOG_MESSAGE_RESOURCE, "string", activity.getPackageName());
            progressDialog.setMessage(activity.getString(progressDialogMsgId));
        }
        progressDialog.setIndeterminate(true);
        progressDialog.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                activity.onKeyDown(keyCode, event);
                return false;
            }
        });
        // progressDialog.setInverseBackgroundForced(true);
        return progressDialog;
    }

    public static AlertDialog newYesNoDialog(final Activity activity, String dialogTitle,
            String screenMessage, int iconResourceId, OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.yes, listener);
        builder.setNegativeButton(android.R.string.no, listener);

        builder.setTitle(dialogTitle);
        builder.setMessage(screenMessage);
        builder.setIcon(iconResourceId);

        return builder.create();
    }

    public static AlertDialog newMessageDialog(final Activity activity, String dialogTitle,
            String screenMessage, int iconResourceId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setPositiveButton("Okay", new OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setTitle(dialogTitle);
        builder.setMessage(screenMessage);
        builder.setIcon(iconResourceId);

        return builder.create();
    }

    public static AlertDialog newErrorHandlerDialog(Activity activity, String dialogTitle,
            Exception error) {
        String screenMessage = "";
        if (error instanceof ResourceMessageException) {
            screenMessage = activity.getString(((ResourceMessageException) error).getClientMessageResourceId());
        } else {
            screenMessage = error.getLocalizedMessage();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(dialogTitle);
        builder.setMessage(screenMessage);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setCancelable(false);
        builder.setPositiveButton(activity.getString(android.R.string.ok), new OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        if (IntentSupport.isIntentAvailable(activity, Intent.ACTION_SEND,
            IntentSupport.MIME_TYPE_EMAIL)) {
            int buttonId = activity.getResources().getIdentifier(
                "droidfu_dialog_button_send_error_report", "string", activity.getPackageName());
            String buttonText = activity.getString(buttonId);
            int bugEmailAddressId = activity.getResources().getIdentifier(
                "droidfu_error_report_email_address", "string", activity.getPackageName());
            String bugReportEmailAddress = activity.getString(bugEmailAddressId);
            int bugEmailSubjectId = activity.getResources().getIdentifier(
                "droidfu_error_report_email_subject", "string", activity.getPackageName());
            String bugReportEmailSubject = activity.getString(bugEmailSubjectId);
            final String diagnosis = DiagnosticSupport.createDiagnosis(activity, error);
            final Intent intent = IntentSupport.newEmailIntent(activity, bugReportEmailAddress,
                bugReportEmailSubject, diagnosis);
            final Activity a = activity;
            builder.setNegativeButton(buttonText, new OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    a.startActivity(intent);
                }
            });
        }

        return builder.create();
    }

    public static <T> Dialog newListDialog(Context context, final List<T> elements,
            final DialogClickListener<T> listener, final boolean closeOnSelect) {

        String[] entries = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            entries[i] = elements.get(i).toString();
        }

        Builder builder = new AlertDialog.Builder(context);
        builder.setSingleChoiceItems(entries, 0, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (closeOnSelect)
                    dialog.dismiss();
                listener.onClick(which, elements.get(which));
            }
        });

        return builder.create();
    }

    public static boolean isApplicationBroughtToBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        if (!taskInfo.isEmpty()) {
            ComponentName topActivity = taskInfo.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }
}
