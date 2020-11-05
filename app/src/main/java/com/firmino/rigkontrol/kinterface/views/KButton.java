package com.firmino.rigkontrol.kinterface.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;

import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.R;


public class KButton extends androidx.appcompat.widget.AppCompatButton {

    public static final int COLOR_SCHEME_DEFAULT = 0;
    public static final int COLOR_SCHEME_ORANGE = 1;
    public static final int COLOR_SCHEME_LIGTH = 2;
    public static final int COLOR_SCHEME_NO_BACKGROUND = 3;

    public final static int ALIGN_NORMAL = 0;
    public final static int ALIGN_RIGHT = 1;
    public final static int ALIGN_LEFT = 2;
    public final static int ALIGN_NONE = 3;

    private Drawable mDrawable;
    private boolean isOn;
    private boolean isToggled;
    private int mBackgroundTintOn, mBackgroundTintOff, mTextColorOn, mTextColorOff;
    private OnKButtonListener onKButtonListener = isOn -> {};

    public KButton(Context context) {
        super(context);
        init();
        isToggled = false;
        setColorScheme(COLOR_SCHEME_DEFAULT);
        setAlign(ALIGN_NORMAL);
    }

    public KButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray ta = getResources().obtainAttributes(attrs, R.styleable.KButton);
        isToggled = ta.getBoolean(R.styleable.KButton_k_is_toggled, false);
        setColorScheme(ta.getInt(R.styleable.KButton_k_color_scheme, COLOR_SCHEME_DEFAULT));
        setAlign(ta.getInt(R.styleable.KButton_k_align, ALIGN_NORMAL));
        ta.recycle();
    }

    public void setColorScheme(int colorScheme) {
        switch (colorScheme) {
            case COLOR_SCHEME_ORANGE:
                mBackgroundTintOn = ResourcesCompat.getColor(getResources(), R.color.foreground, null);
                mBackgroundTintOff = ResourcesCompat.getColor(getResources(), R.color.background_medium, null);
                mTextColorOn = ResourcesCompat.getColor(getResources(), R.color.background, null);
                mTextColorOff = ResourcesCompat.getColor(getResources(), R.color.background, null);
                break;
            case COLOR_SCHEME_LIGTH:
                mBackgroundTintOn = ResourcesCompat.getColor(getResources(), R.color.background_bt_on, null);
                mBackgroundTintOff = ResourcesCompat.getColor(getResources(), R.color.background_medium, null);
                mTextColorOn = ResourcesCompat.getColor(getResources(), R.color.background_dialog, null);
                mTextColorOff = ResourcesCompat.getColor(getResources(), R.color.background_dialog, null);
                break;
            case COLOR_SCHEME_NO_BACKGROUND:
                mBackgroundTintOn = ResourcesCompat.getColor(getResources(), android.R.color.transparent, null);
                mBackgroundTintOff = ResourcesCompat.getColor(getResources(), android.R.color.transparent, null);
                mTextColorOn = ResourcesCompat.getColor(getResources(), R.color.foreground, null);
                mTextColorOff = ResourcesCompat.getColor(getResources(), R.color.background_light, null);
                break;
            default:
                mBackgroundTintOn = ResourcesCompat.getColor(getResources(), R.color.background_bt_on, null);
                mBackgroundTintOff = ResourcesCompat.getColor(getResources(), R.color.background_bt_off, null);
                mTextColorOn = ResourcesCompat.getColor(getResources(), R.color.background_bt_off, null);
                mTextColorOff = ResourcesCompat.getColor(getResources(), R.color.background_bt_on, null);
                break;
        }
        refresh();
    }

    public void setAlign(int align) {
        switch (align) {
            case ALIGN_LEFT:
                mDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_kbutton_left, null);
                break;
            case ALIGN_RIGHT:
                mDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_kbutton_right, null);
                break;
            case ALIGN_NONE:
                mDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_kbutton_center, null);
                break;
            default:
                mDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_kbutton, null);
                break;
        }
        refresh();
    }

    private void init() {
        setAlign(ALIGN_NORMAL);
        setColorScheme(COLOR_SCHEME_DEFAULT);
        setBackgroundDrawable(mDrawable);
        isOn = false;
        setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        setGravity(Gravity.CENTER);
        setTextSize(12);
        setPadding(0,0,0,0);
    }

    @Override
    public boolean performClick() {
        refresh();
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isOn = !isOn;
            refresh();
            performClick();
            onKButtonListener.onKButtonListener(isOn);
        } else if (!isToggled) {
            isOn = false;
            refresh();
            onKButtonListener.onKButtonListener(isOn);
        }
        return true;
    }

    @Override
    public boolean isPressed() {
        return isOn;
    }

    private void refresh() {
        mDrawable.setTint(isOn ? mBackgroundTintOn : mBackgroundTintOff);
        setBackgroundDrawable(mDrawable);
        setTextColor(ColorStateList.valueOf(isOn ? mTextColorOn : mTextColorOff));
    }

    public void setOnKButtonListener(OnKButtonListener onKButtonListener) {
        this.onKButtonListener = onKButtonListener;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean aBoolean) {
        isOn = aBoolean;
        refresh();
    }

    public void setToggle(boolean isToggle) {
        isToggled = isToggle;
    }

    public interface OnKButtonListener {
        void onKButtonListener(boolean isOn);
    }

}
