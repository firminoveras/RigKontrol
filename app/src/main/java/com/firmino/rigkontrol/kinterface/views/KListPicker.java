package com.firmino.rigkontrol.kinterface.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KListPicker extends LinearLayout {

    private static final int FIRST = 0;
    private TextView mTitle, mTextValue;
    private KImageButton mNextButton, mPreviousButton;
    private final Context mContext;
    private List<String> mList;
    private int mSelectedItem = 0;
    OnKListPikerItemSelectedListener onKListPikerItemSelectedListener = (index, item) -> {};

    public KListPicker(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public KListPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KListPicker, 0, 0);
        setTitle(ta.getString(R.styleable.KListPicker_k_list_picker_title));
        addItems(getResources().getStringArray(ta.getResourceId(R.styleable.KListPicker_k_list_picker_item_list, R.array.emptyArray)));
        ta.recycle();
    }

    private void init() {
        int _2dp = (int) getResources().getDimension(R.dimen._2dp);
        mList = new ArrayList<>();

        setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_rack_dialog, null));
        setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.bg_black, null)));
        setPadding(_2dp, _2dp, _2dp, _2dp);
        setOrientation(HORIZONTAL);

        mTitle = new TextView(mContext);
        LayoutParams layTitleText = new LayoutParams((int) getResources().getDimension(R.dimen._0dp), LayoutParams.MATCH_PARENT, 5);
        layTitleText.rightMargin = (int) getResources().getDimension(R.dimen._2dp);
        mTitle.setLayoutParams(layTitleText);
        mTitle.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_button_left_borderless, null));
        mTitle.setBackgroundTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.dark_foreground, null)));
        mTitle.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.light_foreground, null));
        mTitle.setTextSize(12);
        mTitle.setText(R.string.list_picker);

        mPreviousButton = new KImageButton(mContext);
        mPreviousButton.setLayoutParams(new LayoutParams((int) getResources().getDimension(R.dimen._0dp), LayoutParams.MATCH_PARENT, 1));
        mPreviousButton.setAlign(KImageButton.ALIGN_NONE);
        mPreviousButton.setImageResource(R.drawable.ic_arrow_left);
        mPreviousButton.setOnClickListener(view -> selectPreviousItem());

        mTextValue = new TextView(mContext);
        mTextValue.setLayoutParams(new LayoutParams((int) getResources().getDimension(R.dimen._0dp), LayoutParams.MATCH_PARENT, 3));
        mTextValue.setGravity(Gravity.CENTER);
        mTextValue.setTextColor(ResourcesCompat.getColor(getResources(), R.color.foreground, null));
        mTextValue.setTextSize(12);

        mNextButton = new KImageButton(mContext);
        mNextButton.setLayoutParams(new LayoutParams((int) getResources().getDimension(R.dimen._0dp), LayoutParams.MATCH_PARENT, 1));
        mNextButton.setAlign(KImageButton.ALIGN_RIGHT);
        mNextButton.setImageResource(R.drawable.ic_arrow_right);
        mNextButton.setOnClickListener(view -> selectNextItem());

        setSelectedItem(FIRST);

        addView(mTitle);
        addView(mPreviousButton);
        addView(mTextValue);
        addView(mNextButton);
    }

    private void selectNextItem() {
        if ((getSelectedItemIndex() + 1) >= lenght()) {
            setSelectedItem(FIRST);
        } else {
            setSelectedItem((getSelectedItemIndex() + 1));
        }
    }

    private void selectPreviousItem() {
        if ((getSelectedItemIndex() - 1) < 0) {
            setSelectedItem(lenght() - 1);
        } else {
            setSelectedItem((getSelectedItemIndex() - 1));
        }
    }

    public void addItem(String item) {
        mList.add(item);
        setSelectedItem(FIRST);
    }

    public void addItems(String[] items) {
        mList.addAll(Arrays.asList(items));
        setSelectedItem(FIRST);
    }

    public void removeAllItems() {
        mList.clear();
    }

    public void removeItem(String item) {
        mList.remove(item);
    }

    public int lenght() {
        return mList.size();
    }

    public void setSelectedItem(String item) {
        if (mList.contains(item)) {
            String text = (1 + mList.indexOf(item)) + " - " + item;
            mTextValue.setText(text);
            mSelectedItem = mList.indexOf(item);
        }
    }

    public void setSelectedItem(int index) {
        if (index < mList.size()) {
            mTextValue.setText(mList.get(index));
            mSelectedItem = index;
            onKListPikerItemSelectedListener.onKListPikerItemSelectedListener(getSelectedItemIndex(), getSelectedItem());
        }
    }

    public int getSelectedItemIndex() {
        return mSelectedItem;
    }

    public String getSelectedItem() {
        return mList.get(mSelectedItem);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setOnKListPikerItemSelectedListener(OnKListPikerItemSelectedListener onKListPikerItemSelectedListener) {
        this.onKListPikerItemSelectedListener = onKListPikerItemSelectedListener;
    }

    public interface OnKListPikerItemSelectedListener {
        void onKListPikerItemSelectedListener(int index, String item);
    }

}
