package com.firmino.racks;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.firmino.racks.interfaceitems.RoundRackImageButton;
import com.firmino.racks.interfaceitems.SquareRackButton;

import java.util.Objects;

public class Rack extends RelativeLayout {

    private Context mContext;
    private RoundRackImageButton mPowerButton, mMinimizeButton, mConfigButton;
    private LinearLayout mConfigLayout, mContainerLayout;
    private FrameLayout mSaveLayout;
    private TextView mLittleTitle, mBigTitle;
    private SquareRackButton mConfigNameButton, mConfigColorButton, mConfigMidiButton, mConfigDeleteButton, mConfigAddButton;
    private boolean isMinimized = false, isConfigModeOn = false;

    public Rack(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public Rack(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        inflate(mContext, R.layout.rack_layout, this);
        mConfigLayout = findViewById(R.id.Rack_Config_Layout);
        mSaveLayout = findViewById(R.id.Rack_Save_Layout);
        mContainerLayout = findViewById(R.id.Rack_Container_Layout);
        mMinimizeButton = findViewById(R.id.Rack_MinimizeButton);
        mPowerButton = findViewById(R.id.Rack_OnButton);
        mConfigButton = findViewById(R.id.Rack_ConfigButton);
        mLittleTitle = findViewById(R.id.Rack_Little_Title);
        mBigTitle = findViewById(R.id.Rack_Big_Title);
        mConfigNameButton = findViewById(R.id.Rack_Config_Name);
        mConfigColorButton = findViewById(R.id.Rack_Config_Color);
        mConfigMidiButton = findViewById(R.id.Rack_Config_Midi);
        mConfigDeleteButton = findViewById(R.id.Rack_Config_Delete_Rack);
        mConfigAddButton = findViewById(R.id.Rack_Config_Add);

        mConfigNameButton.setOnClickListener(v -> showEditTitleDialog());
        mConfigColorButton.setOnClickListener(v -> showEditColorDialog());

        mPowerButton.setToggle(true);
        //TODO: listener ligado e desativado

        mMinimizeButton.setToggle(false);
        mMinimizeButton.setOnRackButtonClicked((view, isOn) -> {
            if (isOn) setMinimizedMode(!isMinimized);
        });

        mConfigButton.setToggle(true);
        mConfigButton.setOnRackButtonClicked((view, isOn) -> setConfigModeEnabled(isOn));
    }

    public void showEditTitleDialog() {
        View dialogContent = inflate(mContext, R.layout.dialog_rack_edit_name, findViewById(R.id.Rack_Rename_Root));
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogContent);
        Dialog dialog = builder.show();
        EditText name = dialogContent.findViewById(R.id.Rack_Rename_Text);
        name.setText(getRackTitle());
        dialogContent.findViewById(R.id.Rack_Rename_OK).setOnClickListener(v -> {
            if (name.getText().toString().matches("[A-Za-z0-9 ]+")) {
                setRackTitle(name.getText().toString());
                dialog.dismiss();
            } else name.setError("Invalid Characters.");

        });
    }

    public void showEditColorDialog() {
        //TODO: duas cores verdes parecidas
        View dialogContent = inflate(mContext, R.layout.dialog_rack_edit_color, findViewById(R.id.Rack_Color_Root));
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogContent);
        Dialog dialog = builder.show();

        LinearLayout background = dialogContent.findViewById(R.id.Colors_Background_Root);
        LinearLayout foreground = dialogContent.findViewById(R.id.Colors_Foreground_Root);

        for (int i = 0; i < background.getChildCount(); i++) {
            if(Objects.equals(findViewById(R.id.Rack_Background).getBackgroundTintList(), background.getChildAt(i).getBackgroundTintList())){
                ViewGroup.LayoutParams lay = background.getChildAt(i).getLayoutParams();
                lay.height = (int) getResources().getDimension(R.dimen.big_color_circle_size);
                lay.width = (int) getResources().getDimension(R.dimen.big_color_circle_size);
                background.getChildAt(i).setLayoutParams(lay);
            }
            background.getChildAt(i).setOnClickListener(v -> {
                ImageView colorCircle = (ImageView) v;
                for (int index = 0; index < background.getChildCount(); index++) {
                    ViewGroup.LayoutParams lay = background.getChildAt(index).getLayoutParams();
                    lay.height = (int) getResources().getDimension(R.dimen.small_color_circle_size);
                    lay.width = (int) getResources().getDimension(R.dimen.small_color_circle_size);
                    background.getChildAt(index).setLayoutParams(lay);
                }

                ViewGroup.LayoutParams lay = colorCircle.getLayoutParams();
                lay.height = (int) getResources().getDimension(R.dimen.big_color_circle_size);
                lay.width = (int) getResources().getDimension(R.dimen.big_color_circle_size);
                colorCircle.setLayoutParams(lay);
                findViewById(R.id.Rack_Background).setBackgroundTintList(colorCircle.getBackgroundTintList());
            });
        }

        for (int i = 0; i < foreground.getChildCount(); i++) {
            if(Objects.equals(findViewById(R.id.Rack_InnerLayout).getBackgroundTintList(), foreground.getChildAt(i).getBackgroundTintList())){
                ViewGroup.LayoutParams lay = foreground.getChildAt(i).getLayoutParams();
                lay.height = (int) getResources().getDimension(R.dimen.big_color_circle_size);
                lay.width = (int) getResources().getDimension(R.dimen.big_color_circle_size);
                foreground.getChildAt(i).setLayoutParams(lay);
            }
            foreground.getChildAt(i).setOnClickListener(v -> {
                ImageView colorCircle = (ImageView) v;
                for (int index = 0; index < foreground.getChildCount(); index++) {
                    ViewGroup.LayoutParams lay = foreground.getChildAt(index).getLayoutParams();
                    lay.height = (int) getResources().getDimension(R.dimen.small_color_circle_size);
                    lay.width = (int) getResources().getDimension(R.dimen.small_color_circle_size);
                    foreground.getChildAt(index).setLayoutParams(lay);
                }

                ViewGroup.LayoutParams lay = colorCircle.getLayoutParams();
                lay.height = (int) getResources().getDimension(R.dimen.big_color_circle_size);
                lay.width = (int) getResources().getDimension(R.dimen.big_color_circle_size);
                colorCircle.setLayoutParams(lay);

                mBigTitle.setTextColor(colorCircle.getBackgroundTintList());
                mLittleTitle.setTextColor(colorCircle.getBackgroundTintList());
                findViewById(R.id.Rack_InnerLayout).setBackgroundTintList(colorCircle.getBackgroundTintList());

            });
        }

        dialogContent.findViewById(R.id.Rack_Recolor_OK).setOnClickListener(view -> dialog.dismiss());
    }

    public void setConfigModeEnabled(boolean enabled) {
        isConfigModeOn = enabled;
        int maxH = (int) (getResources().getDimension(R.dimen.rack_config_height));
        ValueAnimator anim = ValueAnimator.ofInt(enabled ? 0 : maxH, enabled ? maxH : 0);
        anim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams lay = mConfigLayout.getLayoutParams();
            lay.height = (int) valueAnimator.getAnimatedValue();
            mConfigLayout.setLayoutParams(lay);
        });
        anim.setDuration(300);
        anim.start();
    }

    public void setMinimizedMode(boolean enabled) {
        if (isMinimized != enabled) {
            isMinimized = enabled;
            mMinimizeButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), enabled ? R.drawable.ic_rack_maximize : R.drawable.ic_rack_minimize, null));
            if (enabled && mConfigButton.isOn()) mConfigButton.performClick();
            mSaveLayout.setVisibility(enabled ? View.GONE : View.VISIBLE);
            mContainerLayout.setVisibility(enabled ? View.GONE : View.VISIBLE);
            mConfigButton.setVisibility(enabled ? View.GONE : View.VISIBLE);
            ViewGroup.LayoutParams lay = findViewById(R.id.Rack_InnerLayout).getLayoutParams();
            lay.height = enabled ? ViewGroup.LayoutParams.WRAP_CONTENT : (int) getResources().getDimension(R.dimen.rack_inner_heigth);
            findViewById(R.id.Rack_InnerLayout).setLayoutParams(lay);
        }
    }

    public String getRackTitle() {
        String title = "";
        if (mLittleTitle.getText().length() > 0) {
            title = (String.format("%s %s", mLittleTitle.getText().toString(), mBigTitle.getText().toString()));
        } else {
            title = (mBigTitle.getText().toString());
        }
        return title;
    }

    public void setRackTitle(String title) {
        if (title.matches("[A-Za-z0-9 ]+")) {
            mLittleTitle.setText("");
            mBigTitle.setText("");
            String[] names = title.split(" ");
            if (names.length > 1) {
                StringBuilder littleText = new StringBuilder();
                for (int index = 0; index < names.length - 1; index++)
                    littleText.append(names[index]).append(" ");
                mLittleTitle.setText(littleText.toString().trim());
                mBigTitle.setText(names[names.length - 1]);
            } else mBigTitle.setText(names[0]);
        }
    }

    public void setRackColor(int backgroundColor, int foregroundColor) {
        findViewById(R.id.Rack_Background).setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
        mBigTitle.setTextColor(foregroundColor);
        mLittleTitle.setTextColor(foregroundColor);
        findViewById(R.id.Rack_InnerLayout).setBackgroundTintList(ColorStateList.valueOf(foregroundColor));
    }

    public boolean isConfigModeOn() {
        return isConfigModeOn;
    }
}
