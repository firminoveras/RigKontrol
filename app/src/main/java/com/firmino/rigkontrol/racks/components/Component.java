package com.firmino.rigkontrol.racks.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
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
        mTitle = findViewById(R.id.Component_Title);
        mComponentContainer = findViewById(R.id.Component_View);
        setLayoutParams(new TableLayout.LayoutParams((int) getResources().getDimension(R.dimen._0dp), ViewGroup.LayoutParams.MATCH_PARENT, 1));
        mRemoveButton.setOnClickListener(view -> new ConfirmationAlert("Deleting a Component", getContext().getString(R.string.delete_component), mContext).setOnConfirmationAlertConfirm(() -> {
            ((ViewGroup) getParent()).removeView(Component.this);
        }));
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

    public void setConfigModeEnabled(boolean enabled) {
        mConfigLayout.setEnabled(enabled);
        mConfigButton.setEnabled(enabled);
        mRemoveButton.setEnabled(enabled);
        ValueAnimator anim = ValueAnimator.ofFloat(enabled ? 0 : 1.0f, enabled ? 1.0f : 0);
        anim.addUpdateListener(valueAnimator -> mConfigLayout.setAlpha((Float) valueAnimator.getAnimatedValue()));
        anim.setDuration(500);
        anim.start();
    }

    public void setComponentValue(int value) {
        if (value >= 0 && value <= 127) {
            onComponentMidiControlChangeListener.onComponentMidiControlChangeListener(this.mControlChange, value);
        }
    }

    public void setTitle(String title) {
        mTitle.setText(title.trim());
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
