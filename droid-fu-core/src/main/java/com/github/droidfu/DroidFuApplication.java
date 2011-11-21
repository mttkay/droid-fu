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

package com.github.droidfu;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;

public class DroidFuApplication extends Application {

    private HashMap<String, WeakReference<Context>> contextObjects = new HashMap<String, WeakReference<Context>>();

    public synchronized Context getActiveContext(String className) {
        WeakReference<Context> ref = contextObjects.get(className);
        if (ref == null) {
            return null;
        }

        final Context c = ref.get();
        if (c == null) // If the WeakReference is no longer valid, ensure it is removed.
            contextObjects.remove(className);

        return c;
    }

    public synchronized void setActiveContext(String className, Context context) {
        WeakReference<Context> ref = new WeakReference<Context>(context);
        this.contextObjects.put(className, ref);
    }

    public synchronized void resetActiveContext(String className) {
        contextObjects.remove(className);
    }

    /**
     * <p>
     * Invoked if the application is about to close. Application close is being defined as the
     * transition of the last running Activity of the current application to the Android home screen
     * using the BACK button. You can leverage this method to perform cleanup logic such as freeing
     * resources whenever your user "exits" your app using the back button.
     * </p>
     * <p>
     * Note that you must not rely on this callback as a general purpose "exit" handler, since
     * Android does not give any guarantees as to when exactly the process hosting an application is
     * being terminated. In other words, your application can be terminated at any point in time, in
     * which case this method will NOT be invoked.
     * </p>
     */
    public void onClose() {
        // NO-OP by default
    }
}
