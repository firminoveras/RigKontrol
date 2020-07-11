package com.firmino.rigkontrol.kontrollers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.firmino.rigkontrol.R;

public class Kontroller extends LinearLayout {

    private KSlider mSlider;
    private KPedal mPedal;
    private KButton[] mButton;
    private KButton mButtonPedal;

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
}
