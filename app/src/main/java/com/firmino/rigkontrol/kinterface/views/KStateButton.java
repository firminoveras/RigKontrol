package com.firmino.rigkontrol.kinterface.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.firmino.rigkontrol.R;

public class KStateButton extends LinearLayout {

    private KButton mButtonLeft, mButtonRigth;
    private boolean isOn;
    private OnKStateButtonChangeListener onKStateButtonChangeListener;

    public KStateButton(Context context) {
        super(context);
        init();
    }

    public KStateButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KStateButton, 0, 0);
        mButtonRigth.setText(ta.getString(R.styleable.KStateButton_k_statebutton_text_on));
        mButtonLeft.setText(ta.getString(R.styleable.KStateButton_k_statebutton_text_off));
        isOn = ta.getBoolean(R.styleable.KStateButton_k_statebutton_is_on, false);
        ta.recycle();
        refresh();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        mButtonLeft = new KButton(getContext());
        mButtonRigth = new KButton(getContext());

        LayoutParams buttonsLayLeft = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        buttonsLayLeft.rightMargin = (int) getResources().getDimension(R.dimen._2dp);
        mButtonLeft.setAlign(KButton.ALIGN_LEFT);
        mButtonLeft.setToggle(true);
        mButtonLeft.setColorScheme(KButton.COLOR_SCHEME_ORANGE);
        mButtonLeft.setLayoutParams(buttonsLayLeft);


        LayoutParams buttonsLayRigth = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        buttonsLayLeft.leftMargin = (int) getResources().getDimension(R.dimen._2dp);
        mButtonRigth.setAlign(KButton.ALIGN_RIGHT);
        mButtonRigth.setToggle(true);
        mButtonRigth.setColorScheme(KButton.COLOR_SCHEME_ORANGE);
        mButtonRigth.setLayoutParams(buttonsLayRigth);


        onKStateButtonChangeListener = (kStateButton, isOn) -> {

        };
        mButtonRigth.setOnClickListener(l -> {
            if (!isOn) {
                isOn = true;
                onKStateButtonChangeListener.onKStateButtonChangeListener(this, true);
                refresh();
            }
        });
        mButtonLeft.setOnClickListener(l -> {
            if (isOn) {
                isOn = false;
                onKStateButtonChangeListener.onKStateButtonChangeListener(this, false);
                refresh();
            }
        });
        addView(mButtonLeft);
        addView(mButtonRigth);
    }

    private void refresh() {
        mButtonRigth.setOn(isOn);
        mButtonLeft.setOn(!isOn);

    }

    public void setOnKStateButtonChangeListener(OnKStateButtonChangeListener onKStateButtonChangeListener) {
        this.onKStateButtonChangeListener = onKStateButtonChangeListener;
    }

    public void setOn(boolean isOn) {
        this.isOn = isOn;
        refresh();
    }

    public interface OnKStateButtonChangeListener {

        void onKStateButtonChangeListener(KStateButton kStateButton, boolean isOn);

    }
}
