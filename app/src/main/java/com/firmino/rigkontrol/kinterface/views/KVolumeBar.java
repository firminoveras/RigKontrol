package com.firmino.rigkontrol.kinterface.views;

import android.content.Context;
import android.util.AttributeSet;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import java.util.Locale;

public class KVolumeBar extends KSeekBar{

    private String mText = "";
    private boolean isTracking = false;

    public KVolumeBar(Context context) {
        super(context);
    }

    public KVolumeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mText = mDescription.getText().toString();
    }

    @Override
    protected void init() {
        super.init();
        mSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                mChangeValueListener.onKSeekBarValueChangeListener(KVolumeBar.this, (int) leftValue, mControlNumber);
                float db = (leftValue - (float) 0) * ((float) 20 - (float) -20) / ((float) 127 - (float) 0) + (float) -20;
                String signal = db > 0 ? "+" : "";
                String text = signal + String.format(Locale.getDefault(),"% .1f",db).replace(",", ".") + "dB";
                if(isTracking)mDescription.setText(text);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                isTracking = true;
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                isTracking = false;
                mDescription.setText(mText);
            }
        });
        mDescription.setOnLongClickListener(v -> {
            mSeekBar.setProgress(mSeekBar.getMaxProgress()/2);
            return true;
        });
    }
}
