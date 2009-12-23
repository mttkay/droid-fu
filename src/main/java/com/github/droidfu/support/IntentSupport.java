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

package com.github.droidfu.support;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

public class IntentSupport {

    public static final String MIME_TYPE_EMAIL = "message/rfc822";
    public static final String MIME_TYPE_TEXT = "text/*";

    /**
     * Checks whether there are applications installed which are able to handle
     * the given action/data.
     * 
     * @param context
     *        the current context
     * @param action
     *        the action to check
     * @param uri
     *        that data URI to check (may be null)
     * @param mimeType
     *        the MIME type of the content (may be null)
     * @return true if there are apps which will respond to this action/data
     */
    public static boolean isIntentAvailable(Context context, String action, Uri uri, String mimeType) {
        final Intent intent = (uri != null) ? new Intent(action, uri) : new Intent(action);
        if (mimeType != null) {
            intent.setType(mimeType);
        }
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
            PackageManager.MATCH_DEFAULT_ONLY);
        return !list.isEmpty();
    }

    /**
     * Checks whether there are applications installed which are able to handle
     * the given action/type.
     * 
     * @param context
     *        the current context
     * @param action
     *        the action to check
     * @param mimeType
     *        the MIME type of the content (may be null)
     * @return true if there are apps which will respond to this action/type
     */
    public static boolean isIntentAvailable(Context context, String action, String mimeType) {
        final Intent intent = new Intent(action);
        if (mimeType != null) {
            intent.setType(mimeType);
        }
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
            PackageManager.MATCH_DEFAULT_ONLY);
        return !list.isEmpty();
    }

    /**
     * Checks whether there are applications installed which are able to handle
     * the given intent.
     * 
     * @param context
     *        the current context
     * @param intent
     *        the intent to check
     * @return true if there are apps which will respond to this intent
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
            PackageManager.MATCH_DEFAULT_ONLY);
        return !list.isEmpty();
    }

    public static Intent newEmailIntent(Context context, String address, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.setType(MIME_TYPE_EMAIL);

        return intent;
    }

    public static Intent newShareIntent(Context context, String subject, String message,
            String chooserDialogTitle) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.setType(MIME_TYPE_TEXT);
        return Intent.createChooser(shareIntent, chooserDialogTitle);
    }

    public static Intent newMapsIntent(String address, String placeTitle) {
        StringBuilder sb = new StringBuilder();
        sb.append("geo:0,0?q=");

        String addressEncoded = Uri.encode(address);
        sb.append(addressEncoded);
        // pass text for the info window
        String titleEncoded = Uri.encode("(" + placeTitle + ")");
        sb.append(titleEncoded);
        // set locale; probably not required for the maps app?
        sb.append("&hl=" + Locale.getDefault().getLanguage());

        return new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString()));
    }
}
