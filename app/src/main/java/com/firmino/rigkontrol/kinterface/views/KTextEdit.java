package com.firmino.rigkontrol.kinterface.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.R;

public class KTextEdit extends LinearLayout {

    private final Context context;
    private TextView titleText;
    private EditText textEdit;

    public KTextEdit(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public KTextEdit(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KTextEdit, 0, 0);
        setTextValue(ta.getString(R.styleable.KTextEdit_k_text_edit_text));
        setTitleText(ta.getString(R.styleable.KTextEdit_k_text_edit_title));
        ta.recycle();
    }

    private void init() {
        setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_dialog, null));
        setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.bg_black, null)));
        setOrientation(HORIZONTAL);
        int innerPadding = (int) getResources().getDimension(R.dimen._2dp);
        setPadding(innerPadding, innerPadding, innerPadding, innerPadding);

        titleText = new TextView(context);
        LayoutParams layTitleText = new LayoutParams((int) getResources().getDimension(R.dimen._0dp), LayoutParams.MATCH_PARENT, 1);
        ((MarginLayoutParams) layTitleText).rightMargin = (int) getResources().getDimension(R.dimen._2dp);
        titleText.setLayoutParams(layTitleText);
        titleText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_left_borderless, null));
        titleText.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.dark_foreground, null)));
        titleText.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        titleText.setGravity(Gravity.CENTER);
        titleText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.light_foreground, null));
        titleText.setTextSize(12);
        titleText.setText(R.string.name);

        textEdit = new EditText(context);
        textEdit.setLayoutParams(new LayoutParams((int) getResources().getDimension(R.dimen._0dp), LayoutParams.MATCH_PARENT, 1));
        textEdit.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_right_borderless_pressed, null));
        textEdit.setTypeface(Typeface.create("sans-serif-condensed", Typeface.ITALIC));
        textEdit.setGravity(Gravity.CENTER);
        textEdit.setTextColor(ResourcesCompat.getColor(getResources(), R.color.bg_black, null));
        textEdit.setTextSize(12);
        textEdit.setText(R.string.component);
        textEdit.setMaxLines(1);

        addView(titleText);
        addView(textEdit);
    }

    public void setTitleText(String title) {
        if (title != null && !title.equals("")) titleText.setText(title);
    }

    public void setTextValue(String value) {
        if (value != null && !value.equals("")) textEdit.setText(value);
    }

    public String getText() {
        return textEdit.getText().toString().trim();
    }
}
