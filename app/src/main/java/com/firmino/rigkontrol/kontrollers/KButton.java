package com.firmino.rigkontrol.kontrollers;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
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

    private TextView mDescription;
    private ImageView mButton, mConfigIcon, mPedalDown;
    private Context mContext;
    private LinearLayout mDescriptionLayout;
    private boolean isToggledDialog, isToggled, isOn, isDescriptionVisible;
    private int mControllerNumber, mValueOn, mValueOff;

    public KButton(@NonNull Context context) {
        super(context);
        mControllerNumber = 0;
        mValueOn = 127;
        mValueOff = 0;
        isToggled = false;
        mDescription.setText("0");
        init(context);
    }

    public KButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        try {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KButton, 0, 0);
            mControllerNumber = ta.getInt(R.styleable.KButton_k_button_control_num, 0);
            mValueOn = ta.getInt(R.styleable.KButton_k_button_on_value, 127);
            mValueOff = ta.getInt(R.styleable.KButton_k_button_off_value, 0);
            isToggled = ta.getBoolean(R.styleable.KButton_k_button_is_toggled, false);
            mDescription.setText(ta.getString(R.styleable.KButton_k_button_description));
            ta.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        mContext = context;
        inflate(mContext, R.layout.layout_kbutton, this);
        mPedalDown = findViewById(R.id.K_Pedal_Down_Icon);
        mConfigIcon = findViewById(R.id.K_Config_Icon);
        mButton = findViewById(R.id.K_Button);
        mDescription = findViewById(R.id.K_Description);
        mDescriptionLayout = findViewById(R.id.K_Description_BG);
        mButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                kontrollerButtonDown();
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                kontrollerButtonUp();
                return true;
            }
            return false;
        });
        mDescription.setOnTouchListener((v, event) -> {
            mDescriptionLayout.setBackground(getResources().getDrawable(event.getAction() == MotionEvent.ACTION_DOWN ? R.drawable.bg_button_text_selected : isOn ? R.drawable.bg_button_text_enabled : R.drawable.bg_button_text, null));
            mDescription.setVisibility(event.getAction() == MotionEvent.ACTION_DOWN ? GONE : VISIBLE);
            mConfigIcon.setVisibility(event.getAction() == MotionEvent.ACTION_DOWN ? VISIBLE : GONE);
            performClick();
            return false;
        });
        mDescription.setOnLongClickListener(v -> {
            showConfigs();
            return true;
        });
    }

    private void showConfigs() {
        final AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
        View mDialogContent = LayoutInflater.from(mContext).inflate(R.layout.dialog_config, null);
        mDialog.setView(mDialogContent);
        final AlertDialog dialog = mDialog.show();
        final TextView dialog_Name = mDialogContent.findViewById(R.id.Config_Name);
        final TextView dialog_ComponentNumber = mDialogContent.findViewById(R.id.Config_ComponentNumber);
        final Button dialog_ComponentMinus = mDialogContent.findViewById(R.id.Config_ComponentMinus);
        final Button dialog_ComponentPlus = mDialogContent.findViewById(R.id.Config_ComponentPlus);
        final Button dialog_btHoldOn = mDialogContent.findViewById(R.id.Config_HoldOn);
        final Button dialog_btHoldOff = mDialogContent.findViewById(R.id.Config_HoldOff);
        final Button dialog_btApply = mDialogContent.findViewById(R.id.Config_OK);
        final RangeSeekBar dialog_seekBar = mDialogContent.findViewById(R.id.Config_Values);
        isToggledDialog = false;
        dialog_ComponentMinus.setOnClickListener(l -> {
            if (Integer.parseInt(dialog_ComponentNumber.getText().toString()) > 0)
                dialog_ComponentNumber.setText(String.valueOf(Integer.parseInt(dialog_ComponentNumber.getText().toString()) - 1));
        });
        dialog_ComponentPlus.setOnClickListener(l -> {
            if (Integer.parseInt(dialog_ComponentNumber.getText().toString()) < 120)
                dialog_ComponentNumber.setText(String.valueOf(Integer.parseInt(dialog_ComponentNumber.getText().toString()) + 1));
        });
        dialog_btHoldOn.setOnClickListener(l -> {
            isToggledDialog = true;
            dialog_btHoldOn.setBackground(getResources().getDrawable(R.drawable.bg_button_holdmode_on_active, null));
            dialog_btHoldOn.setTextColor(getResources().getColor(R.color.text_inactive, null));
            dialog_btHoldOff.setBackground(getResources().getDrawable(R.drawable.bg_button_holdmode_off_disactive, null));
            dialog_btHoldOff.setTextColor(getResources().getColor(R.color.white, null));
        });
        dialog_btHoldOff.setOnClickListener(l -> {
            isToggledDialog = false;
            dialog_btHoldOn.setBackground(getResources().getDrawable(R.drawable.bg_button_holdmode_on_disactive, null));
            dialog_btHoldOn.setTextColor(getResources().getColor(R.color.white, null));
            dialog_btHoldOff.setBackground(getResources().getDrawable(R.drawable.bg_button_holdmode_off_active, null));
            dialog_btHoldOff.setTextColor(getResources().getColor(R.color.text_inactive, null));
        });
        dialog_seekBar.getRightSeekBar().setThumbDrawableId(R.drawable.ic_range_close);
        dialog_seekBar.setProgress(dialog_seekBar.getMinProgress(), dialog_seekBar.getMaxProgress());
        dialog_seekBar.setIndicatorTextDecimalFormat("0");
        dialog_btApply.setOnClickListener(l -> {
            kontrollerSetup(
                    dialog_Name.getText().toString(),
                    Integer.parseInt(dialog_ComponentNumber.getText().toString()),
                    (int) dialog_seekBar.getRightSeekBar().getProgress(),
                    (int) dialog_seekBar.getLeftSeekBar().getProgress(),
                    isToggledDialog
            );
            dialog.dismiss();
        });
        dialog_Name.setText(mDescription.getText());
        dialog_ComponentNumber.setText(String.valueOf(mControllerNumber));
        dialog_seekBar.setProgress(mValueOff, mValueOn);
        dialog_btHoldOn.setBackground(getResources().getDrawable(isToggled ? R.drawable.bg_button_holdmode_on_active : R.drawable.bg_button_holdmode_on_disactive, null));
        dialog_btHoldOn.setTextColor(getResources().getColor(isToggled ? R.color.text_inactive : R.color.white, null));
        dialog_btHoldOff.setBackground(getResources().getDrawable(isToggled ? R.drawable.bg_button_holdmode_off_disactive : R.drawable.bg_button_holdmode_off_active, null));
        dialog_btHoldOff.setTextColor(getResources().getColor(isToggled ? R.color.white : R.color.text_inactive, null));
    }

    public void kontrollerSetup(String description, int controlNumber, int valueOn, int valueOff, boolean isToggledOn ){
        mDescription.setText(description);
        mControllerNumber = controlNumber;
        mValueOn = valueOn;
        mValueOff = valueOff;
        isToggled = isToggledOn;
    }

    public void kontrollerButtonUp() {
        if (!isToggled) {
            isOn = false;
            mButton.setImageDrawable(mContext.getDrawable(R.drawable.ic_button_off));
            mDescriptionLayout.setBackground(getResources().getDrawable(R.drawable.bg_button_text, null));
            MidiKontroller.midiSendControlChange(mControllerNumber, mValueOff);
        }
    }

    public void kontrollerButtonDown() {
        isOn = !isOn;
        mButton.setImageDrawable(mContext.getDrawable(isOn ? R.drawable.ic_button_on : R.drawable.ic_button_off));
        mDescriptionLayout.setBackground(getResources().getDrawable(isOn ? R.drawable.bg_button_text_enabled : R.drawable.bg_button_text, null));
        MidiKontroller.midiSendControlChange(mControllerNumber, isOn ? mValueOn : mValueOff);
    }

    public void kontrollerInvertExpanded() {
        setDescriptionVisible(!isDescriptionVisible);
    }

    public void setDescriptionVisible(boolean isExpanded) {
        this.isDescriptionVisible = isExpanded;
        ValueAnimator anim = ValueAnimator.ofFloat(isExpanded ? 1 : 0, isExpanded ? 0 : 1);
        anim.addUpdateListener(animation -> {
            mDescriptionLayout.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, (Float) animation.getAnimatedValue()));
            mPedalDown.setAlpha((Float) animation.getAnimatedValue());
        });
        anim.setDuration(500);
        anim.start();
    }

    public void setPedalDownIcon(boolean isVisible) {
        mPedalDown.setVisibility(isVisible ? VISIBLE : INVISIBLE);
        invalidate();
    }

    public boolean isButtonEnabled() {
        return isOn;
    }

}
