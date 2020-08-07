package com.firmino.rigkontrol.presets;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.firmino.rigkontrol.R;

import java.io.File;
import java.util.Date;

public class PresetItem extends LinearLayout {

    private TextView mTitle, mDate;
    private File mFile;

    public PresetItem(Context context) {
        super(context);
        inflate(context, R.layout.view_preset_item, this);
        mTitle = findViewById(R.id.Preset_Title);
        mDate = findViewById(R.id.Preset_Date);
    }

    public PresetItem setup(File file){
        mFile = file;
        mTitle.setText(file.getName().replace(".xml", ""));
        mDate.setText(new Date(file.lastModified()).toString());
        return this;
    }

    public File getFile(){
        return mFile;
    }

}
