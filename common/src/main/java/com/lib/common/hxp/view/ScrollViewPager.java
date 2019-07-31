package com.lib.common.hxp.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by thinkpad on 2016/6/8.
 */
public class ScrollViewPager extends ViewPager {

    private boolean isScroll = true;

    public ScrollViewPager(Context context) {
        super(context);
    }

    public ScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isScroll && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isScroll && super.onInterceptTouchEvent(event);
    }

    public void setScroll(boolean b) {
        this.isScroll = b;
    }
}
