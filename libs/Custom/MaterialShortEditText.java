package com.example.administrator.myverifycode.Custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zzwwws on 2015/9/9.
 */
public class MaterialShortEditText extends MaterialEditText {

    private TouchListener listener;

    public MaterialShortEditText(Context context) {
        super(context);
    }

    public MaterialShortEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MaterialShortEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTouchListener(TouchListener listener) {
        this.listener = listener;
    }

    public interface TouchListener {
        boolean dispatchEvent(MotionEvent event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (listener != null) return listener.dispatchEvent(event);
        return false;
    }
}

