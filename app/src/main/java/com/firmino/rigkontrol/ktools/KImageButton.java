package com.firmino.rigkontrol.ktools;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.firmino.rigkontrol.R;

public class KImageButton extends androidx.appcompat.widget.AppCompatImageView {

    private final Context mContext;
    private Drawable mDrawableUp, mDrawableDown;
    private boolean isPressed;
    private final boolean isToggled;

    public KImageButton(Context context) {
        super(context);
        mContext = context;
        isToggled = false;
        init();
    }

    public KImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.KImageButton);
        isToggled = ta.getBoolean(R.styleable.KStateButton_k_statebutton_is_on, false);
        ta.recycle();
        init();
    }

    private void init() {
        mDrawableUp = mContext.getDrawable(R.drawable.bg_button);
        mDrawableDown = mContext.getDrawable(R.drawable.bg_button_pressed);
        setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.white, null)));
        setBackgroundDrawable(mDrawableUp);
        isPressed = false;
    }

    @Override
    public boolean performClick() {
        refresh();
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isPressed = !isPressed;
            refresh();
            performClick();
        } else {
            if (!isToggled) {
                isPressed = false;
                refresh();
            }
        }
        return true;
    }

    @Override
    public boolean isPressed() {
        return isPressed;
    }

    private void refresh() {
        setBackgroundDrawable(isPressed ? mDrawableDown : mDrawableUp);
        setImageTintList(ColorStateList.valueOf(getResources().getColor(isPressed ? R.color.bg_dark_gray : R.color.white, null)));
    }
}
