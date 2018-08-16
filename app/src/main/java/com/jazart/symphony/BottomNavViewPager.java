package com.jazart.symphony;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * custom view to place fragments into
 * This helps keep all the fragmets loaded. The
 * custom view disables the typical swiping ability of the viewpager container
 */
public class BottomNavViewPager extends ViewPager {
    private final boolean isSwipeable;

    public BottomNavViewPager(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        isSwipeable = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        performClick();
        return isSwipeable;
    }

    @Override
    public boolean executeKeyEvent(@NonNull KeyEvent event) {
        return isSwipeable && super.executeKeyEvent(event);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return isSwipeable && super.onInterceptHoverEvent(event);
    }

    @Override
    public boolean performClick() {
        return isSwipeable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isSwipeable;
    }
}