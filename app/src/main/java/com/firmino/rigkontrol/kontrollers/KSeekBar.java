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

    private final Context mContext;
    private TextView mText;
    private SeekBar mSeekBar;
    private AttributeSet attrs;
    OnKSeekBarValueChangeListener mChangeValueListener;

    public KSeekBar(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public KSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        mContext = context;
        init();
    }

    public KSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
        mContext = context;
        init();
    }

    private void init() {
        inflate(mContext, R.layout.layout_kseekbar, this);
        mText = findViewById(R.id.K_Seek_Name);
        mSeekBar = findViewById(R.id.K_Seek_Seekbar);
        mChangeValueListener = (seekBar, value) -> {
        };
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mChangeValueListener.onKSeekBarValueChangeListener(KSeekBar.this, progress);
                //TODO: Fazer cada um ter um component number diferente ja por padrao
                //MidiKontroller.midiSendControlChange(mComponentNumber, isEnabled ? mValueEnabled: mValueDisabled);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        if (attrs != null) {
            TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.KSeekBar, 0, 0);
            mText.setText(ta.getString(R.styleable.KSeekBar_k_text));
            mSeekBar.setMin(ta.getInt(R.styleable.KSeekBar_k_min, 0));
            mSeekBar.setMax(ta.getInt(R.styleable.KSeekBar_k_max, 127));
            mSeekBar.setProgress(ta.getInt(R.styleable.KSeekBar_k_mvalue, 50));
            ta.recycle();
        }
    }

    public void setValue(int value) {
        mSeekBar.setProgress(value);
    }

    public int getValue() {
        return mSeekBar.getProgress();
    }

    public void setText(String text) {
        mText.setText(text);
    }

    public String getText() {
        return mText.getText().toString();
    }

    public void setOnKSeekBarValueChangeListener(OnKSeekBarValueChangeListener l){
        mChangeValueListener = l;
    }
}
