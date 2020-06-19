package com.firmino.rigkontrol.kontrollers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.firmino.rigkontrol.R;

public class KPedal extends LinearLayout implements View.OnTouchListener {

    private OnPedalValueChangeListener valueChange;
    private final Context mContext;
    private FrameLayout mPedal;
    private ImageView mPedalShadow;
    private float mOldRawY;
    private int mValue, mOldValue;

    public KPedal(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public KPedal(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public KPedal(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    void init() {
        inflate(mContext, R.layout.layout_kpedal, this);
        mPedal = findViewById(R.id.K_Pedal);
        mPedalShadow = findViewById(R.id.K_Pedal_Shadow);
        mOldRawY = 0;
        mValue = 0;
        mOldValue = 0;
        setOnTouchListener(this);
        valueChange = (pedal, value) -> {
        };
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mValue += (event.getRawY() / 2) - mOldRawY;
                if (mValue > 127) mValue = 127;
                else if (mValue < 0) mValue = 0;
                mPedalShadow.setAlpha(((float) mValue) / 127);
                mPedal.setPadding(mPedal.getPaddingLeft(), mValue / 2, mPedal.getPaddingEnd(), mValue / 5);
                if (mOldValue != mValue) valueChange.onPedalChangeListener(this, 127 - mValue);
                mOldValue = mValue;
                mOldRawY = (event.getRawY() / 2);
                break;
            case MotionEvent.ACTION_DOWN:
                mOldRawY = event.getRawY() / 2;
                break;
        }
        return true;
    }


    public void setOnPedalValueChangeListener(OnPedalValueChangeListener l) {
        valueChange = l;
    }
}

