package com.goldsprite.geometryDash;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class NoScrollScrollView extends ScrollView {
    public NoScrollScrollView(Context context) {
        super(context);
    }

    public NoScrollScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isFocusable() ? super.onTouchEvent(ev) : false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 禁止父视图拦截触摸事件
        return isFocusable() ? super.onInterceptTouchEvent(ev) : false;
    }
}
