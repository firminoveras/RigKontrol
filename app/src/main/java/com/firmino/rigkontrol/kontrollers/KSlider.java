package com.firmino.rigkontrol.kontrollers;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
    private Dialog mDialog;
    private LinearLayout mDescriptionLayout;
    private boolean isExpanded;
    private int mComponentNumber, mValue;

    public KSlider(@NonNull Context context) {
        super(context);
        init(context);
    }

    public KSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KSlider(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void init(Context context) {
        inflate(context, R.layout.layout_pedal_indicator, this);
        mConfigIcon = findViewById(R.id.K_Config_Icon);
        mDescription = findViewById(R.id.K_Description);
        mDescriptionLayout = findViewById(R.id.K_Description_BG);
        mSlider = findViewById(R.id.Slider_Progress);
        mContext = context;
        mComponentNumber = 0;
        mValue = 0;

        mDialog = new Dialog(mContext);
        mDialog.setContentView(R.layout.dialog_config_slide);
        final TextView dialog_Name = mDialog.findViewById(R.id.Config_Slide_Name);
        final TextView dialog_ComponentNumber = mDialog.findViewById(R.id.Config_Slide_ComponentNumber);
        final Button dialog_ComponentMinus = mDialog.findViewById(R.id.Config_Slide_ComponentMinus);
        final Button dialog_ComponentPlus = mDialog.findViewById(R.id.Config_Slide_ComponentPlus);
        final Button dialog_btApply = mDialog.findViewById(R.id.Config_Slide_OK);

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
            mDialog.hide();
        });

        isExpanded = false;

        mDescription.setOnTouchListener((v, event) -> {
            mDescriptionLayout.setBackground(getResources().getDrawable(event.getAction() == MotionEvent.ACTION_DOWN ? R.drawable.bg_button_text_selected : R.drawable.bg_button_text, null));
            mDescription.setVisibility(event.getAction() == MotionEvent.ACTION_DOWN ? GONE : VISIBLE);
            mConfigIcon.setVisibility(event.getAction() == MotionEvent.ACTION_DOWN ? VISIBLE : GONE);
            return false;
        });

        mDescription.setOnLongClickListener(v -> {
            dialog_Name.setText(mDescription.getText());
            dialog_ComponentNumber.setText(String.valueOf(mComponentNumber));
            mDialog.show();
            return true;
        });
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
