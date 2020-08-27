package com.firmino.rigkontrol.kinterface.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.R;

public class KImageButton extends androidx.appcompat.widget.AppCompatImageView {

    private Drawable mDrawableUp, mDrawableDown;
    private boolean isPressed;
    private final boolean isToggled;

    public final static int ALIGN_NORMAL = 0;
    public final static int ALIGN_RIGHT = 1;
    public final static int ALIGN_LEFT = 2;
    public final static int ALIGN_NONE = 3;

    public KImageButton(Context context) {
        super(context);
        isToggled = false;
        init();
    }

    public KImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.KImageButton);
        isToggled = ta.getBoolean(R.styleable.KImageButton_k_is_toggled, false);
        setAlign(ta.getInt(R.styleable.KImageButton_k_align, ALIGN_NORMAL));
        ta.recycle();
    }

    public void setAlign(int align) {
        switch (align) {
            case ALIGN_LEFT:
                mDrawableUp = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_left_borderless, null);
                mDrawableDown = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_left_borderless_pressed, null);
                break;
            case ALIGN_RIGHT:
                mDrawableUp = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_right_borderless, null);
                mDrawableDown = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_right_borderless_pressed, null);
                break;
            case ALIGN_NONE:
                mDrawableUp = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_center_borderless, null);
                mDrawableDown = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_center_borderless_pressed, null);
                break;
            default:
                mDrawableUp = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button, null);
                mDrawableDown = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_pressed, null);
                break;
        }
        refresh();
    }

    private void init() {
        setAlign(ALIGN_NORMAL);
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
