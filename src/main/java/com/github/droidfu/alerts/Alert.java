package com.github.droidfu.alerts;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

public class Alert {

    public static void show(final Activity context, String message) {

        Animation animation = AnimationUtils.makeInAnimation(context, true);
        animation.setDuration(1000);

        int id = context.getResources().getIdentifier("droidfu_alert", "id",
            context.getPackageName());
        final TextView view = (TextView) context.findViewById(id);
        view.setText(message);

        Animation a = new TranslateAnimation(0, 0, -view.getHeight(), view.getHeight());

        a = new AlphaAnimation(0, 1);
        a.setZAdjustment(Animation.ZORDER_TOP);

        a.setDuration(1000);

        a = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        a.setDuration(1000);

        view.startAnimation(a);

        view.setVisibility(View.VISIBLE);
        view.bringToFront();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                view.setVisibility(View.GONE);
                view.getParent().invalidateChildInParent(
                    new int[] { view.getLeft(), view.getTop() },
                    new Rect(0, 0, view.getRight(), view.getBottom()));
            }
        }, 2000);

        view.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Animation a = new TranslateAnimation(0, 0, 0, -v.getBottom());
                a.setDuration(1000);
                v.startAnimation(a);
                v.setVisibility(View.GONE);
                Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
            }
        });

        view.requestFocus();
    }
}
