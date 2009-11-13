package com.github.droidfu.alerts;

import android.app.Activity;
import android.view.View;

public class Alert {

    public static void show(final Activity context, String message) {

        int id = context.getResources().getIdentifier("droidfu_alert", "id",
            context.getPackageName());
        final AlertView view = (AlertView) context.findViewById(id);

        // AnimationSet inAnim = view.loadInAnimation();
        // view.startAnimation(inAnim);

        view.setVisibility(View.VISIBLE);

        view.bringToFront();
        view.requestFocus();
    }

    public static void hideAfterTimeout() {
        // Timer timer = new Timer();
        // timer.schedule(new TimerTask() {
        //
        // @Override
        // public void run() {
        // view.setVisibility(View.GONE);
        // view.getParent().invalidateChildInParent(
        // new int[] { view.getLeft(), view.getTop() },
        // new Rect(0, 0, view.getRight(), view.getBottom()));
        // }
        // }, 2000);

    }
}
