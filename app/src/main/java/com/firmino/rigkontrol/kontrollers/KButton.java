package com.firmino.rigkontrol.kontrollers;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.R;
import com.jaygoo.widget.RangeSeekBar;

import java.util.Objects;

public class KButton extends LinearLayout {

    private TextView mDescription;
    private ImageView mButton, mConfigIcon, mPedalDownIcon;
    private LinearLayout mDescriptionLayout;
    private boolean isToggle, isOn;
    private int mControllerNumber, mValueOn, mValueOff;
    private Drawable mImgButtonOn, mImgButtonOff;
    private OnKButtonStateChangeListener onKButtonStateChangeListener;

    public KButton(@NonNull Context context) {
        super(context);
        setup("0", 0, 127, 0, false);
        init();
    }

    public KButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KButton, 0, 0);
        setup(
                ta.getString(R.styleable.KButton_k_button_description),
                ta.getInt(R.styleable.KButton_k_button_control_num, 0),
                ta.getInt(R.styleable.KButton_k_button_on_value, 127),
                ta.getInt(R.styleable.KButton_k_button_off_value, 0),
                ta.getBoolean(R.styleable.KButton_k_button_is_toggled, false)
        );
        ta.recycle();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        inflate(this.getContext(), R.layout.layout_kbutton, this);
        mPedalDownIcon = findViewById(R.id.K_Pedal_Down_Icon);
        mConfigIcon = findViewById(R.id.K_Config_Icon);
        mButton = findViewById(R.id.K_Button);
        mDescription = findViewById(R.id.K_Description);
        mDescriptionLayout = findViewById(R.id.K_Description_BG);

        onKButtonStateChangeListener = (button, valueOn, valueOff, isOn, controllerNumber) -> {

        };

        mImgButtonOn = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_button_on, null);
        mImgButtonOff = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_button_off, null);

        mButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    kontrollerButtonDown();
                    return true;
                case MotionEvent.ACTION_UP:
                    kontrollerButtonUp();
                    return true;
                default:
                    return false;
            }
        });

        mDescription.setOnTouchListener((v, event) -> {
            mDescriptionLayout.setBackground(ResourcesCompat.getDrawable(getResources(), event.getAction() == MotionEvent.ACTION_DOWN ? R.drawable.bg_button_text_selected : isOn ? R.drawable.bg_button_text_enabled : R.drawable.bg_dialog, null));
            mDescription.setVisibility(event.getAction() == MotionEvent.ACTION_DOWN ? GONE : VISIBLE);
            mConfigIcon.setVisibility(event.getAction() == MotionEvent.ACTION_DOWN ? VISIBLE : GONE);
            return false;
        });

        mDescription.setOnLongClickListener(v -> {
            showConfigsDialog();
            return true;
        });
    }

    private void showConfigsDialog() {
        View mDialogContent = LayoutInflater.from(this.getContext()).inflate(R.layout.dialog_config, null);
        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this.getContext());
        mDialog.setCancelable(false);
        mDialog.setView(mDialogContent);
        AlertDialog dialog = mDialog.show();
        Objects.requireNonNull(dialog.getWindow()).setLayout((int) (400 * getResources().getDisplayMetrics().density), -2);

        TextView dialog_ComponentNumber = mDialogContent.findViewById(R.id.Config_ComponentNumber);
        dialog_ComponentNumber.setText(String.valueOf(mControllerNumber));

        TextView dialog_Name = mDialogContent.findViewById(R.id.Config_Name);
        dialog_Name.setText(mDescription.getText());

        RangeSeekBar dialog_seekBar = mDialogContent.findViewById(R.id.Config_Values);
        dialog_seekBar.getRightSeekBar().setThumbDrawableId(R.drawable.ic_range_close);
        dialog_seekBar.setProgress(dialog_seekBar.getMinProgress(), dialog_seekBar.getMaxProgress());
        dialog_seekBar.setIndicatorTextDecimalFormat("0");
        dialog_seekBar.setProgress(mValueOff, mValueOn);

        KStateButton dialog_holdMode = mDialogContent.findViewById(R.id.Config_HoldMode);
        dialog_holdMode.setOn(isToggle);
        dialog_holdMode.setOnKStateButtonChangeListener((kStateButton, isOn) -> isToggle = isOn);

        mDialogContent.findViewById(R.id.Config_ComponentMinus).setOnClickListener(l -> {
            if (Integer.parseInt(dialog_ComponentNumber.getText().toString()) > 0)
                dialog_ComponentNumber.setText(String.valueOf(Integer.parseInt(dialog_ComponentNumber.getText().toString()) - 1));
        });
        mDialogContent.findViewById(R.id.Config_ComponentPlus).setOnClickListener(l -> {
            if (Integer.parseInt(dialog_ComponentNumber.getText().toString()) < 120)
                dialog_ComponentNumber.setText(String.valueOf(Integer.parseInt(dialog_ComponentNumber.getText().toString()) + 1));
        });

        mDialogContent.findViewById(R.id.Config_OK).setOnClickListener(l -> {
            setup(
                    dialog_Name.getText().toString(),
                    Integer.parseInt(dialog_ComponentNumber.getText().toString()),
                    (int) dialog_seekBar.getRightSeekBar().getProgress(),
                    (int) dialog_seekBar.getLeftSeekBar().getProgress(),
                    isToggle
            );
            dialog.dismiss();
        });
    }

    public void setup(String description, int controlNumber, int valueOn, int valueOff, boolean isToggledOn) {
        mDescription.setText(description);
        mControllerNumber = controlNumber;
        mValueOn = valueOn;
        mValueOff = valueOff;
        isToggle = isToggledOn;
    }

    public void kontrollerButtonUp() {
        if (!isToggle) {
            isOn = false;
            mButton.setImageDrawable(mImgButtonOff);
            mDescriptionLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bg_dialog, null));
            this.onKButtonStateChangeListener.onKButtonStateChangeListener(this, mValueOn, mValueOff, isOn, mControllerNumber);
        }
    }

    public void kontrollerButtonDown() {
        isOn = !isOn;
        mButton.setImageDrawable(isOn ? mImgButtonOn : mImgButtonOff);
        mDescriptionLayout.setBackground(ResourcesCompat.getDrawable(getResources(),isOn ? R.drawable.bg_button_text_enabled : R.drawable.bg_dialog, null));
        this.onKButtonStateChangeListener.onKButtonStateChangeListener(this, mValueOn, mValueOff, isOn, mControllerNumber);
    }

    public void setDescriptionVisible(boolean isExpanded) {
        ValueAnimator anim = ValueAnimator.ofFloat(isExpanded ? 1 : 0, isExpanded ? 0 : 1);
        anim.addUpdateListener(animation -> {
            mDescriptionLayout.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, (Float) animation.getAnimatedValue()));
            mPedalDownIcon.setAlpha((Float) animation.getAnimatedValue());
        });
        anim.setDuration(500);
        anim.start();
    }

    public void setPedalDownIconVisible(boolean isVisible) {
        mPedalDownIcon.setVisibility(isVisible ? VISIBLE : INVISIBLE);
        invalidate();
    }

    public boolean isOn() {
        return isOn;
    }

    public String getKDescription() {
        return mDescription.getText().toString();
    }

    public boolean isToggle() {
        return isToggle;
    }

    public int getKControllerNumber() {
        return mControllerNumber;
    }

    public int getKValueOn() {
        return mValueOn;
    }

    public int getKValueOff() {
        return mValueOff;
    }

    public void setOnKButtonStateChangeListener(OnKButtonStateChangeListener onKButtonStateChangeListener) {
        this.onKButtonStateChangeListener = onKButtonStateChangeListener;
    }

    public interface OnKButtonStateChangeListener {
        void onKButtonStateChangeListener(KButton button, int valueOn, int valueOff, boolean isOn, int controllerNumber);
    }
}
