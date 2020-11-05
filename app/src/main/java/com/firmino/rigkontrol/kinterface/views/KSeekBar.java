package com.firmino.rigkontrol.kinterface.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firmino.rigkontrol.R;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

public class KSeekBar extends LinearLayout {

    TextView mDescription;
    RangeSeekBar mSeekBar;
    int mControlNumber;
    OnKSeekBarValueChangeListener mChangeValueListener;

    public KSeekBar(Context context) {
        super(context);
        init();
        kontrollerSetup("", 0, 127, 50, 127);
    }

    public KSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.KSeekBar, 0, 0);
        kontrollerSetup(
                ta.getString(R.styleable.KSeekBar_k_seekbar_description),
                ta.getInt(R.styleable.KSeekBar_k_seekbar_control_num, 127),
                ta.getInt(R.styleable.KSeekBar_k_seekbar_min, 0),
                ta.getInt(R.styleable.KSeekBar_k_seekbar_max, 127),
                ta.getInt(R.styleable.KSeekBar_k_seekbar_progress, 50)
        );
        ta.recycle();
    }

    void init() {
        inflate(this.getContext(), R.layout.layout_interface_kseekbar, this);
        mDescription = findViewById(R.id.K_Seek_Name);
        mSeekBar = findViewById(R.id.K_Seek_Seekbar);
        mChangeValueListener = (seekBar, value, c) -> {
        };

        mSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                mChangeValueListener.onKSeekBarValueChangeListener(KSeekBar.this, (int) leftValue, mControlNumber);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });
    }

    public void kontrollerSetup(String description, int controlNumber, int min, int max, int progress) {
        mDescription.setText(description);
        mSeekBar.setRange(min,max);
        mSeekBar.setProgress(progress);
        mControlNumber = controlNumber;
    }

    public void setValue(int value) {
        mSeekBar.setProgress(value);
    }

    public int getValue() {
        return mSeekBar.getProgressLeft();
    }

    public void setOnKSeekBarValueChangeListener(OnKSeekBarValueChangeListener l) {
        mChangeValueListener = l;
    }

    public interface OnKSeekBarValueChangeListener {
        void onKSeekBarValueChangeListener(KSeekBar seekBar, int value, int controllerNumber);
    }
}
