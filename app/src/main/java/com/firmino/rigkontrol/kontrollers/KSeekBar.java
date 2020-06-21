package com.firmino.rigkontrol.kontrollers;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.firmino.rigkontrol.R;
import com.firmino.rigkontrol.midi.MidiKontroller;

public class KSeekBar extends LinearLayout {

    private Context mContext;
    private TextView mDescription;
    private SeekBar mSeekBar;
    private int mControlNumber;
    OnKSeekBarValueChangeListener mChangeValueListener;

    public KSeekBar(Context context) {
        super(context);
        init(context);
        mDescription.setText("");
        mSeekBar.setMin(0);
        mSeekBar.setMax(127);
        mSeekBar.setProgress(50);
        mControlNumber = 127;
    }

    public KSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        try {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.KSeekBar, 0, 0);
            mDescription.setText(ta.getString(R.styleable.KSeekBar_k_text));
            mSeekBar.setMin(ta.getInt(R.styleable.KSeekBar_k_min, 0));
            mSeekBar.setMax(ta.getInt(R.styleable.KSeekBar_k_max, 127));
            mSeekBar.setProgress(ta.getInt(R.styleable.KSeekBar_k_mvalue, 50));
            mControlNumber = ta.getInt(R.styleable.KSeekBar_k_control_num, 127);
            ta.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void init(Context context) {
        mContext = context;
        inflate(mContext, R.layout.layout_kseekbar, this);
        mDescription = findViewById(R.id.K_Seek_Name);
        mSeekBar = findViewById(R.id.K_Seek_Seekbar);
        mChangeValueListener = (seekBar, value) -> {
        };
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mChangeValueListener.onKSeekBarValueChangeListener(KSeekBar.this, progress);
                MidiKontroller.midiSendControlChange(mControlNumber, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
}
