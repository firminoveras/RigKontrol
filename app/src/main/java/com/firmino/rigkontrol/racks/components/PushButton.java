package com.firmino.rigkontrol.racks.components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.rigkontrol.R;
import com.firmino.rigkontrol.kinterface.views.KButton;
import com.firmino.rigkontrol.kinterface.views.KListPicker;
import com.firmino.rigkontrol.kinterface.views.KNumberPicker;
import com.firmino.rigkontrol.kinterface.views.KTextEdit;
import com.firmino.rigkontrol.midi.MidiKontroller;

import it.beppi.knoblibrary.Knob;

public class PushButton extends Component {

    private Context mContext;
    private ImageView mButtonImage;
    private boolean isOn = false;
    private Drawable mDrawableOn, mDrawableOff;

    private static final int PUSH_BUTTON_INTERRUPTOR = 0;
    private static final int PUSH_BUTTON_LED = 1;
    private static final int PUSH_BUTTON_METAL = 2;
    private int mStyle;

    public PushButton(@NonNull Context context, ColorStateList foregroundColor) {
        super(context, foregroundColor);
        setType(TYPE_PUSH_BUTTON);
        mContext = context;
        mButtonImage = new ImageView(context);
        mButtonImage.setOnClickListener(view -> performButtonClick());
        setPushButtonStyle(PUSH_BUTTON_INTERRUPTOR);
        setOnConfigButtonClickedListener(this::showConfigDialog);
        setComponentView(mButtonImage);
    }

    private void performButtonClick() {
        isOn = !isOn;
        refresh();
        setComponentValue(isOn ? MidiKontroller.ON : MidiKontroller.OFF);
    }

    public void setPushButtonStyle(int style) {
        mStyle = style;
        int id_on, id_off;
        switch (style) {
            case PUSH_BUTTON_LED:
                id_on = R.drawable.ic_bt2_on;
                id_off = R.drawable.ic_bt2_off;
                break;
            case PUSH_BUTTON_METAL:
                id_on = R.drawable.ic_bt3_on;
                id_off = R.drawable.ic_bt3_off;
                break;
            default:
                id_on = R.drawable.ic_bt1_on;
                id_off = R.drawable.ic_bt1_off;
                break;

        }
        mDrawableOn = ResourcesCompat.getDrawable(mContext.getResources(), id_on, null);
        mDrawableOff = ResourcesCompat.getDrawable(mContext.getResources(), id_off, null);
        refresh();
    }


    private void refresh() {
        mButtonImage.setImageDrawable(isOn ? mDrawableOn : mDrawableOff);
    }

    private void showConfigDialog() {
        View contentView = inflate(mContext, R.layout.layout_component_button_config, findViewById(R.id.PushButton_Config_Root));
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(contentView);
        builder.setCancelable(false);
        Dialog dialog = builder.show();

        ImageView pushButtonExample = contentView.findViewById(R.id.PushButton_Config_Example);
        KListPicker style = contentView.findViewById(R.id.PushButton_Config_Style);
        KTextEdit title = contentView.findViewById(R.id.PushButton_Config_Title);
        KNumberPicker cc = contentView.findViewById(R.id.PushButton_Config_CC);
        KButton applyButton = contentView.findViewById(R.id.PushButton_Config_Apply_Button);

        title.setTextValue(getTitle());
        pushButtonExample.setImageDrawable(mDrawableOn);

        style.setSelectedItem(getStyle());
        style.setOnKListPikerItemSelectedListener((index, item) -> {
            setPushButtonStyle(index);
            pushButtonExample.setImageDrawable(mDrawableOn);
        });

        cc.setValue(getControlChange());

        applyButton.setOnClickListener(view -> {
            setTitle(title.getText());
            setControlChange(cc.getValue());
            dialog.dismiss();
        });
    }

    public int getStyle() {
        return mStyle;
    }
}
