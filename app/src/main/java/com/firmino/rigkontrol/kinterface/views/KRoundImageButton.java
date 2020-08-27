package com.firmino.rigkontrol.kinterface.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.R;


public class KRoundImageButton extends androidx.appcompat.widget.AppCompatImageButton {

    private boolean isToggle = false;
    private boolean isOn = false;
    private OnKRoundButtonClickedListener onClicked = (isOn) -> {};

    public KRoundImageButton(@NonNull Context context) {
        super(context);
    }

    public KRoundImageButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KRoundImageButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        isOn = !isOn;
        onClicked.onKRoundButtonClickedListener(isOn);
        setImageTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), isOn ? R.color.light_foreground : android.R.color.white, null)));
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_rack_button_pressed, null));
            performClick();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_rack_button, null));
            if (!isToggle) performClick();
        }
        return true;
    }

    public void setToggle(boolean toggle) {
        isToggle = toggle;
    }

    public void setOnRackButtonClicked(OnKRoundButtonClickedListener onClicked) {
        this.onClicked = onClicked;
    }

    public boolean isOn() {
        return isOn;
    }

    public interface OnKRoundButtonClickedListener {
        void onKRoundButtonClickedListener(boolean isOn);
    }
}
