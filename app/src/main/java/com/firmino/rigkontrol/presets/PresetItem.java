package com.firmino.rigkontrol.presets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.R;

import java.io.File;
import java.util.Date;

public class PresetItem extends LinearLayout {

    private final TextView mTitle, mDate;
    private File mFile;
    private final Drawable mBgDown, mBgUp;

    public PresetItem(Context context) {
        super(context);
        inflate(context, R.layout.view_preset_item, this);
        mTitle = findViewById(R.id.Preset_Title);
        mDate = findViewById(R.id.Preset_Date);
        mBgUp = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_unicolor , null);
        mBgDown = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_highlight , null);
    }

    public void setup(File file) {
        mFile = file;
        mTitle.setText(file.getName().replace(".xml", ""));
        mDate.setText(new Date(file.lastModified()).toString());
    }

    public File getFile() {
        return mFile;
    }

    @Override
    public boolean performClick() {
        findViewById(R.id.Preset_Main).setBackground(mBgUp);
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        findViewById(R.id.Preset_Main).setBackground(event.getAction() == MotionEvent.ACTION_DOWN ? mBgDown : mBgUp);
        if(event.getAction() == MotionEvent.ACTION_UP) performClick();
        return super.onTouchEvent(event);
    }
}
