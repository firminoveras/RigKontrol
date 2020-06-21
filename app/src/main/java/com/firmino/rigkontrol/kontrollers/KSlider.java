package com.firmino.rigkontrol.kontrollers;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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

public class KSlider extends LinearLayout {

    private TextView mDescription;
    private ImageView mConfigIcon, mSlider;
    private Context mContext;
    private LinearLayout mDescriptionLayout;
    private boolean isExpanded;
    private int mComponentNumber, mValue;

    public KSlider(@NonNull Context context) {
        super(context);
        init(context);
        mComponentNumber = 0;
        mDescription.setText("0");
    }

    public KSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        try{
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KSlider,0,0);
            mComponentNumber = ta.getInt(R.styleable.KSlider_k_slider_control_num, 0);
            mDescription.setText(ta.getString(R.styleable.KSlider_k_slider_description));
            ta.recycle();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    public void init(Context context) {
        mContext = context;
        inflate(mContext, R.layout.layout_pedal_indicator, this);
        mConfigIcon = findViewById(R.id.K_Config_Icon);
        mDescription = findViewById(R.id.K_Description);
        mDescriptionLayout = findViewById(R.id.K_Description_BG);
        mSlider = findViewById(R.id.Slider_Progress);
        mValue = 0;
        isExpanded = false;

        mDescription.setOnTouchListener((v, event) -> {
            mDescriptionLayout.setBackground(getResources().getDrawable(event.getAction() == MotionEvent.ACTION_DOWN ? R.drawable.bg_button_text_selected : R.drawable.bg_button_text, null));
            mDescription.setVisibility(event.getAction() == MotionEvent.ACTION_DOWN ? GONE : VISIBLE);
            mConfigIcon.setVisibility(event.getAction() == MotionEvent.ACTION_DOWN ? VISIBLE : GONE);
            return false;
        });

        mDescription.setOnLongClickListener(v -> {
            showConfigs();
            return true;
        });
    }

    private void showConfigs() {
        final AlertDialog.Builder mDialog = new AlertDialog.Builder(mContext);
        View mDialogContent = LayoutInflater.from(mContext).inflate(R.layout.dialog_config_slide, null);
        mDialog.setView(mDialogContent);
        final AlertDialog dialog = mDialog.show();
        final TextView dialog_Name = mDialogContent.findViewById(R.id.Config_Slide_Name);
        final TextView dialog_ComponentNumber = mDialogContent.findViewById(R.id.Config_Slide_ComponentNumber);
        final Button dialog_ComponentMinus = mDialogContent.findViewById(R.id.Config_Slide_ComponentMinus);
        final Button dialog_ComponentPlus = mDialogContent.findViewById(R.id.Config_Slide_ComponentPlus);
        final Button dialog_btApply = mDialogContent.findViewById(R.id.Config_Slide_OK);
        dialog_ComponentMinus.setOnClickListener(l -> {
            if (Integer.parseInt(dialog_ComponentNumber.getText().toString()) > 0)
                dialog_ComponentNumber.setText(String.valueOf(Integer.parseInt(dialog_ComponentNumber.getText().toString()) - 1));
        });
        dialog_ComponentPlus.setOnClickListener(l -> {
            if (Integer.parseInt(dialog_ComponentNumber.getText().toString()) < 127)
                dialog_ComponentNumber.setText(String.valueOf(Integer.parseInt(dialog_ComponentNumber.getText().toString()) + 1));
        });
        dialog_btApply.setOnClickListener(l -> {
            mDescription.setText(dialog_Name.getText());
            mComponentNumber = Integer.parseInt(dialog_ComponentNumber.getText().toString());
            dialog.hide();
        });
        dialog_Name.setText(mDescription.getText());
        dialog_ComponentNumber.setText(String.valueOf(mComponentNumber));
    }

    public void setProgress(int progress) {
        mValue = progress * (-mSlider.getWidth()) / 127 + mSlider.getWidth();
        mSlider.setPadding(mSlider.getPaddingLeft(), mSlider.getPaddingTop(), mValue, mSlider.getPaddingBottom());
        MidiKontroller.midiSendControlChange(mComponentNumber, progress);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
        ValueAnimator anim = ValueAnimator.ofFloat(isExpanded ? 1 : 0, isExpanded ? 0 : 1);
        anim.addUpdateListener(animation -> mDescriptionLayout.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, (Float) animation.getAnimatedValue())));
        anim.setDuration(500);
        anim.start();
    }

}
