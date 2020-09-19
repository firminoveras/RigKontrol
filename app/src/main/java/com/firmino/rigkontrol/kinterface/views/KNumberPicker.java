package com.firmino.rigkontrol.kinterface.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.R;

public class KNumberPicker extends LinearLayout {

    private final Context context;
    private int minValue = 0, maxValue = 100, value = 0;
    private TextView numberText, titleText;
    private KImageButton minusButton, plusButton;
    private OnKNumberPickerValueChangedListener onKNumberPickerValueChangedListener = v -> {};

    private Handler changeTextSizeHandler;
    private Runnable changeTextSizeRunnable;

    public KNumberPicker(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public KNumberPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KNumberPicker, 0, 0);
        minValue = ta.getInt(R.styleable.KNumberPicker_k_number_picker_min_value, 0);
        maxValue = ta.getInt(R.styleable.KNumberPicker_k_number_picker_max_value, 100);
        setValue(ta.getInt(R.styleable.KNumberPicker_k_number_picker_value, 0));
        titleText.setText(ta.getString(R.styleable.KNumberPicker_k_number_picker_title));
        ta.recycle();
    }

    private void init() {
        changeTextSizeHandler = new Handler();
        changeTextSizeRunnable = () -> numberText.setTextSize(12);

        setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_dialog, null));
        setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.bg_black, null)));
        setOrientation(HORIZONTAL);
        int innerPadding = (int) getResources().getDimension(R.dimen._2dp);
        setPadding(innerPadding, innerPadding, innerPadding, innerPadding);

        numberText = new TextView(context);
        LayoutParams laySmall = new LayoutParams((int) getResources().getDimension(R.dimen._0dp), LayoutParams.MATCH_PARENT, 1);
        numberText.setLayoutParams(laySmall);
        numberText.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        numberText.setGravity(Gravity.CENTER);
        numberText.setText(R.string.zero);
        numberText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        numberText.setTextSize(12);

        titleText = new TextView(context);
        LayoutParams layTitleText = new LayoutParams((int) getResources().getDimension(R.dimen._0dp), LayoutParams.MATCH_PARENT, 3);
        layTitleText.rightMargin = (int) getResources().getDimension(R.dimen._2dp);
        titleText.setLayoutParams(layTitleText);
        titleText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_left_borderless, null));
        titleText.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.dark_foreground, null)));
        titleText.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        titleText.setGravity(Gravity.CENTER);
        titleText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.light_foreground, null));
        titleText.setTextSize(12);
        titleText.setText(R.string.NUMBER);

        minusButton = new KImageButton(context);
        minusButton.setAlign(KImageButton.ALIGN_NONE);
        minusButton.setImageResource(R.drawable.ic_minus_simple);
        int padding = (int) getResources().getDimension(R.dimen._5dp);
        minusButton.setPadding(padding, padding, padding, padding);
        minusButton.setLayoutParams(laySmall);
        minusButton.setOnClickListener(view -> {
            if (setValue(value - 1)) onKNumberPickerValueChangedListener.onKNumberPickerValueChangedListener(value);
            numberText.setTextSize(20);
            changeTextSizeHandler.removeCallbacks(changeTextSizeRunnable);
            changeTextSizeHandler.postDelayed(changeTextSizeRunnable, 500);
        });


        plusButton = new KImageButton(context);
        plusButton.setAlign(KImageButton.ALIGN_RIGHT);
        plusButton.setImageResource(R.drawable.ic_add_simple);
        plusButton.setPadding(padding, padding, padding, padding);
        plusButton.setLayoutParams(laySmall);
        plusButton.setOnClickListener(view -> {
            if (setValue(value + 1)) onKNumberPickerValueChangedListener.onKNumberPickerValueChangedListener(value);
            numberText.setTextSize(20);
            changeTextSizeHandler.removeCallbacks(changeTextSizeRunnable);
            changeTextSizeHandler.postDelayed(changeTextSizeRunnable, 500);
        });

        addView(titleText);
        addView(minusButton);
        addView(numberText);
        addView(plusButton);

    }

    public void setTitle(String title) {
        titleText.setText(title);
    }

    public void setTitle(int id) {
        titleText.setText(id);
    }

    public void setMaxValue(int max) {
        maxValue = max;
        if (value > max) setValue(max);
    }

    public void setMinValue(int min) {
        maxValue = min;
        if (value < min) setValue(min);
    }

    public boolean setValue(int newValue) {
        if (newValue >= minValue && newValue <= maxValue) {
            value = newValue;
            numberText.setText(String.valueOf(value));
            return true;
        } else return false;
    }

    public void setOnKNumberPickerValueChangedListener(OnKNumberPickerValueChangedListener onKNumberPickerValueChangedListener) {
        this.onKNumberPickerValueChangedListener = onKNumberPickerValueChangedListener;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getValue() {
        return value;
    }

    public interface OnKNumberPickerValueChangedListener {
        void onKNumberPickerValueChangedListener(int value);
    }
}
