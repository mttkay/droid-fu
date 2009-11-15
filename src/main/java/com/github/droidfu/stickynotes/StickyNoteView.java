package com.github.droidfu.stickynotes;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.github.droidfu.DroidFu;

public class StickyNoteView extends Button {

    public static final int STICK_LEFT = 1;
    public static final int STICK_TOP = 2;
    public static final int STICK_RIGHT = 4;
    public static final int STICK_BOTTOM = 8;

    private static HashMap<String, Integer> stickModeMap;

    static {
        stickModeMap = new HashMap<String, Integer>(4);
        stickModeMap.put("left", STICK_LEFT);
        stickModeMap.put("top", STICK_TOP);
        stickModeMap.put("right", STICK_RIGHT);
        stickModeMap.put("bottom", STICK_BOTTOM);
    }

    private View targetView;

    private int stickMode = 0;

    public StickyNoteView(Context context, AttributeSet attribs) {
        super(context, attribs);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setClickable(true);

        setVisibility(View.GONE);

        int resId = attribs.getAttributeResourceValue(DroidFu.XMLNS, "targetView", -1);
        targetView = ((Activity) context).findViewById(resId);
        if (targetView == null) {
            throw new IllegalStateException("No target view supplied");
        }

        String stickAttr = attribs.getAttributeValue(DroidFu.XMLNS, "stickTo");
        if (stickAttr == null) {
            stickMode = STICK_TOP;
        } else {
            String[] values = stickAttr.split("\\|");
            for (String val : values) {
                stickMode |= stickModeMap.get(val);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        float offsetLeft = 0, offsetTop = 0;

        int[] targetViewXY = new int[2];
        targetView.getLocationInWindow(targetViewXY);

        // perform Y offset calculations
        if (stickMode == (stickMode | STICK_TOP)) {
            offsetTop = targetViewXY[1] - getMeasuredHeight() - targetView.getMeasuredHeight();
        } else if (stickMode == (stickMode | STICK_BOTTOM)) {
            offsetTop = targetViewXY[1];
        }

        if (stickMode == (stickMode | STICK_LEFT)) {
            offsetLeft = targetViewXY[0] - getMeasuredWidth();
        } else if (stickMode == (stickMode | STICK_RIGHT)) {
            offsetLeft = targetViewXY[0] + targetView.getMeasuredWidth();
        } else {
            // center above/below target view
            float targetCenter = targetView.getMeasuredWidth() / 2.0f;
            float myCenter = getMeasuredWidth() / 2.0f;
            offsetLeft = targetViewXY[0] + (targetCenter - myCenter);
        }

        offsetTopAndBottom((int) offsetTop);
        offsetLeftAndRight((int) offsetLeft);
    }

    public AnimationSet loadInAnimation() {
        AnimationSet inAnimation = new AnimationSet(true);
        inAnimation.setFillAfter(true);

        Animation alpha = new AlphaAnimation(0, 1.0f);
        alpha.setDuration(500);
        inAnimation.addAnimation(alpha);

        return inAnimation;
    }

    public AnimationSet loadOutAnimation() {
        AnimationSet outAnimation = (AnimationSet) AnimationUtils.makeOutAnimation(getContext(),
            true);
        return outAnimation;
    }
}
