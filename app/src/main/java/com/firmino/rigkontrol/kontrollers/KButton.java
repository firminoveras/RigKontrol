package com.firmino.rigkontrol.kontrollers;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firmino.rigkontrol.R;
import com.firmino.rigkontrol.midi.MidiKontroller;
import com.jaygoo.widget.RangeSeekBar;

public class KButton extends LinearLayout {

    //TODO: há um pico de memória quando o app usa o gráfico quando o usuario clica

    private TextView mDescription;
    private ImageView mButton, mConfigIcon, mPedalDownIcon;
    private LinearLayout mDescriptionLayout;
    private boolean isToggleInConfig, isToggle, isOn, isDescriptionVisible;
    private int mControllerNumber, mValueOn, mValueOff;

    public KButton(@NonNull Context context) {
        super(context);
        kontrollerSetup("0", 0, 127, 0, false);
        init();
    }

    public KButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KButton, 0, 0);
        kontrollerSetup(
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
            mDescriptionLayout.setBackground(getResources().getDrawable(event.getAction() == MotionEvent.ACTION_DOWN ? R.drawable.bg_button_text_selected : isOn ? R.drawable.bg_button_text_enabled : R.drawable.bg_button_text, null));
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
        isToggleInConfig = false;
        View mDialogContent = LayoutInflater.from(this.getContext()).inflate(R.layout.dialog_config, null);
        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this.getContext());
        mDialog.setView(mDialogContent);
        AlertDialog dialog = mDialog.show();

        TextView dialog_Name = mDialogContent.findViewById(R.id.Config_Name);
        TextView dialog_ComponentNumber = mDialogContent.findViewById(R.id.Config_ComponentNumber);
        Button dialog_btHoldOn = mDialogContent.findViewById(R.id.Config_HoldOn);
        Button dialog_btHoldOff = mDialogContent.findViewById(R.id.Config_HoldOff);
        RangeSeekBar dialog_seekBar = mDialogContent.findViewById(R.id.Config_Values);

        dialog_ComponentNumber.setText(String.valueOf(mControllerNumber));

        mDialogContent.findViewById(R.id.Config_ComponentMinus).setOnClickListener(l -> {
            if (Integer.parseInt(dialog_ComponentNumber.getText().toString()) > 0)
                dialog_ComponentNumber.setText(String.valueOf(Integer.parseInt(dialog_ComponentNumber.getText().toString()) - 1));
        });
        mDialogContent.findViewById(R.id.Config_ComponentPlus).setOnClickListener(l -> {
            if (Integer.parseInt(dialog_ComponentNumber.getText().toString()) < 120)
                dialog_ComponentNumber.setText(String.valueOf(Integer.parseInt(dialog_ComponentNumber.getText().toString()) + 1));
        });
        mDialogContent.findViewById(R.id.Config_OK).setOnClickListener(l -> {
            kontrollerSetup(
                    dialog_Name.getText().toString(),
                    Integer.parseInt(dialog_ComponentNumber.getText().toString()),
                    (int) dialog_seekBar.getRightSeekBar().getProgress(),
                    (int) dialog_seekBar.getLeftSeekBar().getProgress(),
                    isToggleInConfig
            );
            dialog.dismiss();
        });

        dialog_seekBar.getRightSeekBar().setThumbDrawableId(R.drawable.ic_range_close);
        dialog_seekBar.setProgress(dialog_seekBar.getMinProgress(), dialog_seekBar.getMaxProgress());
        dialog_seekBar.setIndicatorTextDecimalFormat("0");
        dialog_seekBar.setProgress(mValueOff, mValueOn);

        dialog_Name.setText(mDescription.getText());

        dialog_btHoldOn.setOnClickListener(l -> {
            isToggleInConfig = true;
            dialog_btHoldOn.setBackground(getResources().getDrawable(R.drawable.bg_button_holdmode_on_active, null));
            dialog_btHoldOn.setTextColor(getResources().getColor(R.color.text_inactive, null));
            dialog_btHoldOff.setBackground(getResources().getDrawable(R.drawable.bg_button_holdmode_off_disactive, null));
            dialog_btHoldOff.setTextColor(getResources().getColor(R.color.white, null));
        });
        dialog_btHoldOff.setOnClickListener(l -> {
            isToggleInConfig = false;
            dialog_btHoldOn.setBackground(getResources().getDrawable(R.drawable.bg_button_holdmode_on_disactive, null));
            dialog_btHoldOn.setTextColor(getResources().getColor(R.color.white, null));
            dialog_btHoldOff.setBackground(getResources().getDrawable(R.drawable.bg_button_holdmode_off_active, null));
            dialog_btHoldOff.setTextColor(getResources().getColor(R.color.text_inactive, null));
        });
        dialog_btHoldOn.setBackground(getResources().getDrawable(isToggle ? R.drawable.bg_button_holdmode_on_active : R.drawable.bg_button_holdmode_on_disactive, null));
        dialog_btHoldOff.setBackground(getResources().getDrawable(isToggle ? R.drawable.bg_button_holdmode_off_disactive : R.drawable.bg_button_holdmode_off_active, null));
        dialog_btHoldOn.setTextColor(getResources().getColor(isToggle ? R.color.text_inactive : R.color.white, null));
        dialog_btHoldOff.setTextColor(getResources().getColor(isToggle ? R.color.white : R.color.text_inactive, null));
    }

    public void kontrollerSetup(String description, int controlNumber, int valueOn, int valueOff, boolean isToggledOn) {
        mDescription.setText(description);
        mControllerNumber = controlNumber;
        mValueOn = valueOn;
        mValueOff = valueOff;
        isToggle = isToggledOn;
    }

    public void kontrollerButtonUp() {
        if (!isToggle) {
            isOn = false;
            mButton.setImageResource(R.drawable.ic_button_off);
            mDescriptionLayout.setBackground(getResources().getDrawable(R.drawable.bg_button_text, null));
            MidiKontroller.sendControlChange(mControllerNumber, mValueOff);
        }
    }

    public void kontrollerButtonDown() {
        isOn = !isOn;
        mButton.setImageResource(isOn ? R.drawable.ic_button_on : R.drawable.ic_button_off);
        mDescriptionLayout.setBackground(getResources().getDrawable(isOn ? R.drawable.bg_button_text_enabled : R.drawable.bg_button_text, null));
        MidiKontroller.sendControlChange(mControllerNumber, isOn ? mValueOn : mValueOff);
    }

    public void kontrollerInvertExpanded() {
        setDescriptionVisible(!isDescriptionVisible);
    }

    public void setDescriptionVisible(boolean isExpanded) {
        this.isDescriptionVisible = isExpanded;
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

}
