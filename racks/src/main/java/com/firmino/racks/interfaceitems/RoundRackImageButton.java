package com.firmino.racks.interfaceitems;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.racks.R;

public class RoundRackImageButton extends androidx.appcompat.widget.AppCompatImageButton {

    private boolean isToggle = false;
    private boolean isOn = false;
    private OnRackButtonClicked onClicked = (isOn) -> {};

    public RoundRackImageButton(@NonNull Context context) {
        super(context);
    }

    public RoundRackImageButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundRackImageButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        isOn = !isOn;
        onClicked.onRackButtonClickedListener(isOn);
        setImageTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), isOn ? R.color.foreground : android.R.color.white, null)));
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

    public void setOnRackButtonClicked(OnRackButtonClicked onClicked) {
        this.onClicked = onClicked;
    }

    public boolean isOn() {
        return isOn;
    }
}
