package com.firmino.rigkontrol.kinterface.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;

import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.R;


public class KButton extends androidx.appcompat.widget.AppCompatButton {

    public KButton(Context context) {
        super(context);
        init();
    }

    public KButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_rack_square_button,null));
        setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        setGravity(Gravity.CENTER);
        setTextSize(12);
        setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setBackground(ResourcesCompat.getDrawable(getResources(), event.getAction() == MotionEvent.ACTION_DOWN ? R.drawable.bg_rack_square_button_highlight : R.drawable.bg_rack_square_button, null));
        if (event.getAction() == MotionEvent.ACTION_UP) performClick();
        return true;
    }
}
