package com.firmino.rigkontrol.kontrollers;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.Gravity;

import com.firmino.rigkontrol.R;

public class KSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {
    public KSeekBar(Context context) {
        super(context);
    }

    public KSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        drawSplit();
    }

    private void drawSplit() {
        Drawable G_bg = getResources().getDrawable(R.drawable.ic_bg_seek_disactive, null);
        Drawable G_pg = getResources().getDrawable(R.drawable.ic_bg_seek_active, null);
        ClipDrawable c = new ClipDrawable(G_pg, Gravity.START, ClipDrawable.HORIZONTAL);
        LayerDrawable ld = new LayerDrawable(new Drawable[]{G_bg, c});
        setProgressDrawable(ld);
    }

}
