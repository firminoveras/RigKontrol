package com.firmino.racks.interfaceitems;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.core.content.res.ResourcesCompat;

import com.firmino.racks.R;

public class SquareRackButton extends androidx.appcompat.widget.AppCompatButton {

    public SquareRackButton(Context context) {
        super(context);
    }

    public SquareRackButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_rack_square_button_highlight, null));
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_rack_square_button, null));
            performClick();
        }
        return true;
    }
}
