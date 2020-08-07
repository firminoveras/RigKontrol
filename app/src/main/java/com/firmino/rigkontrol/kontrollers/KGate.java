package com.firmino.rigkontrol.kontrollers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firmino.rigkontrol.R;

import it.beppi.knoblibrary.Knob;

public class KGate extends FrameLayout {

    private Drawable mDrawableGateOn, mDrawableGateOff;
    private boolean isOn = false;
    private Knob mKnob;
    private Button mButton;

    private OnKGateEnabledListener onKGateEnabledListener = (isOn, controllerNumber) -> {
    };
    private OnKGateValueChangeListener onKGateValueChangeListener = (progress, controllerNumber) -> {
    };

    public KGate(@NonNull Context context) {
        super(context);
        init();
    }

    public KGate(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_kgate, this);
        mKnob = findViewById(R.id.Gate_Knob);
        mButton = findViewById(R.id.Gate_Button);
        mDrawableGateOn = getResources().getDrawable(R.drawable.bg_gate_button_on, null);
        mDrawableGateOff = getResources().getDrawable(R.drawable.bg_gate_button_off, null);
        mButton.setOnClickListener(l -> {
            isOn = !isOn;
            onKGateEnabledListener.onKGateEnabledListener(isOn, getResources().getInteger(R.integer.cc_gate_on));
            updateGateButtonDrawable();
        });
        mKnob.setOnStateChanged(state -> onKGateValueChangeListener.onKGateValueChangeListener(state, getResources().getInteger(R.integer.cc_gate_knob)));
    }

    private void updateGateButtonDrawable() {
        mButton.setBackground(isOn ? mDrawableGateOn : mDrawableGateOff);
        mButton.setTextColor(getResources().getColor(isOn ? R.color.light_foreground : R.color.dark_foreground, null));
    }

    public void setValue(int value) {
        if (mKnob != null) mKnob.setState(value);
    }

    public int getValue() {
        return (mKnob != null) ? mKnob.getState() : 0;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        this.isOn = on;
        updateGateButtonDrawable();
    }

    public void setOnKGateEnabledListener(OnKGateEnabledListener onKGateEnabledListener) {
        this.onKGateEnabledListener = onKGateEnabledListener;
    }

    public void setOnKGateValueChangeListener(OnKGateValueChangeListener onKGateValueChangeListener) {
        this.onKGateValueChangeListener = onKGateValueChangeListener;
    }

    public interface OnKGateEnabledListener {
        void onKGateEnabledListener(boolean isOn, int controllerNumber);
    }

    public interface OnKGateValueChangeListener {
        void onKGateValueChangeListener(int progress, int controllerNumber);
    }
}
