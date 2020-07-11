package com.firmino.rigkontrol.kontrollers;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.firmino.rigkontrol.R;

public class KSeekBar extends LinearLayout {

    private TextView mDescription;
    private SeekBar mSeekBar;
    private int mControlNumber;
    private OnKSeekBarValueChangeListener mChangeValueListener;

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

    private void init() {
        inflate(this.getContext(), R.layout.layout_kseekbar, this);
        mDescription = findViewById(R.id.K_Seek_Name);
        mSeekBar = findViewById(R.id.K_Seek_Seekbar);
        mChangeValueListener = (seekBar, value, c) -> {
        };

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mChangeValueListener.onKSeekBarValueChangeListener(KSeekBar.this, progress, mControlNumber);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void kontrollerSetup(String description, int controlNumber, int min, int max, int progress) {
        mDescription.setText(description);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) mSeekBar.setMin(min);
        mSeekBar.setMax(max);
        mSeekBar.setProgress(progress);
        mControlNumber = controlNumber;
    }

    public void setValue(int value) {
        mSeekBar.setProgress(value);
    }

    public int getValue() {
        return mSeekBar.getProgress();
    }

    public void setText(String text) {
        mDescription.setText(text);
    }

    public String getText() {
        return mDescription.getText().toString();
    }

    public void setOnKSeekBarValueChangeListener(OnKSeekBarValueChangeListener l) {
        mChangeValueListener = l;
    }

    public interface OnKSeekBarValueChangeListener {
        void onKSeekBarValueChangeListener(KSeekBar seekBar, int value, int controllerNumber);
    }
}
