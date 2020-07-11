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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firmino.rigkontrol.R;

public class KSlider extends LinearLayout {

    private TextView mDescription;
    private ImageView mConfigIcon, mSlider;
    private LinearLayout mDescriptionLayout;
    private boolean isExpanded;
    private int mControllerNumber, mValue;
    private OnKSliderProgressChangeListener onKSliderProgressChangeListener;

    public KSlider(@NonNull Context context) {
        super(context);
        init();
        kontrollerSetup("0", 0);
    }

    public KSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.KSlider, 0, 0);
        kontrollerSetup(
                ta.getString(R.styleable.KSlider_k_slider_description),
                ta.getInt(R.styleable.KSlider_k_slider_control_num, 0)
        );
        ta.recycle();
    }

    public void kontrollerSetup(String description, int componentNumber) {
        mControllerNumber = componentNumber;
        mDescription.setText(description);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void init() {
        inflate(this.getContext(), R.layout.layout_pedal_indicator, this);
        mConfigIcon = findViewById(R.id.K_Config_Icon);
        mDescription = findViewById(R.id.K_Description);
        mDescriptionLayout = findViewById(R.id.K_Description_BG);
        mSlider = findViewById(R.id.Slider_Progress);
        mValue = 0;
        isExpanded = false;
        onKSliderProgressChangeListener = (kSlider, progress, controllerNumber) -> {

        };

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
        final AlertDialog.Builder mDialog = new AlertDialog.Builder(this.getContext());
        View mDialogContent = LayoutInflater.from(this.getContext()).inflate(R.layout.dialog_config_slide, null);
        mDialog.setView(mDialogContent);
        AlertDialog dialog = mDialog.show();
        TextView dialog_Name = mDialogContent.findViewById(R.id.Config_Slide_Name);
        TextView dialog_ComponentNumber = mDialogContent.findViewById(R.id.Config_Slide_ComponentNumber);
        mDialogContent.findViewById(R.id.Config_Slide_ComponentMinus).setOnClickListener(l -> {
            if (Integer.parseInt(dialog_ComponentNumber.getText().toString()) > 0)
                dialog_ComponentNumber.setText(String.valueOf(Integer.parseInt(dialog_ComponentNumber.getText().toString()) - 1));
        });
        mDialogContent.findViewById(R.id.Config_Slide_ComponentPlus).setOnClickListener(l -> {
            if (Integer.parseInt(dialog_ComponentNumber.getText().toString()) < 127)
                dialog_ComponentNumber.setText(String.valueOf(Integer.parseInt(dialog_ComponentNumber.getText().toString()) + 1));
        });
        mDialogContent.findViewById(R.id.Config_Slide_OK).setOnClickListener(l -> {
            mDescription.setText(dialog_Name.getText());
            mControllerNumber = Integer.parseInt(dialog_ComponentNumber.getText().toString());
            dialog.hide();
        });
        dialog_Name.setText(mDescription.getText());
        dialog_ComponentNumber.setText(String.valueOf(mControllerNumber));
    }

    public void setProgress(int progress) {
        mValue = progress * (-mSlider.getWidth()) / 127 + mSlider.getWidth();
        mSlider.setPadding(mSlider.getPaddingLeft(), mSlider.getPaddingTop(), mValue, mSlider.getPaddingBottom());
        //MidiKontroller.sendControlChange(mComponentNumber, progress);
        onKSliderProgressChangeListener.onKSliderProgressChangeListener(this, progress, mControllerNumber);
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

    public void setOnKSliderProgressChangeListener(OnKSliderProgressChangeListener onKSliderProgressChangeListener) {
        this.onKSliderProgressChangeListener = onKSliderProgressChangeListener;
    }

    public interface OnKSliderProgressChangeListener {
        void onKSliderProgressChangeListener(KSlider kSlider, int progress, int controllerNumber);
    }
}
