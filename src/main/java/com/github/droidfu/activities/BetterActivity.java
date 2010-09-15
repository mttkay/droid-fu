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
     *         onRestoreInstanceState was called.
     */
    public boolean isRestoring();

    /**
     * @return true, if the activity is "soft-resuming", i.e. onResume has been
     *         called without a prior call to onCreate
     */
    public boolean isResuming();

    /**
     * @return true, if the activity is launching, i.e. is going through
     *         onCreate but is not restoring.
     */
    public boolean isLaunching();

    /**
     * Android doesn't distinguish between your Activity being paused by another
     * Activity of your own application, or by an Activity of an entirely
     * different application. This function only returns true, if your Activity
     * is being paused by an Activity of another app, thus hiding yours.
     * 
     * @return true, if the Activity is being paused because an Activity of
     *         another application received focus.
     */
    public boolean isApplicationBroughtToBackground();

    /**
     * Retrieves the current intent that was used to create or resume this
     * activity. If the activity received a call to onNewIntent (e.g. because it
     * was launched in singleTop mode), then the Intent passed to that method is
     * returned. Otherwise the returned Intent is the intent returned by
     * getIntent (which is the Intent which was used to initially launch this
     * activity).
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

    /**
     * <p>
     * Creates a new error dialog, with the option to either dismiss or -- if an
     * email application is installed -- report the given error. Clicking the
     * report button will cause the app to collect diagnostic information,
     * compile it to a single text body, and launch the email application with
     * the error report being preset as the email's body.
     * </p>
     * <p>
     * For this to work, you must define the following string resources in your
     * application:
     * <ul>
     * <li>droidfu_dialog_button_send_error_report - The label of the button
     * used to report the error</li>
     * <li>droidfu_error_report_email_address - The email address where the
     * report should be sent</li>
     * <li>droidfu_error_report_email_subject - The subject line used in the
     * email</li>
     * </ul>
     * </p>
     * 
     * @param titleResourceId
     *        the string resource that should be used as the dialog title
     * @param error
     *        the exception that should be displayed and/or reported
     * @return the dialog
     */
    public AlertDialog newErrorHandlerDialog(int titleResourceId, Exception error);

    /**
     * <p>
     * Creates a new error dialog, with the option to either dismiss or -- if an
     * email application is installed -- report the given error. Clicking the
     * report button will cause the app to collect diagnostic information,
     * compile it to a single text body, and launch the email application with
     * the error report being preset as the email's body.
     * </p>
     * <p>
     * For this to work, you must define the following string resources in your
     * application:
     * <ul>
     * <li>droidfu_error_dialog_title - The dialog title</li>
     * <li>droidfu_dialog_button_send_error_report - The label of the button
     * used to report the error</li>
     * <li>droidfu_error_report_email_address - The email address where the
     * report should be sent</li>
     * <li>droidfu_error_report_email_subject - The subject line used in the
     * email</li>
     * </ul>
     * </p>
     * 
     * @param error
     *        the exception that should be displayed and/or reported
     * @return the dialog
     */
    public AlertDialog newErrorHandlerDialog(Exception error);

    /**
     * Creates a new list style dialog from a list of objects. The toString()
     * method of any such object will be used to generate the list item's label.
     * 
     * @param <T>
     *        the type of the list items
     * @param dialogTitle
     *        the title or null to disable the title
     * @param listItems
     *        the list items
     * @param listener
     *        the listener used for processing list item clicks
     * @param closeOnSelect
     *        if true, the dialog will close when an item has been clicked
     * @return the dialog
     */
    public <T> Dialog newListDialog(String title, final List<T> listItems,
            final DialogClickListener<T> listener, boolean closeOnSelect);
}
