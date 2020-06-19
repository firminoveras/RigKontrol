package com.firmino.rigkontrol.kontrollers;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firmino.rigkontrol.R;
import com.jaygoo.widget.RangeSeekBar;

public class KButton extends FrameLayout {

    private TextView mDescription;
    private ImageView mButton, mConfigIcon, mPedalDown;
    private Context mContext;
    private LinearLayout mDescriptionLayout;
    private Dialog mDialog;
    private boolean isToggledDialog, isToggled, isEnabled, isExpanded;
    private int mComponentNumber, mValueEnabled, mValueDisabled;

    public KButton(@NonNull Context context) {
        super(context);
        init(context);
    }

    public KButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        inflate(context, R.layout.layout_kbutton, this);
        mContext = context;
        mPedalDown = findViewById(R.id.K_Pedal_Down_Icon);
        mConfigIcon = findViewById(R.id.K_Config_Icon);
        mButton = findViewById(R.id.K_Button);
        mDescription = findViewById(R.id.K_Description);
        mDescriptionLayout = findViewById(R.id.K_Description_BG);
        mComponentNumber = 0;
        mValueEnabled = 127;
        mValueDisabled = 0;
        isToggled = false;

        mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.dialog_config);

        final TextView dialog_Name = mDialog.findViewById(R.id.Config_Name);
        final TextView dialog_ComponentNumber = mDialog.findViewById(R.id.Config_ComponentNumber);
        final Button dialog_ComponentMinus = mDialog.findViewById(R.id.Config_ComponentMinus);
        final Button dialog_ComponentPlus = mDialog.findViewById(R.id.Config_ComponentPlus);
        final Button dialog_btHoldOn = mDialog.findViewById(R.id.Config_HoldOn);
        final Button dialog_btHoldOff = mDialog.findViewById(R.id.Config_HoldOff);
        final Button dialog_btApply = mDialog.findViewById(R.id.Config_OK);
        final RangeSeekBar dialog_seekBar = mDialog.findViewById(R.id.Config_Values);
        isToggledDialog = false;

        dialog_ComponentMinus.setOnClickListener(l -> {
            if (Integer.parseInt(dialog_ComponentNumber.getText().toString()) > 0)
                dialog_ComponentNumber.setText(String.valueOf(Integer.parseInt(dialog_ComponentNumber.getText().toString()) - 1));
        });
        dialog_ComponentPlus.setOnClickListener(l -> {
            if (Integer.parseInt(dialog_ComponentNumber.getText().toString()) < 127)
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
            mDescription.setText(dialog_Name.getText());
            mComponentNumber = Integer.parseInt(dialog_ComponentNumber.getText().toString());
            mValueEnabled = (int) dialog_seekBar.getRightSeekBar().getProgress();
            mValueDisabled = (int) dialog_seekBar.getLeftSeekBar().getProgress();
            isToggled = isToggledDialog;
            mDialog.hide();
        });
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
            mDescriptionLayout.setBackground(getResources().getDrawable(event.getAction() == MotionEvent.ACTION_DOWN ? R.drawable.bg_button_text_selected : isEnabled ? R.drawable.bg_button_text_enabled : R.drawable.bg_button_text, null));
            mDescription.setVisibility(event.getAction() == MotionEvent.ACTION_DOWN ? GONE : VISIBLE);
            mConfigIcon.setVisibility(event.getAction() == MotionEvent.ACTION_DOWN ? VISIBLE : GONE);
            return false;
        });
        mDescription.setOnLongClickListener(v -> {
            dialog_Name.setText(mDescription.getText());
            dialog_ComponentNumber.setText(String.valueOf(mComponentNumber));
            dialog_seekBar.setProgress(mValueDisabled, mValueEnabled);

            dialog_btHoldOn.setBackground(getResources().getDrawable(isToggled ? R.drawable.bg_button_holdmode_on_active : R.drawable.bg_button_holdmode_on_disactive, null));
            dialog_btHoldOn.setTextColor(getResources().getColor(isToggled ? R.color.text_inactive : R.color.white, null));
            dialog_btHoldOff.setBackground(getResources().getDrawable(isToggled ? R.drawable.bg_button_holdmode_off_disactive : R.drawable.bg_button_holdmode_off_active, null));
            dialog_btHoldOff.setTextColor(getResources().getColor(isToggled ? R.color.white : R.color.text_inactive, null));

            mDialog.show();
            return true;
        });
    }

    public void kontrollerButtonUp() {
        if (!isToggled) {
            isEnabled = false;
            mButton.setImageDrawable(mContext.getDrawable(R.drawable.ic_button_off));
            mDescriptionLayout.setBackground(getResources().getDrawable(R.drawable.bg_button_text, null));
        }
    }

    public void kontrollerButtonDown() {
        isEnabled = !isEnabled;
        mButton.setImageDrawable(mContext.getDrawable(isEnabled ? R.drawable.ic_button_on : R.drawable.ic_button_off));
        mDescriptionLayout.setBackground(getResources().getDrawable(isEnabled ? R.drawable.bg_button_text_enabled : R.drawable.bg_button_text, null));
    }

    public void kontrollerInvertExpanded() {
        setExpanded(!isExpanded);
    }

    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
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
        return isEnabled;
    }

}
