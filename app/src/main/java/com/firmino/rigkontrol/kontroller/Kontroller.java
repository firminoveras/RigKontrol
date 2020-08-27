package com.firmino.rigkontrol.kontroller;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.R;

public class Kontroller extends LinearLayout {

    private KSlider mSlider;
    private KPedal mPedal;
    private KFootSwitch[] mButton;
    private KFootSwitch mButtonPedal;
    private Drawable mLedOn, mLedOff;
    private OnConnectLedClickListener onConnectLedClickListener;

    public Kontroller(Context context) {
        super(context);
        init();
    }

    public Kontroller(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_kontroller, this);
        mSlider = findViewById(R.id.Main_Slider);
        mPedal = findViewById(R.id.Main_Pedal);
        mButtonPedal = findViewById(R.id.Main_BT_Pedal_Down);
        mButton = new KFootSwitch[]{
                findViewById(R.id.Main_BT1),
                findViewById(R.id.Main_BT2),
                findViewById(R.id.Main_BT3),
                findViewById(R.id.Main_BT4),
                findViewById(R.id.Main_BT5),
                findViewById(R.id.Main_BT6),
                findViewById(R.id.Main_BT7),
                findViewById(R.id.Main_BT8),
        };
        onConnectLedClickListener = () -> {

        };
        findViewById(R.id.Kontroller_Led_Status).setOnClickListener(v -> onConnectLedClickListener.onConnectedLedClick());
        mLedOn = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_led_on, null);
        mLedOff = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_led_off, null);
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

    public void setExpandedMode(boolean extended) {
        for (KFootSwitch b : mButton) b.setDescriptionVisible(!extended);
        mButtonPedal.setDescriptionVisible(!extended);
        mSlider.setExpanded(!extended);
    }

    public KFootSwitch[] getButtons() {
        return mButton;
    }

    public KFootSwitch getButtonPedal() {
        return mButtonPedal;
    }

    public KPedal getPedal() {
        return mPedal;
    }

    public KSlider getSlider() {
        return mSlider;
    }

    public void setPedalVisible(boolean visible) {
        ValueAnimator anim = ValueAnimator.ofFloat(visible ? 1.5f : 1, visible ? 1 : 1.5f);
        anim.addUpdateListener(animator -> {
            GridLayout.LayoutParams layoutParams = (GridLayout.LayoutParams) mPedal.getLayoutParams();
            layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, (float) animator.getAnimatedValue());
            mPedal.setLayoutParams(layoutParams);
        });
        anim.setDuration(getResources().getInteger(R.integer.animation_duration_options));
        anim.start();
    }

    public void setConnectionLedOn(boolean isOn) {
        ((ImageView) findViewById(R.id.Kontroller_Led)).setImageDrawable(isOn ? mLedOn : mLedOff);
        ((TextView) findViewById(R.id.Kontroller_Led_Status)).setText(isOn ? "CONNECTED" : "DISCONNECTED");
    }

    public void setOnConnectLedClickListener(OnConnectLedClickListener onConnectLedClickListener) {
        this.onConnectLedClickListener = onConnectLedClickListener;
    }

    public interface OnConnectLedClickListener {
        void onConnectedLedClick();
    }

}
