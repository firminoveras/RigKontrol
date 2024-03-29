package com.firmino.rigkontrol.racks.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.firmino.rigkontrol.R;
import com.firmino.rigkontrol.kinterface.alerts.ConfirmationAlert;

public class Component extends FrameLayout {

    public final static int TYPE_POTENTIOMETER = 0;
    public final static int TYPE_PUSH_BUTTON = 1;
    public final static int TYPE_SLIDE = 2;

    private final Context mContext;
    private ColorStateList mForegroundColor;
    private ImageView mConfigButton, mRemoveButton;
    private TextView mTitle;
    private RelativeLayout mConfigLayout;
    private FrameLayout mComponentContainer;
    private OnConfigButtonClickedListener onConfigButtonClickedListener = () -> {};
    private OnComponentMidiControlChangeListener onComponentMidiControlChangeListener = (cc, value) -> {};
    private OnColorChangeListener onColorChangeListener = (color) -> {};
    private int mControlChange = 0;
    private int mType = -1;
    private boolean isConfigModeEnabled = false;

    public Component(@NonNull Context context) {
        super(context);
        mContext = context;
        init();
    }

    public Component(@NonNull Context context, ColorStateList foregroundColor) {
        super(context);
        mContext = context;
        init();
        setForegroundColor(foregroundColor);
    }

    private void init() {
        inflate(mContext, R.layout.layout_component, this);
        mConfigButton = findViewById(R.id.Component_Config);
        mRemoveButton = findViewById(R.id.Component_Remove);
        mConfigLayout = findViewById(R.id.Component_Config_Layout);
        mComponentContainer = findViewById(R.id.Component_View);
        mTitle = findViewById(R.id.Component_Title);
        setLayoutParams(new TableLayout.LayoutParams((int) getResources().getDimension(R.dimen._0dp), ViewGroup.LayoutParams.MATCH_PARENT, 1));
        mRemoveButton.setOnClickListener(view -> new ConfirmationAlert("Deleting a Component", getContext().getString(R.string.delete_component), mContext).setOnConfirmationAlertConfirm(() -> ((ViewGroup) getParent()).removeView(Component.this)));
        mConfigButton.setOnClickListener(view -> onConfigButtonClickedListener.onConfigButtonClickedListener());
        setForegroundColor(ColorStateList.valueOf(Color.BLACK));
    }

    public ColorStateList getForegroundColor() {
        return mForegroundColor;
    }

    public void setForegroundColor(ColorStateList mForegroundColor) {
        this.mForegroundColor = mForegroundColor;
        mTitle.setTextColor(mForegroundColor.getDefaultColor());
        onColorChangeListener.onColorChangeListener(mForegroundColor);
    }

    public void setConfigModeEnabled(boolean configModeEnabled) {
        if (configModeEnabled != isConfigModeEnabled) {
            isConfigModeEnabled = configModeEnabled;
            mConfigLayout.setEnabled(configModeEnabled);
            mConfigButton.setEnabled(configModeEnabled);
            mRemoveButton.setEnabled(configModeEnabled);
            ValueAnimator anim = ValueAnimator.ofFloat(configModeEnabled ? 0 : .8f, configModeEnabled ? .8f : 0);
            anim.addUpdateListener(valueAnimator -> mConfigLayout.setAlpha((Float) valueAnimator.getAnimatedValue()));
            anim.setDuration(300);
            anim.start();
        }
    }

    public void setComponentValue(int value) {
        if (value >= 0 && value <= 127) {
            onComponentMidiControlChangeListener.onComponentMidiControlChangeListener(this.mControlChange, value);
        }
    }

    public void setTitle(String title) {
        mTitle.setText(title.trim().toUpperCase());
        mTitle.setVisibility(title.length() == 0 ? GONE : VISIBLE);
    }

    public String getTitle() {
        return mTitle.getText().toString();
    }

    public void setComponentView(View view) {
        mComponentContainer.removeAllViews();
        mComponentContainer.addView(view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public int getControlChange() {
        return mControlChange;
    }

    public void setControlChange(int controlChange) {
        mControlChange = controlChange;
    }

    public void setComponentTitle(String title) {
        mTitle.setText(title);
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public void setOnConfigButtonClickedListener(OnConfigButtonClickedListener onConfigButtonClickedListener) {
        this.onConfigButtonClickedListener = onConfigButtonClickedListener;
    }

    public void setOnComponentMidiControlChangeListener(OnComponentMidiControlChangeListener onComponentMidiControlChangeListener) {
        this.onComponentMidiControlChangeListener = onComponentMidiControlChangeListener;
    }

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.onColorChangeListener = onColorChangeListener;
    }

    public interface OnConfigButtonClickedListener {
        void onConfigButtonClickedListener();
    }

    public interface OnComponentMidiControlChangeListener {
        void onComponentMidiControlChangeListener(int cc, int value);
    }

    public interface OnColorChangeListener {
        void onColorChangeListener(ColorStateList color);
    }

}
