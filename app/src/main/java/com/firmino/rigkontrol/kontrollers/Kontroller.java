package com.firmino.rigkontrol.kontrollers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.firmino.rigkontrol.R;

public class Kontroller extends LinearLayout {

    private KSlider mSlider;
    private KPedal mPedal;
    private KButton[] mButton;
    private KButton mButtonPedal;
    private Drawable mLedOn, mLedOff;

    public Kontroller(Context context) {
        super(context);
        init();
    }

    public Kontroller(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_krig_controller, this);
        mSlider = findViewById(R.id.Main_Slider);
        mPedal = findViewById(R.id.Main_Pedal);
        mButtonPedal = findViewById(R.id.Main_BT_Pedal_Down);
        mButton = new KButton[]{
                findViewById(R.id.Main_BT1),
                findViewById(R.id.Main_BT2),
                findViewById(R.id.Main_BT3),
                findViewById(R.id.Main_BT4),
                findViewById(R.id.Main_BT5),
                findViewById(R.id.Main_BT6),
                findViewById(R.id.Main_BT7),
                findViewById(R.id.Main_BT8),
        };
        mLedOn = getResources().getDrawable(R.drawable.ic_led_on, null);
        mLedOff = getResources().getDrawable(R.drawable.ic_led_off, null);
        mPedal.setOnPedalValueChangeListener((pedal, value) -> {
            mSlider.setProgress(value);
            if (value > 120 && !mButtonPedal.isOn()) {
                mButtonPedal.kontrollerButtonDown();
            } else if (value <= 120 && mButtonPedal.isOn()) {
                mButtonPedal.kontrollerButtonUp();
            }
        });
        mButtonPedal.setPedalDownIconVisible(true);
    }

    public void setExpandedMode(boolean extended){
        for (KButton b : mButton) b.setDescriptionVisible(!extended);
        mButtonPedal.setDescriptionVisible(!extended);
        mSlider.setExpanded(!extended);
    }

    public KButton getButtonAt(int index) {
        return mButton[index];
    }

    public KButton[] getButtons() {
        return mButton;
    }

    public KButton getButtonPedal() {
        return mButtonPedal;
    }

    public KPedal getPedal() {
        return mPedal;
    }

    public KSlider getSlider() {
        return mSlider;
    }

    public void setPedalVisible(boolean visible) {
        mPedal.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setConnectionLedOn(boolean isOn){
        ((ImageView)findViewById(R.id.Kontroller_Led)).setImageDrawable(isOn? mLedOn : mLedOff);
        ((TextView)findViewById(R.id.Kontroller_Led_Status)).setText(isOn ? "CONNECTED" : "DISCONNECTED");
    }
}
