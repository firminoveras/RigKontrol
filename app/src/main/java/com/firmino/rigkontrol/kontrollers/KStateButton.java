package com.firmino.rigkontrol.kontrollers;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.firmino.rigkontrol.R;

public class KStateButton extends LinearLayout {

    private Button mButtonOff, mButtonOn;
    private boolean isOn;
    private OnKStateButtonChangeListener onKStateButtonChangeListener;

    public KStateButton(Context context) {
        super(context);
        init();
    }

    public KStateButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KStateButton, 0, 0);
        mButtonOn.setText(ta.getString(R.styleable.KStateButton_k_statebutton_text_on));
        mButtonOff.setText(ta.getString(R.styleable.KStateButton_k_statebutton_text_off));
        isOn = ta.getBoolean(R.styleable.KStateButton_k_statebutton_is_on, false);
        ta.recycle();
        refresh();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_kstatebutton, this);
        mButtonOff = findViewById(R.id.KStateButton_Off);
        mButtonOn = findViewById(R.id.KStateButton_On);
        onKStateButtonChangeListener = (kStateButton, isOn) -> {

        };
        mButtonOn.setOnClickListener(l -> {
            if(!isOn) {
                isOn = true;
                onKStateButtonChangeListener.onKStateButtonChangeListener(this, true);
                refresh();
            }
        });
        mButtonOff.setOnClickListener(l -> {
            if(isOn) {
                isOn = false;
                onKStateButtonChangeListener.onKStateButtonChangeListener(this, false);
                refresh();
            }
        });
    }

    private void refresh() {
        mButtonOn.setBackground(getResources().getDrawable(isOn ? R.drawable.bg_button_right_borderless_pressed : R.drawable.bg_button_right_borderless, null));
        mButtonOff.setBackground(getResources().getDrawable(isOn ? R.drawable.bg_button_left_borderless : R.drawable.bg_button_left_borderless_pressed, null));
        mButtonOn.setTextColor(getResources().getColor(isOn ? R.color.text_inactive : R.color.white, null));
        mButtonOff.setTextColor(getResources().getColor(isOn ? R.color.white : R.color.text_inactive, null));
    }

    public void setOnKStateButtonChangeListener(OnKStateButtonChangeListener onKStateButtonChangeListener) {
        this.onKStateButtonChangeListener = onKStateButtonChangeListener;
    }

    public void setOn(boolean isOn){
        this.isOn = isOn;
        refresh();
    }

    public interface OnKStateButtonChangeListener {

        void onKStateButtonChangeListener(KStateButton kStateButton, boolean isOn);

    }
}
