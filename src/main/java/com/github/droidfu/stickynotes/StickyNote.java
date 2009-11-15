package com.github.droidfu.stickynotes;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;

public class StickyNote {

    public static final String VIEW_ID = "droidfu_sticky";

    public static void show(Activity context) {
        show(context, null, null, null);
    }

    public static void show(Activity context, String message) {
        show(context, message, null, null);
    }

    public static void show(Activity context, String message, Animation inAnimation,
            Animation outAnimation) {
        int id = context.getResources().getIdentifier(VIEW_ID, "id", context.getPackageName());
        StickyNoteView view = (StickyNoteView) context.findViewById(id);
        if (view == null) {
            throw new IllegalStateException(
                    "Sticky note view not found. Did you declare a view with id '" + VIEW_ID
                            + "' in your layout?");
        }

        if (message != null) {
            view.setText(message);
        }

        if (inAnimation == null) {
            inAnimation = view.loadInAnimation();
        }
        view.startAnimation(inAnimation);

        if (outAnimation == null) {
            outAnimation = view.loadOutAnimation();
        }

        final Animation finalOutAnim = outAnimation;
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(finalOutAnim);
                v.setVisibility(View.GONE);
            }
        });

        view.setVisibility(View.VISIBLE);
        view.bringToFront();
    }
}
