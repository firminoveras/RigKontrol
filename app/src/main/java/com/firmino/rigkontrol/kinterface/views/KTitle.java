package com.firmino.rigkontrol.kinterface.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.R;


public class KTitle extends LinearLayout {

    private TextView mText;
    private Context mContext;

    public KTitle(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public KTitle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KTitle, 0, 0);
        setText(ta.getString(R.styleable.KTitle_k_title));
        setDrawable(ta.getResourceId(R.styleable.KTitle_k_icon, R.drawable.ic_warning));
        ta.recycle();
    }

    private void init() {
        setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_pressed, null));
        setGravity(Gravity.CENTER);

        mText = new TextView(mContext);
        mText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mText.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen._5dp));
        mText.setTypeface(Typeface.create("sans-serif-condensed-medium", Typeface.NORMAL));
        mText.setGravity(Gravity.CENTER);
        mText.setMaxLines(1);
        mText.setAllCaps(true);
        int _8dp = (int) getResources().getDimension(R.dimen._8dp);
        mText.setPadding(_8dp, _8dp, _8dp, _8dp);
        mText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.bg_dark_gray, null));
        setText("");
        addView(mText);
    }

    public void setDrawable(int id) {
        mText.setCompoundDrawablesRelativeWithIntrinsicBounds(id, 0, 0, 0);
    }

    public void setText(String text) {
        mText.setText(text);
    }
}
