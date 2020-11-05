package com.firmino.rigkontrol.racks.components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

public class Slider extends Component {

    private SeekBar mSlider;
    private final Context mContext;

    public Slider(@NonNull Context context) {
        super(context);
        mContext = context;
        init();

    }

    public Slider(@NonNull Context context, ColorStateList foregroundColor) {
        super(context, foregroundColor);
        mContext = context;
        init();
    }

    private void init() {
        mSlider = new SeekBar(mContext);
        setComponentView(mSlider);
    }
}
