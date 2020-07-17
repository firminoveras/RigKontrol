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
    private boolean isOn;
    private OnKGateListener onKGateListener;

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
        Knob mKnob = findViewById(R.id.Gate_Knob);
        Button mButton = findViewById(R.id.Gate_Button);
        isOn = false;
        mDrawableGateOn = getResources().getDrawable(R.drawable.bg_gate_button_on, null);
        mDrawableGateOff = getResources().getDrawable(R.drawable.bg_gate_button_off, null);
        onKGateListener = new OnKGateListener() {
            @Override
            public void onKGateEnabledListener(boolean isOn, int controllerNumber) {

            }

            @Override
            public void onKGateValueChangeListener(int progress, int controllerNumber) {

            }
        };
        mButton.setOnClickListener(l -> {
            isOn = !isOn;
            onKGateListener.onKGateEnabledListener(isOn, getResources().getInteger(R.integer.cc_gate_on));
            l.setBackground(isOn ? mDrawableGateOn : mDrawableGateOff);
            ((Button) l).setTextColor(getResources().getColor(isOn ? R.color.light_foreground : R.color.dark_foreground, null));
        });
        mKnob.setOnStateChanged(state -> onKGateListener.onKGateValueChangeListener(state, getResources().getInteger(R.integer.cc_gate_knob)));
    }

    public void setOnKGateListener(OnKGateListener onKGateListener) {
        this.onKGateListener = onKGateListener;
    }

    public interface OnKGateListener {

        void onKGateEnabledListener(boolean isOn, int controllerNumber);

        void onKGateValueChangeListener(int progress, int controllerNumber);

    }
}
